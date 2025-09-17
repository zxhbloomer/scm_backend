package com.xinyirunscm.scm.clickhouse.config;

import com.clickhouse.client.api.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClickHouse Client V2 配置类
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Configuration
@ConditionalOnProperty(prefix = "scm.clickhouse", name = "enabled", havingValue = "true")
public class ClickHouseConfig {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseConfig.class);

    private final ClickHouseProperties clickHouseProperties;

    public ClickHouseConfig(ClickHouseProperties clickHouseProperties) {
        this.clickHouseProperties = clickHouseProperties;
    }

    /**
     * 创建ClickHouse Client V2 实例
     */
    @Bean(name = "clickHouseClient")
    public Client clickHouseClient() {
        logger.info("初始化ClickHouse Client V2，端点: {}", clickHouseProperties.getEndpoints());
        
        try {
            // 使用Client V2 Builder模式创建客户端
            Client.Builder clientBuilder = new Client.Builder();
            
            // 添加所有端点
            for (String endpoint : clickHouseProperties.getEndpoints()) {
                clientBuilder.addEndpoint(endpoint);
            }
            
            // 基本认证配置
            clientBuilder.setUsername(clickHouseProperties.getUsername())
                    .setPassword(clickHouseProperties.getPassword())
                    .setDefaultDatabase(clickHouseProperties.getDatabase());
            
            // 压缩配置
            clientBuilder.compressServerResponse(clickHouseProperties.isCompressServerResponse())
                    .compressClientRequest(clickHouseProperties.isCompressClientRequest());
            
            // 客户端配置
            ClickHouseProperties.ClientConfig clientConfig = clickHouseProperties.getClient();
            clientBuilder.setOption("buffer_size", String.valueOf(clientConfig.getBufferSize()))
                    .setOption("queue_length", String.valueOf(clientConfig.getQueueLength()))
                    .setOption("use_objects_in_array", String.valueOf(clientConfig.isUseObjectsInArray()))
                    .setOption("use_binary_string", String.valueOf(clientConfig.isUseBinaryString()))
                    .setOption("widen_unsigned_types", String.valueOf(clientConfig.isWidenUnsignedTypes()));
            
            // 超时配置
            ClickHouseProperties.Timeout timeout = clickHouseProperties.getTimeout();
            clientBuilder.setOption("connection_timeout", String.valueOf(timeout.getConnection()))
                    .setOption("socket_timeout", String.valueOf(timeout.getSocket()))
                    .setOption("max_execution_time", String.valueOf(timeout.getMaxExecutionTime()));
            
            // 重试配置
            ClickHouseProperties.Retry retry = clickHouseProperties.getRetry();
            clientBuilder.setOption("max_attempts", String.valueOf(retry.getMaxAttempts()))
                    .setOption("retry_delay", String.valueOf(retry.getDelay()))
                    .setOption("retry_multiplier", String.valueOf(retry.getMultiplier()));
            
            Client client = clientBuilder.build();
            
            logger.info("ClickHouse Client V2 初始化成功");
            return client;
            
        } catch (Exception e) {
            logger.error("创建ClickHouse Client V2 失败", e);
            throw new RuntimeException("ClickHouse Client V2 初始化失败", e);
        }
    }

    /**
     * 健康检查Bean
     */
    @Bean(name = "clickHouseHealthChecker")
    public ClickHouseHealthChecker clickHouseHealthChecker(Client clickHouseClient) {
        ClickHouseProperties.Monitoring monitoring = clickHouseProperties.getMonitoring();
        return new ClickHouseHealthChecker(
                clickHouseClient,
                monitoring.isHealthCheckEnabled(),
                monitoring.getHealthCheckInterval(),
                monitoring.isMetricsEnabled()
        );
    }
}