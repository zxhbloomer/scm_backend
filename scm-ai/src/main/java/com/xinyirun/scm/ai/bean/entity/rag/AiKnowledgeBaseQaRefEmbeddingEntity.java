package com.xinyirun.scm.ai.bean.entity.rag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 知识库问答记录-向量引用实体类
 *
 * <p>对应数据库表：ai_knowledge_base_qa_ref_embedding</p>
 * <p>对应 aideepin 实体：KnowledgeBaseQaRefEmbedding</p>
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Data
@TableName("ai_knowledge_base_qa_ref_embedding")
public class AiKnowledgeBaseQaRefEmbeddingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 问答记录ID
     * 关联：ai_knowledge_base_qa.id
     */
    @TableField("qa_record_id")
    private String qaRecordId;

    /**
     * 向量id（Elasticsearch文档ID）
     */
    @TableField("embedding_id")
    private String embeddingId;

    /**
     * 评分（相似度分数）
     */
    @TableField("score")
    private Double score;

    /**
     * 提问用户id
     */
    @TableField("user_id")
    private Long userId;
}
