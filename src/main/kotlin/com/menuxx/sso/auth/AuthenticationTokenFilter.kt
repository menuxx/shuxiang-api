package com.menuxx.sso.auth

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import javax.servlet.FilterChain
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.HttpServletRequest

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/18
 * 微信: yin80871901
 */

class AuthenticationTokenFilter(
        private val tokenHeader: String,
        private val wxUserDetailsService: UserDetailsService,
        private val merchantUserDetailService: UserDetailsService,
        private val adminUserDetailsService : UserDetailsService,
        private val tokenProcessor: TokenProcessor
) : UsernamePasswordAuthenticationFilter() {

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val req = request as HttpServletRequest
        val authToken = req.getHeader(tokenHeader)
        if ( authToken != null ) {
            // 移除 开头的 `token ` 字符
            val token = authToken.substring(6)
            // 从用户传来的 token 中获取 关键 key
            val principal = tokenProcessor.getPrincipalFromToken(token)
            val userType = tokenProcessor.getUsertypeFromToken(token) ?: chain.doFilter(request, response)
            // 根据用户类型 确定 detailService 用户类型在 token 中存储
            val detailService = when(userType) {
                AuthUserTypeNormal -> wxUserDetailsService
                AuthUserTypeMerchant -> merchantUserDetailService
                AuthUserTypeAdmin -> adminUserDetailsService
                else -> null
            } ?: return chain.doFilter(request, response)

            // 如果该用户需要登录
            if ( principal != null && SecurityContextHolder.getContext().authentication == null ) {
                val authUser = detailService.loadUserByUsername(principal) as AuthUser
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