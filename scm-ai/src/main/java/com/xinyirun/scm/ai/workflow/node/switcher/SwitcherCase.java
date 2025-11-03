package com.xinyirun.scm.ai.workflow.node.switcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 条件分支Case配置
 *
 * @author zxh
 * @since 2025-10-23
 */
@Data
public class SwitcherCase {

    private String uuid;
    private String operator;
    private List<Condition> conditions;
    @JsonProperty("target_node_uuid")
    private String targetNodeUuid;

    @Data
    public static class Condition {
        private String uuid;
        @JsonProperty("node_uuid")
        private String nodeUuid;
        @JsonProperty("node_param_name")
        private String nodeParamName;
        private String operator;
        private String value;
    }
}
