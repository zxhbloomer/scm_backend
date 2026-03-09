# 条件分支节点自定义名称 - 设计文档

**日期**: 2026-03-07
**状态**: 已确认

## 需求

条件分支节点（SwitcherNode）的分支名称支持手工修改，包括：
- 普通分支情况（分支情况1、分支情况2...）
- 默认分支

## 方案

方案A：在数据模型中加 `name` 字段，随工作流 JSON 一起持久化。

## 数据模型变更

### 后端

**SwitcherCase.java** — 加1个字段：
```java
private String name;  // 分支名称，可为空，为空时前端降级显示"分支情况N"
```

**SwitcherNodeConfig.java** — 加1个字段：
```java
@JsonProperty("default_case_name")
private String defaultCaseName;  // 默认分支名称，可为空，为空时显示"默认分支"
```

### 数据库

零变更。`node_config` 是 `ai_workflow_node` 表的 TEXT 列，存储整个节点配置的 JSON 字符串，新字段自动包含在 JSON 中。

## 前端变更

### SwitcherNodeProperty.vue（属性面板）

1. 分支情况标题：静态文字改为内联 `el-input`
   - `v-model="wfCase.name"`
   - `placeholder="'分支情况' + (idx + 1)"`
   - 需要阻止 `@click.stop` 防止触发 collapse 展开/收起

2. 默认分支标题：同样改为内联 `el-input`
   - `v-model="nodeConfig.default_case_name"`
   - `placeholder="默认分支"`

### SwitcherNode.vue（画布节点显示）

1. 分支标题：`{{ wfCase.name || '分支情况' + (idx + 1) }}`
2. 默认分支：`{{ node.nodeConfig.default_case_name || '默认分支' }}`

### handleAddCase()

新增分支时初始化 `name: ''`。

## 向后兼容

老数据 `name` 为 `null`，前端用 `||` 降级显示，完全兼容，无需数据迁移。

## 影响范围

| 文件 | 改动量 |
|------|--------|
| `SwitcherCase.java` | +1行 |
| `SwitcherNodeConfig.java` | +2行 |
| `SwitcherNodeProperty.vue` | 修改2处标题区域 |
| `SwitcherNode.vue` | 修改2处显示逻辑 |
| 数据库 | 零变更 |
