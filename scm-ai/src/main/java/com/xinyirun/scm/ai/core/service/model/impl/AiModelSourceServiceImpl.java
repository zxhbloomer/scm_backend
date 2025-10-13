package com.xinyirun.scm.ai.core.service.model.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.core.mapper.model.AiModelSourceMapper;
import com.xinyirun.scm.ai.core.service.model.AiModelSourceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AI模型源服务实现类
 *
 * <p>对标aideepin：com.moyz.adi.chat.service.impl.AiModelServiceImpl</p>
 *
 * @author SCM AI Team
 * @since 2025-10-13
 */
@Slf4j
@Service
public class AiModelSourceServiceImpl implements AiModelSourceService {

    @Autowired
    private AiModelSourceMapper aiModelSourceMapper;

    /**
     * 根据ID查询AI模型配置
     *
     * <p>对标aideepin方法：AiModelService.getByIdOrThrow()</p>
     *
     * @param id AI模型ID
     * @return AI模型配置VO（包含max_input_tokens、max_output_tokens、context_window字段）
     */
    @Override
    public AiModelSourceVo getById(String id) {
        if (id == null || id.isEmpty()) {
            log.warn("查询AI模型配置失败：ID为空");
            return null;
        }

        LambdaQueryWrapper<AiModelSourceEntity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AiModelSourceEntity::getId, id);

        AiModelSourceEntity entity = aiModelSourceMapper.selectOne(wrapper);
        if (entity == null) {
            log.warn("AI模型配置不存在，id: {}", id);
            return null;
        }

        // Entity转VO
        AiModelSourceVo vo = new AiModelSourceVo();
        BeanUtils.copyProperties(entity, vo);

        // 注意：Entity使用camelCase，VO使用underscore命名
        // BeanUtils.copyProperties会自动处理同名字段，但需要手动处理命名不同的字段
        vo.setModel_name(entity.getName());
        vo.setModel_code(entity.getBaseName());
        vo.setType(entity.getType());
        vo.setProvider(entity.getProviderName());
        vo.setApi_key(entity.getAppKey());
        vo.setApi_url(entity.getApiUrl());
        vo.setStatus(entity.getStatus());
        vo.setPermission_type(entity.getPermissionType());
        vo.setOwner(entity.getOwner());
        vo.setOwner_type(entity.getOwnerType());
        vo.setBase_name(entity.getBaseName());
        vo.setContext_window(entity.getContextWindow());
        vo.setMax_input_tokens(entity.getMaxInputTokens());
        vo.setMax_output_tokens(entity.getMaxOutputTokens());
        vo.setC_time(entity.getCreateTime());
        vo.setU_time(entity.getUpdateTime());
        vo.setC_id(entity.getCId());
        vo.setU_id(entity.getUId());
        vo.setDbversion(entity.getDbversion());

        log.debug("查询AI模型配置成功，id: {}, max_input_tokens: {}", id, vo.getMax_input_tokens());
        return vo;
    }

    /**
     * 根据ID查询AI模型配置（不存在时抛出异常）
     *
     * <p>对标aideepin方法：AiModelService.getByIdOrThrow()</p>
     *
     * @param id AI模型ID
     * @return AI模型配置VO
     * @throws RuntimeException 当模型不存在时抛出
     */
    @Override
    public AiModelSourceVo getByIdOrThrow(String id) {
        AiModelSourceVo vo = getById(id);
        if (vo == null) {
            String errorMsg = String.format("AI模型配置不存在，id: %s", id);
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
        return vo;
    }
}
