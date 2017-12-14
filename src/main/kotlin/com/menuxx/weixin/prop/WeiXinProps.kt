package com.menuxx.weixin.prop

import com.menuxx.NoArg
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/13
 * 微信: yin80871901
 */

@NoArg
@ConfigurationProperties("weixin.mp")
data class WeiXinProps(
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
