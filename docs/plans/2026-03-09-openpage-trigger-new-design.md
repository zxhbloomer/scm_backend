# OpenPage节点触发业务页面新增按钮 - 设计方案

## 一、需求

用户在 AI 聊天中输入"打开采购项目新增页面"，系统应自动：
1. 跳转到采购项目列表页（`/po/project`）
2. 自动打开新增 tab，等同于用户手动点击"新增"按钮

## 二、完整调用链路（已验证）

```
用户输入: "打开采购项目新增页面"
  ↓
后端 WorkflowRoutingService
  → LLM路由，选中"通用-打开页面-流程"工作流（uuid: 923afee77630456d8b78d4c69dac6b4e）
  ↓
McpTool节点（获取pagecode路由）
  → 调用 checkPageAccess + getPageMenuPaths
  → 输出: {"found_count":1,"routes":[{"path":"/po/project","page_code":"P00000170",...}]}
  ↓
Classifier节点（路由判断）
  → found_count=1 → 走"存在单个路由时"分支
  ↓
OpenPage节点（打开前端页面）
  → route模式，从上游JSON解析 path=/po/project
  → node_config.page_mode = "new"
  → 输出: open_page_command = {"route":"/po/project","page_mode":"new"}
  ↓
前端 SSE 接收 output 事件（type=output, nodeName=OpenPage）
  → chat.js onOpenPageCommand(command)
  → AiPageRouter.navigateToPage(command)
      ├─ router.push({ path:'/po/project', query:{ _ai:'1', _ai_mode:'new' } })
      └─ $bus.$emit('ai-page-action', { mode:'new' })  ← 当前断点！
  ↓
页面跳转到 /po/project
  → 加载 src/views/40_business/10_po/project/index.vue
  ↓
【缺失】index.vue 没有监听 ai-page-action，也没有检测 _ai_mode
  → 新增 tab 不会自动打开
```

## 三、数据库确认

```sql
-- m_menu 表确认路由
-- page_code=P00000170, path=/po/project, component=/40_business/10_po/project/index

-- ai_workflow_node 确认 OpenPage 节点配置
-- node_config: {"open_mode":"route","page_mode":"new",...}
```

## 四、方案设计

### 方案选择：路由 query 参数检测（方案B）

**理由**：
- `$bus.$emit` 有时序风险（页面 mount 前事件已发出则丢失）
- `$route.query._ai_mode` 在 `mounted` 时一定存在，100% 可靠
- 封装成 mixin，所有业务页面一行代码接入

### 4.1 前端 Mixin

**新建文件**：`src/mixin/aiPageActionMixin.js`

逻辑：
- `mounted` 时检测 `this.$route.query._ai_mode`
- `new` → 构造默认参数，调用 `this.handleNew(data)`
- `edit` / `view` / `approve` → 需要 `_ai_record_id`，调用对应方法（本期只实现 `new`）

```js
// 构造 handleNew 所需的默认参数
const data = {
  operate_tab_info: { show: true, name: '新增' },
  canEdit: false,
  editStatus: 'insert'
}
this.handleNew(data)
```

### 4.2 业务页面接入

**修改文件**：`src/views/40_business/10_po/project/index.vue`

只需两处改动：
1. `import aiPageActionMixin from '@/mixin/aiPageActionMixin'`
2. `mixins: [aiPageActionMixin]`

### 4.3 后端无需改动

- `OpenPageNode.java` 已正确输出 `open_page_command`
- `AiPageRouter.js` 已正确传递 `_ai_mode` query 参数
- 后端 `node_config.page_mode = "new"` 已正确配置

## 五、文件清单

### 前端（仅前端改动）

| 文件 | 操作 | 说明 |
|------|------|------|
| `src/mixin/aiPageActionMixin.js` | 新建 | 检测 `_ai_mode` query 参数，触发对应操作 |
| `src/views/40_business/10_po/project/index.vue` | 修改 | 引入 mixin，验证功能 |

### 后端（无需改动）

- `OpenPageNode.java` ✅ 已正确实现
- `AiPageRouter.js` ✅ 已正确传递参数

## 六、KISS 原则评估

1. **真问题**：是，`$bus` 事件无人监听，新增 tab 不会打开
2. **更简单的方法**：query 参数检测是最简单可靠的方案
3. **会破坏什么**：不会，mixin 只在 `_ai_mode` 存在时才触发，正常访问不受影响
4. **真的需要**：是，这是 OpenPage 节点的核心功能
5. **过度设计**：否，mixin 只有约 20 行代码
6. **幻觉风险**：无，所有数据均经数据库和代码验证
7. **注意事项**：mixin 放在 `src/mixin/` 目录，与现有 `resizeHandlerMixin.js` 同级

## 七、后续扩展（本期不做）

- `edit` 模式：需要 `_ai_record_id`，调用 `handleUpdate`
- `view` 模式：需要 `_ai_record_id`，调用 `handleView`
- 推广到其他业务页面（poorder、pocontract 等）
