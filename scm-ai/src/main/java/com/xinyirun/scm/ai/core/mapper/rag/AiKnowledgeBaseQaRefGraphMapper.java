package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaRefGraphEntity;
import com.xinyirun.scm.ai.bean.vo.rag.QaRefGraphVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 知识库问答-图谱引用 Mapper接口
 * 对应 aideepin：KnowledgeBaseQaRecordRefGraphService
 *
 * @author zxh
 * @since 2025-10-12
 */
@Mapper
public interface AiKnowledgeBaseQaRefGraphMapper extends BaseMapper<AiKnowledgeBaseQaRefGraphEntity> {

    /**
     * 根据问答记录ID查询图谱引用
     * 注意：使用AS别名转驼峰（map-underscore-to-camel-case: false）
     */
    @Select("""
        SELECT
            rg.id AS id,
            rg.qa_record_id AS qaRecordId,
            rg.entities_from_question AS entitiesFromQuestion,
            rg.graph_from_store AS graphFromStore,
            rg.user_id AS userId,
            rg.create_time AS createTime
        FROM ai_knowledge_base_qa_ref_graph rg
        WHERE rg.qa_record_id = #{qaRecordId}
    """)
    QaRefGraphVo selectByQaRecordId(@Param("qaRecordId") String qaRecordId);
}
