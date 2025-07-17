package com.xinyirun.scm.bean.system.vo.business.aprefund;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应付账款退款管理表-财务数据汇总
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApReFundTotalVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -7501393719592926760L;

    private Integer id;

    /**
     * 应付账款退款主表ID
     */
    private Integer ap_refund_id;

    /**
     * 采购订单主表ID
     */
    private Integer po_order_id;

    /**
     * 可退款总金额
     */
    private BigDecimal refundable_amount_total;

    /**
     * 已退款总金额
     */
    private BigDecimal refunded_amount_total;

    /**
     * 退款中总金额
     */
    private BigDecimal refunding_amount_total;

    /**
     * 未退款总金额
     */
    private BigDecimal unrefund_amount_total;

    /**
     * 取消退款总金额
     */
    private BigDecimal cancelrefund_amount_total;

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

    /**
     * 差量数量，在更新时，计算出更新后的常量值，去更新fin表
     */
    private BigDecimal diff_amount;
}