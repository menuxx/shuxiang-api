package com.menuxx.miaosha.disruptor.eventhandler

import com.lmax.disruptor.EventHandler
import com.menuxx.miaosha.bean.UserObtainItemState
import com.menuxx.miaosha.disruptor.ChannelUserEvent
import com.menuxx.miaosha.disruptor.ConfirmState
import com.menuxx.miaosha.queue.ChannelUserStateWriteQueue
import com.menuxx.miaosha.store.ChannelItemStore
import com.menuxx.miaosha.store.ChannelUserStore
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import java.util.concurrent.TimeUnit

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */
class ChannelUserEventPostObtainHandler(
        private val objRedisTemplate: RedisTemplate<String, Any>
        ) : EventHandler<ChannelUserEvent> {

    private val logger = LoggerFactory.getLogger(ChannelUserEventPostObtainHandler::class.java)

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
                        userId = event.userId!!,
                        channelItemId = event.channelId!!,
                        confirmState = ConfirmState.Finish.state
                ))
            }

            // 已经被释放
            ConfirmState.FreeObtain -> {
                logger.info("FreeObtain, userId: ${event.userId}, channelId: ${event.channelId}, confirmState: ${event.confirmState.state}, loopRefId: ${event.loopRefId}")
                objRedisTemplate.opsForValue().set(event.loopRefId, UserObtainItemState(
                        loopRefId = event.loopRefId!!,
                        userId = event.userId!!,
                        channelItemId = event.channelId!!,
                        confirmState = ConfirmState.FreeObtain.state
                ))
            }

            // 重复消费
            ConfirmState.ObtainConsumeAgain -> {
                logger.warn("ObtainConsumeAgain, userId: ${event.userId}, channelId: ${event.channelId}, confirmState: ${event.confirmState.state}, loopRefId: ${event.loopRefId}")
                objRedisTemplate.opsForValue().set(event.loopRefId, UserObtainItemState(
                        loopRefId = event.loopRefId!!,
                        userId = event.userId!!,
                        channelItemId = event.channelId!!,
                        confirmState = ConfirmState.ObtainConsumeAgain.state
                ))
            }

            // 已消费 (存储redis状态逻辑已经被上层实现)
            ConfirmState.ObtainConsumed -> {
                logger.info("ObtainConsumed, userId: ${event.userId}, channelId: ${event.channelId}, confirmState: ${event.confirmState.state}, loopRefId: ${event.loopRefId}")
                // 由上一个循环
            }

            // 消费失败
            ConfirmState.ConsumeFail -> {
                logger.warn("ConsumeFail, userId: ${event.userId}, channelId: ${event.channelId}, confirmState: ${event.confirmState.state}, loopRefId: ${event.loopRefId}")
                objRedisTemplate.opsForValue().set(event.loopRefId, UserObtainItemState(
                        loopRefId = event.loopRefId!!,
                        userId = event.userId!!,
                        channelItemId = event.channelId!!,
                        confirmState = ConfirmState.ConsumeFail.state
                ))
            }
        }

        objRedisTemplate.expire(event.loopRefId, 60, TimeUnit.SECONDS)
    }
}

/**
 * 通过 channelItemStore 在内存中完成单线程资源抢占
 */
class ChannelUserEventHandler(
        private val userStateWriteQueue: ChannelUserStateWriteQueue
)
    : EventHandler<ChannelUserEvent> {

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

        val userId = event.userId!!
        val channelId = event.channelId!!

        val channelGroup = ChannelUserStore.getUserGroup(channelId)

        // 用户没有来过
        val sessionUser = channelGroup.getAndInsertUser(userId, event.copy())

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
                            userId = event.userId!!,
                            channelItemId = channelItem.id,
                            confirmState = ConfirmState.Obtain.state
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
                    // 如果用户持有的 消费令牌 和 商品的欲消费令牌 不一致，消费就失败
                    if ( channelItem.preConsumeToken != null && channelItem.preConsumeToken == sessionUser.consumeToken ) {
                        userStateWriteQueue.commitConsumeState(UserObtainItemState(
                                loopRefId = event.loopRefId!!,
                                userId = event.userId!!,
                                channelItemId = channelItem.id,
                                confirmState = ConfirmState.Obtain.state
                        ))
                        sessionUser.confirmState = ConfirmState.ObtainConsumed
                        event.confirmState = ConfirmState.ObtainConsumed
                        // 产生序号
                        val queueNum = channelStore.counter.getAndIncrement()
                        // 移除该用户信息
                        ChannelItemStore.consumeObtainFromChannel(channelId, channelItem.id)
                        println("userId: $userId, queueNum: $queueNum")
                    } else {
                        // 消费令牌不正确，消费就失败
                        event.confirmState = ConfirmState.ConsumeFail
                    }
                } else {
                    // 超过保留时间，被其他人抢走了
                    event.confirmState = ConfirmState.FreeObtain
                }
            }
            ConfirmState.ObtainConsumed -> {
                // 已消费，通知不能重复消费
                event.confirmState = ConfirmState.ObtainConsumeAgain
            }
            else -> {

            }
        }
    }

}