# OpenPage节点 form_data传递 + ThinkingSteps显示优化 实施计划

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复 OpenPage route模式 form_data 为null的问题，并在 ThinkingSteps 中显示节点输入参数和LLM原始输出。

**Architecture:** 纯后端改动，共3处：①OpenPageNode.java 1行修复；②WorkflowEngine.java 增加 nodeInputCache；③WorkflowEngine.java buildSummary OpenPage case 扩展显示。前端零改动，复用现有 params/outputText 渲染逻辑。

**Tech Stack:** Java 17, Spring Boot 3.1.4, fastjson2

---

## 文件改动清单

| 文件 | 改动类型 | 说明 |
|------|---------|------|
| `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/openpage/OpenPageNode.java` | 修改 | 修复 form_data：整个LLM输出作为form_data |
| `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java` | 修改 | 增加 nodeInputCache + buildSummary OpenPage case 扩展 |

---

## Chunk 1: 修复 OpenPageNode form_data

### Task 1: 修复 OpenPageNode.processRouteMode() 中 form_data 赋值

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/openpage/OpenPageNode.java:100-113`

**背景：**
LLM输出是平铺业务JSON（`name`、`supplier_id`、`detailListData`等），前端 `dialog/ai/new/index.vue` 的 `init()` 直接把 `aiData`（即`form_data`）字段映射到表单。
当前代码用 `llmJson.getJSONObject("form_data")` 取子字段，但LLM输出顶层就是业务数据，`form_data`子字段为null，导致预填失败。

- [ ] **Step 1: 读取当前代码确认位置**

打开 `OpenPageNode.java`，找到 `processRouteMode()` 方法中解析LLM输出的部分（约第95-117行）：

```java
// 解析LLM输出的JSON
if (llmResult != null && !llmResult.isEmpty()) {
    try {
        int start = llmResult.indexOf('{');
        int end = llmResult.lastIndexOf('}');
        if (start >= 0 && end > start) {
            JSONObject llmJson = JSONObject.parseObject(llmResult.substring(start, end + 1));
            if (llmJson.getString("page_mode") != null) {
                pageMode = llmJson.getString("page_mode");
            }
            if (llmJson.getJSONObject("form_data") != null) {   // ← 问题在这里
                formData = llmJson.getJSONObject("form_data");
            }
            if (llmJson.getJSONObject("query_params") != null) {
                queryParams = llmJson.getJSONObject("query_params");
            }
            if (llmJson.getString("record_id") != null) {
                recordId = llmJson.getString("record_id");
            }
        }
    } catch (Exception e) {
        log.warn("OpenPage节点解析LLM输出JSON失败: {}", e.getMessage());
    }
}
```

- [ ] **Step 2: 修改 form_data 赋值**

将：
```java
if (llmJson.getJSONObject("form_data") != null) {
    formData = llmJson.getJSONObject("form_data");
}
```

改为：
```java
// LLM输出的整个JSON就是业务表单数据（平铺字段），直接作为form_data
formData = llmJson;
```

修改后该代码块变为：
```java
JSONObject llmJson = JSONObject.parseObject(llmResult.substring(start, end + 1));
if (llmJson.getString("page_mode") != null) {
    pageMode = llmJson.getString("page_mode");
}
// LLM输出的整个JSON就是业务表单数据（平铺字段），直接作为form_data
formData = llmJson;
if (llmJson.getJSONObject("query_params") != null) {
    queryParams = llmJson.getJSONObject("query_params");
}
if (llmJson.getString("record_id") != null) {
    recordId = llmJson.getString("record_id");
}
```

- [ ] **Step 3: 确认 open_page_command 构建逻辑不需要改动**

第140-148行构建 command 的代码：
```java
JSONObject command = new JSONObject();
command.put("route", path);
if (pageMode != null) command.put("page_mode", pageMode);
command.put("query_params", queryParams);
command.put("form_data", formData);   // ← formData现在是整个LLM输出JSON
command.put("record_id", recordId);
```
这里不需要改，`formData` 已经是正确的值。

- [ ] **Step 4: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/openpage/OpenPageNode.java
git -C 00_scm_backend/scm_backend commit -m "fix(ai): OpenPage route模式LLM输出整体作为form_data"
```

---

## Chunk 2: WorkflowEngine 增加 nodeInputCache

### Task 2: 增加 nodeInputCache，在 runNode 里缓存节点输入

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

**背景：**
`buildSummary` 在 `after` 回调里执行，此时 `WfNodeState` 已不可访问。现有 `nodeOutputCache` 缓存了节点输出，同理需要 `nodeInputCache` 缓存节点输入（`var_data`、`var_outer` 等），供 `buildSummary` 读取显示。

- [ ] **Step 1: 在 nodeOutputCache 声明旁边增加 nodeInputCache 字段**

找到第102行：
```java
private final ConcurrentHashMap<String, List<NodeIOData>> nodeOutputCache = new ConcurrentHashMap<>();
```

在其下方紧接着增加：
```java
/**
 * 节点输入缓存：nodeUuid → 节点输入列表
 * 供after回调中的buildSummary读取（after时WfNodeState已不可访问）
 */
private final ConcurrentHashMap<String, List<NodeIOData>> nodeInputCache = new ConcurrentHashMap<>();
```

- [ ] **Step 2: 在 runNode 里缓存节点输入**

找到第861行（`nodeOutputCache.put` 那行）：
```java
// 缓存节点输出，供after回调中的buildSummary使用（框架after时输出尚未合并进state）
nodeOutputCache.put(wfNode.getUuid(), nodeState.getOutputs());
```

在其下方紧接着增加：
```java
// 缓存节点输入，供after回调中的buildSummary使用
nodeInputCache.put(wfNode.getUuid(), nodeState.getInputs());
```

- [ ] **Step 3: 在 after 回调里清理 nodeInputCache**

找到第1637行（`nodeOutputCache.remove` 的两处）：

第一处（不可见节点提前返回）：
```java
nodeOutputCache.remove(nodeId);
return;
```
改为：
```java
nodeOutputCache.remove(nodeId);
nodeInputCache.remove(nodeId);
return;
```

第二处（正常清理，第1645行）：
```java
nodeOutputCache.remove(nodeId);
```
改为：
```java
nodeOutputCache.remove(nodeId);
nodeInputCache.remove(nodeId);
```

- [ ] **Step 4: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): WorkflowEngine增加nodeInputCache供buildSummary读取节点输入"
```

---

## Chunk 3: buildSummary OpenPage case 扩展显示

### Task 3: buildSummary OpenPage case 增加输入参数和LLM输出显示

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java:1754-1808`

**背景：**
当前 OpenPage case 只显示 `路由`、`页面模式`、`form_data`（来自 open_page_command）。
需要增加：
- 输入参数（`var_data`、`var_outer`）：从 `nodeInputCache` 读取，加入 `params`
- LLM原始输出：从 `outputList` 读 `output` 字段，放入 `outputText`

- [ ] **Step 1: 读取当前 OpenPage case 代码（第1754-1808行）确认结构**

当前结构：
```java
case "OpenPage": {
    String commandJson = findOutputValue(outputList, "open_page_command");
    // ... 解析 route/pageMode/formData ...
    // ... 构建 params（路由、页面模式、form_data）...
    // outputText：含page_mode的中文描述
    if (showOutput) {
        String modeLabel = ...;
        String text = route != null ? "已为您打开" + modeLabel + "页面: " + route : "已为您打开页面";
        summary.put("outputText", text);
    }
    break;
}
```

- [ ] **Step 2: 在 params 构建完成后，追加输入参数**

在 `if (!params.isEmpty()) { summary.put("params", params); }` 这行**之前**，追加：

```java
// 追加节点输入参数（var_data、var_outer等）
List<NodeIOData> nodeInputs = nodeInputCache.get(nodeId);
if (nodeInputs != null) {
    for (NodeIOData input : nodeInputs) {
        if (input.getName() != null && input.getContent() != null
                && input.getContent().getValue() != null) {
            Map<String, String> p = new HashMap<>();
            p.put("name", input.getName());
            p.put("title", input.getTitle() != null ? input.getTitle() : input.getName());
            p.put("value", input.valueToString());
            params.add(p);
        }
    }
}
```

- [ ] **Step 3: 修改 outputText，优先显示LLM原始输出**

将现有 outputText 逻辑：
```java
if (showOutput) {
    String modeLabel = "new".equals(pageMode) ? "新增"
        : "edit".equals(pageMode) ? "编辑"
        : "view".equals(pageMode) ? "查看"
        : "approve".equals(pageMode) ? "审批"
        : "list".equals(pageMode) ? "列表" : "";
    String text = route != null
        ? "已为您打开" + modeLabel + "页面: " + route
        : "已为您打开页面";
    summary.put("outputText", text);
}
```

改为：
```java
if (showOutput) {
    // 优先显示LLM原始输出
    String llmRawOutput = findOutputValue(outputList, DEFAULT_OUTPUT_PARAM_NAME);
    if (llmRawOutput != null && !llmRawOutput.isEmpty()) {
        summary.put("outputText", llmRawOutput);
    } else {
        // fallback：无LLM输出时（如无prompt配置）显示中文描述
        String modeLabel = "new".equals(pageMode) ? "新增"
            : "edit".equals(pageMode) ? "编辑"
            : "view".equals(pageMode) ? "查看"
            : "approve".equals(pageMode) ? "审批"
            : "list".equals(pageMode) ? "列表" : "";
        String text = route != null
            ? "已为您打开" + modeLabel + "页面: " + route
            : "已为您打开页面";
        summary.put("outputText", text);
    }
}
```

- [ ] **Step 4: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): buildSummary OpenPage显示输入参数和LLM原始输出"
```

---

## 验证

改动完成后，运行工作流"采购-项目管理-新增"，检查：

1. **form_data 有值**：日志里 `open_page_command` 应包含 `"form_data":{...业务字段...}`，不再是 `null`
2. **前端表单预填**：打开新增页面后，`项目名称` 等字段应有LLM生成的值
3. **ThinkingSteps 显示**：子工作流"通用-打开页面-流程"的"打开前端页面"节点应显示：
   - `var_data` = （路由判断输出的JSON，截断）
   - `var_outer` = （合并结果JSON，截断）
   - LLM输出内容（截断，悬浮可见全量，可复制）
