package com.xinyirun.scm.ai.bean.entity.config;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI 配置实体类
 * 用于存储 AI 模型相关的敏感配置信息（API Key、密钥等）
 *
 * <p>安全特性：</p>
 * <ul>
 *   <li>敏感信息存储在数据库，不提交到 Git</li>
 *   <li>支持配置加密存储</li>
 *   <li>支持多租户配置隔离</li>
 *   <li>支持多环境配置（dev/test/prod）</li>
 * </ul>
 *
 * @author SCM AI Team
 * @since 2025-10-10
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
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 配置键（如：RAG_PROVIDER, EMBEDDING_SILICONFLOW_API_KEY）
     */
    @TableField("config_key")
    private String configKey;

    /**
     * 配置值（敏感信息）
     */
    @TableField("config_value")
    private String configValue;

    /**
     * 配置描述
     */
    @TableField("description")
    private String description;

    /**
     * 创建时间
     */
    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;

    /**
     * 更新时间
     */
    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime uTime;

    /**
     * 创建人ID
     */
    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    /**
     * 更新人ID
     */
    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    /**
     * 数据版本（乐观锁）
     */
    @TableField("dbversion")
    @Version
    private Integer dbversion;

}
