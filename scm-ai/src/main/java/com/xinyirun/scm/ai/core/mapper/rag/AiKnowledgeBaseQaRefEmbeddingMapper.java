package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaRefEmbeddingEntity;
import com.xinyirun.scm.ai.bean.vo.rag.QaRefEmbeddingVo;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 知识库问答-向量引用 Mapper接口
 *
 * <p>用于管理问答记录与Milvus向量引用的关联关系</p>
 * <p>记录RAG检索时召回的向量片段及其相似度分数</p>
 *
 * @author zxh
 * @since 2025-10-12
 */
@Mapper
public interface AiKnowledgeBaseQaRefEmbeddingMapper extends BaseMapper<AiKnowledgeBaseQaRefEmbeddingEntity> {

    /**
     * 根据知识库UUID删除向量引用记录（物理删除）
     * 通过qa_record_id关联ai_knowledge_base_qa表
     *
     * @param kbUuid 知识库UUID
     * @return 删除的记录数
     */
    @Select("""
        DELETE FROM ai_knowledge_base_qa_ref_embedding
        WHERE qa_record_id IN (
            SELECT id FROM ai_knowledge_base_qa WHERE kb_uuid = #{kbUuid}
        )
    """)
    Integer deleteByKbUuid(@Param("kbUuid") String kbUuid);

    /**
     * 按qaRecordId查询向量引用列表
     *
     * @param qaRecordId 问答记录ID
     * @return 向量引用列表
     */
    @Select("""
        SELECT
            id AS id,
            qa_record_id AS qaRecordId,
            embedding_id AS embeddingId,
            score AS score,
            content AS content,
            user_id AS userId,
            create_time AS createTime
        FROM ai_knowledge_base_qa_ref_embedding
        WHERE qa_record_id = #{qaRecordId}
    """)
    List<AiKnowledgeBaseQaRefEmbeddingEntity> selectListByQaRecordId(@Param("qaRecordId") String qaRecordId);

    /**
     * 按qaRecordId物理删除向量引用记录
     *
     * @param qaRecordId 问答记录ID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_knowledge_base_qa_ref_embedding
        WHERE qa_record_id = #{qaRecordId}
    """)
    int deleteByQaRecordId(@Param("qaRecordId") String qaRecordId);

    /**
     * 按userId物理删除所有向量引用记录
     *
     * @param userId 用户ID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_knowledge_base_qa_ref_embedding
        WHERE user_id = #{userId}
    """)
    int deleteByUserId(@Param("userId") Long userId);

}
