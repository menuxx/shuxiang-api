package com.menuxx.common.cfg

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.FanoutExchange
import org.springframework.amqp.core.Queue
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
@Configuration
class RabbitMqConfig (private val objectMapper: ObjectMapper) : RabbitListenerConfigurer {

    @Bean
    fun jackson2Converter() : MappingJackson2MessageConverter {
        val converter = MappingJackson2MessageConverter()
        converter.objectMapper = objectMapper
        return converter
    }

    /**
     * 消息队列模板，入队消息使用 jackson 来序列化
     */
    @Bean
    fun rabbitMqTemplate(connection: ConnectionFactory) : RabbitTemplate {
        val tpl = RabbitTemplate(connection)
        tpl.messageConverter = Jackson2JsonMessageConverter(objectMapper)
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
    fun rabbitListenerContainerFactory(connectionFactory: ConnectionFactory) : SimpleRabbitListenerContainerFactory {
        val factory = SimpleRabbitListenerContainerFactory()
        factory.setMaxConcurrentConsumers(2)
        factory.setConnectionFactory(connectionFactory)
        factory.setMessageConverter(Jackson2JsonMessageConverter(objectMapper))
        factory.setDefaultRequeueRejected(false)
        return factory
    }

    // 短信发送地队列
    @Bean fun smsQueue() = Queue("sms_queue")

    @Bean fun smsExchange() = FanoutExchange("sms_exchange")

    // 书享 码 批次队列
    @Bean fun codeBatchQueue() = Queue("code_batch_queue")

    @Bean fun codeBatchExchange() = FanoutExchange("code_batch_exchange")

}