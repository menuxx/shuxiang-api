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

        // # 全局无序 topic
        var publicTopic: String,
        var publicProducerId: String,
        var publicConsumerId: String,

        // # 分区有序 topic
        var shardingOrderTopic: String,
        var shardingOrderProducerId: String,
        var shardingOrderConsumerId: String
)