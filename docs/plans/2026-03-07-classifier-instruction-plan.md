# 内容归类节点加分类指令 实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 在"内容归类"节点加入可选的分类指令（instruction）字段，注入到 LLM 提示词中提升分类准确率。

**Architecture:** 后端 ClassifierNodeConfig 加 instruction 字段，ClassifierPrompt 注入到提示词 classification_instructions，ClassifierNode 调用时传入；前端属性面板加 textarea，画布节点不变。数据库零变更，instruction 随 node_config JSON 整体存储。

**Tech Stack:** Java 17 + Lombok、Vue 2.7 + Element UI、Fastjson2

---

### Task 1: 后端 — ClassifierNodeConfig.java 加 instruction 字段

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/classifier/ClassifierNodeConfig.java`

**Step 1: 在 modelName 字段下方加 instruction 字段**

```java
/**
 * 分类指令（可选），用于补充说明判断逻辑，提升分类准确率
 * 对应 Dify 的 instruction 字段
 */
private String instruction;
```

完整文件：
```java
@Data
public class ClassifierNodeConfig {

    private List<ClassifierCategory> categories = new ArrayList<>();

    @JsonProperty("model_name")
    private String modelName;

    /**
     * 分类指令（可选），用于补充说明判断逻辑，提升分类准确率
     */
    private String instruction;
}
```

**Step 2: 确认向后兼容**

老数据 JSON 中没有 instruction 字段，反序列化后为 null，后续代码用 `instruction == null ? "" : instruction` 处理，完全兼容。

---

### Task 2: 后端 — ClassifierPrompt.java 注入 instruction

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/classifier/ClassifierPrompt.java`

**Step 1: 修改方法签名，加 instruction 参数**

```java
public static String createPrompt(String input, List<ClassifierCategory> categories, String instruction) {
```

**Step 2: 修改 User Input 模板，注入 classification_instructions**

原来（第47行）：
```
{"input_text" : ["%s"], "categories" : %s}
```

改为：
```
{"input_text" : ["%s"], "categories" : %s, "classification_instructions" : ["%s"]}
```

对应 `.formatted()` 调用改为：
```java
.formatted(input, JsonUtil.toJson(promptCategories), instruction == null ? "" : instruction);
```

---

### Task 3: 后端 — ClassifierNode.java 更新调用

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/classifier/ClassifierNode.java`

**Step 1: 修改 createPrompt 调用，传入 instruction（第69行附近）**

原来：
```java
String prompt = ClassifierPrompt.createPrompt(defaultInputOpt.get().valueToString(), nodeConfig.getCategories());
```

改为：
```java
String prompt = ClassifierPrompt.createPrompt(defaultInputOpt.get().valueToString(), nodeConfig.getCategories(), nodeConfig.getInstruction());
```

**Step 2: 确认其他逻辑不需要改动**

instruction 只影响提示词生成，不影响分类结果解析和路由逻辑。

---

### Task 4: 前端 — ClassifierNodeProperty.vue 加分类指令输入框

**Files:**
- Modify: `src/components/70_ai/components/workflow/components/properties/ClassifierNodeProperty.vue`

**Step 1: 在模型选择 section 下方、类别列表 section 上方加 instruction section**

在第11行（`<!-- 类别列表 -->`）前插入：

```html
<!-- 分类指令（可选） -->
<div class="property-section">
  <div class="section-title">
    分类指令
    <el-tooltip content="可选。补充说明分类判断逻辑，LLM 会参考此指令提升分类准确率" placement="top">
      <i class="el-icon-question" style="color: #909399; font-size: 14px; margin-left: 4px;" />
    </el-tooltip>
  </div>
  <el-input
    v-model="nodeConfig.instruction"
    type="textarea"
    :autosize="{ minRows: 2, maxRows: 5 }"
    placeholder="例如：根据上游输出的 pagecode 字段判断路由数量"
  />
</div>
```

**Step 2: 确认 nodeConfig computed 初始化**

在 `nodeConfig` computed 中加 instruction 初始化（第106行附近）：

```js
if (nodeConfig.instruction === undefined) {
  this.$set(this.wfNode.nodeConfig, 'instruction', '')
}
```

**Step 3: 画布节点 ClassifierNode.vue 不需要改动**

instruction 是内部配置，不在画布上显示。

---

### Task 5: 验证

**Step 1: 手工测试流程**

1. 打开工作流设计器，拖入"内容归类"节点
2. 属性面板应显示"分类指令"输入框（在模型选择下方）
3. 填写分类指令，保存工作流
4. 刷新页面重新打开，确认指令已持久化
5. 运行工作流，观察 LLM 是否参考了指令进行分类

**Step 2: 向后兼容验证**

打开老的工作流（没有 instruction 字段），确认：
- 属性面板正常显示，instruction 输入框为空
- 运行工作流不报错，分类行为与之前一致
