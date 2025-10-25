# 工作流缺失功能详细设计

**设计日期**: 2025-10-25
**设计原则**: 严格参考 aideepin，不臆想，基于现有代码事实

---

## 1. 现有代码确认

### 1.1 scm 已实现的节点类型

**位置**: `src/main/java/com/xinyirun/scm/ai/workflow/node/`

| 节点类型 | 类名 | 目录 | 状态 |
|---------|------|------|------|
| Start | StartNode | start/ | ✅ 已实现 |
| End | EndNode | node/ | ✅ 已实现 |
| LLM回答 | LLMAnswerNode | answer/ | ✅ 已实现 |
| 内容分类 | ClassifierNode | classifier/ | ✅ 已实现 |
| 条件分支 | SwitcherNode | switcher/ | ✅ 已实现 |
| 模板 | TemplateNode | template/ | ✅ 已实现 |
| 知识库检索 | KnowledgeRetrievalNode | knowledgeretrieval/ | ✅ 已实现 |
| 人工反馈 | HumanFeedbackNode | humanfeedback/ | ✅ 已实现 |
| HTTP请求 | HttpRequestNode | httprequest/ | ✅ 已实现 |

**scm 未实现的节点**（aideepin 有）:
- Dalle3Node (图片生成)
- TongyiwanxNode (通义万相)
- GoogleNode (Google搜索)
- KeywordExtractorNode (关键词提取)
- FaqExtractorNode (FAQ提取)
- DocumentExtractorNode (文档提取)
- MailSendNode (邮件发送)

### 1.2 scm 的 VO 类设计

**AiWfNodeIOVo**: 扁平结构，使用 type 字段区分类型
- ✅ 已有字段：uuid, type, name, title, required, maxLength
- ❌ 缺少方法：checkValue()

---

## 2. 文件 #1: WfNodeFactory.java

### 2.1 文件信息

**新建文件**: `src/main/java/com/xinyirun/scm/ai/workflow/WfNodeFactory.java`

**参考文件**: `aideepin/adi-common/src/main/java/com/moyz/adi/common/workflow/WfNodeFactory.java`

### 2.2 完整代码

```java
package com.xinyirun.scm.ai.workflow;

import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowComponentEntity;
import com.xinyirun.scm.ai.bean.entity.workflow.AiWorkflowNodeEntity;
import com.xinyirun.scm.ai.workflow.node.AbstractWfNode;
import com.xinyirun.scm.ai.workflow.node.EndNode;
import com.xinyirun.scm.ai.workflow.node.answer.LLMAnswerNode;
import com.xinyirun.scm.ai.workflow.node.classifier.ClassifierNode;
import com.xinyirun.scm.ai.workflow.node.httprequest.HttpRequestNode;
import com.xinyirun.scm.ai.workflow.node.humanfeedback.HumanFeedbackNode;
import com.xinyirun.scm.ai.workflow.node.knowledgeretrieval.KnowledgeRetrievalNode;
import com.xinyirun.scm.ai.workflow.node.start.StartNode;
import com.xinyirun.scm.ai.workflow.node.switcher.SwitcherNode;
import com.xinyirun.scm.ai.workflow.node.template.TemplateNode;

/**
 * 工作流节点工厂类
 * 参考 aideepin: com.moyz.adi.common.workflow.WfNodeFactory
 *
 * 功能：根据组件类型创建对应的节点实例
 *
 * @author SCM-AI团队
 * @since 2025-10-25
 */
public class WfNodeFactory {

    /**
     * 根据组件类型创建对应的节点实例
     * 参考 aideepin: WfNodeFactory.create() 第24-78行
     *
     * @param wfComponent 组件定义
     * @param nodeDefinition 节点定义
     * @param wfState 工作流状态
     * @param nodeState 节点状态
     * @return 节点实例
     */
    public static AbstractWfNode create(AiWorkflowComponentEntity wfComponent,
                                       AiWorkflowNodeEntity nodeDefinition,
                                       WfState wfState,
                                       WfNodeState nodeState) {
        AbstractWfNode wfNode = null;
        String componentName = wfComponent.getName();

        // 参考 aideepin 第26-74行的 switch 逻辑
        if ("Start".equals(componentName)) {
            wfNode = new StartNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("Answer".equals(componentName)) {
            wfNode = new LLMAnswerNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("Classifier".equals(componentName)) {
            wfNode = new ClassifierNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("Switcher".equals(componentName)) {
            wfNode = new SwitcherNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("Template".equals(componentName)) {
            wfNode = new TemplateNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("KnowledgeRetrieval".equals(componentName)) {
            wfNode = new KnowledgeRetrievalNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("HumanFeedback".equals(componentName)) {
            wfNode = new HumanFeedbackNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("HttpRequest".equals(componentName)) {
            wfNode = new HttpRequestNode(wfComponent, nodeDefinition, wfState, nodeState);
        } else if ("End".equals(componentName)) {
            wfNode = new EndNode(wfComponent, nodeDefinition, wfState, nodeState);
        }

        return wfNode;
    }
}
```

### 2.3 与 aideepin 的差异对照

| 项目 | aideepin | scm | 说明 |
|------|---------|-----|------|
| 类名 | WfNodeFactory | WfNodeFactory | ✅ 相同 |
| 方法名 | create() | create() | ✅ 相同 |
| 参数1 | WorkflowComponent | AiWorkflowComponentEntity | dto→vo |
| 参数2 | WorkflowNode | AiWorkflowNodeEntity | dto→vo |
| 参数3 | WfState | WfState | ✅ 相同 |
| 参数4 | WfNodeState | WfNodeState | ✅ 相同 |
| 返回值 | AbstractWfNode | AbstractWfNode | ✅ 相同 |
| 节点数量 | 16个 | 9个 | scm 只实现已有的 |

---

## 3. 文件 #2: AiWfNodeIOVo.java - 补充 checkValue() 方法

### 3.1 文件信息

**修改文件**: `src/main/java/com/xinyirun/scm/ai/bean/vo/workflow/AiWfNodeIOVo.java`

**参考文件**:
- `aideepin/WfNodeIO.java` - 基类（第36行定义 checkValue()）
- `aideepin/WfNodeIOText.java` - 文本验证（第28-41行）
- `aideepin/WfNodeIONumber.java` - 数字验证（第24-29行）
- `aideepin/WfNodeIOBool.java` - 布尔验证（第25-30行）
- `aideepin/WfNodeIOFiles.java` - 文件验证（第26-31行）
- `aideepin/WfNodeIOOptions.java` - 选项验证（第27-37行）

### 3.2 需要导入的类

```java
import com.xinyirun.scm.ai.workflow.data.NodeIOData;
import com.xinyirun.scm.ai.workflow.data.NodeIODataTextContent;
import com.xinyirun.scm.ai.workflow.data.NodeIODataNumberContent;
import com.xinyirun.scm.ai.workflow.data.NodeIODataBoolContent;
import com.xinyirun.scm.ai.workflow.data.NodeIODataFilesContent;
import com.xinyirun.scm.ai.workflow.data.NodeIODataOptionsContent;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Map;
```

### 3.3 补充的方法代码

**插入位置**: 在 class 最后，第51行之前

```java
    /**
     * 检查数据是否合规
     * 参考 aideepin: WfNodeIO.checkValue() 及其5个子类实现
     *
     * 由于 scm 使用扁平结构（单个VO类 + type字段），这里根据 type 字段分发到不同验证逻辑
     *
     * @param data 节点输入输出数据
     * @return 是否正确
     */
    public boolean checkValue(NodeIOData data) {
        if (data == null || data.getContent() == null) {
            return false;
        }

        // 根据 type 字段分发验证逻辑
        if (type == 1) {
            // TEXT 类型验证 - 参考 WfNodeIOText.checkValue() 第29-40行
            if (!(data.getContent() instanceof NodeIODataTextContent textContent)) {
                return false;
            }
            String value = textContent.getValue();
            if (required && value == null) {
                return false;
            }
            if (maxLength != null && value != null && value.length() > maxLength) {
                return false;
            }
            return true;

        } else if (type == 2) {
            // NUMBER 类型验证 - 参考 WfNodeIONumber.checkValue() 第24-29行
            if (!(data.getContent() instanceof NodeIODataNumberContent numberContent)) {
                return false;
            }
            return !required || numberContent.getValue() != null;

        } else if (type == 5) {
            // BOOL 类型验证 - 参考 WfNodeIOBool.checkValue() 第25-30行
            if (!(data.getContent() instanceof NodeIODataBoolContent)) {
                return false;
            }
            return !required || data.getContent().getValue() != null;

        } else if (type == 4) {
            // FILES 类型验证 - 参考 WfNodeIOFiles.checkValue() 第26-31行
            if (!(data.getContent() instanceof NodeIODataFilesContent filesContent)) {
                return false;
            }
            return !required || !CollectionUtils.isEmpty(filesContent.getValue());

        } else if (type == 3) {
            // OPTIONS 类型验证 - 参考 WfNodeIOOptions.checkValue() 第27-37行
            if (!(data.getContent() instanceof NodeIODataOptionsContent optionsContent)) {
                return false;
            }
            Map<String, Object> value = optionsContent.getValue();
            if (required && value == null) {
                return false;
            }
            // 注意：scm 的 AiWfNodeIOVo 没有 multiple 字段，默认允许多选
            // 如需限制，可后续添加 multiple 字段
            return true;

        } else {
            // 未知类型
            return false;
        }
    }
```

### 3.4 需要补充的字段（可选）

如果需要完全参考 aideepin 的 OPTIONS 验证逻辑（单选/多选限制）：

```java
/**
 * 是否允许多选（OPTIONS 类型使用）
 */
private Boolean multiple;
```

**说明**: aideepin 的 WfNodeIOOptions 有 `multiple` 字段用于限制单选/多选。scm 当前没有此字段，可以后续补充。

---

## 4. 文件 #3: AiWorkflowRuntimeNodeService.java - 补充 createByState() 方法

### 4.1 文件信息

**修改文件**: `src/main/java/com/xinyirun/scm/ai/core/service/workflow/AiWorkflowRuntimeNodeService.java`

**参考文件**: `aideepin/WorkflowRuntimeNodeService.java:45-59`

### 4.2 当前已有的方法

**create() 方法**（第66-82行）:
```java
public AiWorkflowRuntimeNodeVo create(Long nodeId, Long wfRuntimeId,
                                       String nodeUuid, Integer status) {
    AiWorkflowRuntimeNodeEntity runtimeNode = new AiWorkflowRuntimeNodeEntity();
    runtimeNode.setRuntimeNodeUuid(UuidUtil.createShort());
    runtimeNode.setWorkflowRuntimeId(wfRuntimeId);
    runtimeNode.setNodeId(nodeId);
    runtimeNode.setNodeUuid(nodeUuid);
    runtimeNode.setStatus(status != null ? status : 1);
    baseMapper.insert(runtimeNode);

    runtimeNode = baseMapper.selectById(runtimeNode.getId());

    AiWorkflowRuntimeNodeVo vo = changeNodeToDTO(runtimeNode);
    fillInputOutput(vo);
    return vo;
}
```

### 4.3 需要补充的 createByState() 方法

**插入位置**: 在 create() 方法之后（第82行之后）

**完整代码**:
```java
    /**
     * 根据节点状态创建运行时节点记录
     * 参考 aideepin: WorkflowRuntimeNodeService.createByState() 第45-59行
     *
     * @param userId 用户ID
     * @param wfNodeId 节点ID
     * @param wfRuntimeId 运行实例ID
     * @param state 节点状态
     * @return 节点执行记录VO
     */
    public AiWorkflowRuntimeNodeVo createByState(Long userId, Long wfNodeId,
                                                  Long wfRuntimeId, WfNodeState state) {
        // 参考 aideepin:46-52
        AiWorkflowRuntimeNodeEntity runtimeNode = new AiWorkflowRuntimeNodeEntity();
        runtimeNode.setRuntimeNodeUuid(state.getUuid());
        runtimeNode.setWorkflowRuntimeId(wfRuntimeId);
        runtimeNode.setNodeId(wfNodeId);
        runtimeNode.setNodeUuid(state.getNodeUuid());
        runtimeNode.setStatus(state.getProcessStatus());
        runtimeNode.setIsDeleted(false);
        // 不设置 c_time, u_time, c_id, u_id, dbversion - 自动填充
        baseMapper.insert(runtimeNode);

        // 参考 aideepin:53 - 重新查询获取完整数据
        runtimeNode = baseMapper.selectById(runtimeNode.getId());

        // 参考 aideepin:55-58 - 转换为 VO
        AiWorkflowRuntimeNodeVo vo = changeNodeToDTO(runtimeNode);
        fillInputOutput(vo);
        return vo;
    }
```

### 4.4 需要导入的类

```java
import com.xinyirun.scm.ai.workflow.WfNodeState;
```

### 4.5 与 aideepin 的差异对照

| 项目 | aideepin | scm | 说明 |
|------|---------|-----|------|
| 方法名 | createByState | createByState | ✅ 相同 |
| 参数1 | User user | Long userId | scm 只传 userId |
| 参数2 | long wfNodeId | Long wfNodeId | ✅ 相同 |
| 参数3 | long wfRuntimeId | Long wfRuntimeId | ✅ 相同 |
| 参数4 | WfNodeState state | WfNodeState state | ✅ 相同 |
| 返回值 | WfRuntimeNodeDto | AiWorkflowRuntimeNodeVo | dto→vo |
| uuid来源 | state.getUuid() | state.getUuid() | ✅ 相同 |
| status来源 | state.getProcessStatus() | state.getProcessStatus() | ✅ 相同 |

**关键差异**:
- aideepin: `setUserId(user.getId())`
- scm: 字段自动填充（c_id），不手动设置

---

## 5. 文件 #4: WorkflowEngine.java - runNode() 方法实现

### 5.1 文件信息

**修改文件**: `src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java`

**参考文件**: `aideepin/WorkflowEngine.java:178-220`

**当前代码**（第179-194行）: TODO 占位符

### 5.2 需要添加的成员变量

检查 WorkflowEngine 的成员变量：

**参考 aideepin:36-51** 需要确认 scm 是否有：
- ✅ `workflow` - 已有
- ✅ `components` - 已有
- ✅ `wfNodes` - 已有
- ✅ `wfEdges` - 已有
- ✅ `workflowRuntimeService` - 已有
- ✅ `workflowRuntimeNodeService` - 已有
- ✅ `sseEmitter` - 已有
- ✅ `userId` - 已有
- ✅ `wfState` - 已有
- ✅ `wfRuntimeResp` - 已有

**结论**: ✅ scm 已有所有必要的成员变量

### 5.3 完整代码

**替换位置**: 第179-194行

```java
    /**
     * 执行单个节点
     * 参考 aideepin: WorkflowEngine.runNode() 第178-220行
     *
     * @param wfNode 节点定义
     * @param nodeState 节点状态
     * @return 执行结果Map
     */
    private Map<String, Object> runNode(AiWorkflowNodeEntity wfNode, WfNodeState nodeState) {
        Map<String, Object> resultMap = new HashMap<>();
        try {
            // 1. 找到对应的组件（参考 aideepin:181）
            AiWorkflowComponentEntity wfComponent = components.stream()
                    .filter(item -> item.getId().equals(wfNode.getWorkflowComponentId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("组件不存在"));

            // 2. 通过工厂创建节点实例（参考 aideepin:182）
            AbstractWfNode abstractWfNode = WfNodeFactory.create(wfComponent, wfNode, wfState, nodeState);

            // 3. 创建运行时节点记录（参考 aideepin:184）
            AiWorkflowRuntimeNodeVo runtimeNodeVo = workflowRuntimeNodeService.createByState(
                    userId, wfNode.getId(), wfRuntimeResp.getId(), nodeState);
            wfState.getRuntimeNodes().add(runtimeNodeVo);

            // 4. 发送节点运行开始消息（参考 aideepin:187）
            sendSseMessage("[NODE_RUN_" + wfNode.getUuid() + "]",
                    com.alibaba.fastjson2.JSONObject.toJSONString(runtimeNodeVo));

            // 5. 执行节点，带输入输出回调（参考 aideepin:189-204）
            NodeProcessResult processResult = abstractWfNode.process(
                    // 输入回调（参考 aideepin:189-193）
                    (is) -> {
                        workflowRuntimeNodeService.updateInput(runtimeNodeVo.getId(), nodeState);
                        for (NodeIOData input : nodeState.getInputs()) {
                            sendSseMessage("[NODE_INPUT_" + wfNode.getUuid() + "]",
                                    com.alibaba.fastjson2.JSONObject.toJSONString(input));
                        }
                    },
                    // 输出回调（参考 aideepin:194-203）
                    (is) -> {
                        workflowRuntimeNodeService.updateOutput(runtimeNodeVo.getId(), nodeState);

                        // 并行节点内部的节点执行结束后，需要主动向客户端发送输出结果
                        String nodeUuid = wfNode.getUuid();
                        List<NodeIOData> nodeOutputs = nodeState.getOutputs();
                        for (NodeIOData output : nodeOutputs) {
                            log.info("callback node:{},output:{}", nodeUuid, output.getContent());
                            sendSseMessage("[NODE_OUTPUT_" + nodeUuid + "]",
                                    com.alibaba.fastjson2.JSONObject.toJSONString(output));
                        }
                    }
            );

            // 6. 设置下一个节点（如果有）（参考 aideepin:205-207）
            if (StringUtils.isNotBlank(processResult.getNextNodeUuid())) {
                resultMap.put("next", processResult.getNextNodeUuid());
            }

        } catch (Exception e) {
            log.error("Node run error", e);
            throw new RuntimeException(e);
        }

        // 7. 设置节点名称（参考 aideepin:212）
        resultMap.put("name", wfNode.getTitle());

        // 8. 处理流式生成器（参考 aideepin:213-218）
        // langgraph4j state 中的 data 不做数据存储，只存储元数据
        StreamingChatGenerator<AgentState> generator = wfState.getNodeToStreamingGenerator().get(wfNode.getUuid());
        if (null != generator) {
            resultMap.put("_streaming_messages", generator);
            return resultMap;
        }

        return resultMap;
    }
```

### 5.4 需要确认导入的类

检查是否已导入：
- ✅ `com.alibaba.fastjson2.JSONObject` - 已导入
- ✅ `org.apache.commons.lang3.StringUtils` - 已导入
- ✅ `NodeIOData` - 已导入
- ❓ `StreamingChatGenerator` - 需确认
- ❓ `AgentState` - 需确认

---

## 6. 文件 #5: WorkflowEngine.java - streamingResult() 方法完善

### 6.1 当前代码（第203-216行）

```java
private void streamingResult(WfState wfState, AsyncGenerator<NodeOutput<WfNodeState>> outputs, SseEmitter sseEmitter) {
    for (NodeOutput<WfNodeState> out : outputs) {
        if (out instanceof StreamingOutput<WfNodeState> streamingOutput) {
            String node = streamingOutput.node();
            String chunk = streamingOutput.chunk();
            log.info("node:{},chunk:{}", node, chunk);
            sendSseMessage("[NODE_CHUNK_" + node + "]", chunk);
        } else {
            // TODO: 处理节点输出更新
            log.info("Node output: {}", out.node());
        }
    }
}
```

### 6.2 补充 else 分支代码

**参考**: `aideepin:236-248`

**替换 else 分支内容**:
```java
        } else {
            // 找到对应的 abstractWfNode（参考 aideepin:236-237）
            AbstractWfNode abstractWfNode = wfState.getCompletedNodes().stream()
                    .filter(item -> item.getNode().getUuid().endsWith(out.node()))
                    .findFirst()
                    .orElse(null);

            if (null != abstractWfNode) {
                // 找到对应的运行时节点（参考 aideepin:238-239）
                AiWorkflowRuntimeNodeVo runtimeNodeVo = wfState.getRuntimeNodeByNodeUuid(out.node());
                if (null != runtimeNodeVo) {
                    // 更新运行时节点的输出（参考 aideepin:240-241）
                    workflowRuntimeNodeService.updateOutput(runtimeNodeVo.getId(), abstractWfNode.getState());
                    wfState.setOutput(abstractWfNode.getState().getOutputs());
                } else {
                    log.warn("Can not find runtime node, node uuid:{}", out.node());
                }
            } else {
                log.warn("Can not find node state,node uuid:{}", out.node());
            }
        }
```

### 6.3 需要导入的类

检查是否已导入：
- ✅ `AbstractWfNode` - 需确认
- ✅ `AiWorkflowRuntimeNodeVo` - 需确认

---

## 7. 文件 #6: WorkflowEngine.java - getAndCheckUserInput() 方法完善

### 7.1 当前代码（第225-241行）

```java
private List<NodeIOData> getAndCheckUserInput(List<JSONObject> userInputs, AiWorkflowNodeEntity startNode) {
    // TODO: 实现用户输入校验
    List<NodeIOData> wfInputs = new ArrayList<>();
    // 临时实现:直接创建一个默认输入
    if (!userInputs.isEmpty()) {
        JSONObject firstInput = userInputs.get(0);
        if (firstInput.containsKey("name") && firstInput.containsKey("value")) {
            String name = firstInput.getString("name");
            String value = firstInput.getString("value");
            wfInputs.add(NodeIOData.createByText(name, name, value));
        }
    }
    return wfInputs;
}
```

### 7.2 完整实现代码

**参考**: `aideepin:260-287`

**替换整个方法**:
```java
    /**
     * 校验用户输入并组装成工作流的输入
     * 参考 aideepin: WorkflowEngine.getAndCheckUserInput() 第260-287行
     *
     * @param userInputs 用户输入
     * @param startNode  开始节点定义
     * @return 正确的用户输入列表
     */
    private List<NodeIOData> getAndCheckUserInput(List<JSONObject> userInputs, AiWorkflowNodeEntity startNode) {
        // 参考 aideepin:260 - 获取 Start 节点的输入定义列表
        List<AiWfNodeIOVo> defList = startNode.getInputConfig().getUserInputs();
        List<NodeIOData> wfInputs = new ArrayList<>();

        // 参考 aideepin:262-286 - 遍历每个输入定义，验证用户输入
        for (AiWfNodeIOVo paramDefinition : defList) {
            String paramNameFromDef = paramDefinition.getName();
            boolean requiredParamMissing = paramDefinition.getRequired();

            for (JSONObject userInput : userInputs) {
                // 参考 aideepin:266 - 转换用户输入为 NodeIOData
                NodeIOData nodeIOData = WfNodeIODataUtil.createNodeIOData(userInput);
                if (!paramNameFromDef.equalsIgnoreCase(nodeIOData.getName())) {
                    continue;
                }

                // 参考 aideepin:270-273 - 检查数据类型
                Integer dataType = nodeIOData.getContent().getType();
                if (null == dataType) {
                    throw new RuntimeException("用户输入数据类型无效");
                }

                requiredParamMissing = false;

                // 参考 aideepin:275-278 - 调用 checkValue 验证
                boolean valid = paramDefinition.checkValue(nodeIOData);
                if (!valid) {
                    log.error("用户输入无效,workflowId:{}", startNode.getWorkflowId());
                    throw new RuntimeException("用户输入无效");
                }

                wfInputs.add(nodeIOData);
            }

            // 参考 aideepin:282-285 - 检查必填参数是否缺失
            if (requiredParamMissing) {
                log.error("在流程定义中必填的参数没有传进来,name:{}", paramNameFromDef);
                throw new RuntimeException("必填参数缺失: " + paramNameFromDef);
            }
        }

        return wfInputs;
    }
```

### 7.3 需要导入的类

```java
import com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeIOVo;
```

---

## 8. 导入检查清单

### 8.1 WorkflowEngine.java 需要确认的导入

让我检查 WorkflowEngine.java 当前的导入：

**待确认**:
- `com.alibaba.fastjson2.JSONObject`
- `com.xinyirun.scm.ai.bean.vo.workflow.AiWfNodeIOVo`
- `com.xinyirun.scm.ai.bean.vo.workflow.AiWorkflowRuntimeNodeVo`
- `com.xinyirun.scm.ai.workflow.data.NodeIOData`
- `org.apache.commons.lang3.StringUtils`
- `org.bsc.langgraph4j.langchain4j.generators.StreamingChatGenerator`
- `org.bsc.langgraph4j.state.AgentState`

---

## 9. 实施步骤

### 第一步: 创建 WfNodeFactory.java

- 新建文件
- 实现 create() 方法
- 只处理 scm 已实现的 9 个节点类型

### 第二步: 补充 AiWfNodeIOVo.checkValue()

- 添加 checkValue() 方法
- 根据 type 字段分发到 5 种类型验证逻辑
- 添加必要的 import

### 第三步: 补充 AiWorkflowRuntimeNodeService.createByState()

- 在 create() 方法后添加
- 实现节点状态创建逻辑
- 添加必要的 import

### 第四步: 完善 WorkflowEngine.runNode()

- 替换 TODO 代码
- 实现 8 个步骤
- 添加必要的 import

### 第五步: 完善 WorkflowEngine.streamingResult()

- 补充 else 分支
- 实现节点输出更新
- 无需新增 import

### 第六步: 完善 WorkflowEngine.getAndCheckUserInput()

- 替换临时实现
- 实现完整验证逻辑
- 添加必要的 import

---

## 10. 风险评估

### 低风险 ✅

- WfNodeFactory 创建（新文件，无影响）
- AiWfNodeIOVo.checkValue() 添加（新方法，无影响）
- AiWorkflowRuntimeNodeService.createByState() 添加（新方法，无影响）

### 中风险 ⚠️

- WorkflowEngine.runNode() 实现（替换 TODO，需要测试）
- WorkflowEngine.getAndCheckUserInput() 实现（替换临时代码，需要测试）

---

**设计完成时间**: 2025-10-25
**设计人**: Claude AI
**下一步**: 请确认设计，我将开始实施
