package com.xinyirun.scm.ai.kbase.response;

import com.xinyirun.scm.ai.base.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 知识库响应类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KbaseResponse extends BaseResponse {

    private String uid;

    /**
     * 知识库名称
     */
    private String name;

    /**
     * 知识库描述
     */
    private String description;

    /**
     * 知识库类型
     */
    private String type;

    /**
     * 子类型
     */
    private String subType;

    /**
     * 标题
     */
    private String headline;

    /**
     * 子标题
     */
    private String subHeadline;

    /**
     * URL
     */
    private String url;

    /**
     * Logo URL
     */
    private String logoUrl;

    /**
     * Favicon URL
     */
    private String faviconUrl;

    /**
     * 主题
     */
    private String theme;

    /**
     * 成员数量
     */
    private Integer memberCount;

    /**
     * 文章数量
     */
    private Integer articleCount;

    /**
     * 是否收藏
     */
    private Boolean favorite;

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 是否发布
     */
    private Boolean published;

    /**
     * 描述HTML
     */
    private String descriptionHtml;

    /**
     * 页头HTML
     */
    private String headerHtml;

    /**
     * 页脚HTML
     */
    private String footerHtml;

    /**
     * 自定义CSS
     */
    private String css;

    /**
     * 语言
     */
    private String language;

    /**
     * 级别
     */
    private String level;

    /**
     * 组织UID
     */
    private String orgUid;

    /**
     * 代理UID
     */
    private String agentUid;

    /**
     * 嵌入提供者
     */
    private String embeddingProvider;

    /**
     * 嵌入模型
     */
    private String embeddingModel;

    /**
     * 显示聊天
     */
    private Boolean showChat;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 创建者
     */
    private String createdBy;

    /**
     * 更新者
     */
    private String updatedBy;

    /**
     * 获取显示名称
     */
    public String getDisplayName() {
        return name != null ? name : uid;
    }

    /**
     * 是否公开
     */
    public Boolean isPublic() {
        return this.isPublic;
    }

    /**
     * 是否已发布
     */
    public Boolean isPublished() {
        return this.published;
    }

    /**
     * 是否收藏
     */
    public Boolean isFavorite() {
        return this.favorite;
    }

    /**
     * 获取类型描述
     */
    public String getTypeDescription() {
        if (type == null) {
            return "未知";
        }

        switch (type.toUpperCase()) {
            case "HELPCENTER":
                return "帮助中心";
            case "NOTEBASE":
                return "内部知识库";
            case "LLM":
                return "AI知识库";
            case "AUTOREPLY":
                return "自动回复";
            case "QUICKREPLY":
                return "快捷回复";
            case "TABOO":
                return "敏感词";
            default:
                return type;
        }
    }

    /**
     * 获取统计信息
     */
    public String getStatistics() {
        int members = memberCount != null ? memberCount : 0;
        int articles = articleCount != null ? articleCount : 0;
        
        return String.format("成员: %d, 文章: %d", members, articles);
    }
}