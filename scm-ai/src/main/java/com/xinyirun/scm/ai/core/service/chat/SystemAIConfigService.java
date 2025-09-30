package com.xinyirun.scm.ai.core.service.chat;

import com.xinyirun.scm.ai.bean.constant.ModelConstants;
import com.xinyirun.scm.ai.bean.entity.model.AiModelSourceEntity;
import com.xinyirun.scm.ai.bean.vo.model.AiModelSourceVo;
import com.xinyirun.scm.ai.bean.vo.request.AdvSettingVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceRequestVo;
import com.xinyirun.scm.ai.bean.vo.request.AiModelSourceCreateNameVo;
import com.xinyirun.scm.ai.core.mapper.model.AiModelSourceMapper;
import com.xinyirun.scm.ai.core.mapper.model.ExtAiModelSourceMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
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

@Service
@Transactional(rollbackFor = Exception.class)
@Slf4j
public class SystemAIConfigService {

    @Resource
    private AiModelSourceMapper aiModelSourceMapper;

    /**
     * 检查用户是否有权限访问指定模型
     * 权限规则：用户可以访问自己创建的模型和系统公共模型
     *
     * @param modelOwner 模型拥有者ID
     * @param userId 当前用户ID
     * @return 是否有权限访问
     */
    private boolean hasModelAccess(String modelOwner, String userId) {
        return StringUtils.equalsAny(modelOwner, userId, ModelConstants.SYSTEM_OWNER);
    }

    /**
     * 判断是否为系统公共模型
     *
     * @param owner 模型拥有者
     * @return 是否为系统模型
     */
    private boolean isSystemModel(String owner) {
        return ModelConstants.SYSTEM_OWNER.equalsIgnoreCase(owner);
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
        modelSourceVo.setStatus(modelSource.getStatus());
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
        // 校验权限：用户可以访问自己的模型和系统公共模型
        if (!hasModelAccess(aiModelSource.getOwner(), userId)) {
            throw new RuntimeException("模型信息不存在");
        }
        return getModelSourceVoWithKey(aiModelSource);
    }


    /**
     * 删除模型信息
     * 只有模型拥有者可以删除自己的模型，系统模型需要管理员权限
     *
     * @param id 模型ID
     * @param userId 用户ID
     */
    public void delModelInformation(String id, String userId) {
        AiModelSourceEntity aiModelSource = aiModelSourceMapper.selectById(id);
        if (aiModelSource == null) {
            throw new RuntimeException("模型信息不存在");
        }

        // 检查删除权限：只有模型拥有者可以删除
        if (!StringUtils.equalsIgnoreCase(aiModelSource.getOwner(), userId)) {
            throw new RuntimeException("模型信息不存在");
        }

        aiModelSourceMapper.deleteById(id);
        log.info("删除模型信息成功, id: {}, userId: {}", id, userId);
    }
}