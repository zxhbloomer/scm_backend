package com.xinyirun.scm.bean.entity.business.po.ap;

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
 * 应付账款关联单据表-源单-预收款
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_source_advance")
public class BApSourceAdvanceEntity implements Serializable {

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
     * 应付账款主表id
     */
    @TableField("ap_id")
    private Integer ap_id;

    /**
     * 应付账款主表code
     */
    @TableField("ap_code")
    private String ap_code;

    /**
     * 1-应付、2-预付、3-其他支出
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
     * 总数量
     */
    @TableField("qty_total")
    private BigDecimal qty_total;

    /**
     * 总金额
     */
    @TableField("amount_total")
    private BigDecimal amount_total;

    /**
     * 累计预付款金额
     */
    @TableField("po_advance_payment_amount")
    private BigDecimal po_advance_payment_amount;

    /**
     * 本次申请金额
     */
    @TableField("order_amount")
    private BigDecimal order_amount;

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

    /**
     * 申请付款总金额
     */
    @TableField("payable_amount_total")
    private BigDecimal payable_amount_total;

    /**
     * 已付款总金额
     */
    @TableField("paid_amount_total")
    private BigDecimal paid_amount_total;

    /**
     * 付款中总金额
     */
    @TableField("paying_amount_total")
    private BigDecimal paying_amount_total;

    /**
     * 未付款总金额
     */
    @TableField("unpay_amount_total")
    private BigDecimal unpay_amount_total;

    /**
     * 中止付款总金额
     */
    @TableField("stoppay_amount_total")
    private BigDecimal stoppay_amount_total;

    /**
     * 取消付款总金额
     */
    @TableField("cancelpay_amount_total")
    private BigDecimal cancelpay_amount_total;

}
