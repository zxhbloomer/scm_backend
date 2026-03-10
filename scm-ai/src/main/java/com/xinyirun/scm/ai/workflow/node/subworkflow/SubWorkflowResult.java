package com.xinyirun.scm.ai.workflow.node.subworkflow;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 子工作流执行结果
 * 包含最终输出和内部节点执行步骤（用于父工作流步骤面板折叠显示）
 */
@Data
@Builder
public class SubWorkflowResult {
    /** 子工作流最终输出（原 runSync 返回的 Map） */
    private Map<String, Object> outputs;
    /** 子工作流内部节点步骤（node_complete 事件摘要列表） */
    private List<Map<String, Object>> subSteps;
    /** 子工作流产生的打开页面指令（JSON字符串），需传播到父工作流 */
    private String openPageCommand;
}
