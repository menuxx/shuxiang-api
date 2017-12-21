package com.menuxx.miaosha.queue.lisenter

import com.aliyun.openservices.ons.api.Action
import com.aliyun.openservices.ons.api.ConsumeContext
import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.MessageListener
import com.fasterxml.jackson.databind.ObjectMapper
import com.menuxx.common.sms.SMSSender
import com.menuxx.miaosha.queue.MsgTags
import com.menuxx.miaosha.queue.msg.ConsumeSuccessMsg
import com.menuxx.miaosha.queue.msg.DeliverySuccessMsg
import com.yingtaohuo.ronglian.SmsException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 *
 * 短信发送监听器
 */
@Component
class SmsSenderListener(
        private val objectMapper: ObjectMapper,
        private val smsSender: SMSSender
) : MessageListener {

    private val logger = LoggerFactory.getLogger(SmsSenderListener::class.java)

    override fun consume(message: Message, context: ConsumeContext): Action {
        val nextTag = message.getUserProperties("NextTag")
        return when ( nextTag ) {
            MsgTags.TagConsumeSuccess -> {
                // 你成功获得了一本新书{1}，随后我们会寄出到你填写的地址，请保持{2}联系畅通。
                val msg = objectMapper.readValue(message.body, ConsumeSuccessMsg::class.java)
                return try {
                    smsSender.sendConsumeSuccess(mobile = msg.mobile, data = arrayOf(msg.itemName, msg.receiverPhoneNumber))
                    Action.CommitMessage
                } catch (ex: SmsException) {
                    logger.error("发送消费成功信息($msg)失败：${ex.message}", ex)
                    Action.ReconsumeLater
                }
            }
            MsgTags.TagDeliverySuccess -> {
                // 【网优中心】你成功获得了一本新书{1}，随后我们会寄出到你填写的地址，请保持{2}联系畅通。
                val msg = objectMapper.readValue(message.body, DeliverySuccessMsg::class.java)
                return try {
                    smsSender.sendConsumeSuccess(mobile = msg.mobile, data = arrayOf(msg.expressName, msg.expressNo))
                    Action.CommitMessage
                } catch (ex: SmsException) {
                    logger.error("发送配货信息($msg)失败：${ex.message}", ex)
                    Action.ReconsumeLater
                }
            } else -> Action.CommitMessage
        }
    }

}