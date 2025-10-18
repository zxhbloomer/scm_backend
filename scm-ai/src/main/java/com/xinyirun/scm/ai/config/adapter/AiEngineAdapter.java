package com.xinyirun.scm.ai.config.adapter;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.common.constant.AICommonConstants;
import com.xinyirun.scm.ai.common.exception.AiBusinessException;
import com.xinyirun.scm.ai.config.AiConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * AI引擎适配器
 * 统一不同AI提供商的接口调用
 *
 * @author zxh
 * @since 2025-09-21
 */
@Slf4j
@Component
public class AiEngineAdapter {

    @Autowired
    private AiConfiguration aiConfiguration;

    @Autowired
    private AiProviderManager providerManager;


    /**
     * AI请求参数
     */
    @Data
    public static class AiRequest {
        /**
         * 消息内容
         */
        private String message;

        /**
         * 模型提供商
         */
        private String modelProvider;

        /**
         * 模型名称
         */
        private String modelName;

        /**
         * 温度参数
         */
        private Double temperature;

        /**
         * 最大token数
         */
        private Integer maxTokens;

        /**
         * 系统提示词
         */
        private String systemPrompt;

        /**
         * 会话历史上下文
         */
        private String conversationHistory;

        /**
         * 是否流式响应
         */
        private Boolean stream = false;

        /**
         * 用户ID（用于日志记录）
         */
        private Long userId;

        /**
         * 会话ID（用于日志记录）
         */
        private String conversationId;
    }

    /**
     * AI响应结果
     */
    @Data
    public static class AiResponse {
        /**
         * AI回复内容
         */
        private String content;

        /**
         * 是否成功
         */
        private boolean success;

        /**
         * 错误信息
         */
        private String errorMessage;

        /**
         * 使用的token数量
         */
        private Integer tokensUsed;

        /**
         * Spring AI Usage信息
         */
        private Usage usage;

        /**
         * 输入Token数量
         */
        private Long promptTokens;

        /**
         * 输出Token数量
         */
        private Long completionTokens;

        /**
         * 总Token数量
         */
        private Long totalTokens;

        /**
         * 响应时间（毫秒）
         */
        private Long responseTime;

        /**
         * 完整响应数据（JSON格式）
         */
        private String fullResponse;

        /**
         * 模型信息
         */
        private String modelInfo;

        /**
         * AI提供商名称
         */
        private String modelProvider;

        /**
         * 模型名称
         */
        private String modelName;

        /**
         * 请求ID（用于跟踪）
         */
        private String requestId;

        /**
         * 从Spring AI Usage对象设置Token信息
         */
        public void setUsageFromSpringAi(Usage usage) {
            if (usage != null) {
                this.usage = usage;
                this.promptTokens = usage.getPromptTokens() != null ? usage.getPromptTokens().longValue() : 0L;
                this.completionTokens = usage.getCompletionTokens() != null ? usage.getCompletionTokens().longValue() : 0L;
                this.totalTokens = usage.getTotalTokens() != null ? usage.getTotalTokens().longValue() : 0L;
                this.tokensUsed = this.totalTokens != null ? this.totalTokens.intValue() : 0;
            }
        }

        /**
         * 获取Token使用信息的便利方法
         */
        public Long getPromptTokens() {
            if (promptTokens != null) return promptTokens;
            if (usage != null && usage.getPromptTokens() != null) {
                return usage.getPromptTokens().longValue();
            }
            return 0L;
        }

        public Long getCompletionTokens() {
            if (completionTokens != null) return completionTokens;
            if (usage != null && usage.getCompletionTokens() != null) {
                return usage.getCompletionTokens().longValue();
            }
            return 0L;
        }

        public Long getTotalTokens() {
            if (totalTokens != null) return totalTokens;
            if (usage != null && usage.getTotalTokens() != null) {
                return usage.getTotalTokens().longValue();
            }
            return 0L;
        }
    }

    /**
     * AI提供商接口
     */
    public interface AiProvider {
        /**
         * 发送消息
         */
        AiResponse sendMessage(AiRequest request);

        /**
         * 发送流式消息
         */
        void sendMessageStream(AiRequest request, AiStreamHandler handler);

        /**
         * 是否支持流式响应
         */
        boolean supportsStream();

        /**
         * 获取提供商名称
         */
        String getProviderName();
    }
}