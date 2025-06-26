package com.xinyirun.scm.mq.rabbitmq.callback.manager.config.tenant;

import java.util.List;

/**
 * @ClassName: TenantMqCallbackInterface
 * @Description: 回调接口
 * @Author: zxh
 * @date: 2019/10/16
 * @Version: 1.0
 */
public interface TenantMqCallbackInterface {
    /**
     * 实现回调的内容
     * @param parameterClass
     * @param parameter
     */
    void mqCallBackTestFunction(String parameterClass , String parameter);


    void mqCallBackTestFunction(List<String> callbackBean);
}
