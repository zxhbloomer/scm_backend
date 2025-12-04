# MCP工具发现机制研究报告

## 一、核心问题回答

### Q: MCP工具如何通过提示词被AI找到并执行？

**简短答案**：AI通过**工具描述文本(description)**来理解和选择MCP工具。MCP协议通过`tools/list`接口将工具列表（包括name、description、inputSchema）暴露给AI，AI根据用户的自然语言请求匹配最合适的工具。

---

## 二、MCP工具发现的完整流程

### 2.1 工具注册阶段（服务端）

在您的代码中：
```java
@McpTool(description = """
    创建临时知识库并同步完成向量索引，供workflow后续节点立即使用。
    临时知识库2小时后自动清理。仅做向量索引，不做图谱索引。
    """)
public String createTempKnowledgeBase(
    @McpToolParam(description = "租户编码，用于数据权限控制") String tenantCode,
    @McpToolParam(description = "用户ID，用于审计") Long userId,
    @McpToolParam(description = "文本内容（可选）") String text,
    @McpToolParam(description = "文件URL数组（可选）") List<String> fileUrls) {
    // ...
}
```

**关键要素**：
1. **@McpTool注解**：标记这是一个MCP工具
2. **description属性**：**这是AI理解工具用途的核心**，必须清晰描述工具能做什么
3. **@McpToolParam注解**：描述每个参数的含义和用途

### 2.2 工具发现阶段（协议层面）

#### MCP协议的工具发现机制

根据Model Context Protocol规范，工具发现通过标准的JSON-RPC接口实现：

**请求格式**：
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "method": "tools/list",
  "params": {
    "cursor": "optional-cursor-value"
  }
}
```

**响应格式**：
```json
{
  "jsonrpc": "2.0",
  "id": 1,
  "result": {
    "tools": [
      {
        "name": "createTempKnowledgeBase",
        "description": "创建临时知识库并同步完成向量索引，供workflow后续节点立即使用。临时知识库2小时后自动清理。仅做向量索引，不做图谱索引。",
        "inputSchema": {
          "type": "object",
          "properties": {
            "tenantCode": {
              "type": "string",
              "description": "租户编码,用于数据权限控制"
            },
            "userId": {
              "type": "integer",
              "description": "用户ID,用于审计"
            },
            "text": {
              "type": "string",
              "description": "文本内容（可选）"
            },
            "fileUrls": {
              "type": "array",
              "items": {"type": "string"},
              "description": "文件URL数组（可选）"
            }
          },
          "required": ["tenantCode", "userId"]
        }
      }
    ],
    "nextCursor": null
  }
}
```

### 2.3 AI匹配阶段（LLM端）

当用户发送自然语言请求时，AI的匹配流程：

#### 示例1：直接匹配
```
用户: "为这份合同创建临时知识库"
AI思考过程:
1. 提取关键词："创建"、"临时知识库"
2. 遍历所有可用工具的description
3. 找到匹配工具：createTempKnowledgeBase
   - description包含："创建临时知识库"
4. 解析inputSchema，准备参数
5. 调用工具
```

#### 示例2：语义匹配
```
用户: "我想把这段文本做成一个2小时后自动删除的知识库"
AI思考过程:
1. 语义理解："临时"="2小时后自动删除"
2. 匹配description："临时知识库2小时后自动清理"
3. 识别参数需求：text参数
4. 调用工具
```

### 2.4 工具执行阶段

**调用请求**：
```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "method": "tools/call",
  "params": {
    "name": "createTempKnowledgeBase",
    "arguments": {
      "tenantCode": "tenant_001",
      "userId": 12345,
      "text": "这是一份重要合同的内容..."
    }
  }
}
```

**执行响应**：
```json
{
  "jsonrpc": "2.0",
  "id": 2,
  "result": {
    "content": [
      {
        "type": "text",
        "text": "{\"success\": true, \"kbUuid\": \"kb_123456\", \"message\": \"临时知识库创建成功\"}"
      }
    ],
    "isError": false
  }
}
```

---

## 三、工作流节点中的MCP工具执行

### 3.1 您的场景分析

查看您的代码和截图，您想在工作流节点中执行MCP工具：

```
工作流节点 "MCP工具2"
  ↓
需要调用 TempKnowledgeBaseMcpTools.createTempKnowledgeBase
  ↓
问题：如何在节点配置中指定要调用哪个MCP工具？
```

### 3.2 解决方案

#### 方案A：通过工具名称直接调用

在工作流节点配置中，您可以直接指定工具名称：

```java
// 节点配置示例
{
  "nodeType": "MCP_TOOL",
  "toolName": "createTempKnowledgeBase",  // 工具的name
  "parameters": {
    "tenantCode": "${workflow.tenantCode}",
    "userId": "${workflow.userId}",
    "text": "${input.contractContent}"
  }
}
```

#### 方案B：通过自然语言提示词（推荐）

更智能的方式是让AI根据提示词自动选择工具：

```java
// 节点配置示例
{
  "nodeType": "MCP_TOOL",
  "prompt": "为当前合同创建一个临时知识库，2小时后自动清理",
  "contextVariables": {
    "tenantCode": "${workflow.tenantCode}",
    "userId": "${workflow.userId}",
    "contractContent": "${input.contractContent}"
  }
}
```

AI会自动：
1. 解析prompt："创建临时知识库" → 匹配到 `createTempKnowledgeBase`
2. 从contextVariables提取参数
3. 调用工具

---

## 四、关键技术要点

### 4.1 Description是核心

**好的description示例**：
```java
@McpTool(description = """
    创建临时知识库并同步完成向量索引，供workflow后续节点立即使用。
    临时知识库2小时后自动清理。仅做向量索引，不做图谱索引。

    使用场景：
    - 合同审批workflow：在流程开始时创建临时知识库
    - 临时文档分析：快速索引临时文件进行问答
    - 会话级知识库：仅在当前对话中使用的知识
    """)
```

**关键点**：
1. **动词+对象**：明确说明工具"做什么"
2. **约束条件**：说明限制（如"2小时后自动清理"）
3. **使用场景**：提供具体的应用场景，帮助AI理解何时使用
4. **区分特性**：说明与其他类似工具的区别

### 4.2 参数描述同样重要

```java
@McpToolParam(description = "租户编码，用于数据权限控制") String tenantCode
```

AI会根据参数描述：
1. 理解参数用途
2. 从上下文中提取合适的值
3. 验证参数的合理性

### 4.3 Spring AI的实现方式

Spring AI通过`@McpTool`注解自动生成MCP协议所需的元数据：

```java
// Spring AI内部处理逻辑（简化版）
public Tool buildTool(Method method) {
    McpTool annotation = method.getAnnotation(McpTool.class);

    return Tool.builder()
        .name(annotation.name().isEmpty() ? method.getName() : annotation.name())
        .description(annotation.description())
        .inputSchema(buildInputSchema(method))
        .build();
}

private Schema buildInputSchema(Method method) {
    Schema schema = new Schema();
    schema.setType("object");

    for (Parameter param : method.getParameters()) {
        McpToolParam paramAnnotation = param.getAnnotation(McpToolParam.class);
        schema.addProperty(
            param.getName(),
            Property.builder()
                .type(inferType(param.getType()))
                .description(paramAnnotation.description())
                .build()
        );
    }

    return schema;
}
```

---

## 五、最佳实践

### 5.1 Description编写规范

**DO - 推荐做法**：
```java
@McpTool(description = """
    【核心功能】创建临时知识库并同步执行向量索引
    【时效性】2小时后自动清理
    【适用场景】
    - 合同审批流程：在审批开始时创建
    - 临时文档分析：快速索引后立即使用
    【技术特性】
    - 同步执行，确保后续节点可立即使用
    - 仅向量索引，不做图谱索引（性能优先）
    【返回内容】返回kbUuid供后续检索使用
    """)
```

**DON'T - 不推荐做法**：
```java
@McpTool(description = "创建知识库")  // ❌ 太简单，AI无法理解使用场景
```

### 5.2 工具命名规范

**规则**：
1. 使用动词开头：`create`、`update`、`delete`、`query`
2. 清晰的名词描述：`TempKnowledgeBase` 而不是 `KB`
3. 驼峰命名：`createTempKnowledgeBase`

**示例**：
- ✅ `createTempKnowledgeBase`
- ✅ `searchKnowledgeByKeyword`
- ❌ `tempKB` （不清晰）
- ❌ `doSomething` （太泛化）

### 5.3 参数设计原则

**明确必填/可选**：
```java
@McpTool(description = "...")
public String createTempKnowledgeBase(
    @McpToolParam(description = "租户编码（必填）") String tenantCode,
    @McpToolParam(description = "用户ID（必填）") Long userId,
    @McpToolParam(description = "文本内容（可选，与fileUrls二选一）") String text,
    @McpToolParam(description = "文件URL数组（可选，与text二选一）") List<String> fileUrls
)
```

**在description中说明参数关系**：
- 互斥参数："与X二选一"
- 依赖参数："当X为true时必填"
- 默认值："默认为X"

---

## 六、在您的Workflow中应用

### 6.1 当前代码的优化建议

您的代码已经很好，但可以进一步优化description：

```java
@McpTool(description = """
    【工具名称】创建临时知识库

    【核心功能】
    创建一个临时知识库并同步完成向量索引，确保后续workflow节点可立即使用该知识库进行RAG检索。

    【关键特性】
    1. 临时性：2小时后自动清理，无需手动删除
    2. 同步执行：索引完成后才返回，保证数据可用
    3. 轻量级：仅做向量索引，不做知识图谱（性能优先）

    【典型使用场景】
    - 合同审批workflow：在流程开始时创建临时知识库，存储合同内容
    - 文档问答：临时上传文档并快速建立检索能力
    - 会话级知识：仅在当前对话/流程中使用的知识内容

    【输入说明】
    - text和fileUrls至少提供一个
    - 可以同时提供text和fileUrls，系统会合并处理

    【返回结果】
    返回JSON格式，包含：
    - kbUuid: 知识库唯一标识，供后续检索使用
    - success: 创建是否成功
    - message: 操作结果说明

    【重要提示】
    此工具会等待向量索引完成，可能需要数秒到数分钟（取决于内容量）。
    如果是大文件，建议提示用户稍等或使用异步方式。
    """)
public String createTempKnowledgeBase(...) {
    // ...
}
```

### 6.2 在Workflow节点中配置

假设您的工作流引擎支持AI工具调用，配置示例：

```yaml
nodes:
  - id: "mcp_tool_node"
    type: "mcp_tool"
    config:
      # 方式1：直接指定工具名
      toolName: "createTempKnowledgeBase"
      parameters:
        tenantCode: "{{workflow.tenantCode}}"
        userId: "{{workflow.userId}}"
        text: "{{previousNode.output.contractContent}}"

      # 方式2：让AI根据prompt选择工具（推荐）
      prompt: |
        为当前审批的合同创建一个临时知识库，
        合同内容来自上一步的输出。
        请确保知识库创建成功后返回kbUuid。
      contextVariables:
        tenantCode: "{{workflow.tenantCode}}"
        userId: "{{workflow.userId}}"
        contractContent: "{{previousNode.output.contractContent}}"
```

### 6.3 工作流执行流程

```
1. 工作流引擎执行到"MCP工具"节点
   ↓
2. 根据节点配置调用MCP客户端
   ↓
3. MCP客户端发送tools/list请求，获取所有可用工具
   ↓
4. AI根据prompt或toolName匹配工具
   ↓
5. AI根据inputSchema和contextVariables准备参数
   ↓
6. MCP客户端发送tools/call请求
   ↓
7. 您的TempKnowledgeBaseMcpTools.createTempKnowledgeBase被执行
   ↓
8. 返回结果给工作流引擎
   ↓
9. 工作流继续执行下一个节点
```

---

## 七、实际示例

### 7.1 完整的工具调用示例

**用户在工作流中的输入**：
```
"我需要审批这份采购合同，请先建立知识库以便后续问答"
```

**AI的处理流程**：

1. **工具发现**（调用`tools/list`）：
```json
{
  "tools": [
    {
      "name": "createTempKnowledgeBase",
      "description": "创建临时知识库并同步完成向量索引..."
    },
    {
      "name": "searchKnowledge",
      "description": "在知识库中搜索相关信息..."
    }
  ]
}
```

2. **工具匹配**：
```
AI分析：
- 关键词："建立知识库" → 匹配 "创建临时知识库"
- 选择工具：createTempKnowledgeBase
```

3. **参数准备**：
```json
{
  "tenantCode": "tenant_001",  // 从workflow上下文获取
  "userId": 12345,              // 从workflow上下文获取
  "text": "这是一份采购合同...", // 从上一步节点获取
  "fileUrls": null
}
```

4. **工具调用**：
```json
{
  "method": "tools/call",
  "params": {
    "name": "createTempKnowledgeBase",
    "arguments": { /* 上面的参数 */ }
  }
}
```

5. **返回结果**：
```json
{
  "success": true,
  "kbUuid": "kb_temp_20251203_123456",
  "message": "临时知识库创建成功，已索引1个文本项",
  "itemCount": 1,
  "expireTime": "2025-12-03T16:30:00Z"
}
```

6. **工作流继续**：
```
下一个节点可以使用 kbUuid="kb_temp_20251203_123456" 进行知识检索
```

---

## 八、技术深度：MCP协议细节

### 8.1 工具声明能力

服务端需要在初始化时声明支持工具：

```json
{
  "capabilities": {
    "tools": {
      "listChanged": true  // 支持动态工具列表变更通知
    }
  }
}
```

### 8.2 工具列表变更通知

当工具列表发生变化时，服务端可以主动通知客户端：

```json
{
  "jsonrpc": "2.0",
  "method": "notifications/tools/list_changed"
}
```

客户端收到通知后会重新调用`tools/list`更新工具列表。

### 8.3 错误处理

**工具不存在**：
```json
{
  "jsonrpc": "2.0",
  "id": 3,
  "error": {
    "code": -32602,
    "message": "Unknown tool: invalidToolName"
  }
}
```

**工具执行错误**：
```json
{
  "jsonrpc": "2.0",
  "id": 4,
  "result": {
    "content": [
      {
        "type": "text",
        "text": "创建临时知识库失败: 租户编码无效"
      }
    ],
    "isError": true
  }
}
```

---

## 九、引用来源

### 官方文档
1. [Model Context Protocol Specification](https://modelcontextprotocol.io/specification/2025-11-25) - MCP官方规范
2. [Spring AI MCP Documentation](https://docs.spring.io/spring-ai-mcp/reference/spring-mcp.html) - Spring AI MCP集成文档
3. [Dynamic Tool Updates in Spring AI's Model Context Protocol](https://spring.io/blog/2025/05/04/spring-ai-dynamic-tool-updates-with-mcp/) - Spring官方博客

### 代码示例
4. [Spring AI Examples - MCP Annotations](https://github.com/spring-projects/spring-ai-examples/tree/main/model-context-protocol/mcp-annotations) - Spring官方示例
5. [Spring AI MCP Server Annotations](https://github.com/spring-projects/spring-ai/tree/main/auto-configurations/mcp) - Spring AI源码

### 社区资源
6. [Using Model Context Protocol (MCP) with Spring AI](https://piotrminkowski.com/2025/03/17/using-model-context-protocol-mcp-with-spring-ai/) - Piotr's TechBlog
7. [Building MCP Clients with Spring AI](https://www.danvega.dev/blog/2025/10/28/spring-ai-mcp-client) - Dan Vega博客

---

## 十、总结

### 核心要点

1. **Description是关键**：AI通过description理解工具用途，这是工具被发现的核心
2. **协议标准化**：MCP协议通过`tools/list`和`tools/call`标准化了工具发现和调用
3. **自动化映射**：Spring AI的`@McpTool`注解自动将Java方法转换为MCP工具定义
4. **语义匹配**：AI根据自然语言与description的语义相似度选择合适的工具

### 您的场景应用

在工作流节点中执行MCP工具：
1. **节点配置**：指定工具名称或使用自然语言prompt
2. **参数传递**：从workflow上下文传递参数
3. **结果处理**：将工具返回的JSON结果传递给下一个节点

### 下一步建议

1. **优化description**：按照最佳实践重写工具描述
2. **测试工具发现**：在实际workflow中测试AI是否能正确匹配工具
3. **添加更多场景**：在description中补充更多使用场景，提高匹配准确率
4. **监控日志**：记录工具调用情况，优化description和参数设计

---

**报告创建时间**：2025-12-03
**报告版本**：v1.0
**适用项目**：SCM Backend - AI Module - MCP Tools
