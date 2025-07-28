package com.xinyirun.scm.bean.system.vo.business.so.settlement;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 销售结算汇总表VO类
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BSoSettlementTotalVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -6179056939365154202L;

    /**
     * 主键
     */
    private Integer id;

    /**
     * 销售结算主表id
     */
    private Integer so_settlement_id;

    /**
     * 处理中数量
     */
    private BigDecimal processing_qty;

    /**
     * 处理中重量
     */
    private BigDecimal processing_weight;

    /**
     * 处理中体积
     */
    private BigDecimal processing_volume;

    /**
     * 未处理数量
     */
    private BigDecimal unprocessed_qty;

    /**
     * 未处理重量
     */
    private BigDecimal unprocessed_weight;

    /**
     * 未处理体积
     */
    private BigDecimal unprocessed_volume;

    /**
     * 已处理数量
     */
    private BigDecimal processed_qty;

    /**
     * 已处理重量
     */
    private BigDecimal processed_weight;

    /**
     * 已处理体积
     */
    private BigDecimal processed_volume;

    /**
     * 应结算数量
     */
    private BigDecimal planned_qty;

    /**
     * 应结算重量
     */
    private BigDecimal planned_weight;

    /**
     * 应结算体积
     */
    private BigDecimal planned_volume;

    /**
     * 应结算金额
     */
    private BigDecimal planned_amount;

    /**
     * 实结算数量
     */
    private BigDecimal settled_qty;

    /**
     * 实结算重量
     */
    private BigDecimal settled_weight;

    /**
     * 实结算体积
     */
    private BigDecimal settled_volume;

    /**
     * 实结算金额
     */
    private BigDecimal settled_amount;
}