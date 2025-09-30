package com.xinyirun.scm.ai.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.ai.bean.entity.config.AiConfigEntity;
import com.xinyirun.scm.ai.bean.vo.config.AiConfigVo;
import com.xinyirun.scm.ai.mapper.config.AiConfigMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * AI配置服务
 *
 * 提供AI系统配置管理功能，包括配置的增删改查、缓存管理等
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiConfigService {

    /**
     * 配置缓存，提高查询性能
     */
    private final ConcurrentHashMap<String, String> configCache = new ConcurrentHashMap<>();

    @Resource
    private AiConfigMapper aiConfigMapper;

    /**
     * 根据配置键和租户获取配置值
     *
     * @param configKey 配置键
     * @param tenant 租户标识，可为null表示全局配置
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

            // 从数据库查询
            AiConfigEntity config = aiConfigMapper.selectByConfigKeyAndTenant(configKey);
            if (config != null) {
                String value = config.getConfig_value();
                configCache.put(cacheKey, value);
                return value;
            }

            // 如果数据库中没有，尝试获取默认配置值
            String configValue = getDefaultConfigValue(configKey, defaultValue);

            // 缓存结果
            if (configValue != null) {
                configCache.put(cacheKey, configValue);
            }

            return configValue != null ? configValue : defaultValue;
        } catch (Exception e) {
            log.error("获取配置失败，configKey: " + configKey + ", tenant: " + tenant, e);
            return defaultValue;
        }
    }

    /**
     * 根据配置键和租户获取配置值（无默认值版本）
     *
     * @param configKey 配置键
     * @param tenant 租户标识
     * @return 配置值
     */
    public String getConfigValue(String configKey, String tenant) {
        return getConfigValue(configKey, tenant, null);
    }

    /**
     * 根据ID查询配置
     *
     * @param id 配置ID
     * @return 配置VO
     */
    public AiConfigVo getById(Integer id) {
        try {
            AiConfigEntity entity = aiConfigMapper.selectById(id);
            if (entity != null) {
                return convertToVo(entity);
            }
            return null;
        } catch (Exception e) {
            log.error("根据ID查询配置失败, id: {}", id, e);
            return null;
        }
    }

    /**
     * 查询所有配置
     *
     * @param tenant 租户标识
     * @return 配置列表
     */
    public List<AiConfigVo> getAllConfigs(String tenant) {
        try {
            QueryWrapper<AiConfigEntity> wrapper = new QueryWrapper<>();
            if (StringUtils.hasText(tenant)) {
                wrapper.eq("tenant", tenant);
            }
            wrapper.orderByAsc("config_group", "sort_order");

            List<AiConfigEntity> entities = aiConfigMapper.selectList(wrapper);
            return entities.stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有配置失败, tenant: {}", tenant, e);
            return List.of();
        }
    }

    /**
     * 根据配置分组查询
     *
     * @param configGroup 配置分组
     * @param tenant 租户标识
     * @return 配置列表
     */
    public List<AiConfigVo> getByConfigGroup(String configGroup, String tenant) {
        try {
            QueryWrapper<AiConfigEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("config_group", configGroup);
            if (StringUtils.hasText(tenant)) {
                wrapper.eq("tenant", tenant);
            }
            wrapper.orderByAsc("sort_order");

            List<AiConfigEntity> entities = aiConfigMapper.selectList(wrapper);
            return entities.stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据配置分组查询失败, configGroup: {}, tenant: {}", configGroup, tenant, e);
            return List.of();
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
            log.error("配置值格式错误，configKey: " + configKey + ", value: " + value, e);
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
            log.error("配置值格式错误，configKey: " + configKey + ", value: " + value, e);
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
            log.error("配置值格式错误，configKey: " + configKey + ", value: " + value, e);
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
        log.info("AI配置缓存已清理");
    }

    /**
     * 清除指定配置的缓存
     */
    private void clearCache(String configKey, String tenant) {
        String cacheKey = buildCacheKey(configKey, tenant);
        configCache.remove(cacheKey);
        log.debug("清除配置缓存, cacheKey: {}", cacheKey);
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(String configKey, String tenant) {
        return configKey + ":" + (tenant != null ? tenant : "global");
    }

    /**
     * Entity转VO
     */
    private AiConfigVo convertToVo(AiConfigEntity entity) {
        AiConfigVo vo = new AiConfigVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    /**
     * VO转Entity
     */
    private AiConfigEntity convertToEntity(AiConfigVo vo) {
        AiConfigEntity entity = new AiConfigEntity();
        BeanUtils.copyProperties(vo, entity);
        return entity;
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