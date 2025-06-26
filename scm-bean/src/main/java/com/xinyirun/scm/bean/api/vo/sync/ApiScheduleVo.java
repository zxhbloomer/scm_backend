package com.xinyirun.scm.bean.api.vo.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 物流订单
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiScheduleVo implements Serializable {

    private static final long serialVersionUID = -1264013049869342135L;

    /**
     * 物流订单单号
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
     * 物流订单运输方式：0公运、1铁运、2水运
     */
    private String transport_type;

    /**
     * 合同号
     */
    private String contract_code;

    /**
     * 订单号
     */
    private String order_code;

    /**
     * 发货类型:0出库，1提货
     */
    private String out_type;

    /**
     * 收货类型:0入库，1卸货
     */
    private String in_type;

    /**
     * 入库计划生成方式
     */
    private String in_rule;

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
    private Integer out_plan_detail_id;

    /**
     * 入库计划明细id
     */
    private Integer in_plan_detail_id;

    /**
     * 订单id
     */
    private Integer order_id;

    /**
     * 物料id
     */
    private Integer sku_id;

    /**
     * 物料code
     */
    private String sku_code;

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
     * 入库单位id
     */
    private Integer in_unit_id;

    /**
     * 委托方id-入
     */
    private Integer in_consignor_id;

    /**
     * 委托方code-入
     */
    private String in_consignor_code;

    /**
     * 货主id-入
     */
    private Integer in_owner_id;

    /**
     * 货主code-入
     */
    private String in_owner_code;

    /**
     * 详细地址-入
     */
    private String in_warehouse_address;

    /**
     * 委托方id-出
     */
    private Integer out_consignor_id;

    /**
     * 委托方code-出
     */
    private String out_consignor_code;

    /**
     * 货主id-出
     */
    private Integer out_owner_id;

    /**
     * 货主code-出
     */
    private String out_owner_code;

    /**
     * 详细地址-出
     */
    private String out_warehouse_address;

    /**
     * 出库计划生成方式 0 系统生成 1 手动选择
     */
    private String out_rule;

    /**
     * 创建时间
     */
    private String c_time;

    /**
     * 修改时间
     */
    private String u_time;



}
