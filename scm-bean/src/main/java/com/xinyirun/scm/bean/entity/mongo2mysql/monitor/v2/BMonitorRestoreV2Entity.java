package com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v2;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 监管任务， 旨在从mongo恢复数据到mysql， 不更新 c_time, u_time, dbversion 等字段
 * </p>
 *
 * @author htt
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_monitor")
public class BMonitorRestoreV2Entity implements Serializable {


    private static final long serialVersionUID = 4651787983696998009L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 调度单id
     */
    @TableField("schedule_id")
    private Integer schedule_id;

    /**
     * 任务单号
     */
    @TableField("code")
    private String code;

    /**
     * 监管任务_状态：0空车过磅，1正在装货，2重车出库，3装货完成 4重车过磅，5正在卸货，6空车出库，7卸货完成（完成），8作废
     */
    @TableField("status")
    private String status;

    /**
     * 结算状态: 1 已结算
     */
    @TableField("settlement_status")
    private String settlement_status;

    /**
     * 监管任务_审核状态: 0待审核 1审核驳回 2审核通过 3出库审核通过 4入库审核通过
     */
    @TableField("audit_status")
    private String audit_status;


    /**
     * 司机id
     */
    @TableField("driver_id")
    private Integer driver_id;

    /**
     * 司机code
     */
    @TableField("driver_code")
    private String driver_code;

    /**
     * 车辆id
     */
    @TableField("vehicle_id")
    private Integer vehicle_id;

    /**
     * 车辆code
     */
    @TableField("vehicle_code")
    private String vehicle_code;

    /**
     * 集装箱id
     */
    @TableField("container_id")
    private Integer container_id;

    /**
     * 集装箱code
     */
//    @TableField("container_code")
//    private String container_code;

    /**
     * 承运商id
     */
    @TableField("customer_id")
    private Integer customer_id;

    /**
     * 承运商code
     */
    @TableField("customer_code")
    private String customer_code;

    /**
     * 运单号
     */
    @TableField("waybill_code")
    private String waybill_code;

    /**
     * 轨迹查询开始时间：重车出库
     */
    @TableField("track_start_time")
    private LocalDateTime track_start_time;

    /**
     * 轨迹查询结束时间：重车入库
     */
    @TableField("track_end_time")
    private LocalDateTime track_end_time;

    /**
     * 物流订单创建时间
     */
    @TableField("schedule_time")
    private LocalDateTime schedule_time;

    /**
     * 监管单创建时间
     */
    @TableField("monitor_time")
    private LocalDateTime monitor_time;

    /**
     * 出库/提货时间
     */
    @TableField("out_time")
    private LocalDateTime out_time;

    /**
     * 入库/卸货时间
     */
    @TableField("in_time")
    private LocalDateTime in_time;

    /**
     * 验车状态 0未通过 1通过
     */
    @TableField("validate_vehicle")
    private Boolean validate_vehicle;

    /**
     * 验车时间
     */
    @TableField("validate_time")
    private LocalDateTime validate_time;

    /**
     * 最后定位时间
     */
    @TableField("gps_time")
    private LocalDateTime gps_time;

    /**
     * 验车状态获取类型0-APP 1-PC
     */
    @TableField("validate_vehicle_type")
    private Integer validate_vehicle_type;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

    /**
     * 创建时间
     */
    @TableField(value="c_time")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id")
    private Long u_id;

    /**
     * 入库审核人
     */
    @TableField(value="in_audit_id")
    private Long in_audit_id;

    /**
     * 入库审核时间
     */
    @TableField(value="in_audit_time")
    private LocalDateTime in_audit_time;

    /**
     * 出库审核人
     */
    @TableField(value="out_audit_id")
    private Long out_audit_id;


    /**
     * 出库审核时间
     */
    @TableField(value="out_audit_time")
    private LocalDateTime out_audit_time;

    /**
     * 空车过磅创建时间
     */
    @TableField(value="out_empty_time")
    private LocalDateTime out_empty_time;

    /**
     * 正在装货创建时间
     */
    @TableField(value="out_loading_time")
    private LocalDateTime out_loading_time;

    /**
     * 重车出库创建时间
     */
    @TableField(value="out_heavy_time")
    private LocalDateTime out_heavy_time;

    /**
     * 重车过磅创建时间
     */
    @TableField(value="in_heavy_time")
    private LocalDateTime in_heavy_time;

    /**
     * 正在卸货创建时间
     */
    @TableField(value="in_unloading_time")
    private LocalDateTime in_unloading_time;

    /**
     * 空车出库创建时间
     */
    @TableField(value="in_empty_time")
    private LocalDateTime in_empty_time;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否可以同步
     */
    @TableField("is_sync")
    private String is_sync;

    /**
     * 同步ID
     */
    @TableField("sync_id")
    private String sync_id;

    /**
     * 轨迹日志
     */
    @TableField("track_log")
    private String track_log;

    /**
     * 验车日志
     */
    @TableField("validate_log")
    private String validate_log;
}
