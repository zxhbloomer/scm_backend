package com.xinyirun.scm.ai.bean.entity.chat;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI对话消息-图谱引用实体类
 * 对应数据表：ai_conversation_content_ref_graph
 *
 * 功能说明：记录对话消息引用的图谱检索结果（Neo4j）
 *
 * @author SCM-AI团队
 * @since 2025-11-08
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName(value = "ai_conversation_content_ref_graph")
public class AiConversationContentRefGraphEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息ID
     * 关联：ai_conversation_content.message_id
     */
    @TableField("message_id")
    private String messageId;

    /**
     * 知识库ID
     */
    @TableField("kb_id")
    private String kbId;

    /**
     * 从问题提取的实体
     * LLM提取的实体信息（JSON格式）
     */
    @TableField("entities_from_question")
    private String entitiesFromQuestion;

    /**
     * 图谱数据
     * Neo4j查询结果（JSON格式）
     */
    @TableField("graph_from_store")
    private String graphFromStore;

    /**
     * 实体数量统计
     */
    @TableField("entity_count")
    private Integer entityCount;

    /**
     * 关系数量统计
     */
    @TableField("relation_count")
    private Integer relationCount;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 创建时间
     */
    @TableField("c_time")
    private LocalDateTime cTime;
}
