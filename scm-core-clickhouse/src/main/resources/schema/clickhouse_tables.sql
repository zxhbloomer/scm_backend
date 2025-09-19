-- ClickHouse s_log_sys 系统日志表
DROP TABLE IF EXISTS s_log_sys;

CREATE TABLE s_log_sys (
    id UUID DEFAULT generateUUIDv4() COMMENT '主键ID，自动生成UUID',
    type LowCardinality(String) COMMENT '日志类型：异常NG，正常OK',
    user_name String COMMENT '操作用户账号',
    staff_name String COMMENT '员工姓名',
    operation String COMMENT '操作说明描述',
    time Nullable(UInt64) COMMENT '操作耗时毫秒',
    class_name String COMMENT '调用的类名',
    class_method String COMMENT '调用的方法名',
    http_method LowCardinality(String) COMMENT 'HTTP请求方法GET/POST等',
    params Nullable(String) COMMENT '请求参数JSON格式',
    session String COMMENT 'Session信息JSON格式',
    url String COMMENT '请求URL地址',
    ip String COMMENT '客户端IP地址',
    exception Nullable(String) COMMENT '异常信息详情',
    c_time DateTime COMMENT '创建时间',
    request_id String COMMENT '请求唯一标识ID',
    result Nullable(String) COMMENT '接口返回信息',
    tenant_code LowCardinality(String) COMMENT '租户代码'
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(c_time)
ORDER BY (c_time, tenant_code, type)
SETTINGS index_granularity = 8192
COMMENT '系统操作日志表，记录所有系统操作和异常信息';

-- 创建索引
ALTER TABLE s_log_sys ADD INDEX idx_user_name user_name TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_sys ADD INDEX idx_class_name class_name TYPE ngrambf_v1(3, 256, 2, 0) GRANULARITY 1;
ALTER TABLE s_log_sys ADD INDEX idx_request_id request_id TYPE set(0) GRANULARITY 3;

-- ClickHouse s_log_mq_producer MQ生产者日志表
DROP TABLE IF EXISTS s_log_mq_producer;

CREATE TABLE s_log_mq_producer (
    id UUID DEFAULT generateUUIDv4() COMMENT '主键ID，自动生成UUID',
    type LowCardinality(String) COMMENT '日志类型：异常NG，正常OK',
    message_id String COMMENT '消息体中的MQ消息ID',
    code String COMMENT 'MQ队列编号',
    name String COMMENT 'MQ队列名称',
    exchange String COMMENT 'MQ队列所对应的交换机名称',
    routing_key String COMMENT 'MQ队列所对应的路由键名称',
    mq_data String COMMENT 'MQ消息体内容',
    producer_status UInt8 COMMENT '发送情况：0未发送，1已发送',
    producter_c_time DateTime COMMENT '生产者生成时间',
    producter_exception Nullable(String) COMMENT '异常信息',
    tenant_code LowCardinality(String) COMMENT '租户代码'
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(producter_c_time)
ORDER BY (producter_c_time, tenant_code, type)
SETTINGS index_granularity = 8192
COMMENT 'MQ生产者日志表，记录消息队列发送情况和异常信息';

-- 创建索引
ALTER TABLE s_log_mq_producer ADD INDEX idx_message_id message_id TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_mq_producer ADD INDEX idx_code code TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_mq_producer ADD INDEX idx_exchange exchange TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_mq_producer ADD INDEX idx_routing_key routing_key TYPE set(0) GRANULARITY 3;

-- ClickHouse s_log_mq_consumer MQ消费者日志表
DROP TABLE IF EXISTS s_log_mq_consumer;

CREATE TABLE s_log_mq_consumer (
    id UUID DEFAULT generateUUIDv4() COMMENT '主键ID，自动生成UUID',
    type LowCardinality(String) COMMENT '日志类型：异常NG，正常OK',
    message_id String COMMENT '消息体中的MQ消息ID',
    code String COMMENT 'MQ队列编号',
    name String COMMENT 'MQ队列名称',
    exchange String COMMENT 'MQ队列所对应的交换机名称',
    routing_key String COMMENT 'MQ队列所对应的路由键名称',
    mq_data String COMMENT 'MQ消息体内容',
    consumer_status UInt8 COMMENT '执行情况：0未接受，1已接受',
    consumer_c_time DateTime COMMENT '消费者生成时间',
    consumer_exception Nullable(String) COMMENT '异常信息',
    tenant_code LowCardinality(String) COMMENT '租户代码'
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(consumer_c_time)
ORDER BY (consumer_c_time, tenant_code, type)
SETTINGS index_granularity = 8192
COMMENT 'MQ消费者日志表，记录消息队列消费情况和异常信息';

-- 创建索引
ALTER TABLE s_log_mq_consumer ADD INDEX idx_message_id message_id TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_mq_consumer ADD INDEX idx_code code TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_mq_consumer ADD INDEX idx_exchange exchange TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_mq_consumer ADD INDEX idx_routing_key routing_key TYPE set(0) GRANULARITY 3;