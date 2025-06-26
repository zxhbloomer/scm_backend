package com.xinyirun.scm.core.bpm.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEntityEvent;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.delegate.event.AbstractFlowableEngineEventListener;
import org.flowable.engine.delegate.event.FlowableProcessStartedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 *
 * 貌似无法监听（暂定）,待删除
 * @author : willian fu
 * @date : 2022/8/27
 */
@Slf4j
@Component
public class GlobalTaskListener extends AbstractFlowableEngineEventListener {

    // 指定关注的事件类型（提升性能）
    @Autowired
    private RuntimeService runtimeService;

    @Override
    protected void processStarted(FlowableProcessStartedEvent event) {
        log.info("监听到流程[{}]启动", event.getNestedProcessDefinitionId());
        super.processStarted(event);
    }

    @Override
    protected void taskCreated(FlowableEngineEntityEvent event) {
        log.debug("监听到任务[{}]创建", event.getExecutionId());
        super.taskCreated(event);
    }

    @Override
    protected void taskCompleted(FlowableEngineEntityEvent event) {
        log.debug("监听到任务[{}]结束", event.getExecutionId());
        super.taskCompleted(event);
    }

    @Override
    protected void processCompleted(FlowableEngineEntityEvent event) {
        log.info("[{}]审批流程[{}}]通过", event.getProcessInstanceId());
        super.processCompleted(event);
    }

    @Override
    protected void processCompletedWithTerminateEnd(FlowableEngineEntityEvent event) {
        log.debug("监听到流程[{}]被驳回/撤销", event.getProcessInstanceId());
        super.processCompletedWithTerminateEnd(event);
    }
}
