package com.xinyirun.scm.core.system.service.base.v1.common.bpm;

import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;

/**
 * BPM 通用回调接口
 * 
 * @author xinyirun
 */
public interface IBpmCommonCallBackService<T> {

    /**
     * BPM回调-创建流程时更新bpm实例汇总数据
     */
    UpdateResultAo<Integer> bpmCallBackCreateBpm(T searchCondition);

    /**
     * BPM回调-审批通过
     */
    UpdateResultAo<Integer> bpmCallBackApprove(T searchCondition);

    /**
     * BPM回调-审批拒绝
     */
    UpdateResultAo<Integer> bpmCallBackRefuse(T searchCondition);

    /**
     * BPM回调-审批取消
     */
    UpdateResultAo<Integer> bpmCallBackCancel(T searchCondition);

    /**
     * BPM回调-保存最新审批人
     */
    UpdateResultAo<Integer> bpmCallBackSave(T searchCondition);
}
