package com.xinyirun.scm.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * AI模块配置类
 *
 * @author AI Assistant
 * @since 2025-09-21
 */
@Configuration
@EnableAsync
@ConfigurationProperties(prefix = "scm.ai")
@Data
public class AiConfiguration {

    /**
     * 默认模型提供商
     */
    private String defaultModelProvider = "openai";

    /**
     * 默认模型名称
     */
    private String defaultModelName = "gpt-3.5-turbo";

    /**
     * 默认温度参数
     */
    private Double defaultTemperature = 0.7;

    /**
     * 默认最大token数
     */
    private Integer defaultMaxTokens = 2048;

    /**
     * 上下文最大长度
     */
    private Integer maxContextLength = 4096;

    /**
     * 会话超时时间（分钟）
     */
    private Integer sessionTimeoutMinutes = 30;

    /**
     * 是否启用异步处理
     */
    private Boolean enableAsync = true;

    /**
     * 是否启用消息缓存
     */
    private Boolean enableMessageCache = true;

    /**
     * 缓存过期时间（秒）
     */
    private Integer cacheExpireSeconds = 300;

    /**
     * 每用户每日最大token限制
     */
    private Long dailyTokenLimitPerUser = 10000L;

    /**
     * 每用户每月最大token限制
     */
    private Long monthlyTokenLimitPerUser = 300000L;

    /**
     * 是否启用token使用统计
     */
    private Boolean enableTokenStatistics = true;

    /**
     * 是否启用访问日志
     */
    private Boolean enableAccessLog = true;

    /**
     * 响应超时时间（秒）
     */
    private Integer responseTimeoutSeconds = 60;

    /**
     * 重试次数
     */
    private Integer retryTimes = 3;

    /**
     * 重试间隔（毫秒）
     */
    private Integer retryIntervalMs = 1000;

    /**
     * 流式响应配置
     */
    private StreamConfig stream = new StreamConfig();

    /**
     * 安全配置
     */
    private SecurityConfig security = new SecurityConfig();

    /**
     * 线程池配置
     */
    private ThreadPoolConfig threadPool = new ThreadPoolConfig();

    @Data
    public static class StreamConfig {
        /**
         * 是否默认启用流式响应
         */
        private Boolean defaultEnabled = false;

        /**
         * 流式响应缓冲区大小
         */
        private Integer bufferSize = 1024;

        /**
         * 流式响应超时时间（秒）
         */
        private Integer timeoutSeconds = 120;
    }

    @Data
    public static class SecurityConfig {
        /**
         * 是否启用内容过滤
         */
        private Boolean enableContentFilter = true;

        /**
         * 是否启用敏感词检测
         */
        private Boolean enableSensitiveWordDetection = true;

        /**
         * 最大消息长度
         */
        private Integer maxMessageLength = 4000;

        /**
         * 是否记录敏感操作日志
         */
        private Boolean enableSensitiveOperationLog = true;
    }

    @Data
    public static class ThreadPoolConfig {
        /**
         * 核心线程数
         */
        private Integer corePoolSize = 5;

        /**
         * 最大线程数
         */
        private Integer maxPoolSize = 20;

        /**
         * 队列容量
         */
        private Integer queueCapacity = 100;

        /**
         * 线程空闲时间（秒）
         */
        private Integer keepAliveSeconds = 60;

        /**
         * 线程名前缀
         */
        private String threadNamePrefix = "ai-async-";
    }

    /**
     * AI异步处理线程池
     */
    @Bean("aiAsyncExecutor")
    public Executor aiAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPool.getCorePoolSize());
        executor.setMaxPoolSize(threadPool.getMaxPoolSize());
        executor.setQueueCapacity(threadPool.getQueueCapacity());
        executor.setKeepAliveSeconds(threadPool.getKeepAliveSeconds());
        executor.setThreadNamePrefix(threadPool.getThreadNamePrefix());
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

}