package com.xinyirun.scm.ai.workflow.node.subworkflow;

import lombok.Data;
import java.util.List;

/**
 * 子工作流节点配置
 */
@Data
public class SubWorkflowNodeConfig {

    /**
     * 子工作流UUID
     */
    private String workflowUuid;

    /**
     * 子工作流名称（用于显示）
     */
    private String workflowName;

    /**
     * 输入参数映射
     */
    private List<InputMapping> inputMapping;

    /**
     * 输入参数映射项
     */
    @Data
    public static class InputMapping {
        /**
         * 父工作流参数名（源）
         */
        private String sourceKey;

        /**
         * 子工作流参数名（目标）
         */
        private String targetKey;
    }
}
