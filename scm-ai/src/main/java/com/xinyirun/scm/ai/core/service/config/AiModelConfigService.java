package com.xinyirun.scm.ai.core.service.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.ai.bean.entity.config.AiModelConfigEntity;
import com.xinyirun.scm.ai.bean.vo.config.AiModelConfigVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import com.xinyirun.scm.ai.core.mapper.config.AiModelConfigMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * AI模型配置服务类
 */
@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class AiModelConfigService {

    @Resource
    private AiModelConfigMapper aiModelConfigMapper;

    /**
     * 编辑模型配置
     *
     * @param aiModelConfigVo 模型配置VO
     * @param userId 用户ID（暂时未使用，保留接口兼容性）
     * @return 模型配置VO
     */
    public AiModelConfigVo editModelConfig(AiModelConfigVo aiModelConfigVo, String userId) {
        // 使用条件表达式确定ID并设置操作标志
        Long id = aiModelConfigVo.getId();
        boolean isAddOperation = (id == null);

        // 校验模型名称唯一性
        if (isModelNameDuplicated(aiModelConfigVo.getModelName(), id, isAddOperation)) {
            throw new RuntimeException("模型名称已存在: " + aiModelConfigVo.getModelName());
        }

        // 创建并填充模型对象
        AiModelConfigEntity aiModelConfig;
        if (isAddOperation) {
            // 新增操作：创建新对象
            aiModelConfig = new AiModelConfigEntity();
        } else {
            // 更新操作：先查询原有数据，避免覆盖未传入的字段
            aiModelConfig = aiModelConfigMapper.selectById(id);
            if (aiModelConfig == null) {
                throw new RuntimeException("模型信息不存在");
            }
        }

        buildModelConfig(aiModelConfigVo, aiModelConfig);

        // 根据操作类型执行不同逻辑
        if (isAddOperation) {
            aiModelConfigMapper.insert(aiModelConfig);
            log.info("新增模型配置成功, id: {}, name: {}", aiModelConfig.getId(), aiModelConfig.getModelName());
        } else {
            aiModelConfigMapper.updateById(aiModelConfig);
            log.info("更新模型配置成功, id: {}, name: {}", aiModelConfig.getId(), aiModelConfig.getModelName());
        }

        // 转换Entity为VO返回
        return getModelConfigVo(aiModelConfig);
    }

    /**
     * 验证模型名称是否重复
     */
    private boolean isModelNameDuplicated(String name, Long id, boolean isAddOperation) {
        QueryWrapper<AiModelConfigEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("model_name", name);

        // 更新操作时排除当前记录
        if (!isAddOperation) {
            wrapper.ne("id", id);
        }

        return aiModelConfigMapper.selectCount(wrapper) > 0;
    }

    /**
     * 构建模型配置对象
     */
    private void buildModelConfig(AiModelConfigVo aiModelConfigVo, AiModelConfigEntity aiModelConfig) {
        // 更新时只设置非null字段，避免覆盖原有数据
        if (aiModelConfigVo.getModelName() != null) {
            aiModelConfig.setModelName(aiModelConfigVo.getModelName());
        }
        if (aiModelConfigVo.getModelType() != null) {
            aiModelConfig.setModelType(aiModelConfigVo.getModelType());
        }
        if (aiModelConfigVo.getProvider() != null) {
            aiModelConfig.setProvider(aiModelConfigVo.getProvider());
        }
        if (aiModelConfigVo.getApiKey() != null) {
            aiModelConfig.setApiKey(aiModelConfigVo.getApiKey());
        }
        if (aiModelConfigVo.getBaseUrl() != null) {
            aiModelConfig.setBaseUrl(aiModelConfigVo.getBaseUrl());
        }
        if (aiModelConfigVo.getDeploymentName() != null) {
            aiModelConfig.setDeploymentName(aiModelConfigVo.getDeploymentName());
        }
        if (aiModelConfigVo.getEnabled() != null) {
            aiModelConfig.setEnabled(aiModelConfigVo.getEnabled());
        }

        // 校验并设置高级参数（带默认值和范围校验）
        validateAndSetAdvancedSettings(aiModelConfigVo, aiModelConfig);
    }

    /**
     * 校验并设置高级参数默认值
     */
    private void validateAndSetAdvancedSettings(AiModelConfigVo vo, AiModelConfigEntity entity) {
        // Temperature校验：0.0-2.0，默认0.7
        if (vo.getTemperature() != null) {
            BigDecimal temperature = vo.getTemperature();
            if (temperature.compareTo(BigDecimal.ZERO) < 0 || temperature.compareTo(new BigDecimal("2.0")) > 0) {
                throw new RuntimeException("Temperature必须在0.0-2.0之间");
            }
            entity.setTemperature(temperature);
        } else if (entity.getTemperature() == null) {
            // 只有新增时才设置默认值
            entity.setTemperature(new BigDecimal("0.7"));
        }

        // Top P校验：0.0-1.0，默认1.0
        if (vo.getTopP() != null) {
            BigDecimal topP = vo.getTopP();
            if (topP.compareTo(BigDecimal.ZERO) < 0 || topP.compareTo(BigDecimal.ONE) > 0) {
                throw new RuntimeException("Top P必须在0.0-1.0之间");
            }
            entity.setTopP(topP);
        } else if (entity.getTopP() == null) {
            entity.setTopP(BigDecimal.ONE);
        }

        // Max Tokens校验：>0，默认1024
        if (vo.getMaxTokens() != null) {
            if (vo.getMaxTokens() <= 0) {
                throw new RuntimeException("Max Tokens必须大于0");
            }
            entity.setMaxTokens(vo.getMaxTokens());
        } else if (entity.getMaxTokens() == null) {
            entity.setMaxTokens(1024);
        }

        // Timeout校验：>0，默认60秒
        if (vo.getTimeout() != null) {
            if (vo.getTimeout() <= 0) {
                throw new RuntimeException("Timeout必须大于0");
            }
            entity.setTimeout(vo.getTimeout());
        } else if (entity.getTimeout() == null) {
            entity.setTimeout(60);
        }
    }

    /**
     * 删除模型信息
     */
    public void deleteModelConfig(Long id, String userId) {
        AiModelConfigEntity entity = aiModelConfigMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("模型信息不存在");
        }

        aiModelConfigMapper.deleteById(id);
        log.info("删除模型配置成功, id: {}", id);
    }

    /**
     * 获取模型配置列表
     * 支持按提供商、关键词等条件筛选
     */
    public List<AiModelConfigVo> getModelConfigList(AiModelSourceRequestVo request) {
        // 使用SQL左连接查询,一次性获取用户姓名,避免N+1查询问题
        List<AiModelConfigVo> resultList = aiModelConfigMapper.selectListWithUserName(
            request.getKeyword(),
            request.getProviderName()
        );

        // API密钥脱敏
        resultList.forEach(vo -> vo.setApiKey(maskSkString(vo.getApiKey())));

        return resultList;
    }

    /**
     * 根据ID获取模型配置VO
     */
    public AiModelConfigVo getModelConfigVo(Long id, String userId) {
        // 使用SQL左连接查询,一次性获取用户姓名
        AiModelConfigVo vo = aiModelConfigMapper.selectByIdWithUserName(id);
        if (vo == null) {
            throw new RuntimeException("模型信息不存在");
        }

        // API密钥脱敏
        vo.setApiKey(maskSkString(vo.getApiKey()));

        return vo;
    }

    /**
     * 内部方法:将Entity转换为VO(用于新增/更新后返回)
     */
    private AiModelConfigVo getModelConfigVo(AiModelConfigEntity entity) {
        AiModelConfigVo vo = new AiModelConfigVo();
        BeanUtils.copyProperties(entity, vo);

        // API密钥脱敏
        vo.setApiKey(maskSkString(entity.getApiKey()));

        return vo;
    }

    /**
     * 获取可用的语言模型列表
     * @return 语言模型列表
     */
    public List<AiModelConfigVo> getAvailableLlmModels() {
        return aiModelConfigMapper.selectAvailableLlmModels();
    }

    /**
     * 获取可用的视觉模型列表
     * @return 视觉模型列表
     */
    public List<AiModelConfigVo> getAvailableVisionModels() {
        return aiModelConfigMapper.selectAvailableVisionModels();
    }

    /**
     * 获取可用的嵌入模型列表
     * @return 嵌入模型列表
     */
    public List<AiModelConfigVo> getAvailableEmbeddingModels() {
        return aiModelConfigMapper.selectAvailableEmbeddingModels();
    }

    /**
     * 将API Key字符串进行掩码处理
     */
    public static String maskSkString(String input) {
        if (!StringUtils.isNotBlank(input)) {
            return input;
        }
        // 如果输入为空或长度小于等于6，直接返回原字符串
        if (input.length() <= 6) {
            return input;
        }
        // 提取前缀和后缀
        String prefix = input.substring(0, 4); // sk-AB
        String suffix = input.substring(input.length() - 2); // 最后两个字符
        return prefix + "**** " + suffix;
    }
}
