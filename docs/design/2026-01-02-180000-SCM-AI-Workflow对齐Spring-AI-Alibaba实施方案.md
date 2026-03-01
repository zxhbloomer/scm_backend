# SCM AI Workflow对齐Spring AI Alibaba实施方案

## 一、问题背景

### 1.1 当前状态
SCM AI的WorkflowEngine.runNode方法（约200行）存在以下问题：
1. **虚拟节点代码已失效**：compile时报错`parallel node [virtual_parallel_...] must have only one target`
2. **日志记录机制不符合框架设计**：手动实现80行嵌入式回调，而非使用GraphLifecycleListener
3. **特殊情况过多**：多处组件类型判断散落在引擎层

### 1.2 对齐目标
完全参考Spring AI Alibaba框架逻辑，不考虑向后兼容性：
1. 删除虚拟节点代码
2. 使用GraphLifecycleListener替代嵌入式回调
3. 简化runNode方法

---

## 二、核心差异分析

### 2.1 节点执行流程对比

**Spring AI Alibaba NodeExecutor（~50行核心逻辑）**：
```java
// 行112 - 执行前自动调用监听器
context.doListeners(NODE_BEFORE, null);

// 行114-115 - 执行节点
CompletableFuture<Map<String, Object>> future = action.apply(
    context.getOverallState(), context.getConfig());

// 行171 - 执行后自动调用监听器
context.doListeners(NODE_AFTER, null);

// 行120 - 错误时调用监听器
context.doListeners(ERROR, new Exception(error));
```

**SCM AI runNode（~200行）**：
```java
// 行426-461 - 虚拟节点特殊处理（35行废代码）
if (isVirtualNode) { ... }

// 行483-498 - 手动创建运行时记录（16行）
AiWorkflowRuntimeNodeVo nodeVo = workflowRuntimeNodeService.createByState(...);

// 行501-580 - 嵌入式回调（80行）
NodeProcessResult processResult = abstractWfNode.process(
    (is) -> { /* 输入回调 30行 */ },
    (is) -> { /* 输出回调 50行 */ }
);
```

### 2.2 差异总结

| 维度 | Spring AI Alibaba | SCM AI当前 | 问题 |
|------|-------------------|-----------|------|
| 监听器调用 | 框架自动 | 应用层手动 | 违反框架设计 |
| 代码行数 | ~50行 | ~200行 | 4倍复杂度 |
| 虚拟节点 | 无 | 35行 | 废代码 |
| 回调机制 | GraphLifecycleListener | 函数式回调 | 不可扩展 |
| 错误处理 | 统一onErrorResume | try-catch整体包裹 | 粒度粗 |

---

## 三、实施方案

### 3.1 第一步：删除虚拟节点代码

#### 3.1.1 WorkflowConstants.java
**删除**：
```java
// 行29-37
public static final String VIRTUAL_PARALLEL_NODE_PREFIX = "virtual_parallel_";
public static final String COMPONENT_NAME_PARALLEL_DISPATCH = "ParallelDispatch";
```

#### 3.1.2 WorkflowEngine.java

**删除createParallelDispatchNode方法**（如存在）

**删除runNode中的虚拟节点处理**（行426-461）：
```java
// 删除以下代码块
boolean isVirtualNode = wfNode.getUuid().startsWith(VIRTUAL_PARALLEL_NODE_PREFIX);
if (isVirtualNode) {
    // ... 35行虚拟节点处理逻辑
}
```

**简化processConditionalEdges**：
```java
// 原逻辑：多目标时创建虚拟节点
if (uniqueTargets.size() == 1) {
    mappings.put(sourceHandle, targetUuid);
} else {
    String virtualNodeUuid = createParallelDispatchNode(...);  // 删除
    mappings.put(sourceHandle, virtualNodeUuid);
}

// 新逻辑：接受框架限制，一个条件一个目标
String targetUuid = uniqueTargets.iterator().next();
mappings.put(sourceHandle, targetUuid);
if (uniqueTargets.size() > 1) {
    log.warn("[processConditionalEdges] sourceHandle {} 配置了多个目标，已选择第一个: {}，其他被忽略: {}",
            sourceHandle, targetUuid, uniqueTargets);
}
```

#### 3.1.3 删除ParallelDispatchNode.java（如存在）

#### 3.1.4 WfNodeFactory.java
**删除**：
```java
} else if (COMPONENT_NAME_PARALLEL_DISPATCH.equals(componentName)) {
    wfNode = new ParallelDispatchNode(...);
}
```

---

### 3.2 第二步：实现GraphLifecycleListener

#### 3.2.1 新建ScmWorkflowLifecycleListener.java

**文件路径**：`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/listener/ScmWorkflowLifecycleListener.java`

```java
package com.xinyirun.scm.ai.workflow.listener;

import com.alibaba.cloud.ai.graph.GraphLifecycleListener;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo;
import com.xinyirun.scm.ai.core.service.workflow.WorkflowRuntimeNodeService;
import com.xinyirun.scm.ai.core.service.workflow.ConversationRuntimeNodeService;
import com.xinyirun.scm.ai.workflow.WfNodeState;
import com.xinyirun.scm.ai.workflow.WfState;
import com.xinyirun.scm.ai.workflow.WorkflowCallSource;
import com.xinyirun.scm.ai.workflow.handler.StreamHandler;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.common.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ScmWorkflowLifecycleListener implements GraphLifecycleListener {

    private final WorkflowRuntimeNodeService workflowRuntimeNodeService;
    private final ConversationRuntimeNodeService conversationRuntimeNodeService;
    private final StreamHandler streamHandler;
    private final WfState wfState;
    private final WorkflowCallSource callSource;
    private final Long userId;
    private final String tenantCode;
    private final Long runtimeId;

    // 存储nodeId -> runtimeNodeId的映射
    private final Map<String, Long> nodeRuntimeIds = new ConcurrentHashMap<>();
    // 存储nodeId -> nodeState的映射（用于获取输入输出）
    private final Map<String, WfNodeState> nodeStates = new ConcurrentHashMap<>();
    // 存储nodeId -> componentName的映射（用于判断是否跳过事件）
    private final Map<String, String> nodeComponents = new ConcurrentHashMap<>();
    // 存储nodeId -> showProcessOutput的映射
    private final Map<String, Boolean> nodeShowProcessOutput = new ConcurrentHashMap<>();

    public ScmWorkflowLifecycleListener(
            WorkflowRuntimeNodeService workflowRuntimeNodeService,
            ConversationRuntimeNodeService conversationRuntimeNodeService,
            StreamHandler streamHandler,
            WfState wfState,
            WorkflowCallSource callSource,
            Long userId,
            String tenantCode,
            Long runtimeId) {
        this.workflowRuntimeNodeService = workflowRuntimeNodeService;
        this.conversationRuntimeNodeService = conversationRuntimeNodeService;
        this.streamHandler = streamHandler;
        this.wfState = wfState;
        this.callSource = callSource;
        this.userId = userId;
        this.tenantCode = tenantCode;
        this.runtimeId = runtimeId;
    }

    /**
     * 注册节点状态（在节点执行前由runNode调用）
     */
    public void registerNodeState(String nodeId, WfNodeState nodeState,
                                   String componentName, boolean showProcessOutput) {
        nodeStates.put(nodeId, nodeState);
        nodeComponents.put(nodeId, componentName);
        nodeShowProcessOutput.put(nodeId, showProcessOutput);
    }

    @Override
    public void before(String nodeId, Map<String, Object> state, RunnableConfig config, Long curTime) {
        DataSourceHelper.use(this.tenantCode);

        WfNodeState nodeState = nodeStates.get(nodeId);
        if (nodeState == null) {
            log.warn("[LifecycleListener.before] 未找到nodeState: {}", nodeId);
            return;
        }

        // 1. 创建运行时节点记录
        Long runtimeNodeId;
        if (callSource == WorkflowCallSource.AI_CHAT) {
            var nodeVo = conversationRuntimeNodeService.createByState(
                    runtimeId, nodeState, nodeState.getNodeId(), userId);
            runtimeNodeId = nodeVo.getId();
            streamHandler.sendNodeRun(nodeId, JSONObject.toJSONString(nodeVo));
        } else {
            var nodeVo = workflowRuntimeNodeService.createByState(
                    userId, nodeState.getNodeId(), runtimeId, nodeState);
            wfState.getRuntimeNodes().add(nodeVo);
            runtimeNodeId = nodeVo.getId();
            streamHandler.sendNodeRun(nodeId, JSONObject.toJSONString(nodeVo));
        }
        nodeRuntimeIds.put(nodeId, runtimeNodeId);

        // 2. 更新输入并发送INPUT事件
        if (callSource == WorkflowCallSource.AI_CHAT) {
            conversationRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
        } else {
            workflowRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
        }

        // 3. 发送NODE_INPUT事件（跳过特定组件）
        String componentName = nodeComponents.get(nodeId);
        if (!shouldSkipInputEvent(componentName)) {
            for (NodeIOData input : nodeState.getInputs()) {
                streamHandler.sendNodeInput(nodeId, JSONObject.toJSONString(input));
            }
        } else {
            log.info("[LifecycleListener.before] 跳过NODE_INPUT - componentName: {}", componentName);
        }
    }

    @Override
    public void after(String nodeId, Map<String, Object> state, RunnableConfig config, Long curTime) {
        DataSourceHelper.use(this.tenantCode);

        Long runtimeNodeId = nodeRuntimeIds.remove(nodeId);
        WfNodeState nodeState = nodeStates.remove(nodeId);
        String componentName = nodeComponents.remove(nodeId);
        Boolean showProcessOutput = nodeShowProcessOutput.remove(nodeId);

        if (runtimeNodeId == null || nodeState == null) {
            log.warn("[LifecycleListener.after] 未找到运行时信息: {}", nodeId);
            return;
        }

        // 1. 更新输出
        if (callSource == WorkflowCallSource.AI_CHAT) {
            conversationRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
        } else {
            workflowRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
        }

        // 2. 发送NODE_OUTPUT事件（跳过特定组件和静默模式）
        if (!shouldSkipOutputEvent(componentName) && Boolean.TRUE.equals(showProcessOutput)) {
            List<NodeIOData> nodeOutputs = nodeState.getOutputs();
            for (NodeIOData output : nodeOutputs) {
                streamHandler.sendNodeOutput(nodeId, JSONObject.toJSONString(output));
            }
        } else {
            log.info("[LifecycleListener.after] 跳过NODE_OUTPUT - componentName: {}, showProcessOutput: {}",
                    componentName, showProcessOutput);
        }
    }

    @Override
    public void onError(String nodeId, Map<String, Object> state, Throwable ex, RunnableConfig config) {
        DataSourceHelper.use(this.tenantCode);

        Long runtimeNodeId = nodeRuntimeIds.remove(nodeId);
        nodeStates.remove(nodeId);
        nodeComponents.remove(nodeId);
        nodeShowProcessOutput.remove(nodeId);

        if (runtimeNodeId != null) {
            // 更新节点状态为失败
            if (callSource == WorkflowCallSource.AI_CHAT) {
                conversationRuntimeNodeService.updateStatus(runtimeNodeId, 4, ex.getMessage()); // 4=FAIL
            } else {
                workflowRuntimeNodeService.updateStatus(runtimeNodeId, 4, ex.getMessage());
            }
        }
        log.error("[LifecycleListener.onError] 节点执行失败: {}", nodeId, ex);
    }

    private boolean shouldSkipInputEvent(String componentName) {
        return "Start".equals(componentName)
            || "Answer".equals(componentName)
            || "SubWorkflow".equals(componentName)
            || "End".equals(componentName);
    }

    private boolean shouldSkipOutputEvent(String componentName) {
        return "Start".equals(componentName)
            || "Answer".equals(componentName)
            || "SubWorkflow".equals(componentName)
            || "End".equals(componentName);
    }
}
```

---

### 3.3 第三步：简化runNode方法

**原代码**：~200行（行409-612）
**目标代码**：~60行

```java
private Map<String, Object> runNode(AiWorkflowNodeVo wfNode, WfNodeState nodeState) {
    Map<String, Object> resultMap = new HashMap<>();
    try {
        DataSourceHelper.use(this.tenantCode);

        // 1. 中断节点检查
        if (wfState.getInterruptNodes().contains(wfNode.getUuid())
                && !nodeState.data().containsKey(HUMAN_FEEDBACK_KEY)) {
            log.info("检测到中断节点(HumanFeedbackNode),跳过执行: {}", wfNode.getUuid());
            resultMap.put("name", wfNode.getTitle());
            return resultMap;
        }

        // 2. 找组件（无虚拟节点分支）
        AiWorkflowComponentEntity wfComponent = components.stream()
                .filter(item -> item.getId().equals(wfNode.getWorkflowComponentId()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("组件不存在"));

        // 3. 创建节点实例
        AbstractWfNode abstractWfNode = WfNodeFactory.create(wfComponent, wfNode, wfState, nodeState);

        // 4. 注册节点状态到LifecycleListener（日志记录由Listener处理）
        boolean showProcessOutput = getShowProcessOutput(wfNode);
        lifecycleListener.registerNodeState(wfNode.getUuid(), nodeState,
                wfComponent.getName(), showProcessOutput);

        // 5. 执行节点（无回调参数）
        NodeProcessResult processResult = abstractWfNode.process();

        // 6. 设置下一个节点
        if (StringUtils.isNotBlank(processResult.getNextNodeUuid())) {
            resultMap.put("next", processResult.getNextNodeUuid());
        }
        if (StringUtils.isNotBlank(processResult.getNextSourceHandle())) {
            resultMap.put("next_source_handle", processResult.getNextSourceHandle());
            log.info("[runNode] 条件分支返回sourceHandle: {}", processResult.getNextSourceHandle());
        }

    } catch (Exception e) {
        log.error("Node run error: {} ({})", wfNode.getTitle(), wfNode.getUuid(), e);
        throw new RuntimeException(e);
    }

    // 7. 设置返回值
    resultMap.put("name", wfNode.getTitle());
    String outputKey = NODE_OUTPUT_KEY_PREFIX + wfNode.getUuid();
    resultMap.put(outputKey, nodeState.getOutputs());

    log.info("[runNode] 节点执行完成: uuid={}, outputKey={}, outputs数量={}",
            wfNode.getUuid(), outputKey, nodeState.getOutputs().size());

    return resultMap;
}

private boolean getShowProcessOutput(AiWorkflowNodeVo wfNode) {
    JSONObject nodeConfig = wfNode.getNodeConfig();
    if (nodeConfig != null && nodeConfig.containsKey("show_process_output")) {
        return nodeConfig.getBooleanValue("show_process_output");
    }
    return true; // 默认显示
}
```

---

### 3.4 第四步：修改AbstractWfNode.process方法签名

**原签名**：
```java
public NodeProcessResult process(Consumer<WfNodeState> inputCallback, Consumer<WfNodeState> outputCallback)
```

**新签名**：
```java
public NodeProcessResult process()
```

**影响**：需要修改所有AbstractWfNode子类的process方法实现，移除回调调用。

---

### 3.5 第五步：配置LifecycleListener

在WorkflowEngine初始化时配置：

```java
// WorkflowEngine构造函数或init方法中
this.lifecycleListener = new ScmWorkflowLifecycleListener(
    workflowRuntimeNodeService,
    conversationRuntimeNodeService,
    streamHandler,
    wfState,
    callSource,
    userId,
    tenantCode,
    runtimeId
);

// 编译配置
CompileConfig config = CompileConfig.builder()
    .withLifecycleListener(lifecycleListener)
    .build();

app = mainStateGraph.compile(config);
```

---

## 四、实施顺序

| 步骤 | 内容 | 影响范围 | 风险 |
|------|------|---------|------|
| 1 | 删除虚拟节点常量 | WorkflowConstants.java | 低 |
| 2 | 删除虚拟节点处理逻辑 | WorkflowEngine.runNode | 低 |
| 3 | 简化processConditionalEdges | WorkflowEngine | 低 |
| 4 | 新建ScmWorkflowLifecycleListener | 新文件 | 无 |
| 5 | 修改AbstractWfNode.process签名 | 所有节点子类 | 中 |
| 6 | 简化runNode方法 | WorkflowEngine | 中 |
| 7 | 配置LifecycleListener | WorkflowEngine | 低 |

---

## 五、代码量变化预估

| 文件 | 修改前行数 | 修改后行数 | 变化 |
|------|-----------|-----------|------|
| WorkflowConstants.java | 102 | 94 | -8 |
| WorkflowEngine.runNode | ~200 | ~60 | -140 |
| 新增ScmWorkflowLifecycleListener | 0 | ~160 | +160 |
| **净变化** | - | - | **+12** |

虽然总行数略有增加，但：
1. 职责分离更清晰（日志记录独立为Listener）
2. runNode方法从200行简化到60行，可读性大幅提升
3. 符合框架设计，可扩展性提升
4. 删除了35行废代码（虚拟节点）

---

## 六、验收标准

1. ✅ WorkflowConstants中无虚拟节点常量
2. ✅ WorkflowEngine.runNode中无虚拟节点分支判断
3. ✅ 使用GraphLifecycleListener进行日志记录
4. ✅ runNode方法行数 < 80行
5. ✅ compile()不再报错
6. ✅ 现有工作流正常执行
7. ✅ 节点运行时记录正常写入数据库
8. ✅ 前端正常接收NODE_RUN/NODE_INPUT/NODE_OUTPUT事件

---

## 七、参考资料

- Spring AI Alibaba源码：`D:\2025_project\20_project_in_github\99_tools\spring-ai-alibaba\spring-ai-alibaba-main`
- 关键文件：
  - `NodeExecutor.java` - 节点执行器
  - `GraphLifecycleListener.java` - 生命周期监听接口
  - `GraphObservationLifecycleListener.java` - 官方实现参考
