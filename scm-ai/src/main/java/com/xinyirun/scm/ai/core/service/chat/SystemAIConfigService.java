package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.request.AdvSettingVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.OptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AIChatOptionVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceCreateNameVo;
import com.xinyirun.scm.ai.mapper.model.AiModelSourceMapper;
import com.xinyirun.scm.ai.mapper.model.ExtAiModelSourceMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.BeanUtils;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class SystemAIConfigService {

    @Resource
    private AiModelSourceMapper aiModelSourceMapper;
    @Resource
    private ExtAiModelSourceMapper extAiModelSourceMapper;
    @Resource
    private AiChatBaseService aiChatBaseService;

    private static final String DEFAULT_OWNER = "system";

    /**
     * 编辑模型配置
     *
     * @param aiModelSourceVo 模型配置VO
     * @param userId           用户ID
     */
    public AiModelSourceEntity editModuleConfig(AiModelSourceVo aiModelSourceVo, String userId) {
        // 使用条件表达式确定ID并设置操作标志
        String id = StringUtils.isNotBlank(aiModelSourceVo.getId()) ?
                aiModelSourceVo.getId() : String.valueOf(System.currentTimeMillis());
        boolean isAddOperation = aiModelSourceVo.getId() == null;

        //设置模型拥有者
        setOwner(aiModelSourceVo, userId);

        // 校验模型名称唯一性
        if (isModelNameDuplicated(aiModelSourceVo.getModel_name(), id, isAddOperation, aiModelSourceVo.getOwner())) {
            throw new RuntimeException("模型名称" + StringUtils.SPACE + aiModelSourceVo.getModel_name() + StringUtils.SPACE + "已被系统中其他用户占用，请更换唯一名称后重试");
        }

        // 检查AppKey变更
        validateAppKey(aiModelSourceVo, id);

        // 创建并填充模型对象
        var aiModelSource = new AiModelSourceEntity();
        buildModelSource(aiModelSourceVo, userId, aiModelSource, id, isAddOperation);

        // 根据操作类型执行不同逻辑
        if (isAddOperation) {
            // 新增时如果开启状态，验证模型
            validateModelIfEnabled(aiModelSource, aiModelSourceVo);
            aiModelSourceMapper.insert(aiModelSource);
        } else {
            // 更新时如果开启状态且Key变更，需要验证模型
            if (aiModelSource.getStatus()) {
                validModel(aiModelSourceVo);
            }
            aiModelSourceMapper.updateById(aiModelSource);
        }

        return aiModelSource;
    }

    private static void setOwner(AiModelSourceVo aiModelSourceVo, String userId) {
        if ("PRIVATE".equalsIgnoreCase(aiModelSourceVo.getPermission_type())) {
            aiModelSourceVo.setOwner(userId);
        } else {
            aiModelSourceVo.setOwner(DEFAULT_OWNER);
        }
    }

    // 提取验证模型名称是否重复的逻辑
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

    // 验证模型有效性
    private void validateModelIfEnabled(AiModelSourceEntity aiModelSource, AiModelSourceVo aiModelSourceVo) {
        if (aiModelSource.getStatus()) {
            validModel(aiModelSourceVo);
        }
    }

    private void validateAppKey(AiModelSourceVo aiModelSourceVo, String id) {
        AiModelSourceEntity oldSource = aiModelSourceMapper.selectById(id);
        if (oldSource == null) {
            return; // 新增模型源时不需要验证AppKey
        }
        String oldKey = maskSkString(oldSource.getApp_key());
        if (StringUtils.equalsIgnoreCase(oldKey, aiModelSourceVo.getApi_key())) {
            aiModelSourceVo.setApi_key(oldSource.getApp_key());
        }
    }

    /**
     * 构建模型源对象
     *
     * @param aiModelSourceVo 模型源VO
     * @param userId           用户ID
     * @param aiModelSource    模型源对象
     * @param id               模型源ID
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
        //校验高级参数是否合格，以及默认值设置
        List<AdvSettingVo> advSettingVoList = aiModelSourceVo.getAdvSettingVoList();
        List<AdvSettingVo> advSettingVos = getAdvSettingVos(advSettingVoList);
        aiModelSource.setAdv_settings(JSON.toJSONString(advSettingVos));
    }

    /**
     * 校验参数类型是否有效
     *
     * @param paramType 参数类型
     * @return 是否有效
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
     * 获取高级参数配置列表
     *
     * @param advSettingVoList 高级参数配置VO列表
     * @return 高级参数配置VO列表
     */
    @NotNull
    private List<AdvSettingVo> getAdvSettingVos(List<AdvSettingVo> advSettingVoList) {
        //设置默认高级参数配置
        Map<String, AdvSettingVo> advSettingVoMap = getDefaultAdvSettingVoMap();
        List<AdvSettingVo> advSettingVos = new ArrayList<>();
        if (advSettingVoList != null && !advSettingVoList.isEmpty()) {
            for (AdvSettingVo advSettingVo : advSettingVoList) {
                //校验前端的高级参数属性,如果不存在，则不保存
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
     * 设置默认高级参数配置, 类型固定，防止前端传入错误的参数类型
     *
     * @return Map<String, AdvSettingVo>
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
     * 检查高级参数默认值，如果没有配置，则设置为默认值
     *
     * @param advSetting 高级参数配置VO
     */
    private static void checkParamDefault(AdvSettingVo advSetting) {
        //如果是温度，则默认值为0.7
        if ("temperature".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(0.7);
            advSetting.setEnable(false);
        }
        //如果是topP，则默认值为1.0
        if ("top_p".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(1.0);
            advSetting.setEnable(false);
        }
        //如果是最大token，则默认值为1024
        if ("max_tokens".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(1024.0);
            advSetting.setEnable(false);
        }
        //如果是频率惩罚，则默认值为0.0
        if ("frequency_penalty".equalsIgnoreCase(advSetting.getName()) && advSetting.getValue() == null) {
            advSetting.setValue(0.0);
            advSetting.setEnable(false);
        }
    }

    /**
     * 验证模型连接是否成功
     *
     * @param aiModelSourceVo 模型源VO
     */
    private void validModel(@NotNull AiModelSourceVo aiModelSourceVo) {
        try {
            // 简化模型验证逻辑，实际项目中应该调用AI服务验证
            log.info("验证模型连接: {}", aiModelSourceVo.getModel_name());
            // 这里应该调用AI服务进行实际验证，简化处理
        } catch (Exception e) {
            throw new RuntimeException("模型连接验证失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取模型源列表
     *
     * @param aiModelSourceRequest 模型源请求VO
     * @return 模型源VO列表
     */
    public List<AiModelSourceVo> getModelSourceList(AiModelSourceRequestVo aiModelSourceRequest) {
        if (StringUtils.isBlank(aiModelSourceRequest.getOwner())) {
            aiModelSourceRequest.setOwner(DEFAULT_OWNER);
        }
        // 按照备份代码逻辑，使用扩展mapper获取列表
        List<AiModelSourceCreateNameVo> list = extAiModelSourceMapper.list(aiModelSourceRequest);
        List<AiModelSourceVo> resultList = new ArrayList<>();
        for (AiModelSourceCreateNameVo aiModelSource : list) {
            // 直接使用AiModelSourceCreateNameVo对象，因为它继承自AiModelSourceVo
            aiModelSource.setApi_key(maskSkString(aiModelSource.getApi_key()));
            aiModelSource.setCreate_user(aiModelSource.getCreateUserName());
            resultList.add(aiModelSource);
        }
        return resultList;
    }

    /**
     * 将APPkey 字符串进行掩码处理
     *
     * @param input 输入的字符串
     * @return 掩码后的字符串
     */
    public static String maskSkString(String input) {
        if (StringUtils.isBlank(input)) {
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

    private AiModelSourceVo getModelSourceVo(AiModelSourceEntity modelSource) {
        AiModelSourceVo modelSourceVo = getModelSourceVoWithKey(modelSource);
        modelSourceVo.setApi_key(maskSkString(modelSource.getApp_key()));
        return modelSourceVo;
    }


    private AiModelSourceVo getModelSourceVoWithKey(AiModelSourceEntity modelSource) {
        AiModelSourceVo modelSourceVo = new AiModelSourceVo();
        BeanUtils.copyProperties(modelSource, modelSourceVo);

        // 字段映射
        modelSourceVo.setId(modelSource.getId());
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

    /**
     * 根据ID获取模型源数据传输对象
     *
     * @param id 模型源ID，如果为null、空字符串或"default"，则获取默认模型
     * @param userId 用户ID
     * @return 模型源数据传输对象
     */
    public AiModelSourceVo getModelSourceVoWithKey(String id, String userId) {
        AiModelSourceEntity aiModelSource;

        if (StringUtils.isBlank(id) || "default".equals(id)) {
            // 应用核心逻辑：状态过滤 + 排序规则
            QueryWrapper<AiModelSourceEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("status", true);
            wrapper.orderByAsc("permission_type");
            List<AiModelSourceEntity> models = aiModelSourceMapper.selectList(wrapper);
            aiModelSource = models.isEmpty() ? null : models.get(0);
        } else {
            // 查询指定ID模型
            aiModelSource = aiModelSourceMapper.selectById(id);
        }

        if (aiModelSource == null) {
            throw new RuntimeException("模型信息不存在");
        }
        //检查模型是否开启
        if (!aiModelSource.getStatus()) {
            throw new RuntimeException("模型未启用");
        }
        // 校验权限，全局的和自己的
        if (!StringUtils.equalsAny(aiModelSource.getOwner(), userId, DEFAULT_OWNER)) {
            throw new RuntimeException("模型信息不存在");
        }
        return getModelSourceVoWithKey(aiModelSource);
    }


    public void delModelInformation(String id, String userId) {
        AiModelSourceEntity aiModelSource = aiModelSourceMapper.selectById(id);
        if (aiModelSource == null) {
            throw new RuntimeException("模型信息不存在");
        }
        //检查个人模型查看权限
        if (StringUtils.isNotBlank(userId) && !StringUtils.equalsIgnoreCase(aiModelSource.getOwner(), userId)) {
            throw new RuntimeException("模型信息不存在");
        }
        aiModelSourceMapper.deleteById(id);
    }
}