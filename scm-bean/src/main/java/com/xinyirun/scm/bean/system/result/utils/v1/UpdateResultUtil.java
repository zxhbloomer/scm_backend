package com.xinyirun.scm.bean.system.result.utils.v1;

import com.xinyirun.scm.bean.system.ao.result.UpdateResultAo;

/**
 * @author zxh
 * @date 2019/9/2
 */
public class UpdateResultUtil {
    /**
     * 没有错误，返回结果
     * @param _data
     * @param <T>
     * @return
     */
    public static <T> UpdateResultAo<T> OK(T _data) {
        return UpdateResultAo.<T>builder()
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
    public static <T> UpdateResultAo<T> NG(T _data, String _message) {
        return UpdateResultAo.<T>builder()
            .data(_data)
            .message(_message)
            .success(false)
            .build();
    }
}
