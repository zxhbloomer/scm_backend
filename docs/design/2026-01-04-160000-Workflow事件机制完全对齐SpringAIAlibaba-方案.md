# Workflow事件机制完全对齐Spring AI Alibaba方案

## 1. 问题定义

### 1.1 现象
前端WorkflowRuntimeList.vue执行工作流后，"工作流输出"显示为空（"无输出"）。

### 1.2 根因
`WorkflowEngine.handleGraphResponse()` 返回 `Flux.empty()`，不发送任何节点输出事件。

```java
// WorkflowEngine.java:410-416
return Mono.fromFuture(graphResponse.getOutput())
    .flatMapMany(nodeOutput -> {
        processNodeOutput(nodeOutput);
        // ❌ 问题根因：返回空Flux，不发送任何事件
        return Flux.<WorkflowEventVo>empty();
    })
```

### 1.3 用户需求
**完全对齐Spring AI Alibaba的事件机制**，删除SCM自定义的start/done/error事件类型。

## 2. Spring AI Alibaba事件机制源码分析

### 2.1 核心代码 - GraphProcess.processStream()

```java
public void processStream(Flux<NodeOutput> nodeOutputFlux, Sinks.Many<ServerSentEvent<String>> sink) {
    nodeOutputFlux
        .doOnNext(output -> {
            String nodeName = output.node();
            String content;

            // 通过instanceof区分流式块和完整输出
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

            // ★ 关键：无event名称，只有data
            sink.tryEmitNext(ServerSentEvent.builder(content).build());
        })
        .doOnComplete(() -> sink.tryEmitComplete())  // 完成信号
        .doOnError(e -> sink.tryEmitError(e))        // 错误信号
        .subscribe();
}
```

### 2.2 设计特点

| 特性 | Spring AI Alibaba | SCM当前 |
|------|-------------------|---------|
| **事件类型** | 无（纯data） | 7种event类型 |
| **流式块** | `{nodeUuid: chunk}` | `[NODE_CHUNK_xxx]` event |
| **节点输出** | `{node, data}` | 不发送 / `[NODE_OUTPUT_xxx]` |
| **完成信号** | Flux.complete() | `done` event |
| **错误信号** | Flux.error() | `error` event |
| **初始化** | 无 | `start` event |

### 2.3 核心洞察

1. **无事件类型名称**：所有SSE消息都没有event字段
2. **数据驱动区分**：通过数据结构（instanceof/字段）区分消息类型
3. **Flux信号机制**：完成/错误通过Flux的complete/error信号传递
4. **极简设计**：不传输元数据（如runtimeId），专注于节点输出

## 3. KISS原则评估

### 3.1 这是个真问题还是臆想出来的？
**✅ 真问题**
- 生产环境存在：用户执行工作流，看不到任何输出
- 用户影响：核心功能不可用

### 3.2 有更简单的方法吗？
**⚠️ 需要权衡**

| 方案 | 复杂度 | 破坏性 | 对齐程度 |
|------|--------|--------|----------|
| 保守对齐（只发NODE_OUTPUT） | 低 | 零 | 部分对齐 |
| 完全对齐（删除所有event） | 高 | 高 | 完全对齐 |

**用户选择**：完全对齐

### 3.3 会破坏什么吗？
**⚠️ 高破坏性**
- 前端 `workflowApi.js` 的回调模式需要重构
- 前端 `WorkflowRuntimeList.vue` 的事件处理需要重构
- 前端 `workflowService.js` 的回调模式需要重构
- 需要前后端同步发布

### 3.4 当前项目真的需要这个功能吗？
**✅ 必要**
- 解决核心痛点（工作流无输出显示）
- 与Spring AI Alibaba保持一致便于后续维护

### 3.5 完整性检查
- ✅ 已深度分析Spring AI Alibaba源码
- ✅ 已完整阅读SCM前后端代码
- ✅ 已识别所有受影响的文件

## 4. 方案设计

### 4.1 SCM特殊需求分析

SCM与Spring AI Alibaba的差异：

| 需求 | Spring AI Alibaba | SCM |
|------|-------------------|-----|
| RuntimeUuid传输 | 不在SSE流中 | 需要在SSE流中传输 |
| 前端追踪机制 | 外部状态管理 | 通过runtimeUuid定位本地列表 |

**问题**：如何在无event模式下传输runtimeUuid？

**解决方案**：首条消息约定
- 后端首条消息发送runtime数据（包含runtimeUuid字段）
- 前端通过检测runtimeUuid字段识别首条消息

### 4.2 数据结构设计

#### 4.2.1 SSE消息类型（通过数据结构区分）

**类型1：Runtime初始化消息（首条）**
```json
{
  "type": "runtime",
  "runtimeUuid": "xxx-xxx-xxx",
  "runtimeId": 123,
  "workflowUuid": "xxx-xxx-xxx",
  "conversationId": "xxx"
}
```

**类型2：流式块消息**
```json
{
  "type": "chunk",
  "node": "node-uuid",
  "chunk": "LLM输出的文本块"
}
```

**类型3：节点输出消息**
```json
{
  "type": "output",
  "node": "node-uuid",
  "data": {
    "output": {"value": "xxx", "type": 1},
    "var_files": {"value": [...], "type": 6}
  }
}
```

**类型4：人机交互消息**
```json
{
  "type": "interrupt",
  "node": "node-uuid",
  "tip": "请输入您的反馈"
}
```

### 4.3 后端改动

#### 4.3.1 WorkflowEventVo.java

**删除**：
- `createStartEvent()`
- `createDoneEvent()`
- `createErrorEvent()`
- `createNodeWaitFeedbackEvent()`
- `createNodeRunEvent()`

**新增**：
```java
/**
 * 工作流SSE数据VO
 * 对齐Spring AI Alibaba：无event名，纯data传输
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowEventVo {

    /**
     * SSE数据（JSON字符串）
     * 注意：不再使用event字段
     */
    private String data;

    /**
     * 创建Runtime初始化数据
     */
    public static WorkflowEventVo createRuntimeData(String runtimeUuid, Long runtimeId,
                                                     String workflowUuid, String conversationId) {
        JSONObject json = new JSONObject();
        json.put("type", "runtime");
        json.put("runtimeUuid", runtimeUuid);
        json.put("runtimeId", runtimeId);
        json.put("workflowUuid", workflowUuid);
        json.put("conversationId", conversationId);
        return WorkflowEventVo.builder().data(json.toJSONString()).build();
    }

    /**
     * 创建流式块数据
     */
    public static WorkflowEventVo createChunkData(String nodeUuid, String chunk) {
        JSONObject json = new JSONObject();
        json.put("type", "chunk");
        json.put("node", nodeUuid);
        json.put("chunk", chunk);
        return WorkflowEventVo.builder().data(json.toJSONString()).build();
    }

    /**
     * 创建节点输出数据
     */
    public static WorkflowEventVo createNodeOutputData(String nodeUuid, Map<String, Object> outputs) {
        JSONObject json = new JSONObject();
        json.put("type", "output");
        json.put("node", nodeUuid);
        json.put("data", outputs);
        return WorkflowEventVo.builder().data(json.toJSONString()).build();
    }

    /**
     * 创建人机交互数据
     */
    public static WorkflowEventVo createInterruptData(String nodeUuid, String tip) {
        JSONObject json = new JSONObject();
        json.put("type", "interrupt");
        json.put("node", nodeUuid);
        json.put("tip", tip);
        return WorkflowEventVo.builder().data(json.toJSONString()).build();
    }
}
```

#### 4.3.2 WorkflowEngine.java

**run() 方法改动**：
```java
public Flux<WorkflowEventVo> run(Long userId, List<JSONObject> userInputs, String tenantCode, String parentConversationId) {
    return Flux.defer(() -> {
        // ... 初始化逻辑 ...

        // 顶层工作流：发送runtime数据（首条消息）
        if (parentRuntimeUuid == null) {
            return Flux.just(WorkflowEventVo.createRuntimeData(
                    runtimeUuid, runtimeId, workflow.getWorkflowUuid(), conversationId))
                .concatWith(executeWorkflow(false));
        } else {
            // 子工作流：直接执行
            return executeWorkflow(false);
        }
    });
}
```

**handleGraphResponse() 方法改动**：
```java
private Flux<WorkflowEventVo> handleGraphResponse(GraphResponse<NodeOutput> graphResponse) {
    // isDone处理（人机交互）
    if (graphResponse.isDone()) {
        if (graphResponse.resultValue().isPresent()) {
            Object result = graphResponse.resultValue().get();
            if (result instanceof InterruptionMetadata) {
                String nodeUuid = findInterruptNodeUuid();
                String tip = getHumanFeedbackTip(nodeUuid);
                return Flux.just(WorkflowEventVo.createInterruptData(nodeUuid, tip));
            }
        }
        return Flux.empty();
    }

    // isError处理
    if (graphResponse.isError()) {
        // 通过Flux.error()传递错误，不发送error事件
        return Flux.error(new RuntimeException("节点执行失败"));
    }

    // 正常节点输出
    return Mono.fromFuture(graphResponse.getOutput())
        .flatMapMany(nodeOutput -> {
            processNodeOutput(nodeOutput);

            String nodeId = nodeOutput.node();
            AbstractWfNode abstractWfNode = wfState.getCompletedNodes().stream()
                .filter(item -> item.getNode().getUuid().equals(nodeId))
                .findFirst()
                .orElse(null);

            if (abstractWfNode != null) {
                Map<String, Object> outputs = abstractWfNode.getState().getOutputs();
                return Flux.just(WorkflowEventVo.createNodeOutputData(nodeId, outputs));
            }
            return Flux.empty();
        });
}
```

**删除 createWorkflowDoneEvent() 方法**：
工作流完成通过Flux.complete()信号传递，不再发送done事件。

#### 4.3.3 WorkflowController.java

```java
@PostMapping(value = "/run/{wfUuid}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
public Flux<ServerSentEvent<String>> run(@PathVariable String wfUuid,
                                         @RequestBody List<JSONObject> inputs,
                                         HttpServletRequest request) {
    String tenantCode = request.getHeader("X-Tenant-ID");
    DataSourceHelper.use(tenantCode);

    return workflowStarter.streaming(wfUuid, inputs, tenantCode, WorkflowCallSource.WORKFLOW_TEST, null)
            .map(event -> ServerSentEvent.<String>builder()
                    // ★ 关键：不设置event名称，只设置data
                    .data(event.getData())
                    .build())
            .doOnComplete(() -> log.info("工作流执行完成"))
            .doOnError(e -> log.error("工作流执行失败", e))
            .doFinally(signalType -> DataSourceHelper.close());
}
```

### 4.4 前端改动

#### 4.4.1 workflowApi.js

```javascript
/**
 * 运行工作流（SSE流式）- 对齐Spring AI Alibaba
 *
 * @param {Object} params - 运行参数
 * @param {string} params.uuid - 工作流UUID
 * @param {Array} params.inputs - 用户输入数组
 * @param {AbortSignal} params.signal - 中止信号
 * @param {Function} params.onMessage - 消息回调 (data: Object)
 * @param {Function} params.onComplete - 完成回调
 * @param {Function} params.onError - 错误回调
 */
export function workflowRun (params) {
  const { uuid, inputs, signal, onMessage, onComplete, onError } = params

  const url = `${import.meta.env.VITE_BASE_API}/api/v1/ai/workflow/run/${uuid}`

  fetchEventSource(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'X-Tenant-ID': store.getters.tenantId || ''
    },
    body: JSON.stringify({ inputs }),
    credentials: 'include',
    signal,
    openWhenHidden: true,

    async onopen (response) {
      if (response.ok) return
      if (response.status >= 400 && response.status < 500 && response.status !== 429) {
        const errorText = await response.text()
        if (onError) onError(errorText || `HTTP Error ${response.status}`)
        throw new Error('Fatal error')
      }
      throw new Error('Retriable error')
    },

    onmessage (msg) {
      // ★ 对齐Spring AI Alibaba：无event名，只有data
      if (msg.data) {
        try {
          const data = JSON.parse(msg.data)
          if (onMessage) onMessage(data)
        } catch (e) {
          console.error('解析SSE数据失败:', e)
        }
      }
    },

    onclose () {
      // ★ 对齐Spring AI Alibaba：Flux完成信号
      if (onComplete) onComplete()
    },

    onerror (err) {
      // ★ 对齐Spring AI Alibaba：Flux错误信号
      if (onError) onError(err.message || '工作流运行失败')
      throw err
    }
  })
}
```

#### 4.4.2 WorkflowRuntimeList.vue

```javascript
handleRunWorkflow (inputs) {
  this.running = true

  const inputList = inputs.map(item => ({
    name: item.name,
    content: item.content,
    required: item.required || false
  }))

  const controller = new AbortController()
  this.currentController = controller

  let accumulatedOutput = ''
  let currentRuntimeUuid = null
  let initialized = false

  workflowRun({
    uuid: this.workflow.workflowUuid,
    inputs: inputList,
    signal: controller.signal,

    // ★ 统一消息处理（对齐Spring AI Alibaba）
    onMessage: (data) => {
      const type = data.type

      // 类型1：Runtime初始化
      if (type === 'runtime') {
        initialized = true
        currentRuntimeUuid = data.runtimeUuid

        const runtime = {
          id: data.runtimeId,
          runtimeUuid: data.runtimeUuid,
          workflowUuid: data.workflowUuid,
          conversationId: data.conversationId,
          input: {},
          output: '',
          loading: true,
          status: 1 // 运行中
        }

        inputs.forEach(item => {
          runtime.input[item.name] = item.attachments || item.content
        })

        this.localRuntimeList.push(runtime)
        this.$message.success('工作流已开始执行')
        this.scrollToBottom()
        return
      }

      // 类型2：流式块
      if (type === 'chunk' && currentRuntimeUuid) {
        accumulatedOutput += data.chunk || ''
        this.updateRuntimeOutput(currentRuntimeUuid, accumulatedOutput)
        return
      }

      // 类型3：节点输出
      if (type === 'output' && currentRuntimeUuid) {
        const outputs = data.data || {}
        if (outputs.output && outputs.output.value) {
          const outputValue = outputs.output.value
          if (outputValue !== 'null' && outputValue !== '') {
            if (!accumulatedOutput || accumulatedOutput === 'null') {
              accumulatedOutput = outputValue
            }
            this.updateRuntimeOutput(currentRuntimeUuid, accumulatedOutput)
          }
        }
        return
      }

      // 类型4：人机交互
      if (type === 'interrupt' && currentRuntimeUuid) {
        if (this.$refs.runDetailRef) {
          this.$refs.runDetailRef.setHumanFeedback(currentRuntimeUuid, data.tip)
        }
        this.updateRuntimeStatus(currentRuntimeUuid, 2, false) // 等待输入
        return
      }
    },

    // ★ 完成处理（对齐Spring AI Alibaba Flux.complete()）
    onComplete: () => {
      this.running = false
      this.currentController = null

      if (this.$refs.runDetailRef) {
        this.$refs.runDetailRef.runDone()
      }

      if (currentRuntimeUuid) {
        this.updateRuntimeStatus(currentRuntimeUuid, 3, false, accumulatedOutput) // 成功
      }

      this.$message.success('工作流执行完成')
    },

    // ★ 错误处理（对齐Spring AI Alibaba Flux.error()）
    onError: (error) => {
      this.running = false
      this.currentController = null

      console.error('运行工作流失败:', error)
      this.$message.error(error || '工作流执行失败')

      if (this.$refs.runDetailRef) {
        this.$refs.runDetailRef.runError()
      }
    }
  })
},

// 辅助方法
updateRuntimeOutput (runtimeUuid, output) {
  const index = this.localRuntimeList.findIndex(r => r.runtimeUuid === runtimeUuid)
  if (index !== -1) {
    const oldRuntime = this.localRuntimeList[index]
    const newRuntime = { ...oldRuntime, output }
    this.localRuntimeList.splice(index, 1, newRuntime)
  }
},

updateRuntimeStatus (runtimeUuid, status, loading, output = null) {
  const index = this.localRuntimeList.findIndex(r => r.runtimeUuid === runtimeUuid)
  if (index !== -1) {
    const oldRuntime = this.localRuntimeList[index]
    const newRuntime = {
      ...oldRuntime,
      status,
      loading,
      output: output !== null ? output : oldRuntime.output
    }
    this.localRuntimeList.splice(index, 1, newRuntime)
  }
}
```

## 5. 调用链路对比

### 5.1 改动前
```
前端 workflowRun()
  ↓ SSE连接
后端 WorkflowController.run()
  ↓ event: "start", data: {runtime}
前端 startCallback()
  ↓
后端 节点执行...
  ↓ event: "[NODE_CHUNK_xxx]", data: chunk
前端 messageReceived()
  ↓
后端 handleGraphResponse()
  ↓ return Flux.empty() ❌ 不发送NODE_OUTPUT
后端 createWorkflowDoneEvent()
  ↓ event: "done", data: {content}
前端 doneCallback()
```

### 5.2 改动后（对齐Spring AI Alibaba）
```
前端 workflowRun()
  ↓ SSE连接
后端 WorkflowController.run()
  ↓ data: {type: "runtime", runtimeUuid, ...}  // 无event名
前端 onMessage() → 识别type=runtime → 初始化
  ↓
后端 节点执行...
  ↓ data: {type: "chunk", node, chunk}  // 无event名
前端 onMessage() → 识别type=chunk → 累积输出
  ↓
后端 handleGraphResponse()
  ↓ data: {type: "output", node, data}  // 发送NODE_OUTPUT
前端 onMessage() → 识别type=output → 更新输出
  ↓
后端 Flux.complete()  // 无done事件
前端 onClose() → 完成处理
```

## 6. 风险分析

### 6.1 技术风险

| 风险 | 概率 | 影响 | 缓解措施 |
|------|------|------|----------|
| 前后端不同步 | 中 | 高 | 同一PR提交，同步部署 |
| 数据结构识别错误 | 低 | 中 | type字段明确标识类型 |
| SSE连接异常关闭 | 低 | 中 | onclose统一处理完成逻辑 |

### 6.2 兼容性风险

| 风险 | 影响范围 | 缓解措施 |
|------|----------|----------|
| 旧版前端不兼容 | 全部 | 强制同步发布 |
| workflowService.js调用方 | WorkflowRuntimeList.vue | 同步修改 |

### 6.3 回滚方案
如出现问题，回滚前后端代码到修改前版本。

## 7. 测试验证

### 7.1 单元测试
- [ ] WorkflowEventVo.createRuntimeData() 测试
- [ ] WorkflowEventVo.createChunkData() 测试
- [ ] WorkflowEventVo.createNodeOutputData() 测试
- [ ] WorkflowEventVo.createInterruptData() 测试

### 7.2 集成测试
- [ ] WorkflowEngine.handleGraphResponse() 发送output事件
- [ ] WorkflowController SSE无event名转换
- [ ] Flux.complete()/error() 信号传递

### 7.3 端到端测试
- [ ] 执行简单工作流 → 前端显示输出
- [ ] 执行LLM节点 → 流式输出正常显示
- [ ] 执行多节点工作流 → 所有节点输出显示
- [ ] 人机交互节点 → 正常暂停等待输入
- [ ] 取消执行 → 正常中止

## 8. 实施步骤

### 8.1 后端实施
1. 修改 `WorkflowEventVo.java`：删除旧方法，新增数据创建方法
2. 修改 `WorkflowEngine.java`：
   - `run()` 发送runtime数据
   - `handleGraphResponse()` 发送node output数据
   - 删除 `createWorkflowDoneEvent()`
3. 修改 `WorkflowController.java`：SSE转换不设置event名

### 8.2 前端实施
1. 修改 `workflowApi.js`：删除回调模式，改用onMessage/onComplete/onError
2. 修改 `WorkflowRuntimeList.vue`：重构事件处理逻辑
3. 检查并修改 `workflowService.js`（如有调用方）

### 8.3 测试验证
1. 后端单元测试
2. 前后端联调
3. 端到端功能测试

### 8.4 部署
1. 后端部署
2. 前端部署（同步）
3. 验证生产环境

## 9. 总结

本方案完全对齐Spring AI Alibaba的事件机制：

| 维度 | 改动前 | 改动后 |
|------|--------|--------|
| event字段 | 7种类型 | 无（删除） |
| 消息区分 | event名 | data.type字段 |
| 完成信号 | done事件 | Flux.complete() |
| 错误信号 | error事件 | Flux.error() |
| 初始化 | start事件 | 首条runtime消息 |

**核心改动**：
1. 后端删除所有event类型，通过data.type传递消息类型
2. 前端删除回调模式，统一使用onMessage处理
3. 完成/错误通过Flux信号传递

**预期效果**：
- 完全对齐Spring AI Alibaba设计
- 解决工作流输出不显示问题
- 架构更简洁，维护更方便
