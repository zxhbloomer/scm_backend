# Fix Vuex Dispatch Namespace Bug Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 修复 AI 打开页面功能中 Vuex dispatch 命名空间错误，导致 `permission/getPermissionAndSetTopNavAction` 无法找到的问题。

**Architecture:** `chat.js` 的 `sendMessage` action 里，`dispatch` 是 chat 模块的局部 dispatch（带 `chat/` 前缀），传给 `AiPageRouter.js` 后调用 `permission/getPermissionAndSetTopNavAction` 被解析为 `chat/permission/...` 找不到。修复方案：在 `chat.js` 里包装一个 `rootDispatch`（加 `{ root: true }`），传给 `navigateToPage` 替代局部 dispatch。

**Tech Stack:** Vue 2.7, Vuex 3, JavaScript

---

### Task 1: 修复 chat.js 中的 rootDispatch 传递

**Files:**
- Modify: `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\components\70_ai\store\modules\chat.js`（约第 426 行）

**背景知识：**
- `chat` 模块注册在根级别：`store.registerModule('chat', ...)`，`namespaced: true`
- `sendMessage` action 解构的 `dispatch` 是局部 dispatch，调用时会自动加 `chat/` 前缀
- Vuex 局部 dispatch 支持第三个参数 `{ root: true }`，加了之后从根模块查找 action
- `permission/getPermissionAndSetTopNavAction` 在全局 store 的 `permission` 模块里（`src/store/modules/permission.js`）

**Step 1: 找到 chat.js 里调用 navigateToPage 的位置**

文件：`src/components/70_ai/store/modules/chat.js`，约第 425-433 行：

```javascript
import('@/components/70_ai/components/navigator/AiPageRouter.js').then(({ navigateToPage }) => {
  navigateToPage(command, router, { getters: rootGetters, commit, dispatch }, onStep)
    .then(() => {
      commit('UPDATE_MESSAGE', {
        messageId: navMsgId,
        updates: { status: 'sent', isStreaming: false }
      })
    })
})
```

**Step 2: 修改这段代码，包装 rootDispatch**

将上面代码改为：

```javascript
import('@/components/70_ai/components/navigator/AiPageRouter.js').then(({ navigateToPage }) => {
  const rootDispatch = (type, payload) => dispatch(type, payload, { root: true })
  navigateToPage(command, router, { getters: rootGetters, commit, dispatch: rootDispatch }, onStep)
    .then(() => {
      commit('UPDATE_MESSAGE', {
        messageId: navMsgId,
        updates: { status: 'sent', isStreaming: false }
      })
    })
})
```

**Step 3: 验证**

在浏览器中输入"打开采购项目新增页面"，观察：
1. 聊天窗口出现"正在为您打开页面..."进度消息
2. 进度步骤依次显示：⏳ 正在检查导航栏... → ✅ 导航栏已就绪/已切换 → ✅ 菜单已就绪 → ✅ 页面加载完成
3. 浏览器跳转到采购项目管理页面
4. 控制台不再出现 `unknown local action type: permission/getPermissionAndSetTopNavAction`

**Step 4: 确认无副作用**

- `commit` 仍然是局部 commit（`SET_AI_LOADING_OVERLAY` 在 chat 模块里，正确）
- `getters: rootGetters` 不变（`permission_topNav_code`、`permission_menus_routers` 在全局 getters 里，正确）
- `rootDispatch` 只影响 `navigateToPage` 内部的 dispatch 调用，不影响其他地方
