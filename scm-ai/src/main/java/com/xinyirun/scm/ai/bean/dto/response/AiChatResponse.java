package com.xinyirun.scm.ai.bean.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI聊天响应DTO
 *
 * @author zxh
 * @since 2025-09-21
 */
@Data
@Schema(description = "AI聊天响应结果")
public class AiChatResponse {

    @Schema(description = "消息ID")
    private Long message_id;

    @Schema(description = "会话ID")
    private Long conversation_id;

    @Schema(description = "消息类型（1-用户消息，2-AI回复）")
    private Integer message_type;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "完整消息内容（JSON格式）")
    private String message_content;

    @Schema(description = "使用的token数量")
    private Integer token_used;

    @Schema(description = "响应时间（毫秒）")
    private Long response_time;

    @Schema(description = "模型提供商")
    private String model_provider;

    @Schema(description = "模型名称")
    private String model_name;

    @Schema(description = "是否成功")
    private Boolean success;

    @Schema(description = "错误信息（如果有）")
    private String error_message;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime create_time;

    @Schema(description = "会话标题")
    private String conversation_title;

    @Schema(description = "是否为新会话")
    private Boolean is_new_conversation;

    @Schema(description = "推荐的后续问题（JSON数组格式）")
    private String suggested_questions;

    @Schema(description = "请求ID（用于跟踪）")
    private String request_id;

    @Schema(description = "耗时统计信息")
    private TimingInfo timing;

    @Data
    @Schema(description = "耗时统计信息")
    public static class TimingInfo {
        @Schema(description = "总耗时（毫秒）")
        private Long total_time;

        @Schema(description = "AI处理耗时（毫秒）")
        private Long ai_process_time;

        @Schema(description = "数据库操作耗时（毫秒）")
        private Long db_operation_time;

        @Schema(description = "网络请求耗时（毫秒）")
        private Long network_time;
    }
}