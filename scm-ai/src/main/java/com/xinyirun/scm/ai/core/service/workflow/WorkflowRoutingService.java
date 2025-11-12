package com.xinyirun.scm.ai.core.service.workflow;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowVo;
import com.xinyirun.scm.ai.bean.vo.workflow.WorkflowRouteDecision;
import com.xinyirun.scm.ai.core.mapper.workflow.AiWorkflowMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI工作流智能路由服务
 *
 * <p>3层路由架构：</p>
 * <ul>
 *   <li>Layer 1: 用户指定工作流 (0ms)</li>
 *   <li>Layer 2: LLM智能路由 (1-2s)</li>
 *   <li>Layer 3: 默认兜底策略 (10ms)</li>
 * </ul>
 *
 * @author SCM-AI团队
 * @since 2025-11-10
 */
@Slf4j
@Service
public class WorkflowRoutingService {

    @Resource
    private AiWorkflowMapper aiWorkflowMapper;

    @Lazy
    @Resource
    @Qualifier("workflowRoutingChatClient")
    private ChatClient routingChatClient;

    /**
     * 智能路由：根据用户输入选择最合适的工作流
     *
     * @param userInput 用户输入文本
     * @param userId 用户ID
     * @param specifiedWorkflowUuid 用户指定的工作流UUID（可选，Layer 1）
     * @return 工作流UUID，如果没有匹配返回null
     */
    public String route(String userInput, Long userId, String specifiedWorkflowUuid) {
        log.info("开始工作流路由, userInput={}, userId={}, specifiedWorkflowUuid={}",
                userInput, userId, specifiedWorkflowUuid);

        // Layer 1: 用户指定工作流 (0ms)
        if (StringUtils.isNotBlank(specifiedWorkflowUuid)) {
            log.info("Layer 1: 用户指定工作流, workflowUuid={}", specifiedWorkflowUuid);
            return specifiedWorkflowUuid;
        }

        // 获取用户可用的工作流列表
        List<AiWorkflowVo> availableWorkflows = aiWorkflowMapper.selectAvailableWorkflowsForRouting(userId);

        if (availableWorkflows == null || availableWorkflows.isEmpty()) {
            log.warn("用户没有可用的工作流, userId={}", userId);
            return null;
        }

        // Layer 2: LLM智能路由 (1-2s)
        String workflowUuid = routeByLLM(userInput, availableWorkflows);
        if (StringUtils.isNotBlank(workflowUuid)) {
            log.info("Layer 2: LLM智能路由成功, workflowUuid={}", workflowUuid);
            return workflowUuid;
        }

        // Layer 3: 默认兜底策略 (10ms)
        AiWorkflowEntity defaultWorkflow = aiWorkflowMapper.selectDefaultWorkflow();
        if (defaultWorkflow != null) {
            log.info("Layer 3: 使用默认工作流, workflowUuid={}", defaultWorkflow.getWorkflowUuid());
            return defaultWorkflow.getWorkflowUuid();
        }

        log.warn("没有找到合适的工作流, 将使用普通AI对话");
        return null;
    }

    /**
     * Layer 2: LLM智能路由
     *
     * <p>使用大语言模型进行语义理解和意图识别</p>
     *
     * @param userInput 用户输入
     * @param workflows 可用工作流Vo列表(含分类名称等扩展信息)
     * @return 匹配的工作流UUID，未匹配返回null
     */
    private String routeByLLM(String userInput, List<AiWorkflowVo> workflows) {
        if (StringUtils.isBlank(userInput) || workflows == null || workflows.isEmpty()) {
            return null;
        }

        try {
            // 构建工作流列表JSON - 包含丰富的上下文信息
            String workflowsJson = workflows.stream()
                .map(w -> String.format(
                    "{uuid:\"%s\",title:\"%s\",desc:\"%s\",description:\"%s\",keywords:\"%s\",category_name:\"%s\"}",
                    w.getWorkflowUuid(),
                    w.getTitle(),
                    safeString(w.getRemark(), ""),           // 简短描述
                    safeString(w.getDesc(), ""),             // 详细描述(LLM路由用)
                    safeString(w.getKeywords(), ""),         // 关键词
                    safeString(w.getCategoryName(), "未分类") // 分类名称
                ))
                .collect(Collectors.joining(","));

            // 构建路由提示词
            String prompt = String.format("""
                你是一个智能工作流路由助手。根据用户问题选择最合适的工作流
                根据用户输入选择最合适的工作流。如果没有合适的返回null。

                用户输入: "%s"

                可用工作流: [%s]

                示例:
                用户: "查订单ORD-001"
                工作流: [{uuid:"aaa",title:"订单查询"}]
                输出: {"workflowUuid":"aaa","reasoning":"匹配订单查询","confidence":0.9}

                用户: "今天天气"
                工作流: [{uuid:"aaa",title:"订单查询"}]
                输出: {"workflowUuid":null,"reasoning":"无关业务","confidence":0.0}

                返回JSON格式: workflowUuid, reasoning, confidence
                """,
                userInput,
                workflowsJson
            );

            log.debug("LLM路由提示词: {}", prompt);

            // 调用LLM获取路由决策
            WorkflowRouteDecision decision = routingChatClient.prompt()
                .user(prompt)
                .call()
                .entity(WorkflowRouteDecision.class);

            log.info("LLM路由决策: workflowUuid={}, reasoning={}, confidence={}",
                decision.workflowUuid(), decision.reasoning(), decision.confidence());

            // LLM说没合适的
            if (StringUtils.isBlank(decision.workflowUuid())) {
                log.info("LLM判断没有合适的工作流: reasoning={}", decision.reasoning());
                return null;
            }

            // 验证UUID存在 - 防止LLM幻觉
            boolean exists = workflows.stream()
                .anyMatch(w -> w.getWorkflowUuid().equals(decision.workflowUuid()));

            if (!exists) {
                log.error("LLM返回的workflowUuid不在可用列表中: workflowUuid={}", decision.workflowUuid());
                return null;
            }

            return decision.workflowUuid();

        } catch (Exception e) {
            log.error("LLM路由执行失败: userInput={}", userInput, e);
            return null;
        }
    }

    /**
     * 安全字符串处理：避免null或空字符串
     *
     * @param value 原始值
     * @param defaultValue 默认值
     * @return 非空字符串
     */
    private String safeString(String value, String defaultValue) {
        return StringUtils.defaultIfBlank(value, defaultValue);
    }
}
