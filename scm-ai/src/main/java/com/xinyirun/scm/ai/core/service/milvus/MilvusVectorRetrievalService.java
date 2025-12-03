package com.xinyirun.scm.ai.core.service.milvus;

import com.xinyirun.scm.ai.bean.vo.rag.KbEmbeddingVo;
import com.xinyirun.scm.ai.bean.vo.rag.QaRefEmbeddingVo;
import com.xinyirun.scm.ai.bean.vo.rag.VectorSearchResultVo;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaRefEmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Milvus向量检索服务
 *
 * 功能说明:
 * - 从Milvus检索与用户问题最相似的文本段,用于RAG上下文构建
 * - 使用Spring AI VectorStore抽象
 *
 * 核心流程:
 * 1. 构建SearchRequest - 包含query、topK、similarityThreshold、filterExpression
 * 2. VectorStore.similaritySearch() - 自动向量化问题并搜索
 * 3. 转换为VectorSearchResultVo - 保持API兼容
 *
 * @author SCM AI Team
 * @since 2025-12-02
 */
@Slf4j
@Service
public class MilvusVectorRetrievalService {

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private AiKnowledgeBaseQaRefEmbeddingService qaRefEmbeddingService;

    /**
     * embeddingId到score的映射缓存
     * 用于后续保存ai_knowledge_base_qa_ref_embedding记录时使用
     */
    private final Map<String, Double> embeddingToScore = new ConcurrentHashMap<>();

    /**
     * 搜索与问题相似的文档片段
     *
     * @param question 用户问题文本
     * @param kbUuid 知识库UUID(用于过滤)
     * @param maxResults 最大返回结果数
     * @param minScore 最小相似度分数
     * @return 相似文档片段列表
     */
    public List<VectorSearchResultVo> searchSimilarDocuments(String question, String kbUuid,
                                                              int maxResults, double minScore) {
        try {
            log.info("开始向量检索, question: {}, kbUuid: {}, maxResults: {}, minScore: {}",
                    question, kbUuid, maxResults, minScore);

            // 1. 构建SearchRequest(Spring AI标准API)
            SearchRequest request = SearchRequest.builder()
                    .query(question)
                    .topK(maxResults)
                    .similarityThreshold(minScore)
                    .filterExpression(String.format("kb_uuid == '%s'", kbUuid))
                    .build();

            // 2. 执行搜索(VectorStore自动向量化问题)
            List<Document> documents = vectorStore.similaritySearch(request);
            log.info("Milvus搜索完成, 命中数: {}", documents.size());

            // 3. 转换为VectorSearchResultVo
            List<VectorSearchResultVo> results = new ArrayList<>();
            for (Document doc : documents) {
                String embeddingId = doc.getId();

                // 从metadata获取相似度分数
                Double score = doc.getMetadata().containsKey("score")
                        ? ((Number) doc.getMetadata().get("score")).doubleValue()
                        : 1.0;

                // 缓存embeddingId到score的映射
                embeddingToScore.put(embeddingId, score);
                log.debug("embeddingToScore缓存, embeddingId: {}, score: {}", embeddingId, score);

                // 构建返回结果
                VectorSearchResultVo result = VectorSearchResultVo.builder()
                        .embeddingId(embeddingId)
                        .score(score)
                        .content(doc.getText())
                        .kbUuid((String) doc.getMetadata().get("kb_uuid"))
                        .kbItemUuid((String) doc.getMetadata().get("kb_item_uuid"))
                        .segmentIndex(doc.getMetadata().containsKey("segment_index")
                                ? ((Number) doc.getMetadata().get("segment_index")).intValue()
                                : 0)
                        .metadata(doc.getMetadata())
                        .build();

                results.add(result);
            }

            log.info("向量检索完成, 返回结果数: {}", results.size());
            return results;

        } catch (Exception e) {
            log.error("向量检索失败, question: {}, kbUuid: {}, 错误: {}",
                    question, kbUuid, e.getMessage(), e);
            throw new RuntimeException("向量检索失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取所有embeddingId到score的缓存数据
     *
     * @return embeddingId到score的映射(不可修改的副本)
     */
    public Map<String, Double> getAllCachedScores() {
        return Collections.unmodifiableMap(new HashMap<>(embeddingToScore));
    }

    /**
     * 清除embeddingId到score的缓存
     */
    public void clearScoreCache() {
        embeddingToScore.clear();
        log.debug("embeddingToScore缓存已清除");
    }

    /**
     * 获取QA记录的向量引用列表(用于前端展示)
     *
     * @param qaRecordId 问答记录ID
     * @return 向量引用列表
     */
    public List<QaRefEmbeddingVo> listRefEmbeddings(String qaRecordId) {
        try {
            log.info("查询QA引用向量列表, qaRecordId: {}", qaRecordId);

            // 1. 从MySQL查询引用记录
            List<com.xinyirun.scm.ai.bean.vo.rag.RefEmbeddingVo> refList =
                    qaRefEmbeddingService.listRefEmbeddings(qaRecordId);

            if (refList == null || refList.isEmpty()) {
                log.info("QA记录没有引用向量, qaRecordId: {}", qaRecordId);
                return Collections.emptyList();
            }

            // 2. 提取所有embeddingId
            List<String> embeddingIds = refList.stream()
                    .map(com.xinyirun.scm.ai.bean.vo.rag.RefEmbeddingVo::getEmbeddingId)
                    .collect(Collectors.toList());

            // 3. 从Milvus批量获取文本内容
            Map<String, String> embeddingIdToContent = batchGetEmbeddingContent(embeddingIds);

            // 4. 组装QaRefEmbeddingVo
            List<QaRefEmbeddingVo> results = refList.stream()
                    .map(ref -> QaRefEmbeddingVo.builder()
                            .qaRecordId(qaRecordId)
                            .embeddingId(ref.getEmbeddingId())
                            .score(ref.getScore())
                            .content(embeddingIdToContent.get(ref.getEmbeddingId()))
                            .build())
                    .collect(Collectors.toList());

            log.info("QA引用向量查询完成, qaRecordId: {}, 引用数量: {}", qaRecordId, results.size());
            return results;

        } catch (Exception e) {
            log.error("查询QA引用向量列表失败, qaRecordId: {}, 错误: {}",
                    qaRecordId, e.getMessage(), e);
            throw new RuntimeException("查询QA引用向量列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量从Milvus获取embedding文档的文本内容
     *
     * <p>重要说明：使用MilvusSearchRequest.nativeExpression绕过Spring AI的Filter转换器</p>
     * <p>原因：doc_id是Milvus顶级字段，不在metadata JSON中，
     *    Spring AI的FilterExpressionConverter会把所有字段转换成metadata["xxx"]格式导致查询失败</p>
     *
     * @param embeddingIds embedding文档ID列表
     * @return embeddingId到文本内容的映射
     */
    private Map<String, String> batchGetEmbeddingContent(List<String> embeddingIds) {
        if (embeddingIds == null || embeddingIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            // 构建Milvus原生过滤表达式: doc_id in ['id1', 'id2', ...]
            // doc_id是Milvus顶级字段，必须使用nativeExpression绕过Spring AI的Filter转换器
            String nativeFilter = embeddingIds.stream()
                    .map(id -> "\"" + id + "\"")
                    .collect(Collectors.joining(", ", "doc_id in [", "]"));

            log.info("批量查询Milvus文档内容, embeddingIds数量: {}, nativeFilter: {}", embeddingIds.size(), nativeFilter);

            // 使用MilvusSearchRequest.milvusBuilder()构建请求
            // nativeExpression直接传递给Milvus，不经过FilterExpressionConverter转换
            MilvusSearchRequest request = MilvusSearchRequest.milvusBuilder()
                    .query("document_content_retrieval")
                    .topK(embeddingIds.size())
                    .nativeExpression(nativeFilter)
                    .build();

            List<Document> documents = vectorStore.similaritySearch(request);

            Map<String, String> resultMap = documents.stream()
                    .collect(Collectors.toMap(
                            Document::getId,
                            Document::getText,
                            (existing, replacement) -> existing
                    ));

            log.info("批量查询完成, 成功获取: {}/{}", resultMap.size(), embeddingIds.size());
            return resultMap;

        } catch (Exception e) {
            log.error("批量查询Milvus文档失败, embeddingIds数量: {}, 错误: {}",
                    embeddingIds.size(), e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    /**
     * 查询文档项的向量嵌入列表(用于前端展示)
     *
     * @param itemUuid 知识项UUID
     * @param currentPage 当前页码(从1开始)
     * @param pageSize 每页大小
     * @return 向量嵌入VO列表
     */
    public List<KbEmbeddingVo> listEmbeddings(String itemUuid, Integer currentPage, Integer pageSize) {
        try {
            log.info("查询文档向量列表, itemUuid: {}, page: {}, size: {}", itemUuid, currentPage, pageSize);

            // Milvus不支持原生分页,采用topK限制返回数量
            int topK = currentPage * pageSize;

            // 构建filter表达式: kb_item_uuid == 'xxx'
            SearchRequest request = SearchRequest.builder()
                    .query("embedding_list_query")  // 占位查询
                    .topK(topK)
                    .filterExpression(String.format("kb_item_uuid == '%s'", itemUuid))
                    .build();

            List<Document> documents = vectorStore.similaritySearch(request);

            // 模拟分页: 跳过前面的记录
            int skip = (currentPage - 1) * pageSize;
            List<Document> pageDocuments = documents.stream()
                    .skip(skip)
                    .limit(pageSize)
                    .collect(Collectors.toList());

            // 转换为KbEmbeddingVo
            List<KbEmbeddingVo> results = new ArrayList<>();
            for (Document doc : pageDocuments) {
                KbEmbeddingVo vo = new KbEmbeddingVo();
                vo.setId(doc.getId());
                vo.setKbUuid((String) doc.getMetadata().get("kb_uuid"));
                vo.setKbItemUuid((String) doc.getMetadata().get("kb_item_uuid"));
                vo.setContent(doc.getText());
                vo.setChunkIndex(doc.getMetadata().containsKey("segment_index")
                        ? ((Number) doc.getMetadata().get("segment_index")).intValue()
                        : 0);
                vo.setTokenCount(doc.getMetadata().containsKey("token_count")
                        ? ((Number) doc.getMetadata().get("token_count")).intValue()
                        : null);
                vo.setTimestamp(doc.getMetadata().containsKey("timestamp")
                        ? ((Number) doc.getMetadata().get("timestamp")).longValue()
                        : null);
                results.add(vo);
            }

            log.info("文档向量列表查询完成, itemUuid: {}, 返回数量: {}", itemUuid, results.size());
            return results;

        } catch (Exception e) {
            log.error("查询文档向量列表失败, itemUuid: {}, 错误: {}", itemUuid, e.getMessage(), e);
            return Collections.emptyList();
        }
    }
}
