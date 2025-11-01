package com.xinyirun.scm.ai.workflow;

import cn.hutool.extra.spring.SpringUtil;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatOptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.core.service.chat.AiChatBaseService;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
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
            Object content = next.getContent();

            if (content instanceof List) {
                // 处理列表类型（如文件列表）
                List<?> list = (List<?>) content;
                String joinedValue = String.join(",", list.stream()
                        .map(Object::toString)
                        .toArray(String[]::new));
                result = result.replace("${" + name + "}", joinedValue);
            } else if (content != null) {
                // 处理基本类型
                result = result.replace("${" + name + "}", content.toString());
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
            // 构建聊天请求
            AIChatRequestVo request = new AIChatRequestVo();
            request.setAiType("LLM");

            // 获取模型配置 - 从 Spring 容器中获取 AiChatBaseService
            AiChatBaseService aiChatBaseService = SpringUtil.getBean(AiChatBaseService.class);
            if (aiChatBaseService == null) {
                throw new RuntimeException("AiChatBaseService not found in Spring context");
            }

            var modelConfig = aiChatBaseService.getModule(request, null);

            // 构建聊天选项
            AIChatOptionVo chatOption = new AIChatOptionVo();
            chatOption.setModule(modelConfig);
            chatOption.setPrompt(prompt);

            // 调用 AI 模型获取完整响应
            String response = aiChatBaseService.chat(chatOption).content();

            log.info("LLM response length: {}", StringUtils.isNotBlank(response) ? response.length() : 0);

            // 返回结果
            return NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", response);
        } catch (Exception e) {
            log.error("invoke LLM failed", e);
            throw new RuntimeException("LLM 调用失败: " + e.getMessage(), e);
        }
    }

    /**
     * 流式调用 LLM 模型生成响应
     *
     * 通过 AiChatBaseService 调用配置的 LLM 模型，支持流式响应
     * 用于需要实时流式输出的场景（如 LLM 答案节点）
     *
     * 注意：此方法为静态方法，与 invokeLLM() 方法一致，便于在节点中直接调用
     *
     * @param wfState 工作流状态对象
     * @param nodeState 工作流节点状态
     * @param node 工作流节点定义
     * @param modelName 模型名称
     * @param prompt 提示词/问题
     */
    public static void streamingInvokeLLM(WfState wfState, WfNodeState nodeState, AiWorkflowNodeVo node,
                                           String modelName, String prompt) {
        log.info("invoke LLM (streaming), modelName: {}, prompt length: {}", modelName,
                StringUtils.isNotBlank(prompt) ? prompt.length() : 0);

        try {
            // 构建聊天请求
            AIChatRequestVo request = new AIChatRequestVo();
            request.setAiType("LLM");

            // 获取模型配置 - 从 Spring 容器中获取 AiChatBaseService
            AiChatBaseService aiChatBaseService = SpringUtil.getBean(AiChatBaseService.class);
            if (aiChatBaseService == null) {
                throw new RuntimeException("AiChatBaseService not found in Spring context");
            }

            var modelConfig = aiChatBaseService.getModule(request, null);

            // 构建聊天选项
            AIChatOptionVo chatOption = new AIChatOptionVo();
            chatOption.setModule(modelConfig);
            chatOption.setPrompt(prompt);

            // ⭐ 使用流式调用（chatStream）而不是同步调用（chat）
            // 参考AiChatBaseService的chatStream()方法（新增的无记忆流式方法）
            StringBuilder fullResponse = new StringBuilder();

            aiChatBaseService.chatStream(chatOption)
                    .chatResponse()
                    .doOnNext(chatResponse -> {
                        // 实时接收每个chunk
                        String content = chatResponse.getResult().getOutput().getText();
                        if (StringUtils.isNotBlank(content)) {
                            log.debug("LLM chunk: length={}", content.length());
                            fullResponse.append(content);

                            // ⭐ 通过streamHandler实时发送chunk到前端
                            if (wfState.getStreamHandler() != null) {
                                wfState.getStreamHandler().sendNodeChunk(node.getUuid(), content);
                            }
                        }
                    })
                    .blockLast(); // 等待流式响应完成

            String response = fullResponse.toString();
            log.info("LLM streaming response completed, total length: {}", response.length());

            // 添加输出数据到节点状态
            NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", response);
            nodeState.getOutputs().add(output);
        } catch (Exception e) {
            log.error("invoke LLM (streaming) failed", e);
            throw new RuntimeException("LLM 流式调用失败: " + e.getMessage(), e);
        }
    }

}
