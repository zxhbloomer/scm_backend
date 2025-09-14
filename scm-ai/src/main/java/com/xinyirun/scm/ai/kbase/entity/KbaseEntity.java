package com.xinyirun.scm.ai.kbase.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.ai.base.BytedeskBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 知识库配置实体 - 用于组织和管理知识库的配置信息
 * 结合知识库功能与工作空间特性
 */
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName("scm_ai_kbase")
public class KbaseEntity extends BytedeskBaseEntity {

    /**
     * 知识库名称
     */
    @TableField("name")
    private String name;

    /**
     * 知识库描述
     */
    @TableField("description")
    private String description;

    /**
     * 知识库类型 (HELPCENTER, DOCUMENTATION, FAQ, etc.)
     */
    @Builder.Default
    @TableField("kbase_type")
    private String type = "HELPCENTER";

    /**
     * 知识库子类型 (e.g., DEFAULT, COZE, DIFY, RAGFLOW)
     */
    @Builder.Default
    @TableField("kbase_subtype")
    private String subType = "DEFAULT";

    /**
     * 主标题或知识库显示的标题
     */
    @Builder.Default
    @TableField("headline")
    private String headline = "欢迎使用知识库";

    /**
     * 副标题或次要标题
     */
    @Builder.Default
    @TableField("sub_headline")
    private String subHeadline = "快速找到答案";

    /**
     * 知识库自定义URL
     */
    @Builder.Default
    @TableField("url")
    private String url = "/kbase";

    /**
     * 知识库logo图片URL
     */
    @Builder.Default
    @TableField("logo_url")
    private String logoUrl = "/images/kbase-logo.png";

    /**
     * 知识库favicon URL
     */
    @Builder.Default
    @TableField("favicon_url")
    private String faviconUrl = "/images/favicon.ico";

    /**
     * 知识库自定义封面图片URL
     */
    @TableField("cover_image_url")
    private String coverImageUrl;

    /**
     * 知识库自定义背景图片URL
     */
    @TableField("background_image_url")
    private String backgroundImageUrl;

    /**
     * 知识库主色调
     */
    @Builder.Default
    @TableField("primary_color")
    private String primaryColor = "";

    /**
     * 知识库主题名称
     */
    @Builder.Default
    @TableField("theme")
    private String theme = "DEFAULT";

    /**
     * 知识库成员数量
     */
    @Builder.Default
    @TableField("member_count")
    private Integer memberCount = 0;

    /**
     * 知识库文章数量
     */
    @Builder.Default
    @TableField("article_count")
    private Integer articleCount = 0;

    /**
     * 是否标记为收藏
     */
    @Builder.Default
    @TableField("is_favorite")
    private Boolean favorite = false;

    /**
     * 是否公开访问
     */
    @Builder.Default
    @TableField("is_public")
    private Boolean isPublic = false;

    /**
     * SEO用途的HTML描述(meta description tag)
     */
    @Builder.Default
    @TableField("description_html")
    private String descriptionHtml = "";

    /**
     * 在知识库顶部显示的自定义HTML头部代码
     */
    @Builder.Default
    @TableField("header_html")
    private String headerHtml = "";

    /**
     * 在知识库底部显示的自定义HTML底部代码
     */
    @Builder.Default
    @TableField("footer_html")
    private String footerHtml = "";

    /**
     * 知识库样式的自定义CSS/Less代码
     */
    @Builder.Default
    @TableField("css")
    private String css = "";

    /**
     * 知识库语言设置
     */
    @Builder.Default
    @TableField("language")
    private String language = "ZH_CN";

    /**
     * 知识库激活开始日期
     */
    @TableField("start_date")
    private ZonedDateTime startDate;

    /**
     * 知识库过期结束日期
     */
    @TableField("end_date")
    private ZonedDateTime endDate;

    /**
     * 知识库分类和搜索标签
     */
    @Builder.Default
    @TableField("tag_list")
    private String tagList = "";

    /**
     * 向量搜索功能的LLM嵌入提供商
     */
    @Builder.Default
    @TableField("llm_embedding_provider")
    private String embeddingProvider = "zhipu";
    
    /**
     * 向量搜索功能的LLM嵌入模型
     */
    @Builder.Default
    @TableField("llm_embedding_model")
    private String embeddingModel = "embedding-2";

    /**
     * 是否在知识库中显示聊天功能
     */
    @Builder.Default
    @TableField("show_chat")
    private Boolean showChat = false;

    /**
     * 知识库是否发布和可访问
     */
    @Builder.Default
    @TableField("published")
    private Boolean published = true;

    /**
     * 关联的代理UID，用于代理特定的知识库
     */
    @TableField("agent_uid")
    private String agentUid;


    public String getTheme() {
        return this.theme != null ? this.theme.toLowerCase() : "default";
    }
}