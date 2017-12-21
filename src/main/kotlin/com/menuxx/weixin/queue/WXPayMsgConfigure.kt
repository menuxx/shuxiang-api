package com.menuxx.weixin.queue

import com.aliyun.openservices.ons.api.MessageListener
import com.aliyun.openservices.ons.api.PropertyKeyConst
import com.aliyun.openservices.ons.api.bean.ConsumerBean
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.aliyun.openservices.ons.api.bean.Subscription
import com.menuxx.Const
import com.menuxx.common.prop.AliyunProps
import com.menuxx.miaosha.queue.MsgTags
import com.menuxx.miaosha.queue.lisenter.ObtainConsumeListener
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
        private val obtainConsumeListener: ObtainConsumeListener
) {

    /**
     * 将微信支付的信息推送到消息队列
     * 保证信息的高可靠
     */
    @Bean(name = ["wxPayMsgProducer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun producer() : ProducerBean {
        val producerBean = ProducerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ProducerId, aliyunProps.ons.publicProducerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        producerBean.properties = props
        return producerBean
    }
    /**
     * 注册消费持有的监听器
     * 抢书渠道支付消息接受
     */
    fun registerObtainConsumeListener(subscriptionTable: HashMap<Subscription, MessageListener>) {
        // 注册到支付 topic 中
        val sub1 = Subscription()
        sub1.topic = aliyunProps.ons.publicTopic
        // 只监听信息微信支付，并且为 消费持有的消息
        sub1.expression = MsgTags.TagObtainConsume
        subscriptionTable.put(sub1, obtainConsumeListener)
    }

    /**
     * 微信支付
     */
    @Bean(name = ["wxPayMsgConsumer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun consumer() : ConsumerBean {
        val consumerBean = ConsumerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ConsumerId, aliyunProps.ons.publicConsumerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        // 消费者的 特性在此配置
        consumerBean.properties = props
        // 初始化 hashMap
        consumerBean.subscriptionTable = hashMapOf()
        // 注册
        registerObtainConsumeListener(consumerBean.subscriptionTable as HashMap<Subscription, MessageListener>)
        return consumerBean
    }

}