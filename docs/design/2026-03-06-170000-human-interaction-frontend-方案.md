# 人机交互节点前端编辑器面板设计方案

## 需求背景

后端 `HumanFeedbackNodeConfig` 已完成4种交互类型（text/confirm/select/form）的配置支持。
前端 `HumanFeedbackNodeProperty.vue` 当前仅有一个 `tip` 文本框，需要扩展为完整的可视化编辑器面板。

## 调用链路

```
工作流设计器 → NodePropertyPanel → HumanFeedbackNodeProperty.vue
  ↓ (保存)
wfNode.nodeConfig → JSON → 后端 HumanFeedbackNodeConfig (fastjson2反序列化)
  ↓ (运行时)
WorkflowEngine.handleInterruption() → 读取config → SSE推送interaction_request
  ↓
前端 AiInteractionManager → MessageList → AiUserConfirm/Select/Form 组件
```

## 修改文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `HumanFeedbackNodeProperty.vue` | 修改 | 唯一需修改的文件，重写为完整编辑器 |

**零新增文件**。

## 字段映射（后端Config → 前端控件）

| 后端字段 | 前端控件 | 显示条件 | 默认值 |
|---------|---------|---------|-------|
| `tip` | el-input textarea | 始终显示 | "" |
| `interactionType` | el-select | 始终显示 | "text" |
| `timeoutMinutes` | el-input-number | 始终显示 | 30 |
| `confirmText` | el-input | interactionType === 'confirm' | "确认" |
| `rejectText` | el-input | interactionType === 'confirm' | "驳回" |
| `detail` | el-input textarea | interactionType === 'confirm' | "" |
| `optionsSource` | el-radio-group | interactionType === 'select' | "static" |
| `options` | 动态列表[{key,label}] | select + optionsSource=static | [] |
| `dynamicOptionsParam` | el-input | select + optionsSource=dynamic | "" |
| `fields` | 动态列表[{key,label,type,required,options}] | interactionType === 'form' | [] |

## 编辑器面板布局

```
+------------------------------------------+
| 提示信息                                   |
| [textarea: tip]                          |
|                                          |
| 交互类型                                   |
| [select: 自由文本/确认驳回/单项选择/表单填写]  |
|                                          |
| ── 确认型配置 (仅confirm) ──               |
| 确认按钮文本: [input: confirmText]          |
| 驳回按钮文本: [input: rejectText]           |
| 详情说明:     [textarea: detail]           |
|                                          |
| ── 选择型配置 (仅select) ──                |
| 选项来源: [radio: 静态配置 / 动态获取]        |
|   静态 → 选项列表:                         |
|     [key][label][-]                      |
|     [+ 添加选项]                           |
|   动态 → 上游参数名: [input]               |
|                                          |
| ── 表单型配置 (仅form) ──                  |
| 字段列表:                                  |
|   [key][label][type▼][required☑][-]      |
|   [+ 添加字段]                             |
|   (type=select时可展开子选项)               |
|                                          |
| 超时时间                                   |
| [number: timeoutMinutes] 分钟             |
+------------------------------------------+
```

## 实现要点

### 1. computed 初始化（参考OpenPageNodeProperty模式）

```javascript
computed: {
  nodeConfig () {
    const config = this.wfNode.nodeConfig
    if (!config.tip) this.$set(config, 'tip', '')
    if (!config.interactionType) this.$set(config, 'interactionType', 'text')
    if (config.timeoutMinutes === undefined) this.$set(config, 'timeoutMinutes', 30)
    // confirm
    if (config.confirmText === undefined) this.$set(config, 'confirmText', '确认')
    if (config.rejectText === undefined) this.$set(config, 'rejectText', '驳回')
    if (config.detail === undefined) this.$set(config, 'detail', '')
    // select
    if (config.optionsSource === undefined) this.$set(config, 'optionsSource', 'static')
    if (!config.options) this.$set(config, 'options', [])
    if (config.dynamicOptionsParam === undefined) this.$set(config, 'dynamicOptionsParam', '')
    // form
    if (!config.fields) this.$set(config, 'fields', [])
    return config
  }
}
```

### 2. 条件渲染（参考OpenPageNodeProperty的template v-if模式）

```vue
<template v-if="nodeConfig.interactionType === 'confirm'">
  <!-- confirm配置区 -->
</template>
<template v-if="nodeConfig.interactionType === 'select'">
  <!-- select配置区 -->
</template>
<template v-if="nodeConfig.interactionType === 'form'">
  <!-- form配置区 -->
</template>
```

### 3. 动态列表操作

```javascript
methods: {
  addOption () {
    this.nodeConfig.options.push({ key: '', label: '' })
  },
  removeOption (index) {
    this.nodeConfig.options.splice(index, 1)
  },
  addField () {
    this.nodeConfig.fields.push({ key: '', label: '', type: 'text', required: false })
  },
  removeField (index) {
    this.nodeConfig.fields.splice(index, 1)
  },
  handleTypeChange () {
    this.emitUpdate()
  },
  emitUpdate () {
    this.$nextTick(() => {
      this.$root.$emit('workflow:update-node', {
        nodeUuid: this.wfNode.uuid,
        nodeData: this.wfNode
      })
    })
  }
}
```

### 4. 注意：字段名使用驼峰

后端 `HumanFeedbackNodeConfig` 使用 Java 驼峰命名（`interactionType`、`confirmText`、`optionsSource`等），
前端 nodeConfig 中保存的也必须是驼峰，因为 fastjson2 默认按驼峰反序列化。

## KISS原则7问题

1. **真问题？** 是，当前编辑器只有tip，无法配置4种交互类型
2. **更简单方案？** 方案B用JSON编辑器更简单，但用户体验差，选择方案A
3. **破坏什么？** 零破坏，向后兼容（tip字段保留，interactionType默认text）
4. **真需要？** 是，后端已完成4种类型支持，前端必须跟进
5. **过度设计？** 否，严格按后端字段1:1对应
6. **幻觉？** 否，所有字段来自已实现的HumanFeedbackNodeConfig
7. **注意事项？** 驼峰命名对齐后端；$set初始化响应式；emitUpdate通知X6

## 风险分析

| 风险 | 级别 | 缓解措施 |
|------|------|---------|
| nodeConfig字段名与后端不匹配 | 低 | 严格按HumanFeedbackNodeConfig驼峰字段名 |
| Vue2响应式失效 | 低 | 所有新字段用$set初始化 |
| 老配置数据兼容 | 低 | 所有字段都有默认值，getEffectiveInteractionType()兜底 |
