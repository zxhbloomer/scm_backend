package com.xinyirun.scm.ai.bean.vo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;


import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 知识库RAG查询请求VO
 * 用于发起知识库问答查询（CompositeRAG）
 * 对标：aideepin的RAG查询请求
 *
 * @author zxh
 * @since 2025-10-12
 */
@Data
public class KnowledgeBaseQaRequestVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 知识库UUID（必填）
     */
    @NotBlank(message = "知识库UUID不能为空")
    private String kbUuid;

    /**
     * 用户问题（必填）
     */
    @NotBlank(message = "问题内容不能为空")
    private String question;

    /**
     * AI模型ID（必填）
     */
    @NotBlank(message = "AI模型ID不能为空")
    private String aiModelId;

    /**
     * AI模型名称（可选，如果为空则从aiModelId查询）
     */
    private String aiModelName;

    /**
     * 向量召回数量（TopK）
     * 默认：5
     */
    @NotNull(message = "向量召回数量不能为空")
    private Integer vectorTopK = 5;

    /**
     * 图谱召回深度
     * 默认：2
     */
    @NotNull(message = "图谱召回深度不能为空")
    private Integer graphDepth = 2;

    /**
     * 温度参数（0-1之间）
     * 控制生成文本的随机性
     * 默认：0.7
     */
    private Double temperature = 0.7;

    /**
     * 最大Token数
     * 默认：2000
     */
    private Integer maxTokens = 2000;

    /**
     * 是否启用向量检索
     * 默认：true
     */
    private Boolean enableVectorRetrieval = true;

    /**
     * 是否启用图谱推理
     * 默认：true
     */
    private Boolean enableGraphReasoning = true;

    /**
     * 提问用户ID（从SecurityUtil获取）
     */
    private Long userId;
}
