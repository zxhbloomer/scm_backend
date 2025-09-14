package com.xinyirun.scm.ai.kbase.article;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import com.xinyirun.scm.ai.base.BytedeskBaseEntity;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 帮助文档抽象基类
 */
@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractArticleEntity extends BytedeskBaseEntity {

    private static final long serialVersionUID = 1L;

    @TableField("title")
    private String title;

    @TableField("summary")
    private String summary;

    @TableField("content_markdown")
    private String contentMarkdown;

    @TableField("content_html")
    private String contentHtml;

    @Builder.Default
    @TableField("article_type")
    private String type = "TEXT";

    @Builder.Default
    @TableField("tag_list")
    private String tagList = "";

    @Builder.Default
    @TableField("is_top")
    private Boolean top = false;

    @Builder.Default
    @TableField("is_published")
    private Boolean published = false;

    @Builder.Default
    @TableField("is_markdown")
    private Boolean markdown = false;

    @Builder.Default
    @TableField("read_count")
    private Integer readCount = 0;

    @Builder.Default
    @TableField("like_count")
    private Integer likeCount = 0;

    @Builder.Default
    @TableField("editor")
    private String editor = "";

    @TableField("start_date")
    private ZonedDateTime startDate;

    @TableField("end_date")
    private ZonedDateTime endDate;

    @Builder.Default
    @TableField("need_audit")
    private Boolean needAudit = false;

    @TableField("audit_status")
    private String auditStatus;

    @Builder.Default
    @TableField("audit_opinion")
    private String auditOpinion = "";

    @Builder.Default
    @TableField("audit_user")
    private String auditUser = "";

    @Builder.Default
    @TableField("is_password_protected")
    private Boolean isPasswordProtected = false;

    @TableField("password")
    private String password;

    @TableField("category_uid")
    private String categoryUid;

    @TableField("kbase_uid")
    private String kbaseUid;

    @Builder.Default
    @TableField("create_user")
    private String user = "{}";

    @TableField("elastic_status")
    private String elasticStatus;

    @TableField("vector_status")
    private String vectorStatus;

    @Builder.Default
    @TableField("doc_id_list")
    private String docIdList = "";
}