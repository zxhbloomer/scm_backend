package com.xinyirun.scm.ai.bean.entity.rag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI知识库问答记录-图谱引用实体类
 * 对应数据库表：ai_knowledge_base_qa_ref_graph
 * 对标：aideepin KnowledgeBaseQaRefGraph
 *
 * @author zxh
 * @since 2025-10-12
 */
@Data
@TableName("ai_knowledge_base_qa_ref_graph")
public class AiKnowledgeBaseQaRefGraphEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（自增）
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 问答记录ID
     * 关联：ai_knowledge_base_qa.id
     */
    @TableField("qa_record_id")
    private String qaRecordId;

    /**
     * 从用户问题中解析出来的实体（JSON格式）
     */
    @TableField("entities_from_question")
    private String entitiesFromQuestion;

    /**
     * 从Neo4j图数据库中查找得到的图谱（JSON格式）
     */
    @TableField("graph_from_store")
    private String graphFromStore;

    /**
     * 提问用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 关联的图谱文本块ID
     * 关联：ai_knowledge_base_graph_segment.id
     * 对标：aideepin将整个图存储为JSON导致冗余，scm-ai通过FK关联避免冗余
     */
    @TableField("graph_segment_id")
    private Long graphSegmentId;

    /**
     * 图谱召回的相关性得分（0-1之间，保留4位小数）
     * 计算公式：entityMatchRatio(40%) + graphCompleteRatio(30%) + relationDensity(30%)
     */
    @TableField("relevance_score")
    private BigDecimal relevanceScore;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;
}
