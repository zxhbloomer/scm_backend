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
 * 退款单关联单据表-源单-预收款
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_refund_pay_source_advance")
public class BApReFundPaySourceAdvanceEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1234567890123456789L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 退款单表id
     */
    @TableField("ap_refund_pay_id")
    private Integer ap_refund_pay_id;

    /**
     * 退款单表code
     */
    @TableField("ap_refund_pay_code")
    private String ap_refund_pay_code;

    /**
     * 退款管理id
     */
    @TableField("ap_refund_id")
    private Integer ap_refund_id;

    /**
     * 退款管理code
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
    @DataChangeLabelAnnotation(value="创建人",  extension = "getUserNameExtension")
    private Integer c_id;

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
    private Integer u_id;

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