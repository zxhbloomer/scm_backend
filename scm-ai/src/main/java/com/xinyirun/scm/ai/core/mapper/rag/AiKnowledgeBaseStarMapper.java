package com.xinyirun.scm.ai.core.mapper.rag;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.rag.AiKnowledgeBaseStarEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库收藏记录 Mapper
 *
 * <p>对应 aideepin 的 KnowledgeBaseStarMapper</p>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Mapper
public interface AiKnowledgeBaseStarMapper extends BaseMapper<AiKnowledgeBaseStarEntity> {
}
