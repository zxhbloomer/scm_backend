package com.xinyirun.scm.bean.entity.busniess.wms.inplan;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

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
@Accessors(chain = true)
@TableName("b_in_plan_total")
public class BInPlanTotalEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 35279912486610320L;

    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 入库计划id
     */
    @TableField("in_plan_id")
    private Integer in_plan_id;

    /**
     * 处理中数量汇总
     */
    @TableField("processing_qty_total")
    private BigDecimal processing_qty_total;

    /**
     * 处理中重量汇总
     */
    @TableField("processing_weight_total")
    private BigDecimal processing_weight_total;

    /**
     * 处理中体积汇总
     */
    @TableField("processing_volume_total")
    private BigDecimal processing_volume_total;

    /**
     * 未处理数量汇总
     */
    @TableField("unprocessed_qty_total")
    private BigDecimal unprocessed_qty_total;

    /**
     * 未处理重量汇总
     */
    @TableField("unprocessed_weight_total")
    private BigDecimal unprocessed_weight_total;

    /**
     * 未处理体积汇总
     */
    @TableField("unprocessed_volume_total")
    private BigDecimal unprocessed_volume_total;

    /**
     * 已处理数量汇总
     */
    @TableField("processed_qty_total")
    private BigDecimal processed_qty_total;

    /**
     * 已处理重量汇总
     */
    @TableField("processed_weight_total")
    private BigDecimal processed_weight_total;

    /**
     * 已处理体积汇总
     */
    @TableField("processed_volume_total")
    private BigDecimal processed_volume_total;

    /**
     * 作废数量汇总
     */
    @TableField("cancel_qty_total")
    private BigDecimal cancel_qty_total;

    /**
     * 作废重量汇总
     */
    @TableField("cancel_weight_total")
    private BigDecimal cancel_weight_total;

    /**
     * 作废体积汇总
     */
    @TableField("cancel_volume_total")
    private BigDecimal cancel_volume_total;
}
