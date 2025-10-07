package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseQaRefEmbeddingEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库问答-向量引用 Mapper接口
 *
 * <p>对应 aideepin：KnowledgeBaseQaRecordReferenceService</p>
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Mapper
public interface AiKnowledgeBaseQaRefEmbeddingMapper extends BaseMapper<AiKnowledgeBaseQaRefEmbeddingEntity> {

}
