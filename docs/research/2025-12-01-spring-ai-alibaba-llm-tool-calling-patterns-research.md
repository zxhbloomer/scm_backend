# Spring AI Alibaba LLM工具调用模式调研报告

## 调研背景

用户询问："scm是不是应该使用的是llm来识别执行的吧，可能执行多个，那么spring ai alibaba 中有几种方式？"

**核心理解**：
- MCP工具不是"指定执行"，而是**LLM根据用户输入自动识别并调用**
- LLM可以**顺序调用多个工具**来完成复杂任务
- 需要调研Spring AI Alibaba支持的不同工具调用模式

---

## 一、LLM工具调用的核心机制

### 1.1 什么是LLM Function Calling？

LLM Function Calling是大语言模型的一种能力，允许LLM：
1. **理解用户意图**：分析用户的自然语言输入
2. **选择合适的工具**：从可用工具列表中选择最合适的
3. **提取参数**：从用户输入中提取工具所需的参数
4. **调用工具**：执行工具并获取结果
5. **合成回答**：将工具结果整合到最终回答中

### 1.2 工具调用流程

```
用户输入："北京的天气怎么样？"
    ↓
LLM分析 → 识别需要 get_weather 工具
    ↓
LLM提取参数 → {"city": "北京"}
    ↓
执行工具 → get_weather("北京") → "北京今天晴天,25℃"
    ↓
LLM合成回答 → "北京今天天气晴朗，温度25摄氏度。"
```

---

## 二、Spring AI Alibaba支持的工具调用模式

### 模式1：ReactAgent单代理多工具模式

**核心特点**：
- 一个ReactAgent配置多个工具
- LLM根据用户输入自动选择调用哪个工具
- 支持**单次对话中调用多个工具**

**示例代码**（来自ToolsExample.java）：

```java
// 1. 定义多个工具
ToolCallback weatherTool = FunctionToolCallback.builder(
    "get_weather",
    new WeatherFunction()
)
.description("Get the current weather for a given city")
.inputType(WeatherInput.class)
.build();

ToolCallback searchTool = FunctionToolCallback.builder(
    "search_web",
    new SearchFunction()
)
.description("Search the web for information")
.inputType(SearchInput.class)
.build();

ToolCallback calculatorTool = FunctionToolCallback.builder(
    "calculate",
    new CalculatorFunction()
)
.description("Perform mathematical calculations")
.inputType(CalculatorInput.class)
.build();

// 2. 创建ReactAgent，配置所有工具
ReactAgent agent = ReactAgent.builder()
    .name("multi_tool_agent")
    .model(chatModel)
    .tools(weatherTool, searchTool, calculatorTool)  // 配置多个工具
    .instruction("根据用户问题选择合适的工具来回答")
    .build();

// 3. LLM自动选择工具调用
agent.call("北京的天气怎么样？");           // LLM调用 weatherTool
agent.call("搜索一下最新的AI新闻");         // LLM调用 searchTool
agent.call("计算 25 * 4 + 10");            // LLM调用 calculatorTool
agent.call("北京天气如何？明天上海呢？");   // LLM可能调用 weatherTool 两次
```

**工作原理**：
1. Agent初始化时，所有工具定义（name、description、inputSchema）传给LLM
2. 用户输入后，LLM分析并决定调用哪个工具
3. LLM生成tool_calls（工具名+参数）
4. AgentToolNode执行对应的工具
5. 工具结果返回给LLM，LLM生成最终回答

**顺序调用示例**：
```java
// 用户问题可能触发多个工具顺序调用
agent.call("先查北京天气，然后帮我计算如果温度是25度，华氏度是多少");

// LLM执行流程：
// 1. 调用 get_weather("北京") → "25℃"
// 2. 调用 calculate("25 * 9/5 + 32") → "77℉"
// 3. 合成回答："北京今天25℃，华氏温度是77℉"
```

---

### 模式2：Agent-as-Tool多代理编排模式

**核心特点**：
- 将子Agent包装成工具供主Agent调用
- 主Agent作为编排者，根据任务调用不同的子Agent
- 每个子Agent专注于特定领域

**示例代码**（来自AgentToolExample.java）：

```java
// 1. 创建专业领域的子Agent
ReactAgent writerAgent = ReactAgent.builder()
    .name("writer_agent")
    .model(chatModel)
    .description("可以写文章")
    .instruction("你是一个知名的作家，擅长写作和创作。")
    .build();

ReactAgent translatorAgent = ReactAgent.builder()
    .name("translator_agent")
    .model(chatModel)
    .description("专门负责文本翻译工作")
    .instruction("你是一个专业翻译，能够准确翻译多种语言。")
    .build();

ReactAgent summarizerAgent = ReactAgent.builder()
    .name("summarizer_agent")
    .model(chatModel)
    .description("专门负责内容总结和提炼")
    .instruction("你是一个内容总结专家，擅长提炼关键信息。")
    .build();

// 2. 创建主Agent，将子Agent作为工具
ReactAgent orchestratorAgent = ReactAgent.builder()
    .name("orchestrator")
    .model(chatModel)
    .instruction("你可以访问多个专业工具：写作、翻译和总结。" +
                "根据用户需求选择合适的工具来完成任务。")
    .tools(
        AgentTool.getFunctionToolCallback(writerAgent),      // 子Agent作为工具
        AgentTool.getFunctionToolCallback(translatorAgent),
        AgentTool.getFunctionToolCallback(summarizerAgent)
    )
    .build();

// 3. LLM自动编排子Agent调用
orchestratorAgent.invoke("请写一篇关于AI的文章，然后翻译成英文，最后给出摘要");

// LLM执行流程：
// 1. 调用 writer_agent → 生成中文文章
// 2. 调用 translator_agent(中文文章) → 生成英文翻译
// 3. 调用 summarizer_agent(英文翻译) → 生成英文摘要
// 4. 合成最终回答
```

**与模式1的区别**：
- 模式1：工具是简单函数（get_weather、calculate等）
- 模式2：工具是完整的Agent，每个Agent内部也可以有自己的工具和推理循环

---

### 模式3：类型化输入输出模式

**核心特点**：
- 通过`inputType`和`outputType`定义工具的输入输出类型
- LLM自动将自然语言转换为结构化输入
- 工具返回结构化输出，LLM合成为自然语言

**示例代码**（来自AgentToolExample.java）：

```java
// 1. 定义输入输出类型
record ArticleRequest(String topic, int wordCount, String style) { }

class ArticleOutput {
    private String title;
    private String content;
    private int characterCount;
    // getters and setters
}

// 2. 创建类型化的Agent工具
ReactAgent writerAgent = ReactAgent.builder()
    .name("typed_writer")
    .model(chatModel)
    .description("根据结构化输入写文章")
    .instruction("根据topic、wordCount、style要求创作文章，" +
                "返回title、content、characterCount。")
    .inputType(ArticleRequest.class)   // 输入类型
    .outputType(ArticleOutput.class)   // 输出类型
    .build();

// 3. 主Agent调用
ReactAgent coordinator = ReactAgent.builder()
    .name("coordinator")
    .model(chatModel)
    .tools(AgentTool.getFunctionToolCallback(writerAgent))
    .build();

coordinator.invoke("请写一篇关于春天的散文，大约150字");

// LLM执行流程：
// 1. LLM将用户输入转换为 ArticleRequest(topic="春天", wordCount=150, style="散文")
// 2. 调用 writerAgent(ArticleRequest) → ArticleOutput
// 3. LLM将 ArticleOutput 转换为自然语言回答
```

**优势**：
- 类型安全，避免参数错误
- 强制结构化输出，便于后续处理
- LLM负责自然语言↔结构化数据的转换

---

## 三、ReactAgent的工具调用循环机制

### 3.1 ReactAgent内部结构

从ReactAgent.java源码分析：

```java
public class ReactAgent extends BaseAgent {
    private final AgentLlmNode llmNode;    // LLM推理节点
    private final AgentToolNode toolNode;   // 工具执行节点
    private final Boolean hasTools;         // 是否有工具

    public ReactAgent(AgentLlmNode llmNode, AgentToolNode toolNode, ...) {
        this.llmNode = llmNode;
        this.toolNode = toolNode;
        this.hasTools = toolNode.getToolCallbacks() != null &&
                       !toolNode.getToolCallbacks().isEmpty();
    }
}
```

### 3.2 工具调用循环流程

```
用户输入
    ↓
┌───────────────────────────────────────┐
│  AgentLlmNode                         │
│  - 接收用户输入                        │
│  - LLM分析并决定：                     │
│    * 直接回答 → END                    │
│    * 需要工具 → 生成tool_calls         │
└───────────────────────────────────────┘
    ↓ (如果有tool_calls)
┌───────────────────────────────────────┐
│  AgentToolNode                        │
│  - 解析tool_calls                     │
│  - 执行对应的工具                      │
│  - 收集工具结果                        │
└───────────────────────────────────────┘
    ↓
┌───────────────────────────────────────┐
│  AgentLlmNode (再次调用)               │
│  - 接收工具结果                        │
│  - LLM合成回答或继续调用工具            │
└───────────────────────────────────────┘
    ↓
最终回答 → END
```

### 3.3 关键代码：AgentToolNode执行

从AgentToolNode.java源码：

```java
public Map<String, Object> apply(OverAllState state, RunnableConfig config) {
    List<Message> messages = (List<Message>) state.value("messages").orElseThrow();
    Message lastMessage = messages.get(messages.size() - 1);

    if (lastMessage instanceof AssistantMessage assistantMessage) {
        // LLM返回了tool_calls
        for (AssistantMessage.ToolCall toolCall : assistantMessage.getToolCalls()) {
            String toolName = toolCall.name();       // 工具名称
            String toolArgs = toolCall.arguments();  // 工具参数（JSON）

            // 根据工具名称找到对应的ToolCallback
            ToolCallback callback = resolve(toolName);

            // 执行工具
            ToolCallResponse response = executeToolCallWithInterceptors(
                toolCall, state, config, callback
            );

            toolResponses.add(response.toToolResponse());
        }
    }

    return Map.of("messages", toolResponses);
}

// 工具解析：根据名称查找ToolCallback
private ToolCallback resolve(String toolName) {
    return toolCallbacks.stream()
        .filter(callback -> callback.getToolDefinition().name().equals(toolName))
        .findFirst()
        .orElseGet(() -> toolCallbackResolver == null ? null :
                        toolCallbackResolver.resolve(toolName));
}
```

---

## 四、SCM AI当前的工具调用实现

### 4.1 当前架构

从McpToolNode.java和McpToolConfig.java分析：

```java
// McpToolNode.java - 节点执行
@Override
protected NodeProcessResult onProcess() {
    McpToolNodeConfig config = checkAndGetConfig(McpToolNodeConfig.class);
    String prompt = buildPrompt();
    String modelName = config.getModelName();

    // 使用LLM的Function Calling能力自动选择和调用工具
    WorkflowUtil.streamingInvokeLLM(wfState, state, node, modelName, prompt);

    return new NodeProcessResult();
}

// McpToolConfig.java - MCP工具注册
@Bean
public ToolCallbackProvider mcpToolCallbackProvider(ApplicationContext context) {
    List<ToolCallback> toolCallbacks = new ArrayList<>();

    // 扫描所有@McpTool注解的方法
    for (Method method : beanClass.getMethods()) {
        if (method.isAnnotationPresent(McpTool.class)) {
            // 创建FunctionToolCallback包装
            ToolCallback callback = McpToolCallbackAdapter.createToolCallback(bean, method);
            toolCallbacks.add(callback);
        }
    }

    return ToolCallbackProvider.from(toolCallbacks);
}
```

### 4.2 工具启用控制

从AiChatBaseService.java和WorkflowUtil.java：

```java
// AiChatBaseService.java:228-236 - 动态ChatClient选择
ChatClient selectedClient;
if (Boolean.TRUE.equals(aiChatOption.getEnableMcpTools())) {
    selectedClient = workflowDomainChatClient;  // 包含所有MCP工具
    log.info("✅ [MCP Control] 启用MCP工具");
} else {
    selectedClient = workflowDomainChatClientNoMcp;  // 无工具
    log.info("✅ [MCP Control] 禁用MCP工具");
}

// WorkflowUtil.java:158-162 - 节点类型检测
boolean isMcpToolNode = isMcpToolNode(node);
chatOption.setEnableMcpTools(isMcpToolNode);
log.info("节点类型判断 - 是否MCP工具节点: {}", isMcpToolNode);
```

**当前实现特点**：
- ✅ 已使用LLM自动识别和调用工具（Function Calling）
- ✅ 支持通过`enableMcpTools`全局开关控制工具可用性
- ✅ 使用`ToolCallbackProvider`统一管理所有MCP工具
- ⚠️ 当前是全局开关，要么所有MCP工具可用，要么都不可用

---

## 五、Spring AI Alibaba的多工具调用能力

### 5.1 单次对话调用多个工具

**问题**：LLM可以在一次对话中调用多个工具吗？

**答案**：✅ 可以

**证据**（来自DefaultBuilder.java）：

```java
// Line 109-196: 工具收集逻辑
List<ToolCallback> regularTools = new ArrayList<>();

// 1. 从直接配置的tools收集
if (CollectionUtils.isNotEmpty(tools)) {
    regularTools.addAll(tools);
}

// 2. 从toolCallbackProviders收集
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

// 所有工具都传给LLM和ToolNode
llmNodeBuilder.toolCallbacks(allTools);  // LLM看到所有工具定义
toolNodeBuilder.toolCallbacks(allTools); // ToolNode可以执行所有工具
```

**实际执行示例**：

```java
ReactAgent agent = ReactAgent.builder()
    .tools(weatherTool, searchTool, calculatorTool)
    .build();

// 用户输入可能触发多次工具调用
agent.call("北京今天天气怎么样？如果温度是25℃，华氏度是多少？");

// LLM执行：
// Round 1: LLM生成 tool_calls = [get_weather("北京"), calculate("25*9/5+32")]
// ToolNode执行两个工具 → ["晴天,25℃", "77"]
// Round 2: LLM接收工具结果 → 合成回答
```

### 5.2 顺序工具调用（Tool Chaining）

**问题**：第二个工具可以使用第一个工具的结果作为输入吗？

**答案**：✅ 可以

**原理**：
1. LLM在第一轮调用工具A
2. 工具A的结果添加到消息历史
3. LLM在第二轮分析历史，调用工具B（参数可引用工具A结果）

**示例**：

```java
// 用户："先搜索今天的新闻，然后总结最重要的3条"

// Round 1:
// LLM: tool_calls = [search_web("今天的新闻")]
// ToolNode: search_web() → "新闻1..., 新闻2..., 新闻3..."

// Round 2:
// LLM看到搜索结果
// LLM: tool_calls = [summarize("新闻1..., 新闻2..., 新闻3...")]
// ToolNode: summarize() → "摘要: ..."

// Round 3:
// LLM合成最终回答
```

---

## 六、对比总结

### 6.1 三种模式对比

| 特性 | 模式1: ReactAgent多工具 | 模式2: Agent-as-Tool | 模式3: 类型化工具 |
|------|------------------------|---------------------|------------------|
| **工具类型** | 简单函数（Function） | 完整Agent | 类型化函数 |
| **LLM角色** | 选择和调用工具 | 编排子Agent | 选择和类型转换 |
| **适用场景** | 单一领域多工具 | 多领域复杂任务 | 结构化输入输出 |
| **复杂度** | 低 | 中 | 中 |
| **示例** | 天气+搜索+计算器 | 写作+翻译+总结 | 结构化文章生成 |

### 6.2 SCM AI与Spring AI Alibaba对照

| 方面 | SCM AI当前实现 | Spring AI Alibaba能力 |
|------|---------------|----------------------|
| **工具定义** | @McpTool注解 → FunctionToolCallback | ✅ 完全一致 |
| **工具注册** | ToolCallbackProvider | ✅ 完全一致 |
| **LLM调用** | Function Calling自动选择 | ✅ 完全一致 |
| **多工具支持** | ✅ 所有MCP工具可用 | ✅ 支持任意数量工具 |
| **顺序调用** | ✅ LLM自动chain | ✅ 支持 |
| **工具过滤** | 全局开关（全有或全无） | ⚠️ 可节点级配置 |

---

## 七、结论

### 7.1 Spring AI Alibaba支持的工具调用方式

**总结为3种主要方式**：

1. **ReactAgent单代理多工具模式**
   - LLM从多个工具中自动选择
   - 支持单次对话调用多个工具
   - 适合功能简单、数量较多的工具集

2. **Agent-as-Tool多代理编排模式**
   - 子Agent作为高级工具
   - 主Agent编排子Agent协作
   - 适合复杂任务和多领域协作

3. **类型化工具模式**
   - 强类型输入输出定义
   - LLM自动处理类型转换
   - 适合需要结构化数据的场景

### 7.2 SCM AI当前实现评估

**已正确实现的部分**：
- ✅ 使用LLM Function Calling自动识别和调用工具
- ✅ 通过`ToolCallbackProvider`管理所有MCP工具
- ✅ 支持LLM在单次对话中调用多个工具
- ✅ 支持工具顺序调用（tool chaining）

**架构符合性**：
- ✅ 完全遵循Spring AI Alibaba的ReactAgent模式
- ✅ 使用标准的`FunctionToolCallback`包装MCP工具
- ✅ LLM自动决定调用哪些工具，无需手动指定

**当前限制**：
- ⚠️ 工具控制是全局开关（enableMcpTools），不支持节点级工具子集配置
- ⚠️ 所有MCP工具要么全部可用，要么全部不可用

---

## 八、参考资料

### 8.1 Spring AI Alibaba源码

| 文件 | 位置 | 关键内容 |
|------|------|---------|
| ToolsExample.java | examples/documentation/.../ToolsExample.java | FunctionToolCallback示例 |
| AgentToolExample.java | examples/documentation/.../AgentToolExample.java | Agent-as-Tool模式 |
| ReactAgent.java | agent-framework/.../ReactAgent.java | Agent内部结构 |
| AgentToolNode.java | agent-framework/.../AgentToolNode.java | 工具执行逻辑 |
| DefaultBuilder.java | agent-framework/.../DefaultBuilder.java | 工具收集机制 |

### 8.2 SCM AI实现

| 文件 | 位置 | 关键内容 |
|------|------|---------|
| McpToolNode.java | scm-ai/.../McpToolNode.java | MCP工具节点执行 |
| McpToolConfig.java | scm-ai/.../McpToolConfig.java | MCP工具注册 |
| WorkflowUtil.java | scm-ai/.../WorkflowUtil.java | 工具启用控制 |
| AiChatBaseService.java | scm-ai/.../AiChatBaseService.java | ChatClient选择 |

---

## 文档元信息

- **创建时间**: 2025-12-01
- **作者**: Claude Code
- **调研状态**: 完成
- **核心结论**: Spring AI Alibaba支持3种主要的LLM工具调用模式，SCM AI当前实现完全符合ReactAgent多工具模式，已正确使用LLM自动识别和顺序调用工具
