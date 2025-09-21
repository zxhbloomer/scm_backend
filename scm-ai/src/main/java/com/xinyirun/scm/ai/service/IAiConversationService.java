package com.xinyirun.scm.ai.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.xinyirun.scm.ai.entity.AiConversation;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI对话会话服务接口
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
public interface IAiConversationService extends IService<AiConversation> {

    /**
     * 创建会话
     *
     * @param title 会话标题
     * @param userId 用户ID
     * @param modelProvider 模型提供商
     * @param modelName 模型名称
     * @return 会话实体
     */
    AiConversation createConversation(String title, Long userId, String modelProvider, String modelName);

    /**
     * 更新会话标题
     *
     * @param conversationId 会话ID
     * @param title 新标题
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean updateConversationTitle(String conversationId, String title, Long userId);

    /**
     * 更新会话状态
     *
     * @param conversationId 会话ID
     * @param status 新状态
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean updateConversationStatus(String conversationId, Integer status, Long userId);

    /**
     * 删除会话（软删除）
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 是否成功
     */
    Boolean deleteConversation(String conversationId, Long userId);

    /**
     * 根据用户ID分页查询会话
     *
     * @param userId 用户ID
     * @param current 当前页
     * @param size 每页大小
     * @return 分页结果
     */
    IPage<AiConversation> getConversationsByUserId(Long userId, Long current, Long size);

    /**
     * 根据用户ID查询会话列表
     *
     * @param userId 用户ID
     * @return 会话列表
     */
    List<AiConversation> getConversationsByUserId(Long userId);

    /**
     * 根据用户ID和状态查询会话
     *
     * @param userId 用户ID
     * @param status 会话状态
     * @return 会话列表
     */
    List<AiConversation> getConversationsByUserIdAndStatus(Long userId, Integer status);

    /**
     * 获取用户最近的会话
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 最近会话列表
     */
    List<AiConversation> getRecentConversations(Long userId, Integer limit);

    /**
     * 统计用户会话总数
     *
     * @param userId 用户ID
     * @return 会话总数
     */
    Long countConversationsByUserId(Long userId);

    /**
     * 根据关键字搜索会话
     *
     * @param userId 用户ID
     * @param keyword 关键字
     * @return 会话列表
     */
    List<AiConversation> searchConversations(Long userId, String keyword);

    /**
     * 查询指定时间范围内的会话
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 会话列表
     */
    List<AiConversation> getConversationsByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 检查会话是否属于用户
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 是否属于用户
     */
    Boolean checkConversationOwnership(String conversationId, Long userId);

    /**
     * 获取会话详情（包含权限检查）
     *
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @return 会话实体
     */
    AiConversation getConversationDetail(String conversationId, Long userId);
}