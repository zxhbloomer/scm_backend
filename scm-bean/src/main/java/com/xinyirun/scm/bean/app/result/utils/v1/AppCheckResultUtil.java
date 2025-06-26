package com.xinyirun.scm.bean.app.result.utils.v1;


import com.xinyirun.scm.bean.app.ao.result.AppCheckResultAo;

/**
 * @author zxh
 * @date 2019/8/30
 */
public class AppCheckResultUtil {

    /**
     * check无误
     * @return
     */
    public static AppCheckResultAo OK() {
        return AppCheckResultAo.builder()
            .data(null)
            .message("")
            .success(true)
            .build();
    }

    /**
     * check有错
     * @return
     */
    public static AppCheckResultAo NG(String msg) {
        return AppCheckResultAo.builder()
            .data(null)
            .message(msg)
            .success(false)
            .build();
    }

    /**
     * check有错
     * @return
     */
    public static AppCheckResultAo NG(String msg, Object _data ) {
        return AppCheckResultAo.builder()
            .data(_data)
            .message(msg)
            .success(false)
            .build();
    }

}
