package com.xinyirun.scm.ai.workflow.node.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 分类器LLM响应
 */
@Data
public class ClassifierLLMResp {

    /**
     * 关键词列表
     * LLM从输入文本中提取的关键词
     */
    private List<String> keywords;

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
}
