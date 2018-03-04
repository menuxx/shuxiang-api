package com.menuxx.sso.cfg

import com.menuxx.sso.auth.TokenProcessor
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import org.springframework.session.SessionRepository
import org.springframework.session.data.redis.RedisOperationsSessionRepository
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession
import org.springframework.session.web.http.HeaderHttpSessionStrategy
import org.springframework.session.web.http.HttpSessionStrategy


/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2018/1/5
 * 微信: yin80871901
 */

@Configuration
@EnableRedisHttpSession
class HttpSessionConfig (private val tokenProcessor: TokenProcessor) {

    private final val logger = LoggerFactory.getLogger(HttpSessionConfig::class.java)

    @Bean
    fun concurrentTaskScheduler() : ConcurrentTaskScheduler {
        val scheduler = ConcurrentTaskScheduler()
        scheduler.setErrorHandler { err ->
            logger.error("scheduler error handler: ", err)
        }
        return scheduler
    }

    @Bean
    fun httpSessionStrategy() : HttpSessionStrategy {
        val strategy = HeaderHttpSessionStrategy()
        strategy.setHeaderName("Session-ID")
        return strategy
    }

    @Bean
    fun sessionRepository(redisConnectionFactory: RedisConnectionFactory) : SessionRepository<*> {
        val repository = RedisOperationsSessionRepository(redisConnectionFactory)
        repository.cleanupExpiredSessions()
        return repository
    }

}