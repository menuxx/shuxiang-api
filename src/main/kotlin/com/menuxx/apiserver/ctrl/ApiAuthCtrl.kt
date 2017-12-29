package com.menuxx.apiserver.ctrl

import com.menuxx.Const
import com.menuxx.apiserver.auth.AuthUser
import com.menuxx.apiserver.auth.AuthUserTypeAdmin
import com.menuxx.apiserver.auth.AuthUserTypeMerchant
import com.menuxx.apiserver.auth.TokenProcessor
import com.menuxx.apiserver.bean.ApiResp
import com.menuxx.apiserver.service.AdminUserDetailsService
import com.menuxx.apiserver.service.CaptchaService
import com.menuxx.apiserver.service.MerchantUserDetailsService
import com.menuxx.common.db.AdminDb
import org.hibernate.validator.constraints.NotEmpty
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid
import javax.validation.constraints.NotNull

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/23
 * 微信: yin80871901
 */
@RestController
@RequestMapping("/auth")
class ApiAuhCtrl(
        private val adminDb: AdminDb,
        private val authenticationManager: AuthenticationManager,
        private val merchantUserDetailsService: MerchantUserDetailsService,
        private val adminUserDetailsService: AdminUserDetailsService,
        private val tokenProcessor: TokenProcessor,
        private val captchaService: CaptchaService
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

    data class Mobile(@NotNull val phoneNumber: String)
    @PutMapping("/send_captcha")
    fun sendCaptcha(@Valid @RequestBody mobile: Mobile) : ApiResp {
        return try {
            captchaService.doSendCaptcha(mobile.phoneNumber)
            ApiResp(Const.NotErrorCode, "发送完成")
        } catch (ex: Exception) {
            ApiResp(500, ex.message ?: "未知错误")
        }
    }

    /**
     * 使用微信的授权 code 换取 session token
     */
    data class Captcha(@NotNull val captcha: String, @NotNull val phoneNumber: String)
    data class Result(val token: String, val userInfo: Map<String, Any>)
    @PutMapping("/captcha_to_token")
    fun getSessionTokenByCaptcha(@RequestBody @Valid captcha: Captcha, request: HttpServletRequest) : Result {
        val fromIp = request.remoteAddr

        // 获取用户
        val userDetail = merchantUserDetailsService.loadUserByUsername(captcha.phoneNumber)

        // 尝试让该用户登录，从而验证该用户登录是否正确
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(userDetail, userDetail.password, userDetail.authorities))

        SecurityContextHolder.getContext().authentication = authentication

        val merchant = authentication.principal as AuthUser

        val token = tokenProcessor.genToken(captcha.phoneNumber, AuthUserTypeMerchant, fromIp)

        return Result(token, hashMapOf(
                "id" to merchant.id,
                "userName" to merchant.userName,
                "phoneNumber" to merchant.phoneNumber!!
        ))
    }

    data class UsernameLogin(@NotEmpty val userName: String,@NotEmpty val passwordEncrypted: String);
    @PutMapping("/root_login")
    fun rootLogin(request: HttpServletRequest, @RequestBody loginForm: UsernameLogin) : Result {
        val fromIp = request.remoteAddr

        // 获取用户
        val userDetail = adminUserDetailsService.loadUserByUsername(loginForm.userName)

        // 尝试让该用户登录，从而验证该用户登录是否正确
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(userDetail, userDetail.password, userDetail.authorities))

        SecurityContextHolder.getContext().authentication = authentication

        val adminUser = authentication.principal as AuthUser

        val token = tokenProcessor.genToken(adminUser.userName, AuthUserTypeAdmin, fromIp)

        adminDb.loginUpdate(lastLoginIp = fromIp)

        return Result(token, hashMapOf(
                "id" to adminUser.id,
                "userName" to adminUser.userName,
                "phoneNumber" to adminUser.phoneNumber!!
        ))
    }

}