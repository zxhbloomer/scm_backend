package com.xinyirun.scm.ai.bean.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI对话更新请求业务视图对象
 *
 * 用于更新AI对话信息的请求参数传输
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AIConversationUpdateRequestVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 对话ID
     */
    @Schema(description = "对话ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "对话ID不能为空")
    private String id;

    /**
     * 对话标题
     */
    @Schema(description = "对话标题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "对话标题不能为空")
    private String title;
}