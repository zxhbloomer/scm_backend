package com.xinyirun.scm.bean.entity.busniess.soorder;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 采购订单明细表-商品
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_order_detail")
public class BSoOrderDetailEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 898486526061329867L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 销售订单主表ID
     */
    @TableField("so_order_id")
    private Integer so_order_id;

    /**
     * 物料编码、商品编码
     */
    @TableField("sku_code")
    private String sku_code;

    /**
     * 物料名称、商品名称
     */
    @TableField("sku_name")
    private String sku_name;

    /**
     * 物料ID、商品ID
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 单位ID
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 规格
     */
    @TableField("spec")
    private String spec;

    /**
     * 产地
     */
    @TableField("origin")
    private String origin;

    /**
     * 数量
     */
    @TableField("qty")
    private BigDecimal qty;

    /**
     * 单价（含税）
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 总额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 税额
     */
    @TableField("tax_amount")
    private BigDecimal tax_amount;

    /**
     * 税率
     */
    @TableField("tax_rate")
    private BigDecimal tax_rate;


}
