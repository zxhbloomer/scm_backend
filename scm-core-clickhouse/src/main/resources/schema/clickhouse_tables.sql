-- ClickHouse 数据表创建脚本
-- SCM系统 - 数据变更日志表

-- 创建数据库
CREATE DATABASE IF NOT EXISTS scm_clickhouse;

-- 使用数据库
USE scm_clickhouse;

-- 数据变更日志表
CREATE TABLE IF NOT EXISTS data_change_log (
    log_id String COMMENT '日志ID',
    tenant_id String COMMENT '租户ID',
    table_name String COMMENT '表名',
    operation_type Enum8('INSERT' = 1, 'UPDATE' = 2, 'DELETE' = 3) COMMENT '操作类型',
    record_id String COMMENT '记录ID',
    change_time DateTime64(3) COMMENT '变更时间',
    user_id UInt64 COMMENT '用户ID',
    user_name String COMMENT '用户名',
    request_id String COMMENT '请求ID',
    order_code String COMMENT '订单编号',
    before_data String COMMENT '变更前数据JSON',
    after_data String COMMENT '变更后数据JSON',
    changed_fields String COMMENT '变更字段列表JSON',
    ip_address String COMMENT 'IP地址',
    user_agent String COMMENT '用户代理',
    remark String COMMENT '备注信息',
    create_time DateTime64(3) DEFAULT now64() COMMENT '创建时间'
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(change_time)
ORDER BY (tenant_id, table_name, change_time)
TTL change_time + INTERVAL 365 DAY
SETTINGS index_granularity = 8192
COMMENT '数据变更日志表';

-- 创建索引
-- 索引1：按租户和时间查询
ALTER TABLE data_change_log ADD INDEX idx_tenant_time (tenant_id, change_time) TYPE minmax GRANULARITY 1;

-- 索引2：按表名和时间查询
ALTER TABLE data_change_log ADD INDEX idx_table_time (table_name, change_time) TYPE minmax GRANULARITY 1;

-- 索引3：按用户和时间查询
ALTER TABLE data_change_log ADD INDEX idx_user_time (user_id, change_time) TYPE minmax GRANULARITY 1;

-- 索引4：按订单编号查询
ALTER TABLE data_change_log ADD INDEX idx_order_code (order_code) TYPE bloom_filter(0.01) GRANULARITY 1;

-- 索引5：按请求ID查询
ALTER TABLE data_change_log ADD INDEX idx_request_id (request_id) TYPE bloom_filter(0.01) GRANULARITY 1;

-- 创建物化视图：按表统计变更次数
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_change_stats_by_table
ENGINE = SummingMergeTree()
PARTITION BY toYYYYMM(change_date)
ORDER BY (tenant_id, table_name, change_date, operation_type)
AS SELECT
    tenant_id,
    table_name,
    toDate(change_time) as change_date,
    operation_type,
    count() as change_count,
    uniq(user_id) as unique_users
FROM data_change_log
GROUP BY tenant_id, table_name, change_date, operation_type;

-- 创建物化视图：按用户统计变更次数
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_change_stats_by_user
ENGINE = SummingMergeTree()
PARTITION BY toYYYYMM(change_date)
ORDER BY (tenant_id, user_id, change_date, operation_type)
AS SELECT
    tenant_id,
    user_id,
    user_name,
    toDate(change_time) as change_date,
    operation_type,
    count() as change_count,
    uniq(table_name) as affected_tables
FROM data_change_log
GROUP BY tenant_id, user_id, user_name, change_date, operation_type;

-- 创建物化视图：每小时变更趋势
CREATE MATERIALIZED VIEW IF NOT EXISTS mv_change_trends_hourly
ENGINE = SummingMergeTree()
PARTITION BY toYYYYMM(change_date)
ORDER BY (tenant_id, change_date, change_hour)
AS SELECT
    tenant_id,
    toDate(change_time) as change_date,
    toHour(change_time) as change_hour,
    count() as change_count,
    uniq(user_id) as unique_users,
    uniq(table_name) as affected_tables,
    uniq(operation_type) as operation_types
FROM data_change_log
GROUP BY tenant_id, change_date, change_hour;

-- 业务指标统计表（可选扩展）
CREATE TABLE IF NOT EXISTS business_metrics (
    metric_id String COMMENT '指标ID',
    tenant_id String COMMENT '租户ID',
    metric_name String COMMENT '指标名称',
    metric_value Float64 COMMENT '指标值',
    metric_unit String COMMENT '指标单位',
    dimension_1 String COMMENT '维度1',
    dimension_2 String COMMENT '维度2',
    dimension_3 String COMMENT '维度3',
    metric_time DateTime64(3) COMMENT '指标时间',
    create_time DateTime64(3) DEFAULT now64() COMMENT '创建时间'
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(metric_time)
ORDER BY (tenant_id, metric_name, metric_time)
TTL metric_time + INTERVAL 730 DAY
SETTINGS index_granularity = 8192
COMMENT '业务指标统计表';

-- 性能监控表（可选扩展）
CREATE TABLE IF NOT EXISTS performance_metrics (
    trace_id String COMMENT '追踪ID',
    tenant_id String COMMENT '租户ID',
    service_name String COMMENT '服务名称',
    method_name String COMMENT '方法名称',
    execution_time_ms UInt32 COMMENT '执行时间（毫秒）',
    status_code String COMMENT '状态码',
    error_message String COMMENT '错误信息',
    start_time DateTime64(3) COMMENT '开始时间',
    end_time DateTime64(3) COMMENT '结束时间',
    create_time DateTime64(3) DEFAULT now64() COMMENT '创建时间'
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(start_time)
ORDER BY (tenant_id, service_name, start_time)
TTL start_time + INTERVAL 90 DAY
SETTINGS index_granularity = 8192
COMMENT '性能监控表';

-- 创建测试数据插入示例（开发环境使用）
-- INSERT INTO data_change_log (
--     log_id, tenant_id, table_name, operation_type, record_id,
--     change_time, user_id, user_name, request_id, order_code,
--     before_data, after_data, changed_fields, ip_address, user_agent, remark
-- ) VALUES (
--     generateUUIDv4(),
--     'tenant_001',
--     'm_goods',
--     'UPDATE',
--     '12345',
--     now(),
--     1001,
--     '张三',
--     'req_001',
--     'SO202501170001',
--     '{"goods_name":"原商品名称","price":100.00}',
--     '{"goods_name":"新商品名称","price":120.00}',
--     '["goods_name","price"]',
--     '192.168.1.100',
--     'Mozilla/5.0',
--     '测试数据变更'
-- );

-- 查询示例
-- 1. 查询最近24小时的变更统计
-- SELECT table_name, operation_type, count(*) as cnt
-- FROM data_change_log
-- WHERE change_time >= now() - INTERVAL 1 DAY
-- GROUP BY table_name, operation_type
-- ORDER BY cnt DESC;

-- 2. 查询特定订单的所有变更记录
-- SELECT table_name, operation_type, change_time, user_name, changed_fields
-- FROM data_change_log
-- WHERE order_code = 'SO202501170001'
-- ORDER BY change_time;

-- 3. 查询用户活跃度统计
-- SELECT user_name, count(*) as change_count, uniq(table_name) as affected_tables
-- FROM data_change_log
-- WHERE change_time >= today() - INTERVAL 7 DAY
-- GROUP BY user_name
-- ORDER BY change_count DESC
-- LIMIT 10;