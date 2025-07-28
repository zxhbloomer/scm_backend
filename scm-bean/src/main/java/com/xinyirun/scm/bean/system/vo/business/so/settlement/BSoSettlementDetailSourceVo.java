package com.xinyirun.scm.bean.system.vo.business.so.settlement;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;

/**
 * 销售结算明细表-源单
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BSoSettlementDetailSourceVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 8378076047097750305L;
    
    /**
     * 主键id
     */
    private Integer id;

    /**
     * 销售结算主表id
     */
    private Integer so_settlement_id;

    /**
     * 销售结算主表code
     */
    private String so_settlement_code;

    /**
     * 结算方式：1-先款后货；2-先货后款；3-货到付款
     */
    private String settle_type;

    /**
     * 结算单据类型：1-实际发货结算；2-货转凭证结算
     */
    private String bill_type;

    /**
     * 项目编号
     */
    private String project_code;

    /**
     * 销售合同id
     */
    private Integer so_contract_id;

    /**
     * 销售合同编号
     */
    private String so_contract_code;

    /**
     * 销售订单id
     */
    private Integer so_order_id;

    /**
     * 销售订单编号
     */
    private String so_order_code;

    /**
     * 销售订单明细id
     */
    private Integer so_order_detail_id;
}