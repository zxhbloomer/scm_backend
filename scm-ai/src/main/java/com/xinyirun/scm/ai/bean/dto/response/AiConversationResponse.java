package com.xinyirun.scm.ai.bean.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI会话响应DTO
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Data
@Schema(description = "AI会话响应结果")
public class AiConversationResponse {

    @Schema(description = "会话ID")
    private Long conversation_id;

    @Schema(description = "会话标题")
    private String title;

    @Schema(description = "用户ID")
    private Long user_id;

    @Schema(description = "会话状态（1-活跃，2-归档，3-暂停）")
    private Integer status;

    @Schema(description = "会话状态描述")
    private String status_desc;

    @Schema(description = "模型提供商")
    private String model_provider;

    @Schema(description = "模型名称")
    private String model_name;

    @Schema(description = "会话描述")
    private String description;

    @Schema(description = "系统提示词")
    private String system_prompt;

    @Schema(description = "会话标签")
    private List<String> tags;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime create_time;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime update_time;

    @Schema(description = "最后消息时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime last_message_time;

    @Schema(description = "最后消息内容（预览）")
    private String last_message_preview;

    @Schema(description = "消息总数")
    private Long message_count;

    @Schema(description = "总token使用量")
    private Integer total_tokens;

    @Schema(description = "平均响应时间（毫秒）")
    private Long average_response_time;

    @Schema(description = "会话统计信息")
    private ConversationStats statistics;

    @Schema(description = "会话消息列表（可选）")
    private List<AiChatResponse> messages;

    @Schema(description = "数据版本，乐观锁使用")
    private Integer dbversion;

    @Data
    @Schema(description = "会话统计信息")
    public static class ConversationStats {
        @Schema(description = "用户消息数量")
        private Long user_message_count;

        @Schema(description = "AI回复数量")
        private Long ai_message_count;

        @Schema(description = "总token消耗")
        private Integer total_token_usage;

        @Schema(description = "平均每次对话token消耗")
        private Integer average_token_per_message;

        @Schema(description = "会话时长（分钟）")
        private Long conversation_duration_minutes;

        @Schema(description = "最快响应时间（毫秒）")
        private Long fastest_response_time;

        @Schema(description = "最慢响应时间（毫秒）")
        private Long slowest_response_time;
    }
}