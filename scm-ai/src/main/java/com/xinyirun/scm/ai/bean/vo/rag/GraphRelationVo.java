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
 * <p>
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
     * 
     */
    private String relationId;

    /**
     * 关系类型
     * 
     */
    private String relationType;

    /**
     * 关系描述
     * 
     */
    private String description;

    /**
     * 源实体ID
     * 
     */
    private String sourceEntityId;

    /**
     * 源实体名称
     */
    private String sourceEntityName;

    /**
     * 目标实体ID
     * 
     */
    private String targetEntityId;

    /**
     * 目标实体名称
     */
    private String targetEntityName;

    /**
     * 目标实体类型
     * 用于buildRefGraphVo时构建完整的target顶点信息
     */
    private String targetEntityType;

    /**
     * 目标实体描述
     * 用于buildRefGraphVo时构建完整的target顶点信息
     */
    private String targetDescription;

    /**
     * 关系权重（0-1）
     * scm-ai扩展字段
     */
    private Double weight;
}
