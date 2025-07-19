package com.xinyirun.scm.bean.system.vo.business.po.aprefund;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

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
     * 应付退款主表id
     */
    private Integer ap_refund_id;

    /**
     * 应付退款主表code
     */
    private String ap_refund_code;

    /**
     * 1-应付退款、2-预付退款、3-其他支出退款
     */
    private String type;

    /**
     * 采购合同id
     */
    private Integer po_contract_id;

    /**
     * 采购合同编号
     */
    private String po_contract_code;

    /**
     * 采购订单编号
     */
    private String po_order_code;

    /**
     * 采购订单id
     */
    private Integer po_order_id;

    /**
     * 商品GROUP_CONCAT
     */
    private String po_goods;

    /**
     * 申请退款总金额
     */
    private BigDecimal refundable_amount_total;

    /**
     * 已退款总金额
     */
    private BigDecimal refunded_amount_total;

    /**
     * 退款中总金额
     */
    private BigDecimal refunding_amount_total;

    /**
     * 未退款总金额
     */
    private BigDecimal unrefund_amount_total;

    /**
     * 取消退款总金额
     */
    private BigDecimal cancelrefund_amount_total;

    /**
     * 预付款已付金额
     */
    private BigDecimal advance_paid_total;

    /**
     * 可退金额
     */
    private BigDecimal advance_refund_amount_total;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * 本次申请金额
     */
    private BigDecimal order_amount;


}
