package com.xinyirun.scm.ai.bean.entity.workflow;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI工作流运行时节点实体类
 * 对应数据表：ai_workflow_runtime_node
 *
 * 功能说明：存储工作流执行过程中每个节点的运行状态和输入输出数据
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ai_workflow_runtime_node", autoResultMap = true)
public class AiWorkflowRuntimeNodeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 工作流运行时ID
     */
    @TableField("workflow_runtime_id")
    private Long workflowRuntimeId;

    /**
     * 节点ID
     */
    @TableField("node_id")
    private Long nodeId;

    /**
     * 节点UUID
     */
    @TableField("node_uuid")
    private String nodeUuid;

    /**
     * 节点输入数据(JSON格式)
     */
    @TableField(value = "input", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> input;

    /**
     * 节点输出数据(JSON格式)
     */
    @TableField(value = "output", typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> output;

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
     * 是否删除(0-未删除,1-已删除)
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime uTime;

    /**
     * 创建人ID
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    /**
     * 修改人ID
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    /**
     * 数据版本(乐观锁)
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;
}
