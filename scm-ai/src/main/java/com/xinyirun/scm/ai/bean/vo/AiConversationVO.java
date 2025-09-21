package com.xinyirun.scm.ai.bean.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * AI会话业务视图对象
 *
 * 用于业务逻辑处理的会话数据传输对象
 * 包含会话的详细信息和相关的业务数据
 *
 * @author SCM-AI模块
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
public class AiConversationVO implements Serializable {

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
     * 创建用户ID
     */
    private String create_user;

    /**
     * 创建用户名称
     */
    private String create_user_name;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long create_time;

    /**
     * 创建时间（格式化字符串）
     */
    private String create_time_str;

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
     * 租户ID
     */
    private String tenant_id;

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
    private List<AiConversationContentVO> contents;

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

    // ==================== 业务方法 ====================

    /**
     * 检查会话是否为活跃状态
     */
    public boolean isActive() {
        return "ACTIVE".equals(this.status);
    }

    /**
     * 检查会话是否已归档
     */
    public boolean isArchived() {
        return "ARCHIVED".equals(this.status);
    }

    /**
     * 检查会话是否已删除
     */
    public boolean isDeleted() {
        return "DELETED".equals(this.status);
    }

    /**
     * 获取会话简要描述
     */
    public String getBriefDescription() {
        StringBuilder desc = new StringBuilder();
        desc.append(title != null ? title : "未命名会话");

        if (message_count != null && message_count > 0) {
            desc.append(" (").append(message_count).append("条消息)");
        }

        return desc.toString();
    }

    /**
     * 获取最后活跃时间描述
     */
    public String getLastActiveDescription() {
        if (last_active_time == null) {
            return "从未活跃";
        }

        long now = System.currentTimeMillis();
        long diff = now - last_active_time;
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);

        if (minutes < 1) {
            return "刚刚活跃";
        } else if (minutes < 60) {
            return minutes + "分钟前";
        } else if (hours < 24) {
            return hours + "小时前";
        } else {
            return days + "天前";
        }
    }

    /**
     * 检查是否有新消息
     */
    public boolean hasNewMessages() {
        if (last_update_time == null || last_active_time == null) {
            return false;
        }
        return last_update_time > last_active_time;
    }

    /**
     * 获取会话效率评价
     */
    public String getEfficiencyRating() {
        if (avg_response_time == null) {
            return "未知";
        }

        if (avg_response_time < 2000) {
            return "极快";
        } else if (avg_response_time < 5000) {
            return "快速";
        } else if (avg_response_time < 10000) {
            return "正常";
        } else if (avg_response_time < 20000) {
            return "较慢";
        } else {
            return "缓慢";
        }
    }

    /**
     * 获取Token使用效率
     */
    public String getTokenEfficiency() {
        if (total_token_usage == null || message_count == null || message_count == 0) {
            return "未知";
        }

        double avgTokenPerMessage = (double) total_token_usage / message_count;

        if (avgTokenPerMessage < 100) {
            return "高效";
        } else if (avgTokenPerMessage < 300) {
            return "正常";
        } else if (avgTokenPerMessage < 500) {
            return "一般";
        } else {
            return "低效";
        }
    }

    /**
     * 初始化默认权限
     */
    public void initDefaultPermissions() {
        if (this.permission == null) {
            this.permission = new PermissionInfo()
                .setCan_read(true)
                .setCan_write(true)
                .setCan_delete(true)
                .setCan_share(false)
                .setCan_export(true);
        }
    }

    /**
     * 初始化默认统计信息
     */
    public void initDefaultStatistics() {
        if (this.statistics == null) {
            this.statistics = new StatisticsInfo()
                .setToday_message_count(0)
                .setWeek_message_count(0)
                .setMonth_message_count(0)
                .setToday_token_usage(0)
                .setWeek_token_usage(0)
                .setMonth_token_usage(0);
        }
    }
}