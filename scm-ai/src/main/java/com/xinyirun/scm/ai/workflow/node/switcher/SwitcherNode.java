package com.xinyirun.scm.ai.workflow.node.switcher;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.utils.JsonUtil;
import com.xinyirun.scm.ai.workflow.NodeProcessResult;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.common.exception.system.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.xinyirun.scm.ai.workflow.WfNodeIODataUtil.changeInputsToOutputs;

/**
 * 工作流条件分支节点
 *
 * @author zxh
 * @since 2025-10-23
 */
@Slf4j
public class SwitcherNode extends AbstractWfNode {

    public SwitcherNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeVo node, WfState wfState, WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        // 使用 Fastjson2 的 JSONObject
        JSONObject nodeConfigObj = node.getNodeConfig();
        if (nodeConfigObj == null || nodeConfigObj.isEmpty()) {
            throw new BusinessException("工作流节点配置未找到");
        }
        SwitcherNodeConfig nodeConfig = nodeConfigObj.toJavaObject(SwitcherNodeConfig.class);
        if (null == nodeConfig || CollectionUtils.isEmpty(nodeConfig.getCases())) {
            log.warn("找不到条件分支节点的配置,nodeUuid:{},name:{}", node.getUuid(), node.getTitle());
            throw new BusinessException("工作流节点配置错误");
        }

        // 默认使用 default_handle，对应 default 分支的边
        String matchedSourceHandle = "default_handle";

        // 遍历所有case，找到第一个匹配的
        for (SwitcherCase switcherCase : nodeConfig.getCases()) {
            List<SwitcherCase.Condition> conditions = switcherCase.getConditions();
            // 只需要检查 case.uuid 和 operator，不再依赖 target_node_uuid
            if (StringUtils.isBlank(switcherCase.getUuid()) || StringUtils.isBlank(switcherCase.getOperator()) || CollectionUtils.isEmpty(conditions)) {
                log.warn("Switcher case error: uuid或operator为空, case={}", switcherCase);
                continue;
            }
            int conditionPassCount = 0;
            boolean casePass = false;
            boolean allConditionPassRequired = switcherCase.getOperator().equals(LogicOperatorEnum.AND.getName());
            for (SwitcherCase.Condition condition : switcherCase.getConditions()) {
                NodeIOData ioData = createByReferParam(condition.getNodeUuid(), condition.getNodeParamName());
                if (null == ioData || null == ioData.getContent()) {
                    log.warn("Switcher找不到引用的节点参数,nodeUuid:{},paramName:{}", condition.getNodeUuid(), condition.getNodeParamName());
                    continue;
                }
                String inputValue = ioData.valueToString().toLowerCase();
                String value = condition.getValue().toLowerCase();
                boolean conditionPass = processCondition(value, inputValue, condition.getOperator());
                if (conditionPass) {
                    conditionPassCount++;
                }
                if ((conditionPassCount == conditions.size()) || (conditionPassCount > 0 && !allConditionPassRequired)) {
                    casePass = true;
                    break;
                }
            }
            if (casePass) {
                // case 匹配成功，返回 case.uuid 作为 sourceHandle
                // 这个 uuid 与 edge 表中的 source_handle 对应
                matchedSourceHandle = switcherCase.getUuid();
                log.info("Switcher条件匹配成功, caseUuid={}, nodeUuid={}", matchedSourceHandle, node.getUuid());
                break;
            }
        }

        log.info("Switcher条件路由结果: matchedSourceHandle={}, nodeUuid={}, title={}",
                matchedSourceHandle, node.getUuid(), node.getTitle());

        // 构建输出：透传输入 + 追加匹配的分支名称（供 buildSummary 读取）
        List<NodeIOData> outputs = new ArrayList<>(changeInputsToOutputs(state.getInputs()));
        final String finalMatchedHandle = matchedSourceHandle;
        String caseName;
        if ("default_handle".equals(finalMatchedHandle)) {
            caseName = nodeConfig.getDefaultCaseName() != null && !nodeConfig.getDefaultCaseName().isEmpty()
                ? nodeConfig.getDefaultCaseName() : "默认分支";
        } else {
            caseName = nodeConfig.getCases().stream()
                .filter(c -> finalMatchedHandle.equals(c.getUuid()))
                .findFirst()
                .map(c -> c.getName() != null && !c.getName().isEmpty() ? c.getName() : "分支")
                .orElse("分支");
        }
        outputs.add(NodeIOData.createByText("matched_case_name", "匹配分支", caseName));
        return NodeProcessResult.builder()
                .nextSourceHandle(matchedSourceHandle)
                .content(outputs)
                .build();
    }

    private boolean processCondition(String defValue, String inputValue, String operator) {
        boolean conditionPass = false;
        switch (OperatorEnum.getByName(operator)) {
            case CONTAINS:
                conditionPass = StringUtils.isNotBlank(inputValue) && inputValue.contains(defValue);
                break;
            case NOT_CONTAINS:
                conditionPass = StringUtils.isNotBlank(inputValue) && !inputValue.contains(defValue);
                break;
            case START_WITH:
                conditionPass = StringUtils.isNotBlank(inputValue) && inputValue.startsWith(defValue);
                break;
            case END_WITH:
                conditionPass = StringUtils.isNotBlank(inputValue) && inputValue.endsWith(defValue);
                break;
            case EMPTY:
                conditionPass = StringUtils.isBlank(inputValue);
                break;
            case NOT_EMPTY:
                conditionPass = StringUtils.isNotBlank(inputValue);
                break;
            case EQUAL:
                conditionPass = StringUtils.isNotBlank(inputValue) && defValue.equals(inputValue);
                break;
            case NOT_EQUAL:
                conditionPass = StringUtils.isNotBlank(inputValue) && !defValue.equals(inputValue);
                break;
            case GREATER:
                try {
                    double in = Double.parseDouble(defValue);
                    double vl = Double.parseDouble(inputValue);
                    conditionPass = vl > in;
                } catch (Exception e) {
                    log.error("parse double error", e);
                }
                break;
            case GREATER_OR_EQUAL:
                try {
                    double in = Double.parseDouble(defValue);
                    double vl = Double.parseDouble(inputValue);
                    conditionPass = vl >= in;
                } catch (Exception e) {
                    log.error("parse double error", e);
                }
                break;
            case LESS:
                try {
                    double in = Double.parseDouble(defValue);
                    double vl = Double.parseDouble(inputValue);
                    conditionPass = vl < in;
                } catch (Exception e) {
                    log.error("parse double error", e);
                }
                break;
            case LESS_OR_EQUAL:
                try {
                    double in = Double.parseDouble(defValue);
                    double vl = Double.parseDouble(inputValue);
                    conditionPass = vl <= in;
                } catch (Exception e) {
                    log.error("parse double error", e);
                }
                break;
        }
        return conditionPass;
    }
}
