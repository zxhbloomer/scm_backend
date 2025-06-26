package com.xinyirun.scm.mq.rabbitmq.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ客户端配置
 */
@ConditionalOnProperty(prefix = "spring.rabbitmq.custom.consumer", name = "has-open", havingValue = "true")
@Configuration
public class ConsumerConfiguration {

//    @Bean
//    public SimpleMessageListenerContainer listenerContainer(ConnectionFactory connectionFactory,
//                                        IChannelAwareMessageListener iChannelAwareMessageListener) {
//        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
//        container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
//        container.setConnectionFactory(connectionFactory);
//        container.setQueueNames(mqProperties.getConsumer().getDefaultQueue());
//        container.setMessageListener(iChannelAwareMessageListener);
//        return container;
//    }

    @Bean
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        return factory;
    }
}
