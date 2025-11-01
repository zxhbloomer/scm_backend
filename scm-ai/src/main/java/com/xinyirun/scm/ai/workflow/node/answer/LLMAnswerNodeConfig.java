package com.xinyirun.scm.ai.workflow.node.answer;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 工作流LLM回答节点配置
 */
@Data
public class LLMAnswerNodeConfig {

    /**
     * LLM提示词模板
     * 支持 ${参数名} 的变量替换语法
     * 为空时使用上游节点输出作为提示词
     */
    private String prompt;

    /**
     * 使用的模型名称
     */
    @NotNull
    @JsonProperty("model_name")
    private String modelName;

    /**
     * 是否使用流式回复
     */
    private Boolean streaming;
}
