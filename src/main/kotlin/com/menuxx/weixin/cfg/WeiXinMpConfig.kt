package com.menuxx.weixin.cfg

import com.menuxx.weixin.prop.WeiXinProps
import me.chanjar.weixin.mp.api.WxMpConfigStorage
import me.chanjar.weixin.mp.api.WxMpInRedisConfigStorage
import me.chanjar.weixin.mp.api.WxMpService
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.data.redis.RedisProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import redis.clients.jedis.JedisPool
import java.net.URI

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/14
 * 微信: yin80871901
 */

@Configuration
@ConditionalOnClass(WxMpService::class)
@EnableConfigurationProperties(WeiXinProps::class)
class WeiXinMpConfig(
        private val weiXinProps: WeiXinProps,
        private val redisProps: RedisProperties
) {

    private fun getRedisPool() : JedisPool {
        val poolConfig  = GenericObjectPoolConfig()
        poolConfig.maxIdle = redisProps.pool.maxIdle
        poolConfig.minIdle = redisProps.pool.minIdle
        poolConfig.maxWaitMillis = redisProps.pool.maxWait.toLong()
        poolConfig.maxTotal = redisProps.pool.maxActive
        return JedisPool(poolConfig, URI.create(redisProps.url))
    }

    /**
     * 生产和开发都是用远程 redis 防止 全局 access_token 出现问题
     */
    @Bean
    @ConditionalOnMissingBean
    fun configStorageProd() : WxMpConfigStorage {
        val configStorage = WxMpInRedisConfigStorage(getRedisPool())
        configStorage.appId = weiXinProps.appId
        configStorage.secret = weiXinProps.appSecret
        configStorage.token = weiXinProps.token
        configStorage.aesKey = weiXinProps.aesKey
        return configStorage
    }

    @Bean
    @ConditionalOnMissingBean
    fun wxMpService(configStorage: WxMpConfigStorage): WxMpService {
        // 使用 默认 Apache Http Client 实现
        val wxMpService = me.chanjar.weixin.mp.api.impl.WxMpServiceImpl()
        wxMpService.wxMpConfigStorage = configStorage
        return wxMpService
    }

}