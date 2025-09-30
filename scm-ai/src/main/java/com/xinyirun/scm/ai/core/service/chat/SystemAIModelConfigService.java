package com.xinyirun.scm.ai.core.service.chat;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.xinyirun.scm.ai.bean.constant.ModelConstants;
import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AdvSettingVo;
import com.xinyirun.scm.ai.bean.vo.request.OptionVo;
import com.xinyirun.scm.ai.core.mapper.model.AiModelSourceMapper;
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
        AiModelSourceEntity aiModelSource;
        if (isAddOperation) {
            // 新增操作：创建新对象
            aiModelSource = new AiModelSourceEntity();
        } else {
            // 更新操作：先查询原有数据，避免覆盖未传入的字段
            aiModelSource = aiModelSourceMapper.selectById(id);
            if (aiModelSource == null) {
                throw new RuntimeException("模型信息不存在");
            }
        }
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
     * 设置模型拥有者
     * 根据权限类型确定模型的拥有者：私有模型归用户所有，公共模型归系统所有
     */
    private static void setOwner(AiModelSourceVo aiModelSourceVo, String userId) {
        if (ModelConstants.PermissionType.PRIVATE.getCode().equalsIgnoreCase(aiModelSourceVo.getPermission_type())) {
            aiModelSourceVo.setOwner(userId);
        } else {
            aiModelSourceVo.setOwner(ModelConstants.SYSTEM_OWNER);
        }
    }

    /**
     * 判断是否为系统模型
     */
    private boolean isSystemModel(String owner) {
        return ModelConstants.SYSTEM_OWNER.equalsIgnoreCase(owner);
    }

    /**
     * 为查询条件添加模型访问权限限制
     * 系统模型：所有人可见
     * 个人模型：只有创建者和系统模型可见
     */
    private void addModelAccessCondition(QueryWrapper<AiModelSourceEntity> wrapper, String owner) {
        if (isSystemModel(owner)) {
            // 系统模型检查：检查所有模型的重名情况
            // 不需要添加owner条件
        } else {
            // 个人模型检查：检查个人模型和系统模型的重名情况
            wrapper.and(w -> w.eq("owner", owner).or().eq("owner", ModelConstants.SYSTEM_OWNER));
        }
    }

    /**
     * 为查询条件添加用户权限限制
     * 用户可以访问自己的模型和系统模型
     */
    private void addUserPermissionCondition(QueryWrapper<AiModelSourceEntity> wrapper, String userId) {
        if (StringUtils.isNotBlank(userId)) {
            wrapper.and(w -> w.eq("owner", userId).or().eq("owner", ModelConstants.SYSTEM_OWNER));
        } else {
            wrapper.eq("owner", ModelConstants.SYSTEM_OWNER);
        }
    }

    /**
     * 验证模型名称是否重复
     * 根据模型拥有者类型，应用不同的重名检查策略
     */
    private boolean isModelNameDuplicated(String name, String id, boolean isAddOperation, String owner) {
        QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("name", name);

        // 更新操作时排除当前记录
        if (!isAddOperation) {
            wrapper.ne("id", id);
        }

        // 应用权限检查逻辑
        addModelAccessCondition(wrapper, owner);

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

        // 更新时只设置非null字段，避免覆盖原有数据
        if (aiModelSourceVo.getType() != null) {
            aiModelSource.setType(aiModelSourceVo.getType());
        }
        if (aiModelSourceVo.getModel_name() != null) {
            aiModelSource.setName(aiModelSourceVo.getModel_name());
        }
        if (aiModelSourceVo.getProvider() != null) {
            aiModelSource.setProvider_name(aiModelSourceVo.getProvider());
        }
        if (aiModelSourceVo.getPermission_type() != null) {
            aiModelSource.setPermission_type(aiModelSourceVo.getPermission_type());
        }
        if (aiModelSourceVo.getStatus() != null) {
            aiModelSource.setStatus(Boolean.TRUE.equals(aiModelSourceVo.getStatus()));
        }
        if (aiModelSourceVo.getOwner_type() != null) {
            aiModelSource.setOwner_type(aiModelSourceVo.getOwner_type());
        }
        if (aiModelSourceVo.getOwner() != null) {
            aiModelSource.setOwner(aiModelSourceVo.getOwner());
        }
        if (aiModelSourceVo.getBase_name() != null) {
            aiModelSource.setBase_name(aiModelSourceVo.getBase_name());
        }
        if (aiModelSourceVo.getApi_key() != null) {
            aiModelSource.setApp_key(aiModelSourceVo.getApi_key());
        }
        if (aiModelSourceVo.getApi_url() != null) {
            aiModelSource.setApi_url(aiModelSourceVo.getApi_url());
        }

        // 校验高级参数是否合格，以及默认值设置
        List<AdvSettingVo> advSettingVoList = aiModelSourceVo.getAdvSettingVoList();
        List<AdvSettingVo> advSettingVos = getAdvSettingVos(advSettingVoList);
        aiModelSource.setAdv_settings(JSON.toJSONString(advSettingVos));

        // 注意：c_time 和 c_id 字段由MyBatis Plus自动填充，不需要手动设置
        // @TableField(fill = FieldFill.INSERT) 会自动处理创建时间和创建人

        if (isAddOperation) {
            // 新增操作：c_id将由MyBatis Plus自动填充当前用户ID
            // 这里不需要手动设置，因为Entity字段有@TableField(fill = FieldFill.INSERT)注解
        } else {
            // 更新操作：保持原有创建人，u_id由MyBatis Plus自动填充当前修改人
            // 这里也不需要手动设置
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
     * 获取模型源列表
     * 支持按拥有者、提供商、关键词等条件筛选
     */
    public List<AiModelSourceVo> getModelSourceList(AiModelSourceRequestVo aiModelSourceRequest) {
        // 默认查询系统模型
        if (!StringUtils.isNotBlank(aiModelSourceRequest.getOwner())) {
            aiModelSourceRequest.setOwner(ModelConstants.SYSTEM_OWNER);
        }

        QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();

        // 添加拥有者条件
        if (StringUtils.isNotBlank(aiModelSourceRequest.getOwner())) {
            wrapper.eq("owner", aiModelSourceRequest.getOwner());
        }

        // 添加提供商条件
        if (StringUtils.isNotBlank(aiModelSourceRequest.getProviderName())) {
            wrapper.eq("provider_name", aiModelSourceRequest.getProviderName());
        }

        // 添加关键词搜索条件
        if (StringUtils.isNotBlank(aiModelSourceRequest.getKeyword())) {
            wrapper.and(w -> w.like("name", aiModelSourceRequest.getKeyword())
                          .or().like("base_name", aiModelSourceRequest.getKeyword()));
        }

        wrapper.orderByDesc("c_time"); // 按创建时间倒序

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
        return getModelSourceVo(aiModelSource);
    }

    /**
     * 获取模型源名称列表
     * 返回用户可访问的启用模型（个人模型+系统模型）
     */
    public List<OptionVo> getModelSourceNameList(String userId) {
        QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("status", true); // 只查询启用的模型

        // 应用用户权限过滤
        addUserPermissionCondition(wrapper, userId);

        wrapper.select("id", "name");
        wrapper.orderByDesc("c_time"); // 按创建时间倒序

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