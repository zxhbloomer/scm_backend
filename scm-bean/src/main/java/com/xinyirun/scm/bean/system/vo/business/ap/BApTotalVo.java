package com.xinyirun.scm.bean.system.vo.business.ap;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应付账款管理表-财务数据汇总
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-11
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApTotalVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -7501393719592926759L;

    private Integer id;

    /**
     * 应付账款主表ID
     */
    private Integer ap_id;

    /**
     * 申请付款总金额
     */
    private BigDecimal payable_amount_total;

    /**
     * 已付款总金额
     */
    private BigDecimal paid_amount_total;

    /**
     * 付款中总金额
     */
    private BigDecimal paying_amount_total;

    /**
     * 未付款总金额
     */
    private BigDecimal unpay_amount_total;    /**
     * 中止总金额
     */
    private BigDecimal stoppay_amount_total;

    /**
     * 取消付款总金额
     */
    private BigDecimal cancelpay_amount_total;

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
