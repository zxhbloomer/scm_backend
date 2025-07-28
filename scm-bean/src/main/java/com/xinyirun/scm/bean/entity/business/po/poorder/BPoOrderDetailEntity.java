package com.xinyirun.scm.bean.entity.business.po.poorder;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@TableName("b_po_order_detail")
public class BPoOrderDetailEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -4237689348868922052L;


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 采购订单主表ID
     */
    @TableField("po_order_id")
    private Integer po_order_id;

    /**
     * 商品id
     */
    @TableField("goods_id")
    private Integer goods_id;

    /**
     * 商品编号
     */
    @TableField("goods_code")
    @DataChangeLabelAnnotation("商品编号")
    private String goods_code;

    /**
     * 商品名称
     */
    @TableField("goods_name")
    @DataChangeLabelAnnotation("商品名称")
    private String goods_name;

    /**
     * 规格编号
     */
    @TableField("sku_code")
    @DataChangeLabelAnnotation("规格编号")
    private String sku_code;

    /**
     * 规格名称
     */
    @TableField("sku_name")
    @DataChangeLabelAnnotation("规格名称")
    private String sku_name;

    /**
     * 规格id
     */
    @TableField("sku_id")
    private Integer sku_id;

    /**
     * 单位ID
     */
    @TableField("unit_id")
    private Integer unit_id;

    /**
     * 产地
     */
    @TableField("origin")
    @DataChangeLabelAnnotation("产地")
    private String origin;

    /**
     * 数量
     */
    @TableField("qty")
    @DataChangeLabelAnnotation("数量")
    private BigDecimal qty;

    /**
     * 单价（含税）
     */
    @TableField("price")
    @DataChangeLabelAnnotation("单价")
    private BigDecimal price;

    /**
     * 总额
     */
    @TableField("amount")
    @DataChangeLabelAnnotation("总额")
    private BigDecimal amount;

    /**
     * 税额
     */
    @TableField("tax_amount")
    @DataChangeLabelAnnotation("税额")
    private BigDecimal tax_amount;

    /**
     * 税率
     */
    @TableField("tax_rate")
    @DataChangeLabelAnnotation("税率")
    private BigDecimal tax_rate;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建时间", extension = "getCTimeExtension")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改时间", extension = "getUTimeExtension")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人", extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;
}
