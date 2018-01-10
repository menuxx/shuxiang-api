package com.menuxx.miaosha.disruptor

import com.lmax.disruptor.YieldingWaitStrategy
import com.lmax.disruptor.dsl.Disruptor
import com.lmax.disruptor.dsl.ProducerType
import com.menuxx.miaosha.disruptor.eventhandler.ChannelUserEventHandler
import com.menuxx.miaosha.disruptor.eventhandler.ChannelUserEventPostObtainHandler
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.nio.ByteBuffer
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicInteger
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */
@Component
class ChannelUserEventDisruptor(channelUserEventHandler: ChannelUserEventHandler, postObtainHandler: ChannelUserEventPostObtainHandler) {

    private val logger = LoggerFactory.getLogger(ChannelUserEventDisruptor::class.java)

    private final val disruptor: Disruptor<ChannelUserEvent>

    private final val threadCounter = AtomicInteger(1)

    private final val producer: ChannelUserEventProducer

    init {

        val ringBufferSize = 65536

        // 事件创建工厂
        val eventFactory = { ChannelUserEvent(userId = 0, channelId = 0, channelItemId = null, loopRefId = null, confirmState = ConfirmState.NoObtain, orderId = null, queueNum = null) }

        // 线程工厂
        val threadFactory : (runnable: Runnable) -> Thread = { runnable -> Thread(runnable, "Channel-User-Event-Thread(" + threadCounter.incrementAndGet() + ")") }

        disruptor = Disruptor(eventFactory, ringBufferSize, threadFactory, ProducerType.MULTI, YieldingWaitStrategy())

        disruptor.setDefaultExceptionHandler(MyExceptionHandler("ChannelUserEvent"))

        disruptor.handleEventsWith(channelUserEventHandler).then(postObtainHandler)

        producer = ChannelUserEventProducer(disruptor.ringBuffer)

    }

    /**
     * 将抢购用户信息推入到高速缓存
     * loopRefId 在第一次的时候会有 loopRefId
     */
    fun product(userId: Int, channelId: Int, loopRefId: String?, orderId: Int) {
        val byteBuffer = ByteBuffer.allocate(256)
        byteBuffer.putInt(userId)
        byteBuffer.putInt(channelId)
        byteBuffer.putInt(orderId)
        if ( loopRefId != null ) {
            byteBuffer.put(ByteBuffer.wrap(loopRefId.toByteArray(Charset.forName("UTF-8"))))
        }
        byteBuffer.flip()
        producer.product(byteBuffer)
    }

    @PostConstruct
    fun start() {
        disruptor.start()
    }

    @PreDestroy
    fun shutdown() {
        try {
            disruptor.shutdown(2, TimeUnit.SECONDS)
        } catch (ex: TimeoutException) {
            ex.printStackTrace()
            logger.error("ShutdownTimeout: ${ex.message}")
        }
    }

}