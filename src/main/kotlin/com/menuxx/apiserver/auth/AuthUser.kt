package com.menuxx.apiserver.auth

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/14
 * 微信: yin80871901
 */

class AGrantedAuthority(private val authorize: String) : GrantedAuthority {
    override fun getAuthority(): String {
        return authorize
    }
}

val AuthUserTypeNormal = 1
val AuthUserTypeMerchant = 2
val AuthUserTypeAdmin = 3

open class AuthUser(
        val id: Int,
        val userName: String,
        private val _password: String?,
        val enable: Boolean,
        val openid: String?,
        val phoneNumber: String?,
        val avatarUrl: String?,
        val userType: Int,
        private val authorities: MutableList<AGrantedAuthority>?= mutableListOf()
) : UserDetails {

    // 用户权限组
    override fun getAuthorities() = authorities

    // 用户是否激活
    override fun isEnabled() = enable

    // 用户名
    override fun getUsername() : String {
        return when ( userType ) {
            AuthUserTypeNormal -> openid!!
            AuthUserTypeMerchant -> phoneNumber!!
            AuthUserTypeAdmin -> userName
            else -> phoneNumber!!
        }
    }

    // 证书尚未过期
    override fun isCredentialsNonExpired() = true

    // 密码
    override fun getPassword() = _password

    // 账户尚未过期
    override fun isAccountNonExpired() = true

    // 账户未被锁定
    override fun isAccountNonLocked() = true
}