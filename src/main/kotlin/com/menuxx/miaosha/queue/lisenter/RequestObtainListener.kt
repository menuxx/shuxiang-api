package com.menuxx.miaosha.queue.lisenter

import com.menuxx.miaosha.disruptor.ChannelUserEventDisruptor
import com.menuxx.miaosha.queue.msg.ObtainUserMsg
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 * 将用户请求 持有商品 的请求投递到 disruptor 高速队列
 */

@Component
@RabbitListener( containerFactory = "rabbitListenerContainerFactory" )
class RequestObtainListener (
        private val producerDisruptor: ChannelUserEventDisruptor
) {

    @RabbitListener(
            bindings = [
                (QueueBinding(
                        value = Queue("request_obtain.queue", durable = "true"),
                        exchange = Exchange("request_obtain.queue", type = ExchangeTypes.FANOUT, durable = "true"),
                        key = "disruptor.request_obtain"
                ))
            ]
    )
    @Throws(InterruptedException::class, IOException::class)
    fun onRequestObtainEvent(obtainUser: ObtainUserMsg) {
        producerDisruptor.product(userId = obtainUser.userId, channelId = obtainUser.channelId, orderId = 0, loopRefId = obtainUser.loopRefId)
    }

}