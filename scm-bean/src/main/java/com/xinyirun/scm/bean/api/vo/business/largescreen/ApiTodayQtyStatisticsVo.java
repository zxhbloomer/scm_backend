package com.xinyirun.scm.bean.api.vo.business.largescreen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 采购销售合同量
 *
 * @Author: wangqianfeng
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiTodayQtyStatisticsVo implements Serializable {

    private static final long serialVersionUID = -8031122204743531882L;

    /**
     * 发货数量
     */
    private BigDecimal qty_out_today = BigDecimal.ZERO;

    /**
     * 收货数量
     */
    private BigDecimal qty_in_today = BigDecimal.ZERO;

    /**
     * 在途数量
     */
    private BigDecimal qty_in_transit_today = BigDecimal.ZERO;

    /**
     * 生产数量
     */
    private BigDecimal qty_product_today = BigDecimal.ZERO;

    /**
     * 发车数量
     */
    private Integer monitor_count = 0;

    /**
     * 采购订单数量
     */
    private Integer purchase_order_count;

    /**
     * 销售订单数量
     */
    private Integer sales_order_count;

    /**
     * 当天拍卖数量
     */
    private BigDecimal purchase_contract_num;

    /**
     * 原粮数量
     */
    private BigDecimal row_grain_count;

    /**
     * 当天销售数量
     */
    private BigDecimal sales_coontract_num;

    /**
     * 当天交付数量
     */
    private BigDecimal deliver_num;
}
