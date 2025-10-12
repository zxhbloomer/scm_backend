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
 * 对应数据库表：ai_knowledge_base_qa_ref_embedding
 * 对标：aideepin KnowledgeBaseQaRefEmbedding
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
     * 向量ID（Elasticsearch文档ID）
     */
    @TableField("embedding_id")
    private String embeddingId;

    /**
     * 相似度分数（0-1之间）
     */
    @TableField("score")
    private Double score;

    /**
     * 提问用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 排序序号，表示向量召回的排名
     * 用于保留Elasticsearch召回时的排序顺序
     * 对标：aideepin通过Map迭代无法保证顺序，scm-ai增加rank字段
     */
    @TableField("rank")
    private Integer rank;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
