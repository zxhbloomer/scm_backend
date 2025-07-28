package com.xinyirun.scm.bean.entity.business.bkmonitor;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * monitor 备份 同步信息表
 * </p>
 *
 * @author xinyirun
 * @since 2023-04-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_bk_monitor_sync_log")
public class BBkMonitorSyncLogEntity implements Serializable {

    private static final long serialVersionUID = -7430197687471434504L;

    /**
     * 主键id
     */
    @TableId("id")
    private Integer id;

    /**
     * 监管任务id
     */
    @TableField("monitor_id")
    private Integer monitor_id;

    /**
     * 监管任务编码
     */
    @TableField("monitor_code")
    private String monitor_code;

    /**
     * 当前状态, 1.待备份/待恢复, 2备份中/恢复中(没用到),3.备份完成 /恢复完成,  根据 type 字段决定
     */
    @TableField("status")
    private String status;

    /**
     * 结果, ing, ok, ng
     */
    @TableField("flag")
    private String flag;

    /**
     * 错误日志
     */
    @TableField("exception")
    private String exception;

    /**
     * 当前类型, 1备份, 2恢复
     */
    @TableField("type")
    private String type;

    /**
     * 最后一次备份人
     */
    @TableField(value="last_backup_id")
    private Long last_backup_id;

    /**
     * 最后一次备份时间
     */
    @TableField(value="last_backup_time")
    private LocalDateTime last_backup_time;

    /**
     * 最后一次恢复人
     */
    @TableField(value="last_restore_id")
    private Long last_restore_id;

    /**
     * 最后一次恢复时间
     */
    @TableField(value="last_restore_time")
    private LocalDateTime last_restore_time;


    /**
     * 更新时间, 用于排序
     */
    @TableField(value="u_time")
    private LocalDateTime u_time;

    /**
     * 版本
     */
    @TableField("version")
    private String version;
}
