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
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseQueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public ClickHouseRepository(@Qualifier("clickHouseClient") Client clickHouseClient) {
        this.clickHouseClient = clickHouseClient;
    }

    /**
     * 插入数据变更日志 (Client V2)
     */
    public void insertDataChangeLog(ClickHouseLogEntity logEntity) {
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
                
            } catch (Exception e) {
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
     * 批量插入数据变更日志 (Client V2)
     */
    public void batchInsertDataChangeLog(List<ClickHouseLogEntity> logEntities) {
        if (logEntities == null || logEntities.isEmpty()) {
            return;
        }

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
                
            } catch (Exception e) {
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

        try {
            // 使用Client V2查询
            List<GenericRecord> records = clickHouseClient.queryAll(sql);
            
            for (GenericRecord record : records) {
                ClickHouseLogEntity entity = mapRecordToEntity(record);
                result.add(entity);
            }
            
            logger.debug("查询数据变更日志成功，数量: {}", result.size());

        } catch (Exception e) {
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
     * 执行原生SQL查询 (Client V2)
     * 警告：此方法已经不再安全，应该在Service层确保SQL安全性
     * @deprecated 使用 executeQuery(String sql, Object... params) 替代
     */
    @Deprecated
    public List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> result = new ArrayList<>();

        try {
            // 使用Client V2查询
            List<GenericRecord> records = clickHouseClient.queryAll(sql);
            
            for (GenericRecord record : records) {
                Map<String, Object> row = convertRecordToMap(record);
                result.add(row);
            }

            logger.debug("执行SQL查询成功，结果数量: {}", result.size());

        } catch (Exception e) {
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

        } catch (Exception e) {
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
     */
    private Map<String, Object> convertRecordToMap(GenericRecord record) {
        Map<String, Object> row = new HashMap<>();
        
        // 遍历record的所有字段
        for (String columnName : record.getColumnNames()) {
            Object value = record.getObject(columnName);
            row.put(columnName, value);
        }
        
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
}