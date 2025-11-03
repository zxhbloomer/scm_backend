package com.xinyirun.scm.ai.workflow.node.switcher;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 条件分支节点配置
 *
 * @author zxh
 * @since 2025-10-23
 */
@Data
public class SwitcherNodeConfig {

    private List<SwitcherCase> cases;
    @JsonProperty("default_target_node_uuid")
    private String defaultTargetNodeUuid;
}
