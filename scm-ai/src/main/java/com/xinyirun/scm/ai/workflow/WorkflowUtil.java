package com.xinyirun.scm.ai.workflow;

import cn.hutool.extra.spring.SpringUtil;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatOptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.core.service.chat.AiChatBaseService;
import com.xinyirun.scm.ai.core.service.chat.AiTokenUsageService;
import com.xinyirun.scm.ai.core.service.config.AiModelConfigService;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowComponentService;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.data.NodeIODataContent;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流工具类
 *
 * 提供工作流执行过程中常用的功能：
 * 1. 模板渲染 - 支持 ${paramName} 格式的变量替换
 * 2. LLM 调用 - 调用 AI 模型生成响应
 *
 * 注意：所有方法均为静态方法，不需要 Spring 组件注册
 */
@Slf4j
public class WorkflowUtil {

    /**
     * 渲染模板字符串
     *
     * 将模板中的 ${paramName} 替换为实际的参数值
     * 支持多种数据类型（文本、文件列表、选项等）
     *
     * @param template 包含 ${paramName} 格式的模板字符串
     * @param values 包含参数数据的 NodeIOData 列表
     * @return 渲染后的字符串
     */
    public static String renderTemplate(String template, List<NodeIOData> values) {
        String result = template;
        for (NodeIOData next : values) {
            String name = next.getName();
            NodeIODataContent<?> dataContent = (NodeIODataContent<?>) next.getContent();

            if (dataContent.getValue() instanceof List) {
                List<?> list = (List<?>) dataContent.getValue();
                String joinedValue = String.join(",", list.stream()
                        .map(Object::toString)
                        .toArray(String[]::new));
                result = result.replace("${" + name + "}", joinedValue);
            } else if (dataContent.getValue() != null) {
                result = result.replace("${" + name + "}", dataContent.getValue().toString());
            }
        }
        return result;
    }

    /**
     * 调用 LLM 模型生成响应（非流式）
     *
     * 通过 AiChatBaseService 调用配置的 LLM 模型，获取 AI 生成的响应内容
     * 用于需要获取完整响应的场景（如分类器节点）
     *
     * @param wfState 工作流状态对象
     * @param modelName 模型名称
     * @param prompt 提示词/问题
     * @return 包含 LLM 响应的 NodeIOData 对象
     */
    public static NodeIOData invokeLLM(WfState wfState, String modelName, String prompt) {
        log.info("invoke LLM (non-streaming), modelName: {}, prompt length: {}", modelName,
                StringUtils.isNotBlank(prompt) ? prompt.length() : 0);

        try {
            AIChatRequestVo request = new AIChatRequestVo();
            request.setAiType("LLM");

            AiChatBaseService aiChatBaseService = SpringUtil.getBean(AiChatBaseService.class);
            if (aiChatBaseService == null) {
                throw new RuntimeException("AiChatBaseService not found in Spring context");
            }

            AiModelConfigService aiModelConfigService = SpringUtil.getBean(AiModelConfigService.class);
            AiModelConfigVo modelConfig = resolveModelConfig(modelName, aiModelConfigService, aiChatBaseService, request);
            log.info("invoke LLM (non-streaming), 实际使用模型: {}", modelConfig.getName());

            AIChatOptionVo chatOption = new AIChatOptionVo();
            chatOption.setModule(modelConfig);
            chatOption.setPrompt(prompt);

            String response = aiChatBaseService.chat(chatOption).content();

            log.info("LLM response length: {}", StringUtils.isNotBlank(response) ? response.length() : 0);

            return NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", response);
        } catch (Exception e) {
            log.error("invoke LLM failed", e);
            throw new RuntimeException("LLM 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 流式调用 LLM 模型生成响应（向后兼容版本，默认显示输出）
     *
     * @param wfState 工作流状态对象
     * @param nodeState 工作流节点状态
     * @param node 工作流节点定义
     * @param modelName 模型名称
     * @param prompt 提示词/问题
     */
    public static void streamingInvokeLLM(WfState wfState, WfNodeState nodeState, AiWorkflowNodeVo node,
                                           String modelName, String prompt) {
        streamingInvokeLLM(wfState, nodeState, node, modelName, prompt, false);
    }

    /**
     * 流式调用 LLM 模型生成响应
     *
     * <p>支持多轮对话上下文记忆功能：</p>
     * <ul>
     *   <li>从 wfState 获取 conversationId</li>
     *   <li>LLM 调用前保存 USER 消息</li>
     *   <li>使用 chatWithMemoryStream() 调用 LLM（带记忆）</li>
     *   <li>LLM 调用后保存 ASSISTANT 消息</li>
     *   <li>降级模式: conversationId 为 NULL 时使用 chatStream()（无记忆）</li>
     *   <li>记录Token使用量到ai_token_usage表</li>
     * </ul>
     *
     * @param wfState 工作流状态对象
     * @param nodeState 工作流节点状态
     * @param node 工作流节点定义
     * @param modelName 模型名称
     * @param prompt 提示词/问题
     * @param silentMode 静默模式，true时不流式推送输出到前端，但数据仍传递给下游节点
     */
    public static void streamingInvokeLLM(WfState wfState, WfNodeState nodeState, AiWorkflowNodeVo node,
                                           String modelName, String prompt, boolean silentMode) {
        String conversationId = wfState.getConversationId();

        // 提取原始用户输入（用于对话记录，而不是渲染后的prompt）
        String originalUserInput = extractOriginalUserInput(wfState);

        log.info("invoke LLM (streaming), modelName: {}, conversationId: {}, originalUserInput length: {}, prompt length: {}",
                modelName, conversationId,
                originalUserInput != null ? originalUserInput.length() : 0,
                StringUtils.isNotBlank(prompt) ? prompt.length() : 0);

        // 用于捕获最终Usage(Token使用量)
        final Usage[] finalUsage = {null};
        final long startTime = System.currentTimeMillis();

        try {
            AIChatRequestVo request = new AIChatRequestVo();
            request.setAiType("LLM");

            AiChatBaseService aiChatBaseService = SpringUtil.getBean(AiChatBaseService.class);
            if (aiChatBaseService == null) {
                throw new RuntimeException("AiChatBaseService not found in Spring context");
            }

            AiModelConfigService aiModelConfigService = SpringUtil.getBean(AiModelConfigService.class);
            AiModelConfigVo modelConfig = resolveModelConfig(modelName, aiModelConfigService, aiChatBaseService, request);
            log.info("invoke LLM (streaming), 实际使用模型: {}", modelConfig.getName());

            AIChatOptionVo chatOption = new AIChatOptionVo();
            chatOption.setModule(modelConfig);
            chatOption.setPrompt(prompt);

            // 节点类型检测:判断是否为MCP工具节点
            boolean isMcpToolNode = isMcpToolNode(node);
            chatOption.setEnableMcpTools(isMcpToolNode);
            log.info("节点类型判断 - 节点UUID: {}, 标题: {}, 是否MCP工具节点: {}",
                    node.getUuid(), node.getTitle(), isMcpToolNode);

            StringBuilder fullResponse = new StringBuilder();

            // 构建toolContext（MCP工具节点和有记忆模式都需要）
            Map<String, Object> toolContextMap = new HashMap<>();
            toolContextMap.put("tenantCode", wfState.getTenantCode());
            toolContextMap.put("staffId", wfState.getUserId());
            toolContextMap.put("nodeState", nodeState);
            if (wfState.getPageContext() != null) {
                toolContextMap.put("pageContext", wfState.getPageContext());
            }
            chatOption.setToolContext(toolContextMap);

            // 获取流式响应：根据节点类型和conversationId选择不同模式
            ChatClient.StreamResponseSpec streamSpec;

            if (isMcpToolNode) {
                // MCP工具节点：使用MCP工具但不加载对话历史，避免知识库内容干扰工具调用
                log.info("MCP工具节点使用无记忆模式, 避免对话历史干扰工具调用");
                streamSpec = aiChatBaseService.chatStreamWithMcpTools(chatOption);
            } else if (StringUtils.isBlank(conversationId)) {
                log.warn("conversationId is null, fallback to no-memory mode");
                streamSpec = aiChatBaseService.chatStream(chatOption);
            } else {
                // 有记忆模式
                chatOption.setConversationId(conversationId);
                String runtimeUuid = wfState.getUuid();
                log.debug("LLM流式调用开始 - conversationId: {}, runtimeUuid: {}", conversationId, runtimeUuid);
                streamSpec = aiChatBaseService.chatWithWorkflowMemoryStream(chatOption, runtimeUuid, originalUserInput, wfState.getCallSource());
            }

            // 统一的流式处理逻辑
            streamSpec.chatResponse()
                    .doOnSubscribe(sub -> log.debug("LLM Flux已订阅"))
                    .doOnNext(chatResponse -> {
                        // 在Reactor流回调中设置租户上下文，防止线程切换导致上下文丢失
                        if (wfState.getTenantCode() != null) {
                            DataSourceHelper.use(wfState.getTenantCode());
                        }

                        String content = chatResponse.getResult().getOutput().getText();
                        if (StringUtils.isNotBlank(content)) {
                            fullResponse.append(content);
                        }

                        // 累积Usage信息（通常在最后一个响应中包含完整Usage）
                        if (chatResponse.getMetadata() != null && chatResponse.getMetadata().getUsage() != null) {
                            finalUsage[0] = chatResponse.getMetadata().getUsage();
                        }
                    })
                    .doOnComplete(() -> {
                        log.debug("LLM Flux完成(onComplete)");
                        recordWorkflowTokenUsage(wfState, node, finalUsage[0], modelConfig, startTime);
                    })
                    .doOnError(e -> log.error("LLM Flux发生错误(onError)", e))
                    .doOnCancel(() -> log.warn("LLM Flux被取消(onCancel)"))
                    .timeout(java.time.Duration.ofSeconds(120))
                    .blockLast();

            log.debug("LLM blockLast()已返回 - conversationId: {}", conversationId);

            String response = fullResponse.toString();
            // 移除前导和尾随空白字符,避免Markdown渲染为代码块
            if (StringUtils.isNotBlank(response)) {
                response = response.trim();
            }
            log.info("LLM streaming response completed, conversationId: {}, total length: {}",
                    conversationId, response.length());

            NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", response);
            nodeState.getOutputs().add(output);
        } catch (Exception e) {
            log.error("invoke LLM (streaming) failed, conversationId: {}", conversationId, e);
            throw new RuntimeException("LLM 流式调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 记录工作流节点的Token使用量
     *
     * @param wfState 工作流状态
     * @param node 工作流节点定义
     * @param usage Spring AI返回的Usage对象
     * @param modelConfig 模型配置
     * @param startTime 开始时间戳
     */
    private static void recordWorkflowTokenUsage(WfState wfState, AiWorkflowNodeVo node,
                                                   Usage usage, AiModelConfigVo modelConfig, long startTime) {
        if (usage == null) {
            log.warn("【Workflow-Token】Usage为null,跳过记录: nodeUuid={}", node.getUuid());
            return;
        }

        try {
            // 设置租户上下文（doOnComplete回调中可能丢失上下文）
            if (wfState.getTenantCode() != null) {
                DataSourceHelper.use(wfState.getTenantCode());
            }

            // 从wfState获取运行时节点信息
            AiWorkflowRuntimeNodeVo runtimeNode = wfState.getRuntimeNodeByNodeUuid(node.getUuid());
            String serialId = null;
            if (runtimeNode != null && runtimeNode.getId() != null) {
                serialId = String.valueOf(runtimeNode.getId());
            }

            if (serialId == null) {
                log.warn("【Workflow-Token】RuntimeNodeId为null,使用nodeUuid作为serialId: nodeUuid={}", node.getUuid());
                serialId = node.getUuid();  // 降级使用nodeUuid
            }

            String conversationId = wfState.getConversationId();
            Long userId = wfState.getUserId();

            // 获取token数量
            Long promptTokens = usage.getPromptTokens() != null ? usage.getPromptTokens().longValue() : 0L;
            Long completionTokens = usage.getCompletionTokens() != null ? usage.getCompletionTokens().longValue() : 0L;
            Long responseTime = System.currentTimeMillis() - startTime;

            // 获取AiTokenUsageService
            AiTokenUsageService tokenUsageService = SpringUtil.getBean(AiTokenUsageService.class);
            if (tokenUsageService == null) {
                log.error("【Workflow-Token】AiTokenUsageService not found in Spring context");
                return;
            }

            // 记录token使用
            tokenUsageService.recordTokenUsageAsync(
                    conversationId,                             // conversationId (可能为null)
                    "ai_workflow_runtime_node",                 // serial_type
                    serialId,                                   // serial_id
                    modelConfig.getId().toString(),             // modelSourceId
                    String.valueOf(userId),                     // userId
                    modelConfig.getProvider(),                  // aiProvider
                    modelConfig.getModelName(),                 // aiModelType
                    promptTokens,                               // promptTokens
                    completionTokens,                           // completionTokens
                    true,                                       // success
                    responseTime                                // responseTime
            );

            log.info("【Workflow-Token】记录成功: nodeUuid={}, serialId={}, tokens={}/{}/{}, responseTime={}ms",
                    node.getUuid(), serialId, promptTokens, completionTokens, (promptTokens + completionTokens), responseTime);

        } catch (Exception e) {
            log.error("【Workflow-Token】记录失败: nodeUuid={}", node.getUuid(), e);
            // 记录失败不抛出异常,避免影响workflow执行
        }
    }

    /**
     * 判断节点是否为MCP工具节点
     *
     * 通过查询工作流组件信息,判断节点类型是否为"McpTool"
     *
     * @param node 工作流节点对象
     * @return true-是MCP工具节点, false-不是MCP工具节点
     */
    private static boolean isMcpToolNode(AiWorkflowNodeVo node) {
        if (node == null || node.getWorkflowComponentId() == null) {
            log.warn("节点或组件ID为null,默认为非MCP工具节点");
            return false;
        }

        try {
            AiWorkflowComponentService componentService = SpringUtil.getBean(AiWorkflowComponentService.class);
            if (componentService == null) {
                log.warn("AiWorkflowComponentService not found, 默认为非MCP工具节点");
                return false;
            }

            AiWorkflowComponentEntity component = componentService.getById(node.getWorkflowComponentId());
            if (component == null) {
                log.warn("组件未找到, componentId: {}, 默认为非MCP工具节点", node.getWorkflowComponentId());
                return false;
            }

            // 需要启用MCP工具的组件类型：McpTool、TempKnowledgeBase
            // TempKnowledgeBase通过LLM的Function Calling调用TempKnowledgeBaseMcpTools
            String componentName = component.getName();
            boolean isMcpTool = "McpTool".equals(componentName) || "TempKnowledgeBase".equals(componentName);
            log.debug("节点组件类型检测 - componentId: {}, name: {}, isMcpTool: {}",
                    component.getId(), componentName, isMcpTool);
            return isMcpTool;
        } catch (Exception e) {
            log.error("判断节点类型时发生异常, nodeId: {}, 默认为非MCP工具节点", node.getWorkflowComponentId(), e);
            return false;
        }
    }

    /**
     * 提取原始用户输入
     *
     * 从工作流初始输入中提取TEXT类型参数的值（动态查找第一个TEXT输入）。
     * 这是用户在开始节点输入的原始内容，用于保存到对话历史记录中。
     *
     * 注意：不再硬编码参数名"var_user_input"，而是动态查找type=1(TEXT)的第一个参数。
     * 这样支持用户自定义开始节点的参数名。
     *
     * @param wfState 工作流状态对象
     * @return 原始用户输入字符串，如果未找到则返回null
     */
    private static String extractOriginalUserInput(WfState wfState) {
        if (wfState == null || wfState.getInput() == null) {
            log.warn("❌ extractOriginalUserInput: wfState或input为null");
            return null;
        }

        log.info("🔍 extractOriginalUserInput: 开始提取用户输入，input数量: {}", wfState.getInput().size());

        // 动态查找第一个TEXT类型(type=1)的输入参数
        for (NodeIOData input : wfState.getInput()) {
            log.info("🔍 extractOriginalUserInput: 检查input - name: {}, contentType: {}",
                input.getName(), input.getContent() != null ? input.getContent().getType() : null);

            // 判断是否为TEXT类型(type=1)
            if (input.getContent() != null && Integer.valueOf(1).equals(input.getContent().getType())) {
                String value = input.valueToString();
                log.info("✅ extractOriginalUserInput: 找到TEXT类型输入(name={}), value: {}", input.getName(), value);
                return value;
            }
        }

        log.warn("❌ extractOriginalUserInput: 未找到TEXT类型的输入参数");
        return null;
    }

    // 注意：USER和ASSISTANT消息的保存已由MessageChatMemoryAdvisor自动管理
    // 通过ScmWorkflowMessageChatMemory.add()方法完成
    // 符合Spring AI框架的最佳实践

    /**
     * 根据模型名称解析模型配置
     *
     * 优先使用节点指定的模型（按显示名称查询），若未指定或查询失败则降级到系统默认模型。
     *
     * @param modelName 节点配置的模型显示名称，可为空
     * @param aiModelConfigService 模型配置服务
     * @param aiChatBaseService 聊天基础服务（用于获取默认模型）
     * @param request 聊天请求对象
     * @return 模型配置（含完整API Key）
     */
    private static AiModelConfigVo resolveModelConfig(String modelName,
                                                       AiModelConfigService aiModelConfigService,
                                                       AiChatBaseService aiChatBaseService,
                                                       AIChatRequestVo request) {
        if (StringUtils.isNotBlank(modelName)) {
            try {
                return aiModelConfigService.getModelConfigByName(modelName);
            } catch (Exception e) {
                log.warn("指定模型 [{}] 未找到，使用系统默认模型", modelName);
            }
        }
        return aiChatBaseService.getModule(request, null);
    }

}
