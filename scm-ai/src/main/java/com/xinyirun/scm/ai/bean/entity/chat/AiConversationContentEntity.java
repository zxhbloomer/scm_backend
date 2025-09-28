package com.xinyirun.scm.ai.bean.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI对话内容实体类
 * 对应数据表：ai_conversation_content
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_conversation_content")
public class AiConversationContentEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 对话ID
     */
    @TableField("conversation_id")
    @DataChangeLabelAnnotation("对话ID")
    private String conversation_id;

    /**
     * 记录类型（USER, ASSISTANT, SYSTEM, TOOL）
     */
    @TableField("type")
    @DataChangeLabelAnnotation("记录类型")
    private String type;

    /**
     * 对话内容
     */
    @TableField("content")
    @DataChangeLabelAnnotation("对话内容")
    private String content;

    /**
     * AI模型源ID
     */
    @TableField("model_source_id")
    @DataChangeLabelAnnotation("AI模型源ID")
    private String model_source_id;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @DataChangeLabelAnnotation("创建时间")
    private Long create_time;

    // 兼容方法，用于保持与备份代码的一致性

    /**
     * 设置会话ID（兼容方法）
     */
    public void setConversationId(String conversationId) {
        this.conversation_id = conversationId;
    }

    /**
     * 设置模型源ID（兼容方法）
     */
    public void setModelSourceId(String modelSourceId) {
        this.model_source_id = modelSourceId;
    }

    /**
     * 设置创建时间（兼容方法）
     */
    public void setCreateTime(Long createTime) {
        this.create_time = createTime;
    }
}
