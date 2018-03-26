package com.menuxx.sso.service

import com.menuxx.sso.exception.NotFoundException
import com.menuxx.common.db.OrderDb
import com.menuxx.common.publisher.SmsPublisher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/23
 * 微信: yin80871901
 */

@Service
class OrderService(
        private val orderDb: OrderDb,
        private val smsPublisher: SmsPublisher
) {

    @Transactional
    fun doDoDelivery(orderId: Int, expressNo: String, expressName: String) {
        val order = orderDb.getOrderById(orderId) ?: throw NotFoundException("订单不存在")
        val rNum = orderDb.updateOrderExpress(orderId, expressNo, expressName)
        if ( rNum > 0 ) {
            smsPublisher.sendDelivery(order.receiverPhoneNumber, expressName, expressNo)
        }
    }

}