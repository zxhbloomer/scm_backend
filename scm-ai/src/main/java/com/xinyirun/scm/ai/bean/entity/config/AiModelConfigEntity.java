package com.xinyirun.scm.ai.bean.entity.config;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI模型配置实体类
 */
@Data
@TableName("ai_model_config")
public class AiModelConfigEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1236428713158926194L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("name")
    private String name;

    @TableField("model_name")
    private String modelName;

    @TableField("model_type")
    private String modelType;

    @TableField("provider")
    private String provider;

    @TableField("api_key")
    private String apiKey;

    @TableField("base_url")
    private String baseUrl;

    @TableField("deployment_name")
    private String deploymentName;

    @TableField("temperature")
    private BigDecimal temperature;

    @TableField("max_tokens")
    private Integer maxTokens;

    @TableField("top_p")
    private BigDecimal topP;

    @TableField("timeout")
    private Integer timeout;

    @TableField("enabled")
    private Boolean enabled;

    /**
     * 是否支持对话能力
     */
    @TableField("support_chat")
    private Boolean supportChat;

    /**
     * 是否支持视觉能力
     */
    @TableField("support_vision")
    private Boolean supportVision;

    /**
     * 是否支持嵌入能力
     */
    @TableField("support_embedding")
    private Boolean supportEmbedding;

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

    @TableField("dbversion")
    @Version
    private Integer dbversion;
}
