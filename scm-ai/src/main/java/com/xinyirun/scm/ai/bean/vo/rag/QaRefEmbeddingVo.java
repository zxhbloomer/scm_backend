package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 问答记录-向量引用VO
 *
 * <p>用于前端展示QA记录引用的embedding信息</p>
 * <p>对应数据库表：ai_knowledge_base_qa_ref_embedding</p>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QaRefEmbeddingVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 问答记录ID
     */
    private String qaRecordId;

    /**
     * 向量ID（Elasticsearch文档ID）
     */
    private String embeddingId;

    /**
     * 相似度分数
     */
    private Double score;

    /**
     * 排序序号，表示向量召回的排名
     * 用于保留Elasticsearch召回时的排序顺序
     */
    private Integer rank;

    /**
     * 文本内容（从Elasticsearch查询）
     */
    private String content;

    /**
     * 知识库UUID
     */
    private String kbUuid;

    /**
     * 知识库条目UUID
     */
    private String kbItemUuid;

    /**
     * 用户ID
     */
    private Long userId;
}
