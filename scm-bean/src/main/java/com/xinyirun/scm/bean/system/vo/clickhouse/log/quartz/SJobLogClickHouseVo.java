package com.xinyirun.scm.bean.system.vo.clickhouse.log.quartz;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 定时任务调度日志表 - ClickHouse VO类
 * </p>
 *
 * @author SCM System
 * @since 1.0.39
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SJobLogClickHouseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -837597325091147930L;

    /**
     * 主键ID
     */
    private String id;

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

    /**
     * 创建人ID
     */
    private Long c_id;

    /**
     * 创建人名称
     */
    private String c_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人ID
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 租户代码
     */
    private String tenant_code;

    /**
     * 开始时间（查询条件）
     */
    private LocalDateTime start_time;

    /**
     * 结束时间（查询条件）
     */
    private LocalDateTime over_time;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;
}