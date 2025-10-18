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

}
