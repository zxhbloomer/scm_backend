package com.xinyirun.scm.ai.workflow;

import com.xinyirun.scm.ai.bean.vo.request.AIChatOptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.core.service.chat.AiChatBaseService;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 工作流工具类
 *
 * 提供工作流执行过程中常用的功能：
 * 1. 模板渲染 - 支持 ${paramName} 格式的变量替换
 * 2. LLM 调用 - 调用 AI 模型生成响应
 */
@Slf4j
@Component
public class WorkflowUtil {

    @Autowired
    private AiChatBaseService aiChatBaseService;

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
     * 调用 LLM 模型生成响应
     *
     * 通过 Spring AI 调用配置的 LLM 模型，获取 AI 生成的响应内容
     *
     * @param wfState 工作流状态对象
     * @param modelName 模型名称
     * @param prompt 提示词/问题
     * @return 包含 LLM 响应的 NodeIOData 对象
     */
    public static NodeIOData invokeLLM(WfState wfState, String modelName, String prompt) {
        log.info("invoke LLM, modelName: {}, prompt length: {}", modelName,
                StringUtils.isNotBlank(prompt) ? prompt.length() : 0);

        try {
            // 构建聊天请求
            AIChatRequestVo request = new AIChatRequestVo();
            request.setAiType("LLM");

            // 获取模型配置
            var modelConfig = aiChatBaseService.getModule(request, null);

            // 构建聊天选项
            AIChatOptionVo chatOption = new AIChatOptionVo();
            chatOption.setModule(modelConfig);
            chatOption.setPrompt(prompt);

            // 调用 AI 模型
            String response = aiChatBaseService.chat(chatOption)
                    .content();

            log.info("LLM response length: {}", StringUtils.isNotBlank(response) ? response.length() : 0);

            return NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", response);
        } catch (Exception e) {
            log.error("invoke LLM failed", e);
            throw new RuntimeException("LLM 调用失败: " + e.getMessage(), e);
        }
    }

}
