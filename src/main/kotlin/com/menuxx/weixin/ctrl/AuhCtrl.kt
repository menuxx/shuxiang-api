package com.menuxx.weixin.ctrl

import com.menuxx.common.bean.WXUser
import com.menuxx.common.db.UserDb
import com.menuxx.weixin.auth.TokenProcessor
import com.menuxx.weixin.util.getSex
import me.chanjar.weixin.mp.api.WxMpService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/19
 * 微信: yin80871901
 */

@RestController
@RequestMapping("/auth")
class AuhCtrl(
        private val wxMpService: WxMpService,
        private val userDb: UserDb,
        private val authenticationManager: AuthenticationManager,
        private val tokenProcessor: TokenProcessor
        ) {

    /**
     * 用原始令牌刷新得到新的令牌
     */
    data class SessionToken(@NotNull val token: String)
    @PutMapping("/refresh_token")
    fun refreshSessionCode(@RequestBody @Valid oldToken: SessionToken) : ResponseEntity<Any> {
        val newToken = tokenProcessor.refreshToken(oldToken.token)
        if ( newToken != null ) {
            return ResponseEntity.ok(hashMapOf("token" to newToken))
        } else {
            return ResponseEntity.badRequest().body(null)
        }
    }

    /**
     * 使用微信的授权 code 换取 session token
     */
    data class SessionCode(@NotNull val code: String)
    data class Result(val token: String, val userInfo: Map<String, Any>)
    @PutMapping("/auth_code_to_token")
    fun getSessionTokenByCode(@RequestBody @Valid sessionCode: SessionCode, request: HttpServletRequest) : Result {
        val fromIp = request.remoteAddr
        // 获取该用户在微信中的信息
        val accessToken = wxMpService.oauth2getAccessToken(sessionCode.code)
        val wxUserInfo = wxMpService.userService.userInfo(accessToken.openId)

        // 新用户就绑定，老用户更新信息
        val wxUser = WXUser()
        wxUser.openid = wxUserInfo.openId
        wxUser.refreshToken = accessToken.refreshToken
        wxUser.city = wxUserInfo.city
        wxUser.country = wxUserInfo.country
        wxUser.headimgurl = wxUserInfo.headImgUrl
        wxUser.nickname = wxUserInfo.nickname
        wxUser.province = wxUserInfo.province
        wxUser.unionid = wxUserInfo.unionId
        wxUser.sex = getSex(wxUserInfo.sex)
        val sysUser = userDb.saveUserFromWXUser(wxUser, fromIp)

        // 获取该用户在系统中的信息
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(
                accessToken.openId,
                accessToken.refreshToken
        ))

        SecurityContextHolder.getContext().authentication = authentication
        val token = tokenProcessor.genToken(accessToken.openId, request.remoteAddr)

        return Result(token, hashMapOf(
                "id" to sysUser.id,
                "userName" to sysUser.userName,
                "avatarUrl" to sysUser.avatarUrl
        ))
    }

}