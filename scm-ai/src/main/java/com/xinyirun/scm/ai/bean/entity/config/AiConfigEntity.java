package com.xinyirun.scm.ai.bean.entity.config;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI配置实体类
 * 对应数据表：ai_config
 *
 * 功能说明：存储AI系统的配置信息，包括API密钥、模型参数等
 *
 * @author SCM-AI重构团队
 * @since 2025-09-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_config")
public class AiConfigEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 配置键
     */
    @TableField("config_key")
    @DataChangeLabelAnnotation("配置键")
    private String config_key;

    /**
     * 配置值
     */
    @TableField("config_value")
    @DataChangeLabelAnnotation("配置值")
    private String config_value;

    /**
     * 配置说明
     */
    @TableField("description")
    @DataChangeLabelAnnotation("配置说明")
    private String description;

    /**
     * 租户ID
     */
    @TableField("tenant")
    @DataChangeLabelAnnotation("租户ID")
    private String tenant;

    /**
     * 创建时间
     */
    @TableField("create_time")
    @DataChangeLabelAnnotation("创建时间")
    private Long create_time;

    /**
     * 更新时间
     */
    @TableField("update_time")
    @DataChangeLabelAnnotation("更新时间")
    private Long update_time;
}
