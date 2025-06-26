package com.xinyirun.scm.bean.api.vo.sync;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2022-12-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ApiBMonitorVo implements Serializable {

    private static final long serialVersionUID = -3321059633923412186L;

    /**
     *承运订单编号
     */
    String carriage_order_code;

    /**
     *承运合同编号
     */
    String carriage_contract_code;

    /**
     *承运订单.托运人编号
     */
    String org_code;

    /**
     *承运订单.托运人名称
     */
    String carrier_name;

    /**
     *承运订单.承运人编号
     */
    String carrier_code;

    /**
     *承运订单.承运人名称
     */
    String company_name;

    /**
     *承运订单.运输方式
     */
    String transport_type;

    /**
     *监管任务.车牌号
     */
    String vehicle_no;

    /**
     *监管任务.发货地仓库编号
     */
    String out_warehouse_code;

    /**
     *监管任务.发货地仓库名称
     */
    String out_warehouse_name;

    /**
     *监管任务.发货地仓库简称
     */
    String out_warehouse_simple_name;

    /**
     *监管任务.收货地仓库编号
     */
    String in_warehouse_code;

    /**
     *监管任务.收货地仓库名称
     */
    String in_warehouse_name;

    /**
     *监管任务.收货地仓库简称
     */
    String in_warehouse_simple_name;

    /**
     *监管任务.商品编号
     */
    String goods_code;

    /**
     *监管任务.商品名称
     */
    String goods_name;

    /**
     *监管任务.商品规格编号
     */
    String sku_code;

    /**
     *监管任务.商品规格名称
     */
    String sku_name;

    /**
     *监管任务.发货数量
     */
    String qty_out;

    /**
     *监管任务.收货数量
     */
    String qty_in;

    /**
     *监管任务.损耗
     */
    String qty_loss;

    /**
     *监管任务.状态编码
     */
    String status;

    /**
     *监管任务.状态名称
     */
    String status_name;

    /**
     *监管任务.审核状态编码
     */
    String audit_status;

    /**
     *监管任务.审核状态名称
     */
    String audit_status_name;

}
