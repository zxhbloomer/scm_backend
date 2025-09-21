package com.xinyirun.scm.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.common.constant.AiConstant;
import com.xinyirun.scm.ai.common.exception.AiBusinessException;
import com.xinyirun.scm.ai.entity.AiConversation;
import com.xinyirun.scm.ai.mapper.AiConversationMapper;
import com.xinyirun.scm.ai.service.IAiConversationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI对话会话服务实现
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Slf4j
@Service
public class AiConversationServiceImpl extends ServiceImpl<AiConversationMapper, AiConversation>
        implements IAiConversationService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiConversation createConversation(String title, Long userId, String modelProvider, String modelName) {
        if (userId == null) {
            throw new AiBusinessException("用户ID不能为空");
        }

        // 设置默认标题
        if (!StringUtils.hasText(title)) {
            title = "新对话 " + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("MM-dd HH:mm"));
        }

        // 设置默认模型
        if (!StringUtils.hasText(modelProvider)) {
            modelProvider = AiConstant.DEFAULT_MODEL_PROVIDER;
        }
        if (!StringUtils.hasText(modelName)) {
            modelName = AiConstant.DEFAULT_MODEL_NAME;
        }

        AiConversation conversation = new AiConversation();
        conversation.setTitle(title);
        conversation.setUser_id(userId);
        conversation.setModel_provider(modelProvider);
        conversation.setModel_name(modelName);
        conversation.setStatus(AiConstant.CONVERSATION_STATUS_ACTIVE);
        conversation.setIs_deleted(AiConstant.NOT_DELETED);

        if (!save(conversation)) {
            throw new AiBusinessException("创建会话失败");
        }

        log.info("创建会话成功, conversationId: {}, userId: {}", conversation.getId(), userId);
        return conversation;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConversationTitle(String conversationId, String title, Long userId) {
        if (conversationId == null || userId == null) {
            throw new AiBusinessException("会话ID和用户ID不能为空");
        }

        if (!StringUtils.hasText(title)) {
            throw new AiBusinessException("会话标题不能为空");
        }

        // 检查会话所有权
        if (!checkConversationOwnership(conversationId, userId)) {
            throw new AiBusinessException("无权限修改该会话");
        }

        LambdaUpdateWrapper<AiConversation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiConversation::getId, conversationId)
                .eq(AiConversation::getUser_id, userId)
                .eq(AiConversation::getIs_deleted, AiConstant.NOT_DELETED)
                .set(AiConversation::getTitle, title);

        boolean result = update(updateWrapper);
        if (result) {
            log.info("更新会话标题成功, conversationId: {}, title: {}", conversationId, title);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateConversationStatus(String conversationId, Integer status, Long userId) {
        if (conversationId == null || userId == null || status == null) {
            throw new AiBusinessException("参数不能为空");
        }

        // 检查会话所有权
        if (!checkConversationOwnership(conversationId, userId)) {
            throw new AiBusinessException("无权限修改该会话");
        }

        LambdaUpdateWrapper<AiConversation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiConversation::getId, conversationId)
                .eq(AiConversation::getUser_id, userId)
                .eq(AiConversation::getIs_deleted, AiConstant.NOT_DELETED)
                .set(AiConversation::getStatus, status);

        boolean result = update(updateWrapper);
        if (result) {
            log.info("更新会话状态成功, conversationId: {}, status: {}", conversationId, status);
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteConversation(String conversationId, Long userId) {
        if (conversationId == null || userId == null) {
            throw new AiBusinessException("会话ID和用户ID不能为空");
        }

        // 检查会话所有权
        if (!checkConversationOwnership(conversationId, userId)) {
            throw new AiBusinessException("无权限删除该会话");
        }

        LambdaUpdateWrapper<AiConversation> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiConversation::getId, conversationId)
                .eq(AiConversation::getUser_id, userId)
                .eq(AiConversation::getIs_deleted, AiConstant.NOT_DELETED)
                .set(AiConversation::getIs_deleted, AiConstant.DELETED);

        boolean result = update(updateWrapper);
        if (result) {
            log.info("删除会话成功, conversationId: {}", conversationId);
        }

        return result;
    }

    @Override
    public IPage<AiConversation> getConversationsByUserId(Long userId, Long current, Long size) {
        if (userId == null) {
            throw new AiBusinessException("用户ID不能为空");
        }

        Page<AiConversation> page = new Page<>(current, size);
        return baseMapper.selectPageByUserId(page, userId);
    }

    @Override
    public List<AiConversation> getConversationsByUserId(Long userId) {
        if (userId == null) {
            throw new AiBusinessException("用户ID不能为空");
        }

        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<AiConversation> getConversationsByUserIdAndStatus(Long userId, Integer status) {
        if (userId == null || status == null) {
            throw new AiBusinessException("用户ID和状态不能为空");
        }

        return baseMapper.selectByUserIdAndStatus(userId, status);
    }

    @Override
    public List<AiConversation> getRecentConversations(Long userId, Integer limit) {
        if (userId == null) {
            throw new AiBusinessException("用户ID不能为空");
        }

        if (limit == null || limit <= 0) {
            limit = 10; // 默认10条
        }

        return baseMapper.selectRecentByUserId(userId, limit);
    }

    @Override
    public Long countConversationsByUserId(Long userId) {
        if (userId == null) {
            throw new AiBusinessException("用户ID不能为空");
        }

        return baseMapper.countByUserId(userId);
    }

    @Override
    public List<AiConversation> searchConversations(Long userId, String keyword) {
        if (userId == null) {
            throw new AiBusinessException("用户ID不能为空");
        }

        if (!StringUtils.hasText(keyword)) {
            return getConversationsByUserId(userId);
        }

        return baseMapper.searchByTitle(userId, keyword);
    }

    @Override
    public List<AiConversation> getConversationsByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        if (userId == null || startTime == null || endTime == null) {
            throw new AiBusinessException("参数不能为空");
        }

        return baseMapper.selectByTimeRange(userId, startTime, endTime);
    }

    @Override
    public Boolean checkConversationOwnership(String conversationId, Long userId) {
        if (conversationId == null || userId == null) {
            return false;
        }

        LambdaQueryWrapper<AiConversation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiConversation::getId, conversationId)
                .eq(AiConversation::getUser_id, userId)
                .eq(AiConversation::getIs_deleted, AiConstant.NOT_DELETED);

        return count(queryWrapper) > 0;
    }

    @Override
    public AiConversation getConversationDetail(String conversationId, Long userId) {
        if (conversationId == null || userId == null) {
            throw new AiBusinessException("会话ID和用户ID不能为空");
        }

        // 检查会话所有权
        if (!checkConversationOwnership(conversationId, userId)) {
            throw new AiBusinessException("无权限访问该会话");
        }

        LambdaQueryWrapper<AiConversation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiConversation::getId, conversationId)
                .eq(AiConversation::getUser_id, userId)
                .eq(AiConversation::getIs_deleted, AiConstant.NOT_DELETED);

        AiConversation conversation = getOne(queryWrapper);
        if (conversation == null) {
            throw new AiBusinessException("会话不存在");
        }

        return conversation;
    }
}