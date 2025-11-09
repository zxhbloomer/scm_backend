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

import static com.xinyirun.scm.ai.workflow.WorkflowConstants.DEFAULT_INPUT_PARAM_NAME;
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

            // 日志：子工作流调用参数
            log.debug("子工作流节点调用参数 - subWorkflowUuid: {}, subWorkflowName: {}, parentConversationId: {}, tenantCode: {}, userId: {}",
                    subWorkflowUuid, config.getWorkflowName(), wfState.getConversationId(), wfState.getTenantCode(), wfState.getUserId());
            log.debug("子工作流输入参数: {}", subInputs);

            // 4. 执行子工作流
            log.info("调用子工作流: {} ({})", config.getWorkflowName(), subWorkflowUuid);

            // 获取工作流启动器实例
            WorkflowStarter workflowStarter = SpringUtil.getBean(WorkflowStarter.class);

            // 同步执行子工作流（传递父conversationId以继承对话上下文，传递父runtime_uuid避免创建新记录，传递父StreamHandler以转发流式事件）
            Map<String, Object> subOutputs = workflowStarter.runSync(
                subWorkflowUuid,
                convertToInputList(subInputs),
                wfState.getTenantCode(),
                wfState.getUserId(),
                wfState.getExecutionStack(),
                wfState.getConversationId(),
                wfState.getUuid(),  // 传递父runtime_uuid，子工作流将复用此UUID
                wfState.getStreamHandler()  // 传递父StreamHandler，子工作流的流式事件将实时转发到前端
            );

            // 日志：子工作流执行结果
            log.debug("子工作流执行完成 - subWorkflowUuid: {}, outputs: {}", subWorkflowUuid, subOutputs);

            // 5. 从子工作流输出中提取实际内容
            // 子工作流返回格式: {output={title="",type=1,value="实际回答内容"}}
            // 需要提取output.value的值，而不是整个Map的JSON字符串
            String outputValue = "";
            Object outputObj = subOutputs.get(DEFAULT_OUTPUT_PARAM_NAME);
            if (outputObj instanceof Map) {
                Map<?, ?> outputMap = (Map<?, ?>) outputObj;
                Object valueObj = outputMap.get("value");
                if (valueObj != null) {
                    outputValue = valueObj.toString();
                }
                log.debug("从子工作流输出中提取实际内容: {}", outputValue.length() > 100 ? outputValue.substring(0, 100) + "..." : outputValue);
            } else {
                log.warn("子工作流输出格式异常，预期output是Map类型，实际: {}", outputObj != null ? outputObj.getClass().getName() : "null");
            }

            // 6. 设置输出
            NodeIOData output = NodeIOData.createByText(
                DEFAULT_OUTPUT_PARAM_NAME,
                "",
                outputValue
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
     * 映射输入参数（自动传递模式）
     *
     * 设计理念：
     * 1. 父工作流开始节点的所有参数自动传递给子工作流
     * 2. 上一节点的默认输出作为input参数传递
     * 3. 不依赖手工配置的InputMapping
     *
     * 参数来源：
     * - wfState.getInput(): 父工作流开始节点的参数（如var_user_input）
     * - state.getInputs(): 当前节点（SubWorkflowNode）的输入（包含上一节点的输出）
     */
    private Map<String, Object> mapInputs(SubWorkflowNodeConfig config) {
        Map<String, Object> subInputs = new HashMap<>();

        // 第一步：传递父工作流开始节点的所有参数
        if (wfState.getInput() != null && !wfState.getInput().isEmpty()) {
            for (NodeIOData parentStartInput : wfState.getInput()) {
                subInputs.put(parentStartInput.getName(), parentStartInput.getContent().getValue());
            }
            log.debug("传递父工作流开始节点参数: {}", subInputs);
        }

        // 第二步：添加上一个节点的默认输出作为input参数
        List<NodeIOData> currentNodeInputs = state.getInputs();
        for (NodeIOData input : currentNodeInputs) {
            // 查找默认输入参数（名为"input"的参数）
            if (DEFAULT_INPUT_PARAM_NAME.equals(input.getName())) {
                subInputs.put(DEFAULT_INPUT_PARAM_NAME, input.getContent().getValue());
                log.debug("添加上一节点默认输出作为input参数: {}", input.getContent().getValue());
                break;
            }
        }

        log.info("子工作流最终输入参数: {}", subInputs);
        return subInputs;
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
