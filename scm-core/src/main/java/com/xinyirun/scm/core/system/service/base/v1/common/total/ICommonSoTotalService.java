package com.xinyirun.scm.core.system.service.base.v1.common.total;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.socontract.BSoContractTotalEntity;
import com.xinyirun.scm.bean.system.bo.fund.total.SoTotalDataRecalculateBo;

/**
 * <p>
 * 销售合同表-财务数据汇总 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-11
 */
public interface ICommonSoTotalService extends IService<BSoContractTotalEntity> {

    /**
     * 重新计算所有的财务数据
     * 同步计算
     *
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalData(SoTotalDataRecalculateBo bo);

    /**
     * 按销售合同编号重新生成Total数据
     * @param code 销售合同编号
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataBySoContractCode(String code);

    /**
     * 按销售合同id重新生成Total数据
     * @param id 销售合同id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataBySoContractId(Integer id);

    /**
     * 按销售订单编号重新生成Total数据
     * @param code 销售订单编号
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataBySoOrderCode(String code);

    /**
     * 按销售订单id重新生成Total数据
     * @param id 销售订单id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataBySoOrderId(Integer id);

    /**
     * 按应收账款id重新生成Total数据
     * @param id 应收账款id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByArId(Integer id);

    /**
     * 按应收账款编号重新生成Total数据
     * @param code 应收账款编号
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByArCode(String code);

    /**
     * 按收款单id重新生成Total数据
     * @param id 收款单id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByArReceiveId(Integer id);

    /**
     * 按收款单编号重新生成Total数据
     * @param code 收款单编号
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByArReceiveCode(String code);

    /**
     * 按出库计划id重新生成Total数据
     * @param id 出库计划id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByOutPlanId(Integer id);

    /**
     * 按出库单id重新生成Total数据
     * @param id 出库单id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByOutboundId(Integer id);

    /**
     * 按销售结算id重新生成Total数据
     * @param id 销售结算id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataBySoSettlementId(Integer id);

    /**
     * 按退款id重新生成Total数据
     * @param id 退款id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataBySoRefundId(Integer id);

    /**
     * 按退款编号重新生成Total数据
     * @param code 退款编号
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataBySoRefundCode(String code);

    /**
     * 按货权转移id重新生成Total数据
     * @param id 货权转移id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByCargoRightTransferId(Integer id);

    /**
     * 按货权转移编号重新生成Total数据
     * @param code 货权转移编号
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByCargoRightTransferCode(String code);

}