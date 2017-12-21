package com.menuxx.apiserver.cfg

import com.aliyun.openservices.ons.api.PropertyKeyConst
import com.aliyun.openservices.ons.api.bean.ProducerBean
import com.menuxx.common.prop.AliyunProps
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.*

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/21
 * 微信: yin80871901
 */
@Configuration
class CommonMQConfigure(
        private val aliyunProps: AliyunProps
) {

    @Bean(name = ["publicProducer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun publicProducer() : ProducerBean {
        val producerBean = ProducerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ProducerId, aliyunProps.ons.publicProducerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        producerBean.properties = props
        return producerBean
    }

    @Bean(name = ["publicConsumer"], autowire = Autowire.BY_NAME, initMethod = "start", destroyMethod = "shutdown")
    fun publicConsumer() : ProducerBean {
        val producerBean = ProducerBean()
        val props = Properties()
        props.setProperty(PropertyKeyConst.ConsumerId, aliyunProps.ons.publicConsumerId)
        props.setProperty(PropertyKeyConst.AccessKey, aliyunProps.ons.accessKeyId)
        props.setProperty(PropertyKeyConst.SecretKey, aliyunProps.ons.accessKeySecret)
        producerBean.properties = props
        return producerBean
    }



}