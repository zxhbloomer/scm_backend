package com.xinyirun.scm.bean.system.vo.business.settlement;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 采购结算明细-数据汇总
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BPoSettlementTotalVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 5234317539315724565L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 采购结算主表id
     */
    private Integer po_settlement_id;

    /**
     * 结算处理中数量
     */
    private BigDecimal processing_qty;

    /**
     * 结算处理中重量
     */
    private BigDecimal processing_weight;

    /**
     * 结算处理中体积
     */
    private BigDecimal processing_volume;

    /**
     * 结算待处理数量
     */
    private BigDecimal unprocessed_qty;

    /**
     * 结算待处理重量
     */
    private BigDecimal unprocessed_weight;

    /**
     * 结算待处理体积
     */
    private BigDecimal unprocessed_volume;

    /**
     * 结算已处理(出/入)库数量
     */
    private BigDecimal processed_qty;

    /**
     * 结算已处理(出/入)库重量
     */
    private BigDecimal processed_weight;

    /**
     * 结算已处理(出/入)库体积
     */
    private BigDecimal processed_volume;

    /**
     * 应结-结算数量
     */
    private BigDecimal planned_qty;

    /**
     * 应结-结算重量
     */
    private BigDecimal planned_weight;

    /**
     * 应结-结算体积
     */
    private BigDecimal planned_volume;

    /**
     * 实际结算-结算数量
     */
    private BigDecimal settled_qty;

    /**
     * 实际结算-结算重量
     */
    private BigDecimal settled_weight;

    /**
     * 实际结算-结算体积
     */
    private BigDecimal settled_volume;

    /**
     * 实际结算-结算金额
     */
    private BigDecimal settled_amount;
} 