package com.xinyirun.scm.bean.system.vo.business.po.appay;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 付款来源预付表 Vo
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class BApPaySourceAdvanceVo implements Serializable {
    @Serial
    private static final long serialVersionUID = 3329574980166756118L;
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 付款单主表id
     */
    private Integer ap_pay_id;

    /**
     * 付款主表code
     */
    private String ap_pay_code;

    /**
     * 应付账款主表id
     */
    private Integer ap_id;

    /**
     * 应付账款主表code
     */
    private String ap_code;

    /**
     * 1-应付、2-预付、3-其他支出
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
     * 总数量
     */
    private BigDecimal qty_total;

    /**
     * 总金额
     */
    private BigDecimal amount_total;

    /**
     * 累计预付款金额
     */
    private BigDecimal po_advance_payment_amount;

    /**
     * 本次申请金额
     */
    private BigDecimal order_amount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人id
     */
    private Integer c_id;

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
    private Integer u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;

    /**
     * ap pay 主表 状态
     */
    private String status;

    /**
     * 采购合同ID聚合字符串（GROUP_CONCAT）
     */
    private String po_contract_id_gc;

    /**
     * 采购合同编号聚合字符串（GROUP_CONCAT）
     */
    private String po_contract_code_gc;

    /**
     * 采购订单编号聚合字符串（GROUP_CONCAT）
     */
    private String po_order_code_gc;

    /**
     * 采购订单ID聚合字符串（GROUP_CONCAT）
     */
    private String po_order_id_gc;
} 