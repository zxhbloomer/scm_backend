package com.xinyirun.scm.ai.kbase.article.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;
import lombok.experimental.Accessors;

/**
 * 文章响应类 - 基于ByteDesk源码
 */
@Getter
@Setter
@Builder
@Accessors(chain = true)
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class ArticleResponse {

    private String uid;
    private String userUid;
    private String orgUid;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private String title;

    private String summary;

    private String contentMarkdown;

    private String contentHtml;

    private String type;

    private List<String> tagList;

    // 有效开始日期
    private LocalDateTime startDate;

    // 有效结束日期
    private LocalDateTime endDate;

    private Boolean top;

    private Boolean published;

    private Boolean markdown;

    private Integer readCount;

    private Integer likeCount;

    // editor 编辑者
    private String editor;

    // 是否需要审核
    private Boolean needAudit;

    // 审核状态
    private String auditStatus;

    // 审核意见
    private String auditOpinion;

    // 审核人
    private String auditUser;

    private String categoryUid;

    private String kbUid;

    // elastic 索引状态 (ArticleStatusEnum: PENDING, PROCESSING, SUCCESS, ERROR)
    private String elasticStatus;

    // 向量索引状态 (ArticleStatusEnum: PENDING, PROCESSING, SUCCESS, ERROR)
    private String vectorStatus;

    // 
    public String getStartDate() {
        if (startDate == null) {
            return null;
        }
        return startDate.toString();
    }

    public String getEndDate() {
        if (endDate == null) {
            return null;
        }
        return endDate.toString();
    }

}