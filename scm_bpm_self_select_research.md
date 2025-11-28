# SCM 系统 BPM 自选审批人功能调研报告

## 1. 调研背景

**用户提问**: "在本系统中,提交审批时的弹窗应该可以选择审批人的功能吧?"

**调研目的**: 确认 SCM 系统中 BPM 提交审批对话框是否已实现"自选审批人"功能

**调研对象**: `submitBpmDialog.vue` 组件

---

## 2. 核心结论

### ✅ **功能已实现**

SCM 系统的 BPM 提交对话框**已经完整实现**了自选审批人功能,包括:

1. ✅ **UI 渲染**: 自选节点显示"添加"按钮
2. ✅ **人员选择**: 点击"添加"打开人员选择器
3. ✅ **数据绑定**: 选择的人员绑定到 `process_users` 对象
4. ✅ **多选支持**: 支持单选/多选审批人配置
5. ✅ **删除功能**: 可以删除已选择的审批人
6. ⚠️ **验证逻辑**: **存在Bug** (后续详述)

---

## 3. 功能实现详解

### 3.1 节点渲染逻辑

**文件**: `submitBpmDialog.vue` - `getApprovalNode()` 方法 (line 335-388)

```javascript
getApprovalNode (processData, process) {
  var data = {
    id: process.id,
    title: process.name,
    name: '审批人',
    icon: 'el-icon-s-check',
    isEdit: false,        // ⭐ 默认不可编辑
    multiple: false,
    type: 'APPROVAL',
    approval_mode: process.props.mode,
    users: [],
    desc: ''
  }

  // 判断审批人类型
  switch (process.props.assignedType) {
    case 'SELF_SELECT':
      data.isEdit = true;                                  // ⭐ 允许编辑
      data.multiple = process.props.selfSelect.multiple || false;  // 单选/多选
      data.desc = '自选审批人';
      break;
    case 'ASSIGN_USER':
      data.users = deepcopy(process.props.assignedUser);   // 预设用户
      data.desc = '指定审批人';
      break;
    case 'SELF':
      data.users = [this.orgUserVo];                       // 发起人自己
      data.desc = '发起人自己审批';
      break;
    // ... 其他类型
  }

  processData.push(data);
}
```

**关键字段**:
- `isEdit = true`: 决定是否显示"添加"按钮
- `multiple`: 控制人员选择器的单选/多选模式
- `users = []`: 初始为空数组,等待用户选择

### 3.2 UI 模板渲染

**文件**: `submitBpmDialog.vue` - 模板部分 (line 46-72)

```vue
<div style="display: flex;">
  <!-- 显示已选择的审批人 -->
  <div
    v-for="(user, indexu) in task.users"
    :key="indexu"
    class="avatar show-y"
  >
    <div class="a-img">
      <el-avatar
        style="height: 38px; width: 38px; line-height: 38px;margin-left: 10px"
        :src="user.avatar"
      />
      <!-- ⭐ 可删除图标 (仅当 isEdit=true 时显示) -->
      <i
        v-if="task.isEdit"
        class="close el-icon-close"
        @click="delUser(task.users, user)"
      />
    </div>
    <span class="text">{{ user.name }}</span>
  </div>

  <!-- ⭐ 添加按钮 (仅当 isEdit=true 且允许添加时显示) -->
  <span
    v-if="task.isEdit && (task.multiple || 0 === task.users.length)"
    class="add-user"
    @click="selectUser(task.users, task)"
  >
    <i class="el-icon-plus" />
    <div>添加</div>
  </span>
</div>
```

**显示条件**:
- **添加按钮**: `isEdit=true` 且 (`multiple=true` 或 `users.length=0`)
- **删除图标**: `isEdit=true` 时显示

### 3.3 人员选择流程

#### 步骤1: 点击"添加"按钮

**方法**: `selectUser()` (line 685-691)

```javascript
selectUser (user, task) {
  this.selectedNode = task;                         // ⭐ 记录当前操作的节点
  this.settings.popsettings.one.visible = true;     // 打开人员选择器
  this.settings.popsettings.one.onVuexUpdate = true;
  this.settings.popsettings.one.props.data = user;
  this.settings.popsettings.one.props.multiple = task.multiple;  // ⭐ 单选/多选
}
```

#### 步骤2: 打开人员选择组件

**组件**: `SelectStaff` (line 102-113)

```vue
<SelectStaff
  v-if="settings.popsettings.one.visible"
  :visible="settings.popsettings.one.visible"
  :type="settings.popsettings.one.type"
  :multiple-choices="settings.popsettings.one.props.multiple"  <!-- ⭐ 单选/多选 -->
  :data="settings.popsettings.one.props.data"
  @emitInsertStaffOk="handleInsertStaffOk"         <!-- ⭐ 确定事件 -->
  @emitCloseCancel="handleSelectStaffCancel"       <!-- ⭐ 取消事件 -->
/>
```

#### 步骤3: 选择完成回调

**方法**: `handleInsertStaffOk()` (line 671-684)

```javascript
handleInsertStaffOk (data) {
  // 初始化 process_users 数组
  this.process_users[this.selectedNode.id] = this.process_users[this.selectedNode.id] || [];
  this.selectedNode.users = [];

  // ⭐ 遍历选择的用户
  data.forEach((user) => {
    this.selectedNode.users.push(user);              // 显示层: UI显示
    this.process_users[this.selectedNode.id].push(user);  // ⭐ 数据层: 提交数据
    this.$set(user, 'isEdit', true);                 // 标记可删除
  });

  this.handleSelectStaffCancel();  // 关闭人员选择器
}
```

**关键数据结构**:
```javascript
{
  selectedNode: {
    id: "node_003",
    users: [user1, user2],  // UI 显示用
    isEdit: true,
    multiple: true
  },
  process_users: {
    "node_003": [user1, user2]  // ⭐ 提交给后端的数据
  }
}
```

### 3.4 删除审批人

**方法**: `delUser()` (line 267-270)

```javascript
delUser (users, t) {
  users.splice(users.indexOf(t), 1);                        // 从显示列表删除
  this.process_users[this.selectedNode.id].splice(users.indexOf(t), 1);  // 从数据删除
}
```

### 3.5 提交验证和数据传递

#### 验证逻辑 (⚠️ **存在Bug**)

**方法**: `handleOk()` (line 655-665)

```javascript
handleOk () {
  // ❌ 当前验证逻辑 - 有问题!
  const ifEnd = this.processData.some((task) =>
    task.type !== 'END' && task.users.length === 0
  );

  if (ifEnd) {
    this.$message.warning('请完善表单/流程选项😥');
    this.$emit('closeMeCancel');  // 关闭父组件loading
  } else {
    this.internalVisible = false;
    // ⭐ 提交数据: processData + process_users
    this.$emit('closeMeOk', {
      processData: this.processData,
      process_users: this.process_users
    });
  }
}
```

**Bug 说明**:
- **问题**: 对所有节点都检查 `task.users.length === 0`
- **影响**: SELF_SELECT 节点的 `assignedUser` 配置为空是正常的,但会被误判为未完成
- **结果**: 即使用户选择了审批人,只要 `task.users` 为空(来自配置),就会报错

#### 提交的数据结构

**Event**: `closeMeOk` (line 663)

```javascript
{
  processData: [
    {
      id: "node_root",
      type: "ROOT",
      users: [{ id: "001", name: "张三" }]
    },
    {
      id: "node_003",
      type: "APPROVAL",
      isEdit: true,
      users: [{ id: "002", name: "李四" }],  // 显示数据
      desc: "自选审批人"
    },
    {
      type: "END",
      name: "结束"
    }
  ],
  process_users: {
    "node_003": [                         // ⭐ 实际提交数据
      { id: "002", name: "李四" }
    ]
  }
}
```

---

## 4. 与 wflow 对比分析

### 4.1 相似之处

| 功能点 | SCM 实现 | wflow 实现 | 对比 |
|-------|---------|-----------|------|
| **节点标识** | `isEdit = true` | `enableEdit = true` | ✅ 相同逻辑 |
| **单选/多选** | `multiple` 字段 | `selfSelect.multiple` | ✅ 支持 |
| **人员选择器** | `SelectStaff` 组件 | `OrgPicker` 组件 | ✅ 功能相同 |
| **数据绑定** | `process_users[nodeId]` | `processUsers[nodeId]` | ✅ 字段名不同但逻辑相同 |
| **删除功能** | `delUser()` 方法 | `delUser()` 方法 | ✅ 都支持 |

### 4.2 关键差异

| 功能点 | SCM 实现 | wflow 实现 | 问题 |
|-------|---------|-----------|------|
| **验证节点追踪** | ❌ 无 | ✅ `selectUserNodes` Set | ⚠️ SCM缺失 |
| **验证逻辑** | ❌ 检查所有节点 `users` | ✅ 只检查 `selectUserNodes` 的 `processUsers` | ⚠️ SCM有Bug |
| **错误提示** | ❌ 通用警告 | ✅ `errorShark()` 节点抖动 | ⚠️ SCM不精准 |

### 4.3 wflow 的优势实现

**wflow 的验证追踪机制**:

```javascript
// 1. 渲染时记录自选节点
case 'SELF_SELECT':
  result.enableEdit = true;
  this.selectUserNodes.add(node.id);  // ⭐ 关键: 追踪需要验证的节点
  break;

// 2. 验证时只检查自选节点
validate(call) {
  let isOk = true;

  // ⭐ 只遍历需要验证的节点
  this.selectUserNodes.forEach(nodeId => {
    if ((this._value[nodeId] || []).length === 0) {  // ⭐ 检查 processUsers
      isOk = false;
      this.$refs[nodeId].errorShark();  // 精准错误提示
    }
  });

  if (call) {
    call(isOk);
  }
}
```

---

## 5. 发现的Bug详解

### 5.1 Bug 描述

**位置**: `submitBpmDialog.vue` line 656

**当前代码**:
```javascript
const ifEnd = this.processData.some((task) =>
  task.type !== 'END' && task.users.length === 0
)
```

### 5.2 问题分析

**场景**: 采购订单审批流程

**数据库配置** (`bpm_process_templates.process`):
```json
{
  "id": "node_003",
  "type": "APPROVAL",
  "name": "部门经理审批",
  "props": {
    "assignedType": "SELF_SELECT",
    "selfSelect": { "multiple": true },
    "assignedUser": []  // ⭐ 配置为空,这是正确的!
  }
}
```

**运行时数据** (`processData`):
```javascript
{
  id: "node_003",
  type: "APPROVAL",
  isEdit: true,
  users: [],  // ⭐ 从 assignedUser 复制,为空
  desc: "自选审批人"
}
```

**用户操作**:
1. 用户点击"添加"
2. 选择审批人: 李四
3. 数据更新:
   ```javascript
   process_users["node_003"] = [{ id: "002", name: "李四" }]  // ✅ 有数据
   ```
4. 但是 `task.users` 仍然为空 ❌

**验证结果**:
```javascript
task.users.length === 0  // ❌ true (因为 users 来自 assignedUser,为空)
→ ifEnd = true
→ 显示错误: "请完善表单/流程选项😥"
```

**根本原因**:
- SELF_SELECT 节点的设计就是 `assignedUser` 为空
- 实际选择的人员在 `process_users[nodeId]` 中
- 当前验证只检查 `task.users` (来自 `assignedUser`),不检查 `process_users`

### 5.3 正确的验证逻辑

**应该实现的代码**:

```javascript
handleOk () {
  // ✅ 正确验证: 区分节点类型
  const ifEnd = this.processData.some((task) => {
    // 排除结束节点
    if (task.type === 'END') return false;

    // SELF_SELECT 节点: 检查 process_users
    if (task.props?.assignedType === 'SELF_SELECT') {
      return !this.process_users[task.id] ||
             this.process_users[task.id].length === 0;
    }

    // 其他节点: 检查 users (来自预配置)
    return task.users.length === 0;
  });

  if (ifEnd) {
    this.$message.warning('请完善表单/流程选项😥');
    this.$emit('closeMeCancel');
  } else {
    this.internalVisible = false;
    this.$emit('closeMeOk', {
      processData: this.processData,
      process_users: this.process_users
    });
  }
}
```

**为什么需要 `task.props`?**

当前 `processData` 中的节点**没有保存** `props` 信息,需要修改 `getApprovalNode()`:

```javascript
getApprovalNode (processData, process) {
  var data = {
    id: process.id,
    title: process.name,
    name: '审批人',
    icon: 'el-icon-s-check',
    isEdit: false,
    multiple: false,
    type: 'APPROVAL',
    approval_mode: process.props.mode,
    props: process.props,  // ⭐ 添加这一行,保存完整配置
    users: [],
    desc: ''
  }
  // ...
}
```

---

## 6. 完整功能流程图

```
┌─────────────────────────────────────────────────────────────┐
│ 1. 加载流程配置                                             │
│    getFlowProcessApi({ serial_type }) → 从数据库获取流程    │
│    process.props.assignedType = 'SELF_SELECT'               │
│    process.props.assignedUser = []  ✅ 配置为空             │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 2. 渲染流程节点 (getApprovalNode)                          │
│    case 'SELF_SELECT':                                       │
│      data.isEdit = true          ⭐ 显示"添加"按钮          │
│      data.multiple = true/false  ⭐ 单选/多选配置            │
│      data.users = []             ⭐ 初始为空                 │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 3. UI渲染 (template)                                        │
│    v-if="task.isEdit" → 显示"添加"按钮                      │
│    v-for="user in task.users" → 显示已选人员                │
│    @click="selectUser(task.users, task)" → 点击添加触发     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 4. 打开人员选择器 (selectUser)                              │
│    this.selectedNode = task                                  │
│    settings.popsettings.one.visible = true                   │
│    settings.popsettings.one.props.multiple = task.multiple   │
│    → 打开 <SelectStaff> 组件                                 │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 5. 用户选择审批人                                           │
│    SelectStaff 组件 → 显示人员树/列表                       │
│    用户勾选: 李四, 王五                                      │
│    点击"确定" → @emitInsertStaffOk                           │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 6. 处理选择结果 (handleInsertStaffOk)                       │
│    data = [user1, user2]                                     │
│    ↓                                                         │
│    this.selectedNode.users = [user1, user2]  ⭐ UI显示      │
│    this.process_users[nodeId] = [user1, user2]  ⭐ 提交数据 │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 7. 用户点击"确定" (handleOk)                                │
│    ❌ 当前验证: task.users.length === 0                     │
│       → 误判为空 (users来自assignedUser,配置为空)          │
│    ✅ 应该验证: process_users[task.id].length === 0        │
│       → 正确检查用户选择的数据                              │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│ 8. 提交数据 (@closeMeOk)                                    │
│    {                                                         │
│      processData: [...],                                     │
│      process_users: {                                        │
│        "node_003": [user1, user2]  ⭐ 后端需要这个数据      │
│      }                                                        │
│    }                                                         │
└─────────────────────────────────────────────────────────────┘
```

---

## 7. 后端处理确认

基于之前对 wflow 的深度学习,SCM 后端应该也有类似的处理逻辑:

**预期后端流程**:
1. 接收 `process_users` 参数
2. 合并到 Flowable 流程变量: `processVar.putAll(process_users)`
3. Flowable 引擎创建任务时调用服务获取审批人
4. 服务从流程变量中获取: `execution.getVariable(nodeId, List.class)`
5. 创建多个 UserTask 实例

**需要确认**: SCM 后端是否已经实现了这套机制

---

## 8. 总结

### 8.1 功能现状

| 功能模块 | 实现状态 | 说明 |
|---------|---------|------|
| **UI渲染** | ✅ 完整实现 | 根据 `isEdit` 显示添加按钮 |
| **人员选择** | ✅ 完整实现 | `SelectStaff` 组件支持单选/多选 |
| **数据绑定** | ✅ 完整实现 | `process_users` 数据结构正确 |
| **删除功能** | ✅ 完整实现 | 可删除已选审批人 |
| **验证逻辑** | ❌ **存在Bug** | 误判 SELF_SELECT 节点为空 |
| **错误提示** | ⚠️ 不够精准 | 通用警告,无节点级错误提示 |

### 8.2 核心问题

**验证Bug** (line 656):
```javascript
// ❌ 错误
const ifEnd = this.processData.some((task) =>
  task.type !== 'END' && task.users.length === 0
)

// ✅ 正确
const ifEnd = this.processData.some((task) => {
  if (task.type === 'END') return false;

  if (task.props?.assignedType === 'SELF_SELECT') {
    return !this.process_users[task.id] ||
           this.process_users[task.id].length === 0;
  }

  return task.users.length === 0;
});
```

### 8.3 建议改进

1. **修复验证逻辑** (高优先级)
   - 区分 SELF_SELECT 节点和其他节点
   - 检查 `process_users` 而非 `users`

2. **添加节点追踪** (可选)
   - 参考 wflow,添加 `selectUserNodes` Set
   - 只验证需要自选的节点

3. **精准错误提示** (可选)
   - 显示具体哪个节点未选择审批人
   - 节点抖动或高亮提示

4. **保存节点配置** (必需,用于验证)
   - 在 `getApprovalNode()` 中添加 `props: process.props`
   - 验证时才能判断 `assignedType`

---

## 9. 参考资料

### 9.1 相关文件

- **前端**: `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\components\60_bpm\submitBpmDialog.vue`
- **wflow研究**: `D:\2025_project\20_project_in_github\00_scm_backend\scm_backend\wflow_self_select_research.md`

### 9.2 关键代码位置

| 功能 | 行号 | 说明 |
|------|-----|------|
| 节点渲染 | 335-388 | `getApprovalNode()` |
| UI模板 | 46-72 | 显示审批人和添加按钮 |
| 打开选择器 | 685-691 | `selectUser()` |
| 选择回调 | 671-684 | `handleInsertStaffOk()` |
| 删除审批人 | 267-270 | `delUser()` |
| **验证逻辑 (Bug)** | **656** | **`handleOk()`** |
| 提交数据 | 663 | `closeMeOk` event |

---

**文档版本**: v1.0
**创建日期**: 2025-11-28
**调研人员**: Claude Code Research Agent
