package com.xinyirunscm.scm.clickhouse.service;

import com.xinyirunscm.scm.clickhouse.entity.ClickHouseLogEntity;
import com.xinyirunscm.scm.clickhouse.exception.ClickHouseException;
import com.xinyirunscm.scm.clickhouse.repository.ClickHouseRepository;
import com.xinyirunscm.scm.clickhouse.validation.ClickHouseQueryValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * ClickHouse 查询服务类
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Service
public class ClickHouseQueryService {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseQueryService.class);

    private final ClickHouseRepository clickHouseRepository;
    private final ClickHouseQueryValidator validator;

    public ClickHouseQueryService(ClickHouseRepository clickHouseRepository, ClickHouseQueryValidator validator) {
        this.clickHouseRepository = clickHouseRepository;
        this.validator = validator;
    }

    /**
     * 执行原生SQL查询
     */
    public List<Map<String, Object>> executeQuery(String sql) {
        try {
            logger.debug("执行SQL查询: {}", sql);
            List<Map<String, Object>> result = clickHouseRepository.executeQuery(sql);
            logger.info("SQL查询执行成功，结果数量: {}", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("执行SQL查询失败: {}", sql, e);
            throw new ClickHouseException("执行SQL查询失败", e);
        }
    }

    /**
     * 按时间范围查询变更日志
     */
    public List<ClickHouseLogEntity> queryChangeLogByTime(LocalDateTime startTime, LocalDateTime endTime) {
        try {
            logger.info("按时间范围查询变更日志: {} - {}", startTime, endTime);
            List<ClickHouseLogEntity> result = clickHouseRepository.queryByTimeRange(startTime, endTime);
            logger.info("查询变更日志成功，结果数量: {}", result.size());
            return result;
            
        } catch (Exception e) {
            logger.error("按时间范围查询变更日志失败", e);
            throw new ClickHouseException("按时间范围查询变更日志失败", e);
        }
    }

    /**
     * 按表名查询变更日志 (Client V2)
     */
    public List<Map<String, Object>> queryChangeLogByTable(String tableName, LocalDateTime startTime, LocalDateTime endTime) {
        // 输入验证
        validator.validateTableName(tableName);
        validator.validateTimeRange(startTime, endTime);
        
        String sql = """
            SELECT 
                log_id, operation_type, record_id, change_time, 
                user_name, request_id, order_code, remark
            FROM data_change_log 
            WHERE table_name = '%s' 
              AND change_time BETWEEN '%s' AND '%s'
            ORDER BY change_time DESC
            LIMIT 500
            """.formatted(tableName, 
                         startTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                         endTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        try {
            logger.info("按表名查询变更日志: {}, 时间范围: {} - {}", tableName, startTime, endTime);
            return clickHouseRepository.executeQuery(sql);
            
        } catch (Exception e) {
            logger.error("按表名查询变更日志失败", e);
            throw new ClickHouseException("按表名查询变更日志失败", e);
        }
    }

    /**
     * 按用户查询变更日志 (Client V2)
     */
    public List<Map<String, Object>> queryChangeLogByUser(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        // 输入验证
        validator.validateUserId(userId);
        validator.validateTimeRange(startTime, endTime);
        
        String sql = """
            SELECT 
                table_name, operation_type, record_id, change_time, 
                request_id, order_code, remark
            FROM data_change_log 
            WHERE user_id = %d 
              AND change_time BETWEEN '%s' AND '%s'
            ORDER BY change_time DESC
            LIMIT 500
            """.formatted(userId,
                         startTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                         endTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        try {
            logger.info("按用户查询变更日志: {}, 时间范围: {} - {}", userId, startTime, endTime);
            return clickHouseRepository.executeQuery(sql);
            
        } catch (Exception e) {
            logger.error("按用户查询变更日志失败", e);
            throw new ClickHouseException("按用户查询变更日志失败", e);
        }
    }

    /**
     * 按订单编号查询变更日志 (Client V2)
     */
    public List<Map<String, Object>> queryChangeLogByOrderCode(String orderCode) {
        // 输入验证
        validator.validateOrderCode(orderCode);
        
        String sql = """
            SELECT 
                table_name, operation_type, record_id, change_time, 
                user_name, request_id, before_data, after_data, changed_fields
            FROM data_change_log 
            WHERE order_code = '%s'
            ORDER BY change_time ASC
            LIMIT 1000
            """.formatted(orderCode);

        try {
            logger.info("按订单编号查询变更日志: {}", orderCode);
            return clickHouseRepository.executeQuery(sql);
            
        } catch (Exception e) {
            logger.error("按订单编号查询变更日志失败", e);
            throw new ClickHouseException("按订单编号查询变更日志失败", e);
        }
    }

    /**
     * 获取变更统计报表 - 按表统计 (Client V2)
     */
    public List<Map<String, Object>> getChangeStatsByTable(LocalDateTime startTime, LocalDateTime endTime) {
        // 输入验证
        validator.validateTimeRange(startTime, endTime);
        
        String sql = """
            SELECT 
                table_name,
                countIf(operation_type = 'INSERT') as insert_count,
                countIf(operation_type = 'UPDATE') as update_count,
                countIf(operation_type = 'DELETE') as delete_count,
                count(*) as total_count,
                uniq(user_id) as unique_users
            FROM data_change_log 
            WHERE change_time BETWEEN '%s' AND '%s'
            GROUP BY table_name
            ORDER BY total_count DESC
            LIMIT 50
            """.formatted(startTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                         endTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        try {
            logger.info("获取按表统计的变更报表: {} - {}", startTime, endTime);
            return clickHouseRepository.executeQuery(sql);
            
        } catch (Exception e) {
            logger.error("获取按表统计的变更报表失败", e);
            throw new ClickHouseException("获取按表统计的变更报表失败", e);
        }
    }

    /**
     * 获取变更统计报表 - 按用户统计 (Client V2)
     */
    public List<Map<String, Object>> getChangeStatsByUser(LocalDateTime startTime, LocalDateTime endTime) {
        // 输入验证
        validator.validateTimeRange(startTime, endTime);
        
        String sql = """
            SELECT 
                user_id,
                user_name,
                countIf(operation_type = 'INSERT') as insert_count,
                countIf(operation_type = 'UPDATE') as update_count,
                countIf(operation_type = 'DELETE') as delete_count,
                count(*) as total_count,
                uniq(table_name) as affected_tables
            FROM data_change_log 
            WHERE change_time BETWEEN '%s' AND '%s'
            GROUP BY user_id, user_name
            ORDER BY total_count DESC
            LIMIT 100
            """.formatted(startTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                         endTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        try {
            logger.info("获取按用户统计的变更报表: {} - {}", startTime, endTime);
            return clickHouseRepository.executeQuery(sql);
            
        } catch (Exception e) {
            logger.error("获取按用户统计的变更报表失败", e);
            throw new ClickHouseException("获取按用户统计的变更报表失败", e);
        }
    }

    /**
     * 获取变更趋势报表 - 按时间统计 (Client V2)
     */
    public List<Map<String, Object>> getChangeTrendsByTime(LocalDateTime startTime, LocalDateTime endTime) {
        // 输入验证
        validator.validateTimeRange(startTime, endTime);
        
        String sql = """
            SELECT 
                toDate(change_time) as change_date,
                toHour(change_time) as change_hour,
                count(*) as change_count,
                uniq(user_id) as unique_users,
                uniq(table_name) as affected_tables
            FROM data_change_log 
            WHERE change_time BETWEEN '%s' AND '%s'
            GROUP BY change_date, change_hour
            ORDER BY change_date DESC, change_hour DESC
            LIMIT 1000
            """.formatted(startTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")), 
                         endTime.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        try {
            logger.info("获取变更趋势报表: {} - {}", startTime, endTime);
            return clickHouseRepository.executeQuery(sql);
            
        } catch (Exception e) {
            logger.error("获取变更趋势报表失败", e);
            throw new ClickHouseException("获取变更趋势报表失败", e);
        }
    }

    /**
     * 检查数据库连接状态
     */
    public boolean checkConnection() {
        try {
            String sql = "SELECT 1 as test";
            List<Map<String, Object>> result = clickHouseRepository.executeQuery(sql);
            boolean isConnected = !result.isEmpty();
            logger.info("ClickHouse连接状态检查: {}", isConnected ? "正常" : "异常");
            return isConnected;
            
        } catch (Exception e) {
            logger.error("ClickHouse连接状态检查失败", e);
            return false;
        }
    }

    /**
     * 获取数据库信息
     */
    public Map<String, Object> getDatabaseInfo() {
        String sql = """
            SELECT 
                version() as version,
                uptime() as uptime,
                timezone() as timezone,
                now() as current_time
            """;

        try {
            List<Map<String, Object>> result = executeQuery(sql);
            if (!result.isEmpty()) {
                logger.info("获取数据库信息成功");
                return result.get(0);
            }
            return Map.of();
            
        } catch (Exception e) {
            logger.error("获取数据库信息失败", e);
            throw new ClickHouseException("获取数据库信息失败", e);
        }
    }
}