package com.xinyirun.scm.bean.entity.business.so.arrefundreceive;

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
 * 退款单关联单据表-源单-预收款
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_refund_receive_source_advance")
public class BArReFundReceiveSourceAdvanceEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567890123456789L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 退款单表id
     */
    @TableField("ar_refund_receive_id")
    private Integer ar_refund_receive_id;

    /**
     * 退款单表code
     */
    @TableField("ar_refund_receive_code")
    private String ar_refund_receive_code;

    /**
     * 退款管理id
     */
    @TableField("ar_refund_id")
    private Integer ar_refund_id;

    /**
     * 退款管理code
     */
    @TableField("ar_refund_code")
    private String ar_refund_code;

    /**
     * 1-应收退款、2-预收退款、3-其他收入退款
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
     * 预收款已付总金额
     */
    @TableField("advance_paid_total")
    private BigDecimal advance_paid_total;

    /**
     * 预收款退款总金额
     */
    @TableField("advance_refund_amount_total")
    private BigDecimal advance_refund_amount_total;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;

}