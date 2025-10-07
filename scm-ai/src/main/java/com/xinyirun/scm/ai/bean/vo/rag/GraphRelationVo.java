package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 图谱关系VO
 *
 * <p>描述图谱中两个实体之间的关系</p>
 * <p>对应aideepin的GraphEdge</p>
 *
 * @author SCM AI Team
 * @since 2025-10-06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GraphRelationVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 关系ID（Neo4j关系ID）
     * 对应aideepin的GraphEdge.id
     */
    private String relationId;

    /**
     * 关系类型
     * 对应aideepin的GraphEdge.name（如：WORKS_FOR, LOCATED_IN等）
     */
    private String relationType;

    /**
     * 关系描述
     * 对应aideepin的GraphEdge.description
     */
    private String description;

    /**
     * 源实体ID
     * 对应aideepin的Triple.left（源节点）
     */
    private String sourceEntityId;

    /**
     * 源实体名称
     */
    private String sourceEntityName;

    /**
     * 目标实体ID
     * 对应aideepin的Triple.right（目标节点）
     */
    private String targetEntityId;

    /**
     * 目标实体名称
     */
    private String targetEntityName;

    /**
     * 关系权重（0-1）
     * scm-ai扩展字段
     */
    private Double weight;
}
