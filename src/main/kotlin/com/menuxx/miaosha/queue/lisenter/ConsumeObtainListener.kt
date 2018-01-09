package com.menuxx.miaosha.queue.lisenter

import com.menuxx.miaosha.disruptor.ChannelUserEventDisruptor
import com.menuxx.miaosha.queue.msg.ObtainConsumedMsg
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/9
 * 微信: yin80871901
 * 持有消费 信息 的消费者
 */
@Component
@RabbitListener( containerFactory = "rabbitListenerContainerFactory" )
class ConsumeObtainListener(private val producerDisruptor: ChannelUserEventDisruptor) {

    @RabbitListener(
            bindings = [
                (QueueBinding(
                    value = Queue(value = "consume_obtain.queue", durable = "true"),
                    exchange = Exchange(value = "consume_obtain.exchange", type = ExchangeTypes.FANOUT, durable = "true"),
                    key = "disruptor.consume_obtain"
                ))
            ]
    )
    @Throws(InterruptedException::class, IOException::class)
    fun onConsumeObtainEvent(@Payload msg: ObtainConsumedMsg) {
        producerDisruptor.product(userId = msg.userId, channelId = msg.channelId, orderId = msg.orderId, loopRefId = null)
    }

}