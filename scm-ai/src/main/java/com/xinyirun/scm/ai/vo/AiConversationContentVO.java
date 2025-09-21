package com.xinyirun.scm.ai.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * AI会话内容业务视图对象
 *
 * 用于业务逻辑处理的会话内容数据传输对象
 * 包含消息的详细信息和相关的AI处理数据
 *
 * @author SCM-AI模块
 * @since 1.0.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Accessors(chain = true)
public class AiConversationContentVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 内容ID
     */
    private String id;

    /**
     * 会话ID
     */
    private String conversation_id;

    /**
     * 内容类型
     * USER - 用户消息
     * AI - AI回复
     * SYSTEM - 系统消息
     */
    private String type;

    /**
     * 类型显示名称
     */
    private String type_name;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 内容摘要
     */
    private String content_summary;

    /**
     * 内容长度
     */
    private Integer content_length;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long create_time;

    /**
     * 创建时间（格式化字符串）
     */
    private String create_time_str;

    /**
     * 处理状态
     * PENDING - 处理中
     * SUCCESS - 处理成功
     * FAILED - 处理失败
     * TIMEOUT - 处理超时
     */
    private String status;

    /**
     * 状态显示名称
     */
    private String status_name;

    /**
     * 错误消息
     */
    private String error_message;

    /**
     * 创建用户ID
     */
    private String create_user;

    /**
     * 创建用户名称
     */
    private String create_user_name;

    /**
     * 租户ID
     */
    private String tenant_id;

    /**
     * AI模型信息
     */
    private AiModelInfo ai_model_info;

    /**
     * 性能统计信息
     */
    private PerformanceStats performance_stats;

    /**
     * 内容分析结果
     */
    private ContentAnalysis content_analysis;

    /**
     * 是否包含敏感信息
     */
    private Boolean has_sensitive_info;

    /**
     * 敏感信息类型列表
     */
    private List<String> sensitive_types;

    /**
     * 内容标签
     */
    private List<String> tags;

    /**
     * 关联的文件列表
     */
    private List<AttachmentInfo> attachments;

    /**
     * 引用的其他消息ID
     */
    private String reference_message_id;

    /**
     * 引用的消息内容摘要
     */
    private String reference_content_summary;

    /**
     * 消息序号（在会话中的顺序）
     */
    private Integer sequence_number;

    /**
     * 是否为重新生成的内容
     */
    private Boolean is_regenerated;

    /**
     * 重新生成次数
     */
    private Integer regeneration_count;

    /**
     * 用户反馈评分（1-5）
     */
    private Integer user_rating;

    /**
     * 用户反馈评论
     */
    private String user_feedback;

    /**
     * 是否已读
     */
    private Boolean is_read;

    /**
     * 阅读时间
     */
    private Long read_time;

    /**
     * AI模型信息内嵌类
     */
    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class AiModelInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * AI模型提供商
         */
        private String provider;

        /**
         * AI模型名称
         */
        private String model_name;

        /**
         * 模型版本
         */
        private String model_version;

        /**
         * 是否使用了备用模型
         */
        private Boolean use_fallback;

        /**
         * 模型参数设置
         */
        private String model_parameters;

        /**
         * 模型温度参数
         */
        private Double temperature;

        /**
         * 最大Token限制
         */
        private Integer max_tokens;
    }

    /**
     * 性能统计信息内嵌类
     */
    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class PerformanceStats implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * Token消耗统计
         */
        private Integer token_usage;

        /**
         * 输入Token数量
         */
        private Integer input_tokens;

        /**
         * 输出Token数量
         */
        private Integer output_tokens;

        /**
         * 处理耗时（毫秒）
         */
        private Long processing_time;

        /**
         * 网络请求耗时（毫秒）
         */
        private Long network_time;

        /**
         * 重试次数
         */
        private Integer retry_count;

        /**
         * 队列等待时间（毫秒）
         */
        private Long queue_time;

        /**
         * 响应速度评级
         */
        private String speed_rating;
    }

    /**
     * 内容分析结果内嵌类
     */
    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class ContentAnalysis implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 情感分析结果
         */
        private String sentiment;

        /**
         * 情感得分（-1到1）
         */
        private Double sentiment_score;

        /**
         * 主题分类
         */
        private List<String> topics;

        /**
         * 关键词提取
         */
        private List<String> keywords;

        /**
         * 语言检测结果
         */
        private String language;

        /**
         * 内容质量评分（1-10）
         */
        private Integer quality_score;

        /**
         * 可读性评分
         */
        private Integer readability_score;

        /**
         * 复杂度评级
         */
        private String complexity_level;

        /**
         * 包含的URL列表
         */
        private List<String> urls;

        /**
         * 提及的实体
         */
        private List<String> entities;
    }

    /**
     * 附件信息内嵌类
     */
    @Data
    @NoArgsConstructor
    @Accessors(chain = true)
    public static class AttachmentInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 1L;

        /**
         * 文件ID
         */
        private String file_id;

        /**
         * 文件名
         */
        private String file_name;

        /**
         * 文件类型
         */
        private String file_type;

        /**
         * 文件大小（字节）
         */
        private Long file_size;

        /**
         * 文件URL
         */
        private String file_url;

        /**
         * 缩略图URL
         */
        private String thumbnail_url;

        /**
         * 上传时间
         */
        private Long upload_time;
    }

    // ==================== 业务方法 ====================

    /**
     * 检查是否为用户消息
     */
    public boolean isUserMessage() {
        return "USER".equals(this.type);
    }

    /**
     * 检查是否为AI回复
     */
    public boolean isAiMessage() {
        return "AI".equals(this.type);
    }

    /**
     * 检查是否为系统消息
     */
    public boolean isSystemMessage() {
        return "SYSTEM".equals(this.type);
    }

    /**
     * 检查处理是否成功
     */
    public boolean isProcessingSuccess() {
        return "SUCCESS".equals(this.status);
    }

    /**
     * 检查是否正在处理
     */
    public boolean isPending() {
        return "PENDING".equals(this.status);
    }

    /**
     * 检查处理是否失败
     */
    public boolean isFailed() {
        return "FAILED".equals(this.status) || "TIMEOUT".equals(this.status);
    }

    /**
     * 获取简短摘要
     */
    public String getBriefSummary(int maxLength) {
        if (content == null || content.trim().isEmpty()) {
            return "";
        }

        if (maxLength <= 0) {
            maxLength = 50;
        }

        String cleanedContent = content.trim();
        if (cleanedContent.length() <= maxLength) {
            return cleanedContent;
        }

        return cleanedContent.substring(0, maxLength - 3) + "...";
    }

    /**
     * 获取响应时间描述
     */
    public String getResponseTimeDescription() {
        if (performance_stats == null || performance_stats.getProcessing_time() == null) {
            return "未知";
        }

        long time = performance_stats.getProcessing_time();
        if (time < 1000) {
            return time + "ms";
        } else if (time < 60000) {
            return String.format("%.1fs", time / 1000.0);
        } else {
            return String.format("%.1fm", time / 60000.0);
        }
    }

    /**
     * 获取Token效率描述
     */
    public String getTokenEfficiencyDescription() {
        if (performance_stats == null || performance_stats.getToken_usage() == null || content_length == null || content_length == 0) {
            return "未知";
        }

        double efficiency = (double) performance_stats.getToken_usage() / content_length;
        if (efficiency < 0.5) {
            return "高效";
        } else if (efficiency < 1.0) {
            return "正常";
        } else if (efficiency < 2.0) {
            return "一般";
        } else {
            return "低效";
        }
    }

    /**
     * 获取情感倾向描述
     */
    public String getSentimentDescription() {
        if (content_analysis == null || content_analysis.getSentiment_score() == null) {
            return "中性";
        }

        double score = content_analysis.getSentiment_score();
        if (score > 0.5) {
            return "积极";
        } else if (score > 0.1) {
            return "偏积极";
        } else if (score > -0.1) {
            return "中性";
        } else if (score > -0.5) {
            return "偏消极";
        } else {
            return "消极";
        }
    }

    /**
     * 检查是否有附件
     */
    public boolean hasAttachments() {
        return attachments != null && !attachments.isEmpty();
    }

    /**
     * 获取附件数量
     */
    public int getAttachmentCount() {
        return attachments != null ? attachments.size() : 0;
    }

    /**
     * 检查是否需要用户反馈
     */
    public boolean needsUserFeedback() {
        return isAiMessage() && isProcessingSuccess() && user_rating == null;
    }

    /**
     * 获取质量评级描述
     */
    public String getQualityRatingDescription() {
        if (content_analysis == null || content_analysis.getQuality_score() == null) {
            return "未评估";
        }

        int score = content_analysis.getQuality_score();
        if (score >= 9) {
            return "优秀";
        } else if (score >= 7) {
            return "良好";
        } else if (score >= 5) {
            return "一般";
        } else if (score >= 3) {
            return "较差";
        } else {
            return "很差";
        }
    }

    /**
     * 初始化默认性能统计
     */
    public void initDefaultPerformanceStats() {
        if (this.performance_stats == null) {
            this.performance_stats = new PerformanceStats()
                .setToken_usage(0)
                .setInput_tokens(0)
                .setOutput_tokens(0)
                .setProcessing_time(0L)
                .setRetry_count(0);
        }
    }

    /**
     * 初始化默认内容分析
     */
    public void initDefaultContentAnalysis() {
        if (this.content_analysis == null) {
            this.content_analysis = new ContentAnalysis()
                .setSentiment("neutral")
                .setSentiment_score(0.0)
                .setLanguage("zh-CN")
                .setQuality_score(5)
                .setComplexity_level("medium");
        }
    }
}