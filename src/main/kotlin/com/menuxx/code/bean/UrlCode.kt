package com.menuxx.code.bean

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/18
 * 微信: yin80871901
 */

data class UrlCode(
        val domain: String,
        val channel: String,
        val code: String,
        val salt: String
)