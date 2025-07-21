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
@TableName("b_cargo_right_transfer_total")
public class BCargoRightTransferTotalEntity implements Serializable {

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
     * 转移总数量
     */
    @TableField("qty_total")
    @DataChangeLabelAnnotation("转移总数量")
    private BigDecimal qty_total;

    /**
     * 转移总重量
     */
    @TableField("weight_total")
    @DataChangeLabelAnnotation("转移总重量")
    private BigDecimal weight_total;

    /**
     * 转移总体积
     */
    @TableField("volume_total")
    @DataChangeLabelAnnotation("转移总体积")
    private BigDecimal volume_total;

    /**
     * 转移总金额
     */
    @TableField("amount_total")
    @DataChangeLabelAnnotation("转移总金额")
    private BigDecimal amount_total;
}