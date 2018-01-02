package com.menuxx.miaosha.queue

import com.aliyun.openservices.ons.api.MessageListener
import com.aliyun.openservices.ons.api.PropertyKeyConst
import com.aliyun.openservices.ons.api.bean.ConsumerBean
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.aliyun.openservices.ons.api.bean.Subscription
import com.menuxx.common.prop.AliyunProps
import com.menuxx.miaosha.queue.lisenter.RequestObtainListener
import org.springframework.beans.factory.annotation.Autowire
import com.menuxx.AllOpen
import com.menuxx.miaosha.queue.lisenter.ObtainConsumeListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */

@AllOpen
@Configuration
class MiaoShaMQConfigure(
        private val aliyunProps: AliyunProps,
        private val requestObtainListener: RequestObtainListener,
        private val obtainConsumeListener: ObtainConsumeListener
) {

    /**
     * 已经抢购到名额，发送消费该名额的 信息
     * 1。支付的回调 队列监听器中会发送消息（需要支付的情况下）
     * 2。不需要支付的情况下，会在控制其中直接发送消费一个持有的请求
     */
    @Bean(name = ["obtainConsumeProducer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun obtainConsumeProducer() : ProducerBean {
        val producerBean = ProducerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ProducerId, aliyunProps.ons.consumeObtainProducerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        producerBean.properties = props
        return producerBean
    }

    /**
     * 渠道抢购
     * 将所有 抢购用户 的信息推送到 消息队列
     * 消费 支付免支付，抢购的消费者
     */
    @Bean(name = ["obtainConsumeConsumer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun obtainConsumeConsumer() : ConsumerBean {
        val consumerBean = ConsumerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ConsumerId, aliyunProps.ons.consumeObtainConsumerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        // 消费者的 特性在此配置
        consumerBean.properties = props
        // 初始化 hashMap
        consumerBean.subscriptionTable = hashMapOf()
        // 注册 obtainConsumedListener
        registerObtainConsumeListener(consumerBean.subscriptionTable as HashMap<Subscription, MessageListener>)
        return consumerBean
    }

    /**
     * 注册请求持有的监听器
     */
    fun registerObtainConsumeListener(subscriptionTable: HashMap<Subscription, MessageListener>) {
        val sub = Subscription()
        sub.topic = aliyunProps.ons.consumeObtainTopicName
        // 只监来自服务号的消息
        sub.expression = MsgTags.TagObtainConsume
        subscriptionTable.put(sub, obtainConsumeListener)
    }

    /**
     * 渠道抢购
     * 将所有 抢购用户 的信息推送到 消息队列
     */
    @Bean(name = ["requestObtainProducer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun requestObtainProducer() : ProducerBean {
        val producerBean = ProducerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ProducerId, aliyunProps.ons.obtainItemProducerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        producerBean.properties = props
        return producerBean
    }

    /**
     * 注册请求持有的监听器
     */
    fun registerRequestObtainListener(subscriptionTable: HashMap<Subscription, MessageListener>) {
        val sub = Subscription()
        sub.topic = aliyunProps.ons.obtainItemTopicName
        // 只监来自服务号的消息
        sub.expression = MsgTags.TagFromMpRequestObtain
        subscriptionTable.put(sub, requestObtainListener)
    }

    /**
     * 渠道抢购
     * 将所有 抢购用户 的信息推送到 消息队列
     */
    @Bean(name = ["requestObtainConsumer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun requestObtainConsumer() : ConsumerBean {
        val consumerBean = ConsumerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ConsumerId, aliyunProps.ons.obtainItemConsumerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        // 消费者的 特性在此配置
        consumerBean.properties = props
        // 初始化 hashMap
        consumerBean.subscriptionTable = hashMapOf()
        // 注册
        registerRequestObtainListener(consumerBean.subscriptionTable as HashMap<Subscription, MessageListener>)
        return consumerBean
    }

}