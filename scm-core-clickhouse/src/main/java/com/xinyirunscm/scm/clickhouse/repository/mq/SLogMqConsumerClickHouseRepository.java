package com.xinyirunscm.scm.clickhouse.repository.mq;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.data_formats.ClickHouseBinaryFormatReader;
import com.clickhouse.client.api.insert.InsertResponse;
import com.clickhouse.client.api.insert.InsertSettings;
import com.clickhouse.client.api.query.GenericRecord;
import com.clickhouse.client.api.query.QueryResponse;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.mq.SLogMqConsumerClickHouseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirunscm.scm.clickhouse.entity.mq.SLogMqConsumerClickHouseEntity;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseConnectionException;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseQueryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * MQ消费者日志 ClickHouse Repository
 * 专门处理 s_log_mq_consumer 表的所有操作
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Slf4j
@Repository
public class SLogMqConsumerClickHouseRepository {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String TABLE_NAME = "s_log_mq_consumer";

    private final Client clickHouseClient;

    public SLogMqConsumerClickHouseRepository(@Qualifier("clickHouseClient") Client clickHouseClient) {
        this.clickHouseClient = clickHouseClient;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入单条MQ消费者日志
     */
    public void insert(SLogMqConsumerClickHouseEntity entity) {
        long startTime = System.currentTimeMillis();
        try {
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行POJO自动序列化插入
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, List.of(entity), insertSettings)
                    .get(30, TimeUnit.SECONDS)) {
                
                log.debug("插入MQ消费者日志成功，message_id: {}", entity.getMessage_id());
                
            } catch (Exception e) {
                handleInsertError(e, "插入MQ消费者日志失败", "insert_mq_consumer_log");
            }
            
        } catch (Exception e) {
            handleRepositoryError(e, "插入MQ消费者日志失败");
        }
    }

    /**
     * 批量插入MQ消费者日志 - 性能最优
     */
    public void batchInsert(List<SLogMqConsumerClickHouseEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            log.warn("批量插入MQ消费者日志数据为空，跳过操作");
            return;
        }

        long startTime = System.currentTimeMillis();
        int recordCount = entities.size();
        
        try {
            // 设置创建时间
            LocalDateTime now = LocalDateTime.now();
            for (SLogMqConsumerClickHouseEntity entity : entities) {
                if (entity.getConsumer_c_time() == null) {
                    entity.setConsumer_c_time(now);
                }
            }
            
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行批量插入
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, entities, insertSettings)
                    .get(60, TimeUnit.SECONDS)) {
                
                log.info("批量插入MQ消费者日志成功，数量: {}", recordCount);
                
            } catch (Exception e) {
                handleInsertError(e, "批量插入MQ消费者日志失败", "batch_insert_mq_consumer_log");
            }
            
        } catch (Exception e) {
            handleRepositoryError(e, "批量插入MQ消费者日志失败");
        }
    }

    // ==================== 参数化分页查询操作 ====================

    /**
     * 参数化分页查询MQ消费者日志 - 使用 ClickHouse Client v2 参数化查询
     * 支持的查询条件：类型、队列名称、队列编码、message_id、消息体、时间范围、租户代码
     */
    public IPage<SLogMqConsumerClickHouseVo> selectPageWithParams(SLogMqConsumerClickHouseVo searchCondition) {
        try {
            PageCondition pageCondition = searchCondition.getPageCondition();
            long current = pageCondition != null ? pageCondition.getCurrent() : 1;
            long size = pageCondition != null ? pageCondition.getSize() : 10;
            String sort = pageCondition != null ? pageCondition.getSort() : null;
            
            // 1. 构建参数化查询条件
            StringBuilder whereClause = new StringBuilder("WHERE 1=1");
            Map<String, Object> queryParams = new HashMap<>();
            
            // 类型查询条件 - 精确匹配
            if (StringUtils.isNotBlank(searchCondition.getType())) {
                whereClause.append(" AND type = {type:String}");
                queryParams.put("type", searchCondition.getType());
            }
            
            // 队列名称查询条件 - 模糊匹配
            if (StringUtils.isNotBlank(searchCondition.getName())) {
                whereClause.append(" AND name LIKE {name:String}");
                queryParams.put("name", "%" + searchCondition.getName() + "%");
            }
            
            // 队列编码查询条件 - 模糊匹配
            if (StringUtils.isNotBlank(searchCondition.getCode())) {
                whereClause.append(" AND code LIKE {code:String}");
                queryParams.put("code", "%" + searchCondition.getCode() + "%");
            }
            
            // message_id查询条件 - 模糊匹配
            if (StringUtils.isNotBlank(searchCondition.getMessage_id())) {
                whereClause.append(" AND message_id LIKE {messageId:String}");
                queryParams.put("messageId", "%" + searchCondition.getMessage_id() + "%");
            }
            
            // 消息体查询条件 - 模糊匹配
            if (StringUtils.isNotBlank(searchCondition.getMq_data())) {
                whereClause.append(" AND mq_data LIKE {mqData:String}");
                queryParams.put("mqData", "%" + searchCondition.getMq_data() + "%");
            }

            // 时间范围查询 - 使用字符串格式避免DateTime解析错误
            if (searchCondition.getStart_time() != null) {
                whereClause.append(" AND consumer_c_time >= {startTime:String}");
                queryParams.put("startTime", searchCondition.getStart_time().format(DATETIME_FORMATTER));
            }

            if (searchCondition.getOver_time() != null) {
                whereClause.append(" AND consumer_c_time <= {endTime:String}");
                queryParams.put("endTime", searchCondition.getOver_time().format(DATETIME_FORMATTER));
            }

            // 租户代码条件 - 必须条件，确保数据隔离
            if (StringUtils.isNotBlank(searchCondition.getTenant_code())) {
                whereClause.append(" AND tenant_code = {tenantCode:String}");
                queryParams.put("tenantCode", searchCondition.getTenant_code());
            }
            
            // 2. 执行 COUNT 查询获取总数
            String countSql = "SELECT count(*) as total FROM s_log_mq_consumer " + whereClause;
            long total = 0;
            List<GenericRecord> countRecords = clickHouseClient.queryAll(countSql, queryParams);
            if (!countRecords.isEmpty()) {
                total = countRecords.get(0).getLong("total");
            }
            
            // 3. 构建排序子句
            String orderClause = buildOrderByClause(sort);
            
            // 4. 执行分页数据查询 - 使用参数化查询
            queryParams.put("offset", (current - 1) * size);
            queryParams.put("limit", size);
            
            String dataSql = """
                SELECT id, type, message_id, code, name, exchange, routing_key, mq_data, 
                       consumer_status, consumer_c_time, consumer_exception, tenant_code
                FROM s_log_mq_consumer 
                """ + whereClause + " " + orderClause + """
                
                LIMIT {limit:UInt32} OFFSET {offset:UInt32}
                """;
            
            List<SLogMqConsumerClickHouseEntity> entities = new ArrayList<>();
            try (QueryResponse dataResponse = clickHouseClient.query(dataSql, queryParams).get()) {
                ClickHouseBinaryFormatReader reader = clickHouseClient.newBinaryFormatReader(dataResponse);
                while (reader.hasNext()) {
                    reader.next();
                    entities.add(mapReaderToEntity(reader));
                }
            }
            
            // 5. 转换 Entity 为 VO 并构建 IPage 结果
            List<SLogMqConsumerClickHouseVo> records = entities.stream()
                .map(this::convertEntityToVo)
                .collect(Collectors.toList());
            
            IPage<SLogMqConsumerClickHouseVo> page = new Page<>(current, size, total);
            page.setRecords(records);
            
            log.info("参数化分页查询MQ消费者日志成功，当前页: {}, 页大小: {}, 总数: {}, 结果数量: {}", 
                    current, size, total, records.size());
            
            return page;
            
        } catch (Exception e) {
            log.error("参数化分页查询MQ消费者日志失败", e);
            throw new ClickHouseQueryException("参数化分页查询MQ消费者日志失败", "", e);
        }
    }

    /**
     * 构建安全的排序子句
     */
    private String buildOrderByClause(String sort) {
        if (StringUtils.isBlank(sort)) {
            return "ORDER BY consumer_c_time DESC"; // 默认按创建时间倒序
        }
        
        // 安全的排序字段白名单
        Map<String, String> sortFieldMap = Map.of(
            "type", "type",
            "name", "name", 
            "code", "code",
            "message_id", "message_id",
            "consumer_c_time", "consumer_c_time",
            "consumer_status", "consumer_status"
        );
        
        // 简单的排序解析，格式: "field_ASC" 或 "field_DESC"
        String[] parts = sort.split("_");
        if (parts.length >= 2) {
            String field = parts[0];
            String direction = parts[parts.length - 1].toUpperCase();
            
            if (sortFieldMap.containsKey(field) && 
                ("ASC".equals(direction) || "DESC".equals(direction))) {
                return "ORDER BY " + sortFieldMap.get(field) + " " + direction;
            }
        }
        
        // 如果解析失败，返回默认排序
        return "ORDER BY consumer_c_time DESC";
    }

    /**
     * 转换 Entity 为 VO
     */
    private SLogMqConsumerClickHouseVo convertEntityToVo(SLogMqConsumerClickHouseEntity entity) {
        return (SLogMqConsumerClickHouseVo) BeanUtilsSupport.copyProperties(entity, SLogMqConsumerClickHouseVo.class);
    }

    // ==================== 单条记录查询操作 ====================

    /**
     * 根据ID查询单条MQ消费者日志记录
     * 使用ClickHouse Client v2参数化查询防止SQL注入
     * 支持租户代码过滤，确保数据隔离
     * 
     * @param searchCondition 查询条件（包含ID和tenant_code）
     * @return 找到的VO对象，未找到返回null
     */
    public SLogMqConsumerClickHouseVo getById(SLogMqConsumerClickHouseVo searchCondition) {
        if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
            log.warn("getById查询参数为空或ID为空，返回null");
            return null;
        }

        String sql = """
            SELECT 
                id, type, message_id, code, name, exchange, routing_key, mq_data,
                consumer_status, consumer_c_time, consumer_exception, tenant_code
            FROM s_log_mq_consumer 
            WHERE id = {id:String}
            AND tenant_code = {tenantCode:String}
            LIMIT 1
            """;

        String id = searchCondition.getId().trim();
        String tenantCode = searchCondition.getTenant_code();
        
        Map<String, Object> queryParams = Map.of(
            "id", id,
            "tenantCode", tenantCode != null ? tenantCode : ""
        );

        try {
            List<GenericRecord> records = clickHouseClient.queryAll(sql, queryParams);
            
            if (!records.isEmpty()) {
                GenericRecord record = records.get(0);
                
                // 构建实体对象
                SLogMqConsumerClickHouseEntity entity = new SLogMqConsumerClickHouseEntity();
                entity.setId(record.getString("id"));
                entity.setType(record.getString("type"));
                entity.setMessage_id(record.getString("message_id"));
                entity.setCode(record.getString("code"));
                entity.setName(record.getString("name"));
                entity.setExchange(record.getString("exchange"));
                entity.setRouting_key(record.getString("routing_key"));
                entity.setMq_data(record.getString("mq_data"));
                entity.setConsumer_status(record.getInteger("consumer_status"));
                entity.setConsumer_exception(record.getString("consumer_exception"));
                entity.setTenant_code(record.getString("tenant_code"));
                entity.setConsumer_c_time(record.getLocalDateTime("consumer_c_time"));

                // 转换为VO并返回
                SLogMqConsumerClickHouseVo vo = convertEntityToVo(entity);
                log.info("根据ID查询MQ消费者日志成功，ID: {}, 租户: {}, 队列: {}", id, tenantCode, vo.getName());
                return vo;
            }
            
            log.info("根据ID查询MQ消费者日志未找到记录，ID: {}, 租户: {}", id, tenantCode);
            return null;
            
        } catch (Exception e) {
            log.error("根据ID查询MQ消费者日志失败，ID: {}, 租户: {}", id, tenantCode, e);
            throw new ClickHouseQueryException("根据ID查询MQ消费者日志失败", sql, e);
        }
    }


    // ==================== 私有辅助方法 ====================


    /**
     * 将ClickHouseBinaryFormatReader映射到实体对象 - 用于流式读取
     */
    private SLogMqConsumerClickHouseEntity mapReaderToEntity(ClickHouseBinaryFormatReader reader) {
        SLogMqConsumerClickHouseEntity entity = new SLogMqConsumerClickHouseEntity();
        
        entity.setId(reader.getString("id"));
        entity.setType(reader.getString("type"));
        entity.setMessage_id(reader.getString("message_id"));
        entity.setCode(reader.getString("code"));
        entity.setName(reader.getString("name"));
        entity.setExchange(reader.getString("exchange"));
        entity.setRouting_key(reader.getString("routing_key"));
        entity.setMq_data(reader.getString("mq_data"));
        entity.setConsumer_status(reader.getInteger("consumer_status"));
        entity.setConsumer_exception(reader.getString("consumer_exception"));
        entity.setTenant_code(reader.getString("tenant_code"));
        entity.setConsumer_c_time(reader.getLocalDateTime("consumer_c_time"));
        
        return entity;
    }

    /**
     * 处理插入错误
     */
    private void handleInsertError(Exception e, String message, String operation) {
        log.error(message, e);
        throw new ClickHouseQueryException(message, "INSERT INTO " + TABLE_NAME, e);
    }

    /**
     * 处理Repository异常
     */
    private void handleRepositoryError(Exception e, String message) {
        if (e.getMessage().contains("connection") || e.getMessage().contains("timeout")) {
            throw new ClickHouseConnectionException("ClickHouse连接失败", e);
        } else {
            throw new ClickHouseQueryException(message, "", e);
        }
    }
}