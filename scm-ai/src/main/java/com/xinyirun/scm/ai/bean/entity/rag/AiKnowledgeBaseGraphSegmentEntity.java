package com.xinyirun.scm.ai.bean.entity.rag;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 知识库-图谱-文本块实体类
 *
 * <p>对应数据库表：ai_knowledge_base_graph_segment</p>
 * 
 *
 * @author SCM AI Team
 * @since 2025-10-04
 */
@Data
@TableName("ai_knowledge_base_graph_segment")
public class AiKnowledgeBaseGraphSegmentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 唯一标识（32字符无连字符）
     */
    @TableField("uuid")
    private String uuid;

    /**
     * 所属知识库uuid
     * 关联：ai_knowledge_base.uuid
     */
    @TableField("kb_uuid")
    private String kbUuid;

    /**
     * 所属知识点uuid
     */
    @TableField("kb_item_uuid")
    private String kbItemUuid;

    /**
     * 内容（文本块内容）
     */
    @TableField("remark")
    private String remark;

    /**
     * 是否删除（0：未删除；1：已删除）
     */
    @TableField("is_deleted")
    private Integer isDeleted;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 数据版本,乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;
}
