# 条件分支节点自定义名称 实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 让条件分支节点的分支名称（包括默认分支）支持手工修改并持久化到数据库。

**Architecture:** 在 SwitcherCase 和 SwitcherNodeConfig 各加一个 name 字段，随工作流 JSON 整体存入 ai_workflow_node.node_config（TEXT列），前端属性面板改为内联 input 编辑，画布节点显示时优先用自定义名称降级到序号名称。

**Tech Stack:** Java 17 + Lombok、Vue 2.7 + Element UI、Fastjson2（JSON序列化）

---

### Task 1: 后端 — SwitcherCase.java 加 name 字段

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/switcher/SwitcherCase.java`

**Step 1: 在 uuid 字段下方加一行**

```java
private String uuid;
private String name;   // 分支名称，可为空
private String operator;
```

完整文件改动后：
```java
@Data
public class SwitcherCase {

    private String uuid;
    private String name;   // 分支名称，可为空，前端降级显示"分支情况N"
    private String operator;
    private List<Condition> conditions;
    @JsonProperty("target_node_uuid")
    private String targetNodeUuid;

    @Data
    public static class Condition {
        private String uuid;
        @JsonProperty("node_uuid")
        private String nodeUuid;
        @JsonProperty("node_param_name")
        private String nodeParamName;
        private String operator;
        private String value;
    }
}
```

**Step 2: 确认后端不需要其他改动**

SwitcherNode.java 执行逻辑只用 uuid/operator/conditions，name 字段对执行无影响，无需修改。

**Step 3: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/switcher/SwitcherCase.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): SwitcherCase加name字段支持自定义分支名称"
```

---

### Task 2: 后端 — SwitcherNodeConfig.java 加 defaultCaseName 字段

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/switcher/SwitcherNodeConfig.java`

**Step 1: 在 defaultTargetNodeUuid 字段下方加字段**

```java
@JsonProperty("default_target_node_uuid")
private String defaultTargetNodeUuid;

@JsonProperty("default_case_name")
private String defaultCaseName;   // 默认分支名称，可为空，前端降级显示"默认分支"
```

**Step 2: Commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/switcher/SwitcherNodeConfig.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): SwitcherNodeConfig加defaultCaseName字段"
```

---

### Task 3: 前端 — SwitcherNodeProperty.vue 属性面板支持编辑名称

**Files:**
- Modify: `src/components/70_ai/components/workflow/components/properties/SwitcherNodeProperty.vue`

**Step 1: 修改分支情况标题区域（第11-23行）**

原来：
```html
<template slot="title">
  <div class="case-title-wrapper">
    <span class="case-title-text">分支情况{{ idx + 1 }}</span>
    <el-button ... />
  </div>
</template>
```

改为：
```html
<template slot="title">
  <div class="case-title-wrapper">
    <el-input
      v-model="wfCase.name"
      :placeholder="'分支情况' + (idx + 1)"
      size="mini"
      class="case-title-input"
      @click.native.stop
    />
    <el-button ... />
  </div>
</template>
```

注意：`@click.native.stop` 阻止点击 input 时触发 el-collapse 的展开/收起。

**Step 2: 修改默认分支标题区域（第127-130行）**

原来：
```html
<template slot="title">
  <span style="padding-left: 8px;">默认分支</span>
</template>
```

改为：
```html
<template slot="title">
  <div class="case-title-wrapper" style="padding-left: 8px;">
    <el-input
      v-model="nodeConfig.default_case_name"
      placeholder="默认分支"
      size="mini"
      class="case-title-input"
      @click.native.stop
    />
  </div>
</template>
```

**Step 3: 在 style 中加 case-title-input 样式**

在 `.case-title-text` 样式块附近加：
```scss
.case-title-input {
  flex: 1;
  margin-right: 8px;

  ::v-deep .el-input__inner {
    height: 24px;
    line-height: 24px;
    padding: 0 8px;
    font-size: 13px;
    font-weight: 500;
    border: 1px solid transparent;
    background: transparent;

    &:hover {
      border-color: #dcdfe6;
      background: #fff;
    }

    &:focus {
      border-color: #409eff;
      background: #fff;
    }
  }
}
```

这样平时看起来像普通文字，hover/focus 才显示边框，体验更自然。

**Step 4: handleAddCase() 初始化 name 字段（第344行附近）**

原来：
```js
const newCase = {
  uuid: uuid,
  operator: 'and',
  target_node_uuid: '',
  conditions: [...]
}
```

改为：
```js
const newCase = {
  uuid: uuid,
  name: '',
  operator: 'and',
  target_node_uuid: '',
  conditions: [...]
}
```

**Step 5: Commit**

```bash
git -C 01_scm_frontend/scm_frontend add src/components/70_ai/components/workflow/components/properties/SwitcherNodeProperty.vue
git -C 01_scm_frontend/scm_frontend commit -m "feat(ai): 条件分支属性面板支持自定义分支名称"
```

---

### Task 4: 前端 — SwitcherNode.vue 画布节点显示自定义名称

**Files:**
- Modify: `src/components/70_ai/components/workflow/components/nodes/SwitcherNode.vue`

**Step 1: 修改分支标题显示（第16行）**

原来：
```html
<div class="case-header" :data-port-id="wfCase.uuid">
  分支情况{{ idx + 1 }}
</div>
```

改为：
```html
<div class="case-header" :data-port-id="wfCase.uuid">
  {{ wfCase.name || ('分支情况' + (idx + 1)) }}
</div>
```

**Step 2: 修改默认分支显示（第56-58行）**

原来：
```html
<div class="default-case" data-port-id="default_handle">
  默认分支
</div>
```

改为：
```html
<div class="default-case" data-port-id="default_handle">
  {{ node.nodeConfig.default_case_name || '默认分支' }}
</div>
```

**Step 3: Commit**

```bash
git -C 01_scm_frontend/scm_frontend add src/components/70_ai/components/workflow/components/nodes/SwitcherNode.vue
git -C 01_scm_frontend/scm_frontend commit -m "feat(ai): 条件分支画布节点显示自定义名称"
```

---

### Task 5: 验证

**Step 1: 手工测试流程**

1. 打开工作流设计器，找一个含条件分支节点的工作流
2. 点击条件分支节点，打开属性面板
3. 点击"分支情况1"标题区域，应出现可编辑 input
4. 输入自定义名称（如"存在多个路由"），画布节点标题应实时更新
5. 点击"默认分支"标题区域，输入自定义名称（如"直接处理"）
6. 点击保存
7. 刷新页面，重新打开工作流，确认名称已持久化
8. 新增一个分支情况，确认新分支名称为空（显示"分支情况N"）

**Step 2: 向后兼容验证**

打开一个老的工作流（保存前没有 name 字段的），确认分支名称正常显示为"分支情况1"等，不报错。
