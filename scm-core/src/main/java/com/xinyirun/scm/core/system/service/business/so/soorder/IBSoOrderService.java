package com.xinyirun.scm.core.system.service.business.so.soorder;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.so.soorder.BSoOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.so.soorder.SoOrderVo;

import java.util.List;

/**
 * <p>
 * 采购订单表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-10
 */
public interface IBSoOrderService extends IService<BSoOrderEntity> {

    /**
     * 获取采购订单信息
     */
    SoOrderVo selectById(Integer id);

    /**
     * 采购订单  新增
     */
    InsertResultAo<SoOrderVo> startInsert(SoOrderVo searchCondition);

    /**
     * 采购订单校验
     */
    CheckResultAo checkLogic(SoOrderVo searchCondition, String checkType);

    /**
     * 分页查询
     */
    IPage<SoOrderVo> selectPage(SoOrderVo searchCondition);

    /**
     * 采购订单 统计
     */
    SoOrderVo querySum(SoOrderVo searchCondition);

    /**
     * 更新采购合同信息
     */
    UpdateResultAo<Integer> startUpdate(SoOrderVo searchCondition);

    /**
     * 审批流程回调
     */
    UpdateResultAo<Integer> bpmCallBackCreateBpm(SoOrderVo searchCondition);

    /**
     *  审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackApprove(SoOrderVo searchCondition);

    /**
     *  审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackRefuse(SoOrderVo searchCondition);

    /**
     *  审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackCancel(SoOrderVo searchCondition);

    /**
     *  企业管理审批流程回调
     *  审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackSave(SoOrderVo searchCondition);

    /**
     * 删除采购订单信息
     */
    DeleteResultAo<Integer> delete(List<SoOrderVo> searchCondition);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    SoOrderVo getPrintInfo(SoOrderVo searchCondition);

    /**
     * 报表导出
     */
    List<SoOrderVo> selectExportList(SoOrderVo param);

    /**
     * 作废审批流程摘要
     */
    UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(SoOrderVo searchCondition);

    /**
     *  作废审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackApprove(SoOrderVo searchCondition);

    /**
     *  作废审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackRefuse(SoOrderVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackCancel(SoOrderVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackSave(SoOrderVo searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(SoOrderVo searchCondition);
}
