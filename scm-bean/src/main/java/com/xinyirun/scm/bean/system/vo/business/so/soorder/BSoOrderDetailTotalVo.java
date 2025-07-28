package com.xinyirun.scm.bean.system.vo.business.so.soorder;

import com.xinyirun.scm.bean.system.config.base.BaseVo;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 销售订单明细汇总表VO
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BSoOrderDetailTotalVo extends BaseVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -6930578590189253292L;

    private Integer id;

    /**
     * 销售订单主表ID
     */
    private Integer so_order_id;

    /**
     * 销售订单明细表ID
     */
    private Integer so_order_detail_id;

    /**
     * 库存出库总数量
     */
    private BigDecimal inventory_out_total;

    /**
     * 库存出库计划总数量
     */
    private BigDecimal inventory_out_plan_total;

    /**
     * 可结算数量总计
     */
    private BigDecimal settle_can_qty_total;

    /**
     * 计划结算数量总计
     */
    private BigDecimal settle_planned_qty_total;

    /**
     * 计划结算金额总计
     */
    private BigDecimal settle_planned_amount_total;

    /**
     * 已结算数量总计
     */
    private BigDecimal settled_qty_total;

    /**
     * 已结算金额总计
     */
    private BigDecimal settled_amount_total;

    /**
     * 取消计划结算数量总计
     */
    private BigDecimal settle_cancel_planned_qty_total;

    /**
     * 取消计划结算金额总计
     */
    private BigDecimal settle_cancel_planned_amount_total;

    /**
     * 已取消结算数量总计
     */
    private BigDecimal settled_cancel_qty_total;

    /**
     * 已取消结算金额总计
     */
    private BigDecimal settled_cancel_amount_total;

    /**
     * 出库处理中数量总计
     */
    private BigDecimal outbound_processing_qty_total;

    /**
     * 出库处理中重量总计
     */
    private BigDecimal outbound_processing_weight_total;

    /**
     * 出库处理中体积总计
     */
    private BigDecimal outbound_processing_volume_total;

    /**
     * 出库未处理数量总计
     */
    private BigDecimal outbound_unprocessed_qty_total;

    /**
     * 出库未处理重量总计
     */
    private BigDecimal outbound_unprocessed_weight_total;

    /**
     * 出库未处理体积总计
     */
    private BigDecimal outbound_unprocessed_volume_total;

    /**
     * 出库已处理数量总计
     */
    private BigDecimal outbound_processed_qty_total;

    /**
     * 出库已处理重量总计
     */
    private BigDecimal outbound_processed_weight_total;

    /**
     * 出库已处理体积总计
     */
    private BigDecimal outbound_processed_volume_total;

    /**
     * 出库取消数量总计
     */
    private BigDecimal outbound_cancel_qty_total;

    /**
     * 出库取消重量总计
     */
    private BigDecimal outbound_cancel_weight_total;

    /**
     * 出库取消体积总计
     */
    private BigDecimal outbound_cancel_volume_total;

    /**
     * 货权未转移数量总计
     */
    private BigDecimal cargo_right_untransfer_qty_total;

    /**
     * 货权转移中数量总计
     */
    private BigDecimal cargo_right_transfering_qty_total;

    /**
     * 货权已转移数量总计
     */
    private BigDecimal cargo_right_transferred_qty_total;

    /**
     * 货权转移取消数量总计
     */
    private BigDecimal cargo_right_transfer_cancel_qty_total;

    /**
     * 创建人
     */
    private String c_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 更新人
     */
    private String u_name;

    /**
     * 更新时间
     */
    private LocalDateTime u_time;

    /**
     * 数据版本
     */
    private Integer dbversion;

}