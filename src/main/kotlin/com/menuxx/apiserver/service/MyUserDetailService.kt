package com.menuxx.apiserver.service

import com.menuxx.apiserver.auth.AGrantedAuthority
import com.menuxx.apiserver.auth.AuthUser
import com.menuxx.apiserver.auth.AuthUserTypeAdmin
import com.menuxx.apiserver.auth.AuthUserTypeMerchant
import com.menuxx.common.db.AdminDb
import com.menuxx.common.db.MerchantDb
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/23
 * 微信: yin80871901
 *
 * 超级管理员
 */
@Service("adminUserDetailsService")
class AdminUserDetailsService(private val adminDb: AdminDb) : UserDetailsService {

    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val adminUser = adminDb.findUserByUsername(username)
        if ( adminUser != null ) {
            val authorities = adminDb.findAuthoritiesByAdminId(adminUser.id)
            return AuthUser(id = adminUser.id, userName = adminUser.username, _password = adminUser.passwordEncrypted,
                        enable = true,
                        openid = null,
                        phoneNumber = adminUser.phoneNumber,
                        avatarUrl = "",
                        userType = AuthUserTypeAdmin,
                        authorities = authorities.map { AGrantedAuthority(it.authority.name) }.toMutableList()
                    )
        } else {
            throw UsernameNotFoundException("$username 用户不存在")
        }
    }

}

/**
 * 商户详细信息
 */
@Service("merchantUserDetailsService")
class MerchantUserDetailsService(private val merchantDb: MerchantDb) : UserDetailsService {

    /**
     * 这里的 user 代表手机号
     */
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val merchant = merchantDb.findMerchantByPhoneNumber(username)
        if ( merchant != null ) {
            val authorities = merchantDb.findAuthoritiesByMerchantId(merchant.id)
            return AuthUser(id = merchant.id, userName = merchant.pressName, _password = merchant.captcha,
                    enable = merchant.enable == 1,
                    openid = null,
                    phoneNumber = username,
                    avatarUrl = null,
                    userType = AuthUserTypeMerchant,
                    authorities = authorities.map { AGrantedAuthority(it.authority.name) }.toMutableList()
            )
        } else {
            throw UsernameNotFoundException("商户手机号: $username 用户不存在")
        }
    }

}