package com.xinyirun.scm.core.system.service.business.po.poorder;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.business.po.poorder.BPoOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.BPoOrderDetailVo;
import com.xinyirun.scm.bean.system.vo.business.po.poorder.BPoOrderVo;

import java.util.List;

/**
 * <p>
 * 采购订单表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-10
 */
public interface IBPoOrderService extends IService<BPoOrderEntity> {

    /**
     * 获取采购订单信息
     */
    BPoOrderVo selectById(Integer id);

    /**
     * 采购订单  新增
     */
    InsertResultAo<BPoOrderVo> startInsert(BPoOrderVo searchCondition);

    /**
     * 采购订单校验
     */
    CheckResultAo checkLogic(BPoOrderVo searchCondition, String checkType);

    /**
     * 分页查询
     */
    IPage<BPoOrderVo> selectPage(BPoOrderVo searchCondition);

    /**
     * 按退款条件分页查询
     */
    IPage<BPoOrderVo> selectPageByAprefund(BPoOrderVo searchCondition);

    /**
     * 采购订单 统计
     */
    BPoOrderVo querySum(BPoOrderVo searchCondition);

    /**
     * 按退款条件汇总查询
     */
    BPoOrderVo querySumByAprefund(BPoOrderVo searchCondition);

    /**
     * 更新采购合同信息
     */
    UpdateResultAo<Integer> startUpdate(BPoOrderVo searchCondition);

    /**
     * 审批流程回调
     */
    UpdateResultAo<Integer> bpmCallBackCreateBpm(BPoOrderVo searchCondition);


    /**
     *  审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackApprove(BPoOrderVo searchCondition);

    /**
     *  审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackRefuse(BPoOrderVo searchCondition);

    /**
     *  审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackCancel(BPoOrderVo searchCondition);

    /**
     *  企业管理审批流程回调
     *  审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackSave(BPoOrderVo searchCondition);

    /**
     * 删除采购订单信息
     */
    DeleteResultAo<Integer> delete(List<BPoOrderVo> searchCondition);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BPoOrderVo getPrintInfo(BPoOrderVo searchCondition);

    /**
     * 导出查询
     */
    List<BPoOrderVo> selectExportList(BPoOrderVo param);


    /**
     * 作废审批流程摘要
     */
    UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BPoOrderVo searchCondition);

    /**
     *  作废审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackApprove(BPoOrderVo searchCondition);

    /**
     *  作废审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackRefuse(BPoOrderVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackCancel(BPoOrderVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackSave(BPoOrderVo searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(BPoOrderVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(BPoOrderVo searchCondition);

    /**
     * 分页查询包含结算信息
     */
    IPage<BPoOrderVo> selectOrderListWithSettlePage(BPoOrderVo searchCondition);

    /**
     * 采购订单结算信息统计
     */
    BPoOrderVo queryOrderListWithSettlePageSum(BPoOrderVo searchCondition);

    /**
     * 货权转移专用-分页查询采购订单信息
     */
    IPage<BPoOrderVo> selectOrderListForCargoRightTransferPage(BPoOrderVo searchCondition);

    /**
     * 货权转移专用-采购订单统计
     */
    BPoOrderVo queryOrderListForCargoRightTransferPageSum(BPoOrderVo searchCondition);

    /**
     * 货权转移专用-获取采购订单明细数据
     */
    List<BPoOrderDetailVo> selectDetailData(BPoOrderVo searchCondition);
}
