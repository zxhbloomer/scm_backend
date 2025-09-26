package com.xinyirun.scm.ai.bean.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AIChatRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 6559296483506994613L;

    @Schema(description = "提示词", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String prompt;

    @Schema(description = "模型ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String chatModelId;

    @Schema(description = "对话ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank
    private String conversationId;

    @Schema(description = "组织ID")
    private String organizationId;

    @Schema(description = "租户ID")
    private String tenantId;
}