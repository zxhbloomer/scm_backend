# 优化WorkflowRoutingService - LLM路由提示词增强方案

**设计日期**: 2025-11-11
**设计人**: Claude Code
**需求来源**: 提升AI工作流智能路由准确率

---

## 一、需求背景

### 当前问题
`WorkflowRoutingService.routeByLLM()` 方法在构建LLM提示词时，提供给LLM的上下文信息不够丰富，导致路由准确率不足。

### 优化目标
1. 为LLM提供更多工作流信息（title、desc、description、keywords、category_name）
2. 统一代码命名规范（数据库`description`字段在Java中统一用`desc`）
3. 使用AiWorkflowVo替代Entity，明确业务视图层语义

---

## 二、数据结构分析

### 数据库表结构 (ai_workflow)

| 字段名 | 类型 | 说明 | 当前使用 | 优化后 |
|--------|------|------|---------|--------|
| title | varchar(200) | 工作流标题 | ✅ | ✅ |
| remark | text | 工作流简短描述 | ❌ | ❌ (暂不使用) |
| description | text | 工作流详细描述(供LLM路由使用) | ✅ (命名混乱) | ✅ (统一叫desc) |
| keywords | varchar(500) | 关键词(逗号分隔) | ❌ | ✅ |
| category | varchar(10) | 分类(字典值) | ❌ | ✅ (关联查询category_name) |
| priority | int | 优先级 | ✅ (排序) | ✅ |

### 字典表结构 (s_dict_data)

| 字段名 | 说明 | 示例值 |
|--------|------|--------|
| code | 字典类型编码 | 'ai_workflow_category' |
| dict_value | 字典值 | '0', '1', '2' |
| label | 字典标签(显示名称) | '业务处理', '知识问答', '通用对话' |

**连接方式**:
```sql
LEFT JOIN s_dict_data
  ON s_dict_data.code = 'ai_workflow_category'
  AND s_dict_data.dict_value = ai_workflow.category
```

---

## 三、核心修改点

### 修改1: AiWorkflowEntity.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/entity/workflow/AiWorkflowEntity.java`

**修改内容**: 字段名统一

```java
// ❌ 修改前
@TableField("description")
private String description;

// ✅ 修改后
/**
 * 工作流详细描述,说明适用场景、功能、输入输出等,供AI路由使用
 * 数据库字段: description
 */
@TableField("description")
private String desc;
```

**影响**: 所有使用 `AiWorkflowEntity.description` 的代码需要改为 `desc`

---

### 修改2: AiWorkflowVo.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/vo/workflow/AiWorkflowVo.java`

**新增字段**:

```java
/**
 * 工作流详细描述(供LLM路由使用)
 * 对应数据库字段: description
 */
private String desc;

/**
 * 关键词(逗号分隔)
 * 对应数据库字段: keywords
 */
private String keywords;

/**
 * 工作流分类(字典值)
 * 对应数据库字段: category
 */
private String category;

/**
 * 工作流分类名称(从字典表关联查询)
 * 对应数据库: s_dict_data.label
 */
private String categoryName;

/**
 * 优先级
 * 对应数据库字段: priority
 */
private Integer priority;
```

**完整Vo类** (关键部分):
```java
@Data
public class AiWorkflowVo {
    private Long id;
    private String workflowUuid;
    private String title;
    private String remark;              // 简短描述(保留,前端可能用)
    private String desc;                // 详细描述(新增,LLM路由用)
    private String keywords;            // 关键词(新增)
    private String category;            // 分类值(新增)
    private String categoryName;        // 分类名称(新增,从字典表关联)
    private Integer priority;           // 优先级(新增)
    private Boolean isPublic;
    private Boolean isEnable;
    private Long userId;
    private String userUuid;
    private String userName;
    private List<AiWorkflowNodeVo> nodes;
    private List<AiWorkflowEdgeVo> edges;
    private LocalDateTime cTime;
    private LocalDateTime uTime;
    private List<String> deleteNodes;
    private List<String> deleteEdges;
}
```

---

### 修改3: AiWorkflowMapper.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/mapper/workflow/AiWorkflowMapper.java`

**修改方法**: `selectAvailableWorkflowsForRouting()`

**修改前**:
```java
@Select("""
    SELECT
        id,
        workflow_uuid AS workflowUuid,
        title,
        remark,
        description,
        keywords,
        category,
        priority,
        ...
    FROM ai_workflow
    WHERE is_enable = 1
      AND (is_public = 1 OR c_id = #{userId})
    ORDER BY ...
    """)
List<AiWorkflowEntity> selectAvailableWorkflowsForRouting(@Param("userId") Long userId);
```

**修改后**:
```java
@Select("""
    SELECT
        t1.id,
        t1.workflow_uuid AS workflowUuid,
        t1.title,
        t1.remark,
        t1.description AS desc,
        t1.keywords,
        t1.category,
        t2.label AS categoryName,
        t1.priority,
        t1.is_public AS isPublic,
        t1.is_enable AS isEnable,
        t1.c_time AS cTime,
        t1.u_time AS uTime
    FROM ai_workflow t1
    LEFT JOIN s_dict_data t2
      ON t2.code = 'ai_workflow_category'
      AND t2.dict_value = t1.category
    WHERE t1.is_enable = 1
      AND (t1.is_public = 1 OR t1.c_id = #{userId})
    ORDER BY
      CASE WHEN t1.c_id = #{userId} THEN 0 ELSE 1 END,
      t1.priority DESC,
      t1.u_time DESC
    """)
List<AiWorkflowVo> selectAvailableWorkflowsForRouting(@Param("userId") Long userId);
```

**关键变化**:
1. ✅ 返回类型: `List<AiWorkflowEntity>` → `List<AiWorkflowVo>`
2. ✅ 字段映射: `description AS desc`
3. ✅ 新增字段: `categoryName` (通过LEFT JOIN获取)
4. ✅ 表别名: `ai_workflow` → `t1`, `s_dict_data` → `t2`

---

### 修改4: WorkflowRoutingService.java

**文件路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/WorkflowRoutingService.java`

#### 修改4.1: route() 方法

**修改前**:
```java
public String route(String userInput, Long userId, String specifiedWorkflowUuid) {
    // ...
    List<AiWorkflowEntity> availableWorkflows =
        aiWorkflowMapper.selectAvailableWorkflowsForRouting(userId);

    String workflowUuid = routeByLLM(userInput, availableWorkflows);
    // ...
}
```

**修改后**:
```java
public String route(String userInput, Long userId, String specifiedWorkflowUuid) {
    // ...
    List<AiWorkflowVo> availableWorkflows =
        aiWorkflowMapper.selectAvailableWorkflowsForRouting(userId);

    String workflowUuid = routeByLLM(userInput, availableWorkflows);
    // ...
}
```

#### 修改4.2: routeByLLM() 方法签名

**修改前**:
```java
private String routeByLLM(String userInput, List<AiWorkflowEntity> workflows)
```

**修改后**:
```java
private String routeByLLM(String userInput, List<AiWorkflowVo> workflows)
```

#### 修改4.3: 提示词构建逻辑

**修改前**:
```java
String workflowsJson = workflows.stream()
    .map(w -> String.format("{uuid:\"%s\",title:\"%s\",desc:\"%s\"}",
        w.getWorkflowUuid(),
        w.getTitle(),
        StringUtils.defaultString(w.getDescription(), "")))
    .collect(Collectors.joining(","));
```

**修改后**:
```java
String workflowsJson = workflows.stream()
    .map(w -> String.format(
        "{uuid:\"%s\",title:\"%s\",desc:\"%s\",description:\"%s\",keywords:\"%s\",category_name:\"%s\"}",
        w.getWorkflowUuid(),
        w.getTitle(),
        safeString(w.getRemark(), ""),           // 简短描述
        safeString(w.getDesc(), ""),             // 详细描述(LLM路由用)
        safeString(w.getKeywords(), ""),         // 关键词
        safeString(w.getCategoryName(), "未分类") // 分类名称
    ))
    .collect(Collectors.joining(","));

// 辅助方法: 安全字符串处理
private String safeString(String value, String defaultValue) {
    return StringUtils.defaultIfBlank(value, defaultValue);
}
```

**新提示词示例**:
```
用户输入: "查询订单ORD-001的物流信息"

可用工作流: [
  {
    uuid:"aaa-111",
    title:"订单查询",
    desc:"查询订单详情",
    description:"专门处理订单相关查询,包括订单状态、物流信息、订单历史等。适用场景:用户询问具体订单信息。",
    keywords:"订单,查询,物流,状态,order",
    category_name:"业务处理"
  },
  {
    uuid:"bbb-222",
    title:"客户管理",
    desc:"客户信息查询",
    description:"处理客户信息查询和修改,包括客户资料、联系方式、交易历史等。",
    keywords:"客户,企业,联系方式,customer",
    category_name:"业务处理"
  }
]
```

---

## 四、影响范围分析

### 文件修改清单

| 文件 | 修改类型 | 说明 |
|------|---------|------|
| AiWorkflowEntity.java | 字段改名 | `description` → `desc` |
| AiWorkflowVo.java | 新增字段 | 增加 `desc`, `keywords`, `category`, `categoryName`, `priority` |
| AiWorkflowMapper.java | SQL改写 | LEFT JOIN字典表, 返回类型改为Vo |
| WorkflowRoutingService.java | 类型&逻辑 | 参数类型改为Vo, 提示词包含更多字段 |
| AiWorkflowService.java | 潜在影响 | 如果使用了 `selectAvailableWorkflowsForRouting()` 需要同步 |

### 需要搜索确认的代码位置

```bash
# 1. 搜索所有使用 AiWorkflowEntity.description 的地方
grep -r "\.getDescription()" scm-ai/src/
grep -r "\.setDescription(" scm-ai/src/

# 2. 搜索所有调用 selectAvailableWorkflowsForRouting 的地方
grep -r "selectAvailableWorkflowsForRouting" scm-ai/src/

# 3. 搜索所有调用 routeByLLM 的地方
grep -r "routeByLLM" scm-ai/src/
```

---

## 五、KISS原则检查

### 1. "这是个真问题还是臆想出来的？"
✅ **真问题**: LLM路由准确率不足是用户实际遇到的问题

### 2. "有更简单的方法吗？"
✅ **已是最简**:
- 不引入新表
- 不增加复杂逻辑
- 只是丰富提示词信息

### 3. "会破坏什么吗？"
✅ **影响可控**:
- Entity字段改名会导致编译错误(好事,编译器提示所有影响点)
- Mapper返回类型改变,调用处会编译报错
- 所有影响点都能通过编译检查发现

### 4. "当前项目真的需要这个功能吗？"
✅ **真需求**: 提升AI工作流路由准确率是核心业务需求

### 5. "数据支撑"
✅ **有数据**:
- 数据库已有所有需要的字段
- 字典表已有category数据
- 不需要额外数据准备

---

## 六、实施步骤

### Step 1: 修改Entity (AiWorkflowEntity.java)
```java
// Line 117: 字段改名
@TableField("description")
private String desc;  // 原: private String description;
```

### Step 2: 修改Vo (AiWorkflowVo.java)
```java
// 新增字段
private String desc;
private String keywords;
private String category;
private String categoryName;
private Integer priority;
```

### Step 3: 修改Mapper (AiWorkflowMapper.java)
```java
// Line 176-206: 重写SQL
@Select("""
    SELECT
        t1.id,
        t1.workflow_uuid AS workflowUuid,
        t1.title,
        t1.remark,
        t1.description AS desc,
        t1.keywords,
        t1.category,
        t2.label AS categoryName,
        t1.priority,
        t1.is_public AS isPublic,
        t1.is_enable AS isEnable,
        t1.c_time AS cTime,
        t1.u_time AS uTime
    FROM ai_workflow t1
    LEFT JOIN s_dict_data t2
      ON t2.code = 'ai_workflow_category'
      AND t2.dict_value = t1.category
    WHERE t1.is_enable = 1
      AND (t1.is_public = 1 OR t1.c_id = #{userId})
    ORDER BY
      CASE WHEN t1.c_id = #{userId} THEN 0 ELSE 1 END,
      t1.priority DESC,
      t1.u_time DESC
    """)
List<AiWorkflowVo> selectAvailableWorkflowsForRouting(@Param("userId") Long userId);
```

### Step 4: 修改Service (WorkflowRoutingService.java)
```java
// Line 61: 修改变量类型
List<AiWorkflowVo> availableWorkflows =
    aiWorkflowMapper.selectAvailableWorkflowsForRouting(userId);

// Line 69: 修改方法参数
String workflowUuid = routeByLLM(userInput, availableWorkflows);

// Line 95: 修改方法签名
private String routeByLLM(String userInput, List<AiWorkflowVo> workflows) {
    // ...

    // Line 102-107: 修改提示词构建
    String workflowsJson = workflows.stream()
        .map(w -> String.format(
            "{uuid:\"%s\",title:\"%s\",desc:\"%s\",description:\"%s\",keywords:\"%s\",category_name:\"%s\"}",
            w.getWorkflowUuid(),
            w.getTitle(),
            safeString(w.getRemark(), ""),
            safeString(w.getDesc(), ""),
            safeString(w.getKeywords(), ""),
            safeString(w.getCategoryName(), "未分类")
        ))
        .collect(Collectors.joining(","));
}

// 新增辅助方法
private String safeString(String value, String defaultValue) {
    return StringUtils.defaultIfBlank(value, defaultValue);
}
```

### Step 5: 全局搜索并修改影响点
```bash
# 搜索所有使用 .getDescription() 的地方,改为 .getDesc()
# 搜索所有使用 .setDescription() 的地方,改为 .setDesc()
```

### Step 6: 编译测试
```bash
cd scm-ai
mvn clean compile

# 编译器会报错提示所有需要修改的地方
# 逐一修改直到编译通过
```

---

## 七、测试验证

### 验证1: SQL正确性
```sql
-- 验证字典表连接是否正确
SELECT
    t1.id,
    t1.workflow_uuid,
    t1.title,
    t1.description AS desc,
    t1.keywords,
    t1.category,
    t2.label AS categoryName
FROM ai_workflow t1
LEFT JOIN s_dict_data t2
  ON t2.code = 'ai_workflow_category'
  AND t2.dict_value = t1.category
WHERE t1.is_enable = 1
LIMIT 5;
```

### 验证2: LLM路由测试
```java
// 测试用例
String userInput = "查询订单ORD-001";
String workflowUuid = workflowRoutingService.route(userInput, 123L, null);

// 验证:
// 1. 返回的workflowUuid是否正确
// 2. 日志中的提示词是否包含所有新增字段
// 3. LLM返回的reasoning是否更详细
```

### 验证3: 边界情况
```java
// 测试category为null的情况
// 测试keywords为空字符串的情况
// 测试description为null的情况
```

---

## 八、回滚方案

如果修改后出现问题,回滚步骤:

1. ✅ Git回滚所有Java代码修改
2. ✅ 重新编译部署
3. ✅ 数据库无改动,无需回滚

---

## 九、预期效果

### 修改前提示词:
```
可用工作流: [{uuid:"aaa",title:"订单查询",desc:""}]
```

### 修改后提示词:
```
可用工作流: [{
  uuid:"aaa",
  title:"订单查询",
  desc:"查询订单详情",
  description:"专门处理订单相关查询,包括订单状态、物流信息等",
  keywords:"订单,查询,物流,order",
  category_name:"业务处理"
}]
```

### 预期提升:
- ✅ LLM有更多上下文信息做判断
- ✅ 关键词匹配更精准
- ✅ 分类信息辅助理解工作流用途
- ✅ 详细描述帮助LLM理解适用场景

---

**方案制定完成，等待用户审批**
