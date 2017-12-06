package com.menuxx.ocapiserver.prop

import com.menuxx.ocapiserver.NoArg
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.NestedConfigurationProperty
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/5
 * 微信: yin80871901
 */
@ConfigurationProperties("ronglian")
@Component
@NoArg
data class RongLianProps(
        var accountSid: String, var authToken: String, var appId: String,
        @NestedConfigurationProperty
        var smsTpls: SMSTpls
)

@NoArg
data class SMSTpls(
        var captchaId: String,
        var buySuccessId: String
)