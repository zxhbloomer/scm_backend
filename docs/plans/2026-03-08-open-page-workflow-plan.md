# 通用-打开页面-流程 实施计划

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 用户在 chat 输入"帮我打开采购订单新增页面"，后端 workflow 查询路由并返回导航指令，前端逐步执行导航并在 chat 消息流中实时显示每一步进度。

**Architecture:** 后端 OpenPageNode 改为优先读上游输入的路由（fallback 静态配置），前端 AiPageRouter 增加 onStep 回调逐步执行导航（TopNav切换→菜单展开→页面跳转→触发新增），chat store 创建导航进度消息并实时追加步骤文字。

**Tech Stack:** Java Spring Boot（后端），Vue 2.7 + Vuex + Element UI（前端），SSE 流式通信

---

## Task 1：后端 OpenPageNode — route 模式读上游输入

**文件：**
- 修改：`scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/openpage/OpenPageNode.java`

**背景：**
当前 `processRouteMode` 直接读 `nodeConfig.getRoute()` 和 `nodeConfig.getPageMode()`（静态配置）。
"通用-打开页面-流程"中路由来自上游 MCP 节点动态查询，需要优先读上游 `NodeIOData`。

**Step 1：修改 `processRouteMode` 方法**

找到 `processRouteMode` 方法（约第 69 行），将静态读取改为优先读上游输入：

```java
private NodeProcessResult processRouteMode(OpenPageNodeConfig nodeConfig) {
    // 优先从上游输入读取路由，fallback 到静态配置
    String path = extractInputValue("path");
    if (path == null) path = nodeConfig.getRoute();

    String pageMode = extractInputValue("page_mode");
    if (pageMode == null) pageMode = nodeConfig.getPageMode();

    log.info("OpenPage节点[route模式]开始，path={}, pageMode={}", path, pageMode);

    JSONObject command = new JSONObject();
    command.put("route", path);
    command.put("page_mode", pageMode);

    // 从上游节点输入中提取动态参数
    JSONObject queryParams = extractInputAsJson("query_params");
    JSONObject formData = extractInputAsJson("form_data");
    String recordId = extractInputValue("record_id");

    command.put("query_params", queryParams);
    command.put("form_data", formData);
    command.put("record_id", recordId);

    String commandJson = command.toJSONString();
    wfState.setOpen_page_command(commandJson);
    log.info("OpenPage节点[route模式]导航指令: {}", commandJson);

    state.getOutputs().add(NodeIOData.createByText("open_page_command", "页面导航指令", commandJson));

    if (Boolean.TRUE.equals(nodeConfig.getInteractionEnabled())) {
        return processInteraction(nodeConfig);
    }

    return new NodeProcessResult();
}
```

**Step 2：commit**

```bash
git -C 00_scm_backend/scm_backend add scm-ai/src/main/java/com/xinyirun/scm/ai/workflow/node/openpage/OpenPageNode.java
git -C 00_scm_backend/scm_backend commit -m "feat(ai): OpenPageNode route模式优先读上游输入路由，fallback静态配置"
```

---

## Task 2：前端 OpenPageNodeProperty.vue — 修复 computed 副作用

**文件：**
- 修改：`src/components/70_ai/components/workflow/components/properties/OpenPageNodeProperty.vue`

**背景：**
`computed.nodeConfig` 里有 `this.$set()` 调用，这是副作用，会导致响应式死循环风险。需移到 `created()` 钩子。

**Step 1：在 `computed` 前加 `created` 钩子，移走所有 `$set` 初始化**

找到 `computed: { nodeConfig () {` 块，将其中所有 `this.$set(config, ...)` 移到新增的 `created()` 钩子：

```javascript
created () {
  const config = this.wfNode.nodeConfig
  if (!config.model_name) this.$set(config, 'model_name', '')
  if (!config.prompt) this.$set(config, 'prompt', '')
  if (!config.open_mode) this.$set(config, 'open_mode', 'dialog')
  if (config.route === undefined) this.$set(config, 'route', '')
  if (config.page_mode === undefined) this.$set(config, 'page_mode', 'list')
  if (config.interaction_enabled === undefined) this.$set(config, 'interaction_enabled', false)
  if (config.interaction_type === undefined) this.$set(config, 'interaction_type', 'user_select')
  if (config.interaction_description === undefined) this.$set(config, 'interaction_description', '')
  if (config.timeout_minutes === undefined) this.$set(config, 'timeout_minutes', 30)
},

computed: {
  nodeConfig () {
    return this.wfNode.nodeConfig  // 纯读取，无副作用
  }
},
```

**Step 2：commit**

```bash
git -C 01_scm_frontend/scm_frontend add src/components/70_ai/components/workflow/components/properties/OpenPageNodeProperty.vue
git -C 01_scm_frontend/scm_frontend commit -m "fix(ai): 修复OpenPageNodeProperty computed副作用，初始化移到created钩子"
```

---

## Task 3：前端 AiPageRouter.js — 增加 onStep 回调 + TopNav 切换

**文件：**
- 修改：`src/components/70_ai/components/navigator/AiPageRouter.js`

**背景：**
当前 `navigateToPage` 只做权限校验 + `router.push`，没有 TopNav 切换，也没有步骤回调。
需要：
1. 新增 `onStep` 第四个参数，每步执行前后调用
2. 在 `router.push` 前检查并切换 TopNav（调 `getPermissionAndSetTopNavAction`，它会重新加载路由表，必须等 Promise resolve 后再 push）

**关键知识：**
- `store.getters['permission_topNav_code']` — 当前激活的 TopNav code
- `store.dispatch('permission/getPermissionAndSetTopNavAction', { pathOrIndex, topNavCode: null, type: 'find_by_path' })` — 切换 TopNav 并重新加载路由，返回 Promise
- 切换后必须等 Promise resolve 再 `router.push`，否则新路由未注册

**Step 1：重写 `navigateToPage` 函数**

完整替换 `navigateToPage` 函数（第 48-108 行）：

```javascript
export async function navigateToPage (command, router, store, onStep) {
  const step = onStep || (() => {})

  if (!command || !command.route) {
    console.warn('[AiPageRouter] 无效的导航指令:', command)
    return false
  }

  // 1. 权限校验
  if (!checkRoutePermission(command.route, store)) {
    Vue.prototype.$message.warning('没有访问该页面的权限: ' + command.route)
    step('❌ 没有访问该页面的权限')
    return false
  }

  store.commit('SET_AI_LOADING_OVERLAY', true)

  try {
    // 2. 检查是否需要切换 TopNav
    step('⏳ 正在检查导航栏...')
    const currentTopNavCode = store.getters['permission_topNav_code']

    // 调 permiss_topnav API 查目标路由属于哪个 TopNav
    // 直接复用 getPermissionAndSetTopNavAction，它内部会比较并切换
    // 注意：这个 action 会重新加载路由表，必须 await 完成再 push
    await store.dispatch('permission/getPermissionAndSetTopNavAction', {
      pathOrIndex: command.route,
      topNavCode: null,
      type: 'find_by_path'
    })

    const newTopNavCode = store.getters['permission_topNav_code']
    if (newTopNavCode !== currentTopNavCode) {
      step('✅ 导航栏已切换')
    } else {
      step('✅ 导航栏已就绪')
    }

    // 3. 等待菜单渲染
    step('⏳ 正在展开菜单...')
    await waitForNextTick()
    step('✅ 菜单已就绪')

    // 4. 构建路由参数并跳转
    step('⏳ 正在打开页面，等待加载...')
    const query = { _ai: '1' }
    if (command.page_mode) query._ai_mode = command.page_mode
    if (command.query_params) Object.assign(query, command.query_params)
    if (command.record_id) query._ai_record_id = command.record_id
    if (command.form_data) query._ai_form_data = JSON.stringify(command.form_data)

    await router.push({ path: command.route, query })
    await waitForNextTick()
    step('✅ 页面加载完成')

    // 5. 触发 page_mode 操作
    if (command.page_mode && command.page_mode !== 'list') {
      step('⏳ 正在触发操作...')
      Vue.prototype.$bus && Vue.prototype.$bus.$emit('ai-page-action', {
        mode: command.page_mode,
        record_id: command.record_id,
        form_data: command.form_data
      })
      step('✅ 完成')
    }

    return true
  } catch (err) {
    if (err.name !== 'NavigationDuplicated') {
      console.error('[AiPageRouter] 导航失败:', err)
      step('❌ 页面导航失败')
      Vue.prototype.$message.error('页面导航失败')
    }
    return false
  } finally {
    setTimeout(() => {
      store.commit('SET_AI_LOADING_OVERLAY', false)
    }, 500)
  }
}
```

**Step 2：commit**

```bash
git -C 01_scm_frontend/scm_frontend add src/components/70_ai/components/navigator/AiPageRouter.js
git -C 01_scm_frontend/scm_frontend commit -m "feat(ai): AiPageRouter增加onStep步骤回调和TopNav切换逻辑"
```

---

## Task 4：前端 chat.js — onOpenPageCommand 创建导航进度消息

**文件：**
- 修改：`src/components/70_ai/store/modules/chat.js`

**背景：**
当前 `onOpenPageCommand` 回调（约第 402 行）直接调 `navigateToPage`，没有传 `onStep`，用户看不到导航过程。
需要：
1. 在 chat 消息流中创建一条"导航进度"消息
2. 将 `onStep` 绑定到该消息的内容追加
3. 导航完成后将消息状态改为 `sent`

**Step 1：找到 `onOpenPageCommand` 回调（约第 402-407 行），替换为：**

```javascript
onOpenPageCommand: (command) => {
  // 创建导航进度消息，实时追加步骤
  const navMsgId = 'nav_' + Date.now()
  commit('ADD_MESSAGE', {
    id: navMsgId,
    content: '正在为您打开页面...\n',
    type: 'ai',
    timestamp: new Date().toISOString(),
    status: 'streaming',
    isStreaming: true,
    isHidden: false
  })

  const onStep = (stepText) => {
    const msg = state.messages.find(m => m.id === navMsgId)
    if (msg) {
      commit('UPDATE_MESSAGE', {
        messageId: navMsgId,
        updates: { content: msg.content + stepText + '\n' }
      })
    }
  }

  import('@/components/70_ai/components/navigator/AiPageRouter.js').then(({ navigateToPage }) => {
    navigateToPage(command, router, { getters: rootGetters, commit, dispatch: rootGetters }, onStep)
      .then(() => {
        commit('UPDATE_MESSAGE', {
          messageId: navMsgId,
          updates: { status: 'sent', isStreaming: false }
        })
      })
  })
},
```

**注意：** `navigateToPage` 第三个参数需要 `store` 对象，包含 `getters`、`commit`、`dispatch`。
当前代码传的是 `{ getters: rootGetters, commit }`，需要补上 `dispatch`：

```javascript
// 在 sendMessage action 内，rootGetters 已有，但需要 dispatch
// 改为传完整 store 引用：
navigateToPage(command, router, { getters: rootGetters, commit, dispatch }, onStep)
```

其中 `dispatch` 来自 `sendMessage` action 的解构参数 `{ commit, state, dispatch, rootState, rootGetters }`（第 309 行已有）。

**Step 2：commit**

```bash
git -C 01_scm_frontend/scm_frontend add src/components/70_ai/store/modules/chat.js
git -C 01_scm_frontend/scm_frontend commit -m "feat(ai): chat导航指令创建进度消息，实时显示每步操作"
```

---

## Task 5：前端 AiPageRouter.js — store 参数兼容 dispatch

**文件：**
- 修改：`src/components/70_ai/components/navigator/AiPageRouter.js`

**背景：**
Task 3 中 `navigateToPage` 调用了 `store.dispatch()`，但 Task 4 传入的 store 对象需要包含 `dispatch`。
需要确认 `store` 参数的使用方式统一。

**Step 1：在 `navigateToPage` 函数签名注释中明确 store 参数结构**

在函数顶部注释中补充：

```javascript
/**
 * 执行页面导航
 * @param {Object} command - open_page_command JSON对象
 * @param {Object} router - Vue Router 实例
 * @param {Object} store - 包含 { getters, commit, dispatch } 的 store 对象
 * @param {Function} [onStep] - 步骤回调，每步执行时调用，参数为步骤描述文字
 */
export async function navigateToPage (command, router, store, onStep) {
```

同时确认 `checkRoutePermission` 函数使用 `store.getters`（已正确），`store.dispatch` 用于切换 TopNav。

**Step 2：验证**

手动检查 `navigateToPage` 中所有 `store.` 调用：
- `store.getters['permission_topNav_code']` ✅
- `store.getters.permission_menus_routers` ✅（在 `checkRoutePermission` 里）
- `store.commit('SET_AI_LOADING_OVERLAY', ...)` ✅
- `store.dispatch('permission/getPermissionAndSetTopNavAction', ...)` ✅（需要 dispatch）

**Step 3：commit（如有改动）**

```bash
git -C 01_scm_frontend/scm_frontend add src/components/70_ai/components/navigator/AiPageRouter.js
git -C 01_scm_frontend/scm_frontend commit -m "fix(ai): AiPageRouter store参数补充dispatch支持"
```

---

## 验证步骤

1. 启动前后端
2. 在 chat 输入"帮我打开采购订单新增页面"
3. 观察 chat 消息流中出现导航进度消息，步骤逐条出现：
   ```
   正在为您打开页面...
   ⏳ 正在检查导航栏...
   ✅ 导航栏已就绪（或"已切换"）
   ⏳ 正在展开菜单...
   ✅ 菜单已就绪
   ⏳ 正在打开页面，等待加载...
   ✅ 页面加载完成
   ⏳ 正在触发操作...
   ✅ 完成
   ```
4. 页面正确跳转到采购订单，并自动触发新增弹窗
5. 测试多条路由场景：输入"帮我打开入库"，应出现人机交互选择框
6. 测试未找到场景：输入"帮我打开不存在的页面"，chat 应回复未找到提示
