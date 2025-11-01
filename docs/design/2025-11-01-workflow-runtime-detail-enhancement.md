# 工作流执行详情弹窗功能增强方案

**文档版本**: v1.0
**创建日期**: 2025-11-01
**作者**: SCM-AI团队
**需求来源**: 用户反馈 - SCM执行详情显示不完整，需与aideepin保持一致

---

## 1. 需求背景

### 1.1 问题描述

当前SCM工作流执行详情弹窗功能存在以下问题：

1. **缺少节点详情数据加载** - 点击"执行详情"按钮后，弹窗没有加载节点的 `inputData` 和 `outputData`
2. **数据显示不完整** - 只显示节点名称和状态，缺少：
   - 节点输入参数列表
   - 节点输出参数列表（结构化显示）
3. **显示格式不友好** - 当前使用 Timeline 组件，没有清晰的输入/输出分区

### 1.2 期望效果

参考 aideepin 的执行详情弹窗，实现：

- 卡片式节点详情展示
- 输入参数区：显示所有输入参数（`name: value` 格式）
- 输出参数区：显示所有输出参数（`name: value` 格式）
- 支持特殊类型显示（如 type=4 的图片列表）

---

## 2. KISS原则4问题评估

### 2.1 这是个真问题还是臆想出来的？
✅ **真问题** - 用户明确反馈SCM执行详情显示不完整，与aideepin不一致

### 2.2 有更简单的方法吗？
✅ **当前方案已是最简** - 后端API已存在，前端只需调用并渲染，不需要新建组件

### 2.3 会破坏什么吗？
✅ **无破坏性** - 只修改执行详情弹窗显示逻辑，不影响其他功能

### 2.4 当前项目真的需要这个功能吗？
✅ **必要** - 执行详情查看是调试和分析工作流的核心功能，数据完整性直接影响用户体验

---

## 3. 调用链路分析

### 3.1 完整调用链路

```
用户操作：点击"执行详情"按钮
    ↓
前端：WorkflowRuntimeList.vue → showExecutionDetail(runtime)
    ↓
【新增】前端API调用：getRuntimeNodeDetails(runtime.id)
    ↓
后端接口：GET /api/v1/ai/workflow/runtime/nodes/{runtimeId}
    ↓
后端Controller：WorkflowController.listRuntimeNodes()
    ↓
后端Service：AiWorkflowRuntimeService.listByRuntimeUuid()
    ↓
后端Service：AiWorkflowRuntimeNodeService.listByWfRuntimeId()
    ↓
数据转换：Entity → VO（String → JSONObject）
    ↓
返回前端：List<AiWorkflowRuntimeNodeVo>
    ↓
前端渲染：el-dialog显示节点详情卡片
```

### 3.2 关键数据转换点

**后端数据转换（已实现）**:
```java
// AiWorkflowRuntimeNodeService.listByWfRuntimeId() Lines 56-62
if (StringUtils.isNotBlank(entity.getInputData())) {
    vo.setInputData(JSON.parseObject(entity.getInputData()));
}
if (StringUtils.isNotBlank(entity.getOutputData())) {
    vo.setOutputData(JSON.parseObject(entity.getOutputData()));
}
```

**前端数据结构（接收后）**:
```javascript
{
  id: 123,
  runtimeNodeUuid: "uuid",
  nodeId: 456,
  inputData: {        // ✅ 已是JSONObject
    "var_input": {
      type: 1,
      value: "用户输入内容",
      title: "用户输入"
    }
  },
  outputData: {       // ✅ 已是JSONObject
    "output": {
      type: 1,
      value: "LLM回答内容",
      title: "输出"
    }
  },
  status: 3
}
```

---

## 4. 数据结构分析

### 4.1 后端返回数据结构

**AiWorkflowRuntimeNodeVo**:
```java
{
  id: Long,                     // 节点执行记录ID
  runtimeNodeUuid: String,      // 运行时节点UUID
  workflowRuntimeId: Long,      // 运行时实例ID
  nodeId: Long,                 // 节点ID
  inputData: JSONObject,        // 输入参数（已解析为JSONObject）
  outputData: JSONObject,       // 输出参数（已解析为JSONObject）
  status: Integer               // 状态（1-等待，2-运行，3-成功，4-失败）
}
```

**JSONObject内部结构**:
```json
{
  "paramName": {
    "type": 1,        // 1-TEXT, 2-NUMBER, 3-OPTIONS, 4-FILES, 5-BOOL
    "value": "xxx",   // 实际值
    "title": "参数标题"
  }
}
```

### 4.2 前端显示所需数据

参考 aideepin RuntimeNodes.vue Lines 53-81：

```vue
<!-- 输入参数遍历 -->
<div v-for="(content, name) in node.inputData" :key="`input_${name}`">
  <span>{{ name }}</span>
  <span>{{ content.value || '无内容' }}</span>
</div>

<!-- 输出参数遍历 -->
<div v-for="(content, name) in node.outputData" :key="`output_${name}`">
  <!-- 特殊处理：type=4显示图片 -->
  <template v-if="content.type === 4">
    <el-image v-for="url in content.value" :src="url" />
  </template>
  <!-- 常规参数 -->
  <template v-else>
    <span>{{ name }}</span>
    <span>{{ content.value || '无内容' }}</span>
  </template>
</div>
```

---

## 5. 方案设计

### 5.1 修改文件清单

**前端修改文件**（只需修改1个文件）:
- `src/components/70_ai/components/workflow/components/WorkflowRuntimeList.vue`

**后端修改文件**:
- 无需修改（API已存在）

### 5.2 前端实施方案

#### 5.2.1 修改 `showExecutionDetail` 方法

**位置**: WorkflowRuntimeList.vue Lines 672-676

**当前代码**:
```javascript
showExecutionDetail (runtime) {
  this.currentRuntimeDetail = runtime
  this.detailDialogVisible = true
  // TODO: 如果没有节点详情，从后端加载
}
```

**修改后**:
```javascript
async showExecutionDetail (runtime) {
  this.currentRuntimeDetail = runtime
  this.detailDialogVisible = true

  // ✅ 加载节点详情数据
  if (!runtime.nodes || runtime.nodes.length === 0) {
    try {
      const response = await getRuntimeNodeDetails(runtime.id)
      if (response.code === 20000 && response.data && response.data.length > 0) {
        // 更新runtime的nodes数据
        runtime.nodes = response.data
        // 触发响应式更新
        this.currentRuntimeDetail = { ...runtime, nodes: response.data }
      }
    } catch (error) {
      console.error('加载节点详情失败:', error)
      this.$message.error('加载节点详情失败')
    }
  }
}
```

#### 5.2.2 添加辅助方法

**位置**: WorkflowRuntimeList.vue methods 区域

**新增方法**:
```javascript
/**
 * 格式化参数值显示
 * 处理NodeIOData格式: {type, value, title}
 */
formatParamValue (value) {
  if (value === null || value === undefined) return '无内容'

  // 处理NodeIOData格式
  if (typeof value === 'object' && value.value !== undefined) {
    return this.formatParamValue(value.value)
  }

  // 处理对象
  if (typeof value === 'object') {
    return JSON.stringify(value, null, 2)
  }

  return String(value)
}
```

#### 5.2.3 重构执行详情弹窗模板

**位置**: WorkflowRuntimeList.vue Lines 153-211

**当前代码问题**:
- 使用 el-timeline 组件显示
- 只显示 `node.output` 字符串
- 缺少输入参数显示

**修改后的模板**:
```vue
<el-dialog>
  <div v-if="currentRuntimeDetail" class="execution-detail">
    <!-- 基本信息（保持不变）-->
    <div class="detail-section">...</div>

    <!-- 节点执行详情（新设计）-->
    <div v-if="currentRuntimeDetail.nodes && currentRuntimeDetail.nodes.length" class="detail-section">
      <h4>节点执行详情</h4>

      <!-- 节点卡片列表 -->
      <div v-for="(node, index) in currentRuntimeDetail.nodes" :key="index" class="node-card">
        <!-- 节点标题 -->
        <div class="node-header">
          <span class="node-name">{{ node.nodeTitle || '未命名节点' }}</span>
          <el-tag :type="getStatusType(node.status)" size="mini">
            {{ getStatusText(node.status) }}
          </el-tag>
        </div>

        <!-- 输入参数区 -->
        <div v-if="node.inputData && Object.keys(node.inputData).length > 0" class="node-section">
          <div class="section-title">输入</div>
          <div v-for="(value, key) in node.inputData" :key="`input_${key}`" class="param-item">
            <span class="param-label">{{ key }}:</span>
            <span class="param-value">{{ formatParamValue(value) }}</span>
          </div>
        </div>

        <!-- 输出参数区 -->
        <div v-if="node.outputData && Object.keys(node.outputData).length > 0" class="node-section">
          <div class="section-title">输出</div>
          <div v-for="(value, key) in node.outputData" :key="`output_${key}`" class="param-item">
            <!-- 特殊处理：type=4显示图片 -->
            <template v-if="value && value.type === 4 && value.value">
              <div class="param-label">{{ key }}:</div>
              <div class="image-list">
                <el-image
                  v-for="(url, idx) in value.value"
                  :key="idx"
                  :src="url"
                  :preview-src-list="value.value"
                  fit="cover"
                  style="width: 100px; height: 100px; margin-right: 8px;"
                />
              </div>
            </template>
            <!-- 常规参数 -->
            <template v-else>
              <span class="param-label">{{ key }}:</span>
              <span class="param-value">{{ formatParamValue(value) }}</span>
            </template>
          </div>
        </div>

        <!-- 错误信息（如果有）-->
        <div v-if="node.statusRemark" class="node-error">
          <i class="el-icon-warning" />
          <span>{{ node.statusRemark }}</span>
        </div>
      </div>
    </div>

    <!-- 工作流错误信息（保持不变）-->
    <div v-if="currentRuntimeDetail.status === 4" class="detail-section">...</div>
  </div>
</el-dialog>
```

#### 5.2.4 添加样式

**位置**: WorkflowRuntimeList.vue `<style>` 区域

**新增样式**:
```scss
.execution-detail {
  .detail-section {
    margin-bottom: 24px;

    h4 {
      margin: 0 0 12px 0;
      font-size: 16px;
      font-weight: 500;
      color: #303133;
    }
  }

  .node-card {
    border: 1px solid #e4e7ed;
    border-radius: 4px;
    padding: 16px;
    margin-bottom: 16px;
    background-color: #fff;

    &:last-child {
      margin-bottom: 0;
    }
  }

  .node-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8px 12px;
    background-color: #f5f7fa;
    border-radius: 4px;
    margin-bottom: 12px;

    .node-name {
      font-weight: 500;
      color: #303133;
      font-size: 14px;
    }
  }

  .node-section {
    margin-bottom: 12px;

    &:last-child {
      margin-bottom: 0;
    }

    .section-title {
      font-size: 14px;
      font-weight: 500;
      color: #606266;
      padding-bottom: 8px;
      margin-bottom: 8px;
      border-bottom: 1px solid #ebeef5;
    }
  }

  .param-item {
    display: flex;
    margin-bottom: 8px;
    font-size: 13px;
    line-height: 1.6;

    &:last-child {
      margin-bottom: 0;
    }

    .param-label {
      min-width: 100px;
      font-weight: 500;
      color: #606266;
      flex-shrink: 0;
    }

    .param-value {
      color: #303133;
      word-break: break-word;
      white-space: pre-wrap;
    }
  }

  .image-list {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    margin-top: 8px;
  }

  .node-error {
    margin-top: 12px;
    padding: 8px 12px;
    background-color: #fef0f0;
    border-radius: 4px;
    color: #f56c6c;
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;

    i {
      font-size: 16px;
      flex-shrink: 0;
    }
  }
}
```

---

## 6. 实施步骤

### 6.1 前端实施步骤

1. **导入API方法**（如果未导入）
   ```javascript
   import { getRuntimeNodeDetails } from '@/components/70_ai/api/workflowService'
   ```

2. **修改 `showExecutionDetail` 方法**
   - 添加节点详情数据加载逻辑
   - 处理异常情况（API调用失败）

3. **添加 `formatParamValue` 辅助方法**
   - 统一处理参数值格式化
   - 支持递归提取 `value` 字段

4. **重构执行详情弹窗模板**
   - 替换 Timeline 组件为卡片式布局
   - 添加输入/输出参数区域
   - 支持特殊类型显示（图片）

5. **添加样式**
   - 节点卡片样式
   - 输入/输出分区样式
   - 参数列表样式

### 6.2 测试验证

1. **功能测试**
   - ✅ 点击"执行详情"按钮，弹窗正常打开
   - ✅ 节点详情数据正确加载
   - ✅ 输入参数正确显示
   - ✅ 输出参数正确显示
   - ✅ 图片类型参数（type=4）正确显示图片预览

2. **边界测试**
   - ✅ 节点无输入参数时，不显示输入区域
   - ✅ 节点无输出参数时，不显示输出区域
   - ✅ API调用失败时，显示错误提示
   - ✅ 空数据时，显示友好提示

3. **样式测试**
   - ✅ 卡片布局与aideepin一致
   - ✅ 输入/输出分区清晰
   - ✅ 参数列表对齐美观

---

## 7. 风险分析与缓解

### 7.1 潜在风险

| 风险 | 影响 | 概率 | 缓解措施 |
|------|------|------|----------|
| API调用失败 | 无法显示节点详情 | 低 | 添加try-catch，显示错误提示 |
| 数据格式不一致 | 显示异常 | 低 | 使用可选链和默认值 |
| 样式兼容性问题 | 显示错乱 | 低 | 使用flex布局，确保兼容性 |
| 图片加载失败 | 显示空白 | 中 | 使用el-image组件，自带加载失败提示 |

### 7.2 回退方案

如果新方案出现问题，可以快速回退到当前版本：
- 保留当前 Timeline 显示逻辑
- 注释掉新增的节点详情加载代码

---

## 8. 完成标准

### 8.1 功能完成标准

- ✅ 点击"执行详情"按钮，自动加载节点详情数据
- ✅ 弹窗显示基本信息（状态、时间、耗时）
- ✅ 每个节点以卡片形式展示
- ✅ 节点卡片包含：标题、状态、输入参数、输出参数
- ✅ 参数以 `name: value` 格式显示
- ✅ 支持图片类型参数（type=4）显示图片预览

### 8.2 质量标准

- ✅ 代码通过 ESLint 检查
- ✅ 异常处理完备（API调用失败、数据为空）
- ✅ 样式与aideepin保持一致
- ✅ 无控制台错误或警告

---

## 9. 参考资料

### 9.1 参考代码

- **aideepin RuntimeNodes.vue**: `D:\2025_project\20_project_in_github\99_tools\aideepin\langchain4j-aideepin-web\src\views\workflow\components\RuntimeNodes.vue`
- **SCM WorkflowRuntimeList.vue**: `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\components\70_ai\components\workflow\components\WorkflowRuntimeList.vue`

### 9.2 后端接口

- **接口路径**: `GET /api/v1/ai/workflow/runtime/nodes/{runtimeId}`
- **Controller**: `WorkflowController.listRuntimeNodes()` (Line 314)
- **Service**: `AiWorkflowRuntimeNodeService.listByWfRuntimeId()` (Line 43)

---

## 10. 总结

### 10.1 核心改进点

1. **数据完整性** - 加载并显示节点的输入和输出参数
2. **显示友好性** - 卡片式布局，输入/输出分区清晰
3. **功能对齐** - 与aideepin保持一致的用户体验

### 10.2 技术亮点

- ✅ 复用现有后端API，无需后端改动
- ✅ 使用辅助方法封装重复逻辑
- ✅ 完善的异常处理和边界情况处理
- ✅ 响应式设计，支持不同数据格式

### 10.3 预期收益

- 🎯 **提升用户体验** - 执行详情更完整，调试更方便
- 🎯 **降低学习成本** - 与aideepin一致，用户无需重新学习
- 🎯 **提高开发效率** - 调试工作流更直观，问题定位更快

---

**文档状态**: ✅ 已完成
**待审批**: 是
**预计工作量**: 2-3小时（前端开发 + 测试）
