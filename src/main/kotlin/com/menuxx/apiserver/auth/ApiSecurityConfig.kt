package com.menuxx.apiserver.auth

import com.menuxx.weixin.prop.AuthTokenProps
import com.menuxx.weixin.prop.WeiXinProps
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
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
class ApiSecurityConfig(
        @Autowired @Qualifier("wxUserDetailService") private val wxUserDetailsService: UserDetailsService,
        @Autowired @Qualifier("merchantUserDetailService") private val merchantUserDetailsService: UserDetailsService,
        private val wxProps: WeiXinProps,
        private val authTokenProps: AuthTokenProps,
        private val env: Environment,
        private val unauthorizedHandler: EntryPointUnauthorizedHandler,
        @Autowired @Qualifier("wxAuthenticationProvider") private val wxAuthenticationProvider: AuthenticationProvider,
        @Autowired @Qualifier("merchantAuthenticationProvider") private val merchantAuthenticationProvider: AuthenticationProvider
) : WebSecurityConfigurerAdapter() {

    @Bean
    @Throws(Exception::class)
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    @Autowired
    @Throws(Exception::class)
    fun configureAuthentication(builder: AuthenticationManagerBuilder) {
        builder.authenticationProvider(wxAuthenticationProvider)
        builder.authenticationProvider(merchantAuthenticationProvider)
    }

    /**
     * 会话令牌处理器
     */
    @Bean
    fun tokenProcessor() : TokenProcessor {
        return TokenProcessor(authTokenProps.secret, authTokenProps.expiration)
    }

    @Bean
    @Throws(Exception::class)
    fun authenticationTokenFilter(): AuthenticationTokenFilter {
        val filter = AuthenticationTokenFilter(authTokenProps.header, wxUserDetailsService, merchantUserDetailsService, tokenProcessor())
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
                .antMatchers("/wxauth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/weixin/config").permitAll()

                .antMatchers(HttpMethod.POST, "/weixin_event_handler/**").permitAll() // 微信事件拦截不做权限验证
                .anyRequest().authenticated()

        http.addFilterBefore(authenticationTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)

    }

}