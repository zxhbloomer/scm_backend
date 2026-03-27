package com.xinyirun.scm.ai.workflow.node.humanfeedback;

import lombok.Data;

import java.util.List;

/**
 * 工作流人机交互节点配置
 * 支持5种交互类型: text(自由文本) / confirm(确认驳回) / select(单项选择) / form(表单填写) / table_select(表格选择)
 */
@Data
public class HumanFeedbackNodeConfig {

    /**
     * 提示文本(向后兼容)
     */
    private String tip;

    /**
     * 交互类型: text / confirm / select / form / table_select，默认text
     */
    private String interactionType;

    /**
     * 超时时间(分钟)，默认30
     */
    private Integer timeoutMinutes;

    // --- confirm 类型参数 ---

    /**
     * 确认按钮文本，默认"确认"
     */
    private String confirmText;

    /**
     * 驳回按钮文本，默认"驳回"
     */
    private String rejectText;

    /**
     * 详情说明文本
     */
    private String detail;

    // --- select 类型参数 ---

    /**
     * 静态选项列表（optionsSource=static时使用）
     */
    private List<SelectOption> options;

    // --- form 类型参数 ---

    /**
     * 表单字段列表
     */
    private List<FormField> fields;

    // --- table_select 类型参数 ---

    /**
     * 表格列定义（table_select类型使用）
     */
    private List<TableColumn> columns;

    /**
     * 获取有效的交互类型，默认text
     */
    public String getEffectiveInteractionType() {
        return (interactionType != null && !interactionType.isEmpty()) ? interactionType : "text";
    }

    /**
     * 获取有效的超时时间，默认30分钟
     */
    public int getEffectiveTimeoutMinutes() {
        return (timeoutMinutes != null && timeoutMinutes > 0) ? timeoutMinutes : 30;
    }

    @Data
    public static class SelectOption {
        private String key;
        private String label;
        /**
         * 选项携带的完整业务数据，选中后传给下游节点
         */
        private java.util.Map<String, Object> data;
    }

    @Data
    public static class FormField {
        /**
         * 字段标识
         */
        private String key;

        /**
         * 显示名称
         */
        private String label;

        /**
         * 字段类型: text / textarea / number / select
         */
        private String type;

        /**
         * 是否必填
         */
        private Boolean required;

        /**
         * type=select时的选项
         */
        private List<SelectOption> options;
    }

    @Data
    public static class TableColumn {
        /**
         * 字段标识，对应 option.data 中的 key
         */
        private String key;

        /**
         * 列头显示名称
         */
        private String label;

        /**
         * 列宽（px），可选
         */
        private Integer width;
    }
}
