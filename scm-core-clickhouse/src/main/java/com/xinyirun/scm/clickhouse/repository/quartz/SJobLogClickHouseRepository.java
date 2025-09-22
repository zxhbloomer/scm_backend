package com.xinyirun.scm.clickhouse.repository.quartz;

import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.data_formats.ClickHouseBinaryFormatReader;
import com.clickhouse.client.api.insert.InsertResponse;
import com.clickhouse.client.api.insert.InsertSettings;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clickhouse.client.api.query.GenericRecord;
import com.clickhouse.client.api.query.QueryResponse;
import com.xinyirun.scm.bean.system.vo.clickhouse.log.quartz.SJobLogClickHouseVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
import com.xinyirun.scm.clickhouse.entity.quartz.SJobLogClickHouseEntity;
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
 * 定时任务调度日志 ClickHouse Repository
 * 专门处理 s_job_log 表的所有操作
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Slf4j
@Repository
public class SJobLogClickHouseRepository {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String TABLE_NAME = "s_job_log";

    private final Client clickHouseClient;

    public SJobLogClickHouseRepository(@Qualifier("clickHouseClient") Client clickHouseClient) {
        this.clickHouseClient = clickHouseClient;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入单条定时任务日志
     */
    public void insert(SJobLogClickHouseEntity jobLogEntity) {
        long startTime = System.currentTimeMillis();
        try {
            InsertSettings insertSettings = new InsertSettings();
            
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, List.of(jobLogEntity), insertSettings)
                    .get(30, TimeUnit.SECONDS)) {
                
                log.debug("插入定时任务日志成功，job_id: {}", jobLogEntity.getJob_id());
                
            } catch (Exception e) {
                handleInsertError(e, "插入定时任务日志失败", "insert_job_log");
            }
            
        } catch (Exception e) {
            handleRepositoryError(e, "插入定时任务日志失败");
        }
    }

    /**
     * 批量插入定时任务日志 - 性能最优
     */
    public void batchInsert(List<SJobLogClickHouseEntity> jobLogEntities) {
        if (jobLogEntities == null || jobLogEntities.isEmpty()) {
            log.warn("批量插入定时任务日志数据为空，跳过操作");
            return;
        }

        long startTime = System.currentTimeMillis();
        int recordCount = jobLogEntities.size();
        
        try {
            LocalDateTime now = LocalDateTime.now();
            for (SJobLogClickHouseEntity entity : jobLogEntities) {
                if (entity.getC_time() == null) {
                    entity.setC_time(now);
                }
            }
            
            InsertSettings insertSettings = new InsertSettings();
            
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, jobLogEntities, insertSettings)
                    .get(60, TimeUnit.SECONDS)) {
                
                log.info("批量插入定时任务日志成功，数量: {}", recordCount);
                
            } catch (Exception e) {
                handleInsertError(e, "批量插入定时任务日志失败", "batch_insert_job_log");
            }
            
        } catch (Exception e) {
            handleRepositoryError(e, "批量插入定时任务日志失败");
        }
    }

    // ==================== 参数化分页查询操作 ====================

    /**
     * 参数化分页查询定时任务日志 - 使用 ClickHouse Client v2 参数化查询
     * 支持任务名称、任务组类型、类名、方法名等条件查询，支持分页和排序
     */
    public IPage<SJobLogClickHouseVo> selectPageWithParams(SJobLogClickHouseVo searchCondition) {
        try {
            PageCondition pageCondition = searchCondition.getPageCondition();
            long current = pageCondition != null ? pageCondition.getCurrent() : 1;
            long size = pageCondition != null ? pageCondition.getSize() : 10;
            String sort = pageCondition != null ? pageCondition.getSort() : null;
            
            // 构建参数化查询条件
            StringBuilder whereClause = new StringBuilder("WHERE 1=1");
            Map<String, Object> queryParams = new HashMap<>();
            
            if (searchCondition.getJob_id() != null) {
                whereClause.append(" AND job_id = {jobId:UInt64}");
                queryParams.put("jobId", searchCondition.getJob_id());
            }
            
            if (StringUtils.isNotBlank(searchCondition.getJob_name())) {
                whereClause.append(" AND job_name LIKE {jobName:String}");
                queryParams.put("jobName", "%" + searchCondition.getJob_name() + "%");
            }
            
            if (StringUtils.isNotBlank(searchCondition.getJob_group_type())) {
                whereClause.append(" AND job_group_type = {jobGroupType:String}");
                queryParams.put("jobGroupType", searchCondition.getJob_group_type());
            }
            
            if (StringUtils.isNotBlank(searchCondition.getClass_name())) {
                whereClause.append(" AND class_name LIKE {className:String}");
                queryParams.put("className", "%" + searchCondition.getClass_name() + "%");
            }
            
            if (StringUtils.isNotBlank(searchCondition.getMethod_name())) {
                whereClause.append(" AND method_name LIKE {methodName:String}");
                queryParams.put("methodName", "%" + searchCondition.getMethod_name() + "%");
            }
            
            if (searchCondition.getIs_del() != null) {
                whereClause.append(" AND is_del = {isDel:UInt8}");
                queryParams.put("isDel", searchCondition.getIs_del() ? 1 : 0);
            }

            // 时间范围查询
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
            
            // 执行 COUNT 查询获取总数
            String countSql = "SELECT count(*) as total FROM s_job_log " + whereClause;
            long total = 0;
            List<GenericRecord> countRecords = clickHouseClient.queryAll(countSql, queryParams);
            if (!countRecords.isEmpty()) {
                total = countRecords.get(0).getLong("total");
            }
            
            // 构建排序子句
            String orderClause = buildOrderByClause(sort);
            
            // 执行分页数据查询 - 使用参数化查询
            queryParams.put("offset", (current - 1) * size);
            queryParams.put("limit", size);
            
            String dataSql = """
                SELECT id, job_id, job_name, job_group_type, job_serial_id, job_serial_type,
                       job_desc, job_simple_name, class_name, method_name, param_class, param_data,
                       cron_expression, concurrent, is_cron, misfire_policy, is_del, is_effected,
                       fire_time, scheduled_fire_time, prev_fire_time, next_fire_time, run_times,
                       msg, c_id, c_name, c_time, u_id, u_time, tenant_code
                FROM s_job_log 
                """ + whereClause + " " + orderClause + """
                
                LIMIT {limit:UInt32} OFFSET {offset:UInt32}
                """;
            
            List<SJobLogClickHouseEntity> entities = new ArrayList<>();
            try (QueryResponse dataResponse = clickHouseClient.query(dataSql, queryParams).get()) {
                ClickHouseBinaryFormatReader reader = clickHouseClient.newBinaryFormatReader(dataResponse);
                while (reader.hasNext()) {
                    reader.next();
                    entities.add(mapReaderToEntity(reader));
                }
            }
            
            // 转换 Entity 为 VO 并构建 IPage 结果
            List<SJobLogClickHouseVo> records = entities.stream()
                .map(this::convertEntityToVo)
                .collect(Collectors.toList());
            
            IPage<SJobLogClickHouseVo> page = new Page<>(current, size, total);
            page.setRecords(records);
            
            log.info("参数化分页查询定时任务日志成功，当前页: {}, 页大小: {}, 总数: {}, 结果数量: {}", 
                    current, size, total, records.size());
            
            return page;
            
        } catch (Exception e) {
            log.error("参数化分页查询定时任务日志失败", e);
            throw new ClickHouseQueryException("参数化分页查询定时任务日志失败", "", e);
        }
    }

    /**
     * 构建安全的排序子句
     */
    private String buildOrderByClause(String sort) {
        if (StringUtils.isBlank(sort)) {
            return "ORDER BY c_time DESC";
        }
        
        // 安全的排序字段白名单
        Map<String, String> sortFieldMap = Map.of(
            "job_name", "job_name",
            "job_group_type", "job_group_type", 
            "class_name", "class_name",
            "method_name", "method_name",
            "c_time", "c_time",
            "fire_time", "fire_time",
            "run_times", "run_times"
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
        
        return "ORDER BY c_time DESC";
    }

    /**
     * 转换 Entity 为 VO
     */
    private SJobLogClickHouseVo convertEntityToVo(SJobLogClickHouseEntity entity) {
        return (SJobLogClickHouseVo) BeanUtilsSupport.copyProperties(entity, SJobLogClickHouseVo.class);
    }

    // ==================== 单条记录查询操作 ====================

    /**
     * 根据ID查询单条定时任务日志记录
     */
    public SJobLogClickHouseVo getById(SJobLogClickHouseVo searchCondition) {
        if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
            log.warn("getById查询参数为空或ID为空，返回null");
            return null;
        }

        String sql = """
            SELECT 
                id, job_id, job_name, job_group_type, job_serial_id, job_serial_type,
                job_desc, job_simple_name, class_name, method_name, param_class, param_data,
                cron_expression, concurrent, is_cron, misfire_policy, is_del, is_effected,
                fire_time, scheduled_fire_time, prev_fire_time, next_fire_time, run_times,
                msg, c_id, c_name, c_time, u_id, u_time, tenant_code
            FROM s_job_log 
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
                SJobLogClickHouseEntity entity = mapRecordToEntity(record);
                SJobLogClickHouseVo vo = convertEntityToVo(entity);
                log.info("根据ID查询定时任务日志成功，ID: {}, 租户: {}, 任务名: {}", id, tenantCode, vo.getJob_name());
                return vo;
            }
            
            log.info("根据ID查询定时任务日志未找到记录，ID: {}, 租户: {}", id, tenantCode);
            return null;
            
        } catch (Exception e) {
            log.error("根据ID查询定时任务日志失败，ID: {}, 租户: {}", id, tenantCode, e);
            throw new ClickHouseQueryException("根据ID查询定时任务日志失败", sql, e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 将ClickHouseBinaryFormatReader映射到实体对象 - 用于流式读取
     */
    private SJobLogClickHouseEntity mapReaderToEntity(ClickHouseBinaryFormatReader reader) {
        SJobLogClickHouseEntity entity = new SJobLogClickHouseEntity();
        entity.setId(reader.getString("id"));
        
        try {
            Long jobId = reader.getLong("job_id");
            entity.setJob_id(jobId);
        } catch (Exception e) {
            entity.setJob_id(null);
        }
        
        entity.setJob_name(reader.getString("job_name"));
        entity.setJob_group_type(reader.getString("job_group_type"));
        
        try {
            Long jobSerialId = reader.getLong("job_serial_id");
            entity.setJob_serial_id(jobSerialId);
        } catch (Exception e) {
            entity.setJob_serial_id(null);
        }
        
        entity.setJob_serial_type(reader.getString("job_serial_type"));
        entity.setJob_desc(reader.getString("job_desc"));
        entity.setJob_simple_name(reader.getString("job_simple_name"));
        entity.setClass_name(reader.getString("class_name"));
        entity.setMethod_name(reader.getString("method_name"));
        entity.setParam_class(reader.getString("param_class"));
        entity.setParam_data(reader.getString("param_data"));
        entity.setCron_expression(reader.getString("cron_expression"));
        
        try {
            Boolean concurrent = reader.getBoolean("concurrent");
            entity.setConcurrent(concurrent);
        } catch (Exception e) {
            entity.setConcurrent(null);
        }
        
        try {
            Boolean isCron = reader.getBoolean("is_cron");
            entity.setIs_cron(isCron);
        } catch (Exception e) {
            entity.setIs_cron(null);
        }
        
        entity.setMisfire_policy(reader.getString("misfire_policy"));
        
        try {
            Boolean isDel = reader.getBoolean("is_del");
            entity.setIs_del(isDel);
        } catch (Exception e) {
            entity.setIs_del(false);
        }
        
        try {
            Boolean isEffected = reader.getBoolean("is_effected");
            entity.setIs_effected(isEffected);
        } catch (Exception e) {
            entity.setIs_effected(null);
        }
        
        entity.setFire_time(reader.getLocalDateTime("fire_time"));
        entity.setScheduled_fire_time(reader.getLocalDateTime("scheduled_fire_time"));
        entity.setPrev_fire_time(reader.getLocalDateTime("prev_fire_time"));
        entity.setNext_fire_time(reader.getLocalDateTime("next_fire_time"));
        
        try {
            Integer runTimes = reader.getInteger("run_times");
            entity.setRun_times(runTimes);
        } catch (Exception e) {
            entity.setRun_times(null);
        }
        
        entity.setMsg(reader.getString("msg"));
        
        try {
            Long cId = reader.getLong("c_id");
            entity.setC_id(cId);
        } catch (Exception e) {
            entity.setC_id(null);
        }
        
        entity.setC_name(reader.getString("c_name"));
        entity.setC_time(reader.getLocalDateTime("c_time"));
        
        try {
            Long uId = reader.getLong("u_id");
            entity.setU_id(uId);
        } catch (Exception e) {
            entity.setU_id(null);
        }
        
        entity.setU_time(reader.getLocalDateTime("u_time"));
        entity.setTenant_code(reader.getString("tenant_code"));
        
        return entity;
    }

    /**
     * 将GenericRecord映射到实体对象 - 用于简单查询
     */
    private SJobLogClickHouseEntity mapRecordToEntity(GenericRecord record) {
        SJobLogClickHouseEntity entity = new SJobLogClickHouseEntity();
        entity.setId(record.getString("id"));
        
        try {
            Long jobId = record.getLong("job_id");
            entity.setJob_id(jobId);
        } catch (Exception e) {
            entity.setJob_id(null);
        }
        
        entity.setJob_name(record.getString("job_name"));
        entity.setJob_group_type(record.getString("job_group_type"));
        
        try {
            Long jobSerialId = record.getLong("job_serial_id");
            entity.setJob_serial_id(jobSerialId);
        } catch (Exception e) {
            entity.setJob_serial_id(null);
        }
        
        entity.setJob_serial_type(record.getString("job_serial_type"));
        entity.setJob_desc(record.getString("job_desc"));
        entity.setJob_simple_name(record.getString("job_simple_name"));
        entity.setClass_name(record.getString("class_name"));
        entity.setMethod_name(record.getString("method_name"));
        entity.setParam_class(record.getString("param_class"));
        entity.setParam_data(record.getString("param_data"));
        entity.setCron_expression(record.getString("cron_expression"));
        
        try {
            Boolean concurrent = record.getBoolean("concurrent");
            entity.setConcurrent(concurrent);
        } catch (Exception e) {
            entity.setConcurrent(null);
        }
        
        try {
            Boolean isCron = record.getBoolean("is_cron");
            entity.setIs_cron(isCron);
        } catch (Exception e) {
            entity.setIs_cron(null);
        }
        
        entity.setMisfire_policy(record.getString("misfire_policy"));
        
        try {
            Boolean isDel = record.getBoolean("is_del");
            entity.setIs_del(isDel);
        } catch (Exception e) {
            entity.setIs_del(false);
        }
        
        try {
            Boolean isEffected = record.getBoolean("is_effected");
            entity.setIs_effected(isEffected);
        } catch (Exception e) {
            entity.setIs_effected(null);
        }
        
        entity.setFire_time(record.getLocalDateTime("fire_time"));
        entity.setScheduled_fire_time(record.getLocalDateTime("scheduled_fire_time"));
        entity.setPrev_fire_time(record.getLocalDateTime("prev_fire_time"));
        entity.setNext_fire_time(record.getLocalDateTime("next_fire_time"));
        
        try {
            Integer runTimes = record.getInteger("run_times");
            entity.setRun_times(runTimes);
        } catch (Exception e) {
            entity.setRun_times(null);
        }
        
        entity.setMsg(record.getString("msg"));
        
        try {
            Long cId = record.getLong("c_id");
            entity.setC_id(cId);
        } catch (Exception e) {
            entity.setC_id(null);
        }
        
        entity.setC_name(record.getString("c_name"));
        entity.setC_time(record.getLocalDateTime("c_time"));
        
        try {
            Long uId = record.getLong("u_id");
            entity.setU_id(uId);
        } catch (Exception e) {
            entity.setU_id(null);
        }
        
        entity.setU_time(record.getLocalDateTime("u_time"));
        entity.setTenant_code(record.getString("tenant_code"));
        
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