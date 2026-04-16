# table_select 人机交互类型开发计划

**日期**: 2026-03-18
**状态**: 待 Review

---

## 一、背景与目标

### 问题
MCP 查询商品返回多条结果时，现有 `select` 类型只能展示简单下拉列表（单字段 label），无法展示商品编码、规格等多列信息，用户无法有效区分选项。

### 目标
新增 `table_select` 交互类型，支持以表格形式展示多列数据，用户点击行选中后提交给下游节点。

### 不改变的东西
- 提交数据格式不变：`{ action: "select_record", data: { key, label, ...业务数据 } }`
- `HumanFeedbackNode.parseUserFeedback()` 不变（`select_record` 分支已覆盖）
- 数据库表结构不变（`interaction_type` 是 varchar，存 `user_table_select` 即可）
- 现有 `select` / `confirm` / `form` / `text` 四种类型不受影响

---

## 二、改动清单

### 后端（2处）

#### 1. `HumanFeedbackNodeConfig.java`

**位置**: `scm-ai/.../workflow/node/humanfeedback/HumanFeedbackNodeConfig.java`

**改动**: 增加 `TableColumn` 内部类 + `columns` 字段

```java
// 新增内部类
@Data
public static class TableColumn {
    /** 字段标识，对应 option.data 中的 key */
    private String key;
    /** 列头显示名称 */
    private String label;
    /** 列宽（px），可选 */
    private Integer width;
}

// 新增字段（放在 form 类型参数之后）
/** 表格列定义（table_select 类型使用） */
private List<TableColumn> columns;
```

#### 2. `WorkflowEngine.java` — `buildInteractionParams()`

**位置**: `scm-ai/.../workflow/WorkflowEngine.java`，`buildInteractionParams()` 方法的 switch 块

**改动**: 增加 `table_select` case

```java
case "table_select":
    params.put("options", resolveSelectOptions(config));
    if (config.getColumns() != null) {
        params.put("columns", config.getColumns());
    }
    break;
```

> `resolveSelectOptions()` 已有，直接复用，不需要改。

---

### 前端（3处）

#### 3. 新建 `AiUserTableSelect.vue`

**位置**: `src/components/70_ai/components/interaction/AiUserTableSelect.vue`

**功能**:
- 用 `el-table` 展示多列数据
- 列定义来自 `interaction.params.columns`，数据来自 `interaction.params.options`
- 点击行高亮选中（单选）
- 确认按钮提交 `select_record` + 选中行完整数据
- 倒计时显示（复用 `formatRemainingTime`）

**数据结构**:
```
interaction.params.columns = [
  { key: "goods_name", label: "商品名称", width: 200 },
  { key: "goods_code", label: "商品编码", width: 120 }
]
interaction.params.options = [
  { key: "1", label: "焦炭", data: { goods_name: "焦炭", goods_code: "G001" } },
  { key: "2", label: "铁矿石", data: { goods_name: "铁矿石", goods_code: "G002" } }
]
```

**提交格式**（与现有 `AiUserSelect` 完全一致）:
```js
this.$emit('submit', 'select_record', {
  key: selectedRow.key,
  label: selectedRow.label,
  ...selectedRow.data   // 展开业务数据
})
```

#### 4. `MessageList.vue` — 增加 `table_select` 分支

**位置**: `src/components/70_ai/components/chat/messages/MessageList.vue`

**改动**:
1. import `AiUserTableSelect`
2. 注册组件
3. 在交互区域增加 `v-else-if` 分支

```html
<ai-user-table-select
  v-else-if="activeInteraction.type === 'user_table_select'"
  :interaction="activeInteraction"
  @submit="handleInteractionSubmit"
  @cancel="handleInteractionCancel"
/>
```

#### 5. `HumanFeedbackNodeProperty.vue` — 增加 table_select 配置UI

**位置**: `src/components/70_ai/components/workflow/components/properties/HumanFeedbackNodeProperty.vue`

**改动**:
1. 下拉选项增加 `table_select`
2. 增加 `table_select` 配置区块（列定义 + 选项来源，复用 select 的选项来源逻辑）
3. `computed.nodeConfig` 增加 `columns` 字段初始化
4. `methods` 增加列的增删操作

---

## 三、数据流全链路

```
工作流编辑器配置:
  interactionType = "table_select"
  optionsSource = "dynamic"
  dynamicOptionsParam = "var_goods"   ← 上游MCP节点输出参数名
  columns = [{ key, label, width }, ...]

        ↓ 工作流执行到 HumanFeedbackNode 前中断

WorkflowEngine.buildInteractionParams():
  type = "user_table_select"
  params = {
    columns: [...],
    options: resolveSelectOptions()  ← 从上游节点输出读取
  }

        ↓ SSE 发送 interaction_request 给前端

前端 MessageList 渲染 AiUserTableSelect:
  el-table 展示多列
  用户点击行选中

        ↓ 用户点击"确认选择"

前端提交:
  { action: "select_record", data: { key, label, ...业务字段 } }

        ↓ AiWorkflowInteractionService.submitFeedback()

HumanFeedbackNode.parseUserFeedback() (已有，不改):
  输出: selectedKey / selectedLabel / selectedData / output

        ↓ 下游节点（如 OpenPage）读取 selectedData 预填表单
```

---

## 四、边界情况

| 情况 | 处理方式 |
|------|----------|
| `columns` 为空 | 前端降级：只显示 `label` 列 |
| `options` 为空 | 前端显示"暂无数据" |
| 动态选项数据格式不符合 `[{key,label,data}]` | `resolveSelectOptions()` 解析失败时返回空列表，前端显示"暂无数据" |
| 用户未选中直接点确认 | 按钮 disabled，不可提交 |

---

## 五、不在本次范围内

- 动态选项的数据格式转换（MCP 输出 → `[{key,label,data}]`）：单独处理
- 多选（checkbox）：本次只做单选
- 分页：本次不做，数据量大时前端直接全量展示

---

## 六、文件改动汇总

| 文件 | 类型 | 改动量 |
|------|------|--------|
| `HumanFeedbackNodeConfig.java` | 修改 | +15行（TableColumn类 + columns字段） |
| `WorkflowEngine.java` | 修改 | +5行（table_select case） |
| `AiUserTableSelect.vue` | 新建 | ~100行 |
| `MessageList.vue` | 修改 | +8行（import + 注册 + v-else-if） |
| `HumanFeedbackNodeProperty.vue` | 修改 | +60行（table_select选项 + 列配置UI） |
