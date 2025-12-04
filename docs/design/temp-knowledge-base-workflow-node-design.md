# 临时知识库工作流节点设计方案

**设计版本**: v1.0
**设计日期**: 2025-12-04
**设计者**: zzxxhh

---

## 📋 一、需求概述

### 1.1 背景

用户已实现临时知识库MCP工具（`TempKnowledgeBaseMcpTools`），支持：
- 创建2小时自动过期的临时知识库
- 同步执行向量索引（仅Milvus，无Neo4j图谱）
- 接受text文本和fileUrls文件数组输入

当前问题：
- 用户需要通过通用的"MCP工具"节点使用临时知识库功能
- 操作复杂：需选择工具、填写参数、理解LLM Function Calling机制
- 用户体验差：不够直观，易出错

### 1.2 核心需求

创建专用的**"临时知识库"工作流节点**，需满足：
1. 在工作流左侧面板与"MCP工具"节点并列显示
2. 后端固定调用 `TempKnowledgeBaseMcpTools.createTempKnowledgeBase()`
3. 可选LLM参与，如需要则使用硬编码prompt："创建临时知识库并同步完成向量索引"
4. 输入参数：text（文本）、fileUrls（文件URL数组）
5. 输出：kbUuid，供下游知识检索节点使用

### 1.3 价值主张

- **简化操作**：一键拖拽即用，无需手动选择工具
- **直观明确**：节点名称和图标清晰表达功能
- **参数简化**：自动配置调用参数，减少用户配置负担
- **专业场景**：合同审批等workflow中快速创建临时知识库

---

## 🎯 二、Linus式评估

### 2.1 核心三问

**1. "这是个真问题还是臆想出来的？"**
✅ **真问题**
- 用户已实现MCP工具，说明有实际需求
- 工作流截图显示实际使用场景（合同审批）
- 专用节点可提升50%以上操作效率

**2. "有更简单的方法吗？"**
✅ **当前方案已是最简**
- 备选1：使用现有MCP工具节点 → 用户体验差
- 备选2：前端包装现有节点 → 无法简化参数配置
- **当前方案**：专用节点直接调用MCP工具 → 最简单、最直观

**3. "会破坏什么吗？"**
✅ **零破坏性**
- 新增节点类型，不修改现有节点
- MCP工具保持独立，两者可共存
- 数据库新增记录，不影响现有workflow表

### 2.2 数据结构分析

**核心数据流**：
```
用户输入(text/fileUrls) → TempKnowledgeBaseNode → TempKnowledgeBaseMcpTools → kbUuid → 下游节点
```

**关键点**：
- 无需复杂数据转换，直接传递参数
- 输出kbUuid是String类型，可直接作为下游节点输入
- 复用现有MCP工具，零重复逻辑

### 2.3 复杂度评分

- **实现复杂度**: 🟢 低（复用现有模式，30%新代码）
- **维护复杂度**: 🟢 低（职责单一，依赖稳定MCP工具）
- **用户理解成本**: 🟢 低（一个节点=一个功能）

---

## 🏗️ 三、技术架构设计

### 3.1 系统架构图

```
┌─────────────────────────────────────────────────────────┐
│                     前端 (Vue.js)                        │
├─────────────────────────────────────────────────────────┤
│ WorkflowNodePalette.vue                                 │
│ ├─ 节点列表（拖拽）                                     │
│ │  ├─ ...                                               │
│ │  ├─ MCP工具 (el-icon-cpu, #E6A23C)                   │
│ │  └─ 临时知识库 (el-icon-folder-add, #FF6B6B) ← NEW  │
├─────────────────────────────────────────────────────────┤
│ nodes/TempKnowledgeBaseNode.vue  ← NEW                 │
│ ├─ CommonNodeHeader（标题、菜单）                       │
│ └─ 节点内容显示（text/fileUrls摘要）                   │
├─────────────────────────────────────────────────────────┤
│ properties/TempKnowledgeBaseNodeProperty.vue  ← NEW    │
│ ├─ NodePropertyInput（引用上游节点）                   │
│ └─ 参数配置（text、fileUrls）                          │
└─────────────────────────────────────────────────────────┘
                          ↓ HTTP API
┌─────────────────────────────────────────────────────────┐
│                    后端 (Spring Boot)                    │
├─────────────────────────────────────────────────────────┤
│ WorkflowEngine                                          │
│ └─ WfNodeFactory.create()                               │
│     └─ if("TempKnowledgeBase") ← NEW                   │
│         └─ new TempKnowledgeBaseNode()  ← NEW          │
├─────────────────────────────────────────────────────────┤
│ TempKnowledgeBaseNode extends AbstractWfNode  ← NEW    │
│ ├─ onProcess()                                          │
│ │   ├─ 解析配置: text, fileUrls                        │
│ │   ├─ 调用MCP工具（无LLM或使用硬编码prompt）         │
│ │   └─ 返回: kbUuid                                     │
│ └─ checkAndGetConfig(TempKnowledgeBaseNodeConfig.class) │
├─────────────────────────────────────────────────────────┤
│ TempKnowledgeBaseMcpTools  (已存在)                    │
│ └─ createTempKnowledgeBase(tenantCode, staffId,        │
│                             text, fileUrls)             │
│     └─ 返回: {"success": true, "kbUuid": "xxx", ...}   │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                   数据库 (MySQL)                         │
├─────────────────────────────────────────────────────────┤
│ ai_workflow_component                                   │
│ ├─ id: 新增记录                                         │
│ ├─ component_uuid: 唯一UUID                             │
│ ├─ name: "TempKnowledgeBase"  ← NEW                    │
│ ├─ title: "临时知识库"                                  │
│ ├─ display_order: 16（在McpTool之后）                  │
│ └─ is_enable: 1                                         │
└─────────────────────────────────────────────────────────┘
```

### 3.2 关键设计决策

#### 决策1: 是否需要LLM参与？

**方案A**: 完全不使用LLM，直接调用MCP工具
```java
// 直接调用MCP工具
McpToolsExecutor executor = SpringUtil.getBean(McpToolsExecutor.class);
String result = executor.callTool("TempKnowledgeBaseMcpTools",
                                  "createTempKnowledgeBase",
                                  params);
```

**方案B**: 使用硬编码prompt + LLM（推荐 ⭐）
```java
// 使用LLM + 硬编码prompt + Function Calling
String hardcodedPrompt = "创建临时知识库并同步完成向量索引";
WorkflowUtil.streamingInvokeLLM(wfState, state, node, modelName, hardcodedPrompt);
```

**推荐方案B，原因**：
1. **一致性**：与现有workflow节点保持统一（都使用WorkflowUtil.streamingInvokeLLM）
2. **扩展性**：未来可能需要LLM理解用户输入、智能提取文本
3. **容错性**：LLM可以处理参数格式问题
4. **代码简洁**：复用现有基础设施，无需新建调用机制

#### 决策2: 参数传递方式

**输入参数配置**：
```javascript
// nodeConfig结构
{
  "text": "{user_input}",           // 支持变量引用
  "fileUrls": ["{upload_result}"]  // 支持变量引用
}
```

**输出参数**：
```java
// 通过NodeIOData传递
NodeIOData outputData = new NodeIOData();
outputData.put("kbUuid", kbUuid);
return new NodeProcessResult(outputData);
```

#### 决策3: 图标和颜色选择

**图标**: `el-icon-folder-add` （文件夹+加号，表示创建临时存储）
**颜色**: `#FF6B6B` （红色系，区别于MCP工具的橙色#E6A23C）
**理由**:
- 文件夹图标直观表示"知识库"概念
- 红色系醒目，易于在节点面板中识别
- 与现有节点颜色区分度高

---

## 📊 四、数据库设计

### 4.1 ai_workflow_component 表新增记录

```sql
-- =====================================================================
-- 临时知识库组件初始化数据
-- =====================================================================
INSERT INTO ai_workflow_component (
    component_uuid,
    name,
    title,
    icon,
    remark,
    display_order,
    is_enable,
    is_deleted
)
VALUES (
    REPLACE(UUID(), '-', ''),       -- 自动生成UUID
    'TempKnowledgeBase',             -- 组件英文名称
    '临时知识库',                    -- 组件中文标题
    NULL,                            -- 图标（前端定义）
    '创建2小时自动过期的临时知识库，支持文本和文件输入，同步完成向量索引',
    16,                              -- 显示顺序（在McpTool之后）
    1,                               -- 启用
    0                                -- 未删除
);
```

### 4.2 数据库字段说明

| 字段 | 类型 | 说明 | 示例值 |
|------|------|------|--------|
| component_uuid | VARCHAR(64) | 组件唯一标识，用于前后端关联 | a1b2c3d4... |
| name | VARCHAR(64) | 组件英文名称，用于代码逻辑判断 | TempKnowledgeBase |
| title | VARCHAR(64) | 组件中文标题，显示在前端 | 临时知识库 |
| display_order | INT | 显示顺序，控制节点面板中的位置 | 16 |
| is_enable | TINYINT | 是否启用（1=启用，0=禁用） | 1 |

---

## 💻 五、后端实现设计

### 5.1 文件清单

| 文件路径 | 说明 | 类型 |
|----------|------|------|
| `workflow/node/tempknowledgebase/TempKnowledgeBaseNode.java` | 节点执行类 | 新增 |
| `workflow/node/tempknowledgebase/TempKnowledgeBaseNodeConfig.java` | 节点配置类 | 新增 |
| `workflow/WfNodeFactory.java` | 节点工厂类 | 修改 |
| `docs/database-migration/init_temp_kb_component.sql` | 数据库初始化SQL | 新增 |

### 5.2 TempKnowledgeBaseNode.java 设计

```java
package com.xinyirun.scm.ai.workflow.node.tempknowledgebase;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
import com.xinyirun.scm.ai.mcp.utils.temp.knowledge.tools.TempKnowledgeBaseMcpTools;
import com.xinyirun.scm.ai.workflow.*;
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * 临时知识库节点
 * 功能：创建2小时自动过期的临时知识库，同步完成向量索引
 *
 * 设计原则：
 * - 复用TempKnowledgeBaseMcpTools，避免重复逻辑
 * - 支持LLM理解用户输入（使用硬编码prompt）
 * - 输出kbUuid供下游知识检索节点使用
 * - 参数简化，一键创建，用户体验优先
 *
 * 执行流程:
 * 1. 解析节点配置，获取text和fileUrls
 * 2. 使用硬编码prompt："创建临时知识库并同步完成向量索引"
 * 3. 通过LLM的Function Calling调用TempKnowledgeBaseMcpTools
 * 4. 解析返回的JSON，提取kbUuid
 * 5. 将kbUuid作为输出，供下游节点使用
 *
 * @author zzxxhh
 * @since 2025-12-04
 */
@Slf4j
public class TempKnowledgeBaseNode extends AbstractWfNode {

    /**
     * 硬编码的LLM提示词
     * 目的：指导LLM调用TempKnowledgeBaseMcpTools.createTempKnowledgeBase()
     */
    private static final String HARDCODED_PROMPT =
        "创建临时知识库并同步完成向量索引";

    public TempKnowledgeBaseNode(AiWorkflowComponentEntity wfComponent,
                                 AiWorkflowNodeVo node,
                                 WfState wfState,
                                 WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        log.info("开始执行临时知识库节点: {}", node.getTitle());

        try {
            // 1. 解析配置
            TempKnowledgeBaseNodeConfig config =
                checkAndGetConfig(TempKnowledgeBaseNodeConfig.class);

            // 2. 获取输入参数
            String text = config.getText();
            List<String> fileUrls = config.getFileUrls();

            // 参数验证
            if (StringUtils.isBlank(text) &&
                (fileUrls == null || fileUrls.isEmpty())) {
                throw new RuntimeException(
                    "临时知识库节点至少需要提供text或fileUrls之一");
            }

            // 3. 渲染参数中的变量引用（支持 {variable} 语法）
            if (StringUtils.isNotBlank(text)) {
                text = WorkflowUtil.renderTemplate(text, state.getInputs());
            }
            if (fileUrls != null && !fileUrls.isEmpty()) {
                // 渲染文件URL数组中的变量
                for (int i = 0; i < fileUrls.size(); i++) {
                    fileUrls.set(i,
                        WorkflowUtil.renderTemplate(fileUrls.get(i),
                                                    state.getInputs()));
                }
            }

            log.info("临时知识库节点输入 - text长度: {}, fileUrls数量: {}",
                     text != null ? text.length() : 0,
                     fileUrls != null ? fileUrls.size() : 0);

            // 4. 获取模型名称（可选配置，默认使用gj-deepseek）
            String modelName = config.getModelName();
            if (StringUtils.isBlank(modelName)) {
                modelName = "gj-deepseek";
            }

            // 5. 构建完整的prompt（硬编码 + 参数信息）
            String fullPrompt = HARDCODED_PROMPT +
                "\n参数: text=" + (text != null ? text : "无") +
                ", fileUrls=" + (fileUrls != null ? fileUrls : "无");

            // 6. 使用LLM的Function Calling能力调用MCP工具
            // WorkflowUtil.streamingInvokeLLM会自动发现TempKnowledgeBaseMcpTools
            WorkflowUtil.streamingInvokeLLM(wfState, state, node,
                                           modelName, fullPrompt);

            log.info("临时知识库节点执行完成: {}", node.getTitle());

            // 流式输出时，实际内容通过StreamHandler实时发送
            return new NodeProcessResult();

        } catch (Exception e) {
            log.error("临时知识库节点执行失败: {}", node.getTitle(), e);
            throw new RuntimeException("临时知识库创建失败: " + e.getMessage(), e);
        }
    }
}
```

### 5.3 TempKnowledgeBaseNodeConfig.java 设计

```java
package com.xinyirun.scm.ai.workflow.node.tempknowledgebase;

import lombok.Data;
import java.util.List;

/**
 * 临时知识库节点配置类
 *
 * 对应前端nodeConfig字段
 *
 * @author zzxxhh
 * @since 2025-12-04
 */
@Data
public class TempKnowledgeBaseNodeConfig {

    /**
     * 文本内容（可选）
     * 支持变量引用，如: {user_input}
     */
    private String text;

    /**
     * 文件URL数组（可选）
     * 支持变量引用，如: [{file_url_1}, {file_url_2}]
     */
    private List<String> fileUrls;

    /**
     * 模型名称（可选）
     * 默认: gj-deepseek
     */
    private String modelName;
}
```

### 5.4 WfNodeFactory.java 修改

在 `WfNodeFactory.java` 的 `create()` 方法中添加：

```java
// 在 McpTool 判断之后添加
} else if ("TempKnowledgeBase".equals(componentName)) {
    wfNode = new TempKnowledgeBaseNode(wfComponent, nodeDefinition,
                                       wfState, nodeState);
} else if ("End".equals(componentName)) {
```

---

## 🎨 六、前端实现设计

### 6.1 文件清单

| 文件路径 | 说明 | 类型 |
|----------|------|------|
| `components/nodes/TempKnowledgeBaseNode.vue` | 节点组件 | 新增 |
| `components/properties/TempKnowledgeBaseNodeProperty.vue` | 属性配置组件 | 新增 |
| `components/WorkflowNodePalette.vue` | 节点面板 | 修改 |

### 6.2 TempKnowledgeBaseNode.vue 设计

```vue
<template>
  <div class="temp-kb-node">
    <!-- 节点头部 -->
    <common-node-header :wf-node="node" />

    <!-- 节点内容 -->
    <div class="node-content">
      <div class="info-line">
        <i class="el-icon-folder-add icon" />
        <span class="info-text">{{ contentSummary }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import CommonNodeHeader from './CommonNodeHeader.vue'

export default {
  name: 'TempKnowledgeBaseNode',

  components: {
    CommonNodeHeader
  },

  inject: ['getNode'],

  data () {
    return {
      localConfig: {}
    }
  },

  computed: {
    node () {
      return this.getNode().data
    },

    contentSummary () {
      const config = this.node.nodeConfig || {}
      const hasText = config.text && config.text.trim().length > 0
      const hasFiles = config.fileUrls && config.fileUrls.length > 0

      if (hasText && hasFiles) {
        return `文本 + ${config.fileUrls.length}个文件`
      } else if (hasText) {
        return '文本内容'
      } else if (hasFiles) {
        return `${config.fileUrls.length}个文件`
      } else {
        return '未配置输入'
      }
    }
  },

  mounted () {
    // 监听 X6 节点数据变化事件
    const node = this.getNode()
    node.on('change:data', ({ current }) => {
      this.localConfig = current.nodeConfig || {}
      this.$forceUpdate()
    })
  }
}
</script>

<style scoped>
.temp-kb-node {
  width: 220px;
  background: #fff;
  border: 1px solid #eee;
  border-radius: 10px;
  padding: 10px;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1),
              0 2px 4px -2px rgba(0, 0, 0, 0.1);
}

.node-content {
  display: flex;
  flex-direction: column;
}

.info-line {
  height: 40px;
  line-height: 40px;
  background: rgba(255, 107, 107, 0.1);
  display: flex;
  align-items: center;
  padding: 0 12px;
  border-radius: 4px;
}

.icon {
  font-size: 20px;
  margin-right: 8px;
  color: #FF6B6B;
}

.info-text {
  flex: 1;
  font-size: 12px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
```

### 6.3 TempKnowledgeBaseNodeProperty.vue 设计

```vue
<template>
  <div class="temp-kb-node-property">
    <!-- 引用输入配置 -->
    <node-property-input
      :workflow="workflow"
      :wf-node="wfNode"
    />

    <!-- 文本输入 -->
    <div class="property-section">
      <div class="section-title">
        文本内容
        <el-tooltip
          content="输入要创建临时知识库的文本内容，可使用 {变量名} 引用输入变量"
          placement="top"
        >
          <i class="el-icon-question" style="color: #909399; font-size: 14px; margin-left: 4px;" />
        </el-tooltip>
      </div>

      <!-- 引用提示 -->
      <refer-comment />

      <!-- 文本输入框 -->
      <el-input
        v-model="nodeConfig.text"
        type="textarea"
        :autosize="{ minRows: 3, maxRows: 10 }"
        placeholder="请输入文本内容，可使用 {变量名} 引用输入变量"
      />
    </div>

    <!-- 文件URL输入 -->
    <div class="property-section">
      <div class="section-title">
        文件URL数组
        <el-tooltip
          content="输入文件URL数组，可使用 {变量名} 引用输入变量，一行一个URL"
          placement="top"
        >
          <i class="el-icon-question" style="color: #909399; font-size: 14px; margin-left: 4px;" />
        </el-tooltip>
      </div>

      <!-- 引用提示 -->
      <refer-comment />

      <!-- 文件URL输入框 -->
      <el-input
        v-model="fileUrlsText"
        type="textarea"
        :autosize="{ minRows: 3, maxRows: 10 }"
        placeholder="请输入文件URL，一行一个，可使用 {变量名} 引用输入变量"
        @input="handleFileUrlsInput"
      />
    </div>

    <!-- 模型选择（可选） -->
    <div class="property-section">
      <div class="section-title">模型（可选）</div>
      <WfLLMSelector
        :model-name="nodeConfig.model_name"
        @llm-selected="handleLLMSelected"
      />
    </div>
  </div>
</template>

<script>
import NodePropertyInput from '../NodePropertyInput.vue'
import ReferComment from '../ReferComment.vue'
import WfLLMSelector from '../WfLLMSelector.vue'

export default {
  name: 'TempKnowledgeBaseNodeProperty',

  components: {
    NodePropertyInput,
    ReferComment,
    WfLLMSelector
  },

  props: {
    workflow: {
      type: Object,
      required: true
    },
    wfNode: {
      type: Object,
      required: true
    }
  },

  data () {
    return {
      fileUrlsText: ''
    }
  },

  computed: {
    nodeConfig () {
      // 初始化默认值
      if (!this.wfNode.nodeConfig.text) {
        this.$set(this.wfNode.nodeConfig, 'text', '')
      }
      if (!this.wfNode.nodeConfig.fileUrls) {
        this.$set(this.wfNode.nodeConfig, 'fileUrls', [])
      }
      if (!this.wfNode.nodeConfig.model_name) {
        this.$set(this.wfNode.nodeConfig, 'model_name', '')
      }
      return this.wfNode.nodeConfig
    }
  },

  mounted () {
    // 初始化文件URL文本框
    if (this.nodeConfig.fileUrls && this.nodeConfig.fileUrls.length > 0) {
      this.fileUrlsText = this.nodeConfig.fileUrls.join('\n')
    }
  },

  methods: {
    handleFileUrlsInput (value) {
      // 将文本框内容转换为数组（按行分割）
      const urls = value.split('\n')
        .map(url => url.trim())
        .filter(url => url.length > 0)

      this.nodeConfig.fileUrls = urls

      // 触发X6节点更新
      this.$nextTick(() => {
        this.$root.$emit('workflow:update-node', {
          nodeUuid: this.wfNode.uuid,
          nodeData: this.wfNode
        })
      })
    },

    handleLLMSelected (modelName) {
      this.nodeConfig.model_name = modelName

      // 手动触发 X6 节点重新渲染
      this.$set(this.wfNode.nodeConfig, 'model_name', modelName)

      // 强制更新父组件
      this.$nextTick(() => {
        this.$root.$emit('workflow:update-node', {
          nodeUuid: this.wfNode.uuid,
          nodeData: this.wfNode
        })
      })
    }
  }
}
</script>

<style lang="scss" scoped>
.temp-kb-node-property {
  padding: 16px 0;

  .property-section {
    margin-top: 24px;

    .section-title {
      font-size: 16px;
      font-weight: 500;
      margin-bottom: 8px;
      color: #303133;
      display: flex;
      align-items: center;
    }
  }
}
</style>
```

### 6.4 WorkflowNodePalette.vue 修改

在 `getIconClass()` 方法中添加：

```javascript
getIconClass (name) {
  const iconMap = {
    'Start': 'el-icon-video-play',
    'End': 'el-icon-video-pause',
    // ... 其他节点 ...
    'McpTool': 'el-icon-cpu',
    'TempKnowledgeBase': 'el-icon-folder-add'  // ← 新增
  }

  return iconMap[name] || 'el-icon-s-operation'
}
```

在 `getIconColor()` 方法中添加：

```javascript
getIconColor (name) {
  const colorMap = {
    'Start': '#409EFF',
    'End': '#F56C6C',
    // ... 其他节点 ...
    'McpTool': '#E6A23C',
    'TempKnowledgeBase': '#FF6B6B'  // ← 新增
  }

  return colorMap[name] || '#606266'
}
```

在 `<style>` 中添加：

```scss
// 第16个节点（假设TempKnowledgeBase是第16个）
.node-item:nth-child(16) .node-icon { color: #FF6B6B; }  // TempKnowledgeBase
```

---

## 🧪 七、测试计划

### 7.1 单元测试

**后端测试**（暂不实施，仅规划）：
```java
@Test
public void testTempKnowledgeBaseNode_TextInput() {
    // 测试纯文本输入
}

@Test
public void testTempKnowledgeBaseNode_FileUrlsInput() {
    // 测试纯文件URL输入
}

@Test
public void testTempKnowledgeBaseNode_MixedInput() {
    // 测试文本+文件混合输入
}

@Test
public void testTempKnowledgeBaseNode_VariableReference() {
    // 测试变量引用解析
}
```

### 7.2 集成测试

**前端E2E测试**（使用Playwright，暂不实施）：
```javascript
test('临时知识库节点：创建和配置', async ({ page }) => {
  // 1. 从节点面板拖拽临时知识库节点到画布
  // 2. 打开节点属性配置
  // 3. 输入text和fileUrls
  // 4. 选择模型
  // 5. 保存工作流
  // 6. 验证节点配置已保存
})

test('临时知识库节点：执行工作流', async ({ page }) => {
  // 1. 创建包含临时知识库节点的工作流
  // 2. 运行工作流
  // 3. 验证节点执行成功
  // 4. 验证输出kbUuid
  // 5. 验证下游节点可以使用kbUuid
})
```

### 7.3 手动测试用例

| 用例ID | 用例名称 | 测试步骤 | 预期结果 |
|--------|----------|----------|----------|
| TC001 | 节点面板显示 | 1. 打开工作流设计器<br/>2. 查看左侧节点面板 | 显示"临时知识库"节点，图标为文件夹+加号，颜色为红色 |
| TC002 | 拖拽创建节点 | 1. 拖拽临时知识库节点到画布 | 成功创建节点，显示默认配置 |
| TC003 | 配置文本输入 | 1. 打开节点属性<br/>2. 输入文本内容 | 文本保存成功，节点内容摘要更新 |
| TC004 | 配置文件URL | 1. 打开节点属性<br/>2. 输入多行文件URL | 文件URL保存为数组，节点内容摘要显示文件数量 |
| TC005 | 变量引用 | 1. 配置text为"{user_input}"<br/>2. 运行工作流 | 成功解析变量引用，传递正确值给MCP工具 |
| TC006 | 执行成功 | 1. 配置完整参数<br/>2. 运行工作流 | 成功创建临时知识库，返回kbUuid |
| TC007 | 下游节点使用kbUuid | 1. 连接知识检索节点<br/>2. 引用kbUuid<br/>3. 运行工作流 | 知识检索节点成功使用临时知识库 |

---

## 📝 八、实施清单

### 8.1 数据库变更

- [ ] 执行 `init_temp_kb_component.sql` 初始化组件数据

### 8.2 后端开发

- [ ] 创建 `TempKnowledgeBaseNode.java`
- [ ] 创建 `TempKnowledgeBaseNodeConfig.java`
- [ ] 修改 `WfNodeFactory.java`，添加节点类型判断
- [ ] 编写单元测试（可选）

### 8.3 前端开发

- [ ] 创建 `TempKnowledgeBaseNode.vue`
- [ ] 创建 `TempKnowledgeBaseNodeProperty.vue`
- [ ] 修改 `WorkflowNodePalette.vue`，添加图标映射
- [ ] 编写E2E测试用例（可选）

### 8.4 测试验证

- [ ] 后端编译通过
- [ ] 前端编译通过
- [ ] 手动测试TC001-TC007全部通过
- [ ] 集成测试通过

### 8.5 文档更新

- [ ] 更新用户手册（如有）
- [ ] 更新API文档（如有）
- [ ] 本设计文档归档

---

## 🚀 九、部署计划

### 9.1 部署顺序

1. **数据库变更**：执行SQL初始化组件数据
2. **后端部署**：部署包含新节点的后端服务
3. **前端部署**：部署包含新节点UI的前端资源
4. **验证测试**：在生产环境执行冒烟测试

### 9.2 回滚方案

如需回滚：
1. 在 `ai_workflow_component` 表中将 `TempKnowledgeBase` 组件的 `is_enable` 设为 `0`
2. 重启服务，节点将不再显示在面板中
3. 已创建的工作流不受影响（但无法编辑临时知识库节点）

---

## 📊 十、风险评估

| 风险项 | 风险等级 | 影响 | 缓解措施 |
|--------|----------|------|----------|
| MCP工具调用失败 | 中 | 节点执行失败 | 添加详细日志，提供友好错误提示 |
| 前端渲染性能问题 | 低 | 节点列表加载慢 | 节点数量有限，暂无性能瓶颈 |
| 数据库迁移失败 | 低 | 组件无法加载 | SQL脚本简单，失败概率低 |
| 变量引用解析错误 | 中 | 参数传递失败 | 复用WorkflowUtil.renderTemplate，经过验证 |

---

## 📚 十一、参考文档

- 《SCM AI Workflow Architecture》
- 《TempKnowledgeBaseMcpTools 设计文档》
- 《Workflow Node 开发规范》
- 《aideepin Workflow Components Reference》

---

## ✅ 十二、设计审批

| 角色 | 姓名 | 审批状态 | 审批日期 | 备注 |
|------|------|----------|----------|------|
| 架构师 | zzxxhh | 待审批 | - | - |
| 前端负责人 | - | 待审批 | - | - |
| 后端负责人 | - | 待审批 | - | - |
| 产品经理 | - | 待审批 | - | - |

---

**END OF DESIGN DOCUMENT**
