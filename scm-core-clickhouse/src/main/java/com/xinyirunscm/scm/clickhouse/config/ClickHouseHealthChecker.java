package com.xinyirunscm.scm.clickhouse.config;

import com.clickhouse.client.api.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * ClickHouse Client V2 健康检查器
 * 
 * @author SCM System
 * @since 1.0.39
 */
public class ClickHouseHealthChecker {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseHealthChecker.class);

    private final Client clickHouseClient;
    private final boolean healthCheckEnabled;
    private final int healthCheckInterval; // 秒
    private final boolean metricsEnabled;
    
    private final AtomicBoolean isHealthy = new AtomicBoolean(true);
    private final AtomicLong lastCheckTime = new AtomicLong(System.currentTimeMillis());
    private final AtomicLong successCount = new AtomicLong(0);
    private final AtomicLong failureCount = new AtomicLong(0);

    public ClickHouseHealthChecker(Client clickHouseClient, boolean healthCheckEnabled, 
                                 int healthCheckInterval, boolean metricsEnabled) {
        this.clickHouseClient = clickHouseClient;
        this.healthCheckEnabled = healthCheckEnabled;
        this.healthCheckInterval = healthCheckInterval;
        this.metricsEnabled = metricsEnabled;
    }

    /**
     * 定期健康检查
     */
    @Scheduled(fixedDelayString = "#{@clickHouseHealthChecker.healthCheckInterval * 1000}")
    public void performHealthCheck() {
        if (!healthCheckEnabled) {
            return;
        }

        try {
            // 执行简单的ping查询
            boolean pingResult = clickHouseClient.ping();
            
            if (pingResult) {
                isHealthy.set(true);
                successCount.incrementAndGet();
                if (metricsEnabled) {
                    logger.debug("ClickHouse 健康检查成功");
                }
            } else {
                isHealthy.set(false);
                failureCount.incrementAndGet();
                logger.warn("ClickHouse ping 失败");
            }
            
            lastCheckTime.set(System.currentTimeMillis());
            
        } catch (Exception e) {
            isHealthy.set(false);
            failureCount.incrementAndGet();
            lastCheckTime.set(System.currentTimeMillis());
            logger.error("ClickHouse 健康检查异常", e);
        }
    }

    /**
     * 手动健康检查
     */
    public boolean checkHealth() {
        try {
            return clickHouseClient.ping();
        } catch (Exception e) {
            logger.error("手动健康检查失败", e);
            return false;
        }
    }

    /**
     * 获取健康状态
     */
    public boolean isHealthy() {
        return isHealthy.get();
    }

    /**
     * 获取最后检查时间
     */
    public long getLastCheckTime() {
        return lastCheckTime.get();
    }

    /**
     * 获取成功次数
     */
    public long getSuccessCount() {
        return successCount.get();
    }

    /**
     * 获取失败次数
     */
    public long getFailureCount() {
        return failureCount.get();
    }

    /**
     * 获取健康检查间隔
     */
    public int getHealthCheckInterval() {
        return healthCheckInterval;
    }

    /**
     * 是否启用健康检查
     */
    public boolean isHealthCheckEnabled() {
        return healthCheckEnabled;
    }

    /**
     * 是否启用指标收集
     */
    public boolean isMetricsEnabled() {
        return metricsEnabled;
    }

    /**
     * 重置计数器
     */
    public void resetCounters() {
        successCount.set(0);
        failureCount.set(0);
        logger.info("ClickHouse 健康检查计数器已重置");
    }
}