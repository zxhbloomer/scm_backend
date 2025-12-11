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

    /**
     * 是否显示执行过程输出到chat流
     * true(默认): 流式输出显示在聊天界面
     * false: 不显示流式输出，但结果仍传递给下游节点
     */
    @JsonProperty("show_process_output")
    private Boolean showProcessOutput = true;
}
