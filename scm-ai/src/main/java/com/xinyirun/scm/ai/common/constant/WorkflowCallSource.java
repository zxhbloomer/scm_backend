package com.xinyirun.scm.ai.common.constant;

/**
 * Workflow调用来源枚举
 *
 * <p>用于区分不同入口的Workflow调用,实现数据分离存储</p>
 *
 * @author SCM-AI团队
 * @since 2025-11-11
 */
public enum WorkflowCallSource {

    /**
     * Workflow独立测试
     * <p>数据保存到: ai_workflow_runtime, ai_workflow_runtime_node</p>
     */
    WORKFLOW_TEST,

    /**
     * AI Chat调用Workflow
     * <p>数据保存到: ai_conversation_workflow_runtime, ai_conversation_workflow_runtime_node</p>
     */
    AI_CHAT
}
