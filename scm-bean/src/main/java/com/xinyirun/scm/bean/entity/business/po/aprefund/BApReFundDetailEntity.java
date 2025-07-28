package com.xinyirun.scm.bean.entity.business.po.aprefund;

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
 * 应付退款明细表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_refund_detail")
public class BApReFundDetailEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 2903375761605761982L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 主表id
     */
    @TableField("ap_refund_id")
    private Integer ap_refund_id;

    /**
     * 主表code
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
     * 采购商品信息
     */
    @TableField("po_goods")
    private String po_goods;

    /**
     * 可退款金额
     */
    @TableField("refundable_amount")
    private BigDecimal refundable_amount;

    /**
     * 已退款金额
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
     * 取消金额
     */
    @TableField("cancel_amount")
    private BigDecimal cancel_amount;

    /**
     * 本次申请金额
     */
    @TableField("order_amount")
    private BigDecimal order_amount;


    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人",  extension = "getUserNameExtension")
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
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;


}
