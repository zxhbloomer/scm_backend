package com.xinyirun.scm.bean.system.vo.business.so.arrefund;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 应收退款关联单据表-源单
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BArReFundSourceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 8472951368037429156L;

    private Integer id;

    /**
     * 应收退款主表id
     */
    private Integer ar_refund_id;

    /**
     * 应收退款主表code
     */
    private String ar_refund_code;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    private String type;

    /**
     * 项目编号
     */
    private String project_code;

    /**
     * 销售合同ID
     */
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    private String so_contract_code;

    /**
     * 销售订单ID
     */
    private Integer so_order_id;

    /**
     * 销售订单编号
     */
    private String so_order_code;

    /**
     * 商品名称
     */
    private String so_goods;

    /**
     * 累计预收款金额
     */
    private BigDecimal so_advance_payment_amount;

}