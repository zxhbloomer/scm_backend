package com.xinyirun.scm.ai.core.service.elasticsearch;

import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseItemEntity;
import com.xinyirun.scm.ai.bean.entity.rag.elasticsearch.AiKnowledgeBaseEmbeddingDoc;
import com.xinyirun.scm.ai.config.AiModelProvider;
import com.xinyirun.scm.ai.core.service.splitter.OverlappingTokenTextSplitter;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.core.query.DeleteQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Elasticsearch向量索引服务
 *
 * <p>功能说明：</p>
 * 严格对应aideepin的EmbeddingRAG.ingest()逻辑
 * 将文档分割为文本段，生成embedding向量，存储到Elasticsearch
 *
 * <p>核心流程：</p>
 * <ol>
 *   <li>文本分割 - 使用TokenTextSplitter（对应aideepin的RecursiveCharacterTextSplitter）</li>
 *   <li>生成embedding - 使用EmbeddingModel（对应aideepin的embeddingModel.embed）</li>
 *   <li>存储向量 - 存储到Elasticsearch（对应aideepin的embeddingStore.add）</li>
 * </ol>
 *
 * <p>参考代码：</p>
 * aideepin: EmbeddingRAG.ingest()
 * 路径: D:\2025_project\20_project_in_github\99_tools\aideepin\langchain4j-aideepin\adi-common\src\main\java\com\moyz\adi\common\rag\EmbeddingRAG.java
 *
 * <p>aideepin核心代码：</p>
 * <pre>
 * public void ingest(Document document, int overlap, String tokenEstimator, ChatModel chatModel) {
 *     DocumentSplitter splitter = DocumentSplitters.recursive(
 *         RAG_MAX_SEGMENT_SIZE_IN_TOKENS,  // 300 tokens
 *         overlap,
 *         TokenEstimatorFactory.create(tokenEstimator)
 *     );
 *
 *     EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
 *         .documentSplitter(splitter)
 *         .embeddingModel(embeddingModel)
 *         .embeddingStore(embeddingStore)
 *         .build();
 *
 *     ingestor.ingest(document);
 * }
 * </pre>
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Service
@Slf4j
public class ElasticsearchIndexingService {

    /**
     * 最大文本段大小（tokens）
     * 对应aideepin的RAG_MAX_SEGMENT_SIZE_IN_TOKENS = 300
     */
    private static final int MAX_SEGMENT_SIZE_IN_TOKENS = 300;

    /**
     * 最小文本段字符数
     * Spring AI的TokenTextSplitter参数
     */
    private static final int MIN_CHUNK_SIZE_CHARS = 5;

    /**
     * 最小文本段长度用于embedding
     * Spring AI的TokenTextSplitter参数
     */
    private static final int MIN_CHUNK_LENGTH_TO_EMBED = 100;

    /**
     * Elasticsearch索引名称
     * 对应aideepin的pgvector表名: adi_knowledge_base_embedding
     */
    private static final String INDEX_NAME = "kb_embeddings";

    @Autowired
    private AiModelProvider aiModelProvider;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    /**
     * 执行文档向量化索引
     * 对应aideepin的EmbeddingRAG.ingest()
     *
     * <p>aideepin调用代码：</p>
     * <pre>
     * if (indexTypes.contains(DOC_INDEX_TYPE_EMBEDDING)) {
     *     Metadata metadata = new Metadata();
     *     metadata.put(AdiConstant.MetadataKey.KB_UUID, item.getKbUuid());
     *     metadata.put(AdiConstant.MetadataKey.KB_ITEM_UUID, item.getUuid());
     *     Document document = new DefaultDocument(item.getRemark(), metadata);
     *     compositeRAG.getEmbeddingRAGService().ingest(document, kb.getIngestMaxOverlap(), ...);
     * }
     * </pre>
     *
     * @param kb 知识库配置
     * @param item 文档项
     * @return 索引的文本段数量
     */
    public int ingestDocument(AiKnowledgeBaseEntity kb, AiKnowledgeBaseItemEntity item) {
        try {
            log.info("开始向量化索引，item_uuid: {}, kb_uuid: {}", item.getItemUuid(), item.getKbUuid());

            // 1. 初始化Elasticsearch索引（如果不存在）
            ensureIndexExists();

            // 2. 文本分割（对应aideepin的DocumentSplitters.recursive）
            List<String> textSegments = splitDocument(item.getRemark(), kb);
            log.info("文本分割完成，item_uuid: {}, 文本段数量: {}", item.getItemUuid(), textSegments.size());

            // 3. 为每个文本段生成embedding并存储
            int indexedCount = 0;
            for (int i = 0; i < textSegments.size(); i++) {
                String segment = textSegments.get(i);

                // 生成embedding向量（对应aideepin的embeddingModel.embed）
                float[] embedding = generateEmbedding(segment);

                // 构建元数据（对应aideepin的Metadata）
                Map<String, Object> metadata = buildMetadata(kb, item, i, textSegments.size());

                // 存储到Elasticsearch（对应aideepin的embeddingStore.add）
                storeEmbedding(segment, embedding, metadata);

                indexedCount++;
            }

            log.info("向量化索引完成，item_uuid: {}, 成功索引: {} 个文本段", item.getItemUuid(), indexedCount);
            return indexedCount;

        } catch (Exception e) {
            log.error("向量化索引失败，item_uuid: {}, 错误: {}", item.getItemUuid(), e.getMessage(), e);
            throw new RuntimeException("向量化索引失败: " + e.getMessage(), e);
        }
    }

    /**
     * 分割文档为文本段
     * 对应aideepin的DocumentSplitters.recursive()
     *
     * <p>aideepin实现：</p>
     * <pre>
     * DocumentSplitter splitter = DocumentSplitters.recursive(
     *     RAG_MAX_SEGMENT_SIZE_IN_TOKENS,  // 300
     *     overlap,                          // kb.getIngestMaxOverlap()
     *     TokenEstimatorFactory.create(tokenEstimator)
     * );
     * </pre>
     *
     * <p>scm-ai实现：</p>
     * 使用OverlappingTokenTextSplitter（方案B），正确实现overlap功能
     *
     * @param content 文档内容
     * @param kb 知识库配置
     * @return 分割后的文本段列表
     */
    private List<String> splitDocument(String content, AiKnowledgeBaseEntity kb) {
        // 获取overlap参数
        int overlap = kb.getIngestMaxOverlap() != null ? kb.getIngestMaxOverlap() : 50;

        // 使用OverlappingTokenTextSplitter（对应aideepin的DocumentSplitters.recursive）
        OverlappingTokenTextSplitter splitter = new OverlappingTokenTextSplitter(
            MAX_SEGMENT_SIZE_IN_TOKENS,      // maxSegmentSizeInTokens（对应aideepin的RAG_MAX_SEGMENT_SIZE_IN_TOKENS = 300）
            overlap                          // maxOverlapSizeInTokens（对应aideepin的overlap参数）
        );

        // 创建Document对象
        Document document = new Document(content);

        // 执行分割并返回文本内容列表
        return splitter.apply(Collections.singletonList(document))
                .stream()
                .map(Document::getText)
                .collect(Collectors.toList());
    }

    /**
     * 生成embedding向量
     * 对应aideepin的embeddingModel.embed()
     *
     * <p>aideepin使用LangChain4j的EmbeddingModel</p>
     * <p>scm-ai使用Spring AI的EmbeddingModel</p>
     *
     * @param text 文本内容
     * @return embedding向量（384维float数组，使用all-minilm:l6-v2模型）
     */
    private float[] generateEmbedding(String text) {
        // 调用EmbeddingModel生成向量（对应aideepin的embeddingModel.embed）
        EmbeddingResponse response = aiModelProvider.getEmbeddingModel().embedForResponse(Collections.singletonList(text));

        // 直接返回float[]，无需转换（Elasticsearch的dense_vector字段就是float[]类型）
        return response.getResults().get(0).getOutput();
    }

    /**
     * 构建元数据
     * 对应aideepin的Metadata设置
     *
     * <p>aideepin代码：</p>
     * <pre>
     * Metadata metadata = new Metadata();
     * metadata.put(AdiConstant.MetadataKey.KB_UUID, item.getKbUuid());
     * metadata.put(AdiConstant.MetadataKey.KB_ITEM_UUID, item.getUuid());
     * </pre>
     *
     * @param kb 知识库配置
     * @param item 文档项
     * @param segmentIndex 文本段索引
     * @param totalSegments 文本段总数
     * @return 元数据Map
     */
    private Map<String, Object> buildMetadata(AiKnowledgeBaseEntity kb, AiKnowledgeBaseItemEntity item,
                                               int segmentIndex, int totalSegments) {
        Map<String, Object> metadata = new HashMap<>();

        // 对应aideepin的Metadata字段
        metadata.put("kb_uuid", item.getKbUuid());
        metadata.put("kb_item_uuid", item.getItemUuid());

        // scm-ai扩展字段
        metadata.put("tenant_id", item.getTenantId());
        metadata.put("segment_index", segmentIndex);
        metadata.put("total_segments", totalSegments);
        metadata.put("file_name", item.getSourceFileName());

        return metadata;
    }

    /**
     * 存储embedding到Elasticsearch
     * 对应aideepin的embeddingStore.add()
     *
     * <p>aideepin存储到pgvector:</p>
     * <pre>
     * embeddingStore.add(embedding, textSegment);
     * </pre>
     *
     * <p>scm-ai存储到Elasticsearch，使用AiKnowledgeBaseEmbeddingDoc实体类</p>
     *
     * @param content 文本内容
     * @param embedding embedding向量（float[]格式）
     * @param metadata 元数据
     */
    private void storeEmbedding(String content, float[] embedding, Map<String, Object> metadata) {
        // 构建Elasticsearch文档实体（对应aideepin的TextSegment + Embedding）
        AiKnowledgeBaseEmbeddingDoc doc = new AiKnowledgeBaseEmbeddingDoc();

        // 设置文档ID（对应aideepin的embeddingId）
        doc.setId(UuidUtil.createShort());

        // 设置知识库信息
        doc.setKbUuid((String) metadata.get("kb_uuid"));
        doc.setKbItemUuid((String) metadata.get("kb_item_uuid"));

        // 设置文本段信息
        doc.setSegmentUuid(UuidUtil.createShort());  // 生成segment_uuid作为业务主键
        doc.setSegmentIndex((Integer) metadata.get("segment_index"));
        doc.setSegmentText(content);

        // 设置向量数据（对应aideepin的Embedding）
        doc.setEmbedding(embedding);

        // 设置租户信息
        doc.setTenantId((Long) metadata.get("tenant_id"));

        // 设置创建时间（时间戳）
        doc.setCreateTime(System.currentTimeMillis());

        // 保存到Elasticsearch（对应aideepin的embeddingStore.add）
        IndexCoordinates index = IndexCoordinates.of(INDEX_NAME);
        elasticsearchTemplate.save(doc, index);
    }

    /**
     * 确保Elasticsearch索引存在
     * 对应aideepin的pgvector表创建
     */
    private void ensureIndexExists() {
        IndexCoordinates index = IndexCoordinates.of(INDEX_NAME);
        IndexOperations indexOps = elasticsearchTemplate.indexOps(index);

        if (!indexOps.exists()) {
            // 创建索引（映射定义在ElasticsearchConfig中）
            indexOps.create();
            log.info("Elasticsearch索引创建成功: {}", INDEX_NAME);
        }
    }

    /**
     * 删除文档的所有embedding
     * 对应aideepin的删除逻辑
     *
     * @param itemUuid 文档UUID
     * @return 删除的文本段数量
     */
    public int deleteDocumentEmbeddings(String itemUuid) {
        try {
            log.info("开始删除文档向量，item_uuid: {}", itemUuid);

            // 构建删除查询（对应aideepin的removeAll过滤器）
            // aideepin逻辑: embeddingStore.removeAll(metadata -> kb_item_uuid.equals(itemUuid))

            // 1. 先构建 NativeQuery（查询条件）
            Query nativeQuery = NativeQuery.builder()
                    .withQuery(q -> q
                            .term(t -> t
                                    .field("kb_item_uuid")
                                    .value(itemUuid)
                            )
                    )
                    .build();

            // 2. 使用 DeleteQuery.Builder 包装 NativeQuery
            DeleteQuery deleteQuery = DeleteQuery.builder(nativeQuery).build();

            // 执行删除操作（对应aideepin的embeddingStore.removeAll）
            IndexCoordinates index = IndexCoordinates.of(INDEX_NAME);
            long deletedCount = elasticsearchTemplate.delete(deleteQuery, AiKnowledgeBaseEmbeddingDoc.class, index).getDeleted();

            log.info("文档向量删除完成，item_uuid: {}, 删除数量: {}", itemUuid, deletedCount);
            return (int) deletedCount;

        } catch (Exception e) {
            log.error("文档向量删除失败，item_uuid: {}, 错误: {}", itemUuid, e.getMessage(), e);
            throw new RuntimeException("文档向量删除失败: " + e.getMessage(), e);
        }
    }
}
