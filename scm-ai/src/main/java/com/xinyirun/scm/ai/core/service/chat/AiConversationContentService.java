package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.bean.entity.chat.AiConversationContentEntity;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.common.constant.AiMessageTypeConstant;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentMapper;
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

    @Resource
    private AiConversationContentRefEmbeddingService refEmbeddingService;

    @Resource
    private AiConversationContentRefGraphService refGraphService;

    /**
     * 保存对话内容（简化版，用于Workflow场景）
     *
     * @param conversationId 对话ID
     * @param role 角色（1=用户, 2=AI）
     * @param content 内容
     * @param operatorId 操作员ID
     * @param runtimeUuid 运行时UUID（可选，关联ai_conversation_workflow_runtime）
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
            // Workflow场景下可以不设置模型信息（或设置为默认值）
            entity.setModelSourceId(null);
            entity.setProviderName("workflow");
            entity.setBaseName("workflow");

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

    /**
     * 保存对话内容（包含模型信息和RAG引用记录）
     *
     * <p>用于RAG场景下保存对话内容及其引用的知识库片段和图谱实体。</p>
     * <p>保存流程：</p>
     * <ol>
     *   <li>调用saveConversationContent()保存基础对话内容到MySQL</li>
     *   <li>使用返回的messageId保存向量检索引用（ai_conversation_content_ref_embedding）</li>
     *   <li>保存图谱检索引用（ai_conversation_content_ref_graph）</li>
     * </ol>
     *
     * @param conversationId 对话ID
     * @param type 内容类型（USER/ASSISTANT）
     * @param content 内容
     * @param modelSourceId 模型源ID
     * @param providerName AI提供商名称
     * @param baseName 基础模型名称
     * @param operatorId 操作员ID
     * @param embeddingScores 向量检索结果Map（embeddingId -> score），可为null
     * @param kbId 知识库ID，用于图谱引用
     * @param entitiesFromQuestion 从问题中提取的实体JSON
     * @param graphFromStore 从图数据库检索的图谱JSON
     * @param entityCount 实体数量
     * @param relationCount 关系数量
     * @return 保存的对话内容VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiConversationContentVo saveConversationContentWithReferences(
            String conversationId, String type, String content,
            String modelSourceId, String providerName, String baseName, Long operatorId,
            Map<String, Double> embeddingScores,
            String kbId, String entitiesFromQuestion, String graphFromStore,
            Integer entityCount, Integer relationCount) {

        try {
            // 1. 保存基础对话内容
            AiConversationContentVo savedContent = saveConversationContent(
                    conversationId, type, content, modelSourceId, providerName, baseName, operatorId);

            if (savedContent == null || StringUtils.isBlank(savedContent.getMessage_id())) {
                log.error("保存对话内容失败，无法保存引用记录");
                return null;
            }

            String messageId = savedContent.getMessage_id();
            log.info("对话内容已保存，messageId: {}, 开始保存引用记录", messageId);

            // 2. 保存向量检索引用记录
            if (!CollectionUtils.isEmpty(embeddingScores)) {
                int embeddingCount = refEmbeddingService.saveRefEmbeddings(
                        messageId, embeddingScores, operatorId);
                log.info("保存对话向量引用成功，messageId: {}, 数量: {}", messageId, embeddingCount);
            } else {
                log.debug("向量检索结果为空，跳过保存向量引用，messageId: {}", messageId);
            }

            // 3. 保存图谱检索引用记录
            if (StringUtils.isNotBlank(kbId) && StringUtils.isNotBlank(graphFromStore)) {
                Long graphRefId = refGraphService.saveRefGraph(
                        messageId, kbId, entitiesFromQuestion, graphFromStore,
                        entityCount, relationCount, operatorId);

                if (graphRefId != null) {
                    log.info("保存对话图谱引用成功，messageId: {}, refId: {}, 实体数: {}, 关系数: {}",
                            messageId, graphRefId, entityCount, relationCount);
                } else {
                    log.warn("保存对话图谱引用失败，messageId: {}", messageId);
                }
            } else {
                log.debug("图谱检索结果为空，跳过保存图谱引用，messageId: {}", messageId);
            }

            log.info("对话内容及引用记录保存完成，messageId: {}", messageId);
            return savedContent;

        } catch (Exception e) {
            log.error("保存对话内容及引用记录失败, conversationId: {}, provider: {}, model: {}",
                    conversationId, providerName, baseName, e);
            throw new RuntimeException("保存对话内容及引用记录失败", e);
        }
    }

}
