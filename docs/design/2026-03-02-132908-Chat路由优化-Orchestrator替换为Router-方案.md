# Chat路由优化：Orchestrator替换为轻量Router

**文档编号**: 2026-03-02-132908
**创建时间**: 2026-03-02 13:29:08
**关联调研**: docs/research/2026-03-02-工作流路由与MCP调度模式调研报告.md

---

## 一、问题诊断

### 1.1 现有调用链路

```
HTTP POST /api/v1/ai/conversation/chat/stream
  ↓
AiConversationController.chatStream()
  ↓
WorkflowRoutingService.routeAndExecute()
  ├── [Layer 1] specifiedWorkflowUuid 不为空 → WorkflowStarter.streaming() → 正常
  └── [Layer 2] specifiedWorkflowUuid 为空  → orchestrateAndExecute() ← 问题在这里
                    │
                    ├── 步骤1: 获取所有workflow ToolCallback
                    ├── 步骤2: 获取所有MCP工具 ToolCallback (23个)
                    ├── 步骤3: 构建 Orchestrator prompt（ALL工具信息）
                    ├── 步骤4: 调用 Orchestrator LLM → 解析SubTask列表   [~6.5s]
                    ├── 步骤5: 顺序执行 Workers（workflow或MCP调用）
                    └── 步骤6: Synthesizer LLM 生成自然语言回复           [~3-4s]
```

**每次普通Chat请求总延迟：** Orchestrator(6.5s) + Worker执行 + Synthesizer(3-4s) = 10s+

### 1.2 根因分析

**Orchestrator prompt的实际内容：**
```
你是一个任务分解专家。将用户请求分解为多个子任务...
用户输入: "查一下库存"
可用workflows: [{name:"workflow_xxx1",description:"..."}, ...所有workflow]
可用MCP工具: [{name:"mcp_xxx1",description:"..."}, ...所有23个MCP工具]
分解规则: 1.分析用户意图... 2.每个子任务... 3.子任务之间可以并行执行...
返回JSON格式: {"analysis":"...","tasks":[...]}
```

- 工作流列表 + MCP工具列表 → token数 ≈ 1614
- LLM处理大型prompt → 延迟6.5秒
- 99%的情况结果只有一个SubTask（路由到单个workflow）
- Synthesizer再调用一次LLM → 又3-4秒

**结论：Orchestrator在做一件用一行代码就能做的事。**

### 1.3 现有轻量Router（已存在但未被使用）

`WorkflowRoutingService.route()` 方法已经存在：

```java
// 已实现的轻量路由方法，但 routeAndExecute() 没有调用它
public String route(String userInput, Long userId, String specifiedWorkflowUuid) {
    // Layer 1: 用户指定 (0ms)
    // Layer 2: routeByLLM() - 只传workflow的 uuid+title+desc+keywords  (~1-2s)
    // Layer 3: 默认兜底 (10ms)
    return workflowUuid;
}
```

`routeByLLM()` 的prompt只有 ~200 tokens（仅workflow基本信息，不含MCP工具）。

---

## 二、方案设计

### 2.1 完整调用链路（改造后）

```
HTTP POST /api/v1/ai/conversation/chat/stream
  ↓
AiConversationController.chatStream()
  ↓
WorkflowRoutingService.routeAndExecute()
  ├── [Layer 1] specifiedWorkflowUuid 不为空 → executeWorkflowByUuid()   [不变]
  └── [Layer 2] specifiedWorkflowUuid 为空  → route() → executeWorkflowByUuid()
                    │
                    └── routeByLLM(): 只传 uuid+title+desc+keywords     [~1-2s]
                              ↓
                    WorkflowStarter.streaming()                          [不变]
                              ↓
                    WorkflowEngine执行节点（含McpToolNode）               [不变]
```

**改造后延迟：** Router(1-2s) + Workflow执行(已有时间) = 节省 5-8s

### 2.2 三步改造说明

#### Step 1：Router替换Orchestrator（修改1个方法）

**修改文件：** `WorkflowRoutingService.java`

**修改内容：** `routeAndExecute()` 中的 Layer 2 逻辑

**改造前：**
```java
// Layer 2: Orchestrator-Workers模式
log.info("【routeAndExecute】Layer 2: 进入Orchestrator-Workers模式");
OrchestratorFinalResponse response = orchestrateAndExecute(...); // 10s+
return convertOrchestratorResponseToChatResponseStream(response, ...);
```

**改造后：**
```java
// Layer 2: 轻量Router路由 → 直接执行workflow
String routedUuid = route(userInput, userId, null);
if (routedUuid != null) {
    return executeWorkflowByUuid(routedUuid, userInput, userId, tenantCode, conversationId, pageContext);
}
return Flux.just(ChatResponseVo.createContentChunk("暂无可用的工作流"));
```

**提取公共方法：**
```java
private Flux<ChatResponseVo> executeWorkflowByUuid(
        String workflowUuid, String userInput, Long userId,
        String tenantCode, String conversationId, Map<String, Object> pageContext) {
    // 与现有 Layer 1 的执行代码相同，提取为私有方法复用
    List<JSONObject> userInputs = buildUserInputs(userInput);
    Flux<WorkflowEventVo> eventFlux = workflowStarter.streaming(
        workflowUuid, userInputs, tenantCode, WorkflowCallSource.AI_CHAT, conversationId, pageContext);
    return eventFlux.map(this::convertWorkflowEventToChatResponse);
}
```

**保留 `orchestrateAndExecute()`：** 不删除，仅停止在 `routeAndExecute()` 中调用。

#### Step 2：消除"系统对话"双层路由

**背景：** 当前 `route()` 的可用工作流列表中包含"系统对话"workflow（id=27）。该workflow内部有Classifier节点再次路由，导致：

```
routeByLLM() → "系统对话" workflow → Classifier → 实际业务workflow
                    (第1次路由)           (第2次路由)
```

**修改文件：** `AiWorkflowMapper.java`（修改SQL）

**修改内容：** `selectAvailableWorkflowsForRouting()` 查询中，排除内部含Classifier的"系统对话"类workflow。

具体方式：在 `ai_workflow` 表中为"系统对话"类workflow增加标记，或在SQL中按title排除，或将其 `is_enable` 置为0仅在路由中不可见。

**实施前确认：** 需要查看"系统对话"workflow（id=27）的节点结构，确认其是否确实是路由包装器。若是，在数据库中将其路由可见性关闭，直接路由到下游workflow即可。

#### Step 3：McpToolNode按领域绑定工具

**背景：** `McpToolNode` 通过 `chatStreamWithMcpTools()` 加载 `mcpToolOnlyChatClient`，后者在IoC中预配置了全部23个MCP工具。每次节点执行都把23个工具定义传给LLM，增加token消耗和推理时间。

**修改文件1：** `McpToolNodeConfig.java`（新增字段）

```java
@Data
public class McpToolNodeConfig {
    @JSONField(name = "tool_input")
    private String toolInput;

    @JSONField(name = "model_name")
    private String modelName;

    @JSONField(name = "show_process_output")
    private Boolean showProcessOutput = true;

    // 新增：指定此节点允许使用的MCP工具类名列表
    // 为null或空时，降级为加载全部工具（向后兼容）
    @JSONField(name = "tool_classes")
    private List<String> toolClasses;
}
```

**修改文件2：** `AiChatBaseService.java`（新增方法）

```java
// 新增：按工具类列表过滤后执行MCP调用
public ChatClient.StreamResponseSpec chatStreamWithFilteredMcpTools(
        AIChatOptionVo aiChatOption, List<String> toolClasses) {
    if (toolClasses == null || toolClasses.isEmpty()) {
        // 降级：加载全部工具
        return chatStreamWithMcpTools(aiChatOption);
    }
    // 过滤工具：只加载toolClasses中指定的类
    // 具体实现见代码实施阶段
}
```

**修改文件3：** `WorkflowUtil.java`（传递toolClasses）

在 `streamingInvokeLLM()` 中，当节点为McpToolNode时，从config中读取toolClasses，传给 `chatStreamWithFilteredMcpTools()`。

---

## 三、文件改动清单

### 后端文件

| 文件 | 改动类型 | 改动说明 |
|------|---------|---------|
| `scm-ai/.../service/workflow/WorkflowRoutingService.java` | 修改 | `routeAndExecute()` Layer 2改用route()，提取`executeWorkflowByUuid()`私有方法 |
| `scm-ai/.../workflow/node/mcptool/McpToolNodeConfig.java` | 修改 | 新增 `toolClasses` 字段 |
| `scm-ai/.../core/service/chat/AiChatBaseService.java` | 修改 | 新增 `chatStreamWithFilteredMcpTools()` 方法 |
| `scm-ai/.../workflow/WorkflowUtil.java` | 修改 | 传递toolClasses到MCP工具调用 |
| `scm-ai/.../core/mapper/workflow/AiWorkflowMapper.java` | 修改（Step 2确认后） | `selectAvailableWorkflowsForRouting()` 排除路由包装器workflow |

### 前端文件

本次改造全部在后端，前端不涉及改动。

### 数据库变更

- Step 2：视"系统对话"workflow结构确认后，可能需要更新 `ai_workflow` 表记录
- Step 3：`ai_workflow_node` 表中 McpToolNode 的 config JSON 增加 `tool_classes` 字段（可选配置，不配置则行为不变）

---

## 四、KISS原则7问题

1. **真问题？** 是。性能日志实测：Orchestrator 6.5s，Synthesizer 3-4s，用户体验差，是真实问题。
2. **更简单的方法？** `route()` 方法已存在，只需在 `routeAndExecute()` 中调用它替换 `orchestrateAndExecute()`。极简改动。
3. **会破坏什么？** Layer 1（用户指定workflow）不动。Layer 2 改用已有的 `route()` 方法，该方法经过测试。`orchestrateAndExecute()` 保留不删除，风险极低。
4. **真的需要？** 是。10s响应延迟直接影响用户体验。
5. **过度设计？** 没有。Step 1 修改1个方法中的约10行代码。Step 3 新增1个字段 + 1个方法，向后兼容。
6. **话题模糊会幻觉？** 不会。改动范围明确，基于实际代码。
7. **学习了注意事项？** 是。SQL使用`""" + sql + """`，不使用QueryWrapper，驼峰用AS别名，插入更新用bean操作。

---

## 五、风险分析

| 风险 | 等级 | 缓解措施 |
|------|------|---------|
| `route()` 路由精度不如Orchestrator | 低 | route()已经有3层兜底（用户指定→LLM→默认），且有幻觉防护 |
| Synthesizer去掉后格式可能变化 | 低 | Workflow的EndNode已有自己的输出格式，不依赖Synthesizer |
| Step 2 排除"系统对话"后路由漏洞 | 中 | 需先查看id=27的节点结构再决定，Step 2 独立实施 |
| McpToolNodeConfig向后兼容 | 无 | `toolClasses`为null时降级全量加载，现有节点配置不受影响 |

---

## 六、不在本次范围内

- `orchestrateAndExecute()` 方法本身不删除（保留备用）
- Synthesizer相关代码不删除（保留备用）
- 前端无改动
- 其他workflow节点类型无改动
