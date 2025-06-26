package com.xinyirun.scm.bean.entity.bpm;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 审批流实例表
 * @author xinyirun
 * @since 2024-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("bpm_instance")
public class BpmInstanceEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 973954991023093418L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 审批编号
     */
    @TableField("process_code")
    private String process_code;

    /**
     * 流程实例 ID，唯一标识
     */
    @TableField("process_instance_id")
    private String process_instance_id;

    /**
     * 流程定义 ID
     */
    @TableField("process_definition_id")
    private String process_definition_id;

    /**
     * 流程定义名称
     */
    @TableField("process_definition_name")
    private String process_definition_name;

    /**
     * 流程定义版本号
     */
    @TableField("process_definition_version")
    private Integer process_definition_version;

    /**
     * 业务键，用于关联业务数据
     */
    @TableField("business_key")
    private String business_key;

    /**
     * 业务表单数据
     */
    @TableField("form_json")
    private String form_json;

    /**
     * 流程表单数据
     */
    @TableField("form_data")
    private String form_data;

    /**
     * 业务表单保存实体
     */
    @TableField("form_class")
    private String form_class;

    /**
     * 发起人 code
     */
    @TableField("owner_code")
    private String owner_code;

    /**
     * 发起人 名称
     */
    @TableField("owner_name")
    private String owner_name;

    /**
     * 流程开始时间
     */
    @TableField("start_time")
    private LocalDateTime start_time;

    /**
     * 流程结束时间，若流程未结束则为 null
     */
    @TableField("end_time")
    private LocalDateTime end_time;

    /**
     * 0-正在处理 1-已撤销 2-办结（已完成）3-驳回
     */
    @TableField("status")
    private String status;

    /**
     * 摸板表单
     */
    @TableField("form_items")
    private String form_items;

    /**
     * process
     */
    @TableField("process")
    private String process;

    /**
     * 初始流程节点
     */
    @TableField("initial_process")
    private String initial_process;

    /**
     * 业务键，可用于关联业务数据
     */
    @TableField("serial_id")
    private Integer serial_id;

    /**
     * 表名
     */
    @TableField("serial_type")
    private String serial_type;

    /**
     * 当前任务 ID，可选
     */
    @TableField("current_task_id")
    private String current_task_id;

    /**
     * 当前任务名称，可选
     */
    @TableField("current_task_name")
    private String current_task_name;

    /**
     * bpm_process_templates.id
     */
    @TableField("process_id")
    private Integer process_id;

    /**
     * 下一个审批人code
     */
    @TableField("next_approve_code")
    private String next_approve_code;

    /**
     * 下一个审批人名称
     */
    @TableField("next_approve_name")
    private String next_approve_name;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT)
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;
}
