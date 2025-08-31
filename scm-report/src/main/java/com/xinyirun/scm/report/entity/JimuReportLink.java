package com.xinyirun.scm.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积木报表链接实体
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Data
@TableName("jimu_report_link")
public class JimuReportLink implements Serializable {


    @Serial
    private static final long serialVersionUID = 677202091353309539L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 报表ID
     */
    @TableField("report_id")
    private String reportId;

    /**
     * 参数
     */
    @TableField("parameter")
    private String parameter;

    /**
     * 弹出方式
     */
    @TableField("eject_type")
    private String ejectType;

    /**
     * 链接名称
     */
    @TableField("link_name")
    private String linkName;

    /**
     * API方法
     */
    @TableField("api_method")
    private String apiMethod;

    /**
     * 链接类型
     */
    @TableField("link_type")
    private String linkType;

    /**
     * API地址
     */
    @TableField("api_url")
    private String apiUrl;

    /**
     * 图表类型
     */
    @TableField("link_chart_type")
    private String linkChartType;

    /**
     * 图表组件
     */
    @TableField("provider")
    private String provider;

    /**
     * 参数类型0-简单参数,1-复杂参数
     */
    @TableField("parameter_type")
    private Integer parameterType;

    /**
     * 是否必填
     */
    @TableField("requirement")
    private Integer requirement;

    /**
     * 创建人登录名称
     */
    @TableField(value = "create_by", fill = FieldFill.INSERT)
    private String createBy;

    /**
     * 创建日期
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新人登录名称
     */
    @TableField(value = "update_by", fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /**
     * 更新日期
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 删除标识0-正常,1-已删除
     */
    @TableField("del_flag")
    @TableLogic
    private Integer delFlag;
}