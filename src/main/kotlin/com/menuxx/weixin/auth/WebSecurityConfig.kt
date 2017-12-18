package com.menuxx.weixin.auth

import com.menuxx.weixin.prop.WeiXinProps
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/13
 * 微信: yin80871901
 */

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
        private val wxProps: WeiXinProps,
        private val env: Environment,
        private val wxFilter: WXAuthenticationFilter
) : WebSecurityConfigurerAdapter() {

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

        // 授权异常处理
        http.exceptionHandling()
                .authenticationEntryPoint(WebAuthenticationEntryPoint(wxProps.appId))
                .and()
                .authorizeRequests()
                // 此处 url 跳过授权
                .antMatchers("/auth/**").permitAll()
                // 其他的都需要授权
                .anyRequest().authenticated().and()

        // if (env.activeProfiles.contains("production")) {
            // 使用正式
            // http.addFilterBefore(wxFilter, UsernamePasswordAuthenticationFilter::class.java)
        // } else {
            // 使用模拟
            // http.addFilterBefore(MockWXAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter::class.java)
        // }
                .logout()
                .logoutRequestMatcher(AntPathRequestMatcher("/auth/logout"))
    }

    //    @Autowired
//    @Throws(Exception::class)
//    fun configureGlobal(builder: AuthenticationManagerBuilder) {
//        builder.userDetailsService(adminService).passwordEncoder(BCryptPasswordEncoder())
//    }

}