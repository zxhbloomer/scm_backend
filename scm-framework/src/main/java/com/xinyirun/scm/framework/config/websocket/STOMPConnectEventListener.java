package com.xinyirun.scm.framework.config.websocket;


import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;


/**
 * STOMP监听类
 */
@Slf4j
@Component
public class STOMPConnectEventListener { //implements ApplicationListener<SessionConnectEvent> {

    //连接成功
    @EventListener
    public void onConnectEvent(SessionConnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        if (sha.getUser() == null) {
            return;
        }
        String myPrincipal = sha.getUser().getName();
        log.info("onConnectEvent ====" + myPrincipal);
    }

    //连接断开
    @EventListener
    public void onDisconnectEvent(SessionDisconnectEvent event) {
        StompHeaderAccessor sha = StompHeaderAccessor.wrap(event.getMessage());
        if (sha.getUser() == null) {
            return;
        }
        String myPrincipal = sha.getUser().getName();
        log.info("onDisconnectEvent  ====" + myPrincipal);
    }
}

