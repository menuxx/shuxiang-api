package com.menuxx.miaosha.disruptor

import com.lmax.disruptor.YieldingWaitStrategy
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ProducerType
import com.menuxx.miaosha.disruptor.eventhandler.ChannelUserEventHandler
import com.menuxx.miaosha.disruptor.eventhandler.ChannelUserEventPostObtainHandler
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */

class ChannelUserEventDisruptor(channelUserEventHandler: ChannelUserEventHandler, postObtainHandler: ChannelUserEventPostObtainHandler) {

    private val logger = LoggerFactory.getLogger(ChannelUserEventDisruptor::class.java)

    private val disruptor: Disruptor<ChannelUserEvent>

    private val threadCounter = AtomicInteger(1)

    private val byteBuffer = ByteBuffer.allocate(512)

    private val producer: ChannelUserEventProducer

    init {

        val ringBufferSize = 65536

        // 时间创建工厂
        val eventFactory = { ChannelUserEvent(null, null, null, null, ConfirmState.NoObtain, null) }

        // 线程工厂
        val threadFactory : (runnable: Runnable) -> Thread = { runnable -> Thread(runnable, "Channel-User-Event-Thread(" + threadCounter.incrementAndGet() + ")") }

        disruptor = Disruptor(eventFactory, ringBufferSize, threadFactory, ProducerType.MULTI, YieldingWaitStrategy())

        disruptor.setDefaultExceptionHandler(MyExceptionHandler("ChannelUserEvent"))

        disruptor.handleEventsWith(channelUserEventHandler).then(postObtainHandler)

        producer = ChannelUserEventProducer(disruptor.ringBuffer)

    }

    /**
     * 将抢购用户信息推入到高速缓存
     */
    fun product(userId: Int, channelId: Int, loopRefId: String) {
        byteBuffer.putInt(0, userId)
        byteBuffer.putInt(1, channelId)
        byteBuffer.put(ByteBuffer.wrap(loopRefId.toByteArray(Charset.forName("UTF-8"))))
        byteBuffer.flip()
        producer.product(byteBuffer)
    }

    fun start() {
        disruptor.start()
    }

    fun shutdown() {
        try {
            disruptor.shutdown(2, TimeUnit.SECONDS)
        } catch (ex: TimeoutException) {
            ex.printStackTrace()
            logger.error("ShutdownTimeout: ${ex.message}")
        }
    }

}