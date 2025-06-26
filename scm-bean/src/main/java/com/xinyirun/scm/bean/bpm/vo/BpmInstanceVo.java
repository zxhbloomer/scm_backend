package com.xinyirun.scm.bean.bpm.vo;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 审批流实例表
 * @author xinyirun
 * @since 2024-10-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BpmInstanceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -757100544450430083L;

    private Integer id;

    /**
     * 审批编号
     */
    private String process_code;

    /**
     * 流程实例 ID，唯一标识
     */
    private String process_instance_id;

    /**
     * 流程定义 ID
     */
    private String process_definition_id;

    /**
     * 流程定义名称
     */
    private String process_definition_name;

    /**
     * 流程定义版本号
     */
    private Integer process_definition_version;

    /**
     * 业务键，用于关联业务数据
     */
    private String business_key;

    /**
     * 业务表单数据
     */
    private String form_json;

    /**
     * 流程表单数据
     */
    private String form_data;

    /**
     * 业务表单保存实体
     */
    private String form_class;

    /**
     * 发起人 code
     */
    private String owner_code;

    /**
     * 发起人 名称
     */
    private String owner_name;

    /**
     * 流程开始时间
     */
    private LocalDateTime start_time;

    /**
     * 流程结束时间，若流程未结束则为 null
     */
    private LocalDateTime end_time;

    /**
     * 0-正在处理 1-已撤销 2-办结（已完成）3-驳回
     */
    private String status;

    /**
     * 摸板表单
     */
    private String form_items;

    /**
     * process
     */
    private String process;

    /**
     * 初始流程节点
     */
    private String initial_process;

    /**
     * 业务键，可用于关联业务数据
     */
    private Integer serial_id;

    /**
     * 表名
     */
    private String serial_type;

    /**
     * 当前任务 ID，可选
     */
    private String current_task_id;

    /**
     * 当前任务名称，可选
     */
    private String current_task_name;

    /**
     * bpm_process_templates.id
     */
    private Integer process_id;

    /**
     * 下一个审批人code
     */
    private String next_approve_code;

    /**
     * 下一个审批人名称
     */
    private String next_approve_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 流程名，业务定义：如（新增企业审批）
     */
    private String process_definition_business_name;

    /**
     * 摘要
     */
    private String summary;
}
