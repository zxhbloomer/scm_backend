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
 * 应收退款单表
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-27
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_refund_receive")
public class BArReFundReceiveEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4705871377388125154L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 收款单编号
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
     * 退款单状态：状态（0-待收款、1已收款、2-作废）
     */
    @TableField("status")
    private String status;

    /**
     * 1-应收退款、2-预收退款、3-其他收入退款
     */
    @TableField("type")
    private String type;

    /**
     * 客户ID
     */
    @TableField("customer_id")
    private Integer customer_id;

    /**
     * 客户编码
     */
    @TableField("customer_code")
    private String customer_code;

    /**
     * 客户名称
     */
    @TableField("customer_name")
    private String customer_name;

    /**
     * 销售方ID
     */
    @TableField("seller_id")
    private Integer seller_id;

    /**
     * 销售方编码
     */
    @TableField("seller_code")
    private String seller_code;

    /**
     * 销售方名称
     */
    @TableField("seller_name")
    private String seller_name;

    /**
     * 退款日期
     */
    @TableField("refund_date")
    private LocalDateTime refund_date;

    /**
     * 退款方式：1-银行转账
     */
    @TableField("refund_method")
    private String refund_method;

    /**
     * 退款金额
     */
    @TableField("refundable_amount_total")
    private BigDecimal refundable_amount_total;

    /**
     * 已退款
     */
    @TableField("refunded_amount_total")
    private BigDecimal refunded_amount_total;

    /**
     * 退款中
     */
    @TableField("refunding_amount_total")
    private BigDecimal refunding_amount_total;

    /**
     * 未退款
     */
    @TableField("unrefund_amount_total")
    private BigDecimal unrefund_amount_total;

    /**
     * 退款取消
     */
    @TableField("cancelrefund_amount_total")
    private BigDecimal cancelrefund_amount_total;

    /**
     * 收款指令备注
     */
    @TableField("remark")
    private String remark;

    /**
     * 凭证上传备注
     */
    @TableField("voucher_remark")
    private String voucher_remark;

    /**
     * 作废理由
     */
    @TableField("cancel_reason")
    private String cancel_reason;

    /**
     * 作废附件
     */
    @TableField("cancel_file")
    private Integer cancel_file;

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