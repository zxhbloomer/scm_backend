package com.xinyirun.scm.report.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 积木报表分享配置实体
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Data
@TableName("jimu_report_share")
public class JimuReportShare implements Serializable {


    @Serial
    private static final long serialVersionUID = -4488905738696723630L;

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
     * 分享令牌
     */
    @TableField("share_token")
    private String shareToken;

    /**
     * 分享链接
     */
    @TableField("share_url")
    private String shareUrl;

    /**
     * 预览链接
     */
    @TableField("preview_url")
    private String previewUrl;

    /**
     * 预览锁定 0-不锁定 1-锁定
     */
    @TableField("preview_lock")
    private String previewLock;

    /**
     * 分享时间
     */
    @TableField("share_time")
    private LocalDateTime shareTime;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;

    /**
     * 是否过期 0-未过期 1-已过期
     */
    @TableField("is_expired")
    private Integer isExpired;

    /**
     * 状态 0-禁用 1-启用
     */
    @TableField("status")
    private String status;

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