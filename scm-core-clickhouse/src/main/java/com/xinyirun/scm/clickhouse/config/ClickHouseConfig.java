package com.xinyirun.scm.clickhouse.config;

import com.clickhouse.client.api.Client;
import com.xinyirun.scm.clickhouse.entity.SLogSysClickHouseEntity;
import com.xinyirun.scm.clickhouse.entity.mq.SLogMqConsumerClickHouseEntity;
import com.xinyirun.scm.clickhouse.entity.mq.SLogMqProducerClickHouseEntity;
import com.xinyirun.scm.clickhouse.entity.datachange.SLogDataChangeMainClickHouseEntity;
import com.xinyirun.scm.clickhouse.entity.datachange.SLogDataChangeOperateClickHouseEntity;
import com.xinyirun.scm.clickhouse.entity.datachange.SLogDataChangeDetailClickHouseEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ClickHouse Client V2 配置类
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "scm.clickhouse", name = "enabled", havingValue = "true")
public class ClickHouseConfig {


    private final ClickHouseProperties clickHouseProperties;

    public ClickHouseConfig(ClickHouseProperties clickHouseProperties) {
        this.clickHouseProperties = clickHouseProperties;
    }

    /**
     * 创建ClickHouse Client V2 实例
     */
    @Bean(name = "clickHouseClient")
    public Client clickHouseClient() {
        log.info("初始化ClickHouse Client V2，端点: {}", clickHouseProperties.getEndpoints());
        
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

            // 性能配置 - 基于ClickHouse Java v2最佳实践
            ClickHouseProperties.Performance performance = clickHouseProperties.getPerformance();
            clientBuilder.setMaxConnections(performance.getMaxConnections())
                    .setLZ4UncompressedBufferSize(performance.getLz4UncompressedBufferSize())
                    .setSocketRcvbuf(performance.getSocketReceiveBufferSize())
                    .setClientNetworkBufferSize(performance.getClientNetworkBufferSize());
            
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
            
            // 批量注册所有 POJO 实体类
            log.info("开始批量注册ClickHouse POJO实体类");
            registerAllPojoClasses(client);
            log.info("所有ClickHouse POJO实体类注册完成");
            
            // 连接预热 - 基于性能配置
            if (performance.isWarmupConnections()) {
                try {
                    log.info("开始预热ClickHouse连接池，超时时间: {}秒", performance.getWarmupTimeoutSeconds());
                    boolean pingResult = client.ping(performance.getWarmupTimeoutSeconds());
                    log.info("ClickHouse连接池预热完成，ping结果: {}", pingResult);
                } catch (Exception e) {
                    log.warn("ClickHouse连接预热失败，但不影响后续使用", e);
                }
            }
            
            log.info("ClickHouse Client V2 初始化成功，性能配置 - 最大连接数: {}, LZ4缓冲区: {}KB, 网络缓冲区: {}KB", 
                       performance.getMaxConnections(),
                       performance.getLz4UncompressedBufferSize() / 1024,
                       performance.getClientNetworkBufferSize() / 1024);
            return client;
            
        } catch (Exception e) {
            log.error("创建ClickHouse Client V2 失败", e);
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

    /**
     * 集中注册所有ClickHouse POJO实体类
     * 避免在各个Repository中重复注册逻辑
     */
    private void registerAllPojoClasses(Client client) {
        try {
            // 注册系统日志实体类 (s_log_sys表)
            client.register(SLogSysClickHouseEntity.class, client.getTableSchema("s_log_sys"));
            log.info("POJO注册成功: {} -> s_log_sys", SLogSysClickHouseEntity.class.getSimpleName());
            
            // 注册MQ消费者日志实体类 (s_log_mq_consumer表)
            client.register(SLogMqConsumerClickHouseEntity.class, client.getTableSchema("s_log_mq_consumer"));
            log.info("POJO注册成功: {} -> s_log_mq_consumer", SLogMqConsumerClickHouseEntity.class.getSimpleName());
            
            // 注册MQ生产者日志实体类 (s_log_mq_producer表)
            client.register(SLogMqProducerClickHouseEntity.class, client.getTableSchema("s_log_mq_producer"));
            log.info("POJO注册成功: {} -> s_log_mq_producer", SLogMqProducerClickHouseEntity.class.getSimpleName());
            
            // 注册数据变更主日志实体类 (s_log_data_change_main表)
            client.register(SLogDataChangeMainClickHouseEntity.class, client.getTableSchema("s_log_data_change_main"));
            log.info("POJO注册成功: {} -> s_log_data_change_main", SLogDataChangeMainClickHouseEntity.class.getSimpleName());
            
            // 注册数据变更操作日志实体类 (s_log_data_change_operate表)
            client.register(SLogDataChangeOperateClickHouseEntity.class, client.getTableSchema("s_log_data_change_operate"));
            log.info("POJO注册成功: {} -> s_log_data_change_operate", SLogDataChangeOperateClickHouseEntity.class.getSimpleName());
            
            // 注册数据变更详细日志实体类 (s_log_data_change_detail表)
            client.register(SLogDataChangeDetailClickHouseEntity.class, client.getTableSchema("s_log_data_change_detail"));
            log.info("POJO注册成功: {} -> s_log_data_change_detail", SLogDataChangeDetailClickHouseEntity.class.getSimpleName());
            
        } catch (Exception e) {
            log.error("ClickHouse POJO实体类批量注册失败", e);
            throw new RuntimeException("ClickHouse POJO注册失败，应用启动中止", e);
        }
    }
}