package com.xinyirun.scm.clickhouse.repository.datachange;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.data_formats.ClickHouseBinaryFormatReader;
import com.clickhouse.client.api.insert.InsertResponse;
import com.clickhouse.client.api.insert.InsertSettings;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clickhouse.client.api.query.GenericRecord;
import com.clickhouse.client.api.query.QueryResponse;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeOperateClickHouseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.clickhouse.entity.datachange.SLogDataChangeOperateClickHouseEntity;
import com.xinyirun.scm.clickhouse.exception.ClickHouseConnectionException;
import com.xinyirun.scm.clickhouse.exception.ClickHouseQueryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;
import java.util.concurrent.TimeUnit;

/**
 * 数据变更操作日志 ClickHouse Repository
 * 专门处理 s_log_data_change_operate 表的所有操作
 * 
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-09-19 - 数据变更日志架构实现
 */
@Slf4j
@Repository
public class SLogDataChangeOperateClickHouseRepository {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String TABLE_NAME = "s_log_data_change_operate";

    private final Client clickHouseClient;

    

    public SLogDataChangeOperateClickHouseRepository(@Qualifier("clickHouseClient") Client clickHouseClient) {
        this.clickHouseClient = clickHouseClient;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入单条数据变更操作日志
     */
    public void insert(SLogDataChangeOperateClickHouseEntity operateLogEntity) {
        long startTime = System.currentTimeMillis();
        try {
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行POJO自动序列化插入
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, List.of(operateLogEntity), insertSettings)
                    .get(30, TimeUnit.SECONDS)) {
                
                log.debug("插入数据变更操作日志成功，request_id: {}", operateLogEntity.getRequest_id());
                
            } catch (Exception e) {
                handleInsertError(e, "插入数据变更操作日志失败", "insert_data_change_operate");
            }
            
        } catch (Exception e) {
            handleRepositoryError(e, "插入数据变更操作日志失败");
        }
    }

    /**
     * 批量插入数据变更操作日志 - 性能最优
     */
    public void batchInsert(List<SLogDataChangeOperateClickHouseEntity> operateLogEntities) {
        if (operateLogEntities == null || operateLogEntities.isEmpty()) {
            log.warn("批量插入数据变更操作日志数据为空，跳过操作");
            return;
        }

        long startTime = System.currentTimeMillis();
        int recordCount = operateLogEntities.size();
        
        try {
            // 设置操作时间
            LocalDateTime now = LocalDateTime.now();
            for (SLogDataChangeOperateClickHouseEntity entity : operateLogEntities) {
                if (entity.getOperate_time() == null) {
                    entity.setOperate_time(now);
                }
            }
            
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行批量插入
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, operateLogEntities, insertSettings)
                    .get(60, TimeUnit.SECONDS)) {
                
                log.info("批量插入数据变更操作日志成功，数量: {}", recordCount);
                
            } catch (Exception e) {
                handleInsertError(e, "批量插入数据变更操作日志失败", "batch_insert_data_change_operate");
            }
            
        } catch (Exception e) {
            handleRepositoryError(e, "批量插入数据变更操作日志失败");
        }
    }

    // ==================== 参数化分页查询操作 ====================

    /**
     * 参数化分页查询数据变更操作日志 - 使用 ClickHouse Client v2 参数化查询
     * 支持日志类型、用户名、操作说明、类名、方法名、URL、终端类型条件查询，支持分页和排序
     */
    public IPage<SLogDataChangeOperateClickHouseVo> selectPageWithParams(SLogDataChangeOperateClickHouseVo searchCondition) {
        try {
            PageCondition pageCondition = searchCondition.getPageCondition();
            long current = pageCondition != null ? pageCondition.getCurrent() : 1;
            long size = pageCondition != null ? pageCondition.getSize() : 10;
            String sort = pageCondition != null ? pageCondition.getSort() : null;
            
            // 1. 构建参数化查询条件
            StringBuilder whereClause = new StringBuilder("WHERE 1=1");
            Map<String, Object> queryParams = new HashMap<>();
            
            if (StringUtils.isNotBlank(searchCondition.getType())) {
                whereClause.append(" AND type = {type:String}");
                queryParams.put("type", searchCondition.getType());
            }
            
            if (StringUtils.isNotBlank(searchCondition.getUser_name())) {
                whereClause.append(" AND user_name LIKE {userName:String}");
                queryParams.put("userName", "%" + searchCondition.getUser_name() + "%");
            }
            
            if (StringUtils.isNotBlank(searchCondition.getStaff_name())) {
                whereClause.append(" AND staff_name LIKE {staffName:String}");
                queryParams.put("staffName", "%" + searchCondition.getStaff_name() + "%");
            }
            
            if (StringUtils.isNotBlank(searchCondition.getOperation())) {
                whereClause.append(" AND operation LIKE {operation:String}");
                queryParams.put("operation", "%" + searchCondition.getOperation() + "%");
            }
            
            // 类名查询条件
            if (StringUtils.isNotBlank(searchCondition.getClass_name())) {
                whereClause.append(" AND class_name LIKE {className:String}");
                queryParams.put("className", "%" + searchCondition.getClass_name() + "%");
            }
            
            // 方法名查询条件
            if (StringUtils.isNotBlank(searchCondition.getClass_method())) {
                whereClause.append(" AND class_method LIKE {classMethod:String}");
                queryParams.put("classMethod", "%" + searchCondition.getClass_method() + "%");
            }
            
            // URL查询条件
            if (StringUtils.isNotBlank(searchCondition.getUrl())) {
                whereClause.append(" AND url LIKE {url:String}");
                queryParams.put("url", "%" + searchCondition.getUrl() + "%");
            }
            
            // HTTP方法查询条件
            if (StringUtils.isNotBlank(searchCondition.getHttp_method())) {
                whereClause.append(" AND http_method = {httpMethod:String}");
                queryParams.put("httpMethod", searchCondition.getHttp_method());
            }
            
            // IP地址查询条件
            if (StringUtils.isNotBlank(searchCondition.getIp())) {
                whereClause.append(" AND ip LIKE {ip:String}");
                queryParams.put("ip", "%" + searchCondition.getIp() + "%");
            }


            // 时间范围查询 - 使用字符串格式避免DateTime解析错误
            if (searchCondition.getStart_time() != null) {
                whereClause.append(" AND operate_time >= {startTime:String}");
                queryParams.put("startTime", searchCondition.getStart_time().format(DATETIME_FORMATTER));
            }

            if (searchCondition.getOver_time() != null) {
                whereClause.append(" AND operate_time <= {endTime:String}");
                queryParams.put("endTime", searchCondition.getOver_time().format(DATETIME_FORMATTER));
            }

            // 租户代码条件 - 必须条件，确保数据隔离
            if (StringUtils.isNotBlank(searchCondition.getTenant_code())) {
                whereClause.append(" AND tenant_code = {tenantCode:String}");
                queryParams.put("tenantCode", searchCondition.getTenant_code());
            }
            
            // 2. 执行 COUNT 查询获取总数
            String countSql = "SELECT count(*) as total FROM s_log_data_change_operate " + whereClause;
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
                SELECT id, type, user_name, staff_name, staff_id, operation, time, 
                       class_name, class_method, http_method, url, ip, exception, 
                       operate_time, page_name, request_id, tenant_code
                FROM s_log_data_change_operate 
                """ + whereClause + " " + orderClause + """
                
                LIMIT {limit:UInt32} OFFSET {offset:UInt32}
                """;
            
            List<SLogDataChangeOperateClickHouseEntity> entities = new ArrayList<>();
            try (QueryResponse dataResponse = clickHouseClient.query(dataSql, queryParams).get()) {
                ClickHouseBinaryFormatReader reader = clickHouseClient.newBinaryFormatReader(dataResponse);
                while (reader.hasNext()) {
                    reader.next();
                    entities.add(mapReaderToEntity(reader));
                }
            }
            
            // 5. 转换 Entity 为 VO 并构建 IPage 结果
            List<SLogDataChangeOperateClickHouseVo> records = entities.stream()
                .map(this::convertEntityToVo)
                .collect(Collectors.toList());
            
            IPage<SLogDataChangeOperateClickHouseVo> page = new Page<>(current, size, total);
            page.setRecords(records);
            
            log.info("参数化分页查询数据变更操作日志成功，当前页: {}, 页大小: {}, 总数: {}, 结果数量: {}", 
                    current, size, total, records.size());
            
            return page;
            
        } catch (Exception e) {
            log.error("参数化分页查询数据变更操作日志失败", e);
            throw new ClickHouseQueryException("参数化分页查询数据变更操作日志失败", "", e);
        }
    }

    /**
     * 构建安全的排序子句
     */
    private String buildOrderByClause(String sort) {
        if (StringUtils.isBlank(sort)) {
            return "ORDER BY operate_time DESC"; // 默认按操作时间倒序
        }
        
        // 安全的排序字段白名单
        Map<String, String> sortFieldMap = Map.of(
            "type", "type",
            "user_name", "user_name",
            "staff_name", "staff_name", 
            "operation", "operation",
            "operate_time", "operate_time",
            "class_name", "class_name",
            "class_method", "class_method",
            "http_method", "http_method",
            "url", "url"
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
        return "ORDER BY operate_time DESC";
    }

    /**
     * 转换 Entity 为 VO
     */
    private SLogDataChangeOperateClickHouseVo convertEntityToVo(SLogDataChangeOperateClickHouseEntity entity) {
        return (SLogDataChangeOperateClickHouseVo) BeanUtilsSupport.copyProperties(entity, SLogDataChangeOperateClickHouseVo.class);
    }

    // ==================== 单条记录查询操作 ====================

    /**
     * 根据ID查询单条数据变更操作日志记录
     * 使用ClickHouse Client v2参数化查询防止SQL注入
     * 支持租户代码过滤，确保数据隔离
     * 
     * @param searchCondition 查询条件（包含ID和tenant_code）
     * @return 找到的VO对象，未找到返回null
     */
    public SLogDataChangeOperateClickHouseVo getById(SLogDataChangeOperateClickHouseVo searchCondition) {
        if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
            log.warn("getById查询参数为空或ID为空，返回null");
            return null;
        }

        String sql = """
            SELECT 
                id, type, user_name, staff_name, staff_id, operation, time,
                class_name, class_method, http_method, url, ip, exception,
                operate_time, page_name, request_id, tenant_code
            FROM s_log_data_change_operate 
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
                SLogDataChangeOperateClickHouseEntity entity = new SLogDataChangeOperateClickHouseEntity();
                entity.setId(record.getString("id"));
                entity.setType(record.getString("type"));
                entity.setUser_name(record.getString("user_name"));
                entity.setStaff_name(record.getString("staff_name"));
                entity.setStaff_id(record.getString("staff_id"));
                entity.setOperation(record.getString("operation"));
                
                // time字段空值处理，避免NullValueException
                try {
                    Long timeValue = record.getLong("time");
                    entity.setTime(timeValue);
                } catch (Exception e) {
                    // 如果time字段为空或者读取失败，设为默认值0
                    entity.setTime(0L);
                    log.debug("time字段为空或读取失败，设为默认值0，ID: {}", record.getString("id"));
                }
                
                entity.setClass_name(record.getString("class_name"));
                entity.setClass_method(record.getString("class_method"));
                entity.setHttp_method(record.getString("http_method"));
                entity.setUrl(record.getString("url"));
                entity.setIp(record.getString("ip"));
                entity.setException(record.getString("exception"));
                entity.setOperate_time(record.getLocalDateTime("operate_time"));
                entity.setPage_name(record.getString("page_name"));
                entity.setRequest_id(record.getString("request_id"));
                entity.setTenant_code(record.getString("tenant_code"));

                // 转换为VO并返回
                SLogDataChangeOperateClickHouseVo vo = convertEntityToVo(entity);
                log.info("根据ID查询数据变更操作日志成功，ID: {}, 租户: {}, 用户: {}", id, tenantCode, vo.getUser_name());
                return vo;
            }
            
            log.info("根据ID查询数据变更操作日志未找到记录，ID: {}, 租户: {}", id, tenantCode);
            return null;
            
        } catch (Exception e) {
            log.error("根据ID查询数据变更操作日志失败，ID: {}, 租户: {}", id, tenantCode, e);
            throw new ClickHouseQueryException("根据ID查询数据变更操作日志失败", sql, e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 将ClickHouseBinaryFormatReader映射到实体对象 - 用于流式读取
     */
    private SLogDataChangeOperateClickHouseEntity mapReaderToEntity(ClickHouseBinaryFormatReader reader) {
        SLogDataChangeOperateClickHouseEntity entity = new SLogDataChangeOperateClickHouseEntity();
        entity.setId(reader.getString("id"));
        entity.setType(reader.getString("type"));
        entity.setUser_name(reader.getString("user_name"));
        entity.setStaff_name(reader.getString("staff_name"));
        entity.setStaff_id(reader.getString("staff_id"));
        entity.setOperation(reader.getString("operation"));
        
        // time字段空值处理，避免NullValueException
        try {
            Long timeValue = reader.getLong("time");
            entity.setTime(timeValue);
        } catch (Exception e) {
            // 如果time字段为空或者读取失败，设为默认值0
            entity.setTime(0L);
            log.debug("time字段为空或读取失败，设为默认值0，ID: {}", reader.getString("id"));
        }
        
        entity.setClass_name(reader.getString("class_name"));
        entity.setClass_method(reader.getString("class_method"));
        entity.setHttp_method(reader.getString("http_method"));
        entity.setUrl(reader.getString("url"));
        entity.setIp(reader.getString("ip"));
        entity.setException(reader.getString("exception"));
        entity.setOperate_time(reader.getLocalDateTime("operate_time"));
        entity.setPage_name(reader.getString("page_name"));
        entity.setRequest_id(reader.getString("request_id"));
        entity.setTenant_code(reader.getString("tenant_code"));
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