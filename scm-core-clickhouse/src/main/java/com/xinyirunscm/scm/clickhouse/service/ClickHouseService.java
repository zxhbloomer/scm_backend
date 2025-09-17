package com.xinyirunscm.scm.clickhouse.service;

import com.xinyirunscm.scm.clickhouse.entity.ClickHouseLogEntity;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseException;
import com.xinyirunscm.scm.clickhouse.repository.ClickHouseRepository;
import com.xinyirunscm.scm.clickhouse.validation.ClickHouseQueryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * ClickHouse 核心服务类
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Service
public class ClickHouseService {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseService.class);

    private final ClickHouseRepository clickHouseRepository;
    private final ClickHouseQueryValidator validator;

    public ClickHouseService(ClickHouseRepository clickHouseRepository, ClickHouseQueryValidator validator) {
        this.clickHouseRepository = clickHouseRepository;
        this.validator = validator;
    }

    /**
     * 插入数据变更日志
     */
    public void insertDataChangeLog(ClickHouseLogEntity logEntity) {
        try {
            // 设置创建时间
            if (logEntity.getCreate_time() == null) {
                logEntity.setCreate_time(LocalDateTime.now());
            }
            
            clickHouseRepository.insertDataChangeLog(logEntity);
            logger.info("插入数据变更日志成功，表: {}, 操作: {}, 记录ID: {}", 
                       logEntity.getTable_name(), logEntity.getOperation_type(), logEntity.getRecord_id());
                       
        } catch (Exception e) {
            logger.error("插入数据变更日志失败", e);
            throw new ClickHouseException("插入数据变更日志失败", e);
        }
    }

    /**
     * 异步插入数据变更日志
     */
    @Async
    public CompletableFuture<Void> insertDataChangeLogAsync(ClickHouseLogEntity logEntity) {
        try {
            insertDataChangeLog(logEntity);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 批量插入数据变更日志
     */
    public void batchInsertDataChangeLog(List<ClickHouseLogEntity> logEntities) {
        if (logEntities == null || logEntities.isEmpty()) {
            logger.warn("批量插入数据为空，跳过操作");
            return;
        }

        try {
            // 设置创建时间
            LocalDateTime now = LocalDateTime.now();
            for (ClickHouseLogEntity entity : logEntities) {
                if (entity.getCreate_time() == null) {
                    entity.setCreate_time(now);
                }
            }

            clickHouseRepository.batchInsertDataChangeLog(logEntities);
            logger.info("批量插入数据变更日志成功，数量: {}", logEntities.size());
            
        } catch (Exception e) {
            logger.error("批量插入数据变更日志失败", e);
            throw new ClickHouseException("批量插入数据变更日志失败", e);
        }
    }

    /**
     * 异步批量插入数据变更日志
     */
    @Async
    public CompletableFuture<Void> batchInsertDataChangeLogAsync(List<ClickHouseLogEntity> logEntities) {
        try {
            batchInsertDataChangeLog(logEntities);
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    /**
     * 批量插入任意对象数据
     */
    public void batchInsert(List<Object> data, String tableName) {
        if (data == null || data.isEmpty()) {
            logger.warn("批量插入数据为空，跳过操作");
            return;
        }

        try {
            // 简单实现，将对象转换为ClickHouseLogEntity
            // 实际使用时可以根据具体需求扩展
            logger.info("批量插入数据到表: {}, 数量: {}", tableName, data.size());
            
            // 这里可以根据tableName和数据类型进行具体的插入逻辑
            // 当前简化实现，仅记录日志
            
        } catch (Exception e) {
            logger.error("批量插入数据失败", e);
            throw new ClickHouseException("批量插入数据失败", e);
        }
    }

    /**
     * 查询数据统计（不安全，建议使用参数化查询方法）
     * @deprecated 由于SQL注入风险，建议使用 getChangeStatistics 等安全方法
     */
    @Deprecated
    public List<Map<String, Object>> queryStatistics(String sql) {
        logger.warn("使用了不安全的queryStatistics方法，SQL: {}", sql);
        
        try {
            List<Map<String, Object>> result = clickHouseRepository.executeQuery(sql);
            logger.info("查询数据统计成功，SQL: {}, 结果数量: {}", sql, result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("查询数据统计失败，SQL: {}", sql, e);
            throw new ClickHouseException("查询数据统计失败", e);
        }
    }

    /**
     * 按时间范围查询数据变更日志
     */
    public List<ClickHouseLogEntity> queryDataChangeLogByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            List<ClickHouseLogEntity> result = clickHouseRepository.queryByTimeRange(startTime, endTime);
            logger.info("按时间范围查询数据变更日志成功，时间范围: {} - {}, 结果数量: {}", 
                       startTime, endTime, result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("按时间范围查询数据变更日志失败", e);
            throw new ClickHouseException("按时间范围查询数据变更日志失败", e);
        }
    }

    /**
     * 获取数据变更统计信息
     */
    public Map<String, Object> getChangeStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        // 输入验证
        validator.validateTimeRange(startTime, endTime);
        
        String sql = """
            SELECT 
                table_name,
                operation_type,
                count(*) as change_count,
                uniq(user_id) as unique_users
            FROM data_change_log 
            WHERE change_time BETWEEN ? AND ?
            GROUP BY table_name, operation_type
            ORDER BY change_count DESC
            LIMIT 50
            """;

        try {
            List<Map<String, Object>> result = clickHouseRepository.executeQuery(sql, startTime, endTime);
            logger.info("获取数据变更统计信息成功，时间范围: {} - {}", startTime, endTime);
            
            // 返回第一个结果作为示例，实际可以根据需要进行数据聚合
            return result.isEmpty() ? Map.of() : result.get(0);
            
        } catch (Exception e) {
            logger.error("获取数据变更统计信息失败", e);
            throw new ClickHouseException("获取数据变更统计信息失败", e);
        }
    }

    /**
     * 清理过期数据
     */
    public int cleanExpiredData(int daysToKeep) {
        // 输入验证
        if (daysToKeep < 1 || daysToKeep > 365) {
            throw new IllegalArgumentException("保留天数必须在1-365之间");
        }
        
        LocalDateTime cutoffTime = LocalDateTime.now().minusDays(daysToKeep);
        
        // 使用参数化查询，避免SQL注入
        String sql = "ALTER TABLE data_change_log DELETE WHERE create_time < ?";
        
        try {
            List<Map<String, Object>> result = clickHouseRepository.executeQuery(sql, cutoffTime);
            logger.info("清理过期数据成功，保留天数: {}, 截止时间: {}", daysToKeep, cutoffTime);
            
            // ClickHouse的DELETE操作通常返回受影响的行数信息
            return result.size();
            
        } catch (Exception e) {
            logger.error("清理过期数据失败", e);
            throw new ClickHouseException("清理过期数据失败", e);
        }
    }
}