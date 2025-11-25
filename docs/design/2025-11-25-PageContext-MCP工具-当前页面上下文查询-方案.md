# PageContext MCP工具 - 当前页面上下文查询 - 开发设计方案

## 一、需求概述

### 1.1 需求背景
在AI Chat对话场景中，用户询问"我现在打开的页面是什么？"时，LLM无法正确回答，因为：
- LLM没有前端页面上下文信息
- 现有MCP工具只能查询静态权限数据，无法获取用户当前所在页面

### 1.2 核心需求
让AI Chat能正确回答"我现在打开的页面是什么？"类似问题：
- **前端**：将当前页面上下文（page_code, title, path）添加到聊天请求中
- **后端**：创建MCP工具，从ToolContext中读取页面上下文并返回给LLM
- **范围限定**：第一阶段仅处理路由页面，不处理弹窗/对话框上下文

### 1.3 业务价值
- 提升AI Chat上下文感知能力：LLM知道用户当前操作环境
- 支持更智能的对话：可基于当前页面提供更精准的帮助
- 为后续功能铺垫：如页面操作引导、上下文相关推荐等

---

## 二、KISS原则7问题回答

### 2.1 这是个真问题还是臆想出来的？
✅ **真问题**
- 实际场景：用户在出库计划页面问"我现在打开的页面是什么？"，LLM无法回答
- 用户明确提出此需求
- 这是AI Chat上下文感知的基础能力

### 2.2 有更简单的方法吗？
✅ **当前方案已是最简方案**
- 复用现有ToolContext机制（已有tenantCode、staffId传递先例）
- 前端仅需在发送消息时附加3个字段（page_code, title, path）
- 后端仅需新增1个MCP工具方法，无需新建类
- 无需数据库查询，纯内存读取

**备选方案对比**：
| 方案 | 复杂度 | 优缺点 |
|-----|-------|-------|
| ✅ MCP工具+ToolContext | 低 | 复用现有机制，改动最小 |
| ❌ System Prompt注入 | 中 | 每次对话都要注入，token浪费 |
| ❌ 前端WebSocket实时推送 | 高 | 架构改动大，过度设计 |

### 2.3 会破坏什么吗？
✅ **零破坏性**
- 新增功能，不修改现有逻辑
- ToolContext扩展字段是可选的，不影响现有MCP工具
- 前端请求body新增可选字段，后端向后兼容
- 现有聊天功能完全不受影响

### 2.4 当前项目真的需要这个功能吗？
✅ **确实需要**
- 用户明确提出需求
- AI Chat上下文感知是基础能力，后续功能依赖此基础
- 现有MCP工具矩阵缺少"当前上下文"查询能力

### 2.5 这个问题过度设计了吗？有缺少必要信息吗？
✅ **设计恰当，信息充足**
- **不过度**：仅改动前端1个文件、后端1个文件
- **范围明确**：第一阶段仅处理路由页面，弹窗上下文延后
- **信息充足**：
  - 已确认ToolContext传递机制（McpToolConfig.java）
  - 已确认前端API调用点（aiChatService.js）
  - 已确认Vue Router meta结构（page_code, title, path）

### 2.6 话题是否模糊，是否会导致幻觉的产生？
✅ **话题清晰，无幻觉风险**
- 需求明确：从前端传递页面上下文，后端MCP工具读取并返回
- 数据来源真实：Vue Router的$route对象
- 返回结构简单：page_code, title, path三个字段

### 2.7 是否已经学习了关于代码实施的注意事项的内容？
✅ **已完整学习**
- 已阅读 `McpToolConfig.java`：理解ToolContext参数注入机制
- 已阅读 `aiChatService.js`：确认前端请求body结构
- 已阅读 `chat.js`：确认Vuex store可访问$route信息
- 已阅读 `PermissionMcpTools.java`：掌握MCP工具开发模式

---

## 三、完整调用链路分析

### 3.1 业务流程时序图

```
Vue Frontend (ChatPanel.vue)
     |
     | 1. 用户发送消息: "我现在打开的页面是什么？"
     v
Vuex Store (chat.js - sendMessage action)
     |
     | 2. 获取当前路由信息: this.$route.meta, this.$route.path
     v
aiChatService.js - sendMessageStream()
     |
     | 3. 构建请求body，新增 pageContext 字段:
     |    {
     |      conversationId,
     |      prompt,
     |      chatModelId,
     |      pageContext: {         // 【新增】
     |        page_code: "P00000013",
     |        title: "出库计划",
     |        path: "/out/plan"
     |      }
     |    }
     v
HTTP POST → Backend API
     |
     | 4. Controller接收请求
     v
AiConversationController.java
     |
     | 5. 提取pageContext，放入ToolContext
     v
ToolContext.getContext().put("pageContext", pageContext)
     |
     | 6. LLM调用MCP工具
     v
PageContextMcpTools.getCurrentPageInfo(ToolContext toolContext)
     |
     | 7. 从ToolContext读取pageContext
     v
Map<String, Object> pageContext = toolContext.getContext().get("pageContext")
     |
     | 8. 返回JSON结果
     v
{
  "success": true,
  "pageContext": {
    "page_code": "P00000013",
    "title": "出库计划",
    "path": "/out/plan"
  },
  "message": "用户当前在出库计划页面",
  "_aiHint": "用户正在查看的页面信息已提供,请根据page_code、title和path告知用户当前所在页面"
}
     |
     | 9. LLM生成回复
     v
AI Chat Response:
"您当前正在访问「出库计划」页面 (路径: /out/plan)"
```

### 3.2 关键技术点说明

**1. 前端pageContext获取**
- 数据来源：`this.$route.meta` 和 `this.$route.path`
- Vue Router meta包含：page_code, title, icon, roles等
- path直接从route对象获取

**2. ToolContext传递机制**
- 现有机制已支持tenantCode、staffId传递（McpToolConfig.java第130-145行）
- pageContext作为新增key加入toolContext.getContext()
- MCP工具方法无需显式声明pageContext参数，直接从ToolContext读取

**3. MCP工具调用**
- LLM根据用户问题自动选择调用`get_current_page_info`工具
- 工具返回JSON，LLM解析后生成自然语言回复

---

## 四、方案设计

### 4.1 文件级别修改清单

#### 4.1.1 后端文件修改

**新增文件**:

1. **`PageContextMcpTools.java`**
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000044/tools/PageContextMcpTools.java`
   - 职责: 提供获取当前页面上下文的MCP工具
   - 预计代码行: 约60行

**修改文件**:

2. **`AiConversationController.java`** (可能需要)
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/controller/chat/AiConversationController.java`
   - 修改内容: 从请求body中提取pageContext，放入ToolContext
   - 改动范围: 消息发送方法

3. **`McpToolConfig.java`** (可能需要)
   - 路径: `scm-ai/src/main/java/com/xinyirun/scm/ai/config/mcp/McpToolConfig.java`
   - 修改内容: 支持从ToolContext读取pageContext
   - 改动范围: toolContext参数提取逻辑

#### 4.1.2 前端文件修改

**修改文件**:

1. **`aiChatService.js`**
   - 路径: `src/components/70_ai/api/aiChatService.js`
   - 修改内容: sendMessageStream方法新增pageContext参数
   - 改动范围: 约5行

2. **`chat.js`**
   - 路径: `src/components/70_ai/store/modules/chat.js`
   - 修改内容: sendMessage action获取路由信息并传递给aiChatService
   - 改动范围: 约10行

#### 4.1.3 数据库修改
**无需修改数据库** - 纯内存数据传递

### 4.2 核心代码设计

#### 4.2.1 后端 - PageContextMcpTools.java (新增)

```java
package com.xinyirun.scm.ai.mcp.P00000044.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.ai.chat.model.ToolContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 页面上下文MCP工具
 *
 * 提供获取用户当前所在页面上下文信息的能力
 * 用于AI Chat回答"我现在打开的页面是什么？"等问题
 */
@Slf4j
@Component
public class PageContextMcpTools {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取用户当前所在页面的上下文信息
     *
     * 从ToolContext中读取前端传递的页面上下文，包含page_code、title、path
     *
     * @param toolContext 工具上下文，包含前端传递的pageContext
     * @return JSON格式的页面上下文信息
     */
    @Tool(name = "get_current_page_info",
          description = "获取用户当前正在访问的页面信息。当用户询问'我现在在哪个页面'、'当前页面是什么'等问题时调用此工具。")
    public String getCurrentPageInfo(ToolContext toolContext) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 从ToolContext获取pageContext
            Map<String, Object> pageContext = null;
            if (toolContext != null && toolContext.getContext().containsKey("pageContext")) {
                Object pageContextObj = toolContext.getContext().get("pageContext");
                if (pageContextObj instanceof Map) {
                    pageContext = (Map<String, Object>) pageContextObj;
                }
            }

            if (pageContext == null || pageContext.isEmpty()) {
                // 无页面上下文信息
                result.put("success", false);
                result.put("message", "无法获取当前页面信息");
                result.put("_aiHint", "前端未传递页面上下文信息，可能是用户未在具体业务页面，或页面信息传递异常。请友好告知用户当前无法确定所在页面。");
            } else {
                // 成功获取页面上下文
                result.put("success", true);
                result.put("pageContext", pageContext);

                String title = (String) pageContext.getOrDefault("title", "未知页面");
                String path = (String) pageContext.getOrDefault("path", "");
                String pageCode = (String) pageContext.getOrDefault("page_code", "");

                result.put("message", String.format("用户当前在「%s」页面", title));
                result.put("_aiHint", String.format(
                    "用户正在访问的页面信息：页面名称=%s，路径=%s，页面编码=%s。请用自然语言告知用户当前所在页面，可以提供该页面的简要说明或可用操作。",
                    title, path, pageCode
                ));
            }

            return objectMapper.writeValueAsString(result);

        } catch (Exception e) {
            log.error("获取页面上下文失败", e);
            result.put("success", false);
            result.put("message", "获取页面信息失败: " + e.getMessage());
            result.put("_aiHint", "系统错误，请稍后重试");

            try {
                return objectMapper.writeValueAsString(result);
            } catch (JsonProcessingException jsonEx) {
                return "{\"success\":false,\"message\":\"JSON序列化失败\"}";
            }
        }
    }
}
```

#### 4.2.2 前端 - aiChatService.js 修改

```javascript
// 修改 sendMessageStream 方法签名，新增 pageContext 参数
// 位置: src/components/70_ai/api/aiChatService.js 约第60-80行

/**
 * 发送消息 - SSE流式响应
 * @param {Object} params - 请求参数
 * @param {string} params.conversationId - 会话ID
 * @param {string} params.prompt - 用户消息
 * @param {string} params.chatModelId - 模型ID
 * @param {Object} params.pageContext - 【新增】当前页面上下文
 * @param {string} params.pageContext.page_code - 页面编码
 * @param {string} params.pageContext.title - 页面标题
 * @param {string} params.pageContext.path - 页面路径
 * @param {Object} callbacks - 回调函数
 */
async sendMessageStream({ conversationId, prompt, chatModelId, pageContext }, callbacks) {
  // ... 现有代码 ...

  const response = await fetch(url, {
    method: 'POST',
    headers,
    credentials: 'include',
    body: JSON.stringify({
      conversationId,
      prompt,
      chatModelId,
      pageContext  // 【新增】页面上下文
    }),
    signal: controller.signal
  })

  // ... 现有代码 ...
}
```

#### 4.2.3 前端 - chat.js 修改

```javascript
// 修改 sendMessage action，获取路由信息并传递
// 位置: src/components/70_ai/store/modules/chat.js 约第205-250行

async sendMessage ({ commit, state, dispatch, rootState }, content) {
  if (!content.trim()) return

  // ... 现有代码 ...

  try {
    commit('SET_LOADING', true)

    if (!state.conversationId) {
      await dispatch('createConversation', content)
    }

    // 【新增】获取当前页面上下文
    const pageContext = {}
    if (rootState.route) {
      // 如果使用 vuex-router-sync
      pageContext.page_code = rootState.route.meta?.page_code || ''
      pageContext.title = rootState.route.meta?.title || ''
      pageContext.path = rootState.route.path || ''
    }

    // ... 用户消息创建代码 ...

    _cancelFunction = aiChatService.sendMessageStream({
      conversationId: state.conversationId,
      prompt: content.trim(),
      chatModelId: 'default',
      pageContext  // 【新增】传递页面上下文
    }, {
      onStart: () => { /* ... */ },
      onContent: (contentChunk) => { /* ... */ },
      onComplete: (fullContent, chatResponse) => { /* ... */ },
      onError: (_error) => { /* ... */ }
    })

  } catch (error) {
    // ... 现有错误处理 ...
  }
}
```

### 4.3 后端Controller修改说明

需要确认 `AiConversationController.java` 中如何将pageContext放入ToolContext。查看现有tenantCode/staffId的处理方式后，在同样位置添加pageContext的处理。

---

## 五、风险分析与缓解措施

### 5.1 风险清单

| 风险项 | 风险等级 | 影响范围 | 缓解措施 |
|--------|---------|---------|---------|
| **R1: pageContext未传递** | 🟢 低 | 功能降级 | MCP工具返回友好提示"无法确定当前页面"，不影响其他功能 |
| **R2: 路由信息不完整** | 🟢 低 | 用户体验 | 设置默认值，确保至少返回path信息 |
| **R3: 前后端字段不匹配** | 🟢 低 | 功能异常 | 使用统一的字段名(page_code, title, path)，后端做空值判断 |
| **R4: LLM不调用工具** | 🟢 低 | 功能失效 | 工具description明确说明使用场景，测试验证触发率 |

### 5.2 缓解措施详解

**R1: pageContext未传递**
- 后端MCP工具做空值判断
- 返回`_aiHint`引导LLM友好回复"无法确定当前页面"
- 不影响聊天主流程

**R2: 路由信息不完整**
- 前端对meta字段做空值判断，设置默认值
- page_code缺失时使用空字符串
- title缺失时使用"未知页面"

**R3: 前后端字段不匹配**
- 统一使用snake_case命名(page_code, title, path)
- 后端使用getOrDefault做空值保护

**R4: LLM不调用工具**
- 工具description明确："当用户询问'我现在在哪个页面'、'当前页面是什么'等问题时调用"
- 测试多种问法验证触发率

---

## 六、实施计划

### 6.1 代码实施步骤

**Step 1: 后端 - 新增PageContextMcpTools.java** (约20分钟)
- 创建MCP工具类
- 实现getCurrentPageInfo方法
- 添加@Tool注解和description

**Step 2: 后端 - 修改Controller/Config** (约15分钟)
- 确认pageContext如何放入ToolContext
- 修改相关代码支持pageContext传递

**Step 3: 前端 - 修改aiChatService.js** (约10分钟)
- sendMessageStream方法新增pageContext参数
- 请求body新增pageContext字段

**Step 4: 前端 - 修改chat.js** (约10分钟)
- sendMessage action获取$route信息
- 构建pageContext对象并传递

**Step 5: 集成测试** (约20分钟)
- 测试场景1: 在出库计划页面问"我现在打开的页面是什么？"
- 测试场景2: 在首页问"当前页面是哪个？"
- 测试场景3: 验证其他对话功能不受影响

### 6.2 预计工作量
- **后端开发**: 35分钟
- **前端开发**: 20分钟
- **测试验证**: 20分钟
- **总计**: 1.5小时

---

## 七、验收标准

### 7.1 功能验收

✅ **F1: 基本场景**
- 用户在「出库计划」页面发送消息："我现在打开的页面是什么？"
- AI回复应包含：页面名称"出库计划"、路径"/out/plan"

✅ **F2: 变体问法**
- "当前页面是哪个？" - 应正确回答
- "我在哪个页面？" - 应正确回答
- "这是什么页面？" - 应正确回答

✅ **F3: 降级场景**
- pageContext未传递时，AI友好回复"无法确定您当前所在页面"
- 不影响其他对话功能

### 7.2 非功能验收

✅ **NF1: 性能**
- 新增功能不影响聊天响应时间
- MCP工具调用<10ms（纯内存操作）

✅ **NF2: 兼容性**
- 现有聊天功能完全不受影响
- 不传递pageContext时系统正常工作

✅ **NF3: 代码质量**
- 通过QA代码审查
- 前后端代码符合项目规范

---

## 八、总结

### 8.1 方案核心价值
1. **上下文感知**: AI Chat获得页面上下文感知能力
2. **最简实现**: 复用现有ToolContext机制，改动最小
3. **零破坏性**: 不影响现有功能，向后兼容
4. **可扩展性**: 为后续弹窗上下文、操作引导等功能奠定基础

### 8.2 关键技术点
- **ToolContext扩展**: 利用现有机制传递pageContext
- **MCP工具模式**: 遵循现有工具开发规范
- **前端路由集成**: 从Vue Router获取页面元信息

### 8.3 后续优化方向（第二阶段）
- 支持弹窗/对话框上下文传递
- 支持页面操作状态（如正在编辑、正在查看）
- 支持Tab页多开场景

---

**文档版本**: v1.0
**编写时间**: 2025-11-25
**编写人**: zzxxhh (AI架构师)
**审批状态**: 待审批
