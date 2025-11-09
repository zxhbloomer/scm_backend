package com.xinyirun.scm.ai.core.service.workflow;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowConversationContentVo;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowConversationContentMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * AI工作流对话内容服务
 *
 * 提供工作流对话内容管理功能，包括消息的创建、查询、删除等操作
 * 专注于工作流领域的对话记录管理，与Chat领域分离
 *
 * @author SCM-AI开发团队
 * @since 2025-01-08
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiWorkflowConversationContentService {

    @Resource
    private AiWorkflowConversationContentMapper workflowConversationContentMapper;

    /**
     * 保存工作流对话消息
     *
     * @param conversationId 对话ID（格式：tenantCode::workflowUuid::userId）
     * @param type 内容类型（USER, ASSISTANT, SYSTEM, TOOL）
     * @param content 消息内容
     * @param runtimeUuid 运行时UUID（关联 ai_workflow_runtime.runtime_uuid）
     * @param modelSourceId 模型源ID
     * @param providerName AI提供商名称
     * @param baseName 基础模型名称
     * @param operatorId 操作员ID
     * @return 保存的对话内容VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiWorkflowConversationContentVo saveMessage(String conversationId, String type, String content,
                                                       String runtimeUuid, String modelSourceId, String providerName,
                                                       String baseName, Long operatorId) {
        try {
            AiWorkflowConversationContentEntity entity = new AiWorkflowConversationContentEntity();
            entity.setMessageId(UuidUtil.createShort());
            entity.setConversationId(conversationId);
            entity.setRuntimeUuid(runtimeUuid);
            entity.setType(type);
            entity.setContent(StringUtils.isNotBlank(content) ? content.trim() : content);
            entity.setModelSourceId(modelSourceId);
            entity.setProviderName(providerName);
            entity.setBaseName(baseName);

            int result = workflowConversationContentMapper.insert(entity);

            if (result > 0) {
                log.info("保存工作流对话消息成功, conversationId: {}, runtime_uuid: {}, type: {}, provider: {}, model: {}",
                        conversationId, runtimeUuid, type, providerName, baseName);

                AiWorkflowConversationContentVo vo = new AiWorkflowConversationContentVo();
                BeanUtils.copyProperties(entity, vo);
                return vo;
            }

            return null;
        } catch (Exception e) {
            log.error("保存工作流对话消息失败, conversationId: {}, runtime_uuid: {}, provider: {}, model: {}",
                    conversationId, runtimeUuid, providerName, baseName, e);
            throw new RuntimeException("保存工作流对话消息失败", e);
        }
    }

    /**
     * 根据运行时UUID删除对话历史记录
     *
     * @param runtimeUuid 运行时UUID（关联 ai_workflow_runtime.runtime_uuid）
     * @return 删除的记录数
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByRuntimeUuid(String runtimeUuid) {
        try {
            int count = workflowConversationContentMapper.deleteByRuntimeUuid(runtimeUuid);
            log.info("删除工作流对话历史记录成功, runtime_uuid: {}, 删除数量: {}", runtimeUuid, count);
            return count;
        } catch (Exception e) {
            log.error("删除工作流对话历史记录失败, runtime_uuid: {}", runtimeUuid, e);
            throw new RuntimeException("删除工作流对话历史记录失败", e);
        }
    }

    /**
     * 查询工作流对话历史记录
     *
     * @param conversationId 对话ID（格式：tenantCode::workflowUuid::userId）
     * @param limit 查询数量
     * @return 对话历史记录列表
     */
    public List<AiWorkflowConversationContentVo> getConversationHistory(String conversationId, int limit) {
        try {
            List<AiWorkflowConversationContentVo> history = workflowConversationContentMapper.selectLastByConversationIdByLimit(conversationId, limit);
            log.debug("查询工作流对话历史成功, conversation_id: {}, limit: {}, 查询结果数: {}",
                    conversationId, limit, history != null ? history.size() : 0);
            return history;
        } catch (Exception e) {
            log.error("查询工作流对话历史失败, conversation_id: {}, limit: {}", conversationId, limit, e);
            throw new RuntimeException("查询工作流对话历史失败", e);
        }
    }

}
