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

    /**
     * 图谱检索使用的模型名称
     * <p>仅当enableGraphRetrieval=true时生效</p>
     * <p>用于控制图谱实体提取使用的LLM模型</p>
     */
    @JsonProperty("graph_model_name")
    private String graphModelName;

    /**
     * 查询关键词模板
     * <p>支持使用 ${变量名} 语法引用输入变量</p>
     * <p>为空时自动使用上游节点的默认输出作为查询关键词,保持向后兼容</p>
     * <p>示例: ${input} 表示引用上个节点的执行结果</p>
     */
    @JsonProperty("query_template")
    private String queryTemplate;

    /**
     * 是否使用临时知识库
     * <p>默认false,使用永久知识库,保持向后兼容</p>
     * <p>设置为true时,表示使用上游临时知识库节点创建的临时知识库</p>
     * <p>前端通过检测上游节点类型自动设置此字段</p>
     */
    @JsonProperty("is_temp_kb")
    private Boolean isTempKb;

    /**
     * 临时知识库节点UUID
     * <p>当isTempKb=true时有效</p>
     * <p>用于前端验证上游临时知识库节点是否仍然存在</p>
     * <p>格式:节点的uuid字段(如:1234567890)</p>
     * <p>前端通过X6 graph事件监听维护此字段</p>
     */
    @JsonProperty("temp_kb_node_uuid")
    private String tempKbNodeUuid;

    /**
     * 是否显示执行过程输出到chat流
     * true(默认): 流式输出显示在聊天界面
     * false: 不显示流式输出，但结果仍传递给下游节点
     */
    @JsonProperty("show_process_output")
    private Boolean showProcessOutput = true;
}
