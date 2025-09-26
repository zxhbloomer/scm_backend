package com.xinyirun.scm.ai.adapter;

import com.xinyirun.scm.ai.common.util.CommonBeanFactory;
import com.xinyirun.scm.ai.common.util.LogUtils;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * AI流式响应处理器接口
 * 用于处理AI提供商的流式响应
 *
 * @author zxh
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
        private SimpMessagingTemplate simpMessagingTemplate;

        public WebSocketStreamHandler(String sessionId) {
            this.sessionId = sessionId;
            // 通过Spring上下文获取SimpMessagingTemplate
            try {
                this.simpMessagingTemplate = CommonBeanFactory.getBean(SimpMessagingTemplate.class);
            } catch (Exception e) {
                // 如果获取失败，记录日志但不抛出异常
                LogUtils.error("Failed to get SimpMessagingTemplate: " + e.getMessage());
            }
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
            if (simpMessagingTemplate != null) {
                try {
                    Map<String, Object> message = new HashMap<>();
                    message.put("type", type);
                    message.put("content", content);
                    message.put("timestamp", System.currentTimeMillis());

                    // 发送到用户特定的队列
                    simpMessagingTemplate.convertAndSendToUser(
                        sessionId,
                        "/queue/ai-stream",
                        message
                    );
                } catch (Exception e) {
                    LogUtils.error("Failed to send WebSocket message: " + e.getMessage());
                }
            }
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