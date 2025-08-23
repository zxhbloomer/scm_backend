package com.xinyirun.scm.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积木报表映射实体
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Data
@TableName("jimu_report_map")
public class JimuReportMap implements Serializable {

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
     * 字段名
     */
    @TableField("field_name")
    private String fieldName;

    /**
     * 字段备注
     */
    @TableField("field_txt")
    private String fieldTxt;

    /**
     * 字段类型
     */
    @TableField("field_type")
    private String fieldType;

    /**
     * 查询模式
     */
    @TableField("search_mode")
    private String searchMode;

    /**
     * 是否排序 0否1是
     */
    @TableField("is_order")
    private Integer isOrder;

    /**
     * 排序
     */
    @TableField("order_num")
    private Integer orderNum;

    /**
     * 是否查询 0否1是
     */
    @TableField("is_search")
    private Integer isSearch;

    /**
     * 字典编码
     */
    @TableField("dict_code")
    private String dictCode;

    /**
     * 字典表
     */
    @TableField("dict_table")
    private String dictTable;

    /**
     * 字典text
     */
    @TableField("dict_text")
    private String dictText;

    /**
     * 字典value
     */
    @TableField("dict_value")
    private String dictValue;

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