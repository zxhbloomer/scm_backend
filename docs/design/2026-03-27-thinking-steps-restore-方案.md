# ThinkingSteps 关闭重开后节点还原问题 — 详细设计方案

**日期**: 2026-03-27
**问题**: 调用 agent 时关闭聊天再打开，"问题分析完成"和"调用agent"节点消失；且节点顺序与实时执行时不一致

---

## 根因分析

### 问题1：虚拟节点（"问题分析完成"/"调用agent"）丢失

**完整调用链**：
```
前端 SSE 实时流期间：
  ADD_WORKFLOW_VIRTUAL_STEP(__virtual_analysis__)   ← 立即插入
  ADD_WORKFLOW_VIRTUAL_STEP(__agent_call__)          ← runtime事件到达时插入
  onComplete → updateWorkflowSteps(含虚拟节点)       ← 正常完成时才执行

用户中途关闭聊天 → SSE断连 → onComplete不执行 → updateWorkflowSteps不触发

后端保存消息时（handleStreamComplete）：
  buildWorkflowStepsJson(runtimeId)
  → 从 ai_conversation_runtime_node ORDER BY id ASC 查询
  → __virtual_analysis__ / __agent_call__ 根本不在数据库
  → 保存的 workflow_steps 不含虚拟节点
```

**具体文件**：
- `WorkflowEngine.java` → SSE 流中断时 workflow_steps 未能包含虚拟节点
- `AiConversationContentService.java:buildWorkflowStepsJson()` → 不含虚拟节点的构建方法
- `AiConversationRuntimeNodeMapper.java:selectListByRuntimeId()` → 只查真实节点

**Orchestrator 节点特征**（`WorkflowRoutingService.java`）：
- `nodeId = 0`（`ORCHESTRATOR_MODE_WORKFLOW_ID = 0L`）
- `outputData.workerType = "orchestrator"` → 问题分析节点
- `outputData.workerType = "workflow"` → 调用具体工作流的 worker 节点
- `outputData.workerType = "synthesizer"` → 结果合并节点

但当前 `buildWorkflowStepsJson` 调用 `getComponentNameByNodeId(0L)` 返回 `"Unknown"`，这些 Orchestrator 节点完全被忽略了（即使存在也不能正确展示）。

### 问题2：节点顺序不一致

**根因**：`addNodeToStateGraph` 中每个节点都用 `CompletableFuture.supplyAsync()` 并行执行：
- 并行节点（如"项目管理知识库检索"和"临时知识库"在同一 fork 下）在不同线程中运行
- 谁先完成谁先 `createByState`（写数据库）+ 发 SSE `node_complete` 事件
- 两次执行中线程调度顺序可能不同 → 数据库 id 顺序与 SSE 事件顺序不一致
- `buildWorkflowStepsJson` 按 `ORDER BY id ASC` → 与实时顺序不匹配

---

## 修复方案

### Fix 1：`buildWorkflowStepsJson` 补充虚拟节点

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/chat/AiConversationContentService.java`

**改动位置**: `buildWorkflowStepsJson(Long runtimeId)` 方法（当前第343-377行）

**设计**：
在遍历真实节点之前，检测是否存在 `nodeId=0` 的 Orchestrator 节点：
- 若存在 `workerType="orchestrator"` 节点 → 在最前面插入 `__virtual_analysis__` 虚拟步骤（status=done）
- 若存在 `workerType="workflow"` 节点 → 在真实节点之前插入 `__agent_call__` 虚拟步骤（status=done，nodeTitle 取 workflow 节点的 `outputData.title` 字段）

**伪代码**：
```java
public String buildWorkflowStepsJson(Long runtimeId) {
    List<AiConversationRuntimeNodeVo> nodes = conversationRuntimeNodeService.listByWfRuntimeId(runtimeId);
    if (nodes == null || nodes.isEmpty()) return null;

    JSONArray stepsArray = new JSONArray();

    // 1. 判断是否有 Orchestrator 节点（nodeId=0）
    boolean hasOrchestrator = nodes.stream()
        .anyMatch(n -> Long.valueOf(0L).equals(n.getNodeId()));

    if (hasOrchestrator) {
        // 1a. 插入 __virtual_analysis__ 虚拟节点
        JSONObject analysisStep = new JSONObject();
        analysisStep.put("nodeUuid", "__virtual_analysis__");
        analysisStep.put("nodeName", "Classifier");
        analysisStep.put("nodeTitle", "问题分析");
        analysisStep.put("status", "done");
        analysisStep.put("duration", 0);
        stepsArray.add(analysisStep);

        // 1b. 找 workerType=workflow 的节点，插入 __agent_call__
        nodes.stream()
            .filter(n -> Long.valueOf(0L).equals(n.getNodeId()))
            .filter(n -> {
                JSONObject od = n.getOutputData();
                return od != null && "workflow".equals(od.getString("workerType"));
            })
            .findFirst()
            .ifPresent(workerNode -> {
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

    // 2. 遍历真实节点（跳过 nodeId=0 的 Orchestrator 节点）
    for (AiConversationRuntimeNodeVo node : nodes) {
        if (Long.valueOf(0L).equals(node.getNodeId())) continue; // 跳过 orchestrator 节点
        // ... 现有逻辑不变 ...
    }

    return stepsArray.toJSONString();
}
```

### Fix 2：节点排序稳定化

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/mapper/workflow/AiConversationRuntimeNodeMapper.java`

**改动位置**: `selectListByRuntimeId` SQL（当前第55-74行）

**改动**：`ORDER BY id ASC` → `ORDER BY c_time ASC, id ASC`

理由：`c_time` 是节点开始执行时的时间戳（`createByState` 时写入），反映了节点实际开始执行的顺序。两个真正并行的节点 `c_time` 相同时，用 `id ASC` 兜底，结果稳定。

**改后 SQL**：
```sql
SELECT id, runtime_node_uuid AS runtimeNodeUuid, ...
FROM ai_conversation_runtime_node
WHERE conversation_workflow_runtime_id = #{runtimeId}
ORDER BY c_time ASC, id ASC
```

---

## 影响范围

| 文件 | 改动类型 | 风险 |
|------|---------|------|
| `AiConversationContentService.java` | 修改 `buildWorkflowStepsJson` | 低：只影响历史消息重建，不影响实时执行 |
| `AiConversationRuntimeNodeMapper.java` | 修改 ORDER BY | 低：只影响节点列表查询顺序 |

**不需要改动**：
- 前端代码：`ThinkingSteps.vue` 已有 `__virtual_analysis__` 和 `__agent_call__` 的渲染逻辑
- `WorkflowEngine.java`：实时 SSE 路径不变
- `WorkflowRoutingService.java`：Orchestrator 执行逻辑不变

---

## KISS 原则检查

1. **这是个真问题？** ✅ 是，关闭再打开后节点丢失，用户体验差
2. **有更简单的方法？** ✅ 是，只改后端重建逻辑，2个文件，最小改动
3. **会破坏什么？** 无破坏：`buildWorkflowStepsJson` 只在历史消息加载时被调用；前端 `updateWorkflowSteps` 仍然覆盖保存（正常完成时优先级更高）
4. **当前项目真的需要？** ✅ 是，用户截图明确展示了这个问题

---

## 待办

- [ ] 用户审批此方案
- [ ] 实施 Fix 1（`buildWorkflowStepsJson` 补充虚拟节点）
- [ ] 实施 Fix 2（`selectListByRuntimeId` ORDER BY 稳定化）
- [ ] QA 代码评审
