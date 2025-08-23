package com.xinyirun.scm.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积木报表主表
 * 存储报表的基本信息和设计JSON数据
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Data
@TableName("jimu_report")
public class JimuReport implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;
    
    /**
     * 报表编码
     */
    @TableField("code")
    private String code;
    
    /**
     * 报表名称
     */
    @TableField("name")
    private String name;
    
    /**
     * 报表描述
     */
    @TableField("note")
    private String note;
    
    /**
     * 报表状态：0-设计中，1-已发布
     */
    @TableField("status")
    private String status;
    
    /**
     * 报表类型
     */
    @TableField("type")
    private String type;
    
    /**
     * 报表设计JSON数据 - 核心字段
     */
    @TableField("json_str")
    private String jsonStr;
    
    /**
     * API接口地址
     */
    @TableField("api_url")
    private String apiUrl;
    
    /**
     * 缩略图
     */
    @TableField("thumb")
    private String thumb;
    
    /**
     * 模板标识：0-非模板，1-模板
     */
    @TableField("template")
    private Integer template;
    
    /**
     * 报表查询参数
     */
    @TableField("view_count")
    private Long viewCount;
    
    /**
     * CSS样式
     */
    @TableField("css_str")
    private String cssStr;
    
    /**
     * JS脚本
     */
    @TableField("js_str")
    private String jsStr;
    
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