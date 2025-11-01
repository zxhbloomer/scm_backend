# 工作流流式显示修复方案 - blockLast()问题诊断和解决

**状态**：✅ 已实施完成（2025-10-31）

**修改文件**：
- `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowUtil.java`

**关键改动**：
- 移除 `.blockLast()`，改用 `.subscribe()` + `CountDownLatch`
- 添加 import: `java.util.concurrent.CountDownLatch`, `TimeUnit`, `AtomicReference`
- 添加 `InterruptedException` 异常处理
- 更新方法注释，说明修改原因和工作原理

---

## 问题诊断

### 现象
- 前端工作流聊天页面不能流式显示
- 页面卡在"工作流执行中"，然后瞬间显示完整结果
- 浏览器EventStream显示所有事件的时间戳完全相同（22:56:02.272）
- 后端日志显示事件生成跨越19秒（22:55:43 - 22:56:02）

### 根本原因

**问题代码位置**：`WorkflowUtil.streamingInvokeLLM()` Line 194

```java
chatModel.stream(promptObj)
    .doOnNext(chatResponse -> {
        workflowStreamHandler.sendNodeChunk(node.getUuid(), chunk);
    })
    .doOnComplete(() -> {
        nodeState.getOutputs().add(output);
    })
    .blockLast();  // ❌ 问题所在：阻塞调用导致事件缓冲
```

**技术分析**：

1. **`.blockLast()` 的行为**：
   - 阻塞当前线程，等待整个Flux流完成
   - 虽然 `doOnNext` 会在每个chunk到达时执行
   - 但因为在**同一个线程中阻塞等待**
   - Reactor会**缓冲所有通过FluxSink发送的事件**
   - 直到 `blockLast()` 返回，所有事件才一次性发送

2. **Spring WebFlux SSE要求**：
   - `Flux` 必须是**真正的异步流**
   - 事件必须在**不同的时间点**被emit
   - 如果在单线程同步方法中连续调用 `fluxSink.next()`，Reactor会缓冲这些事件

3. **WorkflowEngine的同步执行模型**：
   - `workflowEngine.run()` 是同步方法（WorkflowStarter.java Line 137）
   - 节点按顺序执行，每个节点的 `onProcess()` 同步调用
   - `streamingInvokeLLM()` 在节点执行过程中被同步调用
   - 使用 `blockLast()` 等待LLM响应完成，保持同步语义

### 对比：AI Chat的正确实现

**AI Chat Controller** (AiConversationController.java Line 264):
```java
aiConversationService.chatStreamWithCallback(request, userId, streamHandler);
```

**AI Chat Service** (AiConversationService.java Line 95-131):
```java
aiChatBaseService.chatWithMemoryStream(aiChatOption)  // 返回 ChatClient.StreamResponseSpec
    .chatResponse()                                    // 真正的Reactor Flux<ChatResponse>
    .doOnNext(chatResponse -> {
        streamHandler.onContent(content);              // 每个chunk立即触发
    })
    .doOnComplete(() -> {
        streamHandler.onComplete(finalResponse);
    })
    .subscribe();  // ✅ 异步订阅，立即返回，不阻塞
```

**关键区别**：
- AI Chat使用 `.subscribe()` - 异步订阅，立即返回
- Workflow使用 `.blockLast()` - 阻塞等待，缓冲所有事件
- AI Chat的Flux在**后台线程**中异步执行
- Workflow的Flux在**同一个线程**中同步执行

---

## 解决方案

### 方案1：保持同步执行 + 使用CountDownLatch（推荐）

**核心思路**：
- 移除 `blockLast()`，改用 `CountDownLatch` 阻塞
- Flux异步执行，在 `doOnComplete` 中释放latch
- 主线程通过 `latch.await()` 等待完成
- FluxSink发送事件时不阻塞，真正实现流式

**修改文件**：
- `WorkflowUtil.java` - `streamingInvokeLLM()` 方法

**代码实现**：

```java
public static void streamingInvokeLLM(WfState wfState, WfNodeState nodeState, AiWorkflowNodeVo node,
                                       String modelName, String prompt) {
    log.info("invoke LLM (streaming), modelName: {}, nodeUuid: {}, prompt length: {}",
            modelName, node.getUuid(), StringUtils.isNotBlank(prompt) ? prompt.length() : 0);

    try {
        // 1. 获取 WorkflowStreamHandler
        WorkflowStreamHandler workflowStreamHandler = wfState.getStreamHandler();
        if (workflowStreamHandler == null) {
            throw new RuntimeException("WorkflowStreamHandler not found in WfState");
        }

        // 2. 获取 ChatModel
        AiModelProvider aiModelProvider = SpringUtil.getBean(AiModelProvider.class);
        if (aiModelProvider == null) {
            throw new RuntimeException("AiModelProvider not found in Spring context");
        }

        var chatModel = aiModelProvider.getChatModel();
        if (chatModel == null) {
            throw new RuntimeException("ChatModel not found in Spring context");
        }

        // 3. 构建 Prompt
        org.springframework.ai.chat.messages.UserMessage userMessage =
            new org.springframework.ai.chat.messages.UserMessage(prompt);
        org.springframework.ai.chat.prompt.Prompt promptObj =
            new org.springframework.ai.chat.prompt.Prompt(java.util.List.of(userMessage));

        // 4. 使用 CountDownLatch 实现同步等待，但允许异步流式发送
        final StringBuilder completeContentBuilder = new StringBuilder();
        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> errorRef = new AtomicReference<>();

        // 5. 异步流式调用
        chatModel.stream(promptObj)
                .doOnNext(chatResponse -> {
                    // 获取内容片段
                    String chunk = chatResponse.getResult().getOutput().getText();
                    if (StringUtils.isNotBlank(chunk)) {
                        completeContentBuilder.append(chunk);

                        // ✅ 发送 NODE_CHUNK 事件到前端 - 不阻塞，立即发送
                        workflowStreamHandler.sendNodeChunk(node.getUuid(), chunk);

                        log.debug("LLM chunk received for node: {}, length: {}",
                                node.getUuid(), chunk.length());
                    }
                })
                .doOnComplete(() -> {
                    // 流式完成，将完整响应添加到节点输出
                    String fullContent = completeContentBuilder.toString();
                    NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", fullContent);
                    nodeState.getOutputs().add(output);

                    log.info("LLM stream completed for node: {}, total length: {}",
                            node.getUuid(), fullContent.length());

                    // ✅ 释放latch，允许主线程继续
                    latch.countDown();
                })
                .doOnError(error -> {
                    log.error("LLM stream error for node: {}", node.getUuid(), error);

                    // 设置节点状态为失败
                    nodeState.setProcessStatus(4); // 4-失败
                    nodeState.setProcessStatusRemark("LLM 流式调用失败: " + error.getMessage());

                    errorRef.set(error);
                    latch.countDown();  // 错误时也释放latch
                })
                .subscribe();  // ✅ 异步订阅，立即返回

        // 6. 等待流式完成（主线程阻塞，但不影响Flux异步执行）
        boolean completed = latch.await(300, TimeUnit.SECONDS);  // 5分钟超时

        if (!completed) {
            throw new RuntimeException("LLM 流式调用超时（5分钟）");
        }

        // 检查是否有错误
        Throwable error = errorRef.get();
        if (error != null) {
            throw new RuntimeException("LLM 流式调用失败: " + error.getMessage(), error);
        }

        log.info("LLM streaming invoked and completed for node: {}", node.getUuid());

    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        log.error("invoke LLM (streaming) interrupted for node: {}", node.getUuid(), e);
        nodeState.setProcessStatus(4);
        nodeState.setProcessStatusRemark("LLM 流式调用被中断");
        throw new RuntimeException("LLM 流式调用被中断", e);
    } catch (Exception e) {
        log.error("invoke LLM (streaming) failed for node: {}", node.getUuid(), e);
        nodeState.setProcessStatus(4);
        nodeState.setProcessStatusRemark("LLM 流式调用失败: " + e.getMessage());
        throw new RuntimeException("LLM 流式调用失败: " + e.getMessage(), e);
    }
}
```

**关键改进**：
1. ❌ 移除 `.blockLast()` - 不再阻塞Reactor线程
2. ✅ 改用 `.subscribe()` - 异步订阅，Flux在后台执行
3. ✅ 使用 `CountDownLatch` - 主线程等待完成，但不阻塞Flux
4. ✅ `doOnNext` 中的 `sendNodeChunk()` 立即发送事件，不缓冲
5. ✅ 添加超时机制（5分钟）和错误处理
6. ✅ 使用 `AtomicReference` 传递错误信息

**为什么这样能工作**：
- `chatModel.stream()` 返回的Flux在**订阅后**开始执行
- `.subscribe()` 启动异步执行，立即返回
- Flux在**Reactor调度器**的后台线程中运行
- 每个chunk到达时，`doOnNext` 在**不同的时间点**执行
- `fluxSink.next()` 立即将事件推送到前端，**不缓冲**
- 主线程通过 `latch.await()` 阻塞，但不影响Flux的异步执行
- `doOnComplete` 在Flux完成时释放latch，主线程继续

---

### 方案2：异步执行 + CompletableFuture（备选）

**核心思路**：
- 完全异步化，使用 `CompletableFuture` 返回结果
- 修改 `WorkflowEngine` 和所有调用方支持异步
- 更符合Reactive编程范式，但改动较大

**优点**：
- 真正的异步，不阻塞任何线程
- 符合Reactive编程最佳实践

**缺点**：
- 需要修改 `WorkflowEngine.run()` 方法签名
- 需要修改所有节点的 `onProcess()` 方法
- 影响范围大，风险高

**不推荐原因**：
- WorkflowEngine当前是同步执行模型
- 节点之间有严格的依赖关系（前一个节点的输出是后一个节点的输入）
- 异步化会增加复杂度，可能引入并发问题

---

## 实施计划

### 第1步：修改WorkflowUtil.streamingInvokeLLM()

**文件**：`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowUtil.java`

**修改内容**：
1. 添加必要的import：
   ```java
   import java.util.concurrent.CountDownLatch;
   import java.util.concurrent.TimeUnit;
   import java.util.concurrent.atomic.AtomicReference;
   ```

2. 替换 `streamingInvokeLLM()` 方法（Line 132-204）为上述方案1的代码

### 第2步：测试验证

**测试场景**：
1. 创建包含LLM节点的工作流（如LLMAnswerNode）
2. 在前端工作流聊天页面执行工作流
3. 观察前端是否实时显示chunk
4. 检查浏览器EventStream的时间戳是否递增
5. 验证完整输出是否正确

**预期结果**：
- 前端实时显示LLM生成的chunk（每个chunk到达时立即显示）
- 浏览器EventStream的事件时间戳递增（不再是同一时间）
- 后端日志显示chunk按时间顺序生成和发送
- 最终输出完整且正确

### 第3步：压力测试

**测试内容**：
1. 多个并发工作流执行
2. 长时间运行的工作流（接近5分钟超时）
3. LLM返回大量数据的场景
4. 错误场景（LLM API失败、超时等）

### 第4步：性能监控

**监控指标**：
- 内存使用（确认没有内存泄漏）
- 线程数量（确认CountDownLatch没有导致线程泄漏）
- 响应时间（确认没有性能下降）
- 事件延迟（前端接收到chunk的延迟）

---

## 风险评估

### 技术风险

**低风险**：
- ✅ 只修改一个方法，影响范围可控
- ✅ 不改变WorkflowEngine的执行模型
- ✅ 不影响其他非流式节点
- ✅ CountDownLatch是标准Java并发工具，成熟稳定

**需要注意**：
- ⚠️ 超时时间设置（默认5分钟，可能需要根据实际情况调整）
- ⚠️ 错误处理（确保所有异常路径都释放latch）
- ⚠️ 线程中断处理（`InterruptedException`）

### 业务风险

**无业务风险**：
- 只修复流式显示问题，不改变业务逻辑
- 最终输出结果与之前完全一致
- 用户体验改善（实时显示 vs 等待完成后显示）

---

## 回滚计划

如果修复后出现问题，可以快速回滚到原实现：

```java
// 回滚到原代码：使用 blockLast()
chatModel.stream(promptObj)
    .doOnNext(chatResponse -> {
        // ... chunk处理 ...
        workflowStreamHandler.sendNodeChunk(node.getUuid(), chunk);
    })
    .doOnComplete(() -> {
        // ... 输出处理 ...
        nodeState.getOutputs().add(output);
    })
    .blockLast();  // 恢复原来的阻塞调用
```

---

## KISS原则评估

### 1. 这是个真问题还是臆想出来的？
✅ **真问题**
- 前端用户明确反馈无法流式显示
- 浏览器EventStream证据确凿（所有事件同一时间戳）
- 影响用户体验（等待19秒 vs 实时显示）

### 2. 有更简单的方法吗？
✅ **当前方案已经是最简方案**
- 只修改一个方法（`streamingInvokeLLM`）
- 不改变WorkflowEngine的执行模型
- 使用标准Java并发工具（CountDownLatch）
- 不引入新的依赖或框架

**其他方案对比**：
- 方案2（异步化）：需要修改多个文件，影响范围大 ❌
- 前端轮询：绕过问题，不解决根本原因 ❌
- WebSocket替代SSE：引入新技术栈，复杂度高 ❌

### 3. 会破坏什么吗？
✅ **零破坏性**
- 向后兼容：不改变方法签名和返回类型
- 不影响其他节点：只修改LLM相关节点的流式行为
- 不改变业务逻辑：最终输出完全一致
- 不改变执行顺序：仍然是同步顺序执行

### 4. 当前项目真的需要这个功能吗？
✅ **必要功能**
- 用户明确需求：工作流聊天页面需要流式显示
- 已有参照实现：AI Chat已经正确实现流式
- 提升用户体验：实时反馈 vs 长时间等待
- 符合产品定位：AI交互应该是流式的

---

## 总结

### 问题本质
- 使用 `.blockLast()` 导致Reactor Flux在同步线程中执行
- 所有 `fluxSink.next()` 调用被缓冲，直到 `blockLast()` 返回才一次性发送

### 解决方案
- 使用 `.subscribe()` + `CountDownLatch` 替代 `.blockLast()`
- Flux异步执行，主线程通过latch等待完成
- 事件立即发送，不缓冲

### 实施策略
- 只修改一个方法，影响可控
- 保持同步执行模型，不引入额外复杂度
- 添加超时和错误处理，确保健壮性

### 预期效果
- 前端实时显示LLM生成的chunk
- 用户体验显著改善（实时反馈 vs 长时间等待）
- 与AI Chat的流式体验一致
