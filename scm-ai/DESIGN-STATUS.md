# AbstractWfNode 迁移设计 - 现状确认

**更新时间**: 2025-10-22 | **状态**: 数据库表和Entity已确认完整

---

## ✅ 已确认的基础设施

### 1. 数据库表完整性
```sql
✓ ai_workflow              - 工作流定义表
✓ ai_workflow_node         - 工作流节点定义表
✓ ai_workflow_component    - 工作流组件库表
✓ ai_workflow_edge         - 工作流连接边表
✓ ai_workflow_runtime      - 工作流运行时表
✓ ai_workflow_runtime_node - 工作流运行时节点表
```

### 2. Java Entity & Mapper 完整性

#### Entity 层（bean/entity/workflow/）
```
✓ AiWorkflowEntity
✓ AiWorkflowNodeEntity
✓ AiWorkflowComponentEntity
✓ AiWorkflowEdgeEntity
✓ AiWorkflowRuntimeEntity
✓ AiWorkflowRuntimeNodeEntity
```

#### Mapper 层（core/mapper/workflow/）
```
✓ AiWorkflowMapper
✓ AiWorkflowNodeMapper
✓ AiWorkflowComponentMapper
✓ AiWorkflowEdgeMapper
✓ AiWorkflowRuntimeMapper
✓ AiWorkflowRuntimeNodeMapper
```

#### VO 层（bean/vo/workflow/）
```
✓ AiWorkflowVo
✓ AiWorkflowNodeVo
✓ AiWorkflowComponentVo
✓ AiWorkflowEdgeVo
✓ AiWorkflowRuntimeVo
✓ AiWorkflowRuntimeNodeVo
```

#### Service 层（core/service/workflow/）
```
✓ AiWorkflowService
✓ AiWorkflowNodeService
✓ AiWorkflowEdgeService
✓ AiWorkflowComponentService
✓ AiWorkflowRuntimeService
✓ AiWorkflowRuntimeNodeService
```

### 3. 运行时节点持久化结构

**ai_workflow_runtime_node 表字段说明**：
```
id                  → BIGINT PK
runtime_node_uuid   → VARCHAR(100) UNIQUE KEY      # 运行时节点唯一标识
workflow_runtime_id → BIGINT FK                    # 指向 ai_workflow_runtime
node_id            → BIGINT                        # 指向 ai_workflow_node.id
node_uuid          → VARCHAR(100)                  # 工作流节点UUID
status             → TINYINT(1-就绪/2-执行/3-成功/4-失败)
status_remark      → TEXT                          # 执行状态说明
input              → JSON                          # 输入参数列表（NodeIOData数组）
output             → JSON                          # 输出结果列表（NodeIOData数组）
[标准审计字段]     → c_id, c_time, u_id, u_time   # 自动填充
dbversion          → INT                           # 乐观锁版本号
is_deleted         → TINYINT(0-未删/1-已删)
```

---

## 📋 设计文档已完成

详见：`DESIGN-AbstractWfNode-Migration.md`

**包含内容**：
- ✅ 现状分析（缺失问题、影响范围）
- ✅ 完整的文件清单和分阶段优先级
- ✅ SCM-AI vs AIDeepin 设计差异详解
- ✅ 40个文件迁移的详细分类
- ✅ 迁移实施细节和改造要点
- ✅ 数据库表结构完整说明
- ✅ 工作量评估

---

## 🔄 WfState.java 编译依赖链

当前阻断点：`AbstractWfNode` 缺失

```
WfState.java (第5行导入失败)
    ↓
需要 AbstractWfNode.java
    ↓
    ├─ P0文件（3个必须）→ 解决编译错误
    │   ├─ AbstractWfNode.java
    │   ├─ DrawNodeUtil.java
    │   └─ EndNode.java
    │
    ├─ P1文件（8个应该）→ 基础节点执行
    │   └─ start/, template/, httprequest/, humanfeedback/
    │
    ├─ P2文件（9个应该）→ AI能力完整
    │   └─ answer/, classifier/, knowledgeretrieval/
    │
    └─ P3/P4文件（20个）→ 后续迭代完成
        └─ faqextractor/, keywordextractor/, switcher等
```

---

## 🎯 待确认的关键决策

### 1️⃣ **迁移范围确认**
- **选项A**（推荐）：完整迁移 P0 + P1 + P2 （20个文件）
  - 优点：功能完整，可支持工作流的核心场景
  - 工时：18小时

- **选项B**：仅迁移 P0 + P1 （11个文件）
  - 优点：最小化投入，快速解决编译错误
  - 缺点：缺少AI能力（LLM回答、分类等）
  - 工时：10小时

- **选项C**：完整迁移所有40个文件
  - 优点：100%功能覆盖
  - 缺点：工作量大（31小时）
  - 工时：31小时

### 2️⃣ **实施时间安排**
- 当前迭代内完成？
- 分步骤进行（P0→P1→P2 分别提交）？

### 3️⃣ **技术集成验证**
需要确认以下配置是否完整：
- [ ] RabbitMQ 消息队列（用于向量化异步处理）
- [ ] Elasticsearch 向量存储（用于RAG检索）
- [ ] Neo4j 图谱存储（用于关系存储）
- [ ] Spring AI 模型服务配置

### 4️⃣ **代码编写规范确认**
- ✓ 严格参考AIDeepin逻辑（无臆想）
- ✓ 完整代码（不简化）
- ✓ 标准注释（无冗余）
- ✓ 前后贯通（无缝合）
- ✓ SCM规范遵循（Entity、VO、Mapper、Service）

---

## 📊 分阶段工作量估算

| 阶段 | 优先级 | 文件数 | 工时 | 说明 |
|------|--------|--------|------|------|
| **必须迁移** | P0 | 3 | 4h | AbstractWfNode 等基础类 |
| **应该迁移** | P1 | 8 | 6h | 通用节点实现 |
| **应该迁移** | P2 | 9 | 8h | AI节点实现 |
| **可后迭代** | P3 | 7 | 5h | 文本处理节点 |
| **可后迭代** | P4 | 13 | 8h | 高级功能节点 |
| **合计** | - | **40** | **18-31h** | 根据范围选择 |

---

## ✍️ 推荐方案

基于当前条件，**推荐采用方案A**：

1. **当前迭代**完成 P0 + P1 + P2 （20个文件，18小时）
   - 解决 WfState.java 编译错误 ✓
   - 实现工作流基础执行功能 ✓
   - 实现AI能力集成 ✓

2. **后续迭代**完成 P3 + P4 （20个文件，13小时）
   - 补充文本处理节点
   - 补充高级功能节点

---

## 📝 下一步行动

**等待用户确认**：
1. ❓ 迁移范围选择（方案A/B/C）？
2. ❓ 当前迭代是否完成所有代码？
3. ❓ 技术集成（RabbitMQ/ES/Neo4j）是否已准备好？
4. ❓ 开始实施 P0 阶段（基础类迁移）？

**确认后立即开始实施**！ 🚀

