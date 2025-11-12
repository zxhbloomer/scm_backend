package com.xinyirun.scm.ai.workflow;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEdgeEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import com.xinyirun.scm.ai.common.constant.WorkflowCallSource;
import com.xinyirun.scm.ai.core.service.workflow.*;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.exception.system.BusinessException;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 工作流启动器
 *
 * <p>负责工作流的流式执行和中断恢复</p>
 *
 * @author zxh
 * @since 2025-10-21
 */
@Slf4j
@Component
public class WorkflowStarter {

    /**
     * self注入 - 用于调用@Async方法
     * 必须使用@Lazy避免循环依赖
     */
    @Lazy
    @Resource
    private WorkflowStarter self;

    /**
     * StreamHandler缓存
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

    @Resource
    private AiConversationWorkflowRuntimeService conversationWorkflowRuntimeService;

    @Resource
    private AiConversationWorkflowRuntimeNodeService conversationWorkflowRuntimeNodeService;

    @Resource(name = "mainExecutor")
    private ThreadPoolTaskExecutor mainExecutor;

    /**
     * 流式执行工作流
     *
     * 使用@Async异步执行工作流，避免阻塞Flux流。
     * Flux.create立即返回，workflowEngine.run()在独立线程执行。
     *
     * @param workflowUuid 工作流UUID
     * @param userInputs 用户输入参数
     * @param tenantCode 租户编码
     * @param callSource 调用来源标识 (WORKFLOW_TEST 或 AI_CHAT)
     * @return Flux流式响应
     */
    public Flux<WorkflowEventVo> streaming(String workflowUuid, List<JSONObject> userInputs, String tenantCode, WorkflowCallSource callSource) {
        Long userId = SecurityUtil.getStaff_id();
        String executionId = UUID.randomUUID().toString();

        // 创建Flux并立即返回(不阻塞)
        // 改为同步订阅,确保FluxSink在asyncRunWorkflow之前创建
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
                        public void onNodeWaitFeedback(String nodeUuid, String tip) {
                            fluxSink.next(WorkflowEventVo.createNodeWaitFeedbackEvent(nodeUuid, tip));
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

            // 存储streamHandler到缓存,供异步方法使用
            handlerCache.put(executionId, streamHandler);

            // 在FluxSink创建后立即启动异步执行
            self.asyncRunWorkflow(executionId, workflowUuid, userId, userInputs, tenantCode, callSource);
        })
        .subscribeOn(Schedulers.boundedElastic())
        .doFinally(signalType -> {
            // 清理缓存和数据源上下文
            handlerCache.remove(executionId);
            DataSourceHelper.close();
        });

        return flux;
    }

    /**
     * 异步执行工作流
     *
     * 此方法在独立线程池中执行，负责工作流的实际运行。
     * 包括数据源切换、配置加载、引擎创建和执行。
     *
     * @param executionId 执行ID
     * @param workflowUuid 工作流UUID
     * @param userId 用户ID
     * @param userInputs 用户输入参数
     * @param tenantCode 租户编码
     * @param callSource 调用来源标识 (WORKFLOW_TEST 或 AI_CHAT)
     */
    @Async("mainExecutor")
    public void asyncRunWorkflow(String executionId,
                                 String workflowUuid,
                                 Long userId,
                                 List<JSONObject> userInputs,
                                 String tenantCode,
                                 WorkflowCallSource callSource) {
        try {
            // 在异步线程中切换到正确的数据源
            DataSourceHelper.use(tenantCode);

            // 从缓存获取StreamHandler
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
                    callSource,
                    workflowRuntimeService,
                    workflowRuntimeNodeService,
                    conversationWorkflowRuntimeService,
                    conversationWorkflowRuntimeNodeService
            );

            // 在独立线程中执行工作流(不阻塞Flux.create)
            // 主工作流执行：传递 null 让系统自动生成新的 conversationId
            workflowEngine.run(userId, userInputs, tenantCode, null);

        } catch (Exception e) {
            log.error("工作流执行异常: workflowUuid={}, userId={}, executionId={}",
                    workflowUuid, userId, executionId, e);

            // 通过streamHandler发送错误
            WorkflowStreamHandler streamHandler = handlerCache.get(executionId);
            if (streamHandler != null) {
                streamHandler.sendError(e);
            }
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
    @Async("mainExecutor")
    public void resumeFlow(String runtimeUuid, String userInput) {
        try {
            WorkflowEngine workflowEngine = InterruptedFlow.RUNTIME_TO_GRAPH.get(runtimeUuid);
            if (workflowEngine == null) {
                log.error("工作流恢复执行时失败,runtime:{}", runtimeUuid);
                throw new RuntimeException("工作流实例不存在或已超时");
            }

            // 在异步线程中切换到正确的数据源
            // WorkflowEngine中保存了tenantCode,使用getTenantCode()获取
            String tenantCode = workflowEngine.getTenantCode();
            if (tenantCode != null) {
                DataSourceHelper.use(tenantCode);
            }

            // 调用engine的resume方法恢复工作流
            workflowEngine.resume(userInput);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }

    /**
     * 恢复暂停的工作流（流式响应）
     * 用于多轮对话场景：工作流暂停等待用户输入后，用户提供输入继续执行
     *
     * 遵循Spring AI模式：每次HTTP请求创建新Flux，但复用WorkflowEngine
     *
     * @param runtimeUuid 工作流运行时UUID（从InterruptedFlow.RUNTIME_TO_GRAPH获取）
     * @param workflowUuid 工作流UUID（用于runtime过期时重启）
     * @param userInput 用户提供的输入内容
     * @param tenantId 租户ID
     * @param callSource 调用来源标识 (WORKFLOW_TEST 或 AI_CHAT)
     * @return 工作流事件流（Flux<WorkflowEventVo>）
     */
    public Flux<WorkflowEventVo> resumeFlowAsFlux(String runtimeUuid, String workflowUuid,
                                                   String userInput, String tenantId, WorkflowCallSource callSource) {
        // 从缓存中获取暂停的工作流引擎
        WorkflowEngine workflowEngine = InterruptedFlow.RUNTIME_TO_GRAPH.get(runtimeUuid);

        if (workflowEngine == null) {
            // KISS优化: 过期优雅降级 - 用户输入重启工作流,而非报错
            log.info("运行时已过期(>30分钟),使用用户输入重新开始工作流: workflowUuid={}", workflowUuid);
            List<JSONObject> userInputs = List.of(
                new JSONObject().fluentPut("content", userInput)
            );
            return streaming(workflowUuid, userInputs, tenantId, callSource);
        }

        // 为本次HTTP请求创建新Flux（Spring AI模式）
        return Flux.<WorkflowEventVo>create(fluxSink -> {
            // 为本次请求创建新的StreamHandler
            WorkflowStreamHandler newHandler = new WorkflowStreamHandler(
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
                    public void onNodeWaitFeedback(String nodeUuid, String tip) {
                        fluxSink.next(WorkflowEventVo.createNodeWaitFeedbackEvent(nodeUuid, tip));
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

            // 替换WorkflowEngine的StreamHandler（连接到当前HTTP响应流）
            workflowEngine.setStreamHandler(newHandler);

            // 异步恢复工作流执行
            CompletableFuture.runAsync(() -> {
                try {
                    String tenantCode = workflowEngine.getTenantCode();
                    if (tenantCode != null) {
                        DataSourceHelper.use(tenantCode);
                    }

                    // 调用WorkflowEngine.resume()恢复执行
                    workflowEngine.resume(userInput);

                } catch (Exception e) {
                    log.error("工作流恢复执行失败, runtimeUuid={}", runtimeUuid, e);
                    newHandler.sendError(e);
                } finally {
                    DataSourceHelper.close();
                }
            }, mainExecutor);
        })
        .timeout(Duration.ofMinutes(30)) // KISS优化: 延长到30分钟
        .onErrorResume(TimeoutException.class, e -> {
            log.warn("工作流恢复执行超时: runtimeUuid={}", runtimeUuid);
            InterruptedFlow.RUNTIME_TO_GRAPH.remove(runtimeUuid);
            return Flux.just(WorkflowEventVo.createErrorEvent("工作流执行超时，已自动取消"));
        })
        .doOnCancel(() -> {
            log.info("用户取消工作流执行: runtimeUuid={}", runtimeUuid);
            // KISS验证: 保留状态供后续恢复(支持误点停止/临时中断场景)
            // PassiveExpiringMap会在30分钟后自动清理
        })
        .subscribeOn(Schedulers.boundedElastic())
        .doFinally(signalType -> {
            DataSourceHelper.close();
        });
    }

    /**
     * 同步执行工作流（用于子工作流调用）
     *
     * <p>与streaming方法不同，此方法同步执行工作流并返回最终输出结果。
     * 主要用于SubWorkflowNode调用子工作流。</p>
     *
     * @param workflowUuid 工作流UUID
     * @param userInputs 用户输入参数
     * @param tenantCode 租户编码
     * @param userId 用户ID
     * @param parentExecutionStack 父工作流的执行栈
     * @param parentConversationId 父工作流的conversationId
     * @param parentRuntimeUuid 父工作流的runtime_uuid（用于子工作流复用，避免创建新runtime记录）
     * @param parentStreamHandler 父工作流的StreamHandler（用于转发子工作流的流式事件）
     * @param callSource 调用来源标识 (WORKFLOW_TEST 或 AI_CHAT)
     * @return 工作流输出结果
     */
    public Map<String, Object> runSync(String workflowUuid,
                                       List<JSONObject> userInputs,
                                       String tenantCode,
                                       Long userId,
                                       Set<String> parentExecutionStack,
                                       String parentConversationId,
                                       String parentRuntimeUuid,
                                       WorkflowStreamHandler parentStreamHandler,
                                       WorkflowCallSource callSource) {
        try {
            // 切换到正确的数据源
            DataSourceHelper.use(tenantCode);

            // 获取工作流配置
            AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

            // 检查工作流是否启用
            if (workflow.getIsEnable() == null || !workflow.getIsEnable()) {
                throw new BusinessException("子工作流已禁用: " + workflowUuid);
            }

            log.info("SubWorkflow runSync: workflowUuid={}, userId={}", workflowUuid, userId);

            // 获取工作流组件、节点、边配置
            List<AiWorkflowComponentEntity> components = workflowComponentService.getAllEnable();
            List<AiWorkflowNodeVo> nodes = workflowNodeService.listByWorkflowId(workflow.getId());
            List<AiWorkflowEdgeEntity> edges = workflowEdgeService.listByWorkflowId(workflow.getId());

            // 创建一个StreamHandler用于收集结果并转发流式事件到父工作流
            final Map<String, Object> result = new ConcurrentHashMap<>();
            final AtomicReference<Throwable> errorRef = new AtomicReference<>();

            WorkflowStreamHandler streamHandler = new WorkflowStreamHandler(
                    new WorkflowStreamHandler.StreamCallback() {
                        @Override
                        public void onStart(String runtimeData) {
                            // 子工作流不需要发送start事件
                        }

                        @Override
                        public void onNodeRun(String nodeUuid, String nodeData) {
                            // 转发子工作流的node run事件到父工作流
                            if (parentStreamHandler != null) {
                                parentStreamHandler.sendNodeRun(nodeUuid, nodeData);
                            }
                        }

                        @Override
                        public void onNodeInput(String nodeUuid, String inputData) {
                            // 转发子工作流的node input事件到父工作流
                            if (parentStreamHandler != null) {
                                parentStreamHandler.sendNodeInput(nodeUuid, inputData);
                            }
                        }

                        @Override
                        public void onNodeOutput(String nodeUuid, String outputData) {
                            // 转发子工作流的node output事件到父工作流
                            if (parentStreamHandler != null) {
                                parentStreamHandler.sendNodeOutput(nodeUuid, outputData);
                            }
                        }

                        @Override
                        public void onNodeChunk(String nodeUuid, String chunk) {
                            // 转发子工作流的chunk事件到父工作流，实现流式输出
                            if (parentStreamHandler != null) {
                                parentStreamHandler.sendNodeChunk(nodeUuid, chunk);
                            }
                        }

                        @Override
                        public void onNodeWaitFeedback(String nodeUuid, String tip) {
                            // 子工作流不支持人机交互
                            throw new RuntimeException("子工作流不支持人机交互节点");
                        }

                        @Override
                        public void onComplete(String data) {
                            // 收集最终输出结果
                            if (data != null && !data.isEmpty()) {
                                JSONObject outputJson = JSONObject.parseObject(data);
                                result.putAll(outputJson);
                            }
                        }

                        @Override
                        public void onError(Throwable error) {
                            errorRef.set(error);
                        }
                    }
            );

            // 创建工作流引擎（支持传入父执行栈和父runtime_uuid）
            WorkflowEngine workflowEngine = new WorkflowEngine(
                    workflow,
                    streamHandler,
                    components,
                    nodes,
                    edges,
                    callSource,
                    workflowRuntimeService,
                    workflowRuntimeNodeService,
                    conversationWorkflowRuntimeService,
                    conversationWorkflowRuntimeNodeService,
                    parentRuntimeUuid  // 传递父runtime_uuid，子工作流将复用此UUID
            );

            // 同步执行工作流（传递父conversationId）
            workflowEngine.run(userId, userInputs, tenantCode, parentConversationId);

            // 检查是否有错误
            if (errorRef.get() != null) {
                throw new RuntimeException("子工作流执行失败", errorRef.get());
            }

            log.info("SubWorkflow runSync completed: workflowUuid={}, result={}", workflowUuid, result);
            return result;

        } catch (Exception e) {
            log.error("子工作流同步执行异常: workflowUuid={}, userId={}", workflowUuid, userId, e);
            throw new RuntimeException("子工作流执行失败: " + e.getMessage(), e);
        } finally {
            // 清理数据源上下文
            DataSourceHelper.close();
        }
    }

}
