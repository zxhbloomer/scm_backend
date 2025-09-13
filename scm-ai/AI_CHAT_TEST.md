# AI对话功能测试指南

## Step 1 完成内容

### 已实现功能
1. ✅ AI对话相关的Bean类（ChatMessage, ChatSession, ChatConfig）
2. ✅ ChatService接口和实现类
3. ✅ ChatController REST接口
4. ✅ 会话管理功能（SessionManager）
5. ✅ 流式响应支持（StreamingResponseHandler）
6. ✅ AI配置类（AiProperties）

### 核心类文件
```
scm-ai/src/main/java/com/xinyirun/scm/ai/
├── chat/
│   ├── model/
│   │   ├── ChatMessage.java       # 消息实体
│   │   ├── ChatSession.java       # 会话实体
│   │   └── ChatConfig.java        # 配置实体
│   ├── dto/
│   │   ├── ChatRequest.java       # 请求DTO
│   │   └── ChatResponse.java      # 响应DTO
│   ├── service/
│   │   ├── ChatService.java       # 服务接口
│   │   └── impl/
│   │       └── ChatServiceImpl.java # 服务实现
│   ├── controller/
│   │   └── ChatController.java    # REST控制器
│   ├── manager/
│   │   └── SessionManager.java    # 会话管理器
│   └── handler/
│       └── StreamingResponseHandler.java # 流式响应处理
└── config/
    └── AiProperties.java          # AI配置属性
```

## 测试接口列表

基础URL: `http://localhost:8088/scm/api/v1/ai`

### 1. 基础对话接口

#### 发送消息（同步）
```bash
POST /api/v1/ai/chat/send
Content-Type: application/json

{
  "message": "你好，请介绍一下自己",
  "model": "glm-4",
  "provider": "zhipuai",
  "stream": false
}
```

#### 发送消息（异步）
```bash
POST /api/v1/ai/chat/send-async
Content-Type: application/json

{
  "sessionId": "会话ID（可选）",
  "message": "能做什么功能？",
  "temperature": 0.7,
  "maxTokens": 2048
}
```

#### 流式对话
```bash
POST /api/v1/ai/chat/stream
Content-Type: application/json
Accept: text/event-stream

{
  "message": "请详细介绍供应链管理系统",
  "stream": true
}
```

### 2. 会话管理接口

#### 创建新会话
```bash
POST /api/v1/ai/chat/session/create
Content-Type: application/json

{
  "title": "供应链咨询",
  "model": "glm-4",
  "provider": "zhipuai"
}
```

#### 获取会话信息
```bash
GET /api/v1/ai/chat/session/{sessionId}
```

#### 获取会话消息历史
```bash
GET /api/v1/ai/chat/session/{sessionId}/messages
```

#### 获取用户会话列表
```bash
GET /api/v1/ai/chat/sessions?userId=1&tenantId=1
```

#### 删除会话
```bash
DELETE /api/v1/ai/chat/session/{sessionId}
```

#### 清空会话消息
```bash
POST /api/v1/ai/chat/session/{sessionId}/clear
```

### 3. 模型和提供商接口

#### 获取可用模型列表
```bash
GET /api/v1/ai/chat/models
```

响应示例：
```json
{
  "models": ["glm-4", "glm-3-turbo", "qwen-plus", "qwen-turbo", "ERNIE-Bot-4", "hunyuan-lite"],
  "default": "glm-4"
}
```

#### 获取可用提供商列表
```bash
GET /api/v1/ai/chat/providers
```

响应示例：
```json
{
  "providers": ["zhipuai", "dashscope", "baidu", "tencent"],
  "default": "zhipuai"
}
```

### 4. 健康检查接口（Step 0创建的）

```bash
GET /api/v1/ai/health
GET /api/v1/ai/info
GET /api/v1/ai/test
```

## 测试步骤

### 1. 基础功能测试

1. **启动系统**
   ```bash
   cd scm-start
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

2. **测试健康检查**
   ```bash
   curl http://localhost:8088/scm/api/v1/ai/health
   ```

3. **测试简单对话**
   ```bash
   curl -X POST http://localhost:8088/scm/api/v1/ai/chat/send \
     -H "Content-Type: application/json" \
     -d '{"message": "你好"}'
   ```

### 2. 会话功能测试

1. **创建会话**
2. **发送多条消息到同一会话**
3. **查看会话历史**
4. **清空会话消息**
5. **删除会话**

### 3. 流式响应测试

使用支持SSE的工具（如Postman或浏览器EventSource）测试流式接口。

## 预期响应

### 成功响应示例
```json
{
  "code": 200,
  "message": "success",
  "sessionId": "uuid-string",
  "messageId": "uuid-string",
  "content": "您好！我是SCM AI助手，很高兴为您服务。",
  "model": "glm-4",
  "provider": "zhipuai",
  "timestamp": "2025-01-12T10:00:00",
  "finished": true,
  "tokenUsage": {
    "inputTokens": 10,
    "outputTokens": 20,
    "totalTokens": 30
  }
}
```

### 错误响应示例
```json
{
  "code": 500,
  "message": "AI服务未启用",
  "timestamp": "2025-01-12T10:00:00",
  "finished": true
}
```

## 注意事项

1. 当前实现为**模拟响应**，还未真正集成AI提供商API
2. 会话数据暂时存储在内存中，重启后会丢失
3. AI提供商默认都设置为`enabled: false`，需要时再开启
4. 流式响应目前是模拟实现，将完整响应分块输出

## 下一步计划（Step 2）

- 集成真实的AI提供商（智谱AI、通义千问等）
- 实现真正的流式API调用
- 添加数据库持久化存储
- 实现多租户隔离
- 添加token计费统计