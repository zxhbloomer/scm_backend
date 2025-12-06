package com.xinyirun.scm.ai.core.service.milvus;

import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.core.event.VectorIndexCompletedEvent;
import com.xinyirun.scm.ai.core.service.splitter.JTokkitTokenTextSplitter;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Milvus向量索引服务
 *
 * 功能说明:
 * - 将文档分割为文本段,通过VectorStore自动生成embedding并存储到Milvus
 * - 使用Spring AI VectorStore抽象
 *
 * 核心流程:
 * 1. 文本分割 - 使用JTokkitTokenTextSplitter
 * 2. 构建Document - 包含metadata(kb_uuid, kb_item_uuid等)
 * 3. VectorStore.add() - 自动生成embedding并存储
 *
 * @author SCM AI Team
 * @since 2025-12-02
 */
@Service
@Slf4j
public class MilvusVectorIndexingService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * 执行文档向量化索引
     *
     * @param kb 知识库配置
     * @param item 文档项
     * @return 索引的文本段数量
     */
    public int ingestDocument(AiKnowledgeBaseEntity kb, AiKnowledgeBaseItemEntity item) {
        try {
            log.info("开始向量化索引, item_uuid: {}, kb_uuid: {}", item.getItemUuid(), item.getKbUuid());
            log.info("[METADATA调试] 输入数据 - title: [{}], brief: [{}], remark长度: {}",
                    item.getTitle(), item.getBrief(),
                    item.getRemark() != null ? item.getRemark().length() : 0);

            // 1. 文本分割
            List<String> textSegments = splitDocument(item.getRemark(), kb);
            log.info("文本分割完成, item_uuid: {}, 文本段数量: {}", item.getItemUuid(), textSegments.size());

            // 2. 构建Spring AI Document列表
            List<Document> documents = new ArrayList<>();
            for (int i = 0; i < textSegments.size(); i++) {
                String segment = textSegments.get(i);

                // 构建metadata
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("kb_uuid", item.getKbUuid());
                metadata.put("kb_item_uuid", item.getItemUuid());
                metadata.put("segment_index", i);
                metadata.put("total_segments", textSegments.size());
                metadata.put("file_name", item.getSourceFileName() != null ? item.getSourceFileName() : "");
                metadata.put("tenant_code", extractTenantCodeFromKbUuid(item.getKbUuid()));
                metadata.put("title", item.getTitle() != null ? item.getTitle() : "");
                metadata.put("brief", item.getBrief() != null ? item.getBrief() : "");

                // 调试日志: 打印完整metadata
                log.info("[METADATA调试] 第{}段metadata: {}", i, metadata);

                // 创建Document(id使用UuidUtil生成)
                Document document = new Document(UuidUtil.createShort(), segment, metadata);
                documents.add(document);
            }

            // 3. 分批添加到Milvus(SiliconFlow embedding API限制batch size <= 64)
            int batchSize = 50;  // 安全批量大小
            int totalBatches = (documents.size() + batchSize - 1) / batchSize;

            for (int batchIndex = 0; batchIndex < totalBatches; batchIndex++) {
                int start = batchIndex * batchSize;
                int end = Math.min(start + batchSize, documents.size());
                List<Document> batch = documents.subList(start, end);

                log.info("正在处理第 {}/{} 批, 文档数: {}", batchIndex + 1, totalBatches, batch.size());
                vectorStore.add(batch);
            }

            log.info("向量化索引完成, item_uuid: {}, 成功索引: {} 个文本段",
                    item.getItemUuid(), documents.size());

            // 4. 发布向量索引完成事件
            String tenantCode = extractTenantCodeFromKbUuid(item.getKbUuid());
            VectorIndexCompletedEvent event = new VectorIndexCompletedEvent(
                    this,
                    item.getKbUuid(),
                    item.getItemUuid(),
                    true,
                    null,
                    documents.size(),
                    tenantCode
            );
            eventPublisher.publishEvent(event);

            return documents.size();

        } catch (Exception e) {
            log.error("向量化索引失败, item_uuid: {}, 错误: {}", item.getItemUuid(), e.getMessage(), e);

            // 发布向量索引失败事件
            String tenantCode = extractTenantCodeFromKbUuid(item.getKbUuid());
            VectorIndexCompletedEvent event = new VectorIndexCompletedEvent(
                    this,
                    item.getKbUuid(),
                    item.getItemUuid(),
                    false,
                    e.getMessage(),
                    0,
                    tenantCode
            );
            eventPublisher.publishEvent(event);

            throw new RuntimeException("向量化索引失败: " + e.getMessage(), e);
        }
    }

    /**
     * 分割文档为文本段
     *
     * @param content 文档内容
     * @param kb 知识库配置
     * @return 分割后的文本段列表
     */
    private List<String> splitDocument(String content, AiKnowledgeBaseEntity kb) {
        int overlap = kb.getIngestMaxOverlap() != null ? kb.getIngestMaxOverlap() : 50;

        JTokkitTokenTextSplitter splitter = JTokkitTokenTextSplitter.builder()
                .withOverlapSize(overlap)
                .build();

        Document document = new Document(content);

        return splitter.apply(Collections.singletonList(document))
                .stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }

    /**
     * 删除文档的所有embedding
     *
     * @param itemUuid 文档UUID
     * @return 删除结果(1表示成功)
     */
    public int deleteDocumentEmbeddings(String itemUuid) {
        try {
            log.info("开始删除文档向量, item_uuid: {}", itemUuid);

            // 使用filter表达式删除
            String filterExpression = String.format("kb_item_uuid == '%s'", itemUuid);
            vectorStore.delete(filterExpression);

            log.info("文档向量删除完成, item_uuid: {}", itemUuid);
            return 1;

        } catch (Exception e) {
            log.error("文档向量删除失败, item_uuid: {}, 错误: {}", itemUuid, e.getMessage(), e);
            throw new RuntimeException("文档向量删除失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除知识库的所有embedding
     *
     * @param kbUuid 知识库UUID
     * @return 删除结果(1表示成功)
     */
    public int deleteKnowledgeBaseEmbeddings(String kbUuid) {
        try {
            log.info("开始删除知识库向量, kb_uuid: {}", kbUuid);

            String filterExpression = String.format("kb_uuid == '%s'", kbUuid);
            vectorStore.delete(filterExpression);

            log.info("知识库向量删除完成, kb_uuid: {}", kbUuid);
            return 1;

        } catch (Exception e) {
            log.error("知识库向量删除失败, kb_uuid: {}, 错误: {}", kbUuid, e.getMessage(), e);
            throw new RuntimeException("知识库向量删除失败: " + e.getMessage(), e);
        }
    }

    /**
     * 统计知识库的文本段数量
     *
     * @param kbUuid 知识库UUID
     * @return 文本段总数
     */
    public Long countSegmentsByKbUuid(String kbUuid) {
        try {
            // Milvus通过SearchRequest统计
            // 使用一个不可能匹配的查询,只获取count
            SearchRequest request = SearchRequest.builder()
                    .query("count_query_placeholder")
                    .topK(1)
                    .filterExpression(String.format("kb_uuid == '%s'", kbUuid))
                    .build();

            // 注意: Spring AI VectorStore目前不直接支持count操作
            // 这里通过Milvus客户端直接查询(后续优化)
            // 临时方案: 返回0,由统计服务使用其他方式统计
            log.warn("Milvus count操作暂未实现, kb_uuid: {}, 返回0", kbUuid);
            return 0L;

        } catch (Exception e) {
            log.error("统计知识库文本段数量失败, kb_uuid: {}", kbUuid, e);
            return 0L;
        }
    }

    /**
     * 从kb_uuid中提取tenant_code
     *
     * @param kbUuid 知识库UUID(格式: tenant_code::uuid)
     * @return tenant_code
     */
    private String extractTenantCodeFromKbUuid(String kbUuid) {
        if (kbUuid == null || !kbUuid.contains("::")) {
            log.warn("kb_uuid格式不正确, 无法提取tenant_code: {}", kbUuid);
            return "";
        }
        return kbUuid.split("::")[0];
    }
}
