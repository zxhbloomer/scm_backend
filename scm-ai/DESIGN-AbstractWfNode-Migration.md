# AbstractWfNode 完整迁移设计方案

## 执行时间: 2025-10-22
## 状态: 待用户确认

---

## 一、现状分析

### 1.1 缺失问题
- ❌ `AbstractWfNode` 类完全不存在
- ❌ `workflow/node/` 目录不存在
- ❌ 所有具体节点实现均缺失（共40个文件）
- ❌ `WfState.java` 无法编译

### 1.2 影响范围
- 编译失败：所有导入 `WfState` 的类
- 功能阻止：工作流执行功能完全不可用
- 级联失败：工作流引擎、运行时、服务层等

### 1.3 AIDeepin 参考结构

```
workflow/node/
├── AbstractWfNode.java                  # 运行时节点基类
├── answer/
│   ├── LLMAnswerNode.java
│   └── LLMAnswerNodeConfig.java
├── classifier/
│   ├── ClassifierNode.java
│   ├── ClassifierNodeConfig.java
│   ├── ClassifierCategory.java
│   ├── ClassifierLLMResp.java
│   └── ClassifierPrompt.java
├── dalle3/
│   ├── Dalle3Node.java
│   └── Dalle3NodeConfig.java
├── documentextractor/
│   └── DocumentExtractorNode.java
├── faqextractor/
│   ├── FaqExtractorNode.java
│   ├── FaqExtractorNodeConfig.java
│   └── FaqExtractorPrompt.java
├── google/
│   ├── GoogleNode.java
│   └── GoogleNodeConfig.java
├── httprequest/
│   ├── HttpRequestNode.java
│   └── HttpRequestNodeConfig.java
├── humanfeedback/
│   ├── HumanFeedbackNode.java
│   └── HumanFeedbackNodeConfig.java
├── keywordextractor/
│   ├── KeywordExtractorNode.java
│   ├── KeywordExtractorNodeConfig.java
│   └── KeywordExtractorPrompt.java
├── knowledgeretrieval/
│   ├── KnowledgeRetrievalNode.java
│   └── KnowledgeRetrievalNodeConfig.java
├── mailsender/
│   ├── MailSendNode.java
│   └── MailSendNodeConfig.java
├── start/
│   ├── StartNode.java
│   └── StartNodeConfig.java
├── switcher/
│   ├── SwitcherNode.java
│   ├── SwitcherNodeConfig.java
│   ├── SwitcherCase.java
│   ├── LogicOperatorEnum.java
│   └── OperatorEnum.java
├── template/
│   ├── TemplateNode.java
│   └── TemplateNodeConfig.java
├── tongyiwanx/
│   ├── TongyiwanxNode.java
│   └── TongyiwanxNodeConfig.java
├── DrawNodeUtil.java
└── EndNode.java

共 40 个文件
```

---

## 二、设计方案 (方案A：完整迁移)

### 2.1 迁移策略

#### 第一阶段：必要基础类迁移 (优先级 P0)
这些类是其他节点的基础，必须先迁移

| 类名 | 文件数 | 优先级 | 说明 |
|------|--------|--------|------|
| `AbstractWfNode` | 1 | P0 | 所有节点的基类，核心 |
| `DrawNodeUtil` | 1 | P0 | 节点工具类 |
| `EndNode` | 1 | P0 | 结束节点 |

**小计：3个文件**

#### 第二阶段：通用节点实现 (优先级 P1)
这些是常用的、简单的节点实现

| 节点类型 | 文件数 | 优先级 | 说明 |
|---------|--------|--------|------|
| `start/` | 2 | P1 | 开始节点，基础 |
| `template/` | 2 | P1 | 模板节点，处理简单文本模板 |
| `httprequest/` | 2 | P1 | HTTP请求节点 |
| `humanfeedback/` | 2 | P1 | 人工反馈节点 |

**小计：8个文件**

#### 第三阶段：AI能力节点 (优先级 P2)
这些是LLM相关的节点，功能复杂但核心功能

| 节点类型 | 文件数 | 优先级 | 说明 |
|---------|--------|--------|------|
| `answer/` | 2 | P2 | LLM回答节点 |
| `classifier/` | 5 | P2 | 分类节点，涉及LLM |
| `knowledgeretrieval/` | 2 | P2 | 知识检索节点（RAG相关） |

**小计：9个文件**

#### 第四阶段：文本处理节点 (优先级 P3)
这些是文本处理和提取的节点

| 节点类型 | 文件数 | 优先级 | 说明 |
|---------|--------|--------|------|
| `faqextractor/` | 3 | P3 | FAQ提取 |
| `keywordextractor/` | 3 | P3 | 关键词提取 |
| `documentextractor/` | 1 | P3 | 文档提取 |

**小计：7个文件**

#### 第五阶段：高级功能节点 (优先级 P4)
这些是特定功能的节点，可选实现

| 节点类型 | 文件数 | 优先级 | 说明 |
|---------|--------|--------|------|
| `switcher/` | 5 | P4 | 条件分支节点 |
| `dalle3/` | 2 | P4 | DALL-E 3 图像生成 |
| `google/` | 2 | P4 | Google搜索集成 |
| `mailsender/` | 2 | P4 | 邮件发送 |
| `tongyiwanx/` | 2 | P4 | 通义万象API（可选） |

**小计：13个文件**

### 2.2 总体统计

| 阶段 | 文件数 | 优先级 | 状态 |
|------|--------|--------|------|
| 第一阶段：基础类 | 3 | P0 | **必须完成** |
| 第二阶段：通用节点 | 8 | P1 | **应该完成** |
| 第三阶段：AI节点 | 9 | P2 | **应该完成** |
| 第四阶段：文本处理 | 7 | P3 | 可在后续迭代完成 |
| 第五阶段：高级功能 | 13 | P4 | 可在后续迭代完成 |
| **总计** | **40** | - | - |

---

## 三、SCM-AI 与 AIDeepin 的设计差异处理

### 3.1 数据存储层差异

#### AIDeepin 方案
- 使用 `WorkflowNode` entity 存储节点定义
- 使用 `WfNodeInputConfig` 存储节点输入配置

#### SCM-AI 方案
- 使用 `AiWorkflowNodeEntity` 存储节点定义
  - 字段：`node_uuid`, `workflow_id`, `workflow_component_id`, `name`, `node_config`, `input_config`
- 使用 JSON 字段存储配置：`input_config` (JSON), `node_config` (JSON)
- MySQL 数据库：`scm_tenant_20250519_001`

### 3.2 运行时状态管理

#### AIDeepin 方案
- 使用 LangGraph4j 的 `AgentState` 作为基类
- 状态完全在内存中管理

#### SCM-AI 方案
- 使用 LangGraph4j 的 `AgentState` 作为基类（保持一致）
- 使用 `AiWorkflowRuntimeEntity` 和 `AiWorkflowRuntimeNodeEntity` 持久化运行时状态
- 关键字段：
  - `ai_workflow_runtime` 表：工作流运行时
  - `ai_workflow_runtime_node` 表：节点运行时（暂未创建）

### 3.3 异步处理差异

#### AIDeepin 方案
- 同步执行节点

#### SCM-AI 方案
- 向量化处理（文档处理后）通过 RabbitMQ 异步处理
- 使用 Quartz 定时任务处理更新逻辑
- 涉及队列：需查看 `MQEnum.java` 定义

### 3.4 关键API适配

#### 用户身份获取
```java
// 错误方式 ❌
SecurityUtil.getAppJwtBaseBo()  // 不可使用

// 正确方式 ✓
Long userId = SecurityUtil.getStaff_id()          // 获取用户ID
String userCode = SecurityUtil.getStaff_code()    // 获取用户编码
// 获取用户名需要查询 m_staff 表
```

#### 节点配置获取
```java
// AIDeepin: ObjectNode 格式
ObjectNode nodeConfig = workflowNode.getNodeConfig()

// SCM-AI: JSON 字符串存储
// 需要从 AiWorkflowNodeEntity 获取 node_config (JSON)
// 使用 JsonUtil 转换为具体配置对象
```

---

## 四、迁移实施细节

### 4.1 AbstractWfNode 核心结构

```
AbstractWfNode (abstract class)
├── 属性
│   ├── wfComponent: WorkflowComponent (组件定义)
│   ├── wfState: WfState (工作流状态)
│   ├── state: WfNodeState (节点状态)
│   └── node: WorkflowNode (节点entity - 改为 AiWorkflowNodeEntity)
├── 方法
│   ├── process() - 执行节点处理
│   ├── initInput() - 初始化输入参数
│   ├── onProcess() - abstract 具体实现
│   └── 工具方法
└── 关键逻辑
    ├── 参数初始化和转换
    ├── 上游节点输出转当前输入
    ├── 引用参数处理
    └── 节点添加到 completedNodes
```

### 4.2 具体节点实现模式

每个具体节点（如 `LLMAnswerNode`）需要：

```java
public class LLMAnswerNode extends AbstractWfNode {

    public LLMAnswerNode(WorkflowComponent wfComponent,
                         AiWorkflowNodeEntity node,
                         WfState wfState,
                         WfNodeState nodeState) {
        super(wfComponent, node, wfState, nodeState);
    }

    @Override
    protected NodeProcessResult onProcess() {
        // 1. 获取和验证节点配置
        LLMAnswerNodeConfig config = checkAndGetConfig(LLMAnswerNodeConfig.class);

        // 2. 获取输入参数
        String input = getFirstInputText();

        // 3. 调用 LLM 处理
        // 使用 Spring AI 调用 LLM 模型

        // 4. 处理输出
        return NodeProcessResult.success(outputs);
    }
}
```

### 4.3 配置类设计模式

每个节点的配置类（如 `LLMAnswerNodeConfig`）：

```java
@Data
@NoArgsConstructor
public class LLMAnswerNodeConfig {
    // 节点配置的具体字段
    private String prompt;
    private String modelName;
    // ...
}
```

从 `AiWorkflowNodeEntity.nodeConfig` (JSON) 反序列化获得。

### 4.4 关键改造点

#### 1. 数据库适配
- AIDeepin: `WorkflowNode` entity
- SCM-AI: `AiWorkflowNodeEntity` entity
- 改造：更新 node 属性的类型声明

#### 2. 组件定义适配
- AIDeepin: `WorkflowComponent` entity
- SCM-AI: `AiWorkflowComponentEntity` entity
- 改造：参考 `AiWorkflowComponentService` 获取组件

#### 3. 配置解析适配
- AIDeepin: `ObjectNode` (Jackson)
- SCM-AI: JSON String → Jackson ObjectNode 或具体配置类
- 改造：使用 `JsonUtil` 进行转换

#### 4. 用户身份适配
- AIDeepin: `wfState.getUserId()` 直接存储
- SCM-AI: `SecurityUtil.getStaff_id()` 或从 token 获取
- 改造：确保用户ID正确传递

---

## 五、迁移文件清单

### 第一阶段必迁移文件 (P0 - 必须)

```
scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/
├── AbstractWfNode.java (关键文件，~228行)
├── DrawNodeUtil.java (工具类，较小)
└── EndNode.java (结束节点，较小)
```

### 第二阶段应迁移文件 (P1 - 应该)

```
scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/
├── start/
│   ├── StartNode.java
│   └── StartNodeConfig.java
├── template/
│   ├── TemplateNode.java
│   └── TemplateNodeConfig.java
├── httprequest/
│   ├── HttpRequestNode.java
│   └── HttpRequestNodeConfig.java
└── humanfeedback/
    ├── HumanFeedbackNode.java
    └── HumanFeedbackNodeConfig.java
```

### 第三阶段应迁移文件 (P2 - 应该)

```
scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/
├── answer/
│   ├── LLMAnswerNode.java
│   └── LLMAnswerNodeConfig.java
├── classifier/
│   ├── ClassifierNode.java
│   ├── ClassifierNodeConfig.java
│   ├── ClassifierCategory.java
│   ├── ClassifierLLMResp.java
│   └── ClassifierPrompt.java
└── knowledgeretrieval/
    ├── KnowledgeRetrievalNode.java
    └── KnowledgeRetrievalNodeConfig.java
```

---

## 六、实施步骤

### 6.1 步骤流程

1. **步骤1**: 迁移 P0 文件 (AbstractWfNode, DrawNodeUtil, EndNode)
   - 修复 WfState.java 编译错误
   - 验证基础类功能

2. **步骤2**: 迁移 P1 文件 (通用节点)
   - 实现 StartNode, TemplateNode 等
   - 验证基础节点执行

3. **步骤3**: 迁移 P2 文件 (AI节点)
   - 实现 LLMAnswerNode 等AI相关节点
   - 集成 Spring AI 调用

4. **步骤4**: 后续迭代完成 P3, P4 文件
   - 这些可在后续功能迭代中完成

### 6.2 质量保证

- ✅ 严格参考 AIDeepin 逻辑，不臆想
- ✅ 完整代码，不简化
- ✅ 标准注释，无冗余注释
- ✅ 前后逻辑贯通，不成为"缝合怪"
- ✅ 每个改造点明确标注 SCM-AI 的设计差异

---

## 七、风险和注意事项

### 7.1 关键依赖
- ✓ LangGraph4j 库：已有
- ✓ Spring AI 库：已集成
- ✓ Jackson 库：已有（JSON处理）
- ? RabbitMQ 集成：需验证消息队列配置
- ? Elasticsearch 集成：需验证向量存储配置

### 7.2 数据库结构
- ✓ `ai_workflow` 表：已创建
- ✓ `ai_workflow_node` 表：已创建
- ✓ `ai_workflow_component` 表：已创建
- ✓ `ai_workflow_edge` 表：已创建
- ✓ `ai_workflow_runtime_node` 表：已创建

### 7.2.1 ai_workflow_runtime_node 表结构
```
id                     BIGINT PK        # 主键
runtime_node_uuid      VARCHAR(100)     # 运行时节点UUID（唯一）
workflow_runtime_id    BIGINT           # 工作流运行时ID
node_id               BIGINT           # 节点ID（ai_workflow_node.id）
node_uuid             VARCHAR(100)     # 节点UUID
status                TINYINT          # 执行状态（1-就绪, 2-执行中, 3-成功, 4-失败）
status_remark         TEXT             # 状态描述
input                 JSON             # 节点输入参数
output                JSON             # 节点输出结果
c_id, c_time          (自动填充)       # 创建人、时间
u_id, u_time          (自动填充)       # 修改人、时间
dbversion             INT              # 乐观锁版本
is_deleted            TINYINT          # 软删除标记
```

### 7.2.2 对应的 Entity、VO、Mapper、Service
- ✓ `AiWorkflowRuntimeNodeEntity.java`：Entity 类
- ✓ `AiWorkflowRuntimeNodeVo.java`：VO 类
- ✓ `AiWorkflowRuntimeNodeMapper.java`：数据库操作
- ✓ `AiWorkflowRuntimeNodeService.java`：服务层

### 7.3 配置适配
- 用户身份获取方式确认
- JSON 配置反序列化验证
- Spring AI 模型调用验证

---

## 八、预期工作量

| 优先级 | 文件数 | 预估工时 | 说明 |
|--------|--------|----------|------|
| P0 | 3 | 4小时 | 基础类，复杂度高 |
| P1 | 8 | 6小时 | 通用节点，相对简单 |
| P2 | 9 | 8小时 | AI节点，涉及LLM集成 |
| P3 | 7 | 5小时 | 可后续完成 |
| P4 | 13 | 8小时 | 可后续完成 |
| **合计** | **40** | **18-31小时** | **分阶段实施** |

---

## 九、确认清单

### 已确认 ✓
- ✓ `ai_workflow_runtime_node` 表已创建
- ✓ `AiWorkflowRuntimeNodeEntity` Entity 已存在
- ✓ `AiWorkflowRuntimeNodeVo` VO 已存在
- ✓ `AiWorkflowRuntimeNodeMapper` Mapper 已存在
- ✓ `AiWorkflowRuntimeNodeService` Service 已存在

### 需要确认 ❓
- [ ] 确认迁移优先级顺序（P0 → P1 → P2 → P3/P4）
- [ ] 确认 P1-P3 文件是否都需在**当前迭代完成**，还是分步进行？
- [ ] 确认 RabbitMQ 消息队列配置（用于向量化异步处理）
- [ ] 确认 Spring AI 模型服务配置
- [ ] 确认用户身份获取逻辑（SecurityUtil 用法）

---

## 十、下一步

**等待用户确认：**
1. 是否按照此设计方案执行?
2. P1-P3 优先级阶段是否都需要在当前迭代完成?
3. 是否需要先进行技术验证（如RabbitMQ、Spring AI配置）?

