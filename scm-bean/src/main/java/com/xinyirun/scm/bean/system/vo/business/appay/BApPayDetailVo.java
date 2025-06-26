package com.xinyirun.scm.bean.system.vo.business.appay;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 付款单明细表 Vo
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApPayDetailVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 967265783738673997L;

    private Integer id;

    /**
     * 付款单明细编号
     */
    private String code;

    /**
     * 付款单id
     */
    private Integer ap_pay_id;

    /**
     * 付款单code
     */
    private String ap_pay_code;

    /**
     * 应付账款表.id
     */
    private Integer ap_id;

    /**
     * 应付账款表.code
     */
    private String ap_code;

    /**
     * 应付账款明细表.id
     */
    private Integer ap_detail_id;

    /**
     * 应付账款明细表.code
     */
    private String ap_detail_code;

    /**
     * 企业银行账户表id
     */
    private Integer bank_accounts_id;

    /**
     * 企业银行账户表编号
     */
    private String bank_accounts_code;

    /**
     * 企业银行款项类型分类表id
     */
    private Integer bank_accounts_type_id;

    /**
     * 企业银行款项类型分类表编号
     */
    private String bank_accounts_type_code;

    /**
     * 计划付款金额
     */
    private BigDecimal payable_amount;

    /**
     * 已付款金额
     */
    private BigDecimal paid_amount;

    /**
     * 本次付款金额
     */
    private BigDecimal pay_amount;

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
     * 付款账户名
     */
    private String name;

    /**
     * 开户行
     */
    private String bank_name;

    /**
     * 银行账号
     */
    private String account_number;

    /**
     * 付款中金额
     */
    private BigDecimal paying_amount;

    /**
     * 未付款金额
     */
    private BigDecimal unpay_amount;

    /**
     * 作废金额
     */
    private BigDecimal cancel_amount;
} 