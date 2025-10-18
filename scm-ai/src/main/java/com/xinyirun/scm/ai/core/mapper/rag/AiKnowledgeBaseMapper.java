package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * AI知识库 Mapper接口
 *
 * @author SCM AI Team
 * @since 2025-10-03
 */
@Mapper
public interface AiKnowledgeBaseMapper extends BaseMapper<AiKnowledgeBaseEntity> {

    /**
     * 按UUID查询知识库（用于Bean操作的selectById）
     *
     * @param kb_uuid 知识库UUID
     * @return 知识库实体
     */
    @Select("""
        SELECT
            id,
            kb_uuid AS kbUuid,
            title,
            remark,
            is_public AS isPublic,
            is_strict AS isStrict,
            ingest_max_overlap AS ingestMaxOverlap,
            ingest_model_name AS ingestModelName,
            ingest_model_id AS ingestModelId,
            ingest_token_estimator AS ingestTokenEstimator,
            ingest_embedding_model AS ingestEmbeddingModel,
            retrieve_max_results AS retrieveMaxResults,
            retrieve_min_score AS retrieveMinScore,
            query_llm_temperature AS queryLlmTemperature,
            query_system_message AS querySystemMessage,
            star_count AS starCount,
            embedding_count AS embeddingCount,
            entity_count AS entityCount,
            relation_count AS relationCount,
            owner_id AS ownerId,
            owner_name AS ownerName,
            item_count AS itemCount,
            c_time,
            u_time,
            c_id,
            u_id,
            dbversion
        FROM ai_knowledge_base
        WHERE kb_uuid = #{kb_uuid}
    """)
    AiKnowledgeBaseEntity selectByKbUuid(@Param("kb_uuid") String kb_uuid);

    /**
     * 根据文档UUID获取知识库
     *
     * @param itemUuid 文档UUID
     * @return 知识库实体
     */
    @Select("""
        SELECT
            kb.id,
            kb.kb_uuid AS kbUuid,
            kb.title,
            kb.remark,
            kb.is_public AS isPublic,
            kb.is_strict AS isStrict,
            kb.ingest_max_overlap AS ingestMaxOverlap,
            kb.ingest_model_name AS ingestModelName,
            kb.ingest_model_id AS ingestModelId,
            kb.ingest_token_estimator AS ingestTokenEstimator,
            kb.ingest_embedding_model AS ingestEmbeddingModel,
            kb.retrieve_max_results AS retrieveMaxResults,
            kb.retrieve_min_score AS retrieveMinScore,
            kb.query_llm_temperature AS queryLlmTemperature,
            kb.query_system_message AS querySystemMessage,
            kb.star_count AS starCount,
            kb.embedding_count AS embeddingCount,
            kb.entity_count AS entityCount,
            kb.relation_count AS relationCount,
            kb.owner_id AS ownerId,
            kb.owner_name AS ownerName,
            kb.item_count AS itemCount,
            kb.c_time,
            kb.u_time,
            kb.c_id,
            kb.u_id,
            kb.dbversion
        FROM ai_knowledge_base kb
        INNER JOIN ai_knowledge_base_item item ON kb.kb_uuid = item.kb_uuid
        WHERE item.item_uuid = #{itemUuid}
        LIMIT 1
    """)
    AiKnowledgeBaseEntity getByItemUuid(@Param("itemUuid") String itemUuid);

    /**
     * 根据UUID物理删除知识库
     *
     * @param kb_uuid 知识库UUID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_knowledge_base
        WHERE kb_uuid = #{kb_uuid}
    """)
    int deleteByKbUuid(@Param("kb_uuid") String kb_uuid);
}
