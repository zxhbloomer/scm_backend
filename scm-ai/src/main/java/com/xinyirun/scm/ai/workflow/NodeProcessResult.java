package com.xinyirun.scm.ai.workflow;

import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 节点处理结果
 *
 * @author zxh
 * @since 2025-10-21
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeProcessResult {

    private List<NodeIOData> content = new ArrayList<>();

    /**
     * 条件执行时使用（单目标路由）
     */
    private String nextNodeUuid;

    /**
     * 条件分支匹配的 sourceHandle（用于多目标路由）
     *
     * 对应 SwitcherCase.uuid 或 "default_handle"
     * 当同一个 sourceHandle 对应多个目标节点时，
     * WorkflowEngine 会创建虚拟并行分发节点来实现并行执行
     */
    private String nextSourceHandle;
}
