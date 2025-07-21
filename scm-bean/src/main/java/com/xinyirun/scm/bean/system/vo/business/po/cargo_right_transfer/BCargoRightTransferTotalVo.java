package com.xinyirun.scm.bean.system.vo.business.po.cargo_right_transfer;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 货权转移汇总表VO类
 * 
 * @author system
 * @since 2025-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BCargoRightTransferTotalVo implements Serializable {


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
     * 转移总数量
     */
    private BigDecimal qty_total;

    /**
     * 转移总重量
     */
    private BigDecimal weight_total;

    /**
     * 转移总体积
     */
    private BigDecimal volume_total;

    /**
     * 转移总金额
     */
    private BigDecimal amount_total;

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