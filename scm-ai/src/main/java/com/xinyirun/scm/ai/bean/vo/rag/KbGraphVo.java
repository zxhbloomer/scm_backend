package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.Data;

import java.util.List;

/**
 * 知识库图谱数据VO类
 *
 * <p>用于展示Neo4j中存储的图谱结构数据，包含顶点和边</p>
 *
 * @author SCM-AI重构团队
 * @since 2025-10-09
 */
@Data
public class KbGraphVo {

    /**
     * 图谱顶点列表
     */
    private List<GraphVertexVo> vertices;

    /**
     * 图谱边列表
     */
    private List<GraphEdgeVo> edges;

    /**
     * 图谱顶点VO类
     */
    @Data
    public static class GraphVertexVo {

        /**
         * 顶点ID（Neo4j内部ID）
         */
        private Long id;

        /**
         * 实体名称
         */
        private String name;

        /**
         * 实体类型
         */
        private String type;

        /**
         * 所属知识库UUID
         */
        private String kbUuid;

        /**
         * 所属知识项UUID
         */
        private String kbItemUuid;
    }

    /**
     * 图谱边VO类
     */
    @Data
    public static class GraphEdgeVo {

        /**
         * 边ID（Neo4j内部ID）
         */
        private Long id;

        /**
         * 源顶点ID
         */
        private Long sourceId;

        /**
         * 目标顶点ID
         */
        private Long targetId;

        /**
         * 关系类型
         */
        private String type;

        /**
         * 关系描述
         */
        private String description;
    }
}
