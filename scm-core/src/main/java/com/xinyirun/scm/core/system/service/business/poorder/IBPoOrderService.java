package com.xinyirun.scm.core.system.service.business.poorder;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.poorder.BPoOrderEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.poorder.PoOrderVo;

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
    PoOrderVo selectById(Integer id);

    /**
     * 采购订单  新增
     */
    InsertResultAo<PoOrderVo> startInsert(PoOrderVo searchCondition);

    /**
     * 采购订单校验
     */
    CheckResultAo checkLogic(PoOrderVo searchCondition, String checkType);

    /**
     * 分页查询
     */
    IPage<PoOrderVo> selectPage(PoOrderVo searchCondition);

    /**
     * 采购订单 统计
     */
    PoOrderVo querySum(PoOrderVo searchCondition);

    /**
     * 更新采购合同信息
     */
    UpdateResultAo<Integer> startUpdate(PoOrderVo searchCondition);

    /**
     * 审批流程回调
     */
    UpdateResultAo<Integer> bpmCallBackCreateBpm(PoOrderVo searchCondition);


    /**
     *  审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackApprove(PoOrderVo searchCondition);

    /**
     *  审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackRefuse(PoOrderVo searchCondition);

    /**
     *  审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackCancel(PoOrderVo searchCondition);

    /**
     *  企业管理审批流程回调
     *  审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackSave(PoOrderVo searchCondition);

    /**
     * 删除采购订单信息
     */
    DeleteResultAo<Integer> delete(List<PoOrderVo> searchCondition);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    PoOrderVo getPrintInfo(PoOrderVo searchCondition);

    /**
     * 导出查询
     */
    List<PoOrderVo> selectExportList(PoOrderVo param);


    /**
     * 作废审批流程摘要
     */
    UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(PoOrderVo searchCondition);

    /**
     *  作废审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackApprove(PoOrderVo searchCondition);

    /**
     *  作废审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackRefuse(PoOrderVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackCancel(PoOrderVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackSave(PoOrderVo searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(PoOrderVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(PoOrderVo searchCondition);
}
