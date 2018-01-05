package com.menuxx.weixin.ctrl

import cn.binarywang.wx.miniapp.api.WxMaService
import com.menuxx.apiserver.auth.AuthUserTypeNormal
import com.menuxx.common.bean.WXUser
import com.menuxx.common.db.UserDb
import com.menuxx.apiserver.auth.TokenProcessor
import com.menuxx.weixin.bean.WxMiniApp
import com.menuxx.weixin.service.WXUserDetailsService
import com.menuxx.weixin.util.getSex
import me.chanjar.weixin.mp.api.WxMpService
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.session.MapSession
import org.springframework.session.SessionRepository
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

data class WXAuthResult(val token: String, val userInfo: Map<String, Any>)

@RestController
@RequestMapping("/wxauth")
class WXAuhCtrl(
        private val wxMpService: WxMpService,   // 公众号接口
        private val wxMaService: WxMaService,   // 小程序接口
        private val userDb: UserDb,
        private val authenticationManager: AuthenticationManager,
        private val wxUserDetailsService: WXUserDetailsService,
        private val tokenProcessor: TokenProcessor,
        private val sessionRepository: SessionRepository<MapSession>
        ) {

    /**
     * 用原始令牌刷新得到新的令牌
     */
    data class SessionToken(@NotNull val token: String)
    @PutMapping("/refresh_token")
    fun refreshSessionCode(@RequestBody @Valid oldToken: SessionToken) : ResponseEntity<Any> {


        val newToken = tokenProcessor.refreshToken(oldToken.token)
        return if ( newToken != null ) {
             ResponseEntity.ok(hashMapOf("token" to newToken))
        } else {
            ResponseEntity.badRequest().body(null)
        }
    }

    /**
     * 支持微信小程序登录
     */
    data class LogoBody(
            @NotEmpty val code: String,
            @NotEmpty val encryptedData: String,
            @NotEmpty val iv: String,
            @NotEmpty val rawData: String,
            @NotEmpty val signature: String
    )
    @PutMapping("/mini_code_to_session")
    fun getSessionByJsCode(@Valid @RequestBody data: LogoBody, request: HttpServletRequest) : WXAuthResult {

        val fromIp = request.remoteAddr
        val sessionInfo = wxMaService.userService.getSessionInfo(data.code)
        val userInfo = wxMaService.userService.getUserInfo(sessionInfo.sessionKey, data.encryptedData, data.iv)

        val wxUser = WXUser()
        wxUser.openid = userInfo.openId
        wxUser.nickname = userInfo.nickName ?: "未命名"
        wxUser.sex = getSex(userInfo.gender)
        wxUser.unionid = userInfo.unionId
        wxUser.city = userInfo.city
        wxUser.country = userInfo.country ?: "中国"
        wxUser.province = userInfo.province
        wxUser.headimgurl = userInfo.avatarUrl ?: "https://file.menuxx.com/images/avatars/default_avatar.png"

        val sysUser = userDb.saveUserFromWXUser(wxUser, fromIp)

        val userDetail = wxUserDetailsService.loadUserByUsername(wxUser.unionid)

        // 尝试让该用户登录，从而验证该用户登录是否正确
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(
                userDetail,
                sessionInfo.unionid,
                userDetail.authorities
        ))

        SecurityContextHolder.getContext().authentication = authentication

        val token = tokenProcessor.genToken(sessionInfo.openid, AuthUserTypeNormal, request.remoteAddr)

        val session = sessionRepository.createSession()

        session.setAttribute(WxMiniApp.sessionKey, sessionInfo.sessionKey)
        session.setAttribute(WxMiniApp.openid, sessionInfo.openid)

        return WXAuthResult(token, hashMapOf(
                "id" to sysUser.id,
                "userName" to sysUser.userName,
                "avatarUrl" to sysUser.avatarUrl,
                "sessionId" to session.id
        ))

    }

    /**
     * 使用微信的授权 code 换取 session token
     */
    data class SessionCode(@NotNull val code: String)
    @PutMapping("/auth_code_to_token")
    fun getSessionTokenByCode(@RequestBody @Valid sessionCode: SessionCode, request: HttpServletRequest) : WXAuthResult {
        val fromIp = request.remoteAddr
        // 获取该用户在微信中的信息
        val accessToken = wxMpService.oauth2getAccessToken(sessionCode.code)
        val wxUserInfo = wxMpService.userService.userInfo(accessToken.openId)

        // 新用户就绑定，老用户更新信息
        val wxUser = WXUser()
        wxUser.openid = wxUserInfo.openId
        wxUser.refreshToken = accessToken.refreshToken
        wxUser.city = wxUserInfo.city
        wxUser.country = wxUserInfo.country ?: "中国"
        wxUser.headimgurl = wxUserInfo.headImgUrl ?: "https://file.menuxx.com/images/avatars/default_avatar.png"
        wxUser.nickname = wxUserInfo.nickname ?: "未命名"
        wxUser.province = wxUserInfo.province
        wxUser.unionid = wxUserInfo.unionId
        wxUser.sex = getSex(wxUserInfo.sex)
        val sysUser = userDb.saveUserFromWXUser(wxUser, fromIp)

        // 获取用户
        val userDetail = wxUserDetailsService.loadUserByUsername(wxUser.unionid)

        // 尝试让该用户登录，从而验证该用户登录是否正确
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(
                userDetail,
                accessToken.refreshToken,
                userDetail.authorities
        ))

        SecurityContextHolder.getContext().authentication = authentication
        val token = tokenProcessor.genToken(accessToken.openId, AuthUserTypeNormal, request.remoteAddr)

        return WXAuthResult(token, hashMapOf(
                "id" to sysUser.id,
                "userName" to sysUser.userName,
                "avatarUrl" to sysUser.avatarUrl
        ))
    }

}