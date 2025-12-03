package com.xinyirun.scm.ai.core.service;

import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.core.event.GraphIndexCompletedEvent;
import com.xinyirun.scm.ai.core.event.VectorIndexCompletedEvent;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseItemMapper;
import com.xinyirun.scm.ai.core.mapper.rag.AiKnowledgeBaseMapper;
import com.xinyirun.scm.ai.core.service.milvus.MilvusVectorIndexingService;
import com.xinyirun.scm.ai.core.service.Neo4jGraphIndexingService;
import com.xinyirun.scm.bean.system.vo.business.ai.KnowledgeBaseStatisticsParamVo;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 知识库统计服务
 * 负责更新知识库和文档的索引状态及统计信息
 *
 * @author SCM System
 */
@Slf4j
@Service
public class KnowledgeBaseStatisticsService {

    @Autowired
    private AiKnowledgeBaseItemMapper itemMapper;

    @Autowired
    private AiKnowledgeBaseMapper kbMapper;

    @Autowired
    private MilvusVectorIndexingService milvusService;

    @Autowired
    private Neo4jGraphIndexingService neo4jService;

    /**
     * 更新文档的向量索引状态
     * 标准操作：selectById → set修改 → updateById
     *
     * @param kb_item_uuid 文档UUID
     * @param status 状态：PENDING/INDEXING/COMPLETED/FAILED
     * @param error_message 错误信息（失败时）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateItemVectorIndexStatus(String kb_item_uuid, String status, String error_message) {
        AiKnowledgeBaseItemEntity item = itemMapper.selectByItemUuid(kb_item_uuid);
        if (item == null) {
            log.warn("文档不存在，无法更新向量索引状态: {}", kb_item_uuid);
            return;
        }

        // 设置向量化状态: 1-待处理, 2-处理中, 3-已完成, 4-失败
        if ("COMPLETED".equals(status)) {
            item.setEmbeddingStatus(3);
        } else if ("FAILED".equals(status)) {
            item.setEmbeddingStatus(4);
        }
        item.setEmbeddingStatusChangeTime(java.time.LocalDateTime.now());

        int updCount = itemMapper.updateById(item);
        if (updCount == 0) {
            log.error("更新文档向量索引状态失败，数据可能已被修改: {}", kb_item_uuid);
        }

        log.info("更新文档向量索引状态成功: kb_item_uuid={}, status={}", kb_item_uuid, status);
    }

    /**
     * 更新文档的图谱索引状态
     * 标准操作：selectById → set修改 → updateById
     *
     * @param kb_item_uuid 文档UUID
     * @param status 状态：PENDING/INDEXING/COMPLETED/FAILED
     * @param error_message 错误信息（失败时）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateItemGraphIndexStatus(String kb_item_uuid, String status, String error_message) {
        AiKnowledgeBaseItemEntity item = itemMapper.selectByItemUuid(kb_item_uuid);
        if (item == null) {
            log.warn("文档不存在，无法更新图谱索引状态: {}", kb_item_uuid);
            return;
        }

        // 设置图谱化状态: 1-待处理, 2-处理中, 3-已完成, 4-失败
        if ("COMPLETED".equals(status)) {
            item.setGraphicalStatus(3);
        } else if ("FAILED".equals(status)) {
            item.setGraphicalStatus(4);
        }
        item.setGraphicalStatusChangeTime(java.time.LocalDateTime.now());

        int updCount = itemMapper.updateById(item);
        if (updCount == 0) {
            log.error("更新文档图谱索引状态失败: {}", kb_item_uuid);
        }

        log.info("更新文档图谱索引状态成功: kb_item_uuid={}, status={}", kb_item_uuid, status);
    }

    /**
     * 更新知识库的统计信息（全量统计）
     * 标准操作：selectById → 查询统计SQL → set修改 → updateById
     *
     * @param kb_uuid 知识库UUID
     * @param tenant_code 租户代码
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateKnowledgeBaseStatistics(String kb_uuid, String tenant_code) {
        try {
            DataSourceHelper.use(tenant_code);

            AiKnowledgeBaseEntity kb = kbMapper.selectByKbUuid(kb_uuid);
            if (kb == null) {
                log.warn("知识库不存在，无法更新统计信息: {}", kb_uuid);
                return;
            }

            // 统计已完成索引的文档数量（embeddingStatus=3）
            Long item_count = itemMapper.countByKbUuidAndEmbeddingStatus(kb_uuid, 3);

            // 统计向量段数量
            long segment_count = milvusService.countSegmentsByKbUuid(kb_uuid);

            // 统计图谱元素数量
            Map<String, Long> graphStats = neo4jService.countGraphElementsByKbUuid(kb_uuid);
            long entity_count = graphStats.getOrDefault("entity_count", 0L);
            long relation_count = graphStats.getOrDefault("relation_count", 0L);

            kb.setItemCount(item_count != null ? item_count.intValue() : 0);
            kb.setEmbeddingCount((int) segment_count);
            kb.setEntityCount((int) entity_count);
            kb.setRelationCount((int) relation_count);

            int updCount = kbMapper.updateById(kb);
            if (updCount == 0) {
                log.error("更新知识库统计失败，数据可能已被修改: {}", kb_uuid);
            }

            log.info("更新知识库统计成功: kb_uuid={}, itemCount={}, embeddingCount={}, entityCount={}, relationCount={}",
                    kb_uuid, item_count, segment_count, entity_count, relation_count);

        } finally {
            DataSourceHelper.close();
        }
    }

    /**
     * 定时任务调用入口：更新知识库统计信息
     * 此方法由 Quartz 调度器调用
     *
     * @param paramVo 参数对象（包含 kbUuid 和 tenantCode）
     */
    public void updateStatistics(KnowledgeBaseStatisticsParamVo paramVo) {
        log.info("定时任务开始执行知识库统计更新: kbUuid={}, tenantCode={}, reason={}",
                paramVo.getKbUuid(), paramVo.getTenantCode(), paramVo.getTriggerReason());

        try {
            updateKnowledgeBaseStatistics(paramVo.getKbUuid(), paramVo.getTenantCode());
            log.info("定时任务执行完成: kbUuid={}", paramVo.getKbUuid());
        } catch (Exception e) {
            log.error("定时任务执行失败: kbUuid={}, error={}", paramVo.getKbUuid(), e.getMessage(), e);
            throw new RuntimeException("知识库统计更新失败: " + e.getMessage(), e);
        }
    }

    /**
     * 监听向量索引完成事件
     */
    @Async("ragTaskExecutor")
    @EventListener
    public void onVectorIndexCompleted(VectorIndexCompletedEvent event) {
        log.info("接收到向量索引完成事件: kb_item_uuid={}, success={}",
                event.getKb_item_uuid(), event.isSuccess());

        try {
            DataSourceHelper.use(event.getTenant_code());

            if (event.isSuccess()) {
                updateItemVectorIndexStatus(event.getKb_item_uuid(), "COMPLETED", null);
            } else {
                updateItemVectorIndexStatus(event.getKb_item_uuid(), "FAILED", event.getError_message());
            }

            if (event.isSuccess()) {
                updateKnowledgeBaseStatistics(event.getKb_uuid(), event.getTenant_code());
            }

        } catch (Exception e) {
            log.error("处理向量索引完成事件失败", e);
        } finally {
            DataSourceHelper.close();
        }
    }

    /**
     * 监听图谱索引完成事件
     */
    @Async("ragTaskExecutor")
    @EventListener
    public void onGraphIndexCompleted(GraphIndexCompletedEvent event) {
        log.info("接收到图谱索引完成事件: kb_item_uuid={}, success={}",
                event.getKb_item_uuid(), event.isSuccess());

        try {
            DataSourceHelper.use(event.getTenant_code());

            if (event.isSuccess()) {
                updateItemGraphIndexStatus(event.getKb_item_uuid(), "COMPLETED", null);
            } else {
                updateItemGraphIndexStatus(event.getKb_item_uuid(), "FAILED", event.getError_message());
            }

            if (event.isSuccess()) {
                updateKnowledgeBaseStatistics(event.getKb_uuid(), event.getTenant_code());
            }

        } catch (Exception e) {
            log.error("处理图谱索引完成事件失败", e);
        } finally {
            DataSourceHelper.close();
        }
    }
}
