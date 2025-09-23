# SCM-AI 流式输出使用指南

## 概述

SCM-AI 现在支持两种流式输出方式：
1. **SSE (Server-Sent Events)** - HTTP流式响应
2. **WebSocket** - 双向实时通信

## 使用方式

### 1. SSE流式聊天

**端点**: `POST /api/v1/ai/conversation/chat/stream`

**请求体**:
```json
{
  "prompt": "用户消息内容",
  "chatModelId": "模型ID",
  "conversationId": "对话ID",
  "organizationId": "组织ID"
}
```

**响应**: `text/event-stream`

**事件类型**:
- `start`: 开始处理
- `content`: 内容片段
- `complete`: 处理完成
- `error`: 发生错误

**JavaScript客户端示例**:
```javascript
const eventSource = new EventSource('/api/v1/ai/conversation/chat/stream', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    prompt: "你好，请介绍一下你自己",
    chatModelId: "default",
    conversationId: "conv-123",
    organizationId: "org-456"
  })
});

eventSource.addEventListener('start', (event) => {
  console.log('AI开始思考...');
});

eventSource.addEventListener('content', (event) => {
  console.log('收到内容片段:', event.data);
  // 实时更新UI显示内容
});

eventSource.addEventListener('complete', (event) => {
  console.log('AI回复完成:', event.data);
  eventSource.close();
});

eventSource.addEventListener('error', (event) => {
  console.error('发生错误:', event.data);
  eventSource.close();
});
```

### 2. WebSocket流式聊天

**连接端点**: `/ws/ai/stream`

**消息目的地**: `/app/ai/chat/stream`

**JavaScript客户端示例**:
```javascript
// 1. 建立WebSocket连接
const socket = new SockJS('/ws/ai/stream');
const stompClient = Stomp.over(socket);

stompClient.connect({}, (frame) => {
  console.log('WebSocket连接成功');

  // 2. 订阅流式响应
  stompClient.subscribe('/user/queue/ai-stream', (message) => {
    const data = JSON.parse(message.body);

    switch(data.type) {
      case 'start':
        console.log('AI开始思考...');
        break;
      case 'content':
        console.log('收到内容片段:', data.content);
        // 实时更新UI
        break;
      case 'complete':
        console.log('AI回复完成:', data.content);
        break;
      case 'error':
        console.error('发生错误:', data.content);
        break;
    }
  });

  // 3. 发送聊天消息
  stompClient.send('/app/ai/chat/stream', {}, JSON.stringify({
    prompt: "你好，请介绍一下你自己",
    chatModelId: "default",
    conversationId: "conv-123",
    organizationId: "org-456"
  }));
});
```

## 流式输出架构

### 核心组件

1. **AiStreamHandler**: 流式处理器接口
   - `DefaultStreamHandler`: 基础实现
   - `WebSocketStreamHandler`: WebSocket实现
   - `CallbackStreamHandler`: 回调实现

2. **AiChatBaseService**: 业务逻辑层
   - `chatWithMemoryStream()`: 流式聊天方法
   - 支持Spring AI的流式响应

3. **控制器端点**:
   - `AiConversationController.chatStream()`: SSE端点
   - `AiChatStreamController.handleStreamChat()`: WebSocket处理器

### 流式处理流程

```
用户请求 → 控制器 → 业务层 → Spring AI ChatClient
    ↓
流式处理器 ← AI模型响应 ← Spring AI Framework
    ↓
WebSocket/SSE → 前端实时更新
```

## 特性说明

### 已实现功能
- ✅ 完整的流式架构设计
- ✅ WebSocket双向通信
- ✅ SSE单向流式响应
- ✅ 消息记忆持久化
- ✅ 错误处理机制
- ✅ 多种流式处理器实现

### 技术优势
- **实时响应**: 内容逐步显示，提升用户体验
- **资源高效**: 避免长时间等待，降低超时风险
- **架构清晰**: 模块化设计，易于扩展维护
- **多种方式**: 支持WebSocket和SSE两种协议

## 配置说明

### WebSocket配置
- 端点: `/ws/ai/chat`, `/ws/ai/stream`
- 消息代理: `/topic`, `/queue`
- 用户前缀: `/user`
- 应用前缀: `/app`

### Spring AI配置
- 默认使用内存消息记忆
- 支持多种AI提供商
- 流式响应基于Reactor

## 注意事项

1. **会话管理**: WebSocket需要维护会话状态
2. **错误处理**: 确保异常情况下正确关闭连接
3. **资源清理**: 及时释放流式连接资源
4. **安全考虑**: 验证用户权限和输入内容
5. **性能监控**: 监控并发连接数和响应时间