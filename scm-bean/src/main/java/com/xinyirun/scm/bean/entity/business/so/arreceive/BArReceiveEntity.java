package com.xinyirun.scm.bean.entity.business.so.arreceive;

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
 * 应收单表
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-26
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ar_receive")
public class BArReceiveEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 5823947162785394127L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 应收单编号
     */
    @TableField("code")
    private String code;

    /**
     * 应收账款主表id
     */
    @TableField("ar_id")
    private Integer ar_id;

    /**
     * 应收账款主表code
     */
    @TableField("ar_code")
    private String ar_code;

    /**
     * 应收单状态：状态（0-待收款、1已收款、2-作废、-1-中止收款）
     */
    @TableField("status")
    private String status;

    /**
     * 1-应收、2-预收、3-其他收入
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
     * 收款日期
     */
    @TableField("receive_date")
    private LocalDateTime receive_date;

    /**
     * 收款方式：1-银行转账
     */
    @TableField("payment_type")
    private String payment_type;

    /**
     * 应收单计划收款总金额
     */
    @TableField("receivable_amount_total")
    private BigDecimal receivable_amount_total;

    /**
     * 应收单已收款总金额
     */
    @TableField("received_amount_total")
    private BigDecimal received_amount_total;

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
     * 未收款总金额
     */
    @TableField("unreceive_amount_total")
    private BigDecimal unreceive_amount_total;

    /**
     * 收款中总金额
     */
    @TableField("receiving_amount_total")
    private BigDecimal receiving_amount_total;

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
    @DataChangeLabelAnnotation(value="创建人", extension = "getUserNameExtension")
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value="创建时间", extension = "getCTimeExtension")
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
    @DataChangeLabelAnnotation(value="修改时间", extension = "getUTimeExtension")
    private LocalDateTime u_time;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;

}