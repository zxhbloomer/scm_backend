package com.xinyirun.scm.ai.workflow;

import cn.hutool.extra.spring.SpringUtil;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatOptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.core.service.chat.AiChatBaseService;
import com.xinyirun.scm.ai.core.service.chat.AiConversationContentService;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.data.NodeIODataContent;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

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

            var modelConfig = aiChatBaseService.getModule(request, null);

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
     * 流式调用 LLM 模型生成响应
     *
     * <p>支持多轮对话上下文记忆功能：</p>
     * <ul>
     *   <li>从 wfState 获取 conversationId</li>
     *   <li>LLM 调用前保存 USER 消息</li>
     *   <li>使用 chatWithMemoryStream() 调用 LLM（带记忆）</li>
     *   <li>LLM 调用后保存 ASSISTANT 消息</li>
     *   <li>降级模式: conversationId 为 NULL 时使用 chatStream()（无记忆）</li>
     * </ul>
     *
     * @param wfState 工作流状态对象
     * @param nodeState 工作流节点状态
     * @param node 工作流节点定义
     * @param modelName 模型名称
     * @param prompt 提示词/问题
     */
    public static void streamingInvokeLLM(WfState wfState, WfNodeState nodeState, AiWorkflowNodeVo node,
                                           String modelName, String prompt) {
        String conversationId = wfState.getConversationId();
        log.info("invoke LLM (streaming), modelName: {}, conversationId: {}, prompt length: {}",
                modelName, conversationId, StringUtils.isNotBlank(prompt) ? prompt.length() : 0);

        try {
            AIChatRequestVo request = new AIChatRequestVo();
            request.setAiType("LLM");

            AiChatBaseService aiChatBaseService = SpringUtil.getBean(AiChatBaseService.class);
            if (aiChatBaseService == null) {
                throw new RuntimeException("AiChatBaseService not found in Spring context");
            }

            var modelConfig = aiChatBaseService.getModule(request, null);

            AIChatOptionVo chatOption = new AIChatOptionVo();
            chatOption.setModule(modelConfig);
            chatOption.setPrompt(prompt);

            StringBuilder fullResponse = new StringBuilder();

            // 降级模式判断: conversationId 为 NULL 时使用无记忆模式
            if (StringUtils.isBlank(conversationId)) {
                log.warn("conversationId is null, fallback to no-memory mode");

                // 无记忆模式 - 使用 chatStream()
                aiChatBaseService.chatStream(chatOption)
                        .chatResponse()
                        .doOnNext(chatResponse -> {
                            String content = chatResponse.getResult().getOutput().getText();
                            if (StringUtils.isNotBlank(content)) {
                                log.debug("LLM chunk: length={}", content.length());
                                fullResponse.append(content);

                                if (wfState.getStreamHandler() != null) {
                                    wfState.getStreamHandler().sendNodeChunk(node.getUuid(), content);
                                }
                            }
                        })
                        .blockLast();
            } else {
                // 有记忆模式 - 使用 chatWithMemoryStream()

                // 1. LLM 调用前保存 USER 消息
                saveUserMessage(conversationId, prompt,
                        modelConfig.getId() != null ? modelConfig.getId().toString() : null,
                        modelConfig.getProvider(), modelConfig.getModelName());

                // 2. 设置 conversationId 并调用 LLM
                chatOption.setConversationId(conversationId);

                aiChatBaseService.chatWithMemoryStream(chatOption)
                        .chatResponse()
                        .doOnNext(chatResponse -> {
                            String content = chatResponse.getResult().getOutput().getText();
                            if (StringUtils.isNotBlank(content)) {
                                log.debug("LLM chunk: length={}", content.length());
                                fullResponse.append(content);

                                if (wfState.getStreamHandler() != null) {
                                    wfState.getStreamHandler().sendNodeChunk(node.getUuid(), content);
                                }
                            }
                        })
                        .blockLast();

                // 3. LLM 调用后保存 ASSISTANT 消息
                String response = fullResponse.toString();
                if (StringUtils.isNotBlank(response)) {
                    response = response.trim();
                    saveAssistantMessage(conversationId, response,
                            modelConfig.getId() != null ? modelConfig.getId().toString() : null,
                            modelConfig.getProvider(), modelConfig.getModelName());
                }
            }

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
     * 保存用户消息到对话历史
     *
     * @param conversationId 对话ID
     * @param content 消息内容
     * @param modelSourceId 模型源ID
     * @param providerName 提供商名称
     * @param baseName 基础模型名称
     */
    private static void saveUserMessage(String conversationId, String content,
                                        String modelSourceId, String providerName, String baseName) {
        try {
            AiConversationContentService conversationContentService =
                    SpringUtil.getBean(AiConversationContentService.class);
            if (conversationContentService == null) {
                log.error("AiConversationContentService not found, skip saving USER message");
                return;
            }

            conversationContentService.saveConversationContent(
                    conversationId,
                    "user",
                    content,
                    modelSourceId,
                    providerName,
                    baseName,
                    null
            );

            log.debug("Saved USER message to conversation: {}", conversationId);
        } catch (Exception e) {
            log.error("Failed to save USER message, conversationId: {}", conversationId, e);
        }
    }

    /**
     * 保存助手消息到对话历史
     *
     * @param conversationId 对话ID
     * @param content 消息内容
     * @param modelSourceId 模型源ID
     * @param providerName 提供商名称
     * @param baseName 基础模型名称
     */
    private static void saveAssistantMessage(String conversationId, String content,
                                             String modelSourceId, String providerName, String baseName) {
        try {
            AiConversationContentService conversationContentService =
                    SpringUtil.getBean(AiConversationContentService.class);
            if (conversationContentService == null) {
                log.error("AiConversationContentService not found, skip saving ASSISTANT message");
                return;
            }

            conversationContentService.saveConversationContent(
                    conversationId,
                    "assistant",
                    content,
                    modelSourceId,
                    providerName,
                    baseName,
                    null
            );

            log.debug("Saved ASSISTANT message to conversation: {}", conversationId);
        } catch (Exception e) {
            log.error("Failed to save ASSISTANT message, conversationId: {}", conversationId, e);
        }
    }

}
