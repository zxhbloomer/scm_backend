package com.xinyirun.scm.ai.constants;

/**
 * AI模块公共常量
 *
 * @author SCM-AI重构团队
 * @since 2025-09-30
 */
public class AICommonConstants {

    /**
     * 消息类型常量 - 与Spring AI框架保持一致
     */
    public static final String MESSAGE_TYPE_USER = "user";
    public static final String MESSAGE_TYPE_ASSISTANT = "assistant";
    public static final String MESSAGE_TYPE_SYSTEM = "system";
    public static final String MESSAGE_TYPE_TOOL = "tool";

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
