package com.menuxx.apiserver.service

import com.aliyun.openservices.ons.api.Message
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.fasterxml.jackson.databind.ObjectMapper
import com.menuxx.apiserver.exception.NotFoundException
import com.menuxx.apiserver.queue.msg.DeliverySuccessMsg
import com.menuxx.common.db.OrderDb
import com.menuxx.common.prop.AliyunProps
import com.menuxx.miaosha.queue.MsgTags
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/23
 * 微信: yin80871901
 */

@Service
class OrderService(
        private val aliyunProps: AliyunProps,
        private val orderDb: OrderDb,
        private val objectMapper: ObjectMapper,
        @Autowired @Qualifier("senderProducer") private val senderProducer: ProducerBean
) {

    @Transactional
    fun doDoDelivery(orderId: Int, expressNo: String, expressName: String) {
        val order = orderDb.getOrderById(orderId) ?: throw NotFoundException("订单不存在")
        val rNum = orderDb.updateOrderExpress(orderId, expressNo, expressName)
        if ( rNum > 0 ) {
            val msgBody = objectMapper.writeValueAsBytes(DeliverySuccessMsg(mobile = order.receiverPhoneNumber, expressNo = order.expressNo, expressName = order.expressName))
            val smsMsg = Message(aliyunProps.ons.senderTopicName, MsgTags.TagSmsSender, "OrderDelivery_$orderId", msgBody)
            smsMsg.putUserProperties("NextTag", MsgTags.TagDeliverySuccess)
            senderProducer.send(smsMsg)
        }
    }

}