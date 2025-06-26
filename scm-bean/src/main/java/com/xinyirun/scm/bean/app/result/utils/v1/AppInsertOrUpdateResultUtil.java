package com.xinyirun.scm.bean.app.result.utils.v1;

import com.xinyirun.scm.bean.app.ao.result.AppInsertOrUpdateResultAo;
import com.xinyirun.scm.bean.system.ao.result.InsertOrUpdateResultAo;

/**
 * @author zxh
 * @date 2019/9/2
 */
public class AppInsertOrUpdateResultUtil {
    /**
     * 没有错误，返回结果
     * @param _data
     * @param <T>
     * @return
     */
    public static <T> AppInsertOrUpdateResultAo<T> OK(T _data) {
        return AppInsertOrUpdateResultAo.<T>builder()
            .data(_data)
            .message("")
            .success(true)
            .build();
    }

    /**
     * 返回错误
     * @param _data
     * @param _message
     * @param <T>
     * @return
     */
    public static <T> AppInsertOrUpdateResultAo<T> NG(T _data, String _message) {
        return AppInsertOrUpdateResultAo.<T>builder()
            .data(_data)
            .message(_message)
            .success(false)
            .build();
    }
}
