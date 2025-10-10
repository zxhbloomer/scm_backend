package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 知识库问答记录VO
 *
 * <p>对应 aideepin DTO：KbQaDto</p>
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiKnowledgeBaseQaVo {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 唯一标识符
     */
    private String uuid;

    /**
     * 所属知识库ID
     */
    private String kbId;

    /**
     * 所属知识库UUID
     */
    private String kbUuid;

    /**
     * 用户的原始问题
     */
    private String question;

    /**
     * 提供给LLM的提示词（包含RAG上下文）
     */
    private String prompt;

    /**
     * 提示词消耗的token
     */
    private Integer promptTokens;

    /**
     * 答案
     */
    private String answer;

    /**
     * 答案消耗的token
     */
    private Integer answerTokens;

    /**
     * 来源文档id,以逗号隔开
     */
    private String sourceFileIds;

    /**
     * 提问用户id
     */
    private Long userId;

    /**
     * AI模型ID
     */
    private String aiModelId;

    /**
     * 向量引用列表
     */
    private List<RefEmbeddingVo> embeddingRefs;

    /**
     * 图谱引用
     */
    private RefGraphVo graphRef;

    /**
     * 创建时间（时间戳毫秒）
     */
    private Long createTime;
}
