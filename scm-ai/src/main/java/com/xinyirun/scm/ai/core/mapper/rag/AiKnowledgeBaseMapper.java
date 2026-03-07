package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import com.xinyirun.scm.ai.bean.vo.rag.AiKnowledgeBaseVo;
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
            is_temp AS isTemp,
            expire_time AS expireTime,
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

    /**
     * 搜索我的知识库（排除临时知识库）
     *
     * @param page 分页参数
     * @param ownerId 所有者ID
     * @param keyword 搜索关键词（可为null）
     * @return 分页结果
     */
    @Select("""
        SELECT
            t1.id,
            t1.kb_uuid AS kbUuid,
            t1.title,
            t1.remark,
            t1.is_public AS isPublic,
            t1.is_strict AS isStrict,
            t1.owner_id AS ownerId,
            t1.owner_name AS ownerName,
            t1.item_count AS itemCount,
            t1.star_count AS starCount,
            t1.embedding_count AS embeddingCount,
            t1.entity_count AS entityCount,
            t1.relation_count AS relationCount,
            t1.is_temp AS isTemp,
            t1.c_time,
            t1.u_time
        FROM ai_knowledge_base t1
        WHERE t1.owner_id = #{ownerId}
            AND (t1.is_temp = 0 OR t1.is_temp IS NULL)
            AND (#{keyword} IS NULL OR #{keyword} = '' OR t1.title LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY t1.c_time DESC
    """)
    IPage<AiKnowledgeBaseVo> searchMineExcludeTemp(Page<AiKnowledgeBaseVo> page, @Param("ownerId") String ownerId, @Param("keyword") String keyword);

    /**
     * 搜索公开知识库（排除临时知识库）
     *
     * @param page 分页参数
     * @param keyword 搜索关键词（可为null）
     * @return 分页结果
     */
    @Select("""
        SELECT
            t1.id,
            t1.kb_uuid AS kbUuid,
            t1.title,
            t1.remark,
            t1.is_public AS isPublic,
            t1.is_strict AS isStrict,
            t1.owner_id AS ownerId,
            t1.owner_name AS ownerName,
            t1.item_count AS itemCount,
            t1.star_count AS starCount,
            t1.embedding_count AS embeddingCount,
            t1.entity_count AS entityCount,
            t1.relation_count AS relationCount,
            t1.is_temp AS isTemp,
            t1.c_time,
            t1.u_time
        FROM ai_knowledge_base t1
        WHERE t1.is_public = 1
            AND (t1.is_temp = 0 OR t1.is_temp IS NULL)
            AND (#{keyword} IS NULL OR #{keyword} = '' OR t1.title LIKE CONCAT('%', #{keyword}, '%'))
        ORDER BY t1.star_count DESC, t1.c_time DESC
    """)
    IPage<AiKnowledgeBaseVo> searchPublicExcludeTemp(Page<AiKnowledgeBaseVo> page, @Param("keyword") String keyword);
}
