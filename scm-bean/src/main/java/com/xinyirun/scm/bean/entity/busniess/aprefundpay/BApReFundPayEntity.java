package com.xinyirun.scm.bean.entity.busniess.aprefundpay;

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
     * 状态（1-待付款、2已付款、3-作废）
     */
    @TableField("status")
    private String status;

    /**
     * 1-应付退款、2-预付退款、3-其他支出退款
     */
    @TableField("type")
    private String type;

    /**
     * 供应商企业银行名称
     */
    @TableField("supplier_enterprise_bank_name")
    private String supplier_enterprise_bank_name;
    /**
     * 供应商企业编号
     */
    @TableField("supplier_enterprise_code")
    private String supplier_enterprise_code;

    /**
     * 供应商企业版本号
     */
    @TableField("supplier_enterprise_version")
    private Integer supplier_enterprise_version;

    /**
     * 供应商企业名称
     */
    @TableField("supplier_enterprise_name")
    private String supplier_enterprise_name;


    /**
     * 主体企业银行名称
     */
    @TableField("buyer_enterprise_bank_name")
    private String buyer_enterprise_bank_name;

    /**
     * 主体企业买家企业编号
     */
    @TableField("buyer_enterprise_code")
    private String buyer_enterprise_code;

    /**
     * 主体企业买家企业版本号
     */
    @TableField("buyer_enterprise_version")
    private Integer buyer_enterprise_version;

    /**
     * 主体企业买家企业名称
     */
    @TableField("buyer_enterprise_name")
    private String buyer_enterprise_name;

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
     * 退款款单总金额
     */
    @TableField("refund_amount")
    private BigDecimal refund_amount;

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
    @TableField("dbversion")
    private Integer dbversion;


}
