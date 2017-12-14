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
class MQConfigure(
        private val aliyunProps: AliyunProps,
        private val requestObtainListener: RequestObtainListener
) {

    @Bean(name = ["channelStatePublishProducer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun channelStatePublishProducer() : ProducerBean {
        val producerBean = ProducerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ProducerId, aliyunProps.ons.channelStateProducerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        producerBean.properties = props
        return producerBean
    }

    /**
     * 渠道抢购
     * 将所有 抢购用户 的信息推送到 消息队列
     */
    @Bean(name = ["channelUserProducer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun channelUserProducer() : ProducerBean {
        val producerBean = ProducerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ProducerId, aliyunProps.ons.obtainItemTopicName)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        producerBean.properties = props
        return producerBean
    }

    /**
     * 注册请求持有的监听器
     */
    fun registerRequestObtainListener(listener: RequestObtainListener, subscriptionTable: HashMap<Subscription, MessageListener>) {
        val sub = Subscription()
        sub.topic = aliyunProps.ons.obtainItemTopicName
        // 只监来自服务好的消息
        sub.expression = "FromServiceNo"
        subscriptionTable.put(sub, listener)
    }

    /**
     * 渠道抢购
     * 将所有 抢购用户 的信息推送到 消息队列
     */
    @Bean(name = ["channelUserConsumer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun channelUserConsumer() : ConsumerBean {
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
        registerRequestObtainListener(requestObtainListener, consumerBean.subscriptionTable as HashMap<Subscription, MessageListener>)
        return consumerBean
    }

}