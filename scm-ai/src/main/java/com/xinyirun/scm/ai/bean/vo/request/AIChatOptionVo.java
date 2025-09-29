package com.xinyirun.scm.ai.bean.vo.request;

import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI聊天选项业务视图对象
 *
 * 用于AI聊天功能的选项配置传输，包含聊天所需的配置信息
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class AIChatOptionVo implements Serializable {

    @Serial
    private static final long serialVersionUID = -9157522931636771829L;

    /**
     * 对话ID
     */
    @Schema(description = "对话ID")
    private String conversationId;

    /**
     * AI模型源信息
     */
    @Schema(description = "AI模型源信息")
    private AiModelSourceVo module;

    /**
     * 用户输入的提示词
     */
    @Schema(description = "提示词")
    private String prompt;

    /**
     * 系统提示词
     */
    @Schema(description = "系统提示词")
    private String system;

    private String tenantId;

    /**
     * 设置提示词并返回当前对象（链式调用）
     *
     * @param prompt 提示词
     * @return 当前对象
     */
    public AIChatOptionVo withPrompt(@NotBlank String prompt) {
        this.prompt = prompt;
        return this;
    }

}