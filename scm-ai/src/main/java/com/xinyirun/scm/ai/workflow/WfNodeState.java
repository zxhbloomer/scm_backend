package com.xinyirun.scm.ai.workflow;

import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.common.utils.UuidUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import com.alibaba.cloud.ai.graph.OverAllState;

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
 * <p>使用组合模式包装OverAllState（final类不能继承）</p>
 *
 * @author zxh
 * @since 2025-10-21
 */
@Setter
@Getter
@ToString
public class WfNodeState implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 内部状态容器（委托模式）
     */
    private final OverAllState state;

    private String uuid = UuidUtil.createShort();
    private Integer processStatus = NODE_PROCESS_STATUS_READY;
    private String processStatusRemark = "";
    private List<NodeIOData> inputs = new ArrayList<>();
    private List<NodeIOData> outputs = new ArrayList<>();

    /**
     * 构造函数
     *
     * @param initData 初始数据
     */
    public WfNodeState(Map<String, Object> initData) {
        this.state = new OverAllState(initData);
    }

    public WfNodeState() {
        this.state = new OverAllState(Map.of());
    }

    /**
     * 委托方法：获取数据Map
     */
    public Map<String, Object> data() {
        return state.data();
    }

    /**
     * 委托方法：获取指定key的值
     */
    public <T> Optional<T> value(String key) {
        return state.value(key);
    }

    /**
     * 获取内部OverAllState实例
     */
    public OverAllState getOverAllState() {
        return state;
    }

    public Optional<NodeIOData> getDefaultInput() {
        return inputs.stream()
                .filter(item -> DEFAULT_INPUT_PARAM_NAME.equals(item.getName()))
                .findFirst();
    }
}
