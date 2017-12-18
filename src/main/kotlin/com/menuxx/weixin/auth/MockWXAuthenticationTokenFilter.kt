package com.menuxx.weixin.auth

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/14
 * 微信: yin80871901
 */

class MockWXAuthenticationTokenFilter : OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filter: FilterChain) {
        val authUser = AUser(id = 1, userName = "尹昶胜", nickName = "尹昶胜", _password = null,
                enable = true,
                openid = "oMnD4viZtYFgTOBqdwHGr6he-8Vc",
                avatarUrl = "http://wx.qlogo.cn/mmopen/jLHyfVmPf7NHkjA3o0wGeGibeIEq762TlwJVX1WL8WcztJ9UGelPVrPdeVFl1ZaaXr6PIysibIWIEvg6t4mbWwIw/0",
                authorities = listOf(AGrantedAuthority("ROLE_USER")).toMutableList()
        )
        val authentication = UsernamePasswordAuthenticationToken(authUser, null, authUser.authorities)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authentication
        filter.doFilter(request, response)
    }

}