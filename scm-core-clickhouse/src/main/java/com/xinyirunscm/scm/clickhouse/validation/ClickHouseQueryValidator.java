package com.xinyirunscm.scm.clickhouse.validation;

import com.xinyirunscm.scm.clickhouse.exception.ClickHouseValidationException;
import org.springframework.stereotype.Component;
import org.apache.commons.lang3.StringUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * ClickHouse查询参数安全验证
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Component
public class ClickHouseQueryValidator {
    
    private static final Pattern TABLE_NAME_PATTERN = Pattern.compile("^[a-z_][a-z0-9_]{1,63}$");
    private static final Pattern ORDER_CODE_PATTERN = Pattern.compile("^[A-Z0-9]{8,30}$");
    private static final Set<String> ALLOWED_TABLES = Set.of(
        "data_change_log", "business_metrics", "performance_metrics"
    );
    
    private static final int MAX_USER_ID = 999999999;
    private static final int MAX_ORDER_CODE_LENGTH = 30;
    private static final int MAX_QUERY_DAYS = 90;
    
    /**
     * 表名安全验证
     */
    public void validateTableName(String tableName) {
        if (StringUtils.isBlank(tableName)) {
            throw new ClickHouseValidationException("表名不能为空");
        }
        
        if (!TABLE_NAME_PATTERN.matcher(tableName).matches()) {
            throw new ClickHouseValidationException("表名格式无效: " + tableName);
        }
        
        if (!ALLOWED_TABLES.contains(tableName)) {
            throw new SecurityException("表访问被拒绝: " + tableName);
        }
    }
    
    /**
     * 用户ID验证
     */
    public void validateUserId(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ClickHouseValidationException("用户ID无效");
        }
        
        if (userId > MAX_USER_ID) {
            throw new ClickHouseValidationException("用户ID超出范围");
        }
    }
    
    /**
     * 订单编号验证
     */
    public void validateOrderCode(String orderCode) {
        if (StringUtils.isBlank(orderCode)) {
            throw new ClickHouseValidationException("订单编号不能为空");
        }
        
        if (!ORDER_CODE_PATTERN.matcher(orderCode).matches()) {
            throw new ClickHouseValidationException("订单编号格式无效: " + orderCode);
        }
        
        if (orderCode.length() > MAX_ORDER_CODE_LENGTH) {
            throw new ClickHouseValidationException("订单编号过长");
        }
    }
    
    /**
     * 时间范围验证
     */
    public void validateTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        if (startTime == null || endTime == null) {
            throw new ClickHouseValidationException("查询时间范围不能为空");
        }
        
        if (startTime.isAfter(endTime)) {
            throw new ClickHouseValidationException("开始时间不能晚于结束时间");
        }
        
        // 查询时间跨度限制
        Duration duration = Duration.between(startTime, endTime);
        if (duration.toDays() > MAX_QUERY_DAYS) {
            throw new ClickHouseValidationException(
                String.format("查询时间跨度超过限制: %d天 > %d天", 
                    duration.toDays(), MAX_QUERY_DAYS));
        }
        
        // 防止查询过于久远的数据
        LocalDateTime earliestAllowed = LocalDateTime.now().minusMonths(12);
        if (startTime.isBefore(earliestAllowed)) {
            throw new ClickHouseValidationException("不允许查询12个月前的数据");
        }
    }
}