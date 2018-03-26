package com.menuxx.miaosha.cfg

import com.menuxx.sso.bean.ApiResp
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/27
 * 微信: yin80871901
 */

@ControllerAdvice
class ExceptionHandlers {

    @ExceptionHandler(
            com.menuxx.common.exception.NotFoundException::class,
            com.menuxx.sso.exception.NotFoundException::class,
            com.menuxx.miaosha.exception.NotFoundException::class
    )
    fun notFoundException(ex: RuntimeException) : ApiResp {
        return ApiResp(404, ex.message ?: "未知异常")
    }

}