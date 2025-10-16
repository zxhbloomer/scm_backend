package com.xinyirun.scm.ai.bean.entity.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI模型源实体类
 * 对应数据表：ai_model_source
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_model_source")
public class AiModelSourceEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 模型名称
     */
    @TableField("name")
    private String name;

    /**
     * 模型类别（大语言/视觉/音频）
     */
    @TableField("type")
    private String type;

    /**
     * 模型供应商
     */
    @TableField("provider_name")
    private String providerName;

    /**
     * 模型类型（公有/私有）
     */
    @TableField("permission_type")
    private String permissionType;

    /**
     * 模型连接状态
     */
    @TableField("status")
    private Boolean status;

    /**
     * 模型拥有者
     */
    @TableField("owner")
    private String owner;

    /**
     * 模型拥有者类型（个人/企业）
     */
    @TableField("owner_type")
    private String ownerType;

    /**
     * 基础名称
     */
    @TableField("base_name")
    private String baseName;

    /**
     * 模型类型
     */
    @TableField("model_type")
    private String modelType;

    /**
     * 模型key
     */
    @TableField("app_key")
    private String appKey;

    /**
     * 模型url
     */
    @TableField("api_url")
    private String apiUrl;

    /**
     * 模型参数配置值
     */
    @TableField("adv_settings")
    private String advSettings;

    /**
     * 上下文窗口大小（总token容量）
     */
    @TableField("context_window")
    private Integer contextWindow;

    /**
     * 最大输入token数（用于判断用户问题是否过长）
     */
    @TableField("max_input_tokens")
    private Integer maxInputTokens;

    /**
     * 最大输出token数
     */
    @TableField("max_output_tokens")
    private Integer maxOutputTokens;

    /**
     * 创建用户
     */
    @TableField("create_user")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人id
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    /**
     * 修改人id
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    /**
     * 数据版本，乐观锁使用
     */
    @TableField("dbversion")
    private Integer dbversion;

    /**
     * 是否为默认模型
     */
    @TableField("is_default")
    private Integer isDefault;

    /**
     * 配置ID
     */
    @TableField("ai_config_id")
    private String aiConfigId;
}
