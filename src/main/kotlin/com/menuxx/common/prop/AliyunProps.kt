package com.menuxx.common.prop

import com.menuxx.NoArg
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/9
 * 微信: yin80871901
 */

@NoArg
@Component
@ConfigurationProperties("aliyun")
data class AliyunProps(
        @NestedConfigurationProperty
        var ons: AliyunONSProps
)

@NoArg
data class AliyunONSProps(
        var accessKeyId: String, var accessKeySecret: String,

        var payTopicName: String,
        var payProducerId: String,
        var payConsumerId: String,

        var obtainItemTopicName: String,
        var obtainItemProducerId: String,
        var obtainItemConsumerId: String,

        var consumeObtainTopicName: String,
        var consumeObtainProducerId: String,
        var consumeObtainConsumerId: String
)