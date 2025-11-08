package com.xinyirun.scm.ai.common.constant;

/**
 * AI消息类型常量
 *
 * 遵循Spring AI官方标准，所有消息类型使用小写值
 * 对应 org.springframework.ai.chat.messages.MessageType 枚举的value值
 *
 * @see org.springframework.ai.chat.messages.MessageType
 * @author SCM-AI开发团队
 * @since 2025-01-08
 */
public class AiMessageTypeConstant {

    /**
     * 用户消息类型
     * 对应Spring AI的MessageType.USER
     */
    public static final String MESSAGE_TYPE_USER = "user";

    /**
     * AI助手消息类型
     * 对应Spring AI的MessageType.ASSISTANT
     */
    public static final String MESSAGE_TYPE_ASSISTANT = "assistant";

    /**
     * 系统消息类型
     * 对应Spring AI的MessageType.SYSTEM
     */
    public static final String MESSAGE_TYPE_SYSTEM = "system";

    /**
     * 工具消息类型
     * 对应Spring AI的MessageType.TOOL
     */
    public static final String MESSAGE_TYPE_TOOL = "tool";

    private AiMessageTypeConstant() {
        // 工具类，禁止实例化
    }
}
