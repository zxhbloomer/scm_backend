package com.xinyirun.scm.mqconsumer.business.ai;

import com.rabbitmq.client.Channel;
import com.xinyirun.scm.ai.repository.elasticsearch.AiKnowledgeBaseEmbeddingRepository;
import com.xinyirun.scm.ai.repository.neo4j.KnowledgeBaseSegmentRepository;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.clickhouse.service.mq.SLogMqConsumerClickHouseService;
import com.xinyirun.scm.framework.utils.mq.MessageUtil;
import com.xinyirun.scm.mq.rabbitmq.enums.MQEnum;
import com.xinyirun.scm.mqconsumer.base.BaseMqConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * AI文档删除消费者
 *
 * <p>功能：</p>
 * <ul>
 *   <li>接收文档删除后的清理任务</li>
 *   <li>删除Elasticsearch中的向量数据</li>
 *   <li>删除Neo4j中的图谱数据（实体、关系、文本段）</li>
 * </ul>
 *
 * <p>队列配置：</p>
 * <ul>
 *   <li>队列：scm_ai_document_deletion</li>
 *   <li>交换机：scm_ai_document_deletion_exchange</li>
 *   <li>路由键：scm_ai_document_deletion.#</li>
 * </ul>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DocumentDeletionConsumer extends BaseMqConsumer {

    private final SLogMqConsumerClickHouseService consumerService;
    private final AiKnowledgeBaseEmbeddingRepository embeddingRepository;
    private final KnowledgeBaseSegmentRepository segmentRepository;

    private MqSenderAo mqSenderAo;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = MQEnum.MqInfo.AI_DOCUMENT_DELETION_QUEUE.queueCode, durable = "true"),
                    exchange = @Exchange(name = MQEnum.MqInfo.AI_DOCUMENT_DELETION_QUEUE.exchange, durable = "true", type = "topic"),
                    key = MQEnum.MqInfo.AI_DOCUMENT_DELETION_QUEUE.routing_key
            )
    )
    @RabbitHandler
    public void onMessage(@Payload Message messageDataObject, @Headers Map<String, Object> headers, Channel channel) throws IOException {
        String message_id = (String) headers.get(AmqpHeaders.MESSAGE_ID);
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        log.info("收到文档删除消息，message_id: {}, deliveryTag: {}", message_id, deliveryTag);

        MqSenderAo mqSenderAo = new MqSenderAo();

        try {
            // 1. 解析消息
            mqSenderAo = MessageUtil.getMessageBodyBean(messageDataObject);
            setTenantDataSource(mqSenderAo); // 多租户数据源切换

            @SuppressWarnings("unchecked")
            Map<String, Object> messageContext = (Map<String, Object>) MessageUtil.getMessageContextBean(messageDataObject);

            String item_uuid = (String) messageContext.get("item_uuid");
            String kb_uuid = (String) messageContext.get("kb_uuid");

            log.info("开始清理文档数据，item_uuid: {}, kb_uuid: {}", item_uuid, kb_uuid);

            // TODO: 从SecurityContext或消息中获取tenant_id
            String tenant_id = "tenant_1"; // 临时硬编码

            // 2. 删除Elasticsearch中的向量数据
            long deletedEmbeddings = embeddingRepository.deleteByKbItemUuid(item_uuid);
            log.info("删除Elasticsearch向量数据成功，item_uuid: {}, 删除数量: {}", item_uuid, deletedEmbeddings);

            // 3. 删除Neo4j中的文本段数据（级联删除关联的实体和关系）
            Integer deletedSegments = segmentRepository.deleteByItemUuidAndTenantId(item_uuid, tenant_id);
            log.info("删除Neo4j文本段数据成功，item_uuid: {}, 删除数量: {}", item_uuid, deletedSegments);

            // 4. TODO: 删除文件存储中的物理文件（如果需要）
            // fileService.deleteFile(file_url);

            log.info("文档数据清理完成，item_uuid: {}", item_uuid);

        } catch (Exception e) {
            log.error("文档删除清理失败，message_id: {}, error: {}", message_id, e.getMessage(), e);

            // 记录失败日志到ClickHouse
            // consumerService.insert(vo, headers, mqSenderAo);

            throw new RuntimeException("文档删除清理失败", e);

        } finally {
            // 确认消息（无论成功失败都确认，避免消息堆积）
            channel.basicAck(deliveryTag, false);
        }
    }
}
