package com.menuxx.weixin.auth

import com.menuxx.common.bean.WXUser
import com.menuxx.common.db.UserDb
import com.menuxx.weixin.util.getSex
import me.chanjar.weixin.mp.api.WxMpService
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/13
 * 微信: yin80871901
 */

@Component
class WXAuthenticationFilter(
        private val wxMpService: WxMpService,
        private val userDb: UserDb,
        @Autowired @Qualifier("wxUserDetailService") private val userDetailsService: UserDetailsService
) : OncePerRequestFilter () {

    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filter: FilterChain) {
        // weixin authCode
        val fromIp = request.remoteAddr
        val fromUrl = request.requestURL.toString()
        val authCode = request.getParameter("code")
        val state = request.getParameter("state")
        // 如果 #后面一直，
        if ( state != WXUrlOAuthState ) {
            logger.warn("weixin redirect url state $state 预期接受 $WXUrlOAuthState")
        }
        if ( StringUtils.isNotBlank(state) && StringUtils.isNotBlank(authCode) ) {
            // 获取该用户在微信中的信息
            val accessToken = wxMpService.oauth2getAccessToken(authCode)
            val wxUserInfo = wxMpService.userService.userInfo(accessToken.openId)
            // 获取该用户在系统中的信息
            val sysUser = userDb.findUserByOpenid(wxUserInfo.openId)
            // 如果用户在系统中存在那就说明该用户绑定过
            // 用户第一次来
            if ( sysUser == null ) {
                val wxUser = WXUser()
                wxUser.openid = wxUserInfo.openId
                wxUser.city = wxUserInfo.city
                wxUser.country = wxUserInfo.country
                wxUser.headimgurl = wxUserInfo.headImgUrl
                wxUser.nickname = wxUserInfo.nickname
                wxUser.province = wxUserInfo.province
                wxUser.unionid = wxUserInfo.unionId
                wxUser.sex = getSex(wxUserInfo.sex)
                userDb.saveUserFromWXUser(wxUser, fromIp)
            }
            val authUser = userDetailsService.loadUserByUsername(wxUserInfo.openId)
            val authentication = UsernamePasswordAuthenticationToken(authUser, null, authUser.authorities)
            authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
            SecurityContextHolder.getContext().authentication = authentication
        } else {
            // 如果没有 code 和 state 等信息，就跳转到微信授权
            response.sendRedirect(wxMpService.oauth2buildAuthorizationUrl(fromUrl, "snsapi_userinfo", WXUrlOAuthState))
            return
        }

        filter.doFilter(request, response)
    }

}