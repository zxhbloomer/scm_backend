# GoodsQueryMcpTools 重构与 returnDirect 白名单实现计划

> **For agentic workers:** REQUIRED: Use superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 重构商品查询MCP工具到通用目录、内联服务层、实现returnDirect白名单、修复工作流节点提示词

**Architecture:** 删除不必要的 GoodsAiService 中间层，将 GoodsQueryMcpTools 移至通用目录并直接操作 Mapper；在 WorkflowConstants 中维护 returnDirect 白名单常量，McpToolConfig 读取白名单设置 ToolMetadata，AiChatMemoryConfig 透传 ToolMetadata，AiChatBaseService 注册 ToolCallAdvisor 使 returnDirect 生效；通过 SQL 修复工作流节点提示词。

**Tech Stack:** Spring Boot 3.1.4, Spring AI, MyBatis Plus, MySQL

---

## 文件变更清单

| 操作 | 文件路径 |
|------|---------|
| 新建 | `scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/utils/master/goods/GoodsQueryMcpTools.java` |
| 删除 | `scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/utils/master/GoodsAiService.java` |
| 删除 | `scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/` 目录 |
| 修改 | `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowConstants.java` |
| 修改 | `scm-ai/src/main/java/com/xinyirun/scm/ai/config/mcp/McpToolConfig.java` |
| 修改 | `scm-ai/src/main/java/com/xinyirun/scm/ai/config/memory/AiChatMemoryConfig.java` |
| 修改 | `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/chat/AiChatBaseService.java` |
| SQL  | UPDATE `ai_workflow_node` WHERE uuid = `zpwRXVCRawID8hPts1mDZefCtNUuVDly` |

---

## Task 1: 新建 GoodsQueryMcpTools（通用目录）

**Files:**
- Create: `scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/utils/master/goods/GoodsQueryMcpTools.java`

- [ ] **Step 1: 创建新文件**

```java
package com.xinyirun.scm.ai.mcp.utils.master.goods;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecVo;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品查询MCP工具集（通用）
 * 支持按结构化JSON关键词或单关键词查询商品规格列表
 */
@Slf4j
@Component
public class GoodsQueryMcpTools {

    @Autowired
    private MGoodsSpecMapper goodsSpecMapper;

    /**
     * 按结构化JSON关键词查询商品（商品名、规格参数、编码独立模糊匹配）
     */
    @McpTool(description = """
        按结构化关键词查询商品（物料规格），支持商品名、规格参数、编码分别独立模糊匹配，精度优于单关键词查询。
        keywordJson 格式：{"goods_name":"商品名","spec":"规格参数如30-80mm","code":"编码"}，字段为空时传空字符串。
        返回启用状态商品列表，包含 sku_id、sku_code、sku_name、goods_id、goods_code、goods_name、unit 等字段。
        total 字段表示查询到的商品数量。
        """)
    public String queryGoodsByKeywordJson(
            @McpToolParam(description = "租户编码，用于数据权限控制") String tenantCode,
            @McpToolParam(description = "结构化关键词JSON，格式：{\"goods_name\":\"\",\"spec\":\"\",\"code\":\"\"}") String keywordJson) {

        log.info("MCP工具调用 - 查询商品(JSON): 租户={}, keywordJson={}", tenantCode, keywordJson);

        try {
            DataSourceHelper.use(tenantCode);

            JSONObject kw = JSON.parseObject(keywordJson);
            MGoodsSpecVo condition = new MGoodsSpecVo();
            String goodsName = kw.getString("goods_name");
            String spec      = kw.getString("spec");
            String code      = kw.getString("code");
            condition.setAi_goods_name(StringUtils.hasText(goodsName) ? goodsName : null);
            condition.setAi_spec(StringUtils.hasText(spec)            ? spec      : null);
            condition.setAi_code(StringUtils.hasText(code)            ? code      : null);

            Page<MGoodsSpecVo> page = new Page<>(1, 50);
            IPage<MGoodsSpecVo> pageResult = goodsSpecMapper.selectPageForAi(page, condition);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("total", pageResult.getTotal());
            result.put("list", pageResult.getRecords());
            result.put("keywordJson", keywordJson);
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 查询商品(JSON): {}", e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "total", 0,
                "list", List.of(),
                "message", "查询商品失败: " + e.getMessage()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            DataSourceHelper.close();
        }
    }

    /**
     * 按关键词查询商品（模糊匹配商品名称、规格、编码）
     */
    @McpTool(description = """
        按关键词查询商品（物料规格），支持模糊匹配商品名称、规格名称、商品编码。
        返回匹配的启用状态商品列表，包含 sku_id、sku_code、sku_name、goods_id、goods_code、goods_name、unit 等字段。
        total 字段表示查询到的商品数量。
        """)
    public String queryGoodsByName(
            @McpToolParam(description = "租户编码，用于数据权限控制") String tenantCode,
            @McpToolParam(description = "商品名称或规格关键词，支持模糊查询") String keyword) {

        log.info("MCP工具调用 - 查询商品: 租户={}, 关键词={}", tenantCode, keyword);

        try {
            DataSourceHelper.use(tenantCode);

            MGoodsSpecVo condition = new MGoodsSpecVo();
            condition.setKeyword(keyword);

            Page<MGoodsSpecVo> page = new Page<>(1, 50);
            IPage<MGoodsSpecVo> pageResult = goodsSpecMapper.selectPage(page, condition);

            List<MGoodsSpecVo> list = pageResult.getRecords().stream()
                .filter(vo -> vo.getEnable() != null && vo.getEnable())
                .collect(Collectors.toList());

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("total", list.size());
            result.put("list", list);
            result.put("keyword", keyword);
            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 查询商品: {}", e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "total", 0,
                "list", List.of(),
                "message", "查询商品失败: " + e.getMessage()
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            DataSourceHelper.close();
        }
    }
}
```

- [ ] **Step 2: 删除旧文件和目录**

删除以下文件：
- `scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/utils/master/GoodsAiService.java`
- `scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/` 整个目录

- [ ] **Step 3: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/utils/master/goods/GoodsQueryMcpTools.java
git -C 00_scm_backend/scm_backend rm -r scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/utils/master/GoodsAiService.java
git -C 00_scm_backend/scm_backend rm -r "scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/"
git -C 00_scm_backend/scm_backend commit -m "refactor(ai): 移动GoodsQueryMcpTools到通用目录，内联GoodsAiService，删除P00000170目录"
```

---

## Task 2: WorkflowConstants 新增 returnDirect 白名单常量

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowConstants.java`

- [ ] **Step 1: 新增 import 和常量**

在文件顶部 `import` 区域新增：
```java
import java.util.Set;
```

在 `WorkflowConstants` 类中，`HUMAN_FEEDBACK_KEY` 常量之后新增：

```java
/**
 * returnDirect 白名单工具集合
 * 这些工具调用后直接返回结果，不再经过LLM二次处理
 * 格式：ClassName.methodName（与 McpToolConfig 中 createToolCallback 的 toolName 一致）
 */
public static final Set<String> MCP_RETURN_DIRECT_TOOLS = Set.of(
    "GoodsQueryMcpTools.queryGoodsByKeywordJson",
    "GoodsQueryMcpTools.queryGoodsByName"
);
```

- [ ] **Step 2: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowConstants.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): WorkflowConstants新增MCP_RETURN_DIRECT_TOOLS白名单常量"
```

---

## Task 3: McpToolConfig 加 ToolMetadata 白名单判断

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/config/mcp/McpToolConfig.java`

- [ ] **Step 1: 新增 import**

在文件顶部 import 区域新增：
```java
import org.springframework.ai.tool.metadata.ToolMetadata;
import com.xinyirun.scm.ai.workflow.WorkflowConstants;
```

- [ ] **Step 2: 修改 createToolCallback() 末尾的构建逻辑**

将原来的：
```java
return FunctionToolCallback.builder(toolName, toolFunction)
        .description(description)
        .inputType(Map.class)
        .inputSchema(inputSchema)
        .build();
```

替换为：
```java
FunctionToolCallback.Builder builder = FunctionToolCallback.builder(toolName, toolFunction)
        .description(description)
        .inputType(Map.class)
        .inputSchema(inputSchema);

// returnDirect 白名单：在白名单中的工具调用后直接返回，不经过LLM二次处理
if (WorkflowConstants.MCP_RETURN_DIRECT_TOOLS.contains(toolName)) {
    builder.toolMetadata(ToolMetadata.builder().returnDirect(true).build());
}

return builder.build();
```

- [ ] **Step 3: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/config/mcp/McpToolConfig.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): McpToolConfig按白名单设置returnDirect ToolMetadata"
```

---

## Task 4: AiChatMemoryConfig 透传 ToolMetadata

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/config/memory/AiChatMemoryConfig.java`

- [ ] **Step 1: 新增 import**

在文件顶部 import 区域新增：
```java
import org.springframework.ai.tool.metadata.ToolMetadata;
```

- [ ] **Step 2: 修改 sanitizeToolCallback() 的匿名类**

在匿名 `ToolCallback` 实现中，`getToolDefinition()` 之后新增 `getToolMetadata()` override：

```java
@Override
public ToolMetadata getToolMetadata() {
    return original.getToolMetadata();
}
```

完整匿名类变为：
```java
return new ToolCallback() {
    @Override
    public ToolDefinition getToolDefinition() {
        return sanitizedDef;
    }

    @Override
    public ToolMetadata getToolMetadata() {
        return original.getToolMetadata();
    }

    @Override
    public String call(String toolInput) {
        return original.call(toolInput);
    }

    @Override
    public String call(String toolInput, ToolContext toolContext) {
        return original.call(toolInput, toolContext);
    }
};
```

- [ ] **Step 3: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/config/memory/AiChatMemoryConfig.java
git -C 00_scm_backend/scm_backend commit -m "fix(ai): sanitizeToolCallback透传getToolMetadata，防止returnDirect被默认值覆盖"
```

---

## Task 5: AiChatBaseService 注册 ToolCallAdvisor

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/chat/AiChatBaseService.java`

- [ ] **Step 1: 新增 import**

```java
import org.springframework.ai.chat.client.advisor.ToolCallAdvisor;
```

- [ ] **Step 2: 修改 chatStreamWithMcpTools() — 过滤工具路径**

将：
```java
ChatClient filteredClient = ChatClient.builder(aiModelProvider.getChatModel())
        .defaultSystem(MCP_TOOL_SYSTEM_PROMPT)
        .defaultToolCallbacks(filteredCallbacks)
        .build();
```
替换为：
```java
ChatClient filteredClient = ChatClient.builder(aiModelProvider.getChatModel())
        .defaultSystem(MCP_TOOL_SYSTEM_PROMPT)
        .defaultToolCallbacks(filteredCallbacks)
        .defaultAdvisors(new ToolCallAdvisor())
        .build();
```

- [ ] **Step 3: 修改 chatStreamWithMcpTools() — 全量工具路径**

将：
```java
ChatClient dynamicMcpClient = ChatClient.builder(aiModelProvider.getChatModel())
        .defaultSystem(MCP_TOOL_SYSTEM_PROMPT)
        .defaultToolCallbacks(mcpToolCallbackMap.values().toArray(new ToolCallback[0]))
        .build();
```
替换为：
```java
ChatClient dynamicMcpClient = ChatClient.builder(aiModelProvider.getChatModel())
        .defaultSystem(MCP_TOOL_SYSTEM_PROMPT)
        .defaultToolCallbacks(mcpToolCallbackMap.values().toArray(new ToolCallback[0]))
        .defaultAdvisors(new ToolCallAdvisor())
        .build();
```

- [ ] **Step 4: 修改 chatWithMcpTools() — 过滤工具路径**

将：
```java
ChatClient filteredClient = ChatClient.builder(aiModelProvider.getChatModel())
        .defaultSystem(MCP_TOOL_SYSTEM_PROMPT)
        .defaultToolCallbacks(filteredCallbacks)
        .build();
```
替换为：
```java
ChatClient filteredClient = ChatClient.builder(aiModelProvider.getChatModel())
        .defaultSystem(MCP_TOOL_SYSTEM_PROMPT)
        .defaultToolCallbacks(filteredCallbacks)
        .defaultAdvisors(new ToolCallAdvisor())
        .build();
```

- [ ] **Step 5: 修改 chatWithMcpTools() — 全量工具路径**

将：
```java
ChatClient dynamicMcpClient = ChatClient.builder(aiModelProvider.getChatModel())
        .defaultSystem(MCP_TOOL_SYSTEM_PROMPT)
        .defaultToolCallbacks(mcpToolCallbackMap.values().toArray(new ToolCallback[0]))
        .build();
```
替换为：
```java
ChatClient dynamicMcpClient = ChatClient.builder(aiModelProvider.getChatModel())
        .defaultSystem(MCP_TOOL_SYSTEM_PROMPT)
        .defaultToolCallbacks(mcpToolCallbackMap.values().toArray(new ToolCallback[0]))
        .defaultAdvisors(new ToolCallAdvisor())
        .build();
```

- [ ] **Step 6: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/chat/AiChatBaseService.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): chatWithMcpTools和chatStreamWithMcpTools注册ToolCallAdvisor使returnDirect生效"
```

---

## Task 6: 修复工作流节点提示词（SQL）

**Files:**
- SQL: UPDATE `ai_workflow_node` WHERE uuid = `zpwRXVCRawID8hPts1mDZefCtNUuVDly`

- [ ] **Step 1: 执行 SQL**

```sql
UPDATE ai_workflow_node
SET node_config = JSON_SET(
    node_config,
    '$.tool_input',
    '根据以下商品查询关键词JSON，调用 queryGoodsByKeywordJson 工具查询商品列表。\n\n关键词JSON：${查询关键词提取}\n\n参数说明：\n- keywordJson：直接使用上面的关键词JSON字符串，格式为 {"goods_name":"...","spec":"...","code":"..."}，字段为空时传空字符串\n\n要求：直接调用工具，不要修改参数内容，工具返回结果直接输出。'
)
WHERE uuid = 'zpwRXVCRawID8hPts1mDZefCtNUuVDly';
```

验证：
```sql
SELECT uuid, JSON_EXTRACT(node_config, '$.tool_input') as tool_input
FROM ai_workflow_node
WHERE uuid = 'zpwRXVCRawID8hPts1mDZefCtNUuVDly';
```

---

## 执行顺序

Task 1 → Task 2 → Task 3 → Task 4 → Task 5 → Task 6

Task 1-5 有依赖关系（Task 3 依赖 Task 2 的常量），按顺序执行。Task 6 独立，可任意时机执行。
