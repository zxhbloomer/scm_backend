package com.xinyirun.scm.bpm.controller.react;

import com.alibaba.fastjson2.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xinyirun.scm.common.bpm.R;
import com.xinyirun.scm.core.bpm.utils.BpmnModelUtils;
import com.xinyirun.scm.core.bpm.utils.IdWorker;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.*;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.Deployment;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import static com.xinyirun.scm.core.bpm.utils.BpmnModelUtils.createExclusiveGateWayEnd;
import static org.flowable.bpmn.model.ImplementationType.IMPLEMENTATION_TYPE_CLASS;

/**
 * @author : lzgabel
 * @version : 1.0
 */
@RestController
@RequestMapping("/react")
//@Api(tags = {"React版本 的Json转Bpmn"})
//@ApiSort(4)
public class ReactJsonToBpmnController {

    @Resource
    private IdWorker idWorker;
    @Resource
    private RepositoryService repositoryService;
    @PostMapping("/jsonToBpmn")
    public Object saveForm(@RequestBody JSONObject jsonObject) throws InvocationTargetException, IllegalAccessException {
        System.err.println(jsonObject.toJSONString());
        BpmnModel bpmnModel =new BpmnModel();
        Process process=new Process();
        bpmnModel.addProcess(process);
        List<SequenceFlow> sequenceFlows = Lists.newArrayList();
        process.setId("flowableV5_"+idWorker.nextId());
        StartEvent startEvent = BpmnModelUtils.createStartEvent();
        process.addFlowElement(startEvent);
        String name = jsonObject.getJSONObject("workFlowDef").getString("name");
        process.setName(name);

        ExtensionAttribute extensionAttribute=new ExtensionAttribute();
        extensionAttribute.setName("DingDing");
        extensionAttribute.setNamespace("http://flowable.org/bpmn");
        extensionAttribute.setValue(jsonObject.toJSONString());
        process.addAttribute(extensionAttribute);
        JSONObject processNodes = jsonObject.getJSONObject("nodeConfig");
        String lastNode = create(startEvent.getId(), processNodes,bpmnModel,process,sequenceFlows);
        EndEvent endEvent = createEndEvent();
        process.addFlowElement(endEvent);
        process.addFlowElement(connect(lastNode, endEvent.getId(),sequenceFlows));

        new BpmnAutoLayout(bpmnModel).execute();
        Deployment deploy = repositoryService.createDeployment()
                .category("React")
                .name("React版本的流程图")
                .addBpmnModel("react.bpmn",bpmnModel)
                .deploy();
        System.err.println(deploy.getId());

        return R.ok("保存成功");
    }

    private static String create(String fromId, JSONObject flowNode,BpmnModel model,Process process,List<SequenceFlow> sequenceFlows) throws InvocationTargetException, IllegalAccessException {
        Integer nodeType = flowNode.getInteger("type");
        if (Type.PARALLEL.type.equals(nodeType)) {
            return createParallelGatewayBuilder(fromId, flowNode,model,process,sequenceFlows);
        }
        else if (Type.ROUTER.type.equals(nodeType)) {
            return createExclusiveGatewayBuilder(fromId, flowNode,model,process,sequenceFlows);
        }
        else if (Type.ROOT.type.equals(nodeType)) {
            flowNode.put("incoming", Collections.singletonList(fromId));
            String id = createTask(flowNode,process,sequenceFlows);

            // 如果当前任务还有后续任务，则遍历创建后续任务
            JSONObject nextNode = flowNode.getJSONObject("childNode");
            if (Objects.nonNull(nextNode)) {
                FlowElement flowElement = model.getFlowElement(id);
                return create(id, nextNode,model,process,sequenceFlows);
            } else {
                return id;
            }
        }
        else if (Type.CC.type.equals(nodeType)) {
            flowNode.put("incoming", Collections.singletonList(fromId));
            String id = createServiceTask(flowNode,process,sequenceFlows);

            // 如果当前任务还有后续任务，则遍历创建后续任务
            JSONObject nextNode = flowNode.getJSONObject("childNode");
            if (Objects.nonNull(nextNode)) {
                FlowElement flowElement = model.getFlowElement(id);
                return create(id, nextNode,model,process,sequenceFlows);
            } else {
                return id;
            }
        }
        else if (Type.USER_TASK.type.equals(nodeType)) {
            flowNode.put("incoming", Collections.singletonList(fromId));
            String id = createTask(flowNode,process,sequenceFlows);

            // 如果当前任务还有后续任务，则遍历创建后续任务
            JSONObject nextNode = flowNode.getJSONObject("childNode");
            if (Objects.nonNull(nextNode)) {
                FlowElement flowElement = model.getFlowElement(id);
                return create(id, nextNode,model,process,sequenceFlows);
            } else {
                return id;
            }
        }
        else {
            throw new RuntimeException("未知节点类型: nodeType=" + nodeType);
        }
    }





    private static String createExclusiveGatewayBuilder(String formId, JSONObject flowNode,BpmnModel model,Process process,List<SequenceFlow> sequenceFlows) throws InvocationTargetException, IllegalAccessException {
        String name = flowNode.getString("nodeName");
        String exclusiveGatewayId = id("exclusiveGateway");
        ExclusiveGateway exclusiveGateway = new ExclusiveGateway();
        exclusiveGateway.setId(exclusiveGatewayId);
        exclusiveGateway.setName(name);
        process.addFlowElement(exclusiveGateway);
        process.addFlowElement(connect(formId, exclusiveGatewayId,sequenceFlows));

        if (Objects.isNull(flowNode.getJSONArray("conditionNodes")) && Objects.isNull(flowNode.getJSONObject("childNode"))) {
            return exclusiveGatewayId;
        }
        List<JSONObject> flowNodes = Optional.ofNullable(flowNode.getJSONArray("conditionNodes")).map(e -> e.toJavaList(JSONObject.class)).orElse(Collections.emptyList());
        List<String> incoming = Lists.newArrayListWithCapacity(flowNodes.size());

        List<JSONObject> conditions = Lists.newCopyOnWriteArrayList();
        for (JSONObject element : flowNodes) {
            JSONObject childNode = element.getJSONObject("childNode");

            String nodeName = element.getString("nodeName");
            String expression = element.getString("conditionExpression");

            if (Objects.isNull(childNode)) {
                incoming.add(exclusiveGatewayId);
                JSONObject condition = new JSONObject();
                condition.fluentPut("nodeName", nodeName)
                        .fluentPut("expression", expression);
                conditions.add(condition);
                continue;
            }
            // 只生成一个任务，同时设置当前任务的条件
            childNode.put("incoming", Collections.singletonList(exclusiveGatewayId));
            String identifier = create(exclusiveGatewayId, childNode,model,process,sequenceFlows);
            List<SequenceFlow> flows = sequenceFlows.stream().filter(flow -> StringUtils.equals(exclusiveGatewayId, flow.getSourceRef()))
                    .collect(Collectors.toList());
            flows.stream().forEach(
                    e -> {
                        if (StringUtils.isBlank(e.getName()) && StringUtils.isNotBlank(nodeName)) {
                            e.setName(nodeName);
                        }
                        // 设置条件表达式
                        if (Objects.isNull(e.getConditionExpression()) && StringUtils.isNotBlank(expression)) {
                            e.setConditionExpression(expression);
                        }
                    }
            );
            if (Objects.nonNull(identifier)) {
                incoming.add(identifier);
            }
        }


        JSONObject childNode = flowNode.getJSONObject("childNode");

        if (Objects.nonNull(childNode)) {
            if (incoming == null || incoming.isEmpty()) {
                return create(exclusiveGatewayId, childNode,model,process,sequenceFlows);
            }
            else {
                // 所有 service task 连接 end exclusive gateway
                childNode.put("incoming", incoming);
                FlowElement flowElement = model.getFlowElement(incoming.get(0));
                // 1.0 先进行边连接, 暂存 nextNode
                JSONObject nextNode = childNode.getJSONObject("childNode");
                String endExId=id("exclusiveGateway")+"end";
                process.addFlowElement(createExclusiveGateWayEnd(endExId));
//                childNode.put("childNode", null);
                String identifier =endExId;/*create(flowElement.getId(), childNode,model,process,sequenceFlows);*/
                for (int i = 0; i < incoming.size(); i++) {
                    process.addFlowElement(connect(incoming.get(i), endExId,sequenceFlows));
                }

                //  针对 gateway 空任务分支 添加条件表达式
                if (!conditions.isEmpty()) {
                    FlowElement flowElement1 = model.getFlowElement(identifier);
                    // 获取从 gateway 到目标节点 未设置条件表达式的节点
                    List<SequenceFlow> flows = sequenceFlows.stream().filter(flow -> StringUtils.equals(flowElement1.getId(), flow.getTargetRef()))
                            .filter(flow -> StringUtils.equals(flow.getSourceRef(), exclusiveGatewayId))
                            .collect(Collectors.toList());
                    flows.stream().forEach(sequenceFlow -> {
                        if (!conditions.isEmpty()) {
                            JSONObject condition = conditions.get(0);
                            String nodeName = condition.getString("nodeName");
                            String expression = condition.getString("expression");

                            if (StringUtils.isBlank(sequenceFlow.getName()) && StringUtils.isNotBlank(nodeName)) {
                                sequenceFlow.setName(nodeName);
                            }
                            // 设置条件表达式
                            if (Objects.isNull(sequenceFlow.getConditionExpression()) && StringUtils.isNotBlank(expression)) {
                                sequenceFlow.setConditionExpression(expression);
                            }

                            conditions.remove(0);
                        }
                    });

                }

                // 1.1 边连接完成后，在进行 nextNode 创建
                if (Objects.nonNull(childNode)) {
                    return create(endExId, childNode,model,process,sequenceFlows);
                } else {
                    return endExId;
                }
            }
        }
        else{
            // 所有 service task 连接 end exclusive gateway
            // 1.0 先进行边连接, 暂存 nextNode
            String endExId=id("exclusiveGateway")+"end";
            process.addFlowElement(createExclusiveGateWayEnd(endExId));
            String identifier =endExId;/*create(flowElement.getId(), childNode,model,process,sequenceFlows);*/
            for (int i = 0; i < incoming.size(); i++) {
                process.addFlowElement(connect(incoming.get(i), endExId,sequenceFlows));
            }

            //  针对 gateway 空任务分支 添加条件表达式
            if (!conditions.isEmpty()) {
                FlowElement flowElement1 = model.getFlowElement(identifier);
                // 获取从 gateway 到目标节点 未设置条件表达式的节点
                List<SequenceFlow> flows = sequenceFlows.stream().filter(flow -> StringUtils.equals(flowElement1.getId(), flow.getTargetRef()))
                        .filter(flow -> StringUtils.equals(flow.getSourceRef(), exclusiveGatewayId))
                        .collect(Collectors.toList());
                flows.stream().forEach(sequenceFlow -> {
                    if (!conditions.isEmpty()) {
                        JSONObject condition = conditions.get(0);
                        String nodeName = condition.getString("nodeName");
                        String expression = condition.getString("expression");

                        if (StringUtils.isBlank(sequenceFlow.getName()) && StringUtils.isNotBlank(nodeName)) {
                            sequenceFlow.setName(nodeName);
                        }
                        // 设置条件表达式
                        if (Objects.isNull(sequenceFlow.getConditionExpression()) && StringUtils.isNotBlank(expression)) {
                            sequenceFlow.setConditionExpression(expression);
                        }

                        conditions.remove(0);
                    }
                });

            }
            return endExId;
        }
//        return exclusiveGatewayId;
    }

    private static String createParallelGatewayBuilder(String formId, JSONObject flowNode,BpmnModel model,Process process,List<SequenceFlow> sequenceFlows) throws InvocationTargetException, IllegalAccessException {
        String name = flowNode.getString("nodeName");
        ParallelGateway parallelGateway = new ParallelGateway();
        String parallelGatewayId = id("parallelGateway");
        parallelGateway.setId(parallelGatewayId);
        parallelGateway.setName(name);
        process.addFlowElement(parallelGateway);
        process.addFlowElement(connect(formId, parallelGatewayId,sequenceFlows));

        if (Objects.isNull(flowNode.getJSONArray("conditionNodes"))
                && Objects.isNull(flowNode.getJSONObject("childNode"))) {
            return parallelGatewayId;
        }

        List<JSONObject> flowNodes = Optional.ofNullable(flowNode.getJSONArray("conditionNodes")).map(e -> e.toJavaList(JSONObject.class)).orElse(Collections.emptyList());
        List<String> incoming = Lists.newArrayListWithCapacity(flowNodes.size());
        for (JSONObject element : flowNodes) {
            JSONObject childNode = element.getJSONObject("childNode");
            if (Objects.isNull(childNode)) {
                incoming.add(parallelGatewayId);
                continue;
            }
            String identifier = create(parallelGatewayId, childNode,model,process,sequenceFlows);
            if (Objects.nonNull(identifier)) {
                incoming.add(identifier);
            }
        }

        JSONObject childNode = flowNode.getJSONObject("childNode");
        if (Objects.nonNull(childNode)) {
            // 普通结束网关
            if (CollectionUtils.isEmpty(incoming)) {
                return create(parallelGatewayId, childNode,model,process,sequenceFlows);
            } else {
                // 所有 service task 连接 end parallel gateway
                childNode.put("incoming", incoming);
                FlowElement flowElement = model.getFlowElement(incoming.get(0));
                // 1.0 先进行边连接, 暂存 nextNode
                JSONObject nextNode = childNode.getJSONObject("childNode");
                childNode.put("childNode", null);
                String identifier = create(incoming.get(0), childNode,model,process,sequenceFlows);
                for (int i = 1; i < incoming.size(); i++) {
                    FlowElement flowElement1 = model.getFlowElement(incoming.get(i));
                    process.addFlowElement(connect(flowElement1.getId(), identifier,sequenceFlows));
                }
                // 1.1 边连接完成后，在进行 nextNode 创建
                if (Objects.nonNull(nextNode)) {
                    return create(identifier, nextNode,model,process,sequenceFlows);
                } else {
                    return identifier;
                }
            }
        }
        return parallelGatewayId;
    }

    private static String createServiceTask(JSONObject flowNode,Process process,List<SequenceFlow> sequenceFlows) {
        List<String> incoming = flowNode.getJSONArray("incoming").toJavaList(String.class);
        // 自动生成id
        String id = id("serviceTask");
        if (incoming != null && !incoming.isEmpty()) {
            ServiceTask serviceTask = new ServiceTask();
            serviceTask.setName(flowNode.getString("nodeName"));
            serviceTask.setId(id);
            serviceTask.setImplementationType(IMPLEMENTATION_TYPE_CLASS);
            serviceTask.setImplementation("com.dingding.mid.listener.ServiceListener");
            process.addFlowElement(serviceTask);
            process.addFlowElement(connect(incoming.get(0), id,sequenceFlows));
        }
        return id;
    }

    private static String createTask(JSONObject flowNode,Process process,List<SequenceFlow> sequenceFlows) {
        List<String> incoming = flowNode.getJSONArray("incoming").toJavaList(String.class);
        // 自动生成id
        String id = id("userTask");
        if (incoming != null && !incoming.isEmpty()) {
            UserTask userTask = new UserTask();
            userTask.setName(flowNode.getString("nodeName"));
            userTask.setId(id);
            process.addFlowElement(userTask);
            if(Type.ROOT.type.equals(flowNode.getInteger("type"))){

            }
            else{
                MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics=new MultiInstanceLoopCharacteristics();
                String examineMode = flowNode.getString("examineMode");
                // 审批人集合参数
                multiInstanceLoopCharacteristics.setInputDataItem(userTask.getId()+"assigneeList");
                // 迭代集合
                multiInstanceLoopCharacteristics.setElementVariable("assigneeName");
                // 并行
                multiInstanceLoopCharacteristics.setSequential(false);
                userTask.setAssignee("${assigneeName}");
                // 设置多实例属性
                userTask.setLoopCharacteristics(multiInstanceLoopCharacteristics);
                if("1".equals(examineMode)){
                    multiInstanceLoopCharacteristics.setSequential(true);
                }
                else if("3".equals(examineMode)){
                    multiInstanceLoopCharacteristics.setCompletionCondition("${nrOfCompletedInstances/nrOfInstances > 0}");
                }
            }
            process.addFlowElement(connect(incoming.get(0), id,sequenceFlows));
        }
        return id;
    }

    private enum Type {
//0 发起人 1审批 2抄送 3条件 4路由
        ROOT(0,UserTask.class),
        ROUTER(4, ExclusiveGateway.class),
        PARALLEL(5, ParallelGateway.class),

        /**
         * 排他事件
         */
//        EXCLUSIVE(4, ExclusiveGateway.class),

        /**
         * 任务
         */
        USER_TASK(1, UserTask.class),
        CC(2, ServiceTask.class);

        private Integer type;

        private Class<?> typeClass;

        Type(Integer type, Class<?> typeClass) {
            this.type = type;
            this.typeClass = typeClass;
        }

        public final static Map<Integer, Class<?>> TYPE_MAP = Maps.newHashMap();

        static {
            for (Type element : Type.values()) {
                TYPE_MAP.put(element.type, element.typeClass);
            }
        }

        public boolean isEqual(String type) {
            return this.type.equals(type);
        }

    }


    private static String id(String prefix) {
        return prefix + "_" + UUID.randomUUID().toString().replace("-", "").toLowerCase();
    }

    private static ServiceTask serviceTask(String name) {
        ServiceTask serviceTask = new ServiceTask();
        serviceTask.setName(name);
        return serviceTask;
    }

    protected static SequenceFlow connect(String from, String to,List<SequenceFlow> sequenceFlows) {
        SequenceFlow flow = new SequenceFlow();
        flow.setId(id("sequenceFlow"));
        flow.setSourceRef(from);
        flow.setTargetRef(to);
        sequenceFlows.add(flow);
        return flow;
    }

    protected static StartEvent createStartEvent() {
        StartEvent startEvent = new StartEvent();
        startEvent.setId(id("start"));
        return startEvent;
    }

    protected static EndEvent createEndEvent() {
        EndEvent endEvent = new EndEvent();
        endEvent.setId(id("end"));
        return endEvent;
    }


}



























































































































































































