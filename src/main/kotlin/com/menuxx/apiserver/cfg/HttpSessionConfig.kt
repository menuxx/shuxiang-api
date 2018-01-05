package com.menuxx.apiserver.cfg

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession
import org.springframework.session.web.http.HttpSessionStrategy



/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/5
 * 微信: yin80871901
 */

@Configuration
@EnableRedisHttpSession
class HttpSessionConfig {

    @Bean
    fun httpSessionStrategy(): HttpSessionStrategy {
        return MyHeaderHttpSessionStrategy()
    }

}