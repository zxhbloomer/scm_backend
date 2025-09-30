package com.xinyirun.scm.ai.core.service.chat;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import com.xinyirun.scm.ai.core.mapper.model.AiModelSourceMapper;
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
public class AiModelSelectionService  {

    @Autowired
    private AiModelSourceMapper aiModelSourceMapper;

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

}