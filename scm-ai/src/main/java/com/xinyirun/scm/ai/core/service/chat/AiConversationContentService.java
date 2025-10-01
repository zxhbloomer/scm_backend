package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentMapper;
import com.xinyirun.scm.bean.clickhouse.vo.ai.SLogAiChatVo;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
            entity.setConversation_id(conversationId);
            entity.setType(type);
            entity.setContent(content);
            entity.setModel_source_id(modelSourceId);
            entity.setProvider_name(providerName);
            entity.setBase_name(baseName);

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
        vo.setConversation_id(entity.getConversation_id());
        vo.setType(entity.getType());
        vo.setContent(entity.getContent());
        vo.setModel_source_id(entity.getModel_source_id());
        vo.setC_id(entity.getC_id());
        vo.setC_time(entity.getC_time());

        // 设置租户编码（从当前数据源上下文获取）
        vo.setTenant_code(DataSourceHelper.getCurrentDataSourceName());

        // 设置创建人名称（从entity获取，如果为null则留空）
        vo.setC_name(entity.getC_id() != null ? String.valueOf(entity.getC_id()) : null);

        // 设置请求标识（使用conversation_id作为请求标识）
        vo.setRequest_id(entity.getConversation_id());

        // 设置模型信息（使用方法参数传入的值）
        if (StringUtils.isNotBlank(providerName)) {
            vo.setProvider_name(providerName);
        }
        if (StringUtils.isNotBlank(baseName)) {
            vo.setBase_name(baseName);
        }

        return vo;
    }

}
