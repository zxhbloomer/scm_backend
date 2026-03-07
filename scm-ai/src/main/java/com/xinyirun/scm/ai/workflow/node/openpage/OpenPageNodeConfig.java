package com.xinyirun.scm.ai.workflow.node.openpage;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * OpenPage节点配置
 * 支持两种模式：dialog(弹窗，默认) 和 route(页面导航)
 */
@Data
public class OpenPageNodeConfig {

    /**
     * 模型名称（可选，dialog模式调用LLM生成JSON）
     */
    @JsonProperty("model_name")
    private String modelName;

    /**
     * 自定义提示词（可选，dialog模式调用LLM）
     */
    private String prompt;

    /**
     * 打开模式：dialog(弹窗，默认) / route(页面导航)
     */
    @JsonProperty("open_mode")
    private String openMode;

    /**
     * 目标路由路径（route模式），如 /po/order
     */
    private String route;

    /**
     * 页面模式（route模式）：list/new/view/edit/approve
     */
    @JsonProperty("page_mode")
    private String pageMode;

    /**
     * 是否启用人机交互（route模式）
     */
    @JsonProperty("interaction_enabled")
    private Boolean interactionEnabled;

    /**
     * 交互类型：user_select/user_confirm/user_form
     */
    @JsonProperty("interaction_type")
    private String interactionType;

    /**
     * 交互描述（显示给用户的提示文字）
     */
    @JsonProperty("interaction_description")
    private String interactionDescription;

    /**
     * 超时时间（分钟），默认30
     */
    @JsonProperty("timeout_minutes")
    private Integer timeoutMinutes;
}
