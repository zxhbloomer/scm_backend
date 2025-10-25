package com.xinyirun.scm.ai.workflow.node.switcher;

import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
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

import java.util.List;

import static com.xinyirun.scm.ai.workflow.WfNodeIODataUtil.changeInputsToOutputs;

/**
 * 【节点】条件分支
 * 对齐AIDeepin: com.moyz.adi.common.workflow.node.switcher.SwitcherNode
 *
 * @author SCM-AI团队
 * @since 2025-10-23
 */
@Slf4j
public class SwitcherNode extends AbstractWfNode {

    public SwitcherNode(AiWorkflowComponentEntity wfComponent, AiWorkflowNodeEntity node, WfState wfState, WfNodeState nodeState) {
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
        String nextNode = nodeConfig.getDefaultTargetNodeUuid();
        if (StringUtils.isBlank(nextNode)) {
            log.error("Switcher default downstream is empty,node:{},name:{}", node.getUuid(), node.getTitle());
            throw new BusinessException("工作流节点配置错误");
        }
        //初始化配置的各种case
        for (SwitcherCase switcherCase : nodeConfig.getCases()) {
            List<SwitcherCase.Condition> conditions = switcherCase.getConditions();
            if (StringUtils.isAnyBlank(switcherCase.getTargetNodeUuid(), switcherCase.getOperator()) || CollectionUtils.isEmpty(conditions)) {
                log.warn("Switcher case error:{}", switcherCase);
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
                nextNode = switcherCase.getTargetNodeUuid();
                break;
            }
        }
        if (StringUtils.isBlank(nextNode)) {
            log.error("Switcher downstream is empty,node:{},name:{}", node.getUuid(), node.getTitle());
            throw new BusinessException("工作流节点配置错误");
        }
        return NodeProcessResult.builder().nextNodeUuid(nextNode).content(changeInputsToOutputs(state.getInputs())).build();
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
