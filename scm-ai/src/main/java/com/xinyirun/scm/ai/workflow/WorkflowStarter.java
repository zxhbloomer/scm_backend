package com.xinyirun.scm.ai.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEdgeEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.core.service.workflow.*;
import com.xinyirun.scm.ai.workflow.helper.SSEEmitterHelper;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
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

    @Resource
    private SSEEmitterHelper sseEmitterHelper;

    /**
     * 流式执行工作流
     *
     * @param workflowUuid 工作流UUID
     * @param userInputs 用户输入参数
     * @param tenantCode 租户编码（从Controller层传递，用于异步线程数据源切换）
     * @return SSE Emitter
     */
    public SseEmitter streaming(String workflowUuid, List<JSONObject> userInputs, String tenantCode) {
        Long userId = SecurityUtil.getStaff_id();
        SseEmitter sseEmitter = new SseEmitter(SSE_TIMEOUT);

        // 检查用户并发限制和限流
        if (!sseEmitterHelper.checkOrComplete(userId, sseEmitter)) {
            return sseEmitter;
        }

        AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

        if (workflow.getIsEnable() == null || !workflow.getIsEnable()) {
            sseEmitterHelper.sendErrorAndComplete(userId, sseEmitter, "工作流已禁用");
            return sseEmitter;
        }

        self.asyncRun(userId, workflow, userInputs, sseEmitter, tenantCode);
        return sseEmitter;
    }

    /**
     * 异步执行工作流
     *
     * @param userId 用户ID
     * @param workflow 工作流实体
     * @param userInputs 用户输入
     * @param sseEmitter SSE Emitter
     * @param tenantCode 租户编码（用于异步线程数据源切换）
     */
    @Async
    public void asyncRun(Long userId, AiWorkflowEntity workflow,
                         List<JSONObject> userInputs, SseEmitter sseEmitter, String tenantCode) {
        log.info("WorkflowEngine run,userId:{},workflowUuid:{},tenantCode:{},userInputs:{}",
                userId, workflow.getWorkflowUuid(), tenantCode, userInputs);

        // 【多租户关键】在异步线程中切换到正确的数据源
        // 参考 TenantDyanmicDataSourceInterceptor 第106行和第143行的标准用法
        DataSourceHelper.use(tenantCode);

        try {
            List<AiWorkflowComponentEntity> components = workflowComponentService.getAllEnable();
            List<AiWorkflowNodeEntity> nodes = workflowNodeService.listByWorkflowId(workflow.getId());
            List<AiWorkflowEdgeEntity> edges = workflowEdgeService.listByWorkflowId(workflow.getId());

            WorkflowEngine workflowEngine = new WorkflowEngine(
                    workflow,
                    sseEmitterHelper,
                    components,
                    nodes,
                    edges,
                    workflowRuntimeService,
                    workflowRuntimeNodeService
            );
            workflowEngine.run(userId, userInputs, sseEmitter);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
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
        // 参考 aideepin: WorkflowStarter.resumeFlow() 第90-97行
        WorkflowEngine workflowEngine = InterruptedFlow.RUNTIME_TO_GRAPH.get(runtimeUuid);
        if (workflowEngine == null) {
            log.error("工作流恢复执行时失败,runtime:{}", runtimeUuid);
            throw new RuntimeException("工作流实例不存在或已超时");
        }

        // 调用engine的resume方法恢复工作流
        // 参考 aideepin: WorkflowStarter.resumeFlow() 第96行
        workflowEngine.resume(userInput);
    }

}
