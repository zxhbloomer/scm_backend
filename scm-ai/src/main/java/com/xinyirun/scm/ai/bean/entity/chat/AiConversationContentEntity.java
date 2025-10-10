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
    private String conversationId;

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
    private String modelSourceId;

    /**
     * AI提供商名称
     */
    @TableField("provider_name")
    @DataChangeLabelAnnotation("AI提供商名称")
    private String providerName;

    /**
     * 基础模型名称
     */
    @TableField("base_name")
    @DataChangeLabelAnnotation("基础模型名称")
    private String baseName;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建时间", extension = "getCTimeExtension")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改时间", extension = "getUTimeExtension")
    private LocalDateTime updateTime;

    /**
     * 创建人id
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建人", extension = "getUserNameExtension")
    private Long cId;

    /**
     * 修改人id
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改人", extension = "getUserNameExtension")
    private Long uId;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;

}
