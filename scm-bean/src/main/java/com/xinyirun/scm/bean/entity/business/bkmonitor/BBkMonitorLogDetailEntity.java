package com.xinyirun.scm.bean.entity.business.bkmonitor;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * monitor 备份状态表
 *
 * @author xinyirun
 * @since 2023-03-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_bk_monitor_log_detail")
public class BBkMonitorLogDetailEntity implements Serializable {

    private static final long serialVersionUID = -6620722833792235871L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * b_monitor表id
     */
    @TableField("monitor_id")
    private Integer monitor_id;

    /**
     * b_monitor_delivery表id
     */
    @TableField("monitor_delivery_id")
    private Integer monitor_delivery_id;

    /**
     * b_monitor_in表id
     */
    @TableField("monitor_in_id")
    private Integer monitor_in_id;

    /**
     * b_monitor_out表id
     */
    @TableField("monitor_out_id")
    private Integer monitor_out_id;

    /**
     * b_monitor_unload表id
     */
    @TableField("monitor_unload_id")
    private Integer monitor_unload_id;

    /**
     * b_bk_monitor_log表id
     */
    @TableField("log_id")
    private Integer log_id;

    /**
     * 1 待备份状态,  2待删除, 3完成
     */
    @TableField("status")
    private String status;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField("exception")
    private String exception;

    /**
     * 消费状态, ing , ok , ng
     */
    @TableField("flag")
    private String flag;

    /**
     * 监管任务单号
     */
    @TableField("monitor_code")
    private String monitor_code;


}
