package com.xinyirun.scm.ai.workflow;

/**
 * 工作流流式处理器 - 回调接口
 * 用于WorkflowEngine向Flux发送事件
 *
 * 参考: AiStreamHandler.CallbackStreamHandler
 * 对齐: 前端 workflowService.js 的 workflowRun() 回调
 *
 * @author SCM-AI团队
 * @since 2025-10-29
 */
public class WorkflowStreamHandler {

    /**
     * 工作流流式回调接口
     */
    public interface StreamCallback {

        /**
         * 工作流开始执行
         * 前端回调: startCallback(data)
         *
         * @param runtimeData 运行时数据（JSON格式）
         */
        void onStart(String runtimeData);

        /**
         * 节点开始执行
         * 前端回调: messageReceived(data, "[NODE_RUN_xxx]")
         *
         * @param nodeUuid 节点UUID
         * @param nodeData 节点数据（JSON格式）
         */
        void onNodeRun(String nodeUuid, String nodeData);

        /**
         * 节点输入数据
         * 前端回调: messageReceived(data, "[NODE_INPUT_xxx]")
         *
         * @param nodeUuid 节点UUID
         * @param inputData 输入数据（JSON格式）
         */
        void onNodeInput(String nodeUuid, String inputData);

        /**
         * 节点输出数据
         * 前端回调: messageReceived(data, "[NODE_OUTPUT_xxx]")
         *
         * @param nodeUuid 节点UUID
         * @param outputData 输出数据（JSON格式）
         */
        void onNodeOutput(String nodeUuid, String outputData);

        /**
         * 节点流式输出块（LLM节点）
         * 前端回调: messageReceived(chunk, "[NODE_CHUNK_xxx]")
         *
         * @param nodeUuid 节点UUID
         * @param chunk 输出块内容
         */
        void onNodeChunk(String nodeUuid, String chunk);

        /**
         * 节点等待用户反馈（人机交互）
         * 前端回调: messageReceived(tip, "[NODE_WAIT_FEEDBACK_BY_xxx]")
         * 参考: aideepin WorkflowEngine.java Line 136
         *
         * @param nodeUuid 节点UUID
         * @param tip 提示信息
         */
        void onNodeWaitFeedback(String nodeUuid, String tip);

        /**
         * 工作流执行完成
         * 前端回调: doneCallback(data)
         *
         * @param data 完成数据（可选）
         */
        void onComplete(String data);

        /**
         * 工作流执行完成（无数据）
         */
        default void onComplete() {
            onComplete(null);
        }

        /**
         * 工作流执行失败
         * 前端回调: errorCallback(error)
         *
         * @param error 错误对象
         */
        void onError(Throwable error);
    }

    private final StreamCallback callback;

    public WorkflowStreamHandler(StreamCallback callback) {
        this.callback = callback;
    }

    /**
     * 发送start事件
     */
    public void sendStart(String runtimeData) {
        callback.onStart(runtimeData);
    }

    /**
     * 发送NODE_RUN事件
     */
    public void sendNodeRun(String nodeUuid, String nodeData) {
        callback.onNodeRun(nodeUuid, nodeData);
    }

    /**
     * 发送NODE_INPUT事件
     */
    public void sendNodeInput(String nodeUuid, String inputData) {
        callback.onNodeInput(nodeUuid, inputData);
    }

    /**
     * 发送NODE_OUTPUT事件
     */
    public void sendNodeOutput(String nodeUuid, String outputData) {
        callback.onNodeOutput(nodeUuid, outputData);
    }

    /**
     * 发送NODE_CHUNK事件
     */
    public void sendNodeChunk(String nodeUuid, String chunk) {
        callback.onNodeChunk(nodeUuid, chunk);
    }

    /**
     * 发送NODE_WAIT_FEEDBACK_BY事件（人机交互）
     * 参考: aideepin WorkflowEngine.java Line 136
     */
    public void sendNodeWaitFeedback(String nodeUuid, String tip) {
        callback.onNodeWaitFeedback(nodeUuid, tip);
    }

    /**
     * 发送done事件
     */
    public void sendComplete(String data) {
        callback.onComplete(data);
    }

    /**
     * 发送done事件（无数据）
     */
    public void sendComplete() {
        callback.onComplete();
    }

    /**
     * 发送error事件
     */
    public void sendError(Throwable error) {
        callback.onError(error);
    }
}
