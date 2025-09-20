package com.xinyirunscm.scm.clickhouse.repository.datachange;

import com.alibaba.fastjson2.JSON;
import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.data_formats.ClickHouseBinaryFormatReader;
import com.clickhouse.client.api.insert.InsertResponse;
import com.clickhouse.client.api.insert.InsertSettings;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clickhouse.client.api.query.GenericRecord;
import com.clickhouse.client.api.query.QueryResponse;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeDetailClickHouseVo;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeDetailOldNewVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirunscm.scm.clickhouse.entity.datachange.SLogDataChangeDetailClickHouseEntity;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseConnectionException;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseQueryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
 * 数据变更详细日志 ClickHouse Repository
 * 专门处理 s_log_data_change_detail 表的所有操作
 * 
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-09-19 - 数据变更日志架构实现
 */
@Slf4j
@Repository
public class SLogDataChangeDetailClickHouseRepository {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String TABLE_NAME = "s_log_data_change_detail";

    private final Client clickHouseClient;


    public SLogDataChangeDetailClickHouseRepository(@Qualifier("clickHouseClient") Client clickHouseClient) {
        this.clickHouseClient = clickHouseClient;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入单条数据变更详细日志
     */
    public void insert(SLogDataChangeDetailClickHouseEntity detailLogEntity) {
        long startTime = System.currentTimeMillis();
        try {
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行POJO自动序列化插入
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, List.of(detailLogEntity), insertSettings)
                    .get(30, TimeUnit.SECONDS)) {
                
                log.debug("插入数据变更详细日志成功，request_id: {}", detailLogEntity.getRequest_id());

            } catch (Exception e) {
                handleInsertError(e, "插入数据变更详细日志失败", "insert_data_change_detail");
            }
            
        } catch (Exception e) {
            handleRepositoryError(e, "插入数据变更详细日志失败");
        }
    }

    /**
     * 批量插入数据变更详细日志 - 性能最优
     */
    public void batchInsert(List<SLogDataChangeDetailClickHouseEntity> detailLogEntities) {
        if (detailLogEntities == null || detailLogEntities.isEmpty()) {
            log.warn("批量插入数据变更详细日志数据为空，跳过操作");
            return;
        }

        long startTime = System.currentTimeMillis();
        int recordCount = detailLogEntities.size();
        
        try {
            // 设置创建时间
            LocalDateTime now = LocalDateTime.now();
            for (SLogDataChangeDetailClickHouseEntity entity : detailLogEntities) {
                if (entity.getC_time() == null) {
                    entity.setC_time(now);
                }
            }
            
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行批量插入
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, detailLogEntities, insertSettings)
                    .get(60, TimeUnit.SECONDS)) {
                
                log.info("批量插入数据变更详细日志成功，数量: {}", recordCount);

            } catch (Exception e) {
                handleInsertError(e, "批量插入数据变更详细日志失败", "batch_insert_data_change_detail");
            }
            
        } catch (Exception e) {
            handleRepositoryError(e, "批量插入数据变更详细日志失败");
        }
    }

    // ==================== 参数化分页查询操作 ====================

    /**
     * 参数化分页查询数据变更详细日志 - 使用 ClickHouse Client v2 参数化查询
     * 支持业务名、数据操作类型、SQL命令类型、表名、实体类名、单号、类名条件查询，支持分页和排序
     */
    public IPage<SLogDataChangeDetailClickHouseVo> selectPageWithParams(SLogDataChangeDetailClickHouseVo searchCondition) {
        try {
            PageCondition pageCondition = searchCondition.getPageCondition();
            long current = pageCondition != null ? pageCondition.getCurrent() : 1;
            long size = pageCondition != null ? pageCondition.getSize() : 10;
            String sort = pageCondition != null ? pageCondition.getSort() : null;
            
            // 1. 构建参数化查询条件
            StringBuilder whereClause = new StringBuilder("WHERE 1=1");
            Map<String, Object> queryParams = new HashMap<>();
            
            if (StringUtils.isNotBlank(searchCondition.getName())) {
                whereClause.append(" AND name LIKE {name:String}");
                queryParams.put("name", "%" + searchCondition.getName() + "%");
            }
            
            if (StringUtils.isNotBlank(searchCondition.getType())) {
                whereClause.append(" AND type = {type:String}");
                queryParams.put("type", searchCondition.getType());
            }
            
            if (StringUtils.isNotBlank(searchCondition.getSql_command_type())) {
                whereClause.append(" AND sql_command_type = {sqlCommandType:String}");
                queryParams.put("sqlCommandType", searchCondition.getSql_command_type());
            }
            
            if (StringUtils.isNotBlank(searchCondition.getTable_name())) {
                whereClause.append(" AND table_name LIKE {tableName:String}");
                queryParams.put("tableName", "%" + searchCondition.getTable_name() + "%");
            }
            
            if (StringUtils.isNotBlank(searchCondition.getEntity_name())) {
                whereClause.append(" AND entity_name LIKE {entityName:String}");
                queryParams.put("entityName", "%" + searchCondition.getEntity_name() + "%");
            }
            
            if (StringUtils.isNotBlank(searchCondition.getOrder_code())) {
                whereClause.append(" AND order_code LIKE {orderCode:String}");
                queryParams.put("orderCode", "%" + searchCondition.getOrder_code() + "%");
            }
            
            // 类名查询条件
            if (StringUtils.isNotBlank(searchCondition.getClass_name())) {
                whereClause.append(" AND class_name LIKE {className:String}");
                queryParams.put("className", "%" + searchCondition.getClass_name() + "%");
            }
            
            // 表ID查询条件
            if (searchCondition.getTable_id() != null) {
                whereClause.append(" AND table_id = {tableId:UInt32}");
                queryParams.put("tableId", searchCondition.getTable_id());
            }
            
            // 创建人ID查询条件
            if (searchCondition.getC_id() != null) {
                whereClause.append(" AND c_id = {cId:UInt64}");
                queryParams.put("cId", searchCondition.getC_id());
            }
            
            // 修改人ID查询条件
            if (searchCondition.getU_id() != null) {
                whereClause.append(" AND u_id = {uId:UInt64}");
                queryParams.put("uId", searchCondition.getU_id());
            }
            
            // 创建人名称查询条件
            if (StringUtils.isNotBlank(searchCondition.getC_name())) {
                whereClause.append(" AND c_name LIKE {cName:String}");
                queryParams.put("cName", "%" + searchCondition.getC_name() + "%");
            }
            
            // 修改人名称查询条件
            if (StringUtils.isNotBlank(searchCondition.getU_name())) {
                whereClause.append(" AND u_name LIKE {uName:String}");
                queryParams.put("uName", "%" + searchCondition.getU_name() + "%");
            }

            // 时间范围查询 - 使用字符串格式避免DateTime解析错误
            if (searchCondition.getStart_time() != null) {
                whereClause.append(" AND c_time >= {startTime:String}");
                queryParams.put("startTime", searchCondition.getStart_time().format(DATETIME_FORMATTER));
            }

            if (searchCondition.getOver_time() != null) {
                whereClause.append(" AND c_time <= {endTime:String}");
                queryParams.put("endTime", searchCondition.getOver_time().format(DATETIME_FORMATTER));
            }

            // 租户代码条件 - 必须条件，确保数据隔离
            if (StringUtils.isNotBlank(searchCondition.getTenant_code())) {
                whereClause.append(" AND tenant_code = {tenantCode:String}");
                queryParams.put("tenantCode", searchCondition.getTenant_code());
            }
            
            // 2. 执行 COUNT 查询获取总数
            String countSql = "SELECT count(*) as total FROM s_log_data_change_detail " + whereClause;
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
                SELECT id, name, type, sql_command_type, table_name, entity_name, 
                       order_code, class_name, details, table_id, c_id, u_id, 
                       c_time, u_time, c_name, u_name, request_id, tenant_code
                FROM s_log_data_change_detail 
                """ + whereClause + " " + orderClause + """
                
                LIMIT {limit:UInt32} OFFSET {offset:UInt32}
                """;
            
            List<SLogDataChangeDetailClickHouseEntity> entities = new ArrayList<>();
            try (QueryResponse dataResponse = clickHouseClient.query(dataSql, queryParams).get()) {
                ClickHouseBinaryFormatReader reader = clickHouseClient.newBinaryFormatReader(dataResponse);
                while (reader.hasNext()) {
                    reader.next();
                    entities.add(mapReaderToEntity(reader));
                }
            }
            
            // 5. 转换 Entity 为 VO 并构建 IPage 结果
            List<SLogDataChangeDetailClickHouseVo> records = entities.stream()
                .map(this::convertEntityToVo)
                .collect(Collectors.toList());
            
            IPage<SLogDataChangeDetailClickHouseVo> page = new Page<>(current, size, total);
            page.setRecords(records);
            
            log.info("参数化分页查询数据变更详细日志成功，当前页: {}, 页大小: {}, 总数: {}, 结果数量: {}", 
                    current, size, total, records.size());
            
            return page;
            
        } catch (Exception e) {
            log.error("参数化分页查询数据变更详细日志失败", e);
            throw new ClickHouseQueryException("参数化分页查询数据变更详细日志失败", "", e);
        }
    }

    /**
     * 构建安全的排序子句
     */
    private String buildOrderByClause(String sort) {
        if (StringUtils.isBlank(sort)) {
            return "ORDER BY c_time DESC"; // 默认按创建时间倒序
        }
        
        // 安全的排序字段白名单
        Map<String, String> sortFieldMap = Map.ofEntries(
            Map.entry("name", "name"),
            Map.entry("type", "type"),
            Map.entry("sql_command_type", "sql_command_type"),
            Map.entry("table_name", "table_name"),
            Map.entry("entity_name", "entity_name"),
            Map.entry("order_code", "order_code"),
            Map.entry("class_name", "class_name"),
            Map.entry("c_time", "c_time"),
            Map.entry("u_time", "u_time"),
            Map.entry("c_name", "c_name"),
            Map.entry("u_name", "u_name")
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
        return "ORDER BY c_time DESC";
    }

    /**
     * 转换 Entity 为 VO
     */
    private SLogDataChangeDetailClickHouseVo convertEntityToVo(SLogDataChangeDetailClickHouseEntity entity) {
        SLogDataChangeDetailClickHouseVo vo = (SLogDataChangeDetailClickHouseVo) BeanUtilsSupport.copyProperties(entity, SLogDataChangeDetailClickHouseVo.class);
        
        // 特殊处理：JSON String → List<SLogDataChangeDetailOldNewVo> 转换
        if (StringUtils.isNotBlank(entity.getDetails())) {
            try {
                List<SLogDataChangeDetailOldNewVo> detailsList = JSON.parseArray(
                    entity.getDetails(), 
                    SLogDataChangeDetailOldNewVo.class
                );
                vo.setDetails(detailsList);
            } catch (Exception e) {
                log.warn("解析details JSON失败，ID: {}, JSON内容: {}", entity.getId(), entity.getDetails(), e);
                vo.setDetails(new ArrayList<>());  // 设置空List作为兜底
            }
        } else {
            vo.setDetails(new ArrayList<>());  // 设置空List
        }
        
        return vo;
    }

    // ==================== 根据订单编码查询操作 ====================

    /**
     * 根据订单编码查询所有相关的数据变更记录
     * 对应MongoDB版本的mongoTemplate.find查询逻辑
     * 支持租户隔离，按更新时间降序排序
     * 
     * @param orderCode 订单编码
     * @param tenantCode 租户代码
     * @return 数据变更记录列表，按更新时间降序排序
     */
    public List<SLogDataChangeDetailClickHouseVo> findByOrderCode(String orderCode, String tenantCode) {
        if (StringUtils.isBlank(orderCode)) {
            log.warn("findByOrderCode查询参数orderCode为空，返回空列表");
            return new ArrayList<>();
        }

        long startTime = System.currentTimeMillis();
        
        String sql = """
            SELECT 
                id, name, type, sql_command_type, table_name, entity_name,
                order_code, class_name, details, table_id, c_id, u_id,
                c_time, u_time, c_name, u_name, request_id, tenant_code
            FROM s_log_data_change_detail 
            WHERE order_code = {orderCode:String}
            AND tenant_code = {tenantCode:String}
            ORDER BY u_time DESC
            """;

        Map<String, Object> queryParams = Map.of(
            "orderCode", orderCode.trim(),
            "tenantCode", tenantCode != null ? tenantCode : ""
        );

        try {
            List<SLogDataChangeDetailClickHouseEntity> entities = new ArrayList<>();
            
            // 使用流式查询处理结果集
            try (QueryResponse response = clickHouseClient.query(sql, queryParams).get(30, TimeUnit.SECONDS)) {
                ClickHouseBinaryFormatReader reader = clickHouseClient.newBinaryFormatReader(response);
                while (reader.hasNext()) {
                    reader.next();
                    entities.add(mapReaderToEntity(reader));
                }
            }
            
            // 转换为VO对象列表（利用现有的convertEntityToVo方法）
            List<SLogDataChangeDetailClickHouseVo> voList = entities.stream()
                .map(this::convertEntityToVo)
                .collect(Collectors.toList());
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("根据订单编码查询数据变更记录成功，订单: {}, 租户: {}, 记录数: {}, 耗时: {}ms", 
                    orderCode, tenantCode, voList.size(), duration);
            
            return voList;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("根据订单编码查询数据变更记录失败，订单: {}, 租户: {}, 耗时: {}ms", 
                    orderCode, tenantCode, duration, e);
            
            
            throw new ClickHouseQueryException("根据订单编码查询数据变更记录失败", sql, e);
        }
    }

    /**
     * 根据请求ID模糊查询数据变更记录
     * 对应MongoDB版本的criteria.and("request_id").regex(regexPattern(request_id))逻辑
     * 使用LIKE模式匹配替代MongoDB的正则表达式查询
     * 支持租户隔离，按更新时间降序排序
     * 
     * @param requestId 请求ID（支持模糊匹配）
     * @param tenantCode 租户代码
     * @return 匹配的数据变更记录列表，按更新时间降序排序
     */
    public List<SLogDataChangeDetailClickHouseVo> findByRequestIdLike(String requestId, String tenantCode) {
        if (StringUtils.isBlank(requestId)) {
            log.warn("findByRequestIdLike查询参数requestId为空，返回空列表");
            return new ArrayList<>();
        }

        long startTime = System.currentTimeMillis();
        
        String sql = """
            SELECT 
                id, name, type, sql_command_type, table_name, entity_name,
                order_code, class_name, details, table_id, c_id, u_id,
                c_time, u_time, c_name, u_name, request_id, tenant_code
            FROM s_log_data_change_detail 
            WHERE request_id LIKE {requestIdPattern:String}
            AND tenant_code = {tenantCode:String}
            ORDER BY u_time DESC
            """;

        // 模糊匹配模式：%requestId% 对应MongoDB的regex模糊匹配
        Map<String, Object> queryParams = Map.of(
            "requestIdPattern", "%" + requestId.trim() + "%",
            "tenantCode", tenantCode != null ? tenantCode : ""
        );

        try {
            List<SLogDataChangeDetailClickHouseEntity> entities = new ArrayList<>();
            
            // 使用流式查询处理结果集
            try (QueryResponse response = clickHouseClient.query(sql, queryParams).get(30, TimeUnit.SECONDS)) {
                ClickHouseBinaryFormatReader reader = clickHouseClient.newBinaryFormatReader(response);
                while (reader.hasNext()) {
                    reader.next();
                    entities.add(mapReaderToEntity(reader));
                }
            }
            
            // 转换为VO对象列表（利用现有的convertEntityToVo方法）
            List<SLogDataChangeDetailClickHouseVo> voList = entities.stream()
                .map(this::convertEntityToVo)
                .collect(Collectors.toList());
            
            long duration = System.currentTimeMillis() - startTime;
            log.info("根据请求ID模糊查询数据变更记录成功，请求ID: {}, 租户: {}, 记录数: {}, 耗时: {}ms", 
                    requestId, tenantCode, voList.size(), duration);
            
            return voList;
            
        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            log.error("根据请求ID模糊查询数据变更记录失败，请求ID: {}, 租户: {}, 耗时: {}ms", 
                    requestId, tenantCode, duration, e);
            
            
            throw new ClickHouseQueryException("根据请求ID模糊查询数据变更记录失败", sql, e);
        }
    }

    // ==================== 单条记录查询操作 ====================

    /**
     * 根据ID查询单条数据变更详细日志记录
     * 使用ClickHouse Client v2参数化查询防止SQL注入
     * 支持租户代码过滤，确保数据隔离
     * 
     * @param searchCondition 查询条件（包含ID和tenant_code）
     * @return 找到的VO对象，未找到返回null
     */
    public SLogDataChangeDetailClickHouseVo getById(SLogDataChangeDetailClickHouseVo searchCondition) {
        if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
            log.warn("getById查询参数为空或ID为空，返回null");
            return null;
        }

        String sql = """
            SELECT 
                id, name, type, sql_command_type, table_name, entity_name,
                order_code, class_name, details, table_id, c_id, u_id,
                c_time, u_time, c_name, u_name, request_id, tenant_code
            FROM s_log_data_change_detail 
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
                SLogDataChangeDetailClickHouseEntity entity = new SLogDataChangeDetailClickHouseEntity();
                entity.setId(record.getString("id"));
                entity.setName(record.getString("name"));
                entity.setType(record.getString("type"));
                entity.setSql_command_type(record.getString("sql_command_type"));
                entity.setTable_name(record.getString("table_name"));
                entity.setEntity_name(record.getString("entity_name"));
                entity.setOrder_code(record.getString("order_code"));
                entity.setClass_name(record.getString("class_name"));
                entity.setDetails(record.getString("details"));
                
                // 数值类型字段空值处理
                try {
                    Integer tableId = record.getInteger("table_id");
                    entity.setTable_id(tableId);
                } catch (Exception e) {
                    entity.setTable_id(null);
                    log.debug("table_id字段为空或读取失败，设为null，ID: {}", record.getString("id"));
                }
                
                try {
                    Long cId = record.getLong("c_id");
                    entity.setC_id(cId);
                } catch (Exception e) {
                    entity.setC_id(null);
                    log.debug("c_id字段为空或读取失败，设为null，ID: {}", record.getString("id"));
                }
                
                try {
                    Long uId = record.getLong("u_id");
                    entity.setU_id(uId);
                } catch (Exception e) {
                    entity.setU_id(null);
                    log.debug("u_id字段为空或读取失败，设为null，ID: {}", record.getString("id"));
                }
                
                entity.setC_time(record.getLocalDateTime("c_time"));
                entity.setU_time(record.getLocalDateTime("u_time"));
                entity.setC_name(record.getString("c_name"));
                entity.setU_name(record.getString("u_name"));
                entity.setRequest_id(record.getString("request_id"));
                entity.setTenant_code(record.getString("tenant_code"));

                // 转换为VO并返回
                SLogDataChangeDetailClickHouseVo vo = convertEntityToVo(entity);
                log.info("根据ID查询数据变更详细日志成功，ID: {}, 租户: {}, 表名: {}", id, tenantCode, vo.getTable_name());
                return vo;
            }
            
            log.info("根据ID查询数据变更详细日志未找到记录，ID: {}, 租户: {}", id, tenantCode);
            return null;
            
        } catch (Exception e) {
            log.error("根据ID查询数据变更详细日志失败，ID: {}, 租户: {}", id, tenantCode, e);
            throw new ClickHouseQueryException("根据ID查询数据变更详细日志失败", sql, e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 将ClickHouseBinaryFormatReader映射到实体对象 - 用于流式读取
     */
    private SLogDataChangeDetailClickHouseEntity mapReaderToEntity(ClickHouseBinaryFormatReader reader) {
        SLogDataChangeDetailClickHouseEntity entity = new SLogDataChangeDetailClickHouseEntity();
        entity.setId(reader.getString("id"));
        entity.setName(reader.getString("name"));
        entity.setType(reader.getString("type"));
        entity.setSql_command_type(reader.getString("sql_command_type"));
        entity.setTable_name(reader.getString("table_name"));
        entity.setEntity_name(reader.getString("entity_name"));
        entity.setOrder_code(reader.getString("order_code"));
        entity.setClass_name(reader.getString("class_name"));
        entity.setDetails(reader.getString("details"));
        
        // 数值类型字段空值处理
        try {
            Integer tableId = reader.getInteger("table_id");
            entity.setTable_id(tableId);
        } catch (Exception e) {
            entity.setTable_id(null);
            log.debug("table_id字段为空或读取失败，设为null，ID: {}", reader.getString("id"));
        }
        
        try {
            Long cId = reader.getLong("c_id");
            entity.setC_id(cId);
        } catch (Exception e) {
            entity.setC_id(null);
            log.debug("c_id字段为空或读取失败，设为null，ID: {}", reader.getString("id"));
        }
        
        try {
            Long uId = reader.getLong("u_id");
            entity.setU_id(uId);
        } catch (Exception e) {
            entity.setU_id(null);
            log.debug("u_id字段为空或读取失败，设为null，ID: {}", reader.getString("id"));
        }
        
        entity.setC_time(reader.getLocalDateTime("c_time"));
        entity.setU_time(reader.getLocalDateTime("u_time"));
        entity.setC_name(reader.getString("c_name"));
        entity.setU_name(reader.getString("u_name"));
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