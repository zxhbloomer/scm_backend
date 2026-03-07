package com.xinyirun.scm.ai.bean.entity.workflow;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI工作流人机交互实体
 *
 * <p>用于存储工作流执行过程中的人机交互记录，
 * 支持用户选择、确认、表单填写等交互类型</p>
 *
 * @author SCM-AI团队
 * @since 2026-03-06
 */
@Data
@TableName("ai_workflow_interaction")
public class AiWorkflowInteractionEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 交互UUID(业务主键)
     */
    @TableField("interaction_uuid")
    private String interactionUuid;

    /**
     * 对话ID，关联ai_conversation
     */
    @TableField("conversation_id")
    private String conversationId;

    /**
     * 运行时UUID，关联ai_conversation_runtime
     */
    @TableField("runtime_uuid")
    private String runtimeUuid;

    /**
     * 触发交互的节点UUID
     */
    @TableField("node_uuid")
    private String nodeUuid;

    /**
     * 交互类型: user_select/user_confirm/user_form
     */
    @TableField("interaction_type")
    private String interactionType;

    /**
     * 交互参数JSON(选项列表/确认文案/表单定义)
     */
    @TableField("interaction_params")
    private String interactionParams;

    /**
     * 交互描述(显示给用户的提示文字)
     */
    @TableField("description")
    private String description;

    /**
     * 状态: WAITING/SUBMITTED/TIMEOUT/CANCELLED
     */
    @TableField("status")
    private String status;

    /**
     * 超时时间(分钟)
     */
    @TableField("timeout_minutes")
    private Integer timeoutMinutes;

    /**
     * 超时截止时间
     */
    @TableField("timeout_at")
    private LocalDateTime timeoutAt;

    /**
     * 用户反馈数据JSON
     */
    @TableField("feedback_data")
    private String feedbackData;

    /**
     * 用户操作: select_record/confirm/reject/cancel/__timeout
     */
    @TableField("feedback_action")
    private String feedbackAction;

    /**
     * 提交时间
     */
    @TableField("submitted_at")
    private LocalDateTime submittedAt;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人ID
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人ID
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本(乐观锁)
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;
}
