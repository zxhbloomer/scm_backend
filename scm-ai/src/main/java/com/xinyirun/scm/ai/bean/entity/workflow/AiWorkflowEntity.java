package com.xinyirun.scm.ai.bean.entity.workflow;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI工作流定义实体类
 * 对应数据表：ai_workflow
 *
 * 功能说明：存储AI工作流的定义信息，包括标题、描述、公开状态等
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_workflow")
public class AiWorkflowEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 工作流UUID(业务主键)
     */
    @TableField("workflow_uuid")
    private String workflowUuid;

    /**
     * 工作流标题
     */
    @TableField("title")
    private String title;

    /**
     * 工作流描述说明
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 是否公开(0-私有,1-公开)
     */
    @TableField("is_public")
    private Boolean isPublic;

    /**
     * 是否启用(0-禁用,1-启用)
     */
    @TableField("is_enable")
    private Boolean isEnable;

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
