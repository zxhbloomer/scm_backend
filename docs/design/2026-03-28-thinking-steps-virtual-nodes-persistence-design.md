# ThinkingSteps 虚拟节点持久化 + Worker 节点状态修复 — 设计方案

**日期**: 2026-03-28
**问题1**: 流程结束后，Worker 节点还显示执行状态（应该显示完成或失败）
**问题2**: 关闭聊天再打开，虚拟节点（"问题分析完成"/"调用agent"）消失

---

## 根因分析

### 问题1：Worker 节点状态永远是"成功"

**调用链**：
```
WorkflowRoutingService.orchestrateAndExecute()
  → 第409行: String result = executeWorker(task, ...)
  → 第413行: updateWorkerNodeRecord(nodeRecordId, result, true, null, tenantCode)
                                                           ↑
                                                    硬编码 true
```

**executeWorker() 返回值**：
- 成功时：workflow/MCP 工具的返回值（可能是任意 JSON）
- 失败时：`{"success": false, "error": "错误信息"}`（第802/832/857/862行）

**问题**：第413行硬编码 `success=true`，没有解析 `result` JSON 中的 `success` 字段，导致即使 worker 执行失败，数据库 status 也是 3（成功）。

### 问题2：虚拟节点丢失

**前端虚拟节点机制**：
- `chat.js` 第377行：发送消息时立即创建 `__virtual_analysis__` 虚拟步骤（前端内存）
- `chat.js` 第427行：收到 `runtime` SSE 事件时创建 `__agent_call__` 虚拟步骤（前端内存）
- `chat.js` 第586行：SSE 流正常结束时调用 `updateWorkflowSteps()` API 保存到数据库

**问题**：用户中途关闭聊天窗口 → SSE 断连 → `onComplete` 不触发 → `updateWorkflowSteps()` 不调用 → 虚拟节点只存在于前端内存，未持久化到数据库。

**正确架构**：虚拟节点本质上是 Orchestrator 的执行记录，应该在后端 Orchestrator 执行时就写入数据库，前端只负责展示。

---

## 修复方案

### Fix 1：Worker 节点状态正确更新

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/WorkflowRoutingService.java`

**改动位置**: `orchestrateAndExecute()` 方法第409-413行

**改动**：
```java
// 原代码（第409-413行）
String result = executeWorker(task, userId, tenantCode, pageContext, userInput);
workerResults.add(result);

// 【日志记录】更新节点记录(执行后)
updateWorkerNodeRecord(nodeRecordId, result, true, null, tenantCode);

// 改为：
String result = executeWorker(task, userId, tenantCode, pageContext, userInput);
workerResults.add(result);

// 【日志记录】更新节点记录(执行后) - 解析 result JSON 提取 success 字段
boolean success = true;
String errorMsg = null;
try {
    JSONObject resultJson = JSON.parseObject(result);
    if (resultJson.containsKey("success")) {
        success = resultJson.getBooleanValue("success");
    }
    if (!success && resultJson.containsKey("error")) {
        errorMsg = resultJson.getString("error");
    }
} catch (Exception e) {
    // result 不是 JSON 或解析失败，默认认为成功
    log.debug("【Worker】result 不是 JSON 格式，默认认为成功: {}", result);
}
updateWorkerNodeRecord(nodeRecordId, result, success, errorMsg, tenantCode);
```

### Fix 2：虚拟节点持久化到数据库

#### 2.1 后端：`WorkflowRoutingService.java` 新增虚拟节点写入

**改动位置**: `orchestrateAndExecute()` 方法第390行之后

**改动**：
```java
// 第390行之后插入
// 【虚拟节点】创建"问题分析完成"节点
Long virtualAnalysisNodeId = createVirtualAnalysisNodeRecord(runtimeId, userId, tenantCode);
log.info("【日志记录】创建虚拟分析节点: nodeId={}", virtualAnalysisNodeId);

// 【虚拟节点】为每个 workflow 类型的子任务创建"调用agent"节点
int agentCallIndex = 0;
for (SubTask task : orchestratorResponse.tasks()) {
    if ("workflow".equals(task.type())) {
        Long virtualAgentCallNodeId = createVirtualAgentCallNodeRecord(
            runtimeId, task.description(), agentCallIndex, userId, tenantCode);
        log.info("【日志记录】创建虚拟AgentCall节点: nodeId={}, title={}",
            virtualAgentCallNodeId, task.description());
        agentCallIndex++;
    }
}
```

**新增两个私有方法**（在 `createOrchestratorNodeRecord()` 方法后面）：

```java
/**
 * 创建虚拟"问题分析完成"节点
 *
 * @param runtimeId 主运行记录ID
 * @param userId 用户ID
 * @param tenantCode 租户代码
 * @return 节点记录ID
 */
private Long createVirtualAnalysisNodeRecord(Long runtimeId, Long userId, String tenantCode) {
    DataSourceHelper.use(tenantCode);

    AiConversationRuntimeNodeEntity nodeRecord = new AiConversationRuntimeNodeEntity();
    nodeRecord.setRuntimeNodeUuid("__virtual_analysis__");
    nodeRecord.setConversationWorkflowRuntimeId(runtimeId);
    nodeRecord.setNodeId(ORCHESTRATOR_MODE_WORKFLOW_ID);

    JSONObject outputData = new JSONObject();
    outputData.put("nodeTitle", "问题分析");
    outputData.put("workerType", "virtual_analysis");
    nodeRecord.setOutputData(outputData.toJSONString());

    nodeRecord.setStatus(WorkflowConstants.NODE_PROCESS_STATUS_SUCCESS);
    nodeRecord.setC_id(userId);
    nodeRecord.setU_id(userId);

    conversationRuntimeNodeMapper.insert(nodeRecord);
    return nodeRecord.getId();
}

/**
 * 创建虚拟"调用agent"节点
 *
 * @param runtimeId 主运行记录ID
 * @param workflowTitle 工作流标题
 * @param index 索引（用于多个 workflow 时区分）
 * @param userId 用户ID
 * @param tenantCode 租户代码
 * @return 节点记录ID
 */
private Long createVirtualAgentCallNodeRecord(Long runtimeId, String workflowTitle,
                                                int index, Long userId, String tenantCode) {
    DataSourceHelper.use(tenantCode);

    AiConversationRuntimeNodeEntity nodeRecord = new AiConversationRuntimeNodeEntity();
    nodeRecord.setRuntimeNodeUuid("__agent_call__" + (index > 0 ? "_" + index : ""));
    nodeRecord.setConversationWorkflowRuntimeId(runtimeId);
    nodeRecord.setNodeId(ORCHESTRATOR_MODE_WORKFLOW_ID);

    JSONObject outputData = new JSONObject();
    outputData.put("nodeTitle", workflowTitle != null ? workflowTitle : "工作流");
    outputData.put("workerType", "virtual_agent_call");
    nodeRecord.setOutputData(outputData.toJSONString());

    nodeRecord.setStatus(WorkflowConstants.NODE_PROCESS_STATUS_SUCCESS);
    nodeRecord.setC_id(userId);
    nodeRecord.setU_id(userId);

    conversationRuntimeNodeMapper.insert(nodeRecord);
    return nodeRecord.getId();
}
```

#### 2.2 后端：`AiConversationContentService.java` 修改检测逻辑

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/chat/AiConversationContentService.java`

**改动位置**: `buildWorkflowStepsJson()` 方法第355-389行

**改动**：将检测 `workerType="orchestrator"` 和 `workerType="workflow"` 的逻辑改为检测 `workerType="virtual_analysis"` 和 `workerType="virtual_agent_call"`

```java
// 原代码（第355-389行）
// 检测是否存在 Orchestrator 节点（nodeId=0），若有则在列表最前面补充虚拟步骤
// Orchestrator 节点不在 ai_workflow_node 表中，outputData.workerType 标记其类型
boolean hasOrchestratorNode = nodes.stream()
        .anyMatch(n -> Long.valueOf(0L).equals(n.getNodeId()));

if (hasOrchestratorNode) {
    // 补充 __virtual_analysis__ 虚拟步骤（对应前端"问题分析完成"）
    JSONObject analysisStep = new JSONObject();
    analysisStep.put("nodeUuid", "__virtual_analysis__");
    analysisStep.put("nodeName", "Classifier");
    analysisStep.put("nodeTitle", "问题分析");
    analysisStep.put("status", "done");
    analysisStep.put("duration", 0);
    stepsArray.add(analysisStep);

    // 补充 __agent_call__ 虚拟步骤（对应前端"调用agent：xxx"）
    // 数据来源：workerType="workflow" 的 Orchestrator 节点，outputData.title 为工作流名称
    // Orchestrator 可能分解为多个子工作流，逐个追加对应的 __agent_call__ 步骤
    nodes.stream()
            .filter(n -> Long.valueOf(0L).equals(n.getNodeId()))
            .filter(n -> {
                JSONObject od = n.getOutputData();
                return od != null && "workflow".equals(od.getString("workerType"));
            })
            .forEach(workerNode -> {
                String workflowTitle = workerNode.getOutputData().getString("title");
                JSONObject agentCallStep = new JSONObject();
                agentCallStep.put("nodeUuid", "__agent_call__");
                agentCallStep.put("nodeName", "AgentCall");
                agentCallStep.put("nodeTitle", workflowTitle != null ? workflowTitle : "工作流");
                agentCallStep.put("status", "done");
                agentCallStep.put("duration", 0);
                stepsArray.add(agentCallStep);
            });
}

// 改为：
// 检测是否存在虚拟节点（workerType="virtual_analysis" 或 "virtual_agent_call"）
boolean hasVirtualNodes = nodes.stream()
        .anyMatch(n -> {
            JSONObject od = n.getOutputData();
            if (od == null) return false;
            String workerType = od.getString("workerType");
            return "virtual_analysis".equals(workerType) || "virtual_agent_call".equals(workerType);
        });

if (hasVirtualNodes) {
    // 直接遍历所有虚拟节点，按 c_time 顺序添加（已在 Mapper 中 ORDER BY c_time ASC）
    nodes.stream()
        .filter(n -> {
            JSONObject od = n.getOutputData();
            if (od == null) return false;
            String workerType = od.getString("workerType");
            return "virtual_analysis".equals(workerType) || "virtual_agent_call".equals(workerType);
        })
        .forEach(virtualNode -> {
            JSONObject step = new JSONObject();
            step.put("nodeUuid", virtualNode.getRuntimeNodeUuid());

            String workerType = virtualNode.getOutputData().getString("workerType");
            if ("virtual_analysis".equals(workerType)) {
                step.put("nodeName", "Classifier");
            } else {
                step.put("nodeName", "AgentCall");
            }

            step.put("nodeTitle", virtualNode.getOutputData().getString("nodeTitle"));
            step.put("status", "done");
            // 耗时：u_time - c_time（毫秒）
            long duration = 0;
            if (virtualNode.getC_time() != null && virtualNode.getU_time() != null) {
                duration = java.time.Duration.between(virtualNode.getC_time(), virtualNode.getU_time()).toMillis();
            }
            step.put("duration", duration);
            stepsArray.add(step);
        });
}
```

#### 2.3 前端：`chat.js` 删除虚拟节点创建代码

**文件**: `scm-frontend/src/components/70_ai/store/modules/chat.js`

**改动1**: 删除第377-389行（`ADD_WORKFLOW_VIRTUAL_STEP` for `__virtual_analysis__`）

```javascript
// 删除这段（第377-389行）
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

**改动2**: 删除第424-441行（`onNodeEvent` 中的 `runtime` 事件处理）

```javascript
// 删除这段（第424-441行）
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
  // ... 其他事件处理保留
}

// 改为：
onNodeEvent: (nodeEvent) => {
  // 删除 runtime 事件处理，虚拟节点由后端直接写入数据库
  if (nodeEvent.nodeEventType === 'node_running') {
    commit('SET_WORKFLOW_NODE_RUNNING', { messageId: aiMessageId, nodeEvent })
    return
  }
  if (nodeEvent.nodeEventType === 'node_complete') {
    commit('SET_WORKFLOW_PROCESS_NODE', {
      messageId: aiMessageId,
      nodeEvent
    })
  }
}
```

---

## 影响范围

| 文件 | 改动类型 | 风险 |
|------|---------|------|
| `WorkflowRoutingService.java` | 修改 `orchestrateAndExecute()`，新增2个私有方法 | 中：影响 Orchestrator 核心逻辑，需要充分测试 |
| `AiConversationContentService.java` | 修改 `buildWorkflowStepsJson()` | 低：只影响历史消息重建，不影响实时执行 |
| `chat.js` (前端) | 删除虚拟节点创建代码 | 低：删除代码，简化逻辑 |

**不需要改动**：
- `ThinkingSteps.vue`：前端渲染逻辑不变，仍然识别 `__virtual_analysis__` 和 `__agent_call__`
- `AiConversationRuntimeNodeMapper.java`：已在之前修改为 `ORDER BY c_time ASC, id ASC`

---

## 验证方案

### 验证 Fix 1（Worker 节点状态）

1. 构造一个会失败的 workflow（例如：Start 节点参数缺失）
2. 通过 Orchestrator 调用该 workflow
3. 查询数据库 `ai_conversation_runtime_node` 表，检查 Worker 节点的 `status` 字段：
   - 预期：`status=4`（失败）
   - 预期：`status_remark` 包含错误信息

### 验证 Fix 2（虚拟节点持久化）

1. 发送消息触发 Orchestrator 调用
2. **在 SSE 流还在执行时关闭聊天窗口**
3. 重新打开聊天窗口
4. 检查 ThinkingSteps 面板：
   - 预期：显示"问题分析完成"节点
   - 预期：显示"调用agent：xxx"节点
5. 查询数据库 `ai_conversation_runtime_node` 表：
   - 预期：存在 `runtime_node_uuid="__virtual_analysis__"` 的记录
   - 预期：存在 `runtime_node_uuid="__agent_call__"` 的记录（或 `__agent_call___1` 等）

---

## KISS 原则检查

1. **这是个真问题？** ✅ 是，用户截图明确展示了这两个问题
2. **有更简单的方法？** ✅ 是，去掉前端虚拟节点机制，后端直接写入数据库，架构更清晰
3. **会破坏什么？** 低风险：虚拟节点从前端内存移到后端数据库，前端渲染逻辑不变；Worker 状态修复只是解析 JSON，不改变执行流程
4. **当前项目真的需要？** ✅ 是，用户明确要求"关闭再打开后显示必须和关闭前一致"

---

## 待办

- [ ] 用户审批此方案
- [ ] 实施 Fix 1（Worker 节点状态正确更新）
- [ ] 实施 Fix 2.1（后端虚拟节点写入）
- [ ] 实施 Fix 2.2（后端检测逻辑修改）
- [ ] 实施 Fix 2.3（前端删除虚拟节点代码）
- [ ] 验证 Fix 1（构造失败场景）
- [ ] 验证 Fix 2（中途关闭聊天窗口）
- [ ] QA 代码评审
