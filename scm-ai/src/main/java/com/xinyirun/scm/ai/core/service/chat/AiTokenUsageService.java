package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.statistics.AiTokenUsageEntity;
import com.xinyirun.scm.ai.bean.vo.statistics.AiTokenUsageVo;
import com.xinyirun.scm.ai.core.mapper.statistics.AiTokenUsageMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AI Token使用记录服务
 *
 * 提供AI Token实时使用记录管理功能，包括记录的创建、查询、统计等操作
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiTokenUsageService {

    @Resource
    private AiTokenUsageMapper aiTokenUsageMapper;

    /**
     * 异步记录Token使用情况
     *
     * @param conversationId 对话ID
     * @param conversationContentId 关联的消息ID（ASSISTANT消息ID）
     * @param modelSourceId 模型源ID
     * @param userId 用户ID
     * @param aiProvider AI提供商
     * @param aiModelType AI模型类型
     * @param promptTokens 输入Token数
     * @param completionTokens 输出Token数
     * @param success 是否成功
     * @param responseTime 响应时间
     */
    @Transactional(rollbackFor = Exception.class)
    public void recordTokenUsageAsync(String conversationId, String conversationContentId, String modelSourceId, String userId,
                                      String aiProvider, String aiModelType,
                                     Long promptTokens, Long completionTokens, Boolean success,
                                     Long responseTime) {
        try {
            // 创建Token使用记录
            AiTokenUsageEntity entity = new AiTokenUsageEntity();

            // 生成UUID作为主键ID
            entity.setId(UUID.randomUUID().toString());

            // 设置基本信息字段
            entity.setConversationId(conversationId);
            entity.setConversationContentId(conversationContentId);
            entity.setModelSourceId(modelSourceId);
            entity.setUserId(userId);

            // 设置AI提供商和模型信息
            entity.setProviderName(aiProvider);
            entity.setModelType(aiModelType);

            // 设置Token使用情况
            entity.setPromptTokens(promptTokens != null ? promptTokens : 0L);
            entity.setCompletionTokens(completionTokens != null ? completionTokens : 0L);
            // total_tokens是数据库生成列，自动计算，不需要手动设置

            // 设置请求结果信息
            entity.setSuccess(success != null ? success : Boolean.TRUE);
            entity.setResponseTime(responseTime != null ? responseTime : 0L);
            entity.setUsageTime(LocalDateTime.now());

            // 设置费用相关字段（暂时设置为默认值，后续可扩展）
            entity.setTokenUnitPrice(java.math.BigDecimal.ZERO);
            entity.setCost(java.math.BigDecimal.ZERO);

            // ai_config_id字段暂时保持null，后续根据业务需要设置

            int result = aiTokenUsageMapper.insert(entity);
            if (result > 0) {
                log.debug("记录Token使用情况成功, conversationId: {}, userId: {}, totalTokens: {}",
                        conversationId, userId, entity.getTotalTokens());
            }
        } catch (Exception e) {
            log.error("记录Token使用情况失败, conversationId: {}, userId: {}, tenant: {}",
                    conversationId, userId, e);
        }
    }

}