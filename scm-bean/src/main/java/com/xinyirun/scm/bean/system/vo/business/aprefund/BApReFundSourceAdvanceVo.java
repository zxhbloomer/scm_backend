package com.xinyirun.scm.bean.system.vo.business.aprefund;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 应付退款管理表-预付款业务表（Accounts Payable）
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApReFundSourceAdvanceVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 2592341317464135882L;
    private Integer id;

    /**
     * 编号
     */
    private String code;

    /**
     * 应付账款主表id
     */
    private Integer ap_pay_id;

    /**
     * 应付账款主表code
     */
    private String ap_pay_code;

    /**
     * 应付账单id
     */
    private Integer ap_id;

    /**
     * 应付账单code
     */
    private String ap_code;

    /**
     * 应付账款id
     */
    private Integer ap_refund_id;

    /**
     * 应付账款code
     */
    private String ap_refund_code;

    /**
     *
     */
    private String type;

    /**
     * 采购合同编号
     */
    private String po_contract_code;

    /**
     * 采购订单编号
     */
    private String po_code;

    /**
     * 商品GROUP_CONCAT
     */
    private String po_goods;

    /**
     * 预付款总金额
     */
    private BigDecimal advance_pay_amount;

    /**
     * 已退金额
     */
    private BigDecimal refunded_amount;

    /**
     * 可退金额
     */
    private BigDecimal can_refunded_amount;

    /**
     * 退款中金额
     */
    private BigDecimal refunding_amount;

    /**
     * 本次申请退款金额
     */
    private BigDecimal refund_amount;

    /**
     * 备注
     */
    private String remark;
}
