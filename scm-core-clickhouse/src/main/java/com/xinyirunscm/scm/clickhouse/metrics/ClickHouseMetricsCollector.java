package com.xinyirunscm.scm.clickhouse.metrics;

import com.clickhouse.client.api.insert.InsertResponse;
import com.clickhouse.client.api.metrics.ClientMetrics;
import com.clickhouse.client.api.metrics.OperationMetrics;
import com.clickhouse.client.api.metrics.ServerMetrics;
import com.clickhouse.client.api.query.QueryResponse;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * ClickHouse 性能指标收集器
 * 基于ClickHouse Java v2最佳实践，收集操作指标用于监控和性能分析
 * 
 * @author SCM System
 * @since 1.0.39
 */
@Component
@ConditionalOnProperty(prefix = "scm.clickhouse.monitoring", name = "metricsEnabled", havingValue = "true")
public class ClickHouseMetricsCollector {

    private static final Logger logger = LoggerFactory.getLogger(ClickHouseMetricsCollector.class);

    @Autowired(required = false)
    private MeterRegistry meterRegistry;

    /**
     * 收集查询操作指标
     */
    public void collectQueryMetrics(QueryResponse response, String operation) {
        try {
            OperationMetrics metrics = response.getMetrics();
            
            // 记录详细日志
            logger.info("ClickHouse查询指标 [{}]:", operation);
            logger.info("- 查询ID: {}", response.getQueryId());
            logger.info("- 读取行数: {}", response.getReadRows());
            logger.info("- 读取字节: {} KB", response.getReadBytes() / 1024);
            logger.info("- 写入行数: {}", response.getWrittenRows());
            logger.info("- 写入字节: {} KB", response.getWrittenBytes() / 1024);
            logger.info("- 结果行数: {}", response.getResultRows());
            logger.info("- 服务器时间: {} ms", TimeUnit.NANOSECONDS.toMillis(response.getServerTime()));
            logger.info("- 预计读取行数: {}", response.getTotalRowsToRead());
            
            // 发送到监控系统（如果配置了MeterRegistry）
            if (meterRegistry != null) {
                // 查询性能指标
                meterRegistry.gauge("clickhouse.query.read.rows", response.getReadRows());
                meterRegistry.gauge("clickhouse.query.read.bytes", response.getReadBytes());
                meterRegistry.gauge("clickhouse.query.result.rows", response.getResultRows());
                meterRegistry.gauge("clickhouse.query.server.time.ms", 
                                  TimeUnit.NANOSECONDS.toMillis(response.getServerTime()));
                
                // 记录查询时间
                Timer.Sample sample = Timer.start(meterRegistry);
                sample.stop(Timer.builder("clickhouse.query.duration")
                          .tag("operation", operation)
                          .register(meterRegistry));
                
                // 操作计数器
                meterRegistry.counter("clickhouse.query.count", "operation", operation).increment();
            }
            
        } catch (Exception e) {
            logger.warn("收集ClickHouse查询指标时出现警告", e);
        }
    }

    /**
     * 收集插入操作指标
     */
    public void collectInsertMetrics(InsertResponse response, String operation, int recordCount) {
        try {
            OperationMetrics metrics = response.getMetrics();
            
            // 记录详细日志
            logger.info("ClickHouse插入指标 [{}]:", operation);
            logger.info("- 查询ID: {}", response.getQueryId());
            logger.info("- 插入记录数: {}", recordCount);
            logger.info("- 写入行数: {}", response.getWrittenRows());
            logger.info("- 写入字节: {} KB", response.getWrittenBytes() / 1024);
            
            // 发送到监控系统
            if (meterRegistry != null) {
                // 插入性能指标
                meterRegistry.gauge("clickhouse.insert.records", recordCount);
                meterRegistry.gauge("clickhouse.insert.written.rows", response.getWrittenRows());
                meterRegistry.gauge("clickhouse.insert.written.bytes", response.getWrittenBytes());
                
                // 操作计数器
                meterRegistry.counter("clickhouse.insert.count", "operation", operation).increment();
                
                // 记录插入效率（行/秒）
                long serverTimeMs = TimeUnit.NANOSECONDS.toMillis(response.getServerTime());
                if (serverTimeMs > 0) {
                    double rowsPerSecond = (double) response.getWrittenRows() * 1000 / serverTimeMs;
                    meterRegistry.gauge("clickhouse.insert.rows.per.second", rowsPerSecond);
                }
            }
            
        } catch (Exception e) {
            logger.warn("收集ClickHouse插入指标时出现警告", e);
        }
    }

    /**
     * 收集流式查询指标
     */
    public void collectStreamMetrics(String operation, long processedCount, long durationMs) {
        try {
            logger.info("ClickHouse流式查询指标 [{}]:", operation);
            logger.info("- 处理记录数: {}", processedCount);
            logger.info("- 处理时间: {} ms", durationMs);
            
            if (processedCount > 0 && durationMs > 0) {
                double recordsPerSecond = (double) processedCount * 1000 / durationMs;
                logger.info("- 处理速度: {:.2f} 记录/秒", recordsPerSecond);
                
                // 发送到监控系统
                if (meterRegistry != null) {
                    meterRegistry.gauge("clickhouse.stream.processed.records", processedCount);
                    meterRegistry.gauge("clickhouse.stream.duration.ms", durationMs);
                    meterRegistry.gauge("clickhouse.stream.records.per.second", recordsPerSecond);
                    
                    meterRegistry.counter("clickhouse.stream.count", "operation", operation).increment();
                }
            }
            
        } catch (Exception e) {
            logger.warn("收集ClickHouse流式查询指标时出现警告", e);
        }
    }

    /**
     * 记录错误指标
     */
    public void collectErrorMetrics(String operation, String errorType, Exception error) {
        try {
            logger.error("ClickHouse操作错误 [{}] - {}: {}", operation, errorType, error.getMessage());
            
            if (meterRegistry != null) {
                meterRegistry.counter("clickhouse.error.count", 
                                    "operation", operation,
                                    "error_type", errorType).increment();
            }
            
        } catch (Exception e) {
            logger.warn("记录ClickHouse错误指标时出现警告", e);
        }
    }

    /**
     * 收集连接健康指标
     */
    public void collectHealthMetrics(boolean isHealthy, long pingTimeMs) {
        try {
            logger.debug("ClickHouse健康检查: {}, ping时间: {} ms", 
                        isHealthy ? "健康" : "异常", pingTimeMs);
            
            if (meterRegistry != null) {
                meterRegistry.gauge("clickhouse.health.status", isHealthy ? 1 : 0);
                meterRegistry.gauge("clickhouse.health.ping.time.ms", pingTimeMs);
                
                meterRegistry.counter("clickhouse.health.check.count").increment();
                
                if (!isHealthy) {
                    meterRegistry.counter("clickhouse.health.failure.count").increment();
                }
            }
            
        } catch (Exception e) {
            logger.warn("收集ClickHouse健康指标时出现警告", e);
        }
    }

    /**
     * 收集POJO序列化指标
     */
    public void collectPojoMetrics(String operation, int recordCount, long durationMs, boolean isPojo) {
        try {
            String method = isPojo ? "POJO序列化" : "手动JSON";
            logger.info("ClickHouse {} 指标 [{}]:", method, operation);
            logger.info("- 记录数: {}", recordCount);
            logger.info("- 执行时间: {} ms", durationMs);
            
            if (recordCount > 0 && durationMs > 0) {
                double recordsPerSecond = (double) recordCount * 1000 / durationMs;
                logger.info("- 处理速度: {:.2f} 记录/秒", recordsPerSecond);
            }
            
            if (meterRegistry != null) {
                String methodTag = isPojo ? "pojo" : "manual_json";
                
                meterRegistry.gauge("clickhouse.serialization.records", 
                                  recordCount);
                meterRegistry.gauge("clickhouse.serialization.duration.ms", 
                                  durationMs);
                meterRegistry.counter("clickhouse.serialization.count", 
                                    "operation", operation,
                                    "method", methodTag).increment();
                
                if (recordCount > 0 && durationMs > 0) {
                    double recordsPerSecond = (double) recordCount * 1000 / durationMs;
                    meterRegistry.gauge("clickhouse.serialization.records.per.second", 
                                      recordsPerSecond);
                }
            }
            
        } catch (Exception e) {
            logger.warn("收集ClickHouse序列化指标时出现警告", e);
        }
    }
}