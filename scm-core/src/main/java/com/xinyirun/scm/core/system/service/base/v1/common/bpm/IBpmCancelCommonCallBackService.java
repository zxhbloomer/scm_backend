package com.xinyirun.scm.core.system.service.base.v1.common.bpm;

import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;

/**
 * BPM 作废通用回调接口
 * 
 * @author xinyirun
 */
public interface IBpmCancelCommonCallBackService<T> {

    /**
     * BPM作废回调-创建流程时更新bpm实例汇总数据
     */
    UpdateResultAo<Integer> bpmCancelCallBackCreateBpm(T searchCondition);

    /**
     * BPM作废回调-审批通过
     */
    UpdateResultAo<Integer> bpmCancelCallBackApprove(T searchCondition);
    /**
     * BPM作废回调-审批拒绝
     */
    UpdateResultAo<Integer> bpmCancelCallBackRefuse(T searchCondition);

    /**
     * BPM作废回调-审批取消
     */
    UpdateResultAo<Integer> bpmCancelCallBackCancel(T searchCondition);

    /**
     * BPM作废回调-保存最新审批人
     */
    UpdateResultAo<Integer> bpmCancelCallBackSave(T searchCondition);
}
