package com.xinyirun.scm.ai.workflow.node.template;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    /**
     * 工作流共享输出，开启后其他节点可引用本节点输出
     */
    @JsonProperty("shared_output")
    private Boolean sharedOutput = false;
}
