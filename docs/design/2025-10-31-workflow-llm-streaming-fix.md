# 工作流 LLM 流式输出修复方案

**日期**: 2025-10-31
**作者**: SCM-AI团队
**问题**: 工作流执行 LLM 节点时，NODE_CHUNK 事件被 Reactor 丢弃（onNextDropped）
**方案**: 直接在 WorkflowUtil 中调用 ChatModel.stream()，移除中间回调层

---

## 问题诊断

### 问题现象
- 工作流执行 LLM 节点时，后端生成流式响应，但前端只收到最终结果
- 日志显示：`onNextDropped: WorkflowEventVo(event=[NODE_CHUNK_xxx])`
- 前端事件序列：[START] → [NODE_RUN] → [NODE_INPUT] → [NODE_OUTPUT] → [DONE]
- **缺失**：[NODE_CHUNK] 事件

### 根本原因

**错误的调用链路**：
```
WorkflowStarter.streaming() {
    return Flux.create(fluxSink -> {
        workflowEngine.run() {
            runNode() {
                WorkflowUtil.streamingInvokeLLM() {
                    AiConversationService.chatStreamWithCallback() {
                        chatWithMemoryStream()
                            .doOnNext(...)
                            .subscribe();  // ❌ 在这里断裂
                    }
                }
            }
        }
    })
    .subscribeOn(Schedulers.boundedElastic());
}
```

**技术原因**：
- 外层 `Flux.create` 创建了响应式上下文
- 内层 `chatStreamWithCallback()` 在方法内部调用 `.subscribe()`
- 两个 Flux 没有建立响应式连接
- Reactor 检测到背压，触发 `onNextDropped` 丢弃事件

### 对比分析

| 功能 | Controller | Service | 流式处理方式 | 是否成功 |
|------|-----------|---------|------------|---------|
| AI问答 | `Flux.create` + 回调 | `chatStreamWithCallback()` | 在 Flux.create 内部创建回调 | ✅ |
| 知识库RAG | `Flux.create` + 直接调用 | `ChatModel.stream()` | 在 Flux.create 内部直接 subscribe | ✅ |
| 工作流 | `Flux.create` + 引擎 | `chatStreamWithCallback()` | Service 内部 subscribe | ❌ |

---

## 解决方案

### 方案选择

**方案1**（采用）：在 WorkflowUtil 中直接调用 ChatModel
- 改动最小：只修改一个文件
- 逻辑清晰：和 RAG 实现一致
- 零破坏性：不影响 AI问答和知识库问答

**方案2**（未采用）：改造 AiConversationService 返回 Flux
- 需要修改 AiConversationService 方法签名
- 影响 AI问答的调用方式
- 破坏性修改

**方案3**（未采用）：完全重写为 SseEmitter
- 需要大量重构
- 破坏现有 WebFlux 架构
- 工作量大

### 架构设计

**修复后的调用链路**：
```
WorkflowStarter.streaming() {
    return Flux.create(fluxSink -> {
        workflowEngine.run() {
            runNode() {
                WorkflowUtil.streamingInvokeLLM() {
                    ChatModel.stream(prompt)
                        .doOnNext(chunk -> {
                            workflowStreamHandler.sendNodeChunk(uuid, chunk)
                            // ✅ 事件通过 fluxSink 正确发送
                        })
                        .blockLast() // 同步等待流完成
                }
            }
        }
    })
    .subscribeOn(Schedulers.boundedElastic());
}
```

**时序图**：
```
前端SSE           Controller           WorkflowStarter      WorkflowEngine       ChatModel
  │                   │                      │                   │                  │
  │◄─────SSE连接──────┤                      │                   │                  │
  │                   │                      │                   │                  │
  │                   │──────streaming()────►│                   │                  │
  │                   │                      │─────run()────────►│                  │
  │                   │                      │                   │─────stream()────►│
  │                   │                      │                   │                  │
  │◄──[START]─────────┼──────────────────────┼───────────────────┤                  │
  │                   │                      │                   │                  │
  │◄──[NODE_RUN]──────┼──────────────────────┼───────────────────┤                  │
  │                   │                      │                   │                  │
  │◄──[NODE_CHUNK]────┼──────────────────────┼───────────────────┼◄─────chunk───────┤
  │◄──[NODE_CHUNK]────┼──────────────────────┼───────────────────┼◄─────chunk───────┤
  │◄──[NODE_CHUNK]────┼──────────────────────┼───────────────────┼◄─────chunk───────┤
  │                   │                      │                   │                  │
  │◄──[NODE_OUTPUT]───┼──────────────────────┼───────────────────┤                  │
  │◄──[DONE]──────────┼──────────────────────┼───────────────────┤                  │
  │                   │                      │                   │                  │
```

---

## 实施细节

### 修改文件
`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowUtil.java`

### 关键改动

**移除**：
- 对 `AiConversationService.chatStreamWithCallback()` 的调用
- 对 `CallbackStreamHandler` 的依赖

**新增**：
- 直接获取 `ChatModel` 并调用 `stream()` 方法
- 使用 `blockLast()` 同步消费流式响应
- 在 `doOnNext` 中通过 `WorkflowStreamHandler` 发送 NODE_CHUNK 事件

### 核心代码

```java
public static void streamingInvokeLLM(WfState wfState, WfNodeState nodeState, AiWorkflowNodeVo node,
                                       String modelName, String prompt) {
    // 1. 获取 WorkflowStreamHandler
    WorkflowStreamHandler workflowStreamHandler = wfState.getStreamHandler();

    // 2. 获取 ChatModel
    ChatModel chatModel = SpringUtil.getBean(AiModelProvider.class).getChatModel();

    // 3. 构建 Prompt
    UserMessage userMessage = new UserMessage(prompt);
    Prompt promptObj = new Prompt(List.of(userMessage));

    // 4. 流式调用 ChatModel
    final StringBuilder completeContentBuilder = new StringBuilder();

    chatModel.stream(promptObj)
            .doOnNext(chatResponse -> {
                String chunk = chatResponse.getResult().getOutput().getText();
                completeContentBuilder.append(chunk);

                // 发送 NODE_CHUNK 事件
                workflowStreamHandler.sendNodeChunk(node.getUuid(), chunk);
            })
            .doOnComplete(() -> {
                // 添加到节点输出
                String fullContent = completeContentBuilder.toString();
                NodeIOData output = NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "", fullContent);
                nodeState.getOutputs().add(output);
            })
            .doOnError(error -> {
                nodeState.setProcessStatus(4);
                nodeState.setProcessStatusRemark("LLM 流式调用失败: " + error.getMessage());
            })
            .blockLast(); // 阻塞等待流完成
}
```

---

## 风险评估

### 技术风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|---------|
| `blockLast()` 阻塞线程 | 低 | 低 | WorkflowEngine 本身就是同步执行，符合设计 |
| ChatModel 获取失败 | 低 | 中 | 增加空值检查和异常处理 |
| 流式响应超时 | 低 | 中 | Spring AI 自带超时机制 |

### 业务风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|---------|
| LLM 响应中断 | 低 | 中 | doOnError 捕获，设置节点失败状态 |
| 前端显示异常 | 极低 | 低 | 前端已有 NODE_CHUNK 处理逻辑 |

### 兼容性评估

✅ **零破坏性**
- AI问答：不受影响（独立调用链）
- 知识库问答：不受影响（直接用 ChatModel）
- 工作流非流式节点：不受影响（使用 `invokeLLM()`）
- 工作流其他节点：不受影响（不调用 LLM）

---

## 测试计划

### 测试场景

1. **基本流式输出**
   - 创建包含 LLM 节点的工作流
   - 输入简单问题
   - 验证前端实时显示流式响应

2. **长文本流式输出**
   - 输入复杂问题，生成长文本
   - 验证所有 NODE_CHUNK 事件都被接收

3. **错误处理**
   - 模拟 LLM 调用失败
   - 验证节点状态设置为失败
   - 验证错误信息正确记录

4. **多节点工作流**
   - 创建包含多个 LLM 节点的工作流
   - 验证每个节点的流式输出都正常

### 验证标准

✅ **成功标准**：
- 前端收到完整的事件序列：[START] → [NODE_RUN] → [NODE_INPUT] → [NODE_CHUNK...] → [NODE_OUTPUT] → [DONE]
- 后端日志不再出现 `onNextDropped`
- 前端实时显示流式响应
- 工作流运行记录正确保存

---

## 参考资料

- AI问答实现：`AiConversationController.java:169-277`
- 知识库RAG实现：`RagService.java:87-330`
- aideepin 参考实现：`langchain4j-aideepin/WorkflowEngine.java:234`
- Spring AI 流式响应文档：https://docs.spring.io/spring-ai/reference/api/chatclient.html#_streaming

---

## 实施记录

- **设计完成**: 2025-10-31
- **代码修改**: 待实施
- **测试验证**: 待完成
- **上线部署**: 待安排
