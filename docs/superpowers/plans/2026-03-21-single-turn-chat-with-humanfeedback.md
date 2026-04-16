# HumanFeedback 单轮对话修复 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复 HumanFeedback 节点中断后产生两轮对话的问题，使数据库只保存一条用户消息 + 一条 AI 消息，刷新后正确显示。

**Architecture:** 当前第一轮 SSE（用户提问→中断）不保存任何记录，第二轮 SSE（feedback→resume→完成）保存 feedback JSON 作为用户消息 + AI 回复。改造后：第一轮保存原始用户问题，第二轮跳过用户消息保存只保存 AI 回复。前端加载历史时过滤掉 `ai_interaction_feedback` 类型的用户消息气泡（兜底）。

**Tech Stack:** Java 17 / Spring Boot 3.1.4 / Vue.js 2.7.16

---

## 文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `scm-ai/src/main/java/com/xinyirun/scm/ai/controller/chat/AiConversationController.java` | 修改 | `handleResponse()` 改保存条件 |
| `scm-ai/src/main/java/com/xinyirun/scm/ai/core/service/chat/AiConversationContentService.java` | 无需改动 | 现有 `saveContent()` 已满足需求 |
| `scm-ai/src/main/java/com/xinyirun/scm/ai/core/mapper/chat/ExtAiConversationContentMapper.java` | 无需改动 | |
| `01_scm_frontend/scm_frontend/src/components/70_ai/store/modules/chat.js` | 修改 | `loadMessages` 过滤 feedback 消息 |

---

## 背景：当前保存逻辑（事实）

```
第一轮（用户提问 → HumanFeedback 中断）：
  handleResponse() 收到 isComplete=true, interaction_request != null
  → 条件 `isComplete && interaction_request == null` 不满足
  → 什么都不保存

第二轮（feedback → resume → 完成）：
  handleResponse() 收到 isComplete=true, interaction_request == null
  → 保存 USER 消息：userPrompt = feedback JSON 字符串
  → 保存 AI 消息：resume 后的完整输出

数据库结果：
  1. {"type":"ai_interaction_feedback",...}  (USER)
  2. AI 完整回复                              (ASSISTANT)
  原始用户问题丢失，刷新后显示 feedback JSON 气泡
```

## 目标保存逻辑

```
第一轮（用户提问 → HumanFeedback 中断）：
  isComplete=true, interaction_request != null
  → 只保存 USER 消息（原始用户问题）

第二轮（feedback → resume → 完成）：
  isComplete=true, interaction_request == null
  → 检测 userPrompt 是 ai_interaction_feedback JSON → 跳过 USER 保存
  → 只保存 AI 消息（完整输出）

数据库结果：
  1. 原始用户问题  (USER)
  2. AI 完整回复   (ASSISTANT)
```

---

## Task 1：后端 — 修改 handleResponse() 保存逻辑

**文件：**
- 修改：`scm-ai/src/main/java/com/xinyirun/scm/ai/controller/chat/AiConversationController.java`

**当前代码（第 478 行附近）：**

```java
if (Boolean.TRUE.equals(response.getIsComplete()) && response.getInteraction_request() == null) {
    // ... 保存 USER + AI 消息
    aiConversationContentService.saveContent(conversationId, 1, userPrompt, ...);  // USER
    aiConversationContentService.saveContent(conversationId, 2, finalAiResponse, ...);  // AI
}
```

**目标逻辑：**

```
情况A：isComplete=true && interaction_request != null（第一轮中断）
  → 只保存 USER 消息（原始问题）

情况B：isComplete=true && interaction_request == null（第二轮完成）
  → 如果 userPrompt 是 ai_interaction_feedback JSON → 只保存 AI 消息
  → 否则（普通对话完成）→ 保存 USER + AI 消息（原有逻辑不变）
```

- [ ] **Step 1：读取当前 handleResponse() 完整代码，确认改动范围**

  读取文件：`AiConversationController.java` 第 455-543 行

- [ ] **Step 2：实现修改**

  在 `handleResponse()` 方法中，将保存逻辑改为：

  ```java
  if (Boolean.TRUE.equals(response.getIsComplete())) {
      // 判断是否为 ai_interaction_feedback（第二轮 resume）
      boolean isFeedbackResume = isInteractionFeedback(userPrompt);

      if (response.getInteraction_request() != null) {
          // 情况A：第一轮中断 — 只保存原始用户问题
          try {
              if (!userPrompt.isEmpty()) {
                  aiConversationContentService.saveContent(
                      conversationId, 1, userPrompt, operatorId, null, null, null
                  );
              }
          } catch (Exception e) {
              log.error("保存用户消息失败(中断场景): conversationId={}", conversationId, e);
          }
      } else {
          // 情况B：工作流完成
          String fullContent = extractFullContent(response, aiResponseAccumulator);

          aiConversationService.updateWorkflowState(
              conversationId, WorkflowStateConstant.STATE_IDLE, null, null
          );

          try {
              if (!isFeedbackResume && !userPrompt.isEmpty()) {
                  // 普通对话：保存用户消息
                  aiConversationContentService.saveContent(
                      conversationId, 1, userPrompt, operatorId, null, null, null
                  );
              }
              // 保存 AI 消息
              if (!fullContent.isEmpty()) {
                  String workflowSteps = buildWorkflowSteps(response);
                  var aiContentVo = aiConversationContentService.saveContent(
                      conversationId, 2, fullContent, operatorId,
                      response.getRuntimeUuid(), response.getAi_open_dialog_para(), workflowSteps
                  );
                  if (aiContentVo != null && aiContentVo.getMessage_id() != null) {
                      response.setMessageId(aiContentVo.getMessage_id());
                  }
              }
          } catch (Exception e) {
              log.error("保存对话内容失败: conversationId={}", conversationId, e);
          }
      }
  }
  ```

  同时新增私有辅助方法：

  ```java
  /**
   * 判断用户输入是否为人机交互反馈（第二轮 resume）
   */
  private boolean isInteractionFeedback(String userPrompt) {
      if (userPrompt == null || userPrompt.isEmpty()) return false;
      try {
          JSONObject json = JSONObject.parseObject(userPrompt);
          return "ai_interaction_feedback".equals(json.getString("type"));
      } catch (Exception e) {
          return false;
      }
  }
  ```

  注意：`extractFullContent` 和 `buildWorkflowSteps` 是将现有内联逻辑提取为私有方法，减少重复。

- [ ] **Step 3：验证逻辑覆盖**

  检查以下场景：
  - 普通 LLM 对话（无 workflow）：`interaction_request == null`，`isFeedbackResume = false` → 保存 USER + AI ✅
  - 第一轮中断：`interaction_request != null` → 只保存 USER ✅
  - 第二轮 resume：`interaction_request == null`，`isFeedbackResume = true` → 只保存 AI ✅

- [ ] **Step 4：commit**

  ```bash
  git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/controller/chat/AiConversationController.java
  git -C 00_scm_backend/scm_backend commit -m "fix(ai): HumanFeedback中断场景修正对话记录保存逻辑"
  ```

---

## Task 2：前端 — loadMessages 过滤 feedback 消息（兜底）

**文件：**
- 修改：`src/components/70_ai/store/modules/chat.js`

**目的：** 即使数据库里存在历史的 feedback JSON 记录（Task 1 上线前产生的旧数据），刷新后也不显示为气泡。

**当前代码（第 637 行附近）：**

```js
const formattedMessages = messages.map(msg => { ... })
commit('SET_MESSAGES', formattedMessages)
```

- [ ] **Step 1：在 map 之后加 filter**

  ```js
  const formattedMessages = messages
    .filter(msg => {
      // 过滤掉 ai_interaction_feedback 类型的用户消息（HumanFeedback 中断产生的旧数据）
      if (msg.type === 'USER' || msg.type === 'user') {
        try {
          const parsed = JSON.parse(msg.content)
          if (parsed && parsed.type === 'ai_interaction_feedback') return false
        } catch (e) { /* 非 JSON，正常显示 */ }
      }
      return true
    })
    .map(msg => { ... /* 原有 map 逻辑不变 */ })
  ```

- [ ] **Step 2：验证**

  - 正常用户消息：显示 ✅
  - feedback JSON 消息：不显示 ✅
  - AI 消息：不受影响 ✅

- [ ] **Step 3：commit**

  ```bash
  git -C 01_scm_frontend/scm_frontend add src/components/70_ai/store/modules/chat.js
  git -C 01_scm_frontend/scm_frontend commit -m "fix(ai): loadMessages过滤ai_interaction_feedback消息气泡"
  ```

---

## 验证场景

完成后手动验证：

1. **正常对话**：发消息 → AI 回复 → 数据库 2 条（USER + ASSISTANT）→ 刷新正常显示
2. **HumanFeedback 流程**：发消息 → 中断弹窗 → 提交 → AI 完整回复 → 数据库 2 条（原始问题 USER + ASSISTANT）→ 刷新正常显示，无 feedback JSON 气泡
3. **旧数据兼容**：数据库里已有 feedback JSON 记录 → 刷新后不显示该气泡

---

## 注意事项

- `isWaitingInput=true` 时 `updateWorkflowState` 的调用位置（第 469 行）不受影响，保持原样
- 第一轮保存 USER 消息时不传 `runtimeUuid`（此时 runtime 还在运行中），第二轮保存 AI 消息时传 `runtimeUuid`
- 不改动 `WorkflowEngine`、`WorkflowRoutingService`、`resumeWorkflow` 任何逻辑
