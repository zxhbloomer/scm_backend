package com.xinyirun.scm.framework.utils.mq;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.system.ao.mqsender.MqSenderAo;
import com.xinyirun.scm.common.utils.reflection.ReflectionUtil;
import com.xinyirun.scm.common.utils.string.convert.Convert;
import org.springframework.amqp.core.Message;

import java.nio.charset.Charset;

/**
 * @ClassName: MessageUtil
 * @Description: 消息工具类
 * @Author: zxh
 * @date: 2019/10/17
 * @Version: 1.0
 */
public class MessageUtil {

    /**
     * 获取mqSenderAo bean
     * @param messageDataObject
     * @return
     */
    public static MqSenderAo getMessageBodyBean(Message messageDataObject){
        String messageData = Convert.str(messageDataObject.getBody(), (Charset)null);
        MqSenderAo mqSenderAo = JSONObject.parseObject(messageData, MqSenderAo.class);
        return mqSenderAo;
    }

    /**
     * 获取 mqSenderAo.MqMessageAo 消息体
     * @param messageDataObject
     * @return
     */
    public static Object getMessageContextBean(Message messageDataObject) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        String messageData = Convert.str(messageDataObject.getBody(), (Charset)null);
        MqSenderAo mqSenderAo = JSONObject.parseObject(messageData, MqSenderAo.class);
//        Object messageContext = ReflectionUtil.getClassBean(mqSenderAo.getMqMessageAo().getMessageBeanClass(), mqSenderAo.getMqMessageAo().getParameterJson());
        Object messageContext = JSONObject.parseObject(mqSenderAo.getMqMessageAo().getParameterJson(), Class.forName(mqSenderAo.getMqMessageAo().getMessageBeanClass()));

        return messageContext;
    }


}
