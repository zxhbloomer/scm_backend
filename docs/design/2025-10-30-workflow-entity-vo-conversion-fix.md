# Workflow Entity→VO 转换修复方案

**日期**: 2025-10-30
**作者**: zzxxhh (SCM AI Team)
**问题**: 工作流运行时 API 返回的 `inputData` 和 `outputData` 字段为空对象

---

## 1. 问题诊断

### 现象
API 返回的 `inputData` 和 `outputData` 始终为空对象 `{}`，无法展示工作流节点的实际输入输出数据。

**用户提供的 HTTP 请求**:
```json
{
  "workflowUuid": "lWfILVkj",
  "params": [
    {
      "name": "var_user_input",
      "content": {"type": 1, "value": "阿萨斯", "title": "用户输入"},
      "required": false
    }
  ]
}
```

**SSE 响应**:
```json
{
  "event": "node",
  "data": {
    "id": 3690,
    "runtimeNodeUuid": "qw2rvgYN",
    "workflowRuntimeId": 1325,
    "nodeId": 1148,
    "inputData": {},   // ❌ 应该包含实际数据
    "outputData": {},  // ❌ 应该包含实际数据
    "status": 2
  }
}
```

### 根本原因分析

**数据流向问题**:
```
数据库 MySQL (json type, stored as text)
    ↓
MyBatis Plus 查询
    ↓
Entity.inputData (String type) = "{\"var_user_input\":\"阿萨斯\"}"
    ↓
changeNodeToDTO(entity) / changeRuntimeToDTO(entity)
    ↓
BeanUtils.copyProperties(entity, vo)  ← ❌ String → JSONObject 转换失败
    ↓
vo.inputData = null (未被赋值)
    ↓
fillInputOutput(vo)
    ↓
vo.inputData = new JSONObject()  ← 设置空对象（防御性逻辑）
    ↓
Jackson 序列化
    ↓
API 响应: {"inputData": {}, "outputData": {}}
```

**技术原因**:
1. **Entity 层设计**: 使用 `String` 类型存储 JSON（参考 `2025-10-29-workflow-entity-vo-separation.md` 设计决策）
2. **VO 层设计**: 使用 `JSONObject` 类型供 API 使用
3. **转换缺陷**: `changeNodeToDTO()` 和 `changeRuntimeToDTO()` 方法仅调用 `BeanUtils.copyProperties()`
4. **类型不兼容**: `BeanUtils.copyProperties()` 无法自动转换 `String` → `JSONObject`
5. **问题掩盖**: `fillInputOutput()` 检测到 null，设置空 JSONObject，掩盖了真实问题

---

## 2. KISS 原则 4 问题回答

### Q1: 这是个真问题还是臆想出来的？
✅ **真问题**
- 生产环境 API 返回数据丢失
- 用户无法看到工作流节点的输入输出数据
- 影响业务功能的正常使用

### Q2: 有更简单的方法吗？
✅ **是**
- 删除不必要的 `changeNodeToDTO()` 和 `changeRuntimeToDTO()` 方法
- 直接在调用点使用 `BeanUtils.copyProperties()` + 手动 JSON 转换
- 代码更清晰，逻辑更直接

### Q3: 会破坏什么吗？
❌ **不会**
- 删除的是 private 方法，无外部调用
- 所有调用点都会显式实现转换逻辑
- 保持向后兼容，API 响应格式不变

### Q4: 当前项目真的需要这个功能吗？
✅ **必须修复**
- 工作流运行时数据是核心业务数据
- 数据展示是基础功能要求
- 不修复将导致功能不可用

---

## 3. 完整调用链路分析

### 当前调用链路（有问题）

**AiWorkflowRuntimeNodeService.listByWfRuntimeId()**:
```
Controller 调用
    ↓
listByWfRuntimeId()
    ↓
MyBatis Plus selectList()
    ↓
List<Entity> (inputData 是 String)
    ↓
for each entity:
    changeNodeToDTO(entity)  ← ❌ JSON 字段未转换
    fillInputOutput(vo)      ← 设置空对象
    ↓
List<VO> (inputData 是 empty JSONObject)
    ↓
返回给 Controller
```

**AiWorkflowRuntimeService.page()**:
```
Controller 调用
    ↓
page()
    ↓
MyBatis Plus selectPage()
    ↓
Page<Entity> (inputData 是 String)
    ↓
for each entity:
    changeRuntimeToDTO(entity)  ← ❌ JSON 字段未转换
    fillInputOutput(vo)         ← 设置空对象
    ↓
Page<VO> (inputData 是 empty JSONObject)
    ↓
返回给 Controller
```

### 修复后的调用链路

```
Controller 调用
    ↓
Service 方法
    ↓
MyBatis Plus 查询
    ↓
Entity (inputData 是 String)
    ↓
直接在调用点:
    BeanUtils.copyProperties(entity, vo)  ← 复制非 JSON 字段
    手动转换 JSON 字段:
        vo.setInputData(JSON.parseObject(entity.getInputData()))
        vo.setOutputData(JSON.parseObject(entity.getOutputData()))
    ↓
VO (inputData 是 populated JSONObject)
    ↓
fillInputOutput(vo)  ← 防御性检查（此时已有数据）
    ↓
返回给 Controller
```

---

## 4. 按文件的详细修改方案

### 文件 1: AiWorkflowRuntimeNodeService.java

**位置**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/AiWorkflowRuntimeNodeService.java`

#### 修改点 1: listByWfRuntimeId() 方法（Lines 42-58）

**修改前**:
```java
public List<AiWorkflowRuntimeNodeVo> listByWfRuntimeId(Long wfRuntimeId) {
    List<AiWorkflowRuntimeNodeEntity> entityList = aiWorkflowRuntimeNodeMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowRuntimeNodeEntity>()
                    .eq(AiWorkflowRuntimeNodeEntity::getWorkflowRuntimeId, wfRuntimeId)
                    .eq(AiWorkflowRuntimeNodeEntity::getIsDeleted, 0)
                    .orderByAsc(AiWorkflowRuntimeNodeEntity::getId)
    );

    List<AiWorkflowRuntimeNodeVo> result = new ArrayList<>();
    for (AiWorkflowRuntimeNodeEntity entity : entityList) {
        AiWorkflowRuntimeNodeVo vo = changeNodeToDTO(entity);
        fillInputOutput(vo);
        result.add(vo);
    }

    return result;
}
```

**修改后**:
```java
public List<AiWorkflowRuntimeNodeVo> listByWfRuntimeId(Long wfRuntimeId) {
    List<AiWorkflowRuntimeNodeEntity> entityList = aiWorkflowRuntimeNodeMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowRuntimeNodeEntity>()
                    .eq(AiWorkflowRuntimeNodeEntity::getWorkflowRuntimeId, wfRuntimeId)
                    .eq(AiWorkflowRuntimeNodeEntity::getIsDeleted, 0)
                    .orderByAsc(AiWorkflowRuntimeNodeEntity::getId)
    );

    List<AiWorkflowRuntimeNodeVo> result = new ArrayList<>();
    for (AiWorkflowRuntimeNodeEntity entity : entityList) {
        AiWorkflowRuntimeNodeVo vo = new AiWorkflowRuntimeNodeVo();
        BeanUtils.copyProperties(entity, vo);

        // 手动转换 JSON 字段: String → JSONObject
        if (StringUtils.isNotBlank(entity.getInputData())) {
            vo.setInputData(JSON.parseObject(entity.getInputData()));
        }
        if (StringUtils.isNotBlank(entity.getOutputData())) {
            vo.setOutputData(JSON.parseObject(entity.getOutputData()));
        }

        fillInputOutput(vo);
        result.add(vo);
    }

    return result;
}
```

#### 修改点 2: createByState() 方法（Lines 70-89）

**修改前**:
```java
public AiWorkflowRuntimeNodeVo createByState(Long userId, Long wfNodeId,
                                              Long wfRuntimeId, WfNodeState state) {
    // 参考 aideepin:46-52
    AiWorkflowRuntimeNodeEntity runtimeNode = new AiWorkflowRuntimeNodeEntity();
    runtimeNode.setRuntimeNodeUuid(state.getUuid());
    runtimeNode.setWorkflowRuntimeId(wfRuntimeId);
    runtimeNode.setNodeId(wfNodeId);
    runtimeNode.setStatus(state.getProcessStatus());
    runtimeNode.setIsDeleted(false);
    // 不设置 c_time, u_time, c_id, u_id, dbversion - 自动填充
    aiWorkflowRuntimeNodeMapper.insert(runtimeNode);

    // 参考 aideepin:53 - 重新查询获取完整数据
    runtimeNode = aiWorkflowRuntimeNodeMapper.selectById(runtimeNode.getId());

    // 参考 aideepin:55-58 - 转换为 VO
    AiWorkflowRuntimeNodeVo vo = changeNodeToDTO(runtimeNode);
    fillInputOutput(vo);
    return vo;
}
```

**修改后**:
```java
public AiWorkflowRuntimeNodeVo createByState(Long userId, Long wfNodeId,
                                              Long wfRuntimeId, WfNodeState state) {
    // 参考 aideepin:46-52
    AiWorkflowRuntimeNodeEntity runtimeNode = new AiWorkflowRuntimeNodeEntity();
    runtimeNode.setRuntimeNodeUuid(state.getUuid());
    runtimeNode.setWorkflowRuntimeId(wfRuntimeId);
    runtimeNode.setNodeId(wfNodeId);
    runtimeNode.setStatus(state.getProcessStatus());
    runtimeNode.setIsDeleted(false);
    // 不设置 c_time, u_time, c_id, u_id, dbversion - 自动填充
    aiWorkflowRuntimeNodeMapper.insert(runtimeNode);

    // 参考 aideepin:53 - 重新查询获取完整数据
    runtimeNode = aiWorkflowRuntimeNodeMapper.selectById(runtimeNode.getId());

    // 参考 aideepin:55-58 - 转换为 VO
    AiWorkflowRuntimeNodeVo vo = new AiWorkflowRuntimeNodeVo();
    BeanUtils.copyProperties(runtimeNode, vo);

    // 手动转换 JSON 字段: String → JSONObject
    if (StringUtils.isNotBlank(runtimeNode.getInputData())) {
        vo.setInputData(JSON.parseObject(runtimeNode.getInputData()));
    }
    if (StringUtils.isNotBlank(runtimeNode.getOutputData())) {
        vo.setOutputData(JSON.parseObject(runtimeNode.getOutputData()));
    }

    fillInputOutput(vo);
    return vo;
}
```

#### 修改点 3: 删除 changeNodeToDTO() 方法（Lines 165-169）

**删除整个方法**:
```java
/**
 * 将节点实体转换为VO
 *
 * @param entity 节点实体
 * @return 节点VO
 */
private AiWorkflowRuntimeNodeVo changeNodeToDTO(AiWorkflowRuntimeNodeEntity entity) {
    AiWorkflowRuntimeNodeVo vo = new AiWorkflowRuntimeNodeVo();
    BeanUtils.copyProperties(entity, vo);
    return vo;
}
```

#### Import 语句检查

确保文件顶部包含:
```java
import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
```

---

### 文件 2: AiWorkflowRuntimeService.java

**位置**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/AiWorkflowRuntimeService.java`

#### 修改点 1: create() 方法（Lines 51-63）

**修改前**:
```java
public AiWorkflowRuntimeVo create(Long userId, Long workflowId) {
    AiWorkflowRuntimeEntity runtime = new AiWorkflowRuntimeEntity();
    runtime.setRuntimeUuid(UuidUtil.createShort());
    runtime.setUserId(userId);
    runtime.setWorkflowId(workflowId);
    runtime.setStatus(1); // 1-运行中
    runtime.setIsDeleted(false);
    // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
    aiWorkflowRuntimeMapper.insert(runtime);

    runtime = aiWorkflowRuntimeMapper.selectById(runtime.getId());
    return changeRuntimeToDTO(runtime);
}
```

**修改后**:
```java
public AiWorkflowRuntimeVo create(Long userId, Long workflowId) {
    AiWorkflowRuntimeEntity runtime = new AiWorkflowRuntimeEntity();
    runtime.setRuntimeUuid(UuidUtil.createShort());
    runtime.setUserId(userId);
    runtime.setWorkflowId(workflowId);
    runtime.setStatus(1); // 1-运行中
    runtime.setIsDeleted(false);
    // 不设置c_time, u_time, c_id, u_id, dbversion - 自动填充
    aiWorkflowRuntimeMapper.insert(runtime);

    runtime = aiWorkflowRuntimeMapper.selectById(runtime.getId());

    AiWorkflowRuntimeVo vo = new AiWorkflowRuntimeVo();
    BeanUtils.copyProperties(runtime, vo);

    // 手动转换 JSON 字段: String → JSONObject
    if (StringUtils.isNotBlank(runtime.getInputData())) {
        vo.setInputData(JSON.parseObject(runtime.getInputData()));
    }
    if (StringUtils.isNotBlank(runtime.getOutputData())) {
        vo.setOutputData(JSON.parseObject(runtime.getOutputData()));
    }

    return vo;
}
```

#### 修改点 2: page() 方法（Lines 177-201）

**修改前**:
```java
public Page<AiWorkflowRuntimeVo> page(String workflowUuid, Integer currentPage, Integer pageSize) {
    AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

    Page<AiWorkflowRuntimeEntity> entityPage = aiWorkflowRuntimeMapper.selectPage(
            new Page<>(currentPage, pageSize),
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowRuntimeEntity>()
                    .eq(AiWorkflowRuntimeEntity::getWorkflowId, workflow.getId())
                    .eq(AiWorkflowRuntimeEntity::getIsDeleted, 0)
                    .orderByDesc(AiWorkflowRuntimeEntity::getUTime)
    );

    Page<AiWorkflowRuntimeVo> voPage = new Page<>();
    voPage.setCurrent(entityPage.getCurrent());
    voPage.setSize(entityPage.getSize());
    voPage.setTotal(entityPage.getTotal());

    List<AiWorkflowRuntimeVo> voList = new ArrayList<>();
    for (AiWorkflowRuntimeEntity entity : entityPage.getRecords()) {
        AiWorkflowRuntimeVo vo = changeRuntimeToDTO(entity);
        fillInputOutput(vo);
        voList.add(vo);
    }
    voPage.setRecords(voList);

    return voPage;
}
```

**修改后**:
```java
public Page<AiWorkflowRuntimeVo> page(String workflowUuid, Integer currentPage, Integer pageSize) {
    AiWorkflowEntity workflow = workflowService.getOrThrow(workflowUuid);

    Page<AiWorkflowRuntimeEntity> entityPage = aiWorkflowRuntimeMapper.selectPage(
            new Page<>(currentPage, pageSize),
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<AiWorkflowRuntimeEntity>()
                    .eq(AiWorkflowRuntimeEntity::getWorkflowId, workflow.getId())
                    .eq(AiWorkflowRuntimeEntity::getIsDeleted, 0)
                    .orderByDesc(AiWorkflowRuntimeEntity::getUTime)
    );

    Page<AiWorkflowRuntimeVo> voPage = new Page<>();
    voPage.setCurrent(entityPage.getCurrent());
    voPage.setSize(entityPage.getSize());
    voPage.setTotal(entityPage.getTotal());

    List<AiWorkflowRuntimeVo> voList = new ArrayList<>();
    for (AiWorkflowRuntimeEntity entity : entityPage.getRecords()) {
        AiWorkflowRuntimeVo vo = new AiWorkflowRuntimeVo();
        BeanUtils.copyProperties(entity, vo);

        // 手动转换 JSON 字段: String → JSONObject
        if (StringUtils.isNotBlank(entity.getInputData())) {
            vo.setInputData(JSON.parseObject(entity.getInputData()));
        }
        if (StringUtils.isNotBlank(entity.getOutputData())) {
            vo.setOutputData(JSON.parseObject(entity.getOutputData()));
        }

        fillInputOutput(vo);
        voList.add(vo);
    }
    voPage.setRecords(voList);

    return voPage;
}
```

#### 修改点 3: 删除 changeRuntimeToDTO() 方法（Lines 258-261）

**删除整个方法**:
```java
/**
 * 将运行实例实体转换为VO
 *
 * @param runtime 运行实例实体
 * @return 运行实例VO
 */
private AiWorkflowRuntimeVo changeRuntimeToDTO(AiWorkflowRuntimeEntity runtime) {
    AiWorkflowRuntimeVo vo = new AiWorkflowRuntimeVo();
    BeanUtils.copyProperties(runtime, vo);
    return vo;
}
```

#### Import 语句检查

确保文件顶部包含:
```java
import com.alibaba.fastjson2.JSON;
import org.apache.commons.lang3.StringUtils;
```

---

## 5. 风险分析和缓解措施

| 风险类型 | 风险描述 | 严重程度 | 缓解措施 | 状态 |
|---------|---------|---------|---------|------|
| **JSON 解析异常** | JSON.parseObject() 遇到非法 JSON 字符串时抛出异常 | 🟡 中 | 1. 依赖数据库约束保证 JSON 格式正确<br>2. fillInputOutput() 作为防御性检查<br>3. 如有必要可添加 try-catch | ✅ 可接受 |
| **空指针异常** | Entity 字段为 null 时的处理 | 🟢 低 | StringUtils.isNotBlank() 已处理 null 情况 | ✅ 已解决 |
| **性能影响** | 增加了 JSON 解析操作 | 🟢 低 | JSON.parseObject() 是轻量级操作，影响可忽略 | ✅ 可接受 |
| **向后兼容性** | 删除 private 方法的影响 | 🟢 低 | 方法是 private，无外部依赖 | ✅ 无风险 |
| **数据一致性** | 确保所有调用点都正确转换 | 🟡 中 | 1. 代码审查确认所有 4 个调用点都已修改<br>2. 集成测试验证 | ⏳ 需验证 |
| **漏改调用点** | 可能存在其他未发现的调用点 | 🟡 中 | 1. IDE 全局搜索 changeNodeToDTO 和 changeRuntimeToDTO<br>2. 编译器会报错未定义方法 | ✅ 编译检查 |

### 增强的 JSON 解析代码（可选）

如果需要更健壮的异常处理，可以使用：

```java
// 带异常处理的版本
try {
    if (StringUtils.isNotBlank(entity.getInputData())) {
        vo.setInputData(JSON.parseObject(entity.getInputData()));
    }
} catch (Exception e) {
    log.error("Failed to parse inputData JSON: {}", entity.getInputData(), e);
    vo.setInputData(new JSONObject());  // 降级为空对象
}

try {
    if (StringUtils.isNotBlank(entity.getOutputData())) {
        vo.setOutputData(JSON.parseObject(entity.getOutputData()));
    }
} catch (Exception e) {
    log.error("Failed to parse outputData JSON: {}", entity.getOutputData(), e);
    vo.setOutputData(new JSONObject());
}
```

**建议**: 暂时不添加 try-catch，依赖数据库约束和 `fillInputOutput()` 防御性逻辑。如果生产环境出现 JSON 解析异常，再考虑添加。

---

## 6. 数据支撑和分析

### 现有数据证据

1. **用户提供的 HTTP 请求**:
   - 工作流执行参数: `[{"name":"var_user_input","content":{"type":1,"value":"阿萨斯","title":"用户输入"},"required":false}]`
   - 证明输入数据确实存在

2. **SSE 响应数据**:
   - 显示 `"inputData": {}, "outputData": {}`
   - 证明 API 层数据丢失

3. **设计文档历史**:
   - `2025-10-29-workflow-entity-vo-separation.md`: 确认 Entity/VO 分离模式
   - `2025-10-30-字段重命名-input-output.md`: 确认字段命名规范

### 数据库验证查询（实施前）

```sql
-- 验证数据库中的 JSON 数据格式
SELECT
    id,
    runtime_node_uuid,
    input_data,
    output_data,
    status
FROM ai_workflow_runtime_node
WHERE workflow_runtime_id = (
    SELECT id FROM ai_workflow_runtime
    ORDER BY c_time DESC
    LIMIT 1
)
ORDER BY id DESC;
```

**预期结果**:
```
id: 3690
runtime_node_uuid: qw2rvgYN
input_data: {"var_user_input":"阿萨斯"}  ← JSON 文本
output_data: null 或 JSON 文本
status: 2
```

如果数据库中 `input_data` 确实是有效的 JSON 字符串，则证实问题在于 Entity → VO 转换逻辑。

---

## 7. 实施步骤

### 代码修改阶段

- [ ] **Step 1**: 修改 `AiWorkflowRuntimeNodeService.listByWfRuntimeId()`（Lines 42-58）
- [ ] **Step 2**: 修改 `AiWorkflowRuntimeNodeService.createByState()`（Lines 70-89）
- [ ] **Step 3**: 删除 `AiWorkflowRuntimeNodeService.changeNodeToDTO()`（Lines 165-169）
- [ ] **Step 4**: 修改 `AiWorkflowRuntimeService.create()`（Lines 51-63）
- [ ] **Step 5**: 修改 `AiWorkflowRuntimeService.page()`（Lines 177-201）
- [ ] **Step 6**: 删除 `AiWorkflowRuntimeService.changeRuntimeToDTO()`（Lines 258-261）

### 编译验证阶段

```bash
cd D:\2025_project\20_project_in_github\00_scm_backend\scm_backend
mvn clean compile
```

**预期结果**: 编译成功，无错误

### 集成测试阶段

1. **启动应用**:
   ```bash
   cd scm-start
   mvn spring-boot:run
   ```

2. **执行测试**:
   - 通过 API 执行工作流
   - 使用用户提供的测试参数
   - 观察 SSE 响应

3. **验证点**:
   - `inputData` 字段包含实际数据: `{"var_user_input": "阿萨斯"}`
   - `outputData` 字段根据节点执行结果填充
   - 无 JSON 解析异常
   - 响应时间无明显增加

### QA 代码评审阶段

- [ ] 审查所有 4 个修改点的代码
- [ ] 确认 JSON 解析逻辑正确
- [ ] 验证 import 语句完整
- [ ] 检查是否有其他调用点遗漏
- [ ] 验证 `fillInputOutput()` 逻辑仍然有效

---

## 8. 修改文件清单

| 文件路径 | 修改类型 | 修改点 | 行号范围 | 影响范围 |
|---------|---------|-------|---------|---------|
| `AiWorkflowRuntimeNodeService.java` | 代码修改 | listByWfRuntimeId() | 42-58 | 查询节点列表 |
| `AiWorkflowRuntimeNodeService.java` | 代码修改 | createByState() | 70-89 | 创建节点记录 |
| `AiWorkflowRuntimeNodeService.java` | 删除方法 | changeNodeToDTO() | 165-169 | 内部转换方法 |
| `AiWorkflowRuntimeService.java` | 代码修改 | create() | 51-63 | 创建运行实例 |
| `AiWorkflowRuntimeService.java` | 代码修改 | page() | 177-201 | 分页查询 |
| `AiWorkflowRuntimeService.java` | 删除方法 | changeRuntimeToDTO() | 258-261 | 内部转换方法 |

**总计**: 2 个文件，4 个代码修改，2 个方法删除

---

## 9. 方案总结

### 核心改进

1. **删除冗余抽象**: 移除 `changeNodeToDTO()` 和 `changeRuntimeToDTO()` 方法
2. **显式类型转换**: 在每个调用点显式执行 JSON 字段转换（String → JSONObject）
3. **保持一致性**: 统一使用 `BeanUtils.copyProperties()` + 手动 JSON 转换模式
4. **防御性检查**: 保留 `fillInputOutput()` 作为最后的防御

### 符合 KISS 原则

- ✅ **解决真实问题**: 修复生产环境数据丢失问题
- ✅ **采用最简方案**: 删除抽象层，直接转换
- ✅ **零破坏性**: private 方法删除无外部影响
- ✅ **代码更清晰**: 显式转换逻辑易于理解和维护

### 技术优势

1. **类型安全**: 显式转换避免类型不匹配
2. **易于调试**: 转换逻辑在调用点可见
3. **性能稳定**: JSON 解析是轻量级操作
4. **可维护性**: 减少抽象层，代码更直接

### 下一步

✅ **Step 3: 数据驱动的方案设计** - 完成
⏳ **Step 4: 方案审批流程** - 等待用户审批
⏳ **Step 5: 实施阶段** - 待批准后执行
⏳ **Step 6: QA 代码评审** - 待实施后评审

---

**文档版本**: v1.0
**最后更新**: 2025-10-30
**状态**: 等待审批
