package com.menuxx.miaosha.service

import com.menuxx.Const
import com.menuxx.common.bean.Order
import com.menuxx.common.db.ChannelItemRecordDb
import com.menuxx.common.db.OrderDb
import com.menuxx.miaosha.bean.UserObtainItemState
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class ChannelUserStateService (
        private val objRedisTemplate: RedisTemplate<String, Any>,
        private val channelItemDb: ChannelItemRecordDb,
        private val orderDb: OrderDb
) {

    private val CommitActionObtain = 2
    private val CommitActionConsume = 3

    fun commitConsumeState(event: UserObtainItemState) {
        persisState(event, CommitActionConsume)
    }

    fun commitObtainState(event: UserObtainItemState) {
        persisState(event, CommitActionObtain)
    }

    @Transactional
    fun persisState(event: UserObtainItemState, action: Int) {
        // 写入缓存状态
        objRedisTemplate.opsForValue().set(event.loopRefId, event.copy())
        when(action) {
            CommitActionObtain -> {
                // 设置超时时间
                objRedisTemplate.expire(event.loopRefId, Const.MaxObtainSeconds.toLong(), TimeUnit.SECONDS)
                // 写入数据库状态
                channelItemDb.itemObtain(event.userId, event.channelItemId!!)
            }
            CommitActionConsume -> {
                objRedisTemplate.expire(event.loopRefId, Const.MaxObtainSeconds.toLong(), TimeUnit.SECONDS)
                // 写入数据库状态
                channelItemDb.itemConsumed(event.channelItemId!!, event.orderId!!, event.queueNum!!)
                orderDb.updateOrderConsumed(orderId = event.orderId!!, status = Order.CONSUMED, queueNum = event.queueNum!!)
            }
        }
    }

}