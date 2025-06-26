package com.xinyirun.scm.bean.app.result.utils.v1;

import com.xinyirun.scm.bean.app.ao.result.AppUpdateResultAo;
import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;

/**
 * @author zxh
 * @date 2019/9/2
 */
public class AppUpdateResultUtil {
    /**
     * 没有错误，返回结果
     * @param _data
 App * @param <T>
     * @return
     */
    public static <T> AppUpdateResultAo<T> OK(T _data) {
        return AppUpdateResultAo.<T>builder()
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
    public static <T> AppUpdateResultAo<T> NG(T _data, String _message) {
        return AppUpdateResultAo.<T>builder()
            .data(_data)
            .message(_message)
            .success(false)
            .build();
    }
}
