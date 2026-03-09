# 人机交互节点设计文档

> 日期: 2026-03-06
> 状态: 已审批，待实施

## 1. 背景

工作流系统中已有 HumanFeedbackNode，但功能过于简单（只有 `tip` 文本字段）。
前端已有 AiUserConfirm / AiUserSelect / AiUserForm 三个交互组件，但未与工作流运行时打通。
需要扩展人机交互节点，支持多种交互类型，并完成前后端闭环。

### 调研参考
- **Dify**: 表单字段 + 操作按钮，DB持久化，支持超时和分支路由
- **FastGPT**: userSelect + formInput，简洁表格配置
- **Spring AI Alibaba**: interruptBefore + Checkpoint，通用 state 键值

## 2. 设计决策

| 决策点 | 选择 | 理由 |
|--------|------|------|
| 交互类型 | text / confirm / select / form | 覆盖SCM业务场景：自由输入、审批确认、供应商选择、补充信息 |
| 分支路由 | 用现有条件分支节点(SwitcherNode) | 不改工作流编辑器连线逻辑，改动最小 |
| 编辑器风格 | FastGPT简洁表格方案 | 与Element UI风格匹配 |
| 选项来源 | 支持静态配置 + 动态(上游节点输出) | 满足AI查询后让用户选择的场景 |
| 持久化 | DB持久化(ai_workflow_interaction表) | 支持刷新恢复、超时处理、审计记录 |
| 超时处理 | Phase 1 就做，超时后终止工作流 | 后端代码已基本就绪，只差定时任务 |

### 不做的事（YAGNI）
- 不做多输出口分支路由
- 不做邮件/站内信通知
- 不做 tip 中的变量模板引用
- 不做多选(multiSelect)
- 超时不做默认值继续

## 3. HumanFeedbackNodeConfig 扩展

```java
@Data
public class HumanFeedbackNodeConfig {
    // --- 基础配置 ---
    private String tip;                        // 提示文本（向后兼容）
    private String interactionType;            // confirm | select | form | text（默认text）
    private Integer timeoutMinutes;            // 超时时间（分钟），默认30

    // --- confirm 类型参数 ---
    private String confirmText;                // 确认按钮文本，默认"确认"
    private String rejectText;                 // 驳回按钮文本，默认"驳回"
    private String detail;                     // 详情说明文本

    // --- select 类型参数 ---
    private String optionsSource;              // "static" | "dynamic"，默认static
    private List<SelectOption> options;         // 静态选项列表
    private String dynamicOptionsParam;        // 动态选项：上游节点输出参数名

    // --- form 类型参数 ---
    private List<FormField> fields;            // 表单字段列表

    @Data
    public static class SelectOption {
        private String key;
        private String label;
    }

    @Data
    public static class FormField {
        private String key;                    // 字段标识
        private String label;                  // 显示名称
        private String type;                   // text | textarea | number | select
        private Boolean required;              // 是否必填
        private List<SelectOption> options;    // type=select时的选项
    }
}
```

## 4. 数据流

### 4.1 总体架构

```
[上游节点] ──输出data──→ 中断点(handleInterruption)
                            │
                 读config + 读上游输出(动态选项)
                 创建DB交互记录(WAITING)
                 合并为 interaction_request
                            │
                   SSE interrupt事件
                            │
                            ▼
                 前端渲染交互UI
                 (AiUserConfirm/Select/Form)
                            │
                       用户操作
                            │
                 DB更新(SUBMITTED)
                 /resume 恢复工作流
                            │
                            ▼
              HumanFeedbackNode.onProcess()
              解析反馈 → 输出多个NodeIOData
                            │
                            ▼
              [条件分支节点] 根据action字段路由
```

### 4.2 SSE中断事件格式

```json
{
  "type": "interrupt",
  "node": "node-uuid",
  "tip": "提示文本",
  "interactionType": "select",
  "interaction_request": {
    "interaction_uuid": "xxx",
    "interaction_type": "user_select",
    "description": "请选择供应商",
    "timeout_minutes": 30,
    "timeout_at": "2026-03-06T17:00:00",
    "params": {
      "options": [
        {"key": "a", "label": "供应商A"},
        {"key": "b", "label": "供应商B"}
      ]
    }
  }
}
```

动态选项：handleInterruption() 从 wfState 中读取上游节点输出（通过 dynamicOptionsParam 指定的参数名），替换到 params.options。

### 4.3 Resume数据格式（用户操作 → 后端）

| 交互类型 | 用户操作 | resume传入JSON |
|---------|---------|---------------|
| confirm | 点确认 | `{"action":"confirm","confirmed":true}` |
| confirm | 点驳回 | `{"action":"reject","confirmed":false}` |
| select | 选一项 | `{"action":"select_record","selectedKey":"a","selectedLabel":"供应商A"}` |
| form | 提交表单 | `{"action":"form_submit","data":{"address":"北京"}}` |
| text | 输入文本 | `{"action":"text_input","text":"用户输入"}` |

### 4.4 节点输出（NodeIOData，给下游节点）

每种类型输出多个NodeIOData参数：

| 参数名 | 说明 | 用途 |
|--------|------|------|
| action | 用户操作类型(confirm/reject/select_record/form_submit/text_input) | 条件分支节点读取 |
| output | 人类可读的文本摘要 | LLM节点读取 |
| selectedKey | select类型：选中的key | 条件分支节点读取 |
| formData | form类型：JSON字符串 | 下游节点读取 |

## 5. DB持久化

### 5.1 表：ai_workflow_interaction（已有Entity/Mapper/Service）

| 字段 | 说明 |
|------|------|
| interaction_uuid | 业务主键 |
| conversation_id | 关联对话 |
| runtime_uuid | 关联运行时 |
| node_uuid | 触发节点 |
| interaction_type | user_select / user_confirm / user_form / user_text |
| interaction_params | 交互参数JSON(选项/表单定义) |
| description | 提示文字 |
| status | WAITING → SUBMITTED / TIMEOUT / CANCELLED |
| timeout_minutes | 超时时间 |
| timeout_at | 超时截止时间 |
| feedback_data | 用户反馈JSON |
| feedback_action | 用户操作 |

### 5.2 状态流转

```
WAITING ──用户操作──→ SUBMITTED
WAITING ──用户取消──→ CANCELLED
WAITING ──定时扫描──→ TIMEOUT（终止工作流）
```

### 5.3 超时定时任务

- 每分钟扫描 `selectExpiredInteractions()`
- 超时记录更新为 TIMEOUT
- 对应工作流标记为终止（更新runtime状态）

## 6. 编辑器面板设计

顶部交互类型选择器（el-radio-group），根据类型动态显示配置区域：

### 6.1 公共配置
- 交互类型选择：自由文本 / 确认驳回 / 单项选择 / 表单填写
- 提示文本：el-input textarea
- 超时时间（分钟）：el-input-number，默认30

### 6.2 confirm类型
- 确认按钮文本：el-input，默认"确认"
- 驳回按钮文本：el-input，默认"驳回"
- 详情说明：el-input textarea

### 6.3 select类型
- 选项来源：el-radio（静态配置 / 动态）
- 静态时：选项列表表格（key + label + 删除按钮 + 添加按钮）
- 动态时：上游输出参数名 el-input

### 6.4 form类型
- 表单字段表格：字段标识 + 显示名称 + 类型(text/textarea/number/select) + 必填 + 删除
- 添加字段按钮

## 7. 已有代码资产

| 代码 | 状态 | 说明 |
|------|------|------|
| AiWorkflowInteractionEntity | 已写好(未提交) | DB实体 |
| AiWorkflowInteractionMapper | 已写好(未提交) | SQL查询 |
| AiWorkflowInteractionService | 已写好(未提交) | CRUD + 超时 |
| AiUserConfirm.vue | 已写好(未提交) | 确认/驳回UI |
| AiUserSelect.vue | 已写好(未提交) | 单选UI |
| AiUserForm.vue | 已写好(未提交) | 表单UI |
| AiInteractionManager.js | 已写好(未提交) | 倒计时+反馈提交 |
| WorkflowEventVo.createAiOpenDialogParaEvent | 已有 | 支持携带interaction_request |
| HumanFeedbackNodeConfig | 需扩展 | 当前只有tip |
| HumanFeedbackNode | 需扩展 | 当前只输出纯文本 |
| WorkflowEngine.handleInterruption | 需扩展 | 当前只发tip |
