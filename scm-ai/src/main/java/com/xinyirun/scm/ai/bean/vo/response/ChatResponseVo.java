package com.xinyirun.scm.ai.bean.vo.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Spring AI标准ChatResponse响应格式
 * 用于流式AI聊天输出，遵循Spring AI标准
 *
 * @Author: SCM-AI
 * @CreateTime: 2025-01-01 00:00
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseVo {

    /**
     * 生成结果列表，通常包含一个结果
     */
    private List<Generation> results;

    /**
     * 响应元数据信息
     */
    private ChatResponseMetadata metadata;

    /**
     * 错误标识(顶层字段,方便前端快速判断)
     * 用于严格模式下的知识库错误拦截
     *
     * <p>
     * true: 错误响应(问题过长、检索结果为空等)
     * false/null: 正常响应
     *
     * @since 2025-10-16 严格模式优化
     */
    @Builder.Default
    private Boolean isError = false;

    /**
     * 生成结果内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Generation {
        /**
         * 输出内容
         */
        private AssistantMessage output;

        /**
         * 生成元数据
         */
        private GenerationMetadata metadata;
    }

    /**
     * 助手消息内容
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssistantMessage {
        /**
         * 消息内容
         */
        private String content;

        /**
         * 消息类型，固定为ASSISTANT
         */
        @Builder.Default
        private String messageType = "ASSISTANT";
    }

    /**
     * 生成元数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GenerationMetadata {
        /**
         * 结束原因
         */
        private String finishReason;
    }

    /**
     * 聊天响应元数据
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatResponseMetadata {
        /**
         * 模型信息
         */
        private String model;

        /**
         * 使用统计信息
         */
        private Usage usage;
    }

    /**
     * 使用统计信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Usage {
        /**
         * 输入token数量
         */
        private Integer promptTokens;

        /**
         * 输出token数量
         */
        private Integer generationTokens;

        /**
         * 总token数量
         */
        private Integer totalTokens;
    }

    /**
     * 创建内容块（用于流式输出）
     */
    public static ChatResponseVo createContentChunk(String content) {
        return ChatResponseVo.builder()
            .results(List.of(
                Generation.builder()
                    .output(AssistantMessage.builder()
                        .content(content)
                        .build())
                    .build()
            ))
            .build();
    }

    /**
     * 创建完成响应（用于流式结束）
     */
    public static ChatResponseVo createCompleteResponse(String fullContent, String model) {
        return ChatResponseVo.builder()
            .results(List.of(
                Generation.builder()
                    .output(AssistantMessage.builder()
                        .content(fullContent)
                        .build())
                    .metadata(GenerationMetadata.builder()
                        .finishReason("stop")
                        .build())
                    .build()
            ))
            .metadata(ChatResponseMetadata.builder()
                .model(model)
                .build())
            .build();
    }

    /**
     * 创建错误响应(用于严格模式拦截)
     *
     * <p>使用方式：</p>
     * <pre>
     * if (maxResults == 0) {
     *     if (Boolean.TRUE.equals(knowledgeBase.getIsStrict())) {
     *         fluxSink.next(createErrorResponse("提问内容过长..."));
     *     }
     * }
     * </pre>
     *
     * @param errorMessage 错误消息内容
     * @return 错误响应对象
     * @since 2025-10-16 严格模式优化
     */
    public static ChatResponseVo createErrorResponse(String errorMessage) {
        return ChatResponseVo.builder()
            .isError(true)  // ✅ 顶层错误标识
            .results(List.of(
                Generation.builder()
                    .output(AssistantMessage.builder()
                        .content(errorMessage)
                        .build())
                    .metadata(GenerationMetadata.builder()
                        .finishReason("error")  // ✅ 保留原有标识(双重保险)
                        .build())
                    .build()
            ))
            .build();
    }
}