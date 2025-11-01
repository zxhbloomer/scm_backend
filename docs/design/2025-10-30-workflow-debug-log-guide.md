# 工作流执行调试日志指南

**日期**: 2025-10-30
**目的**: 通过详细日志诊断工作流完成问题

---

## 一、已添加的调试日志

### 1.1 WorkflowEngine.exe() - 工作流完成检查

**位置**: `WorkflowEngine.java` Line 143-154

**日志输出**:
```
========== Workflow Completion Check ==========
  runtimeUuid: {uuid}
  nextNode: '{value}' (isEmpty: {boolean}, equalsEND: {boolean})
  wfState.processStatus: {status} (0=READY, 1=RUNNING, 2=SUCCESS, 3=WAITING, 4=FAIL)
  completed nodes count: {count}
===============================================
```

**关键信息**:
- `nextNode`: 下一个节点的标识,应该是 "END" 或空字符串才会发送done事件
- `isEmpty`: nextNode是否为空
- `equalsEND`: nextNode是否等于 "END"
- `processStatus`: 工作流当前状态(应该在完成时被设置为2)
- `completed nodes count`: 已完成节点数量

**期望输出**:
```
nextNode: '' (isEmpty: true, equalsEND: false)  或
nextNode: 'END' (isEmpty: false, equalsEND: true)
```

**异常输出**:
```
nextNode: 'ANyO6jsZ7VnkY0ZOpW8SqgUpF1eA3Ag' (isEmpty: false, equalsEND: false)
```
如果nextNode是节点UUID,说明LangGraph4j没有正确识别End节点

---

### 1.2 WorkflowEngine.exe() - 完成事件发送

**位置**: `WorkflowEngine.java` Line 168-190

**日志输出**:
```
Workflow execution completed, preparing to send done event
Set wfState.processStatus to SUCCESS(2)
Updated runtime output, status in DB: {status}
Sending workflow complete event (done)
Workflow execution finished successfully, runtimeUuid: {uuid}
```

**关键信息**:
- 显式设置状态为SUCCESS(2)
- 确认数据库中的状态更新
- 确认done事件已发送

**异常情况**:
如果这些日志没有出现,说明条件判断 `if (StringUtils.isNotBlank(nextNode) && !nextNode.equalsIgnoreCase(END))` 返回了true,进入了等待输入的分支

---

### 1.3 WorkflowEngine.runNode() - 节点执行详情

**位置**: `WorkflowEngine.java` Line 235-288

**日志输出**:
```
========== Running Node: {title} ({uuid}) ==========
  Component: {componentName}
  Created runtimeNode: id={id}, uuid={uuid}
  [INPUT CALLBACK] Node: {uuid}, inputs count: {count}
  [OUTPUT CALLBACK] Node: {uuid}, outputs count: {count}
  [OUTPUT DATA] callback node:{uuid}, output:{content}
  [OUTPUT CALLBACK] Sent {count} NODE_OUTPUT events
  Next node: {nextNodeUuid}  (如果有)
========== Node Execution Completed: {title} ==========
```

**关键信息**:
- 节点创建的runtime node记录
- 输入回调是否被调用
- **输出回调是否被调用** (重点关注)
- 发送了多少个NODE_OUTPUT事件
- 下一个节点UUID(End节点不应该有next)

**异常情况**:
如果End节点的 `[OUTPUT CALLBACK]` 没有出现,说明:
1. `outputConsumer` 为null (不太可能)
2. 执行过程中抛出异常
3. `abstractWfNode.process()` 没有正确调用回调

---

### 1.4 AbstractWfNode.process() - 节点处理流程

**位置**: `AbstractWfNode.java` Line 194-250

**日志输出**:
```
[AbstractWfNode.process] START - Node: {title}
[AbstractWfNode.process] Calling inputConsumer, consumer is null: {boolean}
--node input: {json}
[AbstractWfNode.process] Calling onProcess()
[AbstractWfNode.process] onProcess() returned, result content size: {size}
[AbstractWfNode.process] Set outputs, count: {count}
[AbstractWfNode.process] Calling outputConsumer, consumer is null: {boolean}, outputs count: {count}
[AbstractWfNode.process] outputConsumer.accept() completed
[AbstractWfNode.process] END - Node: {title}
```

**关键信息**:
- 输入/输出回调是否为null
- onProcess()返回的结果大小
- 输出数据是否正确设置
- 输出回调是否被正常调用和完成

**异常情况**:
- `processResult.getContent() is EMPTY!`: End节点没有返回输出数据
- `consumer is null: true`: 回调为null(不应该发生)
- 缺少 `outputConsumer.accept() completed`: 回调执行失败

---

## 二、测试步骤

### 2.1 重新编译和启动

```bash
# 编译项目
cd D:\2025_project\20_project_in_github\00_scm_backend\scm_backend
mvn clean install -DskipTests

# 启动应用
cd scm-start
mvn spring-boot:run
```

### 2.2 执行工作流

1. 打开前端工作流测试页面
2. 选择一个简单的工作流(只有Start和End节点)
3. 点击"运行"按钮
4. 观察前端SSE事件

### 2.3 查看后端日志

**关键搜索词**:

1. **查看工作流完成检查**:
```bash
搜索: "========== Workflow Completion Check =========="
```

2. **查看End节点执行**:
```bash
搜索: "========== Running Node: End"
或
搜索: "Component: End"
```

3. **查看输出回调**:
```bash
搜索: "[OUTPUT CALLBACK]"
```

4. **查看done事件**:
```bash
搜索: "Sending workflow complete event"
```

### 2.4 日志分析清单

请回答以下问题:

**A. 工作流完成检查**:
- [ ] nextNode的值是什么? ___________
- [ ] nextNode.isEmpty 是 true 还是 false? ___________
- [ ] nextNode.equalsEND 是 true 还是 false? ___________
- [ ] wfState.processStatus 的值是? ___________

**B. End节点执行**:
- [ ] End节点执行了几次? ___________
- [ ] 每次执行都创建了runtime node吗? (id=?) ___________
- [ ] 输入回调被调用了吗? ___________
- [ ] 输出回调被调用了吗? ___________

**C. 节点输出**:
- [ ] End节点的outputs count是多少? ___________
- [ ] 发送了多少个NODE_OUTPUT事件? ___________
- [ ] processResult.getContent()是否为空? ___________

**D. 完成事件**:
- [ ] 看到 "Workflow execution completed" 了吗? ___________
- [ ] 看到 "Set wfState.processStatus to SUCCESS(2)" 了吗? ___________
- [ ] 看到 "Sending workflow complete event (done)" 了吗? ___________

---

## 三、预期诊断结果

### 情况1: End节点执行两次

**日志特征**:
```
========== Running Node: End (ANyO6jsZ...) ==========
  Created runtimeNode: id=14, uuid=xxx
========== Node Execution Completed: End ==========

========== Running Node: End (ANyO6jsZ...) ==========
  Created runtimeNode: id=15, uuid=yyy
========== Node Execution Completed: End ==========
```

**诊断**: LangGraph4j的状态图构建有问题,End节点被添加了两次或被重复调用

**解决方向**: 检查 `buildStateGraph()` 逻辑

---

### 情况2: 输出回调未调用

**日志特征**:
```
[AbstractWfNode.process] onProcess() returned, result content size: 1
[AbstractWfNode.process] Set outputs, count: 1
[AbstractWfNode.process] Calling outputConsumer, consumer is null: false, outputs count: 1
// ❌ 缺少: [AbstractWfNode.process] outputConsumer.accept() completed
```

**诊断**: 输出回调执行过程中抛出异常

**解决方向**: 查看异常堆栈,修复回调逻辑

---

### 情况3: nextNode不是END

**日志特征**:
```
========== Workflow Completion Check ==========
  nextNode: 'ANyO6jsZ7VnkY0ZOpW8SqgUpF1eA3Ag' (isEmpty: false, equalsEND: false)
  wfState.processStatus: 0
===============================================
// ❌ 没有进入else分支,没有发送done事件
```

**诊断**: nextNode是End节点的UUID而不是 "END",导致条件判断错误

**解决方向**:
1. 修改判断逻辑
2. 或者检查LangGraph4j的END常量配置

---

### 情况4: 状态未更新

**日志特征**:
```
Workflow execution completed, preparing to send done event
Set wfState.processStatus to SUCCESS(2)
Updated runtime output, status in DB: 1  ← ❌ 应该是2
```

**诊断**: `updateOutput()`方法中状态更新逻辑有问题

**解决方向**: 检查 `AiWorkflowRuntimeService.updateOutput()` 的实现

---

## 四、下一步行动

根据日志分析结果,选择对应的修复方案:

### 如果是nextNode问题:

修改 `WorkflowEngine.java` Line 157的判断逻辑:

```java
// 原代码
if (StringUtils.isNotBlank(nextNode) && !nextNode.equalsIgnoreCase(END)) {
    // 等待输入...
} else {
    // 完成
}

// 修改为
if (StringUtils.isBlank(nextNode) || END.equalsIgnoreCase(nextNode)) {
    // 工作流执行完成
    log.info("Workflow execution completed...");
    // ...
} else {
    // 等待人工输入
    log.info("Workflow entering WAITING_INPUT state...");
    // ...
}
```

### 如果是End节点重复执行:

检查工作流定义的边:
```sql
SELECT * FROM ai_workflow_edge
WHERE workflow_id = {workflowId}
AND source_node_uuid = 'Start节点UUID';
```
确保只有一条边指向End节点。

### 如果是状态更新问题:

已在代码中添加了显式的状态设置(Line 172),应该已经修复。

---

**测试完成后,请将日志输出结果发给我,我会帮你进一步分析问题。**
