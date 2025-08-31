package com.xinyirun.scm.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积木报表数据库连接实体
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Data
@TableName("jimu_report_db")
public class JimuReportDb implements Serializable {


    @Serial
    private static final long serialVersionUID = -6017179879165630856L;
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
     * 数据源ID
     */
    @TableField("data_source_id")
    private String dataSourceId;

    /**
     * 数据库标识
     */
    @TableField("db_key")
    private String dbKey;

    /**
     * 表名
     */
    @TableField("db_table_name")
    private String dbTableName;

    /**
     * 动态表名
     */
    @TableField("db_dynamic_table_name")
    private String dbDynamicTableName;

    /**
     * 主键类型
     */
    @TableField("db_key_type")
    private String dbKeyType;

    /**
     * 表说明
     */
    @TableField("db_table_txt")
    private String dbTableTxt;

    /**
     * 数据源
     */
    @TableField("db_source")
    private String dbSource;

    /**
     * SQL语句
     */
    @TableField("db_sql")
    private String dbSql;

    /**
     * 模板参数
     */
    @TableField("tpl_param")
    private String tplParam;

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