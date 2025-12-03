package com.xinyirun.scm.ai.bean.entity.rag;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * AI知识库文档项实体类
 *
 * <p>对应数据库表：ai_knowledge_base_item</p>
 * <p>注意：字段命名使用 snake_case 与数据库一致</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Data
@TableName("ai_knowledge_base_item")
public class AiKnowledgeBaseItemEntity {

    /**
     * 主键ID（自增）
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 文档UUID（业务主键，32字符无连字符）
     */
    @TableField("item_uuid")
    private String itemUuid;

    /**
     * 所属知识库ID
     */
    @TableField("kb_id")
    private String kbId;

    /**
     * 所属知识库UUID
     */
    @TableField("kb_uuid")
    private String kbUuid;

    /**
     * 文档标题
     */
    @TableField("title")
    private String title;

    /**
     * 简介（文档内容摘要，最多200字符）
     */
    @TableField("brief")
    private String brief;

    /**
     * 备注（文档完整内容）
     */
    @TableField("remark")
    private String remark;

    /**
     * 源文件ID
     */
    @TableField("source_file_id")
    private String sourceFileId;

    /**
     * 源文件名称
     */
    @TableField("source_file_name")
    private String sourceFileName;

    /**
     * 源文件上传时间（时间戳毫秒）
     */
    @TableField("source_file_upload_time")
    private Long sourceFileUploadTime;

    /**
     * 向量化模型
     */
    @TableField("embedding_model")
    private String embeddingModel;

    /**
     * 向量化状态(1-待处理,2-处理中,3-已完成,4-失败)
     */
    @TableField("embedding_status")
    private Integer embeddingStatus;

    /**
     * 向量化状态变更时间
     */
    @TableField("embedding_status_change_time")
    private LocalDateTime embeddingStatusChangeTime;

    /**
     * 图谱化状态(1-待处理,2-处理中,3-已完成,4-失败)
     */
    @TableField("graphical_status")
    private Integer graphicalStatus;

    /**
     * 图谱化状态变更时间
     */
    @TableField("graphical_status_change_time")
    private LocalDateTime graphicalStatusChangeTime;

    /**
     * 创建时间
     */
    @TableField(value="c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime c_time;

    /**
     * 修改时间
     */
    @TableField(value="u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime u_time;

    /**
     * 创建人id
     */
    @TableField(value="c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long c_id;

    /**
     * 修改人id
     */
    @TableField(value="u_id", fill = FieldFill.INSERT_UPDATE)
    private Long u_id;

    /**
     * 数据版本，乐观锁使用
     */
    @Version
    @TableField("dbversion")
    private Integer dbversion;
}
