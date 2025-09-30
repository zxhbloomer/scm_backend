package com.xinyirun.scm.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import com.xinyirun.scm.ai.core.mapper.model.AiModelSourceMapper;
import com.xinyirun.scm.ai.core.service.chat.AiModelSelectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AI模型选择服务实现类
 *
 * 实现动态AI模型选择的核心业务逻辑
 * 根据AI类型从数据库中自动选择最合适的模型
 *
 * @author SCM-AI重构团队
 * @since 2025-09-30
 */
@Slf4j
@Service
public class AiModelSelectionServiceImpl implements AiModelSelectionService {

    @Autowired
    private AiModelSourceMapper aiModelSourceMapper;

    @Override
    public AiModelSourceEntity selectAvailableModel(String aiType) {
        log.debug("开始选择AI模型，类型: {}", aiType);

        // 构建查询条件：type = aiType AND status = true
        // 排序：is_default DESC, c_time DESC
        LambdaQueryWrapper<AiModelSourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiModelSourceEntity::getType, aiType)
                .eq(AiModelSourceEntity::getStatus, true)
                .orderByDesc(AiModelSourceEntity::getIs_default)
                .orderByDesc(AiModelSourceEntity::getC_time)
                .last("LIMIT 1");

        AiModelSourceEntity selectedModel = aiModelSourceMapper.selectOne(queryWrapper);

        if (selectedModel == null) {
            String errorMsg = String.format("没有找到可用的%s类型AI模型", aiType);
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        log.info("成功选择AI模型: [类型: {}, 提供商: {}, 模型: {}, ID: {}]",
                selectedModel.getType(),
                selectedModel.getProvider_name(),
                selectedModel.getBase_name(),
                selectedModel.getId());

        return selectedModel;
    }

    @Override
    public boolean hasAvailableModel(String aiType) {
        log.debug("检查是否有可用模型，类型: {}", aiType);

        LambdaQueryWrapper<AiModelSourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiModelSourceEntity::getType, aiType)
                .eq(AiModelSourceEntity::getStatus, true);

        long count = aiModelSourceMapper.selectCount(queryWrapper);
        boolean hasModel = count > 0;

        log.debug("类型{}的可用模型数量: {}", aiType, count);
        return hasModel;
    }

    @Override
    public AiModelSourceEntity getDefaultModel(String aiType) {
        log.debug("获取默认模型，类型: {}", aiType);

        LambdaQueryWrapper<AiModelSourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiModelSourceEntity::getType, aiType)
                .eq(AiModelSourceEntity::getStatus, true)
                .eq(AiModelSourceEntity::getIs_default, 1)
                .orderByDesc(AiModelSourceEntity::getC_time)
                .last("LIMIT 1");

        AiModelSourceEntity defaultModel = aiModelSourceMapper.selectOne(queryWrapper);

        if (defaultModel != null) {
            log.debug("找到默认模型: [提供商: {}, 模型: {}]",
                    defaultModel.getProvider_name(), defaultModel.getBase_name());
        } else {
            log.debug("类型{}没有配置默认模型", aiType);
        }

        return defaultModel;
    }

    @Override
    public long countAvailableModels(String aiType) {
        log.debug("统计可用模型数量，类型: {}", aiType);

        LambdaQueryWrapper<AiModelSourceEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AiModelSourceEntity::getType, aiType)
                .eq(AiModelSourceEntity::getStatus, true);

        long count = aiModelSourceMapper.selectCount(queryWrapper);
        log.debug("类型{}的可用模型总数: {}", aiType, count);

        return count;
    }
}