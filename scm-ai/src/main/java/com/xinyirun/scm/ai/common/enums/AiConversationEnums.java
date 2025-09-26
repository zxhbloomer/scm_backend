package com.xinyirun.scm.ai.common.enums;

/**
 * AI会话相关枚举定义
 *
 * 定义AI会话系统中使用的各种枚举类型
 * 包括内容类型、状态、AI模型提供商等
 *
 * @author zxh
 * @since 1.0.0
 */
public class AiConversationEnums {

    /**
     * 会话内容类型枚举
     */
    public enum ContentType {
        /**
         * 用户消息
         */
        USER("USER", "用户消息"),

        /**
         * AI回复
         */
        AI("AI", "AI回复"),

        /**
         * 系统消息
         */
        SYSTEM("SYSTEM", "系统消息");

        private final String code;
        private final String description;

        ContentType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据代码获取枚举
         *
         * @param code 代码
         * @return 枚举值，如果不存在返回null
         */
        public static ContentType fromCode(String code) {
            for (ContentType type : values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * 会话状态枚举
     */
    public enum ConversationStatus {
        /**
         * 活跃状态
         */
        ACTIVE("ACTIVE", "活跃"),

        /**
         * 已归档
         */
        ARCHIVED("ARCHIVED", "已归档"),

        /**
         * 已删除
         */
        DELETED("DELETED", "已删除");

        private final String code;
        private final String description;

        ConversationStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据代码获取枚举
         */
        public static ConversationStatus fromCode(String code) {
            for (ConversationStatus status : values()) {
                if (status.getCode().equals(code)) {
                    return status;
                }
            }
            return null;
        }
    }

    /**
     * 处理状态枚举
     */
    public enum ProcessStatus {
        /**
         * 处理中
         */
        PENDING("PENDING", "处理中"),

        /**
         * 处理成功
         */
        SUCCESS("SUCCESS", "处理成功"),

        /**
         * 处理失败
         */
        FAILED("FAILED", "处理失败"),

        /**
         * 处理超时
         */
        TIMEOUT("TIMEOUT", "处理超时");

        private final String code;
        private final String description;

        ProcessStatus(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据代码获取枚举
         */
        public static ProcessStatus fromCode(String code) {
            for (ProcessStatus status : values()) {
                if (status.getCode().equals(code)) {
                    return status;
                }
            }
            return null;
        }
    }

    /**
     * AI模型提供商枚举
     */
    public enum AiProvider {
        /**
         * OpenAI
         */
        OPENAI("openai", "OpenAI", "ChatGPT, GPT-4等模型"),

        /**
         * Anthropic
         */
        ANTHROPIC("anthropic", "Anthropic", "Claude系列模型"),

        /**
         * 智谱AI
         */
        ZHIPUAI("zhipuai", "智谱AI", "GLM系列模型"),

        /**
         * 通义千问
         */
        DASHSCOPE("dashscope", "通义千问", "阿里云大模型服务"),

        /**
         * DeepSeek
         */
        DEEPSEEK("deepseek", "DeepSeek", "DeepSeek系列模型"),

        /**
         * Ollama本地模型
         */
        OLLAMA("ollama", "Ollama", "本地部署模型"),

        /**
         * Minimax
         */
        MINIMAX("minimax", "Minimax", "海螺AI模型");

        private final String code;
        private final String name;
        private final String description;

        AiProvider(String code, String name, String description) {
            this.code = code;
            this.name = name;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据代码获取枚举
         */
        public static AiProvider fromCode(String code) {
            for (AiProvider provider : values()) {
                if (provider.getCode().equals(code)) {
                    return provider;
                }
            }
            return null;
        }
    }

    /**
     * 错误类型枚举
     */
    public enum ErrorType {
        /**
         * 会话不存在
         */
        CONVERSATION_NOT_FOUND("CONVERSATION_NOT_FOUND", "会话不存在"),

        /**
         * 内容不存在
         */
        CONTENT_NOT_FOUND("CONTENT_NOT_FOUND", "内容不存在"),

        /**
         * 无效内容
         */
        INVALID_CONTENT("INVALID_CONTENT", "无效内容"),

        /**
         * AI服务不可用
         */
        AI_SERVICE_UNAVAILABLE("AI_SERVICE_UNAVAILABLE", "AI服务不可用"),

        /**
         * 敏感内容
         */
        SENSITIVE_CONTENT("SENSITIVE_CONTENT", "包含敏感内容"),

        /**
         * 会话过长
         */
        CONVERSATION_TOO_LONG("CONVERSATION_TOO_LONG", "会话过长"),

        /**
         * 权限不足
         */
        PERMISSION_DENIED("PERMISSION_DENIED", "权限不足"),

        /**
         * 系统错误
         */
        SYSTEM_ERROR("SYSTEM_ERROR", "系统错误");

        private final String code;
        private final String description;

        ErrorType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据代码获取枚举
         */
        public static ErrorType fromCode(String code) {
            for (ErrorType errorType : values()) {
                if (errorType.getCode().equals(code)) {
                    return errorType;
                }
            }
            return null;
        }
    }

    /**
     * WebSocket消息类型枚举
     */
    public enum WebSocketMessageType {
        /**
         * 连接消息
         */
        CONNECT("CONNECT", "连接"),

        /**
         * 断开连接消息
         */
        DISCONNECT("DISCONNECT", "断开连接"),

        /**
         * 聊天消息
         */
        CHAT("CHAT", "聊天"),

        /**
         * 状态更新消息
         */
        STATUS("STATUS", "状态更新"),

        /**
         * 错误消息
         */
        ERROR("ERROR", "错误"),

        /**
         * 心跳消息
         */
        HEARTBEAT("HEARTBEAT", "心跳");

        private final String code;
        private final String description;

        WebSocketMessageType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }

        /**
         * 根据代码获取枚举
         */
        public static WebSocketMessageType fromCode(String code) {
            for (WebSocketMessageType type : values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
            return null;
        }
    }

    /**
     * 文件类型枚举
     */
    public enum FileType {
        /**
         * 文本文件
         */
        TEXT("txt", "文本文件", "text/plain"),

        /**
         * PDF文件
         */
        PDF("pdf", "PDF文件", "application/pdf"),

        /**
         * Word文档
         */
        DOC("doc", "Word文档", "application/msword"),

        /**
         * Word文档（新版）
         */
        DOCX("docx", "Word文档", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"),

        /**
         * Markdown文件
         */
        MARKDOWN("md", "Markdown文件", "text/markdown"),

        /**
         * JSON文件
         */
        JSON("json", "JSON文件", "application/json"),

        /**
         * XML文件
         */
        XML("xml", "XML文件", "application/xml");

        private final String extension;
        private final String description;
        private final String mimeType;

        FileType(String extension, String description, String mimeType) {
            this.extension = extension;
            this.description = description;
            this.mimeType = mimeType;
        }

        public String getExtension() {
            return extension;
        }

        public String getDescription() {
            return description;
        }

        public String getMimeType() {
            return mimeType;
        }

        /**
         * 根据扩展名获取枚举
         */
        public static FileType fromExtension(String extension) {
            if (extension == null) {
                return null;
            }

            // 移除可能的点号前缀
            String cleanExtension = extension.startsWith(".") ? extension.substring(1) : extension;

            for (FileType type : values()) {
                if (type.getExtension().equalsIgnoreCase(cleanExtension)) {
                    return type;
                }
            }
            return null;
        }

        /**
         * 检查文件类型是否支持
         */
        public static boolean isSupported(String extension) {
            return fromExtension(extension) != null;
        }
    }
}