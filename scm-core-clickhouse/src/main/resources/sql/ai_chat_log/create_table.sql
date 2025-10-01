-- ============================================================
-- AI聊天日志表 DDL
-- ============================================================
-- 表名: s_log_ai_chat
-- 用途: 存储AI聊天对话的完整日志信息，用于审计、分析和问题排查
-- 引擎: MergeTree（ClickHouse高性能OLAP引擎）
-- 分区: 按月分区（toYYYYMM(c_time)），便于历史数据管理
-- 排序: (c_time, tenant_code, type)，支持按时间+租户+类型的高效查询
-- ============================================================

CREATE TABLE IF NOT EXISTS default.s_log_ai_chat (
    -- ========== 主键ID ==========
    -- ClickHouse自动生成UUID，无需手动设置
    id UUID DEFAULT generateUUIDv4() COMMENT '主键ID，自动生成UUID',

    -- ========== 对话标识 ==========
    -- 关联同一会话的多条日志，支持查询完整对话历史
    conversation_id String COMMENT '对话ID，关联同一会话的多条日志',

    -- ========== 记录类型 ==========
    -- USER（用户提问）或ASSISTANT（AI回复）
    -- 使用LowCardinality优化存储和查询（因为只有两个值）
    type LowCardinality(String) COMMENT '记录类型：USER（用户提问）或ASSISTANT（AI回复）',

    -- ========== 对话内容 ==========
    -- 完整的提问或回复内容，不做长度限制
    content String COMMENT '对话内容，完整的提问或回复',

    -- ========== 模型相关信息（可选） ==========
    -- 记录使用的AI模型信息，用于成本分析和性能评估
    model_source_id Nullable(String) COMMENT '模型源ID，标识使用的AI模型',
    provider_name Nullable(String) COMMENT 'AI提供商名称，如OpenAI、Anthropic、DeepSeek',
    base_name Nullable(String) COMMENT '基础模型名称，如gpt-4、claude-3、deepseek-chat',

    -- ========== 多租户数据隔离字段 ==========
    -- 使用LowCardinality优化（因为租户数量有限）
    tenant_code LowCardinality(String) COMMENT '租户编码，用于多租户数据隔离',

    -- ========== 创建人信息（可选） ==========
    -- 记录是哪个用户发起的对话
    c_id Nullable(UInt64) COMMENT '创建人ID，用户ID',
    c_name Nullable(String) COMMENT '创建人名称，用户名称',

    -- ========== 创建时间 ==========
    -- 日志记录时间，用于分区键和排序键
    c_time DateTime COMMENT '创建时间，日志记录时间',

    -- ========== 请求标识 ==========
    -- 用于链路追踪，关联到具体的请求链路
    request_id String COMMENT '请求标识，关联到具体的请求链路',

    -- ========== 索引定义 ==========
    -- 三个关键字段建立索引，支持常见查询场景
    INDEX idx_conversation_id conversation_id TYPE set(0) GRANULARITY 3 COMMENT '对话ID索引，支持按对话查询',
    INDEX idx_request_id request_id TYPE set(0) GRANULARITY 3 COMMENT '请求ID索引，支持链路追踪',
    INDEX idx_c_id c_id TYPE set(0) GRANULARITY 3 COMMENT '用户ID索引，支持按用户查询'

-- ========== 表引擎和分区策略 ==========
) ENGINE = MergeTree
PARTITION BY toYYYYMM(c_time)  -- 按月分区，每月一个分区，便于历史数据管理和查询优化
ORDER BY (c_time, tenant_code, type)  -- 排序键，支持按时间+租户+类型的高效查询
SETTINGS index_granularity = 8192  -- 索引粒度，默认值，每8192行创建一个索引标记
COMMENT '存储AI聊天对话的完整日志信息，用于审计、分析和问题排查';

-- ============================================================
-- 使用说明
-- ============================================================
-- 1. 数据插入：通过RabbitMQ消费者异步插入，不阻塞主业务流程
-- 2. 数据查询：
--    - 按对话ID查询：SELECT * FROM s_log_ai_chat WHERE conversation_id = 'xxx' ORDER BY c_time
--    - 按用户查询：SELECT * FROM s_log_ai_chat WHERE tenant_code = 'xxx' AND c_id = 123 ORDER BY c_time DESC
--    - 按时间范围查询：SELECT * FROM s_log_ai_chat WHERE tenant_code = 'xxx' AND c_time BETWEEN '2025-09-01' AND '2025-09-30'
-- 3. 数据归档：手动删除历史分区，例如：ALTER TABLE s_log_ai_chat DROP PARTITION '202501'
-- 4. 性能优化：
--    - 查询时始终包含tenant_code过滤条件（利用排序键）
--    - 尽量使用时间范围过滤（利用分区和排序键）
--    - 批量插入优于单条插入
-- ============================================================