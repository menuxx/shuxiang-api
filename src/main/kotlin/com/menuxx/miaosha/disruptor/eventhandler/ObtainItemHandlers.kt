package com.menuxx.miaosha.disruptor.eventhandler

import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.fasterxml.jackson.databind.ObjectMapper
import com.lmax.disruptor.EventHandler
import com.menuxx.common.db.OrderDb
import com.menuxx.common.prop.AliyunProps
import com.menuxx.miaosha.bean.UserObtainItemState
import com.menuxx.miaosha.disruptor.ChannelUserEvent
import com.menuxx.miaosha.disruptor.ConfirmState
import com.menuxx.miaosha.queue.ChannelUserStateWriteQueue
import com.menuxx.miaosha.queue.MsgTags
import com.menuxx.miaosha.queue.msg.ConsumeSuccessMsg
import com.menuxx.miaosha.store.ChannelItemStore
import com.menuxx.miaosha.store.ChannelUserStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */
@Component
class ChannelUserEventPostObtainHandler(
        private val aliyunProps: AliyunProps,
        private val orderDb: OrderDb,
        private val objectMapper: ObjectMapper,
        private val objRedisTemplate: RedisTemplate<String, Any>,
        @Autowired @Qualifier("senderProducer") private val senderProducer: ProducerBean
        ) : EventHandler<ChannelUserEvent> {

    private val logger = LoggerFactory.getLogger(ChannelUserEventPostObtainHandler::class.java)

    /**
     * 发送订单消费提醒
     */
    private fun sendConsumedSmsMsg(orderId: Int) {
        val order = orderDb.getOrderDetail(orderId)!!
        val msgBody = objectMapper.writeValueAsBytes(ConsumeSuccessMsg(mobile = order.receiverPhoneNumber, itemName = order.vipChannel.item.name, receiverPhoneNumber = order.receiverPhoneNumber))
        val sendSmsMsg = Message(aliyunProps.ons.senderTopicName, MsgTags.TagConsumeSuccess, "ObtainConsumedMsg_${order.orderNo}", msgBody)
        senderProducer.sendOneway(sendSmsMsg)
    }

    override fun onEvent(event: ChannelUserEvent, sequence: Long, endOfBatch: Boolean) {

        when (event.confirmState) {

            // 未被持有
            ConfirmState.NoObtain -> {
                logger.warn("NoObtain, userId: ${event.userId}, channelId: ${event.channelId}, confirmState: ${event.confirmState.state}, loopRefId: ${event.loopRefId}")
            }

            // 被持有(存储redis状态逻辑已经被上层实现)
            ConfirmState.Obtain -> {
                logger.info("Obtain, userId: ${event.userId}, channelId: ${event.channelId}, confirmState: ${event.confirmState.state}, loopRefId: ${event.loopRefId}")
                // mqtt 通知 个数减少
            }

            // 已经抢完了
            ConfirmState.Finish -> {
                logger.info("Finish, userId: ${event.userId}, channelId: ${event.channelId}, confirmState: ${event.confirmState.state}, loopRefId: ${event.loopRefId}")
                objRedisTemplate.opsForValue().set(event.loopRefId, UserObtainItemState(
                        loopRefId = event.loopRefId!!,
                        userId = event.userId,
                        channelItemId = event.channelId,
                        confirmState = ConfirmState.Finish.state,
                        orderId = event.orderId,
                        queueNum = null
                ))
            }

            // 已经被释放
            ConfirmState.FreeObtain -> {
                logger.info("FreeObtain, userId: ${event.userId}, channelId: ${event.channelId}, confirmState: ${event.confirmState.state}, loopRefId: ${event.loopRefId}")
                objRedisTemplate.opsForValue().set(event.loopRefId, UserObtainItemState(
                        loopRefId = event.loopRefId!!,
                        userId = event.userId,
                        channelItemId = event.channelId,
                        confirmState = ConfirmState.FreeObtain.state,
                        orderId = event.orderId,
                        queueNum = null
                ))
            }

            // 重复消费
            ConfirmState.ObtainConsumeAgain -> {
                logger.warn("ObtainConsumeAgain, userId: ${event.userId}, channelId: ${event.channelId}, confirmState: ${event.confirmState.state}, loopRefId: ${event.loopRefId}")
                objRedisTemplate.opsForValue().set(event.loopRefId, UserObtainItemState(
                        loopRefId = event.loopRefId!!,
                        userId = event.userId,
                        channelItemId = event.channelId,
                        confirmState = ConfirmState.ObtainConsumeAgain.state,
                        orderId = event.orderId,
                        queueNum = null
                ))
            }

            // 已消费 (存储redis状态逻辑已经被上层实现)
            ConfirmState.ObtainConsumed -> {
                logger.info("ObtainConsumed, userId: ${event.userId}, channelId: ${event.channelId}, confirmState: ${event.confirmState.state}, loopRefId: ${event.loopRefId}")
                // 由上一个循环
                sendConsumedSmsMsg(event.orderId!!)
            }
        }

        objRedisTemplate.expire(event.loopRefId, 60, TimeUnit.SECONDS)
    }
}

/**
 * 通过 channelItemStore 在内存中完成单线程资源抢占
 */
@Component
class ChannelUserEventHandler(
        private val userStateWriteQueue: ChannelUserStateWriteQueue
)
    : EventHandler<ChannelUserEvent> {

    private final val logger = LoggerFactory.getLogger(ChannelUserEventHandler::class.java)

    /**
     * 维护用户状态
     * 缺点 有节点状态
     * 通过分布式缓存可以解决这个问题
     *
     * 支持多渠道同时抢
     *
     * userGroup 多渠道用户分组
     * channelUsers 单渠道用户数组
     * sessionUser 当前会话用户
     */

    override fun onEvent(event: ChannelUserEvent, sequence: Long, endOfBatch: Boolean) {

        val userId = event.userId
        val channelId = event.channelId

        val channelGroup = ChannelUserStore.getUserGroup(channelId)

        // 用户没有来过
        val sessionUser = channelGroup.getAndInsertUser(userId, event.copy())

        // 当 loopRefId 不存在的时候 只有第一次必须有 (获得obtain的时候)
        event.loopRefId = event.loopRefId ?: sessionUser.loopRefId

        logger.debug("event: loopRefId: ${event.loopRefId}, userId: $userId, channelId: $channelId; sessionUser.confirmState: ${sessionUser.confirmState}")

        // 该出不能外空，否则，加载模块 有 bug
        val channelStore = ChannelItemStore.getChannelStore(channelId)!!

        // 如果未持有，就开始持有
        when(sessionUser.confirmState) {
            // 未持有，就申请持有，如果已经被释放了，就再抢
            ConfirmState.NoObtain, ConfirmState.FreeObtain -> {
                // 开始持有
                val channelItem = ChannelItemStore.obtainItemFromChannel(userId, channelId)
                // 如果能够持有，就修改用户状态
                if (channelItem != null) {
                    // 提交到 状态持久化 队列
                    userStateWriteQueue.commitObtainState(UserObtainItemState(
                            loopRefId = event.loopRefId!!,
                            userId = event.userId,
                            channelItemId = channelItem.id,
                            confirmState = ConfirmState.Obtain.state,
                            orderId = null,
                            queueNum = null
                    ))
                    // 同时修改两种状态 sessionUser 标注用户状态，event 标注 disruptor 的下一个 handler 的状态
                    sessionUser.confirmState = ConfirmState.Obtain
                    event.confirmState = ConfirmState.Obtain
                } else {
                    // 抢完了
                    event.confirmState = ConfirmState.Finish
                }
            }
            // 已持有，就申请消费
            ConfirmState.Obtain -> {
                // 必去要持有一个 消费obtain  的令牌，才能执行该步骤
                val channelItem = ChannelItemStore.searchObtainFromChannel(userId, channelId)
                if ( channelItem != null ) {
                    sessionUser.confirmState = ConfirmState.ObtainConsumed
                    event.confirmState = ConfirmState.ObtainConsumed
                    // 产生序号
                    val queueNum = channelStore.counter.getAndIncrement()
                    // 移除该用户信息
                    ChannelItemStore.consumeObtainFromChannel(channelId, channelItem.id)
                    // 持久化导数据库
                    userStateWriteQueue.commitConsumeState(UserObtainItemState(
                            loopRefId = event.loopRefId!!,
                            userId = event.userId,
                            channelItemId = channelItem.id,
                            confirmState = ConfirmState.Obtain.state,
                            orderId = event.orderId,
                            queueNum = queueNum
                    ))
                } else {
                    // 超过保留时间，被其他人抢走了
                    sessionUser.confirmState = ConfirmState.FreeObtain
                    event.confirmState = ConfirmState.FreeObtain
                }
            }
            ConfirmState.ObtainConsumed -> {
                // 已消费，通知不能重复消费
                sessionUser.confirmState = ConfirmState.ObtainConsumeAgain
                event.confirmState = ConfirmState.ObtainConsumeAgain
            }
            else -> {

            }
        }
    }

}