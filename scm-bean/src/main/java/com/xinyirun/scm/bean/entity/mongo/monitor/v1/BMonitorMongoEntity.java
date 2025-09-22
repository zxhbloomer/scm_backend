//package com.xinyirun.scm.bean.entity.mongo.monitor.v1;
//
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//import lombok.experimental.Accessors;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.mongodb.core.mapping.Document;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
///**
// * <p>
// * 监管任务
// * </p>
// *
// * @author htt
// * @since 2021-09-23
// */
//@Data
//@EqualsAndHashCode(callSuper = false)
//@Accessors(chain = true)
//@Document("b_monitor")
//public class BMonitorMongoEntity implements Serializable {
//
//    private static final long serialVersionUID = -6870109437083519269L;
//
//    /**
//     * 主键id
//     */
//    @Id
//    private String id;
//
//    /**
//     * 数据库id
//     */
//    private Integer mysql_id;
//
//    /**
//     * 调度单id
//     */
//    private Integer schedule_id;
//
//    /**
//     * 任务单号
//     */
//    private String code;
//
//    /**
//     * 监管任务_状态：0空车过磅，1正在装货，2重车出库，3装货完成 4重车过磅，5正在卸货，6空车出库，7卸货完成（完成），8作废
//     */
//    private String status;
//
//    /**
//     * 结算状态: 1 已结算
//     */
//    private String settlement_status;
//
//    /**
//     * 审核状态: 1 审核驳回 2 审核通过
//     */
//    private String audit_status;
//
//
//    /**
//     * 司机id
//     */
//    private Integer driver_id;
//
//    /**
//     * 司机code
//     */
//    private String driver_code;
//
//    /**
//     * 车辆id
//     */
//    private Integer vehicle_id;
//
//    /**
//     * 车辆code
//     */
//    private String vehicle_code;
//
//    /**
//     * 承运商id
//     */
//    private Integer customer_id;
//
//    /**
//     * 承运商code
//     */
//    private String customer_code;
//
//    /**
//     * 运单号
//     */
//    private String waybill_code;
//
//    /**
//     * 轨迹查询开始时间：重车出库
//     */
//    private LocalDateTime track_start_time;
//
//    /**
//     * 轨迹查询结束时间：重车入库
//     */
//    private LocalDateTime track_end_time;
//
//    /**
//     * 物流订单创建时间
//     */
//    private LocalDateTime schedule_time;
//
//    /**
//     * 监管单创建时间
//     */
//    private LocalDateTime monitor_time;
//
//    /**
//     * 出库/提货时间
//     */
//    private LocalDateTime out_time;
//
//    /**
//     *入库/卸货时间
//     */
//    private LocalDateTime in_time;
//
//    /**
//     * 验车状态 0未通过 1通过
//     */
//    private Boolean validate_vehicle;
//
//    /**
//     * 验车时间
//     */
//    private LocalDateTime validate_time;
//
//    /**
//     * 最后定位时间
//     */
//    private LocalDateTime gps_time;
//
//    /**
//     * 验车状态获取类型0-APP 1-PC
//     */
//    private Integer validate_vehicle_type;
//
//    /**
//     * 数据版本，乐观锁使用
//     */
//    private Integer dbversion;
//
//    /**
//     * 创建时间
//     */
//    private LocalDateTime c_time;
//
//    /**
//     * 修改时间
//     */
//    private LocalDateTime u_time;
//
//    /**
//     * 创建人id
//     */
//    private Long c_id;
//
//    /**
//     * 修改人id
//     */
//    private Long u_id;
//
//    /**
//     * 入库审核人
//     */
//    private Long in_audit_id;
//
//    /**
//     * 入库审核时间
//     */
//    private LocalDateTime in_audit_time;
//
//    /**
//     * 出库审核人
//     */
//    private Long out_audit_id;
//
//
//    /**
//     * 出库审核时间
//     */
//    private LocalDateTime out_audit_time;
//
//    /**
//     * 空车过磅创建时间
//     */
//    private LocalDateTime out_empty_time;
//
//    /**
//     * 正在装货创建时间
//     */
//    private LocalDateTime out_loading_time;
//
//    /**
//     * 重车出库创建时间
//     */
//    private LocalDateTime out_heavy_time;
//
//    /**
//     * 重车过磅创建时间
//     */
//    private LocalDateTime in_heavy_time;
//
//    /**
//     * 正在卸货创建时间
//     */
//    private LocalDateTime in_unloading_time;
//
//    /**
//     * 空车出库创建时间
//     */
//    private LocalDateTime in_empty_time;
//
//    private Integer monitor_in_id;
//
//    private Integer monitor_out_id;
//
//    private Integer monitor_delivery_id;
//
//    private Integer monitor_unload_id;
//}
