package com.xinyirun.scm.ai.workflow;

/**
 * Workflow常量定义
 *
 * @author SCM-AI团队
 * @since 2025-10-21
 */
public class WorkflowConstants {

    /**
     * 默认输入参数名称
     */
    public static final String DEFAULT_INPUT_PARAM_NAME = "input";

    /**
     * 默认输出参数名称
     */
    public static final String DEFAULT_OUTPUT_PARAM_NAME = "output";

    /**
     * 人机交互的KEY
     */
    public static final String HUMAN_FEEDBACK_KEY = "human_feedback";

    /**
     * 节点处理状态-就绪
     */
    public static final Integer NODE_PROCESS_STATUS_READY = 0;

    /**
     * 节点处理状态-处理中
     */
    public static final Integer NODE_PROCESS_STATUS_DOING = 1;

    /**
     * 节点处理状态-成功
     */
    public static final Integer NODE_PROCESS_STATUS_SUCCESS = 2;

    /**
     * 节点处理状态-失败
     */
    public static final Integer NODE_PROCESS_STATUS_FAIL = 3;

    /**
     * 工作流处理状态-就绪
     */
    public static final Integer WORKFLOW_PROCESS_STATUS_READY = 0;

    /**
     * 工作流处理状态-运行中
     */
    public static final Integer WORKFLOW_PROCESS_STATUS_RUNNING = 1;

    /**
     * 工作流处理状态-等待用户输入
     */
    public static final Integer WORKFLOW_PROCESS_STATUS_WAITING_INPUT = 2;

    /**
     * 工作流处理状态-成功
     */
    public static final Integer WORKFLOW_PROCESS_STATUS_SUCCESS = 3;

    /**
     * 工作流处理状态-失败
     */
    public static final Integer WORKFLOW_PROCESS_STATUS_FAIL = 4;

    private WorkflowConstants() {
        throw new IllegalStateException("Constant class");
    }
}
