package com.xinyirun.scm.ai.config;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * AI聊天记忆配置类
 *
 * 从MeterSphere迁移而来，专门用于配置AI聊天的记忆功能
 * 提供MessageChatMemoryAdvisor Bean的配置
 *
 * @Author: jianxing (原作者)
 * @CreateTime: 2025-05-27  18:36 (原创建时间)
 * @Migration: 2025-09-21 (迁移到scm-ai)
 */
@Configuration
public class AiChatMemoryConfig {

    /**
     * 配置消息聊天记忆顾问
     *
     * 用于管理AI对话的上下文记忆，支持多轮对话的连续性
     *
     * @param scmMessageChatMemory SCM聊天记忆实现
     * @return MessageChatMemoryAdvisor实例
     */
    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ScmMessageChatMemory scmMessageChatMemory) {
        return MessageChatMemoryAdvisor.builder(scmMessageChatMemory).build();
    }
}