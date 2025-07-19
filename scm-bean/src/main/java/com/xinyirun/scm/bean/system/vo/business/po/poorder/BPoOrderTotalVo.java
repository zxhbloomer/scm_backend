package com.xinyirun.scm.bean.system.vo.business.po.poorder;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@NoArgsConstructor
public class BPoOrderTotalVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -1266207309536647673L;

    private Integer id;

    /**
     * 采购订单主表ID
     */
    private Integer po_order_id;

    /**
     * 订单总金额
     */
    private BigDecimal amount_total;

    /**
     * 订单总税额
     */
    private BigDecimal tax_amount_total;

    /**
     * 订单总采购数量
     */
    private BigDecimal qty_total;

    /**
     * 预付款未付总金额
     */
    private BigDecimal advance_unpay_total;

    /**
     * 预付款已付款总金额
     */
    private BigDecimal advance_paid_total;

    /**
     * 预付款计划付款金额
     */
    private BigDecimal advance_pay_total;

    /**
     * 结算总金额
     */
    private BigDecimal settle_amount_total;

    /**
     * 应付款未付总金额
     */
    private BigDecimal payable_unpay_total;

    /**
     * 应付款已付总金额
     */
    private BigDecimal payable_paid_total;

    /**
     * 应付款计划付款金额
     */
    private BigDecimal payable_pay_total;

    /**
     * 累计实付
     */
    private BigDecimal paid_total;

    /**
     * 未开票金额
     */
    private BigDecimal uninvoiced_amount_total;

    /**
     * 已开票金额
     */
    private BigDecimal invoiced_amount_total;

    /**
     * 预付款退款-计划退款金额
     */
    private BigDecimal advance_refundable_total;

    /**
     * 预付款退款-实际退款金额
     */
    private BigDecimal advance_refunded_total;

    /**
     * 预付款退款-退款中金额
     */
    private BigDecimal advance_refunding_total;

    /**
     * 预付款退款-未退款金额
     */
    private BigDecimal advance_unrefund_total;

    /**
     * 预付款退款-取消退款金额
     */
    private BigDecimal advance_cancelrefund_total;


    /**
     * 实际入库汇总
     */
    private BigDecimal inventory_in_total;

    /**
     * 计划入库汇总
     */
    private BigDecimal inventory_in_plan_total;

    /**
     * 待结算数量
     */
    private BigDecimal settle_can_qty_total;

    /**
     * 应结算-数量汇总
     */
    private BigDecimal settle_planned_qty_total;

    /**
     * 应结算-金额汇总
     */
    private BigDecimal settle_planned_amount_total;

    /**
     * 实际结算-数量汇总
     */
    private BigDecimal settled_qty_total;

    /**
     * 实际结算-金额汇总
     */
    private BigDecimal settled_amount_total;

    /**
     * 作废-应结算-数量汇总
     */
    private BigDecimal settle_cancel_planned_qty_total;

    /**
     * 作废-应结算-金额汇总
     */
    private BigDecimal settle_cancel_planned_amount_total;

    /**
     * 作废-实际结算-数量汇总
     */
    private BigDecimal settled_cancel_qty_total;

    /**
     * 作废-实际结算-金额汇总
     */
    private BigDecimal settled_cancel_amount_total;

    /**
     * 处理中数量
     */
    private BigDecimal inbound_processing_qty_total;

    /**
     * 处理中重量
     */
    private BigDecimal inbound_processing_weight_total;

    /**
     * 处理中体积
     */
    private BigDecimal inbound_processing_volume_total;

    /**
     * 待处理数量
     */
    private BigDecimal inbound_unprocessed_qty_total;

    /**
     * 待处理重量
     */
    private BigDecimal inbound_unprocessed_weight_total;

    /**
     * 待处理体积
     */
    private BigDecimal inbound_unprocessed_volume_total;

    /**
     * 已处理(出/入)库数量
     */
    private BigDecimal inbound_processed_qty_total;

    /**
     * 已处理(出/入)库重量
     */
    private BigDecimal inbound_processed_weight_total;

    /**
     * 已处理(出/入)库体积
     */
    private BigDecimal inbound_processed_volume_total;

    /**
     * 作废数量
     */
    private BigDecimal inbound_cancel_qty_total;

    /**
     * 作废重量
     */
    private BigDecimal inbound_cancel_weight_total;

    /**
     * 作废体积
     */
    private BigDecimal inbound_cancel_volume_total;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 可下推预付款金额，虚拟列
     */
    private BigDecimal advance_amount_total;    /**
     * 预付款中止付款总金额
     */
    private BigDecimal advance_stoppay_total;

    /**
     * 预付款取消付款总金额
     */
    private BigDecimal advance_cancelpay_total;

    /**
     * 预付款付款中总金额
     */
    private BigDecimal advance_paying_total;

    /**
     * 可下推预付款退款金额
     */
    private BigDecimal advance_refund_amount_total;

}
