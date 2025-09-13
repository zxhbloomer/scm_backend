/*
 * SCM AI Module - LLM Model Entity
 * Adapted from ByteDesk AI Module for SCM System
 * 
 * Author: SCM Development Team
 * Description: LLM模型实体类，管理大型语言模型配置和提供商关系
 */
package com.xinyirun.scm.ai.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.xinyirun.scm.ai.base.BytedeskBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * LLM模型实体类，用于AI模型管理
 * 管理大型语言模型配置和提供商关系
 * 
 * 数据表: scm_ai_model
 * 用途: 存储LLM模型定义、类型和提供商关联
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("scm_ai_model")
public class LlmModelEntity extends BytedeskBaseEntity {

    /**
     * 模型名称，用于API调用
     */
    @TableField("name")
    private String name;
    
    /**
     * 用户友好的模型昵称
     */
    @TableField("nickname")
    private String nickname;

    /**
     * 模型能力描述
     */
    @TableField("description")
    private String description = "";

    /**
     * LLM模型类型 (TEXT, EMBEDDING等)
     */
    @TableField("model_type")
    private String type = LlmModelTypeEnum.TEXT.name();

    /**
     * 关联的提供商UID
     */
    @TableField("provider_uid")
    private String providerUid;

    /**
     * 模型提供商名称
     */
    @TableField("provider_name")
    private String providerName;

    /**
     * 是否启用
     */
    @TableField("is_enabled")
    private Boolean enabled = true;

    /**
     * 是否允许租户调用系统API，默认不开启
     * 如果开启了，则租户默认调用系统API
     */
    @TableField("is_system_enabled")
    private Boolean systemEnabled = false;

    /**
     * 模型版本
     */
    @TableField("model_version")
    private String modelVersion;

    /**
     * 模型参数配置（JSON格式）
     */
    @TableField("config_json")
    private String configJson;

    /**
     * 模型最大token数
     */
    @TableField("max_tokens")
    private Integer maxTokens;

    /**
     * 排序字段
     */
    @TableField("sort_order")
    private Integer sortOrder = 100;

    /**
     * 备注
     */
    @TableField("remark")
    private String remark;

    // =============== 业务方法 ===============

    /**
     * 获取模型类型枚举
     */
    public LlmModelTypeEnum getTypeEnum() {
        return LlmModelTypeEnum.fromType(this.type);
    }

    /**
     * 设置模型类型枚举
     */
    public void setTypeEnum(LlmModelTypeEnum typeEnum) {
        this.type = typeEnum.name();
    }

    /**
     * 是否为文本模型
     */
    public boolean isTextModel() {
        return LlmModelTypeEnum.TEXT.name().equals(this.type);
    }

    /**
     * 是否为嵌入模型
     */
    public boolean isEmbeddingModel() {
        return LlmModelTypeEnum.EMBEDDING.name().equals(this.type);
    }

    /**
     * 是否为多模态模型
     */
    public boolean isMultiModalModel() {
        return LlmModelTypeEnum.VISION.name().equals(this.type) ||
               LlmModelTypeEnum.IMAGE2TEXT.name().equals(this.type) ||
               LlmModelTypeEnum.TEXT2IMAGE.name().equals(this.type);
    }

    /**
     * 检查模型是否可用
     */
    public boolean isAvailable() {
        return Boolean.TRUE.equals(this.enabled) && 
               this.providerUid != null && 
               !this.providerUid.trim().isEmpty();
    }
}