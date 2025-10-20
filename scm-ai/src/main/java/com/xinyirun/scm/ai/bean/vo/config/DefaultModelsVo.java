package com.xinyirun.scm.ai.bean.vo.config;

import lombok.Data;

/**
 * 默认模型配置VO类
 */
@Data
public class DefaultModelsVo {
    /**
     * 默认语言模型ID
     */
    private Long defaultLlm;

    /**
     * 默认视觉模型ID
     */
    private Long defaultVision;

    /**
     * 默认嵌入模型ID
     */
    private Long defaultEmbedding;
}
