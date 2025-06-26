package com.xinyirun.scm.core.system.service.business.socontract;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.socontract.BSoContractEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.socontract.SoContractVo;

import java.util.List;

/**
 * <p>
 * 销售合同表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
public interface IBSoContractService extends IService<BSoContractEntity> {


    /**
     * 销售合同  新增
     */
    InsertResultAo<SoContractVo> startInsert(SoContractVo SoContractVo);

    /**
     * 分页查询
     */
    IPage<SoContractVo> selectPage(SoContractVo searchCondition);

    /**
     * 获取销售合同信息
     */
    SoContractVo selectById(Integer id);

    /**
     * 更新销售合同信息
     */
    UpdateResultAo<Integer> startUpdate(SoContractVo SoContractVo);

    /**
     * 删除销售合同信息
     */
    DeleteResultAo<Integer> delete(List<SoContractVo> searchCondition);

    /**
     * 按销售合同合计
     */
    SoContractVo querySum(SoContractVo searchCondition);

    /**
     * 销售合同校验
     */
    CheckResultAo checkLogic(SoContractVo bean, String checkType);

    /**
     * 审批流程回调 更新bpm_instance的摘要数据
     */
    UpdateResultAo<Integer> bpmCallBackCreateBpm(SoContractVo searchCondition);

    /**
     *  审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackApprove(SoContractVo searchCondition);

    /**
     *  审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackRefuse(SoContractVo searchCondition);

    /**
     *  审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackCancel(SoContractVo searchCondition);

    /**
     *  更新最新审批人
     */
    UpdateResultAo<Integer> bpmCallBackSave(SoContractVo searchCondition);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    SoContractVo getPrintInfo(SoContractVo searchCondition);

    /**
     * 导出查询
     */
    List<SoContractVo> selectExportList(SoContractVo param);

    /**
     * 作废审批流程摘要
     */
    UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(SoContractVo searchCondition);

    /**
     *  作废审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackApprove(SoContractVo searchCondition);

    /**
     *  作废审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackRefuse(SoContractVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackCancel(SoContractVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackSave(SoContractVo searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(SoContractVo searchCondition);
}
