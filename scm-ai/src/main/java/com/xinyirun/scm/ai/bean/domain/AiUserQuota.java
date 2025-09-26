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
public class AiUserQuota implements Serializable {
    @Schema(description = "配额记录ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_user_quota.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{ai_user_quota.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description = "用户ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_user_quota.user_id.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 50, message = "{ai_user_quota.user_id.length_range}", groups = {Created.class, Updated.class})
    private String userId;

    @Schema(description = "租户ID")
    @Size(max = 30, message = "{ai_user_quota.tenant.length_range}", groups = {Created.class, Updated.class})
    private String tenant;

    @Schema(description = "日Token限额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{ai_user_quota.daily_limit.not_null}", groups = {Created.class})
    @Min(value = 0, message = "{ai_user_quota.daily_limit.min}", groups = {Created.class, Updated.class})
    private Long dailyLimit;

    @Schema(description = "月Token限额", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{ai_user_quota.monthly_limit.not_null}", groups = {Created.class})
    @Min(value = 0, message = "{ai_user_quota.monthly_limit.min}", groups = {Created.class, Updated.class})
    private Long monthlyLimit;

    @Schema(description = "当日已使用Token数")
    @Min(value = 0, message = "{ai_user_quota.daily_used.min}", groups = {Created.class, Updated.class})
    private Long dailyUsed;

    @Schema(description = "当月已使用Token数")
    @Min(value = 0, message = "{ai_user_quota.monthly_used.min}", groups = {Created.class, Updated.class})
    private Long monthlyUsed;

    @Schema(description = "日配额重置日期")
    private LocalDate dailyResetDate;

    @Schema(description = "月配额重置日期")
    private LocalDate monthlyResetDate;

    @Schema(description = "累计费用（美元）")
    @DecimalMin(value = "0.0", message = "{ai_user_quota.total_cost.min}", groups = {Created.class, Updated.class})
    private BigDecimal totalCost;

    @Schema(description = "状态（1启用，0禁用）")
    private Boolean status;

    @Schema(description = "创建时间")
    private Long createTime;

    @Schema(description = "更新时间")
    private Long updateTime;

    private static final long serialVersionUID = 1L;

    public enum Column {
        id("id", "id", "VARCHAR", false),
        userId("user_id", "userId", "VARCHAR", false),
        tenant("tenant", "tenant", "VARCHAR", false),
        dailyLimit("daily_limit", "dailyLimit", "BIGINT", false),
        monthlyLimit("monthly_limit", "monthlyLimit", "BIGINT", false),
        dailyUsed("daily_used", "dailyUsed", "BIGINT", false),
        monthlyUsed("monthly_used", "monthlyUsed", "BIGINT", false),
        dailyResetDate("daily_reset_date", "dailyResetDate", "DATE", false),
        monthlyResetDate("monthly_reset_date", "monthlyResetDate", "DATE", false),
        totalCost("total_cost", "totalCost", "DECIMAL", false),
        status("status", "status", "TINYINT", false),
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