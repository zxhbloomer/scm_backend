package com.xinyirun.scm.ai.bean.vo.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作流SSE事件VO
 * 用于Flux流式输出工作流执行事件
 *
 * 事件格式对齐前端workflowService.js的workflowRun()回调：
 * - start: 工作流开始
 * - [NODE_RUN_xxx]: 节点开始执行
 * - [NODE_INPUT_xxx]: 节点输入数据
 * - [NODE_OUTPUT_xxx]: 节点输出数据
 * - done: 工作流完成
 * - error: 执行错误
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
     * SSE事件名称
     * 示例: "start", "[NODE_RUN_xxx]", "[NODE_INPUT_xxx]", "done", "error"
     */
    private String event;

    /**
     * SSE事件数据（JSON字符串）
     */
    private String data;

    /**
     * 创建start事件
     * 前端回调: startCallback(data)
     *
     * @param data 运行时数据（JSON格式）
     * @return start事件
     */
    public static WorkflowEventVo createStartEvent(String data) {
        return WorkflowEventVo.builder()
                .event("start")
                .data(data)
                .build();
    }

    /**
     * 创建NODE_RUN事件
     * 前端回调: messageReceived(data, "[NODE_RUN_xxx]")
     *
     * @param nodeUuid 节点UUID
     * @param data 节点执行数据（JSON格式）
     * @return NODE_RUN事件
     */
    public static WorkflowEventVo createNodeRunEvent(String nodeUuid, String data) {
        return WorkflowEventVo.builder()
                .event("[NODE_RUN_" + nodeUuid + "]")
                .data(data)
                .build();
    }

    /**
     * 创建NODE_INPUT事件
     * 前端回调: messageReceived(data, "[NODE_INPUT_xxx]")
     *
     * @param nodeUuid 节点UUID
     * @param data 节点输入数据（JSON格式）
     * @return NODE_INPUT事件
     */
    public static WorkflowEventVo createNodeInputEvent(String nodeUuid, String data) {
        return WorkflowEventVo.builder()
                .event("[NODE_INPUT_" + nodeUuid + "]")
                .data(data)
                .build();
    }

    /**
     * 创建NODE_OUTPUT事件
     * 前端回调: messageReceived(data, "[NODE_OUTPUT_xxx]")
     *
     * @param nodeUuid 节点UUID
     * @param data 节点输出数据（JSON格式）
     * @return NODE_OUTPUT事件
     */
    public static WorkflowEventVo createNodeOutputEvent(String nodeUuid, String data) {
        return WorkflowEventVo.builder()
                .event("[NODE_OUTPUT_" + nodeUuid + "]")
                .data(data)
                .build();
    }

    /**
     * 创建NODE_CHUNK事件（LLM流式输出）
     * 前端回调: messageReceived(data, "[NODE_CHUNK_xxx]")
     *
     * @param nodeUuid 节点UUID
     * @param chunk 输出块内容
     * @return NODE_CHUNK事件
     */
    public static WorkflowEventVo createNodeChunkEvent(String nodeUuid, String chunk) {
        return WorkflowEventVo.builder()
                .event("[NODE_CHUNK_" + nodeUuid + "]")
                .data(chunk)
                .build();
    }

    /**
     * 创建done事件
     * 前端回调: doneCallback(data)
     *
     * @param data 完成数据（可选，可为空）
     * @return done事件
     */
    public static WorkflowEventVo createDoneEvent(String data) {
        return WorkflowEventVo.builder()
                .event("done")
                .data(data != null ? data : "")
                .build();
    }

    /**
     * 创建done事件（无数据）
     *
     * @return done事件
     */
    public static WorkflowEventVo createDoneEvent() {
        return createDoneEvent(null);
    }

    /**
     * 创建error事件
     * 前端回调: errorCallback(data)
     *
     * @param errorMessage 错误消息
     * @return error事件
     */
    public static WorkflowEventVo createErrorEvent(String errorMessage) {
        return WorkflowEventVo.builder()
                .event("error")
                .data(errorMessage != null ? errorMessage : "工作流执行失败")
                .build();
    }
}
