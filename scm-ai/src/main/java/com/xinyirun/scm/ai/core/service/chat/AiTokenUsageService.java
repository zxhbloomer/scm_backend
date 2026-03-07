package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.bean.entity.statistics.AiTokenUsageEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationRuntimeEntity;
import com.xinyirun.scm.ai.bean.vo.chat.NodeTokenUsageVo;
import com.xinyirun.scm.ai.core.mapper.statistics.AiTokenUsageMapper;
import com.xinyirun.scm.ai.core.mapper.workflow.AiConversationRuntimeMapper;
import com.xinyirun.scm.ai.core.mapper.workflow.AiConversationRuntimeNodeMapper;
import com.xinyirun.scm.common.utils.UuidUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
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

    @Resource
    private AiConversationRuntimeMapper conversationRuntimeMapper;

    @Resource
    private AiConversationRuntimeNodeMapper conversationRuntimeNodeMapper;

    /**
     * 异步记录Token使用情况
     *
     * @param conversationId 对话ID
     * @param serialType 业务类型(表名): ai_conversation_runtime_node/ai_workflow_runtime_node/ai_knowledge_base_qa
     * @param serialId 业务记录ID(对应表的主键或UUID)
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
    public void recordTokenUsageAsync(String conversationId, String serialType, String serialId,
                                      String modelSourceId, String userId,
                                      String aiProvider, String aiModelType,
                                      Long promptTokens, Long completionTokens, Boolean success,
                                      Long responseTime) {
        try {
            // 创建Token使用记录
            AiTokenUsageEntity entity = new AiTokenUsageEntity();

            // 生成UUID作为主键ID
            entity.setId(UuidUtil.createShort());

            // 设置基本信息字段
            entity.setConversationId(conversationId);
            entity.setSerialType(serialType);
            entity.setSerialId(serialId);
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

            // ai_config_id 设置为 modelSourceId（两者都指向 ai_model_config.id）
            entity.setAiConfigId(modelSourceId);

            // 手动设置c_id和u_id（异步线程无Spring Security Context，自动填充无法获取用户ID）
            Long userIdLong = null;
            if (StringUtils.hasText(userId)) {
                try {
                    userIdLong = Long.parseLong(userId);
                } catch (NumberFormatException e) {
                    log.warn("userId转换Long失败: {}", userId);
                }
            }
            entity.setC_id(userIdLong);
            entity.setU_id(userIdLong);

            // 手动设置c_time和u_time（异步线程保险起见手动设置）
            LocalDateTime now = LocalDateTime.now();
            entity.setC_time(now);
            entity.setU_time(now);

            int result = aiTokenUsageMapper.insert(entity);
            if (result > 0) {
                log.debug("记录Token使用情况成功, conversationId: {}, serialType: {}, serialId: {}, userId: {}, totalTokens: {}",
                        conversationId, serialType, serialId, userId, (promptTokens + completionTokens));
            }
        } catch (Exception e) {
            log.error("记录Token使用情况失败, conversationId: {}, serialType: {}, serialId: {}, userId: {}",
                    conversationId, serialType, serialId, userId, e);
        }
    }

    /**
     * 根据runtime_uuid获取总Token消耗
     *
     * @param runtimeUuid 运行实例UUID
     * @return 总Token数,如果无记录返回null
     */
    public Long getTotalTokensByRuntimeUuid(String runtimeUuid) {
        try {
            // 1. 根据runtime_uuid查询runtime_id
            AiConversationRuntimeEntity runtime = conversationRuntimeMapper.selectByRuntimeUuid(runtimeUuid);

            if (runtime == null) {
                log.warn("未找到runtime记录: runtimeUuid={}", runtimeUuid);
                return null;
            }

            // 2. 查询该runtime下所有节点的ID列表
            List<Long> nodeIds = conversationRuntimeNodeMapper.selectIdsByRuntimeId(runtime.getId());

            if (nodeIds == null || nodeIds.isEmpty()) {
                log.debug("runtime无节点记录: runtimeId={}", runtime.getId());
                return null;
            }

            // 3. 聚合查询Token总数
            // 将nodeIds转换为String列表(serial_id是varchar类型)
            List<String> serialIds = nodeIds.stream()
                    .map(String::valueOf)
                    .collect(Collectors.toList());

            Long totalTokens = aiTokenUsageMapper.sumTotalTokensBySerialIds(serialIds);

            log.debug("runtime总Token: runtimeUuid={}, nodeCount={}, totalTokens={}",
                    runtimeUuid, nodeIds.size(), totalTokens);

            return totalTokens;
        } catch (Exception e) {
            log.error("Token统计失败: runtimeUuid={}, error={}", runtimeUuid, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据节点ID获取Token消耗详情
     *
     * @param nodeId 节点ID
     * @return Token消耗VO,如果无记录返回null
     */
    public NodeTokenUsageVo getNodeTokenUsage(Long nodeId) {
        try {
            AiTokenUsageEntity entity = aiTokenUsageMapper.selectBySerialTypeAndSerialId(
                    "ai_conversation_runtime_node", String.valueOf(nodeId));

            if (entity == null) {
                log.debug("节点无Token记录: nodeId={}", nodeId);
                return null;
            }

            NodeTokenUsageVo vo = new NodeTokenUsageVo();
            vo.setPromptTokens(entity.getPromptTokens());
            vo.setCompletionTokens(entity.getCompletionTokens());
            vo.setTotalTokens(entity.getTotalTokens());

            log.debug("节点Token: nodeId={}, promptTokens={}, completionTokens={}, totalTokens={}",
                    nodeId, vo.getPromptTokens(), vo.getCompletionTokens(), vo.getTotalTokens());

            return vo;
        } catch (Exception e) {
            log.error("Token统计失败: nodeId={}, error={}", nodeId, e.getMessage(), e);
            return null;
        }
    }

}