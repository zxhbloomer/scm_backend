package com.xinyirun.scm.ai.workflow.node.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 分类器分类目录
 */
@Data
public class ClassifierCategory implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 分类UUID
     */
    @JsonProperty("category_uuid")
    private String categoryUuid;

    /**
     * 分类名称
     */
    @JsonProperty("category_name")
    private String categoryName;

    /**
     * 目标节点UUID
     * 当分类匹配此分类时，工作流将跳转到此目标节点
     */
    @JsonProperty("target_node_uuid")
    private String targetNodeUuid;
}
