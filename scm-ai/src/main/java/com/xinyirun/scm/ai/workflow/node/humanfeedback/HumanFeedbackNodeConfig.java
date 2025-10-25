package com.xinyirun.scm.ai.workflow.node.humanfeedback;

import lombok.Data;

/**
 * 工作流人机交互节点配置
 */
@Data
public class HumanFeedbackNodeConfig {

    /**
     * 提示文本
     * 展示给用户的引导提示信息
     */
    private String tip;
}
