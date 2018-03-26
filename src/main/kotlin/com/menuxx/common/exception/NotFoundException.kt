package com.menuxx.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/8
 * 微信: yin80871901
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class NotFoundException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
}