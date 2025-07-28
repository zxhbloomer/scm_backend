package com.xinyirun.scm.bean.entity.business.po.appay;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 付款单明细表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_pay_detail")
public class BApPayDetailEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 967265783738673997L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 付款单明细编号
     */
    @TableField("code")
    private String code;

    /**
     * 付款单id
     */
    @TableField("ap_pay_id")
    private Integer ap_pay_id;

    /**
     * 付款单code
     */
    @TableField("ap_pay_code")
    private String ap_pay_code;

    /**
     * 应付账款表.id
     */
    @TableField("ap_id")
    private Integer ap_id;

    /**
     * 应付账款表.code
     */
    @TableField("ap_code")
    private String ap_code;

    /**
     * 应付账款明细表.id
     */
    @TableField("ap_detail_id")
    private Integer ap_detail_id;

    /**
     * 应付账款明细表.code
     */
    @TableField("ap_detail_code")
    private String ap_detail_code;

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
     * 计划付款金额
     */
    @TableField("payable_amount")
    private BigDecimal payable_amount;

    /**
     * 已付款金额
     */
    @TableField("paid_amount")
    private BigDecimal paid_amount;

    /**
     * 本次付款金额
     */
    @TableField("pay_amount")
    private BigDecimal pay_amount;

    /**
     * 付款中金额
     */
    @TableField("paying_amount")
    private BigDecimal paying_amount;    /**
     * 未付款金额
     */
    @TableField("unpay_amount")    private BigDecimal unpay_amount;

    /**
     * 作废金额
     */
    @TableField("cancel_amount")
    private BigDecimal cancel_amount;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;    
    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

}
