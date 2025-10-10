package com.xinyirun.scm.ai.core.service.elasticsearch;

import com.xinyirun.scm.ai.bean.entity.rag.elasticsearch.AiKnowledgeBaseEmbeddingDoc;
import com.xinyirun.scm.ai.bean.vo.rag.QaRefEmbeddingVo;
import com.xinyirun.scm.ai.bean.vo.rag.VectorSearchResultVo;
import com.xinyirun.scm.ai.config.AiModelProvider;
import com.xinyirun.scm.ai.core.service.rag.AiKnowledgeBaseQaRefEmbeddingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.google.common.primitives.Floats;

/**
 * 向量检索服务
 *
 * <p>功能说明：</p>
 * 严格对应aideepin的AdiEmbeddingStoreContentRetriever.retrieve()逻辑
 * 从Elasticsearch检索与用户问题最相似的文本段，用于RAG上下文构建
 *
 * <p>核心流程：</p>
 * <ol>
 *   <li>问题向量化 - 使用EmbeddingModel将问题转为向量（对应aideepin的embeddingModel.embed）</li>
 *   <li>kNN搜索 - 在Elasticsearch中搜索相似向量（对应aideepin的embeddingStore.search）</li>
 *   <li>缓存分数 - 缓存embeddingId到score的映射（对应aideepin的embeddingToScore）</li>
 *   <li>返回结果 - 返回文本段列表用于RAG上下文</li>
 * </ol>
 *
 * <p>参考代码：</p>
 * aideepin: AdiEmbeddingStoreContentRetriever.java
 * 路径: D:\2025_project\20_project_in_github\99_tools\aideepin\langchain4j-aideepin\adi-common\src\main\java\com\moyz\adi\common\rag\AdiEmbeddingStoreContentRetriever.java
 *
 * <p>aideepin核心代码：</p>
 * <pre>
 * public List<Content> retrieve(Query query) {
 *     // 1. 将问题转为向量
 *     Embedding embeddedQuery = embeddingModel.embed(query.text()).content();
 *
 *     // 2. 构建搜索请求
 *     EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
 *         .queryEmbedding(embeddedQuery)
 *         .maxResults(maxResultsProvider.apply(query))
 *         .minScore(minScoreProvider.apply(query))
 *         .filter(filterProvider.apply(query))
 *         .build();
 *
 *     // 3. 执行搜索
 *     EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);
 *
 *     // 4. 缓存分数并返回结果
 *     List<Content> result = searchResult.matches().stream()
 *         .peek(item -> {
 *             embeddingToScore.put(item.embeddingId(), item.score());
 *             log.info("embeddingToScore,embeddingId:{},score:{}", item.embeddingId(), item.score());
 *         })
 *         .map(EmbeddingMatch::embedded)
 *         .map(Content::from)
 *         .collect(toList());
 *
 *     return result;
 * }
 * </pre>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Slf4j
@Service
public class VectorRetrievalService {

    /**
     * Elasticsearch索引名称
     * 对应aideepin的pgvector表：adi_knowledge_base_embedding
     */
    private static final String INDEX_NAME = "kb_embeddings";

    @Autowired
    private AiModelProvider aiModelProvider;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    private AiKnowledgeBaseQaRefEmbeddingService qaRefEmbeddingService;

    /**
     * embeddingId到score的映射缓存
     * 对应aideepin的embeddingToScore
     * 用于后续保存ai_knowledge_base_qa_ref_embedding记录时使用
     */
    private final Map<String, Double> embeddingToScore = new ConcurrentHashMap<>();

    /**
     * 搜索与问题相似的文档片段
     * 对应aideepin的AdiEmbeddingStoreContentRetriever.retrieve()
     *
     * <p>调用示例：</p>
     * <pre>
     * List<VectorSearchResultVo> results = vectorRetrievalService.searchSimilarDocuments(
     *     "什么是供应链管理？",  // 用户问题
     *     "kb-uuid-123",         // 知识库UUID
     *     10,                     // 最多返回10个结果
     *     0.3                     // 最低相似度分数0.3
     * );
     * </pre>
     *
     * @param question 用户问题文本
     * @param kbUuid 知识库UUID（用于过滤）
     * @param maxResults 最大返回结果数（对应aideepin的maxResults）
     * @param minScore 最小相似度分数（对应aideepin的minScore，范围0-1）
     * @return 相似文档片段列表
     */
    public List<VectorSearchResultVo> searchSimilarDocuments(String question, String kbUuid,
                                                              int maxResults, double minScore) {
        try {
            log.info("开始向量检索，question: {}, kbUuid: {}, maxResults: {}, minScore: {}",
                    question, kbUuid, maxResults, minScore);

            // 1. 将问题转为embedding向量（对应aideepin的embeddingModel.embed）
            float[] questionEmbedding = generateQuestionEmbedding(question);
            log.info("问题向量化完成，向量维度: {}", questionEmbedding.length);

            // 2. 构建并执行Elasticsearch kNN搜索（对应aideepin的embeddingStore.search）
            SearchHits<AiKnowledgeBaseEmbeddingDoc> searchHits = executeKnnSearch(questionEmbedding, kbUuid, maxResults, minScore);
            log.info("Elasticsearch kNN搜索完成，命中数: {}", searchHits.getTotalHits());

            // 3. 处理搜索结果，缓存分数（对应aideepin的embeddingToScore缓存）
            List<VectorSearchResultVo> results = processSearchResults(searchHits);

            log.info("向量检索完成，返回结果数: {}", results.size());
            return results;

        } catch (Exception e) {
            log.error("向量检索失败，question: {}, kbUuid: {}, 错误: {}", question, kbUuid, e.getMessage(), e);
            throw new RuntimeException("向量检索失败: " + e.getMessage(), e);
        }
    }

    /**
     * 生成问题的embedding向量
     * 对应aideepin的embeddingModel.embed(query.text())
     *
     * @param question 问题文本
     * @return embedding向量（384维float数组）
     */
    private float[] generateQuestionEmbedding(String question) {
        // 调用EmbeddingModel生成向量（对应aideepin的embeddingModel.embed）
        EmbeddingResponse response = aiModelProvider.getEmbeddingModel().embedForResponse(Collections.singletonList(question));

        // 直接返回float[]（Elasticsearch的dense_vector字段和kNN查询都使用float[]类型）
        return response.getResults().get(0).getOutput();
    }

    /**
     * 执行Elasticsearch kNN向量搜索
     * 对应aideepin的embeddingStore.search(searchRequest)
     *
     * <p>aideepin的EmbeddingSearchRequest包含：</p>
     * <ul>
     *   <li>queryEmbedding: 问题向量</li>
     *   <li>maxResults: 最大返回数（k）</li>
     *   <li>minScore: 最小相似度分数</li>
     *   <li>filter: 过滤条件（kb_uuid等）</li>
     * </ul>
     *
     * <p>scm-ai使用Spring Data Elasticsearch的NativeQuery.withKnnSearches()实现</p>
     *
     * @param queryVector 问题向量（float[]格式）
     * @param kbUuid 知识库UUID（过滤条件）
     * @param maxResults 最大返回数（k参数，对应aideepin的maxResults）
     * @param minScore 最小分数（对应aideepin的minScore）
     * @return Elasticsearch搜索结果
     */
    private SearchHits<AiKnowledgeBaseEmbeddingDoc> executeKnnSearch(float[] queryVector, String kbUuid,
                                                                      int maxResults, double minScore) {
        // 构建kNN查询（对应aideepin的EmbeddingSearchRequest）
        NativeQuery query = NativeQuery.builder()
                // 配置kNN搜索（对应aideepin的embeddingStore.search）
                .withKnnSearches(knn -> knn
                        // 向量字段名
                        .field("embedding")
                        // 问题向量（对应aideepin的queryEmbedding），需要转换为List<Float>
                        .queryVector(Floats.asList(queryVector))
                        // 返回结果数（对应aideepin的maxResults）
                        .k(maxResults)
                        // 候选数量（通常设置为k的10倍以提高召回率）
                        .numCandidates(maxResults * 10)
                        // 过滤条件：知识库UUID（对应aideepin的filter）
                        .filter(f -> f
                                .term(t -> t
                                        .field("kb_uuid")
                                        .value(kbUuid)
                                )
                        )
                        // 相似度算法：余弦相似度（与kb-embeddings-settings.json中的similarity="cosine"对应）
                        .similarity((float) minScore)
                )
                .build();

        // 执行搜索（对应aideepin的embeddingStore.search返回EmbeddingSearchResult）
        IndexCoordinates index = IndexCoordinates.of(INDEX_NAME);
        return elasticsearchOperations.search(query, AiKnowledgeBaseEmbeddingDoc.class, index);
    }

    /**
     * 处理Elasticsearch搜索结果
     * 对应aideepin的结果流处理和embeddingToScore缓存
     *
     * <p>aideepin代码：</p>
     * <pre>
     * List<Content> result = searchResult.matches().stream()
     *     .peek(item -> {
     *         embeddingToScore.put(item.embeddingId(), item.score());
     *         log.info("embeddingToScore,embeddingId:{},score:{}", item.embeddingId(), item.score());
     *     })
     *     .map(EmbeddingMatch::embedded)
     *     .map(Content::from)
     *     .collect(toList());
     * </pre>
     *
     * @param searchHits Elasticsearch搜索结果
     * @return 向量检索结果列表
     */
    private List<VectorSearchResultVo> processSearchResults(SearchHits<AiKnowledgeBaseEmbeddingDoc> searchHits) {
        List<VectorSearchResultVo> results = new ArrayList<>();

        for (SearchHit<AiKnowledgeBaseEmbeddingDoc> hit : searchHits.getSearchHits()) {
            AiKnowledgeBaseEmbeddingDoc doc = hit.getContent();

            // 提取Elasticsearch文档ID（对应aideepin的embeddingId）
            String embeddingId = doc.getId();

            // 提取相似度分数（对应aideepin的score）
            // hit.getScore() 返回 float（原始类型），不需要 null 检查
            Double score = (double) hit.getScore();

            // 缓存embeddingId到score的映射（对应aideepin的embeddingToScore.put）
            embeddingToScore.put(embeddingId, score);
            log.info("embeddingToScore缓存，embeddingId: {}, score: {}", embeddingId, score);

            // 构建返回结果（对应aideepin的Content）
            VectorSearchResultVo result = VectorSearchResultVo.builder()
                    .embeddingId(embeddingId)
                    .score(score)
                    .content(doc.getSegmentText())  // 文本段内容
                    .kbUuid(doc.getKbUuid())
                    .kbItemUuid(doc.getKbItemUuid())
                    .segmentIndex(doc.getSegmentIndex())
                    .metadata(null)  // 元数据可以从实体类扩展获取
                    .build();

            results.add(result);
        }

        return results;
    }

    /**
     * 获取指定embeddingId的缓存分数
     * 对应aideepin的embeddingToScore.get(embeddingId)
     *
     * <p>用途：</p>
     * 在保存ai_knowledge_base_qa_ref_embedding记录时，
     * 从缓存中获取每个embeddingId对应的相似度分数
     *
     * @param embeddingId Elasticsearch文档ID
     * @return 相似度分数，如果不存在则返回null
     */
    public Double getCachedScore(String embeddingId) {
        return embeddingToScore.get(embeddingId);
    }

    /**
     * 获取所有embeddingId到score的缓存数据
     * 对应aideepin在RAG查询后保存引用记录时使用的embeddingToScore
     *
     * <p>调用场景：</p>
     * <pre>
     * // RAG查询完成后
     * List<VectorSearchResultVo> results = vectorRetrievalService.searchSimilarDocuments(...);
     *
     * // 获取缓存的embeddingId-score映射
     * Map<String, Double> embeddingScores = vectorRetrievalService.getAllCachedScores();
     *
     * // 保存QA引用记录到MySQL
     * qaRefEmbeddingService.saveRefEmbeddings(qaRecordId, embeddingScores, userId);
     *
     * // 清除缓存
     * vectorRetrievalService.clearScoreCache();
     * </pre>
     *
     * @return embeddingId到score的映射（不可修改的副本，防止外部修改缓存）
     */
    public Map<String, Double> getAllCachedScores() {
        return Collections.unmodifiableMap(new HashMap<>(embeddingToScore));
    }

    /**
     * 清除embeddingId到score的缓存
     * 建议在每次RAG查询完成后调用，避免内存泄漏
     */
    public void clearScoreCache() {
        embeddingToScore.clear();
        log.debug("embeddingToScore缓存已清除");
    }

    /**
     * 获取QA记录的向量引用列表（用于前端展示）
     * 对应aideepin的KnowledgeBaseQaService.listRefEmbeddings()
     *
     * <p>实现逻辑：</p>
     * <ol>
     *   <li>从MySQL查询ai_knowledge_base_qa_ref_embedding表，获取引用的embedding_id和score</li>
     *   <li>根据embedding_id从Elasticsearch批量查询文档，获取文本内容</li>
     *   <li>组装QaRefEmbeddingVo返回给前端展示</li>
     * </ol>
     *
     * @param qaRecordId 问答记录ID（ai_knowledge_base_qa.id）
     * @return 向量引用列表（包含embeddingId、score、文本内容）
     */
    public List<QaRefEmbeddingVo> listRefEmbeddings(String qaRecordId) {
        try {
            log.info("查询QA引用向量列表，qaRecordId: {}", qaRecordId);

            // 1. 从MySQL查询引用记录（对应aideepin的查询adi_knowledge_base_qa_record_reference）
            List<com.xinyirun.scm.ai.bean.vo.rag.RefEmbeddingVo> refList =
                    qaRefEmbeddingService.listRefEmbeddings(qaRecordId);

            if (refList == null || refList.isEmpty()) {
                log.info("QA记录没有引用向量，qaRecordId: {}", qaRecordId);
                return Collections.emptyList();
            }

            // 2. 提取所有embeddingId
            List<String> embeddingIds = refList.stream()
                    .map(com.xinyirun.scm.ai.bean.vo.rag.RefEmbeddingVo::getEmbeddingId)
                    .collect(Collectors.toList());

            // 3. 从Elasticsearch批量查询文档（获取文本内容）
            Map<String, String> embeddingIdToContent = batchGetEmbeddingContent(embeddingIds);

            // 4. 组装QaRefEmbeddingVo（包含完整的文本内容）
            List<QaRefEmbeddingVo> results = refList.stream()
                    .map(ref -> QaRefEmbeddingVo.builder()
                            .qaRecordId(qaRecordId)
                            .embeddingId(ref.getEmbeddingId())
                            .score(ref.getScore())
                            .content(embeddingIdToContent.get(ref.getEmbeddingId()))  // 从Elasticsearch获取的文本内容
                            .build())
                    .collect(Collectors.toList());

            log.info("QA引用向量查询完成，qaRecordId: {}, 引用数量: {}", qaRecordId, results.size());
            return results;

        } catch (Exception e) {
            log.error("查询QA引用向量列表失败，qaRecordId: {}, 错误: {}", qaRecordId, e.getMessage(), e);
            throw new RuntimeException("查询QA引用向量列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 批量从Elasticsearch获取embedding文档的文本内容
     * 使用ids查询提高性能
     *
     * @param embeddingIds embedding文档ID列表
     * @return embeddingId到文本内容的映射
     */
    private Map<String, String> batchGetEmbeddingContent(List<String> embeddingIds) {
        if (embeddingIds == null || embeddingIds.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            // 构建ids查询
            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q
                            .ids(ids -> ids
                                    .values(embeddingIds)
                            )
                    )
                    .build();

            // 执行批量查询
            IndexCoordinates index = IndexCoordinates.of(INDEX_NAME);
            SearchHits<AiKnowledgeBaseEmbeddingDoc> searchHits =
                    elasticsearchOperations.search(query, AiKnowledgeBaseEmbeddingDoc.class, index);

            // 构建ID到内容的映射
            return searchHits.getSearchHits().stream()
                    .collect(Collectors.toMap(
                            hit -> hit.getContent().getId(),
                            hit -> hit.getContent().getSegmentText(),
                            (existing, replacement) -> existing  // 如果有重复ID，保留第一个
                    ));

        } catch (Exception e) {
            log.error("批量查询Elasticsearch文档失败，embeddingIds数量: {}, 错误: {}",
                    embeddingIds.size(), e.getMessage(), e);
            return Collections.emptyMap();
        }
    }
}
