package com.xinyirun.scm.bean.system.vo.business.settlement;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 采购结算明细表-源单
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BPoSettlementDetailSourceVo implements Serializable {


    @Serial
    private static final long serialVersionUID = 8378076047097750304L;
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 采购结算主表id
     */
    private Integer po_settlement_id;

    /**
     * 采购结算主表code
     */
    private String po_settlement_code;

    /**
     * 结算方式：1-先款后货；2-先货后款；3-货到付款；
     */
    private String settle_type;

    /**
     * 结算单据类型：1-实际到货结算；2-货转凭证结算
     */
    private String bill_type;

    /**
     * 项目编号
     */
    private String project_code;

    /**
     * 合同ID
     */
    private Integer po_contract_id;

    /**
     * 合同编码
     */
    private String po_contract_code;

    /**
     * 订单ID
     */
    private Integer po_order_id;

    /**
     * 订单编码
     */
    private String po_order_code;
} 