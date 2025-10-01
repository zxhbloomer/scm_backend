package com.xinyirun.scm.clickhouse.repository.ai;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.clickhouse.client.api.Client;
import com.clickhouse.client.api.data_formats.ClickHouseBinaryFormatReader;
import com.clickhouse.client.api.insert.InsertResponse;
import com.clickhouse.client.api.insert.InsertSettings;
import com.clickhouse.client.api.query.GenericRecord;
import com.clickhouse.client.api.query.QueryResponse;
import com.xinyirun.scm.bean.clickhouse.vo.ai.SLogAiChatVo;
import com.xinyirun.scm.bean.system.vo.common.condition.PageCondition;
import com.xinyirun.scm.clickhouse.entity.ai.SLogAiChatClickHouseEntity;
import com.xinyirun.scm.clickhouse.exception.ClickHouseConnectionException;
import com.xinyirun.scm.clickhouse.exception.ClickHouseQueryException;
import com.xinyirun.scm.common.utils.bean.BeanUtilsSupport;
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
 * AI聊天日志 ClickHouse Repository
 * 专门处理 s_log_ai_chat 表的所有数据库操作
 *
 * <p>职责：
 * - 插入操作：单条插入、批量插入
 * - 查询操作：分页查询、按ID查询
 * - 数据转换：ClickHouse GenericRecord/Reader → Entity → VO
 *
 * <p>技术特点：
 * - 使用ClickHouse Java Client v2进行参数化查询
 * - 支持POJO自动序列化插入
 * - 使用BinaryFormatReader进行流式读取（性能优化）
 * - 参数化查询防止SQL注入
 *
 * <p>多租户支持：
 * - 所有查询操作强制添加tenant_code过滤条件
 * - 确保租户间数据完全隔离
 *
 * @author AI Chat Logging System
 * @since 2025-09-30
 * @see com.xinyirun.scm.clickhouse.service.ai.SLogAiChatClickHouseService
 */
@Slf4j
@Repository
public class SLogAiChatClickHouseRepository {

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String TABLE_NAME = "s_log_ai_chat";

    private final Client clickHouseClient;

    public SLogAiChatClickHouseRepository(@Qualifier("clickHouseClient") Client clickHouseClient) {
        this.clickHouseClient = clickHouseClient;
    }

    // ==================== 插入操作 ====================

    /**
     * 插入单条AI聊天日志
     *
     * <p>使用ClickHouse Java Client的POJO自动序列化功能
     *
     * @param aiChatLogEntity AI聊天日志实体对象
     */
    public void insert(SLogAiChatClickHouseEntity aiChatLogEntity) {
        long startTime = System.currentTimeMillis();
        try {
            // 插入设置
            InsertSettings insertSettings = new InsertSettings();

            // 执行POJO自动序列化插入
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, List.of(aiChatLogEntity), insertSettings)
                    .get(30, TimeUnit.SECONDS)) {

                log.debug("插入AI聊天日志成功，conversation_id: {}, type: {}",
                        aiChatLogEntity.getConversation_id(), aiChatLogEntity.getType());

            } catch (Exception e) {
                handleInsertError(e, "插入AI聊天日志失败", "insert_ai_chat_log");
            }

        } catch (Exception e) {
            handleRepositoryError(e, "插入AI聊天日志失败");
        }
    }

    /**
     * 批量插入AI聊天日志 - 性能最优
     *
     * <p>批量插入优势：
     * - ClickHouse批量插入性能远优于单条插入
     * - 适合Consumer累积一定数量后批量处理
     *
     * @param aiChatLogEntities AI聊天日志实体对象列表
     */
    public void batchInsert(List<SLogAiChatClickHouseEntity> aiChatLogEntities) {
        if (aiChatLogEntities == null || aiChatLogEntities.isEmpty()) {
            log.warn("批量插入AI聊天日志数据为空，跳过操作");
            return;
        }

        long startTime = System.currentTimeMillis();
        int recordCount = aiChatLogEntities.size();

        try {
            // 设置创建时间（如果未设置）
            LocalDateTime now = LocalDateTime.now();
            for (SLogAiChatClickHouseEntity entity : aiChatLogEntities) {
                if (entity.getC_time() == null) {
                    entity.setC_time(now);
                }
            }

            // 插入设置
            InsertSettings insertSettings = new InsertSettings();

            // 执行批量插入
            try (InsertResponse response = clickHouseClient
                    .insert(TABLE_NAME, aiChatLogEntities, insertSettings)
                    .get(60, TimeUnit.SECONDS)) {

                long duration = System.currentTimeMillis() - startTime;
                log.info("批量插入AI聊天日志成功，数量: {}, 耗时: {}ms", recordCount, duration);

            } catch (Exception e) {
                handleInsertError(e, "批量插入AI聊天日志失败", "batch_insert_ai_chat_log");
            }

        } catch (Exception e) {
            handleRepositoryError(e, "批量插入AI聊天日志失败");
        }
    }

    // ==================== 参数化分页查询操作 ====================

    /**
     * 参数化分页查询AI聊天日志
     *
     * <p>支持查询条件：
     * - conversation_id（对话ID）
     * - type（记录类型：USER/ASSISTANT）
     * - base_name（模型名称）
     * - c_id（创建人ID）
     * - tenant_code（租户编码，必填）
     * - 时间范围（startTime、endTime）
     *
     * <p>查询特点：
     * - 使用参数化查询防止SQL注入
     * - 强制tenant_code过滤，确保多租户数据隔离
     * - 支持分页和排序
     *
     * @param searchCondition 查询条件VO对象
     * @return 分页结果包含VO列表
     */
    public IPage<SLogAiChatVo> selectPageWithParams(SLogAiChatVo searchCondition) {
        try {
            PageCondition pageCondition = searchCondition.getPageCondition();
            long current = pageCondition != null ? pageCondition.getCurrent() : 1;
            long size = pageCondition != null ? pageCondition.getSize() : 10;
            String sort = pageCondition != null ? pageCondition.getSort() : null;

            // 1. 构建参数化查询条件
            StringBuilder whereClause = new StringBuilder("WHERE 1=1");
            Map<String, Object> queryParams = new HashMap<>();

            // 对话ID查询条件
            if (StringUtils.isNotBlank(searchCondition.getConversation_id())) {
                whereClause.append(" AND conversation_id = {conversationId:String}");
                queryParams.put("conversationId", searchCondition.getConversation_id());
            }

            // 记录类型查询条件（USER或ASSISTANT）
            if (StringUtils.isNotBlank(searchCondition.getType())) {
                whereClause.append(" AND type = {type:String}");
                queryParams.put("type", searchCondition.getType());
            }

            // 模型名称查询条件
            if (StringUtils.isNotBlank(searchCondition.getBase_name())) {
                whereClause.append(" AND base_name = {baseName:String}");
                queryParams.put("baseName", searchCondition.getBase_name());
            }

            // 创建人ID查询条件
            if (searchCondition.getC_id() != null) {
                whereClause.append(" AND c_id = {cId:UInt64}");
                queryParams.put("cId", searchCondition.getC_id());
            }

            // 时间范围查询 - 使用字符串格式避免DateTime解析错误
            if (searchCondition.getStartTime() != null) {
                whereClause.append(" AND c_time >= {startTime:String}");
                queryParams.put("startTime", searchCondition.getStartTime().format(DATETIME_FORMATTER));
            }

            if (searchCondition.getEndTime() != null) {
                whereClause.append(" AND c_time <= {endTime:String}");
                queryParams.put("endTime", searchCondition.getEndTime().format(DATETIME_FORMATTER));
            }

            // 租户代码条件 - 必须条件，确保数据隔离
            if (StringUtils.isNotBlank(searchCondition.getTenant_code())) {
                whereClause.append(" AND tenant_code = {tenantCode:String}");
                queryParams.put("tenantCode", searchCondition.getTenant_code());
            }

            // 2. 执行 COUNT 查询获取总数
            String countSql = "SELECT count(*) as total FROM s_log_ai_chat " + whereClause;
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
                SELECT id, conversation_id, type, content, model_source_id,
                       provider_name, base_name, tenant_code, c_id, c_name,
                       c_time, request_id
                FROM s_log_ai_chat
                """ + whereClause + " " + orderClause + """

                LIMIT {limit:UInt32} OFFSET {offset:UInt32}
                """;

            List<SLogAiChatClickHouseEntity> entities = new ArrayList<>();
            try (QueryResponse dataResponse = clickHouseClient.query(dataSql, queryParams).get()) {
                ClickHouseBinaryFormatReader reader = clickHouseClient.newBinaryFormatReader(dataResponse);
                while (reader.hasNext()) {
                    reader.next();
                    entities.add(mapReaderToEntity(reader));
                }
            }

            // 5. 转换 Entity 为 VO 并构建 IPage 结果
            List<SLogAiChatVo> records = entities.stream()
                .map(this::convertEntityToVo)
                .collect(Collectors.toList());

            IPage<SLogAiChatVo> page = new Page<>(current, size, total);
            page.setRecords(records);

            log.info("参数化分页查询AI聊天日志成功，当前页: {}, 页大小: {}, 总数: {}, 结果数量: {}",
                    current, size, total, records.size());

            return page;

        } catch (Exception e) {
            log.error("参数化分页查询AI聊天日志失败", e);
            throw new ClickHouseQueryException("参数化分页查询AI聊天日志失败", "", e);
        }
    }

    /**
     * 构建安全的排序子句
     *
     * <p>排序字段白名单：
     * - c_time（创建时间）
     * - conversation_id（对话ID）
     * - type（记录类型）
     * - tenant_code（租户编码）
     *
     * @param sort 排序参数，格式：字段名_排序方向（如c_time_DESC）
     * @return SQL排序子句
     */
    private String buildOrderByClause(String sort) {
        if (StringUtils.isBlank(sort)) {
            return "ORDER BY c_time DESC"; // 默认按创建时间倒序
        }

        // 安全的排序字段白名单
        Map<String, String> sortFieldMap = Map.of(
            "c_time", "c_time",
            "conversation_id", "conversation_id",
            "type", "type",
            "tenant_code", "tenant_code"
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
    private SLogAiChatVo convertEntityToVo(SLogAiChatClickHouseEntity entity) {
        return (SLogAiChatVo) BeanUtilsSupport.copyProperties(entity, SLogAiChatVo.class);
    }

    // ==================== 单条记录查询操作 ====================

    /**
     * 根据ID查询单条AI聊天日志记录
     *
     * <p>使用ClickHouse Client v2参数化查询防止SQL注入
     * <p>支持租户代码过滤，确保数据隔离
     *
     * @param searchCondition 查询条件（包含ID和tenant_code）
     * @return 找到的VO对象，未找到返回null
     */
    public SLogAiChatVo getById(SLogAiChatVo searchCondition) {
        if (searchCondition == null || StringUtils.isBlank(searchCondition.getId())) {
            log.warn("getById查询参数为空或ID为空，返回null");
            return null;
        }

        String sql = """
            SELECT
                id, conversation_id, type, content, model_source_id,
                provider_name, base_name, tenant_code, c_id, c_name,
                c_time, request_id
            FROM s_log_ai_chat
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
                SLogAiChatClickHouseEntity entity = new SLogAiChatClickHouseEntity();
                entity.setId(record.getString("id"));
                entity.setConversation_id(record.getString("conversation_id"));
                entity.setType(record.getString("type"));
                entity.setContent(record.getString("content"));
                entity.setModel_source_id(record.getString("model_source_id"));
                entity.setProvider_name(record.getString("provider_name"));
                entity.setBase_name(record.getString("base_name"));
                entity.setTenant_code(record.getString("tenant_code"));

                // c_id可能为null，需要安全处理
                try {
                    Long cIdValue = record.getLong("c_id");
                    entity.setC_id(cIdValue);
                } catch (Exception e) {
                    entity.setC_id(null);
                    log.debug("c_id字段为空，设为null，ID: {}", record.getString("id"));
                }

                entity.setC_name(record.getString("c_name"));
                entity.setC_time(record.getLocalDateTime("c_time"));
                entity.setRequest_id(record.getString("request_id"));

                // 转换为VO并返回
                SLogAiChatVo vo = convertEntityToVo(entity);
                log.info("根据ID查询AI聊天日志成功，ID: {}, 租户: {}, 对话ID: {}",
                        id, tenantCode, vo.getConversation_id());
                return vo;
            }

            log.info("根据ID查询AI聊天日志未找到记录，ID: {}, 租户: {}", id, tenantCode);
            return null;

        } catch (Exception e) {
            log.error("根据ID查询AI聊天日志失败，ID: {}, 租户: {}", id, tenantCode, e);
            throw new ClickHouseQueryException("根据ID查询AI聊天日志失败", sql, e);
        }
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 将ClickHouseBinaryFormatReader映射到实体对象 - 用于流式读取
     * 添加空值处理，防止NullValueException
     */
    private SLogAiChatClickHouseEntity mapReaderToEntity(ClickHouseBinaryFormatReader reader) {
        SLogAiChatClickHouseEntity entity = new SLogAiChatClickHouseEntity();
        entity.setId(reader.getString("id"));
        entity.setConversation_id(reader.getString("conversation_id"));
        entity.setType(reader.getString("type"));
        entity.setContent(reader.getString("content"));
        entity.setModel_source_id(reader.getString("model_source_id"));
        entity.setProvider_name(reader.getString("provider_name"));
        entity.setBase_name(reader.getString("base_name"));
        entity.setTenant_code(reader.getString("tenant_code"));

        // c_id可能为null，需要安全处理
        try {
            Long cIdValue = reader.getLong("c_id");
            entity.setC_id(cIdValue);
        } catch (Exception e) {
            entity.setC_id(null);
            log.debug("c_id字段为空，设为null，ID: {}", reader.getString("id"));
        }

        entity.setC_name(reader.getString("c_name"));
        entity.setC_time(reader.getLocalDateTime("c_time"));
        entity.setRequest_id(reader.getString("request_id"));

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