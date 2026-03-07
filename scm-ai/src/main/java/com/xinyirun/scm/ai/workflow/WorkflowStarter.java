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
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeoutException;

/**
 * 工作流启动器
 *
 * <p>负责工作流的流式执行和中断恢复</p>
 * <p>对齐Spring AI Alibaba - 直接调用WorkflowEngine.run()返回Flux，无回调</p>
 *
 * @author zxh
 * @since 2025-10-21
 */
@Slf4j
@Component
public class WorkflowStarter {

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
    private AiConversationRuntimeService conversationRuntimeService;

    @Resource
    private AiConversationRuntimeNodeService conversationRuntimeNodeService;

    @Resource
    private AiWorkflowInteractionService interactionService;

    /**
     * 流式执行工作流
     *
     * <p>对齐Spring AI Alibaba的GraphRunner模式：
     * 直接调用WorkflowEngine.run()返回Flux，无回调、无subscribeOn</p>
     *
     * @param workflowUuid 工作流UUID
     * @param userInputs 用户输入参数
     * @param tenantCode 租户编码
     * @param callSource 调用来源标识 (WORKFLOW_TEST 或 AI_CHAT)
     * @param conversationId 对话ID (AI_CHAT场景必传,WORKFLOW_TEST传null)
     * @return Flux流式响应
     */
    public Flux<WorkflowEventVo> streaming(String workflowUuid, List<JSONObject> userInputs,
                                           String tenantCode, WorkflowCallSource callSource,
                                           String conversationId) {
        return streaming(workflowUuid, userInputs, tenantCode, callSource, conversationId, null);
    }

    /**
     * 流式执行工作流（带页面上下文）
     *
     * <p>对齐Spring AI Alibaba的GraphRunner模式：
     * 直接调用WorkflowEngine.run()返回Flux，无回调、无subscribeOn</p>
     *
     * @param workflowUuid 工作流UUID
     * @param userInputs 用户输入参数
     * @param tenantCode 租户编码
     * @param callSource 调用来源标识 (WORKFLOW_TEST 或 AI_CHAT)
     * @param conversationId 对话ID (AI_CHAT场景必传,WORKFLOW_TEST传null)
     * @param pageContext 页面上下文(用于MCP工具)
     * @return Flux流式响应
     */
    public Flux<WorkflowEventVo> streaming(String workflowUuid, List<JSONObject> userInputs,
                                           String tenantCode, WorkflowCallSource callSource,
                                           String conversationId, Map<String, Object> pageContext) {
        Long userId = SecurityUtil.getStaff_id();
        log.info("[WorkflowStarter] ===== streaming()开始 ===== workflowUuid={}, userId={}", workflowUuid, userId);

        // 对齐Spring AI Alibaba - 使用Flux.defer()延迟执行，直接返回Engine的Flux
        return Flux.defer(() -> {
            log.info("[WorkflowStarter] Flux.defer()内部开始执行, workflowUuid={}, tenantCode={}", workflowUuid, tenantCode);
            // 切换到正确的数据源
            DataSourceHelper.use(tenantCode);

            // 获取工作流配置
            AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

            // 检查工作流是否启用
            if (workflow.getIsEnable() == null || !workflow.getIsEnable()) {
                return Flux.error(new RuntimeException("工作流已禁用"));
            }

            log.info("WorkflowStarter streaming, userId:{}, workflowUuid:{}, tenantCode:{}, userInputs:{}",
                    userId, workflow.getWorkflowUuid(), tenantCode, userInputs);

            // 获取工作流组件、节点、边配置
            List<AiWorkflowComponentEntity> components = workflowComponentService.getAllEnable();
            List<AiWorkflowNodeVo> nodes = workflowNodeService.listByWorkflowId(workflow.getId());
            List<AiWorkflowEdgeEntity> edges = workflowEdgeService.listByWorkflowId(workflow.getId());

            // 创建工作流引擎（无streamHandler参数）
            WorkflowEngine workflowEngine = new WorkflowEngine(
                    workflow,
                    components,
                    nodes,
                    edges,
                    callSource,
                    workflowRuntimeService,
                    workflowRuntimeNodeService,
                    conversationRuntimeService,
                    conversationRuntimeNodeService,
                    interactionService
            );

            // 设置页面上下文(用于MCP工具)
            if (pageContext != null) {
                workflowEngine.setPageContext(pageContext);
            }

            // 直接调用Engine的run()方法，返回Flux（对齐Spring AI Alibaba模式）
            log.info("[WorkflowStarter] 准备调用workflowEngine.run()...");
            return workflowEngine.run(userId, userInputs, tenantCode, conversationId)
                    .doOnSubscribe(sub -> log.info("[WorkflowStarter] Engine Flux已被订阅(doOnSubscribe)"))
                    .doFirst(() -> log.info("[WorkflowStarter] Engine Flux开始执行(doFirst)"))
                    .doOnNext(event -> log.info("[WorkflowStarter] 收到Engine事件(doOnNext): dataLen={}", event.getData() != null ? event.getData().length() : 0))
                    .doOnComplete(() -> {
                        log.info("[WorkflowStarter] Engine Flux完成(doOnComplete)");
                        // 工作流运行成功后,自动更新测试时间
                        if (callSource == WorkflowCallSource.WORKFLOW_TEST) {
                            try {
                                workflowService.updateTestTime(workflowUuid, tenantCode);
                                log.info("工作流测试运行成功,已更新测试时间: workflowUuid={}, tenantCode={}",
                                        workflowUuid, tenantCode);
                            } catch (Exception e) {
                                log.error("更新测试时间失败: workflowUuid={}, tenantCode={}",
                                        workflowUuid, tenantCode, e);
                            }
                        }
                    })
                    .doOnError(e -> log.error("[WorkflowStarter] Engine Flux错误(doOnError)", e))
                    .doOnCancel(() -> log.warn("[WorkflowStarter] Engine Flux被取消(doOnCancel)"));
        })
        .timeout(Duration.ofMinutes(30))
        .onErrorResume(TimeoutException.class, e -> {
            log.warn("工作流执行超时: workflowUuid={}", workflowUuid);
            return Flux.error(new RuntimeException("工作流执行超时，已自动取消"));
        })
        .onErrorResume(e -> {
            log.error("工作流执行异常: workflowUuid={}", workflowUuid, e);
            return Flux.error(new RuntimeException("工作流执行失败: " + e.getMessage()));
        })
        .doFinally(signalType -> {
            DataSourceHelper.close();
        });
    }

    /**
     * 恢复中断的工作流（同步方法，用于非流式场景）
     *
     * <p>Controller调用后，工作流在当前线程恢复执行。
     * 适用于不需要流式返回的场景。</p>
     *
     * @param runtimeUuid 运行实例UUID
     * @param userInput 用户输入
     */
    public void resumeFlow(String runtimeUuid, String userInput) {
        try {
            WorkflowEngine workflowEngine = InterruptedFlow.RUNTIME_TO_GRAPH.get(runtimeUuid);
            if (workflowEngine == null) {
                log.error("工作流恢复执行时失败,runtime:{}", runtimeUuid);
                throw new RuntimeException("工作流实例不存在或已超时");
            }

            String tenantCode = workflowEngine.getTenantCode();
            if (tenantCode != null) {
                DataSourceHelper.use(tenantCode);
            }

            // 同步执行：阻塞等待Flux完成
            workflowEngine.resume(userInput).blockLast();
        } finally {
            DataSourceHelper.close();
        }
    }

    /**
     * 恢复暂停的工作流(流式响应)
     *
     * <p>用于多轮对话场景:工作流暂停等待用户输入后,用户提供输入继续执行</p>
     * <p>对齐Spring AI Alibaba - 直接调用WorkflowEngine.resume()返回Flux</p>
     *
     * @param runtimeUuid 工作流运行时UUID
     * @param workflowUuid 工作流UUID(用于runtime过期时重启)
     * @param userInput 用户提供的输入内容
     * @param tenantId 租户ID
     * @param callSource 调用来源标识
     * @param conversationId 对话ID
     * @return 工作流事件流
     */
    public Flux<WorkflowEventVo> resumeFlowAsFlux(String runtimeUuid, String workflowUuid,
                                                   String userInput, String tenantId,
                                                   WorkflowCallSource callSource,
                                                   String conversationId) {
        // 从缓存中获取暂停的工作流引擎
        WorkflowEngine workflowEngine = InterruptedFlow.RUNTIME_TO_GRAPH.get(runtimeUuid);

        if (workflowEngine == null) {
            // 过期优雅降级：用户输入重启工作流
            log.info("运行时已过期(>30分钟),使用用户输入重新开始工作流: workflowUuid={}", workflowUuid);
            List<JSONObject> userInputs = List.of(
                new JSONObject().fluentPut("content", userInput)
            );
            return streaming(workflowUuid, userInputs, tenantId, callSource, conversationId);
        }

        // 对齐Spring AI Alibaba - 使用Flux.defer()延迟执行
        return Flux.defer(() -> {
            String tenantCode = workflowEngine.getTenantCode();
            // 校验tenantId与engine内部的tenantCode是否一致
            if (tenantCode != null && !tenantCode.equals(tenantId)) {
                log.warn("resumeFlowAsFlux tenantId不匹配, 使用engine内部值: param={}, engine={}",
                        tenantId, tenantCode);
            }
            if (tenantCode != null) {
                DataSourceHelper.use(tenantCode);
            }

            // 直接调用Engine的resume()方法，返回Flux
            return workflowEngine.resume(userInput);
        })
        .timeout(Duration.ofMinutes(30))
        .onErrorResume(TimeoutException.class, e -> {
            log.warn("工作流恢复执行超时: runtimeUuid={}", runtimeUuid);
            InterruptedFlow.RUNTIME_TO_GRAPH.remove(runtimeUuid);
            return Flux.error(new RuntimeException("工作流执行超时，已自动取消"));
        })
        .onErrorResume(e -> {
            log.error("工作流恢复执行失败, runtimeUuid={}", runtimeUuid, e);
            return Flux.error(new RuntimeException("工作流恢复执行失败: " + e.getMessage()));
        })
        .doOnCancel(() -> {
            log.info("用户取消工作流执行: runtimeUuid={}", runtimeUuid);
        })
        .doFinally(signalType -> {
            DataSourceHelper.close();
        });
    }

    /**
     * 同步执行工作流（用于子工作流调用）
     *
     * <p>与streaming方法不同，此方法同步执行工作流并返回最终输出结果。
     * 主要用于SubWorkflowNode调用子工作流。</p>
     * <p>对齐Spring AI Alibaba - 使用blockLast()收集Flux结果</p>
     *
     * @param workflowUuid 工作流UUID
     * @param userInputs 用户输入参数
     * @param tenantCode 租户编码
     * @param userId 用户ID
     * @param parentExecutionStack 父工作流的执行栈
     * @param parentConversationId 父工作流的conversationId
     * @param parentRuntimeUuid 父工作流的runtime_uuid
     * @param callSource 调用来源标识
     * @return 工作流输出结果
     */
    public Map<String, Object> runSync(String workflowUuid,
                                       List<JSONObject> userInputs,
                                       String tenantCode,
                                       Long userId,
                                       Set<String> parentExecutionStack,
                                       String parentConversationId,
                                       String parentRuntimeUuid,
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

            // 创建工作流引擎（子工作流使用parentRuntimeUuid）
            WorkflowEngine workflowEngine = new WorkflowEngine(
                    workflow,
                    components,
                    nodes,
                    edges,
                    callSource,
                    workflowRuntimeService,
                    workflowRuntimeNodeService,
                    conversationRuntimeService,
                    conversationRuntimeNodeService,
                    interactionService,
                    parentRuntimeUuid
            );

            // 同步执行工作流并收集结果
            // 使用blockLast()阻塞等待Flux完成，获取最后一个事件
            // 对齐Spring AI Alibaba：通过data中的type字段区分消息类型
            final Map<String, Object> result = new HashMap<>();
            WorkflowEventVo lastEvent = workflowEngine.run(userId, userInputs, tenantCode, parentConversationId)
                    .doOnNext(event -> {
                        // 解析data中的type字段
                        String data = event.getData();
                        if (data == null || data.isEmpty()) return;

                        try {
                            JSONObject dataJson = JSONObject.parseObject(data);
                            String type = dataJson.getString("type");

                            // 收集output类型的输出
                            if ("output".equals(type)) {
                                JSONObject outputData = dataJson.getJSONObject("data");
                                if (outputData != null) {
                                    // 从节点输出中提取output变量
                                    for (String key : outputData.keySet()) {
                                        Object outputItem = outputData.get(key);
                                        if (outputItem instanceof JSONObject) {
                                            JSONObject itemJson = (JSONObject) outputItem;
                                            if ("output".equals(itemJson.getString("name"))) {
                                                JSONObject content = itemJson.getJSONObject("content");
                                                if (content != null && content.containsKey("value")) {
                                                    result.put("output", content.get("value"));
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            log.warn("解析子工作流输出失败: {}", data, e);
                        }
                    })
                    .blockLast();

            log.info("SubWorkflow runSync completed: workflowUuid={}, result={}", workflowUuid, result);
            return result;

        } catch (Exception e) {
            log.error("子工作流同步执行异常: workflowUuid={}, userId={}", workflowUuid, userId, e);
            throw new RuntimeException("子工作流执行失败: " + e.getMessage(), e);
        } finally {
            DataSourceHelper.close();
        }
    }

}
