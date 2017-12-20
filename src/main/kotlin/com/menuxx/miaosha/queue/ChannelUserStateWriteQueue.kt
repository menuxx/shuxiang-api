package com.menuxx.miaosha.queue

import com.menuxx.AllOpen
import com.menuxx.Const
import com.menuxx.common.db.ChannelItemRecordDb
import com.menuxx.miaosha.bean.UserObtainItemState
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */

@AllOpen
class ChannelUserStateWriteQueue(
        private val objRedisTemplate: RedisTemplate<String, Any>,
        private val channelItemDb: ChannelItemRecordDb
) {

    private val CommitActionObtain = 1
    private val CommitActionConsume = 2

    // 一个生产者，多个消费者

    private final val consumerThreadNum = 3

    // 消费者线程
    private val consumeConsumerPool = Executors.newFixedThreadPool(consumerThreadNum)

    // 生产者线程
    private val consumeProductPool = Executors.newSingleThreadExecutor()

    // 消费者线程
    private val obtainConsumerPool = Executors.newFixedThreadPool(consumerThreadNum)

    // 生产者线程
    private val obtainProductPool = Executors.newSingleThreadExecutor()

    // 数组实现的有界队列
    // 队列长度 65536，不支持有序，提高队列吞吐率
    // 支持 2万 多个请求堆积
    // 持有队列
    private val obtainQueue = ArrayBlockingQueue<UserObtainItemState>(20000, false)
    // 消费持有队列
    private val consumeQueue = ArrayBlockingQueue<UserObtainItemState>(20000, false)

    @Transactional
    fun persisState(event: UserObtainItemState, action: Int) {
        // 写入缓存状态
        objRedisTemplate.opsForValue().set(event.loopRefId, event.copy())
        when(action) {
            CommitActionObtain -> {
                // 设置超时时间
                objRedisTemplate.expire(event.loopRefId, Const.MaxObtainSeconds.toLong(), TimeUnit.SECONDS)
                // 写入数据库状态
                channelItemDb.itemObtain(event.userId, event.channelItemId)
            }
            CommitActionConsume -> {
                objRedisTemplate.expire(event.loopRefId, 3600, TimeUnit.SECONDS)
                // 写入数据库状态
                channelItemDb.itemConsumed(event.channelItemId)
            }
        }
    }

    fun shutdown() {
        obtainConsumerPool.shutdown()
        obtainProductPool.shutdown()

        consumeConsumerPool.shutdown()
        consumeProductPool.shutdown()
    }

    // 初始化队列
    fun start() {
        // 持有队列消费者
        val obtainConsumer = {
            try {
                val event = obtainQueue.take()
                // 写入状态
                // 以待客户端轮训状态
                persisState(event, CommitActionObtain)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        // 初始化多个消费者
        (1..consumerThreadNum).map {
            obtainConsumerPool.submit(obtainConsumer)
        }

        // 消费队列消费者
        val consumeConsumer = {
            try {
                val event = consumeQueue.take()
                // 写入状态
                // 以待客户端轮训状态
                persisState(event, CommitActionConsume)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        // 初始化多个消费者
        (1..consumerThreadNum).map {
            obtainConsumerPool.submit(consumeConsumer)
        }
    }

    /**
     * 提交持有状态
     */
    fun commitObtainState(event: UserObtainItemState) : Future<*> {
        return obtainProductPool.submit({ obtainQueue.put(event) })
    }

    /**
     * 提交消费抓状态
     */
    fun commitConsumeState(event: UserObtainItemState) : Future<*> {
        return obtainProductPool.submit({ consumeQueue.put(event) })
    }

}