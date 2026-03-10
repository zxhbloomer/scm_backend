package com.xinyirun.scm.ai.workflow.node.openpage;

import com.alibaba.fastjson2.annotation.JSONField;
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
    @JSONField(name = "open_mode")
    private String openMode;

    /**
     * 页面模式（兼容旧配置）：list/new/view/edit/approve
     * 前端UI已移除此配置项，但保留字段以兼容数据库中已保存的旧工作流配置
     * 优先级低于上游输入变量中的 page_mode
     */
    @JsonProperty("page_mode")
    @JSONField(name = "page_mode")
    private String pageMode;

    /**
     * 是否显示执行过程输出到chat流
     * true(默认): 显示在聊天界面
     * false: 不显示，但结果仍传递给下游节点
     */
    @JsonProperty("show_process_output")
    @JSONField(name = "show_process_output")
    private Boolean showProcessOutput = true;
}
