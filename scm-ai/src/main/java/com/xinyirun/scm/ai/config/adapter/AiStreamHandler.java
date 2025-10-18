package com.xinyirun.scm.ai.config.adapter;

import com.xinyirun.scm.ai.common.util.CommonBeanFactory;
import lombok.extern.slf4j.Slf4j;
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
    }
}