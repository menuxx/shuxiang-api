package com.menuxx.sso.cfg

import com.menuxx.sso.auth.TokenProcessor
import org.springframework.security.core.token.Sha512DigestUtils
import org.springframework.session.Session
import org.springframework.session.web.http.HttpSessionStrategy
import org.springframework.util.Assert
import org.springframework.util.StringUtils
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/5
 * 微信: yin80871901
 */

class MyHeaderHttpSessionStrategy(private val tokenProcessor: TokenProcessor) : HttpSessionStrategy {

    private var headerName = "X-Authorization"

    override fun getRequestedSessionId(request: HttpServletRequest): String {
        val authToken = request.getHeader(headerName)
        if ( !StringUtils.isEmpty(authToken) && authToken.length > 6 ) {
            val token = authToken.substring(6)
            if (StringUtils.isEmpty(token)) {
                throw IllegalArgumentException("安全认证令牌格式错误")
            }
            val principal = tokenProcessor.getPrincipalFromToken(token)
            return Sha512DigestUtils.shaHex(principal)
        }
        return ""
    }

    override fun onNewSession(session: Session, request: HttpServletRequest, response: HttpServletResponse) {
        response.setHeader(headerName, session.id)
    }

    override fun onInvalidateSession(request: HttpServletRequest, response: HttpServletResponse) {
        response.setHeader(headerName, "")
    }

    /**
     * The name of the header to obtain the session id from. Default is "x-auth-token".
     *
     * @param headerName the name of the header to obtain the session id from.
     */
    fun setHeaderName(headerName: String) {
        Assert.notNull(headerName, "headerName $headerName cannot be null")
        this.headerName = headerName
    }

}