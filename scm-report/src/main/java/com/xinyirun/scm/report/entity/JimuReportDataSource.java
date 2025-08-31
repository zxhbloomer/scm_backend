package com.xinyirun.scm.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积木报表数据源表
 * 存储报表使用的数据源连接信息
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Data
@TableName("jimu_report_data_source")
public class JimuReportDataSource implements Serializable {


    @Serial
    private static final long serialVersionUID = 5607270934709163868L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 数据源编码
     */
    @TableField("code")
    private String code;
    
    /**
     * 数据源名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 备注
     */
    @TableField("remark")
    private String remark;
    
    /**
     * 数据库类型
     */
    @TableField("db_type")
    private String dbType;
    
    /**
     * 驱动类
     */
    @TableField("db_driver")
    private String dbDriver;
    
    /**
     * 数据源地址
     */
    @TableField("db_url")
    private String dbUrl;
    
    /**
     * 用户名
     */
    @TableField("db_username")
    private String dbUsername;
    
    /**
     * 密码
     */
    @TableField("db_password")
    private String dbPassword;
    
    /**
     * 连接池类型
     */
    @TableField("db_pool_name")
    private String dbPoolName;
    
    /**
     * 创建人
     */
    @TableField("create_by")
    private String createBy;
    
    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
    
    /**
     * 更新人
     */
    @TableField("update_by")
    private String updateBy;
    
    /**
     * 更新时间
     */
    @TableField("update_time")
    private LocalDateTime updateTime;
    
    /**
     * 删除标志：0-正常，1-删除
     */
    @TableField("del_flag")
    @TableLogic
    private Integer delFlag;
}