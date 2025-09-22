package com.xinyirun.scm.ai.bean.dto.sdk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @Author: jianxing
 * @CreateTime: 2024-08-28  17:31
 */
@Data
public class CombineSearch {
    @Schema(description = "匹配模式 所有/任一", allowableValues = {"AND", "OR"})
    private String searchMode = SearchMode.AND.name();

    public String getSearchMode() {
        return StringUtils.isBlank(searchMode) ? SearchMode.AND.name() : searchMode;
    }

    public enum SearchMode {
        /**
         * 所有
         */
        AND,
        /**
         * 任一
         */
        OR
    }
}