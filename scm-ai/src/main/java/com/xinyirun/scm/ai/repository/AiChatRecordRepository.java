package com.xinyirun.scm.ai.repository;

import com.xinyirun.scm.ai.entity.AiChatRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * AI聊天记录Repository接口
 * 
 * 基于Spring Data MongoDB提供数据访问层功能
 * 支持复杂查询和分页操作
 * 
 * @author SCM-AI模块
 * @since 1.0.0
 */
@Repository
public interface AiChatRecordRepository extends MongoRepository<AiChatRecord, String> {

    /**
     * 根据租户ID、用户ID、页面代码查询聊天记录
     * 按创建时间倒序排列，支持分页
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @param pageable 分页参数
     * @return 分页聊天记录
     */
    Page<AiChatRecord> findByTenantIdAndUserIdAndPageCodeOrderByCreateTimeDesc(
            String tenantId, Long userId, String pageCode, Pageable pageable);

    /**
     * 根据会话ID查询聊天记录
     * 按创建时间正序排列，用于显示完整对话历史
     * 
     * @param sessionId 会话ID
     * @return 聊天记录列表
     */
    List<AiChatRecord> findBySessionIdOrderByCreateTimeAsc(String sessionId);

    /**
     * 根据租户ID和用户ID查询最近的聊天记录
     * 跨页面查询，用于用户聊天历史概览
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 分页聊天记录
     */
    Page<AiChatRecord> findByTenantIdAndUserIdOrderByCreateTimeDesc(
            String tenantId, Long userId, Pageable pageable);

    /**
     * 根据租户ID、用户ID、页面代码查询最新的会话ID
     * 用于获取用户在特定页面的最新会话
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @return 最新会话ID
     */
    @Query(value = "{'tenantId': ?0, 'userId': ?1, 'pageCode': ?2}", 
           fields = "{'sessionId': 1}", 
           sort = "{'createTime': -1}")
    Optional<AiChatRecord> findLatestSessionByTenantIdAndUserIdAndPageCode(
            String tenantId, Long userId, String pageCode);

    /**
     * 统计用户在指定页面的消息总数
     * 用于显示聊天统计信息
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @return 消息总数
     */
    long countByTenantIdAndUserIdAndPageCode(String tenantId, Long userId, String pageCode);

    /**
     * 统计指定会话的消息数量
     * 
     * @param sessionId 会话ID
     * @return 消息数量
     */
    long countBySessionId(String sessionId);

    /**
     * 查询指定时间范围内的聊天记录
     * 用于数据分析和报表统计
     * 
     * @param tenantId 租户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 分页聊天记录
     */
    Page<AiChatRecord> findByTenantIdAndCreateTimeBetweenOrderByCreateTimeDesc(
            String tenantId, LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    /**
     * 根据消息状态查询记录
     * 用于查询失败或待处理的消息
     * 
     * @param tenantId 租户ID
     * @param status 消息状态
     * @param pageable 分页参数
     * @return 分页聊天记录
     */
    Page<AiChatRecord> findByTenantIdAndStatusOrderByCreateTimeDesc(
            String tenantId, String status, Pageable pageable);

    /**
     * 查询包含附件的聊天记录
     * 用于文件管理和清理
     * 
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 分页聊天记录
     */
    @Query("{'tenantId': ?0, 'attachments': {'$exists': true, '$ne': []}}")
    Page<AiChatRecord> findByTenantIdWithAttachments(String tenantId, Pageable pageable);

    /**
     * 根据AI提供商统计使用量
     * 用于AI厂商使用情况分析
     * 
     * @param tenantId 租户ID
     * @param provider AI提供商
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 记录数量
     */
    @Query("{'tenantId': ?0, 'aiInfo.provider': ?1, 'createTime': {'$gte': ?2, '$lte': ?3}}")
    long countByTenantIdAndAiProviderAndTimeRange(
            String tenantId, String provider, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询用户最近的N条聊天记录
     * 用于快速获取聊天上下文
     * 
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param pageCode 页面代码
     * @param limit 限制数量
     * @return 聊天记录列表
     */
    @Query(value = "{'tenantId': ?0, 'userId': ?1, 'pageCode': ?2}", 
           sort = "{'createTime': -1}")
    List<AiChatRecord> findRecentChatsByTenantIdAndUserIdAndPageCode(
            String tenantId, Long userId, String pageCode, int limit);

    /**
     * 删除指定时间之前的聊天记录
     * 用于定时清理历史数据
     * 
     * @param beforeTime 截止时间
     * @return 删除的记录数量
     */
    long deleteByCreateTimeBefore(LocalDateTime beforeTime);

    /**
     * 根据会话ID删除聊天记录
     * 用于删除完整会话
     * 
     * @param sessionId 会话ID
     * @return 删除的记录数量
     */
    long deleteBySessionId(String sessionId);

    /**
     * 查询失败的聊天记录
     * 用于错误监控和重试处理
     * 
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 分页聊天记录
     */
    default Page<AiChatRecord> findFailedRecords(String tenantId, Pageable pageable) {
        return findByTenantIdAndStatusOrderByCreateTimeDesc(tenantId, AiChatRecord.STATUS_FAILED, pageable);
    }

    /**
     * 查询待处理的聊天记录
     * 用于处理队列监控
     * 
     * @param tenantId 租户ID
     * @param pageable 分页参数
     * @return 分页聊天记录
     */
    default Page<AiChatRecord> findPendingRecords(String tenantId, Pageable pageable) {
        return findByTenantIdAndStatusOrderByCreateTimeDesc(tenantId, AiChatRecord.STATUS_PENDING, pageable);
    }
}