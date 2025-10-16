package com.xinyirun.scm.ai.bean.vo.response;

import com.xinyirun.scm.ai.bean.vo.rag.QaRefEmbeddingVo;
import com.xinyirun.scm.ai.bean.vo.rag.QaRefGraphVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 知识库问答响应VO
 * 包含完整的RAG查询结果
 * 对标：aideepin的QA响应结构
 *
 * @author zxh
 * @since 2025-10-12
 */
@Data
public class KnowledgeBaseQaResponseVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 问答记录ID
     */
    private String id;

    /**
     * 问答记录UUID
     */
    private String uuid;

    /**
     * 知识库ID
     */
    private String kbId;

    /**
     * 知识库UUID
     */
    private String kbUuid;

    /**
     * 知识库标题（冗余字段，方便前端展示）
     */
    private String kbTitle;

    /**
     * 用户问题
     */
    private String question;

    /**
     * 实际发送给LLM的Prompt（包含上下文）
     */
    private String prompt;

    /**
     * Prompt消耗的Token数
     */
    private Integer promptTokens;

    /**
     * AI回答内容
     */
    private String answer;

    /**
     * Answer消耗的Token数
     */
    private Integer answerTokens;

    /**
     * 来源文件ID列表（逗号分隔）
     */
    private String sourceFileIds;

    /**
     * 来源文件名称列表（前端展示用）
     */
    private List<String> sourceFileNames;

    /**
     * 提问用户ID
     */
    private Long userId;

    /**
     * 提问用户名称（冗余字段）
     */
    private String userName;

    /**
     * AI模型ID
     */
    private String aiModelId;

    /**
     * AI模型名称
     */
    private String aiModelName;

    /**
     * 启用状态（1-启用，0-禁用）
     */
    private Integer enableStatus;

    /**
     * 创建时间（时间戳，毫秒）
     */
    private Long createTime;

    /**
     * 更新时间（时间戳，毫秒）
     */
    private Long updateTime;

    /**
     * 向量检索引用列表
     * 包含召回的向量片段及其分数
     */
    private List<QaRefEmbeddingVo> embeddingRefs;

    /**
     * 图谱推理引用列表
     * 包含召回的图谱片段及其相关性评分
     */
    private List<QaRefGraphVo> graphRefs;

    /**
     * 总Token消耗
     * 计算值：promptTokens + answerTokens
     */
    private Integer totalTokens;

    /**
     * 是否已删除（0-未删除，1-已删除）
     */
    private Integer isDeleted;
}
