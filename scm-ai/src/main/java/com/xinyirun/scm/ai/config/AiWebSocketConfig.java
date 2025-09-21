package com.xinyirun.scm.ai.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * AI WebSocket配置类
 * 用于实现实时聊天功能
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class AiWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * 配置消息代理
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 启用简单的内存消息代理，用于将消息发送回客户端
        config.enableSimpleBroker("/topic", "/queue");

        // 设置应用程序目的地前缀
        config.setApplicationDestinationPrefixes("/app");

        // 设置用户目的地前缀
        config.setUserDestinationPrefix("/user");

        log.info("AI WebSocket消息代理配置完成");
    }

    /**
     * 配置STOMP端点
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 注册WebSocket端点
        registry.addEndpoint("/ws/ai/chat")
                .setAllowedOriginPatterns("*") // 允许跨域
                .withSockJS(); // 启用SockJS支持

        // 注册流式响应端点
        registry.addEndpoint("/ws/ai/stream")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        log.info("AI WebSocket端点注册完成");
    }
}