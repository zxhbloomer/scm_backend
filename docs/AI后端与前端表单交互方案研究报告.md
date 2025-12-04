# AI后端与前端表单交互方案研究报告

## 一、问题分析

### 1.1 核心需求

```
后台AI Chat → 生成表单JSON → 发送给前端 → 自动打开新增页面 → 填充表单数据
```

### 1.2 关键洞察

**您的直觉是对的：不需要额外的WebSocket！**

AI流式输出本身就是一个通道，可以在流中嵌入指令，前端解析后执行动作。

### 1.3 页面标识问题

**问题**：URL是动态配置的（存储在数据库中），硬编码URL路径判断不可靠。

**解决方案**：使用 `page_code` 作为页面唯一标识符。

```javascript
// 每个页面组件在 created() 时都会设置：
this.$options.name = this.$route.meta.page_code

// 例如项目管理页的 page_code 可能是 "B_PROJECT"
```

---

## 二、方案对比

| 方案 | 复杂度 | 是否需要额外连接 | 改动量 | 推荐度 |
|------|--------|----------------|--------|--------|
| ~~WebSocket~~ | 高 | 是 | 大 | ⭐⭐ |
| **SSE流式+指令** | **低** | **否，复用现有流** | **小** | **⭐⭐⭐⭐⭐** |

---

## 三、推荐方案：SSE流式输出 + 指令协议

### 3.1 核心思路

```
┌────────────────────────────────────────────────────────────┐
│  AI Chat 已有的流式输出 (SSE)                               │
├────────────────────────────────────────────────────────────┤
│                                                            │
│  普通文本: "好的，我帮您创建一个采购项目..."                  │
│                         ↓                                  │
│  特殊指令: <!--ACTION:{"type":"FORM_FILL",...}-->          │
│                         ↓                                  │
│  普通文本: "表单已准备好，请确认信息后提交"                   │
│                                                            │
└────────────────────────────────────────────────────────────┘

前端解析流：
- 遇到普通文本 → 正常显示在对话框
- 遇到<!--ACTION:...-->标记 → 解析JSON，执行动作（打开弹窗、填充表单）
```

### 3.2 页面标识机制（关键改进）

**为什么用 `page_code` 而不是 URL 路径？**

| 对比项 | URL路径 | page_code |
|--------|---------|-----------|
| 来源 | 硬编码 | 数据库动态配置 |
| 稳定性 | 会变（菜单调整） | 不变（业务标识） |
| 匹配方式 | `$route.path.includes(...)` | `$route.meta.page_code === ...` |
| 可维护性 | 差 | 好 |

**指令格式更新**：

```json
{
  "type": "FORM_FILL",
  "action": "OPEN_NEW",
  "target_page_code": "B_PROJECT",  // 使用 page_code，不是 URL
  "formData": {
    "name": "采购项目-阿萨泽泽水",
    "supplier_name": "阿萨泽泽水"
  }
}
```

### 3.3 整体架构

```
┌─────────────────────────────────────────────────────────────────┐
│                        前端 (Vue.js)                             │
├─────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────┐    │
│  │                    AI Chat 组件                          │    │
│  │  ┌─────────────────┐    ┌─────────────────┐             │    │
│  │  │ SSE流解析器     │───▶│ 指令分发器       │             │    │
│  │  │ (提取ACTION)    │    │ (EventBus广播)  │             │    │
│  │  └─────────────────┘    └────────┬────────┘             │    │
│  └──────────────────────────────────┼──────────────────────┘    │
│                                     │                            │
│                    EventBus.$emit('ai-form-fill', payload)       │
│                                     │                            │
│                    ┌────────────────┼────────────────┐           │
│                    ▼                ▼                ▼           │
│  ┌──────────────────┐ ┌──────────────────┐ ┌──────────────────┐ │
│  │ 项目管理页       │ │ 采购订单页        │ │ 销售订单页       │ │
│  │ page_code:       │ │ page_code:        │ │ page_code:       │ │
│  │ B_PROJECT        │ │ B_PO_ORDER        │ │ B_SO_ORDER       │ │
│  │                  │ │                   │ │                  │ │
│  │ 监听事件         │ │ 监听事件          │ │ 监听事件         │ │
│  │ 匹配page_code    │ │ 匹配page_code     │ │ 匹配page_code    │ │
│  │ 执行表单操作     │ │ 执行表单操作      │ │ 执行表单操作     │ │
│  └──────────────────┘ └──────────────────┘ └──────────────────┘ │
│                                                                  │
├──────────────────────────────────────────────────────────────────┤
│                        后端 (Spring Boot)                         │
├──────────────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────────────┐   │
│  │  AI Service │───▶│  MCP Tool   │───▶│   SSE 流式响应       │   │
│  │  (对话处理)  │    │  (表单构建)  │    │   (嵌入指令标记)     │   │
│  └─────────────┘    └─────────────┘    └─────────────────────┘   │
└──────────────────────────────────────────────────────────────────┘
```

### 3.4 Tab切换机制（重要）

**现有页面结构分析**：

```
project/index.vue (父容器，路由指向这里)
├── tabs/10_list/index.vue  (列表页)
├── tabs/20_new/index.vue   (新增页) ← 不能通过URL直接打开！
├── tabs/30_edit/index.vue  (编辑页)
├── tabs/40_view/index.vue  (查看页)
└── tabs/50_approve/index.vue (审批页)
```

**问题**：新增页面是通过父容器的 Tab 切换机制控制的，不能通过 URL 直接打开。

**数据流**：
```
列表页点击新增按钮
  → $emit('emitNew', data)
  → 父容器 handleNew()
  → 设置 showNew=true, activeName='edit'
  → 新增页面渲染
```

**解决方案**：在父容器层面监听 EventBus 事件，触发内部 Tab 切换。

---

## 四、详细实现

### 4.1 指令协议定义

#### 4.1.1 指令格式

```
<!--ACTION:{ JSON对象 }-->
```

**为什么用HTML注释格式？**
- 即使前端没有解析，也不会显示乱码（HTML会忽略注释）
- 格式简单，容易解析
- 与正常文本内容区分明显

#### 4.1.2 指令类型定义（更新版）

```typescript
// 指令类型定义
interface AiAction {
  type: 'FORM_FILL' | 'NAVIGATE' | 'NOTIFY' | 'CONFIRM';

  // FORM_FILL 专用
  action?: 'OPEN_NEW' | 'OPEN_EDIT' | 'FILL_ONLY';
  target_page_code?: string;   // 目标页面的 page_code（不是URL！）
  formData?: Record<string, any>;  // 表单数据
  recordId?: number;           // 编辑时的记录ID

  // NAVIGATE 专用（仍然可以用URL，因为这是显式导航）
  target_url?: string;
  query?: Record<string, any>;

  // NOTIFY 专用
  message?: string;
  level?: 'success' | 'warning' | 'error' | 'info';

  // CONFIRM 专用
  title?: string;
  content?: string;
  confirmCallback?: string;
}
```

#### 4.1.3 指令示例

**表单填充指令（使用 page_code）：**
```
<!--ACTION:{"type":"FORM_FILL","action":"OPEN_NEW","target_page_code":"B_PROJECT","formData":{"name":"采购项目-阿萨泽泽水","supplier_name":"阿萨泽泽水","goods_name":"矿泉水","qty":11}}-->
```

**编辑表单指令：**
```
<!--ACTION:{"type":"FORM_FILL","action":"OPEN_EDIT","target_page_code":"B_PROJECT","recordId":12345,"formData":{"name":"更新后的项目名称"}}-->
```

---

### 4.2 后端实现

#### 4.2.1 MCP工具 - 返回带指令的响应

```java
package com.xinyirun.scm.ai.mcp.utils.form;

import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 表单填充MCP工具
 *
 * 核心思路：
 * - MCP工具返回的字符串会被AI整合到流式响应中
 * - 在返回内容中嵌入<!--ACTION:...-->指令
 * - 使用 page_code 标识目标页面，而不是URL路径
 * - 前端解析流时识别指令并执行
 */
@Slf4j
@Component
public class FormFillMcpTools {

    /**
     * 创建项目表单
     *
     * 返回值会被AI整合到响应流中，包含：
     * 1. 给用户看的文字说明
     * 2. 给前端执行的ACTION指令（使用page_code标识）
     */
    @McpTool(description = """
        【工具名称】创建采购项目表单

        【核心功能】
        根据用户描述的项目信息，生成表单数据，并返回包含前端指令的响应。
        前端会自动打开新增页面并填充表单。

        【使用场景】
        - 用户说"帮我创建一个采购项目"
        - 用户说"新建项目，供应商是XXX"
        - 用户描述了项目信息，希望快速录入

        【返回格式】
        返回包含ACTION指令的文本，使用page_code标识目标页面
        """)
    public String createProjectForm(
            @McpToolParam(description = "项目名称") String projectName,
            @McpToolParam(description = "供应商名称（可选）") String supplierName,
            @McpToolParam(description = "商品名称（可选）") String goodsName,
            @McpToolParam(description = "商品规格（可选）") String spec,
            @McpToolParam(description = "数量（可选）") Double qty,
            @McpToolParam(description = "单价（可选）") Double price,
            @McpToolParam(description = "备注（可选）") String remark) {

        log.info("MCP工具调用 - 创建项目表单: projectName={}", projectName);

        // 1. 构建表单数据
        Map<String, Object> formData = new HashMap<>();
        formData.put("name", projectName);

        if (supplierName != null && !supplierName.isEmpty()) {
            formData.put("supplier_name", supplierName);
        }
        if (goodsName != null && !goodsName.isEmpty()) {
            formData.put("goods_name", goodsName);
        }
        if (spec != null && !spec.isEmpty()) {
            formData.put("spec", spec);
        }
        if (qty != null) {
            formData.put("qty", qty);
        }
        if (price != null) {
            formData.put("price", price);
        }
        if (remark != null && !remark.isEmpty()) {
            formData.put("remark", remark);
        }

        // 2. 构建ACTION指令 - 使用 page_code 而不是 URL
        Map<String, Object> action = new HashMap<>();
        action.put("type", "FORM_FILL");
        action.put("action", "OPEN_NEW");
        action.put("target_page_code", "B_PROJECT");  // 页面标识符，不是URL！
        action.put("formData", formData);

        String actionJson = JSON.toJSONString(action);

        // 3. 返回带指令的响应
        return String.format(
            "已为您准备好项目表单。\n\n" +
            "<!--ACTION:%s-->\n\n" +
            "请在弹出的表单中确认信息，然后点击提交。",
            actionJson
        );
    }

    /**
     * 通用表单填充工具
     * 支持任意页面的表单填充
     */
    @McpTool(description = """
        【工具名称】通用表单填充

        【核心功能】
        根据用户描述，为指定页面的表单填充数据。

        【参数说明】
        - targetPageCode: 目标页面标识符，如 B_PROJECT, B_PO_ORDER 等
        - formData: JSON格式的表单数据

        【已知的 page_code 列表】
        - B_PROJECT: 项目管理
        - B_PO_ORDER: 采购订单
        - B_SO_ORDER: 销售订单
        - B_PO_CONTRACT: 采购合同
        - B_SO_CONTRACT: 销售合同
        """)
    public String fillForm(
            @McpToolParam(description = "目标页面标识符（page_code）") String targetPageCode,
            @McpToolParam(description = "表单数据JSON") String formDataJson) {

        log.info("MCP工具调用 - 通用表单填充: targetPageCode={}", targetPageCode);

        // 解析表单数据
        Map<String, Object> formData;
        try {
            formData = JSON.parseObject(formDataJson, Map.class);
        } catch (Exception e) {
            return "表单数据格式错误: " + e.getMessage();
        }

        // 构建ACTION指令
        Map<String, Object> action = new HashMap<>();
        action.put("type", "FORM_FILL");
        action.put("action", "OPEN_NEW");
        action.put("target_page_code", targetPageCode);
        action.put("formData", formData);

        String actionJson = JSON.toJSONString(action);

        return String.format(
            "<!--ACTION:%s-->\n\n" +
            "表单已准备好，请确认信息后提交。",
            actionJson
        );
    }
}
```

---

### 4.3 前端实现

#### 4.3.1 流式响应解析器

```javascript
// src/utils/ai-stream-parser.js

/**
 * AI流式响应解析器
 *
 * 功能：
 * 1. 解析SSE流式响应
 * 2. 识别并提取ACTION指令
 * 3. 分离普通文本和指令
 */

// ACTION指令的正则匹配
const ACTION_PATTERN = /<!--ACTION:(.*?)-->/g

/**
 * 流式响应处理器类
 * 处理可能跨块的ACTION指令
 */
export class StreamParser {
  constructor() {
    this.buffer = ''
    this.onText = null
    this.onAction = null
  }

  /**
   * 设置回调
   */
  setCallbacks({ onText, onAction }) {
    this.onText = onText
    this.onAction = onAction
  }

  /**
   * 处理新的响应块
   */
  processChunk(chunk) {
    this.buffer += chunk

    // 检查是否有完整的ACTION指令
    const actionStart = this.buffer.indexOf('<!--ACTION:')
    const actionEnd = this.buffer.indexOf('-->')

    if (actionStart === -1) {
      // 没有ACTION指令，直接输出文本
      if (this.onText && this.buffer) {
        this.onText(this.buffer)
        this.buffer = ''
      }
      return
    }

    if (actionEnd === -1 || actionEnd < actionStart) {
      // ACTION指令不完整，等待更多数据
      if (actionStart > 0 && this.onText) {
        this.onText(this.buffer.substring(0, actionStart))
        this.buffer = this.buffer.substring(actionStart)
      }
      return
    }

    // 有完整的ACTION指令
    // 1. 输出ACTION之前的文本
    if (actionStart > 0 && this.onText) {
      this.onText(this.buffer.substring(0, actionStart))
    }

    // 2. 提取并执行ACTION
    const actionContent = this.buffer.substring(actionStart + 11, actionEnd)
    try {
      const action = JSON.parse(actionContent)
      if (this.onAction) {
        this.onAction(action)
      }
    } catch (e) {
      console.error('解析ACTION失败:', e)
    }

    // 3. 处理剩余内容
    this.buffer = this.buffer.substring(actionEnd + 3)

    // 递归处理，以防有多个ACTION
    if (this.buffer) {
      this.processChunk('')
    }
  }

  /**
   * 流结束时调用，处理剩余缓冲区
   */
  flush() {
    if (this.buffer && this.onText) {
      this.onText(this.buffer)
      this.buffer = ''
    }
  }
}
```

#### 4.3.2 指令执行器（使用 page_code）

```javascript
// src/utils/ai-action-executor.js

import Vue from 'vue'
import { EventBus } from '@/common/eventbus/eventbus'

/**
 * AI指令执行器
 *
 * 核心改进：
 * - 使用 page_code 标识目标页面，不依赖 URL 路径
 * - 通过 EventBus 广播事件，由匹配的页面组件处理
 */

/**
 * 执行AI指令
 * @param {object} action - 指令对象
 */
export function executeAction(action) {
  console.log('执行AI指令:', action)

  switch (action.type) {
    case 'FORM_FILL':
      handleFormFill(action)
      break

    case 'NAVIGATE':
      handleNavigate(action)
      break

    case 'NOTIFY':
      handleNotify(action)
      break

    case 'CONFIRM':
      handleConfirm(action)
      break

    default:
      console.warn('未知的指令类型:', action.type)
  }
}

/**
 * 处理表单填充指令
 *
 * 核心逻辑：
 * 1. 通过 EventBus 广播事件
 * 2. 所有页面组件都监听此事件
 * 3. 只有 page_code 匹配的页面才响应
 */
function handleFormFill(action) {
  const { target_page_code, formData, action: fillAction, recordId } = action

  // 广播事件，由匹配的页面组件处理
  EventBus.$emit('ai-form-fill', {
    target_page_code,
    action: fillAction || 'OPEN_NEW',
    formData,
    recordId
  })

  console.log(`已广播 ai-form-fill 事件，目标页面: ${target_page_code}`)
}

/**
 * 处理导航指令
 */
function handleNavigate(action) {
  const { target_url, query, params } = action

  // 导航仍然可以用 URL，因为这是显式导航请求
  if (target_url) {
    import('@/router').then(({ default: router }) => {
      router.push({
        path: target_url,
        query: query || {},
        params: params || {}
      })
    })
  }
}

/**
 * 处理通知指令
 */
function handleNotify(action) {
  const { message, level = 'info' } = action

  Vue.prototype.$message({
    message,
    type: level,
    duration: 3000
  })
}

/**
 * 处理确认指令
 */
function handleConfirm(action) {
  const { title, content, confirmCallback } = action

  Vue.prototype.$confirm(content, title, {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    if (confirmCallback) {
      EventBus.$emit('ai-confirm', { callback: confirmCallback, confirmed: true })
    }
  }).catch(() => {
    if (confirmCallback) {
      EventBus.$emit('ai-confirm', { callback: confirmCallback, confirmed: false })
    }
  })
}
```

#### 4.3.3 业务页面父容器（核心：Tab切换 + page_code匹配）

```vue
<!-- src/views/40_business/10_po/project/index.vue -->

<template>
  <div>
    <el-tabs
      ref="refTabs"
      v-model="settings.tabs.activeName"
      @tab-click="handleTabsClick"
      @tab-remove="handleRemoveTab"
    >
      <!-- 项目管理-列表 -->
      <el-tab-pane name="main">
        <template slot="label">项目管理-列表</template>
        <list_template
          :data="dataJson.data"
          @emitView="handleView"
          @emitNew="handleNew"
          @emitUpdate="handleUpdate"
          @emitApprove="handleApprove"
        />
      </el-tab-pane>

      <!-- 新增 -->
      <el-tab-pane v-if="dataJson.tab.showNew" name="edit" closable>
        <template slot="label">{{ dataJson.tab.name }}</template>
        <new_template
          ref="newTemplate"
          :data="dataJson.data"
          :edit-status="dataJson.editStatus"
          @closeMeCancel="handleReturn"
          @closeMeOk="handleCloseMeOk"
        />
      </el-tab-pane>

      <!-- 其他Tab... -->
    </el-tabs>
  </div>
</template>

<script>
import { EventBus } from '@/common/eventbus/eventbus'
import list_template from './tabs/10_list/index.vue'
import new_template from './tabs/20_new/index.vue'
// ... 其他导入

export default {
  components: { list_template, new_template /* ... */ },

  data() {
    return {
      dataJson: {
        tab: {
          showMain: true,
          showNew: false,
          showUpdate: false,
          showView: false,
          showApprove: false,
          name: ''
        },
        data: {},
        editStatus: ''
      },
      settings: {
        tabs: {
          activeName: 'main'
        }
      }
    }
  },

  created() {
    // 设置页面标识 - 这是匹配的关键！
    this.$options.name = this.$route.meta.page_code

    // 监听AI表单填充事件
    EventBus.$on('ai-form-fill', this.handleAiFormFill)
  },

  beforeDestroy() {
    // 组件销毁时取消监听
    EventBus.$off('ai-form-fill', this.handleAiFormFill)
  },

  methods: {
    /**
     * 处理AI表单填充 - 核心方法
     *
     * 关键：使用 page_code 匹配，不用 URL 路径
     */
    handleAiFormFill(payload) {
      const { target_page_code, action, formData, recordId } = payload

      // 核心判断：只有当 page_code 匹配时才处理
      if (target_page_code !== this.$route.meta.page_code) {
        console.log(`page_code 不匹配，忽略。期望: ${target_page_code}, 当前: ${this.$route.meta.page_code}`)
        return
      }

      console.log('page_code 匹配，处理AI表单填充:', payload)

      switch (action) {
        case 'OPEN_NEW':
          // 模拟点击新增按钮的效果
          this.handleNew({
            data: formData,  // 传入AI提供的数据
            editStatus: 'new',
            operate_tab_info: { name: '项目管理-新增' }
          })

          // 等待新增页面渲染后，填充表单
          this.$nextTick(() => {
            setTimeout(() => {
              if (this.$refs.newTemplate) {
                this.fillNewFormWithAiData(formData)
              }
            }, 100)
          })

          // 提示用户
          this.$message({
            message: 'AI已为您打开新增表单并填充数据',
            type: 'success',
            duration: 3000
          })
          break

        case 'OPEN_EDIT':
          // 编辑模式
          this.handleUpdate({
            data: { id: recordId, ...formData },
            editStatus: 'edit',
            operate_tab_info: { name: '项目管理-修改' }
          })
          break

        case 'FILL_ONLY':
          // 仅填充，不切换Tab（当前已在对应Tab时）
          if (this.dataJson.tab.showNew && this.$refs.newTemplate) {
            this.fillNewFormWithAiData(formData)
          }
          break
      }
    },

    /**
     * 填充新增表单数据
     */
    fillNewFormWithAiData(formData) {
      // 方式1：直接操作子组件的 dataJson
      if (this.$refs.newTemplate && this.$refs.newTemplate.dataJson) {
        const targetJson = this.$refs.newTemplate.dataJson.tempJson

        // 映射AI数据到表单字段
        if (formData.name) targetJson.name = formData.name
        if (formData.supplier_name) targetJson.supplier_name = formData.supplier_name
        if (formData.supplier_id) targetJson.supplier_id = formData.supplier_id
        if (formData.purchaser_name) targetJson.purchaser_name = formData.purchaser_name
        if (formData.purchaser_id) targetJson.purchaser_id = formData.purchaser_id
        if (formData.remark) targetJson.remark = formData.remark
        if (formData.delivery_type) targetJson.delivery_type = formData.delivery_type
        if (formData.delivery_location) targetJson.delivery_location = formData.delivery_location
        // ... 其他字段
      }
    },

    /**
     * 原有的新增方法
     */
    handleNew(_data) {
      this.dataJson.data = _data.data
      this.dataJson.tab = _data.operate_tab_info
      this.settings.tabs.activeName = 'edit'
      this.dataJson.tab.showNew = true
      this.dataJson.tab.showUpdate = false
      this.dataJson.tab.showView = false
      this.dataJson.tab.showMain = false
      this.dataJson.editStatus = _data.editStatus
    },

    // ... 其他原有方法保持不变
  }
}
</script>
```

#### 4.3.4 新增页面组件（可选：提供 setFormData 方法）

```vue
<!-- src/views/40_business/10_po/project/tabs/20_new/index.vue -->

<script>
export default {
  // ... 现有代码

  methods: {
    // ... 现有方法

    /**
     * 供父组件调用，设置表单数据
     * （可选实现，如果父组件直接操作 dataJson.tempJson 也可以）
     */
    setFormData(data) {
      if (!data) return

      // 批量设置表单数据
      Object.keys(data).forEach(key => {
        if (this.dataJson.tempJson.hasOwnProperty(key)) {
          this.dataJson.tempJson[key] = data[key]
        }
      })

      console.log('表单数据已设置:', this.dataJson.tempJson)
    }
  }
}
</script>
```

---

## 五、完整交互流程

### 5.1 时序图

```
┌─────┐       ┌──────────┐       ┌─────────┐       ┌──────────────┐
│用户 │       │ AI Chat  │       │ 后端AI  │       │项目管理页     │
└──┬──┘       └────┬─────┘       └────┬────┘       │(page_code:   │
   │               │                  │            │ B_PROJECT)   │
   │               │                  │            └──────┬───────┘
   │ "创建项目"    │                  │                   │
   │──────────────▶│                  │                   │
   │               │                  │                   │
   │               │ POST /chat/stream│                   │
   │               │─────────────────▶│                   │
   │               │                  │                   │
   │               │                  │ 调用MCP工具       │
   │               │                  │ 构建表单JSON      │
   │               │                  │ 设置target_page   │
   │               │                  │ _code="B_PROJECT" │
   │               │                  │                   │
   │               │   SSE流式响应    │                   │
   │               │◀─────────────────│                   │
   │               │  "好的，我来..." │                   │
   │               │  <!--ACTION:...  │                   │
   │               │  target_page_code│                   │
   │               │  ="B_PROJECT"... │                   │
   │               │  -->             │                   │
   │               │                  │                   │
   │  显示文字     │                  │                   │
   │◀──────────────│                  │                   │
   │               │                  │                   │
   │               │ 解析到ACTION     │                   │
   │               │ EventBus.$emit   │                   │
   │               │ ('ai-form-fill') │                   │
   │               │──────────────────────────────────────▶
   │               │                  │                   │
   │               │                  │  检查 page_code   │
   │               │                  │  匹配！执行操作   │
   │               │                  │  handleNew()      │
   │               │                  │  切换到新增Tab    │
   │               │                  │  填充表单数据     │
   │               │                  │                   │
   │  看到填充好的表单                │                   │
   │◀──────────────────────────────────────────────────────
```

### 5.2 page_code 匹配示例

```javascript
// 假设有多个页面组件都在监听 ai-form-fill 事件

// 项目管理页 (page_code: B_PROJECT)
handleAiFormFill(payload) {
  if (payload.target_page_code !== 'B_PROJECT') return  // 不匹配，忽略
  // 处理...
}

// 采购订单页 (page_code: B_PO_ORDER)
handleAiFormFill(payload) {
  if (payload.target_page_code !== 'B_PO_ORDER') return  // 不匹配，忽略
  // 处理...
}

// 当AI发送 target_page_code: "B_PROJECT" 时：
// - 项目管理页：匹配，执行操作
// - 采购订单页：不匹配，忽略
```

---

## 六、实际效果示例

```
用户输入: "帮我创建一个采购项目，供应商是阿萨泽泽水，商品是矿泉水，数量11"

↓ AI流式响应

对话框显示:
┌────────────────────────────────────────────────┐
│ AI: 好的，我来帮您创建采购项目。              │
│                                                │
│     表单已准备好，请确认信息后点击提交。       │
└────────────────────────────────────────────────┘

同时自动切换到新增Tab，表单已填充:
┌────────────────────────────────────────────────┐
│ [项目管理-列表] [项目管理-新增 x]              │
├────────────────────────────────────────────────┤
│  基本信息                                      │
│  ─────────────────────────────────────────     │
│  项目编号: 系统自动生成                        │
│  项目名称: [采购项目-阿萨泽泽水          ]    │
│  类型:     采购业务                            │
│  上游供应商: [阿萨泽泽水                  ]    │
│  下游客户:   [                            ]    │
│                                                │
│  商品明细                                      │
│  ─────────────────────────────────────────     │
│  [新增] [删除]                                 │
│  ┌──────┬──────┬────┬──────┬──────┐           │
│  │商品  │规格  │数量│单价  │金额  │           │
│  ├──────┼──────┼────┼──────┼──────┤           │
│  │矿泉水│-     │11  │-     │-     │           │
│  └──────┴──────┴────┴──────┴──────┘           │
│                                                │
│              [返回]  [提交审批并保存]          │
└────────────────────────────────────────────────┘
```

---

## 七、方案优势

### 7.1 与硬编码URL对比

| 对比项 | 硬编码URL | page_code方案 |
|--------|-----------|--------------|
| 菜单调整时 | 需要改代码 | **无需改动** |
| 多租户场景 | 可能不同 | **统一标识** |
| 可维护性 | 差 | **好** |
| 可读性 | 路径无业务含义 | **业务标识清晰** |

### 7.2 核心优势

1. **零额外连接**：复用AI Chat现有的SSE流式输出
2. **page_code标识**：不依赖URL路径，菜单调整不影响功能
3. **Tab切换机制**：在父容器层面处理，复用现有的Tab切换逻辑
4. **EventBus广播**：解耦AI Chat组件和业务页面
5. **优雅降级**：即使前端没解析，指令也不会显示（HTML注释）

---

## 八、配置与维护

### 8.1 page_code 维护

page_code 存储在数据库的菜单配置中，通过 `$route.meta.page_code` 获取。

常用 page_code 列表：

| page_code | 页面 | 说明 |
|-----------|------|------|
| B_PROJECT | 项目管理 | 采购项目 |
| B_PO_ORDER | 采购订单 | PO订单 |
| B_PO_CONTRACT | 采购合同 | PO合同 |
| B_SO_ORDER | 销售订单 | SO订单 |
| B_SO_CONTRACT | 销售合同 | SO合同 |

### 8.2 新增页面支持

要让新页面支持AI表单填充，只需：

1. 在 `created()` 中监听事件：
```javascript
EventBus.$on('ai-form-fill', this.handleAiFormFill)
```

2. 实现 `handleAiFormFill` 方法，根据 page_code 匹配处理

3. 在 MCP 工具中添加对应的 page_code

---

## 九、总结

### 9.1 核心方案

**SSE流式输出 + 指令协议 + page_code标识**

```
AI响应流 = 普通文本 + <!--ACTION:{target_page_code:"B_PROJECT",...}-->
```

### 9.2 实现要点

1. **后端**：MCP工具返回值中嵌入 `<!--ACTION:...-->` 指令，使用 `target_page_code` 标识目标页面
2. **前端流解析**：提取指令，通过 EventBus 广播
3. **业务页面**：监听事件，匹配 `page_code`，触发 Tab 切换和表单填充

### 9.3 为什么用 page_code 而不是 URL

- URL 是动态配置的，存储在数据库中
- 菜单调整会导致 URL 变化
- page_code 是业务标识，稳定不变
- 匹配更可靠，维护更简单

---

**报告创建时间**：2025-12-04
**报告版本**：v3.0（page_code + Tab切换版）
**核心改进**：
- v1.0: WebSocket方案
- v2.0: SSE流式+指令协议（移除WebSocket）
- v3.0: page_code标识 + Tab切换机制（移除URL硬编码）

**适用项目**：SCM Backend/Frontend - AI Module
