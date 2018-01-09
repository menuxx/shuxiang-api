package com.menuxx.miaosha.ctrl

import com.github.binarywang.wxpay.bean.order.WxPayMpOrderResult
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest
import com.github.binarywang.wxpay.service.WxPayService
import com.menuxx.*
import com.menuxx.apiserver.bean.ApiResp
import com.menuxx.common.bean.Order
import com.menuxx.common.db.OrderChargeDb
import com.menuxx.common.db.OrderDb
import com.menuxx.common.db.VChannelDb
import com.menuxx.miaosha.bean.ChannelItem
import com.menuxx.miaosha.disruptor.ConfirmState
import com.menuxx.miaosha.exception.LaunchException
import com.menuxx.miaosha.exception.NotFoundException
import com.menuxx.miaosha.queue.MsgTags
import com.menuxx.miaosha.queue.publisher.ConsumeObtainPublisher
import com.menuxx.miaosha.queue.publisher.RequestObtainPublisher
import com.menuxx.miaosha.service.ChannelOrderService
import com.menuxx.miaosha.store.ChannelItemStore
import com.menuxx.miaosha.store.ChannelUserGroup
import com.menuxx.miaosha.store.ChannelUserStore
import com.menuxx.miaosha.store.StoreItem
import com.menuxx.weixin.prop.WeiXinProps
import org.slf4j.LoggerFactory
import org.springframework.amqp.AmqpException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ValueOperations
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.InetAddress
import java.time.Instant
import java.util.*
import javax.validation.Valid
import kotlin.collections.HashMap
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
        private val wxProps: WeiXinProps,
        private val channelDb: VChannelDb,
        private val orderDb: OrderDb,
        private val orderChargeDb: OrderChargeDb,
        private val wxPayService: WxPayService,
        private val orderService: ChannelOrderService,
        private val consumeObtainPublisher : ConsumeObtainPublisher,
        private val requestObtainPublisher: RequestObtainPublisher,
        @Autowired @Qualifier("objOperations") private val objOperations: ValueOperations<String, Any>
) {

    @GetMapping("channel_item/{channelId}")
    fun getUserStateAll(@PathVariable channelId: Int) : StoreItem<ChannelItem>? {
        return ChannelItemStore.getChannelStore(channelId)
    }

    @GetMapping("channel_user/{channelId}")
    fun getUserState(@PathVariable channelId: Int) : ChannelUserGroup {
        return ChannelUserStore.getUserGroup(channelId)
    }

    private val logger = LoggerFactory.getLogger(ChannelStoreCtrl::class.java)

    /**
     * 长轮询 state
     * 会直接击中 redis 缓存
     */
    data class LoopResult(val stateCode: Int, val msg: String, val state: HashMap<String, Any?>?)
    @GetMapping("/{channelId}/long_loop_state")
    fun longLoopState(@PathVariable channelId: Int, @RequestParam loopRefId: String) : LoopResult {
        val currentUser = getCurrentUser()
        val user = ChannelUserStore.getUserGroup(channelId).getUser(currentUser.id) ?: return LoopResult(0, "WAIT", null)
        // 验证 loopRefId 是否一致，不一致，无法发起轮询
        // 正常请款修改，是正确的
        logger.debug("loopRefId: UserLoopRefId: ${user.loopRefId}, RequestLoopRefId: $loopRefId")
        return if ( user.loopRefId == loopRefId ) {
            @SuppressWarnings("unchecked")
            val data = objOperations.get(loopRefId) as LinkedHashMap<String, Any>?
            if ( data != null ) {
                LoopResult(data["confirmState"] as Int, "SUCCESS", state = hashMapOf(
                        "queueNum" to data["queueNum"],
                        "channelItemId" to data["queueNum"],
                        "orderId" to data["orderId"]
                ))
            } else {
                LoopResult(0, "WAIT", null)
            }
        } else {
            LoopResult(-1, "FAIL loop token error.", null)
        }
    }

    /**
     * 获取渠道用户状态
     * 1: 如果用户传 loopRefId 就先去查 redis 中的状态，如果状态不存在 就按照下面的来
     * 2: 就在订单表中查订单，如果订单创建时间没有超时，就是待销费状态(Obtain 已持有)，如果超时了就是(FreeObtain 持有释放)状态
     * 3: 如果订单表中也没有，旧查看该渠道是否过期，如果过期，就是结束状态，如果没有过期，就是 NoObtain 状态
     */
    @GetMapping("/{channelId}/user_state")
    fun getChannelUserState(@PathVariable channelId: Int, @RequestParam(required = false) loopRefId: String?) : LoopResult {
        val currentUser = getCurrentUser()
        if (loopRefId != null) {
            @SuppressWarnings("unchecked")
            val data = objOperations.get(loopRefId) as LinkedHashMap<String, Any>?
            if (data != null) {
                return LoopResult(data["confirmState"] as Int, "SUCCESS", state = hashMapOf(
                        "queueNum" to data["queueNum"],
                        "channelItemId" to data["queueNum"],
                        "orderId" to data["orderId"]
                ))
            }
        }
        val order = channelDb.getChannelUserOrder(currentUser.id, channelId)
        return if ( order != null ) {
            // 如果订单已经消费 >= 2 的都是消费的状态
            if ( order.status >= Order.CONSUMED ) {
                // 如果已经消费
                LoopResult(ConfirmState.ObtainConsumed.state, "SUCCESS", hashMapOf(
                        "queueNum" to order.queueNum,
                        "orderId" to order.id
                ))
            } else {
                // 如果没有消费，检查是否过期
                val isExpired = order.createAt.toInstant().plusSeconds(Const.MaxObtainSeconds.toLong()).isAfter(Instant.now())
                if ( isExpired ) {
                    LoopResult(ConfirmState.FreeObtain.state, "SUCCESS", null)
                } else {
                    // 如果没有过期
                    LoopResult(ConfirmState.Obtain.state, "SUCCESS", null)
                }
            }
        } else {
            val channel = channelDb.getSimpleById(channelId)
            if ( channel != null ) {
                // 如果渠道过期
                val channelExpired = channel.endTime.after(Date(System.currentTimeMillis()))
                if ( channelExpired ) {
                    LoopResult(ConfirmState.Finish.state, "SUCCESS", null)
                } else {
                    LoopResult(ConfirmState.NoObtain.state, "SUCCESS", null)
                }
            } else {
                throw NotFoundException("渠道ID: $channelId 不存在")
            }
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
    fun requestConsumeObtain(@PathVariable channelId: Int, @RequestBody @Valid consumeData: OrderConsumeRequestData) : ResponseEntity<OrderConsumeResultData> {
        val currentUser = getCurrentUser()
        // 找到该用户在渠道中的持有
        val channelItem = ChannelItemStore.searchObtainFromChannel(currentUser.id, channelId) ?: throw NotFoundException("超时了，书要被抢完了")
        val userId = channelItem.second.obtainUserId!!

        // 先获取订单，如果用户已经创建过，就不会二次创建订单
        var order = orderDb.getUserChannelOrder(userId, channelId)
        if ( order == null ) {
            val newOrder = orderService.createChannelOrder(channelId, userId, consumeData.addressId)
            order = orderDb.insertOrder(newOrder)
        }
        // 如果不收费，就不拉起微信支付
        return if ( order.payAmount == 0 ) {
            // 往消息队列中，确认该订单的完成
            return try {
                // 下单完成发送消息，给 Const.QueueTagTradeObtain 消费者处理
                consumeObtainPublisher.sendConsumeObtainEvent(userId, channelId, order.id, null)
                ResponseEntity.ok().body(OrderConsumeResultData(wxPayment = null, code = Const.NotErrorCode, orderId = order.id,  msg = "下单完成"))
            } catch (e: AmqpException) {
                logger.error("requestConsumeObtain", e)
                ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(OrderConsumeResultData(wxPayment = null, code = Const.MQPublishErrorCode, orderId = order.id,  msg = e.message!!))
            }
        } else {
            var orderCharge = orderChargeDb.findChargeRecordByOutTradeNo(order.orderNo)
            if ( orderCharge == null ) {
                val dbOrderCharge = orderService.createChannelOrderCharge(order, currentUser.openid!!)
                orderCharge = orderChargeDb.insertChargeRecord(dbOrderCharge)
            }
            val payReq = WxPayUnifiedOrderRequest()
            // 注意该处的 tag 标识
            payReq.notifyURL = "${wxProps.pay.notifyUrl}/${MsgTags.TagConsumeObtain}"
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
            ResponseEntity.accepted().body(OrderConsumeResultData(wxPayment = wxPayment, code = 2, orderId = order.id,  msg = "请在公众号中完成支付"))
        }
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
    fun requestObtain(@PathVariable channelId: Int) : TryObtain {
        val currentUser = getCurrentUser()
        val user = ChannelUserStore.getUserGroup(channelId).getUser(currentUser.id)
        // 长轮询引用id, 第二次就是用原来的 id
        val loopRefId = if ( user == null ) { "$channelId:" + UUID.randomUUID().toString() } else { user.loopRefId!! }
        return try {
            val messageId = requestObtainPublisher.sendUserObtainEvent(userId = currentUser.id, channelId = channelId, loopRefId = loopRefId)
            TryObtain(loopRefId = loopRefId, messageId = messageId, errCode = Const.NotErrorCode, errMsg = "已申请资源抢占信息")
        } catch (e: AmqpException) {
            logger.error("requestObtain: AmqpException", e)
            TryObtain(loopRefId = loopRefId, messageId = "", errCode = Const.MQPublishErrorCode, errMsg = e.message)
        }
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