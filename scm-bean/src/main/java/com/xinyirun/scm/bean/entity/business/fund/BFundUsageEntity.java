package com.xinyirun.scm.bean.entity.business.fund;

import java.io.Serial;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 资金使用情况表
 * </p>
 *
 * @author xinyirun
 * @since 2025-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_fund_usage")
public class BFundUsageEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = 4443076629358653435L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 企业id
     */
    @TableField("enterprise_id")
    private Integer enterprise_id;

    /**
     * 企业code
     */
    @TableField("enterprise_code")
    private String enterprise_code;

    /**
     * 企业银行账户id
     */
    @TableField("bank_account_id")
    private Integer bank_account_id;

    /**
     * 企业银行账户code
     */
    @TableField("bank_account_code")
    private String bank_account_code;

    /**
     * 款项类型id
     */
    @TableField("bank_accounts_type_id")
    private Integer bank_accounts_type_id;

    /**
     * 款项类型code
     */
    @TableField("bank_accounts_type_code")
    private String bank_accounts_type_code;

    /**
     * 资金类型（0：资金池；1：专款专用）
     */
    @TableField("type")
    private String type;

    /**
     * 交易id（资金池：null、合同id：转款专用）
     */
    @TableField("trade_id")
    private Integer trade_id;

    /**
     * 交易编号（资金池：null、合同编号（手输入的编号）：转款专用））
     */
    @TableField("trade_code")
    private String trade_code;

    /**
     * 交易类型（资金池：null、转款专用：表名）
     */
    @TableField("trade_type")
    private String trade_type;

    /**
     * 金额加(审批中)
     */
    @TableField("increase_amount_lock")
    private BigDecimal increase_amount_lock;

    /**
     * 金额减(审批中)
     */
    @TableField("decrease_amount_lock")
    private BigDecimal decrease_amount_lock;

    /**
     * 收付金额
     */
    @TableField("pr_amount")
    private BigDecimal pr_amount;    /**
     * 作废收付金额
     */
    @TableField("cancel_pr_amount")
    private BigDecimal cancel_pr_amount;

    /**
     * 退回金额
     */
    @TableField("refund_amount")
    private BigDecimal refund_amount;

    /**
     * 作废退回金额
     */
    @TableField("cancel_refund_amount")
    private BigDecimal cancel_refund_amount;

    /**
     * 核销金额
     */
    @TableField("settlement_amount")
    private BigDecimal settlement_amount;

    /**
     * 可用金额:'=收付金额-作废收付金额+退回金额-作废退回金额-累计核销金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation("创建时间")
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation("修改时间")
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation("创建人id")
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation("修改人id")
    private Long u_id;


    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField(value="dbversion")
    private Integer dbversion;

}
