package com.xinyirun.scm.ai.workflow.node.subworkflow;

import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowStarter;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_OUTPUT_PARAM_NAME;

/**
 * 子工作流节点
 * 功能：在当前工作流中调用其他工作流，实现工作流复用
 *
 * @author zxh
 * @since 2025-11-06
 */
@Slf4j
public class SubWorkflowNode extends AbstractWfNode {

    public SubWorkflowNode(AiWorkflowComponentEntity wfComponent,
                          AiWorkflowNodeVo node,
                          WfState wfState,
                          WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        log.info("开始执行子工作流节点: {}", node.getTitle());

        try {
            // 1. 解析子工作流配置
            SubWorkflowNodeConfig config = checkAndGetConfig(SubWorkflowNodeConfig.class);
            String subWorkflowUuid = config.getWorkflowUuid();

            if (subWorkflowUuid == null || subWorkflowUuid.isEmpty()) {
                throw new RuntimeException("子工作流未配置");
            }

            // 2. 检测循环依赖
            if (wfState.isInExecutionStack(subWorkflowUuid)) {
                throw new RuntimeException("检测到循环依赖: " +
                    wfState.getExecutionStack() + " → " + subWorkflowUuid);
            }

            // 3. 映射输入参数
            Map<String, Object> subInputs = mapInputs(config);

            // 4. 执行子工作流
            log.info("调用子工作流: {} ({})", config.getWorkflowName(), subWorkflowUuid);

            // 获取工作流启动器实例
            WorkflowStarter workflowStarter = SpringUtil.getBean(WorkflowStarter.class);

            // 同步执行子工作流（传递父conversationId以继承对话上下文）
            Map<String, Object> subOutputs = workflowStarter.runSync(
                subWorkflowUuid,
                convertToInputList(subInputs),
                wfState.getTenantCode(),
                wfState.getUserId(),
                wfState.getExecutionStack(),
                wfState.getConversationId()
            );

            // 5. 设置输出
            NodeIOData output = NodeIOData.createByText(
                DEFAULT_OUTPUT_PARAM_NAME,
                "",
                JSON.toJSONString(subOutputs)
            );

            log.info("子工作流节点执行完成: {}", node.getTitle());

            // 6. 返回结果
            return NodeProcessResult.builder().content(List.of(output)).build();

        } catch (Exception e) {
            log.error("子工作流节点执行失败: {}", node.getTitle(), e);
            throw new RuntimeException("子工作流执行失败: " + e.getMessage(), e);
        }
    }

    /**
     * 映射输入参数
     */
    private Map<String, Object> mapInputs(SubWorkflowNodeConfig config) {
        List<NodeIOData> parentInputs = state.getInputs();
        Map<String, Object> subInputs = new HashMap<>();

        // 将List<NodeIOData>转换为Map<String, Object>
        for (NodeIOData input : parentInputs) {
            subInputs.put(input.getName(), input.getContent().getValue());
        }

        List<SubWorkflowNodeConfig.InputMapping> mappings = config.getInputMapping();
        if (mappings != null && !mappings.isEmpty()) {
            // 如果配置了映射，进行参数映射
            Map<String, Object> mappedInputs = new HashMap<>();
            for (SubWorkflowNodeConfig.InputMapping mapping : mappings) {
                Object value = subInputs.get(mapping.getSourceKey());
                if (value != null) {
                    mappedInputs.put(mapping.getTargetKey(), value);
                }
            }
            log.debug("子工作流输入参数映射: parent={}, sub={}", subInputs, mappedInputs);
            return mappedInputs;
        } else {
            // 如果没有配置映射，直接传递所有输入
            log.debug("子工作流直接传递输入参数: {}", subInputs);
            return subInputs;
        }
    }

    /**
     * 转换为WorkflowEngine需要的输入格式
     * 需要构建符合 WfNodeIODataUtil.createNodeIOData 要求的格式
     */
    private List<JSONObject> convertToInputList(Map<String, Object> inputs) {
        List<JSONObject> inputList = new ArrayList<>();

        for (Map.Entry<String, Object> entry : inputs.entrySet()) {
            JSONObject inputItem = new JSONObject();
            inputItem.put("name", entry.getKey());

            JSONObject content = new JSONObject();
            content.put("type", 1);  // TEXT类型
            content.put("title", entry.getKey());
            content.put("value", entry.getValue());

            inputItem.put("content", content);

            inputList.add(inputItem);
        }

        return inputList;
    }
}
