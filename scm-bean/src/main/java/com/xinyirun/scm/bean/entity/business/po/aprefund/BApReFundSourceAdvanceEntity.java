package com.xinyirun.scm.bean.entity.business.po.aprefund;

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
     * 1-应付退款、2-预付退款、3-其他支出退款
     */
    @TableField("type")
    private String type;

    /**
     * 采购合同id
     */
    @TableField("po_contract_id")
    private Integer po_contract_id;

    /**
     * 采购合同编号
     */
    @TableField("po_contract_code")
    private String po_contract_code;

    /**
     * 采购订单编号
     */
    @TableField("po_order_code")
    private String po_order_code;

    /**
     * 采购订单id
     */
    @TableField("po_order_id")
    private Integer po_order_id;

    /**
     * 商品GROUP_CONCAT
     */
    @TableField("po_goods")
    private String po_goods;

    /**
     * 申请退款总金额
     */
    @TableField("refundable_amount_total")
    private BigDecimal refundable_amount_total;

    /**
     * 已退款总金额
     */
    @TableField("refunded_amount_total")
    private BigDecimal refunded_amount_total;

    /**
     * 退款中总金额
     */
    @TableField("refunding_amount_total")
    private BigDecimal refunding_amount_total;

    /**
     * 未退款总金额
     */
    @TableField("unrefund_amount_total")
    private BigDecimal unrefund_amount_total;

    /**
     * 取消退款总金额
     */
    @TableField("cancelrefund_amount_total")
    private BigDecimal cancelrefund_amount_total;

    /**
     * 预付款已付金额
     */
    @TableField("advance_paid_total")
    private BigDecimal advance_paid_total;

    /**
     * 可退金额
     */
    @TableField("advance_refund_amount_total")
    private BigDecimal advance_refund_amount_total;

    /**
     * 本次申请金额
     */
    @TableField("order_amount")
    private BigDecimal order_amount;


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
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;


}
