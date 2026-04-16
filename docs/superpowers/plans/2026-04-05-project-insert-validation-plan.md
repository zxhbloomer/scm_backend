# 采购项目管理新增校验补充 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 补充"采购-项目管理-新增"提交审批时的前后端校验，防止 supplier_id/purchaser_id 为空及商品明细数据脏入库。

**Architecture:** 后端在 `BProjectServiceImpl.checkLogic` 的 INSERT_CHECK_TYPE 分支追加校验逻辑，注入 `MGoodsSpecMapper` 做批量存在性查询；前端修复 `prop="purchaser_id"` 与 rules key 不一致的问题。

**Tech Stack:** Java 17 + Spring Boot + MyBatis Plus（LambdaQueryWrapper）；Vue 2 + Element UI（el-form rules）

---

## 文件变更清单

| 文件 | 操作 | 说明 |
|------|------|------|
| `scm-core/src/main/java/com/xinyirun/scm/core/system/serviceimpl/business/project/BProjectServiceImpl.java` | Modify | 注入 MGoodsSpecMapper，补充 checkLogic INSERT_CHECK_TYPE 校验 |
| `scm-frontend/src/views/40_business/10_po/project/tabs/20_new/index.vue` | Modify | 修复 prop="purchaser_id" → prop="purchaser_name" |

---

### Task 1: 后端 — 注入 MGoodsSpecMapper 并补充主表字段校验

**Files:**
- Modify: `scm-core/src/main/java/com/xinyirun/scm/core/system/serviceimpl/business/project/BProjectServiceImpl.java`

- [ ] **Step 1: 在 import 区追加两行**

在第75行 `import java.util.Objects;` 之后追加：

```java
import com.xinyirun.scm.bean.entity.master.goods.MGoodsSpecEntity;
import com.xinyirun.scm.core.system.mapper.master.goods.MGoodsSpecMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.util.Map;
import java.util.stream.Collectors;
```

- [ ] **Step 2: 在 @Autowired 注入区追加 MGoodsSpecMapper**

在第151行 `private BPoContractMapper bPoContractMapper;` 之后追加：

```java
    @Autowired
    private MGoodsSpecMapper mGoodsSpecMapper;
```

- [ ] **Step 3: 在 checkLogic INSERT_CHECK_TYPE 的 name 唯一校验之后追加主表字段校验**

找到第705行 `break;` 之前（即 `duplicateNameList` 判断块结束后），在 `break;` 前插入：

```java
                // 校验上游供应商
                if (bean.getSupplier_id() == null) {
                    return CheckResultUtil.NG("上游供应商不能为空");
                }
                // 校验下游客户
                if (bean.getPurchaser_id() == null) {
                    return CheckResultUtil.NG("下游客户不能为空");
                }
```

- [ ] **Step 4: 验证编译通过**

```bash
cd D:/2025_project/20_project_in_github/00_scm_backend/scm_backend
mvn compile -pl scm-core -am -q 2>&1 | tail -5
```

期望输出：无 ERROR，最后一行为 `BUILD SUCCESS` 或空。

---

### Task 2: 后端 — 商品明细批量存在性校验

**Files:**
- Modify: `scm-core/src/main/java/com/xinyirun/scm/core/system/serviceimpl/business/project/BProjectServiceImpl.java`

- [ ] **Step 1: 在 Task 1 Step 3 追加的主表校验之后，继续追加明细校验逻辑**

紧接在 `purchaser_id` 校验块之后、`break;` 之前插入：

```java
                // 商品明细校验（不为空时逐行校验）
                List<BProjectGoodsVo> checkDetailList = bean.getDetailListData();
                if (checkDetailList != null && !checkDetailList.isEmpty()) {
                    // 批量收集非空 sku_id，一次查库，避免 N+1
                    List<Integer> skuIds = checkDetailList.stream()
                            .map(BProjectGoodsVo::getSku_id)
                            .filter(Objects::nonNull)
                            .distinct()
                            .collect(Collectors.toList());

                    // key=sku_id, value=goods_id（用于校验 sku 属于对应 goods）
                    Map<Integer, Integer> validSkuGoodsMap = java.util.Collections.emptyMap();
                    if (!skuIds.isEmpty()) {
                        validSkuGoodsMap = mGoodsSpecMapper.selectList(
                                new LambdaQueryWrapper<MGoodsSpecEntity>()
                                        .select(MGoodsSpecEntity::getId, MGoodsSpecEntity::getGoods_id)
                                        .in(MGoodsSpecEntity::getId, skuIds)
                                        .eq(MGoodsSpecEntity::getIs_del, false)
                                        .eq(MGoodsSpecEntity::getEnable, true)
                        ).stream().collect(Collectors.toMap(
                                MGoodsSpecEntity::getId,
                                MGoodsSpecEntity::getGoods_id,
                                (a, b) -> a
                        ));
                    }

                    for (int i = 0; i < checkDetailList.size(); i++) {
                        BProjectGoodsVo detail = checkDetailList.get(i);
                        int rowNum = i + 1;
                        String goodsLabel = detail.getGoods_name() != null
                                ? "第" + rowNum + "行（" + detail.getGoods_name() + "）"
                                : "第" + rowNum + "行";

                        if (detail.getGoods_id() == null) {
                            return CheckResultUtil.NG(goodsLabel + "商品ID不能为空");
                        }
                        if (org.apache.commons.lang3.StringUtils.isEmpty(detail.getGoods_name())) {
                            return CheckResultUtil.NG("第" + rowNum + "行商品名称不能为空");
                        }
                        if (detail.getSku_id() == null) {
                            return CheckResultUtil.NG(goodsLabel + "规格ID不能为空");
                        }
                        if (org.apache.commons.lang3.StringUtils.isEmpty(detail.getSku_name())) {
                            return CheckResultUtil.NG(goodsLabel + "规格名称不能为空");
                        }
                        // 存在性：sku_id 必须存在且属于对应 goods_id
                        Integer validGoodsId = validSkuGoodsMap.get(detail.getSku_id());
                        if (validGoodsId == null || !validGoodsId.equals(detail.getGoods_id())) {
                            return CheckResultUtil.NG(goodsLabel + "商品规格不存在或已停用");
                        }
                        if (detail.getQty() == null || detail.getQty().compareTo(BigDecimal.ZERO) <= 0) {
                            return CheckResultUtil.NG(goodsLabel + "合同数量必须大于0");
                        }
                        if (detail.getPrice() == null || detail.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                            return CheckResultUtil.NG(goodsLabel + "单价必须大于0");
                        }
                        if (detail.getTax_rate() == null
                                || detail.getTax_rate().compareTo(BigDecimal.ZERO) < 0
                                || detail.getTax_rate().compareTo(new BigDecimal("100")) > 0) {
                            return CheckResultUtil.NG(goodsLabel + "税率须在0-100之间");
                        }
                    }
                }
```

- [ ] **Step 2: 验证编译通过**

```bash
cd D:/2025_project/20_project_in_github/00_scm_backend/scm_backend
mvn compile -pl scm-core -am -q 2>&1 | tail -5
```

期望：无 ERROR。

---

### Task 3: 前端 — 修复 purchaser_name 的 prop 绑定

**Files:**
- Modify: `scm-frontend/src/views/40_business/10_po/project/tabs/20_new/index.vue`

- [ ] **Step 1: 修复第81行的 prop**

将：
```html
              prop="purchaser_id"
```
改为：
```html
              prop="purchaser_name"
```

- [ ] **Step 2: 验证 rules 中 purchaser_name 已存在**

确认文件第667-669行有：
```js
          purchaser_name: [
            { required: true, message: '请选择下游客户（主体企业）', trigger: 'change' }
          ],
```

如果存在，修复完成。如果不存在，在 `supplier_name` 规则之后追加上述内容。

- [ ] **Step 3: 人工验证**

启动前端，打开"采购-项目管理-新增"页面，不填写下游客户直接点"提交审批并保存"，确认出现"请选择下游客户（主体企业）"的红色提示。

---

### Task 4: 集成验证

- [ ] **Step 1: 后端完整构建**

```bash
cd D:/2025_project/20_project_in_github/00_scm_backend/scm_backend
mvn compile -pl scm-core,scm-controller -am -q 2>&1 | tail -5
```

期望：BUILD SUCCESS。

- [ ] **Step 2: 人工测试 — 主表字段校验**

启动后端，打开新增页面，不选上游供应商，点提交，期望弹出"上游供应商不能为空"。
不选下游客户，点提交，期望弹出"下游客户不能为空"。

- [ ] **Step 3: 人工测试 — 商品明细校验**

在明细表格中手动添加一行，将数量清空，点提交，期望弹出"第1行（{商品名}）合同数量必须大于0"。

- [ ] **Step 4: 人工测试 — 正常提交**

填写所有必填项，商品明细填写完整，点提交，期望正常进入审批流程，无报错。
