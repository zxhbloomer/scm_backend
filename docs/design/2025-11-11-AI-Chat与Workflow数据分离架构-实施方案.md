# AI Chat与Workflow数据分离架构 - 完整实施方案

**日期**: 2025-11-11
**需求**: workflow的chat保存在ai_workflow_runtime表,AI chat调用保存在ai_conversation_workflow_runtime表
**状态**: 待审批

---

## 1. 需求分析

### 1.1 核心需求
实现"按用户入口分类"的数据存储架构:

| 入口类型 | 运行时表 | 节点执行表 |
|---------|---------|-----------|
| Workflow独立测试 | `ai_workflow_runtime` | `ai_workflow_runtime_node` |
| AI Chat调用Workflow | `ai_conversation_workflow_runtime` | `ai_conversation_workflow_runtime_node` |

### 1.2 KISS原则验证
1. ✅ **真问题** - AI Chat和Workflow测试需要数据隔离
2. ✅ **最简方案** - 镜像复制Service,枚举路由,无过度设计
3. ✅ **零破坏** - 现有Workflow测试功能完全不变
4. ✅ **必要性** - 两个独立业务场景,数据分离合理
5. ✅ **确定性** - 表结构已创建,技术方案清晰
6. ✅ **简洁性** - 复用现有代码结构,保持函数职责单一

---

## 2. 数据库设计

### 2.1 表结构状态
✅ **已创建完成** (2025-11-11)

**ai_conversation_workflow_runtime** - AI Chat调用Workflow运行时表
```sql
CREATE TABLE `ai_conversation_workflow_runtime` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `runtime_uuid` varchar(100) NOT NULL COMMENT '运行时UUID(业务主键)',
  `conversation_id` varchar(200) DEFAULT NULL COMMENT '对话ID,格式:tenantId::uuid',
  `workflow_id` bigint NOT NULL COMMENT '工作流ID',
  `user_id` bigint NOT NULL DEFAULT '0' COMMENT '执行用户ID',
  `input_data` json DEFAULT NULL COMMENT '输入数据(JSON格式)',
  `output_data` json DEFAULT NULL COMMENT '输出数据(JSON格式)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '执行状态(1-运行中,2-成功,3-失败)',
  `status_remark` text COMMENT '状态说明',
  `c_time` datetime DEFAULT NULL,
  `u_time` datetime DEFAULT NULL,
  `c_id` bigint DEFAULT NULL,
  `u_id` bigint DEFAULT NULL,
  `dbversion` int DEFAULT '0' COMMENT '数据版本(乐观锁)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_runtime_uuid` (`runtime_uuid`),
  KEY `idx_conversation_id` (`conversation_id`),
  KEY `idx_workflow_status` (`workflow_id`,`status`),
  KEY `idx_user_id` (`user_id`)
) ENGINE=InnoDB COMMENT='AI Chat调用Workflow运行时表';
```

**ai_conversation_workflow_runtime_node** - AI Chat调用Workflow节点执行表
```sql
CREATE TABLE `ai_conversation_workflow_runtime_node` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `runtime_node_uuid` varchar(100) NOT NULL COMMENT '运行时节点UUID(业务主键)',
  `conversation_workflow_runtime_id` bigint NOT NULL COMMENT 'AI Chat工作流运行时ID',
  `node_id` bigint NOT NULL COMMENT '节点ID',
  `input_data` json DEFAULT NULL COMMENT '节点输入数据(JSON格式)',
  `output_data` json DEFAULT NULL COMMENT '节点输出数据(JSON格式)',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '执行状态(1-等待中,2-运行中,3-成功,4-失败)',
  `status_remark` text COMMENT '状态说明',
  `c_time` datetime DEFAULT NULL,
  `u_time` datetime DEFAULT NULL,
  `c_id` bigint DEFAULT NULL,
  `u_id` bigint DEFAULT NULL,
  `dbversion` int DEFAULT '0' COMMENT '数据版本(乐观锁)',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_runtime_node_uuid` (`runtime_node_uuid`),
  KEY `idx_node_id` (`node_id`),
  KEY `idx_runtime_status` (`conversation_workflow_runtime_id`,`status`)
) ENGINE=InnoDB COMMENT='AI Chat调用Workflow节点执行表';
```

---

## 3. 架构设计

### 3.1 调用链路分析

**Workflow测试路径**:
```
WorkflowController.run()
  ↓
WorkflowStarter.streaming(callSource=WORKFLOW_TEST)
  ↓
WorkflowStarter.asyncRunWorkflow(callSource=WORKFLOW_TEST)
  ↓
WorkflowEngine.run(callSource=WORKFLOW_TEST)
  ↓
if (callSource == WORKFLOW_TEST) → AiWorkflowRuntimeService
```

**AI Chat调用路径**:
```
AiConversationController.chatStream()
  ↓
WorkflowStarter.streaming(callSource=AI_CHAT)
  ↓
WorkflowStarter.asyncRunWorkflow(callSource=AI_CHAT)
  ↓
WorkflowEngine.run(callSource=AI_CHAT)
  ↓
if (callSource == AI_CHAT) → AiConversationWorkflowRuntimeService
```

### 3.2 服务选择机制

**枚举定义**:
```java
public enum WorkflowCallSource {
    WORKFLOW_TEST,  // Workflow独立测试
    AI_CHAT        // AI Chat调用Workflow
}
```

**动态服务选择**:
```java
// WorkflowEngine中的服务选择逻辑
if (callSource == WorkflowCallSource.AI_CHAT) {
    conversationWorkflowRuntimeService.create(userId, workflowId);
} else {
    workflowRuntimeService.create(userId, workflowId);
}
```

---

## 4. 代码实施设计

### 4.1 Entity实体类

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/entity/workflow/AiConversationWorkflowRuntimeEntity.java`

```java
package com.xinyirun.scm.ai.bean.entity.workflow;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI Chat调用Workflow运行时实体
 */
@Data
@TableName("ai_conversation_workflow_runtime")
public class AiConversationWorkflowRuntimeEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("runtime_uuid")
    private String runtimeUuid;

    @TableField("conversation_id")
    private String conversationId;

    @TableField("workflow_id")
    private Long workflowId;

    @TableField("user_id")
    private Long userId;

    @TableField("input_data")
    private String inputData;

    @TableField("output_data")
    private String outputData;

    @TableField("status")
    private Integer status;

    @TableField("status_remark")
    private String statusRemark;

    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;

    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime uTime;

    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    @TableField("dbversion")
    private Integer dbversion;
}
```

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/entity/workflow/AiConversationWorkflowRuntimeNodeEntity.java`

```java
package com.xinyirun.scm.ai.bean.entity.workflow;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI Chat调用Workflow节点执行实体
 */
@Data
@TableName("ai_conversation_workflow_runtime_node")
public class AiConversationWorkflowRuntimeNodeEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("runtime_node_uuid")
    private String runtimeNodeUuid;

    @TableField("conversation_workflow_runtime_id")
    private Long conversationWorkflowRuntimeId;

    @TableField("node_id")
    private Long nodeId;

    @TableField("input_data")
    private String inputData;

    @TableField("output_data")
    private String outputData;

    @TableField("status")
    private Integer status;

    @TableField("status_remark")
    private String statusRemark;

    @TableField(value = "c_time", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private LocalDateTime cTime;

    @TableField(value = "u_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime uTime;

    @TableField(value = "c_id", fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NOT_EMPTY)
    private Long cId;

    @TableField(value = "u_id", fill = FieldFill.INSERT_UPDATE)
    private Long uId;

    @TableField("dbversion")
    private Integer dbversion;
}
```

### 4.2 Mapper接口

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/mapper/workflow/AiConversationWorkflowRuntimeMapper.java`

```java
package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationWorkflowRuntimeEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI Chat调用Workflow运行时Mapper
 */
@Mapper
public interface AiConversationWorkflowRuntimeMapper extends BaseMapper<AiConversationWorkflowRuntimeEntity> {
}
```

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/mapper/workflow/AiConversationWorkflowRuntimeNodeMapper.java`

```java
package com.xinyirun.scm.ai.core.mapper.workflow;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xinyirun.scm.ai.bean.entity.workflow.AiConversationWorkflowRuntimeNodeEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * AI Chat调用Workflow节点执行Mapper
 */
@Mapper
public interface AiConversationWorkflowRuntimeNodeMapper extends BaseMapper<AiConversationWorkflowRuntimeNodeEntity> {
}
```

### 4.3 VO值对象类

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/vo/workflow/AiConversationWorkflowRuntimeVo.java`

```java
package com.xinyirun.scm.ai.bean.vo.workflow;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI Chat调用Workflow运行时VO
 */
@Data
public class AiConversationWorkflowRuntimeVo {
    private Long id;
    private String runtimeUuid;
    private String conversationId;
    private Long workflowId;
    private Long userId;
    private JSONObject inputData;
    private JSONObject outputData;
    private Integer status;
    private String statusRemark;
    private LocalDateTime cTime;
    private LocalDateTime uTime;
    private Long cId;
    private Long uId;
    private Integer dbversion;
}
```

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/vo/workflow/AiConversationWorkflowRuntimeNodeVo.java`

```java
package com.xinyirun.scm.ai.bean.vo.workflow;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * AI Chat调用Workflow节点执行VO
 */
@Data
public class AiConversationWorkflowRuntimeNodeVo {
    private Long id;
    private String runtimeNodeUuid;
    private Long conversationWorkflowRuntimeId;
    private Long nodeId;
    private JSONObject inputData;
    private JSONObject outputData;
    private Integer status;
    private String statusRemark;
    private LocalDateTime cTime;
    private LocalDateTime uTime;
    private Long cId;
    private Long uId;
    private Integer dbversion;
}
```

### 4.4 Service服务层设计

**镜像复制原则**: 完全复制`AiWorkflowRuntimeService`的所有方法,保持方法签名一致

**关键方法列表** (需要镜像实现):
1. `create(Long userId, Long workflowId)` - 创建运行实例
2. `createWithConversationId(Long userId, Long workflowId, String conversationId)` - 使用指定conversationId创建
3. `updateInput(Long id, WfState wfState)` - 更新输入数据
4. `updateOutput(Long id, WfState wfState)` - 更新输出数据
5. `updateStatus(Long id, Integer status, String statusRemark)` - 更新状态
6. `getByUuid(String runtimeUuid)` - 按UUID查询

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/AiConversationWorkflowRuntimeService.java`

### 4.5 WorkflowCallSource枚举

**文件**: `scm-ai/src/main/java/com/xinyirun/scm/ai/common/constant/WorkflowCallSource.java`

```java
package com.xinyirun.scm.ai.common.constant;

/**
 * Workflow调用来源枚举
 * 用于区分不同入口的Workflow调用,实现数据分离存储
 */
public enum WorkflowCallSource {
    /**
     * Workflow独立测试
     * 数据保存到: ai_workflow_runtime, ai_workflow_runtime_node
     */
    WORKFLOW_TEST,

    /**
     * AI Chat调用Workflow
     * 数据保存到: ai_conversation_workflow_runtime, ai_conversation_workflow_runtime_node
     */
    AI_CHAT
}
```

### 4.6 WorkflowEngine修改设计

**需要修改的11个调用点**:

| 行号 | 方法 | 修改内容 |
|------|------|---------|
| 165-169 | create/createWithConversationId | 根据callSource选择Service |
| 209 | updateInput | 根据callSource选择Service |
| 260, 269 | updateOutput | 根据callSource选择Service |
| 321 | updateStatus | 根据callSource选择Service |
| 356 | getByUuid | 根据callSource选择Service |
| 360 | createByState (node) | 根据callSource选择NodeService |
| 373, 396, 475 | update (node) | 根据callSource选择NodeService |

**修改示例** (Line 165-169):
```java
// 修改前
if (parentConversationId != null && !parentConversationId.isEmpty()) {
    this.wfRuntimeResp = workflowRuntimeService.createWithConversationId(
        userId, workflowId, parentConversationId);
} else {
    this.wfRuntimeResp = workflowRuntimeService.create(userId, workflowId);
}

// 修改后
if (callSource == WorkflowCallSource.AI_CHAT) {
    // AI Chat调用 - 保存到ai_conversation_workflow_runtime表
    if (parentConversationId != null && !parentConversationId.isEmpty()) {
        this.wfRuntimeResp = conversationWorkflowRuntimeService.createWithConversationId(
            userId, workflowId, parentConversationId);
    } else {
        this.wfRuntimeResp = conversationWorkflowRuntimeService.create(userId, workflowId);
    }
} else {
    // Workflow测试 - 保存到ai_workflow_runtime表
    if (parentConversationId != null && !parentConversationId.isEmpty()) {
        this.wfRuntimeResp = workflowRuntimeService.createWithConversationId(
            userId, workflowId, parentConversationId);
    } else {
        this.wfRuntimeResp = workflowRuntimeService.create(userId, workflowId);
    }
}
```

### 4.7 WorkflowStarter修改设计

**修改点**:
1. `streaming()` 方法增加 `WorkflowCallSource callSource` 参数
2. `asyncRunWorkflow()` 方法增加 `WorkflowCallSource callSource` 参数
3. 将callSource传递给WorkflowEngine构造函数

### 4.8 Controller修改设计

**WorkflowController.run()** - 传递 `WORKFLOW_TEST`
```java
return workflowStarter.streaming(wfUuid, inputs, tenantCode, WorkflowCallSource.WORKFLOW_TEST)
```

**AiConversationController.chatStream()** - 传递 `AI_CHAT`
```java
return workflowStarter.streaming(workflowUuid, userInputs, tenantId, WorkflowCallSource.AI_CHAT);
```

---

## 5. 实施步骤

### 5.1 第一阶段:基础代码 (预计30分钟)
1. ✅ 创建Entity实体类 (2个)
2. ✅ 创建Mapper接口 (2个)
3. ✅ 创建VO值对象类 (2个)
4. ✅ 创建WorkflowCallSource枚举

### 5.2 第二阶段:Service层 (预计45分钟)
1. ✅ 创建AiConversationWorkflowRuntimeService (镜像复制)
2. ✅ 创建AiConversationWorkflowRuntimeNodeService (镜像复制)

### 5.3 第三阶段:核心改造 (预计60分钟)
1. ✅ 修改WorkflowEngine (11个调用点)
2. ✅ 修改WorkflowStarter (参数传递)
3. ✅ 修改Controller (2个入口)

### 5.4 第四阶段:测试验证 (预计30分钟)
1. ✅ Workflow测试验证 (确保原功能正常)
2. ✅ AI Chat调用验证 (确保数据写入新表)
3. ✅ 数据库验证 (检查两套表的数据)

**总预计时间**: 2.5-3小时

---

## 6. 风险分析与缓解

### 6.1 技术风险

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| Service镜像复制遗漏方法 | 运行时报错 | 严格对照原Service,逐方法复制 |
| callSource参数传递中断 | 路由错误,数据写错表 | 参数传递链完整性检查 |
| 11个调用点遗漏 | 部分功能仍写旧表 | Grep搜索确认所有调用点 |

### 6.2 业务风险

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| 破坏现有Workflow测试 | 生产环境故障 | 默认值WORKFLOW_TEST,保持原行为 |
| AI Chat数据未正确分离 | 数据混乱 | 充分测试两种场景 |

### 6.3 数据风险

| 风险 | 影响 | 缓解措施 |
|------|------|---------|
| 表结构不匹配 | 字段映射错误 | ✅ 已验证表结构完全一致 |
| 索引性能问题 | 查询慢 | ✅ 已创建所有必要索引 |

---

## 7. 回滚方案

### 7.1 数据库回滚
```sql
-- 如需回滚,删除新表
DROP TABLE IF EXISTS ai_conversation_workflow_runtime_node;
DROP TABLE IF EXISTS ai_conversation_workflow_runtime;
```

### 7.2 代码回滚
```bash
# Git回滚到实施前的commit
git revert <commit-hash>
```

### 7.3 配置回滚
- 无需配置修改,回滚代码即可

---

## 8. 验收标准

### 8.1 功能验收
- ✅ Workflow独立测试数据写入 `ai_workflow_runtime` 表
- ✅ AI Chat调用Workflow数据写入 `ai_conversation_workflow_runtime` 表
- ✅ 两种场景的节点执行数据正确写入对应node表
- ✅ 现有Workflow测试功能完全正常

### 8.2 性能验收
- ✅ 响应时间无明显增加 (增加if/else判断,影响<1ms)
- ✅ 数据库查询性能正常 (索引已优化)

### 8.3 质量验收
- ✅ 无编译错误
- ✅ 无运行时异常
- ✅ 代码符合项目规范

---

## 9. KISS原则最终验证

```text
1. "这是个真问题还是臆想出来的?"
   ✅ 真问题 - 生产环境需要数据隔离

2. "有更简单的方法吗?"
   ✅ 当前方案是最简 - 镜像复制+枚举路由

3. "会破坏什么吗?"
   ✅ 零破坏 - 原功能完全保留

4. "当前项目真的需要这个功能吗?"
   ✅ 必要 - 两个独立业务场景

5. "对任何方面不确定?"
   ✅ 确定 - 方案清晰,数据充足

6. "代码简洁性"
   ✅ 简洁 - 复用结构,无过度抽象
```

---

## 10. 审批确认

**请审核以上方案,确认后方可开始实施。**

审批人: _____________
审批日期: _____________
审批意见: _____________
