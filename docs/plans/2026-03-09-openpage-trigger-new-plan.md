# OpenPage触发业务页面新增按钮 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 用户在 AI 聊天输入"打开采购项目新增页面"后，系统跳转到 `/po/project` 并自动打开新增 tab。

**Architecture:** 在前端新建 `aiPageActionMixin.js`，在业务页面 `mounted` 时检测 `$route.query._ai_mode`，若为 `new` 则自动调用 `handleNew()`。后端无需改动，`AiPageRouter.js` 已正确传递 `_ai_mode` query 参数。

**Tech Stack:** Vue 2.7, Vue Router, src/mixin/ 目录（与现有 resizeHandlerMixin.js 同级）

---

### Task 1: 新建 aiPageActionMixin.js

**Files:**
- Create: `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\mixin\aiPageActionMixin.js`

**背景知识：**
- `AiPageRouter.js` 在路由跳转时会附加 query 参数：`{ _ai:'1', _ai_mode:'new' }`
- 业务页面 `index.vue` 的 `handleNew(_data)` 需要参数：
  ```js
  {
    operate_tab_info: { show: true, name: '新增项目管理' },
    canEdit: false,
    editStatus: 'insert'   // constants_para.STATUS_INSERT 的值
  }
  ```
- `editStatus` 的值是字符串 `'insert'`（来自 `src/common/constants/constants_para.js`）
- mixin 只在 `_ai_mode` 存在时才触发，正常访问不受影响

**Step 1: 创建文件**

```js
/**
 * AI页面动作 Mixin
 *
 * 在业务页面 index.vue 中引入此 mixin，
 * 当 AI 工作流通过 open_page_command 跳转到本页面时，
 * 自动根据 $route.query._ai_mode 触发对应操作。
 *
 * 支持的 _ai_mode 值：
 * - new: 自动打开新增 tab（调用 handleNew）
 */
export default {
  mounted () {
    this._handleAiPageAction()
  },

  methods: {
    _handleAiPageAction () {
      const aiMode = this.$route && this.$route.query && this.$route.query._ai_mode
      if (!aiMode) return

      if (aiMode === 'new' && typeof this.handleNew === 'function') {
        this.$nextTick(() => {
          this.handleNew({
            operate_tab_info: { show: true, name: '新增' },
            canEdit: false,
            editStatus: 'insert'
          })
        })
      }
    }
  }
}
```

**Step 2: 确认文件已创建**

检查文件存在：
```
D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\mixin\aiPageActionMixin.js
```

---

### Task 2: 在 project/index.vue 引入 mixin

**Files:**
- Modify: `D:\2025_project\20_project_in_github\01_scm_frontend\scm_frontend\src\views\40_business\10_po\project\index.vue`

**背景知识：**
- 当前 `index.vue` 的 `created` 里已有 `this.$options.name = this.$route.meta.page_code`
- `mounted` 里只有 tab 样式调整，没有 AI 相关逻辑
- `handleNew(_data)` 在 methods 里，接收参数后切换到新增 tab

**Step 1: 在 script 顶部加 import**

在 `<script>` 标签内，找到现有的 import 语句末尾，添加：
```js
import aiPageActionMixin from '@/mixin/aiPageActionMixin'
```

**Step 2: 在 export default 里加 mixins**

找到 `export default {` 后的第一行（通常是 `name:` 或 `components:`），在其前面加：
```js
mixins: [aiPageActionMixin],
```

完整的 export default 开头应该变成：
```js
export default {
  mixins: [aiPageActionMixin],
  name: 'xxx',   // 原有内容
  components: {  // 原有内容
    ...
  },
  ...
}
```

**Step 3: 验证改动**

确认 `index.vue` 中：
1. import 语句存在
2. `mixins: [aiPageActionMixin]` 存在于 `export default` 内
3. 原有 `mounted`、`created`、`handleNew` 逻辑未被破坏

---

### Task 3: 验证功能

**验证步骤（手动测试）：**

1. 启动前端开发服务器（用户自行运行）
2. 打开 AI 聊天，输入：`打开采购项目新增页面`
3. 预期结果：
   - 页面跳转到 `/po/project`
   - 自动打开"新增项目管理" tab
   - URL 中包含 `_ai=1&_ai_mode=new`

**验证 mixin 不影响正常访问：**

1. 直接从菜单点击"采购项目管理"
2. 预期：正常打开列表页，不会自动弹出新增 tab
3. URL 中不含 `_ai_mode` 参数

---

### Task 4: Git 提交

**Step 1: 提交前端改动**

```bash
git -C D:/2025_project/20_project_in_github/01_scm_frontend/scm_frontend add src/mixin/aiPageActionMixin.js src/views/40_business/10_po/project/index.vue
git -C D:/2025_project/20_project_in_github/01_scm_frontend/scm_frontend commit -m "feat(ai): OpenPage节点触发业务页面新增按钮"
```
