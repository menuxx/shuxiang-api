package com.menuxx.apiserver.queue.listener

import com.menuxx.AllOpen
import com.menuxx.apiserver.queue.msg.DeliverySuccessMsg
import com.menuxx.apiserver.queue.msg.LoginCaptchaMsg
import com.menuxx.common.sms.SMSSender
import com.menuxx.miaosha.queue.msg.ConsumeSuccessMsg
import org.springframework.amqp.core.ExchangeTypes
import org.springframework.amqp.rabbit.annotation.Exchange
import org.springframework.amqp.rabbit.annotation.Queue
import org.springframework.amqp.rabbit.annotation.QueueBinding
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component
import java.io.IOException

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 *
 * 短信发送监听器
 */
@AllOpen
@Component
@RabbitListener( containerFactory = "rabbitListenerContainerFactory" )
class SmsSenderListener1 ( private val smsSender: SMSSender ) {

    @RabbitListener(
            bindings = [
                (QueueBinding(
                    value = Queue(value = "sms.queue", durable = "true"),
                    exchange = Exchange(value = "sms.exchange", type = ExchangeTypes.FANOUT, durable = "true"),
                    key = "sms.send_captcha"
                ))
            ]
    )
    @Throws(InterruptedException::class, IOException::class)
    fun sendCaptcha(@Payload msg: LoginCaptchaMsg) {
        smsSender.sendCaptcha(mobile = msg.mobile, data = arrayOf(msg.captchaNo))
    }

    @RabbitListener(
            bindings = [
                (QueueBinding(
                        value = Queue(value = "sms.queue", durable = "true"),
                        exchange = Exchange(value = "sms.exchange", type = ExchangeTypes.FANOUT, durable = "true"),
                        key = "sms.delivery_success"
                ))
            ]
    )
    @Throws(InterruptedException::class, IOException::class)
    fun sendDeliverySuccess(@Payload msg: DeliverySuccessMsg) {
        smsSender.sendDeliverySuccess(mobile = msg.mobile, data = arrayOf(msg.expressName, msg.expressNo))
    }

    @RabbitListener(
            bindings = [
                (QueueBinding(
                        value = Queue(value = "sms.queue", durable = "true"),
                        exchange = Exchange(value = "sms.exchange", type = ExchangeTypes.FANOUT, durable = "true"),
                        key = "sms.consume_success"
                ))
            ]
    )
    @Throws(InterruptedException::class, IOException::class)
    fun sendConsumeSuccess(@Payload msg: ConsumeSuccessMsg) {
        smsSender.sendConsumeSuccess(mobile = msg.mobile, data = arrayOf(msg.itemName))
    }

}