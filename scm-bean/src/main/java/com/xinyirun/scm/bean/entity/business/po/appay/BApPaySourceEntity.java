package com.xinyirun.scm.bean.entity.business.po.appay;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 付款来源表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_ap_pay_source")
public class BApPaySourceEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 5532505649463255550L;
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 付款主表id
     */
    @TableField("ap_pay_id")
    private Integer ap_pay_id;

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
     * 付款主表code
     */
    @TableField("ap_pay_code")
    private String ap_pay_code;

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
     * 项目编号
     */
    @TableField("project_code")
    private String project_code;

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
} 