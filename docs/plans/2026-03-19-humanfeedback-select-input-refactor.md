# HumanFeedback 节点 select 选项改用标准输入映射 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 删除 HumanFeedback 节点的 `dynamicOptionsParam`/`optionsSource` 字段，改为与其他节点一致的 `ref_inputs` 输入映射方式，让 select/table_select 类型的选项数据通过标准连线从上游节点流入。

**Architecture:** 前端属性面板的"动态选项-上游参数名"文本框替换为 `NodePropertyInput` 组件；后端 `resolveSelectOptions` 改为从节点的 `state.getInputs()` 中读取名为 `options` 的输入参数；数据库中已有的节点配置同步更新。

**Tech Stack:** Vue 2 + Element UI（前端）、Spring Boot + Java 17（后端）、MySQL（数据库）

---

## 文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `scm-ai/.../workflow/WfState.java` | 修改 | 新增 `pendingNodeInputs` map，供 HumanFeedback 节点暂存已初始化的 inputs |
| `scm-ai/.../node/humanfeedback/HumanFeedbackNode.java` | 修改 | `onProcess()` 设置等待标志时，把 `state.getInputs()` 存入 `wfState.pendingNodeInputs` |
| `scm-ai/.../node/humanfeedback/HumanFeedbackNodeConfig.java` | 修改 | 删除 `optionsSource`、`dynamicOptionsParam` 字段（已完成） |
| `scm-ai/.../workflow/WorkflowEngine.java` | 修改 | `resolveSelectOptions` 改为从 `wfState.pendingNodeInputs` 读 `options` 参数 |
| `src/components/70_ai/.../properties/HumanFeedbackNodeProperty.vue` | 修改 | select/table_select 动态模式改用 `NodePropertyInput`，删除"上游参数名"文本框 |
| `src/components/70_ai/.../utils/workflowUtil.js` | 无需修改 | `createHumanFeedback` 默认配置已干净 |
| 数据库 `ai_workflow_node` | 数据修复 | 更新 `EN571rPYAkVAAHGixiwt4sz0gc5HmUn` 节点的 `input_config` 和 `node_config` |

---

## Task 1：WfState 新增 pendingNodeInputs，HumanFeedbackNode 写入

> **背景**：`resolveSelectOptions` 在 `buildInteractionParams` 中调用，此时 HumanFeedback 节点尚未进入 `completedNodes`（`onProcess()` 直接 return，未走到 `completedNodes.add(this)`），但 `initInput()` 已在 `onProcess()` 之前执行，`state.getInputs()` 已有数据。
> 解决方案：在 `WfState` 加一个临时 map，由 `HumanFeedbackNode.onProcess()` 在设置等待标志时写入，`resolveSelectOptions` 从此 map 读取。

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WfState.java`
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/humanfeedback/HumanFeedbackNode.java`

- [ ] **Step 1: WfState.java 新增 `pendingNodeInputs` 字段和存取方法**

在 `WfState.java` 的字段区域（`parallelInterruptFired` 字段附近）新增：

```java
/**
 * 人机交互节点暂存的输入参数（nodeUuid → inputs）
 * HumanFeedbackNode.onProcess() 设置等待标志时写入，供 resolveSelectOptions 读取
 * 原因：节点中断时尚未进入 completedNodes，但 initInput() 已执行完毕
 */
private final Map<String, List<NodeIOData>> pendingNodeInputs = new ConcurrentHashMap<>();

public void savePendingNodeInputs(String nodeUuid, List<NodeIOData> inputs) {
    pendingNodeInputs.put(nodeUuid, new ArrayList<>(inputs));
}

public List<NodeIOData> getPendingNodeInputs(String nodeUuid) {
    return pendingNodeInputs.getOrDefault(nodeUuid, List.of());
}
```

- [ ] **Step 2: HumanFeedbackNode.java 的 `onProcess()` 在设置等待标志前写入 inputs**

找到 `onProcess()` 中设置等待标志的代码（约第 39-43 行）：
```java
log.info("人机交互节点等待用户输入（并行路径），设置等待标志, nodeUuid: {}", node.getUuid());
wfState.setWaitingInteraction(true);
return NodeProcessResult.builder().content(List.of()).build();
```

改为：
```java
log.info("人机交互节点等待用户输入，设置等待标志, nodeUuid: {}", node.getUuid());
// 暂存已初始化的 inputs，供 resolveSelectOptions 读取（此时节点尚未进入 completedNodes）
wfState.savePendingNodeInputs(node.getUuid(), state.getInputs());
wfState.setWaitingInteraction(true);
return NodeProcessResult.builder().content(List.of()).build();
```

- [ ] **Step 3: git commit**
```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WfState.java scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/humanfeedback/HumanFeedbackNode.java
git -C 00_scm_backend/scm_backend commit -m "refactor(ai): WfState新增pendingNodeInputs，HumanFeedbackNode暂存inputs供resolveSelectOptions读取"
```

---

## Task 2：WorkflowEngine.java 改写 resolveSelectOptions

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`（`resolveSelectOptions` 方法，约第 975 行）

- [ ] **Step 1: 将 `resolveSelectOptions` 改为接收 `nodeUuid` 参数，从 `pendingNodeInputs` 读取**

```java
/**
 * 解析select选项
 * 优先从节点输入参数（ref_inputs映射）中读取名为"options"的参数
 * 回退到静态配置的options列表
 */
private List<HumanFeedbackNodeConfig.SelectOption> resolveSelectOptions(
        HumanFeedbackNodeConfig config, String nodeUuid) {
    // 从节点暂存的 inputs 中读取（通过 ref_inputs 连线映射进来的）
    List<NodeIOData> inputs = wfState.getPendingNodeInputs(nodeUuid);
    for (NodeIOData input : inputs) {
        if ("options".equals(input.getName())) {
            try {
                String jsonStr = input.valueToString();
                List<HumanFeedbackNodeConfig.SelectOption> result =
                    com.alibaba.fastjson2.JSON.parseArray(jsonStr, HumanFeedbackNodeConfig.SelectOption.class);
                if (result != null && !result.isEmpty()) {
                    return result;
                }
            } catch (Exception e) {
                log.warn("解析输入选项失败, nodeUuid={}, error={}", nodeUuid, e.getMessage());
            }
        }
    }
    // 回退到静态选项
    return config.getOptions() != null ? config.getOptions() : List.of();
}
```

- [ ] **Step 2: 更新两处调用点（约第 951、955 行），传入节点 UUID**

```java
// select 类型
params.put("options", resolveSelectOptions(config, nextInterruptNode.getUuid()));
// table_select 类型
params.put("options", resolveSelectOptions(config, nextInterruptNode.getUuid()));
```

> 注意：`handleInterruption` 和 `handleParallelInterruption` 各有一处调用，两处都要改。`nextInterruptNode` 在两个方法中都是 `String` 类型（UUID），直接传入即可。

- [ ] **Step 3: 删除旧的 `dynamicOptionsParam` 相关注释，确认编译无报错**

- [ ] **Step 4: git commit**
```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java
git -C 00_scm_backend/scm_backend commit -m "refactor(ai): resolveSelectOptions改为从pendingNodeInputs读取options参数"
```

---

## Task 3：后端 HumanFeedbackNodeConfig.java 确认清理完成

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/humanfeedback/HumanFeedbackNodeConfig.java`

- [ ] **Step 1: 确认 `optionsSource` 和 `dynamicOptionsParam` 字段已删除**

当前文件已在上一次会话中删除这两个字段，确认文件内容正确：
- 无 `optionsSource` 字段
- 无 `dynamicOptionsParam` 字段
- `options` 字段保留（静态选项回退用）

- [ ] **Step 2: git commit**
```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/humanfeedback/HumanFeedbackNodeConfig.java
git -C 00_scm_backend/scm_backend commit -m "refactor(ai): HumanFeedbackNodeConfig删除dynamicOptionsParam和optionsSource字段"
```

---

## Task 3：前端 HumanFeedbackNodeProperty.vue 改用 NodePropertyInput

**Files:**
- Modify: `src/components/70_ai/components/workflow/components/properties/HumanFeedbackNodeProperty.vue`

- [ ] **Step 1: 在 `<template>` 顶部（`<div class="human-feedback-node-property">` 内第一行）加入 `NodePropertyInput`**

```vue
<!-- 输入变量（select/table_select动态选项通过此处连线映射） -->
<node-property-input :workflow="workflow" :wf-node="wfNode" />
```

- [ ] **Step 2: 删除 select 类型的"选项来源"单选组和"动态选项-上游参数名"文本框**

删除以下代码块（约第 61-126 行）：
```vue
<!-- 选项来源 -->
<div class="property-section">
  <div class="section-title">选项来源</div>
  <el-radio-group v-model="nodeConfig.optionsSource" size="small">
    <el-radio-button label="static">静态配置</el-radio-button>
    <el-radio-button label="dynamic">动态获取</el-radio-button>
  </el-radio-group>
</div>

<!-- 动态选项 -->
<template v-if="nodeConfig.optionsSource === 'dynamic'">
  <div class="property-section">
    <div class="section-title">上游参数名 ...</div>
    <el-input v-model="nodeConfig.dynamicOptionsParam" ... />
  </div>
</template>
```

保留静态选项列表部分（`v-if="nodeConfig.optionsSource === 'static'"` 改为直接显示，去掉条件判断）：
```vue
<!-- select 类型配置 -->
<template v-if="nodeConfig.interactionType === 'select'">
  <div class="property-section">
    <div class="section-title">静态选项列表</div>
    <!-- 选项列表保持不变 -->
    ...
  </div>
</template>
```

- [ ] **Step 3: 同样处理 table_select 类型，删除其"选项来源"和"上游参数名"配置**

table_select 的动态选项同样改为通过 `NodePropertyInput` 的 `ref_inputs` 映射，删除约第 132-164 行的"选项来源"和"上游参数名"配置块。

- [ ] **Step 4: 在 `<script>` 中 import 并注册 `NodePropertyInput`**

```js
import NodePropertyInput from '../NodePropertyInput.vue'

export default {
  components: { NodePropertyInput },
  ...
}
```

- [ ] **Step 5: 在 `nodeConfig` computed 中删除 `optionsSource` 和 `dynamicOptionsParam` 的初始化**

删除以下两段代码：
```js
if (config.optionsSource === undefined) {
  this.$set(config, 'optionsSource', 'static')
}
if (config.dynamicOptionsParam === undefined) {
  this.$set(config, 'dynamicOptionsParam', '')
}
```

- [ ] **Step 6: git commit**
```bash
git -C 01_scm_frontend/scm_frontend add src/components/70_ai/components/workflow/components/properties/HumanFeedbackNodeProperty.vue
git -C 01_scm_frontend/scm_frontend commit -m "refactor(ai): HumanFeedbackNodeProperty改用NodePropertyInput替代dynamicOptionsParam"
```

---

## Task 4：前端 workflowUtil.js 清理默认配置

**Files:**
- Modify: `src/components/70_ai/components/workflow/utils/workflowUtil.js`（约第 584 行）

- [ ] **Step 1: `createHumanFeedback` 函数无需改动**

当前默认配置只有 `tip: ''`，不含 `optionsSource`/`dynamicOptionsParam`，已经是干净状态，无需修改。

---

## Task 5：数据库修复已有节点配置

> 修复数据库中 `EN571rPYAkVAAHGixiwt4sz0gc5HmUn`（多条商品选择1条）节点的配置，使其通过 `ref_inputs` 从上游节点（`f9Ove9j9pEo4HEkvGaDGYZRMrWqdNMv`，大模型处理：多条数据）获取选项数据。

**Files:**
- 数据库 `ai_workflow_node` 表

- [ ] **Step 1: 确认上游节点（大模型处理：多条数据）的输出参数名**

Answer 节点的默认输出参数名是 `output`（框架约定）。通过查看该节点的 prompt 配置确认其输出格式为 `[{key, label, data}]` 数组，参数名为 `output`。

```bash
docker exec mysql8 mysql -uroot -p123456 scm_tenant_20250519_001 -e "SELECT uuid, title, JSON_EXTRACT(node_config, '$.prompt') as prompt FROM ai_workflow_node WHERE uuid = 'f9Ove9j9pEo4HEkvGaDGYZRMrWqdNMv'\G"
```

确认 prompt 中要求输出 `[{key, label, data}]` 格式，且参数名为 `output`。

- [ ] **Step 2: 更新 `input_config`，加入 ref_inputs 映射**

```bash
docker exec mysql8 mysql -uroot -p123456 scm_tenant_20250519_001 -e "UPDATE ai_workflow_node SET input_config = JSON_SET(input_config, '$.ref_inputs', JSON_ARRAY(JSON_OBJECT('uuid', 'options_ref_input_001', 'name', 'options', 'node_uuid', 'f9Ove9j9pEo4HEkvGaDGYZRMrWqdNMv', 'node_param_name', 'output'))) WHERE uuid = 'EN571rPYAkVAAHGixiwt4sz0gc5HmUn'"
```

- [ ] **Step 3: 清理 `node_config` 中的旧字段**

```bash
docker exec mysql8 mysql -uroot -p123456 scm_tenant_20250519_001 -e "UPDATE ai_workflow_node SET node_config = JSON_REMOVE(JSON_REMOVE(node_config, '$.optionsSource'), '$.dynamicOptionsParam') WHERE uuid = 'EN571rPYAkVAAHGixiwt4sz0gc5HmUn'"
```

- [ ] **Step 4: 验证结果**

```bash
docker exec mysql8 mysql -uroot -p123456 scm_tenant_20250519_001 -e "SELECT uuid, title, input_config, node_config FROM ai_workflow_node WHERE uuid = 'EN571rPYAkVAAHGixiwt4sz0gc5HmUn'\G"
```

期望结果：
- `input_config.ref_inputs` 包含一条映射：`name=options, node_uuid=f9Ove9j9pEo4HEkvGaDGYZRMrWqdNMv, node_param_name=output`
- `node_config` 中无 `optionsSource`、无 `dynamicOptionsParam`

---

## 验证流程

1. 重启后端服务
2. 在 AI Chat 中触发"采购-项目管理-新增"工作流
3. 输入包含多条商品的查询（如"查询商品A"，确保返回多条）
4. 期望：工作流暂停，前端弹出 `AiUserSelect` 组件，显示商品列表供选择
5. 选择一条商品后点击"确认选择"，工作流继续执行

---

## 注意事项

- `HumanFeedbackNode.onProcess()` 在无用户输入时直接 return，不会进入 `completedNodes`，但 `initInput()` 已在 `process()` 第 203 行执行完毕，`state.getInputs()` 已有数据。通过 `pendingNodeInputs` 桥接这个时序差。
- `pendingNodeInputs` 是临时缓存，每次工作流运行实例独立，不存在跨请求污染问题。
- 并行路径中断（`handleParallelInterruption`）同样通过 `nextInterruptNode`（UUID 字符串）调用 `buildInteractionParams`，逻辑与非并行路径一致，无需特殊处理。
