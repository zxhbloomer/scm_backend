package com.xinyirun.scm.bean.system.result.utils.v1;

import com.xinyirun.scm.bean.api.ao.result.ApiJsonResultAo;
import com.xinyirun.scm.common.constant.JsonResultTypeConstants;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import com.xinyirun.scm.common.utils.CommonUtil;
import com.xinyirun.scm.common.utils.DateTimeUtil;

import jakarta.servlet.http.HttpServletRequest;

/**
 * json返回值工具类
 * @author zxh
 */
public class ApiResultUtil {

    public static <T> ApiJsonResultAo<T> OK(T data, String message) {
        return ApiJsonResultAo.<T>builder()
            .timestamp(DateTimeUtil.getTime())
            .code(ApiResultEnum.OK.getCode())
//            .system_message(ResultEnum.OK.getMsg())
            .message(message)
            .path(CommonUtil.getRequest().getRequestURL().toString())
//            .method(CommonUtil.getRequest().getMethod())
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
    public static <T> ApiJsonResultAo<T> OK(T data, Integer json_null_out) {
        return ApiJsonResultAo.<T>builder()
                .timestamp(DateTimeUtil.getTime())
                .code(ApiResultEnum.OK.getCode())
//                .system_message(ResultEnum.OK.getMsg())
                .message("调用成功")
                .path(CommonUtil.getRequest().getRequestURL().toString())
//                .method(CommonUtil.getRequest().getMethod())
                .success(true)
//                .json_result_type(json_null_out)
                .data(data)
                .build();
    }

    /**
     * 无错误的返回
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ApiJsonResultAo<T> OK(T data) {
        return ApiResultUtil.OK(data, JsonResultTypeConstants.NORMAL);
    }

    /**
     * 含code的无错误的返回
     * @param data
     * @param enumData
     * @param <T>
     * @return
     */
    public static <T> ApiJsonResultAo<T> OK(T data, ApiResultEnum enumData) {
        return ApiJsonResultAo.<T>builder()
            .timestamp(DateTimeUtil.getTime())
            .code(enumData.getCode())
//            .system_message(enumData.getMsg())
            .message("调用成功")
            .path(CommonUtil.getRequest().getRequestURL().toString())
//            .method(CommonUtil.getRequest().getMethod())
            .success(true)
            .data(data)
            .build();
    }

    public static <T> ApiJsonResultAo<T> NG(
            ApiResultEnum enumData,
            String message,
            HttpServletRequest request) {
            // 判断enumData是否为空，为空则默认为系统异常
            if (enumData == null) {
                enumData = ApiResultEnum.UNKNOWN_ERROR;
                return ApiJsonResultAo.<T>builder()
                        .timestamp(DateTimeUtil.getTime())
                        .code(enumData.getCode())
//                .system_message(enumData.getMsg())
                        .message(message)
                        .path(request.getRequestURL().toString())
//                .method(request.getMethod())
                        .success(false)
                        .data(null)
                        .build();
            } else {
                return ApiJsonResultAo.<T>builder()
                        .timestamp(DateTimeUtil.getTime())
                        .code(enumData.getCode())
//                .system_message(enumData.getMsg())
                        .message(message)
                        .path(request.getRequestURL().toString())
//                .method(request.getMethod())
                        .success(false)
                        .data(null)
                        .build();
            }
    }

}
