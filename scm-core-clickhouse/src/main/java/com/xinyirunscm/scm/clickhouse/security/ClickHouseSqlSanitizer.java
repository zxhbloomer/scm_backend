package com.xinyirunscm.scm.clickhouse.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * ClickHouse SQL安全工具类
 * 提供安全的参数化查询支持，防止SQL注入攻击
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Component
public class ClickHouseSqlSanitizer {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseSqlSanitizer.class);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * 危险SQL关键字模式 - 防止SQL注入
     */
    private static final Pattern SQL_INJECTION_PATTERN = Pattern.compile(
            "(?i)(\\b(SELECT|INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|TRUNCATE|EXEC|EXECUTE|UNION|OR|AND|--|/\\*|\\*/|;|\\bxp_|\\bsp_)\\b)",
            Pattern.CASE_INSENSITIVE
    );
    
    /**
     * 安全的字符串参数模式 - 只允许字母、数字、下划线、中文等安全字符
     */
    private static final Pattern SAFE_STRING_PATTERN = Pattern.compile("^[a-zA-Z0-9_\\u4e00-\\u9fa5\\s.-]*$");
    
    /**
     * 租户代码安全模式 - 只允许字母数字下划线
     */
    private static final Pattern TENANT_CODE_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{1,50}$");

    /**
     * 安全的时间范围查询SQL构建
     * 基于ClickHouse Java v2最佳实践，提供安全的时间参数处理
     */
    public String buildTimeRangeQuery(String tableName, String timeColumn, LocalDateTime startTime, LocalDateTime endTime, String additionalConditions) {
        // 验证表名和列名的安全性
        validateIdentifier(tableName, "表名");
        validateIdentifier(timeColumn, "时间列名");
        
        // 验证时间参数
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("开始时间和结束时间不能为空");
        }
        
        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("开始时间不能晚于结束时间");
        }
        
        // 安全地格式化时间
        String startTimeStr = formatDateTime(startTime);
        String endTimeStr = formatDateTime(endTime);
        
        StringBuilder sql = new StringBuilder();
        sql.append("WHERE ").append(timeColumn).append(" BETWEEN '").append(startTimeStr).append("' AND '").append(endTimeStr).append("'");
        
        // 安全地添加额外条件
        if (additionalConditions != null && !additionalConditions.trim().isEmpty()) {
            String sanitizedConditions = sanitizeAdditionalConditions(additionalConditions);
            sql.append(" AND ").append(sanitizedConditions);
        }
        
        return sql.toString();
    }

    /**
     * 安全的租户查询条件构建
     */
    public String buildTenantCondition(String tenantCode) {
        if (tenantCode == null || tenantCode.trim().isEmpty()) {
            throw new IllegalArgumentException("租户代码不能为空");
        }
        
        // 验证租户代码格式
        if (!TENANT_CODE_PATTERN.matcher(tenantCode).matches()) {
            throw new IllegalArgumentException("租户代码格式不合法，只能包含字母、数字和下划线，长度不超过50");
        }
        
        return "tenant_code = '" + tenantCode + "'";
    }

    /**
     * 安全的LIKE查询条件构建
     */
    public String buildLikeCondition(String columnName, String searchValue) {
        validateIdentifier(columnName, "列名");
        
        if (searchValue == null || searchValue.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索值不能为空");
        }
        
        // 验证搜索值的安全性
        String sanitizedValue = sanitizeStringParameter(searchValue);
        
        // 转义LIKE特殊字符
        sanitizedValue = escapeLikeValue(sanitizedValue);
        
        return columnName + " LIKE '%" + sanitizedValue + "%'";
    }

    /**
     * 安全的IN查询条件构建
     */
    public String buildInCondition(String columnName, String... values) {
        validateIdentifier(columnName, "列名");
        
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("IN条件的值列表不能为空");
        }
        
        StringBuilder inClause = new StringBuilder();
        inClause.append(columnName).append(" IN (");
        
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                inClause.append(",");
            }
            String sanitizedValue = sanitizeStringParameter(values[i]);
            inClause.append("'").append(sanitizedValue).append("'");
        }
        
        inClause.append(")");
        return inClause.toString();
    }

    /**
     * 安全的数值范围查询条件构建
     */
    public String buildNumericRangeCondition(String columnName, Long minValue, Long maxValue) {
        validateIdentifier(columnName, "列名");
        
        if (minValue == null && maxValue == null) {
            throw new IllegalArgumentException("数值范围的最小值和最大值不能都为空");
        }
        
        StringBuilder condition = new StringBuilder();
        
        if (minValue != null && maxValue != null) {
            if (minValue > maxValue) {
                throw new IllegalArgumentException("最小值不能大于最大值");
            }
            condition.append(columnName).append(" BETWEEN ").append(minValue).append(" AND ").append(maxValue);
        } else if (minValue != null) {
            condition.append(columnName).append(" >= ").append(minValue);
        } else {
            condition.append(columnName).append(" <= ").append(maxValue);
        }
        
        return condition.toString();
    }

    /**
     * 验证SQL标识符（表名、列名等）的安全性
     */
    public void validateIdentifier(String identifier, String identifierType) {
        if (identifier == null || identifier.trim().isEmpty()) {
            throw new IllegalArgumentException(identifierType + "不能为空");
        }
        
        // 检查是否包含危险字符
        if (identifier.contains("'") || identifier.contains("\"") || identifier.contains(";") 
            || identifier.contains("--") || identifier.contains("/*") || identifier.contains("*/")) {
            throw new IllegalArgumentException(identifierType + "包含不安全的字符");
        }
        
        // 检查是否为纯标识符（字母、数字、下划线）
        if (!identifier.matches("^[a-zA-Z_][a-zA-Z0-9_]*$")) {
            throw new IllegalArgumentException(identifierType + "格式不正确，只能包含字母、数字和下划线，且必须以字母或下划线开头");
        }
    }

    /**
     * 清理字符串参数，防止SQL注入
     */
    public String sanitizeStringParameter(String parameter) {
        if (parameter == null) {
            return "";
        }
        
        String sanitized = parameter.trim();
        
        // 检查是否包含SQL注入模式
        if (SQL_INJECTION_PATTERN.matcher(sanitized).find()) {
            logger.warn("检测到潜在的SQL注入尝试: {}", sanitized);
            throw new IllegalArgumentException("参数包含不安全的SQL关键字");
        }
        
        // 检查是否符合安全字符模式
        if (!SAFE_STRING_PATTERN.matcher(sanitized).matches()) {
            logger.warn("参数包含不安全的字符: {}", sanitized);
            throw new IllegalArgumentException("参数包含不安全的字符");
        }
        
        // 转义单引号
        sanitized = sanitized.replace("'", "''");
        
        return sanitized;
    }

    /**
     * 安全地格式化日期时间
     */
    public String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            throw new IllegalArgumentException("日期时间不能为空");
        }
        
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 清理额外查询条件
     */
    private String sanitizeAdditionalConditions(String conditions) {
        if (conditions == null || conditions.trim().isEmpty()) {
            return "";
        }
        
        String sanitized = conditions.trim();
        
        // 检查是否包含危险的SQL语句
        if (SQL_INJECTION_PATTERN.matcher(sanitized).find()) {
            logger.warn("检测到潜在的SQL注入尝试在额外条件中: {}", sanitized);
            throw new IllegalArgumentException("额外条件包含不安全的SQL关键字");
        }
        
        return sanitized;
    }

    /**
     * 转义LIKE查询中的特殊字符
     */
    private String escapeLikeValue(String value) {
        if (value == null) {
            return "";
        }
        
        return value.replace("\\", "\\\\")
                   .replace("%", "\\%")
                   .replace("_", "\\_")
                   .replace("'", "''");
    }

    /**
     * 验证查询结果限制参数
     */
    public int validateLimit(Integer limit) {
        if (limit == null) {
            return 1000; // 默认限制
        }
        
        if (limit <= 0) {
            throw new IllegalArgumentException("查询限制必须大于0");
        }
        
        if (limit > 10000) {
            logger.warn("查询限制过大，调整为10000: {}", limit);
            return 10000; // 最大限制，防止性能问题
        }
        
        return limit;
    }

    /**
     * 验证排序参数
     */
    public String validateOrderBy(String orderBy) {
        if (orderBy == null || orderBy.trim().isEmpty()) {
            return "c_time DESC"; // 默认排序
        }
        
        String sanitized = orderBy.trim();
        
        // 验证排序表达式的安全性
        if (!sanitized.matches("^[a-zA-Z_][a-zA-Z0-9_]*\\s+(ASC|DESC)$")) {
            throw new IllegalArgumentException("排序参数格式不正确，应为：列名 ASC/DESC");
        }
        
        return sanitized;
    }
}