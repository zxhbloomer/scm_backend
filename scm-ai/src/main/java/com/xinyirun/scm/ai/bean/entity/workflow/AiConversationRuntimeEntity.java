package com.xinyirun.scm.ai.bean.entity.workflow;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI Chat调用Workflow运行时实体
 *
 * <p>用于存储AI Chat调用Workflow时的运行时实例数据</p>
 * <p>与ai_workflow_runtime表结构完全一致,但数据独立存储</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
@Data
@TableName("ai_conversation_runtime")
public class AiConversationRuntimeEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 运行时UUID(业务主键)
     */
    @TableField("runtime_uuid")
    private String runtimeUuid;

    /**
     * 对话ID,格式:tenantId::uuid,关联ai_conversation表
     */
    @TableField("conversation_id")
    private String conversationId;

    /**
     * 执行用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 输入数据(JSON格式)
     */
    @TableField("input_data")
    private String inputData;

    /**
     * 输出数据(JSON格式)
     */
    @TableField("output_data")
    private String outputData;

    /**
     * 执行状态(1-等待中,2-运行中,3-成功,4-失败)
     */
    @TableField("status")
    private Integer status;

    /**
     * 状态说明
     */
    @TableField("status_remark")
    private String statusRemark;

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
    @TableField(value = "c_id")
    private Long c_id;

    /**
     * 修改人ID
     */
    @TableField(value = "u_id")
    private Long u_id;

    /**
     * 数据版本(乐观锁)
     */
    @TableField("dbversion")
    private Integer dbversion;
}
