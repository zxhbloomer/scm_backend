# 规格管理导出功能简化修复方案

## 修复原则：完全复制物料管理逻辑，保持一致性

基于差异分析的7个问题，直接从物料管理复制对应代码，确保逻辑完全一致。

## 文件修改方案

### 文件：`src/views/30_wms/spec/page/10_list/index.vue`

#### 1. 新增缺失的方法（直接复制物料管理）

```javascript
// 新增：智能导出判断逻辑（复制goods/page/10_list/index.vue:717-739）
handleExport () {
  if (this.dataJson.multipleSelection.length <= 0) {
    this.$alert('请在表格中选择数据进行导出', '未选择数据错误', {
      confirmButtonText: '关闭',
      type: 'error'
    })
  } else if (this.dataJson.multipleSelection.length === this.dataJson.listData.length) {
    this.$confirm('请选择：当前页数据导出，全数据导出？', '确认信息', {
      distinguishCancelAndClose: true,
      confirmButtonText: '全数据导出',
      cancelButtonText: '当前页数据导出'
    }).then(() => {
      this.handleExportAllData()
    }).catch(action => {
      if (action !== 'close') {
        this.handleExportSelectionData()
      }
    })
  } else {
    this.handleExportSelectionData()
  }
},

// 新增：文件下载处理（复制goods/page/10_list/index.vue:792-821）
downloadExcelFile (response, fileName) {
  try {
    const blob = new Blob([response], {
      type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    })
    const link = document.createElement('a')
    link.href = URL.createObjectURL(blob)
    link.download = `${fileName}_${new Date().getTime()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    URL.revokeObjectURL(link.href)
  } catch (error) {
    console.error('文件下载失败:', error)
    this.$notify({
      title: '下载失败',
      message: '文件下载过程中发生错误',
      type: 'error',
      duration: this.settings.duration
    })
  }
},

// 新增：标准退出导出模式（复制goods/page/10_list/index.vue:895-901）
handleExportOk () {
  this.settings.btnShowStatus.hidenExport = true
  this.settings.btnShowStatus.showExport = false
  this.settings.exportModel = false
  this.$refs.multipleTable.clearSelection()
}
```

#### 2. 修改现有方法

```javascript
// 修改：进入导出模式（复制goods/page/10_list/index.vue:885-890）
handleModelOpen () {
  this.settings.exportModel = true
  this.settings.btnShowStatus.hidenExport = false
  // 启动用户引导（取消注释）
  this.$tours['myTour'].start()
},

// 修改：导出全部数据（复制goods/page/10_list/index.vue:741-763）
handleExportAllData () {
  this.settings.loading = true
  exportAllApi(this.dataJson.searchForm).then(response => {
    this.downloadExcelFile(response, '规格信息全部导出')
    this.$notify({
      title: '导出成功',
      message: '规格数据已成功导出到Excel文件',
      type: 'success',
      duration: this.settings.duration
    })
  }).catch(error => {
    this.$notify({
      title: '导出失败',
      message: error.message || '导出过程中发生错误',
      type: 'error',
      duration: this.settings.duration
    })
  }).finally(() => {
    this.settings.loading = false
  })
},

// 修改：导出选中数据（复制goods/page/10_list/index.vue:765-790）
handleExportSelectionData () {
  if (this.dataJson.multipleSelection.length === 0) {
    this.$message.warning('请选择要导出的记录')
    return
  }
  this.settings.loading = true
  const selectionIds = this.dataJson.multipleSelection.map(item => item.id)
  const searchData = { ids: selectionIds }
  exportSelectionApi(searchData).then(response => {
    this.downloadExcelFile(response, `规格信息选中导出_${selectionIds.length}条`)
    this.$notify({
      title: '导出成功',
      message: `已成功导出${selectionIds.length}条规格数据到Excel文件`,
      type: 'success',
      duration: this.settings.duration
    })
  }).catch(error => {
    this.$notify({
      title: '导出失败',
      message: error.message || '导出过程中发生错误',
      type: 'error',
      duration: this.settings.duration
    })
  }).finally(() => {
    this.settings.loading = false
  })
}
```

#### 3. 添加按钮（复制物料管理按钮结构）

```vue
<!-- 在现有导出按钮组中添加缺失的按钮 -->
<el-button
  v-if="!settings.btnShowStatus.hidenExport"
  v-permission="'P_SPEC:EXPORT'"
  type="primary"
  icon="el-icon-zoom-in"
  :loading="settings.loading"
  @click="handleExport"
>开始导出</el-button>

<el-button
  v-if="!settings.btnShowStatus.hidenExport"
  v-permission="'P_SPEC:EXPORT'"
  type="primary"
  icon="el-icon-zoom-in"
  :loading="settings.loading"
  @click="handleExportOk"
>关闭导出</el-button>
```

#### 4. 添加Vue Tours配置（复制物料管理配置）

```javascript
// 在data()中添加（复制goods/page/10_list/index.vue:376-392）
tourOption: {
  useKeyboardNavigation: false,
  labels: {
    buttonStop: '结束'
  }
},
steps: [{
  target: '.el-table-column--selection',
  content: '请通过点击多选框，选择要导出的规格数据！',
  params: {
    placement: 'top',
    highlight: false,
    enableScrolling: false
  }
}]
```

#### 5. 添加Vue Tours组件（复制物料管理模板）

```vue
<!-- 在template末尾添加（复制goods/page/10_list/index.vue:285） -->
<v-tour name="myTour" :steps="steps" :options="tourOption" />
```

## 修改总结

1. **直接复制**物料管理的4个核心方法
2. **直接复制**物料管理的按钮结构和事件
3. **直接复制**物料管理的Vue Tours配置
4. **直接复制**物料管理的消息提示格式

**原则**：不添加任何额外功能，确保与物料管理逻辑100%一致。