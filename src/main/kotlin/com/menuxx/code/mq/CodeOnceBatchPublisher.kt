package com.menuxx.code.mq

import com.menuxx.AllOpen

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component


@AllOpen
@Component
class CodeOnceBatchPublisher (
        private val rabbitTemplate: RabbitTemplate
) {

    private final val exchange = "code_batch.exchange"

    private final val routingKey = "code_batch_transaction"

    /**
     * 发送一个批处理
     */
    fun sendOneBatch(data: OneBatch) {
        rabbitTemplate.convertAndSend(exchange, routingKey, data)
    }

}