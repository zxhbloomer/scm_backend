# 工作流事件机制简化 - 对齐Spring AI Alibaba设计

## 背景

### 当前问题
SCM AI工作流使用了一套SSE事件机制（NODE_RUN, NODE_INPUT, NODE_OUTPUT, NODE_CHUNK），但经过深度分析发现：

1. **AI Chat前端不使用这些事件** - `aiChatService.js`使用Spring AI标准格式`chatResponse.results[0].output.content`
2. **事件仅用于workflow测试页** - `WorkflowRuntimeList.vue`使用这些事件显示执行过程
3. **workflow测试页是小功能** - 用户明确表示可以删除

### Spring AI Alibaba对比
- **节点Action**：只返回`Map<String, Object>`或`Flux`，不发送事件
- **NodeOutput**：纯数据结构（node, agent, tokenUsage, state），无事件机制
- **框架职责**：状态管理和流式输出由框架处理，节点只负责业务逻辑

## 核心判断

**值得做**：
1. 删除无用代码，减少复杂度
2. 对齐Spring AI Alibaba设计模式
3. 简化节点开发模型

## 详细方案

### 第一步：删除SSE事件发送代码

#### 1.1 WorkflowEngine.java - 删除回调中的事件发送

**当前代码** (行461-551)：
```java
NodeProcessResult processResult = abstractWfNode.process(
    // 输入回调
    (is) -> {
        // 更新数据库 - 保留
        if (callSource == WorkflowCallSource.AI_CHAT) {
            conversationRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
        } else {
            workflowRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
        }
        // SSE事件 - 删除
        for (NodeIOData input : nodeState.getInputs()) {
            streamHandler.sendNodeInput(wfNode.getUuid(), JSONObject.toJSONString(input));
        }
    },
    // 输出回调
    (is) -> {
        // 更新数据库 - 保留
        if (callSource == WorkflowCallSource.AI_CHAT) {
            conversationRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
        } else {
            workflowRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
        }
        // SSE事件 - 删除
        for (NodeIOData output : nodeState.getOutputs()) {
            streamHandler.sendNodeOutput(nodeUuid, JSONObject.toJSONString(output));
        }
    }
);
```

**优化后**：
```java
// 执行节点（无回调）
NodeProcessResult processResult = abstractWfNode.process();

// 更新输入到数据库
if (callSource == WorkflowCallSource.AI_CHAT) {
    conversationRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
} else {
    workflowRuntimeNodeService.updateInput(runtimeNodeId, nodeState);
}

// 更新输出到数据库
if (callSource == WorkflowCallSource.AI_CHAT) {
    conversationRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
} else {
    workflowRuntimeNodeService.updateOutput(runtimeNodeId, nodeState);
}
```

#### 1.2 删除NODE_RUN事件发送
**WorkflowEngine.java** (行462, 469)：
```java
// 删除这两行
streamHandler.sendNodeRun(wfNode.getUuid(), JSONObject.toJSONString(nodeVo));
```

#### 1.3 WorkflowUtil.java - 删除NODE_CHUNK事件发送

**当前代码** (行198-199, 247-249)：
```java
if (!silentMode && wfState.getStreamHandler() != null) {
    wfState.getStreamHandler().sendNodeChunk(node.getUuid(), content);
}
```

**删除** - LLM流式输出通过`Flux<ChatResponseVo>`返回，不需要单独的CHUNK事件。

### 第二步：简化process()方法签名

#### 2.1 AbstractWfNode.java

**当前签名**：
```java
public NodeProcessResult process(
    Consumer<WfNodeState> inputConsumer,
    Consumer<WfNodeState> outputConsumer
)
```

**简化为**：
```java
public NodeProcessResult process()
```

**完整修改**：
```java
/**
 * 执行节点处理
 * 简化版 - 对齐Spring AI Alibaba设计
 * 节点只负责业务逻辑，不发送事件
 */
public NodeProcessResult process() {
    state.setProcessStatus(NODE_PROCESS_STATUS_DOING);
    initInput();

    // HumanFeedback节点检查...
    if (this.requiresHumanFeedback()) {
        if (!state.data().containsKey(HUMAN_FEEDBACK_KEY)) {
            state.setProcessStatus(NODE_PROCESS_STATUS_WAITING);
            return new NodeProcessResult(true);
        }
    }

    // 执行节点逻辑
    NodeProcessResult processResult = onProcess();

    // 设置输出
    if (!processResult.isWaiting()) {
        setOutput();
        state.setProcessStatus(NODE_PROCESS_STATUS_DONE);
    }

    return processResult;
}
```

### 第三步：删除StreamHandler中的事件方法

#### 3.1 WorkflowStreamHandler.java

**删除以下方法**：
- `sendNodeRun(String nodeUuid, String data)`
- `sendNodeInput(String nodeUuid, String data)`
- `sendNodeOutput(String nodeUuid, String data)`
- `sendNodeChunk(String nodeUuid, String content)`

**保留**：
- `sendAnswer(String content)` - 用于LLM流式输出
- `sendError(String error)` - 用于错误处理
- `sendComplete()` - 用于完成信号

### 第四步：删除前端相关代码

#### 4.1 删除SSE事件常量
**sseEvents.js**：
```javascript
// 删除以下常量
export const NODE_RUN = 'node_run'
export const NODE_INPUT = 'node_input'
export const NODE_OUTPUT = 'node_output'
export const NODE_CHUNK = 'node_chunk'
```

#### 4.2 删除事件处理逻辑
**sseHandler.js** - 删除对应的case处理

#### 4.3 删除workflow测试页
**WorkflowRuntimeList.vue** - 此功能已不需要

## 文件变更清单

| 文件 | 变更类型 | 说明 |
|-----|---------|------|
| AbstractWfNode.java | 修改 | 删除process()回调参数 |
| WorkflowEngine.java | 修改 | 删除回调，直接调用数据库更新 |
| WorkflowUtil.java | 修改 | 删除NODE_CHUNK事件发送 |
| WorkflowStreamHandler.java | 修改 | 删除事件方法 |
| sseEvents.js | 修改 | 删除事件常量 |
| sseHandler.js | 修改 | 删除事件处理 |
| WorkflowRuntimeList.vue | 删除 | 删除测试页 |

## 影响分析

### 保留的功能
1. **数据库日志记录** - runtime节点的input/output更新完整保留
2. **LLM流式输出** - 通过Flux<ChatResponseVo>正常返回
3. **AI Chat功能** - 完全不受影响

### 删除的功能
1. **workflow测试页实时显示** - 小功能，用户确认可删除

## 实施顺序

1. **后端先行**
   - 修改AbstractWfNode.java
   - 修改WorkflowEngine.java
   - 修改WorkflowUtil.java
   - 修改WorkflowStreamHandler.java

2. **前端跟进**
   - 删除sseEvents.js相关常量
   - 删除sseHandler.js相关处理
   - 删除WorkflowRuntimeList.vue

## 验证要点

1. AI Chat聊天功能正常
2. 工作流执行正常
3. runtime节点日志正常记录
4. LLM流式输出正常
