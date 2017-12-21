package com.menuxx.miaosha.queue.lisenter

import com.aliyun.openservices.ons.api.Action
import com.aliyun.openservices.ons.api.ConsumeContext
import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.MessageListener
import com.fasterxml.jackson.databind.ObjectMapper
import me.chanjar.weixin.mp.api.WxMpService

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 */
class WXMsgSenderListener(
        private val wxMpService: WxMpService,
        private val objectMapper: ObjectMapper
) : MessageListener {

    override fun consume(message: Message, context: ConsumeContext): Action {
        // wxMpService.templateMsgService.sendTemplateMsg()
        return Action.CommitMessage
    }

}