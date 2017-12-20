package com.menuxx.weixin.auth

import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/18
 * 微信: yin80871901
 */

@Component
class EntryPointUnauthorizedHandler : AuthenticationEntryPoint {

    override fun commence(request: HttpServletRequest, response: HttpServletResponse, ex: AuthenticationException) {
        ex.printStackTrace()
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Access Denied")
    }

}