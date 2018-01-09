package com.menuxx.miaosha.queue.publisher

import com.menuxx.miaosha.queue.msg.ObtainUserMsg
import org.springframework.amqp.AmqpException
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.support.CorrelationData
import org.springframework.stereotype.Component
import java.util.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/9
 * 微信: yin80871901
 */

@Component
class RequestObtainPublisher (private val rabbitTemplate: RabbitTemplate) {

    @Throws(AmqpException::class)
    fun sendUserObtainEvent(userId: Int, channelId: Int, loopRefId: String) : String {
        val ccData = CorrelationData()
        ccData.id = UUID.randomUUID().toString()
        rabbitTemplate.convertAndSend("request_obtain.queue", "disruptor.request_obtain", ObtainUserMsg(
                userId = userId, channelId = channelId, loopRefId = loopRefId
        ), ccData)
        return ccData.id
    }

}