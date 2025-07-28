package com.xinyirun.scm.bean.entity.business.so.arrefund;

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
 * 应收退款关联单据表-源单-预收款
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_refund_source_advance")
public class BArReFundSourceAdvanceEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -5759429204273384831L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 编号
     */
    @TableField("code")
    private String code;

    /**
     * 应收退款主表id
     */
    @TableField("ar_refund_id")
    private Integer ar_refund_id;

    /**
     * 应收退款主表code
     */
    @TableField("ar_refund_code")
    private String ar_refund_code;

    /**
     * 1-应收、2-预收、3-其他收入
     */
    @TableField("type")
    private String type;

    /**
     * 销售合同id
     */
    @TableField("so_contract_id")
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    @TableField("so_contract_code")
    private String so_contract_code;

    /**
     * 销售订单编号
     */
    @TableField("so_order_code")
    private String so_order_code;

    /**
     * 销售订单id
     */
    @TableField("so_order_id")
    private Integer so_order_id;

    /**
     * 商品GROUP_CONCAT
     */
    @TableField("so_goods")
    private String so_goods;

    /**
     * 本次申请金额
     */
    @TableField("order_amount")
    private BigDecimal order_amount;

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
     * 预付款已支付总额
     */
    @TableField("advance_paid_total")
    private BigDecimal advance_paid_total;

    /**
     * 预付款可退金额总额
     */
    @TableField("advance_refund_amount_total")
    private BigDecimal advance_refund_amount_total;

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