package com.menuxx.weixin.queue

import com.aliyun.openservices.ons.api.MessageListener
import com.aliyun.openservices.ons.api.PropertyKeyConst
import com.aliyun.openservices.ons.api.bean.ConsumerBean
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.aliyun.openservices.ons.api.bean.Subscription
import com.menuxx.common.prop.AliyunProps
import com.menuxx.miaosha.queue.MsgTags
import com.menuxx.weixin.queue.listener.TradeOrderListener
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/9
 * 微信: yin80871901
 */

@Configuration
class WXPayMsgConfigure(
        private val aliyunProps: AliyunProps,
        private val tradeOrderListener: TradeOrderListener
) {

    /**
     * 将微信支付的信息推送到消息队列
     * 保证信息的高可靠
     */
    @Bean(name = ["wxPayMsgProducer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun producer() : ProducerBean {
        val producerBean = ProducerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ProducerId, aliyunProps.ons.payTopicName)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        producerBean.properties = props
        return producerBean
    }

    /**
     * 微信支付订单回调
     */
    fun registerTradeOrderListener(subscriptionTable: HashMap<Subscription, MessageListener>) {
        val sub = Subscription()
        sub.topic = aliyunProps.ons.payTopicName
        sub.expression = MsgTags.TagTradeOrder
        subscriptionTable.put(sub, tradeOrderListener)
    }

    /**
     * 微信支付
     */
    @Bean(name = ["wxPayMsgConsumer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun consumer() : ConsumerBean {
        val consumerBean = ConsumerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ConsumerId, aliyunProps.ons.payConsumerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        // 消费者的 特性在此配置
        consumerBean.properties = props
        // 初始化 hashMap
        consumerBean.subscriptionTable = hashMapOf()
        // 注册
        registerTradeOrderListener(consumerBean.subscriptionTable as HashMap<Subscription, MessageListener>)
        return consumerBean
    }

}