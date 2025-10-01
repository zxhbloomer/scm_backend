package com.xinyirun.scm.clickhouse.entity.ai;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI聊天日志ClickHouse实体
 * 对应ClickHouse表：s_log_ai_chat
 *
 * <p>数据来源：
 * 从MQ消息（SLogAiChatVo）转换而来，存储到ClickHouse用于OLAP分析
 *
 * <p>表结构特点：
 * - 引擎：MergeTree
 * - 分区：按月分区（toYYYYMM(c_time)）
 * - 排序键：(c_time, tenant_code, type)
 * - 索引：conversation_id、request_id、c_id
 *
 * <p>字段映射：
 * - 与SLogAiChatVo字段名完全一致，使用BeanUtilsSupport.copyProperties自动转换
 * - 无需手动映射，所有字段自动拷贝
 *
 * <p>使用场景：
 * 1. Consumer消费MQ消息后，将VO转换为此Entity
 * 2. Repository将Entity插入ClickHouse表
 * 3. 查询时将ClickHouse记录映射为此Entity，再转换为VO返回
 *
 * @author AI Chat Logging System
 * @since 2025-09-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SLogAiChatClickHouseEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     * ClickHouse自动生成UUID（generateUUIDv4()）
     */
    private String id;

    /**
     * 对话ID
     * 关联同一会话的多条日志
     */
    private String conversation_id;

    /**
     * 记录类型
     * USER（用户提问）或ASSISTANT（AI回复）
     * ClickHouse中使用LowCardinality优化
     */
    private String type;

    /**
     * 对话内容
     * 完整的提问或回复内容，不做长度限制
     */
    private String content;

    /**
     * 模型源ID
     * 标识使用的AI模型，可选字段
     */
    private String model_source_id;

    /**
     * AI提供商名称
     * 如OpenAI、Anthropic、DeepSeek等，可选字段
     */
    private String provider_name;

    /**
     * 基础模型名称
     * 如gpt-4、claude-3、deepseek-chat等，可选字段
     */
    private String base_name;

    /**
     * 租户编码
     * 用于多租户数据隔离
     * ClickHouse中使用LowCardinality优化
     */
    private String tenant_code;

    /**
     * 创建人ID
     * 用户ID，可选字段
     */
    private Long c_id;

    /**
     * 创建人名称
     * 用户名称，可选字段
     */
    private String c_name;

    /**
     * 创建时间
     * 日志记录时间，用于分区键
     */
    private LocalDateTime c_time;

    /**
     * 请求标识
     * 关联到具体的请求链路，用于追踪
     */
    private String request_id;
}
