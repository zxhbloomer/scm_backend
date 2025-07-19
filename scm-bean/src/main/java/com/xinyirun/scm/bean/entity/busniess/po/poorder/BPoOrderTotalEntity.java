package com.xinyirun.scm.bean.entity.busniess.po.poorder;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_po_order_total")
public class BPoOrderTotalEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 8442042045795246933L;


    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 采购订单主表ID
     */
    @TableField("po_order_id")
    private Integer po_order_id;

    /**
     * 订单总金额
     */
    @TableField("amount_total")
    private BigDecimal amount_total;

    /**
     * 订单总税额
     */
    @TableField("tax_amount_total")
    private BigDecimal tax_amount_total;

    /**
     * 订单总采购数量
     */
    @TableField("qty_total")
    private BigDecimal qty_total;

    /**
     * 预付款未付总金额
     */
    @TableField("advance_unpay_total")
    private BigDecimal advance_unpay_total;

    /**
     * 预付款已付款总金额
     */
    @TableField("advance_paid_total")
    private BigDecimal advance_paid_total;

    /**
     * 预付款计划付款金额
     */
    @TableField("advance_pay_total")
    private BigDecimal advance_pay_total;    /**
     * 预付款中止付款总金额
     */
    @TableField("advance_stoppay_total")
    private BigDecimal advance_stoppay_total;

    /**
     * 预付款取消付款总金额
     */
    @TableField("advance_cancelpay_total")
    private BigDecimal advance_cancelpay_total;

    /**
     * 预付款付款中总金额
     */
    @TableField("advance_paying_total")
    private BigDecimal advance_paying_total;

    /**
     * 结算总金额
     */
    @TableField("settle_amount_total")
    private BigDecimal settle_amount_total;

    /**
     * 应付未付总金额
     */
    @TableField("payable_unpay_total")
    private BigDecimal payable_unpay_total;

    /**
     * 应付已付总金额
     */
    @TableField("payable_paid_total")
    private BigDecimal payable_paid_total;

    /**
     * 应付款计划付款金额
     */
    @TableField("payable_pay_total")
    private BigDecimal payable_pay_total;

    /**
     * 累计实付
     */
    @TableField("paid_total")
    private BigDecimal paid_total;

    /**
     * 未开票金额
     */
    @TableField("uninvoiced_amount_total")
    private BigDecimal uninvoiced_amount_total;

    /**
     * 已开票金额
     */
    @TableField("invoiced_amount_total")
    private BigDecimal invoiced_amount_total;

    /**
     * 预付款退款-计划退款金额
     */
    @TableField("advance_refundable_total")
    private BigDecimal advance_refundable_total;

    /**
     * 预付款退款-实际退款金额
     */
    @TableField("advance_refunded_total")
    private BigDecimal advance_refunded_total;

    /**
     * 预付款退款-退款中金额
     */
    @TableField("advance_refunding_total")
    private BigDecimal advance_refunding_total;

    /**
     * 预付款退款-未退款金额
     */
    @TableField("advance_unrefund_total")
    private BigDecimal advance_unrefund_total;

    /**
     * 预付款退款-取消退款金额
     */
    @TableField("advance_cancelrefund_total")
    private BigDecimal advance_cancelrefund_total;


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
    @Version
    @TableField("dbversion")
    private Integer dbversion;

}
