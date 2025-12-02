# SCM AI MCP工具节点指定执行调研报告

## 调研背景

用户询问：**关于MCP工具节点,能否指定一个MCP来执行？怎么指定？**

调研目标：
1. 理解Spring AI Alibaba中MCP工具的配置机制
2. 分析SCM AI当前如何使用MCP工具
3. 探索如何在节点级别指定特定MCP工具执行

---

## 一、Spring AI Alibaba MCP工具配置机制

### 1.1 核心组件架构

Spring AI Alibaba的工具系统基于以下核心接口和类：

```java
// 1. ToolCallback - 工具回调接口
public interface ToolCallback {
    ToolDefinition getToolDefinition();  // 工具定义(名称、描述、Schema)
    String call(String arguments, ToolContext context);  // 工具执行
}

// 2. ToolCallbackProvider - 工具提供者接口
public interface ToolCallbackProvider {
    ToolCallback[] getToolCallbacks();  // 提供多个工具
}

// 3. FunctionToolCallback - 工具回调实现类
FunctionToolCallback.builder(String name, BiFunction<Map, ToolContext, String> function)
    .description("工具描述")
    .inputType(Map.class)
    .inputSchema("JSON Schema")
    .build();
```

### 1.2 工具配置方式 - Agent Builder模式

在Spring AI Alibaba Agent Framework中,工具通过Builder模式配置：

```java
// Builder.java 提供的工具配置方法
public Builder tools(List<ToolCallback> tools);          // 直接指定工具列表
public Builder toolCallbackProviders(ToolCallbackProvider... providers);  // 通过Provider提供
public Builder toolNames(String... toolNames);           // 通过名称引用(需配合resolver)
public Builder resolver(ToolCallbackResolver resolver);  // 工具解析器
```

**DefaultBuilder.java中的工具收集逻辑**:

```java
// DefaultBuilder.java:109-196
List<ToolCallback> regularTools = new ArrayList<>();

// 1. 从tools直接添加
if (CollectionUtils.isNotEmpty(tools)) {
    regularTools.addAll(tools);
}

// 2. 从toolCallbackProviders提取
if (CollectionUtils.isNotEmpty(toolCallbackProviders)) {
    for (var provider : toolCallbackProviders) {
        regularTools.addAll(List.of(provider.getToolCallbacks()));
    }
}

// 3. 从toolNames解析
if (CollectionUtils.isNotEmpty(toolNames)) {
    for (String toolName : toolNames) {
        ToolCallback toolCallback = this.resolver.resolve(toolName);
        regularTools.add(toolCallback);
    }
}

// 4. 从resolver自动提取(如果resolver也实现了ToolCallbackProvider)
if (regularTools.isEmpty() && this.resolver != null) {
    if (this.resolver instanceof ToolCallbackProvider provider) {
        ToolCallback[] resolverTools = provider.getToolCallbacks();
        regularTools.addAll(List.of(resolverTools));
    }
}

// 5. 分配给LLM节点和Tool节点
llmNodeBuilder.toolCallbacks(allTools);  // LLM看到这些工具定义
toolNodeBuilder.toolCallbacks(allTools); // Tool节点执行这些工具
```

### 1.3 工具选择机制 - LLM Function Calling

```java
// AgentToolNode.java:249-254 - 工具解析
private ToolCallback resolve(String toolName) {
    return toolCallbacks.stream()
        .filter(callback -> callback.getToolDefinition().name().equals(toolName))
        .findFirst()
        .orElseGet(() -> toolCallbackResolver == null ? null : toolCallbackResolver.resolve(toolName));
}
```

**执行流程**：
1. LLM根据用户输入和工具定义,通过Function Calling选择工具
2. 返回tool_call对象:`{name: "ToolName", arguments: {...}}`
3. AgentToolNode根据name在toolCallbacks列表中查找ToolCallback
4. 调用ToolCallback.call()执行工具
5. 返回结果给LLM继续对话

---

## 二、Spring AI Alibaba官方示例分析

### 2.1 McpNodeExample - 节点级工具配置

```java
// McpNodeExample.java:66-90
public static class McpNode implements NodeAction {
    private final ChatClient chatClient;

    public McpNode(ChatClient.Builder chatClientBuilder, Set<ToolCallback> toolCallbacks) {
        // ⭐ 关键：为这个特定节点配置指定的工具集合
        this.chatClient = chatClientBuilder
                .defaultToolCallbacks(toolCallbacks.toArray(ToolCallback[]::new))
                .build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) {
        String query = state.value("query", "");

        // ⭐ 使用配置了特定工具的chatClient
        Flux<String> streamResult = chatClient.prompt(query).stream().content();
        String result = streamResult.reduce("", (acc, item) -> acc + item).block();

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("mcpcontent", result);
        return resultMap;
    }
}
```

**关键洞察**：
- 每个节点可以有自己的ChatClient实例
- ChatClient.Builder.defaultToolCallbacks()设置该节点可用的工具集合
- 不同节点可以配置不同的工具子集

### 2.2 AgentStaticLoader - 从ToolCallbackProvider加载工具

```java
// AgentStaticLoader.java:55-67
public AgentStaticLoader(ToolCallbackProvider toolCallbackProvider) {
    // ⭐ 从Provider获取所有工具
    List<ToolCallback> toolCallbacks = Arrays.asList(toolCallbackProvider.getToolCallbacks());

    System.out.println("Loaded MCP tool callbacks: " + toolCallbacks.size());

    // ⭐ 传递给Agent构建器
    ReactAgent researchAgent = new DeepResearchAgent().getResearchAgent(toolCallbacks);
}
```

---

## 三、SCM AI当前实现分析

### 3.1 MCP工具注册 - McpToolConfig

```java
// McpToolConfig.java:61-91
@Bean
public ToolCallbackProvider mcpToolCallbackProvider(ApplicationContext context) {
    Map<String, Object> beans = context.getBeansWithAnnotation(Component.class);
    List<ToolCallback> toolCallbacks = new ArrayList<>();

    // ⭐ 扫描所有@McpTool注解的方法
    for (Map.Entry<String, Object> entry : beans.entrySet()) {
        Object bean = entry.getValue();
        Class<?> beanClass = bean.getClass();

        for (Method method : beanClass.getMethods()) {
            if (method.isAnnotationPresent(McpTool.class)) {
                // ⭐ 创建ToolCallback并注册
                ToolCallback callback = McpToolCallbackAdapter.createToolCallback(bean, method);
                toolCallbacks.add(callback);

                String toolName = beanClass.getSimpleName() + "." + method.getName();
                log.info("注册MCP工具: {} - {}", toolName, method.getAnnotation(McpTool.class).description());
            }
        }
    }

    // ⭐ 返回包含所有工具的Provider
    return ToolCallbackProvider.from(toolCallbacks);
}
```

### 3.2 工具控制开关 - enableMcpTools

```java
// AIChatOptionVo.java:66-73
/**
 * 是否启用MCP工具自动调用
 * true: 启用MCP工具(适用于MCP工具节点)
 * false: 禁用MCP工具(适用于生成回答节点等)
 * 默认值: false(安全默认值,避免不必要的工具调用)
 */
@Schema(description = "是否启用MCP工具")
private Boolean enableMcpTools = false;
```

### 3.3 动态ChatClient选择机制

```java
// AiChatBaseService.java:228-236
// 根据enableMcpTools标志动态选择ChatClient
ChatClient selectedClient;
if (Boolean.TRUE.equals(aiChatOption.getEnableMcpTools())) {
    selectedClient = workflowDomainChatClient;  // ⭐ 包含所有MCP工具
    log.info("✅ [MCP Control] 启用MCP工具,使用workflowDomainChatClient");
} else {
    selectedClient = workflowDomainChatClientNoMcp;  // ⭐ 不包含任何MCP工具
    log.info("✅ [MCP Control] 禁用MCP工具,使用workflowDomainChatClientNoMcp");
}
```

### 3.4 节点类型检测

```java
// WorkflowUtil.java:158-162
// 节点类型检测:判断是否为MCP工具节点
boolean isMcpToolNode = isMcpToolNode(node);
chatOption.setEnableMcpTools(isMcpToolNode);
log.info("节点类型判断 - 节点UUID: {}, 标题: {}, 是否MCP工具节点: {}",
        node.getUuid(), node.getTitle(), isMcpToolNode);
```

### 3.5 当前架构图

```
┌─────────────────────────────────────────────────────────────┐
│                    McpToolConfig                            │
│  扫描所有@McpTool注解 → 创建ToolCallbackProvider Bean       │
└────────────────┬───────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│              AiChatMemoryConfig                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ workflowDomainChatClient                            │   │
│  │ - 注入 mcpToolCallbackProvider                      │   │
│  │ - 包含所有@McpTool工具                              │   │
│  └─────────────────────────────────────────────────────┘   │
│                                                             │
│  ┌─────────────────────────────────────────────────────┐   │
│  │ workflowDomainChatClientNoMcp                       │   │
│  │ - 不注入任何工具                                     │   │
│  │ - 用于非MCP工具节点                                  │   │
│  └─────────────────────────────────────────────────────┘   │
└────────────────┬───────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│            AiChatBaseService                                │
│  chatWithWorkflowMemoryStream():                            │
│    if (enableMcpTools == true)                              │
│      → 使用 workflowDomainChatClient (所有工具)             │
│    else                                                     │
│      → 使用 workflowDomainChatClientNoMcp (无工具)          │
└────────────────┬───────────────────────────────────────────┘
                 │
                 ▼
┌─────────────────────────────────────────────────────────────┐
│                McpToolNode                                  │
│  1. 检测节点类型 → setEnableMcpTools(true)                  │
│  2. LLM自动选择工具执行                                      │
│  3. 无法指定特定工具 ❌                                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 四、问题诊断：当前架构的局限性

### 4.1 核心问题

**当前SCM AI实现的MCP工具控制粒度**：
- ✅ **全局级别**：通过`enableMcpTools`开关控制是否启用MCP工具
- ❌ **节点级别**：无法为单个节点指定特定的MCP工具子集

**具体表现**：
```java
// 当前实现 - McpToolNode.java
if (isMcpToolNode) {
    // ⭐ 启用所有MCP工具,无法指定特定工具
    chatOption.setEnableMcpTools(true);
}
```

### 4.2 与Spring AI Alibaba最佳实践的差距

| 维度 | Spring AI Alibaba官方示例 | SCM AI当前实现 | 差距 |
|------|--------------------------|---------------|------|
| 工具配置粒度 | 节点级别(每个节点独立配置) | 全局级别(全部或无) | ❌ 粒度太粗 |
| ChatClient实例 | 每个节点独立ChatClient | 全局共享ChatClient | ❌ 无法隔离 |
| 工具子集控制 | 通过defaultToolCallbacks()指定 | 无法指定 | ❌ 缺失功能 |
| 灵活性 | 高(每个节点可用不同工具集) | 低(只能全开或全关) | ❌ 不够灵活 |

---

## 五、如何指定MCP工具执行 - 解决方案

### 5.1 方案一：扩展McpToolNodeConfig(推荐✅)

**设计思路**：在节点配置中增加`enabled_tools`字段,指定该节点允许使用的MCP工具列表。

#### 5.1.1 修改McpToolNodeConfig

```java
// McpToolNodeConfig.java
@Data
public class McpToolNodeConfig {

    @JSONField(name = "tool_input")
    private String toolInput;

    @JSONField(name = "model_name")
    private String modelName;

    /**
     * ⭐ 新增：允许使用的MCP工具列表
     * 格式: ["ToolClass.methodName1", "ToolClass.methodName2"]
     * 示例: ["LocationQueryService.queryLocationInfo", "StockQueryService.queryStock"]
     *
     * 为空或null时，允许使用所有MCP工具(向后兼容)
     */
    @JSONField(name = "enabled_tools")
    private List<String> enabledTools;
}
```

#### 5.1.2 修改McpToolNode执行逻辑

```java
// McpToolNode.java
@Override
protected NodeProcessResult onProcess() {
    log.info("开始执行MCP工具节点: {}", node.getTitle());

    try {
        // 1. 解析配置
        McpToolNodeConfig config = checkAndGetConfig(McpToolNodeConfig.class);

        // 2. 获取模型名称
        String modelName = StringUtils.isNotBlank(config.getModelName())
            ? config.getModelName()
            : "gj-deepseek";

        // ⭐ 3. 获取允许使用的工具列表
        List<String> enabledTools = config.getEnabledTools();

        // 4. 构建prompt
        String prompt = buildPrompt(config);

        // ⭐ 5. 调用LLM时传递enabledTools
        WorkflowUtil.streamingInvokeLLM(
            wfState,
            state,
            node,
            modelName,
            prompt,
            enabledTools  // 新增参数
        );

        return new NodeProcessResult();
    } catch (Exception e) {
        log.error("MCP工具节点执行失败: {}", node.getTitle(), e);
        throw new RuntimeException("MCP工具执行失败: " + e.getMessage(), e);
    }
}
```

#### 5.1.3 修改WorkflowUtil.streamingInvokeLLM

```java
// WorkflowUtil.java
public static void streamingInvokeLLM(
        WfState wfState,
        WfNodeState nodeState,
        AiWorkflowNodeVo node,
        String modelName,
        String prompt,
        List<String> enabledTools) {  // ⭐ 新增参数

    // ... 现有逻辑 ...

    AIChatOptionVo chatOption = new AIChatOptionVo();
    chatOption.setModule(modelConfig);
    chatOption.setPrompt(prompt);

    // 节点类型检测
    boolean isMcpToolNode = isMcpToolNode(node);
    chatOption.setEnableMcpTools(isMcpToolNode);

    // ⭐ 新增：设置允许使用的工具列表
    if (isMcpToolNode && enabledTools != null && !enabledTools.isEmpty()) {
        chatOption.setEnabledTools(enabledTools);
        log.info("节点级工具控制 - 节点: {}, 允许的工具: {}", node.getTitle(), enabledTools);
    }

    // ... 现有逻辑 ...
}
```

#### 5.1.4 修改AIChatOptionVo

```java
// AIChatOptionVo.java
@Data
public class AIChatOptionVo implements Serializable {

    // ... 现有字段 ...

    @Schema(description = "是否启用MCP工具")
    private Boolean enableMcpTools = false;

    /**
     * ⭐ 新增：允许使用的MCP工具名称列表
     * 格式: ["ToolClass.methodName"]
     * 为空时允许所有工具(向后兼容)
     */
    @Schema(description = "允许使用的MCP工具列表")
    private List<String> enabledTools;
}
```

#### 5.1.5 修改AiChatBaseService.chatWithWorkflowMemoryStream

```java
// AiChatBaseService.java
public ChatClient.StreamResponseSpec chatWithWorkflowMemoryStream(...) {

    // ... 现有逻辑 ...

    // 根据enableMcpTools标志动态选择ChatClient
    ChatClient selectedClient;
    if (Boolean.TRUE.equals(aiChatOption.getEnableMcpTools())) {
        // ⭐ 检查是否需要工具过滤
        if (aiChatOption.getEnabledTools() != null && !aiChatOption.getEnabledTools().isEmpty()) {
            // ⭐ 创建过滤后的ChatClient(只包含指定工具)
            selectedClient = createFilteredChatClient(aiChatOption.getEnabledTools());
            log.info("✅ [MCP Control] 节点级工具控制,工具列表: {}", aiChatOption.getEnabledTools());
        } else {
            // 使用包含所有工具的ChatClient
            selectedClient = workflowDomainChatClient;
            log.info("✅ [MCP Control] 启用所有MCP工具");
        }
    } else {
        selectedClient = workflowDomainChatClientNoMcp;
        log.info("✅ [MCP Control] 禁用MCP工具");
    }

    // ... 现有逻辑 ...
}

/**
 * ⭐ 新增方法：创建只包含指定工具的ChatClient
 */
private ChatClient createFilteredChatClient(List<String> enabledToolNames) {
    // 1. 从mcpToolCallbackProvider获取所有工具
    ToolCallback[] allTools = mcpToolCallbackProvider.getToolCallbacks();

    // 2. 过滤出允许的工具
    List<ToolCallback> filteredTools = Arrays.stream(allTools)
        .filter(tool -> enabledToolNames.contains(tool.getToolDefinition().name()))
        .toList();

    log.info("工具过滤 - 总工具数: {}, 允许工具数: {}, 过滤后: {}",
        allTools.length, enabledToolNames.size(), filteredTools.size());

    // 3. 创建只包含过滤后工具的ChatClient
    return ChatClient.builder(chatModel)
        .defaultToolCallbacks(filteredTools.toArray(ToolCallback[]::new))
        .build();
}
```

### 5.2 方案二：参考McpNodeExample创建节点专属ChatClient

**设计思路**：每个McpToolNode节点在初始化时创建自己的ChatClient实例,配置特定的工具子集。

```java
// McpToolNode.java - 完全重构版
@Slf4j
public class McpToolNode extends AbstractWfNode {

    private ChatClient nodeChatClient;  // ⭐ 节点专属ChatClient

    public McpToolNode(AiWorkflowComponentEntity wfComponent,
                      AiWorkflowNodeVo node,
                      WfState wfState,
                      WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);

        // ⭐ 在构造函数中初始化节点专属ChatClient
        this.nodeChatClient = initializeNodeChatClient();
    }

    /**
     * ⭐ 初始化节点专属ChatClient
     */
    private ChatClient initializeNodeChatClient() {
        // 1. 解析节点配置
        McpToolNodeConfig config = checkAndGetConfig(McpToolNodeConfig.class);
        List<String> enabledToolNames = config.getEnabledTools();

        // 2. 获取ToolCallbackProvider
        ToolCallbackProvider mcpProvider = SpringUtil.getBean("mcpToolCallbackProvider", ToolCallbackProvider.class);
        ToolCallback[] allTools = mcpProvider.getToolCallbacks();

        // 3. 过滤工具(如果配置了enabledTools)
        List<ToolCallback> nodeTools;
        if (enabledToolNames != null && !enabledToolNames.isEmpty()) {
            nodeTools = Arrays.stream(allTools)
                .filter(tool -> enabledToolNames.contains(tool.getToolDefinition().name()))
                .toList();
            log.info("节点 {} 工具过滤: 总数 {} → 允许 {}", node.getTitle(), allTools.length, nodeTools.size());
        } else {
            nodeTools = List.of(allTools);
            log.info("节点 {} 使用所有工具: {}", node.getTitle(), allTools.length);
        }

        // 4. 创建ChatModel
        ChatModel chatModel = SpringUtil.getBean(ChatModel.class);

        // 5. 创建节点专属ChatClient
        return ChatClient.builder(chatModel)
            .defaultToolCallbacks(nodeTools.toArray(ToolCallback[]::new))
            .build();
    }

    @Override
    protected NodeProcessResult onProcess() {
        log.info("开始执行MCP工具节点: {}", node.getTitle());

        try {
            // 1. 构建prompt
            String prompt = buildPrompt();

            // 2. ⭐ 使用节点专属ChatClient调用LLM
            Flux<String> streamResult = nodeChatClient
                .prompt(prompt)
                .stream()
                .content();

            // 3. 处理流式响应
            StringBuilder fullResponse = new StringBuilder();
            streamResult.doOnNext(chunk -> {
                fullResponse.append(chunk);
                if (wfState.getStreamHandler() != null) {
                    wfState.getStreamHandler().sendNodeChunk(node.getUuid(), chunk);
                }
            }).blockLast();

            // 4. 保存输出
            NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", fullResponse.toString());
            state.getOutputs().add(output);

            return new NodeProcessResult();
        } catch (Exception e) {
            log.error("MCP工具节点执行失败: {}", node.getTitle(), e);
            throw new RuntimeException("MCP工具执行失败: " + e.getMessage(), e);
        }
    }
}
```

### 5.3 方案对比

| 维度 | 方案一：扩展配置+动态过滤 | 方案二：节点专属ChatClient |
|------|-------------------------|--------------------------|
| 实现复杂度 | 中等 | 较高 |
| 代码侵入性 | 需修改多处 | 集中在McpToolNode |
| 性能影响 | 运行时过滤,轻微开销 | 初始化时创建ChatClient |
| 扩展性 | 好(配置驱动) | 好(完全隔离) |
| 向后兼容 | ✅ 完全兼容 | ✅ 完全兼容 |
| 与Spring AI对齐 | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐(完全一致) |
| 推荐度 | ✅ 推荐 | ✅✅ 更推荐 |

---

## 六、实施建议

### 6.1 推荐实施方案

**推荐采用方案二：节点专属ChatClient**

**理由**：
1. ✅ **最佳实践对齐**：完全符合Spring AI Alibaba官方示例(McpNodeExample)
2. ✅ **代码隔离性强**：所有改动集中在McpToolNode,不影响其他模块
3. ✅ **性能最优**：工具过滤在节点初始化时完成,运行时无额外开销
4. ✅ **可测试性强**：每个节点独立ChatClient,易于单元测试
5. ✅ **向后兼容**：不配置enabledTools时默认使用所有工具

### 6.2 实施步骤

#### Step 1: 修改McpToolNodeConfig

```java
// McpToolNodeConfig.java
@Data
public class McpToolNodeConfig {

    @JSONField(name = "tool_input")
    private String toolInput;

    @JSONField(name = "model_name")
    private String modelName;

    /**
     * 允许使用的MCP工具列表
     * 格式: ["ToolClass.methodName"]
     * 为空或null时使用所有工具
     */
    @JSONField(name = "enabled_tools")
    private List<String> enabledTools;
}
```

#### Step 2: 重构McpToolNode

```java
// McpToolNode.java - 关键改动
@Slf4j
public class McpToolNode extends AbstractWfNode {

    private ChatClient nodeChatClient;

    public McpToolNode(...) {
        super(...);
        this.nodeChatClient = initializeNodeChatClient();
    }

    private ChatClient initializeNodeChatClient() {
        // 1. 获取配置
        McpToolNodeConfig config = checkAndGetConfig(McpToolNodeConfig.class);

        // 2. 获取工具Provider
        ToolCallbackProvider provider = SpringUtil.getBean("mcpToolCallbackProvider", ToolCallbackProvider.class);
        ToolCallback[] allTools = provider.getToolCallbacks();

        // 3. 过滤工具
        List<ToolCallback> nodeTools = filterTools(allTools, config.getEnabledTools());

        // 4. 创建ChatClient
        ChatModel chatModel = SpringUtil.getBean(ChatModel.class);
        return ChatClient.builder(chatModel)
            .defaultToolCallbacks(nodeTools.toArray(ToolCallback[]::new))
            .build();
    }

    private List<ToolCallback> filterTools(ToolCallback[] allTools, List<String> enabledToolNames) {
        if (enabledToolNames == null || enabledToolNames.isEmpty()) {
            return List.of(allTools);
        }

        return Arrays.stream(allTools)
            .filter(tool -> enabledToolNames.contains(tool.getToolDefinition().name()))
            .toList();
    }

    @Override
    protected NodeProcessResult onProcess() {
        // 使用 nodeChatClient 而不是全局的 workflowDomainChatClient
        // ...
    }
}
```

#### Step 3: 前端配置界面增加工具选择

```vue
<!-- McpToolNodeConfig.vue -->
<template>
  <div class="mcp-tool-config">
    <!-- 现有配置项 -->
    <el-input v-model="config.tool_input" placeholder="工具输入参数" />
    <el-select v-model="config.model_name" placeholder="选择模型">
      <el-option value="gj-deepseek" label="DeepSeek" />
    </el-select>

    <!-- ⭐ 新增：工具选择 -->
    <el-select
      v-model="config.enabled_tools"
      multiple
      collapse-tags
      placeholder="选择允许的MCP工具(不选则允许所有)"
    >
      <el-option
        v-for="tool in availableMcpTools"
        :key="tool.name"
        :label="tool.description"
        :value="tool.name"
      >
        <span>{{ tool.name }}</span>
        <span style="color: #8492a6; font-size: 12px">{{ tool.description }}</span>
      </el-option>
    </el-select>
  </div>
</template>
```

### 6.3 配置示例

```json
{
  "tool_input": "查询库位信息,条件: {input.query_condition}",
  "model_name": "gj-deepseek",
  "enabled_tools": [
    "LocationQueryService.queryLocationInfo",
    "StockQueryService.queryStockByLocation"
  ]
}
```

---

## 七、参考资料

| 资源类型 | 位置 | 关键内容 |
|---------|------|---------|
| Spring AI Alibaba McpNodeExample | `spring-ai-alibaba-main/examples/documentation/src/main/java/.../McpNodeExample.java` | 节点级工具配置示例 |
| Spring AI Alibaba DefaultBuilder | `spring-ai-alibaba-main/spring-ai-alibaba-agent-framework/src/main/java/.../DefaultBuilder.java` | 工具收集和分配逻辑 |
| Spring AI Alibaba AgentToolNode | `spring-ai-alibaba-main/spring-ai-alibaba-agent-framework/src/main/java/.../AgentToolNode.java` | 工具解析和执行 |
| SCM AI McpToolConfig | `scm-ai/src/main/java/.../McpToolConfig.java` | MCP工具注册 |
| SCM AI McpToolNode | `scm-ai/src/main/java/.../McpToolNode.java` | MCP工具节点实现 |
| SCM AI AiChatBaseService | `scm-ai/src/main/java/.../AiChatBaseService.java` | ChatClient选择逻辑 |

---

## 八、结论

### 8.1 关键发现

1. **Spring AI Alibaba原生支持节点级工具配置**
   - 通过`ChatClient.Builder.defaultToolCallbacks()`为每个节点配置独立工具集
   - 官方示例McpNodeExample已提供完整实现参考

2. **SCM AI当前实现的差距**
   - 只有全局开关(enableMcpTools: true/false)
   - 无法为单个节点指定特定MCP工具子集

3. **最佳实施方案**
   - 采用方案二：节点专属ChatClient
   - 在McpToolNode构造时创建独立ChatClient实例
   - 通过节点配置的enabledTools字段过滤工具

### 8.2 实施优先级

| 优先级 | 任务 | 说明 |
|-------|------|------|
| P0 | 修改McpToolNodeConfig | 增加enabled_tools字段 |
| P0 | 重构McpToolNode | 创建节点专属ChatClient |
| P1 | 前端配置界面 | 增加工具多选组件 |
| P2 | 文档和测试 | 使用说明和测试用例 |

### 8.3 回答用户问题

> "关于mcp工具节点,能否指定一个mcp来执行？怎么指定？"

**答案**：

1. **当前无法指定特定MCP工具** ❌
   - SCM AI当前只有全局开关(全部工具或无工具)
   - 无法为单个节点指定特定工具子集

2. **推荐实现方式** ✅
   - 参考Spring AI Alibaba官方McpNodeExample
   - 在节点配置中增加`enabled_tools`字段
   - 节点初始化时创建专属ChatClient,只包含指定工具

3. **配置方法**（实施后）
   ```json
   {
     "tool_input": "查询库位信息",
     "model_name": "gj-deepseek",
     "enabled_tools": [
       "LocationQueryService.queryLocationInfo",
       "StockQueryService.queryStockByLocation"
     ]
   }
   ```

---

## 文档元信息

- **创建时间**: 2025-12-01
- **作者**: Claude Code
- **调研状态**: 完成
- **结论**: 当前不支持,但可参考Spring AI Alibaba官方示例实现节点级工具控制
