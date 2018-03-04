package com.menuxx.weixin.queue.listener

import com.menuxx.common.bean.OrderCharge
import com.menuxx.common.db.MallOrderDb
import com.menuxx.common.db.OrderChargeDb
import com.menuxx.common.db.OrderDb
import com.menuxx.getQueryMap
import com.menuxx.mall.service.MallOrderService
import com.menuxx.miaosha.queue.publisher.ConsumeObtainPublisher
import org.slf4j.LoggerFactory
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
 * 1：更新 t_order_charge 表中 outTradeNo 对应的记录
 */

@Component
@RabbitListener( containerFactory = "rabbitListenerContainerFactory" )
class TradeOrderListener (
        private val orderChargeDb: OrderChargeDb,
        private val orderDb: OrderDb,
        private val consumeObtainPublisher: ConsumeObtainPublisher,
        private val mallOrderService: MallOrderService,
        private val mallOrderDb: MallOrderDb
) {

    private val logger = LoggerFactory.getLogger(TradeOrderListener::class.java)

    @RabbitListener(
            bindings = [
                (QueueBinding(
                        value = Queue("wxpay.queue", durable = "true"),
                        exchange = Exchange("wxpay.exchange", type = ExchangeTypes.FANOUT, durable = "true"),
                        key = "wxpay.obtain_consume"
                ))
            ]
    )
    @Throws(InterruptedException::class, IOException::class)
    fun onWXPayNotifyWithConsumeObtain(@Payload orderChange: OrderCharge) {
        logger.debug("onWXPayNotifyWith ConsumeObtain => " + orderChange)
        orderChargeDb.updateChargeRecordByOutTradeNo(orderChange, orderChange.outTradeNo)
        val attachData = getQueryMap(orderChange.attach)
        val channelId = attachData["channelId"]!!.toInt()
        val orderId = attachData["orderId"]!!.toInt()
        val userId = attachData["userId"]!!.toInt()
        // 确认订单支付
        orderDb.updateOrderPaid(orderId, 1)
        consumeObtainPublisher.sendConsumeObtainEvent(userId, channelId, orderId, null)
    }

    @RabbitListener(
            bindings = [
                (QueueBinding(
                        value = Queue("wxpay.queue", durable = "true"),
                        exchange = Exchange("wxpay.exchange", type = ExchangeTypes.FANOUT, durable = "true"),
                        key = "wxpay.mall_order_pay"
                ))
            ]
    )
    @Throws(InterruptedException::class, IOException::class)
    fun onWXPayNotifyWithMallOrderPay(@Payload orderChange: OrderCharge) {
        logger.debug("onWXPayNotifyWith MallOrderPay => " + orderChange)
        orderChargeDb.updateChargeRecordByOutTradeNo(orderChange, orderChange.outTradeNo)
        val attachData = getQueryMap(orderChange.attach)
        val localOrderNo = attachData["localOrderNo"] as String
        val order = mallOrderDb.getByOrderNo(orderNo = localOrderNo)!!
        val res = mallOrderService.submitCreateOrder(
                addressId = order.addressId,
                paymentMethodId = order.paymentMethodId,
                shipments = order.shipments,
                metaFields = order.metaFields
        )
        val orderId = res["order_id"] as Int
        mallOrderDb.updateYhsdOrderId(localOrderNo, orderId.toString())
    }

}