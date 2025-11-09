package com.xinyirun.scm.ai.common.constant;

/**
 * AI模块公共常量
 *
 * 包含AI类型相关常量。
 * 消息类型常量请使用 {@link AiMessageTypeConstant}
 *
 * @author SCM-AI重构团队
 * @since 2025-09-30
 */
public class AICommonConstants {

    /**
     * AI类型常量
     */
    public static final String AI_TYPE_LLM = "LLM";
    public static final String AI_TYPE_VISION = "VISION";
    public static final String AI_TYPE_AUDIO = "AUDIO";

    /**
     * 默认值常量
     */
    public static final String DEFAULT_AI_TYPE = AI_TYPE_LLM;

    /**
     * 私有构造函数防止实例化
     */
    private AICommonConstants() {
        throw new IllegalStateException("Constants class");
    }
}
