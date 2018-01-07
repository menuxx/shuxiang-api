package com.menuxx.miaosha.cfg

import org.springframework.context.annotation.Configuration

/**
 * 作者: yinchangsheng@gmail.com
 * 创建于: 2017/12/10
 * 微信: yin80871901
 */

@Configuration
class ChannelConfigure(
//        @Autowired @Qualifier("objRedisTemplate") private val objRedisTemplate: RedisTemplate<String, Any>,
//        @Autowired @Qualifier("publicProducer") private val publicProducer: ProducerBean,
//        private val orderDb: OrderDb
) {

    /**
     * 渠道用户状态持久化队列
     */
//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    fun channelUserStateWriteQueue(channelItemDb: ChannelItemRecordDb) : ChannelUserStateWriteQueue {
//        return ChannelUserStateWriteQueue(objRedisTemplate, channelItemDb, orderDb)
//    }

    /**
     * 主要业务业务逻辑发生在这里
     */
//    @Bean
//    fun channelUserEventHandler(channelUserStateWriteQueue: ChannelUserStateWriteQueue) : ChannelUserEventHandler {
//        return ChannelUserEventHandler(channelUserStateWriteQueue)
//    }

    /**
     * 后置处理器，用来 发布时间，状态标记
     */
//    @Bean
//    fun channelUserEventPostObtainHandler(aliyunProps: AliyunProps, objectMapper: ObjectMapper) : ChannelUserEventPostObtainHandler {
//        return ChannelUserEventPostObtainHandler(aliyunProps, orderDb, objectMapper, objRedisTemplate, publicProducer)
//    }

//    @Bean(initMethod = "start", destroyMethod = "shutdown")
//    fun channelUserEventDisruptor(channelUserEventHandler: ChannelUserEventHandler, channelUserEventPostObtainHandler: ChannelUserEventPostObtainHandler) : ChannelUserEventDisruptor {
//        return ChannelUserEventDisruptor(channelUserEventHandler, channelUserEventPostObtainHandler)
//    }

}