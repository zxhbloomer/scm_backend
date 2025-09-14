package com.xinyirun.scm.ai.kbase.request;

import com.xinyirun.scm.ai.base.BaseRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.experimental.Accessors;

/**
 * 知识库请求类
 */
@Data
@SuperBuilder
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class KbaseRequest extends BaseRequest {

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
     * 描述HTML
     */
    private String descriptionHtml;

    /**
     * 页脚HTML
     */
    private String footerHtml;

    /**
     * 语言
     */
    private String language;

    /**
     * 是否公开
     */
    private Boolean isPublic;

    /**
     * 是否发布
     */
    private Boolean published;

    /**
     * 主题
     */
    private String theme;

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
     * 验证必填字段
     */
    public boolean isValid() {
        return name != null && !name.trim().isEmpty();
    }

    /**
     * 验证UID
     */
    public boolean hasValidUid() {
        return uid != null && !uid.trim().isEmpty();
    }
}