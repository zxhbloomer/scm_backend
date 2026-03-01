# Workflow流式事件对齐Spring AI Alibaba优化方案

## 1. 问题诊断

### 1.1 现象
前端WorkflowRuntimeList.vue执行工作流后，"工作流输出"显示为空（"无输出"）。

### 1.2 根因分析
通过深度调研Spring AI Alibaba源码和SCM代码，定位根本原因：

**后端问题**：
```java
// WorkflowEngine.java:414-416
private Flux<WorkflowEventVo> handleGraphResponse(GraphResponse<NodeOutput> graphResponse) {
    // ...
    return Mono.fromFuture(graphResponse.getOutput())
        .flatMapMany(nodeOutput -> {
            processNodeOutput(nodeOutput);
            // ❌ 问题：明确不发送节点输出事件
            return Flux.<WorkflowEventVo>empty();
        });
}
```

**前端问题**：
```javascript
// WorkflowRuntimeList.vue依赖NODE_OUTPUT事件
messageReceived (chunk, eventName) {
    if (eventName.startsWith('[NODE_OUTPUT_')) {
        // 等待NODE_OUTPUT事件
    }
}
```

**结果**：后端不发送，前端收不到，工作流输出为空。

### 1.3 调用链路完整追踪

```
用户点击执行
  ↓
前端 workflowApi.workflowRun()
  ↓ POST /api/v1/ai/workflow/run/{uuid}
后端 WorkflowController.run()
  ↓ Flux<ServerSentEvent<String>>
WorkflowStarter.streaming()
  ↓ Flux.defer()
WorkflowEngine.run()
  ↓ Flux.just(start) + concatWith(executeWorkflow())
WorkflowEngine.executeWorkflow()
  ↓ app.graphResponseStream()
  ↓ flatMap(handleGraphResponse)
WorkflowEngine.handleGraphResponse()
  ↓ processNodeOutput() 更新数据库
  ↓ return Flux.empty() ❌ 不发送事件
  ↓
前端 onmessage(msg)
  ↓ start事件 → startCallback()
  ↓ done事件 → doneCallback()
  ↓ ❌ 缺失NODE_OUTPUT事件
WorkflowRuntimeList.vue
  ↓ accumulatedOutput = "" 空字符串
  ↓ 显示"无输出"
```

## 2. Spring AI Alibaba事件机制深度调研

### 2.1 核心数据结构

```java
// NodeOutput - 基础节点输出
public class NodeOutput {
    protected final String node;           // 节点ID
    protected final OverAllState state;    // 完整状态
    protected String agent;                // 代理名称
    protected Usage tokenUsage;            // token使用量
}

// StreamingOutput - 流式输出（继承NodeOutput）
public class StreamingOutput<T> extends NodeOutput {
    private final String chunk;            // 流式文本块
    private final Message message;         // 完整消息
    private final T originData;            // 原始数据
    private OutputType outputType;         // 输出类型
}
```

### 2.2 SSE事件格式

**Spring AI Alibaba的GraphProcess.processStream()实现**：

```java
public void processStream(Flux<NodeOutput> nodeOutputFlux, Sinks.Many<ServerSentEvent<String>> sink) {
    nodeOutputFlux
        .doOnNext(output -> {
            String nodeName = output.node();
            String content;

            // 关键：通过instanceof区分流式块和完整输出
            if (output instanceof StreamingOutput streamingOutput) {
                // 流式块：{nodeName: chunk}
                content = JSON.toJSONString(Map.of(nodeName, streamingOutput.chunk()));
            } else {
                // 完整节点输出：{node, data}
                JSONObject nodeOutput = new JSONObject();
                nodeOutput.put("data", output.state().data());
                nodeOutput.put("node", nodeName);
                content = JSON.toJSONString(nodeOutput);
            }

            // ✅ 无event名，所有输出都是默认事件
            sink.tryEmitNext(ServerSentEvent.builder(content).build());
        })
        .doOnComplete(() -> sink.tryEmitComplete())
        .doOnError(e -> sink.tryEmitError(e))
        .subscribe();
}
```

### 2.3 设计特点对比

| 特性 | Spring AI Alibaba | SCM当前 | 优化后SCM |
|------|-------------------|---------|----------|
| **事件类型** | 无类型 | 7种类型 | 简化为2种 |
| **节点输出** | `{node, data}` | 不发送 | 发送 |
| **流式块** | `{node, chunk}` | NODE_CHUNK | 保持 |
| **完成信号** | Flux结束 | done事件 | 保持done事件 |
| **错误信号** | Flux错误 | error事件 | 保持error事件 |
| **区分方式** | instanceof | event名 | event名 |

**关键洞察**：
1. Spring AI Alibaba极简设计：无事件类型，通过数据结构区分
2. SCM需要保留event名：因为前端已有逻辑依赖event
3. 核心改进：发送NODE_OUTPUT事件，包含完整节点输出

## 3. KISS原则7问题评估

### 3.1 这是个真问题还是臆想出来的？
**✅ 真问题**
- 生产环境存在：用户执行工作流，看不到任何输出
- 用户影响：无法查看工作流执行结果
- 严重性：高（核心功能不可用）

### 3.2 有更简单的方法吗？
**✅ 已是最简方案**
- 方案1：修改前端解析done事件 → ❌ 不符合Spring AI Alibaba设计
- 方案2：发送NODE_OUTPUT事件 → ✅ 对齐Spring AI Alibaba，最小改动

### 3.3 会破坏什么吗？
**✅ 零破坏性**
- 新增事件发送，不修改已有事件
- 前端已有NODE_OUTPUT处理逻辑，无需改动
- 向后兼容：done事件保留

### 3.4 当前项目真的需要这个功能吗？
**✅ 核心必要功能**
- 工作流输出是核心业务功能
- 用户必须看到执行结果

### 3.5 这个问题过度设计了吗？有缺少必要信息吗？能否继续评估？
**✅ 设计合理，信息完整**
- 深度调研Spring AI Alibaba源码
- 完整追踪SCM调用链路
- 数据支撑充分

### 3.6 话题是否模糊，是否会导致幻觉的产生
**✅ 清晰明确**
- 问题现象清楚：前端显示"无输出"
- 根因定位准确：handleGraphResponse返回Flux.empty()
- 解决方案明确：发送NODE_OUTPUT事件

### 3.7 是否已经学习了关于代码实施的注意事项的内容？
**✅ 已学习并应用**
- 遵循SCM项目规范
- 不创建重复实现
- 数据库操作规范（bean插入/更新，sql查询）
- 日志规范（无icon、无"修复"等词汇）

## 4. 优化方案设计

### 4.1 数据结构设计

**WorkflowEventVo增强**（已存在，无需修改）：
```java
// 已有事件创建方法
public static WorkflowEventVo createStartEvent(String data)
public static WorkflowEventVo createNodeWaitFeedbackEvent(String nodeUuid, String tip)
public static WorkflowEventVo createDoneEvent(String data)
public static WorkflowEventVo createErrorEvent(String message)

// 需要新增：节点输出事件（对齐Spring AI Alibaba）
public static WorkflowEventVo createNodeOutputEvent(String nodeUuid, Map<String, Object> outputs)
```

### 4.2 后端修改方案

#### 4.2.1 WorkflowEventVo.java
**文件**：`scm-ai/src/main/java/com/xinyirun/scm/ai/bean/vo/workflow/WorkflowEventVo.java`

**修改内容**：新增节点输出事件创建方法

```java
/**
 * 创建节点输出事件（对齐Spring AI Alibaba）
 * 前端回调: messageReceived(data, "[NODE_OUTPUT_nodeUuid]")
 *
 * @param nodeUuid 节点UUID
 * @param outputs 节点输出数据
 * @return 节点输出事件
 */
public static WorkflowEventVo createNodeOutputEvent(String nodeUuid, Map<String, Object> outputs) {
    JSONObject eventData = new JSONObject();
    eventData.put("node", nodeUuid);
    eventData.put("data", outputs);

    return WorkflowEventVo.builder()
        .event("[NODE_OUTPUT_" + nodeUuid + "]")
        .data(eventData.toJSONString())
        .build();
}
```

#### 4.2.2 WorkflowEngine.java
**文件**：`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

**修改位置**：`handleGraphResponse()` 方法（第383-422行）

**修改前**：
```java
private Flux<WorkflowEventVo> handleGraphResponse(GraphResponse<NodeOutput> graphResponse) {
    // ...省略isDone和isError处理...

    // 正常节点输出
    return Mono.fromFuture(graphResponse.getOutput())
        .flatMapMany(nodeOutput -> {
            processNodeOutput(nodeOutput);
            // ❌ 当前：不发送事件
            return Flux.<WorkflowEventVo>empty();
        })
        .onErrorResume(e -> {
            log.error("处理节点输出失败", e);
            return Flux.just(WorkflowEventVo.createErrorEvent("处理节点输出失败: " + e.getMessage()));
        });
}
```

**修改后**：
```java
private Flux<WorkflowEventVo> handleGraphResponse(GraphResponse<NodeOutput> graphResponse) {
    // ...isDone和isError处理保持不变...

    // 正常节点输出（对齐Spring AI Alibaba）
    return Mono.fromFuture(graphResponse.getOutput())
        .flatMapMany(nodeOutput -> {
            processNodeOutput(nodeOutput);

            // 获取节点输出数据
            String nodeId = nodeOutput.node();
            AbstractWfNode abstractWfNode = wfState.getCompletedNodes().stream()
                .filter(item -> item.getNode().getUuid().equals(nodeId))
                .findFirst()
                .orElse(null);

            if (abstractWfNode != null) {
                Map<String, Object> outputs = abstractWfNode.getState().getOutputs();
                log.debug("发送NODE_OUTPUT事件: nodeId={}, outputs数量={}", nodeId, outputs.size());
                return Flux.just(WorkflowEventVo.createNodeOutputEvent(nodeId, outputs));
            }

            return Flux.<WorkflowEventVo>empty();
        })
        .onErrorResume(e -> {
            log.error("处理节点输出失败", e);
            return Flux.just(WorkflowEventVo.createErrorEvent("处理节点输出失败: " + e.getMessage()));
        });
}
```

**关键改动说明**：
1. 从`processNodeOutput()`的逻辑中提取节点输出数据
2. 调用新增的`createNodeOutputEvent()`发送事件
3. 日志记录发送行为（不使用icon，描述清晰）

### 4.3 前端修改方案

#### 4.3.1 workflowApi.js
**文件**：`01_scm_frontend/scm_frontend/src/components/70_ai/api/workflowApi.js`

**当前状态**：已正确处理done和error事件（在之前修复中完成）

**验证代码**：
```javascript
onmessage (msg) {
  console.log('[workflowRun] SSE收到消息:', msg.event, msg.data?.substring(0, 100))

  // 处理 start 事件
  if (msg.event === 'start') {
    if (startCallback) startCallback(msg.data)
    return
  }

  // 处理 done 事件 ✅ 已修复
  if (msg.event === 'done') {
    if (doneCallback) doneCallback(msg.data)
    return
  }

  // 处理 error 事件 ✅ 已修复
  if (msg.event === 'error') {
    if (errorCallback) errorCallback(msg.data)
    return
  }

  // 处理其他事件（包括NODE_OUTPUT）
  if (msg.event) {
    if (messageReceived) messageReceived(msg.data || '', msg.event)
  }
}
```

**结论**：无需修改，已支持NODE_OUTPUT事件

#### 4.3.2 WorkflowRuntimeList.vue
**文件**：`01_scm_frontend/scm_frontend/src/components/70_ai/components/workflow/WorkflowRuntimeList.vue`

**当前状态**：已有NODE_OUTPUT事件处理逻辑

**验证代码**：
```javascript
messageReceived (chunk, eventName) {
  console.log('[WorkflowRuntimeList] 收到事件:', eventName, chunk.substring(0, 100))

  if (eventName.startsWith('[NODE_OUTPUT_')) {
    // 解析节点输出
    try {
      const outputData = JSON.parse(chunk)
      const nodeOutputs = outputData.data || {}

      // 累积所有节点的输出文本
      Object.values(nodeOutputs).forEach(output => {
        if (output && output.value) {
          this.accumulatedOutput += output.value + '\n\n'
        }
      })
    } catch (e) {
      console.error('解析NODE_OUTPUT失败:', e)
    }
  }
}
```

**结论**：无需修改，已支持解析NODE_OUTPUT事件

### 4.4 事件流程时序图

```
用户执行工作流
    ↓
前端 workflowApi.workflowRun()
    ↓ SSE连接建立
后端 WorkflowController.run()
    ↓ 返回 Flux<ServerSentEvent<String>>
    |
    ├─→ start事件
    |   └─→ 前端 startCallback()
    |
    ├─→ 节点1执行
    |   └─→ handleGraphResponse()
    |       └─→ NODE_OUTPUT事件 ✅ 新增
    |           └─→ 前端 messageReceived()
    |               └─→ 累积输出
    |
    ├─→ 节点2执行
    |   └─→ handleGraphResponse()
    |       └─→ NODE_OUTPUT事件 ✅ 新增
    |           └─→ 前端 messageReceived()
    |               └─→ 累积输出
    |
    └─→ done事件
        └─→ 前端 doneCallback()
            └─→ 显示最终输出
```

## 5. 风险分析与缓解措施

### 5.1 技术风险

**风险1：事件发送频率过高导致性能问题**
- **概率**：低
- **影响**：中
- **缓解**：
  - NODE_OUTPUT仅在节点完成时发送（非流式块）
  - Spring AI Alibaba已验证此模式的性能
  - 工作流节点数量通常不超过10个

**风险2：大数据量节点输出导致SSE消息过大**
- **概率**：低
- **影响**：中
- **缓解**：
  - 节点输出通常为文本摘要，数据量可控
  - SSE协议支持大消息
  - 可在后续迭代中增加输出截断逻辑

### 5.2 业务风险

**风险3：现有工作流执行受影响**
- **概率**：极低
- **影响**：高
- **缓解**：
  - 新增事件，不修改现有事件
  - 前端已有NODE_OUTPUT处理逻辑
  - 向后完全兼容

### 5.3 兼容性风险

**风险4：旧版前端无法处理新事件**
- **概率**：无
- **影响**：无
- **缓解**：
  - 前端已部署NODE_OUTPUT处理逻辑
  - 只是之前后端未发送此事件

## 6. 测试验证计划

### 6.1 单元测试
- [ ] WorkflowEventVo.createNodeOutputEvent()方法测试
- [ ] 验证事件格式正确性
- [ ] 验证JSON序列化

### 6.2 集成测试
- [ ] WorkflowEngine.handleGraphResponse()发送事件测试
- [ ] 验证节点输出数据提取正确
- [ ] 验证Flux事件流完整性

### 6.3 端到端测试
- [ ] 执行简单工作流（Start → LLM → End）
- [ ] 验证前端收到NODE_OUTPUT事件
- [ ] 验证"工作流输出"显示正确内容
- [ ] 执行复杂工作流（多节点、分支）
- [ ] 验证所有节点输出都被收集

### 6.4 性能测试
- [ ] 10节点工作流执行时间测试
- [ ] SSE消息传输延迟测试
- [ ] 浏览器EventStream处理性能测试

## 7. 实施步骤

### 7.1 后端实施
1. 修改`WorkflowEventVo.java`：新增`createNodeOutputEvent()`
2. 修改`WorkflowEngine.java`：`handleGraphResponse()`发送事件
3. 编译验证无错误

### 7.2 前端实施
1. 验证`workflowApi.js`已支持done/error事件
2. 验证`WorkflowRuntimeList.vue`已支持NODE_OUTPUT事件
3. 无需修改（已完成）

### 7.3 测试验证
1. 后端单元测试
2. 端到端功能测试
3. 性能回归测试

### 7.4 部署上线
1. 后端部署
2. 前端部署（如有修改）
3. 灰度验证
4. 全量发布

## 8. 数据支撑

### 8.1 Spring AI Alibaba源码分析
- **GraphResponse.java**：事件数据结构定义
- **NodeOutput.java**：节点输出基类
- **StreamingOutput.java**：流式输出扩展类
- **GraphProcess.processStream()**：SSE事件处理标准实现

### 8.2 SCM调用链路追踪
- 完整追踪从前端到后端的事件流
- 定位`handleGraphResponse()`返回`Flux.empty()`的根因
- 验证前端已有NODE_OUTPUT处理逻辑

### 8.3 浏览器EventStream日志
```
start
  data: {...}

done
  data: {"content":"{...}","runtime_id":123,...}
```
**结论**：确认后端未发送NODE_OUTPUT事件

## 9. 方案优势

### 9.1 技术优势
- **完全对齐Spring AI Alibaba**：采用官方设计模式
- **最小改动**：仅新增事件发送，不修改现有逻辑
- **零破坏性**：向后完全兼容
- **性能无忧**：Spring AI Alibaba已验证的模式

### 9.2 业务优势
- **用户可见输出**：解决核心痛点
- **实时反馈**：每个节点执行后立即显示输出
- **调试友好**：可查看每个节点的输出数据

### 9.3 维护优势
- **代码简洁**：符合KISS原则
- **易于理解**：标准事件驱动模式
- **扩展性好**：未来可轻松增加更多事件类型

## 10. 总结

本方案通过深度调研Spring AI Alibaba源码，发现其极简事件模型的核心设计思想，并针对SCM项目的实际情况，制定了最小改动、零破坏性的优化方案。

**核心改动**：
1. 后端：`WorkflowEngine.handleGraphResponse()`发送NODE_OUTPUT事件
2. 前端：无需修改（已有处理逻辑）

**预期效果**：
- 用户执行工作流后，可在"工作流输出"看到完整执行结果
- 完全对齐Spring AI Alibaba设计模式
- 零破坏性，向后兼容

**方案价值**：
- 解决核心功能缺失问题
- 提升用户体验
- 技术架构对齐业界标准
