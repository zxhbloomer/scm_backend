package com.xinyirun.scm.ai.bean.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * AI模型信息业务视图对象
 *
 * 用于返回AI模型选择和配置信息
 *
 * @author SCM-AI重构团队
 * @since 2025-09-30
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class AiModelInfoVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 模型源ID
     */
    @Schema(description = "模型源ID")
    private Long id;

    /**
     * AI提供商名称
     */
    @Schema(description = "AI提供商名称", example = "OpenAI")
    private String provider_name;

    /**
     * 基础模型名称
     */
    @Schema(description = "基础模型名称", example = "gpt-3.5-turbo")
    private String base_name;

    /**
     * AI类型
     */
    @Schema(description = "AI类型", example = "LLM")
    private String type;

    /**
     * 启用状态
     */
    @Schema(description = "启用状态")
    private Boolean status;

    /**
     * 是否默认模型
     */
    @Schema(description = "是否默认模型")
    private Boolean is_default;

    /**
     * 模型显示名称
     */
    @Schema(description = "模型显示名称")
    private String display_name;

    /**
     * 模型描述
     */
    @Schema(description = "模型描述")
    private String description;

    /**
     * 最大Token限制
     */
    @Schema(description = "最大Token限制")
    private Integer max_tokens;

    /**
     * 支持的功能列表
     */
    @Schema(description = "支持的功能列表")
    private String capabilities;
}