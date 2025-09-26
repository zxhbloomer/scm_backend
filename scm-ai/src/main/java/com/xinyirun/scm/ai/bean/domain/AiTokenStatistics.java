package com.xinyirun.scm.ai.bean.domain;

import com.xinyirun.scm.ai.validation.groups.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.Data;

@Data
public class AiTokenStatistics implements Serializable {
    @Schema(description = "统计记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_token_statistics.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{ai_token_statistics.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description = "统计类型（daily, monthly, user_daily, user_monthly, tenant_daily, tenant_monthly）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_token_statistics.stat_type.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 30, message = "{ai_token_statistics.stat_type.length_range}", groups = {Created.class, Updated.class})
    private String statType;

    @Schema(description = "统计维度键（如：user_id、tenant、model_source_id）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_token_statistics.stat_key.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 128, message = "{ai_token_statistics.stat_key.length_range}", groups = {Created.class, Updated.class})
    private String statKey;

    @Schema(description = "统计日期", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{ai_token_statistics.stat_date.not_null}", groups = {Created.class})
    private LocalDate statDate;

    @Schema(description = "租户ID")
    @Size(max = 30, message = "{ai_token_statistics.tenant.length_range}", groups = {Created.class, Updated.class})
    private String tenant;

    @Schema(description = "AI提供商")
    @Size(max = 255, message = "{ai_token_statistics.ai_provider.length_range}", groups = {Created.class, Updated.class})
    private String aiProvider;

    @Schema(description = "AI模型类型")
    @Size(max = 255, message = "{ai_token_statistics.ai_model_type.length_range}", groups = {Created.class, Updated.class})
    private String aiModelType;

    @Schema(description = "总请求次数")
    @Min(value = 0, message = "{ai_token_statistics.total_requests.min}", groups = {Created.class, Updated.class})
    private Long totalRequests;

    @Schema(description = "成功请求次数")
    @Min(value = 0, message = "{ai_token_statistics.success_requests.min}", groups = {Created.class, Updated.class})
    private Long successRequests;

    @Schema(description = "总输入token")
    @Min(value = 0, message = "{ai_token_statistics.total_prompt_tokens.min}", groups = {Created.class, Updated.class})
    private Long totalPromptTokens;

    @Schema(description = "总输出token")
    @Min(value = 0, message = "{ai_token_statistics.total_completion_tokens.min}", groups = {Created.class, Updated.class})
    private Long totalCompletionTokens;

    @Schema(description = "总token数")
    @Min(value = 0, message = "{ai_token_statistics.total_tokens.min}", groups = {Created.class, Updated.class})
    private Long totalTokens;

    @Schema(description = "总费用（美元）")
    @DecimalMin(value = "0.0", message = "{ai_token_statistics.total_cost.min}", groups = {Created.class, Updated.class})
    private BigDecimal totalCost;

    @Schema(description = "平均响应时间（毫秒）")
    @Min(value = 0, message = "{ai_token_statistics.avg_response_time.min}", groups = {Created.class, Updated.class})
    private Long avgResponseTime;

    @Schema(description = "最大响应时间（毫秒）")
    @Min(value = 0, message = "{ai_token_statistics.max_response_time.min}", groups = {Created.class, Updated.class})
    private Long maxResponseTime;

    @Schema(description = "最小响应时间（毫秒）")
    @Min(value = 0, message = "{ai_token_statistics.min_response_time.min}", groups = {Created.class, Updated.class})
    private Long minResponseTime;

    @Schema(description = "创建时间")
    private Long createTime;

    @Schema(description = "更新时间")
    private Long updateTime;

    private static final long serialVersionUID = 1L;

    public enum Column {
        id("id", "id", "VARCHAR", false),
        statType("stat_type", "statType", "VARCHAR", false),
        statKey("stat_key", "statKey", "VARCHAR", false),
        statDate("stat_date", "statDate", "DATE", false),
        tenant("tenant", "tenant", "VARCHAR", false),
        aiProvider("ai_provider", "aiProvider", "VARCHAR", false),
        aiModelType("ai_model_type", "aiModelType", "VARCHAR", false),
        totalRequests("total_requests", "totalRequests", "BIGINT", false),
        successRequests("success_requests", "successRequests", "BIGINT", false),
        totalPromptTokens("total_prompt_tokens", "totalPromptTokens", "BIGINT", false),
        totalCompletionTokens("total_completion_tokens", "totalCompletionTokens", "BIGINT", false),
        totalTokens("total_tokens", "totalTokens", "BIGINT", false),
        totalCost("total_cost", "totalCost", "DECIMAL", false),
        avgResponseTime("avg_response_time", "avgResponseTime", "BIGINT", false),
        maxResponseTime("max_response_time", "maxResponseTime", "BIGINT", false),
        minResponseTime("min_response_time", "minResponseTime", "BIGINT", false),
        createTime("create_time", "createTime", "BIGINT", false),
        updateTime("update_time", "updateTime", "BIGINT", false);

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