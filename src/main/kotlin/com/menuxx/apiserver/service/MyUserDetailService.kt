package com.menuxx.apiserver.service

import com.menuxx.apiserver.auth.AGrantedAuthority
import com.menuxx.apiserver.auth.AuthUser
import com.menuxx.apiserver.auth.AuthUserTypeMerchant
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
//@Service("adminUserDetailService")
//class AdminUserDetailService() : UserDetailsService {
//
//
//    override fun loadUserByUsername(username: String): UserDetails {
//    }
//
//}

/**
 * 商户详细信息
 */
@Service("merchantUserDetailService")
class MerchantUserDetailService(private val merchantDb: MerchantDb) : UserDetailsService {

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
            throw UsernameNotFoundException("从商户手机号: $username 用户不存在")
        }
    }

}