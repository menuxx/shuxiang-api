package com.menuxx.miaosha.queue.publisher

import com.menuxx.miaosha.queue.msg.ObtainConsumedMsg
import org.springframework.amqp.AmqpException
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/9
 * 微信: yin80871901
 */


@Component
class ConsumeObtainPublisher ( private val rabbitTemplate: RabbitTemplate ) {

    @Throws(AmqpException::class)
    fun sendConsumeObtainEvent(userId: Int, channelId: Int, orderId: Int, loopRefId: String?) {
        rabbitTemplate.convertAndSend("wxpay.exchange", "wxpay.obtain_consume",
                ObtainConsumedMsg(userId, channelId, orderId, loopRefId)
        )
    }

}