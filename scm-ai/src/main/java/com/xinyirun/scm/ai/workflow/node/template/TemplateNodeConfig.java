package com.xinyirun.scm.ai.workflow.node.template;

import lombok.Data;

/**
 * 工作流模板节点配置
 */
@Data
public class TemplateNodeConfig {

    /**
     * 模板字符串
     * 支持 ${参数名} 的变量替换语法
     */
    private String template;
}
