package com.xinyirun.scm.ai.workflow.node.mcptool;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowUtil;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * MCP工具节点
 * 支持两种执行模式：
 * 1. LLM模式(默认): 通过LLM的Function Calling能力自动选择和调用工具
 * 2. 直接调用模式(direct_call=true): 跳过LLM，直接调用指定工具，适合参数确定的查询场景
 *
 * @author zzxxhh
 * @since 2025-11-19
 */
@Slf4j
public class McpToolNode extends AbstractWfNode {

    public McpToolNode(AiWorkflowComponentEntity wfComponent,
                      AiWorkflowNodeVo node,
                      WfState wfState,
                      WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        log.info("开始执行MCP工具节点: {}", node.getTitle());

        try {
            McpToolNodeConfig config = checkAndGetConfig(McpToolNodeConfig.class);

            // direct_call=true：跳过LLM，直接调用工具
            if (Boolean.TRUE.equals(config.getDirectCall())) {
                executeDirectCall(config);
                return new NodeProcessResult();
            }

            // 默认：LLM Function Calling模式
            String inputText = getFirstInputText();
            String toolInput = config.getToolInput();

            String prompt;
            if (StringUtils.isNotBlank(toolInput)) {
                String renderedToolInput = WorkflowUtil.renderTemplate(toolInput, state.getInputs());
                if (StringUtils.isNotBlank(inputText)) {
                    prompt = renderedToolInput + "\n\n用户问题: " + inputText;
                } else {
                    prompt = renderedToolInput;
                }
            } else {
                prompt = inputText;
            }

            if (StringUtils.isBlank(prompt)) {
                throw new RuntimeException("MCP工具节点缺少输入参数");
            }

            log.info("MCP工具节点输入: {}", prompt);

            boolean silentMode = config.getShowProcessOutput() != null && !config.getShowProcessOutput();
            WorkflowUtil.streamingInvokeLLM(wfState, state, node, config.getModelName(), prompt, silentMode);

            log.info("MCP工具节点执行完成: {}", node.getTitle());
            return new NodeProcessResult();

        } catch (Exception e) {
            log.error("MCP工具节点执行失败: {}", node.getTitle(), e);
            throw new RuntimeException("MCP工具执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 直接调用模式：跳过LLM，直接调用tool_names[0]指定的工具
     * 参数从direct_params读取，支持${varName}变量引用
     */
    @SuppressWarnings("unchecked")
    private void executeDirectCall(McpToolNodeConfig config) {
        List<String> toolNames = config.getToolNames();
        if (toolNames == null || toolNames.isEmpty()) {
            throw new RuntimeException("direct_call=true时必须在tool_names中指定工具名");
        }

        // direct_call模式下tool_names[0]必须是mcpToolCallbackMap的key格式：ClassName_methodName
        // 例如: "GoodsQueryMcpTools_queryGoodsByName"（注意是下划线，不是点）
        // 兼容写法：如果配置了点分隔格式也自动转换
        String toolName = toolNames.get(0).replace(".", "_");

        Map<String, ToolCallback> mcpToolCallbackMap =
                SpringUtil.getBean("mcpToolCallbackMap", Map.class);

        ToolCallback toolCallback = mcpToolCallbackMap.get(toolName);
        if (toolCallback == null) {
            throw new RuntimeException("未找到工具: " + toolName
                    + "，direct_call模式tool_names格式应为 ClassName_methodName，可用工具: "
                    + mcpToolCallbackMap.keySet());
        }

        // 渲染direct_params中的变量引用，构建工具入参JSON
        Map<String, Object> params = new HashMap<>();
        if (config.getDirectParams() != null) {
            for (Map.Entry<String, Object> entry : config.getDirectParams().entrySet()) {
                Object val = entry.getValue();
                if (val instanceof String) {
                    // 渲染 ${varName} 变量
                    val = WorkflowUtil.renderTemplate((String) val, state.getInputs());
                }
                params.put(entry.getKey(), val);
            }
        }
        String toolInput = JSON.toJSONString(params);

        // 构建ToolContext（tenantCode、staffId、nodeState与LLM模式保持一致）
        Map<String, Object> contextMap = new HashMap<>();
        contextMap.put("tenantCode", wfState.getTenantCode());
        contextMap.put("staffId", wfState.getUserId());
        contextMap.put("nodeState", state);
        if (wfState.getPageContext() != null) {
            contextMap.put("pageContext", wfState.getPageContext());
        }
        ToolContext toolContext = new ToolContext(contextMap);

        log.info("MCP直接调用: 工具={}, 参数={}", toolName, toolInput);
        String result = toolCallback.call(toolInput, toolContext);
        log.info("MCP直接调用完成: 工具={}, 结果长度={}", toolName, result != null ? result.length() : 0);

        state.getOutputs().add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", result != null ? result : ""));
    }
}
