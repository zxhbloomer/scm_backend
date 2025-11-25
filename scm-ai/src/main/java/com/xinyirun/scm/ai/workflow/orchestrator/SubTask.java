package com.xinyirun.scm.ai.workflow.orchestrator;

import java.util.Map;

/**
 * Orchestrator分解的子任务
 *
 * 用于描述Orchestrator将复杂任务分解后的单个子任务信息
 * 参考: spring-ai-examples/agentic-patterns/orchestrator-workers/OrchestratorWorkers.java:110-111
 *
 * @param type        任务类型: "workflow" 或 "mcp"
 * @param target      执行目标: workflow_uuid 或 mcp_tool_name
 * @param description 任务描述(给LLM看,用于理解任务意图)
 * @param params      执行参数(JSON格式的Map,传递给Worker执行)
 *
 * @author zzxxhh
 * @since 2025-11-25
 */
public record SubTask(
    String type,
    String target,
    String description,
    Map<String, Object> params
) {}
