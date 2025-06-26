package com.xinyirun.scm.bean.api.vo.business.logistics;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 调度
 * </p>
 *
 * @author wwl
 * @since 2022-11-07
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBLogisticsOrderVo extends BaseVo implements Serializable {

    private static final long serialVersionUID = -3348589823717154418L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 调度单号
     */
    private String code;

    /**
     * 类型 1:物流订单 2:物流调度
     */
    private String type;

    /**
     * 单据状态
     */
    private String status;

    /**
     * 发货类型:0出库，1提货
     */
    private String out_type;

    /**
     * 收货类型:0入库，1卸货
     */
    private String in_type;

    /**
     * 物流订单运输方式：0公运、1铁运、2水运
     */
    private String transport_type;

    /**
     * 合同号
     */
    private String contract_no;

    /**
     * 单据状态
     */
    private String status_name;

    /**
     * 出库计划单号
     */
    private String out_plan_code;

    /**
     * 入库计划单号
     */
    private String in_plan_code;

    /**
     * 出库计划明细单号
     */
    private String out_plan_detail_code;

    /**
     * 入库计划明细单号
     */
    private String in_plan_detail_code;

    /**
     * 出库计划明细id
     */
    private Integer plan_detail_id;

    /**
     * 入库计划明细id
     */
    private Integer in_plan_detail_id;

    /**
     * 承运商id
     */
    private Integer customer_id;

    /**
     * 车辆id
     */
    private Integer vehicle_id;

    /**
     * 司机id
     */
    private Integer driver_id;

    /**
     * 司机code
     */
    private String driver_code;

    /**
     * 车辆code
     */
    private String vehicle_code;

    /**
     * 承运商code
     */
    private String customer_code;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 物料code
     */
    private String sku_code;

    /**
     * 订单id
     */
    private Integer order_id;

    /**
     * 订单code
     */
    private String order_code;

    /**
     * 运单号
     */
    private String waybill_code;

    /**
     * 发货仓库id
     */
    private Integer out_warehouse_id;

    /**
     * 发货库区id
     */
    private Integer out_location_id;

    /**
     * 发货库位id
     */
    private Integer out_bin_id;

    /**
     * 收货仓库id
     */
    private Integer in_warehouse_id;

    /**
     * 收货库区id
     */
    private Integer in_location_id;

    /**
     * 收货库位id
     */
    private Integer in_bin_id;

    /**
     * 计划出库数量
     */
    private BigDecimal out_schedule_qty;

    /**
     * 已出库数量
     */
    private BigDecimal out_operated_qty;

    /**
     * 待出库数量
     */
    private BigDecimal out_balance_qty;

    /**
     * 出库单位id
     */
    private Integer out_unit_id;

    /**
     * 出库单位
     */
    private Integer out_unit;

    /**
     * 计划入库数量
     */
    private BigDecimal in_schedule_qty;

    /**
     * 已入库数量
     */
    private BigDecimal in_operated_qty;

    /**
     * 待入库数量
     */
    private BigDecimal in_balance_qty;

    /**
     * 运输数量
     */
    private BigDecimal actual_count;

    /**
     * 入库单位id
     */
    private Integer in_unit_id;

    /**
     * 入库单位id
     */
    private Integer in_unit;

    /**
     * 发货人货主id
     */
    private Integer out_owner_id;

    /**
     * 发货人货主code
     */
    private String out_owner_code;

    /**
     * 发货人货主
     */
    private String out_owner_name;

    /**
     * 收货人货主id
     */
    private Integer in_owner_id;

    /**
     * 收货人货主code
     */
    private String in_owner_code;

    /**
     * 收货人货主
     */
    private String in_owner_name;

    /**
     * 发货人委托方id
     */
    private Integer out_consignor_id;

    /**
     * 发货人委托方code
     */
    private String out_consignor_code;

    /**
     * 发货人委托方code
     */
    private String out_consignor_name;

    /**
     * 收货人委托方id
     */
    private Integer in_consignor_id;

    /**
     * 收货人委托方code
     */
    private String in_consignor_code;

    /**
     * 收货人委托方code
     */
    private String in_consignor_name;

    /**
     * 装货仓库
     */
    private String out_warehouse_name;

    /**
     * 装货仓库地址
     */
    private String out_warehouse_address;

    /**
     * 卸货仓库
     */
    private String in_warehouse_name;

    /**
     * 卸货仓库地址
     */
    private String in_warehouse_address;

    /**
     * 业务类型
     */
    private String serial_type_name;

    /**
     * 订单号
     */
    private String order_no;

    /**
     * 单据类型
     */
    private String bill_type;

    /**
     * 单据类型
     */
    private String bill_type_name;

    /**
     * 合同日期
     */
    private LocalDateTime contract_dt;

    /**
     * 船名
     */
    private String ship_name;

    /**
     * 客户/供应商名称
     */
    private String order_customer_name;

    /**
     * 采购/销售企业
     */
    private String owner_name;

    /**
     * 合同量
     */
    private BigDecimal contract_num;

    /**
     * 物料名称
     */
    private String goods_name;

    /**
     * 品名
     */
    private String pm;

    /**
     * 规格
     */
    private String spec;

    /**
     * 出库计划生成方式 0 系统生成 1 手动选择
     */
    private String out_rule;

    /**
     * 入库计划生成方式 0 系统生成 1 手动选择
     */
    private String in_rule;

    /**
     * 出库计划明细id
     */
    private Integer out_plan_detail_id;

    /**
     * 已调度单量
     */
    private Integer monitor_count;

    /**
     * 创建人名
     */
    private String c_name;

    /**
     * 修改人名
     */
    private String u_name;

    /**
     * 超发数量
     */
    private BigDecimal out_over_qty;

    /**
     * 超收数量
     */
    private BigDecimal in_over_qty;

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
     * 已发数量
     */
    private BigDecimal out_qty;

    /**
     * 已收数量
     */
    private BigDecimal in_qty;

    /**
     * 待发数量
     */
    private BigDecimal out_balance;

    /**
     * 待收数量
     */
    private BigDecimal in_balance;

    /**
     * 换页条件
     */
    private PageCondition pageCondition;

    /**
     * 开始时间
     */
    private LocalDateTime start_time;

    /**
     * 结束时间
     */
    private LocalDateTime over_time;

    /**
     * 物流订单id
     */
    private Integer schedule_id;

    /**
     * 物流合同号
     */
    private String waybill_contract_no;

    /**
     * 承运商名称
     */
    private String customer_name;

    /**
     * 收货执行情况
     */
    private BigDecimal in_rate;

    /**
     * 发货执行情况
     */
    private BigDecimal out_rate;

    /**
     * 在途数量
     */
    private BigDecimal in_transit;

}
