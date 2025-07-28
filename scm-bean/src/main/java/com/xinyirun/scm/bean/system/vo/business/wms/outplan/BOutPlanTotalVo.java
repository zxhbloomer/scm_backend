package com.xinyirun.scm.bean.system.vo.business.wms.outplan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 出库计划汇总表
 * </p>
 *
 * @author system
 * @since 2025-07-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BOutPlanTotalVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 8231630392927790147L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 出库计划id
     */
    private Integer out_plan_id;

    /**
     * 处理中数量合计
     */
    private BigDecimal processing_qty_total;

    /**
     * 处理中重量合计
     */
    private BigDecimal processing_weight_total;

    /**
     * 处理中体积合计
     */
    private BigDecimal processing_volume_total;

    /**
     * 待处理数量合计
     */
    private BigDecimal unprocessed_qty_total;

    /**
     * 待处理重量合计
     */
    private BigDecimal unprocessed_weight_total;

    /**
     * 待处理体积合计
     */
    private BigDecimal unprocessed_volume_total;

    /**
     * 已处理数量合计
     */
    private BigDecimal processed_qty_total;

    /**
     * 已处理重量合计
     */
    private BigDecimal processed_weight_total;

    /**
     * 已处理体积合计
     */
    private BigDecimal processed_volume_total;

    /**
     * 取消数量合计
     */
    private BigDecimal cancel_qty_total;

    /**
     * 取消重量合计
     */
    private BigDecimal cancel_weight_total;

    /**
     * 取消体积合计
     */
    private BigDecimal cancel_volume_total;
}