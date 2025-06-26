package com.xinyirun.scm.bean.system.vo.business.soorder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @CreateTime : 2025/2/19 14:06
 */


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SoOrderDetailVo {

    /**
     * 物料编码、商品编码
     */
    private String sku_code;

    /**
     * 物料名称、商品名称
     */
    private String sku_name;

    /**
     * 物料ID、商品ID
     */
    private Integer sku_id;

    /**
     * 单位ID
     */
    private Integer unit_id;

    /**
     * 规格
     */
    private String spec;

    /**
     * 产地
     */
    private String origin;

    /**
     * 数量
     */
    private BigDecimal qty;

    /**
     * 单价（含税）
     */
    private BigDecimal price;

    /**
     * 总额
     */
    private BigDecimal amount;

    /**
     * 税额
     */
    private BigDecimal tax_amount;

    /**
     * 采购合同id
     */
    private Integer so_order_id;

    /**
     * 税率
     */
    private BigDecimal tax_rate;
}
