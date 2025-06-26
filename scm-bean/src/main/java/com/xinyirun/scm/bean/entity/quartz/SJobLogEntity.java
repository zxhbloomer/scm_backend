package com.xinyirun.scm.bean.entity.quartz;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinyirun.scm.bean.entity.base.entity.v1.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 定时任务调度日志表
 * </p>
 *
 * @author zxh
 * @since 2019-10-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("s_job_log")
public class SJobLogEntity extends BaseEntity<SJobLogEntity> implements Serializable {

    private static final long serialVersionUID = 3687772139151833885L;

    /**
     * 定时任务调度表
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务主键
     */
    @TableField("job_id")
    private Long job_id;

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
     * 任务描述
     */
    @TableField("job_desc")
    private String job_desc;

    /**
     * 任务简称
     */
    @TableField("job_simple_name")
    private String job_simple_name;

    /**
     * Bean名称
     */
    @TableField("class_name")
    private String class_name;

    /**
     * 方法名称
     */
    @TableField("method_name")
    private String method_name;

    /**
     * 参数类型
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
     * 计划策略：0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行
     */
    @TableField("misfire_policy")
    private String misfire_policy;

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

    @TableField("c_id")
    private Long c_id;

    @TableField("c_time")
    private LocalDateTime c_time;

    @TableField("u_id")
    private Long u_id;

    @TableField("u_time")
    private LocalDateTime u_time;

}
