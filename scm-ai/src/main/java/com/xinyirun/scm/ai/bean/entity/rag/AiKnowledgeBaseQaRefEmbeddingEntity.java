package com.xinyirun.scm.ai.bean.entity.rag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI知识库问答记录-向量引用实体类
 *
 * <p>对应数据库表：ai_knowledge_base_qa_ref_embedding</p>
 * <p>用于记录RAG问答时，从Milvus向量数据库中召回的文档片段引用关系</p>
 *
 * @author zxh
 * @since 2025-10-12
 */
@Data
@TableName("ai_knowledge_base_qa_ref_embedding")
public class AiKnowledgeBaseQaRefEmbeddingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 问答记录ID
     * 关联：ai_knowledge_base_qa.id
     */
    @TableField("qa_record_id")
    private String qaRecordId;

    /**
     * Milvus向量文档ID（对应Milvus的doc_id字段）
     * 用于关联Milvus中存储的向量和文本内容
     */
    @TableField("embedding_id")
    private String embeddingId;

    /**
     * 相似度分数（0-1之间）
     */
    @TableField("score")
    private Double score;

    /**
     * 向量检索的文本内容
     * RAG问答时保存，避免后续查询Milvus
     */
    @TableField("content")
    private String content;

    /**
     * 提问用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
