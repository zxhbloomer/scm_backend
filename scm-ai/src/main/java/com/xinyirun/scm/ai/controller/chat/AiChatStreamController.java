package com.xinyirun.scm.ai.controller.chat;

import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.service.AiConversationService;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.common.annotations.SysLogAnnotion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.security.Principal;

/**
 * AI聊天流式输出控制器
 * 处理WebSocket流式聊天请求
 *
 * @author zxh
 * @since 2025-09-22
 */
@Slf4j
@Controller
public class AiChatStreamController {

    @Resource
    private AiConversationService aiConversationService;

    /**
     * 处理流式聊天消息
     *
     * @param request 聊天请求
     * @param headerAccessor WebSocket消息头访问器
     * @param principal 用户主体信息
     */
    @MessageMapping("/ai/chat/stream")
    @SysLogAnnotion("AI流式聊天消息")
    public void handleStreamChat(@Validated @Payload AIChatRequestVo request,
                                SimpMessageHeaderAccessor headerAccessor,
                                Principal principal) {
        try {
            // 获取WebSocket会话ID
            String sessionId = headerAccessor.getSessionId();

            // 获取用户ID (适配当前系统的SecurityUtil)
            Long operatorId = SecurityUtil.getStaff_id();
            String userId = operatorId != null ? operatorId.toString() : null;

            log.info("接收到流式聊天请求, sessionId: {}, userId: {}, conversationId: {}",
                    sessionId, userId, request.getConversationId());

            // 调用流式聊天服务
            aiConversationService.chatStream(request, userId, sessionId);

        } catch (Exception e) {
            log.error("处理流式聊天消息失败", e);
            // 可以通过WebSocket发送错误消息给客户端
        }
    }

    /**
     * 处理WebSocket连接事件
     *
     * @param headerAccessor WebSocket消息头访问器
     * @param principal 用户主体信息
     */
    @MessageMapping("/ai/connect")
    @SysLogAnnotion("WebSocket连接")
    public void handleConnect(SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        String sessionId = headerAccessor.getSessionId();
        log.info("WebSocket连接建立, sessionId: {}", sessionId);
    }

    /**
     * 处理WebSocket断开事件
     *
     * @param headerAccessor WebSocket消息头访问器
     * @param principal 用户主体信息
     */
    @MessageMapping("/ai/disconnect")
    @SysLogAnnotion("WebSocket断开")
    public void handleDisconnect(SimpMessageHeaderAccessor headerAccessor, Principal principal) {
        String sessionId = headerAccessor.getSessionId();
        log.info("WebSocket连接断开, sessionId: {}", sessionId);
    }

}