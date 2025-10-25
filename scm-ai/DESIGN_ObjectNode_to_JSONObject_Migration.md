# Jackson ObjectNode 到 Fastjson2 JSONObject 迁移设计方案

## 1. 项目概述

**目标**: 将 scm-ai 模块中所有使用 Jackson `ObjectNode` 的地方替换为 Fastjson2 `JSONObject`

**背景**:
- scm-ai 已迁移到 Fastjson2 作为主要 JSON 处理库
- 目前仍有部分代码使用 Jackson 的 ObjectNode，导致库依赖混乱和潜在的序列化问题
- 需要统一使用 Fastjson2 的 JSONObject 以保持技术栈一致性

**影响范围**: 14 个 Java 文件，共 37 处使用

---

## 2. 受影响文件清单

### 2.1 Entity 层（1 个文件）
- `AiWorkflowNodeEntity.java` - 工作流节点实体类

### 2.2 VO 层（2 个文件）
- `AiWorkflowRuntimeNodeVo.java` - 工作流运行时节点 VO
- `AiWorkflowRuntimeVo.java` - 工作流运行时 VO

### 2.3 Controller 层（1 个文件）
- `WorkflowController.java` - 工作流控制器

### 2.4 Service 层（3 个文件）
- `AiWorkflowNodeService.java` - 工作流节点服务
- `AiWorkflowRuntimeNodeService.java` - 工作流运行时节点服务
- `AiWorkflowRuntimeService.java` - 工作流运行时服务

### 2.5 工具类（1 个文件）
- `JsonUtil.java` - JSON 工具类

### 2.6 工作流引擎（6 个文件）
- `AbstractWfNode.java` - 抽象工作流节点
- `EndNode.java` - 结束节点
- `SwitcherNode.java` - 分支节点
- `WfNodeIODataUtil.java` - 节点 IO 数据工具
- `WorkflowEngine.java` - 工作流引擎
- `WorkflowStarter.java` - 工作流启动器

---

## 3. 核心问题分析

### 3.1 当前问题

**问题 1: 数据库序列化失败**
- **位置**: `AiWorkflowNodeEntity.inputConfig` 字段
- **原因**: 使用 `JacksonTypeHandler` 处理强类型 Bean `AiWfNodeInputConfigVo`
- **症状**: 从数据库读取的 `inputConfig` 为 null，导致前端无法显示 Start 节点的 user_inputs
- **根本原因**: JacksonTypeHandler 与 Fastjson2 Bean 不兼容

**问题 2: 库依赖混乱**
- 同时依赖 Jackson 和 Fastjson2
- 增加项目复杂度和维护成本
- 可能导致版本冲突

**问题 3: 类型转换复杂**
- `ObjectNode` 和 `JSONObject` 之间需要转换
- 增加了代码复杂度和出错风险

### 3.2 迁移目标

1. **完全移除 Jackson ObjectNode 依赖**
2. **统一使用 Fastjson2 JSONObject**
3. **创建自定义 TypeHandler 处理 JSON 字段**
4. **保持数据库兼容性**（MySQL JSON 类型）
5. **保持前后端 API 兼容性**

---

## 4. 详细迁移方案

### 4.1 Entity 层迁移

#### 文件: `AiWorkflowNodeEntity.java`

**当前代码**:
```java
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;

@TableField(value = "node_config", typeHandler = JacksonTypeHandler.class)
private ObjectNode nodeConfig;

@TableField(value = "input_config", typeHandler = JacksonTypeHandler.class)
private AiWfNodeInputConfigVo inputConfig;
```

**迁移后代码**:
```java
import com.alibaba.fastjson2.JSONObject;
import com.xinyirun.scm.ai.core.handler.FastjsonTypeHandler;

@TableField(value = "node_config", typeHandler = FastjsonTypeHandler.class)
private JSONObject nodeConfig;

@TableField(value = "input_config", typeHandler = FastjsonInputConfigTypeHandler.class)
private AiWfNodeInputConfigVo inputConfig;
```

**变更说明**:
1. `nodeConfig`: `ObjectNode` → `JSONObject`（灵活的 JSON 配置）
2. `inputConfig`: 保持强类型 Bean，但使用自定义 TypeHandler
3. 移除 Jackson 导入，添加 Fastjson2 导入

---

### 4.2 VO 层迁移

#### 文件: `AiWorkflowRuntimeNodeVo.java`

**当前代码**:
```java
import com.fasterxml.jackson.databind.node.ObjectNode;

private ObjectNode input;
private ObjectNode output;
```

**迁移后代码**:
```java
import com.alibaba.fastjson2.JSONObject;

private JSONObject input;
private JSONObject output;
```

#### 文件: `AiWorkflowRuntimeVo.java`

**当前代码**:
```java
import com.fasterxml.jackson.databind.node.ObjectNode;

private ObjectNode input;
private ObjectNode output;
```

**迁移后代码**:
```java
import com.alibaba.fastjson2.JSONObject;

private JSONObject input;
private JSONObject output;
```

---

### 4.3 Controller 层迁移

#### 文件: `WorkflowController.java`

**当前代码**:
```java
import com.fasterxml.jackson.databind.node.ObjectNode;

public ResponseData<List<AiWorkflowRuntimeVo>> runBatch(
    @RequestBody List<ObjectNode> inputs) {
    // ...
}
```

**迁移后代码**:
```java
import com.alibaba.fastjson2.JSONObject;

public ResponseData<List<AiWorkflowRuntimeVo>> runBatch(
    @RequestBody List<JSONObject> inputs) {
    // ...
}
```

---

### 4.4 Service 层迁移

#### 文件: `AiWorkflowNodeService.java`

**当前代码**:
```java
import com.fasterxml.jackson.databind.node.ObjectNode;

// 创建空 nodeConfig
node.setNodeConfig(JsonUtil.createObjectNode());

// 转换 inputConfig
if (entity.getInputConfig() != null) {
    ObjectNode objectNode = (ObjectNode) JsonUtil.classToJsonNode(entity.getInputConfig());
    vo.setInputConfig(JSONObject.parseObject(objectNode.toString()));
}
```

**迁移后代码**:
```java
import com.alibaba.fastjson2.JSONObject;

// 创建空 nodeConfig
node.setNodeConfig(new JSONObject());

// 转换 inputConfig
if (entity.getInputConfig() != null) {
    vo.setInputConfig(JSONObject.parseObject(
        JSONObject.toJSONString(entity.getInputConfig())
    ));
}
```

#### 文件: `AiWorkflowRuntimeNodeService.java`

**当前代码**:
```java
ObjectNode inputNode = JsonUtil.createObjectNode();
ObjectNode outputNode = JsonUtil.createObjectNode();
```

**迁移后代码**:
```java
JSONObject inputNode = new JSONObject();
JSONObject outputNode = new JSONObject();
```

#### 文件: `AiWorkflowRuntimeService.java`

**当前代码**:
```java
ObjectNode inputNode = com.xinyirun.scm.ai.utils.JsonUtil.createObjectNode();
ObjectNode outputNode = com.xinyirun.scm.ai.utils.JsonUtil.createObjectNode();
```

**迁移后代码**:
```java
JSONObject inputNode = new JSONObject();
JSONObject outputNode = new JSONObject();
```

---

### 4.5 工具类迁移

#### 文件: `JsonUtil.java`

**当前代码**:
```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

private static final ObjectMapper objectMapper = new ObjectMapper();

public static ObjectNode createObjectNode() {
    return objectMapper.createObjectNode();
}
```

**迁移后代码**:
```java
// 移除 createObjectNode() 方法
// 改为直接使用 new JSONObject()

// 如果需要保留兼容性，可以添加：
@Deprecated
public static JSONObject createObjectNode() {
    return new JSONObject();
}
```

---

### 4.6 工作流引擎迁移

#### 文件: `AbstractWfNode.java`

**当前代码**:
```java
import com.fasterxml.jackson.databind.node.ObjectNode;

ObjectNode configObj = node.getNodeConfig();
```

**迁移后代码**:
```java
import com.alibaba.fastjson2.JSONObject;

JSONObject configObj = node.getNodeConfig();
```

#### 文件: `EndNode.java`

**当前代码**:
```java
import com.fasterxml.jackson.databind.node.ObjectNode;

ObjectNode nodeConfigObj = node.getNodeConfig();
```

**迁移后代码**:
```java
import com.alibaba.fastjson2.JSONObject;

JSONObject nodeConfigObj = node.getNodeConfig();
```

#### 其他工作流引擎文件类似迁移

---

## 5. 新增自定义 TypeHandler

### 5.1 创建 `FastjsonTypeHandler`

**文件路径**: `src/main/java/com/xinyirun/scm/ai/core/handler/FastjsonTypeHandler.java`

**用途**: 处理 `JSONObject` 类型字段与 MySQL JSON 类型的转换

**代码**:
```java
package com.xinyirun.scm.ai.core.handler;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Fastjson2 JSONObject TypeHandler
 * 用于处理 MySQL JSON 类型字段
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(JSONObject.class)
public class FastjsonTypeHandler extends BaseTypeHandler<JSONObject> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONObject parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.toJSONString());
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return json == null ? null : JSON.parseObject(json);
    }

    @Override
    public JSONObject getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return json == null ? null : JSON.parseObject(json);
    }

    @Override
    public JSONObject getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return json == null ? null : JSON.parseObject(json);
    }
}
```

### 5.2 创建 `FastjsonInputConfigTypeHandler`

**文件路径**: `src/main/java/com/xinyirun/scm/ai/core/handler/FastjsonInputConfigTypeHandler.java`

**用途**: 专门处理 `AiWfNodeInputConfigVo` 类型字段

**代码**:
```java
package com.xinyirun.scm.ai.core.handler;

import com.alibaba.fastjson2.JSON;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeInputConfigVo;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Fastjson2 InputConfig TypeHandler
 * 专门处理 AiWfNodeInputConfigVo 类型的序列化和反序列化
 */
@MappedJdbcTypes(JdbcType.VARCHAR)
@MappedTypes(AiWfNodeInputConfigVo.class)
public class FastjsonInputConfigTypeHandler extends BaseTypeHandler<AiWfNodeInputConfigVo> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, AiWfNodeInputConfigVo parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public AiWfNodeInputConfigVo getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String json = rs.getString(columnName);
        return json == null ? null : JSON.parseObject(json, AiWfNodeInputConfigVo.class);
    }

    @Override
    public AiWfNodeInputConfigVo getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String json = rs.getString(columnIndex);
        return json == null ? null : JSON.parseObject(json, AiWfNodeInputConfigVo.class);
    }

    @Override
    public AiWfNodeInputConfigVo getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String json = cs.getString(columnIndex);
        return json == null ? null : JSON.parseObject(json, AiWfNodeInputConfigVo.class);
    }
}
```

---

## 6. 迁移步骤

### 第一阶段: 准备工作

1. ✅ 创建新的 TypeHandler 类
   - `FastjsonTypeHandler.java`
   - `FastjsonInputConfigTypeHandler.java`

2. ✅ 验证 Fastjson2 依赖已正确配置
   ```xml
   <dependency>
       <groupId>com.alibaba.fastjson2</groupId>
       <artifactId>fastjson2</artifactId>
   </dependency>
   ```

### 第二阶段: Entity 层迁移

3. ✅ 迁移 `AiWorkflowNodeEntity.java`
   - 替换 `ObjectNode` → `JSONObject`
   - 替换 `JacksonTypeHandler` → `FastjsonTypeHandler` / `FastjsonInputConfigTypeHandler`
   - 移除 Jackson 导入

### 第三阶段: VO 层迁移

4. ✅ 迁移 `AiWorkflowRuntimeNodeVo.java`
5. ✅ 迁移 `AiWorkflowRuntimeVo.java`

### 第四阶段: Service 层迁移

6. ✅ 迁移 `AiWorkflowNodeService.java`
7. ✅ 迁移 `AiWorkflowRuntimeNodeService.java`
8. ✅ 迁移 `AiWorkflowRuntimeService.java`

### 第五阶段: Controller 和工具类迁移

9. ✅ 迁移 `WorkflowController.java`
10. ✅ 迁移 `JsonUtil.java`

### 第六阶段: 工作流引擎迁移

11. ✅ 迁移 `AbstractWfNode.java`
12. ✅ 迁移 `EndNode.java`
13. ✅ 迁移 `SwitcherNode.java`
14. ✅ 迁移 `WfNodeIODataUtil.java`
15. ✅ 迁移 `WorkflowEngine.java`
16. ✅ 迁移 `WorkflowStarter.java`

### 第七阶段: 测试和验证

17. ✅ 单元测试
   - 测试 TypeHandler 序列化和反序列化
   - 测试 Entity 的数据库操作

18. ✅ 集成测试
   - 测试工作流创建
   - 测试工作流查询
   - 测试工作流运行

19. ✅ 前端兼容性测试
   - 验证 Start 节点 user_inputs 显示
   - 验证所有节点配置正常

### 第八阶段: 清理工作

20. ✅ 移除不必要的 Jackson 依赖（如果没有其他模块使用）
21. ✅ 更新项目文档

---

## 7. API 兼容性说明

### 7.1 前端 API 无需变更

**原因**:
- `JSONObject` 序列化后的 JSON 字符串与 `ObjectNode` 完全兼容
- HTTP 响应格式保持不变
- 前端无需任何修改

**示例**:
```json
// ObjectNode 和 JSONObject 序列化后都是：
{
  "user_inputs": [
    {
      "uuid": "xxx",
      "type": 1,
      "name": "var_user_input",
      "title": "用户输入"
    }
  ],
  "ref_inputs": []
}
```

### 7.2 数据库兼容性

**MySQL JSON 类型**:
- 原有数据无需迁移
- JSON 字段读写逻辑保持一致
- TypeHandler 自动处理字符串与对象的转换

---

## 8. 风险评估

### 8.1 低风险项

✅ **VO 层迁移** - 纯粹的数据传输对象，影响小
✅ **工具类迁移** - 方法调用简单替换
✅ **Controller 层迁移** - 参数类型替换，逻辑不变

### 8.2 中风险项

⚠️ **Entity 层迁移** - 需要新的 TypeHandler，需充分测试
⚠️ **Service 层迁移** - 数据转换逻辑变更，需仔细验证

### 8.3 高风险项

🔴 **工作流引擎迁移** - 核心业务逻辑，需全面测试

---

## 9. 回滚方案

如果迁移后出现问题，可以快速回滚：

1. **代码回滚**: Git 回退到迁移前的 commit
2. **数据库无需回滚**: JSON 字段格式兼容
3. **前端无需回滚**: API 响应格式不变

---

## 10. 成功标准

- ✅ 所有单元测试通过
- ✅ 所有集成测试通过
- ✅ 前端工作流功能正常
- ✅ Start 节点 user_inputs 正常显示
- ✅ 工作流创建、查询、运行功能正常
- ✅ 无 Jackson ObjectNode 依赖残留
- ✅ 代码 lint 检查通过
- ✅ 性能无明显下降

---

## 11. 附录

### 11.1 Fastjson2 vs Jackson ObjectNode 对比

| 特性 | Jackson ObjectNode | Fastjson2 JSONObject |
|------|-------------------|---------------------|
| 库依赖 | Jackson Databind | Fastjson2 |
| 性能 | 中等 | 高（国内优化） |
| API 易用性 | 中等 | 高 |
| 社区支持 | 国际主流 | 国内主流 |
| scm 项目统一性 | ❌ 不统一 | ✅ 统一 |

### 11.2 常用 API 对照表

| 操作 | Jackson ObjectNode | Fastjson2 JSONObject |
|------|-------------------|---------------------|
| 创建空对象 | `mapper.createObjectNode()` | `new JSONObject()` |
| 添加字段 | `node.put("key", value)` | `json.put("key", value)` |
| 获取字段 | `node.get("key")` | `json.get("key")` |
| 转JSON字符串 | `node.toString()` | `json.toJSONString()` |
| 解析JSON | `mapper.readTree(json)` | `JSON.parseObject(json)` |

---

**文档版本**: v1.0
**创建时间**: 2025-10-25
**作者**: Claude AI
**审核状态**: 待审核
