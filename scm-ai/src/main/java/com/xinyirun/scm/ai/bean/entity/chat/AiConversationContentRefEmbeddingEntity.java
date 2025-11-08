package com.xinyirun.scm.ai.bean.entity.chat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI对话消息-向量引用实体类
 * 对应数据表：ai_conversation_content_ref_embedding
 *
 * 功能说明：记录对话消息引用的向量检索结果（Elasticsearch）
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ai_conversation_content_ref_embedding")
public class AiConversationContentRefEmbeddingEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息ID
     * 关联：ai_conversation_content.message_id
     */
    @TableField("message_id")
    private String messageId;

    /**
     * 向量ID
     * Elasticsearch文档ID
     */
    @TableField("embedding_id")
    private String embeddingId;

    /**
     * 知识库ID
     */
    @TableField("kb_id")
    private String kbId;

    /**
     * 知识点ID
     */
    @TableField("kb_item_id")
    private String kbItemId;

    /**
     * 相关度评分
     * 取值范围：0-1
     */
    @TableField("score")
    private BigDecimal score;

    /**
     * 内容片段
     * 匹配的文本摘要
     */
    @TableField("content_snippet")
    private String contentSnippet;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField("c_time")
    private LocalDateTime cTime;
}
