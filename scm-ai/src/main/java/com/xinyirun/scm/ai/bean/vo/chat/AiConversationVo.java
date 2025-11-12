package com.xinyirun.scm.ai.bean.vo.chat;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI会话业务视图对象
 *
 * 用于业务逻辑处理的会话数据传输对象
 * 包含会话的详细信息和相关的业务数据
 *
 * @author zxh
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
public class AiConversationVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 会话ID
     */
    private String id;

    /**
     * 会话标题
     */
    private String title;

    /**
     * 创建用户名称
     */
    private String create_user_name;

    /**
     * 创建时间
     */
    private LocalDateTime c_time;

    /**
     * 创建时间（格式化字符串）
     */
    private String c_time_str;

    /**
     * 会话状态
     * ACTIVE - 活跃
     * ARCHIVED - 已归档
     * DELETED - 已删除
     */
    private String status;

    /**
     * 状态显示名称
     */
    private String status_name;


    /**
     * 页面代码
     */
    private String page_code;

    /**
     * 页面名称
     */
    private String page_name;

    /**
     * 最后一条消息内容
     */
    private String last_content;

    /**
     * 最后一条消息摘要
     */
    private String last_content_summary;

    /**
     * 最后更新时间（毫秒时间戳）
     */
    private Long last_update_time;

    /**
     * 最后更新时间（格式化字符串）
     */
    private String last_update_time_str;

    /**
     * 消息数量
     */
    private Integer message_count;

    /**
     * 用户消息数量
     */
    private Integer user_message_count;

    /**
     * AI回复数量
     */
    private Integer ai_message_count;

    /**
     * 总Token消耗
     */
    private Integer total_token_usage;

    /**
     * 平均响应时间（毫秒）
     */
    private Long avg_response_time;

    /**
     * 主要使用的AI模型
     */
    private String primary_ai_model;

    /**
     * 主要使用的AI提供商
     */
    private String primary_ai_provider;

    /**
     * 是否包含敏感信息
     */
    private Boolean has_sensitive_info;

    /**
     * 会话标签列表
     */
    private List<String> tags;

    /**
     * 会话内容列表（用于详细展示）
     */
    private List<AiConversationContentVo> contents;

    /**
     * 是否收藏
     */
    private Boolean is_favorite;

    /**
     * 会话总时长（从第一条到最后一条消息的时间跨度，分钟）
     */
    private Long duration_minutes;

    /**
     * 会话质量评分（1-5分）
     */
    private Integer quality_score;

    /**
     * 最后活跃时间（毫秒时间戳）
     */
    private Long last_active_time;

    /**
     * 访问次数
     */
    private Integer access_count;

    /**
     * 分享链接
     */
    private String share_url;

    /**
     * 导出格式列表
     */
    private List<String> export_formats;

    /**
     * 权限信息
     */
    private PermissionInfo permission;

    /**
     * 统计信息
     */
    private StatisticsInfo statistics;

    /**
     * 当前活跃工作流UUID
     */
    private String currentWorkflowUuid;

    /**
     * 当前工作流运行时UUID
     */
    private String currentRuntimeUuid;

    /**
     * 工作流状态
     * IDLE-空闲, WORKFLOW_RUNNING-执行中, WORKFLOW_WAITING_INPUT-等待输入
     */
    private String workflowState;



    /**
     * 权限信息内嵌类
     */
    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PermissionInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 是否可读
         */
        private Boolean can_read;

        /**
         * 是否可写
         */
        private Boolean can_write;

        /**
         * 是否可删除
         */
        private Boolean can_delete;

        /**
         * 是否可分享
         */
        private Boolean can_share;

        /**
         * 是否可导出
         */
        private Boolean can_export;
    }

    /**
     * 统计信息内嵌类
     */
    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class StatisticsInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 今日消息数
         */
        private Integer today_message_count;

        /**
         * 本周消息数
         */
        private Integer week_message_count;

        /**
         * 本月消息数
         */
        private Integer month_message_count;

        /**
         * 今日Token消耗
         */
        private Integer today_token_usage;

        /**
         * 本周Token消耗
         */
        private Integer week_token_usage;

        /**
         * 本月Token消耗
         */
        private Integer month_token_usage;

        /**
         * 最快响应时间（毫秒）
         */
        private Long fastest_response_time;

        /**
         * 最慢响应时间（毫秒）
         */
        private Long slowest_response_time;
    }

}