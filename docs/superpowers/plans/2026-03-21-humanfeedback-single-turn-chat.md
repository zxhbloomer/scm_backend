# HumanFeedback 单轮对话修复 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 修复 HumanFeedback 节点中断后产生两轮对话记录的问题，使数据库只保存原始用户问题 + AI 完整回复，前端 UI 将 resume 后的输出追加到同一 AI 气泡。

**Architecture:** 后端在 `handleResponse()` 的 `isWaitingInput=true` 分支保存原始用户问题，在 `isComplete=true` 分支用 `workflowState` 判断 resume 场景并跳过 USER 消息保存。前端新增 `resumeInteraction` action，`MessageList.vue` 的 submit/cancel 改走该 action（不再触发新的 sendMessage），`loadMessages` 加 filter 兜底旧数据。

**Tech Stack:** Java 17 / Spring Boot 3.1.4 / Vue.js 2.7.16 / Vuex

---

## 文件清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `scm-ai/src/main/java/com/xinyirun/scm/ai/controller/chat/AiConversationController.java` | 修改 | `handleResponse()` 签名 + 保存逻辑 + 调用处 |
| `01_scm_frontend/scm_frontend/src/components/70_ai/store/modules/chat.js` | 修改 | `onInteractionRequest` 存 aiMessageId + 新增 `resumeInteraction` action + `loadMessages` filter |
| `01_scm_frontend/scm_frontend/src/components/70_ai/components/chat/messages/MessageList.vue` | 修改 | `handleInteractionSubmit` + `handleInteractionCancel` 改走 `resumeInteraction` |
| `01_scm_frontend/scm_frontend/src/components/70_ai/components/interaction/AiInteractionManager.js` | 修改 | 将 `stopCountdown` 改为 `export function` 并加入 `export default` |

---

## Task 0：前端 — AiInteractionManager.js export stopCountdown

**文件：**
- 修改：`01_scm_frontend/scm_frontend/src/components/70_ai/components/interaction/AiInteractionManager.js`

**背景：**
- `stopCountdown` 当前是私有函数（第 156 行：`function stopCountdown () {`），未被 export
- `resumeInteraction` action 需要调用它来停止倒计时
- `export default` 里当前有：`startInteraction, submitFeedback, cancelInteraction, clearInteraction, getActiveInteraction, formatRemainingTime`

- [ ] **Step 1：将 stopCountdown 改为 export function，并加入 export default**

  将第 156 行：
  ```js
  function stopCountdown () {
  ```
  改为：
  ```js
  export function stopCountdown () {
  ```

  然后在 `export default` 对象里加入 `stopCountdown`（第 163 行附近）：
  ```js
  export default {
    startInteraction,
    submitFeedback,
    cancelInteraction,
    clearInteraction,
    getActiveInteraction,
    formatRemainingTime,
    stopCountdown
  }
  ```

- [ ] **Step 2：commit**

  ```bash
  git -C 01_scm_frontend/scm_frontend add src/components/70_ai/components/interaction/AiInteractionManager.js
  git -C 01_scm_frontend/scm_frontend commit -m "fix(ai): export stopCountdown供resumeInteraction调用"
  ```

---

## Task 1：后端 — 修改 handleResponse() 保存逻辑

**文件：**
- 修改：`scm-ai/src/main/java/com/xinyirun/scm/ai/controller/chat/AiConversationController.java`

**背景：**
- `handleResponse()` 当前签名（第 455 行）：`private ChatResponseVo handleResponse(ChatResponseVo response, String conversationId, String userPrompt, Long operatorId, AtomicReference<String> aiResponseAccumulator)`
- 调用处（第 417 行）：`return handleResponse(response, conversationId, userPrompt, operatorId, aiResponseAccumulator);`
- `workflowState` 在第 323 行捕获：`String workflowState = conversation.getWorkflowState();`
- `isWaitingInput=true` 分支（第 469-476 行）：只调用 `updateWorkflowState`，不保存任何内容
- `isComplete=true` 分支（第 478-541 行）：第 516 行 `if (!userPrompt.isEmpty() || !finalAiResponse.isEmpty())` 保存 USER + AI

- [ ] **Step 1：修改 handleResponse() 签名，新增 workflowState 参数**

  将第 455-456 行：
  ```java
  private ChatResponseVo handleResponse(ChatResponseVo response, String conversationId,
          String userPrompt, Long operatorId, AtomicReference<String> aiResponseAccumulator) {
  ```
  改为：
  ```java
  private ChatResponseVo handleResponse(ChatResponseVo response, String conversationId,
          String userPrompt, Long operatorId, AtomicReference<String> aiResponseAccumulator,
          String workflowState) {
  ```

- [ ] **Step 2：修改调用处，传入 workflowState**

  将第 417 行：
  ```java
  return handleResponse(response, conversationId, userPrompt, operatorId, aiResponseAccumulator);
  ```
  改为：
  ```java
  return handleResponse(response, conversationId, userPrompt, operatorId, aiResponseAccumulator, workflowState);
  ```

- [ ] **Step 3：在 isWaitingInput=true 分支里保存原始用户问题**

  将第 469-476 行：
  ```java
  if (Boolean.TRUE.equals(response.getIsWaitingInput())) {
      aiConversationService.updateWorkflowState(
          conversationId,
          WorkflowStateConstant.STATE_WORKFLOW_WAITING_INPUT,
          response.getWorkflowUuid(),
          response.getRuntimeUuid()
      );
  }
  ```
  改为：
  ```java
  if (Boolean.TRUE.equals(response.getIsWaitingInput())) {
      aiConversationService.updateWorkflowState(
          conversationId,
          WorkflowStateConstant.STATE_WORKFLOW_WAITING_INPUT,
          response.getWorkflowUuid(),
          response.getRuntimeUuid()
      );
      // 第一轮中断：保存原始用户问题
      // workflowState != STATE_WORKFLOW_WAITING_INPUT 确保多节点场景下只保存第一次原始问题
      try {
          if (userPrompt != null && !userPrompt.isEmpty()
                  && !WorkflowStateConstant.STATE_WORKFLOW_WAITING_INPUT.equals(workflowState)) {
              aiConversationContentService.saveContent(
                  conversationId, 1, userPrompt, operatorId, null, null, null
              );
          }
      } catch (Exception e) {
          log.error("保存用户消息失败(中断场景): conversationId={}", conversationId, e);
      }
  }
  ```

- [ ] **Step 4：修改 isComplete=true 分支，resume 场景跳过 USER 消息保存**

  将第 516-519 行：
  ```java
  if (!userPrompt.isEmpty() || !finalAiResponse.isEmpty()) {
      aiConversationContentService.saveContent(
          conversationId, 1, userPrompt, operatorId, null, null, null
      );
  ```
  完整替换第 508-537 行的保存块：
  ```java
  // 保存对话内容
  try {
      String finalAiResponse = fullContent;
      String runtimeUuid = response.getRuntimeUuid();

      log.info("【AI-Chat-保存】准备保存对话内容: conversationId={}, runtimeUuid={}",
          conversationId, runtimeUuid);

      // resume 场景（第二轮）：跳过 USER 消息保存，原始问题已在第一轮保存
      boolean isResume = WorkflowStateConstant.STATE_WORKFLOW_WAITING_INPUT.equals(workflowState);
      if (!isResume && userPrompt != null && !userPrompt.isEmpty()) {
          aiConversationContentService.saveContent(
              conversationId, 1, userPrompt, operatorId, null, null, null
          );
      }

      // 保存 AI 消息（普通对话和 resume 都保存）
      if (!finalAiResponse.isEmpty()) {
          // 构建工作流思考步骤JSON（根据runtimeId查询节点执行记录）
          String workflowSteps = null;
          if (response.getRuntimeId() != null) {
              try {
                  workflowSteps = aiConversationContentService.buildWorkflowStepsJson(response.getRuntimeId());
              } catch (Exception e) {
                  log.warn("构建工作流思考步骤失败, runtimeId={}", response.getRuntimeId(), e);
              }
          }

          var aiContentVo = aiConversationContentService.saveContent(
              conversationId, 2, finalAiResponse, operatorId, runtimeUuid, response.getAi_open_dialog_para(), workflowSteps
          );
          if (aiContentVo != null && aiContentVo.getMessage_id() != null) {
              response.setMessageId(aiContentVo.getMessage_id());
          }
      }
  } catch (Exception e) {
      log.error("保存对话内容失败: conversationId={}", conversationId, e);
  }
  ```

- [ ] **Step 5：commit 后端改动**

  ```bash
  git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/controller/chat/AiConversationController.java
  git -C 00_scm_backend/scm_backend commit -m "fix(ai): HumanFeedback中断场景修正对话记录保存逻辑"
  ```

---

## Task 2：前端 — chat.js 改动

**文件：**
- 修改：`01_scm_frontend/scm_frontend/src/components/70_ai/store/modules/chat.js`

**背景：**
- `onInteractionRequest` 回调（第 476-481 行）：当前调用 `startInteraction(request, ...)` 未传 `_aiMessageId`
- `aiMessageId` 在 `sendMessage` action 闭包里（`const aiMessageId = 'ai_' + Date.now()`），`onInteractionRequest` 在同一闭包内可访问
- `loadMessages` action（第 629 行）：`formattedMessages = messages.map(...)` 需要在 map 前加 filter

- [ ] **Step 1：修改 onInteractionRequest，存入 aiMessageId**

  将第 476-481 行：
  ```js
  onInteractionRequest: (request) => {
    // 人机交互请求：启动交互状态管理
    import('@/components/70_ai/components/interaction/AiInteractionManager.js').then(({ startInteraction }) => {
      startInteraction(request, { state: { chat: state }, commit })
    })
  },
  ```
  改为：
  ```js
  onInteractionRequest: (request) => {
    // 人机交互请求：启动交互状态管理，存入 aiMessageId 供 resumeInteraction 追加内容使用
    import('@/components/70_ai/components/interaction/AiInteractionManager.js').then(({ startInteraction }) => {
      startInteraction({ ...request, _aiMessageId: aiMessageId }, { state: { chat: state }, commit })
    })
  },
  ```

- [ ] **Step 2：在 loadMessages action 里加 filter（第 637 行）**

  将第 637 行：
  ```js
  const formattedMessages = messages.map(msg => {
  ```
  改为：
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
    .map(msg => {
  ```

- [ ] **Step 3：新增 resumeInteraction action**

  在 `loadMessages` action 之前（第 628 行 `async loadMessages` 前），插入新 action：
  ```js
  async resumeInteraction ({ commit, state }) {
    const interaction = state.activeInteraction
    if (!interaction) return

    const aiMessageId = interaction._aiMessageId
    const conversationId = state.conversationId

    if (!aiMessageId || !conversationId) return

    // 构建 feedback 消息
    const feedbackMessage = JSON.stringify({
      type: 'ai_interaction_feedback',
      interaction_uuid: interaction.interaction_uuid,
      action: interaction._pendingAction,
      data: interaction._pendingData
    })

    // 标记气泡为 resuming 状态
    commit('UPDATE_MESSAGE', { messageId: aiMessageId, updates: { status: 'streaming', isStreaming: true, isHidden: false } })
    commit('SET_TYPING', true)

    // 停止倒计时（stopCountdown 已在 Task 0 中 export）
    import('@/components/70_ai/components/interaction/AiInteractionManager.js').then(({ stopCountdown }) => {
      stopCountdown()
    })

    // sendMessageStream 第一个参数是对象，第二个参数是 callbacks
    // 注意：aiChatService 使用文件顶部已有的静态 import，不需要动态 import
    aiChatService.sendMessageStream(
      { conversationId, prompt: feedbackMessage, chatModelId: 'default' },
      {
        onContent: (content) => {
          const msg = state.messages.find(m => m.id === aiMessageId)
          commit('UPDATE_MESSAGE', {
            messageId: aiMessageId,
            updates: { content: (msg?.content || '') + content, status: 'streaming', isHidden: false }
          })
        },
        onComplete: (fullContent, chatResponse) => {
          commit('SET_TYPING', false)
          // 用气泡当前内容（已追加完毕），不用 fullContent（只含本轮内容，会抹掉第一轮输出）
          const finalContent = state.messages.find(m => m.id === aiMessageId)?.content || ''
          commit('UPDATE_MESSAGE', {
            messageId: aiMessageId,
            updates: {
              content: finalContent,
              status: 'delivered',
              isStreaming: false
            }
          })
        },
        onError: (error) => {
          commit('SET_TYPING', false)
          commit('UPDATE_MESSAGE', {
            messageId: aiMessageId,
            updates: { status: 'error', isStreaming: false, content: error.message || '执行失败' }
          })
        },
        onInteractionRequest: (request) => {
          // 多个连续 HumanFeedback 节点：继续存入新的 interaction
          import('@/components/70_ai/components/interaction/AiInteractionManager.js').then(({ startInteraction }) => {
            startInteraction({ ...request, _aiMessageId: aiMessageId }, { state: { chat: state }, commit })
          })
        }
      }
    )
  },
  ```

  **注意**：`resumeInteraction` 里的 `aiChatService` 直接使用文件顶部已有的静态 import，不需要动态 import。`stopCountdown` 需要在 Task 0 中先 export。

- [ ] **Step 4：commit 前端 chat.js 改动**

  ```bash
  git -C 01_scm_frontend/scm_frontend add src/components/70_ai/store/modules/chat.js
  git -C 01_scm_frontend/scm_frontend commit -m "fix(ai): resumeInteraction追加AI气泡内容+loadMessages过滤feedback消息"
  ```

---

## Task 3：前端 — MessageList.vue 改动

**文件：**
- 修改：`01_scm_frontend/scm_frontend/src/components/70_ai/components/chat/messages/MessageList.vue`

**背景：**
- `handleInteractionSubmit`（第 579 行）：调用 `submitFeedback()` 然后 `dispatch('chat/sendMessage', feedbackMessage)` — 触发全新 sendMessage，产生新气泡
- `handleInteractionCancel`（第 585 行）：调用 `cancelInteraction()` 然后 `dispatch('chat/sendMessage', feedbackMessage)` — 同样问题

- [ ] **Step 1：修改 handleInteractionSubmit 和 handleInteractionCancel**

  将第 579-590 行：
  ```js
  handleInteractionSubmit (action, data) {
    const feedbackMessage = submitFeedback(action, data, this.$store)
    if (feedbackMessage) {
      this.$store.dispatch('chat/sendMessage', feedbackMessage)
    }
  },
  handleInteractionCancel () {
    const feedbackMessage = cancelInteraction(this.$store)
    if (feedbackMessage) {
      this.$store.dispatch('chat/sendMessage', feedbackMessage)
    }
  },
  ```
  改为：
  ```js
  handleInteractionSubmit (action, data) {
    // 把 action/data 暂存到 activeInteraction，由 resumeInteraction 使用
    this.$store.commit('SET_ACTIVE_INTERACTION', {
      ...this.$store.state.chat.activeInteraction,
      status: 'SUBMITTED',
      _pendingAction: action,
      _pendingData: data
    })
    this.$store.dispatch('chat/resumeInteraction')
  },
  handleInteractionCancel () {
    // cancel 同样走 resumeInteraction，避免产生 feedback JSON 气泡
    this.$store.commit('SET_ACTIVE_INTERACTION', {
      ...this.$store.state.chat.activeInteraction,
      status: 'SUBMITTED',
      _pendingAction: 'cancel',
      _pendingData: null
    })
    this.$store.dispatch('chat/resumeInteraction')
  },
  ```

- [ ] **Step 2：commit 前端 MessageList.vue 改动**

  ```bash
  git -C 01_scm_frontend/scm_frontend add src/components/70_ai/components/chat/messages/MessageList.vue
  git -C 01_scm_frontend/scm_frontend commit -m "fix(ai): handleInteractionSubmit/Cancel改走resumeInteraction"
  ```

---

## 验证场景

完成后手动验证：

1. **普通对话**：发消息 → AI 回复 → 数据库 2 条（USER + ASSISTANT）→ 刷新正常显示
2. **HumanFeedback 单节点**：发消息 → 中断弹窗 → 提交 → AI 完整回复追加到同一气泡 → 数据库 2 条（原始问题 USER + ASSISTANT）→ 刷新正常显示，无 feedback JSON 气泡
3. **HumanFeedback cancel**：发消息 → 中断弹窗 → 取消 → AI 回复 → 数据库 2 条 → 刷新正常
4. **旧数据兼容**：数据库里已有 feedback JSON 记录 → 刷新后不显示该气泡

---

## 注意事项

- `workflowState` 在 `flatMapMany` 闭包里（第 323 行）捕获，代表请求进入时的状态，是 final 有效变量，可以在 `.map()` 的 lambda 里直接引用
- 第一轮保存 USER 消息时不传 `runtimeUuid`（传 null），第二轮保存 AI 消息时传 `runtimeUuid`
- `resumeInteraction` 里检查 `aiChatService` 的 import 方式，与文件顶部保持一致
- 不改动 `WorkflowEngine.java`、`WorkflowRoutingService.java`、`resumeWorkflow()` 任何逻辑
