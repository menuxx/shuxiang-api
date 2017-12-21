package com.menuxx.common.exception

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */
class NotFoundException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
}