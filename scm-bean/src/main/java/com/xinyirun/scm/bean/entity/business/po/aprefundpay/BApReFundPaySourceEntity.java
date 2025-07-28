package com.xinyirun.scm.bean.entity.business.po.aprefundpay;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 退款单关联单据表-源单
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_refund_pay_source")
public class BApReFundPaySourceEntity implements Serializable {

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
     * 类型：1-应付退款、2-预付退款、3-其他支出退款
     */
    @TableField("type")
    private String type;

    /**
     * 项目编号
     */
    @TableField("project_code")
    private String project_code;

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
     * 采购订单id
     */
    @TableField("po_order_id")
    private Integer po_order_id;

    /**
     * 采购订单编号
     */
    @TableField("po_order_code")
    private String po_order_code;

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