package com.xinyirun.scm.bean.entity.busniess.po.aprefundpay;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 应付退款单表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_refund_pay")
public class BApReFundPayEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 4705871377388125154L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 付款单编号
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
     * 退款单状态：状态（0-待付款、1已付款、2-作废）
     */
    @TableField("status")
    private String status;

    /**
     * 1-应付退款、2-预付退款、3-其他支出退款
     */
    @TableField("type")
    private String type;

    /**
     * 供应商ID
     */
    @TableField("supplier_id")
    private Integer supplier_id;

    /**
     * 供应商编码
     */
    @TableField("supplier_code")
    private String supplier_code;

    /**
     * 供应商名称
     */
    @TableField("supplier_name")
    private String supplier_name;

    /**
     * 购买方ID
     */
    @TableField("purchaser_id")
    private Integer purchaser_id;

    /**
     * 采购方编码
     */
    @TableField("purchaser_code")
    private String purchaser_code;

    /**
     * 采购方名称
     */
    @TableField("purchaser_name")
    private String purchaser_name;

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
     * 付款指令备注
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
