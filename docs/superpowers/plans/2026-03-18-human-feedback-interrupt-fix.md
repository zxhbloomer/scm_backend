# 人机交互中断修复 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复人机交互中断流程中的2个问题：(1) `handleInterruption()` 应直接从 `InterruptionMetadata.node()` 获取被中断节点ID，而非通过 `completedNodes` 过滤推断；(2) `AiUserTableSelect.vue` 的 `submitData` 展开顺序存在边界问题，业务数据中的 `key` 字段可能覆盖选中项标识。

**Architecture:** 后端修改 `WorkflowEngine.handleInterruption()` 一处逻辑，直接读取框架提供的 `InterruptionMetadata.node()` 作为被中断节点UUID，并保留原有过滤逻辑作为降级兜底。前端修改 `AiUserTableSelect.vue` 的 `handleSubmit()` 方法，调整对象展开顺序，确保 `key`/`label` 字段不被 `opt.data` 中的同名字段覆盖。

**Tech Stack:** Java 17 / Spring Boot 3.1.4 / Spring AI Alibaba（`InterruptionMetadata`）/ Vue.js 2.7.16 / Element UI

---

## 文件清单

| 操作 | 文件 | 说明 |
|------|------|------|
| 修改 | `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java` | `handleInterruption()` 节点ID获取逻辑 |
| 修改 | `src/components/70_ai/components/interaction/AiUserTableSelect.vue`（前端仓库） | `handleSubmit()` 展开顺序修复 |

---

## Task 1：后端 — 修复 `handleInterruption()` 节点ID获取逻辑

**文件：**
- 修改：`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`（第759-832行）

**背景：**
当前代码通过遍历 `wfState.getInterruptNodes()` 并过滤 `completedNodes` 来推断被中断的节点UUID。
Spring AI Alibaba 框架在触发 `interruptsBefore` 时，会将 `InterruptionMetadata` 实例放入 `GraphResponse.resultValue()`（现有代码第668行已有 `instanceof` 检查证实此行为）。`InterruptionMetadata` 继承自 `NodeOutput`，其 `node()` 方法直接返回被中断节点的UUID，比过滤推断更准确可靠。

**修改目标：**
优先使用 `InterruptionMetadata.node()` 获取节点ID，原有过滤逻辑保留为降级兜底。

- [ ] **Step 1：定位并修改 `handleInterruption()` 方法**

找到 `WorkflowEngine.java` 第759行的 `handleInterruption()` 方法，将节点ID获取逻辑从：

```java
// 获取中断节点信息
String nextInterruptNode = wfState.getInterruptNodes().stream()
    .filter(nodeUuid -> wfState.getCompletedNodes().stream()
        .noneMatch(completedNode -> completedNode.getNode().getUuid().equals(nodeUuid)))
    .findFirst()
    .orElse(null);
```

改为：

```java
// 优先从 InterruptionMetadata 直接获取被中断节点ID（框架提供，最准确）
String nextInterruptNode = null;
if (graphResponse.resultValue().isPresent()) {
    Object result = graphResponse.resultValue().get();
    if (result instanceof com.alibaba.cloud.ai.graph.action.InterruptionMetadata interruptionMeta) {
        nextInterruptNode = interruptionMeta.node();
        log.info("从InterruptionMetadata获取中断节点: {}", nextInterruptNode);
    }
}
// 降级：框架未提供时，通过completedNodes过滤推断
if (nextInterruptNode == null) {
    nextInterruptNode = wfState.getInterruptNodes().stream()
        .filter(nodeUuid -> wfState.getCompletedNodes().stream()
            .noneMatch(completedNode -> completedNode.getNode().getUuid().equals(nodeUuid)))
        .findFirst()
        .orElse(null);
    if (nextInterruptNode != null) {
        log.info("降级：通过completedNodes过滤获取中断节点: {}", nextInterruptNode);
    }
}
```

- [ ] **Step 2：确认方法其余部分不需要改动**

`handleInterruption()` 第769行之后的逻辑（`InterruptedFlow.RUNTIME_TO_GRAPH.put`、读取节点配置、创建DB记录、发送SSE事件）均使用 `nextInterruptNode` 变量，无需修改。

---

## Task 2：前端 — 修复 `AiUserTableSelect.vue` submitData 展开顺序

**文件：**
- 修改：前端仓库 `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend` 下的 `src/components/70_ai/components/interaction/AiUserTableSelect.vue`

**背景：**
当前 `handleSubmit()` 代码：
```javascript
const submitData = {
  key: this.selectedRow._key,
  label: this.selectedRow._label,
  ...this.selectedRow   // ← 如果 opt.data 里有 key/label 字段，会覆盖上面赋的值
}
delete submitData._key
delete submitData._label
```

`tableData` 计算属性将 `opt.data` 的所有字段展开到行数据中。若业务数据（如商品数据）中包含名为 `key` 或 `label` 的字段，`...this.selectedRow` 展开时会覆盖前面明确赋值的 `key: this.selectedRow._key`。

**修改目标：**
调整展开顺序，先展开业务数据，再用 `_key`/`_label` 覆盖，确保选中项标识不被业务数据污染。

- [ ] **Step 1：修改 `handleSubmit()` 方法**

找到 `AiUserTableSelect.vue` 的 `handleSubmit()` 方法，将：

```javascript
handleSubmit () {
  if (!this.selectedRow) return
  this.submitted = true
  const submitData = {
    key: this.selectedRow._key,
    label: this.selectedRow._label,
    ...this.selectedRow
  }
  // 清理内部字段
  delete submitData._key
  delete submitData._label
  this.$emit('submit', 'select_record', submitData)
},
```

改为：

```javascript
handleSubmit () {
  if (!this.selectedRow) return
  this.submitted = true
  // 先展开行数据（业务字段），再用 _key/_label 覆盖，防止业务数据中的 key/label 字段污染选中项标识
  const submitData = {
    ...this.selectedRow,
    key: this.selectedRow._key,
    label: this.selectedRow._label
  }
  delete submitData._key
  delete submitData._label
  this.$emit('submit', 'select_record', submitData)
},
```

- [ ] **Step 2：确认 `tableData` 计算属性的数据结构**

确认 `tableData` 中每行数据结构为 `{ _key, _label, ...opt.data, label: opt.label }`，
`submitData` 展开后包含所有业务字段 + 正确的 `key`（来自 `_key`）+ 正确的 `label`（来自 `_label`）。

---

## 验证方式

修改完成后，通过以下步骤人工验证：

1. 在工作流中配置一个 `table_select` 类型的人机交互节点，动态选项数据中包含 `key` 字段（如商品数据有 `goods_code` 等）
2. 触发工作流，确认：
   - 工作流在 HumanFeedbackNode 前正确暂停（SSE 收到 `interaction_request` 事件）
   - 前端渲染 `AiUserTableSelect` 组件，显示表格数据
   - 选中一行后点击确认，`submitData.key` 为选项的 `key` 值（非业务数据中的 `key`）
   - 工作流恢复执行，下游节点收到正确的 `selectedKey`/`selectedData`
