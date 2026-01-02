package com.xinyirun.scm.ai.core.service.workflow;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationRuntimeEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationRuntimeNodeEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowVo;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowRouteDecision;
import com.xinyirun.scm.ai.bean.vo.response.ChatResponseVo;
import com.xinyirun.scm.ai.core.adapter.WorkflowEventAdapter;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.common.constant.WorkflowCallSource;
import com.xinyirun.scm.ai.core.mapper.workflow.AiConversationRuntimeMapper;
import com.xinyirun.scm.ai.core.mapper.workflow.AiConversationRuntimeNodeMapper;
import com.xinyirun.scm.ai.workflow.WorkflowConstants;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowMapper;
import com.xinyirun.scm.ai.workflow.WorkflowStarter;
import com.xinyirun.scm.ai.workflow.enums.WfIODataTypeEnum;
import com.xinyirun.scm.ai.workflow.orchestrator.OrchestratorResponse;
import com.xinyirun.scm.ai.workflow.orchestrator.OrchestratorFinalResponse;
import com.xinyirun.scm.ai.workflow.orchestrator.SubTask;
import com.xinyirun.scm.ai.workflow.orchestrator.WorkflowToolCallbackService;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import com.xinyirun.scm.ai.core.service.chat.AiTokenUsageService;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import cn.hutool.extra.spring.SpringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI工作流智能路由服务
 * 智能工作流
 *
 * <p>3层路由架构：</p>
 * <ul>
 *   <li>Layer 1: 用户指定工作流 (0ms)</li>
 *   <li>Layer 2: LLM智能路由 (1-2s)</li>
 *   <li>Layer 3: 默认兜底策略 (10ms)</li>
 * </ul>
 *
 * @author SCM-AI团队
 * @since 2025-11-10
 */
@Slf4j
@Service
public class WorkflowRoutingService {

    @Resource
    private AiWorkflowMapper aiWorkflowMapper;

    @Lazy
    @Resource
    @Qualifier("workflowRoutingChatClient")
    private ChatClient routingChatClient;

    @Lazy
    @Resource
    @Qualifier("orchestratorChatClient")
    private ChatClient orchestratorChatClient;

    @Resource
    private WorkflowToolCallbackService workflowCallbackService;

    @Lazy
    @Resource
    private WorkflowStarter workflowStarter;

    @Lazy
    @Resource
    @Qualifier("mcpToolCallbackMap")
    private Map<String, ToolCallback> mcpToolCallbackMap;

    @Resource
    private AiConversationRuntimeMapper conversationRuntimeMapper;

    @Resource
    private AiConversationRuntimeNodeMapper conversationRuntimeNodeMapper;

    @Lazy
    @Resource
    private com.xinyirun.scm.ai.core.service.chat.AiChatBaseService aiChatBaseService;

    @Resource
    private WorkflowEventAdapter workflowEventAdapter;

    /**
     * 智能路由：根据用户输入选择最合适的工作流
     *
     * @param userInput 用户输入文本
     * @param userId 用户ID
     * @param specifiedWorkflowUuid 用户指定的工作流UUID（可选，Layer 1）
     * @return 工作流UUID，如果没有匹配返回null
     */
    public String route(String userInput, Long userId, String specifiedWorkflowUuid) {
        log.info("开始工作流路由, userInput={}, userId={}, specifiedWorkflowUuid={}",
                userInput, userId, specifiedWorkflowUuid);

        // Layer 1: 用户指定工作流 (0ms)
        if (StringUtils.isNotBlank(specifiedWorkflowUuid)) {
            log.info("Layer 1: 用户指定工作流, workflowUuid={}", specifiedWorkflowUuid);
            return specifiedWorkflowUuid;
        }

        // 获取用户可用的工作流列表
        List<AiWorkflowVo> availableWorkflows = aiWorkflowMapper.selectAvailableWorkflowsForRouting(userId);

        if (availableWorkflows == null || availableWorkflows.isEmpty()) {
            log.warn("用户没有可用的工作流, userId={}", userId);
            return null;
        }

        // Layer 2: LLM智能路由 (1-2s)
        String workflowUuid = routeByLLM(userInput, availableWorkflows);
        if (StringUtils.isNotBlank(workflowUuid)) {
            log.info("Layer 2: LLM智能路由成功, workflowUuid={}", workflowUuid);
            return workflowUuid;
        }

        // Layer 3: 默认兜底策略 (10ms)
        AiWorkflowEntity defaultWorkflow = aiWorkflowMapper.selectDefaultWorkflow();
        if (defaultWorkflow != null) {
            log.info("Layer 3: 使用默认工作流, workflowUuid={}", defaultWorkflow.getWorkflowUuid());
            return defaultWorkflow.getWorkflowUuid();
        }

        log.warn("没有找到合适的工作流, 将使用普通AI对话");
        return null;
    }

    /**
     * Layer 2: LLM智能路由
     *
     * <p>使用大语言模型进行语义理解和意图识别</p>
     *
     * @param userInput 用户输入
     * @param workflows 可用工作流Vo列表(含分类名称等扩展信息)
     * @return 匹配的工作流UUID，未匹配返回null
     */
    private String routeByLLM(String userInput, List<AiWorkflowVo> workflows) {
        if (StringUtils.isBlank(userInput) || workflows == null || workflows.isEmpty()) {
            return null;
        }

        try {
            // 构建工作流列表JSON - 包含丰富的上下文信息
            String workflowsJson = workflows.stream()
                .map(w -> String.format(
                    "{uuid:\"%s\",title:\"%s\",desc:\"%s\",description:\"%s\",keywords:\"%s\",category_name:\"%s\"}",
                    w.getWorkflowUuid(),
                    w.getTitle(),
                    safeString(w.getRemark(), ""),           // 简短描述
                    safeString(w.getDesc(), ""),             // 详细描述(LLM路由用)
                    safeString(w.getKeywords(), ""),         // 关键词
                    safeString(w.getCategoryName(), "未分类") // 分类名称
                ))
                .collect(Collectors.joining(","));

            // 构建路由提示词
            String prompt = String.format("""
                你是一个智能工作流路由助手。根据用户问题选择最合适的工作流
                根据用户输入选择最合适的工作流。如果没有合适的返回null。

                用户输入: "%s"

                可用工作流: [%s]

                示例:
                用户: "查订单ORD-001"
                工作流: [{uuid:"aaa",title:"订单查询"}]
                输出: {"workflowUuid":"aaa","reasoning":"匹配订单查询","confidence":0.9}

                用户: "今天天气"
                工作流: [{uuid:"aaa",title:"订单查询"}]
                输出: {"workflowUuid":null,"reasoning":"无关业务","confidence":0.0}

                返回JSON格式: workflowUuid, reasoning, confidence
                """,
                userInput,
                workflowsJson
            );

            log.debug("LLM路由提示词: {}", prompt);

            // 调用LLM获取路由决策
            WorkflowRouteDecision decision = routingChatClient.prompt()
                .user(prompt)
                .call()
                .entity(WorkflowRouteDecision.class);

            log.info("LLM路由决策: workflowUuid={}, reasoning={}, confidence={}",
                decision.workflowUuid(), decision.reasoning(), decision.confidence());

            // LLM说没合适的
            if (StringUtils.isBlank(decision.workflowUuid())) {
                log.info("LLM判断没有合适的工作流: reasoning={}", decision.reasoning());
                return null;
            }

            // 验证UUID存在 - 防止LLM幻觉
            boolean exists = workflows.stream()
                .anyMatch(w -> w.getWorkflowUuid().equals(decision.workflowUuid()));

            if (!exists) {
                log.error("LLM返回的workflowUuid不在可用列表中: workflowUuid={}", decision.workflowUuid());
                return null;
            }

            return decision.workflowUuid();

        } catch (Exception e) {
            log.error("LLM路由执行失败: userInput={}", userInput, e);
            return null;
        }
    }

    /**
     * 安全字符串处理：避免null或空字符串
     *
     * @param value 原始值
     * @param defaultValue 默认值
     * @return 非空字符串
     */
    private String safeString(String value, String defaultValue) {
        return StringUtils.defaultIfBlank(value, defaultValue);
    }

    /**
     * Orchestrator-Workers模式执行
     *
     * <p>使用LLM将用户任务分解为多个子任务,并发执行workflow和MCP工具</p>
     * <p>同步执行(for循环),无并发问题,每个Worker执行完成后记录到数据库</p>
     * <p>支持多轮对话: 通过conversationId获取历史对话上下文,实现上下文理解</p>
     *
     * @param userInput 用户输入
     * @param userId 用户ID
     * @param tenantCode 租户代码
     * @param conversationId 对话ID(用于多轮对话memory,格式: tenantCode::conversationUUID)
     * @param pageContext 页面上下文(可选)
     * @return Orchestrator的最终分析和Workers的执行结果
     */
    public OrchestratorFinalResponse orchestrateAndExecute(
            String userInput,
            Long userId,
            String tenantCode,
            String conversationId,
            Map<String, Object> pageContext) {

        log.info("【Orchestrator-Workers】开始执行, userInput={}, userId={}, conversationId={}", userInput, userId, conversationId);

        // 【日志记录】创建主运行记录
        Long runtimeId = null;
        String runtimeUuid = null;

        try {
            // Step 1: 获取可用的workflow列表
            List<ToolCallback> workflowCallbacks = workflowCallbackService.getAllCallbacks();
            log.info("【Orchestrator】获取到{}个可用workflow", workflowCallbacks.size());

            // Step 2: 构建Orchestrator的prompt - 告诉LLM有哪些工具可用
            String workflowsInfo = workflowCallbacks.stream()
                .map(callback -> {
                    var definition = callback.getToolDefinition();
                    return String.format("{name:\"%s\",description:\"%s\"}",
                        definition.name(), definition.description());
                })
                .collect(Collectors.joining(","));

            String mcpToolsInfo = mcpToolCallbackMap.keySet().stream()
                .map(name -> {
                    var callback = mcpToolCallbackMap.get(name);
                    var definition = callback.getToolDefinition();
                    return String.format("{name:\"%s\",description:\"%s\"}",
                        definition.name(), definition.description());
                })
                .collect(Collectors.joining(","));

            String orchestratorPrompt = String.format("""
                你是一个任务分解专家。将用户请求分解为多个子任务,每个子任务可以是workflow或MCP工具调用。

                用户输入: "%s"

                可用workflows: [%s]
                可用MCP工具: [%s]

                分解规则:
                1. 分析用户意图,识别需要完成的子任务
                2. 每个子任务指定type(workflow或mcp)、target(工具名称)、params(执行参数)
                3. 子任务之间可以并行执行
                4. 如果用户问题可以用单个workflow解决,只返回一个子任务
                5. 如果需要多个工具协作,返回多个子任务

                返回JSON格式:
                {
                  "analysis": "任务分析和分解策略",
                  "tasks": [
                    {"type":"workflow","target":"workflow_uuid","description":"任务描述","params":{"key":"value"}},
                    {"type":"mcp","target":"mcp_tool_name","description":"任务描述","params":{"key":"value"}}
                  ]
                }
                """,
                userInput,
                workflowsInfo,
                mcpToolsInfo
            );

            // Step 3: 调用Orchestrator LLM进行任务分解(支持多轮对话memory)
            log.info("【Orchestrator】调用LLM进行任务分解, conversationId={}", conversationId);
            log.info("【Orchestrator】Prompt内容:\n{}", orchestratorPrompt);
            log.info("【Orchestrator】Prompt长度: {} 字符, 预估token数: {}",
                orchestratorPrompt.length(),
                orchestratorPrompt.length() / 2);
            log.info("【Orchestrator】workflowsInfo长度: {}, mcpToolsInfo长度: {}",
                workflowsInfo.length(),
                mcpToolsInfo.length());

            final long orchestratorStartTime = System.currentTimeMillis();
            log.info("【Orchestrator】LLM API调用开始, 时间戳: {}", orchestratorStartTime);

            // 获取ChatResponse以提取Usage信息(而非直接.entity()丢失元数据)
            // extraBody 已在 orchestratorChatClient Bean 的 defaultOptions 中配置
            var chatResponse = orchestratorChatClient.prompt()
                .user(orchestratorPrompt)
                // 传递conversationId给Memory Advisor,自动注入历史对话上下文
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .chatResponse();

            final long orchestratorEndTime = System.currentTimeMillis();
            log.info("【Orchestrator】LLM API调用结束, 时间戳: {}, 耗时: {}ms",
                orchestratorEndTime,
                orchestratorEndTime - orchestratorStartTime);

            // 【注意】Token记录延后到runtime创建之后,以便获取runtimeId作为serial_id
            Usage orchestratorUsage = null;
            if (chatResponse != null && chatResponse.getMetadata() != null) {
                orchestratorUsage = chatResponse.getMetadata().getUsage();
            }

            // 从ChatResponse解析OrchestratorResponse实体
            String responseContent = chatResponse.getResult().getOutput().getText();
            OrchestratorResponse orchestratorResponse = parseOrchestratorResponse(responseContent);

            log.info("【Orchestrator】任务分解完成: analysis={}, tasks={}",
                orchestratorResponse.analysis(),
                orchestratorResponse.tasks().size());

            // 【日志记录】创建ai_conversation_runtime主记录
            runtimeUuid = UuidUtil.createShort();
            AiConversationRuntimeEntity runtime = createOrchestratorRuntime(
                runtimeUuid, userId, tenantCode, conversationId, userInput, orchestratorResponse.analysis());
            runtimeId = runtime.getId();
            log.info("【日志记录】创建Orchestrator运行记录: runtimeId={}, runtimeUuid={}, conversationId={}", runtimeId, runtimeUuid, conversationId);

            // 【日志记录】创建Orchestrator节点记录(用于Token记录关联)
            Long orchestratorNodeId = createOrchestratorNodeRecord(runtimeId, userInput, orchestratorResponse.analysis(), userId, tenantCode);
            log.info("【日志记录】创建Orchestrator节点记录: nodeId={}", orchestratorNodeId);

            // 【Token记录】记录Orchestrator LLM的Token使用量(关联到节点记录)
            if (orchestratorUsage != null) {
                recordOrchestratorTokenUsage(orchestratorUsage, conversationId, orchestratorNodeId, userId, tenantCode, orchestratorStartTime);
            }

            // Step 4: 顺序执行所有Workers (同步执行,无并发问题)
            List<String> workerResults = new ArrayList<>();
            int taskIndex = 0;
            for (SubTask task : orchestratorResponse.tasks()) {
                log.info("【Worker】开始执行: type={}, target={}, description={}",
                    task.type(), task.target(), task.description());

                // 【日志记录】创建节点记录(执行前)
                Long nodeRecordId = createWorkerNodeRecord(runtimeId, task, userId, taskIndex, tenantCode);

                // 传递原始userInput,确保workflow能获取用户输入
                String result = executeWorker(task, userId, tenantCode, pageContext, userInput);
                workerResults.add(result);

                // 【日志记录】更新节点记录(执行后)
                updateWorkerNodeRecord(nodeRecordId, result, true, null, tenantCode);

                log.info("【Worker】执行完成: type={}, target={}, result={}",
                    task.type(), task.target(), result);
                taskIndex++;
            }

            // 【日志记录】更新主记录状态为完成
            // 状态: 3-成功(WORKFLOW_PROCESS_STATUS_SUCCESS)
            updateOrchestratorRuntime(runtimeId, WorkflowConstants.WORKFLOW_PROCESS_STATUS_SUCCESS, "执行完成", workerResults, tenantCode);

            // Step 5: 返回最终结果(包含runtimeUuid用于前端显示执行详情icon, runtimeId用于Token记录)
            OrchestratorFinalResponse finalResponse = new OrchestratorFinalResponse(
                orchestratorResponse.analysis(),
                workerResults,
                runtimeUuid,
                runtimeId
            );

            log.info("【Orchestrator-Workers】执行完成, workerResults={}", workerResults.size());
            return finalResponse;

        } catch (Exception e) {
            log.error("【Orchestrator-Workers】执行失败: userInput={}", userInput, e);

            // 【日志记录】更新主记录状态为失败
            // 状态: 4-失败(WORKFLOW_PROCESS_STATUS_FAIL)
            if (runtimeId != null) {
                updateOrchestratorRuntime(runtimeId, WorkflowConstants.WORKFLOW_PROCESS_STATUS_FAIL, "执行失败: " + e.getMessage(), null, tenantCode);
            }

            throw new RuntimeException("Orchestrator-Workers执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * Orchestrator模式的特殊workflowId标识
     * 使用0表示这是Orchestrator-Workers模式,而非传统workflow
     */
    private static final Long ORCHESTRATOR_MODE_WORKFLOW_ID = 0L;

    /**
     * 创建Orchestrator运行主记录
     *
     * @param runtimeUuid 运行UUID
     * @param userId 用户ID
     * @param tenantCode 租户代码
     * @param conversationId 对话ID(来自前端,格式: tenantCode::conversationUUID)
     * @param userInput 用户输入
     * @param analysis Orchestrator的任务分析
     * @return 创建的运行记录实体
     */
    private AiConversationRuntimeEntity createOrchestratorRuntime(
            String runtimeUuid, Long userId, String tenantCode,
            String conversationId, String userInput, String analysis) {

        // 切换到租户数据源
        DataSourceHelper.use(tenantCode);

        AiConversationRuntimeEntity runtime = new AiConversationRuntimeEntity();
        runtime.setRuntimeUuid(runtimeUuid);
        runtime.setUserId(userId);

        // 使用传入的conversationId(来自前端的真实对话ID)
        // 格式: tenantCode::conversationUUID, 用于关联AI Chat对话和删除时匹配
        runtime.setConversationId(conversationId);

        // 保存输入数据,包含mode标识用于区分Orchestrator模式
        JSONObject inputData = new JSONObject();
        inputData.put("userInput", userInput);
        inputData.put("analysis", analysis);
        inputData.put("mode", "orchestrator-workers");
        runtime.setInputData(inputData.toJSONString());

        // 初始状态: 2-运行中(WORKFLOW_PROCESS_STATUS_RUNNING)
        runtime.setStatus(WorkflowConstants.WORKFLOW_PROCESS_STATUS_RUNNING);
        runtime.setC_id(userId);
        runtime.setU_id(userId);

        conversationRuntimeMapper.insert(runtime);
        return conversationRuntimeMapper.selectById(runtime.getId());
    }

    /**
     * 更新Orchestrator运行主记录
     *
     * @param runtimeId 运行记录ID
     * @param status 状态(3-成功WORKFLOW_PROCESS_STATUS_SUCCESS, 4-失败WORKFLOW_PROCESS_STATUS_FAIL)
     * @param statusRemark 状态说明
     * @param workerResults Worker执行结果列表
     * @param tenantCode 租户代码
     */
    private void updateOrchestratorRuntime(Long runtimeId, Integer status,
                                            String statusRemark, List<String> workerResults,
                                            String tenantCode) {
        // 切换到租户数据源
        DataSourceHelper.use(tenantCode);

        AiConversationRuntimeEntity runtime = conversationRuntimeMapper.selectById(runtimeId);
        if (runtime == null) {
            log.error("【日志记录】运行记录不存在: runtimeId={}", runtimeId);
            return;
        }

        runtime.setStatus(status);
        if (StringUtils.isNotBlank(statusRemark)) {
            runtime.setStatusRemark(StringUtils.substring(statusRemark, 0, 500));
        }

        // 保存输出数据
        if (workerResults != null) {
            JSONObject outputData = new JSONObject();
            outputData.put("workerResults", workerResults);
            outputData.put("resultCount", workerResults.size());
            runtime.setOutputData(outputData.toJSONString());
        }

        conversationRuntimeMapper.updateById(runtime);
    }

    /**
     * 创建Worker节点执行记录
     *
     * @param runtimeId 主运行记录ID
     * @param task Worker任务
     * @param userId 用户ID
     * @param taskIndex 任务序号
     * @param tenantCode 租户代码
     * @return 节点记录ID
     */
    private Long createWorkerNodeRecord(Long runtimeId, SubTask task, Long userId, int taskIndex, String tenantCode) {
        // 切换到租户数据源
        DataSourceHelper.use(tenantCode);

        AiConversationRuntimeNodeEntity nodeRecord = new AiConversationRuntimeNodeEntity();
        nodeRecord.setRuntimeNodeUuid(UuidUtil.createShort());
        nodeRecord.setConversationWorkflowRuntimeId(runtimeId);
        // nodeId=0 表示Orchestrator模式(非传统workflow node)
        nodeRecord.setNodeId(ORCHESTRATOR_MODE_WORKFLOW_ID);

        // 保存输入数据: type, target, description, params
        JSONObject inputData = new JSONObject();
        inputData.put("type", task.type());
        inputData.put("target", task.target());
        inputData.put("description", task.description());
        inputData.put("taskIndex", taskIndex);
        if (task.params() != null) {
            inputData.put("params", task.params());
        }
        nodeRecord.setInputData(inputData.toJSONString());

        // 保存输出数据: uuid/title(workflow)或description(MCP)
        JSONObject outputData = new JSONObject();
        if ("workflow".equals(task.type())) {
            // workflow: 保存uuid和title
            String workflowUuid = task.target();
            if (workflowUuid.startsWith("workflow_")) {
                workflowUuid = workflowUuid.substring("workflow_".length());
            }
            outputData.put("uuid", workflowUuid);
            outputData.put("title", task.description());
            outputData.put("workerType", "workflow");
        } else if ("mcp".equals(task.type())) {
            // MCP: 保存description(MCP没有uuid)
            ToolCallback mcpCallback = mcpToolCallbackMap.get(task.target());
            String mcpDescription = "";
            if (mcpCallback != null && mcpCallback.getToolDefinition() != null) {
                mcpDescription = mcpCallback.getToolDefinition().description();
            }
            outputData.put("mcpToolName", task.target());
            outputData.put("description", mcpDescription);
            outputData.put("workerType", "mcp");
        }
        nodeRecord.setOutputData(outputData.toJSONString());

        // 初始状态: 2-运行中(WORKFLOW_PROCESS_STATUS_RUNNING)
        nodeRecord.setStatus(WorkflowConstants.WORKFLOW_PROCESS_STATUS_RUNNING);
        nodeRecord.setC_id(userId);
        nodeRecord.setU_id(userId);

        conversationRuntimeNodeMapper.insert(nodeRecord);
        return nodeRecord.getId();
    }

    /**
     * 更新Worker节点执行记录
     *
     * @param nodeRecordId 节点记录ID
     * @param result 执行结果
     * @param success 是否成功
     * @param errorMsg 错误信息(失败时)
     * @param tenantCode 租户代码
     */
    private void updateWorkerNodeRecord(Long nodeRecordId, String result,
                                         boolean success, String errorMsg, String tenantCode) {
        // 切换到租户数据源
        DataSourceHelper.use(tenantCode);

        AiConversationRuntimeNodeEntity nodeRecord =
            conversationRuntimeNodeMapper.selectById(nodeRecordId);
        if (nodeRecord == null) {
            log.error("【日志记录】节点记录不存在: nodeRecordId={}", nodeRecordId);
            return;
        }

        // 更新输出数据,添加执行结果
        JSONObject outputData;
        if (StringUtils.isNotBlank(nodeRecord.getOutputData())) {
            outputData = JSON.parseObject(nodeRecord.getOutputData());
        } else {
            outputData = new JSONObject();
        }
        outputData.put("result", result);
        outputData.put("success", success);
        if (StringUtils.isNotBlank(errorMsg)) {
            outputData.put("errorMsg", errorMsg);
        }
        nodeRecord.setOutputData(outputData.toJSONString());

        // 状态: 3-成功(NODE_PROCESS_STATUS_SUCCESS), 4-失败(NODE_PROCESS_STATUS_FAIL)
        nodeRecord.setStatus(success ? WorkflowConstants.NODE_PROCESS_STATUS_SUCCESS : WorkflowConstants.NODE_PROCESS_STATUS_FAIL);
        if (!success && StringUtils.isNotBlank(errorMsg)) {
            nodeRecord.setStatusRemark(StringUtils.substring(errorMsg, 0, 500));
        }

        conversationRuntimeNodeMapper.updateById(nodeRecord);
    }

    /**
     * 创建Orchestrator节点执行记录
     *
     * <p>Orchestrator是任务分解阶段的LLM调用,需要创建对应的节点记录用于Token关联</p>
     *
     * @param runtimeId 主运行记录ID
     * @param userInput 用户输入
     * @param analysis Orchestrator的任务分析结果
     * @param userId 用户ID
     * @param tenantCode 租户代码
     * @return 节点记录ID
     */
    private Long createOrchestratorNodeRecord(Long runtimeId, String userInput, String analysis,
                                               Long userId, String tenantCode) {
        // 切换到租户数据源
        DataSourceHelper.use(tenantCode);

        AiConversationRuntimeNodeEntity nodeRecord = new AiConversationRuntimeNodeEntity();
        nodeRecord.setRuntimeNodeUuid(UuidUtil.createShort());
        nodeRecord.setConversationWorkflowRuntimeId(runtimeId);
        // nodeId=0 表示Orchestrator模式(非传统workflow node)
        nodeRecord.setNodeId(ORCHESTRATOR_MODE_WORKFLOW_ID);

        // 保存输入数据: userInput
        JSONObject inputData = new JSONObject();
        inputData.put("userInput", userInput);
        inputData.put("nodeType", "orchestrator");
        nodeRecord.setInputData(inputData.toJSONString());

        // 保存输出数据: analysis(Orchestrator的任务分析)
        JSONObject outputData = new JSONObject();
        outputData.put("analysis", analysis);
        outputData.put("workerType", "orchestrator");
        nodeRecord.setOutputData(outputData.toJSONString());

        // 状态: 3-成功(NODE_PROCESS_STATUS_SUCCESS) - Orchestrator调用完成即成功
        nodeRecord.setStatus(WorkflowConstants.NODE_PROCESS_STATUS_SUCCESS);
        nodeRecord.setC_id(userId);
        nodeRecord.setU_id(userId);

        conversationRuntimeNodeMapper.insert(nodeRecord);
        return nodeRecord.getId();
    }

    /**
     * 创建Synthesizer节点执行记录
     *
     * <p>Synthesizer是结果合成阶段的LLM调用,需要创建对应的节点记录用于Token关联</p>
     *
     * @param runtimeId 主运行记录ID
     * @param prompt Synthesizer的输入prompt
     * @param userId 用户ID
     * @param tenantCode 租户代码
     * @return 节点记录ID
     */
    private Long createSynthesizerNodeRecord(Long runtimeId, String prompt,
                                              Long userId, String tenantCode) {
        // 切换到租户数据源
        DataSourceHelper.use(tenantCode);

        AiConversationRuntimeNodeEntity nodeRecord = new AiConversationRuntimeNodeEntity();
        nodeRecord.setRuntimeNodeUuid(UuidUtil.createShort());
        nodeRecord.setConversationWorkflowRuntimeId(runtimeId);
        // nodeId=0 表示Orchestrator模式(非传统workflow node)
        nodeRecord.setNodeId(ORCHESTRATOR_MODE_WORKFLOW_ID);

        // 保存输入数据: prompt
        JSONObject inputData = new JSONObject();
        // prompt可能很长,只保存前500字符
        inputData.put("prompt", StringUtils.substring(prompt, 0, 500));
        inputData.put("nodeType", "synthesizer");
        nodeRecord.setInputData(inputData.toJSONString());

        // 初始输出数据: 标记为synthesizer类型
        JSONObject outputData = new JSONObject();
        outputData.put("workerType", "synthesizer");
        nodeRecord.setOutputData(outputData.toJSONString());

        // 初始状态: 2-运行中(WORKFLOW_PROCESS_STATUS_RUNNING)
        nodeRecord.setStatus(WorkflowConstants.WORKFLOW_PROCESS_STATUS_RUNNING);
        nodeRecord.setC_id(userId);
        nodeRecord.setU_id(userId);

        conversationRuntimeNodeMapper.insert(nodeRecord);
        return nodeRecord.getId();
    }

    /**
     * 更新Synthesizer节点执行记录
     *
     * @param nodeRecordId 节点记录ID
     * @param result LLM生成的回复内容
     * @param success 是否成功
     * @param errorMsg 错误信息(失败时)
     * @param tenantCode 租户代码
     */
    private void updateSynthesizerNodeRecord(Long nodeRecordId, String result,
                                              boolean success, String errorMsg, String tenantCode) {
        // 切换到租户数据源
        DataSourceHelper.use(tenantCode);

        AiConversationRuntimeNodeEntity nodeRecord =
            conversationRuntimeNodeMapper.selectById(nodeRecordId);
        if (nodeRecord == null) {
            log.error("【日志记录】Synthesizer节点记录不存在: nodeRecordId={}", nodeRecordId);
            return;
        }

        // 更新输出数据,添加执行结果
        JSONObject outputData;
        if (StringUtils.isNotBlank(nodeRecord.getOutputData())) {
            outputData = JSON.parseObject(nodeRecord.getOutputData());
        } else {
            outputData = new JSONObject();
        }
        // result可能很长,只保存前1000字符
        outputData.put("result", StringUtils.substring(result, 0, 1000));
        outputData.put("success", success);
        if (StringUtils.isNotBlank(errorMsg)) {
            outputData.put("errorMsg", errorMsg);
        }
        nodeRecord.setOutputData(outputData.toJSONString());

        // 状态: 3-成功(NODE_PROCESS_STATUS_SUCCESS), 4-失败(NODE_PROCESS_STATUS_FAIL)
        nodeRecord.setStatus(success ? WorkflowConstants.NODE_PROCESS_STATUS_SUCCESS : WorkflowConstants.NODE_PROCESS_STATUS_FAIL);
        if (!success && StringUtils.isNotBlank(errorMsg)) {
            nodeRecord.setStatusRemark(StringUtils.substring(errorMsg, 0, 500));
        }

        conversationRuntimeNodeMapper.updateById(nodeRecord);
    }

    /**
     * 执行单个Worker(workflow或MCP)
     *
     * @param task 子任务
     * @param userId 用户ID
     * @param tenantCode 租户代码
     * @param pageContext 页面上下文
     * @param userInput 原始用户输入(用于workflow的var_user_input参数)
     * @return Worker执行结果(JSON字符串)
     */
    private String executeWorker(SubTask task, Long userId, String tenantCode, Map<String, Object> pageContext, String userInput) {
        try {
            if ("workflow".equals(task.type())) {
                // 去掉workflow_前缀(WorkflowToolCallback在创建name时加了前缀)
                String workflowUuid = task.target();
                if (workflowUuid.startsWith("workflow_")) {
                    workflowUuid = workflowUuid.substring("workflow_".length());
                }

                // 【关键】设置租户数据源,确保查询ai_workflow表时使用正确的数据库
                // MCP工具执行后可能清理了ThreadLocal中的数据源上下文
                if (StringUtils.isNotBlank(tenantCode)) {
                    DataSourceHelper.use(tenantCode);
                    log.debug("【Worker-workflow】设置租户数据源: {}", tenantCode);
                }

                // 动态查询数据库获取workflow
                ToolCallback callback = workflowCallbackService.getCallback(workflowUuid);
                if (callback == null) {
                    return "{\"success\": false, \"error\": \"Workflow不存在: " + task.target() + "\"}";
                }

                // 【关键修复】构建workflow参数,确保包含var_user_input
                // Orchestrator LLM可能没有在params中指定用户输入,需要补充
                Map<String, Object> workflowParams = new HashMap<>();
                if (task.params() != null) {
                    workflowParams.putAll(task.params());
                }
                // 如果params中没有var_user_input,则添加原始用户输入
                if (!workflowParams.containsKey("var_user_input") && StringUtils.isNotBlank(userInput)) {
                    workflowParams.put("var_user_input", userInput);
                    log.info("【Worker-workflow】补充var_user_input参数: {}", userInput);
                }

                return callback.call(
                    JSON.toJSONString(workflowParams),
                    createToolContext(userId, tenantCode, pageContext)
                );

            } else if ("mcp".equals(task.type())) {
                // 【关键】MCP工具执行前也需要确保租户数据源正确
                if (StringUtils.isNotBlank(tenantCode)) {
                    DataSourceHelper.use(tenantCode);
                    log.debug("【Worker-mcp】设置租户数据源: {}", tenantCode);
                }

                // MCP工具从静态Map获取(启动时注册,不会运行时增删)
                ToolCallback mcpCallback = mcpToolCallbackMap.get(task.target());
                if (mcpCallback == null) {
                    return "{\"success\": false, \"error\": \"MCP工具不存在: " + task.target() + "\"}";
                }

                // 【修复2】构建MCP工具参数,从pageContext提取page_code等信息
                Map<String, Object> mcpParams = new HashMap<>();
                if (task.params() != null) {
                    mcpParams.putAll(task.params());
                }

                // 如果MCP工具是权限相关工具,从pageContext中提取pageCode
                if (task.target().contains("Permission") && pageContext != null) {
                    String pageCode = (String) pageContext.get("page_code");
                    if (StringUtils.isNotBlank(pageCode) && !mcpParams.containsKey("pageCode")) {
                        mcpParams.put("pageCode", pageCode);
                        log.info("【Worker-mcp】从pageContext补充pageCode参数: {}", pageCode);
                    }
                }

                return mcpCallback.call(
                    JSON.toJSONString(mcpParams),
                    createToolContext(userId, tenantCode, pageContext)
                );


            } else {
                return "{\"success\": false, \"error\": \"未知任务类型: " + task.type() + "\"}";
            }

        } catch (Exception e) {
            log.error("【Worker】执行失败: task={}", task, e);
            return "{\"success\": false, \"error\": \"" + e.getMessage() + "\"}";
        }
    }

    /**
     * 创建ToolContext上下文
     *
     * @param userId 用户ID
     * @param tenantCode 租户代码
     * @param pageContext 页面上下文
     * @return ToolContext对象
     */
    private ToolContext createToolContext(Long userId, String tenantCode, Map<String, Object> pageContext) {
        Map<String, Object> context = new HashMap<>();
        context.put("userId", userId);
        // 【修复1】MCP工具期望的是staffId,不是userId
        context.put("staffId", userId);
        context.put("tenantCode", tenantCode);
        if (pageContext != null) {
            context.put("pageContext", pageContext);
        }
        return new ToolContext(context);
    }

    /**
     * 恢复暂停的工作流执行
     *
     * 从 AiConversationController 迁移的逻辑
     *
     * @param runtimeUuid 运行时UUID
     * @param workflowUuid 工作流UUID
     * @param userInput 用户输入
     * @param tenantCode 租户编码
     * @param conversationId 会话ID
     * @return Flux<ChatResponseVo> 流式响应
     */
    public Flux<ChatResponseVo> resumeWorkflow(
            String runtimeUuid,
            String workflowUuid,
            String userInput,
            String tenantCode,
            String conversationId) {

        log.info("【resumeWorkflow】恢复工作流执行, runtimeUuid={}, workflowUuid={}, conversationId={}, userInput={}",
                runtimeUuid, workflowUuid, conversationId, userInput);

        return workflowStarter.resumeFlowAsFlux(
            runtimeUuid,
            workflowUuid,
            userInput,
            tenantCode,
            WorkflowCallSource.AI_CHAT,
            conversationId
        )
        .doOnError(e -> log.error("【resumeWorkflow】恢复工作流失败: runtimeUuid={}, workflowUuid={}, error={}",
            runtimeUuid, workflowUuid, e.getMessage(), e))
        .map(event -> workflowEventAdapter.convert(event));
    }

    /**
     * 判断用户输入是继续当前工作流还是新意图
     *
     * 从 AiConversationController.isInputContinuation() 迁移
     *
     * @param userInput 用户输入
     * @param currentWorkflowUuid 当前工作流UUID
     * @param userId 用户ID
     * @return true-继续当前工作流, false-新意图需要路由
     */
    public boolean isInputContinuation(String userInput, String currentWorkflowUuid, Long userId) {
        // 策略1: 明确的继续关键词 (支持中英文,大小写不敏感)
        // 使用contains方式,避免正则表达式对中文的处理问题
        String normalizedInput = userInput.trim().toLowerCase();
        if (normalizedInput.contains("继续") ||
            normalizedInput.contains("continue") ||
            normalizedInput.equals("是") ||
            normalizedInput.equals("好") ||
            normalizedInput.equals("确认") ||
            normalizedInput.equalsIgnoreCase("ok") ||
            normalizedInput.equalsIgnoreCase("yes")) {
            log.info("【isInputContinuation】策略1命中: 继续关键词, userInput={}", userInput);
            return true;
        }

        // 策略2: 短输入(<=10字符),可能是具体值(订单号/数量等)
        // 说明: 10个字符足够输入一个短订单号或简单回复,但不足以表达新意图
        final int SHORT_INPUT_THRESHOLD = 10;
        if (userInput.length() <= SHORT_INPUT_THRESHOLD) {
            log.info("【isInputContinuation】策略2命中: 短输入({}字符), userInput={}", userInput.length(), userInput);
            return true;
        }

        // 策略3: 路由判断 - 是否匹配到新工作流
        String newWorkflowUuid = route(userInput, userId, null);

        // 没有匹配新工作流,或匹配的还是当前工作流 → 继续
        boolean isContinuation = newWorkflowUuid == null || newWorkflowUuid.equals(currentWorkflowUuid);
        log.info("【isInputContinuation】策略3判断: newWorkflowUuid={}, currentWorkflowUuid={}, isContinuation={}",
            newWorkflowUuid, currentWorkflowUuid, isContinuation);
        return isContinuation;
    }

    /**
     * 路由并执行工作流(新架构入口方法)
     *
     * <p>两层架构:</p>
     * <ul>
     *   <li>Layer 1: 用户指定工作流 - 直接执行传统workflow</li>
     *   <li>Layer 2: Orchestrator-Workers模式 - LLM分解任务并执行</li>
     * </ul>
     *
     * @param userInput 用户输入文本
     * @param userId 用户ID
     * @param tenantCode 租户代码
     * @param conversationId 对话ID
     * @param pageContext 页面上下文(可选)
     * @param specifiedWorkflowUuid 用户指定的工作流UUID(可选,Layer 1)
     * @return Flux<ChatResponseVo> 流式事件响应(兼容Spring AI标准格式)
     */
    public Flux<ChatResponseVo> routeAndExecute(
            String userInput,
            Long userId,
            String tenantCode,
            String conversationId,
            Map<String, Object> pageContext,
            String specifiedWorkflowUuid) {

        log.info("【routeAndExecute】开始, userInput={}, userId={}, specifiedWorkflowUuid={}",
                userInput, userId, specifiedWorkflowUuid);

        // Layer 1: 用户指定工作流 - 直接执行传统workflow
        if (StringUtils.isNotBlank(specifiedWorkflowUuid)) {
            log.info("【routeAndExecute】Layer 1: 用户指定工作流, workflowUuid={}", specifiedWorkflowUuid);

            // 构建用户输入参数
            // 格式必须符合WfNodeIODataUtil.createNodeIOData()的要求
            List<JSONObject> userInputs = new ArrayList<>();
            JSONObject input = new JSONObject();
            input.put("name", "user_input");

            // 构建content对象,TEXT类型
            JSONObject content = new JSONObject();
            content.put("type", WfIODataTypeEnum.TEXT.getValue());
            content.put("title", "用户输入");
            content.put("value", userInput);
            input.put("content", content);

            userInputs.add(input);

            // 传统workflow需要转换WorkflowEventVo为ChatResponseVo格式
            Flux<WorkflowEventVo> eventFlux = workflowStarter.streaming(
                    specifiedWorkflowUuid,
                    userInputs,
                    tenantCode,
                    WorkflowCallSource.AI_CHAT,
                    conversationId,
                    pageContext
            );

            // 转换WorkflowEventVo为ChatResponseVo(保持与前端兼容)
            return eventFlux.map(this::convertWorkflowEventToChatResponse);
        }

        // Layer 2: Orchestrator-Workers模式
        log.info("【routeAndExecute】Layer 2: 进入Orchestrator-Workers模式");

        try {
            // 调用Orchestrator进行任务分解和执行(传递conversationId支持多轮对话)
            OrchestratorFinalResponse response = orchestrateAndExecute(userInput, userId, tenantCode, conversationId, pageContext);

            // 将Orchestrator结果转换为ChatResponseVo格式(兼容前端,传递conversationId支持多轮对话)
            return convertOrchestratorResponseToChatResponseStream(response, userInput, userId, tenantCode, conversationId);

        } catch (Exception e) {
            log.error("【routeAndExecute】Orchestrator-Workers执行失败: userInput={}", userInput, e);
            return Flux.just(ChatResponseVo.createErrorResponse("Orchestrator-Workers执行失败: " + e.getMessage()));
        }
    }

    /**
     * 将Orchestrator执行结果转换为SSE事件流
     *
     * @param response Orchestrator最终响应
     * @param userInput 原始用户输入
     * @return Flux<WorkflowEventVo> 事件流
     */
    private Flux<WorkflowEventVo> convertOrchestratorResponseToEventStream(
            OrchestratorFinalResponse response,
            String userInput) {

        log.info("【SSE事件转换】开始转换Orchestrator结果为SSE事件流");
        log.info("【SSE事件转换】analysis={}", response.analysis());
        log.info("【SSE事件转换】workerResults数量={}", response.workerResults().size());

        List<WorkflowEventVo> events = new ArrayList<>();

        // 1. 发送开始事件
        JSONObject startData = new JSONObject();
        startData.put("type", "orchestrator");
        startData.put("analysis", response.analysis());
        startData.put("taskCount", response.workerResults().size());
        WorkflowEventVo startEvent = WorkflowEventVo.createStartEvent(startData.toJSONString());
        events.add(startEvent);
        log.info("【SSE事件-1】START事件: event={}, data长度={}", startEvent.getEvent(), startEvent.getData().length());
        log.debug("【SSE事件-1】START事件完整data: {}", startEvent.getData());

        // 2. 发送每个Worker的执行结果
        int index = 0;
        for (String workerResult : response.workerResults()) {
            JSONObject nodeData = new JSONObject();
            nodeData.put("workerIndex", index);
            nodeData.put("result", workerResult);
            WorkflowEventVo nodeEvent = WorkflowEventVo.createNodeRunEvent("worker_" + index, nodeData.toJSONString());
            events.add(nodeEvent);
            log.info("【SSE事件-{}】NODE_RUN事件: event={}, data长度={}", index + 2, nodeEvent.getEvent(), nodeEvent.getData().length());
            log.debug("【SSE事件-{}】NODE_RUN事件完整data: {}", index + 2, nodeEvent.getData());
            log.info("【SSE事件-{}】workerResult前100字符: {}", index + 2,
                workerResult.length() > 100 ? workerResult.substring(0, 100) : workerResult);
            index++;
        }

        // 3. 发送完成事件,包含汇总结果
        String summary = buildSummaryFromResults(response);
        JSONObject doneData = new JSONObject();
        doneData.put("analysis", response.analysis());
        doneData.put("workerResults", response.workerResults());
        doneData.put("summary", summary);
        WorkflowEventVo doneEvent = WorkflowEventVo.createDoneEvent(doneData.toJSONString());
        events.add(doneEvent);
        log.info("【SSE事件-{}】DONE事件: event={}, data长度={}", index + 2, doneEvent.getEvent(), doneEvent.getData().length());
        log.debug("【SSE事件-{}】DONE事件完整data: {}", index + 2, doneEvent.getData());
        log.info("【SSE事件-{}】summary前100字符: {}", index + 2,
            summary.length() > 100 ? summary.substring(0, 100) : summary);

        log.info("【SSE事件转换】完成,总共生成{}个事件", events.size());
        return Flux.fromIterable(events);
    }

    /**
     * 将Orchestrator执行结果转换为ChatResponseVo流(兼容前端Spring AI格式)
     *
     * 核心改进:使用Synthesizer LLM将工具执行结果转换为自然语言对话,而不是直接返回结构化数据
     * 支持多轮对话: 传递conversationId给Memory Advisor,保持上下文一致性
     * 支持Token记录: 捕获Usage对象并记录到ai_token_usage表
     *
     * @param response Orchestrator最终响应
     * @param userInput 原始用户输入
     * @param userId 用户ID
     * @param tenantCode 租户代码
     * @param conversationId 对话ID(用于多轮对话memory)
     * @return Flux<ChatResponseVo> 事件流
     */
    private Flux<ChatResponseVo> convertOrchestratorResponseToChatResponseStream(
            OrchestratorFinalResponse response,
            String userInput,
            Long userId,
            String tenantCode,
            String conversationId) {

        log.info("【Synthesizer】开始使用LLM合成最终回复");
        log.info("【Synthesizer】用户问题={}", userInput);
        log.info("【Synthesizer】工具结果数量={}", response.workerResults().size());
        log.info("【Synthesizer】runtimeUuid={}", response.runtimeUuid());
        log.info("【Synthesizer】conversationId={}", conversationId);

        // 构建Synthesizer的prompt - 让LLM根据工具结果生成自然语言回复
        StringBuilder synthesizerPrompt = new StringBuilder();
        synthesizerPrompt.append("你是一个智能助手,需要根据工具执行结果回答用户问题。\n\n");
        synthesizerPrompt.append("用户问题: ").append(userInput).append("\n\n");
        synthesizerPrompt.append("工具执行结果:\n");

        int index = 1;
        for (String workerResult : response.workerResults()) {
            synthesizerPrompt.append(index).append(". ").append(workerResult).append("\n\n");
            index++;
        }

        synthesizerPrompt.append("\n请根据以上工具执行结果,用自然、友好的语言回答用户的问题。");
        synthesizerPrompt.append("不要提及'工具'、'执行结果'等技术细节,就像正常聊天一样回复用户。");

        log.info("【Synthesizer】调用LLM生成回复, prompt长度={}", synthesizerPrompt.length());

        // 【关键修复】在Synthesizer层累积完整内容，确保isComplete事件包含完整回复
        // 使用final数组包装StringBuilder，解决lambda中变量必须是effectively final的问题
        final StringBuilder[] contentAccumulator = { new StringBuilder() };

        // 保存runtimeUuid的引用(用于在lambda中访问)
        final String runtimeUuid = response.runtimeUuid();

        // 【Token记录】保存runtimeId引用,捕获Usage对象和开始时间
        final Long runtimeId = response.runtimeId();
        final Usage[] finalUsage = {null};
        final long startTime = System.currentTimeMillis();

        // 【日志记录】创建Synthesizer节点记录(用于Token记录关联)
        final Long synthesizerNodeId = createSynthesizerNodeRecord(runtimeId, synthesizerPrompt.toString(), userId, tenantCode);
        log.info("【日志记录】创建Synthesizer节点记录: nodeId={}", synthesizerNodeId);

        // 调用Synthesizer LLM生成自然语言回复(流式,支持多轮对话memory)
        // 使用orchestratorChatClient(与Orchestrator阶段相同的ChatClient)
        // extraBody 已在 orchestratorChatClient Bean 的 defaultOptions 中配置
        return orchestratorChatClient.prompt()
            .user(synthesizerPrompt.toString())
            // 传递conversationId给Memory Advisor,保持上下文一致性
            .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId))
            .stream()
            .chatResponse()
            .doOnNext(springChatResponse -> {
                // 【Token记录】累积Usage信息(通常在最后一个响应中包含完整Usage)
                if (springChatResponse.getMetadata() != null && springChatResponse.getMetadata().getUsage() != null) {
                    finalUsage[0] = springChatResponse.getMetadata().getUsage();
                }
            })
            .map(springChatResponse -> {
                // 转换Spring AI的ChatResponse为我们的ChatResponseVo格式
                // 提取content内容
                String content = "";
                if (springChatResponse.getResult() != null
                    && springChatResponse.getResult().getOutput() != null) {
                    content = springChatResponse.getResult().getOutput().getText();
                    if (content == null) {
                        content = "";
                    }
                }

                // 累积所有chunk内容
                contentAccumulator[0].append(content);

                // 【修复】检查是否是最后一个chunk - 必须检查finishReason的具体值
                // Spring AI流式API中,每个chunk都可能有metadata,但只有最后一个chunk的finishReason表示完成
                // 不同的LLM API可能返回不同的完成标志: STOP, END_TURN, COMPLETE, LENGTH等
                boolean isComplete = false;
                if (springChatResponse.getResult() != null
                    && springChatResponse.getResult().getMetadata() != null
                    && springChatResponse.getResult().getMetadata().getFinishReason() != null) {
                    String finishReason = springChatResponse.getResult().getMetadata().getFinishReason();
                    // 检查是否是完成标志(支持多种LLM API的finishReason)
                    // STOP: OpenAI标准完成
                    // END_TURN: Anthropic完成
                    // COMPLETE/FINISHED: 其他可能的完成标志
                    // LENGTH: 达到token限制(也算完成)
                    isComplete = finishReason != null && (
                        "STOP".equalsIgnoreCase(finishReason) ||
                        "END_TURN".equalsIgnoreCase(finishReason) ||
                        "COMPLETE".equalsIgnoreCase(finishReason) ||
                        "FINISHED".equalsIgnoreCase(finishReason) ||
                        "LENGTH".equalsIgnoreCase(finishReason)
                    );
                    log.info("【Synthesizer】chunk检查: finishReason={}, isComplete={}, 累积长度={}",
                        finishReason, isComplete, contentAccumulator[0].length());
                }

                if (isComplete) {
                    // 【关键】最后一个chunk,使用累积的完整内容而不是最后一个chunk的内容
                    String fullContent = contentAccumulator[0].toString();
                    log.info("【Synthesizer】完成事件,累积内容长度={}, runtimeUuid={}", fullContent.length(), runtimeUuid);

                    return ChatResponseVo.builder()
                        .results(List.of(
                            ChatResponseVo.Generation.builder()
                                .output(ChatResponseVo.AssistantMessage.builder()
                                    .content(fullContent)  // 使用完整累积内容
                                    .build())
                                .metadata(ChatResponseVo.GenerationMetadata.builder()
                                    .finishReason("stop")
                                    .build())
                                .build()
                        ))
                        .isComplete(true)
                        .runtimeUuid(runtimeUuid)  // 【新增】返回runtimeUuid用于前端显示执行详情icon
                        .build();
                } else {
                    // 中间chunk,只包含当前内容
                    return ChatResponseVo.createContentChunk(content);
                }
            })
            .doOnComplete(() -> {
                log.info("【Synthesizer】LLM流式回复完成, 最终内容长度={}", contentAccumulator[0].length());

                // 【Token记录】记录Synthesizer LLM的Token使用量(关联到节点记录)
                recordSynthesizerTokenUsage(finalUsage[0], conversationId, synthesizerNodeId, userId, tenantCode, startTime);

                // 【日志记录】更新Synthesizer节点记录(执行完成)
                updateSynthesizerNodeRecord(synthesizerNodeId, contentAccumulator[0].toString(), true, null, tenantCode);
            })
            .doOnError(e -> log.error("【Synthesizer】LLM调用失败", e));
    }

    /**
     * 格式化Worker执行结果为用户友好的文本
     *
     * @param workerResult Worker执行结果(通常是JSON字符串)
     * @param index Worker序号
     * @return 格式化后的文本
     */
    private String formatWorkerResult(String workerResult, int index) {
        try {
            // 尝试解析为JSON
            JSONObject resultJson = JSON.parseObject(workerResult);

            // 检查是否是成功的结果
            Boolean success = resultJson.getBoolean("success");

            if (Boolean.FALSE.equals(success)) {
                // 失败情况:提取error信息
                String error = resultJson.getString("error");
                return String.format("⚠️ 执行步骤 %d 遇到问题:\n%s\n\n", index,
                    error != null ? error : "未知错误");
            } else {
                // 成功情况:提取message信息
                String message = resultJson.getString("message");

                if (message != null) {
                    return String.format("✅ 执行步骤 %d:\n%s\n\n", index, message);
                } else {
                    // 没有message字段,尝试提取其他有用信息
                    // 例如pageContext等
                    if (resultJson.containsKey("pageContext")) {
                        JSONObject pageContext = resultJson.getJSONObject("pageContext");
                        String pageTitle = pageContext.getString("title");
                        String pageCode = pageContext.getString("page_code");
                        return String.format("✅ 执行步骤 %d:\n获取了页面信息: %s (编码: %s)\n\n",
                            index, pageTitle, pageCode);
                    }

                    // 默认返回成功但没有详细信息
                    return String.format("✅ 执行步骤 %d: 完成\n\n", index);
                }
            }

        } catch (Exception e) {
            // 如果不是JSON或解析失败,直接返回原始文本
            log.warn("【formatWorkerResult】解析worker结果失败,返回原始文本: {}", e.getMessage());
            return String.format("🔧 执行步骤 %d:\n%s\n\n", index, workerResult);
        }
    }

    /**
     * 从Orchestrator结果构建汇总文本
     *
     * @param response Orchestrator最终响应
     * @return 汇总文本
     */
    private String buildSummaryFromResults(OrchestratorFinalResponse response) {
        StringBuilder summary = new StringBuilder();
        summary.append("📋 任务分析:\n").append(response.analysis()).append("\n\n");
        summary.append("📊 执行结果:\n");

        int index = 1;
        for (String result : response.workerResults()) {
            // 同样格式化汇总中的结果
            String formattedResult = formatWorkerResult(result, index);
            summary.append(formattedResult);
            index++;
        }

        return summary.toString();
    }

    /**
     * 将WorkflowEventVo转换为ChatResponseVo(兼容前端)
     *
     * <p>WorkflowEventVo是内部工作流事件格式,前端期望ChatResponseVo格式</p>
     *
     * @param event 工作流事件
     * @return ChatResponseVo
     */
    private ChatResponseVo convertWorkflowEventToChatResponse(WorkflowEventVo event) {
        try {
            // 将event.data解析为JSON对象
            JSONObject eventData = JSON.parseObject(event.getData());

            // 根据不同的event类型构建不同的响应
            String eventType = event.getEvent();

            if ("start".equals(eventType)) {
                // start事件: 返回空内容块(前端需要这个事件来初始化)
                return ChatResponseVo.createContentChunk("");

            } else if (eventType != null && eventType.startsWith("[NODE_CHUNK_")) {
                // NODE_CHUNK事件: LLM流式输出,提取chunk内容
                String chunk = eventData.getString("chunk");
                return ChatResponseVo.createContentChunk(chunk != null ? chunk : "");

            } else if ("done".equals(eventType)) {
                // done事件: 标记完成,提取完整内容
                String content = eventData.getString("fullContent");
                if (content == null) {
                    content = "";
                }

                return ChatResponseVo.builder()
                    .results(List.of(
                        ChatResponseVo.Generation.builder()
                            .output(ChatResponseVo.AssistantMessage.builder()
                                .content(content)
                                .build())
                            .metadata(ChatResponseVo.GenerationMetadata.builder()
                                .finishReason("stop")
                                .build())
                            .build()
                    ))
                    .isComplete(true)
                    .runtimeId(eventData.getLong("runtimeId"))
                    .runtimeUuid(eventData.getString("runtimeUuid"))
                    .workflowUuid(eventData.getString("workflowUuid"))
                    .build();

            } else if ("error".equals(eventType)) {
                // error事件: 提取错误消息
                String errorMsg = eventData.getString("errorMessage");
                return ChatResponseVo.createErrorResponse(errorMsg != null ? errorMsg : "工作流执行失败");

            } else {
                // 其他事件(NODE_RUN, NODE_INPUT, NODE_OUTPUT等): 返回空内容块
                return ChatResponseVo.createContentChunk("");
            }

        } catch (Exception e) {
            log.error("转换WorkflowEventVo失败: event={}", event, e);
            return ChatResponseVo.createContentChunk("");
        }
    }

    /**
     * 解析Orchestrator LLM响应为结构化对象
     *
     * @param responseContent LLM返回的JSON字符串
     * @return OrchestratorResponse对象
     */
    private OrchestratorResponse parseOrchestratorResponse(String responseContent) {
        try {
            // 尝试直接解析JSON
            String jsonContent = responseContent.trim();

            // 处理可能的markdown代码块包装
            if (jsonContent.startsWith("```json")) {
                jsonContent = jsonContent.substring(7);
            } else if (jsonContent.startsWith("```")) {
                jsonContent = jsonContent.substring(3);
            }
            if (jsonContent.endsWith("```")) {
                jsonContent = jsonContent.substring(0, jsonContent.length() - 3);
            }
            jsonContent = jsonContent.trim();

            JSONObject json = JSON.parseObject(jsonContent);
            String analysis = json.getString("analysis");
            List<SubTask> tasks = json.getList("tasks", SubTask.class);

            return new OrchestratorResponse(analysis, tasks != null ? tasks : new ArrayList<>());
        } catch (Exception e) {
            log.error("【Orchestrator】解析响应失败, content={}", responseContent, e);
            // 返回空任务列表的默认响应
            return new OrchestratorResponse("解析失败: " + e.getMessage(), new ArrayList<>());
        }
    }

    /**
     * 记录Orchestrator模式的Token使用量
     *
     * @param usage Spring AI返回的Usage对象
     * @param conversationId 对话ID
     * @param nodeId 节点记录ID(ai_conversation_runtime_node.id)
     * @param userId 用户ID
     * @param tenantCode 租户代码
     * @param startTime 开始时间戳
     */
    private void recordOrchestratorTokenUsage(Usage usage, String conversationId, Long nodeId,
                                               Long userId, String tenantCode, long startTime) {
        if (usage == null) {
            log.warn("【Orchestrator-Token】Usage为null,跳过记录: conversationId={}", conversationId);
            return;
        }

        try {
            // 设置租户上下文
            if (StringUtils.isNotBlank(tenantCode)) {
                DataSourceHelper.use(tenantCode);
            }

            // 获取模型配置
            AiModelConfigVo modelConfig;
            try {
                AIChatRequestVo request = new AIChatRequestVo();
                request.setAiType("LLM");
                modelConfig = aiChatBaseService.getModule(request, null);
            } catch (Exception e) {
                log.error("【Orchestrator-Token】获取模型配置失败", e);
                return;
            }

            // 提取Token数量
            Long promptTokens = usage.getPromptTokens() != null ? usage.getPromptTokens().longValue() : 0L;
            Long completionTokens = usage.getCompletionTokens() != null ? usage.getCompletionTokens().longValue() : 0L;
            Long responseTime = System.currentTimeMillis() - startTime;

            // 获取AiTokenUsageService
            AiTokenUsageService tokenUsageService = SpringUtil.getBean(AiTokenUsageService.class);
            if (tokenUsageService == null) {
                log.error("【Orchestrator-Token】AiTokenUsageService not found in Spring context");
                return;
            }

            // 【修正】对于Orchestrator模式:
            // serial_type = "ai_conversation_runtime_node" (关联到节点记录)
            // serial_id = nodeId (节点记录的主键ID)
            tokenUsageService.recordTokenUsageAsync(
                    conversationId,
                    "ai_conversation_runtime_node",
                    String.valueOf(nodeId),
                    modelConfig.getId().toString(),
                    String.valueOf(userId),
                    modelConfig.getProvider(),
                    modelConfig.getModelName(),
                    promptTokens,
                    completionTokens,
                    true,
                    responseTime
            );

            log.info("【Orchestrator-Token】记录成功: conversationId={}, nodeId={}, tokens={}/{}/{}, responseTime={}ms",
                    conversationId, nodeId, promptTokens, completionTokens, (promptTokens + completionTokens), responseTime);

        } catch (Exception e) {
            log.error("【Orchestrator-Token】记录失败: conversationId={}, nodeId={}", conversationId, nodeId, e);
        }
    }

    /**
     * 记录Synthesizer模式的Token使用量
     *
     * @param usage Spring AI返回的Usage对象
     * @param conversationId 对话ID
     * @param nodeId 节点记录ID(ai_conversation_runtime_node.id)
     * @param userId 用户ID
     * @param tenantCode 租户代码
     * @param startTime 开始时间戳
     */
    private void recordSynthesizerTokenUsage(Usage usage, String conversationId, Long nodeId,
                                              Long userId, String tenantCode, long startTime) {
        if (usage == null) {
            log.warn("【Synthesizer-Token】Usage为null,跳过记录: conversationId={}", conversationId);
            return;
        }

        try {
            // 设置租户上下文（doOnComplete回调中可能丢失上下文）
            if (StringUtils.isNotBlank(tenantCode)) {
                DataSourceHelper.use(tenantCode);
            }

            // 获取模型配置（使用默认LLM配置）
            AiModelConfigVo modelConfig;
            try {
                AIChatRequestVo request = new AIChatRequestVo();
                request.setAiType("LLM");
                modelConfig = aiChatBaseService.getModule(request, null);
            } catch (Exception e) {
                log.error("【Synthesizer-Token】获取模型配置失败", e);
                return;
            }

            // 提取Token数量
            Long promptTokens = usage.getPromptTokens() != null ? usage.getPromptTokens().longValue() : 0L;
            Long completionTokens = usage.getCompletionTokens() != null ? usage.getCompletionTokens().longValue() : 0L;
            Long responseTime = System.currentTimeMillis() - startTime;

            // 获取AiTokenUsageService
            AiTokenUsageService tokenUsageService = SpringUtil.getBean(AiTokenUsageService.class);
            if (tokenUsageService == null) {
                log.error("【Synthesizer-Token】AiTokenUsageService not found in Spring context");
                return;
            }

            // 【修正】对于Synthesizer模式:
            // serial_type = "ai_conversation_runtime_node" (关联到节点记录)
            // serial_id = nodeId (节点记录的主键ID)
            tokenUsageService.recordTokenUsageAsync(
                    conversationId,
                    "ai_conversation_runtime_node",
                    String.valueOf(nodeId),
                    modelConfig.getId().toString(),
                    String.valueOf(userId),
                    modelConfig.getProvider(),
                    modelConfig.getModelName(),
                    promptTokens,
                    completionTokens,
                    true,
                    responseTime
            );

            log.info("【Synthesizer-Token】记录成功: conversationId={}, nodeId={}, tokens={}/{}/{}, responseTime={}ms",
                    conversationId, nodeId, promptTokens, completionTokens, (promptTokens + completionTokens), responseTime);

        } catch (Exception e) {
            log.error("【Synthesizer-Token】记录失败: conversationId={}, nodeId={}", conversationId, nodeId, e);
            // 记录失败不抛出异常,避免影响Synthesizer执行
        }
    }
}
