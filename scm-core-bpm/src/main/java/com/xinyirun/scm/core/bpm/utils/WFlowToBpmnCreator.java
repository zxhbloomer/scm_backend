package com.xinyirun.scm.core.bpm.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.bean.bpm.enums.ApprovalTypeEnum;
import com.xinyirun.scm.bean.bpm.vo.props.*;
import com.xinyirun.scm.bean.system.vo.business.bpm.BpmProcessNodeVo;
import com.xinyirun.scm.bean.bpm.enums.NodeTypeEnum;
import com.xinyirun.scm.core.bpm.config.WflowGlobalVarDef;
import com.xinyirun.scm.core.bpm.listener.CcListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * json -> bpmn核心转换器
 * @author : willian fu
 * @date : 2022/8/19
 */
@Slf4j
public class WFlowToBpmnCreator {

    //条件顺序流
    private final Map<String, SequenceFlow> sequenceFlowMap = new LinkedHashMap<>();
    //节点Map映射，提高取效率
    @Getter
    private final Map<String, BpmProcessNodeVo<?>> nodeMap = new LinkedHashMap<>();
    //节点列表
    private final List<BpmProcessNodeVo<?>> nodeList = new LinkedList<>();
    //分支栈
    private final Stack<BpmProcessNodeVo<?>> currentBranchStack = new Stack<>();
    //支路->该支路末端节点
    private final Map<String, List<String>> footerNode = new LinkedHashMap<>();
    //节点及顺序流元素组件
    private final List<FlowElement> elements = new ArrayList<>();
    //正常完成结束事件
    private final EndEvent endNode = new EndEvent();
    //被驳回的结束事件
    private final EndEvent refuseEndNode = new EndEvent();
    //取消流程的结束事件
    private final EndEvent cancelEndNode = new EndEvent();

    private final static List<FlowableListener> taskListeners = new ArrayList<>();
    static {
        FlowableListener taskListener = new FlowableListener();
        taskListener.setEvent("all");
        taskListener.setImplementationType("delegateExpression");
        taskListener.setImplementation("${userTaskListener}");
        taskListeners.add(taskListener);
    }

    /**
     * wflow -> bpmnModel 转换
     * @param id 表单流程模型id
     * @param name 流程名
     * @param root //根节点
     * @return 返回转换后的bpmnModel数据
     */
    public BpmnModel loadBpmnFlowXmlByProcess(String id, String name, BpmProcessNodeVo<?> root){
        StartEvent startEvent = new StartEvent();
        startEvent.setId("start");
        startEvent.setName("开始");
        loadProcess(root);
        loadProcessEndNode();
        //构建流程
        Process process = new Process();
        process.setId(id);
        process.setName(name);
        process.addFlowElement(startEvent);
        //将组件添加到流程节点
        elements.forEach(process::addFlowElement);
        //将结束事件连接到主干流程
        getMainLastNodes().forEach(ln -> {
            BpmProcessNodeVo<?> BpmProcessNodeVo = nodeMap.get(ln);
            //最后一个节点如果包含是有审批节点，就要处理一下
            if(NodeTypeEnum.APPROVAL.equals(BpmProcessNodeVo.getType())||NodeTypeEnum.TASK.equals(BpmProcessNodeVo.getType())){
                SequenceFlow pass = createdConnectLine(ln, endNode.getId());
                process.addFlowElement(pass);
            }else if(NodeTypeEnum.CONDITION.equals(BpmProcessNodeVo.getType())) {
                //如果是孤立的条件节点，就要直连它的网关
                SequenceFlow connectLine = createdConnectLine(BpmProcessNodeVo.getParentId(), endNode.getId());
                connectLine.setId(BpmProcessNodeVo.getId());
                process.addFlowElement(connectLine);
                //需要再给网关插入outgoing
                for (FlowElement element : elements) {
                    if (element.getId().equals(BpmProcessNodeVo.getParentId()) && element instanceof ExclusiveGateway){
                        //找到网关
                        ((ExclusiveGateway) element).getOutgoingFlows().add(connectLine);
                        break;
                    }
                }
            }else {
                process.addFlowElement(createdConnectLine(ln, endNode.getId()));
            }
        });
        process.addFlowElement(endNode);
        process.addFlowElement(cancelEndNode);
        process.addFlowElement(refuseEndNode);
        //构建Bpmn模型
        BpmnModel bpmnModel = new BpmnModel();
        bpmnModel.addProcess(process);

        // Bpmn xml自动生成布局及布局节点位置
        new BpmnAutoLayout(bpmnModel).execute();
        //log.info("构建审批流程[{}] 的 xml为：{}", name, xmlStr);
        return bpmnModel;
    }

    /**
     * 加载结束节点
     */
    private void loadProcessEndNode(){
        endNode.setId("process-end");
        endNode.setName("审批流程结束");
        cancelEndNode.setId("cancel-end");
        cancelEndNode.setName("审批流程撤消");
        refuseEndNode.setId("refuse-end");
        TerminateEventDefinition eventDefinition = new TerminateEventDefinition();
        eventDefinition.setTerminateAll(true);
        cancelEndNode.setEventDefinitions(CollectionUtil.newArrayList(eventDefinition));
        //强制终止流程
        TerminateEventDefinition eventDefinition2 = new TerminateEventDefinition();
        eventDefinition2.setTerminateAll(true);
        refuseEndNode.setEventDefinitions(CollectionUtil.newArrayList(eventDefinition2));
        refuseEndNode.setName("审批流程被驳回");
    }

    /**
     * 节点props属性强制转换
     * @param node 节点
     */
    public synchronized static void coverProps(BpmProcessNodeVo<?> node){
        if (node.getType().getTypeClass() != Object.class){
            node.setProps(((JSONObject)node.getProps()).toJavaObject((Type) node.getType().getTypeClass()));
        }
    }

    /**
     * 获取主流程末端最后的业务节点
     * @return 所有业务节点
     */
    private List<String> getMainLastNodes(){
        ArrayList<String> lastNodes = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(nodeList)){
            BpmProcessNodeVo<?> lastNode = nodeList.get(nodeList.size() - 1);
            if (NodeTypeEnum.EMPTY.equals(lastNode.getType()) && NodeTypeEnum.CONDITIONS.equals(lastNode.getParentType())){
                //分支，需要处理分支末端所有节点
                lastNodes.addAll(footerNode.get(lastNode.getParentId()));
            }else {
                lastNodes.add(lastNode.getId());
            }
        }
        return lastNodes;
    }

    /**
     * 判断并加载分支末端所有的节点
     * @param node 当前节点
     */
    private void loadBranchEndNodes(BpmProcessNodeVo<?> node){
        if (!hasChildren(node) && currentBranchStack.size() > 0){
            //没有后续节点，代表该分支部分结束，塞入末端缓存
            Optional.ofNullable(currentBranchStack.peek()).ifPresent(bn -> {
                List<String> endNodes = footerNode.get(bn.getId());
                if (CollectionUtil.isEmpty(endNodes)){
                    endNodes = new ArrayList<>();
                    footerNode.put(bn.getId(), endNodes);
                }
                if (NodeTypeEnum.EMPTY.equals(node.getType())){
                    currentBranchStack.pop();
                    if (currentBranchStack.size() > 0){
                        List<String> finalEndNodes = endNodes;
                        Optional.ofNullable(currentBranchStack.peek()).ifPresent(beforeBranch -> {
                            List<String> bfed = footerNode.get(beforeBranch.getId());
                            if (CollectionUtil.isEmpty(bfed)){
                                bfed = new ArrayList<>();
                                footerNode.put(beforeBranch.getId(), bfed);
                            }
                            if (NodeTypeEnum.CONDITIONS.equals(node.getParentType())){
                                bfed.addAll(finalEndNodes);
                            }else {
                                bfed.add(node.getId());
                            }
                        });
                    }
                    currentBranchStack.push(bn);
                }else {
                    endNodes.add(node.getId());
                    //末端如果是审批节点，默认添加一条驳回的链接线
                }
            });
        }
    }

    /**
     * 校验是否存在后续子节点
     * @param node 当前节点
     * @return 1/0
     */
    private boolean hasChildren(BpmProcessNodeVo<?> node) {
        return ObjectUtil.isNotNull(node.getChildren()) && StrUtil.isNotBlank(node.getChildren().getId());
    }

    /**
     * 递归加载所有流程树
     * @param node 起始节点
     */
    public void loadProcess(BpmProcessNodeVo<?> node){
        if (Objects.isNull(node.getId())){
            return;
        }
        loadBranchEndNodes(node);
        coverProps(node);
        nodeMap.put(node.getId(), node);
        nodeList.add(node);
        switch (node.getType()){
            case ROOT:
                UserTask initiatorNode = createInitiatorNode(node.getId(), node.getName());
                elements.add(initiatorNode);
                break;
            case APPROVAL:
            case TASK:
                UserTask userTask = createApprovalNode((BpmProcessNodeVo<ApprovalProps>) node);
                elements.add(userTask);
                break;
            case CC:
                elements.add(createCcTask((BpmProcessNodeVo<CcProps>) node));
                break;
//            case DELAY:
//                elements.add(createDelayNode((BpmProcessNodeVo<DelayProps>) node));
//                break;
            case CONDITIONS:
                currentBranchStack.push(node);
                ExclusiveGateway conditionsNode = createConditionsNode(node);
                elements.add(conditionsNode);
                node.getBranchs().forEach(this::loadProcess);
                List<BpmProcessNodeVo<?>> collect = node.getBranchs().stream().filter(n ->
                        CollectionUtil.isEmpty(((ConditionProps) n.getProps()).getGroups()))
                        .collect(Collectors.toList());
                List<SequenceFlow> outgoings = new ArrayList<>();
                node.getBranchs().forEach(c -> Optional.ofNullable(sequenceFlowMap.get(c.getId())).ifPresent(outgoings::add));
                conditionsNode.setOutgoingFlows(outgoings);
                if (CollectionUtil.isNotEmpty(collect)){
                    conditionsNode.setDefaultFlow(collect.get(0).getId());
                }
                break;
            case CONCURRENTS:
                currentBranchStack.push(node);
                ParallelGateway concurrentNode = createConcurrentNode(node);
                elements.add(concurrentNode);
                node.getBranchs().forEach(this::loadProcess);
                break;
//            case TRIGGER:
//                elements.add(createTriggerTask((BpmProcessNodeVo<TriggerProps>) node));
//                break;
            case EMPTY:
                if (NodeTypeEnum.CONCURRENTS.equals(node.getParentType())){
                    //并行网关成对存在，因此再添加一个聚合节点
                    ParallelGateway collectNode = createConcurrentNode(node);
                    elements.add(collectNode);
                }
                break;
        }
        addAndCreateConnLine(node);
        loadProcess(node.getChildren());
    }

    //发起人节点
    private UserTask createInitiatorNode(String id, String name){
        UserTask userTask = new UserTask();
        userTask.setId(id);
        userTask.setName(name);
        userTask.setAssignee("${" + WflowGlobalVarDef.INITIATOR + "}");
        return userTask;
    }

    //触发器任务
//    private FlowElement createTriggerTask(BpmProcessNodeVo<TriggerProps> node) {
//        ServiceTask ccTask = new ServiceTask();
//        ccTask.setId(node.getId());
//        ccTask.setName(node.getName());
//        ccTask.setImplementationType("class");
//        ccTask.setImplementation(TriggerServiceTask.class.getName());
//        return ccTask;
//    }

    //将当前节点向上连接，向上查找应该连接到本节点的所有父节点
    private void addAndCreateConnLine(BpmProcessNodeVo<?> node){
        if (NodeTypeEnum.EMPTY.equals(node.getType())){
            //空节点代表一个分支结束，缓存出栈
            BpmProcessNodeVo<?> branch = currentBranchStack.pop();
            //判断是并行网关还是条件网关
            if (NodeTypeEnum.CONCURRENTS.equals(node.getParentType())){
                footerNode.get(branch.getId()).forEach(en -> {
                    SequenceFlow connectLine = createdConnectLine(en, node.getId());
                    if (NodeTypeEnum.APPROVAL.equals(nodeMap.get(en).getType())||NodeTypeEnum.TASK.equals(nodeMap.get(en).getType())){
                        connectLine.setName("同意");
                    }
                    elements.add(connectLine);
                });
            }
        }else if (Objects.nonNull(node.getParentId())){
            //非空节点，特殊处理，判断父级是啥子
            BpmProcessNodeVo<?> parentNode = nodeMap.get(node.getParentId());
            if (Objects.isNull(parentNode)){
                return;
            }
            switch (parentNode.getType()){
                case EMPTY:
                    //父级节点是空节点，判断聚合节点类型
                    if (NodeTypeEnum.CONDITIONS.equals(parentNode.getParentType())){
                        //如果本节点是并行网关，直连了条件网关空节点，那么特殊处理
                        BpmProcessNodeVo<?> cNode = NodeTypeEnum.CONCURRENTS.equals(node.getType()) ? parentNode : node;
                        //条件节点特殊处理，直接连接到所有条件分支末端
                        footerNode.get(parentNode.getParentId()).forEach(en -> {
                            SequenceFlow connectLine = null;
                            BpmProcessNodeVo<?> BpmProcessNodeVo = nodeMap.get(en);
                            if (NodeTypeEnum.APPROVAL.equals(BpmProcessNodeVo.getType())||NodeTypeEnum.TASK.equals(BpmProcessNodeVo.getType())){
                                connectLine = createdConnectLine(en, cNode.getId());
                                connectLine.setName("同意");
                            }else if (NodeTypeEnum.CONDITION.equals(BpmProcessNodeVo.getType())){
                                //空条件分支，直接连网关
                                connectLine = createdConnectLine(BpmProcessNodeVo.getParentId(), cNode.getId());
                                connectLine.setId(en);
                                connectLine.setName("默认分支");
                                SequenceFlow finalConnectLine = connectLine;
                                elements.stream().filter(el -> BpmProcessNodeVo.getParentId().equals(el.getId())).findFirst().ifPresent(el -> {
                                    if (el instanceof ExclusiveGateway){
                                        ((ExclusiveGateway) el).getOutgoingFlows().add(finalConnectLine);
                                    }
                                });
                            }else {
                                connectLine = createdConnectLine(en, cNode.getId());
                            }
                            elements.add(connectLine);
                        });
                        if (NodeTypeEnum.CONCURRENTS.equals(node.getType())){
                            //如果条件网关下面直连并行网关，那么条件网关需要成对
                            ExclusiveGateway exclusiveGateway = createConditionsNode(parentNode);
                            elements.add(exclusiveGateway);
                            //把条件网关合流连接到并行网关
                            SequenceFlow line = createdConnectLine(parentNode.getId(), node.getId());
                            exclusiveGateway.setOutgoingFlows(CollectionUtil.newArrayList(line));
                            elements.add(line);
                        }
                    }else {
                        elements.add(createdConnectLine(parentNode.getId(), node.getId()));
                    }
                    break;
                case CONCURRENT:
                    //并行分支，直接连接到并行网关
                    SequenceFlow cuLine = createdConnectLine(parentNode.getParentId(), node.getId());
                    cuLine.setName(parentNode.getName());
                    elements.add(cuLine);
                    break;
                case CONDITION:
                    //条件分支，构造条件并连接到条件网关
                    SequenceFlow cdLine = createdConnectLine(parentNode.getParentId(), node.getId());
                    cdLine.setId(parentNode.getId());
                    sequenceFlowMap.put(cdLine.getId(), cdLine);
                    cdLine.setName(parentNode.getName());
                    String conditionExplainCreator = conditionExplainCreator(cdLine.getId(), (ConditionProps) parentNode.getProps());
                    if (StrUtil.isNotBlank(conditionExplainCreator)){
                        cdLine.setConditionExpression(conditionExplainCreator);
                    }
                    elements.add(cdLine);
                    break;
                case APPROVAL:
                case TASK:
                    //父级节点是审批节点，要构造条件顺序流
                    SequenceFlow passLine = createdConnectLine(parentNode.getId(), node.getId());
                    passLine.setName("同意");
                    elements.add(passLine);
                    break;
                case DELAY:
                case CC:
                case ROOT:
                case TRIGGER:
                    elements.add(createdConnectLine(parentNode.getId(), node.getId()));
                    break;
            }
        }else if (NodeTypeEnum.ROOT.equals(node.getType())){
            //发起人节点链接到开始节点
            elements.add(createdConnectLine("start", node.getId()));
        }
    }

    //审批-用户任务
    private UserTask createApprovalNode(BpmProcessNodeVo<ApprovalProps> node) {
        UserTask userTask = new UserTask();
        userTask.setName(node.getName());
        ApprovalProps props = node.getProps();
        //全部按多人审批处理
        userTask.setTaskListeners(taskListeners);
        if(ApprovalTypeEnum.SELF.equals(props.getAssignedType())){
            //发起人自己审批
            userTask.setAssignee("${" + WflowGlobalVarDef.INITIATOR + "}");
        }else {
            userTask.setAssignee("${assignee}");
            userTask.setLoopCharacteristics(createAndOrMode(node.getId(), props));
        }
        userTask.setId(node.getId());
        //处理审批超时，添加定时器边界事件
        /*ApprovalProps.TimeLimit timeLimit = props.getTimeLimit();
        if (Objects.nonNull(timeLimit.getTimeout().getValue()) && timeLimit.getTimeout().getValue() > 0){
            BoundaryEvent boundaryEvent = new BoundaryEvent();
            boundaryEvent.setId(node.getId() + "-timeout");
            boundaryEvent.setName("审批超时");
            TimerEventDefinition timerEventDefinition = new TimerEventDefinition();
            String timeValue = getISO8601Time(timeLimit.getTimeout().getValue(), timeLimit.getTimeout().getUnit());
            timerEventDefinition.setTimeDuration(timeValue);
            if (ApprovalProps.TimeLimit.Handler.HandlerType.NOTIFY.equals(timeLimit.getHandler().getType())){
                //根据是否循环提醒来修改定时规则
                ApprovalProps.TimeLimit.Handler.Notify notify = timeLimit.getHandler().getNotify();
                if (!notify.isOnce()){
                    timerEventDefinition.setTimeDuration(null);
                    //默认最大10次，这里参考 https://en.wikipedia.org/wiki/ISO_8601#Repeating_intervals
                    timerEventDefinition.setTimeCycle("R10/" + timeValue);
                }
            }
            boundaryEvent.addEventDefinition(timerEventDefinition);
            boundaryEvent.setCancelActivity(false);
            boundaryEvent.setAttachedToRef(userTask);
            //创建边界事件的出口
            ServiceTask timeoutTask = new ServiceTask();
            timeoutTask.setId(node.getId() + "-timeoutTask");
            timeoutTask.setName(node.getName() + "超时处理");
            timeoutTask.setImplementationType("class");
            timeoutTask.setImplementation(ApprovalTimeoutServiceTask.class.getName());
            elements.add(boundaryEvent);
            elements.add(timeoutTask);
            elements.add(createdConnectLine(boundaryEvent.getId(), timeoutTask.getId()));
        }*/
        return userTask;
    }

    //并行块节点-并行网关
    private ParallelGateway createConcurrentNode(BpmProcessNodeVo<?> node) {
        ParallelGateway parallelGateway = new ParallelGateway();
        parallelGateway.setId(node.getId());
        parallelGateway.setName(NodeTypeEnum.EMPTY.equals(node.getType()) ? "并行分支聚合":"并行分支");
        return parallelGateway;
    }

    //抄送任务
    private ServiceTask createCcTask(BpmProcessNodeVo<CcProps> node) {
        ServiceTask ccTask = new ServiceTask();
        ccTask.setId(node.getId());
        ccTask.setName(node.getName());
        ccTask.setImplementationType("class");
        ccTask.setImplementation(CcListener.class.getName());
        //ccTask.addAttribute();
        return ccTask;
    }

    //构建延时节点
//    private IntermediateCatchEvent createDelayNode(BpmProcessNodeVo<DelayProps> node) {
//        IntermediateCatchEvent catchEvent = new IntermediateCatchEvent();
//        TimerEventDefinition timerDefinition = new TimerEventDefinition();
//        DelayProps props = node.getProps();
//        if (props.getType().equals(DelayProps.Type.FIXED)) {
//            timerDefinition.setTimeDuration(getISO8601Time(props.getTime(), props.getUnit()));
//        } else {
//            //动态计算时长
//            timerDefinition.setTimeDate("${uelTools.getDelayDuration(execution)}");
//        }
//        catchEvent.setId(node.getId());
//        catchEvent.setName(node.getName());
//        //插入定时器捕获中间事件
//        catchEvent.addEventDefinition(timerDefinition);
//        return catchEvent;
//    }

    //条件块节点-排他网关
    private ExclusiveGateway createConditionsNode(BpmProcessNodeVo<?> node) {
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setExclusive(true);
        exclusiveGateway.setId(node.getId());
        exclusiveGateway.setName("条件分支");
        return exclusiveGateway;
    }

    private SequenceFlow createdConnectLine(String source, String target) {
        SequenceFlow flow = new SequenceFlow();
        flow.setId(source + "_" + target);
        flow.setSourceRef(source);
        flow.setTargetRef(target);
        return flow;
    }

    //构建条件表达式
    private String conditionExplainCreator(String nodeId, ConditionProps conditions) {
        if (CollectionUtil.isNotEmpty(conditions.getGroups())){
            return "${uelTools.conditionCompare('"+ nodeId + "', execution)}";
        }
        return null;
    }

    //多人签署设置-会签/或签
    private MultiInstanceLoopCharacteristics createAndOrMode(String nodeId, ApprovalProps props) {
        MultiInstanceLoopCharacteristics loopCharacteristics = new MultiInstanceLoopCharacteristics();
        loopCharacteristics.setId(IdUtil.randomUUID());
        loopCharacteristics.setElementVariable("assignee");
        loopCharacteristics.setInputDataItem("${iBpmProcessTemplatesService.getNodeApprovalUsers(execution)}");
        //设置完成条件，先判断会签还是或签
        String completionCondition = "";
        switch (props.getMode()) {
            case OR: //有任意一个人处理过就结束
                completionCondition = "nrOfCompletedInstances >= 1";
                loopCharacteristics.setSequential(false);
                break;
            case AND: //所有任务都结束
                completionCondition = "nrOfActiveInstances == 0";
                loopCharacteristics.setSequential(false);
                break;
            case NEXT:
                completionCondition = "nrOfActiveInstances == 0";
                loopCharacteristics.setSequential(true);
                break;
        }
        loopCharacteristics.setCompletionCondition("${" + completionCondition + "}");
        return loopCharacteristics;
    }

    /**
     * 获取ISO8601时间
     * @param time 值
     * @param unit 单位
     * @return 格式化时间
     */
    private String getISO8601Time(Integer time, String unit){
        switch (unit){
            case "D": return "P" + time + unit;
            case "H": return "PT" + time + unit;
            case "M": return "PT" + time + unit;
        }
        return null;
    }

}
