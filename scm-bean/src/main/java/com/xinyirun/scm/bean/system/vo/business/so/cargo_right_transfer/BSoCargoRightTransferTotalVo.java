package com.xinyirun.scm.bean.system.vo.business.so.cargo_right_transfer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售货权转移汇总表VO类
 * 
 * @author system
 * @since 2025-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BSoCargoRightTransferTotalVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -2861801175298860216L;

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 货权转移主表ID
     */
    private Integer cargo_right_transfer_id;

    /**
     * 销售订单ID
     */
    private Integer so_order_id;

    /**
     * 货权转移-未处理中数量
     */
    private BigDecimal cargo_right_untransfer_qty_total;

    /**
     * 货权转移-处理中数量
     */
    private BigDecimal cargo_right_transfering_qty_total;

    /**
     * 货权转移-已处理移数量
     */
    private BigDecimal cargo_right_transferred_qty_total;

    /**
     * 货权转移-已作废数量
     */
    private BigDecimal cargo_right_transfer_cancel_qty_total;

    /**
     * 转移总数量
     */
    private BigDecimal transfer_qty_total;

    /**
     * 转移总金额
     */
    private BigDecimal transfer_amount_total;

    /**
     * 订单总数量
     */
    private BigDecimal order_qty_total;

    /**
     * 订单总金额
     */
    private BigDecimal order_amount_total;

    // ========== 扩展字段 ==========

    /**
     * 转移商品种类数量
     */
    private Integer goodsTypeCount;

    /**
     * 转移SKU种类数量
     */
    private Integer skuTypeCount;

    /**
     * 平均单价
     */
    private BigDecimal avgPrice;

    /**
     * 转移完成率
     */
    private BigDecimal completionRate;
}