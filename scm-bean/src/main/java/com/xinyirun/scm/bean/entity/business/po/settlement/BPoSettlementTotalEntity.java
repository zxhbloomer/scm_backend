package com.xinyirun.scm.bean.entity.business.po.settlement;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 采购结算明细-数据汇总
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_po_settlement_total")
public class BPoSettlementTotalEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -6778840050089596548L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 采购结算主表id
     */
    @TableField("po_settlement_id")
    private Integer po_settlement_id;

    /**
     * 结算处理中数量
     */
    @TableField("processing_qty")
    private BigDecimal processing_qty;

    /**
     * 结算处理中重量
     */
    @TableField("processing_weight")
    private BigDecimal processing_weight;

    /**
     * 结算处理中体积
     */
    @TableField("processing_volume")
    private BigDecimal processing_volume;

    /**
     * 结算待处理数量
     */
    @TableField("unprocessed_qty")
    private BigDecimal unprocessed_qty;

    /**
     * 结算待处理重量
     */
    @TableField("unprocessed_weight")
    private BigDecimal unprocessed_weight;

    /**
     * 结算待处理体积
     */
    @TableField("unprocessed_volume")
    private BigDecimal unprocessed_volume;

    /**
     * 结算已处理(出/入)库数量
     */
    @TableField("processed_qty")
    private BigDecimal processed_qty;

    /**
     * 结算已处理(出/入)库重量
     */
    @TableField("processed_weight")
    private BigDecimal processed_weight;

    /**
     * 结算已处理(出/入)库体积
     */
    @TableField("processed_volume")
    private BigDecimal processed_volume;

    /**
     * 应结-结算数量
     */
    @TableField("planned_qty")
    private BigDecimal planned_qty;

    /**
     * 应结-结算重量
     */
    @TableField("planned_weight")
    private BigDecimal planned_weight;

    /**
     * 应结-结算体积
     */
    @TableField("planned_volume")
    private BigDecimal planned_volume;

    /**
     * 应结-结算金额
     */
    @TableField("planned_amount")
    private BigDecimal planned_amount;

    /**
     * 实际结算-结算数量
     */
    @TableField("settled_qty")
    private BigDecimal settled_qty;

    /**
     * 实际结算-结算重量
     */
    @TableField("settled_weight")
    private BigDecimal settled_weight;

    /**
     * 实际结算-结算体积
     */
    @TableField("settled_volume")
    private BigDecimal settled_volume;

    /**
     * 实际结算-结算金额
     */
    @TableField("settled_amount")
    private BigDecimal settled_amount;
}