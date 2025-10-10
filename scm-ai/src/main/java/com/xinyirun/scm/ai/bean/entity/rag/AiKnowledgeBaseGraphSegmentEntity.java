package com.xinyirun.scm.ai.bean.entity.rag;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 知识库-图谱-文本块实体类
 *
 * <p>对应数据库表：ai_knowledge_base_graph_segment</p>
 * <p>对应 aideepin 实体：KnowledgeBaseGraphSegment</p>
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
     * 创建用户id
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 创建时间（时间戳毫秒）
     */
    @TableField("create_time")
    private Long createTime;

    /**
     * 更新时间（时间戳毫秒）
     */
    @TableField("update_time")
    private Long updateTime;

    /**
     * 是否删除（0：未删除；1：已删除）
     */
    @TableField("is_deleted")
    private Integer isDeleted;
}
