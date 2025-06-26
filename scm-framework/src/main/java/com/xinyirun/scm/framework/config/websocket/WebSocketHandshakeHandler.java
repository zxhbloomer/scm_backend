package com.xinyirun.scm.framework.config.websocket;

import com.xinyirun.scm.common.constant.WebSocketConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;

/**
 * websocket 握手
 */
@Slf4j
@Component
    public class WebSocketHandshakeHandler extends DefaultHandshakeHandler {

    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        if(attributes != null && attributes.get(WebSocketConstants.WEBSOCKET_SESSION)!=null){
            return (Principal)attributes.get(WebSocketConstants.WEBSOCKET_SESSION);
        }
        log.error("未登录系统，禁止登录websocket!");
        return null;
    }

}
