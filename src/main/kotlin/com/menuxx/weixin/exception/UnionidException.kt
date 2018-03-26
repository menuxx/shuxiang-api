package com.menuxx.weixin.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/26
 * 微信: yin80871901
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
class UnionidException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
}