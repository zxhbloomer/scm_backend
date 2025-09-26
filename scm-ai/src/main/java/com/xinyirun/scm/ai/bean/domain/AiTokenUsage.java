package com.xinyirun.scm.ai.bean.domain;

import com.xinyirun.scm.ai.validation.groups.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.Data;

@Data
public class AiTokenUsage implements Serializable {
    @Schema(description = "Token使用记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_token_usage.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{ai_token_usage.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description = "对话ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_token_usage.conversation_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{ai_token_usage.conversation_id.length_range}", groups = {Created.class, Updated.class})
    private String conversationId;

    @Schema(description = "模型源ID")
    @Size(max = 50, message = "{ai_token_usage.model_source_id.length_range}", groups = {Created.class, Updated.class})
    private String modelSourceId;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_token_usage.user_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{ai_token_usage.user_id.length_range}", groups = {Created.class, Updated.class})
    private String userId;

    @Schema(description = "租户ID")
    @Size(max = 30, message = "{ai_token_usage.tenant.length_range}", groups = {Created.class, Updated.class})
    private String tenant;

    @Schema(description = "AI提供商名称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_token_usage.ai_provider.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{ai_token_usage.ai_provider.length_range}", groups = {Created.class, Updated.class})
    private String aiProvider;

    @Schema(description = "AI模型类型", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_token_usage.ai_model_type.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 255, message = "{ai_token_usage.ai_model_type.length_range}", groups = {Created.class, Updated.class})
    private String aiModelType;

    @Schema(description = "输入token数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{ai_token_usage.prompt_tokens.not_null}", groups = {Created.class})
    @Min(value = 0, message = "{ai_token_usage.prompt_tokens.min}", groups = {Created.class, Updated.class})
    private Long promptTokens;

    @Schema(description = "输出token数量", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{ai_token_usage.completion_tokens.not_null}", groups = {Created.class})
    @Min(value = 0, message = "{ai_token_usage.completion_tokens.min}", groups = {Created.class, Updated.class})
    private Long completionTokens;

    @Schema(description = "总token数量（计算列）")
    private Long totalTokens;

    @Schema(description = "Token单价（美元，支持高精度）")
    @DecimalMin(value = "0.0", message = "{ai_token_usage.token_unit_price.min}", groups = {Created.class, Updated.class})
    private BigDecimal tokenUnitPrice;

    @Schema(description = "费用（美元）")
    @DecimalMin(value = "0.0", message = "{ai_token_usage.cost.min}", groups = {Created.class, Updated.class})
    private BigDecimal cost;

    @Schema(description = "请求是否成功（1成功，0失败）")
    private Boolean success;

    @Schema(description = "响应时间（毫秒）")
    @Min(value = 0, message = "{ai_token_usage.response_time.min}", groups = {Created.class, Updated.class})
    private Long responseTime;

    @Schema(description = "创建时间")
    private Long createTime;

    @Schema(description = "配置ID")
    @Size(max = 50, message = "{ai_token_usage.ai_config_id.length_range}")
    private String aiConfigId;

    private static final long serialVersionUID = 1L;

    public enum Column {
        id("id", "id", "VARCHAR", false),
        conversationId("conversation_id", "conversationId", "VARCHAR", false),
        modelSourceId("model_source_id", "modelSourceId", "VARCHAR", false),
        userId("user_id", "userId", "VARCHAR", false),
        tenant("tenant", "tenant", "VARCHAR", false),
        aiProvider("ai_provider", "aiProvider", "VARCHAR", false),
        aiModelType("ai_model_type", "aiModelType", "VARCHAR", false),
        promptTokens("prompt_tokens", "promptTokens", "BIGINT", false),
        completionTokens("completion_tokens", "completionTokens", "BIGINT", false),
        totalTokens("total_tokens", "totalTokens", "BIGINT", false),
        tokenUnitPrice("token_unit_price", "tokenUnitPrice", "DECIMAL", false),
        cost("cost", "cost", "DECIMAL", false),
        success("success", "success", "TINYINT", false),
        responseTime("response_time", "responseTime", "BIGINT", false),
        createTime("create_time", "createTime", "BIGINT", false),
        aiConfigId("ai_config_id", "aiConfigId", "VARCHAR", false);

        private static final String BEGINNING_DELIMITER = "`";

        private static final String ENDING_DELIMITER = "`";

        private final String column;

        private final boolean isColumnNameDelimited;

        private final String javaProperty;

        private final String jdbcType;

        public String value() {
            return this.column;
        }

        public String getValue() {
            return this.column;
        }

        public String getJavaProperty() {
            return this.javaProperty;
        }

        public String getJdbcType() {
            return this.jdbcType;
        }

        Column(String column, String javaProperty, String jdbcType, boolean isColumnNameDelimited) {
            this.column = column;
            this.javaProperty = javaProperty;
            this.jdbcType = jdbcType;
            this.isColumnNameDelimited = isColumnNameDelimited;
        }

        public String desc() {
            return this.getEscapedColumnName() + " DESC";
        }

        public String asc() {
            return this.getEscapedColumnName() + " ASC";
        }

        public static Column[] excludes(Column ... excludes) {
            ArrayList<Column> columns = new ArrayList<>(Arrays.asList(Column.values()));
            if (excludes != null && excludes.length > 0) {
                columns.removeAll(new ArrayList<>(Arrays.asList(excludes)));
            }
            return columns.toArray(new Column[]{});
        }

        public static Column[] all() {
            return Column.values();
        }

        public String getEscapedColumnName() {
            if (this.isColumnNameDelimited) {
                return new StringBuilder().append(BEGINNING_DELIMITER).append(this.column).append(ENDING_DELIMITER).toString();
            } else {
                return this.column;
            }
        }

        public String getAliasedEscapedColumnName() {
            return this.getEscapedColumnName();
        }
    }
}