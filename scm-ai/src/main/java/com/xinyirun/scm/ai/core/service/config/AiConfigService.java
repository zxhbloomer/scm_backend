package com.xinyirun.scm.ai.core.service.config;

import com.xinyirun.scm.ai.bean.entity.config.AiConfigEntity;
import com.xinyirun.scm.ai.bean.vo.config.DefaultModelsVo;
import com.xinyirun.scm.ai.core.mapper.config.AiConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * AI 配置服务
 * 从数据库 ai_config 表读取配置
 *
 * <p>简单设计:</p>
 * <ul>
 *   <li>根据 config_key 获取 config_value</li>
 *   <li>支持缓存提升性能</li>
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
    public String getConfigValue(String configKey) {
        try {
            AiConfigEntity config = aiConfigMapper.selectByConfigKey(configKey);

            if (config != null && StringUtils.hasText(config.getConfigValue())) {
                return config.getConfigValue();
            }

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
     * 设置配置值
     *
     * @param configKey 配置键
     * @param configValue 配置值
     * @param description 配置描述
     */
    @Transactional(rollbackFor = Exception.class)
    public void setConfigValue(String configKey, String configValue, String description) {
        try {
            AiConfigEntity config = aiConfigMapper.selectByConfigKey(configKey);

            if (config != null) {
                // 更新现有配置
                config.setConfigValue(configValue);
                if (StringUtils.hasText(description)) {
                    config.setDescription(description);
                }
                aiConfigMapper.updateById(config);
            } else {
                // 插入新配置
                config = new AiConfigEntity();
                config.setConfigKey(configKey);
                config.setConfigValue(configValue);
                config.setDescription(description);
                aiConfigMapper.insert(config);
            }
        } catch (Exception e) {
            log.error("设置AI配置失败: key={}, error={}", configKey, e.getMessage(), e);
            throw new RuntimeException("设置AI配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取默认模型配置（只返回默认选中的模型ID）
     *
     * @return 默认模型ID的VO对象
     */
    public DefaultModelsVo getDefaultModels() {
        DefaultModelsVo result = new DefaultModelsVo();

        // 查询已设置的默认模型ID
        List<AiConfigEntity> configs = aiConfigMapper.selectDefaultModels();
        for (AiConfigEntity config : configs) {
            String key = config.getConfigKey();
            String value = config.getConfigValue();

            if (!StringUtils.hasText(value)) {
                continue;
            }

            try {
                Long modelId = Long.parseLong(value);
                switch (key) {
                    case "DEFAULT_LLM_MODEL_ID":
                        result.setDefaultLlm(modelId);
                        break;
                    case "DEFAULT_VISION_MODEL_ID":
                        result.setDefaultVision(modelId);
                        break;
                    case "DEFAULT_EMBEDDING_MODEL_ID":
                        result.setDefaultEmbedding(modelId);
                        break;
                    default:
                        log.warn("未知的默认模型配置键: {}", key);
                }
            } catch (NumberFormatException e) {
                log.warn("默认模型ID格式错误: key={}, value={}", key, value);
            }
        }

        return result;
    }

    /**
     * 设置默认模型
     *
     * @param modelType 模型类型：LLM/VISION/EMBEDDING
     * @param modelId 模型ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void setDefaultModel(String modelType, Long modelId) {
        String configKey;
        String description;

        switch (modelType.toUpperCase()) {
            case "LLM":
                configKey = "DEFAULT_LLM_MODEL_ID";
                description = "默认语言模型ID";
                break;
            case "VISION":
                configKey = "DEFAULT_VISION_MODEL_ID";
                description = "默认视觉模型ID";
                break;
            case "EMBEDDING":
                configKey = "DEFAULT_EMBEDDING_MODEL_ID";
                description = "默认嵌入模型ID";
                break;
            default:
                throw new IllegalArgumentException("不支持的模型类型: " + modelType);
        }

        String configValue = modelId != null ? modelId.toString() : null;
        setConfigValue(configKey, configValue, description);
    }
}
