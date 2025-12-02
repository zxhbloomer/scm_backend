package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import com.xinyirun.scm.ai.common.constant.AiMessageTypeConstant;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentMapper;
import com.xinyirun.scm.ai.core.service.config.AiModelConfigService;
import com.xinyirun.scm.bean.clickhouse.vo.ai.SLogAiChatVo;
import com.xinyirun.scm.common.utils.UuidUtil;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.mq.rabbitmq.producer.business.log.ai.LogAiChatProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI对话内容服务
 *
 * 提供AI对话内容管理功能，包括消息的创建、查询等操作
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiConversationContentService {

    @Resource
    private AiConversationContentMapper aiConversationContentMapper;

    @Autowired
    private LogAiChatProducer logAiChatProducer;

    @Autowired
    private AiModelConfigService aiModelConfigService;


    /**
     * 保存对话内容（简化版，用于Workflow场景）
     *
     * @param conversationId 对话ID
     * @param role 角色（1=用户, 2=AI）
     * @param content 内容
     * @param operatorId 操作员ID
     * @param runtimeUuid 运行时UUID（可选，关联ai_conversation_runtime）
     * @return 保存的对话内容VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContentVo saveContent(String conversationId, Integer role, String content, Long operatorId, String runtimeUuid) {
        try {
            AiConversationContentEntity entity = new AiConversationContentEntity();
            entity.setMessageId(UuidUtil.createShort());
            entity.setConversationId(conversationId);
            // 根据role设置type: 1=USER, 2=ASSISTANT (使用Spring AI标准小写常量)
            entity.setType(role == 1 ? AiMessageTypeConstant.MESSAGE_TYPE_USER : AiMessageTypeConstant.MESSAGE_TYPE_ASSISTANT);
            // 移除前导和尾随空白字符，避免Markdown渲染为代码块
            entity.setContent(StringUtils.isNotBlank(content) ? content.trim() : content);
            // 设置运行时UUID（可选）
            entity.setRuntimeUuid(runtimeUuid);

            // 获取默认LLM模型配置并设置模型字段
            try {
                AiModelConfigVo defaultModel = aiModelConfigService.getDefaultModelConfigWithKey("LLM");
                entity.setModelSourceId(String.valueOf(defaultModel.getId()));
                entity.setProviderName(defaultModel.getProvider());
                entity.setBaseName(defaultModel.getModelName());
            } catch (Exception e) {
                // 降级处理: 获取失败时使用默认值,不影响主流程
                log.warn("Workflow场景获取默认LLM模型配置失败,使用降级值: {}", e.getMessage());
                entity.setModelSourceId(null);
                entity.setProviderName("workflow");
                entity.setBaseName("workflow");
            }

            // 设置创建人和修改人ID（使用传入的operatorId参数）
            entity.setCId(operatorId);
            entity.setUId(operatorId);

            // 设置创建时间和修改时间
            LocalDateTime now = LocalDateTime.now();
            entity.setCreateTime(now);
            entity.setUpdateTime(now);

            // 保存到MySQL
            int result = aiConversationContentMapper.insert(entity);

            if (result > 0) {
                log.info("保存对话内容成功 (Workflow场景), conversationId: {}, role: {}, contentLength: {}",
                        conversationId, role, content != null ? content.length() : 0);

                // 异步发送MQ消息到ClickHouse日志系统
                try {
                    SLogAiChatVo logVo = buildLogVo(entity, "workflow", "workflow");
                    logAiChatProducer.mqSendMq(logVo);
                    log.debug("发送AI聊天日志MQ消息成功 (Workflow场景)，conversation_id: {}, role: {}",
                            conversationId, role);
                } catch (Exception e) {
                    // 日志发送失败不影响主业务，仅记录错误
                    log.error("发送AI聊天日志MQ消息失败 (Workflow场景)，conversation_id: {}, role: {}",
                            conversationId, role, e);
                }

                AiConversationContentVo vo = new AiConversationContentVo();
                BeanUtils.copyProperties(entity, vo);
                // 手动设置message_id（因为Entity字段名是messageId，VO字段名是message_id，BeanUtils不会自动拷贝）
                vo.setMessage_id(entity.getMessageId());
                return vo;
            }

            return null;
        } catch (Exception e) {
            log.error("保存对话内容失败 (Workflow场景), conversationId: {}, role: {}",
                    conversationId, role, e);
            throw new RuntimeException("保存对话内容失败", e);
        }
    }

    /**
     * 保存对话内容（包含模型信息）
     *
     * @param conversationId 对话ID
     * @param type 内容类型
     * @param content 内容
     * @param modelSourceId 模型源ID
     * @param providerName AI提供商名称
     * @param baseName 基础模型名称
     * @param operatorId 操作员ID
     * @return 保存的对话内容VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContentVo saveConversationContent(String conversationId, String type, String content,
                                                          String modelSourceId, String providerName, String baseName, Long operatorId) {
        try {
            AiConversationContentEntity entity = new AiConversationContentEntity();
            entity.setMessageId(UuidUtil.createShort());
            entity.setConversationId(conversationId);
            entity.setType(type);
            // 移除前导和尾随空白字符，避免Markdown渲染为代码块
            entity.setContent(StringUtils.isNotBlank(content) ? content.trim() : content);
            entity.setModelSourceId(modelSourceId);
            entity.setProviderName(providerName);
            entity.setBaseName(baseName);

            // 设置创建人和修改人ID（使用传入的operatorId参数）
            entity.setCId(operatorId);
            entity.setUId(operatorId);

            // 设置创建时间和修改时间
            LocalDateTime now = LocalDateTime.now();
            entity.setCreateTime(now);
            entity.setUpdateTime(now);

            // 1. 保存到MySQL
            int result = aiConversationContentMapper.insert(entity);

            if (result > 0) {
                log.info("保存对话内容成功, conversationId: {}, type: {}, provider: {}, model: {}",
                        conversationId, type, providerName, baseName);

                // 2. 异步发送MQ消息到ClickHouse日志系统
                try {
                    SLogAiChatVo logVo = buildLogVo(entity, providerName, baseName);
                    logAiChatProducer.mqSendMq(logVo);
                    log.debug("发送AI聊天日志MQ消息成功，conversation_id: {}, type: {}",
                            conversationId, type);
                } catch (Exception e) {
                    // 日志发送失败不影响主业务，仅记录错误
                    log.error("发送AI聊天日志MQ消息失败，conversation_id: {}, type: {}",
                            conversationId, type, e);
                }

                AiConversationContentVo vo = new AiConversationContentVo();
                BeanUtils.copyProperties(entity, vo);
                // 手动设置message_id（因为Entity字段名是messageId，VO字段名是message_id，BeanUtils不会自动拷贝）
                vo.setMessage_id(entity.getMessageId());
                return vo;
            }

            return null;
        } catch (Exception e) {
            log.error("保存对话内容失败, conversationId: {}, provider: {}, model: {}",
                    conversationId, providerName, baseName, e);
            throw new RuntimeException("保存对话内容失败", e);
        }
    }

    /**
     * 构建AI聊天日志VO对象
     *
     * <p>从MySQL实体对象转换为MQ消息VO对象
     * <p>补充应用层字段：tenant_code、c_name、request_id
     * <p>使用参数传入的模型信息：provider_name、base_name
     *
     * @param entity MySQL实体对象
     * @param providerName AI提供商名称
     * @param baseName 基础模型名称
     * @return SLogAiChatVo MQ消息VO对象
     */
    private SLogAiChatVo buildLogVo(AiConversationContentEntity entity, String providerName, String baseName) {
        SLogAiChatVo vo = new SLogAiChatVo();

        // 从entity拷贝基础字段
        vo.setConversation_id(entity.getConversationId());
        vo.setType(entity.getType());
        vo.setContent(entity.getContent());
        vo.setModel_source_id(entity.getModelSourceId());
        vo.setC_id(entity.getCId());
        // MyBatis Plus自动填充只在数据库层面，不会回填到entity对象，所以直接使用当前时间
        vo.setC_time(java.time.LocalDateTime.now());

        // 设置租户编码（从当前数据源上下文获取）
        vo.setTenant_code(DataSourceHelper.getCurrentDataSourceName());

        // 设置创建人名称（从entity获取，如果为null则留空）
        vo.setC_name(entity.getCId() != null ? String.valueOf(entity.getCId()) : null);

        // 设置请求标识（使用conversation_id作为请求标识）
        vo.setRequest_id(entity.getConversationId());

        // 设置模型信息（使用方法参数传入的值）
        if (StringUtils.isNotBlank(providerName)) {
            vo.setProvider_name(providerName);
        }
        if (StringUtils.isNotBlank(baseName)) {
            vo.setBase_name(baseName);
        }

        return vo;
    }

    /**
     * 根据消息ID删除单条消息记录
     *
     * @param messageId 消息ID (对应数据库message_id字段)
     * @return 是否删除成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByMessageId(String messageId) {
        try {
            int result = aiConversationContentMapper.deleteByMessageId(messageId);
            log.info("删除对话消息成功, message_id: {}, result: {}", messageId, result);
            return result > 0;
        } catch (Exception e) {
            log.error("删除对话消息失败, message_id: {}", messageId, e);
            throw new RuntimeException("删除对话消息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据对话ID删除对话历史记录
     *
     * @param conversationId 对话ID
     * @return 删除的记录数
     */
    @Transactional(rollbackFor = Exception.class)
    public int deleteByConversationId(String conversationId) {
        try {
            int count = aiConversationContentMapper.deleteByConversationId(conversationId);
            log.info("删除对话历史记录成功, conversation_id: {}, 删除数量: {}", conversationId, count);
            return count;
        } catch (Exception e) {
            log.error("删除对话历史记录失败, conversation_id: {}", conversationId, e);
            throw new RuntimeException("删除对话历史记录失败", e);
        }
    }

}
