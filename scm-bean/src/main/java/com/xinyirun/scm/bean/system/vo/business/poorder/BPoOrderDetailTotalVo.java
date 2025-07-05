package com.xinyirun.scm.bean.system.vo.business.poorder;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 采购结算明细-数据汇总
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BPoOrderDetailTotalVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 4839105774711068069L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 采购订单主表ID
     */
    private Integer po_order_id;

    /**
     * 采购订单明细ID
     */
    private Integer po_order_detail_id;

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
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
} 