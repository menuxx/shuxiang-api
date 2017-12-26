package com.menuxx.miaosha.ctrl

import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.OnExceptionContext
import com.aliyun.openservices.ons.api.SendCallback
import com.aliyun.openservices.ons.api.SendResult
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest
import com.github.binarywang.wxpay.service.WxPayService
import com.menuxx.*
import com.menuxx.apiserver.bean.ApiResp
import com.menuxx.common.db.OrderChargeDb
import com.menuxx.common.db.OrderDb
import com.menuxx.common.db.VChannelDb
import com.menuxx.common.prop.AliyunProps
import com.menuxx.miaosha.exception.LaunchException
import com.menuxx.miaosha.exception.NotFoundException
import com.menuxx.miaosha.queue.MsgTags
import com.menuxx.miaosha.queue.msg.ObtainConsumedMsg
import com.menuxx.miaosha.queue.msg.ObtainUserMsg
import com.menuxx.miaosha.service.ChannelOrderService
import com.menuxx.miaosha.store.ChannelItemStore
import com.menuxx.miaosha.store.ChannelUserStore
import com.menuxx.weixin.prop.WeiXinProps
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ValueOperations
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult
import java.net.InetAddress
import java.util.*
import javax.validation.Valid
import kotlin.collections.LinkedHashMap

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */

@AllOpen
@RestController
@RequestMapping("/v_channel_store")
class ChannelStoreCtrl(
        private val aliyunProps: AliyunProps,
        private val wxProps: WeiXinProps,
        private val channelDb: VChannelDb,
        private val orderDb: OrderDb,
        private val orderChargeDb: OrderChargeDb,
        private val wxPayService: WxPayService,
        private val orderService: ChannelOrderService,
        @Autowired @Qualifier("obtainConsumeProducer") private val obtainConsumeProducer: ProducerBean,
        @Autowired @Qualifier("requestObtainProducer") private val requestObtainProducer: ProducerBean,
        private val objectMapper: ObjectMapper,
        @Autowired @Qualifier("objOperations") private val objOperations: ValueOperations<String, Any>
) {

    private val logger = LoggerFactory.getLogger(ChannelStoreCtrl::class.java)

    /**
     * 长轮询 state
     * 会直接击中 redis 缓存
     */
    data class LoopResult(val stateCode: Int, val msg: String)
    @GetMapping("/{channelId}/long_loop_state")
    fun longLoopState(@PathVariable channelId: Int, @RequestParam loopRefId: String) : LoopResult {
        val currentUser = getCurrentUser()
        val user = ChannelUserStore.getUserGroup(channelId).getUser(currentUser.id) ?: return LoopResult(0, "WAIT")
        // 验证 loopRefId 是否一致，不一致，无法发起轮询
        // 正常请款修改，是正确的
        logger.debug("loopRefId: UserLoopRefId: ${user.loopRefId}, RequestLoopRefId: $loopRefId")
        return if ( user.loopRefId == loopRefId ) {
            val data = objOperations.get(loopRefId) as LinkedHashMap<String, Any>?
            if ( data != null ) {
                LoopResult(data["confirmState"] as Int, "SUCCESS")
            } else {
                LoopResult(0, "WAIT")
            }
        } else {
            LoopResult(-1, "FAIL loop token error.")
        }
    }

    // val code: Int, val message: String
    @GetMapping("/{channelId}/state")
    fun lookupState(@PathVariable channelId: Int) : Map<String, Any> {
        val state = ChannelItemStore.getChannelStore(channelId)
        return if ( state != null ) {
            mapOf("code" to Const.NotErrorCode,  "count" to state)
        } else {
            mapOf("code" to 404, "message" to "VIP渠道不存在")
        }
    }

    /**
     * 请求消费持有
     * 1. 需要先检查该用户是否，已经成功持有(obtain)
     * 2. 然后将给用户持有的 ChannelItem 标注为消费，并异步在数据库生成一条订单记录
     * 消费持有，一般伴随支付，为了让支付更可靠，引入消息队列机制，消费成功后，通知 message handler 消费持有
     * 由消息队列输入
     */
    data class OrderConsumeRequestData(val addressId: Int)
    data class OrderConsumeResultData(var wxPayment: WxPayMpOrderResult?, val orderId: Int, val code: Int, val msg: String)
    @PutMapping("/{channelId}/consume")
    fun requestConsumeObtain(@PathVariable channelId: Int, @RequestBody @Valid consumeData: OrderConsumeRequestData) : DeferredResult<OrderConsumeResultData> {
        val asyncResult = DeferredResult<OrderConsumeResultData>()
        val currentUser = getCurrentUser()
        // 找到该用户在渠道中的持有
        val channelItem = ChannelItemStore.searchObtainFromChannel(currentUser.id, channelId) ?: throw NotFoundException("超时了，书要被抢完了")
        val userId = channelItem.second.obtainUserId!!

        var order = orderDb.getUserChannelOrder(userId, channelId)
        if ( order == null ) {
            val newOrder = orderService.createChannelOrder(channelId, userId, consumeData.addressId)
            order = orderDb.insertOrder(newOrder)
        }

        // 如果不收费，就不拉起微信支付
        if ( order.payAmount == 0 ) {
            // 往消息队列中，确认该订单的完成
            val msgBody = objectMapper.writeValueAsBytes(ObtainConsumedMsg(channelId = channelId, userId = userId, orderId = order.id, loopRefId = null))
            val msg = Message(aliyunProps.ons.consumeObtainTopicName, MsgTags.TagObtainConsume, "NoFeeConsume_${order.orderNo}", msgBody)
            // 下单完成发送消息，给 Const.QueueTagTradeObtain 消费者处理
            obtainConsumeProducer.sendAsync(msg, object : SendCallback {
                override fun onSuccess(sendResult: SendResult) {
                    asyncResult.setResult(OrderConsumeResultData(null, order.id, Const.NotErrorCode, "下单完成"))
                }
                override fun onException(context: OnExceptionContext) {
                    asyncResult.setErrorResult(context.exception)
                }
            })
        } else {
            var orderCharge = orderChargeDb.findChargeRecordByOutTradeNo(order.orderNo)
            if ( orderCharge == null ) {
                val dbOrderCharge = orderService.createChannelOrderCharge(order, currentUser.openid!!)
                orderCharge = orderChargeDb.insertChargeRecord(dbOrderCharge)
            }
            val payReq = WxPayUnifiedOrderRequest()
            // 注意该处的 tag 标识
            payReq.notifyURL = "${wxProps.pay.notifyUrl}/${MsgTags.TagObtainConsume}"
            payReq.outTradeNo = orderCharge.outTradeNo
            payReq.attach = orderCharge.attach
            payReq.body = orderCharge.body
            payReq.openid = orderCharge.openid
            payReq.totalFee = orderCharge.totalFee
            payReq.timeExpire = formatWXTime(orderCharge.timeExpire)    // 过期时间
            payReq.tradeType = orderCharge.tradeType
            payReq.deviceInfo = orderCharge.deviceInfo
            payReq.spbillCreateIp = InetAddress.getLocalHost().hostAddress
            payReq.nonceStr = orderCharge.nonceStr
            // 需要前端建立的会话数据
            val wxPayment = wxPayService.createOrder<WxPayMpOrderResult>(payReq)
            asyncResult.setResult(OrderConsumeResultData(wxPayment = wxPayment, code = 2, orderId = order.id,  msg = "请在公众号中完成支付"))
        }
        return asyncResult
    }

    /**
     * 请求持有，就是对某个 channel_item 加锁，目前加锁最长时间 Const.MaxObtainSeconds 30 秒
     * 超过加锁时间后，其他用户可以抢
     *
     * 该方法会立即返回，前端将执行 轮询 抢购结果。
     * 轮训到持有成功后，将 请求消费持有
     *
     * 1. 检查该用户资格
     * 2. 成功，
     */
    data class TryObtain(val loopRefId: String?, val messageId: String, val errCode: Int, val errMsg: String?)
    @GetMapping("/{channelId}/obtain")
    fun requestObtain(@PathVariable channelId: Int) : DeferredResult<TryObtain> {
        val currentUser = getCurrentUser()
        val user = ChannelUserStore.getUserGroup(channelId).getUser(currentUser.id)
        // 长轮询引用id, 第二次就是用原来的 id
        val loopRefId = if ( user == null ) { "$channelId:" + UUID.randomUUID().toString() } else { user.loopRefId!! }

        val msgBody = objectMapper.writeValueAsBytes(ObtainUserMsg(userId = currentUser.id, channelId = channelId, loopRefId = loopRefId))

        val msg = Message(aliyunProps.ons.obtainItemTopicName, MsgTags.TagFromMpRequestObtain, "RequestObtain_${channelId}_${currentUser.id}", msgBody)

        // 已不返回，降低线程池压力
        val asyncResult = DeferredResult<TryObtain>()
        // 消息队列发送持有请求消息
        requestObtainProducer.sendAsync(msg, object : SendCallback {
            override fun onSuccess(result: SendResult) {
                asyncResult.setResult(TryObtain(
                        loopRefId = loopRefId,
                        messageId = result.messageId,
                        errCode = 0,
                        errMsg = "资格已申请"
                ))
            }
            override fun onException(ctx: OnExceptionContext) {
                logger.error("ObtainError(messageId: ${ctx.messageId}, topic: ${ctx.topic}), ONSClientException( message: ${ctx.exception.message} )")
                asyncResult.setErrorResult(TryObtain(
                        loopRefId = null,
                        messageId = ctx.messageId,
                        errCode = 501,
                        errMsg = ctx.exception.message
                ))
            }
        })
        return asyncResult
    }

    /**
     * 加载渠道 到就绪状态
     */
    @PutMapping("/{channelId}/launch")
    fun channelReady(@PathVariable channelId: Int) : ApiResp {
        return try {
            channelDb.launchChannel(channelId)
            ApiResp(Const.NotErrorCode, "启动成功")
        } catch (ex: LaunchException) {
            ApiResp(ex.code, ex.message!!)
        }
    }

}