package com.xinyirun.scm.bean.system.result.utils.v1;

import com.xinyirun.scm.bean.system.ao.result.CheckResultAo;

/**
 * @author zxh
 * @date 2019/8/30
 */
public class CheckResultUtil {

    /**
     * check无误
     * @return
     */
    public static CheckResultAo OK() {
        return CheckResultAo.builder()
            .data(null)
            .message("")
            .success(true)
            .build();
    }

    /**
     * check有错
     * @return
     */
    public static CheckResultAo NG(String msg) {
        return CheckResultAo.builder()
            .data(null)
            .message(msg)
            .success(false)
            .build();
    }

    /**
     * check有错
     * @return
     */
    public static CheckResultAo NG(String msg, Object _data ) {
        return CheckResultAo.builder()
            .data(_data)
            .message(msg)
            .success(false)
            .build();
    }

}
