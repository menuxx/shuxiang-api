package com.menuxx.common.publisher

import com.menuxx.AllOpen
import com.menuxx.apiserver.queue.msg.DeliverySuccessMsg
import com.menuxx.apiserver.queue.msg.LoginCaptchaMsg
import com.menuxx.miaosha.queue.msg.ConsumeSuccessMsg
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/9
 * 微信: yin80871901
 */

@AllOpen
@Component
class SmsPublisher (
        private val rabbitTemplate: RabbitTemplate
) {

    fun sendCaptcha(mobile: String, captchaNo: String) {
        rabbitTemplate.convertAndSend("sms.exchange", "sms.send_captcha", LoginCaptchaMsg(mobile, captchaNo))
    }

    fun sendDelivery(mobile: String, expressName: String, expressNo: String) {
        rabbitTemplate.convertAndSend("sms.exchange", "sms.delivery_success", DeliverySuccessMsg(mobile, expressName, expressNo))
    }

    fun sendConsumeSuccess(mobile: String, itemName: String) {
        rabbitTemplate.convertAndSend("sms.exchange", "sms.consume_success", ConsumeSuccessMsg(mobile, itemName))
    }

}