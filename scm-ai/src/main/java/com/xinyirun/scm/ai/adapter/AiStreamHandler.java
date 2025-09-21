package com.xinyirun.scm.ai.adapter;

/**
 * AI流式响应处理器接口
 * 用于处理AI提供商的流式响应
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
public interface AiStreamHandler {

    /**
     * 流式响应开始
     */
    void onStart();

    /**
     * 接收到内容片段
     *
     * @param content 内容片段
     */
    void onContent(String content);

    /**
     * 流式响应完成
     *
     * @param finalResponse 最终响应结果
     */
    void onComplete(AiEngineAdapter.AiResponse finalResponse);

    /**
     * 发生错误
     *
     * @param error 错误信息
     */
    void onError(Throwable error);

    /**
     * 默认流式处理器实现
     */
    class DefaultStreamHandler implements AiStreamHandler {

        private final StringBuilder contentBuilder = new StringBuilder();
        private volatile boolean completed = false;
        private volatile Throwable error = null;

        @Override
        public void onStart() {
            contentBuilder.setLength(0);
            completed = false;
            error = null;
        }

        @Override
        public void onContent(String content) {
            if (content != null) {
                contentBuilder.append(content);
            }
        }

        @Override
        public void onComplete(AiEngineAdapter.AiResponse finalResponse) {
            completed = true;
        }

        @Override
        public void onError(Throwable error) {
            this.error = error;
            completed = true;
        }

        /**
         * 获取完整内容
         */
        public String getCompleteContent() {
            return contentBuilder.toString();
        }

        /**
         * 是否已完成
         */
        public boolean isCompleted() {
            return completed;
        }

        /**
         * 是否有错误
         */
        public boolean hasError() {
            return error != null;
        }

        /**
         * 获取错误信息
         */
        public Throwable getError() {
            return error;
        }
    }

    /**
     * WebSocket流式处理器实现
     */
    class WebSocketStreamHandler implements AiStreamHandler {

        private final String sessionId;
        private final StringBuilder contentBuilder = new StringBuilder();

        public WebSocketStreamHandler(String sessionId) {
            this.sessionId = sessionId;
        }

        @Override
        public void onStart() {
            contentBuilder.setLength(0);
            // 发送开始事件到WebSocket客户端
            sendToWebSocket("start", "");
        }

        @Override
        public void onContent(String content) {
            if (content != null) {
                contentBuilder.append(content);
                // 发送内容片段到WebSocket客户端
                sendToWebSocket("content", content);
            }
        }

        @Override
        public void onComplete(AiEngineAdapter.AiResponse finalResponse) {
            // 发送完成事件到WebSocket客户端
            sendToWebSocket("complete", finalResponse.getContent());
        }

        @Override
        public void onError(Throwable error) {
            // 发送错误事件到WebSocket客户端
            sendToWebSocket("error", error.getMessage());
        }

        /**
         * 发送消息到WebSocket客户端
         */
        private void sendToWebSocket(String type, String content) {
            // TODO: 实现WebSocket消息发送逻辑
            // 这里应该通过Spring WebSocket的SimpMessagingTemplate发送消息
            // simpMessagingTemplate.convertAndSendToUser(sessionId, "/queue/ai-stream",
            //     Map.of("type", type, "content", content));
        }

        /**
         * 获取会话ID
         */
        public String getSessionId() {
            return sessionId;
        }

        /**
         * 获取完整内容
         */
        public String getCompleteContent() {
            return contentBuilder.toString();
        }
    }

    /**
     * 回调流式处理器实现
     */
    class CallbackStreamHandler implements AiStreamHandler {

        public interface StreamCallback {
            void onStreamStart();
            void onStreamContent(String content);
            void onStreamComplete(AiEngineAdapter.AiResponse response);
            void onStreamError(Throwable error);
        }

        private final StreamCallback callback;
        private final StringBuilder contentBuilder = new StringBuilder();

        public CallbackStreamHandler(StreamCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onStart() {
            contentBuilder.setLength(0);
            if (callback != null) {
                callback.onStreamStart();
            }
        }

        @Override
        public void onContent(String content) {
            if (content != null) {
                contentBuilder.append(content);
                if (callback != null) {
                    callback.onStreamContent(content);
                }
            }
        }

        @Override
        public void onComplete(AiEngineAdapter.AiResponse finalResponse) {
            if (callback != null) {
                callback.onStreamComplete(finalResponse);
            }
        }

        @Override
        public void onError(Throwable error) {
            if (callback != null) {
                callback.onStreamError(error);
            }
        }

        /**
         * 获取完整内容
         */
        public String getCompleteContent() {
            return contentBuilder.toString();
        }
    }
}