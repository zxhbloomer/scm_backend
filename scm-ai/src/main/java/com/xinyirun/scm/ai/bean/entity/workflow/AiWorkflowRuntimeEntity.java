package com.xinyirun.scm.ai.bean.entity.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.ai.config.handler.FastjsonTypeHandler;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI工作流运行时实体类
 * 对应数据表：ai_workflow_runtime
 *
 * 功能说明：存储工作流的执行实例，包括输入输出数据、执行状态等
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ai_workflow_runtime", autoResultMap = true)
public class AiWorkflowRuntimeEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
     * 工作流ID
     */
    @TableField("workflow_id")
    private Long workflowId;

    /**
     * 执行用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 输入数据(JSON格式)
     * 使用 Fastjson2 的 JSONObject 替代 Map<String, Object>
     */
    @TableField(value = "input", typeHandler = FastjsonTypeHandler.class)
    private JSONObject input;

    /**
     * 输出数据(JSON格式)
     * 使用 Fastjson2 的 JSONObject 替代 Map<String, Object>
     */
    @TableField(value = "output", typeHandler = FastjsonTypeHandler.class)
    private JSONObject output;

    /**
     * 执行状态(1-运行中,2-成功,3-失败)
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
    private Boolean isDeleted;

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
