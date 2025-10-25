package com.xinyirun.scm.ai.workflow.node.start;

import lombok.Data;

/**
 * 工作流开始节点配置
 */
@Data
public class StartNodeConfig {

    /**
     * 开场白内容
     * 如果配置了开场白，开始节点会将其作为输出而不是使用工作流初始输入
     */
    private String prologue;
}
