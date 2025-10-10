package com.xinyirun.scm.ai.core.service.elasticsearch;

import co.elastic.clients.elasticsearch._types.SortOrder;
import com.xinyirun.scm.ai.bean.entity.rag.elasticsearch.AiKnowledgeBaseEmbeddingDoc;
import com.xinyirun.scm.ai.bean.vo.rag.KbEmbeddingVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Elasticsearch查询服务
 *
 * <p>功能说明：查询知识库文档的向量嵌入数据</p>
 *
 * <p>重要：使用 ElasticsearchOperations 接口而不是 ElasticsearchTemplate 具体类，
 * 这是Spring Data Elasticsearch官方推荐的做法，遵循面向接口编程原则。</p>
 *
 * @author SCM-AI重构团队
 * @since 2025-10-09
 */
@Service
@Slf4j
public class ElasticsearchQueryService {

    /**
     * Elasticsearch索引名称
     */
    private static final String INDEX_NAME = "kb_embeddings";

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    /**
     * 查询文档的向量嵌入列表
     *
     * @param itemUuid 知识项UUID
     * @param currentPage 当前页码
     * @param pageSize 每页大小
     * @return 向量嵌入VO列表
     */
    public List<KbEmbeddingVo> listEmbeddings(String itemUuid, Integer currentPage, Integer pageSize) {
        try {
            log.info("查询文档向量嵌入，item_uuid: {}, 页码: {}, 页大小: {}", itemUuid, currentPage, pageSize);

            // 构建查询条件（使用keyword字段进行精确匹配）
            NativeQuery query = NativeQuery.builder()
                    .withQuery(q -> q
                            .term(t -> t
                                    .field("kb_item_uuid.keyword")
                                    .value(itemUuid)
                            )
                    )
                    .withPageable(PageRequest.of(currentPage - 1, pageSize))
                    .withSort(s -> s
                            .field(f -> f
                                    .field("segment_index")
                                    .order(SortOrder.Asc)
                            )
                    )
                    .build();

            // 执行查询
            IndexCoordinates index = IndexCoordinates.of(INDEX_NAME);
            SearchHits<AiKnowledgeBaseEmbeddingDoc> searchHits =
                    elasticsearchOperations.search(query, AiKnowledgeBaseEmbeddingDoc.class, index);

            // 转换为VO
            List<KbEmbeddingVo> result = searchHits.getSearchHits()
                    .stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());

            log.info("查询文档向量嵌入完成，item_uuid: {}, 返回数量: {}", itemUuid, result.size());
            return result;

        } catch (Exception e) {
            log.error("查询文档向量嵌入失败，item_uuid: {}, 错误: {}", itemUuid, e.getMessage(), e);
            throw new RuntimeException("查询文档向量嵌入失败: " + e.getMessage(), e);
        }
    }

    /**
     * 转换Elasticsearch文档为VO
     *
     * @param searchHit 搜索结果
     * @return KbEmbeddingVo
     */
    private KbEmbeddingVo convertToVo(SearchHit<AiKnowledgeBaseEmbeddingDoc> searchHit) {
        AiKnowledgeBaseEmbeddingDoc doc = searchHit.getContent();

        KbEmbeddingVo vo = new KbEmbeddingVo();
        vo.setId(doc.getId());
        vo.setKbUuid(doc.getKbUuid());
        vo.setKbItemUuid(doc.getKbItemUuid());
        vo.setContent(doc.getSegmentText());
        vo.setChunkIndex(doc.getSegmentIndex());
        vo.setTokenCount(doc.getTokenCount() != null ? doc.getTokenCount() : 0);
        vo.setTimestamp(doc.getCreateTime()); // createTime已经是时间戳，直接使用

        return vo;
    }
}
