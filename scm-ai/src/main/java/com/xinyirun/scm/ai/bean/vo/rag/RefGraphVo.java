package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 图谱引用VO
 *
 * <p>对应 aideepin DTO：RefGraphDto</p>
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefGraphVo {

    /**
     * 从用户问题中解析出的实体列表
     */
    private List<String> entitiesFromQuestion;

    /**
     * 图谱顶点列表
     */
    private List<GraphVertexVo> vertices;

    /**
     * 图谱边列表
     */
    private List<GraphEdgeVo> edges;

    /**
     * 图谱段ID（MySQL ai_knowledge_base_graph_segment表的ID）
     * scm-ai扩展字段，用于关联segment数据
     */
    private Long graphSegmentId;

    /**
     * 相关性分数（图谱召回质量评分）
     * scm-ai扩展字段，用于评估检索质量
     */
    private java.math.BigDecimal relevanceScore;

    /**
     * 图谱顶点VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraphVertexVo {
        /**
         * Neo4j节点ID
         */
        private String id;

        /**
         * 节点名称
         */
        private String name;

        /**
         * 节点类型
         */
        private String type;

        /**
         * 节点描述
         */
        private String description;
    }

    /**
     * 图谱边VO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraphEdgeVo {
        /**
         * Neo4j关系ID
         */
        private String id;

        /**
         * 关系名称
         */
        private String name;

        /**
         * 关系描述
         */
        private String description;

        /**
         * 起始节点ID
         */
        private String source;

        /**
         * 结束节点ID
         */
        private String target;
    }
}
