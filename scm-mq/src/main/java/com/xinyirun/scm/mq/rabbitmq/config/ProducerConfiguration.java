package com.xinyirun.scm.mq.rabbitmq.config;

import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.properties.MQProperties;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MQ配置：生产者
 */
@ConditionalOnProperty(name = {"spring.rabbitmq.enable", "spring.rabbitmq.custom.producer.enable"}, havingValue = "true")
@EnableConfigurationProperties(MQProperties.class)
@Configuration
public class ProducerConfiguration {

    @Autowired
    private MQProperties mqProperties;

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public Queue queue(RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue(mqProperties.getProducer().getDefaultQueue(), true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    public Exchange exchange(RabbitAdmin rabbitAdmin) {
        Exchange exchange = ExchangeBuilder.topicExchange(mqProperties.getProducer().getDefaultExchange()).durable(false).build();
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public Binding binding(Queue queue, Exchange exchange, RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(mqProperties.getProducer().getDefaultRoutingKey()).noargs();
        rabbitAdmin.declareBinding(binding);

        /**
         * TODO：初始化项目中使用的mq
         */
        binding(rabbitAdmin, MQEnum.MQ_LOG_QUARTZ_QUEUE);
        // AI模块队列初始化
        binding(rabbitAdmin, MQEnum.MQ_AI_DOCUMENT_INDEXING_QUEUE);
        binding(rabbitAdmin, MQEnum.MQ_AI_DOCUMENT_DELETION_QUEUE);
        binding(rabbitAdmin, MQEnum.MQ_AI_KB_DELETION_QUEUE);
        return binding;
    }

    /**
     * 设置mq
     *
     * @param rabbitAdmin
     * @param mqEnum
     * @return
     */
    public Binding binding(RabbitAdmin rabbitAdmin, MQEnum mqEnum) {
        /**
         * 设置queue
         */
        Queue queue = new Queue(mqEnum.getQueueCode(), true);
        rabbitAdmin.declareQueue(queue);
        /**
         * 设置exchange
         */
        Exchange exchange = ExchangeBuilder.topicExchange(mqEnum.getExchange()).durable(true).build();
        rabbitAdmin.declareExchange(exchange);
        /**
         * 设置binding
         */
        Binding binding = BindingBuilder.bind(queue).to(exchange).with(mqEnum.getRouting_key()).noargs();
        rabbitAdmin.declareBinding(binding);
        return binding;
    }
}
