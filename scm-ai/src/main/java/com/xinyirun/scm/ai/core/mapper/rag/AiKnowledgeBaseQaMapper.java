package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库问答记录 Mapper接口
 *
 * <p>对应 aideepin：KnowledgeBaseQaService</p>
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Mapper
public interface AiKnowledgeBaseQaMapper extends BaseMapper<AiKnowledgeBaseQaEntity> {

}
