package com.xinyirun.scm.ai.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.request.AdvSettingVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceCreateNameVo;
import com.xinyirun.scm.ai.mapper.model.AiModelSourceMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AI模型源服务
 *
 * 提供AI模型源管理功能，包括模型源的创建、查询、更新、状态管理等操作
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Service
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Slf4j
public class AiModelSourceService {

    @Resource
    private AiModelSourceMapper aiModelSourceMapper;

    /**
     * 根据ID查询模型源
     *
     * @param id 模型源ID
     * @return 模型源VO
     */
    public AiModelSourceVo getById(Integer id) {
        try {
            AiModelSourceEntity entity = aiModelSourceMapper.selectById(id);
            if (entity != null) {
                return convertToVo(entity);
            }
            return null;
        } catch (Exception e) {
            log.error("根据ID查询模型源失败, id: {}", id, e);
            return null;
        }
    }

    /**
     * 根据提供商查询模型源
     *
     * @param provider 提供商
     * @param tenant 租户标识
     * @param pageNum 页码
     * @param pageSize 页大小
     * @return 模型源分页列表
     */
    public IPage<AiModelSourceVo> getByProvider(String provider, String tenant, int pageNum, int pageSize) {
        try {
            Page<AiModelSourceEntity> page = new Page<>(pageNum, pageSize);
            QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();

            wrapper.eq("provider_name", provider);
            // 注意：数据库表中没有tenant字段，暂时不过滤租户
            // if (StringUtils.hasText(tenant)) {
            //     wrapper.eq("tenant", tenant);
            // }
            wrapper.eq("status", true); // status是bit类型，true表示启用
            wrapper.orderByDesc("create_time"); // 按创建时间倒序

            IPage<AiModelSourceEntity> entityPage = aiModelSourceMapper.selectPage(page, wrapper);

            // 转换为VO分页
            Page<AiModelSourceVo> voPage = new Page<>(pageNum, pageSize);
            voPage.setTotal(entityPage.getTotal());
            voPage.setRecords(entityPage.getRecords().stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList()));

            return voPage;
        } catch (Exception e) {
            log.error("根据提供商查询模型源失败, provider: {}, tenant: {}", provider, tenant, e);
            return new Page<>(pageNum, pageSize);
        }
    }

    /**
     * 查询所有可用模型源
     *
     * @param tenant 租户标识
     * @return 模型源列表
     */
    public List<AiModelSourceVo> getAllEnabledModels(String tenant) {
        try {
            QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();
            // 注意：数据库表中没有tenant字段，暂时不过滤租户
            // if (StringUtils.hasText(tenant)) {
            //     wrapper.eq("tenant", tenant);
            // }
            wrapper.eq("status", true); // status是bit类型，true表示启用
            wrapper.orderByAsc("provider_name", "create_time");

            List<AiModelSourceEntity> entities = aiModelSourceMapper.selectList(wrapper);
            return entities.stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("查询所有可用模型源失败, tenant: {}", tenant, e);
            return List.of();
        }
    }

    /**
     * 根据模型名称查询
     *
     * @param modelName 模型名称
     * @param tenant 租户标识
     * @return 模型源VO
     */
    public AiModelSourceVo getByModelName(String modelName, String tenant) {
        try {
            QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("name", modelName); // 数据库字段是name不是model_name
            // 注意：数据库表中没有tenant字段，暂时不过滤租户
            // if (StringUtils.hasText(tenant)) {
            //     wrapper.eq("tenant", tenant);
            // }
            wrapper.eq("status", true); // status是bit类型，true表示启用

            AiModelSourceEntity entity = aiModelSourceMapper.selectOne(wrapper);
            if (entity != null) {
                return convertToVo(entity);
            }
            return null;
        } catch (Exception e) {
            log.error("根据模型名称查询失败, modelName: {}, tenant: {}", modelName, tenant, e);
            return null;
        }
    }

    /**
     * 根据模型类型查询
     *
     * @param modelType 模型类型
     * @param tenant 租户标识
     * @return 模型源列表
     */
    public List<AiModelSourceVo> getByModelType(String modelType, String tenant) {
        try {
            QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("model_type", modelType);
            // 注意：数据库表中没有tenant字段，暂时不过滤租户
            // if (StringUtils.hasText(tenant)) {
            //     wrapper.eq("tenant", tenant);
            // }
            wrapper.eq("status", true); // status是bit类型，true表示启用
            wrapper.orderByDesc("create_time");

            List<AiModelSourceEntity> entities = aiModelSourceMapper.selectList(wrapper);
            return entities.stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据模型类型查询失败, modelType: {}, tenant: {}", modelType, tenant, e);
            return List.of();
        }
    }

    /**
     * 创建新模型源
     *
     * @param modelSourceVo 模型源VO
     * @param operatorId 操作员ID
     * @return 创建的模型源VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiModelSourceVo createModelSource(AiModelSourceVo modelSourceVo, Long operatorId) {
        try {
            AiModelSourceEntity entity = convertToEntity(modelSourceVo);

            Long now = System.currentTimeMillis();
            entity.setCreate_time(now);
            entity.setCreate_user(operatorId != null ? operatorId.toString() : null);
            entity.setStatus(true); // 设置为启用状态
            // 注意：数据库表中没有update_time、is_enabled、dbversion字段

            int result = aiModelSourceMapper.insert(entity);
            if (result > 0) {
                log.info("创建模型源成功, modelName: {}", entity.getName());
                return convertToVo(entity);
            }

            return null;
        } catch (Exception e) {
            log.error("创建模型源失败", e);
            throw new RuntimeException("创建模型源失败", e);
        }
    }

    /**
     * 更新模型源信息
     *
     * @param modelSourceVo 模型源VO
     * @param operatorId 操作员ID
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateModelSource(AiModelSourceVo modelSourceVo, Long operatorId) {
        try {
            AiModelSourceEntity entity = convertToEntity(modelSourceVo);
            // 注意：数据库表中没有update_time、u_id字段，仅设置ID用于更新标识

            int result = aiModelSourceMapper.updateById(entity);
            if (result > 0) {
                log.info("更新模型源成功, id: {}", entity.getId());
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("更新模型源失败", e);
            throw new RuntimeException("更新模型源失败", e);
        }
    }

    /**
     * 启用/禁用模型源
     *
     * @param modelSourceId 模型源ID
     * @param enabled 是否启用
     * @param operatorId 操作员ID
     * @return 更新结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleModelSource(Integer modelSourceId, boolean enabled, Long operatorId) {
        try {
            AiModelSourceEntity entity = new AiModelSourceEntity();
            entity.setId(String.valueOf(modelSourceId));
            entity.setStatus(enabled); // status是bit类型，直接设置boolean值
            // 注意：数据库表中没有update_time、u_id字段

            int result = aiModelSourceMapper.updateById(entity);
            if (result > 0) {
                log.info("{}模型源成功, modelSourceId: {}", enabled ? "启用" : "禁用", modelSourceId);
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("{}模型源失败, modelSourceId: {}", enabled ? "启用" : "禁用", modelSourceId, e);
            throw new RuntimeException((enabled ? "启用" : "禁用") + "模型源失败", e);
        }
    }

    /**
     * 删除模型源（物理删除）
     *
     * @param modelSourceId 模型源ID
     * @return 删除结果
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteModelSource(Integer modelSourceId) {
        try {
            int result = aiModelSourceMapper.deleteById(modelSourceId);
            if (result > 0) {
                log.info("删除模型源成功, modelSourceId: {}", modelSourceId);
                return true;
            }

            return false;
        } catch (Exception e) {
            log.error("删除模型源失败, modelSourceId: {}", modelSourceId, e);
            throw new RuntimeException("删除模型源失败", e);
        }
    }

    /**
     * 测试模型连接
     *
     * @param modelSourceId 模型源ID
     * @return 连接测试结果
     */
    public boolean testModelConnection(Integer modelSourceId) {
        try {
            AiModelSourceEntity entity = aiModelSourceMapper.selectById(modelSourceId);
            if (entity == null) {
                log.warn("模型源不存在, modelSourceId: {}", modelSourceId);
                return false;
            }

            // TODO: 实现具体的模型连接测试逻辑
            // 这里应该根据不同的提供商实现具体的连接测试
            log.info("模型连接测试, modelName: {}, provider: {}", entity.getName(), entity.getProvider_name());
            return true;
        } catch (Exception e) {
            log.error("模型连接测试失败, modelSourceId: {}", modelSourceId, e);
            return false;
        }
    }

    /**
     * 编辑模型配置 - 兼容原有API
     *
     * @param aiModelSourceVo 模型源VO
     * @param operatorId 操作人ID
     * @return 编辑后的模型源VO
     */
    @Transactional(rollbackFor = Exception.class)
    public AiModelSourceVo editModuleConfig(AiModelSourceVo aiModelSourceVo, Long operatorId) {
        try {
            // 处理高级设置参数
            processAdvancedSettings(aiModelSourceVo);

            if (aiModelSourceVo.getId() != null) {
                // 更新操作
                updateModelSource(aiModelSourceVo, operatorId);
                return aiModelSourceVo;
            } else {
                // 创建操作
                return createModelSource(aiModelSourceVo, operatorId);
            }
        } catch (Exception e) {
            log.error("编辑模型配置失败, modelSourceVo: {}, operatorId: {}", aiModelSourceVo, operatorId, e);
            throw new RuntimeException("编辑模型配置失败", e);
        }
    }

    /**
     * 根据查询请求获取模型源VO列表 - 兼容备份代码
     *
     * @param request 查询请求
     * @return 模型源VO列表
     */
    public List<AiModelSourceVo> getModelSourceVoList(AiModelSourceRequestVo request) {
        try {
            QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();

            // 添加查询条件
            if (StringUtils.hasText(request.getOwner())) {
                wrapper.eq("owner", request.getOwner());
            }
            if (StringUtils.hasText(request.getProviderName())) {
                wrapper.eq("provider_name", request.getProviderName()); // 使用正确的数据库字段
            }
            if (StringUtils.hasText(request.getKeyword())) {
                wrapper.like("name", request.getKeyword()) // 数据库字段是name不是model_name
                       .or().like("base_name", request.getKeyword()); // 没有description字段，使用base_name
            }

            wrapper.eq("status", true); // status是bit类型，true表示启用
            wrapper.orderByDesc(request.getSortField() != null ? request.getSortField() : "create_time");

            List<AiModelSourceEntity> entities = aiModelSourceMapper.selectList(wrapper);

            return entities.stream()
                    .map(this::convertToVo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据查询请求获取模型源VO列表失败, request: {}", request, e);
            return List.of();
        }
    }

    /**
     * 根据查询请求获取模型源列表
     *
     * @param request 查询请求
     * @return 模型源列表
     */
    public List<AiModelSourceCreateNameVo> getModelSourceList(AiModelSourceRequestVo request) {
        try {
            QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();

            // 添加查询条件
            if (StringUtils.hasText(request.getOwner())) {
                wrapper.eq("owner", request.getOwner());
            }
            if (StringUtils.hasText(request.getProviderName())) {
                wrapper.eq("provider_name", request.getProviderName()); // 使用正确的数据库字段
            }
            if (StringUtils.hasText(request.getKeyword())) {
                wrapper.like("name", request.getKeyword()) // 数据库字段是name不是model_name
                       .or().like("base_name", request.getKeyword()); // 没有description字段，使用base_name
            }

            wrapper.eq("status", true); // status是bit类型，true表示启用
            wrapper.orderByDesc(request.getSortField() != null ? request.getSortField() : "create_time");

            List<AiModelSourceEntity> entities = aiModelSourceMapper.selectList(wrapper);

            return entities.stream()
                    .map(this::convertToCreateNameVo)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("根据查询请求获取模型源列表失败, request: {}", request, e);
            return List.of();
        }
    }

    /**
     * 处理高级设置参数
     *
     * @param aiModelSourceVo 模型源VO
     */
    private void processAdvancedSettings(AiModelSourceVo aiModelSourceVo) {
        List<AdvSettingVo> advSettingVoList = aiModelSourceVo.getAdvSettingVoList();
        if (advSettingVoList != null && !advSettingVoList.isEmpty()) {
            // 获取默认高级参数配置
            Map<String, AdvSettingVo> defaultSettingsMap = getDefaultAdvSettingVoMap();
            List<AdvSettingVo> processedSettings = new ArrayList<>();

            for (AdvSettingVo advSettingVo : advSettingVoList) {
                // 校验参数类型是否有效
                if (isValidParamType(advSettingVo.getName())) {
                    AdvSettingVo defaultSetting = defaultSettingsMap.get(advSettingVo.getName());
                    if (defaultSetting != null) {
                        // 复制用户设置到默认配置
                        defaultSetting.setValue(advSettingVo.getValue());
                        defaultSetting.setEnable(advSettingVo.getEnable());
                        checkParamDefault(defaultSetting);
                        processedSettings.add(defaultSetting);
                    }
                }
            }

            aiModelSourceVo.setAdvSettingVoList(processedSettings);
        }
    }

    /**
     * 获取默认高级参数配置映射
     *
     * @return 默认高级参数配置映射
     */
    private Map<String, AdvSettingVo> getDefaultAdvSettingVoMap() {
        Map<String, AdvSettingVo> advSettingVoMap = new HashMap<>();

        AdvSettingVo temperatureSetting = new AdvSettingVo("temperature", "温度", null, false);
        temperatureSetting.setMinValue(0.0);
        temperatureSetting.setMaxValue(2.0);
        advSettingVoMap.put(temperatureSetting.getName(), temperatureSetting);

        AdvSettingVo topPSetting = new AdvSettingVo("top_p", "Top P", null, false);
        topPSetting.setMinValue(0.0);
        topPSetting.setMaxValue(1.0);
        advSettingVoMap.put(topPSetting.getName(), topPSetting);

        AdvSettingVo maxTokensSetting = new AdvSettingVo("max_tokens", "最大Token", null, false);
        maxTokensSetting.setMinValue(1.0);
        maxTokensSetting.setMaxValue(4096.0);
        advSettingVoMap.put(maxTokensSetting.getName(), maxTokensSetting);

        AdvSettingVo frequencyPenaltySetting = new AdvSettingVo("frequency_penalty", "频率惩罚", null, false);
        frequencyPenaltySetting.setMinValue(-2.0);
        frequencyPenaltySetting.setMaxValue(2.0);
        advSettingVoMap.put(frequencyPenaltySetting.getName(), frequencyPenaltySetting);

        return advSettingVoMap;
    }

    /**
     * 校验参数类型是否有效
     *
     * @param paramType 参数类型
     * @return 是否有效
     */
    private boolean isValidParamType(String paramType) {
        List<String> validTypes = List.of(
                "temperature", "top_p", "max_tokens", "frequency_penalty"
        );
        return validTypes.contains(paramType);
    }

    /**
     * 检查高级参数默认值
     *
     * @param advSetting 高级参数配置
     */
    private void checkParamDefault(AdvSettingVo advSetting) {
        if ("temperature".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(0.7);
            advSetting.setEnable(false);
        }
        if ("top_p".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(1.0);
            advSetting.setEnable(false);
        }
        if ("max_tokens".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(1024.0);
            advSetting.setEnable(false);
        }
        if ("frequency_penalty".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(0.0);
            advSetting.setEnable(false);
        }
    }

    /**
     * Entity转VO - 手动映射确保字段正确
     */
    private AiModelSourceVo convertToVo(AiModelSourceEntity entity) {
        AiModelSourceVo vo = new AiModelSourceVo();

        // ID和基础字段
        vo.setId(entity.getId());
        vo.setModel_name(entity.getName());
        vo.setProvider(entity.getProvider_name());
        vo.setApi_key(entity.getApp_key());
        vo.setApi_url(entity.getApi_url());
        // 注意：数据库表中没有tenant字段，需要根据实际情况处理
        // vo.setTenant(entity.getTenant());

        // 状态字段映射：status(bit) -> is_enabled(Integer)
        vo.setIs_enabled(entity.getStatus() != null && entity.getStatus() ? 1 : 0);

        // 处理高级设置参数
        if (StringUtils.hasText(entity.getAdv_settings())) {
            try {
                List<AdvSettingVo> advSettingVoList = JSON.parseArray(entity.getAdv_settings(), AdvSettingVo.class);
                vo.setAdvSettingVoList(advSettingVoList);
            } catch (Exception e) {
                log.warn("解析高级设置参数失败, entityId: {}, advSettings: {}", entity.getId(), entity.getAdv_settings(), e);
            }
        }

        // 时间字段转换：数据库中只有create_time(bigint)
        if (entity.getCreate_time() != null) {
            vo.setC_time(LocalDateTime.ofEpochSecond(entity.getCreate_time() / 1000, 0, java.time.ZoneOffset.of("+8")));
        }
        // 注意：数据库表中没有update_time字段
        // if (entity.getUpdate_time() != null) {
        //     vo.setU_time(LocalDateTime.ofEpochSecond(entity.getUpdate_time() / 1000, 0, java.time.ZoneOffset.of("+8")));
        // }

        return vo;
    }

    /**
     * VO转Entity - 手动映射确保字段正确
     */
    private AiModelSourceEntity convertToEntity(AiModelSourceVo vo) {
        AiModelSourceEntity entity = new AiModelSourceEntity();

        // ID和基础字段
        if (vo.getId() != null) {
            entity.setId(vo.getId());
        }
        entity.setName(vo.getModel_name());
        entity.setProvider_name(vo.getProvider());
        entity.setApp_key(vo.getApi_key());
        entity.setApi_url(vo.getApi_url());
        // 注意：数据库表中没有tenant字段
        // entity.setTenant(vo.getTenant());

        // 状态字段映射：is_enabled(Integer) -> status(Boolean)
        entity.setStatus(vo.getIs_enabled() != null && vo.getIs_enabled() == 1);

        // 处理高级设置参数
        if (vo.getAdvSettingVoList() != null && !vo.getAdvSettingVoList().isEmpty()) {
            String advSettingsJson = JSON.toJSONString(vo.getAdvSettingVoList());
            entity.setAdv_settings(advSettingsJson);
        }

        // 时间字段转换：数据库中只有create_time(bigint)
        if (vo.getC_time() != null) {
            entity.setCreate_time(vo.getC_time().atZone(java.time.ZoneId.of("Asia/Shanghai")).toEpochSecond() * 1000);
        }
        // 注意：数据库表中没有update_time字段
        // if (vo.getU_time() != null) {
        //     entity.setUpdate_time(vo.getU_time().atZone(java.time.ZoneId.of("Asia/Shanghai")).toEpochSecond() * 1000);
        // }

        return entity;
    }

    /**
     * Entity转CreateNameVo
     */
    private AiModelSourceCreateNameVo convertToCreateNameVo(AiModelSourceEntity entity) {
        AiModelSourceCreateNameVo vo = new AiModelSourceCreateNameVo();

        // 继承自AiModelSourceVo的字段
        vo.setId(entity.getId());
        vo.setModel_name(entity.getName());
        vo.setProvider(entity.getProvider_name());
        vo.setApi_key(entity.getApp_key());
        vo.setApi_url(entity.getApi_url());
        // 注意：数据库表中没有tenant字段
        // vo.setTenant(entity.getTenant());
        vo.setIs_enabled(entity.getStatus() != null && entity.getStatus() ? 1 : 0);

        // CreateNameVo特有字段 - 创建人名称（这里需要根据实际情况获取用户名称）
        // TODO: 根据create_user获取实际用户名称
        vo.setCreateUserName(entity.getCreate_user() != null ? entity.getCreate_user() : "系统用户");

        // 时间字段转换：数据库中只有create_time(bigint)
        if (entity.getCreate_time() != null) {
            vo.setC_time(LocalDateTime.ofEpochSecond(entity.getCreate_time() / 1000, 0, java.time.ZoneOffset.of("+8")));
        }
        // 注意：数据库表中没有update_time字段
        // if (entity.getUpdate_time() != null) {
        //     vo.setU_time(LocalDateTime.ofEpochSecond(entity.getUpdate_time() / 1000, 0, java.time.ZoneOffset.of("+8")));
        // }

        return vo;
    }
}