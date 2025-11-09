package com.xinyirun.scm.ai.config.memory;

import com.xinyirun.scm.ai.common.constant.AiMessageTypeConstant;
import com.xinyirun.scm.ai.core.service.workflow.AiWorkflowConversationContentService;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientMessageAggregator;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * Workflow领域对话记录Advisor
 *
 * 通过Spring AI的Advisor参数系统传递runtime_uuid，避免ThreadLocal的线程安全问题。
 *
 * 使用方式：
 * <pre>
 * chatClient.prompt()
 *     .user("question")
 *     .advisors(a -> {
 *         a.param(ChatMemory.CONVERSATION_ID, conversationId);
 *         a.param(WorkflowConversationAdvisor.RUNTIME_UUID, runtimeUuid);
 *     })
 *     .call()
 *     .content();
 * </pre>
 *
 * @author SCM-AI开发团队
 * @since 2025-01-09
 */
@Slf4j
@Component("workflowConversationAdvisor")
public class WorkflowConversationAdvisor implements CallAdvisor, StreamAdvisor {

    /**
     * Runtime UUID参数键
     * 用于在Advisor参数中传递运行时UUID
     */
    public static final String RUNTIME_UUID = "WORKFLOW_RUNTIME_UUID";

    /**
     * 原始用户输入参数键
     * 用于保存纯粹的用户输入，而不是渲染后的prompt
     */
    public static final String ORIGINAL_USER_INPUT = "ORIGINAL_USER_INPUT";

    @Resource
    @Lazy
    private AiWorkflowConversationContentService conversationContentService;

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        // 使用与MessageChatMemoryAdvisor相同的优先级
        // DEFAULT_CHAT_MEMORY_PRECEDENCE_ORDER = HIGHEST_PRECEDENCE + 1000
        // 两个Advisor都参与对话记录，应该在相同优先级范围内
        return Ordered.HIGHEST_PRECEDENCE + 1000;
    }

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        log.info("🎯 [WorkflowConversationAdvisor] adviseCall 被调用");

        // 执行前：保存USER消息
        String conversationId = (String) request.context().get(ChatMemory.CONVERSATION_ID);
        String runtimeUuid = (String) request.context().get(RUNTIME_UUID);

        log.info("📝 [WorkflowConversationAdvisor] 参数获取 - conversationId: {}, runtimeUuid: {}",
            conversationId, runtimeUuid);

        if (conversationId != null && runtimeUuid != null) {
            // 优先使用原始用户输入，如果为空则降级使用完整prompt
            String originalUserInput = (String) request.context().get(ORIGINAL_USER_INPUT);

            String userContent;
            if (StringUtils.isNotBlank(originalUserInput)) {
                // 使用原始用户输入（推荐）
                userContent = originalUserInput;
                log.info("👤 [WorkflowConversationAdvisor] Call 使用原始用户输入, 长度: {}", userContent.length());
            } else {
                // 降级：使用完整prompt（兼容旧代码）
                userContent = request.prompt().getUserMessage().getText();
                log.warn("⚠️ [WorkflowConversationAdvisor] Call ORIGINAL_USER_INPUT为空，降级使用完整prompt, 长度: {}",
                    userContent != null ? userContent.length() : 0);
            }

            if (userContent != null && !userContent.isEmpty()) {
                saveMessage(conversationId, runtimeUuid, AiMessageTypeConstant.MESSAGE_TYPE_USER, userContent);
            }
        } else {
            log.warn("⚠️ [WorkflowConversationAdvisor] 参数为空，跳过USER消息保存");
        }

        // 继续调用链
        ChatClientResponse response = chain.nextCall(request);

        // 执行后：保存ASSISTANT消息
        if (conversationId != null && runtimeUuid != null && response != null && response.chatResponse() != null) {
            String assistantContent = response.chatResponse().getResult().getOutput().getText();
            log.info("🤖 [WorkflowConversationAdvisor] ASSISTANT消息内容长度: {}",
                assistantContent != null ? assistantContent.length() : 0);

            if (assistantContent != null && !assistantContent.isEmpty()) {
                saveMessage(conversationId, runtimeUuid, AiMessageTypeConstant.MESSAGE_TYPE_ASSISTANT, assistantContent);
            }
        } else {
            log.warn("⚠️ [WorkflowConversationAdvisor] 响应为空或参数为空，跳过ASSISTANT消息保存");
        }

        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(ChatClientRequest request, StreamAdvisorChain chain) {
        log.info("🎯 [WorkflowConversationAdvisor] adviseStream 被调用");

        // 执行前：保存USER消息
        String conversationId = (String) request.context().get(ChatMemory.CONVERSATION_ID);
        String runtimeUuid = (String) request.context().get(RUNTIME_UUID);

        log.info("📝 [WorkflowConversationAdvisor] Stream参数获取 - conversationId: {}, runtimeUuid: {}",
            conversationId, runtimeUuid);

        if (conversationId != null && runtimeUuid != null) {
            // 优先使用原始用户输入，如果为空则降级使用完整prompt
            String originalUserInput = (String) request.context().get(ORIGINAL_USER_INPUT);

            String userContent;
            if (StringUtils.isNotBlank(originalUserInput)) {
                // 使用原始用户输入（推荐）
                userContent = originalUserInput;
                log.info("👤 [WorkflowConversationAdvisor] Stream 使用原始用户输入, 长度: {}", userContent.length());
            } else {
                // 降级：使用完整prompt（兼容旧代码）
                userContent = request.prompt().getUserMessage().getText();
                log.warn("⚠️ [WorkflowConversationAdvisor] Stream ORIGINAL_USER_INPUT为空，降级使用完整prompt, 长度: {}",
                    userContent != null ? userContent.length() : 0);
            }

            if (userContent != null && !userContent.isEmpty()) {
                saveMessage(conversationId, runtimeUuid, AiMessageTypeConstant.MESSAGE_TYPE_USER, userContent);
            }
        } else {
            log.warn("⚠️ [WorkflowConversationAdvisor] Stream参数为空，跳过USER消息保存");
        }

        // 继续调用链并聚合响应
        Flux<ChatClientResponse> responseFlux = chain.nextStream(request);

        if (conversationId == null || runtimeUuid == null) {
            log.warn("⚠️ [WorkflowConversationAdvisor] Stream参数为空，跳过响应聚合");
            return responseFlux;
        }

        // 使用ChatClientMessageAggregator聚合流式响应并保存
        return new ChatClientMessageAggregator().aggregateChatClientResponse(
            responseFlux,
            aggregatedResponse -> {
                log.info("🔄 [WorkflowConversationAdvisor] Stream响应聚合完成");

                // 聚合完成后保存ASSISTANT消息
                if (aggregatedResponse.chatResponse() != null) {
                    String assistantContent = aggregatedResponse.chatResponse().getResult().getOutput().getText();
                    log.info("🤖 [WorkflowConversationAdvisor] Stream ASSISTANT消息内容长度: {}",
                        assistantContent != null ? assistantContent.length() : 0);

                    if (assistantContent != null && !assistantContent.isEmpty()) {
                        saveMessage(conversationId, runtimeUuid, AiMessageTypeConstant.MESSAGE_TYPE_ASSISTANT, assistantContent);
                    }
                } else {
                    log.warn("⚠️ [WorkflowConversationAdvisor] Stream聚合响应为空");
                }
            }
        );
    }

    /**
     * 保存消息到数据库
     */
    private void saveMessage(String conversationId, String runtimeUuid, String messageType, String content) {
        log.info("💾 [WorkflowConversationAdvisor] 开始保存{}消息 - conversationId: {}, runtimeUuid: {}, 内容长度: {}",
            messageType, conversationId, runtimeUuid, content.length());

        try {
            String tenantId = parseTenantId(conversationId);
            log.info("🏢 [WorkflowConversationAdvisor] 解析租户ID: {}", tenantId);

            DataSourceHelper.use(tenantId);
            log.info("🔄 [WorkflowConversationAdvisor] 已切换到租户数据源: {}", tenantId);

            conversationContentService.saveMessage(
                conversationId,
                messageType,
                content,
                runtimeUuid,
                null, null, null, null
            );

            log.info("✅ [WorkflowConversationAdvisor] 成功保存{}消息 - conversationId: {}, runtimeUuid: {}",
                messageType, conversationId, runtimeUuid);
        } catch (Exception e) {
            log.error("❌ [WorkflowConversationAdvisor] 保存{}消息失败 - conversationId: {}, runtimeUuid: {}",
                messageType, conversationId, runtimeUuid, e);
        } finally {
            DataSourceHelper.close();
        }
    }

    /**
     * 从conversationId解析租户ID
     * 格式：tenantCode::workflowUuid::userId
     */
    private String parseTenantId(String conversationId) {
        return conversationId.split("::", 2)[0];
    }
}
