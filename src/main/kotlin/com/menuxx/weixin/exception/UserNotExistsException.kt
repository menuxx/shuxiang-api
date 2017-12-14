package com.menuxx.weixin.exception

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/14
 * 微信: yin80871901
 */

class UserNotExistsException : RuntimeException {
    constructor() : super()
    constructor(message: String) : super(message)
}