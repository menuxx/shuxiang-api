package com.menuxx.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/24
 * 微信: yin80871901
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
class ResourceNotFoundException : RuntimeException {
    constructor() : super()
    constructor(message: String?) : super(message)
    constructor(message: String?, error: String, status: Int) : super(message)
}