/**
 * 聊天记忆服务，提供多轮对话的上下文记忆管理功能
 */
package com.xinyirun.scm.ai.memory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ChatMemoryService extends ServiceImpl<ChatMemoryMapper, ChatMemoryEntity> {

    @Autowired
    private ChatMemoryMapper chatMemoryMapper;

    /**
     * 保存聊天记忆
     */
    @Transactional
    public void saveMemory(String conversationId, String content, ChatMemoryType messageType) {
        ChatMemoryEntity memory = ChatMemoryEntity.builder()
                .conversationId(conversationId)
                .content(content)
                .messageType(messageType.name())
                .timestamp(System.currentTimeMillis())
                .build();
        
        this.save(memory);
        log.debug("Saved chat memory for conversation: {}, type: {}", conversationId, messageType);
    }

    /**
     * 批量保存聊天记忆
     */
    @Transactional
    public void saveMemories(String conversationId, List<ChatMemoryItem> memories) {
        List<ChatMemoryEntity> entities = memories.stream()
                .map(memory -> ChatMemoryEntity.builder()
                        .conversationId(conversationId)
                        .content(memory.getContent())
                        .messageType(memory.getType().name())
                        .timestamp(memory.getTimestamp() != null ? memory.getTimestamp() : System.currentTimeMillis())
                        .extra(memory.getExtra())
                        .build())
                .collect(Collectors.toList());
        
        this.saveBatch(entities);
        log.debug("Saved {} chat memories for conversation: {}", entities.size(), conversationId);
    }

    /**
     * 获取会话的所有记忆
     */
    public List<ChatMemoryEntity> getMemoriesByConversationId(String conversationId) {
        return chatMemoryMapper.findByConversationId(conversationId);
    }

    /**
     * 获取会话的最新N条记忆
     */
    public List<ChatMemoryEntity> getLatestMemories(String conversationId, int limit) {
        List<ChatMemoryEntity> memories = chatMemoryMapper.findLatestByConversationId(conversationId, limit);
        // 返回时按时间正序排列
        return memories.stream()
                .sorted((m1, m2) -> Long.compare(m1.getTimestamp(), m2.getTimestamp()))
                .collect(Collectors.toList());
    }

    /**
     * 获取指定类型的记忆
     */
    public List<ChatMemoryEntity> getMemoriesByType(String conversationId, ChatMemoryType messageType) {
        return chatMemoryMapper.findByConversationIdAndType(conversationId, messageType.name());
    }

    /**
     * 获取时间范围内的记忆
     */
    public List<ChatMemoryEntity> getMemoriesByTimeRange(String conversationId, Long startTime, Long endTime) {
        return chatMemoryMapper.findByConversationIdAndTimeRange(conversationId, startTime, endTime);
    }

    /**
     * 清除会话的所有记忆
     */
    @Transactional
    public void clearMemories(String conversationId) {
        chatMemoryMapper.deleteByConversationId(conversationId);
        log.info("Cleared all memories for conversation: {}", conversationId);
    }

    /**
     * 物理删除会话记忆
     */
    @Transactional
    public void hardDeleteMemories(String conversationId) {
        chatMemoryMapper.hardDeleteByConversationId(conversationId);
        log.info("Hard deleted all memories for conversation: {}", conversationId);
    }

    /**
     * 统计会话记忆数量
     */
    public Long countMemories(String conversationId) {
        return chatMemoryMapper.countByConversationId(conversationId);
    }

    /**
     * 获取所有活跃的会话ID
     */
    public List<String> getAllActiveConversationIds() {
        return chatMemoryMapper.findAllConversationIds();
    }

    /**
     * 清理过期记忆（超过指定天数的记忆）
     */
    @Transactional
    public int cleanExpiredMemories(int daysToKeep) {
        long expireTime = System.currentTimeMillis() - (daysToKeep * 24L * 60 * 60 * 1000);
        int deleted = chatMemoryMapper.deleteExpiredMemories(expireTime);
        log.info("Cleaned {} expired memories older than {} days", deleted, daysToKeep);
        return deleted;
    }

    /**
     * 获取会话的上下文摘要（用于AI对话）
     */
    public String getContextSummary(String conversationId, int maxMessages) {
        List<ChatMemoryEntity> memories = getLatestMemories(conversationId, maxMessages);
        
        StringBuilder context = new StringBuilder();
        for (ChatMemoryEntity memory : memories) {
            context.append(memory.getMessageType()).append(": ").append(memory.getContent()).append("\n");
        }
        
        return context.toString();
    }

    /**
     * 检查会话是否有记忆
     */
    public boolean hasMemories(String conversationId) {
        return countMemories(conversationId) > 0;
    }

    /**
     * 构建对话上下文，用于AI推理
     */
    public ConversationContext buildConversationContext(String conversationId, int contextWindow) {
        List<ChatMemoryEntity> memories = getLatestMemories(conversationId, contextWindow);
        
        ConversationContext context = new ConversationContext();
        context.setConversationId(conversationId);
        context.setMemories(memories);
        context.setTotalMemoryCount(countMemories(conversationId));
        
        return context;
    }
}