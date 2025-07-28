package com.xinyirun.scm.bean.system.vo.business.so.arrefund;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应收退款管理表-财务数据汇总
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArReFundTotalVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -7501393719592926762L;

    private Integer id;

    /**
     * 应收退款主表ID
     */
    private Integer ar_refund_id;

    /**
     * 销售订单ID
     */
    private Integer so_order_id;

    /**
     * 申请退款总金额
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
     * 差量数量，在更新时，计算出更新后的常量值，去更新fin表
     */
    private BigDecimal diff_amount;
}