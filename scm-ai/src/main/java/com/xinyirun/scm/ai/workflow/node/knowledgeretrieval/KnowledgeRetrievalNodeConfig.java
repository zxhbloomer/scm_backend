package com.xinyirun.scm.ai.workflow.node.knowledgeretrieval;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 工作流知识检索节点配置
 */
@Data
public class KnowledgeRetrievalNodeConfig {

    /**
     * 知识库UUID
     */
    @JsonProperty("knowledge_base_uuid")
    private String knowledgeBaseUuid;

    /**
     * 知识库名称
     */
    @JsonProperty("knowledge_base_name")
    private String knowledgeBaseName;

    /**
     * 相似度阈值（0.0-1.0）
     */
    private Double score;

    /**
     * 返回的最大结果数
     */
    @JsonProperty("top_n")
    private Integer topN;

    /**
     * 是否严格模式
     */
    @JsonProperty("is_strict")
    private Boolean isStrict;

    /**
     * 默认响应内容
     * 当检索结果为空时使用
     */
    @JsonProperty("default_response")
    private String defaultResponse;

    /**
     * 是否启用图谱检索
     * <p>默认false，仅使用向量检索，保持向后兼容</p>
     * <p>设置为true时，同时执行向量检索和图谱检索，合并结果</p>
     */
    @JsonProperty("enable_graph_retrieval")
    private Boolean enableGraphRetrieval;
}
