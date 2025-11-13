package com.xinyirun.scm.ai.workflow;

/**
 * Workflow常量定义
 *
 * @author zxh
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
     * 工作流组件UUID常量
     * 对应 ai_workflow_component 表的 component_uuid 字段
     */
    public static final String COMPONENT_UUID_START = "395c7985b00411f0aeeca284340e1cbb";
    public static final String COMPONENT_UUID_END = "39686b3fb00411f0aeeca284340e1cbb";
    public static final String COMPONENT_UUID_HUMAN_FEEDBACK = "402c0936b00411f0aeeca284340e1cbb";

    /**
     * 节点处理状态-等待中
     */
    public static final Integer NODE_PROCESS_STATUS_READY = 1;

    /**
     * 节点处理状态-运行中
     */
    public static final Integer NODE_PROCESS_STATUS_DOING = 2;

    /**
     * 节点处理状态-成功
     */
    public static final Integer NODE_PROCESS_STATUS_SUCCESS = 3;

    /**
     * 节点处理状态-失败
     */
    public static final Integer NODE_PROCESS_STATUS_FAIL = 4;

    /**
     * 工作流处理状态-等待中（包括就绪和等待用户输入）
     */
    public static final Integer WORKFLOW_PROCESS_STATUS_READY = 1;

    /**
     * 工作流处理状态-运行中
     */
    public static final Integer WORKFLOW_PROCESS_STATUS_RUNNING = 2;

    /**
     * 工作流处理状态-等待用户输入（已废弃，使用READY代替）
     * @deprecated 使用 WORKFLOW_PROCESS_STATUS_READY 代替
     */
    @Deprecated
    public static final Integer WORKFLOW_PROCESS_STATUS_WAITING_INPUT = 1;

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
