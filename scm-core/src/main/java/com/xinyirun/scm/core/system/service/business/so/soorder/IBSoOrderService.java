package com.xinyirun.scm.core.system.service.business.so.soorder;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.so.soorder.BSoOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.BSoOrderVo;

import java.util.List;

/**
 * <p>
 * 销售订单表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-07-23
 */
public interface IBSoOrderService extends IService<BSoOrderEntity> {

    /**
     * 获取销售订单信息
     */
    BSoOrderVo selectById(Integer id);

    /**
     * 销售订单  新增
     */
    InsertResultAo<BSoOrderVo> startInsert(BSoOrderVo searchCondition);

    /**
     * 销售订单校验
     */
    CheckResultAo checkLogic(BSoOrderVo searchCondition, String checkType);

    /**
     * 分页查询
     */
    IPage<BSoOrderVo> selectPage(BSoOrderVo searchCondition);

    /**
     * 按应收退款条件分页查询
     */
    IPage<BSoOrderVo> selectPageByArrefund(BSoOrderVo searchCondition);

    /**
     * 销售订单 统计
     */
    BSoOrderVo querySum(BSoOrderVo searchCondition);

    /**
     * 按应收退款条件汇总查询
     */
    BSoOrderVo querySumByArrefund(BSoOrderVo searchCondition);

    /**
     * 更新销售合同信息
     */
    UpdateResultAo<Integer> startUpdate(BSoOrderVo searchCondition);

    /**
     * 审批流程回调
     */
    UpdateResultAo<Integer> bpmCallBackCreateBpm(BSoOrderVo searchCondition);


    /**
     *  审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackApprove(BSoOrderVo searchCondition);

    /**
     *  审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackRefuse(BSoOrderVo searchCondition);

    /**
     *  审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackCancel(BSoOrderVo searchCondition);

    /**
     *  企业管理审批流程回调
     *  审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackSave(BSoOrderVo searchCondition);

    /**
     * 删除销售订单信息
     */
    DeleteResultAo<Integer> delete(List<BSoOrderVo> searchCondition);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BSoOrderVo getPrintInfo(BSoOrderVo searchCondition);

    /**
     * 导出查询
     */
    List<BSoOrderVo> selectExportList(BSoOrderVo param);


    /**
     * 作废审批流程摘要
     */
    UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BSoOrderVo searchCondition);

    /**
     *  作废审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackApprove(BSoOrderVo searchCondition);

    /**
     *  作废审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackRefuse(BSoOrderVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackCancel(BSoOrderVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackSave(BSoOrderVo searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BSoOrderVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BSoOrderVo searchCondition);

    /**
     * 分页查询包含结算信息
     */
    IPage<BSoOrderVo> selectOrderListWithSettlePage(BSoOrderVo searchCondition);

    /**
     * 销售订单结算信息统计
     */
    BSoOrderVo queryOrderListWithSettlePageSum(BSoOrderVo searchCondition);

    /**
     * 货权转移专用-分页查询销售订单信息
     */
    IPage<BSoOrderVo> selectOrderListForCargoRightTransferPage(BSoOrderVo searchCondition);

    /**
     * 货权转移专用-销售订单统计
     */
    BSoOrderVo queryOrderListForCargoRightTransferPageSum(BSoOrderVo searchCondition);

    /**
     * 货权转移专用-获取销售订单明细数据
     */
    List<BSoOrderDetailVo> selectDetailData(BSoOrderVo searchCondition);
}