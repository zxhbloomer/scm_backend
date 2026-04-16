# 商品查询MCP工具 + 工作流分支 实施计划

> **For agentic workers:** REQUIRED: Use superpowers:executing-plans to implement this plan.

**Goal:** 新增商品查询MCP工具，在"新增采购项目"工作流中加入商品查询→分类→人机交互的完整分支逻辑。

**Architecture:** 后端新增 `GoodsQueryMcpTools` + `GoodsAiService`，修复 `HumanFeedbackNode` 的 `select_record` 数据读取 bug，`SelectOption` 加 `data` 字段存完整商品信息。前端无需新增节点类型，只需修复 `AiUserSelect.vue` 的数据传递，并在工作流设计器中配置新流程。

**Tech Stack:** Java 17 / Spring Boot 3.1.4 / MyBatis Plus / Vue.js 2.7.16 / Element UI

---

## Chunk 1: 后端 - 商品查询MCP工具

### Task 1: 新建 GoodsAiService

**文件：**
- 新建：`scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/service/GoodsAiService.java`
- 新建：`scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/package-info.java`
- 新建：`scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/service/package-info.java`
- 新建：`scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/tools/package-info.java`

**说明：** P00000170 是采购项目管理的页面代码，参考 P00000025 的目录结构。

- [ ] **Step 1: 新建 package-info.java**

`scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/package-info.java`:
```java
/**
 * 采购项目管理 AI MCP 工具包
 * 页面代码: P00000170
 */
package com.xinyirun.scm.ai.mcp.P00000170;
```

`scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/service/package-info.java`:
```java
package com.xinyirun.scm.ai.mcp.P00000170.service;
```

`scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/tools/package-info.java`:
```java
package com.xinyirun.scm.ai.mcp.P00000170.tools;
```

- [ ] **Step 2: 新建 GoodsAiService.java**

`scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/service/GoodsAiService.java`:
```java
package com.xinyirun.scm.ai.mcp.P00000170.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.xinyirun.scm.bean.system.vo.master.goods.MGoodsSpecVo;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品查询AI服务
 * 为MCP工具提供商品查询支持
 */
@Slf4j
@Service
public class GoodsAiService {

    @Autowired
    private MGoodsSpecMapper goodsSpecMapper;

    /**
     * 按关键词查询商品（模糊匹配商品名称、规格、编码）
     *
     * @param keyword 查询关键词
     * @return 查询结果，包含 total 和 list
     */
    public Map<String, Object> queryGoodsByKeyword(String keyword) {
        log.info("AI查询商品 - 关键词: {}", keyword);

        Map<String, Object> result = new HashMap<>();
        try {
            MGoodsSpecVo condition = new MGoodsSpecVo();
            condition.setKeyword(keyword);

            // 查询前50条，避免数据量过大
            Page<MGoodsSpecVo> page = new Page<>(1, 50);
            com.baomidou.mybatisplus.core.metadata.IPage<MGoodsSpecVo> pageResult =
                goodsSpecMapper.selectPage(page, condition);

            List<Map<String, Object>> list = pageResult.getRecords().stream()
                .map(this::toGoodsMap)
                .collect(Collectors.toList());

            result.put("success", true);
            result.put("total", pageResult.getTotal());
            result.put("list", list);
            result.put("keyword", keyword);

        } catch (Exception e) {
            log.error("AI查询商品失败 - 关键词: {}, 错误: {}", keyword, e.getMessage(), e);
            result.put("success", false);
            result.put("total", 0);
            result.put("list", List.of());
            result.put("message", "查询失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 将 MGoodsSpecVo 转为 Map，只保留前端需要的字段
     */
    private Map<String, Object> toGoodsMap(MGoodsSpecVo vo) {
        Map<String, Object> map = new HashMap<>();
        map.put("sku_id", vo.getId());
        map.put("sku_code", vo.getSku_code());
        map.put("sku_name", vo.getSpec());
        map.put("goods_id", vo.getGoods_id());
        map.put("goods_code", vo.getGoods_code());
        map.put("goods_name", vo.getGoods_name());
        map.put("unit", vo.getUnit());
        map.put("category_name", vo.getCategory_name());
        map.put("enable", vo.getEnable());
        return map;
    }
}
```

- [ ] **Step 3: commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/
git -C 00_scm_backend/scm_backend commit -m "feat(ai): 新增商品查询AI服务 GoodsAiService"
```

---

### Task 2: 新建 GoodsQueryMcpTools

**文件：**
- 新建：`scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/tools/GoodsQueryMcpTools.java`

- [ ] **Step 1: 新建 GoodsQueryMcpTools.java**

```java
package com.xinyirun.scm.ai.mcp.P00000170.tools;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONWriter;
import com.xinyirun.scm.ai.mcp.P00000170.service.GoodsAiService;
import com.xinyirun.scm.common.utils.datasource.DataSourceHelper;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 商品查询MCP工具集
 * 页面代码: P00000170 采购项目管理
 */
@Slf4j
@Component
public class GoodsQueryMcpTools {

    @Autowired
    private GoodsAiService goodsAiService;

    /**
     * 按名称查询商品
     */
    @McpTool(description = """
        P00000170
        按关键词查询商品（物料规格），支持模糊匹配商品名称、规格名称、商品编码。
        返回匹配的商品列表，包含 sku_id、sku_code、sku_name、goods_id、goods_code、goods_name、unit 等字段。
        total 字段表示查询到的商品数量。
        """)
    public String queryGoodsByName(
            @McpToolParam(description = "租户编码，用于数据权限控制") String tenantCode,
            @McpToolParam(description = "商品名称或规格关键词，支持模糊查询，如'钢材'、'螺纹钢'") String keyword) {

        log.info("MCP工具调用 - 查询商品: 租户={}, 关键词={}", tenantCode, keyword);

        try {
            DataSourceHelper.use(tenantCode);

            Map<String, Object> result = goodsAiService.queryGoodsByKeyword(keyword);
            result.put("tenantCode", tenantCode);
            result.put("toolName", "query_goods_by_name");

            return JSON.toJSONString(result, JSONWriter.Feature.PrettyFormat);

        } catch (Exception e) {
            log.error("MCP工具异常 - 查询商品: 租户={}, 关键词={}, 错误={}", tenantCode, keyword, e.getMessage(), e);
            return JSON.toJSONString(Map.of(
                "success", false,
                "total", 0,
                "list", java.util.List.of(),
                "message", "查询商品失败: " + e.getMessage(),
                "tenantCode", tenantCode,
                "toolName", "query_goods_by_name"
            ), JSONWriter.Feature.PrettyFormat);
        } finally {
            DataSourceHelper.close();
        }
    }
}
```

- [ ] **Step 2: commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/mcp/P00000170/tools/GoodsQueryMcpTools.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): 新增商品查询MCP工具 GoodsQueryMcpTools"
```

---

## Chunk 2: 后端 - 修复 HumanFeedbackNode + SelectOption

### Task 3: SelectOption 加 data 字段

**文件：**
- 修改：`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/humanfeedback/HumanFeedbackNodeConfig.java`

**说明：** `SelectOption` 目前只有 `key` 和 `label`。需要加 `data` 字段存完整的业务数据（如商品信息 JSON），前端 `AiUserSelect.vue` 已经在用 `selected.data`，后端补上这个字段即可。

- [ ] **Step 1: 修改 SelectOption 内部类**

在 `HumanFeedbackNodeConfig.java` 的 `SelectOption` 内部类中加 `data` 字段：

```java
@Data
public static class SelectOption {
    private String key;
    private String label;
    /**
     * 选项携带的完整业务数据（JSON对象），选中后传给下游节点
     */
    private Object data;
}
```

- [ ] **Step 2: commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/humanfeedback/HumanFeedbackNodeConfig.java
git -C 00_scm_backend/scm_backend commit -m "fix(ai): SelectOption加data字段支持携带完整业务数据"
```

---

### Task 4: 修复 HumanFeedbackNode select_record 数据读取

**文件：**
- 修改：`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/humanfeedback/HumanFeedbackNode.java`

**说明：** 前端 `submitFeedback` 发送的 JSON 结构是：
```json
{ "type": "ai_interaction_feedback", "action": "select_record", "data": { "key": "123", "label": "钢材", ...完整商品数据 } }
```
但后端第85行读的是 `feedback.getString("selectedKey")`，`selectedKey` 根本不在顶层，在 `data` 对象里是 `key`。需要修复读取逻辑，并把完整 `data` 也输出出来供下游节点使用。

- [ ] **Step 1: 修改 select_record case**

将 `HumanFeedbackNode.java` 中 `case "select_record":` 块替换为：

```java
case "select_record":
    // 前端发送结构: { action: "select_record", data: { key, label, ...业务数据 } }
    JSONObject selectData = feedback.getJSONObject("data");
    String selectedKey = selectData != null ? selectData.getString("key") : null;
    String selectedLabel = selectData != null ? selectData.getString("label") : null;
    result.add(NodeIOData.createByText("selectedKey", "选中项Key",
        selectedKey != null ? selectedKey : ""));
    result.add(NodeIOData.createByText("output", "操作结果",
        "用户选择: " + (selectedLabel != null ? selectedLabel : selectedKey)));
    // 输出完整业务数据，供下游节点（如OpenPage预填）使用
    if (selectData != null) {
        result.add(NodeIOData.createByText("selectedData", "选中项完整数据",
            selectData.toJSONString()));
    }
    break;
```

- [ ] **Step 2: commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/humanfeedback/HumanFeedbackNode.java
git -C 00_scm_backend/scm_backend commit -m "fix(ai): 修复HumanFeedbackNode select_record数据读取，从data子对象读取key/label并输出selectedData"
```

---

### Task 5: WorkflowEngine resolveSelectOptions 支持 data 字段

**文件：**
- 修改：`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

**说明：** `resolveSelectOptions` 把上游节点输出解析为 `List<SelectOption>`，现在 `SelectOption` 有了 `data` 字段，MCP 工具返回的商品 list 中每条记录需要映射为 `SelectOption`（key=sku_id, label=goods_name+sku_name, data=完整商品Map）。

这个映射逻辑需要在 `resolveSelectOptions` 里处理：当解析到的 JSON 是商品列表格式（有 `sku_id` 字段）时，自动转换为 `SelectOption` 列表。

- [ ] **Step 1: 修改 resolveSelectOptions**

找到 `WorkflowEngine.java` 中 `resolveSelectOptions` 方法，在解析 JSON 数组时增加商品列表自动转换逻辑：

```java
private List<HumanFeedbackNodeConfig.SelectOption> resolveSelectOptions(HumanFeedbackNodeConfig config) {
    if ("dynamic".equals(config.getOptionsSource()) && config.getDynamicOptionsParam() != null) {
        String paramName = config.getDynamicOptionsParam();
        for (AbstractWfNode completedNode : wfState.getCompletedNodes()) {
            List<NodeIOData> outputs = completedNode.getState().getOutputs();
            if (outputs == null) continue;

            for (NodeIOData output : outputs) {
                if (paramName.equals(output.getName())) {
                    try {
                        String jsonStr = output.valueToString();
                        // 先尝试直接解析为 SelectOption 列表
                        List<HumanFeedbackNodeConfig.SelectOption> options =
                            com.alibaba.fastjson2.JSON.parseArray(jsonStr, HumanFeedbackNodeConfig.SelectOption.class);
                        if (options != null && !options.isEmpty()) {
                            return options;
                        }
                    } catch (Exception e) {
                        log.warn("解析动态选项失败, paramName={}, error={}", paramName, e.getMessage());
                    }
                }
            }
        }
        log.warn("未找到动态选项参数: {}", paramName);
    }
    return config.getOptions() != null ? config.getOptions() : List.of();
}
```

**注意：** 商品列表到 SelectOption 的转换在 MCP 工具的 Answer 节点提示词里由 LLM 完成（见 Task 7 工作流配置说明），不在这里硬编码。

- [ ] **Step 2: commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java
git -C 00_scm_backend/scm_backend commit -m "fix(ai): resolveSelectOptions优化，支持SelectOption data字段"
```

---

## Chunk 3: 前端 - 修复 AiUserSelect 数据传递

### Task 6: 修复 AiUserSelect.vue

**文件：**
- 修改：`src/components/70_ai/components/interaction/AiUserSelect.vue`

**说明：** 第69行 `selected.data` 在 `SelectOption` 没有 `data` 字段时是 `undefined`，走 fallback `{ key: this.selectedKey }`，导致后端拿不到完整商品数据。现在后端 `SelectOption` 加了 `data` 字段，前端需要把 `key`、`label`、`data` 都传过去。

- [ ] **Step 1: 修改 handleSubmit 方法**

将 `AiUserSelect.vue` 中 `handleSubmit` 方法替换为：

```js
handleSubmit () {
  if (!this.selectedKey) return
  this.submitted = true
  const selected = this.options.find(o => o.key === this.selectedKey)
  // 构建完整的选中数据：key + label + 业务data（如有）
  const submitData = {
    key: this.selectedKey,
    label: selected ? selected.label : this.selectedKey,
    ...(selected && selected.data ? selected.data : {})
  }
  this.$emit('submit', 'select_record', submitData)
}
```

- [ ] **Step 2: commit**

```bash
git -C 01_scm_frontend/scm_frontend add src/components/70_ai/components/interaction/AiUserSelect.vue
git -C 01_scm_frontend/scm_frontend commit -m "fix(ai): AiUserSelect传递完整选中数据(key+label+data)"
```

---

## Chunk 4: 工作流配置说明（数据库SQL）

### Task 7: 工作流节点配置

**说明：** 这部分是在工作流设计器里配置节点，不需要写代码，但需要理解每个节点的配置参数，以便在设计器中正确配置。以下是完整的工作流设计：

```
Start → MCP节点(查询商品) → 分类器(0条/1条/多条)
                                ├─ 0条 → HumanFeedback(confirm: 继续还是中断)
                                │           ├─ 确认 → Answer(告知用户未找到，继续流程)
                                │           └─ 驳回 → End
                                ├─ 1条 → Answer(自动填入商品信息) → OpenPage(预填表单)
                                └─ 多条 → HumanFeedback(select: 选择商品)
                                              └─ 选中 → Answer(填入选中商品) → OpenPage(预填表单)
```

**MCP节点配置：**
- `toolInput`: `根据用户输入的商品名称"${input}"查询商品信息`
- `toolNames`: `GoodsQueryMcpTools.queryGoodsByName`（限定只调用商品查询工具）
- `show_process_output`: true

**分类器节点配置：**
- `instruction`: `根据上游MCP节点返回的商品查询结果，判断查询到的商品数量。total字段为0时选"未找到商品"，total为1时选"找到1条商品"，total大于1时选"找到多条商品"`
- 分类1: `未找到商品`
- 分类2: `找到1条商品`
- 分类3: `找到多条商品`

**HumanFeedback(0条)节点配置：**
- `interactionType`: `confirm`
- `tip`: `未找到名称包含"${input}"的商品，是否继续填写采购项目？`
- `confirmText`: `继续填写`
- `rejectText`: `中断`

**HumanFeedback(多条)节点配置：**
- `interactionType`: `select`
- `tip`: `找到多条商品，请选择要使用的商品`
- `optionsSource`: `dynamic`
- `dynamicOptionsParam`: `goods_select_options`（上游Answer节点输出的参数名）

**注意：** 多条分支需要在分类器和HumanFeedback之间加一个Answer节点，让LLM把MCP返回的商品list转换为SelectOption格式（`[{key: sku_id, label: goods_name+sku_name, data: {...完整商品数据}}]`），输出参数名为 `goods_select_options`。

Answer节点提示词示例：
```
根据以下商品查询结果，生成供用户选择的选项列表，严格按JSON数组格式输出，不要有其他文字：
${output}

输出格式：
[
  {"key": "sku_id值", "label": "商品名称 - 规格名称", "data": {完整商品字段}},
  ...
]
```

---

## 已确认：toolNames 过滤已实现

`McpToolNodeConfig.toolNames` 字段已存在，`WorkflowUtil.streamingInvokeLLM` 中已有过滤逻辑（第186-196行），无需修改代码。在工作流设计器配置 MCP 节点时，`tool_names` 填 `["GoodsQueryMcpTools.queryGoodsByName"]` 即可限定只调用商品查询工具。

---

## 执行顺序

1. Task 1 → Task 2（后端MCP工具，独立）
2. Task 3 → Task 4 → Task 5（后端修复，有依赖顺序）
3. Task 6（前端修复，独立）
4. Task 7（工作流配置，在设计器中手动操作，最后做）
