package com.xinyirun.scm.ai.core.repository.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.ScriptScoreQuery;
import com.xinyirun.scm.ai.bean.entity.rag.elasticsearch.AiKnowledgeBaseEmbeddingDoc;
import com.xinyirun.scm.ai.bean.vo.rag.VectorSearchScriptParamsVo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Elasticsearch 向量检索 Repository
 *
 * <p>使用 NativeQuery 实现向量相似度检索</p>
 * <p>性能要求：top 10 结果检索 &lt;2 秒</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Repository
@RequiredArgsConstructor
public class EmbeddingSearchRepository {

    private final ElasticsearchOperations elasticsearchOperations;

    /**
     * 向量相似度检索（余弦相似度）
     *
     * <p>使用 script_score 查询计算余弦相似度</p>
     * <p>公式：cosineSimilarity(params.query_vector, 'embedding') + 1.0</p>
     *
     * @param kb_uuid 知识库UUID（过滤条件）
     * @param query_vector 查询向量（384维）
     * @param topK 返回前K个结果（默认10）
     * @return 相似度排序的检索结果列表
     */
    public List<AiKnowledgeBaseEmbeddingDoc> vectorSearch(String kb_uuid, float[] query_vector, int topK) {
        // 构建过滤条件：kb_uuid 匹配
        Query filterQuery = Query.of(q -> q
                .term(t -> t
                        .field("kb_uuid")
                        .value(kb_uuid)
                )
        );

        // 构建 script_score 查询
        VectorSearchScriptParamsVo scriptParams = VectorSearchScriptParamsVo.of(query_vector);

        ScriptScoreQuery scriptScoreQuery = ScriptScoreQuery.of(s -> s
                .query(filterQuery)
                .script(script -> script
                        .source("cosineSimilarity(params.query_vector, 'embedding') + 1.0")
                        .params(scriptParams.toScriptParams())
                )
                .minScore(0.5f)  // 最小相似度阈值 0.5
        );

        // 构建 NativeQuery
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.scriptScore(scriptScoreQuery)))
                .withPageable(PageRequest.of(0, topK))
                .build();

        // 执行检索
        SearchHits<AiKnowledgeBaseEmbeddingDoc> searchHits = elasticsearchOperations.search(
                searchQuery,
                AiKnowledgeBaseEmbeddingDoc.class
        );

        // 提取结果并设置相似度分数
        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    AiKnowledgeBaseEmbeddingDoc doc = hit.getContent();
                    // 将 Elasticsearch score 转换为 0-1 的相似度分数
                    // script_score 返回的是 cosineSimilarity + 1.0，范围是 [0, 2]
                    // 转换为 [0, 1] 范围
                    doc.setSimilarityScore((hit.getScore() - 1.0f));
                    return doc;
                })
                .collect(Collectors.toList());
    }

    /**
     * 混合检索（向量检索 + 关键词检索）
     *
     * <p>结合向量相似度和文本匹配，提高检索准确性</p>
     *
     * @param kb_uuid 知识库UUID
     * @param query_vector 查询向量
     * @param keyword 关键词（可选）
     * @param topK 返回前K个结果
     * @return 相似度排序的检索结果列表
     */
    public List<AiKnowledgeBaseEmbeddingDoc> hybridSearch(
            String kb_uuid,
            float[] query_vector,
            String keyword,
            int topK) {

        // 构建基础过滤条件
        Query filterQuery = Query.of(q -> q
                .term(t -> t
                        .field("kb_uuid")
                        .value(kb_uuid)
                )
        );

        // 如果提供了关键词，添加文本匹配条件
        final Query mainQuery;
        if (keyword != null && !keyword.trim().isEmpty()) {
            mainQuery = Query.of(q -> q
                    .bool(b -> b
                            .must(filterQuery)
                            .should(Query.of(sq -> sq
                                    .match(m -> m
                                            .field("segment_text")
                                            .query(keyword)
                                            .boost(0.3f)  // 文本匹配权重 30%
                                    )
                            ))
                    )
            );
        } else {
            mainQuery = filterQuery;
        }

        // 构建 script_score 查询
        VectorSearchScriptParamsVo scriptParams = VectorSearchScriptParamsVo.of(query_vector);

        ScriptScoreQuery scriptScoreQuery = ScriptScoreQuery.of(s -> s
                .query(mainQuery)
                .script(script -> script
                        .source("cosineSimilarity(params.query_vector, 'embedding') + 1.0")
                        .params(scriptParams.toScriptParams())
                )
                .minScore(0.5f)
        );

        // 构建 NativeQuery
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(Query.of(q -> q.scriptScore(scriptScoreQuery)))
                .withPageable(PageRequest.of(0, topK))
                .build();

        // 执行检索
        SearchHits<AiKnowledgeBaseEmbeddingDoc> searchHits = elasticsearchOperations.search(
                searchQuery,
                AiKnowledgeBaseEmbeddingDoc.class
        );

        // 提取结果
        return searchHits.getSearchHits().stream()
                .map(hit -> {
                    AiKnowledgeBaseEmbeddingDoc doc = hit.getContent();
                    doc.setSimilarityScore((hit.getScore() - 1.0f));
                    return doc;
                })
                .collect(Collectors.toList());
    }

    /**
     * 获取文本段的上下文（前后相邻文本段）
     *
     * @param kb_item_uuid 文档UUID
     * @param segment_index 文本段索引
     * @param contextSize 上下文大小（前后各取N个）
     * @return 包含上下文的文本段列表
     */
    public List<AiKnowledgeBaseEmbeddingDoc> getSegmentContext(
            String kb_item_uuid,
            int segment_index,
            int contextSize) {

        // 构建范围查询：segment_index 在 [index-contextSize, index+contextSize] 范围内
        Query rangeQuery = Query.of(q -> q
                .bool(b -> b
                        .must(Query.of(mq -> mq
                                .term(t -> t
                                        .field("kb_item_uuid")
                                        .value(kb_item_uuid)
                                )
                        ))
                        .must(Query.of(mq -> mq
                                .range(r -> r
                                        .number(n -> n
                                                .field("segment_index")
                                                .gte((double) (segment_index - contextSize))
                                                .lte((double) (segment_index + contextSize))
                                        )
                                )
                        ))
                )
        );

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(rangeQuery)
                .withPageable(PageRequest.of(0, contextSize * 2 + 1))
                .build();

        SearchHits<AiKnowledgeBaseEmbeddingDoc> searchHits = elasticsearchOperations.search(
                searchQuery,
                AiKnowledgeBaseEmbeddingDoc.class
        );

        return searchHits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
