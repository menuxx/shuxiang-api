package com.menuxx.weixin.service

import com.menuxx.common.db.UserDb
import com.menuxx.apiserver.auth.AGrantedAuthority
import com.menuxx.apiserver.auth.AuthUser
import com.menuxx.apiserver.auth.AuthUserTypeNormal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/14
 * 微信: yin80871901
 */

@Service("wxUserDetailsService")
class WXUserDetailsService(private val userDb: UserDb) : UserDetailsService {

    /**
     * 此处的 userName 就是 unionId
     */
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userDb.findUserByUnionIdOrderOpenId(username)
        if ( user != null ) {
            val authorities = userDb.findAuthoritiesByUserId(user.id)
            return AuthUser(id = user.id, userName = user.userName, _password = user.passwordToken,
                    enable = user.enable == 1,
                    unionid = user.wxUser.unionid,
                    openid = user.wxUser.openid,
                    avatarUrl = user.avatarUrl,
                    phoneNumber = user.phoneNumber,
                    userType = AuthUserTypeNormal,
                    authorities = authorities.map { AGrantedAuthority(it.authority.name) }.toMutableList()
            )
        } else {
            throw UsernameNotFoundException("用户名: $username 用户不存在")
        }
    }

}