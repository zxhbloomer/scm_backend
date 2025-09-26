package com.xinyirun.scm.ai.config.event;

import com.xinyirun.scm.ai.core.service.chat.AiConversationService;
import com.xinyirun.scm.common.event.UserConversationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户AI会话事件监听器
 *
 * @author zxh
 */
@Component
@Slf4j
public class UserConversationEventListener {

    @Autowired
    private AiConversationService aiConversationService;

    @EventListener
    @Transactional
    public void handleUserConversationEvent(UserConversationEvent event) {
        try {
            // 通过service层在chat-ai数据库中创建ai_conversation记录
            aiConversationService.createConversationForUser(
                    event.getConvUuid(),
                    event.getUserId(),
                    event.getUserName(),
                    event.getTenant()
            );

            log.info("AI会话记录创建成功：userId={}, convUuid={}",
                    event.getUserId(), event.getConvUuid());
        } catch (Exception e) {
            log.error("创建AI会话记录失败：userId={}, convUuid={}",
                     event.getUserId(), event.getConvUuid(), e);
        }
    }
}