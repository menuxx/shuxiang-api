package com.menuxx.weixin.queue.listener

import com.aliyun.openservices.ons.api.*
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.aliyun.openservices.ons.api.exception.ONSClientException
import com.fasterxml.jackson.databind.ObjectMapper
import com.menuxx.common.bean.OrderCharge
import com.menuxx.common.db.OrderChargeDb
import com.menuxx.common.db.OrderDb
import com.menuxx.common.prop.AliyunProps
import com.menuxx.getQueryMap
import com.menuxx.miaosha.queue.MsgTags
import com.menuxx.miaosha.queue.msg.ObtainConsumedMsg
import com.menuxx.miaosha.store.ChannelUserStore
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 * 1：更新 t_order_charge 表中 outTradeNo 对应的记录
 */
@Component
class TradeOrderListener(
        @Autowired @Qualifier("obtainConsumeProducer") private val obtainConsumeProducer: ProducerBean,
        private val aliyunProps: AliyunProps,
        private val objectMapper: ObjectMapper,
        private val orderChargeDb: OrderChargeDb,
        private val orderDb: OrderDb
) : MessageListener {

    private val logger = LoggerFactory.getLogger(TradeOrderListener::class.java)

    override fun consume(message: Message, context: ConsumeContext): Action {
        // 传递过来的是 WxPayOrderNotifyResult 对象实体
        // 在系统中 OrderCharge 是 WxPayOrderNotifyResult 的子集，可以收集 WxPayOrderNotifyResult 中的大部分数据
        val payNotifyEvent = objectMapper.readValue(message.body, OrderCharge::class.java)
        orderChargeDb.updateChargeRecordByOutTradeNo(payNotifyEvent, payNotifyEvent.outTradeNo)
        val nextTag = message.getUserProperties("NextTag")
        return when ( nextTag  ) {
            // 方案待定
            MsgTags.TagObtainConsume -> {
                val attachData = getQueryMap(payNotifyEvent.attach)
                val channelId = attachData["channelId"]!!.toInt()
                val orderId = attachData["orderId"]!!.toInt()
                val userId = attachData["userId"]!!.toInt()

                // 确认订单支付
                orderDb.updateOrderPaid(orderId, 1)

                // 通知 完成 抢购 item 的消费
                // loopRefId 给 null，下面的处理环节 会自动 从 ChannelUserStore 中找到
                val msgbody = objectMapper.writeValueAsBytes(ObtainConsumedMsg(userId = userId, channelId = channelId, orderId = orderId, loopRefId = null))
                val successConsumeItemMsg = Message(aliyunProps.ons.consumeObtainTopicName, MsgTags.TagObtainConsume, "WXPayConsume_${payNotifyEvent.outTradeNo}", msgbody)
                successConsumeItemMsg.shardingKey = "Channel_$channelId"
                return try {
                    obtainConsumeProducer.send(successConsumeItemMsg)
                    Action.CommitMessage
                } catch (ex: ONSClientException) {
                    logger.error("ObtainConsumed: ObtainConsumedMsg(userId = $userId, $channelId = $channelId, orderId = $orderId)", ex)
                    Action.ReconsumeLater
                }
            }
            else -> Action.CommitMessage // 位置消息方形，防止出现无线堆积
        }

    }

}