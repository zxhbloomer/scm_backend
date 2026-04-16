# 去除 node_start 事件 Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 前端去掉对 `node_start` 事件的依赖，改为 `node_complete` 直接驱动步骤列表；后端清理死代码。

**Architecture:** 前端新增 `ADD_WORKFLOW_VIRTUAL_STEP` mutation 统一处理虚拟步骤插入；`SET_WORKFLOW_PROCESS_NODE` 只处理 `node_complete`，直接 push done 状态步骤；`FLUSH_PENDING_NODE_COMPLETE` 简化为只收尾 `__agent_call__`。后端删除 `resolveNextNodes`、`createNodeStartData` 两个死方法及过时注释。

**Tech Stack:** Vue 2 + Vuex（前端），Java 17 + Spring Boot（后端）

**注意：不操作 git**

---

## Chunk 1: 前端 mutations 改造

### Task 1: 新增 `ADD_WORKFLOW_VIRTUAL_STEP` mutation

**Files:**
- Modify: `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\components\70_ai\store\modules\chat.js`

- [ ] **Step 1: 在 `SET_WORKFLOW_PROCESS_NODE` 之前插入新 mutation**

在 `chat.js` 的 mutations 对象中，找到 `SET_WORKFLOW_PROCESS_NODE` 定义（约第 201 行），在其**前面**插入：

```javascript
ADD_WORKFLOW_VIRTUAL_STEP (state, { messageId, step }) {
  if (!state.workflowProcessNodes[messageId]) {
    state.workflowProcessNodes = { ...state.workflowProcessNodes, [messageId]: { steps: [] } }
  }
  const steps = state.workflowProcessNodes[messageId].steps
  // 去重：同一 nodeUuid 的虚拟步骤只插入一次
  if (!steps.find(s => s.nodeUuid === step.nodeUuid)) {
    steps.push(step)
  }
},
```

---

### Task 2: 重写 `SET_WORKFLOW_PROCESS_NODE` mutation

**Files:**
- Modify: `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\components\70_ai\store\modules\chat.js:201-239`

- [ ] **Step 1: 替换整个 mutation 实现**

将第 201-239 行的 `SET_WORKFLOW_PROCESS_NODE` 完整替换为：

```javascript
SET_WORKFLOW_PROCESS_NODE (state, { messageId, nodeEvent }) {
  if (!state.workflowProcessNodes[messageId]) {
    // 注意：不再有 pendingCompletes 字段
    state.workflowProcessNodes = { ...state.workflowProcessNodes, [messageId]: { steps: [] } }
  }
  const processData = state.workflowProcessNodes[messageId]
  const steps = processData.steps

  if (nodeEvent.nodeEventType === 'node_complete') {
    // 处理虚拟"问题分析"步骤：第一个真实节点完成时，将虚拟步骤标记完成
    const virtualIdx = steps.findIndex(s => s.nodeUuid === '__virtual_analysis__')
    if (virtualIdx !== -1 && steps[virtualIdx].status !== 'done') {
      steps[virtualIdx].status = 'done'
      steps[virtualIdx].duration = nodeEvent.nodeTimestamp - steps[virtualIdx].timestamp
    }
    // 直接加入步骤列表并标为 done
    // 注意：后端 node_complete 不带 depth 字段，统一用 depth:1
    steps.push({
      nodeUuid: nodeEvent.nodeUuid,
      nodeName: nodeEvent.nodeName,
      nodeTitle: nodeEvent.nodeTitle,
      status: 'done',
      timestamp: nodeEvent.nodeTimestamp,
      duration: nodeEvent.nodeDuration || null,
      summary: nodeEvent.nodeSummary || null,
      depth: 1
    })
  }
},
```

---

### Task 3: 简化 `FLUSH_PENDING_NODE_COMPLETE` mutation

**Files:**
- Modify: `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\components\70_ai\store\modules\chat.js:242-260`

- [ ] **Step 1: 替换 mutation 实现，删除 pendingCompletes 遍历逻辑**

将 `FLUSH_PENDING_NODE_COMPLETE` 的实现替换为（保留注释行和 `__agent_call__` 收尾逻辑，删除 pendingCompletes 相关代码）：

```javascript
// 刷新缓冲的node_complete（流结束时调用，确保最后一步正确标记为done）
FLUSH_PENDING_NODE_COMPLETE (state, messageId) {
  const processData = state.workflowProcessNodes[messageId]
  // 标记 agent 包裹行完成（__agent_call__ 虚拟步骤在流结束时收尾）
  if (processData) {
    const agentStep = processData.steps.find(s => s.nodeUuid === '__agent_call__')
    if (agentStep && agentStep.status !== 'done') {
      agentStep.status = 'done'
    }
  }
},
```

---

## Chunk 2: 前端 actions 改造

### Task 4: 修改 `sendMessage` action 中的虚拟步骤创建

**Files:**
- Modify: `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\components\70_ai\store\modules\chat.js:362-372`

- [ ] **Step 1: 将 commit node_start 假事件改为 commit ADD_WORKFLOW_VIRTUAL_STEP**

找到约第 362-372 行：
```javascript
// 立即显示"深度思考 · 问题分析中..."（虚拟步骤，等真实Classifier事件替换）
commit('SET_WORKFLOW_PROCESS_NODE', {
  messageId: aiMessageId,
  nodeEvent: {
    nodeEventType: 'node_start',
    nodeUuid: '__virtual_analysis__',
    nodeName: 'Classifier',
    nodeTitle: '问题分析',
    nodeTimestamp: Date.now()
  }
})
```

替换为：
```javascript
// 立即显示"深度思考 · 问题分析中..."（虚拟步骤，等真实节点事件到来后标记完成）
commit('ADD_WORKFLOW_VIRTUAL_STEP', {
  messageId: aiMessageId,
  step: {
    nodeUuid: '__virtual_analysis__',
    nodeName: 'Classifier',
    nodeTitle: '问题分析',
    status: 'running',
    timestamp: Date.now(),
    duration: null,
    summary: null,
    depth: 1
  }
})
```

---

### Task 5: 修改 `sendMessage` action 中的 onNodeEvent 回调

**Files:**
- Modify: `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\components\70_ai\store\modules\chat.js:407-426`

- [ ] **Step 1: 修改 runtime 分支和 node_complete 过滤**

找到约第 407-426 行的 `onNodeEvent` 回调：
```javascript
onNodeEvent: (nodeEvent) => {
  if (nodeEvent.nodeEventType === 'runtime') {
    // runtime事件：插入"调用agent：xxx"包裹行
    commit('SET_WORKFLOW_PROCESS_NODE', {
      messageId: aiMessageId,
      nodeEvent: {
        nodeEventType: 'node_start',
        nodeUuid: '__agent_call__',
        nodeName: 'AgentCall',
        nodeTitle: nodeEvent.workflowTitle || '工作流',
        nodeTimestamp: Date.now()
      }
    })
    return
  }
  // node_start / node_complete 事件
  commit('SET_WORKFLOW_PROCESS_NODE', {
    messageId: aiMessageId,
    nodeEvent
  })
},
```

替换为：
```javascript
onNodeEvent: (nodeEvent) => {
  if (nodeEvent.nodeEventType === 'runtime') {
    // runtime事件：插入"调用agent：xxx"包裹行
    commit('ADD_WORKFLOW_VIRTUAL_STEP', {
      messageId: aiMessageId,
      step: {
        nodeUuid: '__agent_call__',
        nodeName: 'AgentCall',
        nodeTitle: nodeEvent.workflowTitle || '工作流',
        status: 'running',
        timestamp: Date.now(),
        duration: null,
        summary: null,
        depth: 1
      }
    })
    return
  }
  // 只处理 node_complete 事件
  if (nodeEvent.nodeEventType === 'node_complete') {
    commit('SET_WORKFLOW_PROCESS_NODE', {
      messageId: aiMessageId,
      nodeEvent
    })
  }
},
```

---

### Task 6: 修改 `executeWorkflowCommand` action 中的 onNodeEvent 过滤

**Files:**
- Modify: `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\components\70_ai\store\modules\chat.js:801-807`

- [ ] **Step 1: 去掉 node_start 过滤条件**

找到约第 801-807 行：
```javascript
onNodeEvent: (nodeEvent) => {
  if (nodeEvent.nodeEventType === 'node_start' || nodeEvent.nodeEventType === 'node_complete') {
    commit('SET_WORKFLOW_PROCESS_NODE', {
      messageId: aiMessageId,
      nodeEvent
    })
  }
},
```

替换为：
```javascript
onNodeEvent: (nodeEvent) => {
  if (nodeEvent.nodeEventType === 'node_complete') {
    commit('SET_WORKFLOW_PROCESS_NODE', {
      messageId: aiMessageId,
      nodeEvent
    })
  }
},
```

---

## Chunk 3: 后端死代码清理

### Task 7: 删除 WorkflowEngine 中的 resolveNextNodes 死方法

**Files:**
- Modify: `D:\2025_project\20_project_in_github\00_scm_backend\scm_backend\scm-ai\src\main\java\com\xinyirun\scm\ai\workflow\WorkflowEngine.java`

- [ ] **Step 1: 搜索并删除 resolveNextNodes 方法**

搜索 `resolveNextNodes` 方法定义，完整删除该方法（包括 Javadoc 注释）。

- [ ] **Step 2: 搜索并更新 NodeEventListener.before() 附近的过时注释**

搜索 `node_start 预告` 或类似描述旧逻辑的注释，删除或更新为准确描述。

---

### Task 8: 删除 WorkflowEventVo 中的 createNodeStartData 死方法

**Files:**
- Modify: `D:\2025_project\20_project_in_github\00_scm_backend\scm_backend\scm-ai\src\main\java\com\xinyirun\scm\ai\bean\vo\workflow\WorkflowEventVo.java`

- [ ] **Step 1: 搜索并删除 createNodeStartData 方法**

搜索 `createNodeStartData` 方法定义，完整删除该方法（包括 Javadoc 注释）。

---

### Task 9: 全局验证 node_start 残留

**Files:**
- 全局搜索

- [ ] **Step 1: 搜索 scm-ai 模块下的 node_start 残留**

在 `scm-ai` 目录下搜索 `node_start`、`NODE_START`、`createNodeStartData`，确认无残留调用点（注释除外）。

- [ ] **Step 2: 搜索前端的 node_start 残留**

在前端 `src/components/70_ai` 目录下搜索 `node_start`，确认只剩注释，无逻辑代码引用。
