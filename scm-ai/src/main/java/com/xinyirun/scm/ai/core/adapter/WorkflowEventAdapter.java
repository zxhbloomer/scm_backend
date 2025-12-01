package com.xinyirun.scm.ai.core.adapter;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.vo.response.ChatResponseVo;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 工作流事件适配器
 *
 * 负责将 WorkflowEventVo 转换为 ChatResponseVo
 * 从 AiConversationController 迁移的转换逻辑
 *
 * @author SCM-AI
 * @since 2025-11-30
 */
@Slf4j
@Component
public class WorkflowEventAdapter {

    // ==================== 常量定义 ====================

    /** MCP工具调用名称前缀 */
    private static final String MCP_TOOL_CALL_PREFIX = "mcp_tool_call_";

    /** MCP工具调用类型标识 (WfIODataTypeEnum.MCP_TOOL_CALL) */
    private static final int MCP_TOOL_TYPE = 3;

    /** 节点输出事件前缀 */
    private static final String NODE_OUTPUT_PREFIX = "[NODE_OUTPUT_";

    /** 节点输入事件前缀 */
    private static final String NODE_INPUT_PREFIX = "[NODE_INPUT_";

    /** 等待用户反馈事件前缀 */
    private static final String NODE_WAIT_FEEDBACK_PREFIX = "[NODE_WAIT_FEEDBACK_BY_";

    /** 完成事件名称 */
    private static final String DONE_EVENT = "done";

    /**
     * 转换 WorkflowEventVo 为 ChatResponseVo
     *
     * @param event 工作流事件
     * @return ChatResponseVo
     */
    public ChatResponseVo convert(WorkflowEventVo event) {
        ChatResponseVo.ChatResponseVoBuilder builder = ChatResponseVo.builder();

        // NODE_OUTPUT事件特殊处理：提取MCP工具返回值
        String eventName = event.getEvent();
        boolean isNodeOutput = eventName != null && eventName.startsWith(NODE_OUTPUT_PREFIX);
        boolean isNodeInput = eventName != null && eventName.startsWith(NODE_INPUT_PREFIX);

        if (isNodeOutput && event.getData() != null) {
            // 解析NODE_OUTPUT事件,查找MCP工具调用结果
            try {
                JSONObject dataJson = JSONObject.parseObject(event.getData());
                String name = dataJson.getString("name");

                // 检查是否是MCP工具调用结果
                if (name != null && name.startsWith(MCP_TOOL_CALL_PREFIX)) {
                    JSONObject content = dataJson.getJSONObject("content");
                    if (content != null && Integer.valueOf(MCP_TOOL_TYPE).equals(content.getInteger("type"))) {
                        JSONObject value = content.getJSONObject("value");
                        String toolName = value.getString("toolName");

                        log.info("【MCP工具结果】检测到MCP工具调用: toolName={}, value={}", toolName, value);

                        // 将MCP工具结果添加到response中
                        List<Map<String, Object>> mcpResults = new ArrayList<>();
                        Map<String, Object> toolResult = new HashMap<>();
                        toolResult.put("toolName", toolName);
                        toolResult.put("result", value);
                        mcpResults.add(toolResult);
                        builder.mcpToolResults(mcpResults);

                        // NODE_OUTPUT事件不生成文本内容
                        return builder.build();
                    }
                }
            } catch (Exception e) {
                log.warn("解析NODE_OUTPUT事件中的MCP工具结果失败", e);
            }
        }

        // 跳过 NODE_INPUT 和 NODE_OUTPUT 事件的内容提取
        boolean isNodeInputOutput = isNodeInput || isNodeOutput;

        // done事件的content字段包含工作流输出JSON结构
        boolean isDoneEvent = DONE_EVENT.equals(event.getEvent());

        // 设置基础字段（排除NODE_INPUT/OUTPUT和done事件）
        if (event.getData() != null && !isNodeInputOutput && !isDoneEvent) {
            try {
                JSONObject dataJson = JSONObject.parseObject(event.getData());
                String content = dataJson.getString("content");
                if (content != null) {
                    builder.results(List.of(
                        ChatResponseVo.Generation.builder()
                            .output(ChatResponseVo.AssistantMessage.builder()
                                .content(content)
                                .build())
                            .build()
                    ));
                }
            } catch (Exception e) {
                // 如果data不是JSON,直接作为content
                builder.results(List.of(
                    ChatResponseVo.Generation.builder()
                        .output(ChatResponseVo.AssistantMessage.builder()
                            .content(event.getData())
                            .build())
                        .build()
                ));
            }
        }

        ChatResponseVo response = builder.build();

        // 标记特殊事件并提取runtime信息
        if (isDoneEvent) {
            response.setIsComplete(true);
            if (event.getData() != null) {
                try {
                    JSONObject dataJson = JSONObject.parseObject(event.getData());

                    String runtimeUuid = dataJson.getString("runtime_uuid");
                    Long runtimeId = dataJson.getLong("runtime_id");
                    String workflowUuid = dataJson.getString("workflow_uuid");

                    log.info("【AI-Chat-Done事件】提取runtime信息: runtimeUuid={}, runtimeId={}, workflowUuid={}",
                        runtimeUuid, runtimeId, workflowUuid);

                    if (runtimeUuid != null) {
                        response.setRuntimeUuid(runtimeUuid);
                    }
                    if (runtimeId != null) {
                        response.setRuntimeId(runtimeId);
                    }
                    if (workflowUuid != null) {
                        response.setWorkflowUuid(workflowUuid);
                    }
                } catch (Exception e) {
                    log.debug("Failed to extract runtime info from done event", e);
                }
            }
        }

        if (event.getEvent() != null && event.getEvent().startsWith(NODE_WAIT_FEEDBACK_PREFIX)) {
            response.setIsWaitingInput(true);
            response.setRuntimeUuid(extractRuntimeUuid(event));
            response.setWorkflowUuid(extractWorkflowUuid(event));
        }

        return response;
    }

    /**
     * 从事件中提取 runtimeUuid
     *
     * @param event 工作流事件
     * @return runtimeUuid
     */
    public String extractRuntimeUuid(WorkflowEventVo event) {
        if (event.getData() != null) {
            try {
                JSONObject dataJson = JSONObject.parseObject(event.getData());
                return dataJson.getString("runtime_uuid");
            } catch (Exception e) {
                log.warn("Failed to extract runtimeUuid from event", e);
            }
        }
        return null;
    }

    /**
     * 从事件中提取 workflowUuid
     *
     * @param event 工作流事件
     * @return workflowUuid
     */
    public String extractWorkflowUuid(WorkflowEventVo event) {
        if (event.getData() != null) {
            try {
                JSONObject dataJson = JSONObject.parseObject(event.getData());
                return dataJson.getString("workflow_uuid");
            } catch (Exception e) {
                log.warn("Failed to extract workflowUuid from event", e);
            }
        }
        return null;
    }
}
