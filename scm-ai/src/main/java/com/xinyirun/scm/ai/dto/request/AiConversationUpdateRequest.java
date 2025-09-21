package com.xinyirun.scm.ai.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * AI会话更新请求DTO
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Data
@Schema(description = "AI会话更新请求参数")
public class AiConversationUpdateRequest {

    @Schema(description = "会话ID", required = true)
    @NotNull(message = "会话ID不能为空")
    private Long conversation_id;

    @Schema(description = "用户ID", required = true)
    @NotNull(message = "用户ID不能为空")
    private Long user_id;

    @Schema(description = "会话标题")
    private String title;

    @Schema(description = "会话状态（1-活跃，2-归档，3-暂停）")
    private Integer status;

    @Schema(description = "会话描述")
    private String description;

    @Schema(description = "模型提供商")
    private String model_provider;

    @Schema(description = "模型名称")
    private String model_name;

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

    @Schema(description = "数据版本，乐观锁使用")
    private Integer dbversion;
}