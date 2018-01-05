package com.menuxx.common.prop

import com.menuxx.NoArg
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/5
 * 微信: yin80871901
 */

@NoArg
@ConfigurationProperties("app")
class AppProps(val codeBaseUrl: String)