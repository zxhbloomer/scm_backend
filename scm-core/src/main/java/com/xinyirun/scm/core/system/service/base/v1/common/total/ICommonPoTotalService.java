package com.xinyirun.scm.core.system.service.base.v1.common.total;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.po.pocontract.BPoContractTotalEntity;
import com.xinyirun.scm.bean.system.bo.fund.total.TotalDataRecalculateBo;

/**
 * <p>
 * 采购合同表-财务数据汇总 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-06-11
 */
public interface ICommonPoTotalService extends IService<BPoContractTotalEntity> {

    /**
     * 重新计算所有的财务数据
     * 同步计算
     *
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalData(TotalDataRecalculateBo bo);

    /**
     * 按采购合同编号重新生成Total数据
     * @param code 采购合同编号
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByPoContractCode(String code);

    /**
     * 按采购合同id重新生成Total数据
     * @param id 采购合同id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByPoContractId(Integer id);

    /**
     * 按采购订单编号重新生成Total数据
     * @param code 采购订单编号
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByPoOrderCode(String code);

    /**
     * 按采购订单id重新生成Total数据
     * @param id 采购订单id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByPoOrderId(Integer id);

    /**
     * 按应付账款id重新生成Total数据
     * @param id 应付账款id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByApId(Integer id);

    /**
     * 按应付账款编号重新生成Total数据
     * @param code 应付账款编号
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByApCode(String code);

    /**
     * 按付款单id重新生成Total数据
     * @param id 付款单id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByApPayId(Integer id);

    /**
     * 按付款单编号重新生成Total数据
     * @param code 付款单编号
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByApPayCode(String code);

    /**
     * 按入库计划id重新生成Total数据
     * @param id 入库计划id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByPlanId(Integer id);

    /**
     * 按入库单id重新生成Total数据
     * @param id 入库单id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByInboundId(Integer id);

    /**
     * 按采购结算id重新生成Total数据
     * @param id 采购结算id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByPoSettlementId(Integer id);

    /**
     * 按退款id重新生成Total数据
     * @param id 退款id
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByPoRefundId(Integer id);

    /**
     * 按退款编号重新生成Total数据
     * @param code 退款编号
     * @return 是否操作成功
     */
    Boolean reCalculateAllTotalDataByPoRefundCode(String code);

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
