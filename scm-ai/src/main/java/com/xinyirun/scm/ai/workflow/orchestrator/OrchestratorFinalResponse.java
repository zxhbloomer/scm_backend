package com.xinyirun.scm.ai.workflow.orchestrator;

import java.util.List;

/**
 * Orchestrator-Workers最终响应
 *
 * 包含Orchestrator的任务分析和所有Workers的执行结果
 * 参考: spring-ai-examples/agentic-patterns/orchestrator-workers/OrchestratorWorkers.java:134
 *
 * @param analysis      任务理解和分解策略(Orchestrator的分析)
 * @param workerResults Workers的执行结果列表(字符串格式,通常是JSON)
 *
 * @author zzxxhh
 * @since 2025-11-25
 */
public record OrchestratorFinalResponse(
    String analysis,
    List<String> workerResults
) {}
