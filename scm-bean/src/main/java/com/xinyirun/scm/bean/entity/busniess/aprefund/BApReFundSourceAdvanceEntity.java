package com.xinyirun.scm.bean.entity.busniess.aprefund;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 应付退款关联单据表-源单-预收款
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_refund_source_advance")
public class BApReFundSourceAdvanceEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -5759429204273384830L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 应付退款主表id
     */
    @TableField("ap_refund_id")
    private Integer ap_refund_id;

    /**
     * 应付退款主表code
     */
    @TableField("ap_refund_code")
    private String ap_refund_code;

    /**
     * 应付账单id
     */
    @TableField("ap_id")
    private Integer ap_id;

    /**
     * 应付账单code
     */
    @TableField("ap_code")
    private String ap_code;

    /**
     * 1-应付退款、2-预付退款、3-其他支出退款
     */
    @TableField("type")
    private String type;

    /**
     * 采购合同编号
     */
    @TableField("po_contract_code")
    private String po_contract_code;

    /**
     * 采购订单编号
     */
    @TableField("po_code")
    private String po_code;

    /**
     * 商品GROUP_CONCAT
     */
    @TableField("po_goods")
    private String po_goods;

    /**
     * 预付款总金额
     */
    @TableField("advance_pay_amount")
    private BigDecimal advance_pay_amount;

    /**
     * 已退金额
     */
    @TableField("refunded_amount")
    private BigDecimal refunded_amount;

    /**
     * 退款中金额
     */
    @TableField("refunding_amount")
    private BigDecimal refunding_amount;

    /**
     * 本次申请退款金额
     */
    @TableField("refund_amount")
    private BigDecimal refund_amount;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;


}
