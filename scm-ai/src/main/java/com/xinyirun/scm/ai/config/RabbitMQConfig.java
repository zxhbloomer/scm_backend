package com.xinyirun.scm.ai.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ 配置类 - AI 知识库专用队列
 * 用于异步处理文档索引和删除操作
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Configuration
public class RabbitMQConfig {

    /**
     * 队列名称常量
     */
    public static final String DOCUMENT_INDEXING_QUEUE = "ai.knowledge.base.document.indexing";
    public static final String DOCUMENT_DELETION_QUEUE = "ai.knowledge.base.document.deletion";
    public static final String KB_DELETION_QUEUE = "ai.knowledge.base.deletion";

    /**
     * 队列最大长度限制
     */
    private static final int MAX_QUEUE_LENGTH = 1000;

    /**
     * 文档索引队列
     * 用于异步处理文档的向量化和索引操作
     *
     * @return 文档索引队列
     */
    @Bean
    public Queue documentIndexingQueue() {
        return QueueBuilder.durable(DOCUMENT_INDEXING_QUEUE)
                .maxLength(MAX_QUEUE_LENGTH)
                .build();
    }

    /**
     * 文档删除队列
     * 用于异步处理文档在 Elasticsearch 和 Neo4j 中的删除操作
     *
     * @return 文档删除队列
     */
    @Bean
    public Queue documentDeletionQueue() {
        return QueueBuilder.durable(DOCUMENT_DELETION_QUEUE)
                .maxLength(MAX_QUEUE_LENGTH)
                .build();
    }

    /**
     * 知识库删除队列
     * 用于异步处理知识库级联删除操作（包括所有文档、向量和图谱数据）
     *
     * @return 知识库删除队列
     */
    @Bean
    public Queue kbDeletionQueue() {
        return QueueBuilder.durable(KB_DELETION_QUEUE)
                .maxLength(MAX_QUEUE_LENGTH)
                .build();
    }

    /**
     * 消息转换器 - 使用 Jackson2 JSON 转换器
     * 将 Java 对象转换为 JSON 消息
     *
     * @return JSON 消息转换器
     */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * RabbitTemplate 配置
     * 配置消息发送模板，使用 JSON 转换器
     *
     * @param connectionFactory RabbitMQ 连接工厂
     * @param jsonMessageConverter JSON 消息转换器
     * @return 配置好的 RabbitTemplate
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter jsonMessageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter);
        return rabbitTemplate;
    }
}
