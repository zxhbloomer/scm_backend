package com.xinyirun.scm.ai.bean.entity.chat;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI对话预设关系实体类
 * 对应数据表：ai_conversation_preset_rel
 *
 * 功能说明：存储用户对话与预设的关联关系
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ai_conversation_preset_rel")
public class AiConversationPresetRelEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 业务主键UUID
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 预设对话ID
     * 关联：ai_conversation_preset.id
     */
    @TableField("preset_conv_id")
    private String presetConvId;

    /**
     * 用户对话ID
     * 关联：ai_conversation.id
     */
    @TableField("user_conv_id")
    private String userConvId;

    /**
     * 用户自定义修改
     * JSON格式，记录用户对预设的个性化调整
     */
    @TableField("custom_modifications")
    private String customModifications;

    /**
     * 创建人ID
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;

    /**
     * 修改人ID
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime uTime;

    /**
     * 数据版本（乐观锁）
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

    /**
     * 创建人用户名
     */
    @TableField("create_user")
    private String createUser;
}
