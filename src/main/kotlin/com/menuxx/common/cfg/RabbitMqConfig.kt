package com.menuxx.common.cfg

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.annotation.EnableRabbit
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory

/**
 * rabbitmq 消息队列配置
 */
@EnableRabbit
@Configuration
class RabbitMqConfig (private val objectMapper: ObjectMapper) : RabbitListenerConfigurer {

    @Bean
    fun jackson2Converter() : MappingJackson2MessageConverter {
        val converter = MappingJackson2MessageConverter()
        converter.objectMapper = objectMapper
        return converter
    }

    @Bean
    fun jackson2JsonMessageConverter() = Jackson2JsonMessageConverter(objectMapper)

    /**
     * 消息队列模板，入队消息使用 jackson 来序列化
     */
    @Bean
    fun rabbitMqTemplate(connection: ConnectionFactory) : RabbitTemplate {
        val tpl = RabbitTemplate(connection)
        tpl.messageConverter = jackson2JsonMessageConverter()
        return tpl
    }

    @Bean
    fun jacksonHandlerMethodFactory() : MessageHandlerMethodFactory {
        val factory = DefaultMessageHandlerMethodFactory()
        factory.setMessageConverter(jackson2Converter())
        return factory
    }

    override fun configureRabbitListeners(registrar: RabbitListenerEndpointRegistrar) {
        registrar.messageHandlerMethodFactory = jacksonHandlerMethodFactory()
    }

    @Bean
    fun codeListenerContainerFactory(connectionFactory: ConnectionFactory) : SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setChannelTransacted(true)
        factory.setMaxConcurrentConsumers(5)
        factory.setConcurrentConsumers(2)
        factory.setConnectionFactory(connectionFactory)
        factory.setMessageConverter(jackson2JsonMessageConverter())
        factory.setPrefetchCount(5)
        return factory
    }

    // 短信发送地队列
    @Bean fun smsQueue() = Queue("sms.queue", true, false, false)

    @Bean fun smsExchange() = FanoutExchange("sms.exchange", true, false)

    // 书享 码 批次队列
    @Bean fun codeBatchQueue() = Queue("code_batch.queue", true, false, false)

    @Bean fun codeBatchExchange() = FanoutExchange("code_batch.exchange", true, false)

    // 消费一个持有
    @Bean fun consumeObtainQueue() = Queue("consume_obtain.queue", true, false, false)

    @Bean fun consumeObtainExchange() = FanoutExchange("consume_obtain.exchange", true, false)

    // 申请一个持有
    @Bean fun requestObtainQueue() = Queue("request_obtain.queue", true, false, false)

    @Bean fun requestObtainExchange() = FanoutExchange("request_obtain.exchange", true, false)

    // 微信支付
    @Bean fun wxPayQueue() = Queue("wxpay.queue", true, false, false)

    @Bean fun wxPayExchange() = FanoutExchange("wxpay.exchange", true, false)

}