package com.xinyirun.scm.ai.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * AI聊天记录实体类 - MongoDB存储
 * 
 * 用于存储用户与AI的聊天记录，支持租户隔离、用户隔离和页面code隔离
 * 支持文件上传和多种消息类型
 * 使用MongoDB存储以支持灵活的文档结构和大数据量
 * 
 * @author SCM-AI模块
 * @since 1.0.0
 */
@Data
@Accessors(chain = true)
@Document(collection = "ai_chat_record")
@CompoundIndexes({
    @CompoundIndex(name = "tenant_user_page_idx", def = "{'tenantId': 1, 'userId': 1, 'pageCode': 1}"),
    @CompoundIndex(name = "session_time_idx", def = "{'sessionId': 1, 'createTime': 1}"),
    @CompoundIndex(name = "tenant_time_idx", def = "{'tenantId': 1, 'createTime': -1}")
})
public class AiChatRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * MongoDB主键ID
     * MongoDB自动生成的ObjectId
     */
    @Id
    private String id;

    /**
     * 租户ID - 多租户隔离
     * 必填字段，用于数据隔离
     */
    @Field("tenantId")
    private String tenantId;

    /**
     * 用户ID - 用户隔离
     * 记录是哪个用户的聊天记录
     */
    @Field("userId")
    private Long userId;

    /**
     * 页面代码 - 页面隔离
     * 用于区分不同业务页面的聊天记录
     * 例如: 'PO_LIST', 'SO_EDIT', 'INVENTORY_VIEW'
     */
    @Field("pageCode")
    private String pageCode;

    /**
     * 会话ID - 同一个页面可能有多个会话
     * 用于将相关的多轮对话组织在一起
     * 格式: tenantId_userId_pageCode_timestamp
     */
    @Field("sessionId")
    private String sessionId;

    /**
     * 消息类型
     * USER - 用户消息
     * AI - AI回复
     * SYSTEM - 系统消息
     */
    @Field("messageType")
    private String messageType;

    /**
     * 消息内容
     * 用户输入的问题或AI的回复内容
     */
    @Field("content")
    private String content;

    /**
     * 附件文件信息列表
     * 支持多个文件上传
     */
    @Field("attachments")
    private List<AttachmentInfo> attachments;

    /**
     * AI模型信息
     * 记录使用的AI相关信息
     */
    @Field("aiInfo")
    private AiModelInfo aiInfo;

    /**
     * 消息状态
     * PENDING - 处理中
     * SUCCESS - 成功
     * FAILED - 失败
     * TIMEOUT - 超时
     */
    @Field("status")
    private String status;

    /**
     * 错误信息
     * 当处理失败时记录错误详情
     */
    @Field("errorMessage")
    private String errorMessage;

    /**
     * 性能统计信息
     */
    @Field("performanceStats")
    private PerformanceStats performanceStats;

    /**
     * 创建时间
     */
    @Field("createTime")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @Field("updateTime")
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     */
    @Field("createUserId")
    private Long createUserId;

    /**
     * 修改人ID
     */
    @Field("updateUserId")
    private Long updateUserId;

    /**
     * 扩展信息
     * 用于存储额外的业务数据，支持动态扩展
     */
    @Field("extra")
    private Object extra;

    /**
     * 附件信息内嵌文档
     */
    @Data
    @Accessors(chain = true)
    public static class AttachmentInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = -4927252465438033768L;
        /**
         * 文件原始名称
         */
        private String originalName;
        
        /**
         * 存储文件名
         */
        private String storedName;
        
        /**
         * 文件路径
         */
        private String filePath;
        
        /**
         * 文件大小（字节）
         */
        private Long fileSize;
        
        /**
         * 文件类型/MIME类型
         */
        private String contentType;
        
        /**
         * 上传时间
         */
        private LocalDateTime uploadTime;
    }

    /**
     * AI模型信息内嵌文档
     */
    @Data
    @Accessors(chain = true)
    public static class AiModelInfo implements Serializable {
        @Serial
        private static final long serialVersionUID = 5630276947718732989L;
        /**
         * AI模型提供商
         * openai, anthropic, zhipuai, dashscope等
         */
        private String provider;
        
        /**
         * AI模型名称
         * gpt-4, claude-3, glm-4等
         */
        private String modelName;
        
        /**
         * 模型版本
         */
        private String modelVersion;
        
        /**
         * 是否使用了备用模型
         */
        private Boolean useFallback;
    }

    /**
     * 性能统计信息内嵌文档
     */
    @Data
    @Accessors(chain = true)
    public static class PerformanceStats implements Serializable {
        @Serial
        private static final long serialVersionUID = 4813825917483091305L;
        /**
         * Token消耗统计
         * 记录本次对话消耗的token数量
         */
        private Integer tokenUsage;
        
        /**
         * 输入token数量
         */
        private Integer inputTokens;
        
        /**
         * 输出token数量
         */
        private Integer outputTokens;
        
        /**
         * 处理耗时（毫秒）
         * 记录AI响应时间，用于性能分析
         */
        private Long processingTime;
        
        /**
         * 网络请求耗时（毫秒）
         */
        private Long networkTime;
        
        /**
         * 重试次数
         */
        private Integer retryCount;
    }

    // 消息类型常量
    public static final String MESSAGE_TYPE_USER = "USER";
    public static final String MESSAGE_TYPE_AI = "AI";
    public static final String MESSAGE_TYPE_SYSTEM = "SYSTEM";

    // 消息状态常量
    public static final String STATUS_PENDING = "PENDING";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_TIMEOUT = "TIMEOUT";

    /**
     * 生成会话ID
     * 格式: tenantId_userId_pageCode_timestamp
     */
    public static String generateSessionId(String tenantId, Long userId, String pageCode) {
        return String.format("%s_%d_%s_%d", tenantId, userId, pageCode, System.currentTimeMillis());
    }
}