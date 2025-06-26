package com.xinyirun.scm.bean.entity.tenant.manager.quartz;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import lombok.experimental.Tolerate;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 任务主表
 * </p>
 *
 * @author zxh
 * @since 2019-10-14
 */
@Data
@TableName("s_job_manager")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SJobManagerEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -895671923379269150L;

    /**
     * 任务ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户定时任务id
     */
    @TableField(value = "tenant_job_id")
    private Long tenant_job_id;

    /**
     * 租户code
     */
    @TableField(value = "tenant_code")
    private String tenant_code;


    /**
     * 任务名称
     */
    @TableField("job_name")
    private String job_name;

    /**
     * 任务组类型
     */
    @TableField("job_group_type")
    private String job_group_type;

    /**
     * 关联编号
     */
    @TableField("job_serial_id")
    private Long job_serial_id;

    /**
     * 关联表名字
     */
    @TableField("job_serial_type")
    private String job_serial_type;

    /**
     * 任务说明
     */
    @TableField("job_desc")
    private String job_desc;

    /**
     * 任务简称
     */
    @TableField("job_simple_name")
    private String job_simple_name;

    /**
     * class名称
     */
    @TableField("class_name")
    private String class_name;

    /**
     * 方法名称
     */
    @TableField("method_name")
    private String method_name;

    /**
     * 参数class
     */
    @TableField("param_class")
    private String param_class;

    /**
     * 参数
     */
    @TableField("param_data")
    private String param_data;

    /**
     * 表达式
     */
    @TableField("cron_expression")
    private String cron_expression;

    /**
     * 计划策略：0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行
     */
    @TableField("misfire_policy")
    private String misfire_policy;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    @TableField("concurrent")
    private Boolean concurrent;

    /**
     * 判断是否是cron表达式，还是simpletrigger
     */
    @TableField("is_cron")
    private Boolean is_cron;

    /**
     * 是否是已经删除
     */
    @TableField("is_del")
    private Boolean is_del;

    /**
     * 是否有效
     */
    @TableField("is_effected")
    private Boolean is_effected;

    /**
     * 首次执行时间
     */
    @TableField("fire_time")
    private LocalDateTime fire_time;

    /**
     * 计划首次执行时间
     */
    @TableField("scheduled_fire_time")
    private LocalDateTime scheduled_fire_time;

    /**
     * 上次执行时间
     */
    @TableField("prev_fire_time")
    private LocalDateTime prev_fire_time;

    /**
     * next_fire_time
     */
    @TableField("next_fire_time")
    private LocalDateTime next_fire_time;

    /**
     * 运行次数
     */
    @TableField("run_times")
    private Integer run_times;

    /**
     * 执行情况
     */
    @TableField("msg")
    private String msg;

    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;


}
