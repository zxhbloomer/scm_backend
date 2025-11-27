package com.xinyirun.scm.ai.bean.entity.workflow;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI Chat调用Workflow节点执行实体
 *
 * <p>用于存储AI Chat调用Workflow时的节点执行数据</p>
 * <p>与ai_workflow_runtime_node表结构完全一致,但数据独立存储</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
@Data
@TableName("ai_conversation_runtime_node")
public class AiConversationRuntimeNodeEntity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 运行时节点UUID(业务主键)
     */
    @TableField("runtime_node_uuid")
    private String runtimeNodeUuid;

    /**
     * AI Chat工作流运行时ID(关联ai_conversation_runtime表)
     */
    @TableField("conversation_workflow_runtime_id")
    private Long conversationWorkflowRuntimeId;

    /**
     * 节点ID
     */
    @TableField("node_id")
    private Long nodeId;

    /**
     * 节点输入数据(JSON格式)
     */
    @TableField("input_data")
    private String inputData;

    /**
     * 节点输出数据(JSON格式)
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
