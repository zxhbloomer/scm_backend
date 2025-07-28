package com.xinyirun.scm.bean.entity.business.po.settlement;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * 采购结算明细表-源单
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("b_po_settlement_detail_source")
public class BPoSettlementDetailSourceEntity implements Serializable {


    @Serial
    private static final long serialVersionUID = -870230595345925554L;
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 采购结算主表id
     */
    @TableField("po_settlement_id")
    private Integer po_settlement_id;

    /**
     * 采购结算主表code
     */
    @TableField("po_settlement_code")
    private String po_settlement_code;

    /**
     * 结算方式：1-先款后货；2-先货后款；3-货到付款；
     */
    @TableField("settle_type")
    private String settle_type;

    /**
     * 结算单据类型：1-实际到货结算；2-货转凭证结算
     */
    @TableField("bill_type")
    private String bill_type;

    /**
     * 项目编号
     */
    @TableField("project_code")
    private String project_code;

    /**
     * 合同ID
     */
    @TableField("po_contract_id")
    private Integer po_contract_id;

    /**
     * 合同编码
     */
    @TableField("po_contract_code")
    private String po_contract_code;

    /**
     * 订单ID
     */
    @TableField("po_order_id")
    private Integer po_order_id;

    /**
     * 订单编码
     */
    @TableField("po_order_code")
    private String po_order_code;

    /**
     * 订单明细ID
     */
    @TableField("po_order_detail_id")
    private Integer po_order_detail_id;
} 