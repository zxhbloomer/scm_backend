package com.xinyirun.scm.redis.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {

    public RedisKeyExpirationListener(
        @Qualifier("scm_redis_listener_container") RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    /**
     * redis失效key事件处理
     *
     * @param message
     * @param pattern
     */
    @Override public void onMessage(Message message, byte[] pattern) {
        String expiredKey = message.toString();
        log.info("失效key:" + expiredKey + "====" + "已从redis缓存中自动删除");
    }
}