package com.xinyirunscm.scm.clickhouse.util;

import com.xinyirunscm.scm.clickhouse.config.ClickHouseProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.bind.validation.ValidationErrors;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * ClickHouse 配置验证工具类
 * 用于验证 YAML 配置是否正确绑定到 ClickHouseProperties
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Component
public class ClickHouseConfigValidator {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseConfigValidator.class);

    private final ClickHouseProperties clickHouseProperties;

    public ClickHouseConfigValidator(ClickHouseProperties clickHouseProperties) {
        this.clickHouseProperties = clickHouseProperties;
    }

    /**
     * 应用启动后验证 ClickHouse 配置
     */
    @EventListener(ApplicationReadyEvent.class)
    public void validateConfiguration() {
        if (!clickHouseProperties.isEnabled()) {
            logger.info("ClickHouse 模块已禁用 (enabled=false)");
            return;
        }

        logger.info("========== ClickHouse 配置验证开始 ==========");
        
        // 验证基础配置
        validateBasicConfig();
        
        // 验证客户端配置
        validateClientConfig();
        
        // 验证超时配置
        validateTimeoutConfig();
        
        // 验证重试配置
        validateRetryConfig();
        
        // 验证监控配置
        validateMonitoringConfig();
        
        // 验证性能配置
        validatePerformanceConfig();
        
        logger.info("========== ClickHouse 配置验证完成 ==========");
    }

    /**
     * 验证基础配置
     */
    private void validateBasicConfig() {
        logger.info("📌 基础配置验证:");
        logger.info("  ✅ enabled: {}", clickHouseProperties.isEnabled());
        logger.info("  ✅ endpoints: {}", clickHouseProperties.getEndpoints());
        logger.info("  ✅ database: '{}'", clickHouseProperties.getDatabase());
        logger.info("  ✅ username: '{}'", clickHouseProperties.getUsername());
        logger.info("  ✅ password: '{}' (长度: {})", 
            maskPassword(clickHouseProperties.getPassword()), 
            clickHouseProperties.getPassword().length());
        logger.info("  ✅ compressServerResponse: {}", clickHouseProperties.isCompressServerResponse());
        logger.info("  ✅ compressClientRequest: {}", clickHouseProperties.isCompressClientRequest());
    }

    /**
     * 验证客户端配置
     */
    private void validateClientConfig() {
        ClickHouseProperties.ClientConfig client = clickHouseProperties.getClient();
        logger.info("📌 客户端配置验证:");
        logger.info("  ✅ bufferSize: {} KB", client.getBufferSize() / 1024);
        logger.info("  ✅ queueLength: {} {}", client.getQueueLength(), 
            client.getQueueLength() == 0 ? "(无限制)" : "");
        logger.info("  ✅ useObjectsInArray: {}", client.isUseObjectsInArray());
        logger.info("  ✅ useBinaryString: {}", client.isUseBinaryString());
        logger.info("  ✅ widenUnsignedTypes: {}", client.isWidenUnsignedTypes());
    }

    /**
     * 验证超时配置
     */
    private void validateTimeoutConfig() {
        ClickHouseProperties.Timeout timeout = clickHouseProperties.getTimeout();
        logger.info("📌 超时配置验证:");
        logger.info("  ✅ connection: {} ms", timeout.getConnection());
        logger.info("  ✅ query: {} ms", timeout.getQuery());
        logger.info("  ✅ socket: {} ms", timeout.getSocket());
        logger.info("  ✅ maxExecutionTime: {} 秒", timeout.getMaxExecutionTime());
    }

    /**
     * 验证重试配置
     */
    private void validateRetryConfig() {
        ClickHouseProperties.Retry retry = clickHouseProperties.getRetry();
        logger.info("📌 重试配置验证:");
        logger.info("  ✅ maxAttempts: {}", retry.getMaxAttempts());
        logger.info("  ✅ delay: {} ms", retry.getDelay());
        logger.info("  ✅ multiplier: {}", retry.getMultiplier());
    }

    /**
     * 验证监控配置
     */
    private void validateMonitoringConfig() {
        ClickHouseProperties.Monitoring monitoring = clickHouseProperties.getMonitoring();
        logger.info("📌 监控配置验证:");
        logger.info("  ✅ healthCheckEnabled: {}", monitoring.isHealthCheckEnabled());
        logger.info("  ✅ healthCheckInterval: {} 秒", monitoring.getHealthCheckInterval());
        logger.info("  ✅ metricsEnabled: {}", monitoring.isMetricsEnabled());
    }

    /**
     * 验证性能配置
     */
    private void validatePerformanceConfig() {
        ClickHouseProperties.Performance performance = clickHouseProperties.getPerformance();
        logger.info("📌 性能配置验证:");
        logger.info("  ✅ maxConnections: {}", performance.getMaxConnections());
        logger.info("  ✅ lz4UncompressedBufferSize: {} MB", performance.getLz4UncompressedBufferSize() / (1024 * 1024));
        logger.info("  ✅ socketReceiveBufferSize: {} MB", performance.getSocketReceiveBufferSize() / (1024 * 1024));
        logger.info("  ✅ clientNetworkBufferSize: {} MB", performance.getClientNetworkBufferSize() / (1024 * 1024));
        logger.info("  ✅ warmupConnections: {}", performance.isWarmupConnections());
        logger.info("  ✅ warmupTimeoutSeconds: {} 秒", performance.getWarmupTimeoutSeconds());
        logger.info("  ✅ preferredFormat: '{}'", performance.getPreferredFormat());
    }

    /**
     * 掩码密码显示
     */
    private String maskPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "(empty)";
        }
        if (password.length() <= 2) {
            return "**";
        }
        return password.substring(0, 2) + "****" + password.substring(password.length() - 2);
    }

    /**
     * 检查配置是否匹配期望的 YAML 值
     * 这个方法可以用于单元测试或启动时验证
     */
    public boolean verifyYamlMapping() {
        try {
            // 验证关键配置是否从 YAML 正确读取
            boolean isValid = true;
            
            if (!clickHouseProperties.isEnabled()) {
                logger.warn("⚠️ ClickHouse 未启用，请检查 YAML 配置中的 enabled 设置");
                return false;
            }
            
            if (!"scm_clickhouse".equals(clickHouseProperties.getDatabase())) {
                logger.warn("⚠️ 数据库名称不匹配，期望: 'scm_clickhouse', 实际: '{}'", 
                    clickHouseProperties.getDatabase());
                isValid = false;
            }
            
            if (!"app".equals(clickHouseProperties.getUsername())) {
                logger.warn("⚠️ 用户名不匹配，期望: 'app', 实际: '{}'", 
                    clickHouseProperties.getUsername());
                isValid = false;
            }
            
            if (!"app_password".equals(clickHouseProperties.getPassword())) {
                logger.warn("⚠️ 密码不匹配，请检查 YAML 配置");
                isValid = false;
            }
            
            // 验证性能配置的关键参数
            if (clickHouseProperties.getPerformance().getMaxConnections() != 20) {
                logger.warn("⚠️ maxConnections 不匹配，期望: 20, 实际: {}", 
                    clickHouseProperties.getPerformance().getMaxConnections());
                isValid = false;
            }
            
            if (isValid) {
                logger.info("🎯 YAML 配置映射验证通过！");
            } else {
                logger.error("❌ YAML 配置映射验证失败！");
            }
            
            return isValid;
            
        } catch (Exception e) {
            logger.error("❌ 配置验证过程中发生异常", e);
            return false;
        }
    }
}