package com.menuxx.miaosha.queue.lisenter

import com.aliyun.openservices.ons.api.Action
import com.aliyun.openservices.ons.api.ConsumeContext
import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.MessageListener
import com.fasterxml.jackson.databind.ObjectMapper
import com.menuxx.miaosha.disruptor.ChannelUserEventDisruptor
import com.menuxx.miaosha.queue.msg.ObtainConsumedMsg
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/9
 * 微信: yin80871901
 * 持有消费 信息 的消费者
 */
@Component
class ObtainConsumeListener(
        private val objectMapper: ObjectMapper,
        private val producerDisruptor: ChannelUserEventDisruptor
        ) : MessageListener {
    /**
     * 消息结构
     * ObtainConsumedMsg
     */
    override fun consume(message: Message, context: ConsumeContext): Action {
        val consumed = objectMapper.readValue(message.body, ObtainConsumedMsg::class.java)
        // 价格消息转发到 disruptor
        producerDisruptor.product(userId = consumed.userId, channelId = consumed.channelId, orderId = consumed.orderId, loopRefId = null)
        return Action.CommitMessage
    }

}