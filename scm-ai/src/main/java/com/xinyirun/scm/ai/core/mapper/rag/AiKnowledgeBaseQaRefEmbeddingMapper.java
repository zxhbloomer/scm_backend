package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaRefEmbeddingEntity;
import com.xinyirun.scm.ai.bean.vo.rag.QaRefEmbeddingVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 知识库问答-向量引用 Mapper接口
 * 对应 aideepin：KnowledgeBaseQaRecordReferenceService
 *
 * @author zxh
 * @since 2025-10-12
 */
@Mapper
public interface AiKnowledgeBaseQaRefEmbeddingMapper extends BaseMapper<AiKnowledgeBaseQaRefEmbeddingEntity> {

    /**
     * 查询问答记录的向量引用详情
     * 注意：使用AS别名转驼峰（map-underscore-to-camel-case: false）
     * 注意：text内容需要从Elasticsearch查询，这里只返回embeddingId、score、rank
     */
    @Select("""
        SELECT
            ref.id AS id,
            ref.qa_record_id AS qaRecordId,
            ref.embedding_id AS embeddingId,
            ref.score AS score,
            ref.user_id AS userId,
            ref.rank AS rank,
            ref.create_time AS createTime
        FROM ai_knowledge_base_qa_ref_embedding ref
        WHERE ref.qa_record_id = #{qaRecordId}
        ORDER BY ref.rank ASC
    """)
    List<QaRefEmbeddingVo> selectByQaRecordId(@Param("qaRecordId") String qaRecordId);

    /**
     * 批量插入向量引用记录
     * 注意：MyBatis Plus的saveBatch已经支持，这里不需要额外SQL
     */
}
