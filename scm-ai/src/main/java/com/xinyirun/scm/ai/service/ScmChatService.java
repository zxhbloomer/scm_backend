package com.xinyirun.scm.ai.service;

import com.xinyirun.scm.ai.entity.AiChatRecord;
import com.xinyirun.scm.ai.repository.AiChatRecordRepository;
import com.xinyirun.scm.ai.config.properties.ScmAiProperties;
import com.xinyirun.scm.ai.common.exception.AiServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SCM AI聊天服务类
 * 
 * 提供AI聊天功能的核心业务逻辑，包括：
 * 1. 多厂商AI模型调用与故障转移
 * 2. 聊天记录的存储和查询
 * 3. 文件上传处理
 * 4. 会话管理
 * 5. 性能监控和统计
 * 
 * @author SCM-AI模块
 * @since 1.0.0
 */
@Slf4j
@Service
public class ScmChatService {

    @Autowired
    private AiChatRecordRepository chatRecordRepository;

    @Autowired
    private ScmAiProperties aiProperties;

    @Autowired
    private ScmFileUploadService fileUploadService;

    @Autowired
    private ScmTokenUsageService tokenUsageService;

    /**
     * 主要的ChatClient - 通过配置类注入
     */
    @Autowired
    @Qualifier("primaryChatClient")
    private ChatClient primaryChatClient;

    /**
     * 备用的ChatClient - 通过配置类注入
     */
    @Autowired
    @Qualifier("fallbackChatClient")
    private ChatClient fallbackChatClient;

    /**
     * 发送聊天消息（带故障转移）
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @param sessionId 会话ID（可选，为空时自动生成）
     * @param message 消息内容
     * @param files 附件文件（可选）
     * @return AI回复内容
     */
    public String sendMessage(String tenantId, Long userId, String pageCode, 
                            String sessionId, String message, List<MultipartFile> files) {
        
        log.info("用户发送消息 - 租户: {}, 用户: {}, 页面: {}, 会话: {}", tenantId, userId, pageCode, sessionId);
        
        // 1. 生成或使用现有会话ID
        if (sessionId == null || sessionId.trim().isEmpty()) {
            sessionId = AiChatRecord.generateSessionId(tenantId, userId, pageCode);
            log.debug("生成新会话ID: {}", sessionId);
        }

        // 2. 保存用户消息
        AiChatRecord userRecord = saveUserMessage(tenantId, userId, pageCode, sessionId, message, files);
        
        // 3. 调用AI获取回复（带故障转移）
        String aiResponse;
        AiChatRecord.AiModelInfo aiInfo = new AiChatRecord.AiModelInfo();
        AiChatRecord.PerformanceStats performanceStats = new AiChatRecord.PerformanceStats();
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 尝试主要厂商
            aiResponse = callAiWithRetry(primaryChatClient, message, aiProperties.getPrimaryProvider(), aiInfo, performanceStats);
            aiInfo.setUseFallback(false);
            log.info("主要AI厂商响应成功: {}", aiProperties.getPrimaryProvider());
            
        } catch (Exception e) {
            log.warn("主要AI厂商失败，尝试备用厂商 - 错误: {}", e.getMessage());
            
            try {
                // 尝试备用厂商
                aiResponse = callAiWithRetry(fallbackChatClient, message, aiProperties.getFallbackProvider(), aiInfo, performanceStats);
                aiInfo.setUseFallback(true);
                log.info("备用AI厂商响应成功: {}", aiProperties.getFallbackProvider());
                
            } catch (Exception fallbackException) {
                log.error("所有AI厂商均失败 - 主要错误: {}, 备用错误: {}", e.getMessage(), fallbackException.getMessage());
                
                // 保存失败记录
                saveAiErrorMessage(tenantId, userId, pageCode, sessionId, userRecord.getId(), 
                                 e.getMessage() + "; 备用失败: " + fallbackException.getMessage(), 
                                 aiInfo, performanceStats, System.currentTimeMillis() - startTime);
                
                throw new AiServiceUnavailableException("AI服务暂时不可用，请稍后重试");
            }
        }
        
        // 4. 保存AI回复消息
        saveAiMessage(tenantId, userId, pageCode, sessionId, aiResponse, aiInfo, performanceStats, 
                     System.currentTimeMillis() - startTime);
        
        return aiResponse;
    }

    /**
     * 调用AI服务（带重试机制）
     */
    private String callAiWithRetry(ChatClient chatClient, String message, String provider, 
                                 AiChatRecord.AiModelInfo aiInfo, AiChatRecord.PerformanceStats performanceStats) {
        
        int maxRetries = aiProperties.getProviders().getOrDefault(provider, new ScmAiProperties.ProviderConfig()).getMaxRetries();
        Exception lastException = null;
        
        for (int i = 0; i <= maxRetries; i++) {
            try {
                long networkStart = System.currentTimeMillis();
                
                // 调用AI
                ChatResponse response = chatClient.prompt()
                    .user(message)
                    .call()
                    .chatResponse();
                
                long networkTime = System.currentTimeMillis() - networkStart;
                
                // 设置性能统计
                performanceStats.setNetworkTime(networkTime);
                performanceStats.setRetryCount(i);
                
                // 设置AI信息
                aiInfo.setProvider(provider);
                if (response.getMetadata() != null) {
                    aiInfo.setModelName(response.getMetadata().getModel());
                    
                    // 使用Token统计服务记录Token使用情况
                    tokenUsageService.recordTokenUsage(response.getMetadata(), aiInfo, performanceStats);
                }
                
                return response.getResult().getOutput().getText();
                
            } catch (Exception e) {
                lastException = e;
                log.warn("AI调用失败，第{}次重试 - 厂商: {}, 错误: {}", i + 1, provider, e.getMessage());
                
                if (i < maxRetries) {
                    try {
                        // 指数退避重试
                        Thread.sleep(1000L * (1L << i));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        throw new AiServiceUnavailableException("AI厂商 " + provider + " 调用失败: " + 
                                              (lastException != null ? lastException.getMessage() : "未知错误"));
    }

    /**
     * 保存用户消息
     */
    private AiChatRecord saveUserMessage(String tenantId, Long userId, String pageCode, 
                                       String sessionId, String message, List<MultipartFile> files) {
        
        AiChatRecord record = new AiChatRecord()
                .setTenantId(tenantId)
                .setUserId(userId)
                .setPageCode(pageCode)
                .setSessionId(sessionId)
                .setMessageType(AiChatRecord.MESSAGE_TYPE_USER)
                .setContent(message)
                .setStatus(AiChatRecord.STATUS_SUCCESS)
                .setCreateTime(LocalDateTime.now())
                .setCreateUserId(userId);

        // 处理文件上传
        if (files != null && !files.isEmpty()) {
            List<AiChatRecord.AttachmentInfo> attachments = new ArrayList<>();
            
            for (MultipartFile file : files) {
                try {
                    var attachmentInfo = fileUploadService.uploadChatFile(tenantId, userId, sessionId, file);
                    attachments.add(attachmentInfo);
                    log.debug("文件上传成功: {}", file.getOriginalFilename());
                    
                } catch (Exception e) {
                    log.error("文件上传失败: {} - 错误: {}", file.getOriginalFilename(), e.getMessage());
                    // 继续处理其他文件，不中断聊天流程
                }
            }
            
            if (!attachments.isEmpty()) {
                record.setAttachments(attachments);
            }
        }

        return chatRecordRepository.save(record);
    }

    /**
     * 保存AI回复消息
     */
    private void saveAiMessage(String tenantId, Long userId, String pageCode, String sessionId, 
                             String response, AiChatRecord.AiModelInfo aiInfo, 
                             AiChatRecord.PerformanceStats performanceStats, long totalTime) {
        
        performanceStats.setProcessingTime(totalTime);
        
        AiChatRecord record = new AiChatRecord()
                .setTenantId(tenantId)
                .setUserId(userId)
                .setPageCode(pageCode)
                .setSessionId(sessionId)
                .setMessageType(AiChatRecord.MESSAGE_TYPE_AI)
                .setContent(response)
                .setAiInfo(aiInfo)
                .setPerformanceStats(performanceStats)
                .setStatus(AiChatRecord.STATUS_SUCCESS)
                .setCreateTime(LocalDateTime.now())
                .setCreateUserId(userId);

        chatRecordRepository.save(record);
        
        log.debug("AI回复已保存 - 会话: {}, 耗时: {}ms, tokens: {}", 
                 sessionId, totalTime, performanceStats.getTokenUsage());
    }

    /**
     * 保存AI错误消息
     */
    private void saveAiErrorMessage(String tenantId, Long userId, String pageCode, String sessionId, 
                                  String userRecordId, String errorMessage, AiChatRecord.AiModelInfo aiInfo, 
                                  AiChatRecord.PerformanceStats performanceStats, long totalTime) {
        
        performanceStats.setProcessingTime(totalTime);
        
        AiChatRecord record = new AiChatRecord()
                .setTenantId(tenantId)
                .setUserId(userId)
                .setPageCode(pageCode)
                .setSessionId(sessionId)
                .setMessageType(AiChatRecord.MESSAGE_TYPE_AI)
                .setContent("抱歉，AI服务暂时不可用，请稍后重试。")
                .setAiInfo(aiInfo)
                .setPerformanceStats(performanceStats)
                .setStatus(AiChatRecord.STATUS_FAILED)
                .setErrorMessage(errorMessage)
                .setCreateTime(LocalDateTime.now())
                .setCreateUserId(userId);

        chatRecordRepository.save(record);
    }

    /**
     * 获取聊天历史记录（分页）
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @param page 页号（从0开始）
     * @param size 每页大小
     * @return 分页聊天记录
     */
    public Page<AiChatRecord> getChatHistory(String tenantId, Long userId, String pageCode, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return chatRecordRepository.findByTenantIdAndUserIdAndPageCodeOrderByCreateTimeDesc(
                tenantId, userId, pageCode, pageable);
    }

    /**
     * 获取会话详细记录
     * 
     * @param sessionId 会话ID
     * @return 会话聊天记录列表（按时间正序）
     */
    public List<AiChatRecord> getSessionHistory(String sessionId) {
        return chatRecordRepository.findBySessionIdOrderByCreateTimeAsc(sessionId);
    }

    /**
     * 获取用户最近的聊天上下文
     * 用于AI理解对话历史
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @param limit 限制数量（默认10）
     * @return 最近的聊天记录
     */
    public List<AiChatRecord> getRecentContext(String tenantId, Long userId, String pageCode, int limit) {
        if (limit <= 0) {
            limit = 10;
        }
        return chatRecordRepository.findRecentChatsByTenantIdAndUserIdAndPageCode(tenantId, userId, pageCode, limit);
    }

    /**
     * 删除会话
     * 
     * @param sessionId 会话ID
     * @return 删除的记录数量
     */
    public long deleteSession(String sessionId) {
        log.info("删除会话: {}", sessionId);
        return chatRecordRepository.deleteBySessionId(sessionId);
    }

    /**
     * 获取聊天统计信息
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @return 统计信息
     */
    public ChatStatistics getChatStatistics(String tenantId, Long userId, String pageCode) {
        long totalMessages = chatRecordRepository.countByTenantIdAndUserIdAndPageCode(tenantId, userId, pageCode);
        
        // 可以扩展更多统计信息
        ChatStatistics stats = new ChatStatistics();
        stats.setTotalMessages(totalMessages);
        
        return stats;
    }

    /**
     * 简化的聊天方法（用于向后兼容）
     */
    public String chatWithFallback(String message) {
        // 使用默认参数调用完整方法
        return sendMessage("default", 1L, "system", null, message, null);
    }

    /**
     * 聊天统计信息类
     */
    public static class ChatStatistics {
        private long totalMessages;
        private long totalSessions;
        private long averageResponseTime;
        
        // getters and setters
        public long getTotalMessages() { return totalMessages; }
        public void setTotalMessages(long totalMessages) { this.totalMessages = totalMessages; }
        
        public long getTotalSessions() { return totalSessions; }
        public void setTotalSessions(long totalSessions) { this.totalSessions = totalSessions; }
        
        public long getAverageResponseTime() { return averageResponseTime; }
        public void setAverageResponseTime(long averageResponseTime) { this.averageResponseTime = averageResponseTime; }
    }
}