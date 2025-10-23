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
 * @author SCM-AI团队
 * @since 2025-10-21
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NodeProcessResult {

    private List<NodeIOData> content = new ArrayList<>();

    /**
     * 条件执行时使用
     */
    private String nextNodeUuid;
}
