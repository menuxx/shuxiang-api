package com.menuxx.miaosha.queue.lisenter

import com.aliyun.openservices.ons.api.Action
import com.aliyun.openservices.ons.api.ConsumeContext
import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.MessageListener
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/9
 * 微信: yin80871901
 * 持有消费 信息 的消费者
 */
@Component
class TradeObtainListener : MessageListener {

    override fun consume(message: Message, context: ConsumeContext): Action {
        return Action.CommitMessage
    }

}