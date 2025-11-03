package com.xinyirun.scm.ai.workflow;

import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.bsc.langgraph4j.state.AgentState;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_INPUT_PARAM_NAME;
import static com.xinyirun.scm.ai.workflow.WorkflowConstants.NODE_PROCESS_STATUS_READY;

/**
 * 工作流节点实例状态
 *
 * @author zxh
 * @since 2025-10-21
 */
@Setter
@Getter
@ToString(callSuper = true)
public class WfNodeState extends AgentState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String uuid = UuidUtil.createShort();
    private Integer processStatus = NODE_PROCESS_STATUS_READY;
    private String processStatusRemark = "";
    private List<NodeIOData> inputs = new ArrayList<>();
    private List<NodeIOData> outputs = new ArrayList<>();

    /**
     * Constructs an AgentState with the given initial data.
     *
     * @param initData the initial data for the agent state
     */
    public WfNodeState(Map<String, Object> initData) {
        super(initData);
    }

    public WfNodeState() {
        super(Map.of());
    }

    public Optional<NodeIOData> getDefaultInput() {
        return inputs.stream()
                .filter(item -> DEFAULT_INPUT_PARAM_NAME.equals(item.getName()))
                .findFirst();
    }
}
