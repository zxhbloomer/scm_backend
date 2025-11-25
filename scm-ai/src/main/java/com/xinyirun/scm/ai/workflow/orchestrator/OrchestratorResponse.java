package com.xinyirun.scm.ai.workflow.orchestrator;

import java.util.List;

/**
 * Orchestrator的任务分解结果
 *
 * Orchestrator分析用户输入后,返回任务理解和分解的子任务列表
 * 参考: spring-ai-examples/agentic-patterns/orchestrator-workers/OrchestratorWorkers.java:122-123
 *
 * @param analysis 任务理解和分解策略(Orchestrator的分析)
 * @param tasks    分解的子任务列表(通常2-5个子任务)
 *
 * @author zzxxhh
 * @since 2025-11-25
 */
public record OrchestratorResponse(
    String analysis,
    List<SubTask> tasks
) {}
