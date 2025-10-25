package com.xinyirun.scm.ai.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEdgeEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.core.service.workflow.*;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

/**
 * 工作流启动器
 *
 * <p>负责工作流的流式执行和中断恢复</p>
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Slf4j
@Component
public class WorkflowStarter {

    private static final Long SSE_TIMEOUT = 60 * 60 * 1000L; // 1小时超时

    @Lazy
    @Resource
    private WorkflowStarter self;

    @Resource
    private AiWorkflowService workflowService;

    @Resource
    private AiWorkflowNodeService workflowNodeService;

    @Resource
    private AiWorkflowEdgeService workflowEdgeService;

    @Resource
    private AiWorkflowComponentService workflowComponentService;

    @Resource
    private AiWorkflowRuntimeService workflowRuntimeService;

    @Resource
    private AiWorkflowRuntimeNodeService workflowRuntimeNodeService;

    /**
     * 流式执行工作流
     *
     * @param workflowUuid 工作流UUID
     * @param userInputs 用户输入参数
     * @return SSE Emitter
     */
    public SseEmitter streaming(String workflowUuid, List<JSONObject> userInputs) {
        Long userId = SecurityUtil.getStaff_id();
        SseEmitter sseEmitter = new SseEmitter(SSE_TIMEOUT);

        // TODO: 检查用户并发限制
        // if (!sseEmitterHelper.checkOrComplete(user, sseEmitter)) {
        //     return sseEmitter;
        // }

        AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

        if (workflow.getIsEnable() == null || !workflow.getIsEnable()) {
            sendErrorAndComplete(userId, sseEmitter, "工作流已禁用");
            return sseEmitter;
        }

        self.asyncRun(userId, workflow, userInputs, sseEmitter);
        return sseEmitter;
    }

    /**
     * 异步执行工作流
     *
     * @param userId 用户ID
     * @param workflow 工作流实体
     * @param userInputs 用户输入
     * @param sseEmitter SSE Emitter
     */
    @Async
    public void asyncRun(Long userId, AiWorkflowEntity workflow,
                         List<JSONObject> userInputs, SseEmitter sseEmitter) {
        log.info("WorkflowEngine run,userId:{},workflowUuid:{},userInputs:{}",
                userId, workflow.getWorkflowUuid(), userInputs);

        try {
            List<AiWorkflowComponentEntity> components = workflowComponentService.getAllEnable();
            List<AiWorkflowNodeEntity> nodes = workflowNodeService.listByWorkflowId(workflow.getId());
            List<AiWorkflowEdgeEntity> edges = workflowEdgeService.listByWorkflowId(workflow.getId());

            WorkflowEngine workflowEngine = new WorkflowEngine(
                    workflow,
                    components,
                    nodes,
                    edges,
                    workflowRuntimeService,
                    workflowRuntimeNodeService
            );
            workflowEngine.run(userId, userInputs, sseEmitter);
        } catch (Exception e) {
            log.error("Workflow execution failed", e);
            sendErrorAndComplete(userId, sseEmitter, e.getMessage());
        }
    }

    /**
     * 恢复中断的工作流
     *
     * @param runtimeUuid 运行实例UUID
     * @param userInput 用户输入
     */
    @Async
    public void resumeFlow(String runtimeUuid, String userInput) {
        WorkflowEngine workflowEngine = InterruptedFlow.RUNTIME_TO_GRAPH.get(runtimeUuid);
        if (workflowEngine == null) {
            log.error("工作流恢复执行时失败,runtime:{}", runtimeUuid);
            throw new RuntimeException("工作流实例不存在或已超时");
        }

        // TODO: 实现恢复逻辑
        // workflowEngine.resume(userInput);
    }

    /**
     * 发送错误并完成SSE
     *
     * @param userId 用户ID
     * @param sseEmitter SSE Emitter
     * @param errorMsg 错误消息
     */
    private void sendErrorAndComplete(Long userId, SseEmitter sseEmitter, String errorMsg) {
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("error")
                    .data(errorMsg));
            sseEmitter.complete();
        } catch (Exception e) {
            log.error("发送SSE错误失败,userId:{}", userId, e);
        }
    }
}
