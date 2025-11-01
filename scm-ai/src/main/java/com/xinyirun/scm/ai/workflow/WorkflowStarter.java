package com.xinyirun.scm.ai.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEdgeEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import com.xinyirun.scm.ai.core.service.workflow.*;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    /**
     * self注入 - 用于调用@Async方法
     * 必须使用@Lazy避免循环依赖
     * 参考: aideepin WorkflowStarter Line 24-26
     */
    @Lazy
    @Resource
    private WorkflowStarter self;

    /**
     * StreamHandler临时缓存
     * Key: executionId (UUID)
     * Value: WorkflowStreamHandler
     * 用于在Flux.create和@Async方法之间传递StreamHandler
     */
    private final ConcurrentHashMap<String, WorkflowStreamHandler> handlerCache
            = new ConcurrentHashMap<>();

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
     * 流式执行工作流 - @Async优化版本
     *
     * <p>核心改进（2025-11-01）：</p>
     * <ul>
     *   <li>使用@Async异步执行工作流，避免Flux.create lambda阻塞</li>
     *   <li>Flux.create立即返回，workflowEngine.run()在独立线程执行</li>
     *   <li>解决Reactor缓冲事件导致的流式响应延迟问题</li>
     * </ul>
     *
     * <p>参考: aideepin WorkflowStarter.streaming() Line 50-65</p>
     *
     * @param workflowUuid 工作流UUID
     * @param userInputs 用户输入参数
     * @param tenantCode 租户编码（从Controller层传递，用于异步线程数据源切换）
     * @return Flux流式响应
     */
    public Flux<WorkflowEventVo> streaming(String workflowUuid, List<JSONObject> userInputs, String tenantCode) {
        Long userId = SecurityUtil.getStaff_id();
        String executionId = UUID.randomUUID().toString();

        // ✅ 创建Flux并立即返回（不阻塞）
        // ⭐【时序修复】改为同步订阅，确保FluxSink在asyncRunWorkflow之前创建
        Flux<WorkflowEventVo> flux = Flux.<WorkflowEventVo>create(fluxSink -> {
            // 创建工作流流式回调处理器
            WorkflowStreamHandler streamHandler = new WorkflowStreamHandler(
                    new WorkflowStreamHandler.StreamCallback() {
                        @Override
                        public void onStart(String runtimeData) {
                            fluxSink.next(WorkflowEventVo.createStartEvent(runtimeData));
                        }

                        @Override
                        public void onNodeRun(String nodeUuid, String nodeData) {
                            fluxSink.next(WorkflowEventVo.createNodeRunEvent(nodeUuid, nodeData));
                        }

                        @Override
                        public void onNodeInput(String nodeUuid, String inputData) {
                            fluxSink.next(WorkflowEventVo.createNodeInputEvent(nodeUuid, inputData));
                        }

                        @Override
                        public void onNodeOutput(String nodeUuid, String outputData) {
                            fluxSink.next(WorkflowEventVo.createNodeOutputEvent(nodeUuid, outputData));
                        }

                        @Override
                        public void onNodeChunk(String nodeUuid, String chunk) {
                            fluxSink.next(WorkflowEventVo.createNodeChunkEvent(nodeUuid, chunk));
                        }

                        @Override
                        public void onComplete(String data) {
                            fluxSink.next(WorkflowEventVo.createDoneEvent(data));
                            fluxSink.complete();
                        }

                        @Override
                        public void onError(Throwable error) {
                            fluxSink.error(error);
                        }
                    }
            );

            // ⭐ 存储streamHandler到缓存，供异步方法使用
            handlerCache.put(executionId, streamHandler);

            // ⭐【关键】在FluxSink创建后立即启动异步执行
            self.asyncRunWorkflow(executionId, workflowUuid, userId, userInputs, tenantCode);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .doFinally(signalType -> {
            // ⭐ 清理缓存和数据源上下文
            handlerCache.remove(executionId);
            DataSourceHelper.close();
        });

        return flux;
    }

    /**
     * 异步执行工作流（在独立线程中）
     *
     * <p>此方法使用@Async注解，在mainExecutor线程池中执行</p>
     * <p>核心职责：</p>
     * <ul>
     *   <li>切换到正确的租户数据源</li>
     *   <li>获取工作流配置和组件</li>
     *   <li>创建WorkflowEngine并执行</li>
     *   <li>通过StreamHandler实时发送事件</li>
     *   <li>异常处理和资源清理</li>
     * </ul>
     *
     * <p>参考: aideepin WorkflowStarter.asyncRun() Line 67-87</p>
     *
     * @param executionId 执行ID（用于从缓存获取StreamHandler）
     * @param workflowUuid 工作流UUID
     * @param userId 用户ID
     * @param userInputs 用户输入参数
     * @param tenantCode 租户编码
     */
    @Async("mainExecutor")
    public void asyncRunWorkflow(String executionId,
                                 String workflowUuid,
                                 Long userId,
                                 List<JSONObject> userInputs,
                                 String tenantCode) {
        try {
            // ⭐【多租户关键】在异步线程中切换到正确的数据源
            DataSourceHelper.use(tenantCode);

            // ⭐ 从缓存获取StreamHandler
            WorkflowStreamHandler streamHandler = handlerCache.get(executionId);
            if (streamHandler == null) {
                log.error("StreamHandler not found for execution: {}", executionId);
                return;
            }

            // 获取工作流配置
            AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

            // 检查工作流是否启用
            if (workflow.getIsEnable() == null || !workflow.getIsEnable()) {
                streamHandler.sendError(new BusinessException("工作流已禁用"));
                return;
            }

            log.info("WorkflowEngine run,userId:{},workflowUuid:{},tenantCode:{},userInputs:{}",
                    userId, workflow.getWorkflowUuid(), tenantCode, userInputs);

            // 获取工作流组件、节点、边配置
            List<AiWorkflowComponentEntity> components = workflowComponentService.getAllEnable();
            List<AiWorkflowNodeVo> nodes = workflowNodeService.listByWorkflowId(workflow.getId());
            List<AiWorkflowEdgeEntity> edges = workflowEdgeService.listByWorkflowId(workflow.getId());

            // 创建工作流引擎
            WorkflowEngine workflowEngine = new WorkflowEngine(
                    workflow,
                    streamHandler,
                    components,
                    nodes,
                    edges,
                    workflowRuntimeService,
                    workflowRuntimeNodeService
            );

            // ✅ 在独立线程中执行工作流（不阻塞Flux.create）
            // 参考: aideepin WorkflowStarter Line 86
            workflowEngine.run(userId, userInputs, tenantCode);

        } catch (Exception e) {
            log.error("工作流执行异常: workflowUuid={}, userId={}, executionId={}",
                    workflowUuid, userId, executionId, e);

            // 通过streamHandler发送错误
            WorkflowStreamHandler streamHandler = handlerCache.get(executionId);
            if (streamHandler != null) {
                streamHandler.sendError(e);
            }
        } finally {
            // ⭐ 清理数据源上下文
            DataSourceHelper.close();
        }
    }

    /**
     * 恢复中断的工作流
     *
     * @param runtimeUuid 运行实例UUID
     * @param userInput 用户输入
     */
    @Async("mainExecutor")
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
