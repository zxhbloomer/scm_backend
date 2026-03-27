package com.xinyirun.scm.ai.core.adapter;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.vo.response.ChatResponseVo;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 工作流事件适配器
 * 对齐Spring AI Alibaba：通过data.type区分消息类型
 *
 * @author SCM-AI
 * @since 2025-11-30
 */
@Slf4j
@Component
public class WorkflowEventAdapter {

    /**
     * 转换 WorkflowEventVo 为 ChatResponseVo
     * 对齐Spring AI Alibaba：通过data.type区分消息类型
     *
     * @param event 工作流事件
     * @return ChatResponseVo
     */
    public ChatResponseVo convert(WorkflowEventVo event) {
        ChatResponseVo.ChatResponseVoBuilder builder = ChatResponseVo.builder();

        if (event.getData() == null) {
            return builder.build();
        }

        try {
            JSONObject dataJson = JSONObject.parseObject(event.getData());
            String type = dataJson.getString("type");

            switch (type != null ? type : "") {
                case "runtime":
                    // 工作流启动：提取runtime信息
                    String runtimeUuid = dataJson.getString("runtimeUuid");
                    Long runtimeId = dataJson.getLong("runtimeId");
                    String workflowUuid = dataJson.getString("workflowUuid");
                    String workflowTitle = dataJson.getString("workflowTitle");

                    ChatResponseVo runtimeResponse = builder.build();
                    runtimeResponse.setRuntimeUuid(runtimeUuid);
                    runtimeResponse.setRuntimeId(runtimeId);
                    runtimeResponse.setWorkflowUuid(workflowUuid);
                    runtimeResponse.setWorkflowTitle(workflowTitle);
                    runtimeResponse.setNodeEventType("runtime");
                    return runtimeResponse;

                case "chunk":
                    // LLM流式输出
                    String chunk = dataJson.getString("chunk");
                    builder.results(List.of(
                        ChatResponseVo.Generation.builder()
                            .output(ChatResponseVo.AssistantMessage.builder()
                                .content(chunk != null ? chunk : "")
                                .build())
                            .build()
                    ));
                    break;

                case "output":
                    // 节点完整输出：只渲染 End 节点的 output 变量值（其他节点的 output 是中间数据，不显示给用户）
                    String outputNodeName = dataJson.getString("nodeName");
                    if ("End".equals(outputNodeName)) {
                        JSONObject outputData = dataJson.getJSONObject("data");
                        if (outputData != null) {
                            for (String key : outputData.keySet()) {
                                Object outputItem = outputData.get(key);
                                if (outputItem instanceof JSONObject) {
                                    JSONObject itemJson = (JSONObject) outputItem;
                                    if ("output".equals(itemJson.getString("name"))) {
                                        JSONObject content = itemJson.getJSONObject("content");
                                        if (content != null && content.containsKey("value")) {
                                            builder.results(List.of(
                                                ChatResponseVo.Generation.builder()
                                                    .output(ChatResponseVo.AssistantMessage.builder()
                                                        .content(content.getString("value"))
                                                        .build())
                                                    .build()
                                            ));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    break;

                case "interrupt":
                    // 人机交互中断：解析node、tip、交互类型和交互请求
                    ChatResponseVo interruptResponse = builder.build();
                    interruptResponse.setIsWaitingInput(true);
                    interruptResponse.setRuntimeUuid(dataJson.getString("runtimeUuid"));
                    interruptResponse.setWorkflowUuid(dataJson.getString("workflowUuid"));
                    String interruptNode = dataJson.getString("node");
                    String interruptTip = dataJson.getString("tip");
                    // 传递tip文本作为消息内容
                    if (interruptTip != null && !interruptTip.isEmpty()) {
                        interruptResponse.setResults(List.of(
                            ChatResponseVo.Generation.builder()
                                .output(ChatResponseVo.AssistantMessage.builder()
                                    .content(interruptTip)
                                    .build())
                                .build()
                        ));
                    }
                    // 传递interaction_request给前端（触发交互组件渲染）
                    JSONObject interactionReq = dataJson.getJSONObject("interaction_request");
                    if (interactionReq != null) {
                        interruptResponse.setInteraction_request(interactionReq.toJSONString());
                    }
                    log.debug("人机交互中断: node={}, tip={}, interactionType={}",
                        interruptNode, interruptTip, dataJson.getString("interactionType"));
                    return interruptResponse;

                case "node_running": {
                    // 节点开始执行事件
                    ChatResponseVo nodeRunningResp = ChatResponseVo.createContentChunk("");
                    nodeRunningResp.setNodeEventType("node_running");
                    nodeRunningResp.setNodeUuid(dataJson.getString("node"));
                    nodeRunningResp.setNodeName(dataJson.getString("nodeName"));
                    nodeRunningResp.setNodeTitle(dataJson.getString("nodeTitle"));
                    nodeRunningResp.setNodeTimestamp(dataJson.getLong("timestamp"));
                    return nodeRunningResp;
                }

                case "node_complete": {
                    // 节点执行完成事件
                    ChatResponseVo nodeCompleteResp = builder.build();
                    nodeCompleteResp.setNodeEventType("node_complete");
                    nodeCompleteResp.setNodeUuid(dataJson.getString("node"));
                    nodeCompleteResp.setNodeName(dataJson.getString("nodeName"));
                    nodeCompleteResp.setNodeTitle(dataJson.getString("nodeTitle"));
                    nodeCompleteResp.setNodeDuration(dataJson.getLong("duration"));
                    nodeCompleteResp.setNodeTimestamp(dataJson.getLong("timestamp"));
                    Object summaryObj = dataJson.get("summary");
                    if (summaryObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> summaryMap = (Map<String, Object>) summaryObj;
                        nodeCompleteResp.setNodeSummary(summaryMap);
                    }
                    return nodeCompleteResp;
                }

                case "workflow_output_data": {
                    // 工作流完成输出：包含open_page_command和interaction_request
                    ChatResponseVo outputResp = builder.build();
                    outputResp.setIsComplete(true);
                    String openPageCmd = dataJson.getString("open_page_command");
                    if (openPageCmd != null) {
                        outputResp.setOpen_page_command(openPageCmd);
                    }
                    String interactionReqStr = dataJson.getString("interaction_request");
                    if (interactionReqStr != null) {
                        outputResp.setInteraction_request(interactionReqStr);
                        outputResp.setIsComplete(false); // 有人机交互时不算真正完成
                        outputResp.setIsWaitingInput(true);
                    }
                    // 传递runtimeUuid和runtimeId，用于保存AI消息时关联思考步骤
                    String outputRuntimeUuid = dataJson.getString("runtimeUuid");
                    if (outputRuntimeUuid != null) {
                        outputResp.setRuntimeUuid(outputRuntimeUuid);
                    }
                    Long outputRuntimeId = dataJson.getLong("runtimeId");
                    if (outputRuntimeId != null) {
                        outputResp.setRuntimeId(outputRuntimeId);
                    }
                    return outputResp;
                }

                default:
                    // 未知类型：尝试提取content
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

        return builder.build();
    }
}
