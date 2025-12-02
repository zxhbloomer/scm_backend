package com.xinyirun.scm.mqconsumer.business.ai;

import com.rabbitmq.client.Channel;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseItemMapper;
import com.xinyirun.scm.ai.core.service.DocumentIndexingService;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.clickhouse.service.mq.SLogMqConsumerClickHouseService;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mqconsumer.base.BaseMqConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * AI文档索引消费者
 *
 * <p>功能：</p>
 * <ul>
 *   <li>接收文档上传后的索引任务</li>
 *   <li>执行文档处理（文本分块、向量化）</li>
 *   <li>更新MySQL文档索引状态</li>
 * </ul>
 *
 * <p>队列配置：</p>
 * <ul>
 *   <li>队列：scm_ai_document_indexing</li>
 *   <li>交换机：scm_ai_document_indexing_exchange</li>
 *   <li>路由键：scm_ai_document_indexing.#</li>
 * </ul>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentIndexingConsumer extends BaseMqConsumer {

    private final SLogMqConsumerClickHouseService consumerService;
    private final AiKnowledgeBaseItemMapper itemMapper;
    private final DocumentIndexingService documentIndexingService;

    private MqSenderAo mqSenderAo;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MQEnum.MqInfo.AI_DOCUMENT_INDEXING_QUEUE.queueCode, durable = "true"),
                    exchange = @Exchange(name = MQEnum.MqInfo.AI_DOCUMENT_INDEXING_QUEUE.exchange, durable = "true", type = "topic"),
                    key = MQEnum.MqInfo.AI_DOCUMENT_INDEXING_QUEUE.routing_key
            )
    )
    @RabbitHandler
    public void onMessage(@Payload Message messageDataObject, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        String message_id = (String) headers.get(AmqpHeaders.MESSAGE_ID);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        log.info("收到文档索引消息，message_id: {}, deliveryTag: {}", message_id, deliveryTag);

        try {
            // 1. 解析消息
            mqSenderAo = MessageUtil.getMessageBodyBean(messageDataObject);
            setTenantDataSource(mqSenderAo); // 多租户数据源切换

            @SuppressWarnings("unchecked")
            Map<String, Object> messageContext = (Map<String, Object>) MessageUtil.getMessageContextBean(messageDataObject);

            String item_uuid = (String) messageContext.get("item_uuid");
            String kb_uuid = (String) messageContext.get("kb_uuid");
            String file_url = (String) messageContext.get("file_url");
            String file_name = (String) messageContext.get("file_name");
            @SuppressWarnings("unchecked")
            List<String> index_types = (List<String>) messageContext.get("index_types");

            log.info("开始处理文档索引，item_uuid: {}, kb_uuid: {}, file_name: {}, index_types: {}",
                    item_uuid, kb_uuid, file_name, index_types);
            // 设置租户数据库
            setTenantDataSource(mqSenderAo);

            // 2. 执行文档索引处理（状态更新在 DocumentIndexingService 中处理）
            String tenantCode = mqSenderAo.getTenant_code();
            documentIndexingService.processDocument(tenantCode, item_uuid, kb_uuid, file_url, file_name, index_types);

            log.info("文档索引处理完成，item_uuid: {}", item_uuid);

        } catch (Exception e) {
            log.error("文档索引处理失败，message_id: {}, error: {}", message_id, e.getMessage(), e);
            throw new RuntimeException("文档索引处理失败", e);

        } finally {
            // 确认消息（无论成功失败都确认，避免消息堆积）
            channel.basicAck(deliveryTag, false);
        }
    }
}
