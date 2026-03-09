# 通用-打开页面-流程 设计文档

## 目标

用户在 chat 输入"帮我打开采购订单新增页面"，后端 workflow 查询路由并返回导航指令，前端逐步执行导航（切换 TopNav → 展开菜单 → 打开页面 → 触发新增），每一步实时显示在 chat 消息流中。

---

## 整体流程

```
用户输入
  ↓
[后端 Workflow]
  MCP工具节点 → checkPageAccess + getPageMenuPaths
  内容归类节点 → single_route / multiple_routes / not_found
    ├─ single_route    → OpenPageNode → 结束（发送 open_page_command）
    ├─ multiple_routes → HumanFeedbackNode（用户选择）→ OpenPageNode → 结束
    └─ not_found       → LLMAnswerNode（告知用户）→ 结束

[前端]
  SSE 接收 open_page_command
  → AiPageRouter.navigateToPage()（带 onStep 回调）
  → chat 消息流实时追加步骤进度
```

---

## 后端改动

### 1. OpenPageNode.java — route 模式改造

**改动**：`processRouteMode` 优先从上游 `NodeIOData` 读取路由，fallback 到静态配置。

```java
// 优先读上游输入，fallback 静态配置
String path = extractInputValue("path");
if (path == null) path = nodeConfig.getRoute();

String pageMode = extractInputValue("page_mode");
if (pageMode == null) pageMode = nodeConfig.getPageMode();

JSONObject command = new JSONObject();
command.put("route", path);
command.put("page_mode", pageMode);
```

**不删除** `OpenPageNodeConfig` 中的 `route`/`page_mode` 字段，保持向后兼容。

---

## 前端改动

### 2. AiPageRouter.js — 增加 onStep 回调 + TopNav 切换

**改动**：`navigateToPage(command, router, store, onStep)` 新增第四个参数 `onStep`。

执行步骤：
1. 权限校验
2. `onStep('⏳ 正在查找页面导航路径...')`
3. 调用 `permiss_topnav` API（`type=find_by_path`，传 `path`）
4. 比较返回的 `active_code` 与 store 中 `permission_topNav_code`
5. 如果不同：`onStep('⏳ 正在切换导航栏...')` → dispatch `getPermissionAndSetTopNavAction` → `onStep('✅ 导航栏已切换')`
6. `onStep('⏳ 正在展开菜单...')` → nextTick → `onStep('✅ 菜单已就绪')`
7. `onStep('⏳ 正在打开页面，等待加载...')` → `router.push` → 等待 → `onStep('✅ 页面加载完成')`
8. 如果 `page_mode === 'new'`：`onStep('⏳ 正在触发新增...')` → emit `ai-page-action` → `onStep('✅ 完成')`

### 3. chat.js — onOpenPageCommand 传入 onStep 回调

**改动**：在 `onOpenPageCommand` 回调中，创建一条"导航进度"AI 消息，并将 `onStep` 绑定到该消息的内容追加。

```javascript
onOpenPageCommand: (command) => {
  // 创建导航进度消息
  const navMsgId = 'nav_' + Date.now()
  commit('ADD_MESSAGE', {
    id: navMsgId,
    content: '正在为您打开页面...\n',
    type: 'ai',
    timestamp: new Date().toISOString(),
    status: 'streaming',
    isNavProgress: true  // 标记为导航进度消息
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
    navigateToPage(command, router, { getters: rootGetters, commit }, onStep).then(() => {
      // 导航完成，消息状态改为 sent
      commit('UPDATE_MESSAGE', { messageId: navMsgId, updates: { status: 'sent', isStreaming: false } })
    })
  })
}
```

### 4. OpenPageNodeProperty.vue — 修复 computed 副作用

将 `computed.nodeConfig` 中的 `this.$set()` 初始化逻辑移到 `created()` 钩子，computed 只做纯读取。

---

## 各节点提示词配置（workflow 数据库记录）

### MCP工具节点（tool_input）
```
用户想要打开某个页面，请根据用户输入理解其意图，提取页面关键词，然后：
1. 调用 checkPageAccess 工具，用提取的关键词查询用户可访问的页面列表
2. 如果找到页面，对每个 page_code 调用 getPageMenuPaths 工具获取菜单路径
3. 将所有查询结果整合后返回，包含：found_count（找到的路由数量）、routes（路由列表，每项含 page_code、name、meta_title、path、component）

用户输入：${input}
```

### 内容归类节点（instruction）
```
根据上游 MCP 工具返回的 found_count 字段判断：
- found_count 等于 1：归类为 single_route
- found_count 大于 1：归类为 multiple_routes
- found_count 等于 0 或 routes 为空：归类为 not_found
```

分类列表：
- `single_route`：找到唯一路由
- `multiple_routes`：找到多条路由
- `not_found`：未找到任何路由

### 人机交互节点（tip）
```
找到了多个匹配的页面，请选择您要打开的页面：
```
`dynamicOptionsParam` 指向上游 MCP 节点的 `routes` 输出，选项显示 `meta_title`，值为完整路由对象 JSON。

### LLM回答节点（未找到分支，prompt）
```
用户想要打开"${input}"相关的页面，但系统中没有找到您有权限访问的匹配页面。
请用友好的语气告知用户未找到匹配页面，建议确认页面名称或联系管理员确认权限。
```

---

## 数据流

```
用户输入 "帮我打开采购订单新增页面"
  ↓ [MCP工具节点]
  checkPageAccess("采购订单") → [{page_code: "P00000011", name: "采购订单"}]
  getPageMenuPaths("P00000011") → [{path: "/po/order", meta_title: "采购订单", component: "..."}]
  输出: {found_count: 1, routes: [{path: "/po/order", meta_title: "采购订单", page_mode: "new"}]}
  ↓ [内容归类节点]
  found_count=1 → single_route
  ↓ [OpenPageNode]
  读取上游 path="/po/order"，page_mode 从节点配置读 "new"
  写入 WfState.open_page_command = {"route":"/po/order","page_mode":"new"}
  ↓ [SSE → 前端]
  onOpenPageCommand({route: "/po/order", page_mode: "new"})
  ↓ [AiPageRouter]
  步骤1: 查找导航路径 ✅
  步骤2: 切换 TopNav（如需要）✅
  步骤3: 展开菜单 ✅
  步骤4: 打开页面，等待加载 ✅
  步骤5: 触发新增 ✅
```

---

## 改动文件清单

| 文件 | 类型 | 改动说明 |
|------|------|---------|
| `scm-ai/.../OpenPageNode.java` | 后端 | route 模式优先读上游输入 |
| `navigator/AiPageRouter.js` | 前端 | 增加 onStep 回调 + TopNav 切换逻辑 |
| `store/modules/chat.js` | 前端 | onOpenPageCommand 创建导航进度消息 + 传 onStep |
| `properties/OpenPageNodeProperty.vue` | 前端 | 修复 computed 副作用 |
