package com.xinyirun.scm.core.bpm.listener;

import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.core.bpm.service.business.IBpmInstanceService;
import com.xinyirun.scm.core.bpm.service.business.notice.IBpmBNoticeService;
import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEventListener;
import org.flowable.common.engine.impl.cfg.TransactionState;
import org.flowable.common.engine.impl.event.FlowableEntityEventImpl;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 审批流任务监听器
 * 全局流程引擎事件（覆盖全类型任务/执行事件）
 * 粗粒度（可监听流程实例、任务、执行等全局事件）
 *
 */

@Slf4j
@Component
public class GlobalTaskEventListener implements FlowableEventListener {

    @Autowired
    private IBpmInstanceService iBpmInstanceService;

    @Autowired
    private IBpmBNoticeService iBpmBNoticeService;

    @Override
    public void onEvent(FlowableEvent event) {
        log.debug("onEvent: {}", event.getType());

        // 判断事件类型
        if (event.getType() == FlowableEngineEventType.TASK_CREATED) {
            // 处理任务创建事件
            try {
                handleTaskCreatedEvent(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (event.getType() == FlowableEngineEventType.TASK_COMPLETED) {
            // 处理任务完成事件 同意/拒绝都在这里操作，通过获取变量判断
            try {
                handleTaskCompletedEvent(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if (event.getType() == FlowableEngineEventType.TASK_OWNER_CHANGED) {
            // 处理任务转交事件
            try {
                handleTransferCompletedEvent(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (event.getType() == FlowableEngineEventType.PROCESS_COMPLETED) {
            // 流程实例完成事件
            handleProcessInstanceCompletedEvent(event);
        } else if(event.getType() == FlowableEngineEventType.PROCESS_STARTED) {
            // 审批流启动时间
            try {
                handleProcessStartedEvent(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (event.getType() == FlowableEngineEventType.PROCESS_COMPLETED_WITH_TERMINATE_END_EVENT) {
            // 审批流撤销事件
            System.out.println("审批流撤销事件");
            try {
                handleTaskCompletedWithCancelEvent(event);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    /**
     * 处理任务转交事件
     */
    public void handleTransferCompletedEvent(FlowableEvent event) throws Exception {
        if (event instanceof FlowableEntityEventImpl) {
            FlowableEntityEventImpl entityEvent = (FlowableEntityEventImpl) event;
            TaskEntity task = (TaskEntity) entityEvent.getEntity();
            log.debug("处理任务转交事件：{}", task);

            // 保存待办任务数据 以及 更新流程节点信息
            iBpmInstanceService.saveTaskTransfer(task);
        } else {
            throw new BusinessException("处理任务完成事件类型错误");
        }
    }


    /**
     * 处理任务创建事件
     */
    public void handleTaskCreatedEvent(FlowableEvent event) throws Exception {
        if (event instanceof FlowableEntityEventImpl) {
            FlowableEntityEventImpl entityEvent = (FlowableEntityEventImpl) event;
            TaskEntity task = (TaskEntity) entityEvent.getEntity();
            log.debug("处理任务创建事件：{}", task);

            // 保存待办任务数据 以及 更新流程节点信息
            iBpmInstanceService.saveTaskCreated(task);
        } else {
            throw new BusinessException("处理任务完成事件类型错误");
        }
    }

    /**
     * 处理任务完成事件
     */
    public void handleTaskCompletedEvent(FlowableEvent event) throws Exception {
        if (event instanceof org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl) {
            org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl entityEvent = (org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl) event;
            TaskEntity task = (TaskEntity) entityEvent.getEntity();
            log.debug("处理任务事件task：{}", task);

            // 更新待办任务状态为已完成
            iBpmInstanceService.saveTaskCompleted(task);

            // 消息通知
            iBpmBNoticeService.sendBpmTodoNotice(task);
        } else {
            throw new BusinessException("任务完成事件类型错误");
        }
    }

    /**
     * 审批流撤销事件
     */
    public void handleTaskCompletedWithCancelEvent(FlowableEvent event) throws Exception {
        if (event instanceof org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl) {
            org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl entityEvent = (org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl) event;
            ProcessInstance processInstance = (ProcessInstance) entityEvent.getEntity();
            log.debug("处理审批流撤销事件task：{}", processInstance);

            // 消息通知
            iBpmBNoticeService.sendBpTerminateNotice(processInstance);
        } else {
            throw new BusinessException("任务完成事件类型错误");
        }
    }



    /**
     * 处理任务完成事件
     */
    public void handleProcessStartedEvent(FlowableEvent event) throws Exception {
        if (event instanceof org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl) {
            org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl entityEvent = (org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl) event;
            ProcessInstance processInstance = (ProcessInstance) entityEvent.getEntity();
            log.debug("流程实例完成事件：{}", processInstance);

            iBpmInstanceService.processStarted(processInstance);
        } else {
            throw new BusinessException("任务完成事件类型错误");
        }
    }

    /**
     * 流程实例完成事件
     */
    public void handleProcessInstanceCompletedEvent(FlowableEvent event) {
        if (event instanceof org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl) {
            org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl entityEvent = (org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl) event;
            ProcessInstance processInstance = (ProcessInstance) entityEvent.getEntity();
            log.debug("流程实例完成事件：{}", processInstance);

            iBpmInstanceService.saveProcessInstanceCompleted(processInstance);

            // 消息通知
            iBpmBNoticeService.sendBpmPassNotice(processInstance);
        } else {
            throw new BusinessException("流程实例完成事件类型错误");
        }
    }

    // 其他方法...

    @Override
    public boolean isFailOnException() {
        // 返回 false，异常不会影响流程执行
        return false;
    }

    @Override
    public boolean isFireOnTransactionLifecycleEvent() {
        return true;
    }

    @Override
    public String getOnTransaction() {
        //事务提交后触发
        return TransactionState.COMMITTED.name();
    }
}