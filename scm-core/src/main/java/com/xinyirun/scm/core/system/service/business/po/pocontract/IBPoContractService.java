package com.xinyirun.scm.core.system.service.business.po.pocontract;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.po.pocontract.BPoContractEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.PoContractImportVo;
import com.xinyirun.scm.bean.system.vo.business.po.pocontract.PoContractVo;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCancelCommonCallBackService;
import com.xinyirun.scm.core.system.service.base.v1.common.bpm.IBpmCommonCallBackService;

import java.util.List;

/**
 * <p>
 * 采购合同表 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-14
 */
public interface IBPoContractService extends IService<BPoContractEntity> ,
        IBpmCommonCallBackService<PoContractVo>,
        IBpmCancelCommonCallBackService<PoContractVo> {

    /**
     * 采购合同  新增
     */
    InsertResultAo<PoContractVo> startInsert(PoContractVo poContractVo);

    /**
     * 分页查询
     */
    IPage<PoContractVo> selectPage(PoContractVo searchCondition);

    /**
     * 获取采购合同信息
     */
    PoContractVo selectById(Integer id);

    /**
     * 更新采购合同信息
     */
    UpdateResultAo<Integer> startUpdate(PoContractVo poContractVo);

    /**
     * 删除采购合同信息
     */
    DeleteResultAo<Integer> delete(List<PoContractVo> searchCondition);

    /**
     * 按采购合同合计
     */
    PoContractVo querySum(PoContractVo searchCondition);

    /**
     * 采购合同校验
     */
    CheckResultAo checkLogic(PoContractVo bean, String checkType);



    /**
     * 获取报表系统参数，并组装打印参数
     */
    PoContractVo getPrintInfo(PoContractVo searchCondition);

    /**
     * 导出查询
     */
    List<PoContractVo> selectExportList(PoContractVo param);

    /**
     * 作废审批流程摘要
     */
    UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(PoContractVo searchCondition);

    /**
     *  作废审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackApprove(PoContractVo searchCondition);

    /**
     *  作废审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackRefuse(PoContractVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackCancel(PoContractVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackSave(PoContractVo searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<Integer> cancel(PoContractVo searchCondition);

    /**
     * 完成
     */
    UpdateResultAo<Integer> finish(PoContractVo searchCondition);

    /**
     * 导入数据
     */
    List<PoContractImportVo> importData(List<PoContractImportVo
            > beans);
}
