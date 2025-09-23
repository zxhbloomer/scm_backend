package com.xinyirun.scm.ai.bean.domain;

import com.xinyirun.scm.ai.validation.groups.Created;
import com.xinyirun.scm.ai.validation.groups.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

@Data
public class AiPrompt implements Serializable {
    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_prompt.id.not_blank}", groups = {Updated.class})
    @Size(min = 1, max = 50, message = "{ai_prompt.id.length_range}", groups = {Created.class, Updated.class})
    private String id;

    @Schema(description = "编号", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_prompt.code.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 20, message = "{ai_prompt.code.length_range}", groups = {Created.class, Updated.class})
    private String code;

    @Schema(description = "简称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_prompt.nickname.not_blank}", groups = {Created.class})
    @Size(min = 1, max = 20, message = "{ai_prompt.nickname.length_range}", groups = {Created.class, Updated.class})
    private String nickname;

    @Schema(description = "描述")
    @Size(max = 100, message = "{ai_prompt.desc.length_range}", groups = {Created.class, Updated.class})
    private String desc;

    @Schema(description = "提示词类型（1：客服提示词、2：知识库提示词）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "{ai_prompt.type.not_blank}", groups = {Created.class})
    private Integer type;

    @Schema(description = "提示词内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "{ai_prompt.prompt.not_blank}", groups = {Created.class})
    private String prompt;

    private static final long serialVersionUID = 1L;

    public enum Column {
        id("id", "id", "VARCHAR", false),
        code("code", "code", "VARCHAR", false),
        nickname("nickname", "nickname", "VARCHAR", false),
        desc("desc", "desc", "VARCHAR", true),
        type("type", "type", "INTEGER", false),
        prompt("prompt", "prompt", "LONGTEXT", false);

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