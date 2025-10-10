package com.xinyirun.scm.ai.bean.vo.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 知识库问答记录请求VO
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "知识库问答记录请求")
public class QARecordRequestVo {

    /**
     * 用户问题
     */
    @NotBlank(message = "问题内容不能为空")
    @Schema(description = "用户问题", required = true, example = "什么是供应链管理？")
    private String question;

    /**
     * AI模型ID（可选，不指定则使用知识库默认模型）
     */
    @Schema(description = "AI模型ID", required = false)
    private String aiModelId;
}
