package com.xinyirun.scm.bean.entity.business.so.soorder;

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
 * 销售订单明细汇总表
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_so_order_detail_total")
public class BSoOrderDetailTotalEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -1453992372534693781L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 销售订单主表ID
     */
    @TableField("so_order_id")
    private Integer so_order_id;

    /**
     * 销售订单明细表ID
     */
    @TableField("so_order_detail_id")
    private Integer so_order_detail_id;

    /**
     * 库存出库总数量
     */
    @TableField("inventory_out_total")
    private BigDecimal inventory_out_total;

    /**
     * 库存出库计划总数量
     */
    @TableField("inventory_out_plan_total")
    private BigDecimal inventory_out_plan_total;

    /**
     * 可结算数量总计
     */
    @TableField("settle_can_qty_total")
    private BigDecimal settle_can_qty_total;

    /**
     * 计划结算数量总计
     */
    @TableField("settle_planned_qty_total")
    private BigDecimal settle_planned_qty_total;

    /**
     * 计划结算金额总计
     */
    @TableField("settle_planned_amount_total")
    private BigDecimal settle_planned_amount_total;

    /**
     * 已结算数量总计
     */
    @TableField("settled_qty_total")
    private BigDecimal settled_qty_total;

    /**
     * 已结算金额总计
     */
    @TableField("settled_amount_total")
    private BigDecimal settled_amount_total;

    /**
     * 取消计划结算数量总计
     */
    @TableField("settle_cancel_planned_qty_total")
    private BigDecimal settle_cancel_planned_qty_total;

    /**
     * 取消计划结算金额总计
     */
    @TableField("settle_cancel_planned_amount_total")
    private BigDecimal settle_cancel_planned_amount_total;

    /**
     * 已取消结算数量总计
     */
    @TableField("settled_cancel_qty_total")
    private BigDecimal settled_cancel_qty_total;

    /**
     * 已取消结算金额总计
     */
    @TableField("settled_cancel_amount_total")
    private BigDecimal settled_cancel_amount_total;

    /**
     * 出库处理中数量总计
     */
    @TableField("outbound_processing_qty_total")
    private BigDecimal outbound_processing_qty_total;

    /**
     * 出库处理中重量总计
     */
    @TableField("outbound_processing_weight_total")
    private BigDecimal outbound_processing_weight_total;

    /**
     * 出库处理中体积总计
     */
    @TableField("outbound_processing_volume_total")
    private BigDecimal outbound_processing_volume_total;

    /**
     * 出库未处理数量总计
     */
    @TableField("outbound_unprocessed_qty_total")
    private BigDecimal outbound_unprocessed_qty_total;

    /**
     * 出库未处理重量总计
     */
    @TableField("outbound_unprocessed_weight_total")
    private BigDecimal outbound_unprocessed_weight_total;

    /**
     * 出库未处理体积总计
     */
    @TableField("outbound_unprocessed_volume_total")
    private BigDecimal outbound_unprocessed_volume_total;

    /**
     * 出库已处理数量总计
     */
    @TableField("outbound_processed_qty_total")
    private BigDecimal outbound_processed_qty_total;

    /**
     * 出库已处理重量总计
     */
    @TableField("outbound_processed_weight_total")
    private BigDecimal outbound_processed_weight_total;

    /**
     * 出库已处理体积总计
     */
    @TableField("outbound_processed_volume_total")
    private BigDecimal outbound_processed_volume_total;

    /**
     * 出库取消数量总计
     */
    @TableField("outbound_cancel_qty_total")
    private BigDecimal outbound_cancel_qty_total;

    /**
     * 出库取消重量总计
     */
    @TableField("outbound_cancel_weight_total")
    private BigDecimal outbound_cancel_weight_total;

    /**
     * 出库取消体积总计
     */
    @TableField("outbound_cancel_volume_total")
    private BigDecimal outbound_cancel_volume_total;

    /**
     * 货权未转移数量总计
     */
    @TableField("cargo_right_untransfer_qty_total")
    private BigDecimal cargo_right_untransfer_qty_total;

    /**
     * 货权转移中数量总计
     */
    @TableField("cargo_right_transfering_qty_total")
    private BigDecimal cargo_right_transfering_qty_total;

    /**
     * 货权已转移数量总计
     */
    @TableField("cargo_right_transferred_qty_total")
    private BigDecimal cargo_right_transferred_qty_total;

    /**
     * 货权转移取消数量总计
     */
    @TableField("cargo_right_transfer_cancel_qty_total")
    private BigDecimal cargo_right_transfer_cancel_qty_total;

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