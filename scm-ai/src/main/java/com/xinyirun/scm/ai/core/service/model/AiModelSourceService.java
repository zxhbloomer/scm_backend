package com.xinyirun.scm.ai.core.service.model;

import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;

/**
 * AI模型源服务接口
 *
 * <p>对标aideepin：com.moyz.adi.chat.service.AiModelService</p>
 *
 * <p>用途：</p>
 * <ul>
 *   <li>查询AI模型配置信息（包含maxInputTokens、maxOutputTokens、contextWindow）</li>
 *   <li>用于严格模式判断点1：获取模型的最大输入Token限制</li>
 * </ul>
 *
 * @author SCM AI Team
 * @since 2025-10-13
 */
public interface AiModelSourceService {

    /**
     * 根据ID查询AI模型配置
     *
     * <p>对标aideepin方法：AiModelService.getByIdOrThrow()</p>
     *
     * @param id AI模型ID
     * @return AI模型配置VO（包含max_input_tokens、max_output_tokens、context_window字段）
     */
    AiModelSourceVo getById(String id);

    /**
     * 根据ID查询AI模型配置（不存在时抛出异常）
     *
     * <p>对标aideepin方法：AiModelService.getByIdOrThrow()</p>
     *
     * @param id AI模型ID
     * @return AI模型配置VO
     * @throws RuntimeException 当模型不存在时抛出
     */
    AiModelSourceVo getByIdOrThrow(String id);
}
