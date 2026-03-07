package com.xinyirun.scm.ai.core.service.workflow;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowInteractionEntity;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowInteractionMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI工作流人机交互Service
 *
 * <p>管理工作流执行过程中的人机交互生命周期，
 * 包括创建交互、提交反馈、取消、超时处理</p>
 *
 * @author SCM-AI团队
 * @since 2026-03-06
 */
@Slf4j
@Service
public class AiWorkflowInteractionService extends ServiceImpl<AiWorkflowInteractionMapper, AiWorkflowInteractionEntity> {

    @Resource
    private AiWorkflowInteractionMapper interactionMapper;

    /**
     * 创建交互记录
     */
    public AiWorkflowInteractionEntity createInteraction(
            String conversationId,
            String runtimeUuid,
            String nodeUuid,
            String interactionType,
            String interactionParams,
            String description,
            Integer timeoutMinutes) {

        AiWorkflowInteractionEntity entity = new AiWorkflowInteractionEntity();
        entity.setInteractionUuid(UuidUtil.createShort());
        entity.setConversationId(conversationId);
        entity.setRuntimeUuid(runtimeUuid);
        entity.setNodeUuid(nodeUuid);
        entity.setInteractionType(interactionType);
        entity.setInteractionParams(interactionParams);
        entity.setDescription(description);
        entity.setStatus("WAITING");

        int minutes = (timeoutMinutes != null && timeoutMinutes > 0) ? timeoutMinutes : 30;
        entity.setTimeoutMinutes(minutes);
        entity.setTimeoutAt(LocalDateTime.now().plusMinutes(minutes));

        save(entity);
        log.info("创建人机交互记录: interactionUuid={}, type={}, conversationId={}",
                entity.getInteractionUuid(), interactionType, conversationId);
        return entity;
    }

    /**
     * 提交用户反馈，更新状态为SUBMITTED
     */
    public boolean submitFeedback(String interactionUuid, String feedbackAction, String feedbackData) {
        AiWorkflowInteractionEntity entity = interactionMapper.selectByInteractionUuid(interactionUuid);
        if (entity == null || !"WAITING".equals(entity.getStatus())) {
            log.warn("交互记录不存在或状态非WAITING: interactionUuid={}", interactionUuid);
            return false;
        }

        entity.setStatus("SUBMITTED");
        entity.setFeedbackAction(feedbackAction);
        entity.setFeedbackData(feedbackData);
        entity.setSubmittedAt(LocalDateTime.now());
        updateById(entity);

        log.info("提交交互反馈: interactionUuid={}, action={}", interactionUuid, feedbackAction);
        return true;
    }

    /**
     * 取消交互，更新状态为CANCELLED
     */
    public boolean cancelInteraction(String interactionUuid) {
        AiWorkflowInteractionEntity entity = interactionMapper.selectByInteractionUuid(interactionUuid);
        if (entity == null || !"WAITING".equals(entity.getStatus())) {
            return false;
        }

        entity.setStatus("CANCELLED");
        entity.setFeedbackAction("cancel");
        updateById(entity);

        log.info("取消交互: interactionUuid={}", interactionUuid);
        return true;
    }

    /**
     * 超时处理，更新状态为TIMEOUT
     */
    public boolean timeoutInteraction(String interactionUuid) {
        AiWorkflowInteractionEntity entity = interactionMapper.selectByInteractionUuid(interactionUuid);
        if (entity == null || !"WAITING".equals(entity.getStatus())) {
            return false;
        }

        entity.setStatus("TIMEOUT");
        entity.setFeedbackAction("__timeout");
        updateById(entity);

        log.info("交互超时: interactionUuid={}", interactionUuid);
        return true;
    }

    /**
     * 查找对话中WAITING状态的交互
     */
    public AiWorkflowInteractionEntity findWaitingByConversationId(String conversationId) {
        return interactionMapper.selectWaitingByConversationId(conversationId);
    }

    /**
     * 查找已超时的交互记录
     */
    public List<AiWorkflowInteractionEntity> findExpiredInteractions() {
        return interactionMapper.selectExpiredInteractions();
    }
}
