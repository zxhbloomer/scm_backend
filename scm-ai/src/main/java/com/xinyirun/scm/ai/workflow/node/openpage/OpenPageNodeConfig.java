package com.xinyirun.scm.ai.workflow.node.openpage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * OpenPage节点配置
 * 当前OpenPage节点不调用LLM，字段保留为可选以兼容已有工作流配置
 */
@Data
public class OpenPageNodeConfig {

    /**
     * 模型名称（可选，当前OpenPage节点不调用LLM）
     */
    @JsonProperty("model_name")
    private String modelName;

    /**
     * 自定义提示词（可选，当前OpenPage节点不调用LLM）
     */
    private String prompt;
}
