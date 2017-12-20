package com.menuxx.weixin.auth

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/18
 * 微信: yin80871901
 */

class AuthenticationTokenFilter(
        private val tokenHeader: String,
        private val userDetailsService: UserDetailsService,
        private val tokenProcessor: TokenProcessor
) : UsernamePasswordAuthenticationFilter() {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest
        val res = response as HttpServletResponse
        val authToken = req.getHeader(tokenHeader)
        if ( authToken != null ) {
            // 移除 开头的 `token ` 字符
            val token = authToken.substring(6)
            // 从用户传来的 token 中获取 openid
            val openId = tokenProcessor.getOpenIdFromToken(token)
            // 如果该用户需要登录
            if ( openId != null && SecurityContextHolder.getContext().authentication == null ) {
                val authUser = userDetailsService.loadUserByUsername(openId) as AuthUser
                // 验证该用户的 token 是否正确
                if ( tokenProcessor.validateToken(token, authUser) ) {
                    val authentication = UsernamePasswordAuthenticationToken(authUser, null, authUser.authorities)
                    SecurityContextHolder.getContext().authentication = authentication
                }
            }
        }
        chain.doFilter(request, response)
    }

}