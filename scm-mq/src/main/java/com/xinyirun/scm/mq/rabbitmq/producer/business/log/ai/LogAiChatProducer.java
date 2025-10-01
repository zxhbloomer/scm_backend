package com.xinyirun.scm.mq.rabbitmq.producer.business.log.ai;

import com.xinyirun.scm.bean.clickhouse.vo.ai.SLogAiChatVo;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.ao.mqsender.builder.MqSenderAoBuilder;
import com.xinyirun.scm.common.enums.mq.MqSenderEnum;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mq.rabbitmq.producer.ScmMqProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * AI聊天日志MQ生产者
 *
 * <p>职责：
 * 异步发送AI聊天日志消息到RabbitMQ队列scm_ai_chat_log
 *
 * <p>使用场景：
 * 在AiConversationService保存对话内容到MySQL后，调用此Producer发送MQ消息
 *
 * <p>架构模式：
 * MySQL保存 → Producer异步发送MQ → Consumer消费 → ClickHouse插入
 *
 * <p>设计要点：
 * - @Async("logExecutor")：使用日志专用线程池，不阻塞主业务流程
 * - 异常处理：发送失败不抛出异常，避免影响主业务
 * - 租户上下文：从VO中获取tenant_code并设置到MqSenderAo
 *
 * @author AI Chat Logging System
 * @since 2025-09-30
 * @see com.xinyirun.scm.ai.core.service.chat.AiConversationService
 * @see com.xinyirun.scm.mq.consumer.business.log.ai.LogAiChatConsumer
 */
@Component
@Slf4j
public class LogAiChatProducer {

    @Autowired
    private ScmMqProducer mqProducer;

    /**
     * 异步发送AI聊天日志消息到MQ
     *
     * <p>使用@Async注解确保异步执行，不阻塞AI聊天主业务流程
     *
     * <p>发送流程：
     * 1. 构建MqSenderAo消息包装对象
     * 2. 设置租户编码（用于Consumer端的多租户数据隔离）
     * 3. 调用ScmMqProducer发送到MQ_LOG_AI_CHAT_QUEUE队列
     *
     * <p>异常处理：
     * 发送失败仅记录日志，不抛出异常，确保AI聊天主业务不受影响
     *
     * @param data AI聊天日志VO对象，包含完整的对话信息
     */
    @Async("logExecutor")
    public void mqSendMq(SLogAiChatVo data) {
        try {
            // 构建MQ消息包装对象
            MqSenderAo ao = MqSenderAoBuilder.buildMqSenderAo(data, MqSenderEnum.MQ_LOG_AI_CHAT_QUEUE);

            // 设置租户编码，用于Consumer端的多租户数据隔离
            ao.setTenant_code(data.getTenant_code());

            // 异步发送消息到MQ_LOG_AI_CHAT_QUEUE队列
            mqProducer.send(ao, MQEnum.MQ_LOG_AI_CHAT_QUEUE);

            log.debug("发送AI聊天日志MQ消息成功，conversation_id: {}, type: {}, tenant_code: {}",
                    data.getConversation_id(), data.getType(), data.getTenant_code());

        } catch (Exception e) {
            // 异步处理，不抛出异常，避免影响主业务
            log.error("发送AI聊天日志MQ消息失败，conversation_id: {}, type: {}, tenant_code: {}",
                    data != null ? data.getConversation_id() : "null",
                    data != null ? data.getType() : "null",
                    data != null ? data.getTenant_code() : "null", e);
        }
    }
}