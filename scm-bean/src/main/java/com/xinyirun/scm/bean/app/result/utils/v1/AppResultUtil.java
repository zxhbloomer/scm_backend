package com.xinyirun.scm.bean.app.result.utils.v1;

import com.xinyirun.scm.bean.app.ao.result.AppJsonResultAo;
import com.xinyirun.scm.common.constant.JsonResultTypeConstants;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.utils.CommonUtil;
import com.xinyirun.scm.common.utils.DateTimeUtil;
import com.xinyirun.scm.common.utils.ExceptionUtil;
import org.springframework.http.HttpStatus;

import jakarta.servlet.http.HttpServletRequest;

/**
 * json返回值工具类
 * @author zxh
 */
public class AppResultUtil {

    public static <T> AppJsonResultAo<T> OK(T data, String message) {
        return AppJsonResultAo.<T>builder()
            .timestamp(DateTimeUtil.getTime())
            .http_status(HttpStatus.OK.value())
            .system_code(ResultEnum.OK.getCode())
            .system_message(ResultEnum.OK.getMsg())
            .message(message)
            .path(CommonUtil.getRequest().getRequestURL().toString())
            .method(CommonUtil.getRequest().getMethod())
            .success(true)
            .data(data)
            .build();
    }

    /**
     * 无错误的返回
     * @param data
     * @param <T>
     * @return
     */
    public static <T> AppJsonResultAo<T> OK(T data, Integer json_null_out) {
        return AppJsonResultAo.<T>builder()
                .timestamp(DateTimeUtil.getTime())
                .http_status(HttpStatus.OK.value())
                .system_code(ResultEnum.OK.getCode())
                .system_message(ResultEnum.OK.getMsg())
                .message("调用成功")
                .path(CommonUtil.getRequest().getRequestURL().toString())
                .method(CommonUtil.getRequest().getMethod())
                .success(true)
                .json_result_type(json_null_out)
                .data(data)
                .build();
    }

    /**
     * 无错误的返回
     * @param data
     * @param <T>
     * @return
     */
    public static <T> AppJsonResultAo<T> OK(T data) {
        return AppResultUtil.OK(data, JsonResultTypeConstants.NORMAL);
    }

    /**
     * 含code的无错误的返回
     * @param data
     * @param system_code
     * @param <T>
     * @return
     */
    public static <T> AppJsonResultAo<T> OK(T data, ResultEnum system_code) {
        return AppJsonResultAo.<T>builder()
            .timestamp(DateTimeUtil.getTime())
            .http_status(HttpStatus.OK.value())
            .system_code(system_code.getCode())
            .system_message(system_code.getMsg())
            .message("调用成功")
            .path(CommonUtil.getRequest().getRequestURL().toString())
            .method(CommonUtil.getRequest().getMethod())
            .success(true)
            .data(data)
            .build();
    }

    public static <T> AppJsonResultAo<T> NG(Integer httpStatus, ResultEnum system_code, Exception exception, String message, HttpServletRequest request) {

        return AppJsonResultAo.<T>builder()
                .timestamp(DateTimeUtil.getTime())
                .http_status(httpStatus)
                .system_code(system_code.getCode())
                .system_message(system_code.getMsg())
                .message(message)
                .path(request.getRequestURL().toString())
                .method(request.getMethod())
                .success(false)
                .data((T) ExceptionUtil.getException(exception))
                .build();
    }
}
