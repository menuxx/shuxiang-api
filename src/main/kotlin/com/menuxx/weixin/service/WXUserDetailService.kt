package com.menuxx.weixin.service

import com.menuxx.common.db.UserDb
import com.menuxx.weixin.auth.AGrantedAuthority
import com.menuxx.weixin.auth.AUser
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/14
 * 微信: yin80871901
 */

@Service("wxUserDetailService")
class WXUserDetailService(private val userDb: UserDb) : UserDetailsService {

    /**
     * 此处的 userName 就是 openid
     */
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userDb.findUserByOpenid(username)
        if ( user != null ) {
            val authorities = userDb.findAuthoritiesByUserId(user.id)
            return AUser(id = user.id, userName = user.userName, nickName = user.userName, _password = null,
                enable = user.enable == 1,
                    openid = username,
                    avatarUrl = user.avatarUrl,
                    authorities = authorities.map { AGrantedAuthority(it.authority.name) }.toMutableList()
            )
        } else {
            throw UsernameNotFoundException("用户名: $username 用户不存在")
        }
    }

}