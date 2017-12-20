package com.menuxx.weixin.auth

import com.menuxx.weixin.prop.AuthTokenProps
import com.menuxx.weixin.prop.WeiXinProps
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/13
 * 微信: yin80871901
 */

@Configuration
@EnableConfigurationProperties(AuthTokenProps::class)
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
        private val userDetailsService: UserDetailsService,
        private val wxProps: WeiXinProps,
        private val authTokenProps: AuthTokenProps,
        private val env: Environment,
        private val unauthorizedHandler: EntryPointUnauthorizedHandler
) : WebSecurityConfigurerAdapter() {

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Autowired
    @Throws(Exception::class)
    fun configureAuthentication(builder: AuthenticationManagerBuilder) {
        builder.userDetailsService(userDetailsService)
                // 密码不作任何加密，因为用户的密码是 微信的 refreshToken
                .passwordEncoder(NoOpPasswordEncoder.getInstance())
    }

    /**
     * 会话令牌处理器
     */
    @Bean
    fun tokenProcessor() : TokenProcessor {
        return TokenProcessor(authTokenProps.secret!!, authTokenProps.expiration!!)
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationTokenFilter(): AuthenticationTokenFilter {
        val filter = AuthenticationTokenFilter(authTokenProps.header!!, userDetailsService, tokenProcessor())
        filter.setAuthenticationManager(authenticationManagerBean())
        return filter
    }

    override fun configure(web: WebSecurity) {
        web.ignoring().antMatchers(
                HttpMethod.GET,
                "/auth/**",
                "/*.html",
                "/favicon.ico",
                "/**/*.html",
                "/**/*.css",
                "/**/*.txt",
                "/**/*.js",
                "/**/*.map"
        )
    }

    override fun configure(http: HttpSecurity) {

        http
            .csrf().disable()
            .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/auth/**").permitAll()
                .anyRequest().authenticated()

        http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)

    }

}