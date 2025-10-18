package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 图谱检索结果VO
 *
 * <p>用于GraphRetrievalService的检索结果</p>
 * <p>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphSearchResultVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 实体ID（Neo4j节点ID）
     * 
     */
    private String entityId;

    /**
     * 实体名称
     * 
     */
    private String entityName;

    /**
     * 实体类型
     * 
     */
    private String entityType;

    /**
     * 实体描述
     * 
     */
    private String description;

    /**
     * 知识库UUID
     */
    private String kbUuid;

    /**
     * 知识库条目UUID
     */
    private String kbItemUuid;

    /**
     * 关联的边（关系）列表
     * 
     */
    private List<GraphRelationVo> relations;

    /**
     * 相关性分数（基于实体匹配度计算）
     * scm-ai扩展字段，用于排序
     */
    private Double score;

    /**
     * 图谱段ID（MySQL ai_knowledge_base_graph_segment表的ID）
     * scm-ai扩展字段，用于关联segment数据
     */
    private Long segmentId;
}
