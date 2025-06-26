package com.xinyirun.scm.bean.entity.mongo.monitor.v2;

import com.xinyirun.scm.bean.entity.mongo2mysql.monitor.v2.*;
import com.xinyirun.scm.bean.system.vo.mongo.monitor.v2.BMonitorDataDetailMongoV2Vo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Wang Qianfeng
 * @Description TODO
 * @date 2023/2/10 10:14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Document(collection = "monitor_v2")
public class BMonitorDataMongoEntity implements Serializable {

    private static final long serialVersionUID = 6083827692808410166L;

    /**
     * 主键id
     */
    @Id
    private String id;

    /**
     * 任务单号
     */
    private String code;

    /**
     * 状态:0空车过磅，1正在装货，2重车出库，3装货完成,4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    private String status_name;

    /**
     * 状态:0空车过磅，1正在装货，2重车出库，3装货完成,4重车过磅，5正在卸货，6空车出库，7卸货完成
     */
    private String status;

    /**
     * 审核状态: 0待审核 1审核驳回 2审核通过
     */
    private String audit_status_name;

    /**
     * 监管任务_审核状态: 0待审核 1审核驳回 2审核通过 3出库审核通过 4入库审核通过
     */
    private String audit_status;

    /**
     * 结算状态: 0未结算 1已结算
     */
    private String settlement_status_name;

    /**
     * 结算状态: 0未结算 1已结算
     */
    private String settlement_status;

    /**
     * 物流订单单号
     */
    private String schedule_code;

    /**
     * 物流订单type
     */
    private String schedule_type;

    /**
     * 物流订单ID
     */
    private Integer schedule_id;

    /**
     * 合同号
     */
    private String contract_no;

    /**
     * 出库计划单号
     */
    private String out_plan_code;

    /**
     * 入库计划单号
     */
    private String in_plan_code;

    /**
     * 运单号
     */
    private String waybill_code;

    /**
     * 物流合同号
     */
    private String waybill_contract_no;

    /**
     * 监管任务_出库类型
     */
    private String out_type_name;

    /**
     * 装货简称仓库
     */
    private String out_warehouse_short_name;

    /**
     * 装货简称仓库
     */
    private String out_warehouse_name;


    /**
     * 装货简称仓库
     */
    private Integer out_warehouse_id;

    /**
     * 收货仓库类型
     */
    private String out_warehouse_type_name;

    /**
     * 收货仓库类型
     */
    private String out_warehouse_type;

    /**
     * 装货仓库地址
     */
    private String out_warehouse_address;

    /**
     * 出库/提货时间
     */
    private LocalDateTime out_time;

    /**
     * 监管任务_入库类型
     */
    private String in_type_name;

    /**
     * 收货地
     */
    private String in_warehouse_short_name;

    /**
     * 收货地
     */
    private String in_warehouse_name;

    /**
     * 收货地
     */
    private Integer in_warehouse_id;

    /**
     * 卸货仓库类型
     */
    private String in_warehouse_type_name;

    /**
     * 卸货仓库类型
     */
    private String in_warehouse_type;

    /**
     * 卸货仓库地址
     */
    private String in_warehouse_address;

    /**
     *入库/卸货时间
     */
    private LocalDateTime in_time;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 物料编号
     */
    private String goods_code;

    /**
     * 品名
     */
    private String pm;

    /**
     * 规格名称
     */
    private String sku_name;

    /**
     * 规格编号
     */
    private String sku_code;

    /**
     * 承运商名称
     */
    private String customer_name;

    /**
     * 司机名称
     */
    private String driver_name;

    /**
     * 车牌号
     */
    private String vehicle_no;

    /**
     * 验车状态
     */
    private String validate_vehicle;

    /**
     * 发货数量
     */
    private BigDecimal out_qty;

    /**
     * 收货数量
     */
    private BigDecimal in_qty;

    /**
     * 损耗
     */
    private BigDecimal qty_loss;

    /**
     * 退货数量
     */
    private BigDecimal return_qty;

    /**
     * 是否有退货 0否 1是
     */
    private Integer if_return_qty;

    /**
     * 是否有退货
     */
    private String if_return_qty_name;

    /**
     * 承运商code
     */
    private String customer_code;

    /**
     * 作废备注
     */
    private String cancel_remark;

    /**
     * 出库审核人
     */
    private String out_audit_name;

    /**
     * 出库审核时间
     */
    private LocalDateTime out_audit_time;

    /**
     * 入库审核人
     */
    private String in_audit_name;

    /**
     * 入库审核时间
     */
    private LocalDateTime in_audit_time;

    /**
     * 创建人名
     */
    private String c_name;

    /**
     * 修改人名
     */
    private String u_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 详情
     */
    private BMonitorDataDetailMongoV2Vo detailVo;

    /**
     * 监管任务审核时间, 年
     */
    private Integer audit_year;

    /**
     * 监管任务审核时间, 月
     */
    private Integer audit_month;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

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
     * 司机id
     */
    private Integer driver_id;

    /**
     * 司机code
     */
    private String driver_code;

    /**
     * 司机手机号
     */
    private String driver_mobile_phone;

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
     * 物流订单创建时间
     */
//    private LocalDateTime schedule_time;

    /**
     * 监管单创建时间
     */
//    private LocalDateTime monitor_time;

    /**
     * 监管单完成时间
     */
//    private LocalDateTime monitor_finish_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 监管任务_入库id
     */
    private Integer monitor_in_id_bk;

    /**
     * 监管任务_出库id
     */
    private Integer monitor_out_id_bk;

    /**
     * 监管任务_出库id
     */
    private Integer monitor_delivery_id_bk;

    /**
     * 监管任务_出库id
     */
    private Integer monitor_unload_id_bk;

    private Integer monitor_id;

    private BMonitorRestoreV2Entity monitor_json;

    private BMonitorInRestoreV2Entity monitor_in_json;

    private BMonitorOutRestoreV2Entity monitor_out_json;

    private BMonitorDeliveryRestoreV2Entity monitor_delivery_json;

    private BMonitorUnloadRestoreV2Entity monitor_unload_json;

    private BReturnRelationRestoreV2Entity return_relation_json;

    /**
     * 是否恢复, 0否, 1是
     */
    private String is_restore;

    /**
     * 是否恢复, 0不可见, 其他可见
     */
    private String is_show;

    /**
     * 是否同步
     */
    private String is_sync;

    /**
     * 是否同步 名称
     */
    private String is_sync_name;

}
