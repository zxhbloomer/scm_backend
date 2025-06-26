package com.xinyirun.scm.bean.system.vo.business.monitor;

import com.xinyirun.scm.bean.system.ao.fs.UploadFileResultAo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * <p>
 * 监管任务
 * </p>
 *
 * @author wwl
 * @since 2021-09-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BCarriageOrderDetailVo extends UploadFileResultAo implements Serializable {

    private static final long serialVersionUID = 1444516582442094485L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 上一条id
     */
    private Integer prev_id;

    /**
     * 吓一条id
     */
    private Integer next_id;

    /**
     * 行号
     */
    private Integer no;

    /**
     * 调度单id
     */
    private Integer schedule_id;

    /**
     * 监管任务_入库id
     */
    private Integer monitor_in_id;

    /**
     * 监管任务_出库id
     */
    private Integer monitor_out_id;

    /**
     * 监管任务_入库类型
     */
    private String in_type;

    /**
     * 监管任务_出库类型
     */
    private String out_type;

    /**
     * 监管任务_入库类型
     */
    private String in_type_name;
    /**
     * 监管任务_出库类型
     */
    private String out_type_name;


    /**
     * 状态:0空车过磅，1正在装货，2重车出库，3装货完成,4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    private String status;

    /**
     * 状态:0空车过磅，1正在装货，2重车出库，3装货完成,4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    private String[] status_list;

    /**
     * 状态:0空车过磅，1正在装货，2重车出库，3装货完成,4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    private String status_name;

    /**
     * 结算状态: 0未结算 1已结算
     */
    private String settlement_status_name;

    /**
     * 结算状态: 0未结算 1已结算
     */
    private String settlement_status;

    /**
     * 审核状态: 0待审核 1审核驳回 2审核通过
     */
    private String audit_status_name;

    /**
     * 监管任务_审核状态: 0待审核 1审核驳回 2审核通过 3出库审核通过 4入库审核通过
     */
    private String audit_status;
    private String[] audit_status_list;

    /**
     * 任务单号
     */
    private String code;

    /**
     * 司机id
     */
    private Integer driver_id;

    /**
     * 司机code
     */
    private String driver_code;

    /**
     * 车辆id
     */
    private Integer vehicle_id;

    /**
     * 车辆code
     */
    private String vehicle_code;

    /**
     * 承运商id
     */
    private Integer customer_id;

    /**
     * 承运商code
     */
    private String customer_code;

    /**
     * 运单号
     */
    private String waybill_code;

    /**
     * 运单号
     */
    private BigDecimal in_qty;

    /**
     * 运单号
     */
    private BigDecimal out_qty;

    /**
     * 损耗
     */
    private BigDecimal qty_loss;

    /**
     * 规格名称
     */
    private String sku_name;

    /**
     * 规格编号
     */
    private String sku_code;

    /**
     * 品名
     */
    private String pm;

    /**
     * 物流订单创建时间
     */
    private LocalDateTime schedule_time;

    /**
     * 监管单创建时间
     */
    private LocalDateTime monitor_time;

    /**
     * 监管单完成时间
     */
    private LocalDateTime monitor_finish_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 物流订单单号
     */
    private String schedule_code;

    /**
     * 出库计划单号
     */
    private String out_plan_code;

    /**
     * 入库计划单号
     */
    private String in_plan_code;

    /**
     * 装货仓库
     */
    private String out_warehouse_name;

    /**
     * 收货仓库类型
     */
    private String out_warehouse_type_name;

    /**
     * 收货仓库类型
     */
    private String[] out_warehouse_types;

    /**
     * 装货仓库地址
     */
    private String out_warehouse_address;

    /**
     * 卸货仓库
     */
    private String in_warehouse_name;

    /**
     * 卸货仓库类型
     */
    private String in_warehouse_type_name;

    /**
     * 卸货仓库类型
     */
    private String[] in_warehouse_types;

    /**
     * 卸货仓库地址
     */
    private String in_warehouse_address;

    /**
     * 合同号
     */
    private String contract_no;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 承运商名称
     */
    private String customer_name;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 司机名称
     */
    private String driver_name;

    /**
     * 司机手机号
     */
    private String driver_mobile_phone;

    /**
     * 创建人名
     */
    private String c_name;

    /**
     * 修改人名
     */
    private String u_name;

    /**
     * 出库/提货时间
     */
    private LocalDateTime out_time;

    /**
     *入库/卸货时间
     */
    private LocalDateTime in_time;

    /**
     * 监管任务_出库对象
     */
    private BMonitorOutDeliveryVo monitorOutVo;

    /**
     * 监管任务_入库对象
     */
    private BMonitorInUnloadVo monitorInVo;

    /**
     * 综合查询字段：物流订单号、发货仓库、收货仓库、司机、车牌号
     */
    private String combine_search_condition;

    /**
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    private LocalDateTime over_time;

    /**
     * 出库单号
     */
    private String out_code;

    /**
     * 入库单号
     */
    private String in_code;

    /**
     * 出库单状态
     */
    private String out_status_name;

    /**
     * 入库单状态
     */
    private String in_status_name;

    /**
     * 出库单状态
     */
    private String out_status;

    /**
     * 入库单状态
     */
    private String in_status;

    /**
     * 作废备注
     */
    private String remark;

    /**
     * 作废备注
     */
    private String cancel_remark;

    /**
     * 轨迹查询开始时间：重车出库
     */
    private LocalDateTime track_start_time;

    /**
     * 轨迹查询结束时间：重车入库
     */
    private LocalDateTime track_end_time;

    /**
     * 验车状态
     */
    private String validate_vehicle;

    /**
     * 验车时间
     */
    private LocalDateTime validate_time;

    /**
     * 定位时间
     */
    private LocalDateTime gps_time;

    /**
     * 排序sql
     */
    private String sort_sql;

    private List<BPreviewDataVo> preview_data;

    /**
     * 入库审核人
     */
    private Long in_audit_id;
    private String in_audit_name;

    /**
     * 入库审核时间
     */
    private LocalDateTime in_audit_time;

    /**
     * 出库审核人
     */
    private Long out_audit_id;
    private String out_audit_name;

    /**
     * 出库审核时间
     */
    private LocalDateTime out_audit_time;

    /** 当前序号 */
    private long index;
    /** 当前页 */
    private long current;
    /**  每页显示条数*/
    private long size;

    /**
     * 完成时间开始
     */
    private LocalDateTime in_time_start;

    /**
     * 完成时间结束
     */
    private LocalDateTime in_time_end;

    private String un_status;

    /**
     * 物流合同号
     */
    private String waybill_contract_no;

    /**
     * 出库/入库审核时间起止
     */
    private LocalDateTime out_audit_start;
    private LocalDateTime out_audit_end;
    private LocalDateTime in_audit_start;
    private LocalDateTime in_audit_end;

    /**
     * 创建时间起止
     */
    private LocalDateTime c_time_start;
    private LocalDateTime c_time_end;

    /**
     * 业务启动日期
     */
    private String batch;

    /**
     * 备份状态
     */
    private String backup_status
            , backup_status_name;

    /**
     * 备份结果
     */
    private String backup_flag;

    /**
     * 1备份., 2 恢复
     */
    private String backup_type;

    /**
     * 数量
     */
    private Long count;

    /**
     * 箱号
     */
    private String container_code;

    /**
     * 箱号 ID
     */
    private Integer container_id;

    /**
     * 页签
     */
    private String active_tabs_index;

    /**
     * 导出 ids
     */
    private List<Integer> ids;
}
