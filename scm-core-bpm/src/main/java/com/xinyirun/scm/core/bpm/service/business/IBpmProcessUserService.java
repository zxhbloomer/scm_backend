package com.xinyirun.scm.core.bpm.service.business;

import com.xinyirun.scm.bean.system.vo.master.user.MStaffVo;

import java.util.List;

/**
 * <p>
 * BPM审批流人员查询服务类
 * </p>
 *
 * @author xinyirun
 * @since 2025-01-28
 */
public interface IBpmProcessUserService {

    /**
     * 获取所有使用中审批流的参与人员
     * @return 所有审批流参与人员编码列表
     */
    List<String> getAllProcessUsers();

    /**
     * 根据deployment_id解析processKey
     * @param deploymentId Flowable部署ID，格式如：FlowableB_SO_ORDER_CANCEL:3:1899007012641820672
     * @return processKey，如：B_SO_ORDER_CANCEL
     */
    String extractProcessKey(String deploymentId);

    /**
     * 获取指定审批流的参与人员
     * @param processKey 流程定义Key
     * @return 该审批流的参与人员列表
     */
    List<MStaffVo> getProcessUsersByKey(String processKey);

    /**
     * 获取所有待办任务的处理人员
     * @return 待办任务处理人员编码列表
     */
    List<String> getTodoAssigneeUsers();
}