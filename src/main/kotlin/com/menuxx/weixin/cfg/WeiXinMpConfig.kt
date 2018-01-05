package com.menuxx.weixin.cfg

import cn.binarywang.wx.miniapp.api.WxMaService
import cn.binarywang.wx.miniapp.config.WxMaConfig
import com.github.binarywang.wxpay.config.WxPayConfig
import com.github.binarywang.wxpay.service.WxPayService
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
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig



/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/14
 * 微信: yin80871901
 */

@Configuration
@ConditionalOnClass(WxMpService::class)
@EnableConfigurationProperties(WeiXinProps::class)
class WeiXinMpConfig(
        private val wxProps: WeiXinProps,
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
        configStorage.jsapiTicketExpiresTime = 7200 * 1000
        configStorage.expiresTime = 7200 * 1000
        configStorage.appId = wxProps.mp.appId
        configStorage.secret = wxProps.mp.appSecret
        configStorage.token = wxProps.mp.token
        configStorage.aesKey = wxProps.mp.aesKey
        return configStorage
    }

    @Bean
    @ConditionalOnMissingBean
    fun wxMaConfig() : WxMaConfig {
        val config = WxMaInMemoryConfig()
        config.appid = wxProps.miniApp.appId
        config.secret = wxProps.miniApp.appSecret
        config.token =  wxProps.miniApp.token
        config.aesKey = wxProps.miniApp.aesKey
        config.msgDataFormat = wxProps.miniApp.msgDataFormat
        return config
    }

    @Bean
    @ConditionalOnMissingBean
    fun wxMaService() : WxMaService {
        val service = cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl()
        service.wxMaConfig = wxMaConfig()
        return service
    }

    @Bean
    @ConditionalOnMissingBean
    fun wxMpService(configStorage: WxMpConfigStorage): WxMpService {
        // 使用 默认 Apache Http Client 实现
        val wxMpService = me.chanjar.weixin.mp.api.impl.WxMpServiceImpl()
        wxMpService.wxMpConfigStorage = configStorage
        return wxMpService
    }

    /**
     * 初始化微信支付服务
     */
    @Bean
    @ConditionalOnMissingBean
    fun wxPayService() : WxPayService {
        val payConfig = WxPayConfig()
        // http 5 秒过期
        payConfig.httpConnectionTimeout = 5 * 1000
        payConfig.httpTimeout = 5 * 1000
        // 支付账户必要信息
        payConfig.mchId = wxProps.pay.mchId
        payConfig.mchKey = wxProps.pay.paySecret
        payConfig.appId = wxProps.mp.appId
        val wxPayService = com.github.binarywang.wxpay.service.impl.WxPayServiceImpl()
        wxPayService.config = payConfig
        return wxPayService
    }

}