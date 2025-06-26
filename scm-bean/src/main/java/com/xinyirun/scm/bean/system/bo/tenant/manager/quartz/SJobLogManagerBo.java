package com.xinyirun.scm.bean.system.bo.tenant.manager.quartz;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
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
@NoArgsConstructor
@EqualsAndHashCode(callSuper=false)
public class SJobLogManagerBo implements Serializable {


    @Serial
    private static final long serialVersionUID = -144950662279279967L;

    /**
     * 定时任务调度表
     */
    private Long id;

    /**
     * 租户定时任务id
     */
    private Long tenant_job_id;

    /**
     * 租户code
     */
    private String tenant_code;

    /**
     * 任务主键
     */
    private Long job_id;

    /**
     * 任务名称
     */
    private String job_name;

    /**
     * 任务组类型
     */
    private String job_group_type;

    /**
     * 关联编号
     */
    private Long job_serial_id;

    /**
     * 关联表名字
     */
    private String job_serial_type;

    /**
     * 任务描述
     */
    private String job_desc;

    /**
     * 任务简称
     */
    private String job_simple_name;

    /**
     * Bean名称
     */
    private String class_name;

    /**
     * 方法名称
     */
    private String method_name;

    /**
     * 参数类型
     */
    private String param_class;

    /**
     * 参数
     */
    private String param_data;

    /**
     * 表达式
     */
    private String cron_expression;

    /**
     * 是否并发执行（0允许 1禁止）
     */
    private Boolean concurrent;

    /**
     * 判断是否是cron表达式，还是simpletrigger
     */
    private Boolean is_cron;

    /**
     * 计划策略：0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行
     */
    private String misfire_policy;

    /**
     * 是否是已经删除
     */
    private Boolean is_del;

    /**
     * 是否有效
     */
    private Boolean is_effected;

    /**
     * 首次执行时间
     */
    private LocalDateTime fire_time;

    /**
     * 计划首次执行时间
     */
    private LocalDateTime scheduled_fire_time;

    /**
     * 上次执行时间
     */
    private LocalDateTime prev_fire_time;

    /**
     * next_fire_time
     */
    private LocalDateTime next_fire_time;

    /**
     * 运行次数
     */
    private Integer run_times;

    /**
     * 执行情况
     */
    private String msg;

    private Long c_id;

    private LocalDateTime c_time;

    private Long u_id;

    private LocalDateTime u_time;

}
