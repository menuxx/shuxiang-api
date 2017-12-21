package com.menuxx.weixin.prop

import com.menuxx.NoArg
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/13
 * 微信: yin80871901
 */

@NoArg
@ConfigurationProperties("weixin")
data class WeiXinProps(
        @NestedConfigurationProperty
        var mp: WeiXinMpProps,
        @NestedConfigurationProperty
        var pay: WeiXinPayProps
)

@NoArg
data class WeiXinMpProps(
        /**
         * 设置微信公众号的appid
         */
        var appId: String,
        /**
         * 设置微信公众号的app secret
         */
        var appSecret: String,
        /**
         * 设置微信公众号的token
         */
        var token: String,
        /**
         * 设置微信公众号的EncodingAESKey
         */
        val aesKey: String
)

@NoArg
data class WeiXinPayProps(
        var mchId: String,
        var paySecret: String,
        var notifyUrl: String
)