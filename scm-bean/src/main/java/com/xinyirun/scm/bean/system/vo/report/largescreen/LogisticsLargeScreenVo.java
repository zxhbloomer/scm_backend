package com.xinyirun.scm.bean.system.vo.report.largescreen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @Author: Wqf
 * @Description: 加工稻谷入库 列表
 * @CreateTime : 2023/8/1 15:11
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogisticsLargeScreenVo implements Serializable {

    private static final long serialVersionUID = 6407365645665690190L;

    // 供应链

    /**
     * 原粮累计出库量
     */
    private BigDecimal raw_grain_out_qty;

    /**
     * 销售累计出库量
     */
    private BigDecimal purchase_out_qty;

    /**
     * 加工厂累计生产数量
     */
    private BigDecimal product_qty;

    /**
     * 加工厂累计生产杂质数量
     */
    private BigDecimal impurity_qty;

    /**
     * 加工厂累计生产稻壳数量
     */
    private BigDecimal rice_husk_qty;

    /**
     * 饲料厂收货数量
     */
    private BigDecimal feed_in_qty;

//     车次
    /**
     * 累计派车次数
     */
    private int monitor_total;

    /**
     * 在途车次数量
     */
    private int in_transit_monitor;

    /**
     * 累计完成车次
     */
    private int complete_monitor;

    /**
     * 当月派车次数
     */
    private int month_monitor;

    /**
     * 异常车次
     */
    private int unusual_monitor;

    /**
     * 当月完成车次
     */
    private int month_complete_monitor;

    // 物流合同
    /**
     * 累计运输合同量
     */
    private BigDecimal carriage_num;

    /**
     * 待运合同量
     */
    private BigDecimal predict_carriage_num;

    /**
     * 累计完成量
     */
    private BigDecimal complete_carriage_num;

    /**
     * 累计损耗数量
     */
    private BigDecimal loss_carriage_num;

    /**
     * 在途数量
     */
    private BigDecimal in_transit_carriage_num;

    /**
     * 合同完成进度
     */
    private BigDecimal carriage_complete_processing;

    /**
     * 承运商数量
     */
    private int customer_count;

    /**
     * 合同逾期数量
     */
    private int contract_over_due_count;

    // 当月数据
    /**
     * 完成运输量
     */
    private BigDecimal month_complete_qty;

    /**
     * 损耗
     */
    private BigDecimal month_loss_qty;

    /**
     * 饲料厂收货量
     */
    private BigDecimal month_feed_in_qty;

    /**
     * 加工厂生产量
     */
    private BigDecimal month_product_qty;

    /**
     * 原粮出库数量
     */
    private BigDecimal month_raw_grain_qty;

    /**
     * 销售出库量
     */
    private BigDecimal month_sales_out_qty;

    // 当天数据
    /**
     * 派车数
     */
    private int daily_monitor_count;

    /**
     * 销售出库数量
     */
    private BigDecimal daily_sales_out_qty;

    /**
     * 完成车次
     */
    private int daily_complete_monitor_count;

    /**
     * 生产数量
     */
    private BigDecimal daily_product_qty;

    /**
     * 在途车次
     */
    private int daily_in_transit_monitor_count;

    /**
     * 收货数量
     */
    private BigDecimal daily_in_qty;


    /**
     *业务员起止日期
     */
    private String batch_date;
}
