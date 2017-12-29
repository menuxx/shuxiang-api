package com.menuxx.code.factory

import com.menuxx.AllOpen
import com.menuxx.code.mq.OneBatch
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import java.io.IOException

/**
 * 一个批次的事务处理消息接收器
 */
@AllOpen
@RabbitListener(
        containerFactory = "rabbitListenerContainerFactory",
        bindings = [
            QueueBinding(
                    value = Queue("code_batch"),
                    exchange = Exchange(value = "", type = ExchangeTypes.FANOUT),
                    key = ""
            )
        ]
)
class BatchTransactionReceiver ( private val batchTransactionService: BatchTransactionService) {

    /**
     * 处理一个批次
     */
    @RabbitHandler
    @Throws(InterruptedException::class, IOException::class)
    fun doOneBatch(@Payload batch: OneBatch, @Header(AmqpHeaders.DELIVERY_TAG) deliverytag: Long) {
        batchTransactionService.doOneBatch(count = batch.count, startCode = batch.startCode, endCode = batch.endCode)
    }

}