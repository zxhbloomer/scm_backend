package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.xinyirun.scm.ai.common.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentHashMap;

/**
 * AI配置服务
 * 提供AI系统配置管理和Token价格计算功能
 *
 * @author zxh
 * @createTime 2025-09-25
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiConfigService {

    /**
     * 配置缓存，提高查询性能
     */
    private final ConcurrentHashMap<String, String> configCache = new ConcurrentHashMap<>();


    // TODO: 注入AiConfigMapper
    // @Resource
    // private AiConfigMapper aiConfigMapper;

    /**
     * 获取配置值
     *
     * @param configKey 配置键
     * @param tenant 租户ID，可为null表示全局配置
     * @param defaultValue 默认值
     * @return 配置值
     */
    public String getConfigValue(String configKey, String tenant, String defaultValue) {
        try {
            String cacheKey = buildCacheKey(configKey, tenant);

            // 先从缓存获取
            String cachedValue = configCache.get(cacheKey);
            if (cachedValue != null) {
                return cachedValue;
            }

            // TODO: 从数据库查询
            // AiConfigExample example = new AiConfigExample();
            // example.createCriteria()
            //     .andConfigKeyEqualTo(configKey)
            //     .andTenantEqualTo(tenant);
            // List<AiConfig> configs = aiConfigMapper.selectByExample(example);

            // 模拟数据库查询结果
            String configValue = getDefaultConfigValue(configKey, defaultValue);

            // 缓存结果
            configCache.put(cacheKey, configValue);
            return configValue;

        } catch (Exception e) {
            LogUtils.error("获取配置失败，configKey: " + configKey + ", tenant: " + tenant, e);
            return defaultValue;
        }
    }

    /**
     * 获取布尔配置值
     */
    public Boolean getBooleanConfig(String configKey, String tenant, Boolean defaultValue) {
        String value = getConfigValue(configKey, tenant, defaultValue != null ? defaultValue.toString() : "false");
        return Boolean.valueOf(value);
    }

    /**
     * 获取布尔配置值（无租户版本）
     */
    public Boolean getBooleanConfig(String configKey, Boolean defaultValue) {
        return getBooleanConfig(configKey, null, defaultValue);
    }

    /**
     * 获取整数配置值
     */
    public Integer getIntegerConfig(String configKey, String tenant, Integer defaultValue) {
        String value = getConfigValue(configKey, tenant, defaultValue != null ? defaultValue.toString() : "0");
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            LogUtils.error("配置值格式错误，configKey: " + configKey + ", value: " + value, e);
            return defaultValue;
        }
    }

    /**
     * 获取整数配置值（无租户版本）
     */
    public Integer getIntegerConfig(String configKey, Integer defaultValue) {
        return getIntegerConfig(configKey, null, defaultValue);
    }

    /**
     * 获取长整型配置值
     */
    public Long getLongConfig(String configKey, String tenant, Long defaultValue) {
        String value = getConfigValue(configKey, tenant, defaultValue != null ? defaultValue.toString() : "0");
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            LogUtils.error("配置值格式错误，configKey: " + configKey + ", value: " + value, e);
            return defaultValue;
        }
    }

    /**
     * 获取BigDecimal配置值
     */
    public BigDecimal getDecimalConfig(String configKey, String tenant, BigDecimal defaultValue) {
        String value = getConfigValue(configKey, tenant, defaultValue != null ? defaultValue.toString() : "0.0");
        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            LogUtils.error("配置值格式错误，configKey: " + configKey + ", value: " + value, e);
            return defaultValue;
        }
    }



    /**
     * 是否启用Token统计功能
     */
    public Boolean isTokenStatisticsEnabled() {
        return getBooleanConfig("TOKEN_STATISTICS_ENABLED", null, true);
    }


    /**
     * 是否启用Token配额检查
     */
    public Boolean isTokenQuotaCheckEnabled() {
        return getBooleanConfig("TOKEN_QUOTA_CHECK_ENABLED", null, true);
    }

    /**
     * 获取默认日Token限额
     */
    public Long getDefaultDailyTokenLimit() {
        return getLongConfig("DEFAULT_DAILY_TOKEN_LIMIT", null, 10000L);
    }

    /**
     * 获取默认月Token限额
     */
    public Long getDefaultMonthlyTokenLimit() {
        return getLongConfig("DEFAULT_MONTHLY_TOKEN_LIMIT", null, 300000L);
    }



    /**
     * 清理配置缓存
     */
    public void clearConfigCache() {
        configCache.clear();
        LogUtils.info("AI配置缓存已清理");
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(String configKey, String tenant) {
        return tenant != null ? configKey + ":" + tenant : configKey;
    }

    /**
     * 获取默认配置值（模拟数据库查询）
     */
    private String getDefaultConfigValue(String configKey, String defaultValue) {
        // 模拟一些默认配置
        switch (configKey) {
            case "TOKEN_STATISTICS_ENABLED":
                return "true";
            case "TOKEN_QUOTA_CHECK_ENABLED":
                return "true";
            case "DEFAULT_DAILY_TOKEN_LIMIT":
                return "10000";
            case "DEFAULT_MONTHLY_TOKEN_LIMIT":
                return "300000";
            default:
                return defaultValue;
        }
    }
}