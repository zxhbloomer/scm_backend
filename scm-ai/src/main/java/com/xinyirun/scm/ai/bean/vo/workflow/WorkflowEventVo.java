package com.xinyirun.scm.ai.bean.vo.workflow;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 工作流SSE数据VO
 * 对齐Spring AI Alibaba：无event名，纯data传输，通过type字段区分消息类型
 *
 * 消息类型：
 * - type=runtime: 工作流运行时初始化数据
 * - type=chunk: LLM流式输出块
 * - type=output: 节点完整输出
 * - type=interrupt: 人机交互中断
 *
 * @author SCM-AI团队
 * @since 2025-10-29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEventVo {

    /**
     * SSE数据（JSON字符串）
     * 包含type字段用于区分消息类型
     */
    private String data;

    /**
     * 创建Runtime初始化数据
     * 工作流启动时发送的首条消息
     *
     * @param runtimeUuid 运行时UUID
     * @param runtimeId 运行时ID
     * @param workflowUuid 工作流UUID
     * @param conversationId 对话ID
     * @return 运行时初始化事件
     */
    public static WorkflowEventVo createRuntimeData(String runtimeUuid, Long runtimeId,
                                                     String workflowUuid, String conversationId) {
        JSONObject json = new JSONObject();
        json.put("type", "runtime");
        json.put("runtimeUuid", runtimeUuid);
        json.put("runtimeId", runtimeId);
        json.put("workflowUuid", workflowUuid);
        json.put("conversationId", conversationId);
        return WorkflowEventVo.builder().data(json.toJSONString()).build();
    }

    /**
     * 创建流式块数据
     * LLM流式输出时的文本块
     *
     * @param nodeUuid 节点UUID
     * @param chunk 流式文本块
     * @return 流式块事件
     */
    public static WorkflowEventVo createChunkData(String nodeUuid, String chunk) {
        JSONObject json = new JSONObject();
        json.put("type", "chunk");
        json.put("node", nodeUuid);
        json.put("chunk", chunk);
        return WorkflowEventVo.builder().data(json.toJSONString()).build();
    }

    /**
     * 创建节点输出数据
     * 节点执行完成时的完整输出
     *
     * @param nodeUuid 节点UUID
     * @param outputs 节点输出Map
     * @return 节点输出事件
     */
    public static WorkflowEventVo createNodeOutputData(String nodeUuid, Map<String, Object> outputs) {
        JSONObject json = new JSONObject();
        json.put("type", "output");
        json.put("node", nodeUuid);
        json.put("data", outputs);
        return WorkflowEventVo.builder().data(json.toJSONString()).build();
    }

    /**
     * 创建人机交互数据
     * 工作流暂停等待用户输入
     *
     * @param nodeUuid 节点UUID
     * @param tip 提示信息
     * @return 人机交互事件
     */
    public static WorkflowEventVo createInterruptData(String nodeUuid, String tip) {
        JSONObject json = new JSONObject();
        json.put("type", "interrupt");
        json.put("node", nodeUuid);
        json.put("tip", tip != null ? tip : "请输入您的反馈");
        return WorkflowEventVo.builder().data(json.toJSONString()).build();
    }
}
