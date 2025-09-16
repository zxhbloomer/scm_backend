package com.xinyirun.scm.ai.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@Data
@ConfigurationProperties(prefix = "scm.ai")
public class ScmAiProperties {

    /**
     * 主要AI厂商
     */
    private String primaryProvider = "openai";

    /**
     * 备用AI厂商
     */
    private String fallbackProvider = "zhipuai";

    /**
     * 厂商配置
     */
    private Map<String, ProviderConfig> providers = new HashMap<>();

    /**
     * MCP配置
     */
    private McpConfig mcp = new McpConfig();

    @Data
    public static class ProviderConfig {
        /**
         * API密钥
         */
        private String apiKey;

        /**
         * API基础URL
         */
        private String baseUrl;

        /**
         * 默认模型
         */
        private String model;

        /**
         * 请求超时时间（毫秒）
         */
        private Integer timeout = 30000;

        /**
         * 最大重试次数
         */
        private Integer maxRetries = 3;

        /**
         * 是否启用
         */
        private Boolean enabled = true;
    }

    @Data
    public static class McpConfig {
        /**
         * 是否启用MCP服务器
         */
        private boolean enabled = true;

        /**
         * 最大并行工具调用数
         */
        private int maxParallelCalls = 5;

        /**
         * 工具配置
         */
        private Map<String, ToolConfig> tools = new HashMap<>();

        /**
         * 服务器配置
         */
        private ServerConfig server = new ServerConfig();

        @Data
        public static class ToolConfig {
            private boolean enabled = true;
            private int rateLimit = 100;
        }

        @Data
        public static class ServerConfig {
            private String name = "SCM MCP Server";
            private String version = "1.0.0";
            private int port = 8089;
        }
    }
}