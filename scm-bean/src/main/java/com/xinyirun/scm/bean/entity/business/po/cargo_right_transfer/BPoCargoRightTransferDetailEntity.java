package com.xinyirun.scm.bean.entity.business.po.cargo_right_transfer;

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
 * 货权转移明细表实体类
 * 
 * @author system
 * @since 2025-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_po_cargo_right_transfer_detail")
public class BPoCargoRightTransferDetailEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 9190020336370022071L;
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @DataChangeLabelAnnotation("主键ID")
    private Integer id;

    /**
     * 货权转移主表ID
     */
    @TableField("cargo_right_transfer_id")
    @DataChangeLabelAnnotation("货权转移主表ID")
    private Integer cargo_right_transfer_id;

    /**
     * 采购订单明细ID
     */
    @TableField("po_order_detail_id")
    @DataChangeLabelAnnotation("采购订单明细ID")
    private Integer po_order_detail_id;

    /**
     * 采购订单ID
     */
    @TableField("po_order_id")
    @DataChangeLabelAnnotation("采购订单ID")
    private Integer po_order_id;

    /**
     * 采购订单号
     */
    @TableField("po_order_code")
    @DataChangeLabelAnnotation("采购订单号")
    private String po_order_code;

    /**
     * 商品ID
     */
    @TableField("goods_id")
    @DataChangeLabelAnnotation("商品ID")
    private Integer goods_id;

    /**
     * 商品编码
     */
    @TableField("goods_code")
    @DataChangeLabelAnnotation("商品编码")
    private String goods_code;

    /**
     * 商品名称
     */
    @TableField("goods_name")
    @DataChangeLabelAnnotation("商品名称")
    private String goods_name;

    /**
     * SKU ID
     */
    @TableField("sku_id")
    @DataChangeLabelAnnotation("SKU ID")
    private Integer sku_id;

    /**
     * SKU编码
     */
    @TableField("sku_code")
    @DataChangeLabelAnnotation("SKU编码")
    private String sku_code;

    /**
     * SKU名称
     */
    @TableField("sku_name")
    @DataChangeLabelAnnotation("SKU名称")
    private String sku_name;

    /**
     * 单位ID
     */
    @TableField("unit_id")
    @DataChangeLabelAnnotation("单位ID")
    private Integer unit_id;

    /**
     * 产地
     */
    @TableField("origin")
    @DataChangeLabelAnnotation("产地")
    private String origin;

    /**
     * 订单数量
     */
    @TableField("order_qty")
    @DataChangeLabelAnnotation("订单数量")
    private BigDecimal order_qty;

    /**
     * 订单单价
     */
    @TableField("order_price")
    @DataChangeLabelAnnotation("订单单价")
    private BigDecimal order_price;

    /**
     * 订单金额
     */
    @TableField("order_amount")
    @DataChangeLabelAnnotation("订单金额")
    private BigDecimal order_amount;

    /**
     * 本次转移数量
     */
    @TableField("transfer_qty")
    @DataChangeLabelAnnotation("本次转移数量")
    private BigDecimal transfer_qty;

    /**
     * 转移单价
     */
    @TableField("transfer_price")
    @DataChangeLabelAnnotation("转移单价")
    private BigDecimal transfer_price;

    /**
     * 转移金额
     */
    @TableField("transfer_amount")
    @DataChangeLabelAnnotation("转移金额")
    private BigDecimal transfer_amount;

    /**
     * 质量状态(1-合格,2-不合格,3-待检)
     */
    @TableField("quality_status")
    @DataChangeLabelAnnotation("质量状态")
    private String quality_status;

    /**
     * 批次号
     */
    @TableField("batch_no")
    @DataChangeLabelAnnotation("批次号")
    private String batch_no;

    /**
     * 生产日期
     */
    @TableField("production_date")
    @DataChangeLabelAnnotation("生产日期")
    private LocalDateTime production_date;

    /**
     * 有效期
     */
    @TableField("expiry_date")
    @DataChangeLabelAnnotation("有效期")
    private Integer expiry_date;

    /**
     * 备注
     */
    @TableField("remark")
    @DataChangeLabelAnnotation("备注")
    private String remark;

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