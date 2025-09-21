package com.xinyirun.scm.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.common.constant.AiConstant;
import com.xinyirun.scm.ai.common.exception.AiBusinessException;
import com.xinyirun.scm.ai.entity.AiConversationContent;
import com.xinyirun.scm.ai.mapper.AiConversationContentMapper;
import com.xinyirun.scm.ai.service.IAiConversationContentService;
import com.xinyirun.scm.ai.service.IAiConversationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI对话内容服务实现
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Slf4j
@Service
public class AiConversationContentServiceImpl extends ServiceImpl<AiConversationContentMapper, AiConversationContent>
        implements IAiConversationContentService {

    @Autowired
    private IAiConversationService conversationService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContent addUserMessage(String conversationId, String content, Long userId) {
        if (conversationId == null || userId == null) {
            throw new AiBusinessException("会话ID和用户ID不能为空");
        }

        if (!StringUtils.hasText(content)) {
            throw new AiBusinessException("消息内容不能为空");
        }

        // 检查会话权限
        if (!conversationService.checkConversationOwnership(conversationId, userId)) {
            throw new AiBusinessException("无权限访问该会话");
        }

        AiConversationContent contentEntity = new AiConversationContent();
        contentEntity.setConversation_id(conversationId);
        contentEntity.setType(AiConstant.MESSAGE_TYPE_USER);
        contentEntity.setContent(content);
        contentEntity.setIs_deleted(AiConstant.NOT_DELETED);

        if (!save(contentEntity)) {
            throw new AiBusinessException("保存用户消息失败");
        }

        log.info("添加用户消息成功, conversationId: {}, messageId: {}", conversationId, contentEntity.getId());
        return contentEntity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContent addAiMessage(String conversationId, String content, String messageContent,
                                                   Integer tokenUsed, Long responseTime) {
        if (conversationId == null) {
            throw new AiBusinessException("会话ID不能为空");
        }

        if (!StringUtils.hasText(content)) {
            throw new AiBusinessException("消息内容不能为空");
        }

        AiConversationContent contentEntity = new AiConversationContent();
        contentEntity.setConversation_id(conversationId);
        contentEntity.setType(AiConstant.MESSAGE_TYPE_AI);
        contentEntity.setContent(content);
        contentEntity.setMessage_content(messageContent);
        contentEntity.setToken_used(tokenUsed != null ? tokenUsed : 0);
        contentEntity.setResponse_time(responseTime != null ? responseTime : 0L);
        contentEntity.setIs_deleted(AiConstant.NOT_DELETED);

        if (!save(contentEntity)) {
            throw new AiBusinessException("保存AI消息失败");
        }

        log.info("添加AI消息成功, conversationId: {}, messageId: {}, tokens: {}, responseTime: {}ms",
                conversationId, contentEntity.getId(), tokenUsed, responseTime);
        return contentEntity;
    }

    @Override
    public List<AiConversationContent> getContentsByConversationId(String conversationId) {
        if (conversationId == null) {
            throw new AiBusinessException("会话ID不能为空");
        }

        return baseMapper.selectByConversationId(conversationId);
    }

    @Override
    public IPage<AiConversationContent> getContentsByConversationId(String conversationId, Long current, Long size) {
        if (conversationId == null) {
            throw new AiBusinessException("会话ID不能为空");
        }

        Page<AiConversationContent> page = new Page<>(current, size);
        return baseMapper.selectPageByConversationId(page, conversationId);
    }

    @Override
    public List<AiConversationContent> getContentsByConversationIdAndType(String conversationId, Integer messageType) {
        if (conversationId == null || messageType == null) {
            throw new AiBusinessException("会话ID和消息类型不能为空");
        }

        return baseMapper.selectByConversationIdAndType(conversationId, messageType);
    }

    @Override
    public AiConversationContent getLastMessage(String conversationId) {
        if (conversationId == null) {
            throw new AiBusinessException("会话ID不能为空");
        }

        return baseMapper.selectLastByConversationId(conversationId);
    }

    @Override
    public List<AiConversationContent> getRecentMessages(String conversationId, Integer limit) {
        if (conversationId == null) {
            throw new AiBusinessException("会话ID不能为空");
        }

        if (limit == null || limit <= 0) {
            limit = 20; // 默认20条
        }

        return baseMapper.selectRecentByConversationId(conversationId, limit);
    }

    @Override
    public Long countMessagesByConversationId(String conversationId) {
        if (conversationId == null) {
            throw new AiBusinessException("会话ID不能为空");
        }

        return baseMapper.countByConversationId(conversationId);
    }

    @Override
    public List<AiConversationContent> getContentsByUserId(Long userId) {
        if (userId == null) {
            throw new AiBusinessException("用户ID不能为空");
        }

        return baseMapper.selectByUserId(userId);
    }

    @Override
    public List<AiConversationContent> searchMessages(String conversationId, String keyword) {
        if (conversationId == null) {
            throw new AiBusinessException("会话ID不能为空");
        }

        if (!StringUtils.hasText(keyword)) {
            return getContentsByConversationId(conversationId);
        }

        return baseMapper.searchByKeyword(conversationId, keyword);
    }

    @Override
    public List<AiConversationContent> getMessagesByTimeRange(String conversationId, LocalDateTime startTime, LocalDateTime endTime) {
        if (conversationId == null || startTime == null || endTime == null) {
            throw new AiBusinessException("参数不能为空");
        }

        return baseMapper.selectByTimeRange(conversationId, startTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteMessagesByConversationId(String conversationId) {
        if (conversationId == null) {
            throw new AiBusinessException("会话ID不能为空");
        }

        LambdaUpdateWrapper<AiConversationContent> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(AiConversationContent::getConversation_id, conversationId)
                .eq(AiConversationContent::getIs_deleted, AiConstant.NOT_DELETED)
                .set(AiConversationContent::getIs_deleted, AiConstant.DELETED);

        boolean result = update(updateWrapper);
        if (result) {
            log.info("删除会话消息成功, conversationId: {}", conversationId);
        }

        return result;
    }

    @Override
    public AiConversationContent getFirstUserMessage(String conversationId) {
        if (conversationId == null) {
            throw new AiBusinessException("会话ID不能为空");
        }

        return baseMapper.selectFirstUserMessage(conversationId);
    }

    @Override
    public Integer calculateTotalTokens(String conversationId) {
        if (conversationId == null) {
            throw new AiBusinessException("会话ID不能为空");
        }

        LambdaQueryWrapper<AiConversationContent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiConversationContent::getConversation_id, conversationId)
                .eq(AiConversationContent::getMessage_type, AiConstant.MESSAGE_TYPE_AI)
                .eq(AiConversationContent::getIs_deleted, AiConstant.NOT_DELETED)
                .select(AiConversationContent::getToken_used);

        List<AiConversationContent> messages = list(queryWrapper);
        return messages.stream()
                .mapToInt(msg -> msg.getToken_used() != null ? msg.getToken_used() : 0)
                .sum();
    }

    @Override
    public Long calculateAverageResponseTime(String conversationId) {
        if (conversationId == null) {
            throw new AiBusinessException("会话ID不能为空");
        }

        LambdaQueryWrapper<AiConversationContent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiConversationContent::getConversation_id, conversationId)
                .eq(AiConversationContent::getMessage_type, AiConstant.MESSAGE_TYPE_AI)
                .eq(AiConversationContent::getIs_deleted, AiConstant.NOT_DELETED)
                .gt(AiConversationContent::getResponse_time, 0)
                .select(AiConversationContent::getResponse_time);

        List<AiConversationContent> messages = list(queryWrapper);
        if (messages.isEmpty()) {
            return 0L;
        }

        long totalTime = messages.stream()
                .mapToLong(msg -> msg.getResponse_time() != null ? msg.getResponse_time() : 0L)
                .sum();

        return totalTime / messages.size();
    }
}