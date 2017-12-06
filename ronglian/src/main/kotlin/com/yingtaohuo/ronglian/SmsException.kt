package com.yingtaohuo.ronglian

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/10/18
 * 微信: yin80871901
 */

class SmsException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, cause: Throwable?) : super(message, cause)
    constructor(cause: Throwable?) : super(cause)
    constructor(message: String?, cause: Throwable?, enableSuppression: Boolean, writableStackTrace: Boolean) : super(message, cause, enableSuppression, writableStackTrace)
}