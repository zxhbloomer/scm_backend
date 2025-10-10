package com.xinyirun.scm.ai.bean.entity.statistics;

import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.common.annotations.DataChangeLabelAnnotation;
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
    @DataChangeLabelAnnotation("对话ID")
    private String conversationId;

    @TableField("user_id")
    @DataChangeLabelAnnotation("用户ID")
    private String userId;

    @TableField("model_source_id")
    @DataChangeLabelAnnotation("模型源ID")
    private String modelSourceId;

    @TableField("conversation_content_id")
    @DataChangeLabelAnnotation("关联的消息ID")
    private String conversationContentId;

    @TableField("provider_name")
    @DataChangeLabelAnnotation("AI提供商名称")
    private String providerName;

    @TableField("model_type")
    @DataChangeLabelAnnotation("模型类型")
    private String modelType;

    @TableField("prompt_tokens")
    @DataChangeLabelAnnotation("输入Token数")
    private Long promptTokens;

    @TableField("completion_tokens")
    @DataChangeLabelAnnotation("输出Token数")
    private Long completionTokens;

    @TableField(value = "total_tokens", insertStrategy = FieldStrategy.NEVER, updateStrategy = FieldStrategy.NEVER)
    @DataChangeLabelAnnotation("总Token数")
    private Long totalTokens;

    @TableField("usage_time")
    @DataChangeLabelAnnotation("使用时间")
    private LocalDateTime usageTime;

    @TableField("token_unit_price")
    @DataChangeLabelAnnotation("Token单价")
    private java.math.BigDecimal tokenUnitPrice;

    @TableField("cost")
    @DataChangeLabelAnnotation("总费用")
    private java.math.BigDecimal cost;

    @TableField("success")
    @DataChangeLabelAnnotation("请求是否成功")
    private Boolean success;

    @TableField("response_time")
    @DataChangeLabelAnnotation("响应时间(毫秒)")
    private Long responseTime;

    @TableField("ai_config_id")
    @DataChangeLabelAnnotation("AI配置ID")
    private String aiConfigId;

    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建时间", extension = "getCTimeExtension")
    private LocalDateTime createTime;

    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改时间", extension = "getUTimeExtension")
    private LocalDateTime updateTime;

    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    @DataChangeLabelAnnotation(value = "创建人", extension = "getUserNameExtension")
    private Long cId;

    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    @DataChangeLabelAnnotation(value = "修改人", extension = "getUserNameExtension")
    private Long uId;

    @TableField("dbversion")
    private Integer dbversion;
}
