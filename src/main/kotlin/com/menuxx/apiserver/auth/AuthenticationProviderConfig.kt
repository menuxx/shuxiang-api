package com.menuxx.apiserver.auth

import com.menuxx.apiserver.service.MerchantUserDetailService
import com.menuxx.weixin.service.WXUserDetailService
import org.springframework.beans.factory.annotation.Autowire
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.password.NoOpPasswordEncoder

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/23
 * 微信: yin80871901
 */
@Configuration
class AuthenticationProviderConfig(
        private val wxUserDetailService: WXUserDetailService,
        private val merchantUserDetailService: MerchantUserDetailService
) {

    // 验证 普通微信用户
    @Bean(name = ["wxAuthenticationProvider"], autowire = Autowire.BY_NAME)
    fun wxAuthenticationProvider() : AuthenticationProvider {
        val provider = object : DaoAuthenticationProvider() {
            override fun authenticate(authentication: Authentication): Authentication? {
                if ( authentication.principal is AuthUser ) {
                    val user = authentication.principal as AuthUser
                    if ( user.userType == AuthUserTypeNormal ) {
                        return super.authenticate(authentication)
                    }
                }
                return null
            }
        }
        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance())
        provider.setUserDetailsService(wxUserDetailService)
        return provider
    }

    // 验证商户用户
    @Bean(name = ["merchantAuthenticationProvider"], autowire = Autowire.BY_NAME)
    fun merchantAuthenticationProvider() : AuthenticationProvider {
        val provider = object : DaoAuthenticationProvider() {
            override fun authenticate(authentication: Authentication): Authentication? {
                if ( authentication.principal is AuthUser ) {
                    val user = authentication.principal as AuthUser
                    if ( user.userType == AuthUserTypeMerchant ) {
                        return super.authenticate(authentication)
                    }
                }
                return null
            }
        }
        provider.setPasswordEncoder(NoOpPasswordEncoder.getInstance())
        provider.setUserDetailsService(merchantUserDetailService)
        return provider
    }


}