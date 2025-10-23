package com.xinyirun.scm.ai.bean.entity.workflow;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI工作流组件库实体类
 * 对应数据表：ai_workflow_component
 *
 * 功能说明：存储可复用的工作流组件定义，包括组件名称、图标、显示顺序等
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_workflow_component")
public class AiWorkflowComponentEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 组件UUID(业务主键)
     */
    @TableField("component_uuid")
    private String componentUuid;

    /**
     * 组件名称(唯一标识)
     */
    @TableField("name")
    private String name;

    /**
     * 组件标题(显示名称)
     */
    @TableField("title")
    private String title;

    /**
     * 组件图标
     */
    @TableField("icon")
    private String icon;

    /**
     * 组件描述说明
     */
    @TableField("remark")
    private String remark;

    /**
     * 显示顺序
     */
    @TableField("display_order")
    private Integer displayOrder;

    /**
     * 是否启用(0-禁用,1-启用)
     */
    @TableField("is_enable")
    private Integer isEnable;

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
