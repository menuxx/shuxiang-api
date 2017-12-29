package com.menuxx.code.mq

import com.menuxx.AllOpen
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component


@AllOpen
@Component
class CodeOnceBatchPublisher (
        private val msgTpl: RabbitTemplate
) {

    private final val exchange = ""

    private final val routingKey = ""

    /**
     * 发送一个批处理
     */
    fun sendOneBatch(data: OneBatch) {
        msgTpl.convertAndSend(exchange, routingKey, data)
    }

}