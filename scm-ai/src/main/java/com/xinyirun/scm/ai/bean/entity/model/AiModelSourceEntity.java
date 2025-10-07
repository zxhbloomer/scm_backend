package com.xinyirun.scm.ai.bean.entity.model;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
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
    @DataChangeLabelAnnotation("模型名称")
    private String name;

    /**
     * 模型类别（大语言/视觉/音频）
     */
    @TableField("type")
    @DataChangeLabelAnnotation("模型类别")
    private String type;

    /**
     * 模型供应商
     */
    @TableField("provider_name")
    @DataChangeLabelAnnotation("模型供应商")
    private String providerName;

    /**
     * 模型类型（公有/私有）
     */
    @TableField("permission_type")
    @DataChangeLabelAnnotation("模型类型")
    private String permissionType;

    /**
     * 模型连接状态
     */
    @TableField("status")
    @DataChangeLabelAnnotation("模型连接状态")
    private Boolean status;

    /**
     * 模型拥有者
     */
    @TableField("owner")
    @DataChangeLabelAnnotation("模型拥有者")
    private String owner;

    /**
     * 模型拥有者类型（个人/企业）
     */
    @TableField("owner_type")
    @DataChangeLabelAnnotation("模型拥有者类型")
    private String ownerType;

    /**
     * 基础名称
     */
    @TableField("base_name")
    @DataChangeLabelAnnotation("基础名称")
    private String baseName;

    /**
     * 模型类型
     */
    @TableField("model_type")
    @DataChangeLabelAnnotation("模型类型")
    private String modelType;

    /**
     * 模型key
     */
    @TableField("app_key")
    @DataChangeLabelAnnotation("模型key")
    private String appKey;

    /**
     * 模型url
     */
    @TableField("api_url")
    @DataChangeLabelAnnotation("模型url")
    private String apiUrl;

    /**
     * 模型参数配置值
     */
    @TableField("adv_settings")
    @DataChangeLabelAnnotation("模型参数配置值")
    private String advSettings;

    /**
     * 创建用户
     */
    @TableField("create_user")
    @DataChangeLabelAnnotation("创建用户")
    private String createUser;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建时间", extension = "getCTimeExtension")
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改时间", extension = "getUTimeExtension")
    private LocalDateTime updateTime;

    /**
     * 创建人id
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建人", extension = "getUserNameExtension")
    private Long cId;

    /**
     * 修改人id
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改人", extension = "getUserNameExtension")
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
    @DataChangeLabelAnnotation("是否为默认模型")
    private Integer isDefault;

    /**
     * 配置ID
     */
    @TableField("ai_config_id")
    @DataChangeLabelAnnotation("配置ID")
    private String aiConfigId;
}
