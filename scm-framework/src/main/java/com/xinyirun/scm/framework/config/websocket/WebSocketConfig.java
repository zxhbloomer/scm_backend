package com.xinyirun.scm.framework.config.websocket;

import com.xinyirun.scm.common.constant.WebSocketConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * @author yirun
 * @version
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private WebSocketHandshakeHandler webSocketHandshakeHandler;

    @Autowired
    private AuthHandshakeInterceptor sessionAuthHandshakeInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 配置客户端尝试连接地址
//        registry.addEndpoint("/notice").setAllowedOrigins("*").withSockJS().setInterceptors(httpSessionHandshakeInterceptor());;
        //注册一个Stomp的节点（endpoint）,并指定使用SockJS协议。
        registry.addEndpoint(WebSocketConstants.WEBSOCKET_PATH)
                .addInterceptors(sessionAuthHandshakeInterceptor)
                .setHandshakeHandler(webSocketHandshakeHandler)
                .addInterceptors(new HttpSessionHandshakeInterceptor())
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 设置广播节点
        registry.enableSimpleBroker(WebSocketConstants.WEBSOCKET_BROADCAST_PATH, WebSocketConstants.WEBSOCKET_2USER_PATH);
        // 指定用户发送（一对一）的前缀 /user/
        registry.setUserDestinationPrefix(WebSocketConstants.WEBSOCKET_2USER_PATH);
        // 客户端向服务端发送消息需有/xxx 前缀
        registry.setApplicationDestinationPrefixes(WebSocketConstants.WEBSOCKET_2SERVER_PATH);
    }

}