package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI知识库VO类
 *
 * <p>与AiKnowledgeBaseEntity一一对应</p>
 *
 * @author SCM-AI重构团队
 * @since 2025-10-03
 */
@Data
public class AiKnowledgeBaseVo {

    /**
     * 主键ID
     */
    private String id;

    /**
     * 知识库UUID（业务主键）
     */
    private String kbUuid;

    /**
     * 知识库标题
     */
    private String title;

    /**
     * 备注说明
     */
    private String remark;

    /**
     * 是否公开（0-私有，1-公开）
     */
    private Integer isPublic;

    /**
     * 所有者ID
     */
    private String ownerId;

    /**
     * 所有者名称
     */
    private String ownerName;

    /**
     * 文档项数量
     */
    private Integer itemCount;

    /**
     * 是否严格模式(0-非严格,1-严格)
     */
    private Integer isStrict;

    /**
     * 文档切块重叠数量
     */
    private Integer ingestMaxOverlap;

    /**
     * 索引时使用的LLM模型名称
     */
    private String ingestModelName;

    /**
     * 索引时使用的LLM模型ID
     */
    private String ingestModelId;

    /**
     * Token估计器
     */
    private String ingestTokenEstimator;

    /**
     * 向量化模型
     */
    private String ingestEmbeddingModel;

    /**
     * 检索最大结果数
     */
    private Integer retrieveMaxResults;

    /**
     * 检索最小分数
     */
    private java.math.BigDecimal retrieveMinScore;

    /**
     * LLM温度参数
     */
    private java.math.BigDecimal queryLlmTemperature;

    /**
     * 系统消息模板
     */
    private String querySystemMessage;

    /**
     * 点赞数
     */
    private Integer starCount;

    /**
     * 向量数量
     */
    private Integer embeddingCount;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    private Long c_id;

    /**
     * 修改人id
     */
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    private Integer dbversion;
}
