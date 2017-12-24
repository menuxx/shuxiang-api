package com.menuxx.weixin.prop

import com.menuxx.NoArg
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/18
 * 微信: yin80871901
 */
@NoArg
@ConfigurationProperties("auth-token")
data class AuthTokenProps(
        var secret: String,
        var header: String,
        var expiration: Int
)