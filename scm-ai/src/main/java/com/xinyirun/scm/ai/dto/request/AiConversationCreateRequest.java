package com.xinyirun.scm.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * AI会话创建请求DTO
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Data
@Schema(description = "AI会话创建请求参数")
public class AiConversationCreateRequest {

    @Schema(description = "会话标题")
    private String title;

    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long user_id;

    @Schema(description = "模型提供商（如：openai、claude、qianfan等）")
    private String model_provider;

    @Schema(description = "模型名称（如：gpt-3.5-turbo、claude-3-sonnet等）")
    private String model_name;

    @Schema(description = "会话描述")
    private String description;

    @Schema(description = "系统提示词")
    private String system_prompt;

    @Schema(description = "温度参数（0.0-2.0，控制输出随机性）")
    private Double temperature;

    @Schema(description = "最大token数量")
    private Integer max_tokens;

    @Schema(description = "会话标签（JSON数组格式）")
    private String tags;

    @Schema(description = "会话配置（JSON格式）")
    private String settings;
}