package com.xinyirunscm.scm.clickhouse.repository.datachange;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.data_formats.ClickHouseBinaryFormatReader;
import com.clickhouse.client.api.insert.InsertResponse;
import com.clickhouse.client.api.insert.InsertSettings;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clickhouse.client.api.query.GenericRecord;
import com.clickhouse.client.api.query.QueryResponse;
import com.xinyirun.scm.bean.system.vo.clickhouse.datachange.SLogDataChangeMainClickHouseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirunscm.scm.clickhouse.entity.datachange.SLogDataChangeMainClickHouseEntity;
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
 * 数据变更主日志 ClickHouse Repository
 * 专门处理 s_log_data_change_main 表的所有操作
 * 
 * @author SCM System
 * @since 1.0.39
 * @updated 2025-09-19 - 数据变更日志架构实现
 */
@Slf4j
@Repository
public class SLogDataChangeMainClickHouseRepository {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String TABLE_NAME = "s_log_data_change_main";

    private final Client clickHouseClient;

    

    public SLogDataChangeMainClickHouseRepository(@Qualifier("clickHouseClient") Client clickHouseClient) {
        this.clickHouseClient = clickHouseClient;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入单条数据变更主日志
     */
    public void insert(SLogDataChangeMainClickHouseEntity mainLogEntity) {
        long startTime = System.currentTimeMillis();
        try {
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行POJO自动序列化插入
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, List.of(mainLogEntity), insertSettings)
                    .get(30, TimeUnit.SECONDS)) {
                
                log.debug("插入数据变更主日志成功，request_id: {}", mainLogEntity.getRequest_id());
                
            } catch (Exception e) {
                handleInsertError(e, "插入数据变更主日志失败", "insert_data_change_main");
            }
            
        } catch (Exception e) {
            handleRepositoryError(e, "插入数据变更主日志失败");
        }
    }

    /**
     * 批量插入数据变更主日志 - 性能最优
     */
    public void batchInsert(List<SLogDataChangeMainClickHouseEntity> mainLogEntities) {
        if (mainLogEntities == null || mainLogEntities.isEmpty()) {
            log.warn("批量插入数据变更主日志数据为空，跳过操作");
            return;
        }

        long startTime = System.currentTimeMillis();
        int recordCount = mainLogEntities.size();
        
        try {
            // 设置创建时间
            LocalDateTime now = LocalDateTime.now();
            for (SLogDataChangeMainClickHouseEntity entity : mainLogEntities) {
                if (entity.getC_time() == null) {
                    entity.setC_time(now);
                }
            }
            
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();
            
            // 执行批量插入
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, mainLogEntities, insertSettings)
                    .get(60, TimeUnit.SECONDS)) {
                
                log.info("批量插入数据变更主日志成功，数量: {}", recordCount);
                
            } catch (Exception e) {
                handleInsertError(e, "批量插入数据变更主日志失败", "batch_insert_data_change_main");
            }
            
        } catch (Exception e) {
            handleRepositoryError(e, "批量插入数据变更主日志失败");
        }
    }

    // ==================== 参数化分页查询操作 ====================

    /**
     * 参数化分页查询数据变更主日志 - 使用 ClickHouse Client v2 参数化查询
     * 支持单号类型、单号、名称条件查询，支持分页和排序
     */
    public IPage<SLogDataChangeMainClickHouseVo> selectPageWithParams(SLogDataChangeMainClickHouseVo searchCondition) {
        try {
            PageCondition pageCondition = searchCondition.getPageCondition();
            long current = pageCondition != null ? pageCondition.getCurrent() : 1;
            long size = pageCondition != null ? pageCondition.getSize() : 10;
            String sort = pageCondition != null ? pageCondition.getSort() : null;
            
            // 1. 构建参数化查询条件
            StringBuilder whereClause = new StringBuilder("WHERE 1=1");
            Map<String, Object> queryParams = new HashMap<>();
            
            if (StringUtils.isNotBlank(searchCondition.getOrder_type())) {
                whereClause.append(" AND order_type = {orderType:String}");
                queryParams.put("orderType", searchCondition.getOrder_type());
            }
            
            if (StringUtils.isNotBlank(searchCondition.getOrder_code())) {
                whereClause.append(" AND order_code LIKE {orderCode:String}");
                queryParams.put("orderCode", "%" + searchCondition.getOrder_code() + "%");
            }
            
            if (StringUtils.isNotBlank(searchCondition.getName())) {
                whereClause.append(" AND name LIKE {name:String}");
                queryParams.put("name", "%" + searchCondition.getName() + "%");
            }
            
            // 更新人名称查询条件
            if (StringUtils.isNotBlank(searchCondition.getU_name())) {
                whereClause.append(" AND u_name LIKE {uName:String}");
                queryParams.put("uName", "%" + searchCondition.getU_name() + "%");
            }
            
            // 更新人ID查询条件
            if (StringUtils.isNotBlank(searchCondition.getU_id())) {
                whereClause.append(" AND u_id = {uId:String}");
                queryParams.put("uId", searchCondition.getU_id());
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
            String countSql = "SELECT count(*) as total FROM s_log_data_change_main " + whereClause;
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
                SELECT id, order_type, order_code, name, c_time, u_time, 
                       u_name, u_id, request_id, tenant_code
                FROM s_log_data_change_main 
                """ + whereClause + " " + orderClause + """
                
                LIMIT {limit:UInt32} OFFSET {offset:UInt32}
                """;
            
            List<SLogDataChangeMainClickHouseEntity> entities = new ArrayList<>();
            try (QueryResponse dataResponse = clickHouseClient.query(dataSql, queryParams).get()) {
                ClickHouseBinaryFormatReader reader = clickHouseClient.newBinaryFormatReader(dataResponse);
                while (reader.hasNext()) {
                    reader.next();
                    entities.add(mapReaderToEntity(reader));
                }
            }
            
            // 5. 转换 Entity 为 VO 并构建 IPage 结果
            List<SLogDataChangeMainClickHouseVo> records = entities.stream()
                .map(this::convertEntityToVo)
                .collect(Collectors.toList());
            
            IPage<SLogDataChangeMainClickHouseVo> page = new Page<>(current, size, total);
            page.setRecords(records);
            
            log.info("参数化分页查询数据变更主日志成功，当前页: {}, 页大小: {}, 总数: {}, 结果数量: {}", 
                    current, size, total, records.size());
            
            return page;
            
        } catch (Exception e) {
            log.error("参数化分页查询数据变更主日志失败", e);
            throw new ClickHouseQueryException("参数化分页查询数据变更主日志失败", "", e);
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
        Map<String, String> sortFieldMap = Map.of(
            "order_type", "order_type",
            "order_code", "order_code", 
            "name", "name",
            "c_time", "c_time",
            "u_time", "u_time",
            "u_name", "u_name",
            "u_id", "u_id",
            "request_id", "request_id"
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
    private SLogDataChangeMainClickHouseVo convertEntityToVo(SLogDataChangeMainClickHouseEntity entity) {
        return (SLogDataChangeMainClickHouseVo) BeanUtilsSupport.copyProperties(entity, SLogDataChangeMainClickHouseVo.class);
    }

    // ==================== 单条记录查询操作 ====================

    /**
     * 根据ID查询单条数据变更主日志记录
     * 使用ClickHouse Client v2参数化查询防止SQL注入
     * 支持租户代码过滤，确保数据隔离
     * 
     * @param searchCondition 查询条件（包含ID和tenant_code）
     * @return 找到的VO对象，未找到返回null
     */
    public SLogDataChangeMainClickHouseVo getById(SLogDataChangeMainClickHouseVo searchCondition) {
        if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
            log.warn("getById查询参数为空或ID为空，返回null");
            return null;
        }

        String sql = """
            SELECT 
                id, order_type, order_code, name, c_time, u_time,
                u_name, u_id, request_id, tenant_code
            FROM s_log_data_change_main 
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
                SLogDataChangeMainClickHouseEntity entity = new SLogDataChangeMainClickHouseEntity();
                entity.setId(record.getString("id"));
                entity.setOrder_type(record.getString("order_type"));
                entity.setOrder_code(record.getString("order_code"));
                entity.setName(record.getString("name"));
                entity.setC_time(record.getLocalDateTime("c_time"));
                entity.setU_time(record.getLocalDateTime("u_time"));
                entity.setU_name(record.getString("u_name"));
                entity.setU_id(record.getString("u_id"));
                entity.setRequest_id(record.getString("request_id"));
                entity.setTenant_code(record.getString("tenant_code"));

                // 转换为VO并返回
                SLogDataChangeMainClickHouseVo vo = convertEntityToVo(entity);
                log.info("根据ID查询数据变更主日志成功，ID: {}, 租户: {}, 单号: {}", id, tenantCode, vo.getOrder_code());
                return vo;
            }
            
            log.info("根据ID查询数据变更主日志未找到记录，ID: {}, 租户: {}", id, tenantCode);
            return null;
            
        } catch (Exception e) {
            log.error("根据ID查询数据变更主日志失败，ID: {}, 租户: {}", id, tenantCode, e);
            throw new ClickHouseQueryException("根据ID查询数据变更主日志失败", sql, e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 将ClickHouseBinaryFormatReader映射到实体对象 - 用于流式读取
     */
    private SLogDataChangeMainClickHouseEntity mapReaderToEntity(ClickHouseBinaryFormatReader reader) {
        SLogDataChangeMainClickHouseEntity entity = new SLogDataChangeMainClickHouseEntity();
        entity.setId(reader.getString("id"));
        entity.setOrder_type(reader.getString("order_type"));
        entity.setOrder_code(reader.getString("order_code"));
        entity.setName(reader.getString("name"));
        entity.setC_time(reader.getLocalDateTime("c_time"));
        entity.setU_time(reader.getLocalDateTime("u_time"));
        entity.setU_name(reader.getString("u_name"));
        entity.setU_id(reader.getString("u_id"));
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