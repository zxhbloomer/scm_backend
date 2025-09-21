package com.xinyirun.scm.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * AI聊天请求DTO
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Data
@Schema(description = "AI聊天请求参数")
public class AiChatRequest {

    @Schema(description = "会话ID（可为空，自动创建新会话）")
    private Long conversation_id;

    @Schema(description = "消息内容", required = true)
    @NotBlank(message = "消息内容不能为空")
    private String message;

    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long user_id;

    @Schema(description = "模型提供商（如：openai、claude等）")
    private String model_provider;

    @Schema(description = "模型名称（如：gpt-3.5-turbo、claude-3等）")
    private String model_name;

    @Schema(description = "会话标题（仅在创建新会话时使用）")
    private String title;

    @Schema(description = "系统提示词")
    private String system_prompt;

    @Schema(description = "温度参数（0.0-2.0，控制输出随机性）")
    private Double temperature;

    @Schema(description = "最大token数量")
    private Integer max_tokens;

    @Schema(description = "是否流式响应")
    private Boolean stream;

    @Schema(description = "上下文长度限制")
    private Integer context_limit;
}