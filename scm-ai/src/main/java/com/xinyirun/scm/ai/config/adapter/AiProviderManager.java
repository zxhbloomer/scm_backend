package com.xinyirun.scm.ai.config.adapter;

import com.xinyirun.scm.ai.common.exception.AiBusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * AI提供商管理器
 * 管理各种AI服务提供商的实现
 *
 * @author zxh
 * @since 2025-09-21
 */
@Slf4j
@Component
public class AiProviderManager {

    /**
     * AI提供商注册表
     */
    private final Map<String, AiEngineAdapter.AiProvider> providers = new ConcurrentHashMap<>();

    /**
     * 注册AI提供商
     *
     * @param providerName 提供商名称
     * @param provider 提供商实现
     */
    public void registerProvider(String providerName, AiEngineAdapter.AiProvider provider) {
        if (providerName == null || provider == null) {
            throw new IllegalArgumentException("提供商名称和实现不能为空");
        }

        providers.put(providerName.toLowerCase(), provider);
        log.info("注册AI提供商成功: {}", providerName);
    }


    /**
     * 获取所有已注册的提供商名称
     *
     * @return 提供商名称集合
     */
    public String[] getAvailableProviders() {
        return providers.keySet().toArray(new String[0]);
    }


    /**
     * 默认AI提供商实现（模拟）
     */
    @Component
    public static class DefaultAiProvider implements AiEngineAdapter.AiProvider {

        @Override
        public AiEngineAdapter.AiResponse sendMessage(AiEngineAdapter.AiRequest request) {
            AiEngineAdapter.AiResponse response = new AiEngineAdapter.AiResponse();

            try {
                // 模拟AI处理
                Thread.sleep(500); // 模拟处理时间

                // 构建回复内容
                String replyContent = buildMockReply(request.getMessage());

                response.setContent(replyContent);
                response.setSuccess(true);
                response.setTokensUsed(calculateTokens(request.getMessage(), replyContent));
                response.setModelInfo(request.getModelProvider() + "/" + request.getModelName());
                response.setRequestId("mock-" + System.currentTimeMillis());

                log.info("默认AI提供商处理消息完成, tokens: {}", response.getTokensUsed());

            } catch (Exception e) {
                response.setSuccess(false);
                response.setErrorMessage("默认AI提供商处理失败: " + e.getMessage());
                log.error("默认AI提供商处理失败", e);
            }

            return response;
        }

        @Override
        public void sendMessageStream(AiEngineAdapter.AiRequest request, AiStreamHandler handler) {
            try {
                // 模拟流式响应
                String content = buildMockReply(request.getMessage());
                String[] words = content.split("\\s+");

                handler.onStart();

                for (String word : words) {
                    Thread.sleep(100); // 模拟流式输出延迟
                    handler.onContent(word + " ");
                }

                AiEngineAdapter.AiResponse finalResponse = new AiEngineAdapter.AiResponse();
                finalResponse.setContent(content);
                finalResponse.setSuccess(true);
                finalResponse.setTokensUsed(calculateTokens(request.getMessage(), content));
                finalResponse.setModelInfo(request.getModelProvider() + "/" + request.getModelName());

                handler.onComplete(finalResponse);

            } catch (Exception e) {
                handler.onError(new AiBusinessException("流式处理失败: " + e.getMessage()));
            }
        }

        @Override
        public boolean supportsStream() {
            return true;
        }

        @Override
        public String getProviderName() {
            return "default";
        }

        /**
         * 构建模拟回复
         */
        private String buildMockReply(String userMessage) {
            if (userMessage.toLowerCase().contains("hello") || userMessage.toLowerCase().contains("你好")) {
                return "你好！我是AI助手，很高兴为您服务。有什么我可以帮助您的吗？";
            } else if (userMessage.toLowerCase().contains("help") || userMessage.toLowerCase().contains("帮助")) {
                return "我可以帮助您回答问题、提供信息、进行对话等。请告诉我您需要什么帮助。";
            } else if (userMessage.toLowerCase().contains("time") || userMessage.toLowerCase().contains("时间")) {
                return "当前时间是 " + java.time.LocalDateTime.now().format(
                        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } else {
                return "感谢您的消息：\"" + userMessage + "\"。这是来自默认AI提供商的模拟回复。" +
                        "在实际应用中，这里会调用真实的AI服务API，如OpenAI、Claude、百度千帆等。";
            }
        }

        /**
         * 计算token使用量（简化实现）
         */
        private Integer calculateTokens(String userMessage, String aiReply) {
            // 简化的token计算：每4个字符约等于1个token
            return (userMessage.length() + aiReply.length()) / 4;
        }
    }

}