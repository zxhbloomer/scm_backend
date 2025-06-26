package com.xinyirun.scm.core.bpm.listener;

import com.xinyirun.scm.bean.bpm.vo.props.ApprovalProps;
import com.xinyirun.scm.bean.system.vo.business.bpm.BpmProcessHandlerParamsVo;
import com.xinyirun.scm.common.constant.DictConstant;
import com.xinyirun.scm.core.bpm.config.WflowGlobalVarDef;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.runtime.ChangeActivityStateBuilder;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.task.service.delegate.TaskListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户任务监听器
 * 限定于用户任务节点（UserTask）的特定生命周期事件
 * 细粒度（精确到任务创建、分配、完成等节点事件）
 */
@Slf4j
@Component("userTaskListener")
public class UserTaskListener implements TaskListener, ExecutionListener {

    private static final long serialVersionUID = -7269243963442138961L;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void notify(DelegateTask delegateTask) {
        //获取用户任务节点ID
        String nodeId = delegateTask.getTaskDefinitionKey();
        //assignment时间早于create触发，此时还没有创建成功task
        if ("create".equals(delegateTask.getEventName())) {
            //当任务被指派时，判断任务指派人是不是属于系统自动办理
            String assignee = delegateTask.getAssignee();
            if (WflowGlobalVarDef.WFLOW_TASK_AGRRE.equals(assignee)
                    || WflowGlobalVarDef.WFLOW_TASK_REFUSE.equals(assignee)) {
                boolean result = WflowGlobalVarDef.WFLOW_TASK_AGRRE.equals(assignee);
                Map<String, Object> var = new HashMap<>();
                var.put(WflowGlobalVarDef.APPROVE + delegateTask.getId(),
                        result ? BpmProcessHandlerParamsVo.Action.agree : BpmProcessHandlerParamsVo.Action.refuse);
                taskService.complete(delegateTask.getId(), var);
                log.info("无审批人任务节点[{}]，交付系统控制[审批结果 {}]", nodeId, result);
            } else {
//                processChangeNotify(delegateTask, false);
                log.info("实例[{}]的节点[{}]任务被指派给[{}]处理", delegateTask.getProcessInstanceId(), delegateTask.getTaskDefinitionKey(), assignee);
            }
        } else if ("complete".equals(delegateTask.getEventName())) {
            //当任务完成时，判断是不是驳回，驳回就执行驳回操作
             String action = delegateTask.getVariable(WflowGlobalVarDef.APPROVE+ delegateTask.getId(), String.class);
            /**
             * 20250117：错过一次，修复后能够执行。后来又错了，这次代码还原就ok了
             * 原代码：String action = delegateTask.getVariable(WflowGlobalVarDef.APPROVE+ delegateTask.getId(), String.class);
             * 抛出异常：Cannot cast com.xinyirun.scm.bean.system.vo.business.bpm.BpmProcessHandlerParamsVo$Action to java.lang.String
             * 此处应该有bug：action应该是枚举类型，而不是String类型
             *
             */
//             String action = delegateTask.getVariable(WflowGlobalVarDef.APPROVE+ delegateTask.getId(), BpmProcessHandlerParamsVo.Action.class).name();
            //获取当前执行实例
            if (DictConstant.DICT_SYS_CODE_BPM_INSTANCE_PROCESS_ACTION_REFUSE.equals(action)) {
                List<Execution> executions = runtimeService.createExecutionQuery()
                        .parentId(delegateTask.getProcessInstanceId())
                        .onlyChildExecutions().list();
                Map nodeProps = delegateTask.getVariable(WflowGlobalVarDef.WFLOW_NODE_PROPS, Map.class);
                ApprovalProps props = (ApprovalProps) nodeProps.get(nodeId);
                String target = "TO_NODE".equals(props.getRefuse().getType()) ? props.getRefuse().getTarget() : "refuse-end";
                //强制流程指向驳回/其他
                ChangeActivityStateBuilder builder = runtimeService.createChangeActivityStateBuilder()
                        .processInstanceId(delegateTask.getProcessInstanceId());
                if (executions.size() > 1) {
                    //多实例
                    builder.moveExecutionsToSingleActivityId(executions.stream().map(Execution::getId)
                            .collect(Collectors.toList()), target).changeState();
                } else {
                    builder.moveActivityIdTo(delegateTask.getTaskDefinitionKey(), target).changeState();
                }
                if ("TO_END".equals(props.getRefuse().getType())) {
//                    processChangeNotify(delegateTask, true);
                }
            }
            log.info("任务[{} - {}]由[{}]完成", delegateTask.getTaskDefinitionKey(), delegateTask.getName(), delegateTask.getAssignee());
        }
    }

    @Override
    public void notify(DelegateExecution execution) {
        log.info("执行监听触发[活动:{}]", execution.getCurrentActivityId());
    }

}
