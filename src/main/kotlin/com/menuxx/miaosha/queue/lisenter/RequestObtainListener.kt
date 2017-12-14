package com.menuxx.miaosha.queue.lisenter

import com.aliyun.openservices.ons.api.Action
import com.aliyun.openservices.ons.api.ConsumeContext
import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.MessageListener
import com.fasterxml.jackson.databind.ObjectMapper
import com.menuxx.miaosha.disruptor.ChannelUserEvent
import com.menuxx.miaosha.disruptor.ChannelUserEventDisruptor
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 * 将用户请求 持有商品 的请求投递到 disruptor 高速队列
 */
@Component
class RequestObtainListener(
        private val objectMapper: ObjectMapper,
        private val producerDisruptor: ChannelUserEventDisruptor
) : MessageListener {

    private val logger = LoggerFactory.getLogger(RequestObtainListener::class.java)

    // Message json { userId: Int, channelId: Int, loopRefId: String }
    override fun consume(message: Message, context: ConsumeContext): Action {
        val event = objectMapper.readValue(message.body, ChannelUserEvent::class.java)
        // 提交到 disruptor
        return try {
            producerDisruptor.product(userId = event.userId!!, channelId = event.channelId!!, loopRefId = event.loopRefId!!)
            Action.CommitMessage
        } catch (ex: Exception) {
            logger.error("ProducerDisruptor: ${ex.message}")
            Action.ReconsumeLater
        }
    }

}