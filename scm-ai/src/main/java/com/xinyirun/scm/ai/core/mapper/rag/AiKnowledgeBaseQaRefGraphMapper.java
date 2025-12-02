package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaRefGraphEntity;
import com.xinyirun.scm.ai.bean.vo.rag.QaRefGraphVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 知识库问答-图谱引用 Mapper接口
 *
 * <p>用于管理问答记录与Neo4j图谱引用的关联关系</p>
 * <p>记录RAG检索时从图谱中召回的实体和关系</p>
 *
 * @author zxh
 * @since 2025-10-12
 */
@Mapper
public interface AiKnowledgeBaseQaRefGraphMapper extends BaseMapper<AiKnowledgeBaseQaRefGraphEntity> {

    /**
     * 根据知识库UUID删除图谱引用记录（物理删除）
     * 通过qa_record_id关联ai_knowledge_base_qa表
     *
     * @param kbUuid 知识库UUID
     * @return 删除的记录数
     */
    @Select("""
        DELETE FROM ai_knowledge_base_qa_ref_graph
        WHERE qa_record_id IN (
            SELECT id FROM ai_knowledge_base_qa WHERE kb_uuid = #{kbUuid}
        )
    """)
    Integer deleteByKbUuid(@Param("kbUuid") String kbUuid);

}
