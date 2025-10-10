package com.xinyirun.scm.ai.bean.vo.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 模型选项信息VO
 *
 * <p>用于下拉选择器的数据传输，包含模型的基本信息和显示所需的字段</p>
 *
 * @author SCM AI Team
 * @since 2025-01-08
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "模型选项信息VO")
public class ModelOptionVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模型ID
     */
    @Schema(description = "模型ID")
    private String modelId;

    /**
     * 模型名称（技术标识，如gpt-4-turbo）
     */
    @Schema(description = "模型名称（技术标识）")
    private String modelName;

    /**
     * 模型标题（用户友好显示，如GPT-4 Turbo）
     */
    @Schema(description = "模型标题（用户友好显示）")
    private String modelTitle;

    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean enable;

    /**
     * 模型平台/供应商
     */
    @Schema(description = "模型平台/供应商")
    private String modelPlatform;
}
