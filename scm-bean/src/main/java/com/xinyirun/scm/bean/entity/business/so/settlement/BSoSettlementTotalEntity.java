package com.xinyirun.scm.bean.entity.business.so.settlement;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售结算汇总表实体类
 * 
 * @Author: zxh
 * @since 2024-07-24
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_settlement_total")
public class BSoSettlementTotalEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -532016190966337813L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 销售结算主表id
     */
    @TableField("so_settlement_id")
    @DataChangeLabelAnnotation("销售结算主表id")
    private Integer so_settlement_id;

    /**
     * 处理中数量
     */
    @TableField("processing_qty")
    @DataChangeLabelAnnotation("处理中数量")
    private BigDecimal processing_qty;

    /**
     * 处理中重量
     */
    @TableField("processing_weight")
    @DataChangeLabelAnnotation("处理中重量")
    private BigDecimal processing_weight;

    /**
     * 处理中体积
     */
    @TableField("processing_volume")
    @DataChangeLabelAnnotation("处理中体积")
    private BigDecimal processing_volume;

    /**
     * 未处理数量
     */
    @TableField("unprocessed_qty")
    @DataChangeLabelAnnotation("未处理数量")
    private BigDecimal unprocessed_qty;

    /**
     * 未处理重量
     */
    @TableField("unprocessed_weight")
    @DataChangeLabelAnnotation("未处理重量")
    private BigDecimal unprocessed_weight;

    /**
     * 未处理体积
     */
    @TableField("unprocessed_volume")
    @DataChangeLabelAnnotation("未处理体积")
    private BigDecimal unprocessed_volume;

    /**
     * 已处理数量
     */
    @TableField("processed_qty")
    @DataChangeLabelAnnotation("已处理数量")
    private BigDecimal processed_qty;

    /**
     * 已处理重量
     */
    @TableField("processed_weight")
    @DataChangeLabelAnnotation("已处理重量")
    private BigDecimal processed_weight;

    /**
     * 已处理体积
     */
    @TableField("processed_volume")
    @DataChangeLabelAnnotation("已处理体积")
    private BigDecimal processed_volume;

    /**
     * 应结算数量
     */
    @TableField("planned_qty")
    @DataChangeLabelAnnotation("应结算数量")
    private BigDecimal planned_qty;

    /**
     * 应结算重量
     */
    @TableField("planned_weight")
    @DataChangeLabelAnnotation("应结算重量")
    private BigDecimal planned_weight;

    /**
     * 应结算体积
     */
    @TableField("planned_volume")
    @DataChangeLabelAnnotation("应结算体积")
    private BigDecimal planned_volume;

    /**
     * 应结算金额
     */
    @TableField("planned_amount")
    @DataChangeLabelAnnotation("应结算金额")
    private BigDecimal planned_amount;

    /**
     * 实结算数量
     */
    @TableField("settled_qty")
    @DataChangeLabelAnnotation("实结算数量")
    private BigDecimal settled_qty;

    /**
     * 实结算重量
     */
    @TableField("settled_weight")
    @DataChangeLabelAnnotation("实结算重量")
    private BigDecimal settled_weight;

    /**
     * 实结算体积
     */
    @TableField("settled_volume")
    @DataChangeLabelAnnotation("实结算体积")
    private BigDecimal settled_volume;

    /**
     * 实结算金额
     */
    @TableField("settled_amount")
    @DataChangeLabelAnnotation("实结算金额")
    private BigDecimal settled_amount;
}