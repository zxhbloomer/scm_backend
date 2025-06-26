package com.xinyirun.scm.bean.app.result.utils.v1;

import com.xinyirun.scm.bean.app.ao.result.AppDeleteResultAo;
import com.xinyirun.scm.bean.system.ao.result.DeleteResultAo;

/**
 * @author zxh
 * @date 2019/9/2
 */
public class AppDeleteResultUtil {
    /**
     * 没有错误，返回结果
     * @param _data
     * @param <T>
     * @return
     */
    public static <T> AppDeleteResultAo<T> OK(T _data) {
        return AppDeleteResultAo.<T>builder()
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
    public static <T> AppDeleteResultAo<T> NG(T _data, String _message) {
        return AppDeleteResultAo.<T>builder()
            .data(_data)
            .message(_message)
            .success(false)
            .build();
    }
}
