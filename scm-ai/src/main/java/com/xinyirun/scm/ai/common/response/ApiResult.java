package com.xinyirun.scm.ai.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * AI模块统一API响应结果
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 成功响应
     */
    public static <T> ApiResult<T> success(String message, T data) {
        return ApiResult.<T>builder()
                .code(200)
                .message(message)
                .data(data)
                .success(true)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 成功响应（无数据）
     */
    public static <T> ApiResult<T> success(String message) {
        return success(message, null);
    }

    /**
     * 错误响应
     */
    public static <T> ApiResult<T> error(String message) {
        return error(500, message);
    }

    /**
     * 错误响应（带状态码）
     */
    public static <T> ApiResult<T> error(Integer code, String message) {
        return ApiResult.<T>builder()
                .code(code)
                .message(message)
                .data(null)
                .success(false)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}