package com.menuxx.apiserver.cfg

import com.aliyun.openservices.ons.api.MessageListener
import com.aliyun.openservices.ons.api.PropertyKeyConst
import com.aliyun.openservices.ons.api.bean.ConsumerBean
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.aliyun.openservices.ons.api.bean.Subscription
import com.menuxx.common.prop.AliyunProps
import com.menuxx.miaosha.queue.MsgTags
import com.menuxx.apiserver.queue.listener.SmsSenderListener
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 *
 * 消费者与生产者分开注册
 * 防止出现
 */
@Configuration
class CommonMQConfigure(
        private val aliyunProps: AliyunProps,
        private val smsSenderListener: SmsSenderListener
) {

    @Bean(name = ["senderConsumer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun senderConsumer() : ConsumerBean {
        val consumerBean = ConsumerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ConsumeThreadNums, "40")
        props.setProperty(PropertyKeyConst.ConsumerId, aliyunProps.ons.senderConsumerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        consumerBean.properties = props
        // 初始化 hashMap
        consumerBean.subscriptionTable = hashMapOf()
        registerSmsSenderListener(consumerBean.subscriptionTable as HashMap<Subscription, MessageListener>)
        return consumerBean
    }

    fun registerSmsSenderListener(subscriptionTable: HashMap<Subscription, MessageListener>) {
        val sub = Subscription()
        sub.topic = aliyunProps.ons.senderTopicName
        // 只监来自服务号的消息
        sub.expression = MsgTags.TagSmsSender
        subscriptionTable.put(sub, smsSenderListener)
    }

    @Bean(name = ["senderProducer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun senderProducer() : ProducerBean {
        val producerBean = ProducerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ProducerId, aliyunProps.ons.senderProducerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        producerBean.properties = props
        return producerBean
    }

}