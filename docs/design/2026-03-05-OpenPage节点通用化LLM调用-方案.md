# OpenPage节点通用化LLM调用方案

## 1. 问题背景

`OpenPageNode` 当前实现硬编码搜索含 `ai_new_route` 字符串的输入，既不通用，也没有使用工作流编辑器中配置的 prompt 和 model_name。

**根本问题**：OpenPage 节点的 `node_config` 中有 `prompt` 和 `model_name` 配置，但代码完全忽略它们。用户无法通过编辑器自定义节点行为。

## 2. 完整调用链路

```
工作流编辑器配置 node_config.prompt / model_name
                    ↓
OpenPageNode.onProcess()
    ├─ checkAndGetConfig(OpenPageNodeConfig.class)   读取配置
    ├─ renderTemplate(prompt, state.getInputs())     渲染变量
    ├─ WorkflowUtil.invokeLLM(wfState, model, prompt) 静默调LLM
    └─ wfState.setAi_open_dialog_para(output)       存侧通道
                    ↓
WorkflowEngine.doOnComplete()
    └─ 检查 wfState.getAi_open_dialog_para() != null
       → sink.tryEmitNext(createAiOpenDialogParaEvent())
                    ↓
WorkflowRoutingService.executeWorkflowByUuid()
    └─ 拦截 workflow_output_data 事件
       → capturedAiOpenDialogPara[0] = data
       → completeResponse.setAi_open_dialog_para(...)
                    ↓
前端 complete 事件
    └─ ai_open_dialog_para 不为空 → 打开业务弹窗 + 渲染"打开页面"按钮
```

**说明**：`invokeLLM()` 是非流式调用，不会向 SSE 推送 chunk 事件，生成的 JSON 不会出现在聊天窗口。

## 3. 问题诊断

| 问题 | 当前 | 期望 |
|---|---|---|
| 数据来源 | 硬编码搜索 `ai_new_route` | 通过 LLM + 用户配置的 prompt 生成 |
| prompt 使用 | 完全忽略 | `renderTemplate()` 渲染变量后调 LLM |
| 通用性 | 与特定 workflow 强耦合 | 任意 workflow 可复用 |
| 多节点支持 | 不支持 | 每个 OpenPage 节点独立配置 |

## 4. 按文件设计

### 4.1 修改：OpenPageNode.java

**路径**：`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/openpage/OpenPageNode.java`

**改动说明**：
- 删除 `findOpenPageData()` 方法（硬编码特殊情况）
- 修改 `onProcess()`：读配置 → 渲染变量 → 调 LLM → 存侧通道

**改动前（当前代码）**：
```java
@Override
public NodeProcessResult onProcess() {
    String inputText = findOpenPageData();
    log.info("OpenPage节点处理，输入长度: {}", inputText != null ? inputText.length() : 0);
    wfState.setAi_open_dialog_para(inputText);
    return new NodeProcessResult();
}

private String findOpenPageData() {
    for (NodeIOData input : state.getInputs()) {
        String text = input.valueToString();
        if (text != null && text.contains("ai_new_route")) {
            log.info("OpenPage找到业务JSON数据，来源变量: {}", input.getName());
            return text;
        }
    }
    log.warn("OpenPage未找到含ai_new_route的输入，回退使用默认输入");
    return getFirstInputText();
}
```

**改动后（目标代码）**：
```java
@Override
public NodeProcessResult onProcess() {
    OpenPageNodeConfig nodeConfig = checkAndGetConfig(OpenPageNodeConfig.class);
    String prompt = WorkflowUtil.renderTemplate(nodeConfig.getPrompt(), state.getInputs());
    log.info("OpenPage节点开始调用LLM，prompt长度: {}", prompt != null ? prompt.length() : 0);

    NodeIOData output = WorkflowUtil.invokeLLM(wfState, nodeConfig.getModelName(), prompt);
    state.getOutputs().add(output);

    String result = output.valueToString();
    wfState.setAi_open_dialog_para(result);
    log.info("OpenPage节点完成，LLM输出长度: {}", result != null ? result.length() : 0);

    return new NodeProcessResult();
}
```

**删除**：整个 `findOpenPageData()` 方法。

### 4.2 无需修改的文件（已验证）

| 文件 | 原因 |
|---|---|
| `OpenPageNodeConfig.java` | 已有 `prompt` 和 `modelName` 字段 |
| `WorkflowUtil.java` | `invokeLLM()` 和 `renderTemplate()` 已有 |
| `WfState.java` | `ai_open_dialog_para` 字段已有 |
| `WorkflowEngine.java` | `doOnComplete` 侧通道逻辑已有 |
| `WorkflowRoutingService.java` | 事件拦截逻辑已有 |
| `ChatResponseVo.java` | `ai_open_dialog_para` 字段已有 |

## 5. KISS原则7问回答

1. **真问题？** 是。OpenPage 的 prompt 完全不生效，用户配置无意义，且与特定 workflow 强耦合
2. **更简方法？** 已是最简——只改一个文件，30行代码，复用4个已有方法
3. **会破坏什么？** 无破坏。侧通道链路不变，前端对接不变，无 OpenPage 节点的 workflow 不受影响
4. **真需要？** 是。用户已明确要求通用化
5. **过度设计？** 无。去掉1个方法，改3行逻辑，极简
6. **话题模糊？** 不模糊。代码已全部读过，方法已确认存在
7. **学习注意事项？** 已使用 `checkAndGetConfig`（复用模式）、`renderTemplate`（复用）、`invokeLLM`（复用），无 Map<String, Object>，无重复实现

## 6. 文件变更清单

| 文件 | 操作 | 改动量 |
|---|---|---|
| `.../workflow/node/openpage/OpenPageNode.java` | 修改 | 删除15行，新增10行 |

## 7. 向后兼容分析

- 无 OpenPage 节点的 workflow → 不受任何影响
- 有 OpenPage 节点但 prompt 为空的 workflow → `renderTemplate` 返回空字符串，`invokeLLM` 调用 LLM，LLM 返回空或错误，`ai_open_dialog_para` 为空或报错，行为比当前更明确（不是静默失败）
- 现有侧通道链路完全不变

## 8. 风险分析

| 风险 | 等级 | 说明 |
|---|---|---|
| LLM 返回非 JSON | 低 | 前端解析失败时静默忽略（已有兜底） |
| prompt 模板变量未定义 | 低 | `renderTemplate` 找不到变量时保留原始占位符 |
| LLM 调用超时 | 低 | `invokeLLM` 有 `aiChatBaseService.chat()` 的超时机制 |
