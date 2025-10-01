package com.xinyirun.scm.mqconsumer.business.log.ai;

import com.alibaba.fastjson2.JSONObject;
import com.rabbitmq.client.Channel;
import com.xinyirun.scm.bean.clickhouse.vo.ai.SLogAiChatVo;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqConsumerClickHouseVo;
import com.xinyirun.scm.clickhouse.service.ai.SLogAiChatClickHouseService;
import com.xinyirun.scm.clickhouse.service.mq.SLogMqConsumerClickHouseService;
import com.xinyirun.scm.common.exception.mq.MessageConsumerQueueException;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mqconsumer.base.BaseMqConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI聊天日志MQ消费者
 *
 * <p>职责：
 * 消费MQ队列scm_ai_chat_log中的AI聊天日志消息，并将数据插入ClickHouse
 *
 * <p>消费流程：
 * 1. 接收MQ消息（SLogAiChatVo）
 * 2. 设置租户数据源上下文
 * 3. 调用ClickHouse Service插入日志
 * 4. 记录消费成功/失败到s_log_mq_consumer表
 * 5. 确认消息（ACK）
 *
 * <p>异常处理：
 * - 消费失败记录到s_log_mq_consumer表（type=NG）
 * - 抛出MessageConsumerQueueException触发RabbitMQ重试机制
 * - 最大重试3次（RabbitMQ配置）
 *
 * <p>多租户支持：
 * - 从MqSenderAo中获取tenant_code
 * - 调用setTenantDataSource设置租户上下文
 * - 确保ClickHouse插入时使用正确的租户标识
 *
 * @author AI Chat Logging System
 * @since 2025-09-30
 * @see com.xinyirun.scm.mq.rabbitmq.producer.business.log.ai.LogAiChatProducer
 * @see com.xinyirun.scm.clickhouse.service.ai.SLogAiChatClickHouseService
 */
@Component
@Slf4j
public class LogAiChatConsumer extends BaseMqConsumer {

    @Autowired
    private SLogAiChatClickHouseService sLogAiChatClickHouseService;

    @Autowired
    private SLogMqConsumerClickHouseService consumerService;

    /**
     * RabbitMQ监听器 - 消费AI聊天日志队列
     *
     * <p>队列配置：
     * - 队列名称：scm_ai_chat_log
     * - 交换机：scm_ai_chat_log（Topic Exchange）
     * - 路由键：scm_ai_chat_log.#
     * - 持久化：true
     *
     * <p>消费逻辑：
     * 1. 解析MQ消息体（MqSenderAo和SLogAiChatVo）
     * 2. 设置租户数据源（setTenantDataSource）
     * 3. 插入AI聊天日志到ClickHouse
     * 4. 记录消费成功日志
     * 5. 异常情况记录失败日志并重试
     *
     * @param messageDataObject MQ消息对象
     * @param headers MQ消息头（包含message_id、delivery_tag等）
     * @param channel RabbitMQ通道（用于ACK确认）
     * @throws IOException IO异常
     */
    @RabbitListener(
        bindings = @QueueBinding(
            value = @Queue(value = MQEnum.MqInfo.LOG_AI_CHAT_QUEUE.queueCode, durable = "true"),
            exchange = @Exchange(name = MQEnum.MqInfo.LOG_AI_CHAT_QUEUE.exchange, durable = "true", type = "topic"),
            key = MQEnum.MqInfo.LOG_AI_CHAT_QUEUE.routing_key
        )
    )
    @RabbitHandler
    public void onMessage(@Payload Message messageDataObject, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        String message_id = (String) headers.get(AmqpHeaders.MESSAGE_ID);
        log.debug("------AI聊天日志消费者开始消费：message_id {}-----", message_id);

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        MqSenderAo mqSenderAo = new MqSenderAo();
        try {
            // 1. 解析MQ消息体
            mqSenderAo = MessageUtil.getMessageBodyBean(messageDataObject);

            // 2. 设置租户数据库上下文
            setTenantDataSource(mqSenderAo);

            // 3. 获取AI聊天日志VO对象
            Object messageContext = MessageUtil.getMessageContextBean(messageDataObject);
            SLogAiChatVo vo = (SLogAiChatVo) messageContext;

            // 4. 插入ClickHouse
            sLogAiChatClickHouseService.insert(vo);

            log.info("插入AI聊天日志到ClickHouse成功，conversation_id: {}, type: {}, tenant_code: {}",
                    vo.getConversation_id(), vo.getType(), vo.getTenant_code());

            // 5. 记录消费成功日志到s_log_mq_consumer表
            SLogMqConsumerClickHouseVo consumerVo = new SLogMqConsumerClickHouseVo();
            consumerVo.setMessage_id(message_id);
            consumerVo.setConsumer_c_time(LocalDateTime.now());
            consumerVo.setConsumer_status(0);  // 0表示成功
            consumerVo.setType("OK");
            consumerVo.setTenant_code(mqSenderAo.getTenant_code());
            consumerVo.setMq_data(JSONObject.toJSONString(messageDataObject));
            consumerService.insert(consumerVo, headers, mqSenderAo);

        } catch (Exception e) {
            // 6. 记录消费失败日志到s_log_mq_consumer表
            SLogMqConsumerClickHouseVo vo = new SLogMqConsumerClickHouseVo();
            vo.setMessage_id(message_id);
            vo.setConsumer_c_time(LocalDateTime.now());
            vo.setConsumer_exception(e.getMessage());
            vo.setConsumer_status(0);  // 0表示失败
            vo.setType("NG");  // NG表示失败
            vo.setMq_data(JSONObject.toJSONString(messageDataObject));
            consumerService.insert(vo, headers, mqSenderAo);

            log.error("------AI聊天日志消费者消费失败：message_id {}-----", message_id);
            log.error("消费AI聊天日志失败", e);

            // 7. 抛出异常触发RabbitMQ重试机制（最大重试3次）
            throw new MessageConsumerQueueException(e);

        } finally {
            // 8. 确认消息（ACK），无论成功还是失败都要确认
            channel.basicAck(deliveryTag, false);
            log.debug("------AI聊天日志消费者消费结束：message_id {}-----", message_id);
        }
    }
}