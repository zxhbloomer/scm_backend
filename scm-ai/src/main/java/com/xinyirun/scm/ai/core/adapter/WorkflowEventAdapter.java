package com.xinyirun.scm.ai.core.adapter;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.vo.response.ChatResponseVo;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowEventVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

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

                    ChatResponseVo runtimeResponse = builder.build();
                    runtimeResponse.setRuntimeUuid(runtimeUuid);
                    runtimeResponse.setRuntimeId(runtimeId);
                    runtimeResponse.setWorkflowUuid(workflowUuid);
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
                    // 节点完整输出：提取output变量的值
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
                    break;

                case "interrupt":
                    // 人机交互中断：解析node和tip字段
                    ChatResponseVo interruptResponse = builder.build();
                    interruptResponse.setIsWaitingInput(true);
                    // interrupt消息中包含node（中断节点UUID）和tip（提示信息）
                    String interruptNode = dataJson.getString("node");
                    String interruptTip = dataJson.getString("tip");
                    log.debug("人机交互中断: node={}, tip={}", interruptNode, interruptTip);
                    return interruptResponse;

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
