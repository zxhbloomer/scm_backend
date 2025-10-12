package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * 问答记录-图谱引用VO
 *
 * <p>用于前端展示QA记录引用的图谱信息</p>
 * <p>对应aideepin DTO：KbQaRefGraphDto</p>
 * <p>对应数据库表：ai_knowledge_base_qa_ref_graph</p>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QaRefGraphVo implements Serializable {

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
     * 从用户问题中提取的实体列表
     * 对应aideepin的entitiesFromQuestion字段
     */
    private List<String> entitiesFromQuestion;

    /**
     * 图谱顶点列表
     * 对应aideepin的vertices
     */
    private List<RefGraphVo.GraphVertexVo> vertices;

    /**
     * 图谱边列表
     * 对应aideepin的edges
     */
    private List<RefGraphVo.GraphEdgeVo> edges;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 关联的图谱文本块ID
     * 关联：ai_knowledge_base_graph_segment.id
     * 对标：aideepin将整个图存储为JSON导致冗余，scm-ai通过FK关联避免冗余
     */
    private Long graphSegmentId;

    /**
     * 图谱召回的相关性得分（0-1之间，保留4位小数）
     * 计算公式：entityMatchRatio(40%) + graphCompleteRatio(30%) + relationDensity(30%)
     */
    private BigDecimal relevanceScore;
}
