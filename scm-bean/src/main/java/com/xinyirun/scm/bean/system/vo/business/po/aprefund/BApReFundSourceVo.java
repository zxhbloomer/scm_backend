package com.xinyirun.scm.bean.system.vo.business.po.aprefund;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 应付退款关联单据表-源单
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApReFundSourceVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 3931597114134830543L;

    private Integer id;

    /**
     * 应付账款主表id
     */
    private Integer ap_refund_id;

    /**
     * 应付账款主表code
     */
    private String ap_refund_code;

    /**
     * 1-应付退款、2-预付退款、3-其他支出退款
     */
    private String type;

    /**
     * 项目编号
     */
    private String project_code;

    /**
     * 采购合同ID
     */
    private Integer po_contract_id;

    /**
     * 采购合同编号
     */
    private String po_contract_code;

    /**
     * 采购订单ID
     */
    private Integer po_order_id;

    /**
     * 采购订单编号
     */
    private String po_order_code;

    /**
     * 商品名称
     */
    private String po_goods;

    /**
     * 累计预付款金额
     */
    private BigDecimal po_advance_payment_amount;

}
