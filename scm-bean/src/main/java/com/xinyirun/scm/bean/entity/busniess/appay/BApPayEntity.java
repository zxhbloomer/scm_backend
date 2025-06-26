package com.xinyirun.scm.bean.entity.busniess.appay;

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
 * 付款单表
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_pay")
public class BApPayEntity implements Serializable {

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
     * 付款单状态：状态（0-待付款、1已付款、2-作废、-1-中止付款）
     */
    @TableField("status")
    private String status;

    /**
     * 1-应付、2-预付、3-其他支出
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
     * 付款日期
     */
    @TableField("pay_date")
    private LocalDateTime pay_date;

    /**
     * 付款方式：1-银行转账
     */
    @TableField("payment_type")
    private String payment_type;

    /**
     * 付款单计划付款总金额
     */
    @TableField("payable_amount_total")
    private BigDecimal payable_amount_total;

    /**
     * 付款单已付款总金额
     */
    @TableField("paid_amount_total")
    private BigDecimal paid_amount_total;

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
     * 未付款总金额
     */
    @TableField("unpay_amount_total")
    private BigDecimal unpay_amount_total;

    /**
     * 付款中总金额
     */
    @TableField("paying_amount_total")
    private BigDecimal paying_amount_total;

    /**
     * 作废总金额
     */
    @TableField("cancel_amount_total")
    private BigDecimal cancel_amount_total;

    /**
     * 作废理由
     */
    @TableField("cancel_reason")
    private String cancel_reason;

    /**
     * 作废附件ID
     */
    @TableField("cancel_file")
    private Integer cancel_file;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建人",  extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value="修改人", extension = "getUserNameExtension")
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

}
