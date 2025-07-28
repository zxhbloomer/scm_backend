package com.xinyirun.scm.bean.entity.business.so.cargo_right_transfer;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售货权转移汇总表实体类
 * 
 * @author system
 * @since 2025-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_cargo_right_transfer_total")
public class BSoCargoRightTransferTotalEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 7720533394914511171L;

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
     * 销售订单ID
     */
    @TableField("so_order_id")
    @DataChangeLabelAnnotation("销售订单ID")
    private Integer so_order_id;

    /**
     * 货权转移-未处理中数量
     */
    @TableField("cargo_right_untransfer_qty_total")
    @DataChangeLabelAnnotation("货权转移-未处理中数量")
    private BigDecimal cargo_right_untransfer_qty_total;

    /**
     * 货权转移-处理中数量
     */
    @TableField("cargo_right_transfering_qty_total")
    @DataChangeLabelAnnotation("货权转移-处理中数量")
    private BigDecimal cargo_right_transfering_qty_total;

    /**
     * 货权转移-已处理移数量
     */
    @TableField("cargo_right_transferred_qty_total")
    @DataChangeLabelAnnotation("货权转移-已处理移数量")
    private BigDecimal cargo_right_transferred_qty_total;

    /**
     * 货权转移-已作废数量
     */
    @TableField("cargo_right_transfer_cancel_qty_total")
    @DataChangeLabelAnnotation("货权转移-已作废数量")
    private BigDecimal cargo_right_transfer_cancel_qty_total;

    /**
     * 转移总数量
     */
    @TableField("transfer_qty_total")
    @DataChangeLabelAnnotation("转移总数量")
    private BigDecimal transfer_qty_total;

    /**
     * 转移总金额
     */
    @TableField("transfer_amount_total")
    @DataChangeLabelAnnotation("转移总金额")
    private BigDecimal transfer_amount_total;

    /**
     * 订单总数量
     */
    @TableField("order_qty_total")
    @DataChangeLabelAnnotation("订单总数量")
    private BigDecimal order_qty_total;

    /**
     * 订单总金额
     */
    @TableField("order_amount_total")
    @DataChangeLabelAnnotation("订单总金额")
    private BigDecimal order_amount_total;
}