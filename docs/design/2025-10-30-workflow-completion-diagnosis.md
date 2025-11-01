# 工作流执行完成问题诊断报告

**日期**: 2025-10-30
**问题**: 工作流执行后状态停留在"运行中",历史记录不显示
**现象**: SSE事件显示End节点执行,但缺少OUTPUT和done事件

---

## 一、问题现象

### 1.1 SSE事件分析

从前端接收到的SSE事件:

```javascript
// Start节点正常执行
[START] 工作流开始
[NODE_RUN_7b46c2b366a245f5b3ec6837a960827c] Start节点运行
[NODE_INPUT_7b46c2b366a245f5b3ec6837a960827c] 输入数据
[NODE_OUTPUT_7b46c2b366a245f5b3ec6837a960827c] 输出数据

// End节点执行了两次
[NODE_RUN_ANyO6jsZ7VnkY0ZOpW8SqgUpF1eA3Ag] id=14, runtimeNodeUuid="6f11387782064d8aaa9a89ea12d6eec8"
[NODE_RUN_ANyO6jsZ7VnkY0ZOpW8SqgUpF1eA3Ag] id=15, runtimeNodeUuid="6323a075a8a04f9e978f2f2d97170a18"

// 缺失的事件
❌ [NODE_OUTPUT_ANyO6jsZ7VnkY0ZOpW8SqgUpF1eA3Ag] (End节点输出)
❌ [DONE] (工作流完成)
```

### 1.2 前端表现

- 执行详情对话框显示"运行中"状态,没有关闭
- 历史记录列表(WorkflowRuntimeList)没有显示该次执行
- 数据库中 `ai_workflow_runtime` 表的 `status` 字段为 1 (运行中)

---

## 二、代码执行流程分析

### 2.1 正常执行流程

```
WorkflowEngine.run()
  ├─> WorkflowEngine.exe()
  │     ├─> app.stream() // LangGraph4j执行图
  │     ├─> streamingResult() // 处理节点输出
  │     └─> app.getState().nextNode()
  │           ├─ 如果nextNode != END: 等待人工输入
  │           └─ 如果nextNode == END:
  │                ├─> workflowRuntimeService.updateOutput()
  │                └─> streamHandler.sendComplete() ✅ done事件
  │
  └─> 每个节点执行:
        └─> WorkflowEngine.runNode()
              ├─> workflowRuntimeNodeService.createByState() // 创建runtime node
              ├─> streamHandler.sendNodeRun() // NODE_RUN事件
              ├─> abstractWfNode.process()
              │     ├─> inputConsumer: sendNodeInput() // NODE_INPUT事件
              │     ├─> onProcess() // 执行节点逻辑
              │     └─> outputConsumer: sendNodeOutput() ✅ NODE_OUTPUT事件
              └─> return processResult
```

### 2.2 End节点执行流程

```java
// EndNode.java Line 36-59
@Override
protected NodeProcessResult onProcess() {
    List<NodeIOData> result = new ArrayList<>();
    JSONObject nodeConfigObj = node.getNodeConfig();
    String output = "";

    if (nodeConfigObj != null && !nodeConfigObj.isEmpty()) {
        String resultTemplate = nodeConfigObj.getString("result");
        if (resultTemplate != null) {
            WfNodeIODataUtil.changeFilesContentToMarkdown(state.getInputs());
            output = WorkflowUtil.renderTemplate(resultTemplate, state.getInputs());
        }
    }

    result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", output));
    return NodeProcessResult.builder().content(result).build(); // ✅ 返回结果
}
```

### 2.3 输出回调应该触发

```java
// AbstractWfNode.java Line 233-235
if (null != outputConsumer) {
    outputConsumer.accept(state);  // 应该发送NODE_OUTPUT事件
}
```

但SSE事件中**没有**End节点的OUTPUT事件,说明:
1. `outputConsumer` 为 null (不太可能)
2. `state.getOutputs()` 为空 (需要确认)
3. 执行过程中抛出异常 (需要查看日志)

---

## 三、问题根因推断

### 3.1 End节点执行两次

**现象**: SSE显示同一个End节点(nodeUuid: ANyO6jsZ7VnkY0ZOpW8SqgUpF1eA3Ag)被执行了两次,生成了两条runtime node记录(id=14, id=15)

**可能原因**:

#### A. 工作流图构建错误
```java
// WorkflowEngine.java Line 466-535: buildStateGraph()
// 可能Start节点有多条边指向End节点
// 或者End节点在并行分支中被重复引用
```

**诊断方法**:
1. 检查工作流定义中的边(edges)
2. 确认Start→End之间只有一条路径
3. 查看后端日志中 `buildStateGraph()` 的输出

#### B. LangGraph4j的END节点处理问题
```java
// Line 533: 没有下一个节点时,添加到END的边
} else {
    addEdgeToStateGraph(stateGraph, stateGraphNodeUuid, END);
}
```

可能LangGraph4j内部重复调用了End节点。

### 3.2 done事件缺失

**原因分析**:

```java
// WorkflowEngine.java Line 140-167
StateSnapshot<WfNodeState> stateSnapshot = app.getState(invokeConfig);
String nextNode = stateSnapshot.config().nextNode().orElse("");

if (StringUtils.isNotBlank(nextNode) && !nextNode.equalsIgnoreCase(END)) {
    // 等待人工输入...
} else {
    // ← 应该走这个分支
    workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
    streamHandler.sendComplete(); // done事件
}
```

**问题**: `nextNode` 的值可能不是 "END",而是:
- End节点的UUID ("ANyO6jsZ7VnkY0ZOpW8SqgUpF1eA3Ag")
- 空字符串 ("")
- null

**需要确认**: `stateSnapshot.config().nextNode()` 的实际返回值

### 3.3 工作流状态未更新

```java
// WorkflowEngine.java Line 156
AiWorkflowRuntimeEntity updatedRuntime = workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
```

**问题**: 在调用 `updateOutput()` 时,`wfState.getProcessStatus()` 可能还是 `WORKFLOW_PROCESS_STATUS_READY` (0),导致数据库中的status没有被更新为"成功"(2)。

**缺失的代码**:
```java
// 应该在更新输出之前显式设置状态
wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_SUCCESS); // 2-成功
workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
```

---

## 四、诊断步骤

### 4.1 检查后端日志

**关键日志点**:

1. **End节点是否真的执行了两次**:
```
搜索: "Node run error" 或 "runNode"
期望: 看到End节点(nodeUuid: ANyO6jsZ7VnkY0ZOpW8SqgUpF1eA3Ag)的两次执行记录
```

2. **输出回调是否被调用**:
```
搜索: "callback node:.*output:"
期望: 看到End节点的输出数据
实际: 如果没有找到,说明outputConsumer没有被调用
```

3. **nextNode的实际值**:
```
搜索: "nextNode" 或添加日志打印 nextNode 的值
期望: nextNode == "END"
实际: 需要确认是什么值
```

4. **工作流状态更新**:
```
搜索: "updateOutput" 或 "workflowRuntimeService"
期望: 看到status被更新为2(成功)
实际: 可能还是1(运行中)
```

### 4.2 检查数据库状态

```sql
-- 查询运行实例状态
SELECT
    id,
    runtime_uuid,
    workflow_id,
    user_id,
    status,  -- 1=运行中, 2=成功, 3=等待输入, 4=失败
    status_remark,
    input_data,
    output_data,
    c_time,
    u_time
FROM ai_workflow_runtime
WHERE runtime_uuid = '当前执行的runtimeUuid'
ORDER BY id DESC
LIMIT 1;

-- 查询节点执行记录
SELECT
    id,
    runtime_node_uuid,
    workflow_runtime_id,
    node_id,
    status,  -- 1=等待, 2=运行中, 3=成功, 4=失败
    status_remark,
    input_data,
    output_data,
    c_time
FROM ai_workflow_runtime_node
WHERE workflow_runtime_id = (
    SELECT id FROM ai_workflow_runtime WHERE runtime_uuid = '当前执行的runtimeUuid'
)
ORDER BY id;
```

**预期结果**:
- `ai_workflow_runtime.status` 应该是 2 (成功),实际可能是 1 (运行中)
- `ai_workflow_runtime_node` 应该有2条记录(Start和End),实际可能有3条(Start和两个End)

### 4.3 检查工作流定义

```sql
-- 查询工作流的边定义
SELECT
    id,
    source_node_uuid,
    target_node_uuid,
    source_handle,
    target_handle
FROM ai_workflow_edge
WHERE workflow_id = (
    SELECT workflow_id FROM ai_workflow_runtime WHERE runtime_uuid = '当前执行的runtimeUuid'
);

-- 查询节点定义
SELECT
    id,
    uuid,
    title,
    workflow_component_id,
    node_config
FROM ai_workflow_node
WHERE workflow_id = (
    SELECT workflow_id FROM ai_workflow_runtime WHERE runtime_uuid = '当前执行的runtimeUuid'
)
ORDER BY id;
```

**检查点**:
- Start节点和End节点之间应该**只有一条边**
- End节点应该**没有出边**

---

## 五、修复方案建议

### 5.1 临时修复(快速解决)

**修改 `WorkflowEngine.java` Line 153-167**:

```java
} else {
    // 工作流执行完成
    // ✅ 修复1: 显式设置工作流状态为成功
    wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_SUCCESS);

    // ✅ 修复2: 添加日志确认执行
    log.info("Workflow execution completed, runtimeUuid: {}, nextNode: {}",
             wfState.getUuid(), nextNode);

    AiWorkflowRuntimeEntity updatedRuntime = workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);

    // ✅ 修复3: 确保状态被更新
    if (updatedRuntime.getStatus() != WORKFLOW_PROCESS_STATUS_SUCCESS) {
        log.warn("Workflow status not updated correctly, force update");
        workflowRuntimeService.updateStatus(
            wfRuntimeResp.getId(),
            WORKFLOW_PROCESS_STATUS_SUCCESS,
            "执行成功"
        );
    }

    String outputStr = updatedRuntime.getOutputData();
    if (StringUtils.isBlank(outputStr)) {
        outputStr = "{}";
    }

    // 发送完成事件
    streamHandler.sendComplete();
    InterruptedFlow.RUNTIME_TO_GRAPH.remove(wfState.getUuid());
}
```

### 5.2 根本修复(需要进一步调查)

#### A. 修复End节点重复执行

**可能需要**:
1. 检查 `buildStateGraph()` 逻辑,确保End节点只被添加一次
2. 检查边定义,确保Start→End只有一条路径
3. 添加去重逻辑:
```java
// WorkflowEngine.java Line 581-602: addNodeToStateGraph()
List<StateGraph<WfNodeState>> stateGraphList = stateGraphNodes.computeIfAbsent(stateGraphNodeUuid, k -> new ArrayList<>());
boolean exist = stateGraphList.stream().anyMatch(item -> item == stateGraph);
if (exist) {
    log.info("state graph node exist,stateGraphNodeUuid:{}", stateGraphNodeUuid);
    return; // ✅ 已经有去重逻辑
}
```

#### B. 修复nextNode判断

**添加调试日志**:
```java
// WorkflowEngine.java Line 140-144
StateSnapshot<WfNodeState> stateSnapshot = app.getState(invokeConfig);
String nextNode = stateSnapshot.config().nextNode().orElse("");

// ✅ 添加日志
log.info("Checking workflow completion: nextNode='{}', isEmpty={}, equalsEND={}",
         nextNode,
         StringUtils.isBlank(nextNode),
         "END".equalsIgnoreCase(nextNode));
```

**可能需要修改判断逻辑**:
```java
// 原代码
if (StringUtils.isNotBlank(nextNode) && !nextNode.equalsIgnoreCase(END)) {
    // 等待输入...
} else {
    // 完成
}

// 修改为更严格的判断
if (StringUtils.isBlank(nextNode) || END.equalsIgnoreCase(nextNode)) {
    // 工作流执行完成
} else {
    // 等待人工输入
}
```

---

## 六、优先级行动项

### 高优先级(立即执行)

1. **添加日志** - 在关键位置添加日志,确认:
   - `nextNode` 的实际值
   - `wfState.getProcessStatus()` 的实际值
   - End节点的输出数据

2. **检查数据库** - 确认:
   - 工作流实例的status
   - 节点执行记录的数量

3. **临时修复** - 应用5.1的修复,确保状态被正确更新

### 中优先级(后续优化)

1. **修复End节点重复执行** - 如果确认是重复执行,需要修复图构建逻辑

2. **优化状态管理** - 统一状态更新逻辑,避免遗漏

3. **完善错误处理** - 确保异常情况下也能正确发送done或error事件

---

## 七、参考代码位置

| 文件 | 行数 | 说明 |
|------|------|------|
| WorkflowEngine.java | 135-167 | exe()方法,done事件发送 |
| WorkflowEngine.java | 208-265 | runNode()方法,节点执行 |
| AbstractWfNode.java | 193-238 | process()方法,输出回调 |
| EndNode.java | 36-59 | onProcess()方法,结果渲染 |
| AiWorkflowRuntimeService.java | 116-142 | updateOutput()方法,状态更新 |
| WorkflowStreamHandler.java | 132-141 | sendComplete()方法,done事件 |

---

**报告结束**

建议: 先执行"高优先级行动项",收集日志和数据,再决定具体的修复方案。
