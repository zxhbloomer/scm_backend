package com.xinyirun.scm.bean.system.vo.business.po.aprefund;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应付退款管理表明细（Accounts Payable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApReFundDetailVo implements Serializable {


    @Serial
    private static final long serialVersionUID = -4297890846625345575L;

    private Integer id;

    /**
     * 编号
     */
    private String code;


    /**
     * 主表id
     */
    private Integer ap_refund_id;


    /**
     * 主表code
     */
    private String ap_refund_code;

    /**
     * 企业银行账户表id
     */
    private Integer bank_accounts_id;

    /**
     * 企业银行账户表编号
     */
    private String bank_accounts_code;

    /**
     * 采购商品信息
     */
    private String po_goods;

    /**
     * 可退款金额
     */
    private BigDecimal refundable_amount;

    /**
     * 已退款金额
     */
    private BigDecimal refunded_amount;

    /**
     * 退款中金额
     */
    private BigDecimal refunding_amount;

    /**
     * 未退款金额
     */
    private BigDecimal unrefund_amount;

    /**
     * 取消金额
     */
    private BigDecimal cancel_amount;

    /**
     * 本次申请金额
     */
    private BigDecimal order_amount;


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
     * 企业银行账户表名称
     */
    private String name;

    /**
     * 开户行
     */
    private String bank_name;

    /**
     * 银行账户
     */
    private String account_number;

    /**
     * 企业银行账户类型
     */
    private String accounts_purpose_type_name;

    /**
     * 银行账户类型名称
     */
    private String bank_type_name;

}