package com.xinyirun.scm.ai.bean.vo.rag;

import com.xinyirun.scm.ai.bean.entity.rag.neo4j.EntityNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 直接关系查询结果VO
 *
 * <p>用于EntityRepository.findDirectRelationships()查询结果封装</p>
 * <p>包含目标实体、关系类型和关系强度</p>
 *
 * @author SCM AI Team
 * @since 2025-10-19
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DirectRelationshipVo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 目标实体节点
     */
    private EntityNode target;

    /**
     * 关系类型（supplies, belongs_to, signed, purchased等）
     */
    private String relationType;

    /**
     * 关系强度（0.0-1.0）
     */
    private Float strength;
}
