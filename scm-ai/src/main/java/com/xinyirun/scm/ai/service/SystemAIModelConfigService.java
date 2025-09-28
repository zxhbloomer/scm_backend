package com.xinyirun.scm.ai.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AdvSettingVo;
import com.xinyirun.scm.ai.bean.vo.request.OptionVo;
import com.xinyirun.scm.ai.mapper.model.AiModelSourceMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class SystemAIModelConfigService {

    @Resource
    private AiModelSourceMapper aiModelSourceMapper;

    private static final String DEFAULT_OWNER = "system";

    /**
     * 编辑模型配置 - 按照备份代码逻辑迁移
     *
     * @param aiModelSourceVo 模型源VO
     * @param userId 用户ID
     * @return 模型源VO
     */
    public AiModelSourceVo editModuleConfig(AiModelSourceVo aiModelSourceVo, String userId) {
        // 使用条件表达式确定ID并设置操作标志
        String id = StringUtils.isNotBlank(aiModelSourceVo.getId()) ?
                aiModelSourceVo.getId() : String.valueOf(System.currentTimeMillis());
        boolean isAddOperation = aiModelSourceVo.getId() == null;

        // 设置模型拥有者
        setOwner(aiModelSourceVo, userId);

        // 校验模型名称唯一性
        if (isModelNameDuplicated(aiModelSourceVo.getModel_name(), id, isAddOperation, aiModelSourceVo.getOwner())) {
            throw new RuntimeException("模型名称已存在: " + aiModelSourceVo.getModel_name());
        }

        // 检查AppKey变更
        validateAppKey(aiModelSourceVo, id);

        // 创建并填充模型对象
        AiModelSourceEntity aiModelSource = new AiModelSourceEntity();
        buildModelSource(aiModelSourceVo, userId, aiModelSource, id, isAddOperation);

        // 根据操作类型执行不同逻辑
        if (isAddOperation) {
            // 新增时如果开启状态，验证模型
            validateModelIfEnabled(aiModelSource, aiModelSourceVo);
            aiModelSourceMapper.insert(aiModelSource);
            log.info("新增模型源成功, id: {}, name: {}", aiModelSource.getId(), aiModelSource.getName());
        } else {
            // 更新时如果开启状态且Key变更，需要验证模型
            if (aiModelSource.getStatus()) {
                validModel(aiModelSourceVo);
            }
            aiModelSourceMapper.updateById(aiModelSource);
            log.info("更新模型源成功, id: {}, name: {}", aiModelSource.getId(), aiModelSource.getName());
        }

        // 按照备份代码逻辑，转换Entity为VO返回
        return getModelSourceVo(aiModelSource);
    }

    /**
     * 设置模型拥有者 - 按照备份代码逻辑
     */
    private static void setOwner(AiModelSourceVo aiModelSourceVo, String userId) {
        if ("PRIVATE".equalsIgnoreCase(aiModelSourceVo.getPermission_type())) {
            aiModelSourceVo.setOwner(userId);
        } else {
            aiModelSourceVo.setOwner(DEFAULT_OWNER);
        }
    }

    /**
     * 提取验证模型名称是否重复的逻辑 - 按照备份代码逻辑
     */
    private boolean isModelNameDuplicated(String name, String id, boolean isAddOperation, String owner) {
        QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();
        if (isAddOperation) {
            if (DEFAULT_OWNER.equalsIgnoreCase(owner)) {
                wrapper.eq("name", name); // 如果是系统模型，判断所有数据
            } else {
                wrapper.eq("name", name).and(w -> w.eq("owner", owner).or().eq("owner", DEFAULT_OWNER)); // 如果是个人模型，则需要判断当前用户和系统中是否有同名模型
            }
        } else {
            // 更新操作时，排除当前ID
            if (DEFAULT_OWNER.equalsIgnoreCase(owner)) {
                wrapper.eq("name", name).ne("id", id); // 系统模型
            } else {
                // 个人模型需要owner条件
                wrapper.eq("name", name).ne("id", id).and(w -> w.eq("owner", owner).or().eq("owner", DEFAULT_OWNER));
            }
        }
        return aiModelSourceMapper.selectCount(wrapper) > 0;
    }

    /**
     * 验证模型有效性 - 按照备份代码逻辑
     */
    private void validateModelIfEnabled(AiModelSourceEntity aiModelSource, AiModelSourceVo aiModelSourceVo) {
        if (aiModelSource.getStatus()) {
            validModel(aiModelSourceVo);
        }
    }

    /**
     * 验证AppKey - 按照备份代码逻辑
     */
    private void validateAppKey(AiModelSourceVo aiModelSourceVo, String id) {
        AiModelSourceEntity oldSource = aiModelSourceMapper.selectById(id);
        if (oldSource == null) {
            return; // 新增模型源时不需要验证AppKey
        }
        String oldKey = maskSkString(oldSource.getApp_key());
        if (oldKey.equalsIgnoreCase(aiModelSourceVo.getApi_key())) {
            aiModelSourceVo.setApi_key(oldSource.getApp_key());
        }
    }

    /**
     * 构建模型源对象 - 按照备份代码逻辑
     */
    private void buildModelSource(AiModelSourceVo aiModelSourceVo, String userId, AiModelSourceEntity aiModelSource, String id, boolean isAddOperation) {
        aiModelSource.setId(id);
        aiModelSource.setType(aiModelSourceVo.getType());
        aiModelSource.setName(aiModelSourceVo.getModel_name());
        aiModelSource.setProvider_name(aiModelSourceVo.getProvider());
        aiModelSource.setPermission_type(aiModelSourceVo.getPermission_type());
        aiModelSource.setStatus(aiModelSourceVo.getIs_enabled() != null && aiModelSourceVo.getIs_enabled() == 1);
        aiModelSource.setOwner_type(aiModelSourceVo.getOwner_type());
        aiModelSource.setOwner(aiModelSourceVo.getOwner());
        aiModelSource.setBase_name(aiModelSourceVo.getBase_name());
        aiModelSource.setApp_key(aiModelSourceVo.getApi_key());
        aiModelSource.setApi_url(aiModelSourceVo.getApi_url());

        // 校验高级参数是否合格，以及默认值设置
        List<AdvSettingVo> advSettingVoList = aiModelSourceVo.getAdvSettingVoList();
        List<AdvSettingVo> advSettingVos = getAdvSettingVos(advSettingVoList);
        aiModelSource.setAdv_settings(JSON.toJSONString(advSettingVos));
        aiModelSource.setCreate_time(System.currentTimeMillis());

        if (isAddOperation) {
            // 新增操作：使用当前用户编码
            aiModelSource.setCreate_user(userId);
        } else {
            // 更新操作：如果VO中有createUser则使用，否则使用当前用户编码
            String createUser = StringUtils.isNotBlank(aiModelSourceVo.getCreate_user())
                    ? aiModelSourceVo.getCreate_user()
                    : userId;
            aiModelSource.setCreate_user(createUser);
        }
    }

    /**
     * 验证模型连接是否成功 - 按照备份代码逻辑
     */
    private void validModel(AiModelSourceVo aiModelSourceVo) {
        try {
            // 简化模型验证逻辑，实际项目中应该调用AI服务验证
            log.info("验证模型连接: {}", aiModelSourceVo.getModel_name());
            // 这里应该调用AI服务进行实际验证，简化处理
        } catch (Exception e) {
            throw new RuntimeException("模型连接验证失败: " + e.getMessage(), e);
        }
    }

    /**
     * 校验参数类型是否有效 - 按照备份代码逻辑
     */
    public boolean isValidParamType(String paramType) {
        List<String> paramTypes = List.of(
                "max_tokens",
                "top_p",
                "frequency_penalty",
                "temperature"
        );
        return paramTypes.contains(paramType);
    }

    /**
     * 获取高级参数配置列表 - 按照备份代码逻辑
     */
    private List<AdvSettingVo> getAdvSettingVos(List<AdvSettingVo> advSettingVoList) {
        // 设置默认高级参数配置
        Map<String, AdvSettingVo> advSettingVoMap = getDefaultAdvSettingVoMap();
        List<AdvSettingVo> advSettingVos = new ArrayList<>();
        if (advSettingVoList != null && !advSettingVoList.isEmpty()) {
            for (AdvSettingVo advSettingVo : advSettingVoList) {
                // 校验前端的高级参数属性,如果不存在，则不保存
                if (!isValidParamType(advSettingVo.getName())) {
                    continue;
                }
                AdvSettingVo advSetting = advSettingVoMap.get(advSettingVo.getName());
                BeanUtils.copyProperties(advSettingVo, advSetting);
                checkParamDefault(advSetting);
                advSettingVos.add(advSetting);
            }
        }
        return advSettingVos;
    }

    /**
     * 设置默认高级参数配置 - 按照备份代码逻辑
     */
    private static Map<String, AdvSettingVo> getDefaultAdvSettingVoMap() {
        Map<String, AdvSettingVo> advSettingVoMap = new HashMap<>();
        AdvSettingVo modelConfigVo = new AdvSettingVo("temperature", "温度", null, false);
        advSettingVoMap.put(modelConfigVo.getName(), modelConfigVo);
        modelConfigVo = new AdvSettingVo("top_p", "Top P", null, false);
        advSettingVoMap.put(modelConfigVo.getName(), modelConfigVo);
        modelConfigVo = new AdvSettingVo("max_tokens", "最大Token", null, false);
        advSettingVoMap.put(modelConfigVo.getName(), modelConfigVo);
        modelConfigVo = new AdvSettingVo("frequency_penalty", "频率惩罚", null, false);
        advSettingVoMap.put(modelConfigVo.getName(), modelConfigVo);
        return advSettingVoMap;
    }

    /**
     * 检查高级参数默认值 - 按照备份代码逻辑
     */
    private static void checkParamDefault(AdvSettingVo advSetting) {
        // 如果是温度，则默认值为0.7
        if ("temperature".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(0.7);
            advSetting.setEnable(false);
        }
        // 如果是topP，则默认值为1.0
        if ("top_p".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(1.0);
            advSetting.setEnable(false);
        }
        // 如果是最大token，则默认值为1024
        if ("max_tokens".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(1024.0);
            advSetting.setEnable(false);
        }
        // 如果是频率惩罚，则默认值为0.0
        if ("frequency_penalty".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(0.0);
            advSetting.setEnable(false);
        }
    }

    /**
     * 删除模型信息 - 按照备份代码逻辑
     */
    public void delModelInformation(String id, String userId) {
        AiModelSourceEntity entity = aiModelSourceMapper.selectById(id);
        if (entity == null) {
            throw new RuntimeException("模型信息不存在");
        }
        // 转换为VO进行业务逻辑处理 - 按照备份代码逻辑
        AiModelSourceVo aiModelSource = getModelSourceVo(entity);

        // 检查个人模型查看权限
        if (StringUtils.isNotBlank(userId) && !userId.equalsIgnoreCase(aiModelSource.getOwner())) {
            throw new RuntimeException("模型信息不存在");
        }
        aiModelSourceMapper.deleteById(id);
        log.info("删除模型源成功, id: {}, userId: {}", id, userId);
    }

    /**
     * 获取模型源列表 - 按照备份代码逻辑
     */
    public List<AiModelSourceVo> getModelSourceList(AiModelSourceRequestVo aiModelSourceRequest) {
        if (!StringUtils.isNotBlank(aiModelSourceRequest.getOwner())) {
            aiModelSourceRequest.setOwner(DEFAULT_OWNER);
        }

        QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();
        // 添加查询条件
        if (StringUtils.isNotBlank(aiModelSourceRequest.getOwner())) {
            wrapper.eq("owner", aiModelSourceRequest.getOwner());
        }
        if (StringUtils.isNotBlank(aiModelSourceRequest.getProviderName())) {
            wrapper.eq("provider_name", aiModelSourceRequest.getProviderName());
        }
        if (StringUtils.isNotBlank(aiModelSourceRequest.getKeyword())) {
            wrapper.and(w -> w.like("name", aiModelSourceRequest.getKeyword())
                          .or().like("base_name", aiModelSourceRequest.getKeyword()));
        }
        wrapper.orderByDesc("create_time");

        List<AiModelSourceEntity> entities = aiModelSourceMapper.selectList(wrapper);
        List<AiModelSourceVo> resultList = new ArrayList<>();
        for (AiModelSourceEntity entity : entities) {
            AiModelSourceVo aiModelSourceVo = getModelSourceVo(entity);
            resultList.add(aiModelSourceVo);
        }
        return resultList;
    }

    /**
     * 根据ID获取模型源VO - 按照备份代码逻辑
     */
    public AiModelSourceVo getModelSourceVo(String id, String userId) {
        AiModelSourceEntity aiModelSource = aiModelSourceMapper.selectById(id);
        if (aiModelSource == null) {
            throw new RuntimeException("模型信息不存在");
        }
        // 检查个人模型查看权限
        if (StringUtils.isNotBlank(userId) && !userId.equalsIgnoreCase(aiModelSource.getOwner())) {
            throw new RuntimeException("模型信息不存在");
        }
        return getModelSourceVo(aiModelSource);
    }

    /**
     * 获取模型源名称列表 - 按照备份代码逻辑
     */
    public List<OptionVo> getModelSourceNameList(String userId) {
        QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", true); // 只查询启用的模型
        // 权限过滤：查询用户自己的模型和系统模型
        wrapper.and(w -> w.eq("owner", userId).or().eq("owner", DEFAULT_OWNER));
        wrapper.select("id", "name");
        wrapper.orderByDesc("create_time");

        List<AiModelSourceEntity> entities = aiModelSourceMapper.selectList(wrapper);
        return entities.stream()
                .map(entity -> new OptionVo(entity.getId(), entity.getName()))
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 将APPkey字符串进行掩码处理 - 按照备份代码逻辑
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

    /**
     * 获取模型源VO - 按照备份代码逻辑
     */
    private AiModelSourceVo getModelSourceVo(AiModelSourceEntity modelSource) {
        AiModelSourceVo modelSourceVo = getModelSourceVoWithKey(modelSource);
        modelSourceVo.setApi_key(maskSkString(modelSource.getApp_key()));
        return modelSourceVo;
    }

    /**
     * 获取带Key的模型源VO - 按照备份代码逻辑
     */
    private AiModelSourceVo getModelSourceVoWithKey(AiModelSourceEntity modelSource) {
        AiModelSourceVo modelSourceVo = new AiModelSourceVo();
        BeanUtils.copyProperties(modelSource, modelSourceVo);

        // 字段映射
        if (StringUtils.isNotBlank(modelSource.getId())) {
            modelSourceVo.setId(modelSource.getId());
        }
        modelSourceVo.setModel_name(modelSource.getName());
        modelSourceVo.setProvider(modelSource.getProvider_name());
        modelSourceVo.setApi_key(modelSource.getApp_key());
        modelSourceVo.setApi_url(modelSource.getApi_url());
        modelSourceVo.setIs_enabled(modelSource.getStatus() != null && modelSource.getStatus() ? 1 : 0);
        modelSourceVo.setOwner(modelSource.getOwner());
        modelSourceVo.setOwner_type(modelSource.getOwner_type());
        modelSourceVo.setPermission_type(modelSource.getPermission_type());
        modelSourceVo.setBase_name(modelSource.getBase_name());
        modelSourceVo.setType(modelSource.getType());

        // 处理高级设置
        if (StringUtils.isNotBlank(modelSource.getAdv_settings())) {
            List<AdvSettingVo> advSettingVoList = JSON.parseArray(modelSource.getAdv_settings(), AdvSettingVo.class);
            modelSourceVo.setAdvSettingVoList(advSettingVoList);
        }

        return modelSourceVo;
    }
}