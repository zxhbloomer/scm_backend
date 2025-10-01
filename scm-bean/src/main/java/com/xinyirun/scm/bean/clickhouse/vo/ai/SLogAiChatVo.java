package com.xinyirun.scm.bean.clickhouse.vo.ai;

import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI聊天日志VO
 * 用于MQ消息传输，从MySQL ai_conversation_content表同步数据到ClickHouse s_log_ai_chat表
 *
 * <p>数据流向：
 * MySQL (ai_conversation_content) → MQ (SLogAiChatVo) → ClickHouse (s_log_ai_chat)
 *
 * <p>字段说明：
 * - 基础字段：直接从MySQL表拷贝（conversation_id, type, content等）
 * - 应用层字段：在发送MQ前设置（tenant_code, c_name, request_id）
 *
 * <p>使用场景：
 * 1. Producer发送MQ消息时构建此VO
 * 2. Consumer消费MQ消息时接收此VO
 * 3. 转换为ClickHouseEntity后插入ClickHouse
 *
 * @author AI Chat Logging System
 * @since 2025-09-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class SLogAiChatVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * 注意：ClickHouse会自动生成UUID，此字段通常为null
     */
    private String id;

    /**
     * 对话ID（必填）
     * 关联同一会话的多条日志，支持查询完整对话历史
     * 从MySQL ai_conversation_content.conversation_id拷贝
     */
    private String conversation_id;

    /**
     * 记录类型（必填）
     * 值域：USER（用户提问）或ASSISTANT（AI回复）
     * 从MySQL ai_conversation_content.type拷贝
     */
    private String type;

    /**
     * 对话内容（必填）
     * 完整的提问或回复内容，不做长度限制
     * 从MySQL ai_conversation_content.content拷贝
     */
    private String content;

    /**
     * 模型源ID（可选）
     * 标识使用的AI模型
     * 从MySQL ai_conversation_content.model_source_id拷贝
     */
    private String model_source_id;

    /**
     * AI提供商名称（可选）
     * 如：OpenAI、Anthropic、DeepSeek等
     * 从MySQL ai_conversation_content.provider_name拷贝
     */
    private String provider_name;

    /**
     * 基础模型名称（可选）
     * 如：gpt-4、claude-3、deepseek-chat等
     * 从MySQL ai_conversation_content.base_name拷贝
     */
    private String base_name;

    /**
     * 租户编码（必填）
     * 用于多租户数据隔离
     * 应用层设置：从DataSourceHelper.getCurrentDataSourceName()获取
     */
    private String tenant_code;

    /**
     * 创建人ID（可选）
     * 用户ID
     * 从MySQL ai_conversation_content.c_id拷贝
     */
    private Long c_id;

    /**
     * 创建人名称（可选）
     * 用户名称
     * 应用层设置：从SecurityContext或UserContext获取
     */
    private String c_name;

    /**
     * 创建时间（必填）
     * 日志记录时间
     * 从MySQL ai_conversation_content.c_time拷贝
     */
    private LocalDateTime c_time;

    /**
     * 请求标识（必填）
     * 关联到具体的请求链路，用于追踪
     * 应用层设置：从RequestContext或request attributes获取
     */
    private String request_id;

    // ========== 查询条件字段（用于Repository查询） ==========

    /**
     * 查询开始时间（查询条件）
     * 用于时间范围查询
     */
    private LocalDateTime startTime;

    /**
     * 查询结束时间（查询条件）
     * 用于时间范围查询
     */
    private LocalDateTime endTime;

    /**
     * 分页条件（查询条件）
     * 用于分页查询
     */
    private PageCondition pageCondition;

    public PageCondition getPageCondition() {
        if (pageCondition == null) {
            pageCondition = new PageCondition();
        }
        return pageCondition;
    }
}