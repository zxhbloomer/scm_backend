package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.bean.entity.chat.AiConversationEntity;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationContentVo;
import com.xinyirun.scm.ai.bean.vo.chat.AiConversationVo;
import com.xinyirun.scm.ai.bean.vo.chat.AiPromptVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatOptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AIConversationUpdateRequestVo;
import com.xinyirun.scm.ai.common.exception.AiBusinessException;
import com.xinyirun.scm.ai.config.adapter.AiEngineAdapter;
import com.xinyirun.scm.ai.config.adapter.AiStreamHandler;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationContentMapper;
import com.xinyirun.scm.ai.core.mapper.chat.AiConversationMapper;
import com.xinyirun.scm.ai.core.mapper.chat.ExtAiConversationContentMapper;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: jianxing
 * @CreateTime: 2025-05-28  13:44
 */
@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class AiConversationService {

    @Resource
    AiChatBaseService aiChatBaseService;
    @Resource
    AiConversationMapper aiConversationMapper;
    @Resource
    AiConversationContentMapper aiConversationContentMapper;
    @Resource
    AiPromptService aiPromptService;
    @Resource
    AiTokenUsageService aiTokenUsageService;
    @Resource
    private ExtAiConversationContentMapper extAiConversationContentMapper;
    @Resource
    private com.xinyirun.scm.ai.core.service.workflow.AiConversationRuntimeService conversationRuntimeService;

    /**
     * 获取默认系统提示词
     * @return 默认系统提示词，如果不存在返回null
     */
    private String getDefaultSystemPrompt() {
        try {
            AiPromptVo defaultPrompt = aiPromptService.getByCode("CS_DEFAULT");
            return defaultPrompt != null ? defaultPrompt.getPrompt() : null;
        } catch (Exception e) {
            log.error("获取默认系统提示词失败", e);
            return null;
        }
    }

    /**
     * 使用回调方式的流式聊天
     *
     * @param request 聊天请求
     * @param userId 用户ID
     * @param streamHandler 流式处理器
     */
    public void chatStreamWithCallback(AIChatRequestVo request, String userId, AiStreamHandler.CallbackStreamHandler streamHandler) {
        // 获取模型ID
        final Long modelId = aiChatBaseService.getModule(request, userId).getId();

        AIChatOptionVo aiChatOption = AIChatOptionVo.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(request.getPrompt())
                .system(getDefaultSystemPrompt())
                .tenantId(request.getTenantId())
                .build();
        StringBuilder completeContent = new StringBuilder();
        final Usage[] finalUsage = new Usage[1];

        try {
            // 【多租户关键】在异步流开始前设置数据源,供ChatMemory查询历史记录使用
            if (StringUtils.isNotBlank(request.getTenantId())) {
                DataSourceHelper.use(request.getTenantId());
            }

            // 使用流式聊天
            aiChatBaseService.chatWithMemoryStream(aiChatOption)
                .chatResponse()
                .doOnNext(chatResponse -> {
                    // 获取内容片段
                    String content = chatResponse.getResult().getOutput().getText();
                    // 发送内容片段
                    streamHandler.onContent(content);
                    completeContent.append(content);

                    // 保存最后一个响应的Usage信息（通常在最后一个响应中）
                    if (chatResponse.getMetadata() != null && chatResponse.getMetadata().getUsage() != null) {
                        finalUsage[0] = chatResponse.getMetadata().getUsage();
                    }
                })
                .doOnComplete(() -> {
                    // 设置租户数据库上下文
                    DataSourceHelper.use(request.getTenantId());
                    // 获取完整的AI回复内容
                    String fullContent = completeContent.toString();

                    // 发送完成事件
                    AiEngineAdapter.AiResponse finalResponse = new AiEngineAdapter.AiResponse();
                    finalResponse.setContent(fullContent);
                    finalResponse.setSuccess(true);

                    // 设置Usage信息
                    if (finalUsage[0] != null) {
                        finalResponse.setUsageFromSpringAi(finalUsage[0]);
                    }

                    streamHandler.onComplete(finalResponse);
                })
                .doOnError(error -> {
                    log.error(error.getMessage());
                    streamHandler.onError(error);
                })
                .subscribe();

            // 发送开始事件
            streamHandler.onStart();

        } catch (Exception e) {
            log.error(e.getMessage());
            streamHandler.onError(e);
        }
    }

    public AiConversationVo add(AIChatRequestVo request, String userId) {
        String prompt = """
                概况用户输入的主旨生成本轮对话的标题，只返回标题，不带标点符号，最好50字以内，不超过255。
                用户输入:
                """ + request.getPrompt();
        AIChatOptionVo aiChatOption = AIChatOptionVo.builder()
                .conversationId(request.getConversationId())
                .module(aiChatBaseService.getModule(request, userId))
                .prompt(prompt)
                .tenantId(request.getTenantId())
                .build();

        String conversationTitle = request.getPrompt();
        try {
            conversationTitle = aiChatBaseService.chat(aiChatOption)
                    .content();
            conversationTitle = conversationTitle.replace("\"", "");
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        if (conversationTitle.length() > 255) {
            conversationTitle = conversationTitle.substring(0, 255);
        }
        AiConversationEntity aiConversation = new AiConversationEntity();
        aiConversation.setId(request.getConversationId());
        aiConversation.setTitle(conversationTitle);
        // 注意：c_time 和 c_id 字段由MyBatis Plus自动填充，不需要手动设置
        // @TableField(fill = FieldFill.INSERT) 会自动处理创建时间和创建人
        aiConversationMapper.insert(aiConversation);
        AiConversationVo vo = new AiConversationVo();
        BeanUtils.copyProperties(aiConversation, vo);
        return vo;
    }

    public void delete(String conversationId, String userId) {
        log.info("开始删除对话,conversationId: {}, userId: {}", conversationId, userId);

        // 1. 删除workflow运行记录
        int workflowCount = conversationRuntimeService.deleteByConversationId(conversationId);
        log.info("删除workflow运行记录,conversationId: {}, 数量: {}", conversationId, workflowCount);

        // 2. 删除对话内容
        int contentCount = aiConversationContentMapper.deleteByConversationId(conversationId);
        log.info("删除对话内容,conversationId: {}, 数量: {}", conversationId, contentCount);

        // 3. 删除对话记录
        aiConversationMapper.deleteById(conversationId);
        log.info("删除对话完成,conversationId: {}", conversationId);
    }

    /**
     * 清空对话内容(保留对话记录,只删除消息内容)
     * @param conversationId 对话ID
     * @param userId 用户ID
     */
    public void clearConversationContent(String conversationId, String userId) {
        try {
            log.info("🧹【清空对话】开始清空对话内容 - conversationId: {}, userId: {}", conversationId, userId);

            // 1. 删除workflow运行记录(包括ai_conversation_runtime和ai_conversation_runtime_node)
            int workflowCount = conversationRuntimeService.deleteByConversationId(conversationId);
            log.info("🧹【清空对话】步骤1-删除workflow运行记录完成 - conversationId: {}, 删除runtime数量: {}", conversationId, workflowCount);

            // 2. 删除对话内容(ai_conversation_content)
            int contentCount = aiConversationContentMapper.deleteByConversationId(conversationId);
            log.info("🧹【清空对话】步骤2-删除对话内容完成 - conversationId: {}, 删除content数量: {}", conversationId, contentCount);

            log.info("🧹【清空对话】全部完成 - conversationId: {}, 删除runtime: {}, 删除content: {}",
                    conversationId, workflowCount, contentCount);

        } catch (Exception e) {
            log.error("🧹【清空对话】失败 - conversationId: {}, error: {}", conversationId, e.getMessage(), e);
            throw new AiBusinessException("清空对话内容失败:" + e.getMessage());
        }
    }

    public List<AiConversationVo> list(String userId) {
        List<AiConversationEntity> entities = aiConversationMapper.selectByUserId(Long.valueOf(userId));
        return entities.stream().map(entity -> {
            AiConversationVo vo = new AiConversationVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据conversationId获取对话信息
     * @param conversationId 对话ID
     * @return 对话信息
     */
    public AiConversationVo getConversation(String conversationId) {
        AiConversationEntity entity = aiConversationMapper.selectById(conversationId);
        if (entity != null) {
            AiConversationVo vo = new AiConversationVo();
            BeanUtils.copyProperties(entity, vo);
            return vo;
        }
        return null;
    }


    /**
     * 从Spring AI Usage接口记录Token使用情况（流式聊天回调）
     * 该方法与Spring AI框架集成，用于记录实际的Token消耗
     *
     * @param conversationId 对话ID
     * @param serialType 业务类型(表名): ai_conversation_runtime_node/ai_workflow_runtime_node/ai_knowledge_base_qa
     * @param serialId 业务记录ID(对应表的主键或UUID)
     * @param userId 用户ID
     * @param aiProvider AI提供商
     * @param modelSourceId 模型源ID
     * @param modelType 模型类型（base_name）
     * @param promptTokens 输入Token数
     * @param completionTokens 输出Token数
     */
    public void recordTokenUsageFromSpringAI(String conversationId, String serialType, String serialId, String userId,
                                            String aiProvider, String modelSourceId, String modelType,
                                            Long promptTokens, Long completionTokens) {
        try {
            // 直接使用传入的modelSourceId，无需查找
            aiTokenUsageService.recordTokenUsageAsync(
                    conversationId,
                    serialType,            // 业务类型
                    serialId,              // 业务记录ID
                    modelSourceId,         // 模型源ID
                    userId,
                    aiProvider,
                    modelType,             // 使用真正的模型类型
                    promptTokens,
                    completionTokens,
                    true, // success
                    0L    // responseTime
            );

            log.info("流式聊天回调Token使用记录 - conversationId: {}, serialType: {}, serialId: {}, userId: {}, tokens: {}",
                    conversationId, serialType, serialId, userId, (promptTokens + completionTokens));

        } catch (Exception e) {
            log.error("流式聊天回调Token使用记录失败", e);
            // 记录失败不抛出异常，避免影响主业务流程
        }
    }

    public List<AiConversationContentVo> chatList(String conversationId, String userId) {
        List<AiConversationContentVo> result = extAiConversationContentMapper.selectByConversationId(conversationId);

        log.info("【AI-Chat-查询】查询历史消息: conversationId={}, 返回{}条消息", conversationId, result.size());

        // 打印每条消息的runtime_uuid信息
        for (int i = 0; i < result.size(); i++) {
            AiConversationContentVo msg = result.get(i);
            log.info("【AI-Chat-查询】消息[{}]: messageId={}, type={}, runtime_uuid={}, content前20字={}",
                i, msg.getMessage_id(), msg.getType(), msg.getRuntime_uuid(),
                msg.getContent() != null && msg.getContent().length() > 20 ? msg.getContent().substring(0, 20) : msg.getContent());
        }

        return result;
    }

    public AiConversationVo update(AIConversationUpdateRequestVo request, String userId) {
        AiConversationEntity aiConversation = new AiConversationEntity();
        aiConversation.setId(request.getId());
        aiConversation.setTitle(request.getTitle());
        aiConversationMapper.updateById(aiConversation);
        return getConversation(request.getId());
    }

    /**
     * 为用户创建AI会话记录（由事件触发）
     *
     * @param convUuid 会话UUID
     * @param userId 用户ID
     * @param userName 用户名
     */
    public void createConversationForUser(String convUuid, Long userId, String userName, String tenant) {
        try {
            // 检查会话是否已存在
            AiConversationEntity existingConversation = aiConversationMapper.selectById(convUuid);
            if (existingConversation != null) {
                log.info("AI会话记录已存在，跳过创建：convUuid={}", convUuid);
                return;
            }

            // 创建AI会话记录
            AiConversationEntity aiConversation = new AiConversationEntity();
            aiConversation.setId(convUuid);
            aiConversation.setTitle("新对话"); // 设置默认标题
            // 注意：c_time 和 c_id 字段由MyBatis Plus自动填充，不需要手动设置
            // @TableField(fill = FieldFill.INSERT) 会自动处理创建时间和创建人

            aiConversationMapper.insert(aiConversation);

            log.info("在chat-ai数据库中创建AI会话记录成功 - convUuid={}, userId={}, userName={}",
                    convUuid, userId, userName);
        } catch (Exception e) {
            log.error("创建AI会话记录失败：userId={}, convUuid={}", userId, convUuid, e);
            throw e; // 重新抛出异常，让调用方处理
        }
    }

    /**
     * 结束对话
     */
    public void endConversation(String conversationId, String userId) {
        log.info("对话已结束 - conversationId: {}, userId: {}", conversationId, userId);
    }

    /**
     * 更新对话的工作流状态
     *
     * <p>此方法支持在异步上下文中调用,会自动从conversationId解析tenantCode并设置数据源</p>
     *
     * <p>conversationId格式说明:</p>
     * <ul>
     *   <li>Chat领域: tenantCode::conversationUUID (2段)</li>
     *   <li>Workflow领域: tenantCode::workflowUuid::userId (3段)</li>
     * </ul>
     *
     * @param conversationId 对话ID(格式: tenantCode::xxx 或 tenantCode::xxx::xxx)
     * @param workflowState 工作流状态（IDLE/WORKFLOW_RUNNING/WORKFLOW_WAITING_INPUT）
     * @param workflowUuid 工作流UUID（可选，IDLE时传null）
     * @param runtimeUuid 运行时UUID（可选，IDLE/WORKFLOW_RUNNING时传null）
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateWorkflowState(String conversationId, String workflowState,
                                     String workflowUuid, String runtimeUuid) {
        // 从conversationId解析tenantCode并设置数据源
        // conversationId格式: tenantCode::xxx 或 tenantCode::xxx::xxx
        if (StringUtils.isNotBlank(conversationId) && conversationId.contains("::")) {
            String tenantCode = conversationId.split("::", 2)[0];
            if (StringUtils.isNotBlank(tenantCode)) {
                DataSourceHelper.use(tenantCode);
                log.debug("updateWorkflowState: 从conversationId解析tenantCode={}", tenantCode);
            }
        }

        AiConversationEntity entity = aiConversationMapper.selectById(conversationId);
        if (entity == null) {
            throw new AiBusinessException("对话不存在: " + conversationId);
        }

        entity.setWorkflowState(workflowState);
        entity.setCurrentWorkflowUuid(workflowUuid);
        entity.setCurrentRuntimeUuid(runtimeUuid);

        aiConversationMapper.updateById(entity);

        log.info("更新对话工作流状态: conversationId={}, state={}, workflowUuid={}, runtimeUuid={}",
                 conversationId, workflowState, workflowUuid, runtimeUuid);
    }

    /**
     * 获取对话历史记录（用于ChatMemory）
     * @param conversationId 对话ID
     * @param limit 限制条数
     * @return 对话内容列表
     */
    public List<AiConversationContentVo> getConversationHistory(String conversationId, int limit) {
        return extAiConversationContentMapper.selectLastByConversationIdByLimit(conversationId, limit);
    }

}