package com.menuxx.mall.prop

import com.menuxx.NoArg
import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/18
 * 微信: yin80871901
 */
@NoArg
@ConfigurationProperties("youhao")
data class YouhaoProps(var appKey: String, var appSecret: String)