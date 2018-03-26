package com.menuxx.mall.exception

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/23
 * 微信: yin80871901
 */

class YouHaoException(val code: Int, message: String?, val body: Map<String, Any?>?) : RuntimeException(message)