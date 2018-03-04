package com.menuxx.weixin.queue.publisher

import com.menuxx.common.bean.OrderCharge
import org.springframework.amqp.AmqpException
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/9
 * 微信: yin80871901
 */

@Component
class TradeOrderPublisher ( private val rabbitTemplate: RabbitTemplate ) {

    @Throws(AmqpException::class)
    fun sendTradeOrderWithObtainConsume(orderCharge: OrderCharge) {
        rabbitTemplate.convertAndSend("wxpay.exchange", "wxpay.obtain_consume", orderCharge)
    }

    @Throws(AmqpException::class)
    fun sendTradeMallOrderPay(orderCharge: OrderCharge) {
        rabbitTemplate.convertAndSend("wxpay.exchange", "wxpay.mall_order_pay", orderCharge)
    }

}