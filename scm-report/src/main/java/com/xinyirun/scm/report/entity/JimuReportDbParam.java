package com.xinyirun.scm.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积木报表参数实体
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Data
@TableName("jimu_report_db_param")
public class JimuReportDbParam implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 报表ID
     */
    @TableField("jimu_report_id")
    private String jimuReportId;

    /**
     * 参数名
     */
    @TableField("param_name")
    private String paramName;

    /**
     * 参数备注
     */
    @TableField("param_txt")
    private String paramTxt;

    /**
     * 参数值
     */
    @TableField("param_value")
    private String paramValue;

    /**
     * 参数类型
     */
    @TableField("param_type")
    private String paramType;

    /**
     * 是否必须0-非必须,1-必须
     */
    @TableField("is_required")
    private Integer isRequired;

    /**
     * 排序
     */
    @TableField("order_num")
    private Integer orderNum;

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