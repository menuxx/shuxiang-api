package com.menuxx.code.factory

import com.menuxx.AllOpen
import com.menuxx.code.mq.OneBatch
import org.slf4j.LoggerFactory
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.*
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * 一个批次的事务处理消息接收器
 */
@AllOpen
@Component
class BatchTransactionReceiver ( private val batchTransactionService: BatchTransactionService) {

    private final val logger = LoggerFactory.getLogger(BatchTransactionReceiver::class.java)

    /**
     * 处理一个批次
     */
    @RabbitListener(
            containerFactory = "rabbitListenerContainerFactory",
            bindings = [
                QueueBinding(
                        value = Queue(value = "code_batch_queue", durable = "true"),
                        exchange = Exchange(value = "code_batch_exchange", type = ExchangeTypes.FANOUT, durable = "true"),
                        key = "code_batch_transaction"
                )
            ]
    )

    @Throws(InterruptedException::class, IOException::class)
    fun doOneBatch(@Payload batch: OneBatch) {
        batchTransactionService.doOneBatch(count = batch.count, startCode = batch.startCode, endCode = batch.endCode)
    }

}