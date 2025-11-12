package com.xinyirun.scm.ai.bean.vo.workflow;

/**
 * 工作流路由决策结果
 *
 * <p>LLM智能路由返回的结构化数据，用于类型安全的路由决策</p>
 *
 * @param workflowUuid 选中的工作流UUID，如果没有合适的返回null
 * @param reasoning 推理过程，说明为什么选择这个工作流
 * @param confidence 置信度 (0.0-1.0)，建议阈值>=0.7
 *
 * @author SCM-AI团队
 * @since 2025-11-10
 */
public record WorkflowRouteDecision(
    String workflowUuid,
    String reasoning,
    Double confidence
) {
}
