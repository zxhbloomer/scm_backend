package com.xinyirun.scm.ai.bean.vo.rag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 问答记录-图谱引用VO
 *
 * <p>用于前端展示QA记录引用的图谱信息</p>
 * <p>
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
     * 
     */
    private List<String> entitiesFromQuestion;

    /**
     * 图谱顶点列表
     * 
     */
    private List<RefGraphVo.GraphVertexVo> vertices;

    /**
     * 图谱边列表
     * 
     */
    private List<RefGraphVo.GraphEdgeVo> edges;

    /**
     * 用户ID
     */
    private Long userId;
}
