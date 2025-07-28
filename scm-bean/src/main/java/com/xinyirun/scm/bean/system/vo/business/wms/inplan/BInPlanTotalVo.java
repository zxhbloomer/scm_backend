package com.xinyirun.scm.bean.system.vo.business.wms.inplan;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 入库计划汇总
 * </p>
 *
 * @author system
 * @since 2025-06-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class BInPlanTotalVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 8556116770480740880L;

    /**
     * 主键id
     */
    private Integer id;

    /**
     * 入库计划id
     */
    private Integer in_plan_id;

    /**
     * 处理中数量汇总
     */
    private BigDecimal processing_qty_total;

    /**
     * 处理中重量汇总
     */
    private BigDecimal processing_weight_total;

    /**
     * 处理中体积汇总
     */
    private BigDecimal processing_volume_total;

    /**
     * 未处理数量汇总
     */
    private BigDecimal unprocessed_qty_total;

    /**
     * 未处理重量汇总
     */
    private BigDecimal unprocessed_weight_total;

    /**
     * 未处理体积汇总
     */
    private BigDecimal unprocessed_volume_total;

    /**
     * 已处理数量汇总
     */
    private BigDecimal processed_qty_total;

    /**
     * 已处理重量汇总
     */
    private BigDecimal processed_weight_total;

    /**
     * 已处理体积汇总
     */
    private BigDecimal processed_volume_total;

    /**
     * 作废数量汇总
     */
    private BigDecimal cancel_qty_total;

    /**
     * 作废重量汇总
     */
    private BigDecimal cancel_weight_total;

    /**
     * 作废体积汇总
     */
    private BigDecimal cancel_volume_total;
}
