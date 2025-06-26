package com.xinyirun.scm.bean.system.bo.fund.total;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class TotalDataRecalculateBo implements Serializable {

    @Serial
    private static final long serialVersionUID = -4769315358634061959L;

    /**
     * 采购合同ID
     */
    private Integer poContractId;

    private List<Integer> poContractIds;

    /**
     * 采购订单ID
     */
    private Integer poOrderId;
    private List<Integer> poOrderIds;

    /**
     * 采购订单编号
     */
    private String poOrderCode;
    private List<String> poOrderCodes;

    /**
     * 采购合同编号
     */
    private String poContractCode;
    private List<String> poContractCodes;

    /**
     * ap id
     */
    private Integer apId;
    private List<Integer> apIds;

    /**
     * ap 编号
     */
    private String apCode;
    private List<String> apCodes;

    /**
     * 付款单编号
     */
    private String apPayCode;
    private List<String> apPayCodes;

    /**
     * 付款单ID
     */
    private Integer apPayId;
    private List<Integer> apPayIds;

}
