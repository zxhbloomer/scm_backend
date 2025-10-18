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
 *
 * <p>用于管理问答记录与Elasticsearch向量引用的关联关系</p>
 * <p>记录RAG检索时召回的向量片段及其相似度分数</p>
 *
 * @author zxh
 * @since 2025-10-12
 */
@Mapper
public interface AiKnowledgeBaseQaRefEmbeddingMapper extends BaseMapper<AiKnowledgeBaseQaRefEmbeddingEntity> {

}
