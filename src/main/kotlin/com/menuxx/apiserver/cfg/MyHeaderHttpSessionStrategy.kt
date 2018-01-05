package com.menuxx.apiserver.cfg

import org.springframework.session.Session
import org.springframework.session.web.http.HttpSessionStrategy
import org.springframework.util.Assert
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/5
 * 微信: yin80871901
 */

class MyHeaderHttpSessionStrategy : HttpSessionStrategy {

    private var headerName = "X-SessionID"

    override fun getRequestedSessionId(request: HttpServletRequest): String {
        return request.getHeader(this.headerName)
    }

    override fun onNewSession(session: Session, request: HttpServletRequest, response: HttpServletResponse) {
        response.setHeader(this.headerName, session.id)
    }

    override fun onInvalidateSession(request: HttpServletRequest, response: HttpServletResponse) {
        response.setHeader(this.headerName, "")
    }

    /**
     * The name of the header to obtain the session id from. Default is "x-auth-token".
     *
     * @param headerName the name of the header to obtain the session id from.
     */
    fun setHeaderName(headerName: String) {
        Assert.notNull(headerName, "headerName cannot be null")
        this.headerName = headerName
    }

}