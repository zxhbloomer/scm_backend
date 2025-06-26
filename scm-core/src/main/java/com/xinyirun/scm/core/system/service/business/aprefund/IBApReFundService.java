package com.xinyirun.scm.core.system.service.business.aprefund;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.bean.entity.busniess.ap.BApEntity;
import com.xinyirun.scm.bean.entity.busniess.aprefund.BApReFundEntity;
import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;
import com.xinyirun.scm.bean.system.vo.business.aprefund.BApReFundVo;

import java.util.List;

/**
 * <p>
 * 应付退款管理表（Accounts Payable） 服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-02-26
 */
public interface IBApReFundService extends IService<BApReFundEntity> {

    /**
     * 获取业务类型
     */
    List<BApReFundVo> getType();

    /**
     * 新增
     */
    InsertResultAo<BApReFundVo> startInsert(BApReFundVo searchCondition);

    /**
     * 更新
     */
    UpdateResultAo<BApReFundVo> startUpdate(BApReFundVo searchCondition);

    /**
     * 分页查询
     */
    IPage<BApReFundVo> selectPage(BApReFundVo searchCondition);

    /**
     * 根据id查询
     */
    BApReFundVo selectById(Integer id);

    /**
     * 校验
     */
    CheckResultAo checkLogic(BApReFundVo searchCondition, String checkType);

    /**
     * 审批流程回调
     */
    UpdateResultAo<Integer> bpmCallBackCreateBpm(BApReFundVo searchCondition);

    /**
     *  审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackApprove(BApReFundVo searchCondition);

    /**
     *  审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackRefuse(BApReFundVo searchCondition);

    /**
     *  审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCallBackCancel(BApReFundVo searchCondition);

    /**
     *  审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCallBackSave(BApReFundVo searchCondition);

    /**
     * 导出查询
     */
    List<BApReFundVo> selectExportList(BApReFundVo param);

    /**
     * 获取报表系统参数，并组装打印参数
     */
    BApReFundVo getPrintInfo(BApReFundVo searchCondition);

    /**
     * 删除
     */
    DeleteResultAo<Integer> delete(List<BApReFundVo> searchCondition);

    /**
     * 作废
     */
    UpdateResultAo<BApReFundVo> cancel(BApReFundVo searchCondition);


    /**
     * 中止付款
     */
    UpdateResultAo<Integer> suspendPayment(BApReFundVo searchCondition);

    /**
     * 作废审批流程摘要
     */
    UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(BApReFundVo searchCondition);

    /**
     *  作废审批流程通过 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackApprove(BApReFundVo searchCondition);

    /**
     *  作废审批流程拒绝 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackRefuse(BApReFundVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态驳回
     */
    UpdateResultAo<Integer> bpmCancelCallBackCancel(BApReFundVo searchCondition);

    /**
     *  作废审批流程撤销 更新审核状态通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackSave(BApReFundVo searchCondition);
}
