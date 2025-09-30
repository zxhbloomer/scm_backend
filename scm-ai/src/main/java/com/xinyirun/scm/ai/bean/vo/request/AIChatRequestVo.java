package com.xinyirun.scm.ai.bean.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI聊天请求业务视图对象
 *
 * 用于AI聊天功能的请求参数传输，包含聊天所需的核心信息
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AIChatRequestVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 6559296483506994613L;

    /**
     * 用户输入的提示词
     */
    @Schema(description = "提示词", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "提示词不能为空")
    private String prompt;

    /**
     * AI类型
     */
    @Schema(description = "AI类型 (LLM/VISION/AUDIO)", example = "LLM")
    private String aiType = "LLM";

    /**
     * 对话ID
     */
    @Schema(description = "对话ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "对话ID不能为空")
    private String conversationId;

    /**
     * 组织ID
     */
    @Schema(description = "组织ID")
    private String organizationId;

    private String tenantId;

}