package com.menuxx.common.cfg

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.ValueOperations
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */

@Configuration
class RedisConfig {

    @Bean
    fun jackson2JsonRedisSerializer(objectMapper: ObjectMapper): Jackson2JsonRedisSerializer<Any> {
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Any::class.java)
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper)
        return jackson2JsonRedisSerializer
    }

    @Bean("objRedisTemplate")
    fun objRedisTemplate(connectionFactory: RedisConnectionFactory,
                                  jackson2JsonRedisSerializer: Jackson2JsonRedisSerializer<Any>): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = connectionFactory
        redisTemplate.defaultSerializer = jackson2JsonRedisSerializer
        val stringRedisSerializer = StringRedisSerializer()
        redisTemplate.keySerializer = stringRedisSerializer
        redisTemplate.hashKeySerializer = stringRedisSerializer
        return redisTemplate
    }

    @Bean("intRedisTemplate")
    fun intRedisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, Int> {
        val redisTemplate = RedisTemplate<String, Int>()
        redisTemplate.connectionFactory = connectionFactory
        return redisTemplate
    }

    @Bean("objOperations")
    fun objOperations(@Autowired @Qualifier("objRedisTemplate") redisTemplate: RedisTemplate<String, Any>): ValueOperations<String, Any> {
        return redisTemplate.opsForValue()
    }

}