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

-- ClickHouse s_log_data_change_main 数据变更主日志表
DROP TABLE IF EXISTS s_log_data_change_main;

CREATE TABLE s_log_data_change_main (
    id UUID DEFAULT generateUUIDv4() COMMENT '主键ID，自动生成UUID',
    order_type LowCardinality(String) COMMENT '单号类型',
    order_code String COMMENT '单号',
    name Nullable(String) COMMENT '名称',
    c_time DateTime COMMENT '创建时间',
    u_time Nullable(DateTime) COMMENT '最后更新时间',
    u_name Nullable(String) COMMENT '更新人名称',
    u_id Nullable(String) COMMENT '更新人ID',
    request_id String COMMENT '请求ID',
    tenant_code LowCardinality(String) COMMENT '租户代码'
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(c_time)
ORDER BY (c_time, tenant_code, order_code)
SETTINGS index_granularity = 8192
COMMENT '数据变更主日志表，记录数据变更的主要信息和单号关联';

-- 创建索引
ALTER TABLE s_log_data_change_main ADD INDEX idx_order_code order_code TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_data_change_main ADD INDEX idx_request_id request_id TYPE set(0) GRANULARITY 3;

-- ClickHouse s_log_data_change_operate 数据变更操作日志表
DROP TABLE IF EXISTS s_log_data_change_operate;

CREATE TABLE s_log_data_change_operate (
    id UUID DEFAULT generateUUIDv4() COMMENT '主键ID，自动生成UUID',
    type LowCardinality(String) COMMENT '日志类型：异常NG，正常OK',
    user_name String COMMENT '操作用户账号',
    staff_name String COMMENT '员工姓名',
    staff_id Nullable(String) COMMENT '员工ID',
    operation Nullable(String) COMMENT '操作说明描述',
    time Nullable(UInt64) COMMENT '操作耗时毫秒',
    class_name Nullable(String) COMMENT '调用的类名',
    class_method Nullable(String) COMMENT '调用的方法名',
    http_method LowCardinality(String) COMMENT 'HTTP请求方法GET/POST等',
    url Nullable(String) COMMENT '请求URL地址',
    ip Nullable(String) COMMENT '客户端IP地址',
    exception Nullable(String) COMMENT '异常信息详情',
    operate_time DateTime COMMENT '操作时间',
    page_name Nullable(String) COMMENT '页面名称',
    request_id String COMMENT '请求唯一标识ID',
    tenant_code LowCardinality(String) COMMENT '租户代码'
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(operate_time)
ORDER BY (operate_time, tenant_code, type, terminal)
SETTINGS index_granularity = 8192
COMMENT '数据变更操作日志表，记录操作行为的详细信息';

-- 创建索引
ALTER TABLE s_log_data_change_operate ADD INDEX idx_user_name user_name TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_data_change_operate ADD INDEX idx_staff_name staff_name TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_data_change_operate ADD INDEX idx_request_id request_id TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_data_change_operate ADD INDEX idx_class_name class_name TYPE set(0) GRANULARITY 3;

-- ClickHouse s_log_data_change_detail 数据变更详细日志表
DROP TABLE IF EXISTS s_log_data_change_detail;

CREATE TABLE s_log_data_change_detail (
    id UUID DEFAULT generateUUIDv4() COMMENT '主键ID，自动生成UUID',
    name Nullable(String) COMMENT '操作业务名：entity的注解名称',
    type LowCardinality(String) COMMENT '数据操作类型：UPDATE|INSERT|DELETE',
    sql_command_type LowCardinality(String) COMMENT 'SQL命令类型',
    table_name String COMMENT '数据库表名',
    entity_name Nullable(String) COMMENT '对应的实体类名',
    order_code Nullable(String) COMMENT '单号',
    class_name Nullable(String) COMMENT '调用策略模式的数据变更类名',
    details Nullable(String) COMMENT '具体的变更前后数据，JSON格式存储',
    table_id Nullable(UInt32) COMMENT '数据库表对应的ID',
    c_id Nullable(UInt64) COMMENT '创建人ID',
    u_id Nullable(UInt64) COMMENT '修改人ID',
    c_time DateTime COMMENT '创建时间',
    u_time Nullable(DateTime) COMMENT '修改时间',
    c_name Nullable(String) COMMENT '创建人名称',
    u_name Nullable(String) COMMENT '修改人名称',
    request_id String COMMENT '请求唯一标识ID',
    tenant_code LowCardinality(String) COMMENT '租户代码'
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(c_time)
ORDER BY (c_time, tenant_code, table_name, type)
SETTINGS index_granularity = 8192
COMMENT '数据变更详细日志表，记录具体的数据变更信息和字段明细';

-- 创建索引
ALTER TABLE s_log_data_change_detail ADD INDEX idx_order_code order_code TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_data_change_detail ADD INDEX idx_request_id request_id TYPE set(0) GRANULARITY 3;
ALTER TABLE s_log_data_change_detail ADD INDEX idx_table_name table_name TYPE set(0) GRANULARITY 3;

-- ClickHouse s_job_log 定时任务调度日志表
DROP TABLE IF EXISTS s_job_log;

CREATE TABLE s_job_log (
    id UUID DEFAULT generateUUIDv4() COMMENT '主键ID，自动生成UUID',
    job_id Nullable(UInt64) COMMENT '任务主键',
    job_name Nullable(String) COMMENT '任务名称',
    job_group_type String DEFAULT '' COMMENT '任务组类型（ORDER BY字段，不能为Nullable）',
    job_serial_id Nullable(UInt64) COMMENT '关联编号',
    job_serial_type Nullable(String) COMMENT '关联表名字',
    job_desc Nullable(String) COMMENT '任务描述',
    job_simple_name Nullable(String) COMMENT '任务简称',
    class_name Nullable(String) COMMENT 'Bean名称',
    method_name Nullable(String) COMMENT '方法名称',
    param_class Nullable(String) COMMENT '参数类型',
    param_data Nullable(String) COMMENT '参数',
    cron_expression Nullable(String) COMMENT '表达式',
    concurrent Nullable(UInt8) COMMENT '是否并发执行（0允许 1禁止）',
    is_cron Nullable(UInt8) COMMENT '判断是否是cron表达式，还是simpletrigger',
    misfire_policy Nullable(String) COMMENT '计划策略：0=默认,1=立即触发执行,2=触发一次执行,3=不触发立即执行',
    is_del Nullable(UInt8) DEFAULT 0 COMMENT '是否是已经删除',
    is_effected Nullable(UInt8) COMMENT '是否有效',
    fire_time Nullable(DateTime) COMMENT '首次执行时间',
    scheduled_fire_time Nullable(DateTime) COMMENT '计划首次执行时间',
    prev_fire_time Nullable(DateTime) COMMENT '上次执行时间',
    next_fire_time Nullable(DateTime) COMMENT 'next_fire_time',
    run_times Nullable(UInt32) COMMENT '运行次数',
    msg Nullable(String) COMMENT '执行情况',
    c_id Nullable(UInt64) COMMENT '创建人ID',
    c_name Nullable(String) COMMENT '创建人名称',
    c_time DateTime DEFAULT now() COMMENT '创建时间（PARTITION BY字段，不能为Nullable）',
    u_id Nullable(UInt64) COMMENT '修改人ID',
    u_time Nullable(DateTime) COMMENT '修改时间',
    tenant_code String DEFAULT '' COMMENT '租户代码（ORDER BY字段，不能为Nullable）'
) ENGINE = MergeTree()
PARTITION BY toYYYYMM(c_time)
ORDER BY (c_time, tenant_code, job_group_type)
SETTINGS index_granularity = 8192
COMMENT '定时任务调度日志表，记录任务执行历史和状态信息';

-- 创建索引
ALTER TABLE s_job_log ADD INDEX idx_job_id job_id TYPE set(0) GRANULARITY 3;
ALTER TABLE s_job_log ADD INDEX idx_job_name job_name TYPE ngrambf_v1(3, 256, 2, 0) GRANULARITY 1;
ALTER TABLE s_job_log ADD INDEX idx_class_name class_name TYPE ngrambf_v1(3, 256, 2, 0) GRANULARITY 1;
ALTER TABLE s_job_log ADD INDEX idx_method_name method_name TYPE ngrambf_v1(3, 256, 2, 0) GRANULARITY 1;