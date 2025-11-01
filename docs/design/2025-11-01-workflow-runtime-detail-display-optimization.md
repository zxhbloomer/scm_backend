# 工作流执行详情弹窗显示优化方案

## 1. 问题描述

用户反馈执行详情弹窗存在以下显示问题：

1. **执行时间显示为空**：基本信息中"执行时间"字段没有值
2. **节点显示"未命名节点"**：所有节点标题都显示"未命名节点"
3. **状态标签显示错误**：节点状态显示"等待输入"，实际应该是"运行中"
4. **缺少执行时长信息**：基本信息中没有显示工作流总耗时

## 2. 根因分析

### 2.1 数据结构分析

**后端返回的节点数据**（AiWorkflowRuntimeNodeVo）：
```java
{
  "id": 123,
  "runtimeNodeUuid": "uuid-xxx",
  "workflowRuntimeId": 456,
  "nodeId": 789,           // ⚠️ 只有节点ID，没有节点标题
  "inputData": {...},
  "outputData": {...},
  "status": 2              // 1-等待中,2-运行中,3-成功,4-失败
}
```

**前端工作流数据**（workflow.nodes）：
```javascript
{
  "id": 789,
  "uuid": "node-uuid",
  "title": "LLM答案",     // ✅ 节点标题在这里
  "wfComponent": {
    "name": "LLMAnswer",
    "title": "LLM答案"
  }
}
```

### 2.2 问题原因

**问题1：节点名称显示"未命名节点"**
- 后端 VO 没有返回 `nodeTitle` 字段，只返回了 `nodeId`
- 前端模板使用 `node.nodeTitle || node.name || '未命名节点'`
- 由于 nodeTitle 和 name 都不存在，显示 fallback 值"未命名节点"

**问题2：状态映射错误**
```javascript
// 前端 getStatusText (错误)
2: '等待输入'

// 后端 WorkflowConstants (正确)
2: RUNNING ('运行中')
```

**问题3：执行时间为空**
- 字段名映射问题：可能是 `cTime` / `c_time` / `createTime`
- 需要检查实际返回的字段名

**问题4：缺少耗时信息**
- 需要检查后端是否返回 `elapsedMs` 或 `elapsed_ms` 字段

## 3. KISS原则评估

### 3.1 四个关键问题

**1. "这是个真问题还是臆想出来的？"**
- ✅ **真问题**：用户提供截图证明问题存在
- 影响用户理解工作流执行历史

**2. "有更简单的方法吗？"**
- ❌ **没有更简单的方法**
- 必须修复状态映射和节点名称提取逻辑

**3. "会破坏什么吗？"**
- ✅ **不会破坏**：只修改前端显示逻辑，不改变数据结构

**4. "当前项目真的需要这个功能吗？"**
- ✅ **必要**：执行详情是工作流的核心功能

## 4. 实施方案

### 4.1 前端修改（WorkflowRuntimeList.vue）

#### 修改1：添加节点名称获取方法
```javascript
/**
 * 获取节点显示名称
 * 通过 nodeId 关联 workflow.nodes 获取节点标题
 */
getNodeDisplayName(node) {
  if (!node || !node.nodeId) {
    return '未命名节点'
  }

  // 通过 nodeId 查找 workflow.nodes
  const workflowNode = this.workflow.nodes?.find(n => n.id === node.nodeId)

  if (workflowNode) {
    // 优先级：title > wfComponent.title > wfComponent.name
    return workflowNode.title ||
           workflowNode.wfComponent?.title ||
           workflowNode.wfComponent?.name ||
           '未命名节点'
  }

  return '未命名节点'
}
```

#### 修改2：修复节点状态映射
```javascript
getNodeStatusType(status) {
  const typeMap = {
    1: 'info',    // 等待中
    2: 'primary', // 运行中（改为primary，更明显）
    3: 'success', // 成功
    4: 'danger'   // 失败
  }
  return typeMap[status] || 'info'
},

getNodeStatusText(status) {
  const textMap = {
    1: '等待中',
    2: '运行中',  // ✅ 修复：从"等待输入"改为"运行中"
    3: '成功',
    4: '失败'
  }
  return textMap[status] || '未知'
}
```

#### 修改3：统一字段访问方法
```javascript
/**
 * 安全获取时间字段（兼容驼峰和下划线）
 */
getTimeField(obj, fieldName) {
  if (!obj) return null
  // 尝试驼峰命名
  const camelCase = fieldName
  // 尝试下划线命名
  const snakeCase = fieldName.replace(/[A-Z]/g, letter => `_${letter.toLowerCase()}`)

  return obj[camelCase] || obj[snakeCase] || null
},

/**
 * 格式化运行时对象的时间显示
 */
getRuntimeTime(runtime) {
  const time = this.getTimeField(runtime, 'cTime') ||
               this.getTimeField(runtime, 'createTime')
  return this.formatTime(time)
},

/**
 * 获取耗时毫秒数
 */
getElapsedMs(runtime) {
  return this.getTimeField(runtime, 'elapsedMs') ||
         this.getTimeField(runtime, 'elapsed_ms') ||
         null
}
```

#### 修改4：更新模板使用新方法
```vue
<!-- 基本信息 -->
<el-descriptions-item label="执行时间">
  {{ getRuntimeTime(currentRuntimeDetail) }}
</el-descriptions-item>

<el-descriptions-item v-if="getElapsedMs(currentRuntimeDetail)" label="耗时">
  {{ getElapsedMs(currentRuntimeDetail) }}ms
</el-descriptions-item>

<!-- 节点卡片 -->
<div class="node-header">
  <span class="node-name">{{ getNodeDisplayName(node) }}</span>
  <el-tag :type="getNodeStatusType(node.status)" size="mini">
    {{ getNodeStatusText(node.status) }}
  </el-tag>
</div>
```

### 4.2 后端优化（可选，低优先级）

如果后续需要简化前端逻辑，可以修改后端：

**AiWorkflowRuntimeNodeVo 添加字段**：
```java
@Data
public class AiWorkflowRuntimeNodeVo {
    // 现有字段
    private Long id;
    private String runtimeNodeUuid;
    private Long workflowRuntimeId;
    private Long nodeId;
    private JSONObject inputData;
    private JSONObject outputData;
    private Integer status;

    // 新增字段（前端需要）
    private String nodeTitle;        // 节点标题
    private String nodeType;         // 节点类型
    private String statusRemark;     // 状态备注
}
```

**AiWorkflowRuntimeNodeService.listByWfRuntimeId() 关联查询**：
```java
public List<AiWorkflowRuntimeNodeVo> listByWfRuntimeId(Long wfRuntimeId) {
    // ... 现有查询逻辑

    for (AiWorkflowRuntimeNodeEntity entity : entityList) {
        AiWorkflowRuntimeNodeVo vo = new AiWorkflowRuntimeNodeVo();
        BeanUtils.copyProperties(entity, vo);

        // 关联查询节点信息
        if (entity.getNodeId() != null) {
            AiWorkflowNodeEntity node = workflowNodeService.getById(entity.getNodeId());
            if (node != null) {
                vo.setNodeTitle(node.getTitle());
                // 或者从组件获取：vo.setNodeTitle(node.getWfComponent().getTitle());
            }
        }

        // 其他逻辑...
    }

    return result;
}
```

**注意**：后端修改暂不实施，优先使用前端方案，避免增加后端复杂度。

## 5. 测试验证

### 5.1 测试场景

1. **节点名称显示测试**：
   - 创建包含不同类型节点的工作流（Start、LLM答案、End）
   - 执行工作流
   - 查看执行详情，确认节点名称正确显示

2. **状态显示测试**：
   - 验证不同状态的节点显示正确的状态文本和颜色
   - 等待中：灰色"等待中"
   - 运行中：蓝色"运行中"
   - 成功：绿色"成功"
   - 失败：红色"失败"

3. **时间显示测试**：
   - 确认执行时间正确显示
   - 确认耗时信息正确显示（如果字段存在）

### 5.2 预期结果

**优化前**：
```
节点: "未命名节点"
状态: "等待输入" (黄色)
执行时间: (空)
```

**优化后**：
```
节点: "Start"、"LLM答案"、"End"
状态: "运行中" (蓝色)、"成功" (绿色)
执行时间: "2025-11-01 12:30:45"
耗时: "3845ms" (如果有)
```

## 6. 风险评估

### 6.1 技术风险

- **低风险**：只修改前端显示逻辑，不影响数据存储
- **兼容性好**：向后兼容所有字段名版本（驼峰/下划线）
- **无破坏性**：不改变 API 接口和数据结构

### 6.2 性能影响

- **可忽略**：`workflow.nodes` 已经加载，只是 Array.find() 查找
- **优化建议**：如果 nodes 数组很大（>100），可以使用 Map 缓存

## 7. 实施计划

### 阶段1：前端修改（当前）
- [ ] 添加 getNodeDisplayName() 方法
- [ ] 修复 getNodeStatusText() 状态映射
- [ ] 添加 getNodeStatusType() 方法
- [ ] 添加 getTimeField() / getRuntimeTime() / getElapsedMs() 方法
- [ ] 更新模板使用新方法
- [ ] ESLint 检查通过

### 阶段2：测试验证
- [ ] 节点名称显示测试
- [ ] 状态显示测试
- [ ] 时间显示测试
- [ ] 用户验收确认

### 阶段3：后端优化（可选，未来）
- [ ] 评估是否需要后端添加 nodeTitle 字段
- [ ] 如果需要，修改 AiWorkflowRuntimeNodeVo
- [ ] 修改 Service 层关联查询逻辑

## 8. 附录：字段映射表

### Runtime 对象字段
| 前端字段 | 后端字段（可能） | 说明 |
|---------|----------------|------|
| cTime | c_time / cTime / createTime | 创建时间 |
| elapsedMs | elapsed_ms / elapsedMs | 执行耗时（毫秒） |
| status | status | 执行状态 |

### Node 对象字段
| 前端字段 | 后端字段 | 数据来源 |
|---------|---------|---------|
| nodeTitle | - | 需通过 workflow.nodes 查找 |
| nodeId | nodeId | API 直接返回 |
| inputData | inputData | API 直接返回 |
| outputData | outputData | API 直接返回 |
| status | status | API 直接返回 |

### 节点状态映射
| 状态值 | 后端定义 | 前端显示（修复前） | 前端显示（修复后） | 颜色 |
|-------|---------|------------------|------------------|------|
| 1 | WAITING | 就绪 | 等待中 | info (灰色) |
| 2 | RUNNING | 等待输入 ❌ | 运行中 ✅ | primary (蓝色) |
| 3 | SUCCESS | 成功 | 成功 | success (绿色) |
| 4 | FAIL | 失败 | 失败 | danger (红色) |
