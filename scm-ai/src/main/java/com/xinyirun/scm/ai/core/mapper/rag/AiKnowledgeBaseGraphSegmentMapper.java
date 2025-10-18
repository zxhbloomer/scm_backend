package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseGraphSegmentEntity;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 知识库图谱分段 Mapper接口
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Mapper
public interface AiKnowledgeBaseGraphSegmentMapper extends BaseMapper<AiKnowledgeBaseGraphSegmentEntity> {

    /**
     * 根据知识库UUID物理删除所有graph segment数据
     *
     * @param kb_uuid 知识库UUID
     * @return 删除的行数
     */
    @Delete("""
        DELETE FROM ai_knowledge_base_graph_segment
        WHERE kb_uuid = #{kb_uuid}
    """)
    int deleteByKbUuid(@Param("kb_uuid") String kb_uuid);
}
