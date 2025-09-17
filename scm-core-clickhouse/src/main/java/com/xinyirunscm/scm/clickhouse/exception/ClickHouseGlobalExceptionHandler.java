package com.xinyirunscm.scm.clickhouse.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * ClickHouse 全局异常处理器
 * 
 * @author SCM System
 * @since 1.0.39
 */
@ControllerAdvice
public class ClickHouseGlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseGlobalExceptionHandler.class);

    /**
     * 处理 ClickHouse 连接异常
     */
    @ExceptionHandler(ClickHouseConnectionException.class)
    public ResponseEntity<Map<String, Object>> handleConnectionException(ClickHouseConnectionException e) {
        logger.error("ClickHouse连接异常", e);
        
        Map<String, Object> errorResponse = createErrorResponse(
            e.getErrorCode(),
            "ClickHouse连接失败",
            e.getMessage(),
            HttpStatus.SERVICE_UNAVAILABLE.value()
        );
        
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
    }

    /**
     * 处理 ClickHouse 验证异常
     */
    @ExceptionHandler(ClickHouseValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ClickHouseValidationException e) {
        logger.warn("ClickHouse输入验证失败: {}", e.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            e.getErrorCode(),
            "输入参数验证失败",
            e.getMessage(),
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 处理 ClickHouse 查询异常
     */
    @ExceptionHandler(ClickHouseQueryException.class)
    public ResponseEntity<Map<String, Object>> handleQueryException(ClickHouseQueryException e) {
        logger.error("ClickHouse查询执行失败，SQL: {}", e.getSql(), e);
        
        Map<String, Object> errorResponse = createErrorResponse(
            e.getErrorCode(),
            "数据库查询执行失败",
            "查询执行出现异常，请联系系统管理员",
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        errorResponse.put("sql_hash", e.getSql() != null ? e.getSql().hashCode() : null);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理通用 ClickHouse 异常
     */
    @ExceptionHandler(ClickHouseException.class)
    public ResponseEntity<Map<String, Object>> handleClickHouseException(ClickHouseException e) {
        logger.error("ClickHouse操作异常: {}", e.getMessage(), e);
        
        Map<String, Object> errorResponse = createErrorResponse(
            e.getErrorCode(),
            "ClickHouse操作失败",
            e.getMessage(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * 处理系统安全异常
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> handleSecurityException(SecurityException e) {
        logger.warn("ClickHouse安全违规: {}", e.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            "SECURITY_VIOLATION",
            "操作被安全策略拒绝",
            e.getMessage(),
            HttpStatus.FORBIDDEN.value()
        );
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errorResponse);
    }

    /**
     * 处理非法参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException e) {
        logger.warn("ClickHouse非法参数: {}", e.getMessage());
        
        Map<String, Object> errorResponse = createErrorResponse(
            "ILLEGAL_ARGUMENT",
            "参数错误",
            e.getMessage(),
            HttpStatus.BAD_REQUEST.value()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }

    /**
     * 创建标准错误响应
     */
    private Map<String, Object> createErrorResponse(String errorCode, String error, String message, int status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now());
        errorResponse.put("status", status);
        errorResponse.put("error", error);
        errorResponse.put("error_code", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("module", "CLICKHOUSE");
        return errorResponse;
    }
}