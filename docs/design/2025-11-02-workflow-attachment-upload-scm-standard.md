# 工作流附件上传SCM标准化改造方案

## 文档信息
- **创建日期**：2025-11-02
- **功能名称**：工作流附件上传SCM标准化
- **影响模块**：前端（WorkflowRunDetail.vue、WorkflowRuntimeList.vue）

## 一、需求背景

### 1.1 当前问题
1. ❌ 工作流附件上传使用 `el-upload` 组件，与SCM业务页面标准不一致
2. ❌ 使用AI专用上传API `/scm/api/v1/ai/file/upload`，返回UUID，不符合SCM模式
3. ❌ 附件展示为纯文本UUID数组，用户体验差，无法下载预览
4. ❌ 缺少附件文件名、上传时间等元数据

### 1.2 目标
1. ✅ 统一使用SCM标准的 `SimpleUploadMutilFile` 组件
2. ✅ 调用SCM文件系统服务获取文件URL
3. ✅ 使用 `51_preview_description` 组件展示附件列表
4. ✅ 支持附件下载和预览功能

## 二、KISS原则评估

### 2.1 这是个真问题还是臆想出来的？
✅ **真问题**
- 工作流需要附件支持（如审批单据、证明文件）
- SCM系统已有成熟的附件上传模式
- 需要统一前端组件和交互方式

### 2.2 有更简单的方法吗？
✅ **已是最简方案**
- 复用现有 `SimpleUploadMutilFile` 组件（已测试稳定）
- 复用现有 `51_preview_description` 展示组件
- 遵循现有数据结构模式（doc_att 三数组模式）
- 无需重新开发，只需集成

### 2.3 会破坏什么吗？
⚠️ **需要注意的点**
- 修改 `WorkflowRunDetail.vue` 的附件上传逻辑
- 修改 `WorkflowRuntimeList.vue` 的附件展示逻辑
- 后端无需修改（input.content.value 已支持任意JSON数据）
- 保持向后兼容（已有UUID格式仍可显示）

### 2.4 当前项目真的需要这个功能吗？
✅ **确实需要**
- 工作流场景常需要附件支持
- 符合SCM业务系统的标准模式
- 提升用户体验和系统完整性

## 三、核心数据结构

### 3.1 SCM标准附件数据结构
```javascript
// 三个数组结构（参考: 项目管理-新增页面）
doc_att: [
  {
    fileName: "合同.pdf",
    url: "http://file.xinyirunscm.com/xxx/xxx.pdf",
    timestamp: 1698765432000
  }
]  // 完整对象（用于展示）

doc_att_file: [
  "http://file.xinyirunscm.com/xxx/xxx.pdf"
]  // URL数组（用于删除查找）

tempJson.doc_att_files: doc_att  // 提交后端（与doc_att同步）
```

### 3.2 工作流输入数据格式
```javascript
// 运行工作流时的输入格式
userInputs: [
  {
    name: "var_text",
    content: {
      type: 1,        // TEXT类型
      value: "文本内容",
      title: "文本输入"
    },
    required: true
  },
  {
    name: "var_file",
    content: {
      type: 4,        // FILES类型
      value: [
        "http://file.xinyirunscm.com/xxx/xxx.pdf"
      ],              // ⭐ 改为URL数组
      title: "附件上传"
    },
    required: false
  }
]
```

### 3.3 展示数据格式
```javascript
// WorkflowRuntimeList中展示格式
runtime.input = {
  var_text: "文本内容",
  var_file: [
    {
      fileName: "合同.pdf",
      url: "http://file.xinyirunscm.com/xxx/xxx.pdf",
      timestamp: 1698765432000
    }
  ]  // ⭐ 保存完整附件对象用于展示
}
```

## 四、详细设计方案

### 4.1 前端文件修改清单

#### 4.1.1 WorkflowRunDetail.vue（用户输入表单）

**需要修改的部分**：

1. **引入组件**：
```javascript
import SimpleUploadMutilFile from '@/components/10_file/SimpleUploadMutilFile/index.vue'
```

2. **data定义**：
```javascript
data () {
  return {
    // ... 其他数据

    // 附件相关数据（参考项目管理页面）
    doc_att: [],        // 完整附件对象数组
    doc_att_file: [],   // URL数组

    submitting: false,
    // ...
  }
}
```

3. **模板修改（Line 54-76）**：
```vue
<!-- 替换原有的 el-upload -->
<!-- 类型4: FILES - 文件上传 (SCM标准模式) -->
<template v-if="userInput.content.type === 4">
  <!-- 上传组件 -->
  <Simple-upload-mutil-file
    :accept="'*'"
    @upload-success="handleUploadFileSuccess"
    @upload-error="handleUploadFileError"
  />

  <!-- 已上传附件列表 -->
  <div v-if="doc_att.length > 0" style="margin-top: 10px;">
    <el-descriptions
      title=""
      :column="1"
      :label-style="{ display: 'none' }"
      direction="horizontal"
      border
    >
      <el-descriptions-item
        v-for="(file, index) in doc_att"
        :key="index"
      >
        <span>
          附件名称：{{ file.fileName }} &nbsp;
          文件上传日期：{{ formatTimestamp(file.timestamp) }} &nbsp;
          <span class="clickable" @click="handleRemoveFile(file)">删除</span>
        </span>
      </el-descriptions-item>
    </el-descriptions>
  </div>
</template>
```

4. **方法修改**：
```javascript
methods: {
  // 上传成功处理（参考项目管理）
  handleUploadFileSuccess (res) {
    // res.response 结构: {code: 0, data: {url, fileName}, timestamp}

    // 1. 添加时间戳到附件对象
    res.response.data.timestamp = res.response.timestamp

    // 2. 保存完整附件对象（用于页面展示）
    this.doc_att.push(res.response.data)

    // 3. 保存附件URL（用于快速查找和删除）
    this.doc_att_file.push(res.response.data.url)

    // 4. 更新到userInput.content.value（运行时传递给后端）
    const fileInput = this.userInputs.find(input => input.content.type === 4)
    if (fileInput) {
      fileInput.content.value = this.doc_att_file  // ⭐ 保存URL数组
    }
  },

  // 上传失败处理
  handleUploadFileError () {
    this.$message.error('文件上传发生错误！')
  },

  // 删除文件
  handleRemoveFile (file) {
    // 1. 根据URL查找索引
    const _index = this.doc_att_file.lastIndexOf(file.url)

    // 2. 从两个数组中同步删除
    this.doc_att.splice(_index, 1)
    this.doc_att_file.splice(_index, 1)

    // 3. 同步到userInput.content.value
    const fileInput = this.userInputs.find(input => input.content.type === 4)
    if (fileInput) {
      fileInput.content.value = this.doc_att_file
    }
  },

  // 格式化时间戳
  formatTimestamp (timestamp) {
    if (!timestamp) return ''
    const date = new Date(timestamp)
    return date.toLocaleString('zh-CN')
  },

  // ⭐ 修改 handleRun 方法
  async handleRun () {
    if (this.submitting || !this.canRun) return

    this.submitting = true

    // 构造输入数据
    const inputs = this.userInputs.map(input => ({
      name: input.name,
      content: {
        type: input.content.type,
        value: input.content.value,  // ⭐ 对于FILES类型，这是URL数组
        title: input.content.title
      },
      required: input.required,
      // ⭐ 新增：如果是附件类型，附加完整附件对象（用于展示）
      attachments: input.content.type === 4 ? this.doc_att : undefined
    }))

    try {
      // 触发父组件的运行事件
      this.$emit('run', inputs)
    } catch (error) {
      this.$message.error(`运行失败: ${error.message || '未知错误'}`)
      this.submitting = false
    }
  },

  // ⭐ 修改 resetInputs 方法
  resetInputs () {
    // 清空附件
    this.doc_att = []
    this.doc_att_file = []

    // 清空用户输入
    this.userInputs.forEach(input => {
      input.content.value = null
    })
  }
}
```

#### 4.1.2 WorkflowRuntimeList.vue（历史记录展示）

**需要修改的部分**：

1. **引入组件**：
```javascript
import PreviewDescription from '@/components/51_preview_description/index.vue'
```

2. **模板修改（Line 32-39）**：
```vue
<!-- 用户输入消息 -->
<div class="message-content">
  <div v-if="runtime.input && Object.keys(runtime.input).length" class="input-content">
    <div v-for="(value, key) in runtime.input" :key="key" class="input-item">
      <!-- ⭐ 判断是否为附件数组 -->
      <template v-if="isAttachmentArray(value)">
        <!-- 使用SCM标准附件展示组件 -->
        <PreviewDescription :attachment-files="value" />
      </template>
      <template v-else>
        <!-- 普通文本展示 -->
        <span class="input-value">{{ formatValue(value) }}</span>
      </template>
    </div>
  </div>
  <div v-else class="no-input">
    <span>无输入</span>
  </div>
</div>
```

3. **方法修改**：
```javascript
methods: {
  // ... 其他方法

  // ⭐ 新增：判断是否为附件数组
  isAttachmentArray (value) {
    // 判断是否为数组且第一个元素有fileName和url属性
    return Array.isArray(value) &&
           value.length > 0 &&
           value[0].fileName !== undefined &&
           value[0].url !== undefined
  },

  // ⭐ 修改 handleRunWorkflow 方法（Line 495）
  handleRunWorkflow (inputs) {
    if (this.running || !this.canRun) {
      return
    }

    this.running = true

    // 构造输入数组
    const inputList = inputs.map(item => ({
      name: item.name,
      content: item.content,
      required: item.required || false
    }))

    // 创建AbortController
    const controller = new AbortController()
    this.currentController = controller

    let accumulatedOutput = ''
    let currentRuntimeUuid = null

    // 使用回调函数处理SSE事件流
    workflowRun({
      wfUuid: this.workflow.workflowUuid,
      inputs: inputList,
      signal: controller.signal,

      // [START]事件回调
      startCallback: (wfRuntimeJson) => {
        if (!wfRuntimeJson) {
          this.$message.error('启动失败')
          this.running = false
          return
        }

        const runtime = JSON.parse(wfRuntimeJson)
        currentRuntimeUuid = runtime.runtimeUuid

        // ⭐ 保存用户输入到runtime.input
        runtime.input = {}
        inputs.forEach(item => {
          // 如果有附件数据，保存完整对象用于展示
          if (item.attachments) {
            runtime.input[item.name] = item.attachments
          } else {
            runtime.input[item.name] = item.content
          }
        })

        runtime.output = ''
        runtime.loading = true

        this.localRuntimeList.push(runtime)
        this.$message.success('工作流已开始执行')

        this.$nextTick(() => {
          const container = this.$refs.scrollContainer
          if (container) {
            container.scrollTop = container.scrollHeight
          }
        })
      },

      // ... 其他回调
    })
  }
}
```

### 4.2 后端影响评估

**无需修改后端代码**

理由：
1. ✅ 后端 `ai_workflow_runtime` 表的 `input` 字段为 JSON 类型，支持任意结构
2. ✅ 附件URL数组可以直接存储在 `input.content.value` 中
3. ✅ 后端只负责存储和返回，不关心具体格式
4. ✅ 向后兼容：已有UUID格式数据仍可正常读取和展示

## 五、数据流程图

### 5.1 附件上传流程
```
用户操作            文件系统            前端数据            工作流运行
  │                   │                   │                   │
  ├─ 选择文件 ────────→│                   │                   │
  │                   │                   │                   │
  │←────────────────  上传 ─────────────→│                   │
  │                   │                   │                   │
  │                   │← 返回文件URL ────→│                   │
  │                   │                   │                   │
  │                   │                保存到 doc_att         │
  │                   │                保存到 doc_att_file    │
  │                   │                同步到 input.value     │
  │                   │                   │                   │
  ├─ 删除文件 ─────────────────────────→ 从数组中删除        │
  │                   │                   │                   │
  ├─ 点击运行 ─────────────────────────→ 构造inputs ────────→│
  │                   │                   │                   │
  │                   │                   │                开始执行
  │                   │                   │                保存input
  │                   │                   │                (含URL数组)
  │                   │                   │                   │
  │                   │                   │←─ 返回runtime ────┤
  │                   │                   │                   │
  │                   │                保存到localRuntimeList │
  │                   │                (含完整附件对象)       │
```

### 5.2 附件展示流程
```
用户查看历史        WorkflowRuntimeList     PreviewDescription    文件系统
  │                       │                       │                   │
  ├─ 打开运行历史 ────────→│                       │                   │
  │                       │                       │                   │
  │                   遍历 runtime.input          │                   │
  │                   判断类型                    │                   │
  │                       │                       │                   │
  │                   附件数组 ──────────────────→│                   │
  │                       │                       │                   │
  │                       │                   显示附件列表            │
  │                       │                   (fileName, timestamp)   │
  │                       │                       │                   │
  ├─ 点击下载 ────────────────────────────────────→│                   │
  │                       │                       │                   │
  │                       │                       │─ 下载文件 ────────→│
  │                       │                       │                   │
  │←────────────────────────────────────────────── 返回文件 ──────────┤
```

## 六、风险分析

### 6.1 技术风险

| 风险项 | 严重程度 | 影响范围 | 缓解措施 |
|--------|---------|---------|---------|
| 文件上传失败 | 中 | 用户体验 | 完善错误提示，支持重新上传 |
| 历史数据展示异常 | 低 | 已有数据 | 保持向后兼容，UUID格式仍可展示 |
| 组件引入冲突 | 低 | 页面加载 | 测试确认无命名冲突 |

### 6.2 业务风险

| 风险项 | 严重程度 | 影响范围 | 缓解措施 |
|--------|---------|---------|---------|
| 用户不熟悉新交互 | 低 | 用户体验 | 与SCM标准一致，用户已熟悉 |
| 附件丢失 | 低 | 数据完整性 | 文件系统服务已稳定运行 |

### 6.3 性能风险

| 风险项 | 严重程度 | 影响范围 | 缓解措施 |
|--------|---------|---------|---------|
| 大文件上传慢 | 中 | 用户等待 | 文件系统服务已有限制（10M） |
| 附件列表渲染慢 | 低 | 页面性能 | 单个工作流附件数量有限 |

## 七、测试计划

### 7.1 功能测试

| 测试项 | 测试场景 | 预期结果 |
|--------|---------|---------|
| 附件上传 | 选择单个文件上传 | 显示附件信息，包含文件名和时间 |
| 附件上传 | 选择多个文件上传 | 所有文件均成功上传并显示 |
| 附件删除 | 删除已上传的附件 | 附件从列表中移除 |
| 工作流运行 | 带附件参数运行工作流 | URL数组正确传递给后端 |
| 历史记录展示 | 查看带附件的运行历史 | 使用PreviewDescription展示附件列表 |
| 附件下载 | 点击历史记录中的下载链接 | 成功下载文件 |
| 向后兼容 | 查看旧格式UUID数据 | 仍可正常展示（纯文本） |

### 7.2 兼容性测试

| 测试项 | 测试场景 | 预期结果 |
|--------|---------|---------|
| 数据格式兼容 | 读取旧版UUID格式数据 | formatValue正常显示纯文本 |
| 组件兼容 | 不同浏览器测试 | Chrome、Edge、Firefox均正常 |

## 八、实施步骤

### 8.1 修改文件清单

| 序号 | 文件路径 | 修改类型 | 工作量 |
|------|---------|---------|--------|
| 1 | WorkflowRunDetail.vue | 修改 | 2小时 |
| 2 | WorkflowRuntimeList.vue | 修改 | 1小时 |

**总工作量**：约 3 小时

### 8.2 实施顺序

1. **Phase 1：WorkflowRunDetail.vue 修改**（2小时）
   - 引入 SimpleUploadMutilFile 组件
   - 添加 doc_att 相关数据定义
   - 替换上传模板
   - 实现上传、删除、格式化方法
   - 修改 handleRun 和 resetInputs 方法

2. **Phase 2：WorkflowRuntimeList.vue 修改**（1小时）
   - 引入 PreviewDescription 组件
   - 修改展示模板（添加附件判断）
   - 实现 isAttachmentArray 方法
   - 修改 handleRunWorkflow 保存逻辑

3. **Phase 3：测试验证**（建议用户自行测试）
   - 功能测试
   - 兼容性测试
   - 用户验收

## 九、回滚方案

### 9.1 快速回滚
如果上线后发现问题，可以通过以下步骤快速回滚：

1. Git回退到修改前的版本
2. 重新部署前端
3. 已上传的附件数据不受影响（仍可通过URL访问）

### 9.2 数据兼容性
- ✅ 新格式数据：使用 PreviewDescription 展示
- ✅ 旧格式数据：使用 formatValue 纯文本展示
- ✅ 混合格式：自动判断类型，分别展示

## 十、关键洞察（Linus式总结）

### 数据结构
- ✅ 核心数据关系清晰：File → URL → doc_att → input.value
- ✅ 三数组模式经过SCM业务验证，稳定可靠
- ✅ 无不必要的数据复制，各有用途

### 复杂度
- ✅ 全部复用现有组件，零新增代码复杂度
- ✅ 修改点集中在两个文件，影响范围小
- ✅ 遵循SCM标准模式，无学习成本

### 风险点
- ✅ 向后兼容：旧数据仍可展示
- ✅ 零破坏性：不影响已有功能
- ✅ 可回滚：Git版本管理，随时恢复

### 实用性
- ✅ 真实需求：工作流审批需要附件支持
- ✅ 方案简洁：复用成熟组件，无过度设计
- ✅ 价值匹配：工作量3小时，提升用户体验明显
