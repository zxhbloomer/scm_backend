# 工作流人机交互节点中断机制修复方案

## 问题概述

**现象**: 配置了 HumanFeedbackNode 的工作流 "aaaa" 执行时卡在 "运行中" 状态，显示 "无输出"

**数据库证据**:
```sql
-- workflow runtime: status=1 (运行中)
SELECT * FROM ai_workflow_runtime WHERE runtime_uuid = 'ec704d566dbe43209c75f888633e0be3';

-- 节点执行记录:
-- 节点40 (开始): status=2 (成功), 有输出
-- 节点46 (人机交互): status=3 (失败), error: "process error: 未获取到用户反馈"
SELECT * FROM ai_workflow_runtime_node WHERE workflow_runtime_id = ...;
```

## 根因分析

### 1. LangGraph4j 的正确实现模式

根据 Context7 文档 (https://github.com/bsorrentino/langgraph4j/blob/main/how-tos/wait-user-input.ipynb)：

```java
// ✅ 正确：使用 interruptBefore 在节点执行前中断
var compileConfig = CompileConfig.builder()
    .checkpointSaver(saver)
    .interruptBefore("human_feedback")  // 关键配置
    .releaseThread(true)
    .build();

// humanFeedback 节点在中断后只是占位，不执行实际逻辑
AsyncNodeAction<State> humanFeedback = node_async( state -> {
    return Map.of();  // 空实现，实际逻辑在用户输入后才执行
});

// 执行流程:
// 1. graph.stream() 会在 human_feedback 前自动停止
// 2. 用户输入后，调用 graph.updateState() 更新状态
// 3. 再次调用 graph.stream() 继续执行
```

### 2. SCM 当前实现的问题

**问题1: 缺少 `interruptBefore` 配置**

```java
// WorkflowEngine.java 第126-139行
// ❌ 问题：没有配置 interruptBefore，节点会被直接执行
CompileConfig compileConfig = CompileConfig.builder()
    .checkpointSaver(memorySaver)
    .releaseThread(true)
    // 缺少: .interruptBefore("HumanFeedbackNode")
    .build();
```

**问题2: HumanFeedbackNode 期望 KEY 已存在**

```java
// HumanFeedbackNode.java 第32-51行
@Override
protected NodeProcessResult onProcess() {
    // ❌ 问题：节点被执行时，期望 HUMAN_FEEDBACK_KEY 已经在 state 中
    Object feedbackData = state.data().get(HUMAN_FEEDBACK_KEY);
    if (feedbackData == null) {
        log.error("人机交互节点未获取到用户反馈，nodeUuid: {}", node.getUuid());
        throw new BusinessException("未获取到用户反馈");  // 直接抛异常
    }

    String userInput = feedbackData.toString();
    List<NodeIOData> result = List.of(
        NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", userInput)
    );
    return NodeProcessResult.builder().content(result).build();
}
```

**问题3: WorkflowEngine 的检测逻辑在节点执行后**

```java
// WorkflowEngine.java 第159-169行
// ❌ 问题：这个检查是在节点执行后，但 HumanFeedbackNode 已经抛异常了
AsyncGenerator<NodeOutput<WfNodeState>> outputs = app.stream(resume ? null : Map.of(), invokeConfig);
streamingResult(wfState, outputs);

StateSnapshot<WfNodeState> stateSnapshot = app.getState(invokeConfig);
String nextNode = stateSnapshot.config().nextNode().orElse("");

// 这个逻辑永远不会被执行到，因为 HumanFeedbackNode 已经失败了
if (StringUtils.isNotBlank(nextNode) && !nextNode.equalsIgnoreCase(END)) {
    streamHandler.sendNodeInput(nextNode, intTip);
    wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_WAITING_INPUT);
    workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
}
```

### 3. 执行时序对比

**LangGraph4j 正确流程：**
```
1. graph.compile(interruptBefore("human_feedback"))
2. graph.stream(input)
   → 执行 step1 → 到达 human_feedback → **自动中断** → 返回
3. StateSnapshot.nextNode() = "human_feedback"
4. 前端检测到中断，显示输入框
5. 用户输入 → graph.updateState(Map.of("human_feedback", input))
6. graph.stream(null) → 继续执行 human_feedback 节点 → step3 → END
```

**SCM 当前流程（错误）：**
```
1. graph.compile() - 无 interruptBefore 配置
2. graph.stream(input)
   → 执行 Start → **直接执行 HumanFeedbackNode**
   → HumanFeedbackNode.onProcess() 检查 HUMAN_FEEDBACK_KEY
   → HUMAN_FEEDBACK_KEY 不存在
   → **抛出 BusinessException("未获取到用户反馈")**
   → 节点 status=3 (失败)
   → 工作流 status=1 (运行中，因为异常被捕获但状态没更新)
3. WorkflowEngine 第159行的检查永远不会执行
```

## 解决方案

### 方案A：实现标准的 interruptBefore 机制（推荐）

**优点**: 完全符合 LangGraph4j 标准，代码清晰
**缺点**: 需要修改多处代码

#### 步骤1: WorkflowEngine 添加 interruptBefore 支持

```java
// WorkflowEngine.java

public class WorkflowEngine {
    // 添加字段记录需要中断的节点
    private final Set<String> humanFeedbackNodeUuids = new HashSet<>();

    // 在构造函数中识别 HumanFeedbackNode
    public WorkflowEngine(...) {
        this.workflow = workflow;
        this.components = components;
        this.wfNodes = nodes;

        // 识别所有 HumanFeedbackNode (component_id = 14)
        for (AiWorkflowNodeVo node : nodes) {
            if (node.getWorkflowComponentId() == 14) {  // HumanFeedbackNode
                humanFeedbackNodeUuids.add(node.getUuid());
            }
        }
    }

    // 修改 compile 方法
    private void compile() {
        MemorySaver memorySaver = new MemorySaver();

        // 配置 interruptBefore
        CompileConfig.Builder configBuilder = CompileConfig.builder()
            .checkpointSaver(memorySaver)
            .releaseThread(true);

        // 如果有 HumanFeedbackNode，添加 interruptBefore
        if (!humanFeedbackNodeUuids.isEmpty()) {
            configBuilder.interruptBefore(new ArrayList<>(humanFeedbackNodeUuids));
        }

        CompileConfig compileConfig = configBuilder.build();
        this.app = stateGraph.compile(compileConfig);
    }
}
```

#### 步骤2: HumanFeedbackNode 改为占位实现

```java
// HumanFeedbackNode.java

@Override
protected NodeProcessResult onProcess() {
    // 如果在 interruptBefore 配置下，这个方法会在 updateState 后才执行
    // 此时 HUMAN_FEEDBACK_KEY 应该已经存在

    Object feedbackData = state.data().get(HUMAN_FEEDBACK_KEY);

    // 如果还是不存在，说明是第一次执行（中断前）
    if (feedbackData == null) {
        // 返回空结果，不抛异常
        log.info("HumanFeedbackNode waiting for user input, nodeUuid: {}", node.getUuid());
        return NodeProcessResult.builder()
            .content(Collections.emptyList())
            .build();
    }

    // 第二次执行（中断后），HUMAN_FEEDBACK_KEY 已存在
    String userInput = feedbackData.toString();
    log.info("HumanFeedbackNode received user input: {}", userInput);

    List<NodeIOData> result = List.of(
        NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", userInput)
    );

    return NodeProcessResult.builder().content(result).build();
}
```

#### 步骤3: WorkflowEngine 检测中断状态

```java
// WorkflowEngine.java - exe() 方法

private void exe(RunnableConfig invokeConfig, boolean resume) {
    AsyncGenerator<NodeOutput<WfNodeState>> outputs = app.stream(resume ? null : Map.of(), invokeConfig);
    streamingResult(wfState, outputs);

    StateSnapshot<WfNodeState> stateSnapshot = app.getState(invokeConfig);
    String nextNode = stateSnapshot.config().nextNode().orElse("");

    log.info("========== Workflow Execution Status ==========");
    log.info("  nextNode: '{}', isEmpty: {}, equalsEND: {}",
             nextNode, StringUtils.isBlank(nextNode), "END".equalsIgnoreCase(nextNode));

    // 检查是否是 HumanFeedbackNode 需要中断
    if (StringUtils.isNotBlank(nextNode) && !nextNode.equalsIgnoreCase(END)) {
        // 检查 nextNode 是否是 HumanFeedbackNode
        boolean isHumanFeedbackNode = humanFeedbackNodeUuids.contains(nextNode);

        if (isHumanFeedbackNode) {
            log.info("Detected HumanFeedbackNode interruption: {}", nextNode);

            // 获取提示信息
            String tip = wfNodes.stream()
                .filter(node -> node.getUuid().equals(nextNode))
                .findFirst()
                .map(node -> HumanFeedbackNode.getTip(node))
                .orElse("请输入反馈");

            // 发送等待输入事件
            streamHandler.sendNodeInput(nextNode, tip);

            // 保存到 InterruptedFlow
            InterruptedFlow.RUNTIME_TO_GRAPH.put(wfState.getUuid(), this);

            // 更新状态为等待输入
            wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_WAITING_INPUT);
            workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);

            log.info("Workflow entering WAITING_INPUT state for node: {}", nextNode);
            return;  // 停止执行
        }
    }

    // 如果没有下一个节点或已到 END，表示完成
    if (StringUtils.isBlank(nextNode) || nextNode.equalsIgnoreCase(END)) {
        wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_SUCCESS);
        AiWorkflowRuntimeEntity updatedRuntime = workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
        streamHandler.sendComplete();
        InterruptedFlow.RUNTIME_TO_GRAPH.remove(wfState.getUuid());
        log.info("Workflow execution finished successfully");
    }
}
```

#### 步骤4: 保持 resume() 方法不变

```java
// WorkflowEngine.java - resume() 方法
public void resume(String userInput) {
    RunnableConfig invokeConfig = RunnableConfig.builder().build();
    try {
        // updateState 会将 userInput 注入到 HUMAN_FEEDBACK_KEY
        app.updateState(invokeConfig, Map.of(HUMAN_FEEDBACK_KEY, userInput), null);

        // 继续执行（这次 HumanFeedbackNode 会找到 HUMAN_FEEDBACK_KEY）
        exe(invokeConfig, true);
    } catch (Exception e) {
        errorWhenExe(e);
    } finally {
        if (wfState.getProcessStatus() != WORKFLOW_PROCESS_STATUS_WAITING_INPUT) {
            InterruptedFlow.RUNTIME_TO_GRAPH.remove(wfState.getUuid());
        }
    }
}
```

### 方案B：兼容现有实现（临时方案）

如果不想大规模修改，可以在 HumanFeedbackNode 中捕获异常：

```java
// HumanFeedbackNode.java

@Override
protected NodeProcessResult onProcess() {
    Object feedbackData = state.data().get(HUMAN_FEEDBACK_KEY);

    if (feedbackData == null) {
        // 不抛异常，而是返回特殊标记
        log.warn("HumanFeedbackNode: HUMAN_FEEDBACK_KEY not found, waiting for input");

        // 返回一个特殊的 NodeProcessResult 表示需要等待
        return NodeProcessResult.builder()
            .content(Collections.emptyList())
            .needsInput(true)  // 添加这个标记
            .build();
    }

    String userInput = feedbackData.toString();
    log.info("用户反馈输入: {}", userInput);

    List<NodeIOData> result = List.of(
        NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", userInput)
    );

    return NodeProcessResult.builder().content(result).build();
}
```

然后在 WorkflowEngine 检查 `needsInput` 标记。

## 推荐实施步骤

1. **采用方案A**（标准 LangGraph4j 实现）
2. **修改顺序**：
   - 步骤1: WorkflowEngine 添加 interruptBefore 支持
   - 步骤2: HumanFeedbackNode 改为占位实现
   - 步骤3: WorkflowEngine 检测中断状态
   - 步骤4: 测试 "aaaa" 工作流

3. **测试验证**：
   ```sql
   -- 执行工作流后检查状态
   SELECT status, status_remark FROM ai_workflow_runtime
   WHERE runtime_uuid = '...';
   -- 期望: status=2 (等待输入)

   SELECT status, status_remark FROM ai_workflow_runtime_node
   WHERE workflow_runtime_id = ...;
   -- 期望: HumanFeedbackNode status=1 (等待执行)，不是3 (失败)
   ```

## 参考资料

- LangGraph4j Human-in-the-Loop 文档: https://github.com/bsorrentino/langgraph4j/blob/main/how-tos/wait-user-input.ipynb
- Aideepin WorkflowEngine 实现: `D:\2025_project\20_project_in_github\99_tools\aideepin\langchain4j-aideepin\adi-common\src\main\java\com\moyz\adi\common\workflow\WorkflowEngine.java`
- Aideepin WorkflowUtil.getHumanFeedbackTip(): `D:\2025_project\20_project_in_github\99_tools\aideepin\langchain4j-aideepin\adi-common\src\main\java\com\moyz\adi\common\workflow\WorkflowUtil.java`

## 关键常量

```java
// WorkflowConstants.java
public static final String HUMAN_FEEDBACK_KEY = "HUMAN_FEEDBACK";
public static final int WORKFLOW_PROCESS_STATUS_WAITING_INPUT = 2;  // 等待输入
public static final int WORKFLOW_PROCESS_STATUS_SUCCESS = 3;         // 成功
```

## 数据库状态码说明

```java
// ai_workflow_runtime.status
0 = 就绪
1 = 运行中
2 = 等待输入  // HumanFeedbackNode 应该设置这个状态
3 = 成功
4 = 失败

// ai_workflow_runtime_node.status
1 = 等待执行
2 = 运行中
3 = 成功
4 = 失败
```
