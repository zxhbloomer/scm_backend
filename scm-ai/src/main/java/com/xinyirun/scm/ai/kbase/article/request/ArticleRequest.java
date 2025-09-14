package com.xinyirun.scm.ai.kbase.article.request;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.xinyirun.scm.ai.base.BaseRequest;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Accessors;

/**
 * 文章请求类
 */
@Getter
@Setter
@SuperBuilder
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class ArticleRequest extends BaseRequest {

    private String title;

    private String summary;

    private String contentMarkdown;

    private String contentHtml;

    @Builder.Default
    private List<String> tagList = new ArrayList<>();

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Builder.Default
    private Boolean top = false;

    @Builder.Default
    private Boolean published = false;

    @Builder.Default
    private Boolean markdown = false;

    @Builder.Default
    private Integer readCount = 0;

    @Builder.Default
    private Integer likeCount = 0;

    @Builder.Default
    private String editor = "";

    @Builder.Default
    private Boolean needAudit = false;

    @Builder.Default
    private String auditStatus = "PENDING";

    @Builder.Default
    private String auditOpinion = "";

    @Builder.Default
    private String auditUser = "";

    @Builder.Default
    private String elasticStatus = "NEW";

    @Builder.Default
    private String vectorStatus = "NEW";

    private String categoryUid;

    private String kbUid;

    private String componentType;

    // 添加搜索相关字段
    private String searchText;
}