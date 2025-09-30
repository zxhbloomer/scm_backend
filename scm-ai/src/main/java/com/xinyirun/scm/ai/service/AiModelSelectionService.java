package com.xinyirun.scm.ai.service;

import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;

/**
 * AI模型选择服务接口
 *
 * 负责根据AI类型自动选择合适的AI模型
 * 核心业务逻辑：从数据库中选择状态为启用的模型，优先选择默认模型，然后按创建时间排序
 *
 * @author SCM-AI重构团队
 * @since 2025-09-30
 */
public interface AiModelSelectionService {

    /**
     * 根据AI类型自动选择可用的AI模型
     *
     * 选择逻辑：
     * 1. 查询指定类型且状态为启用(status=true)的模型
     * 2. 按is_default DESC, create_time DESC排序
     * 3. 返回第一条记录
     *
     * @param aiType AI类型 (LLM, VISION, AUDIO)
     * @return 选中的AI模型实体，如果没有可用模型则返回null
     * @throws RuntimeException 当没有可用模型时抛出异常
     */
    AiModelSourceEntity selectAvailableModel(String aiType);

    /**
     * 检查指定类型是否有可用模型
     *
     * @param aiType AI类型
     * @return 是否有可用模型
     */
    boolean hasAvailableModel(String aiType);

    /**
     * 获取指定类型的默认模型
     *
     * @param aiType AI类型
     * @return 默认模型实体，如果没有默认模型则返回null
     */
    AiModelSourceEntity getDefaultModel(String aiType);

    /**
     * 统计指定类型的可用模型数量
     *
     * @param aiType AI类型
     * @return 可用模型数量
     */
    long countAvailableModels(String aiType);
}