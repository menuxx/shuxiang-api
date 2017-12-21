package com.menuxx.miaosha.disruptor

import com.lmax.disruptor.EventTranslatorOneArg
import com.lmax.disruptor.RingBuffer
import java.nio.ByteBuffer
import java.nio.charset.Charset

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */
class ChannelUserEventTranslator : EventTranslatorOneArg<ChannelUserEvent, ByteBuffer> {
    override fun translateTo(event: ChannelUserEvent, sequence: Long, byteBuffer: ByteBuffer) {
        event.userId = byteBuffer.int
        event.channelId = byteBuffer.int
        event.orderId = byteBuffer.int
        event.loopRefId = Charset.forName("UTF-8").decode(byteBuffer).toString()
    }
}

class ChannelUserEventProducer(private val ringBuffer: RingBuffer<ChannelUserEvent>) {

    private val translator = ChannelUserEventTranslator()

    fun product(bb: ByteBuffer) {
        ringBuffer.publishEvent<ByteBuffer>(translator, bb)
    }

}