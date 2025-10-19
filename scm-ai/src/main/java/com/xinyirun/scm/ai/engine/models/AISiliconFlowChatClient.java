package com.xinyirun.scm.ai.engine.models;

import com.xinyirun.scm.ai.engine.annotations.AIRegister;
import com.xinyirun.scm.ai.engine.common.AIChatClient;
import com.xinyirun.scm.ai.engine.common.AIChatOptions;
import com.xinyirun.scm.ai.engine.common.AIModelType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Component;

/**
 * 硅基流动（SiliconFlow）Chat Client 适配器
 *
 * <p>硅基流动兼容 OpenAI API 格式，支持多种开源大模型：</p>
 * <ul>
 *   <li>视觉大模型：THUDM/GLM-4.1V-9B-Thinking（支持图像理解）</li>
 *   <li>推理大模型：deepseek-ai/DeepSeek-R1（深度推理能力）</li>
 *   <li>LLM大模型：Qwen系列、ChatGLM系列等</li>
 * </ul>
 *
 * <p>通过 OpenAI API 兼容层实现，无需额外适配代码</p>
 *
 * @author SCM AI Team
 * @since 2025-10-19
 */
@AIRegister(AIModelType.SILICON_FLOW)
@Component
public class AISiliconFlowChatClient extends AIChatClient {

    /**
     * 创建 OpenAiApi 实例（指向硅基流动 API）
     *
     * @param options 包含 API Key、Base URL 和其他设置的选项
     * @return 配置好的 OpenAiApi 实例
     */
    private OpenAiApi createOpenAiApi(AIChatOptions options) {
        return OpenAiApi.builder()
                .apiKey(options.getApiKey())
                .baseUrl(options.getBaseUrl())  // https://api.siliconflow.cn/v1
                .build();
    }

    /**
     * 创建 OpenAiChatModel 实例
     *
     * @param options   包含模型类型等选项的对象
     * @param openAiApi 创建好的 OpenAiApi 实例
     * @return 配置好的 OpenAiChatModel 实例
     */
    private OpenAiChatModel createChatModel(AIChatOptions options, OpenAiApi openAiApi) {
        return OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model(options.getModelType())  // deepseek-ai/DeepSeek-R1 或 THUDM/GLM-4.1V-9B-Thinking
                        .build())
                .build();
    }

    /**
     * 使用提供的选项创建一个 ChatClient 实例
     *
     * @param options 包含 API Key、模型类型及其他设置的选项
     * @return 配置好的 ChatClient 实例
     */
    @Override
    public ChatClient chatClient(AIChatOptions options) {
        // 创建 OpenAiApi 和 ChatModel 实例
        OpenAiApi openAiApi = createOpenAiApi(options);
        OpenAiChatModel chatModel = createChatModel(options, openAiApi);

        // 构建 ChatClient.Builder 实例
        ChatClient.Builder builder = ChatClient.builder(chatModel);

        // 添加默认顾问
        this.addAdvisor(options, builder);

        // 将构建好的选项应用到 ChatClient.Builder 中
        builder.defaultOptions(this.builderChatOptions(options).build());

        return builder.build();
    }

    /**
     * 创建一个 ChatModel 实例，用于与硅基流动 API 进行通信
     *
     * @param options 包含 API Key、Base URL、模型类型等选项的对象
     * @return 配置好的 ChatModel 实例
     */
    @Override
    public ChatModel chatModel(AIChatOptions options) {
        // 创建并返回 ChatModel 实例
        return createChatModel(options, createOpenAiApi(options));
    }
}
