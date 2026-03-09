# 人机交互节点实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 扩展工作流人机交互节点，支持 text/confirm/select/form 四种交互类型，打通前后端闭环。

**Architecture:** 后端扩展 HumanFeedbackNodeConfig 和 handleInterruption()，创建DB交互记录并通过SSE发送完整交互配置给前端。前端编辑器面板支持4种类型配置，运行时根据类型渲染已有的AiUserConfirm/Select/Form组件。超时通过定时任务扫描处理。

**Tech Stack:** Spring Boot 3.1.4, MyBatis Plus, Vue.js 2.7.16, Element UI, SSE

**设计文档:** `docs/plans/2026-03-06-human-interaction-node-design.md`

---

## Task 1: 创建DB表

**Files:**
- Create: `temp_create_interaction_table.sql`

**Step 1: 编写DDL**

```sql
CREATE TABLE IF NOT EXISTS `ai_workflow_interaction` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `interaction_uuid` VARCHAR(64) NOT NULL COMMENT '交互UUID(业务主键)',
  `conversation_id` VARCHAR(64) DEFAULT NULL COMMENT '对话ID',
  `runtime_uuid` VARCHAR(64) DEFAULT NULL COMMENT '运行时UUID',
  `node_uuid` VARCHAR(64) DEFAULT NULL COMMENT '触发节点UUID',
  `interaction_type` VARCHAR(32) NOT NULL COMMENT '交互类型: user_select/user_confirm/user_form/user_text',
  `interaction_params` TEXT DEFAULT NULL COMMENT '交互参数JSON',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '交互描述',
  `status` VARCHAR(20) NOT NULL DEFAULT 'WAITING' COMMENT '状态: WAITING/SUBMITTED/TIMEOUT/CANCELLED',
  `timeout_minutes` INT DEFAULT 30 COMMENT '超时时间(分钟)',
  `timeout_at` DATETIME DEFAULT NULL COMMENT '超时截止时间',
  `feedback_data` TEXT DEFAULT NULL COMMENT '用户反馈数据JSON',
  `feedback_action` VARCHAR(32) DEFAULT NULL COMMENT '用户操作',
  `submitted_at` DATETIME DEFAULT NULL COMMENT '提交时间',
  `c_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `u_time` DATETIME DEFAULT NULL COMMENT '修改时间',
  `c_id` BIGINT DEFAULT NULL COMMENT '创建人ID',
  `u_id` BIGINT DEFAULT NULL COMMENT '修改人ID',
  `dbversion` INT DEFAULT 0 COMMENT '数据版本(乐观锁)',
  PRIMARY KEY (`id`),
  INDEX `idx_ai_workflow_interaction_1` (`interaction_uuid`),
  INDEX `idx_ai_workflow_interaction_2` (`conversation_id`, `status`),
  INDEX `idx_ai_workflow_interaction_3` (`status`, `timeout_at`),
  INDEX `idx_ai_workflow_interaction_4` (`runtime_uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI工作流人机交互记录';
```

**Step 2: 执行DDL**

在数据库 `scm_tenant_20250519_001` 中执行上述SQL。

---

## Task 2: 扩展 HumanFeedbackNodeConfig

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/humanfeedback/HumanFeedbackNodeConfig.java`

**Step 1: 替换整个文件内容**

```java
package com.xinyirun.scm.ai.workflow.node.humanfeedback;

import lombok.Data;
import java.util.List;

/**
 * 工作流人机交互节点配置
 *
 * 支持4种交互类型：
 * - text: 自由文本输入（默认，向后兼容）
 * - confirm: 确认/驳回
 * - select: 单项选择
 * - form: 表单填写
 */
@Data
public class HumanFeedbackNodeConfig {

    /**
     * 提示文本（向后兼容）
     */
    private String tip;

    /**
     * 交互类型: confirm | select | form | text
     * 默认text（向后兼容）
     */
    private String interactionType;

    /**
     * 超时时间（分钟），默认30
     */
    private Integer timeoutMinutes;

    // ---- confirm 类型参数 ----

    /**
     * 确认按钮文本，默认"确认"
     */
    private String confirmText;

    /**
     * 驳回按钮文本，默认"驳回"
     */
    private String rejectText;

    /**
     * 详情说明文本
     */
    private String detail;

    // ---- select 类型参数 ----

    /**
     * 选项来源: "static" | "dynamic"，默认static
     */
    private String optionsSource;

    /**
     * 静态选项列表
     */
    private List<SelectOption> options;

    /**
     * 动态选项：上游节点输出参数名
     */
    private String dynamicOptionsParam;

    // ---- form 类型参数 ----

    /**
     * 表单字段列表
     */
    private List<FormField> fields;

    /**
     * 获取有效的交互类型，默认text
     */
    public String getEffectiveInteractionType() {
        return (interactionType != null && !interactionType.isEmpty()) ? interactionType : "text";
    }

    /**
     * 获取有效的超时时间，默认30分钟
     */
    public int getEffectiveTimeoutMinutes() {
        return (timeoutMinutes != null && timeoutMinutes > 0) ? timeoutMinutes : 30;
    }

    @Data
    public static class SelectOption {
        private String key;
        private String label;
    }

    @Data
    public static class FormField {
        private String key;
        private String label;
        /**
         * 字段类型: text | textarea | number | select
         */
        private String type;
        private Boolean required;
        /**
         * type=select时的下拉选项
         */
        private List<SelectOption> options;
    }
}
```

**Step 2: 确认编译无误**

确认无导入错误、语法正确。

---

## Task 3: 扩展 WorkflowEventVo — 增加带交互配置的中断事件

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/bean/vo/workflow/WorkflowEventVo.java`

**Step 1: 在 createInterruptData 方法之后，新增重载方法**

在现有 `createInterruptData(String nodeUuid, String tip)` 方法（约第99行）之后，添加：

```java
/**
 * 创建带交互配置的人机交互中断数据
 *
 * @param nodeUuid 节点UUID
 * @param tip 提示信息
 * @param interactionType 交互类型(confirm/select/form/text)
 * @param interactionRequestJson 交互请求JSON字符串(含interaction_uuid等)
 * @return 人机交互事件
 */
public static WorkflowEventVo createInterruptDataWithInteraction(
        String nodeUuid, String tip, String interactionType, String interactionRequestJson) {
    JSONObject json = new JSONObject();
    json.put("type", "interrupt");
    json.put("node", nodeUuid);
    json.put("tip", tip != null ? tip : "请输入您的反馈");
    json.put("interactionType", interactionType != null ? interactionType : "text");
    if (interactionRequestJson != null) {
        json.put("interaction_request", interactionRequestJson);
        json.put("waiting_interaction", true);
    }
    return WorkflowEventVo.builder().data(json.toJSONString()).build();
}
```

---

## Task 4: 扩展 WorkflowEngine.handleInterruption — 读取config + 创建DB记录

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/WorkflowEngine.java` (约第497-523行 handleInterruption方法、第1114-1120行 getHumanFeedbackTip方法)

**Step 1: 在WorkflowEngine中注入AiWorkflowInteractionService**

在类的字段声明区域（约第44-80行）添加：

```java
private AiWorkflowInteractionService interactionService;
```

在构造方法或初始化方法中注入（根据当前构造方式传入）。如果WorkflowEngine不是Spring Bean（是new出来的），需要通过参数传入。确认WorkflowEngine的实例化方式后再决定具体注入方式。

**Step 2: 重写 handleInterruption 方法（替换第497-523行）**

```java
private Flux<WorkflowEventVo> handleInterruption(GraphResponse<NodeOutput> graphResponse) {
    DataSourceHelper.use(this.tenantCode);

    String nextInterruptNode = wfState.getInterruptNodes().stream()
        .filter(nodeUuid -> wfState.getCompletedNodes().stream()
            .noneMatch(completedNode -> completedNode.getNode().getUuid().equals(nodeUuid)))
        .findFirst()
        .orElse(null);

    if (nextInterruptNode != null) {
        AiWorkflowNodeVo feedbackNode = getNodeByUuid(nextInterruptNode);
        HumanFeedbackNodeConfig nodeConfig = getHumanFeedbackConfig(feedbackNode);
        String effectiveType = nodeConfig.getEffectiveInteractionType();
        String tip = nodeConfig.getTip() != null ? nodeConfig.getTip() : "等待用户输入: " + feedbackNode.getTitle();

        // 构建交互参数（含动态选项解析）
        String interactionParams = buildInteractionParams(nodeConfig, effectiveType);

        // 创建DB交互记录
        String runtimeUuid = wfState.getUuid();
        String conversationId = this.conversationId;
        String interactionRequestJson = null;

        if (interactionService != null) {
            AiWorkflowInteractionEntity interaction = interactionService.createInteraction(
                conversationId, runtimeUuid, nextInterruptNode,
                "user_" + effectiveType, interactionParams, tip,
                nodeConfig.getEffectiveTimeoutMinutes());

            // 构建前端需要的interaction_request JSON
            JSONObject reqJson = new JSONObject();
            reqJson.put("interaction_uuid", interaction.getInteractionUuid());
            reqJson.put("interaction_type", "user_" + effectiveType);
            reqJson.put("description", tip);
            reqJson.put("timeout_minutes", interaction.getTimeoutMinutes());
            reqJson.put("timeout_at", interaction.getTimeoutAt() != null ? interaction.getTimeoutAt().toString() : null);
            reqJson.put("params", JSONObject.parseObject(interactionParams));
            interactionRequestJson = reqJson.toJSONString();
        }

        InterruptedFlow.RUNTIME_TO_GRAPH.put(wfState.getUuid(), this);
        wfState.setProcessStatus(WORKFLOW_PROCESS_STATUS_READY);

        if (this.wfRuntimeResp != null) {
            workflowRuntimeService.updateOutput(wfRuntimeResp.getId(), wfState);
        } else if (this.conversationRuntimeResp != null) {
            conversationRuntimeService.updateOutput(conversationRuntimeResp.getId(), wfState);
        }

        return Flux.just(WorkflowEventVo.createInterruptDataWithInteraction(
            nextInterruptNode, tip, effectiveType, interactionRequestJson));
    }

    return Flux.<WorkflowEventVo>empty();
}
```

**Step 3: 替换 getHumanFeedbackTip 为 getHumanFeedbackConfig（替换第1114-1120行）**

```java
private HumanFeedbackNodeConfig getHumanFeedbackConfig(AiWorkflowNodeVo feedbackNode) {
    try {
        com.alibaba.fastjson2.JSONObject configObj = feedbackNode.getNodeConfig();
        if (configObj != null && !configObj.isEmpty()) {
            return configObj.toJavaObject(HumanFeedbackNodeConfig.class);
        }
    } catch (Exception e) {
        log.warn("解析人机交互节点配置失败: {}", e.getMessage());
    }
    return new HumanFeedbackNodeConfig();
}
```

**Step 4: 添加 buildInteractionParams 方法**

```java
/**
 * 构建交互参数JSON
 * 如果是动态选项，从wfState的上游节点输出中读取
 */
private String buildInteractionParams(HumanFeedbackNodeConfig config, String effectiveType) {
    JSONObject params = new JSONObject();

    switch (effectiveType) {
        case "confirm":
            params.put("confirm_text", config.getConfirmText() != null ? config.getConfirmText() : "确认");
            params.put("reject_text", config.getRejectText() != null ? config.getRejectText() : "驳回");
            if (config.getDetail() != null) {
                params.put("detail", config.getDetail());
            }
            break;

        case "select":
            List<HumanFeedbackNodeConfig.SelectOption> options = config.getOptions();
            // 动态选项：从上游节点输出中读取
            if ("dynamic".equals(config.getOptionsSource()) && config.getDynamicOptionsParam() != null) {
                options = resolveDynamicOptions(config.getDynamicOptionsParam());
            }
            params.put("options", options != null ? options : List.of());
            break;

        case "form":
            params.put("fields", config.getFields() != null ? config.getFields() : List.of());
            break;

        default: // text
            break;
    }

    return params.toJSONString();
}

/**
 * 从wfState的已完成节点输出中解析动态选项
 */
private List<HumanFeedbackNodeConfig.SelectOption> resolveDynamicOptions(String paramName) {
    try {
        for (var completedNode : wfState.getCompletedNodes()) {
            List<NodeIOData> outputs = completedNode.getOutputs();
            if (outputs == null) continue;
            for (NodeIOData output : outputs) {
                if (paramName.equals(output.getName())) {
                    String value = output.getContent();
                    if (value != null && !value.isEmpty()) {
                        return JSONObject.parseArray(value, HumanFeedbackNodeConfig.SelectOption.class);
                    }
                }
            }
        }
    } catch (Exception e) {
        log.warn("解析动态选项失败, paramName={}: {}", paramName, e.getMessage());
    }
    return List.of();
}
```

---

## Task 5: 扩展 HumanFeedbackNode.onProcess — 解析JSON反馈，输出多个NodeIOData

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/humanfeedback/HumanFeedbackNode.java`

**Step 1: 替换 onProcess 方法**

```java
@Override
protected NodeProcessResult onProcess() {
    HumanFeedbackNodeConfig nodeConfig = checkAndGetConfig(HumanFeedbackNodeConfig.class);
    log.info("HumanFeedbackNode config: {}", nodeConfig);

    Object feedbackData = state.data().get(HUMAN_FEEDBACK_KEY);
    if (feedbackData == null) {
        log.warn("人机交互节点未获取到用户反馈, nodeUuid: {}", node.getUuid());
        return NodeProcessResult.builder().content(List.of()).build();
    }

    String userInput = feedbackData.toString();
    log.info("用户反馈输入: {}", userInput);

    List<NodeIOData> result = parseUserFeedback(userInput);
    return NodeProcessResult.builder().content(result).build();
}

/**
 * 解析用户反馈JSON，输出多个NodeIOData
 * 向后兼容：如果不是JSON格式，当作text类型处理
 */
private List<NodeIOData> parseUserFeedback(String userInput) {
    List<NodeIOData> result = new java.util.ArrayList<>();

    try {
        com.alibaba.fastjson2.JSONObject feedback = com.alibaba.fastjson2.JSONObject.parseObject(userInput);
        String action = feedback.getString("action");

        if (action == null) {
            // 不是结构化反馈，当作纯文本
            result.add(NodeIOData.createByText("action", "default", "text_input"));
            result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", userInput));
            return result;
        }

        result.add(NodeIOData.createByText("action", "default", action));

        switch (action) {
            case "confirm":
            case "reject":
                String confirmDesc = "confirm".equals(action) ? "用户确认了操作" : "用户驳回了操作";
                result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", confirmDesc));
                break;

            case "select_record":
                String selectedKey = feedback.getString("selectedKey");
                String selectedLabel = feedback.getString("selectedLabel");
                result.add(NodeIOData.createByText("selectedKey", "default", selectedKey != null ? selectedKey : ""));
                result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default",
                    "用户选择了: " + (selectedLabel != null ? selectedLabel : selectedKey)));
                break;

            case "form_submit":
                com.alibaba.fastjson2.JSONObject data = feedback.getJSONObject("data");
                String formDataStr = data != null ? data.toJSONString() : "{}";
                result.add(NodeIOData.createByText("formData", "default", formDataStr));
                result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default",
                    "用户填写了表单: " + formDataStr));
                break;

            case "text_input":
                String text = feedback.getString("text");
                result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default",
                    text != null ? text : ""));
                break;

            default:
                result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", userInput));
                break;
        }
    } catch (Exception e) {
        // JSON解析失败，当作纯文本处理（向后兼容）
        log.debug("用户反馈非JSON格式，作为纯文本处理: {}", e.getMessage());
        result.add(NodeIOData.createByText("action", "default", "text_input"));
        result.add(NodeIOData.createByText(DEFAULT_OUTPUT_PARAM_NAME, "default", userInput));
    }

    return result;
}
```

---

## Task 6: 超时定时任务 — 终止工作流

**Files:**
- Modify: `scm-ai/src/main/java/com/xinyirun/scm/ai/core/schedule/InteractionTimeoutScheduler.java`

**Step 1: 扩展 processExpiredForCurrentDs 方法，超时后终止工作流**

在 `interactionService.timeoutInteraction()` 调用后，添加终止对应工作流的逻辑：

```java
private void processExpiredForCurrentDs(String dsName) {
    List<AiWorkflowInteractionEntity> expired = interactionService.findExpiredInteractions();
    if (expired.isEmpty()) {
        return;
    }

    log.info("租户{}发现{}条超时交互记录", dsName, expired.size());
    for (AiWorkflowInteractionEntity entity : expired) {
        try {
            interactionService.timeoutInteraction(entity.getInteractionUuid());

            // 终止对应工作流：从缓存中移除并更新runtime状态
            String runtimeUuid = entity.getRuntimeUuid();
            if (runtimeUuid != null) {
                InterruptedFlow.RUNTIME_TO_GRAPH.remove(runtimeUuid);
                log.info("已终止超时工作流: runtimeUuid={}, interactionUuid={}",
                    runtimeUuid, entity.getInteractionUuid());
            }
        } catch (Exception e) {
            log.error("处理超时交互失败: interactionUuid={}", entity.getInteractionUuid(), e);
        }
    }
}
```

需要添加import: `import com.xinyirun.scm.ai.workflow.InterruptedFlow;`

---

## Task 7: 前端编辑器面板 — HumanFeedbackNodeProperty.vue

**Files:**
- Modify: `01_scm_frontend/scm_frontend/src/components/70_ai/components/workflow/components/properties/HumanFeedbackNodeProperty.vue`

**Step 1: 替换整个文件**

完整代码见下方。核心功能：
- el-radio-group 切换交互类型 (text/confirm/select/form)
- 根据类型动态显示配置区域
- select类型支持静态/动态选项来源切换
- form类型支持动态添加/删除字段行

```vue
<template>
  <div class="human-feedback-node-property">
    <!-- 交互类型 -->
    <div class="property-section">
      <div class="section-title">交互类型</div>
      <el-radio-group v-model="nodeConfig.interactionType" size="small">
        <el-radio-button label="text">自由文本</el-radio-button>
        <el-radio-button label="confirm">确认/驳回</el-radio-button>
        <el-radio-button label="select">单项选择</el-radio-button>
        <el-radio-button label="form">表单填写</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 提示文本 -->
    <div class="property-section">
      <div class="section-title">提示文本</div>
      <el-input
        v-model="nodeConfig.tip"
        type="textarea"
        :autosize="{ minRows: 2, maxRows: 6 }"
        placeholder="请输入提示信息"
      />
    </div>

    <!-- 超时时间 -->
    <div class="property-section">
      <div class="section-title">超时时间（分钟）</div>
      <el-input-number
        v-model="nodeConfig.timeoutMinutes"
        :min="1"
        :max="1440"
        size="small"
        style="width: 100%"
      />
    </div>

    <!-- confirm 类型配置 -->
    <template v-if="nodeConfig.interactionType === 'confirm'">
      <div class="property-section">
        <div class="section-title">确认按钮文本</div>
        <el-input v-model="nodeConfig.confirmText" placeholder="确认" size="small" />
      </div>
      <div class="property-section">
        <div class="section-title">驳回按钮文本</div>
        <el-input v-model="nodeConfig.rejectText" placeholder="驳回" size="small" />
      </div>
      <div class="property-section">
        <div class="section-title">详情说明（选填）</div>
        <el-input
          v-model="nodeConfig.detail"
          type="textarea"
          :autosize="{ minRows: 2, maxRows: 4 }"
          placeholder="补充说明信息"
        />
      </div>
    </template>

    <!-- select 类型配置 -->
    <template v-if="nodeConfig.interactionType === 'select'">
      <div class="property-section">
        <div class="section-title">选项来源</div>
        <el-radio-group v-model="nodeConfig.optionsSource" size="small">
          <el-radio label="static">静态配置</el-radio>
          <el-radio label="dynamic">动态（上游节点输出）</el-radio>
        </el-radio-group>
      </div>

      <!-- 静态选项列表 -->
      <div v-if="nodeConfig.optionsSource !== 'dynamic'" class="property-section">
        <div class="section-title">选项列表</div>
        <div
          v-for="(opt, idx) in nodeConfig.options"
          :key="idx"
          class="option-row"
        >
          <el-input v-model="opt.key" placeholder="选项标识" size="small" style="width: 40%; margin-right: 4px" />
          <el-input v-model="opt.label" placeholder="显示文本" size="small" style="width: 46%; margin-right: 4px" />
          <el-button type="text" icon="el-icon-delete" size="small" style="color: #F56C6C" @click="removeOption(idx)" />
        </div>
        <el-button type="text" icon="el-icon-plus" size="small" @click="addOption">添加选项</el-button>
      </div>

      <!-- 动态选项参数名 -->
      <div v-if="nodeConfig.optionsSource === 'dynamic'" class="property-section">
        <div class="section-title">上游输出参数名</div>
        <el-input v-model="nodeConfig.dynamicOptionsParam" placeholder="如: options_list" size="small" />
      </div>
    </template>

    <!-- form 类型配置 -->
    <template v-if="nodeConfig.interactionType === 'form'">
      <div class="property-section">
        <div class="section-title">表单字段</div>
        <div
          v-for="(field, idx) in nodeConfig.fields"
          :key="idx"
          class="field-row"
        >
          <el-input v-model="field.key" placeholder="字段标识" size="small" style="width: 22%; margin-right: 4px" />
          <el-input v-model="field.label" placeholder="显示名称" size="small" style="width: 22%; margin-right: 4px" />
          <el-select v-model="field.type" placeholder="类型" size="small" style="width: 22%; margin-right: 4px">
            <el-option label="文本" value="text" />
            <el-option label="多行文本" value="textarea" />
            <el-option label="数字" value="number" />
            <el-option label="下拉选择" value="select" />
          </el-select>
          <el-checkbox v-model="field.required" style="margin-right: 4px">必填</el-checkbox>
          <el-button type="text" icon="el-icon-delete" size="small" style="color: #F56C6C" @click="removeField(idx)" />
        </div>
        <el-button type="text" icon="el-icon-plus" size="small" @click="addField">添加字段</el-button>
      </div>
    </template>
  </div>
</template>

<script>
export default {
  name: 'HumanFeedbackNodeProperty',

  props: {
    workflow: {
      type: Object,
      required: true
    },
    wfNode: {
      type: Object,
      required: true
    }
  },

  computed: {
    nodeConfig () {
      const cfg = this.wfNode.nodeConfig
      // 初始化默认值
      if (!cfg.interactionType) this.$set(cfg, 'interactionType', 'text')
      if (!cfg.tip) this.$set(cfg, 'tip', '')
      if (!cfg.timeoutMinutes) this.$set(cfg, 'timeoutMinutes', 30)
      if (!cfg.optionsSource) this.$set(cfg, 'optionsSource', 'static')
      if (!cfg.options) this.$set(cfg, 'options', [])
      if (!cfg.fields) this.$set(cfg, 'fields', [])
      return cfg
    }
  },

  methods: {
    addOption () {
      this.nodeConfig.options.push({ key: '', label: '' })
    },
    removeOption (idx) {
      this.nodeConfig.options.splice(idx, 1)
    },
    addField () {
      this.nodeConfig.fields.push({ key: '', label: '', type: 'text', required: false })
    },
    removeField (idx) {
      this.nodeConfig.fields.splice(idx, 1)
    }
  }
}
</script>

<style lang="scss" scoped>
.human-feedback-node-property {
  padding: 16px 0;

  .property-section {
    margin-top: 16px;

    .section-title {
      font-size: 13px;
      font-weight: 500;
      margin-bottom: 6px;
      color: #303133;
    }
  }

  .option-row,
  .field-row {
    display: flex;
    align-items: center;
    margin-bottom: 6px;
  }
}
</style>
```

---

## Task 8: 前端SSE处理 — 根据interactionType渲染交互组件

**Files:**
- Modify: `01_scm_frontend/scm_frontend/src/components/70_ai/api/aiChatService.js` (约第147行，已有interaction_request处理逻辑)

**Step 1: 确认现有SSE处理逻辑**

当前 aiChatService.js 第147行已经有：
```javascript
if (chatResponse.interaction_request && typeof onInteractionRequest === 'function') {
    const interactionReq = JSON.parse(chatResponse.interaction_request)
    onInteractionRequest(interactionReq)
}
```

这段代码已经可以工作。需要确认的是：SSE interrupt 事件的 data 中包含 `interaction_request` 字段时，前端能否正确解析。

后端 `createInterruptDataWithInteraction` 方法直接把 `interaction_request` 作为字符串放在 JSON data 中。前端解析 SSE data 后，`chatResponse.interaction_request` 是字符串，`JSON.parse` 后得到交互请求对象，传给 `onInteractionRequest` 回调。

**需要确认前端 SSE 处理链路中 interrupt 类型的事件是否经过 aiChatService.js 中的这段逻辑。** 如果 interrupt 事件走的是不同的处理路径，需要在那里也加上 interaction_request 的解析。

---

## Task 9: 集成验证

**Step 1: 后端编译确认**

确保所有Java文件编译通过，无导入错误。

**Step 2: 前端确认**

确保 HumanFeedbackNodeProperty.vue 在工作流编辑器中正确渲染，4种类型切换正常。

**Step 3: 端到端验证**

1. 创建一个工作流：开始 → 人机交互(confirm类型) → 结束
2. 运行工作流
3. 确认SSE事件包含 interactionType 和 interaction_request
4. 确认前端弹出 AiUserConfirm 组件
5. 点击确认，确认工作流恢复并输出 action=confirm

---

## 注意事项

1. **WorkflowEngine 实例化方式**：WorkflowEngine 是 new 出来的还是 Spring Bean？如果是 new 出来的，`interactionService` 需要通过构造参数传入。Task 4 需要根据实际情况调整注入方式。

2. **向后兼容**：所有扩展都保持向后兼容。老的 nodeConfig 只有 tip 字段时，interactionType 默认为 text，行为和之前完全一致。

3. **前端SSE处理链路**：Task 8 需要确认 interrupt 事件是否经过 aiChatService.js 的处理路径。如果不是，需要在正确的位置添加 interaction_request 解析。
