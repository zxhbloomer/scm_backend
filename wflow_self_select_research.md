# wflow 发起人自选审批逻辑研究文档

## 1. 概述

本文档详细研究了 wflow-pro 工作流系统中"发起人自选审批人"功能的实现机制,包括前端设计界面配置、流程渲染、提交验证以及后端数据结构处理。

**核心功能**: 允许流程发起人在提交审批时动态选择审批人,而非使用预先配置的固定审批人。

## 2. 系统架构

### 2.1 技术栈
- **后端**: Spring Boot 2.2.3 + Flowable 6.7.2 + MyBatis Plus
- **前端**: Vue.js 2.6.11 + Element UI 2.15.8 + Vuex

### 2.2 核心模块
```
wflow-pro-back-end/
├── workflow/bean/process/props/ApprovalProps.java    # 审批节点属性定义
├── workflow/bean/vo/ProcessStartParamsVo.java        # 流程启动参数VO
└── workflow/controller/ProcessInstanceController.java # 流程实例控制器

wflow-pro-front-end/
├── views/common/process/config/ApprovalNodeConfig.vue # 审批节点配置UI
├── views/process/ProcessRender.vue                    # 流程运行时渲染
└── views/workspace/InitiateProcess.vue                # 发起流程页面
```

## 3. 后端设计

### 3.1 审批节点属性定义

**文件**: `ApprovalProps.java`

#### 核心枚举 - ApprovalTypeEnum

```java
public enum ApprovalTypeEnum {
    ASSIGN_USER,    // 指定人员
    SELF_SELECT,    // 发起人自选 ⭐
    SELF,           // 发起人自己
    LEADER,         // 部门主管
    LEADER_TOP,     // 多级部门主管
    ASSIGN_LEADER,  // 指定部门的主管
    ROLE,           // 系统角色
    FORM_USER,      // 表单内联系人
    FORM_DEPT,      // 表单内部门主管
    REFUSE          // 系统自动拒绝
}
```

#### SelfSelect 配置类

```java
@Data
public static class SelfSelect implements Serializable {
    private static final long serialVersionUID = -45475579271153023L;
    private boolean multiple;  // 是否支持多选审批人
}
```

**关键点**:
- `multiple = false`: 自选一个审批人
- `multiple = true`: 自选多个审批人

#### 完整节点属性结构

```java
@Data
public class ApprovalProps implements Serializable {
    private ApprovalTypeEnum assignedType;  // 审批人分配类型
    private ApprovalModeEnum mode;          // 多人审批模式(会签/或签)
    private boolean sign;                   // 是否需要签字
    private Nobody nobody;                  // 审批人为空时的处理
    private TimeLimit timeLimit;            // 审批期限设置
    private List<OrgUser> assignedUser;     // 指定审批人列表
    private SelfSelect selfSelect;          // 自选配置 ⭐
    private Leader leader;                  // 主管级别配置
    private List<OrgUser> role;             // 角色列表
    private String formUser;                // 表单联系人字段ID
    private Refuse refuse;                  // 驳回配置
    private List<FormPerm> formPerms;       // 表单字段权限
    private OperationPerm operationPerm;    // 操作权限
}
```

### 3.2 流程启动参数

**文件**: `ProcessStartParamsVo.java`

```java
@Data
public class ProcessStartParamsVo {
    private String deptId;                              // 发起部门ID
    private Map<String, Object> formData;               // 表单数据 (字段ID -> 值)
    private Map<String, List<OrgUser>> processUsers;    // 节点ID -> 用户列表 ⭐
}
```

**processUsers 结构示例**:
```json
{
  "node_898778765353": [
    {
      "id": "user_001",
      "name": "张三",
      "type": "user"
    },
    {
      "id": "user_002",
      "name": "李四",
      "type": "user"
    }
  ]
}
```

**关键点**:
- `processUsers` 是一个 Map,key 是节点ID,value 是该节点的审批人列表
- 对于 `SELF_SELECT` 类型的审批节点,发起人必须在 `processUsers` 中填充审批人
- 后端启动流程时会从 `processUsers` 中提取对应节点的审批人信息

### 3.3 流程启动接口

**文件**: `ProcessInstanceController.java`

```java
@PostMapping("start/{defId}")
public Object startTheProcess(@PathVariable String defId,
                              @RequestBody ProcessStartParamsVo params) {
    String instanceId = processService.startProcess(defId, params);
    return R.ok("启动流程实例 " + instanceId + " 成功");
}
```

**流程**:
1. 接收流程定义ID (`defId`) 和启动参数 (`ProcessStartParamsVo`)
2. 调用 `processService.startProcess()` 启动流程
3. 服务层会解析流程定义,根据节点配置和 `processUsers` 分配审批任务

## 4. 前端设计

### 4.1 流程设计 - 节点配置

**文件**: `ApprovalNodeConfig.vue`

#### 审批类型选择

```vue
<el-radio-group v-model="nodeProps.assignedType">
  <el-radio v-for="t in approvalTypes" :label="t.type" :key="t.type">
    {{ t.name }}
  </el-radio>
</el-radio-group>
```

#### SELF_SELECT 配置UI

```vue
<div v-else-if="nodeProps.assignedType === 'SELF_SELECT'">
  <el-radio-group size="mini" v-model="nodeProps.selfSelect.multiple">
    <el-radio-button :label="false">自选一个人</el-radio-button>
    <el-radio-button :label="true">自选多个人</el-radio-button>
  </el-radio-group>
</div>
```

**配置项**:
- `assignedType = 'SELF_SELECT'`: 标记为发起人自选类型
- `selfSelect.multiple = false`: 单选模式
- `selfSelect.multiple = true`: 多选模式

#### 多人审批模式配置

```vue
<div v-if="showMode">
  <el-form-item label="👩‍👦‍👦 多人审批时审批方式">
    <el-radio-group v-model="nodeProps.mode">
      <el-radio label="NEXT">会签 (按选择顺序审批,须全部同意)</el-radio>
      <el-radio label="AND">会签 (可同时审批,须全部同意)</el-radio>
      <el-radio label="OR">或签 (有一人同意即可)</el-radio>
    </el-radio-group>
  </el-form-item>
</div>
```

**computed 计算逻辑**:
```javascript
showMode() {
  switch (this.nodeProps.assignedType) {
    case "SELF_SELECT":
      return this.nodeProps.selfSelect.multiple;  // 只有多选时显示
    // ... 其他类型
  }
}
```

### 4.2 流程运行时 - 节点渲染

**文件**: `ProcessRender.vue`

#### 核心数据结构

```javascript
data() {
  return {
    selectUserNodes: new Set(),    // 记录所有需要自选的节点ID ⭐
    processTasks: [],              // 渲染的流程任务列表
    _value: {},                    // processUsers (nodeId -> users)
    userCatch: {},                 // 用户缓存
    conditionFormItem: new Set()   // 条件表单项
  }
}
```

#### SELF_SELECT 节点渲染逻辑

**位置**: `getApprovalNode()` 方法

```javascript
getApprovalNode(node) {
  let result = {
    id: node.id,
    title: node.name,
    name: '审批人',
    icon: 'el-icon-s-check',
    enableEdit: false,    // 是否可编辑
    multiple: false,      // 是否多选
    mode: node.props.mode,
    users: [],           // 审批人列表
    desc: ''             // 描述文本
  }

  switch (node.props.assignedType) {
    case 'SELF_SELECT':
      result.enableEdit = true;                              // ⭐ 允许编辑
      this.selectUserNodes.add(node.id);                     // ⭐ 记录自选节点
      result.multiple = node.props.selfSelect.multiple || false;
      result.desc = '自选审批人';
      break;
    case 'ASSIGN_USER':
      result.users = this.$deepCopy(node.props.assignedUser);
      result.desc = '指定审批人';
      break;
    // ... 其他类型
  }

  // 如果已选择过审批人,从缓存恢复
  if (this.userCatch[node.id] && this.userCatch[node.id].length > 0) {
    result.users = this.userCatch[node.id];
  }

  return result;
}
```

**关键点**:
1. `enableEdit = true`: 标记该节点可以添加/删除审批人
2. `selectUserNodes.add(node.id)`: 将节点ID加入自选节点集合,用于后续验证
3. `multiple`: 控制人员选择器是单选还是多选

#### 用户选择与绑定

```javascript
// 添加审批人 (触发人员选择器)
addUser(node) {
  this.selectedNode = node;
  this.$refs.orgPicker.show();
}

// 确认选择的人员
selected(users) {
  this._value[this.selectedNode.id] = [];  // ⭐ 更新 processUsers
  users.forEach(u => {
    if (this.selectedNode.users.findIndex(v => v.id === u.id) === -1) {
      this.$set(u, 'enableEdit', true);
      this.selectedNode.users.push(u);      // 显示在界面
      this._value[this.selectedNode.id].push(u);  // 绑定到数据模型
    }
  })
}

// 删除审批人
delUser(nodeId, i) {
  this._value[nodeId].splice(i, 1);  // ⭐ 从 processUsers 移除
}
```

#### 提交前验证

```javascript
validate(call) {
  let isOk = true;
  this.selectUserNodes.forEach(nodeId => {
    if ((this._value[nodeId] || []).length === 0) {  // ⭐ 检查自选节点是否有审批人
      isOk = false;
      this.$refs[nodeId].errorShark();  // 显示错误抖动
    }
  })
  if (call) {
    call(isOk);
  }
}
```

**验证逻辑**:
1. 遍历所有 `SELF_SELECT` 节点 (`selectUserNodes`)
2. 检查 `processUsers[nodeId]` 是否为空
3. 如果为空,显示错误提示并返回 `false`
4. 只有所有自选节点都有审批人,验证才通过

### 4.3 流程发起页面

**文件**: `InitiateProcess.vue`

#### 核心结构

```vue
<template>
  <div>
    <el-row :gutter="20">
      <el-col :span="15">
        <!-- 表单渲染 -->
        <form-render ref="form" v-model="formData"/>
      </el-col>
      <el-col :span="9">
        <!-- 流程渲染 ⭐ -->
        <process-render
          ref="process"
          v-model="processUsers"    <!-- 双向绑定 processUsers -->
          :forms="forms"
          :formData="formData"
          :process="process"
          :dept-id="userDeptId"/>
      </el-col>
    </el-row>
  </div>
</template>
```

#### 提交流程

```javascript
methods: {
  // 验证表单和流程
  validate(call) {
    this.$refs.form.validate(validForm => {
      this.$refs.process.validate(validProcess => {  // ⭐ 验证流程节点
        call(validForm, validProcess);
      });
    });
  },

  // 提交审批
  submit() {
    let startParams = {
      deptId: this.userDeptId,
      formData: this.formData,
      processUsers: this.processUsers  // ⭐ 包含自选的审批人
    };

    startProcess(this.form.processDefId, startParams).then(rsp => {
      this.$message.success(rsp.data);
      this.$emit('ok');
    }).catch(err => {
      this.$emit('fail');
      this.$err(err, '发起审批失败');
    })
  }
}
```

**数据流**:
```
ProcessRender (选择审批人)
    ↓ (v-model双向绑定)
InitiateProcess.processUsers
    ↓ (submit提交)
Backend ProcessStartParamsVo.processUsers
    ↓ (流程引擎解析)
Flowable 任务分配
```

## 5. 完整业务流程

### 5.1 流程设计阶段

1. **打开流程设计器**
   - 访问 `/admin/FormProcessDesign`
   - 拖拽添加审批节点

2. **配置审批节点**
   - 点击审批节点打开配置面板 (`ApprovalNodeConfig.vue`)
   - 选择"发起人自选"类型: `assignedType = 'SELF_SELECT'`
   - 配置单选/多选: `selfSelect.multiple = true/false`
   - 配置多人审批模式(如果多选): `mode = 'AND'/'OR'/'NEXT'`

3. **保存流程定义**
   - 流程JSON结构:
   ```json
   {
     "id": "node_898778765353",
     "type": "APPROVAL",
     "name": "部门审批",
     "props": {
       "assignedType": "SELF_SELECT",
       "selfSelect": {
         "multiple": true
       },
       "mode": "AND",
       "assignedUser": []  // 空数组,等待发起时填充
     }
   }
   ```

### 5.2 流程发起阶段

1. **打开发起页面**
   - 访问 `/workspace/InitiateProcess?code={modelCode}`
   - 加载流程模型和表单配置

2. **渲染流程预览**
   - `ProcessRender.loadProcessRender()` 递归解析流程节点
   - 遇到 `SELF_SELECT` 节点:
     - 设置 `enableEdit = true`
     - 添加到 `selectUserNodes` 集合
     - 显示"自选审批人"占位提示

3. **选择审批人**
   - 点击"添加审批人"按钮
   - 弹出 `OrgPicker` 组织人员选择器
   - 选择人员后,调用 `selected()` 方法:
     - 更新 `processTasks[i].users` (界面显示)
     - 更新 `processUsers[nodeId]` (数据模型)

4. **提交验证**
   - 填写表单数据
   - 点击提交
   - `validate()` 方法检查:
     - 表单验证: `this.$refs.form.validate()`
     - 流程验证: `this.$refs.process.validate()`
       - 遍历 `selectUserNodes`
       - 检查 `processUsers[nodeId]` 是否为空
       - 空则显示错误,阻止提交

5. **后端处理**
   - 发送 POST 请求到 `/wflow/process/start/{defId}`
   - 请求体:
   ```json
   {
     "deptId": "dept_001",
     "formData": {
       "field_001": "采购申请",
       "field_002": 50000
     },
     "processUsers": {
       "node_898778765353": [
         {"id": "user_001", "name": "张三", "type": "user"},
         {"id": "user_002", "name": "李四", "type": "user"}
       ]
     }
   }
   ```

6. **Flowable引擎分配任务**
   - 解析流程定义
   - 遇到 `SELF_SELECT` 节点:
     - 从 `processUsers[nodeId]` 获取审批人
     - 创建对应数量的审批任务
     - 分配给选中的用户

## 6. 与 SCM 项目对比分析

### 6.1 SCM 当前实现

**数据库配置** (查询结果):
```json
{
  "type": "b_po_project",
  "name": "采购项目管理审批流",
  "process": {
    "id": "root",
    "type": "ROOT",
    "props": {
      "assignedUser": []  // ROOT节点为空
    },
    "children": {
      "id": "node_898778765353",
      "type": "APPROVAL",
      "props": {
        "assignedType": "SELF_SELECT",
        "assignedUser": [],
        "selfSelect": {"multiple": true}
      }
    }
  }
}
```

**前端验证逻辑** (`submitBpmDialog.vue`):
```javascript
handleOk() {
  const ifEnd = this.processData.some(task =>
    task.type !== 'END' && task.users.length === 0
  );
  if (ifEnd) {
    this.$message.warning('请完善表单/流程选项😥');
    this.$emit('closeMeCancel');  // 已修复loading问题
  } else {
    this.internalVisible = false;
    this.$emit('closeMeOk', {
      processData: this.processData,
      process_users: this.process_users
    });
  }
}
```

### 6.2 问题根源

**wflow 的设计理念**:
- `SELF_SELECT` 节点的 `assignedUser` 在设计时**故意为空**
- 发起时,前端会弹出人员选择器,让发起人选择审批人
- 选择后的审批人填充到 `processUsers[nodeId]`
- 验证时检查 `processUsers[nodeId]` 而不是 `assignedUser`

**SCM 的问题**:
- 前端验证逻辑检查 `task.users.length === 0`
- 但 `SELF_SELECT` 节点的 users 本来就应该是空的
- 缺少人员选择器的弹出逻辑
- 没有实现 `processUsers` 的填充和验证

### 6.3 解决方案建议

#### 方案一: 完整实现 SELF_SELECT 功能

1. **前端修改** (`submitBpmDialog.vue`):

```javascript
// 修改验证逻辑
handleOk() {
  // 区分 SELF_SELECT 和其他类型
  const ifEnd = this.processData.some(task => {
    if (task.type === 'END') return false;

    // SELF_SELECT 检查 process_users
    if (task.assignedType === 'SELF_SELECT') {
      return !this.process_users[task.id] ||
             this.process_users[task.id].length === 0;
    }

    // 其他类型检查 users
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

// 添加选择审批人方法
selectApprover(task) {
  // 弹出人员选择器
  this.$refs.userPicker.show({
    multiple: task.selfSelect?.multiple || false,
    onConfirm: (users) => {
      this.$set(this.process_users, task.id, users);
    }
  });
}
```

2. **添加人员选择器触发按钮**:

```vue
<div v-if="task.assignedType === 'SELF_SELECT'">
  <el-button
    size="mini"
    type="primary"
    @click="selectApprover(task)">
    选择审批人
  </el-button>
  <div v-if="process_users[task.id]">
    已选: {{ process_users[task.id].map(u => u.name).join(',') }}
  </div>
</div>
```

#### 方案二: 改为固定审批人

如果不需要发起人自选功能,直接修改数据库配置:

```sql
-- 将 assignedType 改为 ASSIGN_USER
-- 预设审批人列表
UPDATE bpm_process_templates
SET process = JSON_SET(
  process,
  '$.children.props.assignedType', 'ASSIGN_USER',
  '$.children.props.assignedUser', JSON_ARRAY(
    JSON_OBJECT('id', 'user_001', 'name', '张三', 'type', 'user'),
    JSON_OBJECT('id', 'user_002', 'name', '李四', 'type', 'user')
  )
)
WHERE type = 'b_po_project';
```

#### 方案三: 使用其他审批类型

根据业务需求选择合适的类型:

- `LEADER`: 发起人的直接主管
- `LEADER_TOP`: 连续多级主管
- `ROLE`: 指定角色的所有用户
- `FORM_USER`: 从表单字段获取审批人

## 7. wflow 核心优势

### 7.1 灵活的审批人分配策略

| 类型 | 说明 | 使用场景 |
|------|------|----------|
| ASSIGN_USER | 固定指定人员 | 固定审批人的流程 |
| **SELF_SELECT** | 发起人选择 | 灵活的临时审批 |
| SELF | 发起人自己 | 自我审批/确认 |
| LEADER | 指定级别主管 | 层级审批 |
| LEADER_TOP | 连续多级主管 | 逐级上报 |
| ROLE | 系统角色 | 角色审批 |
| FORM_USER | 表单联系人 | 动态审批人 |

### 7.2 完善的验证机制

```javascript
// wflow 的验证逻辑
validate(call) {
  let isOk = true;

  // 只验证需要自选的节点
  this.selectUserNodes.forEach(nodeId => {
    if ((this._value[nodeId] || []).length === 0) {
      isOk = false;
      this.$refs[nodeId].errorShark();  // 精准错误提示
    }
  });

  if (call) {
    call(isOk);
  }
}
```

**优势**:
- 针对性验证: 只验证 `SELF_SELECT` 节点
- 精准错误提示: 通过 `errorShark()` 显示具体节点错误
- 分离关注点: 不影响其他类型节点的验证

### 7.3 数据结构设计

**processUsers 的优势**:
```javascript
{
  "node_001": [user1, user2],  // 自选节点的审批人
  "node_002": [user3],         // 可添加抄送人的节点
  // 其他节点不在这里,使用预定义配置
}
```

- 只存储需要动态选择的节点
- 节省数据传输量
- 清晰的职责划分

## 8. 实现建议

### 8.1 参考 wflow 的关键代码

**必须复用的核心逻辑**:

1. **节点渲染时的标记**:
```javascript
case 'SELF_SELECT':
  result.enableEdit = true;
  this.selectUserNodes.add(node.id);  // ⭐ 关键
  result.multiple = node.props.selfSelect.multiple;
  break;
```

2. **人员选择后的绑定**:
```javascript
selected(users) {
  this._value[this.selectedNode.id] = [];
  users.forEach(u => {
    this.selectedNode.users.push(u);           // 显示
    this._value[this.selectedNode.id].push(u);  // 数据绑定
  });
}
```

3. **提交前的验证**:
```javascript
validate(call) {
  let isOk = true;
  this.selectUserNodes.forEach(nodeId => {
    if ((this._value[nodeId] || []).length === 0) {
      isOk = false;
      // 显示错误
    }
  });
  call(isOk);
}
```

### 8.2 SCM 项目改造步骤

1. **后端准备** (可能不需要改,如果已支持 processUsers)
   - 确认 `ProcessStartParamsVo` 有 `processUsers` 字段
   - 确认后端能正确解析和分配

2. **前端 submitBpmDialog.vue**:
   - 添加 `selectUserNodes` 集合追踪自选节点
   - 渲染时识别 `SELF_SELECT` 类型并标记
   - 添加人员选择器弹出逻辑
   - 修改验证逻辑区分不同类型
   - 提交时包含 `process_users` 数据

3. **测试验证**:
   - 测试单选审批人
   - 测试多选审批人
   - 测试验证逻辑
   - 测试后端流程启动

## 9. 完整技术流程深度分析

### 9.1 前端完整数据流

#### 步骤1: 流程加载和节点渲染

**文件**: `ProcessRender.vue` - `getApprovalNode()` 方法 (line 162-245)

```javascript
getApprovalNode(node) {
  let result = {
    id: node.id,
    title: node.name,
    name: '审批人',
    icon: 'el-icon-s-check',
    enableEdit: false,      // ⭐ 默认不可编辑
    multiple: false,
    mode: node.props.mode,
    users: [],
    desc: ''
  }

  switch (node.props.assignedType) {
    case 'SELF_SELECT':
      result.enableEdit = true;                          // ⭐ 允许编辑
      this.selectUserNodes.add(node.id);                 // ⭐ 记录需要验证的节点
      result.multiple = node.props.selfSelect.multiple || false;  // 单选/多选
      result.desc = '自选审批人';
      break;
    case 'ASSIGN_USER':
      result.users = this.$deepCopy(node.props.assignedUser);  // 预设用户
      result.desc = '指定审批人';
      break;
    // ... 其他类型
  }

  // 如果之前已选择过用户(缓存),恢复显示
  if (this.userCatch[node.id] && this.userCatch[node.id].length > 0) {
    result.users = this.userCatch[node.id];
  }

  return result;
}
```

**关键点**:
1. **`enableEdit = true`**: 决定节点是否显示"添加"按钮
2. **`selectUserNodes.add(nodeId)`**: 将节点ID加入验证集合,用于提交时检查
3. **`users` 数组**: 用于UI显示,初始为空

#### 步骤2: 用户点击选择审批人

**文件**: `ProcessRender.vue` - 渲染部分 (line 94-98)

```jsx
<el-timeline-item icon={task.icon} size="large" class="task">
  <ProcessNodeRender
    ref={task.id}
    task={task}
    desc={task.desc}
    onAddUser={this.addUser}    // ⭐ 点击"添加"触发
    onDelUser={this.delUser}    // ⭐ 删除已选用户
  />
</el-timeline-item>
```

**触发方法** (line 457-460):
```javascript
addUser(node) {
  this.selectedNode = node;         // 记录当前操作的节点
  this.$refs.orgPicker.show();      // 打开人员选择器
}
```

#### 步骤3: 人员选择完成回调

**文件**: `ProcessRender.vue` - `selected()` 方法 (line 444-453)

```javascript
selected(users) {
  this._value[this.selectedNode.id] = [];  // ⭐ 清空旧数据

  users.forEach(u => {
    if (this.selectedNode.users.findIndex(v => v.id === u.id) === -1) {
      this.$set(u, 'enableEdit', true);           // 标记可删除
      this.selectedNode.users.push(u);            // ⭐ 显示层: UI显示用户
      this._value[this.selectedNode.id].push(u);  // ⭐ 数据层: 绑定到processUsers
    }
  });
}
```

**数据绑定机制** (line 51-58):
```javascript
computed: {
  _value: {
    get() {
      return this.value;  // value = processUsers (父组件传入)
    },
    set(val) {
      this.$emit('input', val);  // 触发v-model更新
    }
  }
}
```

**父组件绑定** (`InitiateProcess.vue` line 16):
```vue
<process-render
  v-model="processUsers"  // ⭐ 双向绑定到processUsers对象
  :forms="forms"
  :formData="formData"
  :process="process"
/>
```

#### 步骤4: 提交前验证

**文件**: `ProcessRender.vue` - `validate()` 方法 (line 488-501)

```javascript
validate(call) {
  let isOk = true;

  // ⭐ 只遍历SELF_SELECT类型的节点
  this.selectUserNodes.forEach(nodeId => {
    if ((this._value[nodeId] || []).length === 0) {  // ⭐ 检查processUsers[nodeId]
      isOk = false;
      this.$refs[nodeId].errorShark();  // 节点抖动提示错误
    }
  });

  if (call) {
    call(isOk);
  }
}
```

**父组件调用验证** (`InitiateProcess.vue` line 112-117):
```javascript
validate(call) {
  this.$refs.form.validate(validForm => {          // 表单验证
    this.$refs.process.validate(validProcess => {  // 流程验证 ⭐
      call(validForm, validProcess);               // 都通过才成功
    });
  });
}
```

#### 步骤5: 提交到后端

**文件**: `InitiateProcess.vue` - `submit()` 方法 (line 119-133)

```javascript
submit() {
  let startParams = {
    deptId: this.userDeptId,
    formData: this.formData,
    processUsers: this.processUsers  // ⭐ 包含自选的审批人
  };

  startProcess(this.form.processDefId, startParams).then(rsp => {
    this.$message.success(rsp.data);
    this.$emit('ok');
  }).catch(err => {
    this.$emit('fail');
    this.$err(err, '发起审批失败');
  });
}
```

**数据结构示例**:
```javascript
{
  deptId: "dept_001",
  formData: {
    "field_001": "采购单",
    "field_002": 50000,
    // ...
  },
  processUsers: {
    "node_approval_001": [  // ⭐ SELF_SELECT节点
      { id: "user_001", name: "张三", type: "user" },
      { id: "user_002", name: "李四", type: "user" }
    ]
    // 其他节点不在这里
  }
}
```

### 9.2 后端完整处理流程

#### 步骤1: 接收流程启动请求

**文件**: `ProcessInstanceController.java`

```java
@PostMapping("/start/{defId}")
public String startProcess(@PathVariable String defId,
                          @RequestBody ProcessStartParamsVo params) {
    return processInstanceService.startProcess(defId, params);
}
```

#### 步骤2: 合并processUsers到流程变量

**文件**: `ProcessInstanceServiceImpl.java` - `startProcess()` 方法 (line 100-141)

```java
@Override
@Transactional
public String startProcess(String defId, ProcessStartParamsVo params) {
    Map<String, Object> processVar = new HashMap<>();

    // 1. 合并表单数据
    processVar.putAll(params.getFormData());

    // 2. ⭐ 关键: 合并processUsers到Flowable变量
    //    Map<nodeId, List<OrgUser>> → execution variables
    processVar.putAll(params.getProcessUsers());

    // 3. 添加流程发起人信息
    String userId = UserUtil.getLoginUserId();
    String userName = orgRepositoryService.getUserById(userId).getUserName();
    String deptName = orgRepositoryService.getDeptById(params.getDeptId()).getDeptName();

    processVar.put("owner", ProcessInstanceOwnerDto.builder()
            .owner(userId)
            .ownerName(userName)
            .ownerDeptId(params.getDeptId())
            .ownerDeptName(deptName)
            .build());

    // 4. 加载节点配置到流程变量
    WflowModels wflowModels = modelsMapper.selectOne(
        new LambdaQueryWrapper<WflowModels>().eq(WflowModels::getProcessDefId, defId));
    Map<String, ProcessNode<?>> nodeMap = nodeCatchService.reloadProcessByStr(wflowModels.getProcess());
    Map<String, Object> propsMap = nodeMap.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey,
            v -> null == v.getValue().getProps() ? new HashMap<>() : v.getValue().getProps()));

    processVar.put(WflowGlobalVarDef.WFLOW_NODE_PROPS, propsMap);  // 所有节点配置
    processVar.put(WflowGlobalVarDef.WFLOW_FORMS, JSONArray.parseArray(wflowModels.getFormItems(), Form.class));
    processVar.put(WflowGlobalVarDef.INITIATOR, userId);

    // 5. 启动流程实例
    Authentication.setAuthenticatedUserId(userId);
    ProcessInstance processInstance = runtimeService.startProcessInstanceById(defId, processVar);

    // 6. 自动完成ROOT任务
    Task rootTask = taskService.createTaskQuery()
        .processInstanceId(processInstance.getProcessInstanceId()).active().singleResult();
    if (Objects.nonNull(rootTask)){
        taskService.complete(rootTask.getId());
    }

    Authentication.setAuthenticatedUserId(null);
    return processInstance.getProcessInstanceId();
}
```

**核心**: `processVar.putAll(params.getProcessUsers())` 将 `Map<nodeId, List<OrgUser>>` 存入 Flowable execution variables

#### 步骤3: BPMN转换时配置多实例

**文件**: `WFlowToBpmnCreator.java` - `createApprovalNode()` 方法 (line 396-443)

```java
// 审批-用户任务
private UserTask createApprovalNode(ProcessNode<ApprovalProps> node) {
    UserTask userTask = new UserTask();
    userTask.setName(node.getName());
    ApprovalProps props = node.getProps();

    userTask.setTaskListeners(taskListeners);

    if(ApprovalTypeEnum.SELF.equals(props.getAssignedType())){
        // 发起人自己审批,直接分配
        userTask.setAssignee("${" + WflowGlobalVarDef.INITIATOR + "}");
    } else {
        // 其他类型,使用多实例
        userTask.setAssignee("${assignee}");  // 多实例变量
        userTask.setLoopCharacteristics(createAndOrMode(node.getId(), props));  // ⭐
    }

    userTask.setId(node.getId());
    return userTask;
}

// 多人签署设置-会签/或签
private MultiInstanceLoopCharacteristics createAndOrMode(String nodeId, ApprovalProps props) {
    MultiInstanceLoopCharacteristics loopCharacteristics = new MultiInstanceLoopCharacteristics();
    loopCharacteristics.setId(IdUtil.randomUUID());
    loopCharacteristics.setElementVariable("assignee");  // 循环变量名

    // ⭐⭐⭐ 关键: 调用服务方法获取审批人列表
    loopCharacteristics.setInputDataItem("${processTaskService.getNodeApprovalUsers(execution)}");

    // 设置完成条件
    String completionCondition = "";
    switch (props.getMode()) {
        case OR:  // 或签: 任意一人通过即可
            completionCondition = "nrOfCompletedInstances >= 1";
            loopCharacteristics.setSequential(false);  // 并行
            break;
        case AND:  // 会签: 所有人都要通过
            completionCondition = "nrOfActiveInstances == 0";
            loopCharacteristics.setSequential(false);  // 并行
            break;
        case NEXT:  // 依次审批
            completionCondition = "nrOfActiveInstances == 0";
            loopCharacteristics.setSequential(true);   // 串行
            break;
    }
    loopCharacteristics.setCompletionCondition("${" + completionCondition + "}");
    return loopCharacteristics;
}
```

**Flowable多实例机制**:
- `inputDataItem`: 指定一个表达式,返回审批人ID列表
- `elementVariable`: 循环时每个审批人ID赋值给 `assignee` 变量
- `${assignee}`: UserTask的 assignee 使用这个变量,创建多个任务实例

#### 步骤4: Flowable引擎创建任务时调用服务

**Flowable引擎**: 当执行到 UserTask 节点时,评估 `${processTaskService.getNodeApprovalUsers(execution)}`

**文件**: `ProcessTaskServiceImpl.java` - `getNodeApprovalUsers()` 方法 (line 301-313)

```java
@Override
public List<String> getNodeApprovalUsers(ExecutionEntity execution) {
    // 1. 从缓存检查,避免多实例重复解析
    List<String> cacheUsers = taskCache.get(
        execution.getProcessInstanceId() + execution.getActivityId());
    if (Objects.nonNull(cacheUsers)){
        return cacheUsers;
    }

    log.info("获取节点[{}]的审批人", execution.getActivityId());

    // 2. 获取节点配置
    Map propsMap = execution.getVariable(WflowGlobalVarDef.WFLOW_NODE_PROPS, Map.class);
    ApprovalProps props = (ApprovalProps) propsMap.get(execution.getActivityId());

    // 3. ⭐ 根据配置获取审批人
    List<String> approvalUsers = getApprovalUsers(execution, props);

    // 4. 缓存结果
    taskCache.put(execution.getProcessInstanceId() + execution.getActivityId(), approvalUsers);
    return approvalUsers;
}
```

#### 步骤5: 根据assignedType获取审批人

**文件**: `ProcessTaskServiceImpl.java` - `getApprovalUsers()` 方法 (line 358-430)

```java
public List<String> getApprovalUsers(ExecutionEntity execution, ApprovalProps props) {
    Set<String> userSet = new LinkedHashSet<>();

    switch (props.getAssignedType()) {
        case REFUSE:
            userSet.add(WflowGlobalVarDef.WFLOW_TASK_REFUSE);
            break;

        case SELF: // 取流程发起人
            ProcessInstanceOwnerDto owner = execution.getVariable("owner", ProcessInstanceOwnerDto.class);
            Optional.ofNullable(owner).ifPresent(on -> userSet.add(on.getOwner()));
            break;

        case ROLE: // 取角色
            userSet.addAll(userDeptOrLeaderService.getUsersByRoles(
                props.getRole().stream().map(OrgUser::getId).collect(Collectors.toList())));
            break;

        case FORM_USER: // 从表单字段取
            List<Map<String, Object>> userList = execution.getVariable(props.getFormUser(), List.class);
            Optional.ofNullable(userList).ifPresent(users -> {
                userSet.addAll(users.stream().map(u -> u.get("id").toString()).collect(Collectors.toList()));
            });
            break;

        case ASSIGN_USER: // 指定用户
            userSet.addAll(props.getAssignedUser().stream()
                .map(OrgUser::getId).collect(Collectors.toList()));
            break;

        case SELF_SELECT: // ⭐⭐⭐ 自选用户,从执行变量取
            // execution.getActivityId() = nodeId
            List<OrgUser> selectUsers = execution.getVariable(execution.getActivityId(), List.class);
            Optional.ofNullable(selectUsers).ifPresent(on ->
                userSet.addAll(on.stream().map(OrgUser::getId).collect(Collectors.toList())));
            break;

        case LEADER: // 用户的指定级别部门主管
            ProcessInstanceOwnerDto owner2 = execution.getVariable("owner", ProcessInstanceOwnerDto.class);
            String leaderByLevel = userDeptOrLeaderService.getUserLeaderByLevel(
                owner2.getOwner(), owner2.getOwnerDeptId(),
                props.getLeader().getLevel(), props.getLeader().getSkipEmpty());
            Optional.ofNullable(leaderByLevel).ifPresent(userSet::add);
            break;

        // ... 其他类型
    }

    // 处理审批人为空时的默认策略
    if (CollectionUtil.isEmpty(userSet)) {
        switch (props.getNobody().getHandler()) {
            case TO_USER:   // 转给指定用户
                userSet.addAll(props.getNobody().getAssignedUser().stream()
                    .map(OrgUser::getId).collect(Collectors.toList()));
                break;
            case TO_ADMIN:  // 转给管理员
                userSet.addAll(userDeptOrLeaderService.getUsersByRoles(
                    CollectionUtil.newArrayList(WflowGlobalVarDef.WFLOW_APPROVAL_ADMIN)));
                break;
            case TO_PASS:   // 自动通过
                userSet.add(WflowGlobalVarDef.WFLOW_TASK_AGRRE);
                break;
            case TO_REFUSE: // 自动驳回
                userSet.add(WflowGlobalVarDef.WFLOW_TASK_REFUSE);
                break;
        }
    } else {
        // 将用户替换为当前代理人(如果有设置代理)
        return userDeptOrLeaderService.replaceUserAsAgent(userSet);
    }

    return new ArrayList<>(userSet);
}
```

**SELF_SELECT 的核心逻辑** (line 391-393):
```java
case SELF_SELECT:
    // ⭐ 从执行变量中取出,变量名 = nodeId
    List<OrgUser> selectUsers = execution.getVariable(execution.getActivityId(), List.class);
    Optional.ofNullable(selectUsers).ifPresent(on ->
        userSet.addAll(on.stream().map(OrgUser::getId).collect(Collectors.toList())));
    break;
```

**数据流**:
```
前端: processUsers[nodeId] = [user1, user2]
  ↓ POST提交
后端: processVar.putAll(params.getProcessUsers())
  ↓ Flowable存储
execution.variables[nodeId] = [user1, user2]
  ↓ 引擎调用
execution.getVariable(nodeId, List.class)
  ↓ 提取ID
return [user1.id, user2.id]
  ↓ 多实例
创建2个UserTask: assignee=user1.id, assignee=user2.id
```

#### 步骤6: Flowable创建多个任务实例

Flowable引擎根据返回的用户ID列表 `["user_001", "user_002"]`:

1. **创建第一个任务**: `assignee = "user_001"`, `name = "node_approval_001"`
2. **创建第二个任务**: `assignee = "user_002"`, `name = "node_approval_001"`
3. **设置完成条件**:
   - **会签(AND)**: `nrOfActiveInstances == 0` - 所有任务都完成
   - **或签(OR)**: `nrOfCompletedInstances >= 1` - 任意一个完成
   - **依次(NEXT)**: `sequential=true` - 按顺序执行

### 9.3 数据结构对比

#### wflow 数据结构

**节点配置** (数据库 `wflow_models.process` 字段):
```json
{
  "id": "node_approval_001",
  "type": "APPROVAL",
  "name": "部门经理审批",
  "props": {
    "assignedType": "SELF_SELECT",
    "mode": "AND",
    "selfSelect": {
      "multiple": true
    },
    "assignedUser": [],  // ⭐ 空数组,正常!
    "nobody": {
      "handler": "TO_ADMIN"
    }
  }
}
```

**运行时数据** (提交时):
```json
{
  "deptId": "dept_001",
  "formData": { ... },
  "processUsers": {  // ⭐ 运行时填充
    "node_approval_001": [
      { "id": "user_001", "name": "张三", "type": "user" },
      { "id": "user_002", "name": "李四", "type": "user" }
    ]
  }
}
```

**Flowable变量** (引擎内部):
```json
{
  "owner": { "owner": "initiator_001", "ownerName": "王五", ... },
  "WFLOW_NODE_PROPS": {
    "node_approval_001": { "assignedType": "SELF_SELECT", ... },
    "node_approval_002": { "assignedType": "ASSIGN_USER", ... }
  },
  "WFLOW_FORMS": [ ... ],
  "initiator": "initiator_001",
  "field_001": "采购单",
  "field_002": 50000,
  "node_approval_001": [  // ⭐ processUsers合并进来
    { "id": "user_001", "name": "张三", "type": "user" },
    { "id": "user_002", "name": "李四", "type": "user" }
  ]
}
```

#### SCM 当前数据结构

**节点配置** (`bpm_process_templates.process`):
```json
{
  "id": "node_003",
  "type": "APPROVAL",
  "name": "部门经理审批",
  "props": {
    "assignedType": "SELF_SELECT",
    "mode": "AND",
    "selfSelect": {
      "multiple": true
    },
    "assignedUser": []  // ⭐ 与wflow一致
  }
}
```

**提交数据** (前端):
```json
{
  "process_users": {  // ⭐ 字段名可能不同
    "node_003": [ ... ]
  }
}
```

**问题**: 前端验证逻辑错误
```javascript
// ❌ 错误: 检查 task.users (来自assignedUser)
const ifEnd = this.processData.some((task) =>
  task.type !== 'END' && task.users.length === 0
)

// ✅ 正确: 应该检查 process_users[task.id]
const ifEnd = this.processData.some((task) => {
  if (task.type === 'END') return false;

  // SELF_SELECT节点检查process_users
  if (task.props?.assignedType === 'SELF_SELECT') {
    return !this.process_users[task.id] || this.process_users[task.id].length === 0;
  }

  // 其他类型检查users
  return task.users.length === 0;
});
```

## 10. 总结

### 9.1 核心要点

1. **SELF_SELECT 的本质**:
   - 配置时 `assignedUser` 为空是**正确的**
   - 发起时由前端填充 `processUsers[nodeId]`
   - 后端从 `processUsers` 获取审批人

2. **前端职责**:
   - 识别并标记自选节点
   - 提供人员选择界面
   - 验证时检查 `processUsers` 而非 `assignedUser`

3. **数据流转**:
   ```
   流程配置 (assignedUser=[])
       ↓
   前端渲染 (enableEdit=true)
       ↓
   用户选择 (processUsers[nodeId]=[user1,user2])
       ↓
   提交验证 (检查 processUsers)
       ↓
   后端分配 (创建审批任务)
   ```

### 9.2 wflow 可借鉴的设计

1. **枚举驱动的类型系统**: 通过 `ApprovalTypeEnum` 支持10种审批人分配方式
2. **渲染时验证准备**: 在渲染阶段就收集需要验证的节点集合
3. **双数据结构**: `users`(显示) + `_value`/`processUsers`(数据)分离
4. **精准错误提示**: `errorShark()` 抖动显示具体错误节点

### 9.3 参考资料

- **wflow 项目路径**: `D:\2025_project\20_project_in_github\99_tools\wflow`
- **关键文件**:
  - 前端: `ProcessRender.vue`, `ApprovalNodeConfig.vue`, `InitiateProcess.vue`
  - 后端: `ApprovalProps.java`, `ProcessStartParamsVo.java`
- **在线示例**: wflow 启动后访问 `http://localhost:88`

---

**文档版本**: v1.0
**创建日期**: 2025-11-28
**作者**: Claude Code Research Agent
