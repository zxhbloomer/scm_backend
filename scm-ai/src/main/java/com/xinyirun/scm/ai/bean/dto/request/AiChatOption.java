package com.xinyirun.scm.ai.bean.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AI聊天选项配置DTO
 *
 * 聊天配置封装类，用于传递AI引擎所需的各种参数
 * 从MeterSphere的AIChatOption迁移而来，适配scm-ai架构
 *
 * @Author: 迁移适配
 * @Migration: 2025-09-21 (迁移到scm-ai)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "AI聊天选项配置")
public class AiChatOption implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "会话ID（为空时创建新会话）")
    private String conversation_id;

    @Schema(description = "用户提示词", required = true)
    private String prompt;

    @Schema(description = "系统提示词（可选，用于设定AI角色和行为）")
    private String system_prompt;

    @Schema(description = "模型提供商（如：openai、claude、deepseek等）")
    private String model_provider;

    @Schema(description = "模型名称（如：gpt-3.5-turbo、claude-3等）")
    private String model_name;

    @Schema(description = "温度参数（0.0-2.0，控制输出随机性）")
    private Double temperature;

    @Schema(description = "最大token数量")
    private Integer max_tokens;

    @Schema(description = "是否流式响应")
    private Boolean stream;

    @Schema(description = "上下文长度限制")
    private Integer context_limit;

    @Schema(description = "用户ID（用于权限控制）")
    private Long user_id;

    @Schema(description = "会话标题（创建新会话时使用）")
    private String title;

    @Schema(description = "是否启用记忆功能（默认true）")
    private Boolean enable_memory;

    /**
     * 设置提示词的便捷方法
     *
     * 支持链式调用，从MeterSphere的withPrompt方法迁移
     *
     * @param prompt 提示词内容
     * @return 当前对象，支持链式调用
     */
    public AiChatOption withPrompt(String prompt) {
        this.prompt = prompt;
        return this;
    }

    /**
     * 设置系统提示词的便捷方法
     *
     * @param systemPrompt 系统提示词内容
     * @return 当前对象，支持链式调用
     */
    public AiChatOption withSystemPrompt(String systemPrompt) {
        this.system_prompt = systemPrompt;
        return this;
    }

    /**
     * 设置会话ID的便捷方法
     *
     * @param conversationId 会话ID
     * @return 当前对象，支持链式调用
     */
    public AiChatOption withConversationId(String conversationId) {
        this.conversation_id = conversationId;
        return this;
    }

    /**
     * 设置用户ID的便捷方法
     *
     * @param userId 用户ID
     * @return 当前对象，支持链式调用
     */
    public AiChatOption withUserId(Long userId) {
        this.user_id = userId;
        return this;
    }

    /**
     * 获取默认的聊天选项配置
     *
     * @return 默认配置的AiChatOption实例
     */
    public static AiChatOption defaultOption() {
        return AiChatOption.builder()
                .stream(false)
                .enable_memory(true)
                .temperature(0.7)
                .max_tokens(1000)
                .context_limit(4000)
                .build();
    }

    /**
     * 创建快速聊天选项（无记忆）
     *
     * @param prompt 提示词
     * @param userId 用户ID
     * @return 快速聊天配置
     */
    public static AiChatOption quickChat(String prompt, Long userId) {
        return AiChatOption.builder()
                .prompt(prompt)
                .user_id(userId)
                .enable_memory(false)
                .stream(false)
                .temperature(0.7)
                .max_tokens(500)
                .build();
    }

    /**
     * 创建记忆聊天选项
     *
     * @param prompt 提示词
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 记忆聊天配置
     */
    public static AiChatOption memoryChat(String prompt, String conversationId, Long userId) {
        return AiChatOption.builder()
                .prompt(prompt)
                .conversation_id(conversationId)
                .user_id(userId)
                .enable_memory(true)
                .stream(false)
                .temperature(0.7)
                .max_tokens(1500)
                .context_limit(8000)
                .build();
    }
}