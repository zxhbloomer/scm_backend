package com.xinyirun.scm.ai.bean.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
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
    private String conversationId;

    /**
     * 记录类型（USER, ASSISTANT, SYSTEM, TOOL）
     */
    @TableField("type")
    private String type;

    /**
     * 对话内容
     */
    @TableField("content")
    private String content;

    /**
     * AI模型源ID
     */
    @TableField("model_source_id")
    private String modelSourceId;

    /**
     * AI提供商名称
     */
    @TableField("provider_name")
    private String providerName;

    /**
     * 基础模型名称
     */
    @TableField("base_name")
    private String baseName;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人id
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    /**
     * 修改人id
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;

}
