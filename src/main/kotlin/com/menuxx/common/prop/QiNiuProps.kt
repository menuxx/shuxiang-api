package com.menuxx.common.prop

import com.menuxx.NoArg
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/5
 * 微信: yin80871901
 */
@Component
@ConfigurationProperties("qiniu")
@NoArg
data class QiNiuProps(var bucket: String, var accessKey: String, var secretKey: String)