package com.xinyirun.scm.bean.entity.busniess.po.aprefundpay;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应付退款单明细表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_refund_pay_detail")
public class BApReFundPayDetailEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 639779810444323830L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 退款单表id
     */
    @TableField("ap_refund_pay_id")
    private Integer ap_refund_pay_id;

    /**
     * 退款单表code
     */
    @TableField("ap_refund_pay_code")
    private String ap_refund_pay_code;

    /**
     * 退款管理id
     */
    @TableField("ap_refund_id")
    private Integer ap_refund_id;

    /**
     * 退款管理code
     */
    @TableField("ap_refund_code")
    private String ap_refund_code;

    /**
     * 企业银行账户表id
     */
    @TableField("bank_accounts_id")
    private Integer bank_accounts_id;

    /**
     * 企业银行账户表编号
     */
    @TableField("bank_accounts_code")
    private String bank_accounts_code;

    /**
     * 计划退款金额
     */
    @TableField("refundable_amount")
    private BigDecimal refundable_amount;

    /**
     * 实退金额
     */
    @TableField("refunded_amount")
    private BigDecimal refunded_amount;

    /**
     * 退款中金额
     */
    @TableField("refunding_amount")
    private BigDecimal refunding_amount;

    /**
     * 未退款金额
     */
    @TableField("unrefund_amount")
    private BigDecimal unrefund_amount;

    /**
     * 作废退款金额
     */
    @TableField("cancel_amount")
    private BigDecimal cancel_amount;

    /**
     * 本次退款金额
     */
    @TableField("order_amount")
    private BigDecimal order_amount;

    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;

}
