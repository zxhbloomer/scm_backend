package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

/**
 * AI知识库 Mapper接口
 *
 * @author SCM AI Team
 * @since 2025-10-03
 */
@Mapper
public interface AiKnowledgeBaseMapper extends BaseMapper<AiKnowledgeBaseEntity> {

    /**
     * 更新知识库统计数据
     * 更新 embedding_count 和 item_count（通过子查询计算）
     *
     * @param kbUuid 知识库UUID
     * @param embeddingCount 向量数量
     */
    @Update("""
        UPDATE ai_knowledge_base
        SET item_count = (
                SELECT COUNT(1)
                FROM ai_knowledge_base_item
                WHERE kb_uuid = #{kbUuid}
                  AND embedding_status = 3
            ),
            embedding_count = #{embeddingCount}
        WHERE kb_uuid = #{kbUuid}
    """)
    void updateStatByUuid(@Param("kbUuid") String kbUuid, @Param("embeddingCount") Integer embeddingCount);

    /**
     * 根据文档UUID获取知识库
     *
     * @param itemUuid 文档UUID
     * @return 知识库实体
     */
    @Select("""
        SELECT kb.*
        FROM ai_knowledge_base kb
        INNER JOIN ai_knowledge_base_item item ON kb.kb_uuid = item.kb_uuid
        WHERE item.item_uuid = #{itemUuid}
        LIMIT 1
    """)
    AiKnowledgeBaseEntity getByItemUuid(@Param("itemUuid") String itemUuid);
}
