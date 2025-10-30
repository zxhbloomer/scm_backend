# Workflow Entity/VO分离修复 - 详细设计

**日期**: 2025-10-29
**目标**: 修复应用启动失败，实现Entity/VO分离架构

---

## 文件修改清单

### 1. AiWorkflowNodeEntity.java
**路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/entity/workflow/AiWorkflowNodeEntity.java`

#### 修改1.1: 删除autoResultMap
**位置**: 第27行
**修改前**:
```java
@TableName(value = "ai_workflow_node", autoResultMap = true)
```
**修改后**:
```java
@TableName(value = "ai_workflow_node")
```

#### 修改1.2: 清理无用import
**位置**: 第3、5行
**修改前**:
```java
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeInputConfigVo;
```
**修改后**:
```java
import com.baomidou.mybatisplus.annotation.*;
```

---

### 2. AiWorkflowRuntimeEntity.java
**路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/entity/workflow/AiWorkflowRuntimeEntity.java`

#### 修改2.1: 删除autoResultMap
**位置**: 第26行
**修改前**:
```java
@TableName(value = "ai_workflow_runtime", autoResultMap = true)
```
**修改后**:
```java
@TableName(value = "ai_workflow_runtime")
```

#### 修改2.2: 清理无用import
**位置**: 第3、5行
**修改前**:
```java
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.ai.config.handler.FastjsonTypeHandler;
```
**修改后**:
```java
import com.baomidou.mybatisplus.annotation.*;
```

---

### 3. AiWorkflowRuntimeNodeEntity.java
**路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/entity/workflow/AiWorkflowRuntimeNodeEntity.java`

#### 修改3.1: 删除autoResultMap
**位置**: 第26行
**修改前**:
```java
@TableName(value = "ai_workflow_runtime_node", autoResultMap = true)
```
**修改后**:
```java
@TableName(value = "ai_workflow_runtime_node")
```

#### 修改3.2: 清理无用import
**位置**: 第3、5行
**修改前**:
```java
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.xinyirun.scm.ai.config.handler.FastjsonTypeHandler;
```
**修改后**:
```java
import com.baomidou.mybatisplus.annotation.*;
```

---

### 4. AiWorkflowRuntimeMapper.java
**路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/mapper/workflow/AiWorkflowRuntimeMapper.java`

#### 修改4.1: 删除未使用的方法
**位置**: 第18-126行
**删除内容**:
```java
    /**
     * 按UUID查询工作流运行时
     */
    @Select("""
        SELECT ...
    """)
    AiWorkflowRuntimeEntity selectByRuntimeUuid(@Param("runtime_uuid") String runtime_uuid);

    /**
     * 查询用户的工作流运行记录
     */
    @Select("""
        SELECT ...
    """)
    List<AiWorkflowRuntimeEntity> selectByUserId(@Param("user_id") Long user_id);

    /**
     * 按工作流UUID查询运行记录
     */
    @Select("""
        SELECT ...
    """)
    List<AiWorkflowRuntimeEntity> selectByWorkflowUuid(@Param("workflow_uuid") String workflow_uuid);
```

**保留内容**:
```java
    /**
     * 更新运行时状态
     */
    @Update("""
        UPDATE ai_workflow_runtime
        SET status = #{status}
        WHERE runtime_uuid = #{runtime_uuid}
    """)
    int updateStatus(@Param("runtime_uuid") String runtime_uuid,
                     @Param("status") Integer status);
```

---

### 5. AiWorkflowNodeMapper.java
**路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/mapper/workflow/AiWorkflowNodeMapper.java`

#### 修改5.1: 修改返回类型（方法1）
**位置**: 第61-62行
**修改前**:
```java
    AiWorkflowNodeEntity selectByWorkflowIdAndUuid(@Param("workflowId") Long workflowId,
                                                     @Param("uuid") String uuid);
```
**修改后**:
```java
    AiWorkflowNodeVo selectByWorkflowIdAndUuid(@Param("workflowId") Long workflowId,
                                                @Param("uuid") String uuid);
```

#### 修改5.2: 修改返回类型（方法2）
**位置**: 第102行
**修改前**:
```java
    List<AiWorkflowNodeEntity> selectByWorkflowId(@Param("workflowId") Long workflowId);
```
**修改后**:
```java
    List<AiWorkflowNodeVo> selectByWorkflowId(@Param("workflowId") Long workflowId);
```

#### 修改5.3: 修改返回类型（方法3）
**位置**: 第143-144行
**修改前**:
```java
    AiWorkflowNodeEntity selectStartNode(@Param("workflowId") Long workflowId,
                                          @Param("componentId") Long componentId);
```
**修改后**:
```java
    AiWorkflowNodeVo selectStartNode(@Param("workflowId") Long workflowId,
                                      @Param("componentId") Long componentId);
```

#### 修改5.4: 修改返回类型（方法4）
**位置**: 第184-185行
**修改前**:
```java
    AiWorkflowNodeEntity selectByWorkflowIdAndUuidIncludeDeleted(@Param("workflowId") Long workflowId,
                                                                   @Param("uuid") String uuid);
```
**修改后**:
```java
    AiWorkflowNodeVo selectByWorkflowIdAndUuidIncludeDeleted(@Param("workflowId") Long workflowId,
                                                              @Param("uuid") String uuid);
```

#### 修改5.5: 添加import
**位置**: 第6行之后
**添加**:
```java
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
```

---

### 6. AiWorkflowNodeService.java
**路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/AiWorkflowNodeService.java`

#### 修改6.1: 修改getStartNode返回类型
**位置**: 第52-55行
**修改前**:
```java
    public AiWorkflowNodeEntity getStartNode(Long workflowId) {
        Long startComponentId = workflowComponentService.getStartComponent().getId();
        return aiWorkflowNodeMapper.selectStartNode(workflowId, startComponentId);
    }
```
**修改后**:
```java
    public AiWorkflowNodeVo getStartNode(Long workflowId) {
        Long startComponentId = workflowComponentService.getStartComponent().getId();
        return aiWorkflowNodeMapper.selectStartNode(workflowId, startComponentId);
    }
```

#### 修改6.2: 修改getByUuid返回类型
**位置**: 第64-66行
**修改前**:
```java
    public AiWorkflowNodeEntity getByUuid(Long workflowId, String uuid) {
        return aiWorkflowNodeMapper.selectByWorkflowIdAndUuid(workflowId, uuid);
    }
```
**修改后**:
```java
    public AiWorkflowNodeVo getByUuid(Long workflowId, String uuid) {
        return aiWorkflowNodeMapper.selectByWorkflowIdAndUuid(workflowId, uuid);
    }
```

#### 修改6.3: 修改listByWorkflowId返回类型
**位置**: 第74-76行
**修改前**:
```java
    public List<AiWorkflowNodeEntity> listByWorkflowId(Long workflowId) {
        return aiWorkflowNodeMapper.selectByWorkflowId(workflowId);
    }
```
**修改后**:
```java
    public List<AiWorkflowNodeVo> listByWorkflowId(Long workflowId) {
        return aiWorkflowNodeMapper.selectByWorkflowId(workflowId);
    }
```

#### 修改6.4: 简化listDtoByWfId方法
**位置**: 第84-93行
**修改前**:
```java
    public List<AiWorkflowNodeVo> listDtoByWfId(Long workflowId) {
        List<AiWorkflowNodeEntity> nodeList = listByWorkflowId(workflowId);
        List<AiWorkflowNodeVo> result = new ArrayList<>();
        for (AiWorkflowNodeEntity entity : nodeList) {
            AiWorkflowNodeVo vo = new AiWorkflowNodeVo();
            BeanUtils.copyProperties(entity, vo);
            result.add(vo);
        }
        return result;
    }
```
**修改后**:
```java
    public List<AiWorkflowNodeVo> listDtoByWfId(Long workflowId) {
        return listByWorkflowId(workflowId);
    }
```

#### 修改6.5: 修改copyNodes方法内部类型
**位置**: 第105行
**修改前**:
```java
        List<AiWorkflowNodeEntity> sourceNodes = self.listByWorkflowId(sourceWorkflowId);
```
**修改后**:
```java
        List<AiWorkflowNodeVo> sourceNodes = self.listByWorkflowId(sourceWorkflowId);
```

#### 修改6.6: 修改copyNodes方法循环体（VO转Entity）
**位置**: 第106行开始的for循环内
**修改前**:
```java
        for (AiWorkflowNodeEntity sourceNode : sourceNodes) {
            AiWorkflowNodeEntity newNode = new AiWorkflowNodeEntity();
            BeanUtils.copyProperties(sourceNode, newNode);
```
**修改后**:
```java
        for (AiWorkflowNodeVo sourceNode : sourceNodes) {
            AiWorkflowNodeEntity newNode = new AiWorkflowNodeEntity();
            BeanUtils.copyProperties(sourceNode, newNode);
            // JSON对象字段需要转String
            if (sourceNode.getInputConfig() != null) {
                newNode.setInputConfig(JSONObject.toJSONString(sourceNode.getInputConfig()));
            }
            if (sourceNode.getNodeConfig() != null) {
                newNode.setNodeConfig(JSONObject.toJSONString(sourceNode.getNodeConfig()));
            }
```

#### 修改6.7: 修改batchSave方法内部类型
**位置**: 第141行
**修改前**:
```java
            AiWorkflowNodeEntity old = self.getByUuid(workflowId, nodeVo.getUuid());
```
**修改后**:
```java
            AiWorkflowNodeVo old = self.getByUuid(workflowId, nodeVo.getUuid());
```

#### 修改6.8: 修改batchSave方法Entity创建
**位置**: 第142行开始
**添加**:
```java
            // VO转Entity
            AiWorkflowNodeEntity entity = new AiWorkflowNodeEntity();
            BeanUtils.copyProperties(nodeVo, entity);
            if (nodeVo.getInputConfig() != null) {
                entity.setInputConfig(JSONObject.toJSONString(nodeVo.getInputConfig()));
            }
            if (nodeVo.getNodeConfig() != null) {
                entity.setNodeConfig(JSONObject.toJSONString(nodeVo.getNodeConfig()));
            }
```

#### 修改6.9: 修改logicalDeleteByUuid方法内部类型
**位置**: 第200行
**修改前**:
```java
            AiWorkflowNodeEntity node = self.getByUuid(workflowId, uuid);
```
**修改后**:
```java
            AiWorkflowNodeVo node = self.getByUuid(workflowId, uuid);
```

---

### 7. WorkflowStarter.java
**路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowStarter.java`

#### 修改7.1: 修改nodes变量类型
**位置**: 第82行
**修改前**:
```java
                List<AiWorkflowNodeEntity> nodes = workflowNodeService.listByWorkflowId(workflow.getId());
```
**修改后**:
```java
                List<AiWorkflowNodeVo> nodes = workflowNodeService.listByWorkflowId(workflow.getId());
```

#### 修改7.2: 添加import
**位置**: import区域
**添加**:
```java
import com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowNodeVo;
```

---

### 8. AiWorkflowRuntimeService.java
**路径**: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/workflow/AiWorkflowRuntimeService.java`

#### 修改8.1: 修复updateInput方法
**位置**: 第90行
**修改前**:
```java
        runtime.setInput(inputNode);
```
**修改后**:
```java
        runtime.setInput(inputNode.toJSONString());
```

#### 修改8.2: 修复updateOutput方法
**查找类似的setOutput调用并修复**
**搜索关键字**: `runtime.setOutput(`
**修改模式**: 添加 `.toJSONString()`

---

## 验证检查点

1. ✅ 编译通过：`mvn clean compile`
2. ✅ 应用启动成功：无autoResultMap错误
3. ✅ WorkflowStarter功能正常
4. ✅ 节点CRUD操作正常
5. ✅ JSON字段读写正确
