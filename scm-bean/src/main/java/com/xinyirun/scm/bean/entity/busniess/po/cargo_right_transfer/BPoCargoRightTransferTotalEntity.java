package com.xinyirun.scm.bean.entity.busniess.po.cargo_right_transfer;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 货权转移汇总表实体类
 * 
 * @author system
 * @since 2025-07-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_po_cargo_right_transfer_total")
public class BPoCargoRightTransferTotalEntity implements Serializable {

    private static final long serialVersionUID = 1L;

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
     * 采购订单ID
     */
    @TableField("po_order_id")
    @DataChangeLabelAnnotation("采购订单ID")
    private Integer po_order_id;

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

}