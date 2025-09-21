package com.xinyirun.scm.ai.config;

import com.xinyirun.scm.ai.bean.entity.AiConversationContent;
import com.xinyirun.scm.ai.core.mapper.ExtAiConversationContentMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SCM聊天记忆实现类
 *
 * 使用 MessageWindowChatMemory 只能记忆和持久化 max_messages 条消息
 * 该自定义类，能持久化所有消息，并且设置 ai 记忆消息的条数
 *
 * 从MeterSphere MsMessageChatMemory迁移而来，适配scm-ai架构
 *
 * @Author: 原作者未知
 * @Migration: 2025-09-21 (迁移到scm-ai)
 */
@Slf4j
@Component
@Transactional(rollbackFor = Exception.class)
public class ScmMessageChatMemory implements ChatMemory {

    /**
     * 默认记忆10条消息
     */
    private static final int DEFAULT_MAX_MESSAGES = 10;

    @Resource
    private ExtAiConversationContentMapper extAiConversationContentMapper;

    /**
     * 添加消息到记忆中
     *
     * 注意：这里不处理，手动保存原始的提示词
     * 消息的持久化由AiChatBaseService负责
     *
     * @param conversationId 对话ID
     * @param messages 消息列表
     */
    @Override
    public void add(String conversationId, List<Message> messages) {
        log.debug("ScmMessageChatMemory.add() called - conversationId: {}, messageCount: {}",
                conversationId, messages.size());
        // 这里不处理，手动保存原始的提示词
        // 实际的消息持久化由AiChatBaseService的saveUserConversationContent和saveAssistantConversationContent方法负责
    }

    /**
     * 获取对话记忆中的消息
     *
     * 从数据库中获取最近的几条聊天记录，用于AI记忆
     *
     * @param conversationId 对话ID
     * @return 消息列表
     */
    @Override
    public List<Message> get(String conversationId) {
        try {
            log.debug("ScmMessageChatMemory.get() called - conversationId: {}", conversationId);

            // 获取最近的几条聊天，进行记忆
            List<AiConversationContent> contents = extAiConversationContentMapper
                    .selectLastByConversationIdByLimit(conversationId, DEFAULT_MAX_MESSAGES);

            // 反转列表，按时间正序排列
            Collections.reverse(contents);

            // 先持久化了提示词，会重复，这里去掉最后一条
            if (!contents.isEmpty()) {
                contents.remove(contents.size() - 1);
            }

            List<Message> messages = contents.stream()
                    .map(this::convertToMessage)
                    .collect(Collectors.toList());

            log.debug("获取聊天记忆成功 - conversationId: {}, 记忆条数: {}", conversationId, messages.size());
            return messages;

        } catch (Exception e) {
            log.error("获取聊天记忆失败 - conversationId: {}, 错误: {}", conversationId, e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    /**
     * 清除对话记忆
     *
     * @param conversationId 对话ID
     */
    @Override
    public void clear(String conversationId) {
        log.debug("ScmMessageChatMemory.clear() called - conversationId: {}", conversationId);
        // 目前不实现清除功能
        // 如果需要清除，可以通过AiConversationService的delete方法删除整个对话
    }

    /**
     * 将AiConversationContent转换为Spring AI的Message
     *
     * @param conversationContent 对话内容实体
     * @return Message实例
     */
    private Message convertToMessage(AiConversationContent conversationContent) {
        String content = conversationContent.getContent();
        String type = conversationContent.getType();

        // 根据消息类型创建对应的Message实例
        switch (type) {
            case AiConversationContent.TYPE_USER:
                return new UserMessage(content);
            case AiConversationContent.TYPE_AI:
                return new AssistantMessage(content);
            case AiConversationContent.TYPE_SYSTEM:
                return new SystemMessage(content);
            default:
                log.warn("未知的消息类型: {}, 默认作为用户消息处理", type);
                return new UserMessage(content);
        }
    }

    /**
     * 获取记忆消息的最大条数
     *
     * @return 最大消息条数
     */
    public int getMaxMessages() {
        return DEFAULT_MAX_MESSAGES;
    }

    /**
     * 检查对话是否有记忆
     *
     * @param conversationId 对话ID
     * @return true如果有记忆消息
     */
    public boolean hasMemory(String conversationId) {
        try {
            List<Message> messages = get(conversationId);
            return !messages.isEmpty();
        } catch (Exception e) {
            log.error("检查对话记忆失败 - conversationId: {}", conversationId, e);
            return false;
        }
    }

    /**
     * 获取对话记忆的消息数量
     *
     * @param conversationId 对话ID
     * @return 消息数量
     */
    public int getMemorySize(String conversationId) {
        try {
            List<Message> messages = get(conversationId);
            return messages.size();
        } catch (Exception e) {
            log.error("获取对话记忆大小失败 - conversationId: {}", conversationId, e);
            return 0;
        }
    }
}