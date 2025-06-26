package com.xinyirun.scm.core.bpm.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.bpm.vo.props.ApprovalProps;
import com.xinyirun.scm.bean.system.vo.business.bpm.BpmProcessHandlerParamsVo;
import com.xinyirun.scm.core.bpm.config.WflowGlobalVarDef;
import com.xinyirun.scm.core.bpm.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.common.engine.impl.identity.Authentication;
import org.flowable.engine.HistoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.flowable.engine.history.HistoricProcessInstance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 审批超时服务任务
 *
 * @author : willian fu
 * @date : 2022/9/12
 */
@Slf4j
public class ApprovalTimeoutServiceTask implements JavaDelegate {

    private static TaskService taskService;

    private static HistoryService historyService;

//    private static NotifyService notifyService;

    public ApprovalTimeoutServiceTask() {
        taskService = BeanUtil.getBean(TaskService.class);
        historyService = BeanUtil.getBean(HistoryService.class);
//        notifyService = BeanUtil.getBean(NotifyService.class);
    }

    @Override
    public void execute(DelegateExecution execution) {
        //执行审批超期逻辑
        FlowElement element = execution.getCurrentFlowElement();
        String[] split = element.getId().split("-");
        Map variable = execution.getVariable(WflowGlobalVarDef.WFLOW_NODE_PROPS, Map.class);
        ApprovalProps props = (ApprovalProps) variable.get(split[0]);
        ApprovalProps.TimeLimit timeLimit = props.getTimeLimit();
        switch (timeLimit.getHandler().getType()) {
            case PASS: //自动代替审批人处理同意审批
                handlerApprovalTask(execution.getProcessInstanceId(), split[0], true);
                break;
            default: //自动代替审批人处理拒绝审批
                handlerApprovalTask(execution.getProcessInstanceId(), split[0], false);
                break;
        }
    }

    /**
     * 系统自动处理审批任务
     *
     * @param instanceId 实例ID
     * @param agree      是否同意
     */
    private void handlerApprovalTask(String instanceId, String nodeId, Boolean agree) {
        taskService.createTaskQuery().processInstanceId(instanceId)
                .taskDefinitionKey(nodeId).active().list().forEach(task -> {
                    try {
                        String assignee = task.getAssignee();
                        Authentication.setAuthenticatedUserId(assignee);
                        Map<String, Object> var = new HashMap<>();
                        var.put(WflowGlobalVarDef.APPROVE + task.getId(),
                                agree ? BpmProcessHandlerParamsVo.Action.agree : BpmProcessHandlerParamsVo.Action.refuse);
                        taskService.complete(task.getId(), var);
                        log.info("审批实例[{}] 节点[{}] 审批人[{}]处理[{}]超时, 自动{}", instanceId, task.getTaskDefinitionKey(), assignee, task.getId(), agree ? "同意" : "驳回");
                        taskService.addComment(task.getId(), instanceId, JSONObject.toJSONString(new BpmProcessHandlerParamsVo.ProcessComment("审批超时，系统自动处理", Collections.emptyList())));
                        Authentication.setAuthenticatedUserId(null);
                    } catch (Exception ignored) {}
                });

    }

//    /**
//     * 发送消息通知
//     *
//     * @param execution 执行实例
//     */
//    private void sendNotify(DelegateExecution execution) {
//        HistoricProcessInstance instance = historyService.createHistoricProcessInstanceQuery()
//                .processInstanceId(execution.getProcessInstanceId()).singleResult();
//        historyService.createHistoricTaskInstanceQuery().processInstanceId(execution.getProcessInstanceId())
//                .unfinished().list().forEach(task -> {
//                    String assignee = task.getAssignee();
//                    notifyService.notify(NotifyDto.builder()
//                            .title("审批超时提醒")
//                            .processDefId(execution.getProcessDefinitionId())
//                            .instanceId(execution.getProcessInstanceId())
//                            .target(assignee)
//                            .content(StrUtil.builder("您有一项【",
//                                    instance.getProcessDefinitionName(),
//                                    "】审批任务已超时，请即时处理").toString())
//                            .type(NotifyDto.TypeEnum.WARNING)
//                            .build());
//                    log.info("审批[{}]超时催办通知，您[{}]有一条审批任务[{}]等待处理", instance.getId(), assignee, task.getName());
//                });
//    }
}
