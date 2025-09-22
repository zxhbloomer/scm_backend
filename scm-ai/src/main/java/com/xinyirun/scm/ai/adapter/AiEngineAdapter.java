package com.xinyirun.scm.ai.adapter;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.domain.AiConversationContent;
import com.xinyirun.scm.ai.common.constant.AiConstant;
import com.xinyirun.scm.ai.common.exception.AiBusinessException;
import com.xinyirun.scm.ai.config.AiConfiguration;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * AI引擎适配器
 * 统一不同AI提供商的接口调用
 *
 * @author AI Assistant
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
     * 发送消息给AI并获取回复
     *
     * @param request AI请求参数
     * @return AI响应结果
     */
    public AiResponse sendMessage(AiRequest request) {
        validateRequest(request);

        try {
            // 获取AI提供商
            AiProvider provider = providerManager.getProvider(request.getModelProvider());
            if (provider == null) {
                throw new AiBusinessException("不支持的AI提供商: " + request.getModelProvider());
            }

            // 记录开始时间
            long startTime = System.currentTimeMillis();

            // 调用AI服务
            AiResponse response = provider.sendMessage(request);

            // 计算响应时间
            long responseTime = System.currentTimeMillis() - startTime;
            response.setResponseTime(responseTime);

            // 记录调用日志
            logAiCall(request, response);

            return response;

        } catch (Exception e) {
            log.error("AI引擎调用失败, provider: {}, model: {}, error: {}",
                    request.getModelProvider(), request.getModelName(), e.getMessage(), e);
            throw new AiBusinessException("AI引擎调用失败: " + e.getMessage());
        }
    }

    /**
     * 发送消息给AI并获取流式回复
     *
     * @param request AI请求参数
     * @param handler 流式响应处理器
     */
    public void sendMessageStream(AiRequest request, AiStreamHandler handler) {
        validateRequest(request);

        try {
            // 获取AI提供商
            AiProvider provider = providerManager.getProvider(request.getModelProvider());
            if (provider == null) {
                throw new AiBusinessException("不支持的AI提供商: " + request.getModelProvider());
            }

            // 检查是否支持流式响应
            if (!provider.supportsStream()) {
                throw new AiBusinessException("该AI提供商不支持流式响应: " + request.getModelProvider());
            }

            // 调用流式AI服务
            provider.sendMessageStream(request, handler);

        } catch (Exception e) {
            log.error("AI流式调用失败, provider: {}, model: {}, error: {}",
                    request.getModelProvider(), request.getModelName(), e.getMessage(), e);
            handler.onError(new AiBusinessException("AI流式调用失败: " + e.getMessage()));
        }
    }

    /**
     * 构建AI请求参数
     *
     * @param message 用户消息
     * @param conversationHistory 会话历史
     * @param modelProvider 模型提供商
     * @param modelName 模型名称
     * @return AI请求参数
     */
    public AiRequest buildRequest(String message, List<AiConversationContent> conversationHistory,
                                  String modelProvider, String modelName) {
        AiRequest request = new AiRequest();

        // 设置基本参数
        request.setMessage(message);
        request.setModelProvider(StringUtils.hasText(modelProvider) ? modelProvider : aiConfiguration.getDefaultModelProvider());
        request.setModelName(StringUtils.hasText(modelName) ? modelName : aiConfiguration.getDefaultModelName());
        request.setTemperature(aiConfiguration.getDefaultTemperature());
        request.setMaxTokens(aiConfiguration.getDefaultMaxTokens());

        // 构建会话上下文
        if (conversationHistory != null && !conversationHistory.isEmpty()) {
            request.setConversationHistory(buildConversationContext(conversationHistory));
        }

        return request;
    }

    /**
     * 验证请求参数
     */
    private void validateRequest(AiRequest request) {
        if (request == null) {
            throw new AiBusinessException("AI请求参数不能为空");
        }

        if (!StringUtils.hasText(request.getMessage())) {
            throw new AiBusinessException("消息内容不能为空");
        }

        if (!StringUtils.hasText(request.getModelProvider())) {
            throw new AiBusinessException("模型提供商不能为空");
        }

        if (!StringUtils.hasText(request.getModelName())) {
            throw new AiBusinessException("模型名称不能为空");
        }

        // 检查消息长度限制
        if (request.getMessage().length() > aiConfiguration.getSecurity().getMaxMessageLength()) {
            throw new AiBusinessException("消息长度超过限制: " + aiConfiguration.getSecurity().getMaxMessageLength());
        }
    }

    /**
     * 构建会话上下文
     */
    private String buildConversationContext(List<AiConversationContent> conversationHistory) {
        StringBuilder context = new StringBuilder();

        // 限制上下文长度，只取最近的消息
        int maxContextLength = aiConfiguration.getMaxContextLength();
        int currentLength = 0;

        for (int i = conversationHistory.size() - 1; i >= 0; i--) {
            AiConversationContent content = conversationHistory.get(i);
            String messageText = content.getContent();

            if (currentLength + messageText.length() > maxContextLength) {
                break;
            }

            String role = AiConstant.MESSAGE_TYPE_USER.equals(content.getType()) ? "user" : "assistant";
            context.insert(0, String.format("[%s]: %s\n", role, messageText));
            currentLength += messageText.length();
        }

        return context.toString();
    }

    /**
     * 记录AI调用日志
     */
    private void logAiCall(AiRequest request, AiResponse response) {
        if (!aiConfiguration.getEnableAccessLog()) {
            return;
        }

        JSONObject logData = new JSONObject();
        logData.put("provider", request.getModelProvider());
        logData.put("model", request.getModelName());
        logData.put("messageLength", request.getMessage().length());
        logData.put("responseTime", response.getResponseTime());
        logData.put("tokensUsed", response.getTokensUsed());
        logData.put("success", response.isSuccess());

        if (!response.isSuccess()) {
            logData.put("errorMessage", response.getErrorMessage());
        }

        log.info("AI调用日志: {}", logData.toJSONString());
    }

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
        private Long conversationId;
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
         * 请求ID（用于跟踪）
         */
        private String requestId;
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