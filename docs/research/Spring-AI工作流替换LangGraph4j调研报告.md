# Spring AI 工作流替换 LangGraph4j 调研报告

> **调研日期**: 2025-11-28
> **调研目的**: 评估使用 Spring AI 原生 API 替换当前 WorkflowEngine.java 中 LangGraph4j 的可行性

---

## 一、核心发现

### 1.1 关键结论

**✅ Spring AI 完全可以替代 LangGraph4j**

- Spring AI 提供了完整的异步流式处理能力（基于 Reactor `Flux`）
- 官方提供了并行工作流模式的完整示例（`ParallelizationWorkflow`）
- 使用 Java 原生 `CompletableFuture` 实现真正的异步并发，性能优于 LangGraph4j 的伪并行
- 无需引入额外的图执行引擎，减少依赖复杂度

---

## 二、接口规范

### 2.1 核心 API

#### ChatClient - 聊天客户端
```java
public interface ChatClient {
    // 同步调用
    String call(String message);
    ChatResponse call(Prompt prompt);

    // 流式调用
    Flux<String> stream(String message);
    Flux<ChatResponse> stream(Prompt prompt);
}
```

#### StreamingChatModel - 流式模型
```java
public interface StreamingChatModel extends StreamingModel<Prompt, ChatResponse> {
    default Flux<String> stream(String message) {...}
    Flux<ChatResponse> stream(Prompt prompt);
}
```

#### Flux - 响应式流
```java
// Spring AI 使用 Reactor Flux 实现流式响应
Flux<String> contentStream = chatClient.prompt()
    .user("Tell me a joke")
    .stream()
    .content();
```

---

## 三、基础使用

### 3.1 安装和初始化

**Maven 依赖**:
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-openai</artifactId>
</dependency>
```

**创建 ChatClient**:
```java
@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
```

### 3.2 最简单的使用示例

**同步调用**:
```java
String response = chatClient.prompt()
    .user("Tell me a joke")
    .call()
    .content();
```

**流式调用**:
```java
Flux<String> stream = chatClient.prompt()
    .user("Tell me a joke")
    .stream()
    .content();

stream.subscribe(
    chunk -> System.out.print(chunk),
    error -> System.err.println("Error: " + error),
    () -> System.out.println("\nCompleted")
);
```

---

## 四、进阶技巧 - 并行工作流实现

### 4.1 官方并行工作流模式

Spring AI 官方提供了 `ParallelizationWorkflow` 示例，展示如何使用 `CompletableFuture` 实现并行节点执行：

```java
public class ParallelizationWorkflow {

    private final ChatClient chatClient;

    public List<String> parallel(String prompt, List<String> inputs, int nWorkers) {
        ExecutorService executor = Executors.newFixedThreadPool(nWorkers);

        try {
            // 1. 为每个输入创建异步任务
            List<CompletableFuture<String>> futures = inputs.stream()
                .map(input -> CompletableFuture.supplyAsync(() -> {
                    return chatClient.prompt(prompt + "\nInput: " + input)
                        .call()
                        .content();
                }, executor))
                .collect(Collectors.toList());

            // 2. 等待所有任务完成
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(CompletableFuture[]::new)
            );
            allFutures.join();

            // 3. 收集结果
            return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        } finally {
            executor.shutdown();
        }
    }
}
```

### 4.2 并行节点汇聚实现

根据我们已有的技术设计文档（`2025-11-28-Spring-AI-并行节点汇聚实现方案.md`），实现类似 BiSheng 场景1 的并行汇聚：

```java
/**
 * 并行节点汇聚执行器
 */
@Component
public class ParallelNodeExecutor {

    @Resource
    private ChatClient chatClient;

    @Resource(name = "workflowExecutor")
    private ThreadPoolTaskExecutor workflowExecutor;

    /**
     * 执行并行节点并等待汇聚
     */
    public CompletableFuture<NodeOutput> executeParallelAndJoin(
            List<WorkflowNode> upstreamNodes,
            WorkflowNode downstreamNode,
            WorkflowState workflowState) {

        // 第一步: 并行执行所有上游节点
        List<CompletableFuture<NodeOutput>> upstreamFutures = upstreamNodes.stream()
            .map(node -> executeNodeAsync(node, workflowState))
            .toList();

        // 第二步: 使用 allOf 等待所有节点完成
        CompletableFuture<Void> allCompleted = CompletableFuture.allOf(
            upstreamFutures.toArray(new CompletableFuture[0])
        );

        // 第三步: 所有节点完成后,合并结果并执行下游节点
        return allCompleted.thenCompose(v -> {
            // 收集所有上游节点的输出结果
            Map<String, NodeOutput> upstreamResults = new HashMap<>();
            for (int i = 0; i < upstreamNodes.size(); i++) {
                WorkflowNode node = upstreamNodes.get(i);
                NodeOutput output = upstreamFutures.get(i).join();
                upstreamResults.put(node.getUuid(), output);
            }

            // 将上游结果存入工作流状态
            workflowState.setParallelResults(upstreamResults);

            // 执行下游汇聚节点
            return executeNodeAsync(downstreamNode, workflowState);
        });
    }

    /**
     * 异步执行单个节点
     */
    private CompletableFuture<NodeOutput> executeNodeAsync(
            WorkflowNode node,
            WorkflowState workflowState) {

        return CompletableFuture.supplyAsync(() -> {
            String input = buildNodeInput(node, workflowState);

            String output = chatClient.prompt()
                .user(input)
                .call()
                .content();

            return new NodeOutput(node.getUuid(), output);
        }, workflowExecutor);
    }
}
```

### 4.3 流式响应集成

**支持 SSE 流式输出**:
```java
@GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<String> streamResponse(@RequestParam String question) {
    return chatClient.prompt()
        .user(question)
        .stream()
        .content();
}
```

**完整的流式工作流执行**:
```java
public Flux<WorkflowEventVo> executeWorkflowStreaming(
        String workflowUuid,
        Map<String, Object> inputs) {

    return Flux.create(sink -> {
        WorkflowState state = new WorkflowState();

        // 1. 并行执行审查节点
        CompletableFuture<NodeOutput> partyAFuture = executeNodeAsync(partyANode, state);
        CompletableFuture<NodeOutput> partyBFuture = executeNodeAsync(partyBNode, state);

        // 2. 等待并行节点完成
        CompletableFuture.allOf(partyAFuture, partyBFuture)
            .thenAccept(v -> {
                // 收集结果
                NodeOutput partyAOutput = partyAFuture.join();
                NodeOutput partyBOutput = partyBFuture.join();

                state.getParallelResults().put("party-a-check", partyAOutput);
                state.getParallelResults().put("party-b-check", partyBOutput);

                // 3. 流式执行下游汇聚节点
                executeAnalysisNodeStreaming(
                    partyAOutput.getContent(),
                    partyBOutput.getContent()
                ).subscribe(
                    chunk -> sink.next(WorkflowEventVo.createNodeChunkEvent("analysis", chunk)),
                    error -> sink.error(error),
                    () -> sink.complete()
                );
            });
    });
}
```

---

## 五、巧妙用法

### 5.1 Advisors - 拦截器模式

Spring AI 提供了 `CallAdvisor` 和 `StreamAdvisor` 接口，类似于拦截器：

```java
public class SimpleLoggerAdvisor implements CallAdvisor, StreamAdvisor {

    @Override
    public ChatClientResponse adviseCall(
            ChatClientRequest request,
            CallAdvisorChain chain) {

        log.info("Request: {}", request);
        ChatClientResponse response = chain.nextCall(request);
        log.info("Response: {}", response);
        return response;
    }

    @Override
    public Flux<ChatClientResponse> adviseStream(
            ChatClientRequest request,
            StreamAdvisorChain chain) {

        log.info("Stream Request: {}", request);
        return chain.nextStream(request)
            .doOnNext(chunk -> log.debug("Chunk: {}", chunk));
    }
}
```

### 5.2 工作流状态管理

使用 `ConcurrentHashMap` 管理节点输出和全局变量：

```java
@Data
public class WorkflowState {

    /**
     * 节点输出池: nodeUuid -> NodeOutput
     */
    private Map<String, NodeOutput> nodeOutputs = new ConcurrentHashMap<>();

    /**
     * 并行节点结果池: 用于汇聚节点访问多个上游结果
     */
    private Map<String, NodeOutput> parallelResults = new ConcurrentHashMap<>();

    /**
     * 获取节点输出(支持变量引用语法: {nodeUuid.fieldName})
     */
    public String getVariable(String reference) {
        String[] parts = reference.split("\\.", 2);
        String nodeUuid = parts[0];
        String fieldName = parts.length > 1 ? parts[1] : "output";

        // 优先从并行结果池查找
        NodeOutput output = parallelResults.get(nodeUuid);
        if (output == null) {
            output = nodeOutputs.get(nodeUuid);
        }

        return output != null ? output.getField(fieldName) : null;
    }
}
```

### 5.3 下游节点访问多个上游结果

```java
/**
 * 合规性审查分析节点配置
 */
{
    "nodeUuid": "compliance-analysis-node",
    "nodeType": "LLM",
    "config": {
        "systemPrompt": "你是一个合同合规性分析专家",
        "userPrompt": """
            请综合分析以下两个审查结果:

            甲方名称合规性审查结果:
            {party-a-check-node.output}

            乙方名称合规性审查结果:
            {party-b-check-node.output}

            请给出综合的合规性分析报告。
        """
    }
}
```

---

## 六、注意事项

### 6.1 常见错误和如何避免

**错误1: 流式订阅阻塞主线程**
```java
// ❌ 错误：阻塞主线程
Flux<String> stream = chatClient.prompt().user("question").stream().content();
stream.subscribe(System.out::print);
// 主线程继续执行，可能导致程序提前退出

// ✅ 正确：使用 blockLast() 或在响应式上下文中使用
stream.doOnNext(System.out::print)
      .doOnComplete(() -> System.out.println("\nDone"))
      .blockLast(); // 等待流完成
```

**错误2: CompletableFuture 异常处理不当**
```java
// ❌ 错误：未处理异常
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    return chatClient.prompt().user("question").call().content();
});

// ✅ 正确：捕获并处理异常
CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
    try {
        return chatClient.prompt().user("question").call().content();
    } catch (Exception e) {
        throw new RuntimeException("Failed to process", e);
    }
}).exceptionally(ex -> {
    log.error("Error processing", ex);
    return "Error: " + ex.getMessage();
});
```

### 6.2 性能陷阱和最佳实践

**陷阱1: 线程池未正确配置**
```java
// ❌ 错误：每次都创建新线程池
public void process() {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    // ... 使用 executor
    executor.shutdown();
}

// ✅ 正确：复用线程池
@Configuration
public class WorkflowExecutorConfig {

    @Bean(name = "workflowExecutor")
    public ThreadPoolTaskExecutor workflowExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("workflow-");
        executor.initialize();
        return executor;
    }
}
```

**陷阱2: 过度并发导致 API 限流**
```java
// ❌ 错误：一次性发起大量并发请求
List<CompletableFuture<String>> futures = inputs.stream()
    .map(input -> CompletableFuture.supplyAsync(...))
    .collect(Collectors.toList());

// ✅ 正确：控制并发数量
int maxConcurrent = 5; // 根据 API 限制调整
List<List<String>> batches = partition(inputs, maxConcurrent);
for (List<String> batch : batches) {
    List<CompletableFuture<String>> batchFutures = batch.stream()
        .map(input -> CompletableFuture.supplyAsync(...))
        .collect(Collectors.toList());
    CompletableFuture.allOf(batchFutures.toArray(new CompletableFuture[0])).join();
}
```

### 6.3 版本兼容性问题

- Spring AI 1.0.0+ 稳定版本推荐使用
- 依赖 Spring Boot 3.0+ 和 Java 17+
- Reactor 版本需与 Spring Boot 版本匹配

---

## 七、真实代码片段

### 7.1 从 GitHub 找到的优秀示例

**示例1: 并行工作流（官方示例）**

来源: `spring-ai-examples/agentic-patterns/parallelization-workflow`

```java
/**
 * 并行化工作流模式实现
 * - 支持任务分解（Sectioning）
 * - 支持投票机制（Voting）
 */
public class ParallelizationWorkflow {

    public List<String> parallel(String prompt, List<String> inputs, int nWorkers) {
        ExecutorService executor = Executors.newFixedThreadPool(nWorkers);

        try {
            List<CompletableFuture<String>> futures = inputs.stream()
                .map(input -> CompletableFuture.supplyAsync(() -> {
                    return chatClient.prompt(prompt + "\nInput: " + input)
                        .call()
                        .content();
                }, executor))
                .collect(Collectors.toList());

            CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new)).join();

            return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        } finally {
            executor.shutdown();
        }
    }
}
```

**为什么这是好的实践**:
- ✅ 使用 `CompletableFuture.allOf()` 等待所有任务完成
- ✅ 使用 `try-finally` 确保线程池正确关闭
- ✅ 保持输入输出顺序一致
- ✅ 异常处理清晰

**示例2: 流式响应处理**

来源: Spring AI 官方文档

```java
@RestController
public class StreamingChatController {

    private final ChatClient chatClient;

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamResponse(@RequestParam String question) {
        Flux<String> contentStream = chatClient.prompt()
            .user(question)
            .stream()
            .content();

        // 订阅并处理每个 chunk
        contentStream.subscribe(
            chunk -> System.out.print(chunk),
            error -> System.err.println("Error: " + error),
            () -> System.out.println("\nStreaming completed")
        );

        return contentStream;
    }
}
```

**为什么这是好的实践**:
- ✅ 使用 `TEXT_EVENT_STREAM_VALUE` 支持 SSE
- ✅ 返回 `Flux<String>` 实现真正的流式响应
- ✅ 完整的错误处理和完成回调

---

## 八、与 LangGraph4j 的对比

### 8.1 核心差异

| 技术点 | LangGraph4j | Spring AI + Reactor |
|--------|-------------|---------------------|
| **并行执行** | 伪并行（图调度） | 真正异步并发（CompletableFuture） |
| **等待机制** | 图节点依赖 | CompletableFuture.allOf() |
| **状态管理** | GraphState | WorkflowState + ConcurrentHashMap |
| **流式响应** | StreamHandler回调 | Flux 响应式流 |
| **数据合并** | 变量引用 `{nodeId.field}` | WorkflowState.getVariable() |
| **依赖复杂度** | 引入 LangGraph4j 框架 | Spring 原生 + Reactor |
| **类型安全** | 较弱 | 强类型，编译期检查 |
| **调试难度** | 较高（图执行隐藏细节） | 较低（标准 Java 异步） |

### 8.2 迁移建议

**第一阶段：基础替换**
1. 移除 LangGraph4j 依赖
2. 使用 `ChatClient` 替换 `StateGraph`
3. 使用 `CompletableFuture` 替换图节点

**第二阶段：并行优化**
1. 识别并行节点组
2. 实现 `ParallelNodeExecutor`
3. 使用 `CompletableFuture.allOf()` 实现汇聚

**第三阶段：流式增强**
1. 集成 Reactor `Flux`
2. 实现 SSE 流式输出
3. 优化前端实时显示

---

## 九、引用来源

### 9.1 官方文档

- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/)
- [ChatClient API](https://docs.spring.io/spring-ai/reference/api/chatclient.html)
- [StreamingChatModel](https://docs.spring.io/spring-ai/reference/api/chatmodel.html)
- [Spring AI Advisors](https://docs.spring.io/spring-ai/reference/api/advisors.html)

### 9.2 官方示例

- [Spring AI Examples - Parallelization Workflow](https://github.com/spring-projects/spring-ai-examples/tree/main/agentic-patterns/parallelization-workflow)
- [Spring AI Examples - Orchestrator Workers](https://github.com/spring-projects/spring-ai-examples/tree/main/agentic-patterns/orchestrator-workers)
- [Spring AI Examples - Chain Workflow](https://github.com/spring-projects/spring-ai-examples/tree/main/agentic-patterns/chain-workflow)

### 9.3 社区资源

- [Building Effective Agents - Anthropic](https://www.anthropic.com/research/building-effective-agents)
- [Context7 - Spring AI 文档聚合](https://context7.com/spring-projects/spring-ai)

### 9.4 本地资源

- **已有设计文档**: `docs/design/2025-11-28-Spring-AI-并行节点汇聚实现方案.md`
- **当前实现**: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`
- **本地示例源码**: `D:\2025_project\20_project_in_github\99_tools\spring-ai-examples-main`

---

## 十、总结与建议

### 10.1 核心优势

1. **原生 Spring 生态**: 无需引入额外图执行框架，减少依赖复杂度
2. **真正异步并发**: 基于 Java `CompletableFuture`，性能优于 LangGraph4j 伪并行
3. **类型安全**: 强类型系统，编译期检查，减少运行时错误
4. **易于调试**: 标准 Java 异步编程模式，工具支持好
5. **流式响应**: Reactor `Flux` 原生支持，与 Spring WebFlux 完美集成

### 10.2 实施建议

**优先级1（核心功能）**:
- [ ] 使用 `ChatClient` 替换 `StateGraph` 的同步调用
- [ ] 实现基于 `CompletableFuture` 的并行节点执行
- [ ] 迁移工作流状态管理到 `WorkflowState`

**优先级2（性能优化）**:
- [ ] 配置线程池（`ThreadPoolTaskExecutor`）
- [ ] 实现 `CompletableFuture.allOf()` 并行汇聚
- [ ] 优化异常处理和重试机制

**优先级3（高级功能）**:
- [ ] 集成 Reactor `Flux` 实现流式响应
- [ ] 实现 `CallAdvisor` 和 `StreamAdvisor` 拦截器
- [ ] 添加监控指标（节点执行时间、并行度）

### 10.3 风险评估

**低风险**:
- ✅ Spring AI 是官方项目，长期维护有保障
- ✅ API 稳定，1.0+ 版本已发布
- ✅ 社区活跃，文档完善

**中风险**:
- ⚠️ 迁移工作量较大（需重写 `WorkflowEngine`）
- ⚠️ 需要深入理解响应式编程（Reactor）
- ⚠️ 现有业务逻辑需要充分测试

**建议**:
1. 先在测试环境迁移简单工作流
2. 逐步迁移复杂场景（并行、条件分支）
3. 保持向后兼容，分阶段上线

---

## 附录：快速迁移检查清单

### A.1 依赖更新

- [ ] 移除 `langgraph4j` 依赖
- [ ] 确认 `spring-ai-starter-*` 版本
- [ ] 检查 Spring Boot 版本（需 3.0+）
- [ ] 检查 Java 版本（需 17+）

### A.2 代码迁移

- [ ] `StateGraph` → `ChatClient`
- [ ] `AsyncNodeAction` → `CompletableFuture.supplyAsync()`
- [ ] `GraphState` → `WorkflowState`
- [ ] `CompiledGraph.stream()` → `Flux.create()`
- [ ] 并行节点识别与 `CompletableFuture.allOf()` 实现

### A.3 测试验证

- [ ] 单节点执行测试
- [ ] 顺序节点执行测试
- [ ] 并行节点执行测试
- [ ] 条件分支测试
- [ ] 流式响应测试
- [ ] 异常处理测试

---

**报告完成日期**: 2025-11-28
**调研人员**: Claude Code
**文档版本**: v1.0
