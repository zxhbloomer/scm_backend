/**
 * Thread工具类，提供Thread对象构建和操作的便捷方法
 */
package com.xinyirun.scm.ai.thread;

import com.xinyirun.scm.ai.rbac.user.UserProtobuf;
import com.xinyirun.scm.ai.thread.enums.ThreadTypeEnum;

public class ThreadUtils {
    
    public static ThreadProtobuf getThreadProtobuf(String topic, ThreadTypeEnum type, UserProtobuf user) {
        return ThreadProtobuf.builder()
                .topic(topic)
                .type(type)
                .user(user)
        .build();
    }
}