package com.xinyirun.scm.bean.entity.business.po.poorder;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购结算明细-数据汇总
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_po_order_detail_total")
public class BPoOrderDetailTotalEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 4963739451383616980L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 采购订单主表ID
     */
    @TableField("po_order_id")
    private Integer po_order_id;

    /**
     * 采购订单明细ID
     */
    @TableField("po_order_detail_id")
    private Integer po_order_detail_id;

    /**
     * 实际入库汇总
     */
    @TableField("inventory_in_total")
    private BigDecimal inventory_in_total;

    /**
     * 计划入库汇总
     */
    @TableField("inventory_in_plan_total")
    private BigDecimal inventory_in_plan_total;

    /**
     * 待结算数量
     */
    @TableField("settle_can_qty_total")
    private BigDecimal settle_can_qty_total;

    /**
     * 应结算-数量汇总
     */
    @TableField("settle_planned_qty_total")
    private BigDecimal settle_planned_qty_total;

    /**
     * 应结算-金额汇总
     */
    @TableField("settle_planned_amount_total")
    private BigDecimal settle_planned_amount_total;

    /**
     * 实际结算-数量汇总
     */
    @TableField("settled_qty_total")
    private BigDecimal settled_qty_total;

    /**
     * 实际结算-金额汇总
     */
    @TableField("settled_amount_total")
    private BigDecimal settled_amount_total;

    /**
     * 作废-应结算-数量汇总
     */
    @TableField("settle_cancel_planned_qty_total")
    private BigDecimal settle_cancel_planned_qty_total;

    /**
     * 作废-应结算-金额汇总
     */
    @TableField("settle_cancel_planned_amount_total")
    private BigDecimal settle_cancel_planned_amount_total;

    /**
     * 作废-实际结算-数量汇总
     */
    @TableField("settled_cancel_qty_total")
    private BigDecimal settled_cancel_qty_total;

    /**
     * 作废-实际结算-金额汇总
     */
    @TableField("settled_cancel_amount_total")
    private BigDecimal settled_cancel_amount_total;

    /**
     * 处理中数量
     */
    @TableField("inbound_processing_qty_total")
    private BigDecimal inbound_processing_qty_total;

    /**
     * 处理中重量
     */
    @TableField("inbound_processing_weight_total")
    private BigDecimal inbound_processing_weight_total;

    /**
     * 处理中体积
     */
    @TableField("inbound_processing_volume_total")
    private BigDecimal inbound_processing_volume_total;

    /**
     * 待处理数量
     */
    @TableField("inbound_unprocessed_qty_total")
    private BigDecimal inbound_unprocessed_qty_total;

    /**
     * 待处理重量
     */
    @TableField("inbound_unprocessed_weight_total")
    private BigDecimal inbound_unprocessed_weight_total;

    /**
     * 待处理体积
     */
    @TableField("inbound_unprocessed_volume_total")
    private BigDecimal inbound_unprocessed_volume_total;

    /**
     * 已处理(出/入)库数量
     */
    @TableField("inbound_processed_qty_total")
    private BigDecimal inbound_processed_qty_total;

    /**
     * 已处理(出/入)库重量
     */
    @TableField("inbound_processed_weight_total")
    private BigDecimal inbound_processed_weight_total;

    /**
     * 已处理(出/入)库体积
     */
    @TableField("inbound_processed_volume_total")
    private BigDecimal inbound_processed_volume_total;

    /**
     * 作废数量
     */
    @TableField("inbound_cancel_qty_total")
    private BigDecimal inbound_cancel_qty_total;

    /**
     * 作废重量
     */
    @TableField("inbound_cancel_weight_total")
    private BigDecimal inbound_cancel_weight_total;

    /**
     * 作废体积
     */
    @TableField("inbound_cancel_volume_total")
    private BigDecimal inbound_cancel_volume_total;

    /**
     * 货权转移-未处理中数量
     */
    @TableField("cargo_right_untransfer_qty_total")
    private BigDecimal cargo_right_untransfer_qty_total;

    /**
     * 货权转移-处理中数量
     */
    @TableField("cargo_right_transfering_qty_total")
    private BigDecimal cargo_right_transfering_qty_total;

    /**
     * 货权转移-已处理移数量
     */
    @TableField("cargo_right_transferred_qty_total")
    private BigDecimal cargo_right_transferred_qty_total;

    /**
     * 货权转移-已作废数量
     */
    @TableField("cargo_right_transfer_cancel_qty_total")
    private BigDecimal cargo_right_transfer_cancel_qty_total;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;
} 