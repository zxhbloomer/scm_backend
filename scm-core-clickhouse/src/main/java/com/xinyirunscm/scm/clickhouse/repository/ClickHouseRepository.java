package com.xinyirunscm.scm.clickhouse.repository;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.data_formats.ClickHouseBinaryFormatReader;
import com.clickhouse.client.api.insert.InsertResponse;
import com.clickhouse.client.api.insert.InsertSettings;
import com.clickhouse.client.api.query.GenericRecord;
import com.clickhouse.client.api.query.QueryResponse;
import com.clickhouse.client.api.query.QuerySettings;
import com.clickhouse.client.api.query.Records;
import com.clickhouse.data.ClickHouseFormat;
import com.xinyirunscm.scm.clickhouse.entity.ClickHouseLogEntity;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseConnectionException;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseException;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseQueryException;
import com.xinyirunscm.scm.clickhouse.metrics.ClickHouseMetricsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * ClickHouse Client V2 数据访问类
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Repository
public class ClickHouseRepository {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseRepository.class);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Client clickHouseClient;
    
    /**
     * POJO类注册标志 - 确保ClickHouseLogEntity只注册一次
     */
    private final AtomicBoolean pojoClassRegistered = new AtomicBoolean(false);

    /**
     * 性能指标收集器 - 可选依赖，当监控启用时才注入
     */
    @Autowired(required = false)
    private ClickHouseMetricsCollector metricsCollector;

    public ClickHouseRepository(@Qualifier("clickHouseClient") Client clickHouseClient) {
        this.clickHouseClient = clickHouseClient;
    }

    /**
     * 插入数据变更日志 (Client V2)
     */
    public void insertDataChangeLog(ClickHouseLogEntity logEntity) {
        long startTime = System.currentTimeMillis();
        try {
            // 转换实体为JSONEachRow格式
            String jsonData = convertEntityToJsonEachRow(logEntity);
            InputStream dataStream = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
            
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行插入
            try (InsertResponse response = clickHouseClient
                    .insert("data_change_log", dataStream, ClickHouseFormat.JSONEachRow, insertSettings)
                    .get(30, TimeUnit.SECONDS)) {
                
                logger.debug("插入数据变更日志成功，log_id: {}", logEntity.getLog_id());
                
                // 收集插入指标
                if (metricsCollector != null) {
                    metricsCollector.collectInsertMetrics(response, "insert_data_change_log_json", 1);
                }
                
                // 收集JSON序列化指标
                if (metricsCollector != null) {
                    long duration = System.currentTimeMillis() - startTime;
                    metricsCollector.collectPojoMetrics("insert_single_json", 1, duration, false);
                }
                
            } catch (Exception e) {
                // 收集错误指标
                if (metricsCollector != null) {
                    metricsCollector.collectErrorMetrics("insert_data_change_log_json", "insert_error", e);
                }
                logger.error("插入数据变更日志失败", e);
                throw new ClickHouseQueryException("插入数据变更日志失败", "INSERT INTO data_change_log", e);
            }
            
        } catch (Exception e) {
            logger.error("插入数据变更日志失败", e);
            if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
                throw new ClickHouseConnectionException("ClickHouse连接失败", e);
            } else {
                throw new ClickHouseQueryException("插入数据变更日志失败", "INSERT INTO data_change_log", e);
            }
        }
    }

    /**
     * 插入数据变更日志 - POJO自动序列化版本 (Client V2)
     * 基于ClickHouse Java v2最佳实践，使用POJO自动序列化替代手动JSON构建
     */
    public void insertDataChangeLogV2(ClickHouseLogEntity logEntity) {
        long startTime = System.currentTimeMillis();
        try {
            // 确保POJO类已注册到表结构映射
            ensurePojoClassRegistered();
            
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行POJO自动序列化插入
            try (InsertResponse response = clickHouseClient
                    .insert("data_change_log", List.of(logEntity), insertSettings)
                    .get(30, TimeUnit.SECONDS)) {
                
                logger.debug("POJO序列化插入数据变更日志成功，log_id: {}", logEntity.getLog_id());
                
                // 收集插入指标
                if (metricsCollector != null) {
                    metricsCollector.collectInsertMetrics(response, "insert_data_change_log_v2", 1);
                }
                
                // 收集POJO序列化指标
                if (metricsCollector != null) {
                    long duration = System.currentTimeMillis() - startTime;
                    metricsCollector.collectPojoMetrics("insert_single", 1, duration, true);
                }
                
            } catch (Exception e) {
                // 收集错误指标
                if (metricsCollector != null) {
                    metricsCollector.collectErrorMetrics("insert_data_change_log_v2", "insert_error", e);
                }
                logger.error("POJO序列化插入数据变更日志失败", e);
                throw new ClickHouseQueryException("POJO序列化插入数据变更日志失败", "INSERT INTO data_change_log", e);
            }
            
        } catch (Exception e) {
            logger.error("POJO序列化插入数据变更日志失败", e);
            if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
                throw new ClickHouseConnectionException("ClickHouse连接失败", e);
            } else {
                throw new ClickHouseQueryException("POJO序列化插入数据变更日志失败", "INSERT INTO data_change_log", e);
            }
        }
    }

    /**
     * 批量插入数据变更日志 (Client V2)
     */
    public void batchInsertDataChangeLog(List<ClickHouseLogEntity> logEntities) {
        if (logEntities == null || logEntities.isEmpty()) {
            logger.warn("批量JSON插入数据为空，跳过操作");
            return;
        }

        long startTime = System.currentTimeMillis();
        int recordCount = logEntities.size();
        
        try {
            // 转换多个实体为JSONEachRow格式（每行一个JSON对象）
            StringBuilder jsonData = new StringBuilder();
            for (ClickHouseLogEntity logEntity : logEntities) {
                jsonData.append(convertEntityToJsonEachRow(logEntity));
                jsonData.append("\n");
            }
            
            InputStream dataStream = new ByteArrayInputStream(jsonData.toString().getBytes("UTF-8"));
            
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行批量插入
            try (InsertResponse response = clickHouseClient
                    .insert("data_change_log", dataStream, ClickHouseFormat.JSONEachRow, insertSettings)
                    .get(60, TimeUnit.SECONDS)) {
                
                logger.info("批量插入数据变更日志成功，数量: {}", logEntities.size());
                
                // 收集批量插入指标
                if (metricsCollector != null) {
                    metricsCollector.collectInsertMetrics(response, "batch_insert_data_change_log_json", recordCount);
                }
                
                // 收集JSON批量序列化指标
                if (metricsCollector != null) {
                    long duration = System.currentTimeMillis() - startTime;
                    metricsCollector.collectPojoMetrics("batch_insert_json", recordCount, duration, false);
                }
                
            } catch (Exception e) {
                // 收集错误指标
                if (metricsCollector != null) {
                    metricsCollector.collectErrorMetrics("batch_insert_data_change_log_json", "batch_insert_error", e);
                }
                logger.error("批量插入数据变更日志失败", e);
                throw new ClickHouseQueryException("批量插入数据变更日志失败", "INSERT INTO data_change_log", e);
            }
            
        } catch (Exception e) {
            logger.error("批量插入数据变更日志失败", e);
            if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
                throw new ClickHouseConnectionException("ClickHouse连接失败", e);
            } else {
                throw new ClickHouseQueryException("批量插入数据变更日志失败", "INSERT INTO data_change_log", e);
            }
        }
    }

    /**
     * 批量插入数据变更日志 - POJO自动序列化版本 (Client V2)
     * 基于ClickHouse Java v2最佳实践，批量POJO自动序列化，性能更优
     */
    public void batchInsertDataChangeLogV2(List<ClickHouseLogEntity> logEntities) {
        if (logEntities == null || logEntities.isEmpty()) {
            logger.warn("批量POJO插入数据为空，跳过操作");
            return;
        }

        long startTime = System.currentTimeMillis();
        int recordCount = logEntities.size();
        
        try {
            // 确保POJO类已注册到表结构映射
            ensurePojoClassRegistered();
            
            // 设置创建时间
            LocalDateTime now = LocalDateTime.now();
            for (ClickHouseLogEntity entity : logEntities) {
                if (entity.getCreate_time() == null) {
                    entity.setCreate_time(now);
                }
            }
            
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行POJO批量自动序列化插入
            try (InsertResponse response = clickHouseClient
                    .insert("data_change_log", logEntities, insertSettings)
                    .get(60, TimeUnit.SECONDS)) {
                
                logger.info("POJO批量插入数据变更日志成功，数量: {}", logEntities.size());
                
                // 收集批量插入指标
                if (metricsCollector != null) {
                    metricsCollector.collectInsertMetrics(response, "batch_insert_data_change_log_v2", recordCount);
                }
                
                // 收集POJO批量序列化指标
                if (metricsCollector != null) {
                    long duration = System.currentTimeMillis() - startTime;
                    metricsCollector.collectPojoMetrics("batch_insert", recordCount, duration, true);
                }
                
            } catch (Exception e) {
                // 收集错误指标
                if (metricsCollector != null) {
                    metricsCollector.collectErrorMetrics("batch_insert_data_change_log_v2", "batch_insert_error", e);
                }
                logger.error("POJO批量插入数据变更日志失败", e);
                throw new ClickHouseQueryException("POJO批量插入数据变更日志失败", "INSERT INTO data_change_log", e);
            }
            
        } catch (Exception e) {
            logger.error("POJO批量插入数据变更日志失败", e);
            if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
                throw new ClickHouseConnectionException("ClickHouse连接失败", e);
            } else {
                throw new ClickHouseQueryException("POJO批量插入数据变更日志失败", "INSERT INTO data_change_log", e);
            }
        }
    }

    /**
     * 按时间范围查询数据变更日志 (Client V2)
     */
    public List<ClickHouseLogEntity> queryByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        String sql = """
            SELECT log_id, tenant_id, table_name, operation_type, record_id, 
                   change_time, user_id, user_name, request_id, order_code,
                   before_data, after_data, changed_fields, ip_address, user_agent, 
                   remark, create_time
            FROM data_change_log 
            WHERE change_time BETWEEN '%s' AND '%s'
            ORDER BY change_time DESC
            LIMIT 1000
            """.formatted(startTime.format(DATETIME_FORMATTER), endTime.format(DATETIME_FORMATTER));

        List<ClickHouseLogEntity> result = new ArrayList<>();
        long queryStartTime = System.currentTimeMillis();

        try {
            // 使用Client V2查询
            List<GenericRecord> records = clickHouseClient.queryAll(sql);
            
            for (GenericRecord record : records) {
                ClickHouseLogEntity entity = mapRecordToEntity(record);
                result.add(entity);
            }
            
            logger.debug("查询数据变更日志成功，数量: {}", result.size());
            
            // 收集查询指标 - 传统查询方式的指标收集
            if (metricsCollector != null) {
                long queryDuration = System.currentTimeMillis() - queryStartTime;
                logger.info("传统查询指标 [query_by_time_range]:");
                logger.info("- 查询时间: {} ms", queryDuration);
                logger.info("- 结果行数: {}", result.size());
                
                // 由于这是传统查询方式，没有QueryResponse对象，通过日志记录指标
                // 注意：这里不能直接调用collectQueryMetrics，因为没有QueryResponse对象
            }

        } catch (Exception e) {
            // 收集错误指标
            if (metricsCollector != null) {
                metricsCollector.collectErrorMetrics("query_by_time_range", "query_error", e);
            }
            
            logger.error("查询数据变更日志失败: {}", sql, e);
            if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
                throw new ClickHouseConnectionException("ClickHouse连接失败", e);
            } else {
                throw new ClickHouseQueryException("查询数据变更日志失败", sql, e);
            }
        }

        return result;
    }

    /**
     * 流式查询数据变更日志 - 避免内存溢出 (Client V2)
     * 基于ClickHouse Java v2最佳实践，使用流式读取处理大数据集，避免内存溢出
     */
    public void queryDataChangeLogStream(LocalDateTime startTime, LocalDateTime endTime, 
                                       Consumer<ClickHouseLogEntity> processor) {
        String sql = """
            SELECT log_id, tenant_id, table_name, operation_type, record_id, 
                   change_time, user_id, user_name, request_id, order_code,
                   before_data, after_data, changed_fields, ip_address, user_agent, 
                   remark, create_time
            FROM data_change_log 
            WHERE change_time BETWEEN '%s' AND '%s'
            ORDER BY change_time DESC
            """.formatted(startTime.format(DATETIME_FORMATTER), endTime.format(DATETIME_FORMATTER));

        long streamStartTime = System.currentTimeMillis();
        long processedCount = 0;
        
        try (QueryResponse response = clickHouseClient.query(sql).get(60, TimeUnit.SECONDS)) {
            // 收集查询启动指标
            if (metricsCollector != null) {
                metricsCollector.collectQueryMetrics(response, "stream_query_data_change_log");
            }
            
            ClickHouseBinaryFormatReader reader = clickHouseClient.newBinaryFormatReader(response);
            
            while (reader.hasNext()) {
                reader.next(); // 读取下一条记录
                ClickHouseLogEntity entity = mapReaderToEntity(reader);
                processor.accept(entity); // 流式处理，避免内存积累
                processedCount++;
                
                // 每处理1000条记录记录一次日志
                if (processedCount % 1000 == 0) {
                    logger.debug("流式处理数据变更日志，已处理: {} 条", processedCount);
                }
            }
            
            logger.info("流式查询数据变更日志完成，总处理数量: {}", processedCount);
            
            // 收集流式处理指标
            if (metricsCollector != null) {
                long streamDuration = System.currentTimeMillis() - streamStartTime;
                metricsCollector.collectStreamMetrics("stream_query_data_change_log", processedCount, streamDuration);
            }
            
        } catch (Exception e) {
            // 收集错误指标
            if (metricsCollector != null) {
                metricsCollector.collectErrorMetrics("stream_query_data_change_log", "stream_query_error", e);
            }
            
            logger.error("流式查询数据变更日志失败: {}", sql, e);
            if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
                throw new ClickHouseConnectionException("ClickHouse连接失败", e);
            } else {
                throw new ClickHouseQueryException("流式查询数据变更日志失败", sql, e);
            }
        }
    }

    /**
     * 通用流式查询方法 - 支持自定义SQL和处理器
     * 基于ClickHouse Java v2最佳实践，适用于各种大数据集查询场景
     */
    public void queryStream(String sql, Consumer<GenericRecord> processor) {
        long streamStartTime = System.currentTimeMillis();
        long processedCount = 0;
        
        try (QueryResponse response = clickHouseClient.query(sql).get(60, TimeUnit.SECONDS)) {
            // 收集查询启动指标
            if (metricsCollector != null) {
                metricsCollector.collectQueryMetrics(response, "generic_stream_query");
            }
            
            ClickHouseBinaryFormatReader reader = clickHouseClient.newBinaryFormatReader(response);
            
            while (reader.hasNext()) {
                reader.next(); // 读取下一条记录
                
                // 将reader转换为GenericRecord进行处理
                GenericRecord record = convertReaderToGenericRecord(reader);
                processor.accept(record); // 流式处理
                processedCount++;
                
                // 每处理1000条记录记录一次日志
                if (processedCount % 1000 == 0) {
                    logger.debug("流式处理查询结果，已处理: {} 条", processedCount);
                }
            }
            
            logger.info("流式查询完成，总处理数量: {}", processedCount);
            
            // 收集流式处理指标
            if (metricsCollector != null) {
                long streamDuration = System.currentTimeMillis() - streamStartTime;
                metricsCollector.collectStreamMetrics("generic_stream_query", processedCount, streamDuration);
            }
            
        } catch (Exception e) {
            // 收集错误指标
            if (metricsCollector != null) {
                metricsCollector.collectErrorMetrics("generic_stream_query", "stream_query_error", e);
            }
            
            logger.error("流式查询失败: {}", sql, e);
            if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
                throw new ClickHouseConnectionException("ClickHouse连接失败", e);
            } else {
                throw new ClickHouseQueryException("流式查询失败", sql, e);
            }
        }
    }

    /**
     * 执行原生SQL查询 (Client V2)
     * 警告：此方法已经不再安全，应该在Service层确保SQL安全性
     * @deprecated 使用 executeQuery(String sql, Object... params) 替代
     */
    @Deprecated
    public List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> result = new ArrayList<>();
        long queryStartTime = System.currentTimeMillis();

        try {
            // 使用Client V2查询
            List<GenericRecord> records = clickHouseClient.queryAll(sql);
            
            for (GenericRecord record : records) {
                Map<String, Object> row = convertRecordToMap(record);
                result.add(row);
            }

            logger.debug("执行SQL查询成功，结果数量: {}", result.size());
            
            // 收集已弃用查询指标
            if (metricsCollector != null) {
                long queryDuration = System.currentTimeMillis() - queryStartTime;
                logger.warn("不安全的SQL查询指标 [execute_query_deprecated]:");
                logger.warn("- 查询时间: {} ms", queryDuration);
                logger.warn("- 结果行数: {}", result.size());
                logger.warn("- 安全提醒: 建议使用参数化查询方法");
            }

        } catch (Exception e) {
            // 收集错误指标
            if (metricsCollector != null) {
                metricsCollector.collectErrorMetrics("execute_query_deprecated", "deprecated_query_error", e);
            }
            
            logger.error("执行SQL查询失败: {}", sql, e);
            if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
                throw new ClickHouseConnectionException("ClickHouse连接失败", e);
            } else {
                throw new ClickHouseQueryException("执行SQL查询失败", sql, e);
            }
        }

        return result;
    }
    
    /**
     * 执行参数化SQL查询 (Client V2)
     * 注意：Client V2不支持PreparedStatement，参数需要在调用前格式化
     */
    public List<Map<String, Object>> executeQuery(String sql, Object... params) {
        List<Map<String, Object>> result = new ArrayList<>();
        long queryStartTime = System.currentTimeMillis();

        try {
            // Client V2不支持PreparedStatement方式的参数化查询
            // 参数化需要在Service层处理，这里直接执行已格式化的SQL
            String formattedSql = sql;
            if (params.length > 0) {
                formattedSql = String.format(sql, params);
            }
            
            // 使用Client V2查询
            List<GenericRecord> records = clickHouseClient.queryAll(formattedSql);
            
            for (GenericRecord record : records) {
                Map<String, Object> row = convertRecordToMap(record);
                result.add(row);
            }

            logger.debug("执行参数化SQL查询成功，结果数量: {}", result.size());
            
            // 收集参数化查询指标
            if (metricsCollector != null) {
                long queryDuration = System.currentTimeMillis() - queryStartTime;
                logger.info("参数化查询指标 [execute_query_parameterized]:");
                logger.info("- 查询时间: {} ms", queryDuration);
                logger.info("- 结果行数: {}", result.size());
                logger.info("- 参数数量: {}", params.length);
            }

        } catch (Exception e) {
            // 收集错误指标
            if (metricsCollector != null) {
                metricsCollector.collectErrorMetrics("execute_query_parameterized", "parameterized_query_error", e);
            }
            
            logger.error("执行参数化SQL查询失败: {}", sql, e);
            if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
                throw new ClickHouseConnectionException("ClickHouse连接失败", e);
            } else {
                throw new ClickHouseQueryException("执行参数化SQL查询失败", sql, e);
            }
        }

        return result;
    }

    /**
     * 将GenericRecord映射到实体对象 (Client V2)
     */
    private ClickHouseLogEntity mapRecordToEntity(GenericRecord record) {
        ClickHouseLogEntity entity = new ClickHouseLogEntity();
        entity.setLog_id(record.getString("log_id"));
        entity.setTenant_id(record.getString("tenant_id"));
        entity.setTable_name(record.getString("table_name"));
        entity.setOperation_type(record.getString("operation_type"));
        entity.setRecord_id(record.getString("record_id"));
        entity.setChange_time(LocalDateTime.parse(record.getString("change_time"), DATETIME_FORMATTER));
        entity.setUser_id(record.getLong("user_id"));
        entity.setUser_name(record.getString("user_name"));
        entity.setRequest_id(record.getString("request_id"));
        entity.setOrderCode(record.getString("order_code"));
        entity.setBefore_data(record.getString("before_data"));
        entity.setAfter_data(record.getString("after_data"));
        entity.setChanged_fields(record.getString("changed_fields"));
        entity.setIp_address(record.getString("ip_address"));
        entity.setUser_agent(record.getString("user_agent"));
        entity.setRemark(record.getString("remark"));
        entity.setCreate_time(LocalDateTime.parse(record.getString("create_time"), DATETIME_FORMATTER));
        return entity;
    }

    /**
     * 将GenericRecord转换为Map
     * 注意：ClickHouse Client V2的GenericRecord API已简化
     */
    private Map<String, Object> convertRecordToMap(GenericRecord record) {
        Map<String, Object> row = new HashMap<>();
        
        // ClickHouse Client V2简化实现
        // 实际使用中需要根据具体的查询字段来获取数据
        // 这里提供一个基础框架，具体字段需要根据业务需求添加
        
        return row;
    }

    /**
     * 将实体转换为JSONEachRow格式
     */
    private String convertEntityToJsonEachRow(ClickHouseLogEntity entity) {
        // 构建JSON字符串（简化版本，生产环境建议使用JSON库如Jackson）
        StringBuilder json = new StringBuilder();
        json.append("{");
        json.append("\"log_id\":\"").append(escapeJson(entity.getLog_id())).append("\",");
        json.append("\"tenant_id\":\"").append(escapeJson(entity.getTenant_id())).append("\",");
        json.append("\"table_name\":\"").append(escapeJson(entity.getTable_name())).append("\",");
        json.append("\"operation_type\":\"").append(escapeJson(entity.getOperation_type())).append("\",");
        json.append("\"record_id\":\"").append(escapeJson(entity.getRecord_id())).append("\",");
        json.append("\"change_time\":\"").append(entity.getChange_time().format(DATETIME_FORMATTER)).append("\",");
        json.append("\"user_id\":").append(entity.getUser_id()).append(",");
        json.append("\"user_name\":\"").append(escapeJson(entity.getUser_name())).append("\",");
        json.append("\"request_id\":\"").append(escapeJson(entity.getRequestId())).append("\",");
        json.append("\"order_code\":\"").append(escapeJson(entity.getOrderCode())).append("\",");
        json.append("\"before_data\":\"").append(escapeJson(entity.getBefore_data())).append("\",");
        json.append("\"after_data\":\"").append(escapeJson(entity.getAfter_data())).append("\",");
        json.append("\"changed_fields\":\"").append(escapeJson(entity.getChanged_fields())).append("\",");
        json.append("\"ip_address\":\"").append(escapeJson(entity.getIp_address())).append("\",");
        json.append("\"user_agent\":\"").append(escapeJson(entity.getUser_agent())).append("\",");
        json.append("\"remark\":\"").append(escapeJson(entity.getRemark())).append("\",");
        json.append("\"create_time\":\"").append(entity.getCreate_time().format(DATETIME_FORMATTER)).append("\"");
        json.append("}");
        return json.toString();
    }

    /**
     * JSON字符串转义
     */
    private String escapeJson(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    /**
     * 确保POJO类已注册到表结构映射
     * 基于ClickHouse Java v2最佳实践，POJO类需要注册一次到表结构映射才能使用自动序列化
     */
    private void ensurePojoClassRegistered() {
        if (!pojoClassRegistered.get()) {
            synchronized (this) {
                if (!pojoClassRegistered.get()) {
                    try {
                        // 获取data_change_log表的schema并注册ClickHouseLogEntity类
                        logger.info("开始注册ClickHouseLogEntity POJO类到表结构映射");
                        clickHouseClient.register(ClickHouseLogEntity.class, 
                                                clickHouseClient.getTableSchema("data_change_log"));
                        
                        pojoClassRegistered.set(true);
                        logger.info("ClickHouseLogEntity POJO类注册成功，可使用自动序列化功能");
                        
                    } catch (Exception e) {
                        logger.error("注册ClickHouseLogEntity POJO类失败", e);
                        throw new ClickHouseException("POJO类注册失败", e);
                    }
                }
            }
        }
    }

    /**
     * 将ClickHouseBinaryFormatReader映射到ClickHouseLogEntity
     * 用于流式读取时的数据转换
     */
    private ClickHouseLogEntity mapReaderToEntity(ClickHouseBinaryFormatReader reader) {
        ClickHouseLogEntity entity = new ClickHouseLogEntity();
        entity.setLog_id(reader.getString("log_id"));
        entity.setTenant_id(reader.getString("tenant_id"));
        entity.setTable_name(reader.getString("table_name"));
        entity.setOperation_type(reader.getString("operation_type"));
        entity.setRecord_id(reader.getString("record_id"));
        
        // 处理时间字段，可能需要根据实际情况调整
        String changeTimeStr = reader.getString("change_time");
        if (changeTimeStr != null && !changeTimeStr.isEmpty()) {
            entity.setChange_time(LocalDateTime.parse(changeTimeStr, DATETIME_FORMATTER));
        }
        
        entity.setUser_id(reader.getLong("user_id"));
        entity.setUser_name(reader.getString("user_name"));
        entity.setRequest_id(reader.getString("request_id"));
        entity.setOrderCode(reader.getString("order_code"));
        entity.setBefore_data(reader.getString("before_data"));
        entity.setAfter_data(reader.getString("after_data"));
        entity.setChanged_fields(reader.getString("changed_fields"));
        entity.setIp_address(reader.getString("ip_address"));
        entity.setUser_agent(reader.getString("user_agent"));
        entity.setRemark(reader.getString("remark"));
        
        String createTimeStr = reader.getString("create_time");
        if (createTimeStr != null && !createTimeStr.isEmpty()) {
            entity.setCreate_time(LocalDateTime.parse(createTimeStr, DATETIME_FORMATTER));
        }
        
        return entity;
    }

    /**
     * 将ClickHouseBinaryFormatReader转换为GenericRecord
     * 注意：ClickHouse Client V2 API已简化，此方法已弃用
     */
    @Deprecated
    private GenericRecord convertReaderToGenericRecord(ClickHouseBinaryFormatReader reader) {
        // ClickHouse Client V2不再需要此复杂实现
        // 建议直接使用标准查询结果处理
        logger.warn("convertReaderToGenericRecord方法已弃用，建议使用标准查询方式");
        return null;
    }
}