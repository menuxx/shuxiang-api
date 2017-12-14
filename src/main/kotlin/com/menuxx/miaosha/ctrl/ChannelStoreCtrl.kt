package com.menuxx.miaosha.ctrl

import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.OnExceptionContext
import com.aliyun.openservices.ons.api.SendCallback
import com.aliyun.openservices.ons.api.SendResult
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.fasterxml.jackson.databind.ObjectMapper
import com.menuxx.AllOpen
import com.menuxx.Const
import com.menuxx.apiserver.bean.ApiResp
import com.menuxx.common.bean.User
import com.menuxx.common.db.VipChannelDb
import com.menuxx.miaosha.exception.LaunchException
import com.menuxx.miaosha.exception.NotFoundException
import com.menuxx.miaosha.store.ChannelItemStore
import com.menuxx.miaosha.store.ChannelUserStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.async.DeferredResult
import java.util.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */

@AllOpen
@RestController
@RequestMapping("/channel_store")
class ChannelStoreCtrl(
        private val channelDb: VipChannelDb,
        @Autowired @Qualifier("channelUserProducer") private val channelUserProducer: ProducerBean,
        private val objectMapper: ObjectMapper,
        private val intRedisTemplate: RedisTemplate<String, Int>
) {

    private val logger = LoggerFactory.getLogger(ChannelStoreCtrl::class.java)

    /**
     * 长轮询 state
     * 会直接击中 redis 缓存
     */
    data class LoopResult(val stateCode: Int, val msg: String)
    @GetMapping("/{channelId}/long_loop_state")
    fun longLoopState(@PathVariable channelId: Int, @RequestParam loopRefId: String) : LoopResult {
//        val currentUser = SecurityContextHolder.getContext().authentication as User
//        val user = ChannelUserStore.getUserGroup(channelId).getUser(currentUser.id) ?: throw NotFoundException("用户状态不能进行该操作[LOOG_LOOP_USER_NOT_EXISTS]")
//        // 验证 loopRefId 是否一致，不一致，无法发起轮询
//        // 正常请款修改，是正确的
//        return if ( user.loopRefId == loopRefId ) {
//            val confirmState = intRedisTemplate.opsForValue().get(loopRefId)
//            LoopResult(confirmState, "SUCCESS")
//        } else {
//            LoopResult(-1, "FAIL")
//        }
        return LoopResult(-1, "FAIL")
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
    fun requestConsumeObtain() {
        // val currentUser = SecurityContextHolder.getContext().authentication as User
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
        val currentUser = User()
        // val currentUser = SecurityContextHolder.getContext().authentication as User
        val msg = Message()
        msg.tag = "FromServiceNo" // 从服务号抢购
        msg.key = "/channel_store/$channelId/obtain"    // url

        // 长轮询引用id
        val loopRefId = "$channelId:" + UUID.randomUUID().toString()

        // 消息实体 json 格式
        msg.body = objectMapper.writeValueAsBytes(
                mapOf("userId" to currentUser.id, "channelId" to channelId, "loopRefId" to loopRefId)
        )

        // 已不返回，降低线程池压力
        val asyncResult = DeferredResult<TryObtain>()
        // 消息队列发送持有请求消息
        channelUserProducer.sendAsync(msg, object : SendCallback {
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
    @PutMapping("/{channelId}")
    fun channelReady(@PathVariable channelId: Int) : ApiResp {
        return try {
            channelDb.launchChannel(channelId)
            ApiResp(Const.NotErrorCode, "启动成功")
        } catch (ex: LaunchException) {
            ApiResp(ex.code, ex.message!!)
        }
    }

    /**
     * 查看 channel_store 中的数据
     */
    @GetMapping("/{channelId}")
    fun lookChannelStore(@PathVariable channelId: Int) : Map<String, Any>? {
        val store = ChannelItemStore.getChannelStore(channelId)
        if ( store != null ) {
            val data = store.data.toMap()
            val counter = store.counter.get()
            return mapOf("data" to data, "counter" to counter)
        }
        throw NotFoundException("没有找到对应 channel_store, 可能该活动还没有启动")
    }

}