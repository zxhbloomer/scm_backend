package com.xinyirun.scm.ai.bean.entity.statistics;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * AI Token使用实体类
 * 对应数据表：ai_token_usage
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_token_usage")
public class AiTokenUsageEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -3503317847400235662L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField("conversation_id")
    private String conversationId;

    @TableField("user_id")
    private String userId;

    @TableField("model_source_id")
    private String modelSourceId;

    @TableField("conversation_content_id")
    private String conversationContentId;

    @TableField("provider_name")
    private String providerName;

    @TableField("model_type")
    private String modelType;

    @TableField("prompt_tokens")
    private Long promptTokens;

    @TableField("completion_tokens")
    private Long completionTokens;

    @TableField(value = "total_tokens", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    private Long totalTokens;

    @TableField("usage_time")
    private LocalDateTime usageTime;

    @TableField("token_unit_price")
    private java.math.BigDecimal tokenUnitPrice;

    @TableField("cost")
    private java.math.BigDecimal cost;

    @TableField("success")
    private Boolean success;

    @TableField("response_time")
    private Long responseTime;

    @TableField("ai_config_id")
    private String aiConfigId;

    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime createTime;

    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    @TableField("dbversion")
    private Integer dbversion;
}
