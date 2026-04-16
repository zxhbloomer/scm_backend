# Workflow End节点输出显示 & 思考面板折叠 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复两个问题：(1) End节点所有输出变量都显示在前端（不只是单个`output`变量）；(2) 工作流完成后思考过程面板自动折叠。

**Architecture:** 
- 问题1（End节点多输出）：`WorkflowEventAdapter.java` 的 `output` 事件处理只取 `name==="output"` 的单个变量，需要改为拼接所有输出变量。
- 问题2（面板折叠）：`ThinkingSteps.vue` 已有折叠逻辑（`watch.isCompleted` → `collapsed=true`），但 `isStreamComplete` 的判断依赖 `workflowProcessNodes` 被清除。需要确认 `CLEAR_WORKFLOW_PROCESS_NODE` 的调用时序是否正确，以及 `streamComplete` prop 是否正确传递。

**Tech Stack:** Java (Spring Boot), Vue 2, Vuex, SSE

---

## 文件变更清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `scm-ai/src/main/java/com/xinyirun/scm/ai/core/adapter/WorkflowEventAdapter.java` | 修改 | 修复 `output` 事件处理，拼接 End 节点所有输出变量 |
| `src/components/70_ai/components/chat/messages/MessageList.vue` | 修改 | 确认 `isStreamComplete` 逻辑，修复面板折叠时序 |
| `src/components/70_ai/store/modules/chat.js` | 修改（如需要） | 调整 `CLEAR_WORKFLOW_PROCESS_NODE` 调用时序 |

---

## Task 1: 修复 End 节点多输出变量显示

**背景：** `WorkflowEventAdapter.java:69-95` 的 `output` 事件处理逻辑：
```java
case "output":
    String outputNodeName = dataJson.getString("nodeName");
    if ("End".equals(outputNodeName)) {
        JSONObject outputData = dataJson.getJSONObject("data");
        if (outputData != null) {
            for (String key : outputData.keySet()) {
                Object outputItem = outputData.get(key);
                if (outputItem instanceof JSONObject) {
                    JSONObject itemJson = (JSONObject) outputItem;
                    if ("output".equals(itemJson.getString("name"))) {  // ← 只取 name==="output" 的变量
                        ...
                    }
                }
            }
        }
    }
```
End 节点可能有多个输出变量（如 `output`、`summary`、`result` 等），当前只取 `name==="output"` 的那一个。

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/adapter/WorkflowEventAdapter.java:69-95`

- [ ] **Step 1: 读取并理解当前 output 事件处理逻辑**

  读取 `WorkflowEventAdapter.java` 第 69-95 行，确认当前只处理 `name==="output"` 的单个变量。

- [ ] **Step 2: 修改 output 事件处理，拼接所有 End 节点输出变量**

  将 `WorkflowEventAdapter.java` 中 `case "output":` 的处理逻辑从"只取 name=output 的单个变量"改为"拼接所有输出变量的值"：

  ```java
  case "output":
      // 节点完整输出：只渲染 End 节点的输出变量值（其他节点的 output 是中间数据，不显示给用户）
      String outputNodeName = dataJson.getString("nodeName");
      if ("End".equals(outputNodeName)) {
          JSONObject outputData = dataJson.getJSONObject("data");
          if (outputData != null) {
              StringBuilder sb = new StringBuilder();
              for (String key : outputData.keySet()) {
                  Object outputItem = outputData.get(key);
                  if (outputItem instanceof JSONObject) {
                      JSONObject itemJson = (JSONObject) outputItem;
                      JSONObject content = itemJson.getJSONObject("content");
                      if (content != null && content.containsKey("value")) {
                          String val = content.getString("value");
                          if (val != null && !val.isEmpty()) {
                              if (sb.length() > 0) sb.append("\n\n");
                              sb.append(val);
                          }
                      }
                  }
              }
              if (sb.length() > 0) {
                  builder.results(List.of(
                      ChatResponseVo.Generation.builder()
                          .output(ChatResponseVo.AssistantMessage.builder()
                              .content(sb.toString())
                              .build())
                          .build()
                  ));
              }
          }
      }
      break;
  ```

- [ ] **Step 3: 验证修改正确性**

  检查修改后的代码：
  - 确认 `sb.length() > 0` 判断防止空输出
  - 确认多个输出变量用 `\n\n` 分隔
  - 确认不影响非 End 节点的 output 事件（`if ("End".equals(outputNodeName))` 保护）

- [ ] **Step 4: 编译验证**

  在后端项目根目录运行：
  ```bash
  cd D:\2025_project\20_project_in_github\00_scm_backend\scm_backend
  mvn compile -pl scm-ai -am -q
  ```
  Expected: BUILD SUCCESS，无编译错误

---

## Task 2: 诊断思考面板折叠问题

**背景：** `ThinkingSteps.vue` 已有折叠逻辑：
```javascript
watch: {
  isCompleted (val) {
    if (val && this.streamComplete) {
      this.collapsed = true  // ← 当 streamComplete=true 且 isCompleted=true 时折叠
    }
  }
}
```

`streamComplete` 由 `MessageList.vue:isStreamComplete()` 计算：
```javascript
isStreamComplete (messageId) {
  const realtime = this.workflowProcessNodes[messageId]
  return !(realtime && realtime.steps && realtime.steps.length > 0)
  // 没有实时数据 = 流已完成
}
```

`CLEAR_WORKFLOW_PROCESS_NODE` 在 `chat.js:618` 的 `onComplete` 回调中调用，清除后 `isStreamComplete` 返回 `true`。

**问题诊断：** 需要确认 `streamComplete` prop 是否是响应式的，以及 `watch.isCompleted` 是否在 `streamComplete` 变化时重新触发。

**Files:**
- Read: `src/components/70_ai/components/chat/messages/ThinkingSteps.vue:247-257`
- Read: `src/components/70_ai/components/chat/messages/MessageList.vue:160-170`

- [ ] **Step 1: 确认 ThinkingSteps 的 watch 逻辑**

  当前 `watch.isCompleted` 只监听 `isCompleted` 变化，不监听 `streamComplete` 变化。
  
  问题：如果 `isCompleted` 先变为 `true`（所有步骤 done），然后 `streamComplete` 才变为 `true`（CLEAR 后），`watch.isCompleted` 不会再次触发，面板不会折叠。

  验证：在 `ThinkingSteps.vue` 中添加 `streamComplete` 的 watch：

  ```javascript
  watch: {
    isCompleted (val) {
      if (val && this.streamComplete) {
        this.collapsed = true
      } else if (!val && !this.streamComplete) {
        this.collapsed = false
      }
    },
    // 新增：监听 streamComplete 变化
    streamComplete (val) {
      if (val && this.isCompleted) {
        // SSE 流结束后，如果所有步骤已完成，自动折叠
        this.collapsed = true
      }
    }
  }
  ```

- [ ] **Step 2: 修改 ThinkingSteps.vue 添加 streamComplete watch**

  编辑 `ThinkingSteps.vue` 第 247-257 行，在现有 `watch` 对象中添加 `streamComplete` 监听：

  ```javascript
  watch: {
    isCompleted (val) {
      if (val && this.streamComplete) {
        // 只有 SSE 流真正结束后才自动收缩，避免 Orchestrator 多子任务场景中途误收缩
        this.collapsed = true
      } else if (!val && !this.streamComplete) {
        // 流还在运行中且 isCompleted 变为 false（新步骤进来），确保展开
        this.collapsed = false
      }
    },
    streamComplete (val) {
      if (val && this.isCompleted) {
        this.collapsed = true
      }
    }
  },
  ```

- [ ] **Step 3: 验证修改**

  检查修改后的逻辑：
  - `isCompleted` 先变 true，`streamComplete` 后变 true → `streamComplete` watch 触发 → `collapsed=true` ✓
  - `streamComplete` 先变 true，`isCompleted` 后变 true → `isCompleted` watch 触发 → `collapsed=true` ✓
  - 两者同时变 true → 任一 watch 触发 → `collapsed=true` ✓

---

## Task 3: 端到端验证

**Files:**
- Read: `src/components/70_ai/store/modules\chat.js:575-620` (onComplete 回调)

- [ ] **Step 1: 确认 onComplete 中的调用顺序**

  读取 `chat.js:575-620`，确认 `FLUSH_PENDING_NODE_COMPLETE` 和 `CLEAR_WORKFLOW_PROCESS_NODE` 的调用顺序：
  
  ```javascript
  // 575: FLUSH_PENDING_NODE_COMPLETE(aiMessageId)  ← 标记最后步骤为 done
  // 576: ...
  // 618: CLEAR_WORKFLOW_PROCESS_NODE(aiMessageId)  ← 清除实时数据，触发 isStreamComplete=true
  ```
  
  这个顺序是正确的：先 flush（确保所有步骤 done），再 clear（触发 streamComplete 变化）。

- [ ] **Step 2: 确认 isStreamComplete 的响应式传递**

  在 `MessageList.vue` 中，`isStreamComplete(message.id)` 是一个方法调用，不是计算属性。
  
  检查 `ThinkingSteps` 组件的 `:stream-complete` 绑定：
  ```html
  <thinking-steps
    v-if="getNodeSteps(message.id)"
    :steps="getNodeSteps(message.id)"
    :stream-complete="isStreamComplete(message.id)"
  />
  ```
  
  `isStreamComplete` 依赖 `workflowProcessNodes`（计算属性），当 `CLEAR_WORKFLOW_PROCESS_NODE` 触发 Vuex 状态变化时，Vue 会重新渲染，`streamComplete` prop 会更新为 `true`，触发 `ThinkingSteps` 的 `watch.streamComplete`。
  
  这个链路是正确的，Task 2 的修改应该能解决问题。

- [ ] **Step 3: 手动测试验证**

  启动前后端服务，执行以下测试场景：
  
  **场景1：单输出变量 End 节点**
  - 创建工作流：Start → Answer → End（End 节点只有 `output` 变量）
  - 执行工作流
  - 预期：前端显示 Answer 节点的输出内容，思考面板完成后自动折叠
  
  **场景2：多输出变量 End 节点**
  - 创建工作流：Start → Template → End（End 节点有多个输出变量）
  - 执行工作流
  - 预期：前端显示所有输出变量的值（用空行分隔），思考面板完成后自动折叠
  
  **场景3：思考面板折叠时序**
  - 执行任意工作流
  - 观察：工作流完成后，思考面板是否自动折叠（不需要手动点击）
  - 预期：所有步骤变为 done 后，面板自动折叠，显示"思考Xs · N个步骤"摘要

---

## 自审查

### Spec 覆盖检查

| 需求 | 对应 Task |
|------|----------|
| End节点所有输出变量都显示 | Task 1 |
| 工作流完成后思考面板自动折叠 | Task 2 |
| 向后兼容（不破坏现有功能） | Task 1 Step 2 的 `if ("End".equals(outputNodeName))` 保护 |

### 风险点

1. **End 节点输出变量顺序**：`JSONObject.keySet()` 的遍历顺序不保证。如果需要固定顺序，可以先按 key 排序。当前实现不排序，输出顺序取决于 JSON 解析顺序。
2. **多输出变量分隔符**：当前用 `\n\n`，如果某个变量值本身包含 `\n\n`，视觉上可能混淆。可以考虑用 `---` 分隔线，但这会改变现有单输出变量的行为（加了多余的分隔线）。保持 `\n\n` 是最简单的方案。
3. **ThinkingSteps 折叠时序**：Task 2 的修改假设 `streamComplete` prop 变化会触发 Vue 重渲染。这依赖 `workflowProcessNodes` 是响应式的（Vuex state），已确认是响应式的。
