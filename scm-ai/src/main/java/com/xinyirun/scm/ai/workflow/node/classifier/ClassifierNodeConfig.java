package com.xinyirun.scm.ai.workflow.node.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 工作流分类器节点配置
 */
@Data
public class ClassifierNodeConfig {

    /**
     * 分类列表
     */
    private List<ClassifierCategory> categories = new ArrayList<>();

    /**
     * 分类使用的模型名称
     */
    @JsonProperty("model_name")
    private String modelName;
}
