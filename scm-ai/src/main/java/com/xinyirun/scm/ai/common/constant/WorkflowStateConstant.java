package com.xinyirun.scm.ai.common.constant;

/**
 * 工作流状态常量(KISS简化版)
 *
 * @author SCM-AI团队
 * @since 2025-11-10
 */
public class WorkflowStateConstant {

    /**
     * 空闲状态 - 没有活跃工作流
     */
    public static final String STATE_IDLE = "IDLE";

    /**
     * 执行中 - 工作流正在执行
     */
    public static final String STATE_WORKFLOW_RUNNING = "WORKFLOW_RUNNING";

    /**
     * 等待输入 - 工作流暂停,等待用户提供输入
     */
    public static final String STATE_WORKFLOW_WAITING_INPUT = "WORKFLOW_WAITING_INPUT";

    // KISS优化: 移除ROUTING和WORKFLOW_COMPLETED瞬态状态
}
