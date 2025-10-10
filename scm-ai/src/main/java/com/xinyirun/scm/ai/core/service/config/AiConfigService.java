package com.xinyirun.scm.ai.core.service.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinyirun.scm.ai.bean.entity.config.AiConfigEntity;
import com.xinyirun.scm.ai.core.mapper.config.AiConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * AI 配置服务
 * 从数据库 ai_config 表读取配置
 *
 * <p>简单设计：</p>
 * <ul>
 *   <li>根据 config_key 获取 config_value</li>
 *   <li>支持缓存提升性能</li>
 *   <li>敏感信息掩码日志输出</li>
 * </ul>
 *
 * @author SCM AI Team
 * @since 2025-10-10
 */
@Slf4j
@Service
public class AiConfigService {

    @Autowired
    private AiConfigMapper aiConfigMapper;

    /**
     * 根据配置键获取配置值
     *
     * @param configKey 配置键（使用 AiConstant 中的常量）
     * @return 配置值（如果不存在返回 null）
     */
    @Cacheable(value = "ai_config", key = "T(com.xinyirun.scm.common.utils.datasource.DataSourceHelper).getCurrentDataSourceName() + ':' + #configKey")
    public String getConfigValue(String configKey) {
        try {
            LambdaQueryWrapper<AiConfigEntity> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(AiConfigEntity::getConfigKey, configKey);

            AiConfigEntity config = aiConfigMapper.selectOne(queryWrapper);

            if (config != null && StringUtils.hasText(config.getConfigValue())) {
                // 日志掩码处理（API Key等敏感信息）
                if (configKey.contains("API_KEY") || configKey.contains("SECRET")) {
                    log.debug("读取AI配置: key={}, value={}***",
                        configKey, maskSensitiveValue(config.getConfigValue()));
                } else {
                    log.debug("读取AI配置: key={}, value={}", configKey, config.getConfigValue());
                }
                return config.getConfigValue();
            }

            log.warn("AI配置不存在: key={}", configKey);
            return null;

        } catch (Exception e) {
            log.error("读取AI配置失败: key={}, error={}", configKey, e.getMessage(), e);
            return null;
        }
    }

    /**
     * 根据配置键获取配置值（带默认值）
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值（如果不存在返回默认值）
     */
    public String getConfigValue(String configKey, String defaultValue) {
        String value = getConfigValue(configKey);
        return StringUtils.hasText(value) ? value : defaultValue;
    }

    /**
     * 根据配置键获取整数配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public Integer getIntValue(String configKey, Integer defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.hasText(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.warn("配置值无法转换为整数: key={}, value={}", configKey, value);
            }
        }
        return defaultValue;
    }

    /**
     * 根据配置键获取布尔配置值
     *
     * @param configKey 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public Boolean getBooleanValue(String configKey, Boolean defaultValue) {
        String value = getConfigValue(configKey);
        if (StringUtils.hasText(value)) {
            return Boolean.parseBoolean(value);
        }
        return defaultValue;
    }

    /**
     * 掩码敏感值（用于日志输出）
     *
     * @param value 原始值
     * @return 掩码后的值
     */
    private String maskSensitiveValue(String value) {
        if (!StringUtils.hasText(value) || value.length() < 12) {
            return "***";
        }
        return value.substring(0, 8) + "***" + value.substring(value.length() - 4);
    }
}
