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
public class SoTotalDataRecalculateBo implements Serializable {

    @Serial
    private static final long serialVersionUID = -6892547126841573281L;

    /**
     * 销售合同ID
     */
    private Integer soContractId;

    private List<Integer> soContractIds;

    /**
     * 销售订单ID
     */
    private Integer soOrderId;
    private List<Integer> soOrderIds;

    /**
     * 销售订单编号
     */
    private String soOrderCode;
    private List<String> soOrderCodes;

    /**
     * 销售合同编号
     */
    private String soContractCode;
    private List<String> soContractCodes;

    /**
     * ar id
     */
    private Integer arId;
    private List<Integer> arIds;

    /**
     * ar 编号
     */
    private String arCode;
    private List<String> arCodes;

    /**
     * 收款单编号
     */
    private String arReceiveCode;
    private List<String> arReceiveCodes;

    /**
     * 收款单ID
     */
    private Integer arReceiveId;
    private List<Integer> arReceiveIds;

    /**
     * 出库计划ID
     */
    private Integer outPlanId;
    private List<Integer> outPlanIds;

    /**
     * 出库单ID
     */
    private Integer outboundId;
    private List<Integer> outboundIds;

    /**
     * 结算ID
     */
    private Integer soSettlementId;
    private List<Integer> soSettlementIds;

    /**
     * 退款id
     */
    private Integer soRefundId;
    private List<Integer> soRefundIds;

    /**
     * 货权转移id
     */
    private Integer cargoRightTransferId;
    private List<Integer> cargoRightTransferIds;
}