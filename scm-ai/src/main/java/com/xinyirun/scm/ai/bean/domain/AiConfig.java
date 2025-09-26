package com.xinyirun.scm.ai.bean.domain;

import com.xinyirun.scm.ai.validation.groups.*;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import lombok.Data;

@Data
public class AiConfig implements Serializable {
    @Schema(description = "配置ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_config.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{ai_config.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description = "配置键", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_config.config_key.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 100, message = "{ai_config.config_key.length_range}", groups = {Created.class, Updated.class})
    private String configKey;

    @Schema(description = "配置值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_config.config_value.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 500, message = "{ai_config.config_value.length_range}", groups = {Created.class, Updated.class})
    private String configValue;

    @Schema(description = "配置说明")
    @Size(max = 200, message = "{ai_config.description.length_range}", groups = {Created.class, Updated.class})
    private String description;

    @Schema(description = "租户ID")
    @Size(max = 30, message = "{ai_config.tenant.length_range}", groups = {Created.class, Updated.class})
    private String tenant;

    @Schema(description = "创建时间")
    private Long createTime;

    @Schema(description = "更新时间")
    private Long updateTime;



    private static final long serialVersionUID = 1L;

    public enum Column {
        id("id", "id", "VARCHAR", false),
        configKey("config_key", "configKey", "VARCHAR", false),
        configValue("config_value", "configValue", "VARCHAR", false),
        description("description", "description", "VARCHAR", false),
        tenant("tenant", "tenant", "VARCHAR", false),
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