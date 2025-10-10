package com.xinyirun.scm.ai.core.repository.elasticsearch;

import com.xinyirun.scm.ai.bean.entity.rag.elasticsearch.AiKnowledgeBaseEmbeddingDoc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Elasticsearch 知识库嵌入向量 Repository
 *
 * <p>用于基本的 CRUD 操作和简单查询</p>
 * <p>复杂的向量检索操作请使用 {@link EmbeddingSearchRepository}</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Repository
public interface AiKnowledgeBaseEmbeddingRepository extends ElasticsearchRepository<AiKnowledgeBaseEmbeddingDoc, String> {

    /**
     * 根据知识库UUID查询所有嵌入向量文档
     *
     * @param kb_uuid 知识库UUID
     * @param pageable 分页参数
     * @return 嵌入向量文档分页列表
     */
    Page<AiKnowledgeBaseEmbeddingDoc> findByKbUuid(String kbUuid, Pageable pageable);

    /**
     * 根据文档UUID查询所有嵌入向量文档
     *
     * @param kb_item_uuid 文档UUID
     * @return 嵌入向量文档列表
     */
    List<AiKnowledgeBaseEmbeddingDoc> findByKbItemUuid(String kbItemUuid);

    /**
     * 根据知识库UUID和文档UUID查询嵌入向量文档
     *
     * @param kb_uuid 知识库UUID
     * @param kb_item_uuid 文档UUID
     * @return 嵌入向量文档列表
     */
    List<AiKnowledgeBaseEmbeddingDoc> findByKbUuidAndKbItemUuid(String kbUuid, String kbItemUuid);

    /**
     * 根据文档UUID删除所有嵌入向量
     *
     * @param kb_item_uuid 文档UUID
     * @return 删除的文档数量
     */
    long deleteByKbItemUuid(String kbItemUuid);

    /**
     * 根据知识库UUID删除所有嵌入向量
     *
     * @param kb_uuid 知识库UUID
     * @return 删除的文档数量
     */
    long deleteByKbUuid(String kbUuid);

    /**
     * 统计知识库的向量文档数量
     *
     * @param kb_uuid 知识库UUID
     * @return 文档数量
     */
    long countByKbUuid(String kbUuid);

    /**
     * 统计文档的向量分段数量
     *
     * @param kb_item_uuid 文档UUID
     * @return 分段数量
     */
    long countByKbItemUuid(String kbItemUuid);
}
