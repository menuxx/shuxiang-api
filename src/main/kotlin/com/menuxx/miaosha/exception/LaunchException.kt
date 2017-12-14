package com.menuxx.miaosha.exception

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */

class LaunchException(val code: Int, message: String?) : RuntimeException(message)