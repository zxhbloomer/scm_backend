---
applyTo: '**'
---
# 采购合同 Mapper 数据库逻辑分析

## 一、主表从表的链接和链接关系

### 1. 主表：b_po_contract (采购合同表)
- **主键：** id
- **重要字段：** contract_code, supplier_id, purchaser_id, status, type, delivery_type, settle_type, bill_type, payment_type

### 2. 从表关系

#### 2.1 采购合同详情表 (b_po_contract_detail)
```sql
-- 关联关系：一对多
LEFT JOIN (
    select po_contract_id,JSON_ARRAYAGG(
        JSON_OBJECT(
            'sku_code', sku_code,
            'sku_name', sku_name,
            'origin', origin,
            'sku_id', sku_id,
            'unit_id', unit_id,
            'goods_id', goods_id,
            'goods_code', goods_code,
            'goods_name', goods_name,
            'qty', qty,
            'price', price,
            'amount', amount,
            'tax_amount', tax_amount,
            'tax_rate', tax_rate
        )
    ) as detailListData
    from b_po_contract_detail 
    GROUP BY po_contract_id
) tab2 ON tab1.id = tab2.po_contract_id
```
- **关联条件：** b_po_contract.id = b_po_contract_detail.po_contract_id
- **数据处理：** 使用JSON_ARRAYAGG聚合多条明细记录为JSON数组

#### 2.2 采购合同附件表 (b_po_contract_attach)
```sql
LEFT JOIN b_po_contract_attach tab3 on tab1.id = tab3.po_contract_id
```
- **关联条件：** b_po_contract.id = b_po_contract_attach.po_contract_id
- **获取字段：** one_file as doc_att_file

#### 2.3 采购合同财务汇总表 (b_po_contract_total)
```sql
LEFT JOIN b_po_contract_total tab2 ON tab1.id = tab2.po_contract_id
```
- **关联条件：** b_po_contract.id = b_po_contract_total.po_contract_id
- **用途：** 合计查询中获取财务汇总数据

#### 2.4 采购订单表 (b_po_order)
```sql
LEFT JOIN b_po_order tab12 on tab12.po_contract_id = tab1.id
    and tab12.is_del = false 
    and tab1.type = '0' -- 特定类型的合同
```
- **关联条件：** b_po_contract.id = b_po_order.po_contract_id
- **用途：** 判断是否存在关联订单 (existence_order 字段)

## 二、字典表的链接，获取的name

### 2.1 合同状态字典
```sql
LEFT JOIN s_dict_data tab3 ON 
    tab3.code = 'B_PO_CONTRACT_STATUS' 
    AND tab3.dict_value = tab1.status
```
- **获取字段：** tab3.label as status_name
- **字典类型：** B_PO_CONTRACT_STATUS
- **对应主表字段：** status

### 2.2 合同类型字典
```sql
LEFT JOIN s_dict_data tab4 ON 
    tab4.code = 'B_PO_CONTRACT_TYPE' 
    AND tab4.dict_value = tab1.type
```
- **获取字段：** tab4.label as type_name
- **字典类型：** B_PO_CONTRACT_TYPE
- **对应主表字段：** type

### 2.3 交付方式字典
```sql
LEFT JOIN s_dict_data tab5 ON 
    tab5.code = 'B_PO_CONTRACT_DELIVERY_TYPE' 
    AND tab5.dict_value = tab1.delivery_type
```
- **获取字段：** tab5.label as delivery_type_name
- **字典类型：** B_PO_CONTRACT_DELIVERY_TYPE
- **对应主表字段：** delivery_type

### 2.4 结算方式字典
```sql
LEFT JOIN s_dict_data tab6 ON 
    tab6.code = 'B_PO_CONTRACT_SETTLE_TYPE' 
    AND tab6.dict_value = tab1.settle_type
```
- **获取字段：** tab6.label as settle_type_name
- **字典类型：** B_PO_CONTRACT_SETTLE_TYPE
- **对应主表字段：** settle_type

### 2.5 票据类型字典
```sql
LEFT JOIN s_dict_data tab7 ON 
    tab7.code = 'B_PO_CONTRACT_BILL_TYPE' 
    AND tab7.dict_value = tab1.bill_type
```
- **获取字段：** tab7.label as bill_type_name
- **字典类型：** B_PO_CONTRACT_BILL_TYPE
- **对应主表字段：** bill_type

### 2.6 付款方式字典
```sql
LEFT JOIN s_dict_data tab8 ON 
    tab8.code = 'B_PO_CONTRACT_PAYMENT_TYPE' 
    AND tab8.dict_value = tab1.payment_type
```
- **获取字段：** tab8.label as payment_type_name
- **字典类型：** B_PO_CONTRACT_PAYMENT_TYPE
- **对应主表字段：** payment_type

## 三、ID获取name的链接和获取的内容

### 3.1 员工信息获取（创建人、修改人）

#### 3.1.1 创建人信息
```sql
LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
```
- **关联条件：** m_staff.id = b_po_contract.c_id
- **获取字段：** tab13.name as c_name
- **用途：** 获取创建人姓名

#### 3.1.2 修改人信息
```sql
LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
```
- **关联条件：** m_staff.id = b_po_contract.u_id
- **获取字段：** tab14.name as u_name
- **用途：** 获取修改人姓名

### 3.2 流程实例信息
```sql
LEFT JOIN (
    SELECT * FROM bmp_instance 
    WHERE serial_type = 'BPM_INSTANCE_B_PO_CONTRACT'
    ORDER BY c_time DESC 
    LIMIT 1
) as tab11 on tab11.serial_id = tab1.id
```
- **关联条件：** bpm_instance.serial_id = b_po_contract.id
- **获取字段：** tab11.process_code as process_code
- **用途：** 获取最新的流程编码
- **特殊逻辑：** 按创建时间倒序取最新一条记录

### 3.3 特殊字段处理

#### 3.3.1 自动创建订单状态转换
```sql
iF(tab1.auto_create_order,'是','否') auto_create_name
```
- **逻辑：** 布尔值转换为中文显示
- **true：** '是'
- **false：** '否'

#### 3.3.2 是否存在订单判断
```sql
iF(tab12.id,false,true) existence_order
```
- **逻辑：** 如果关联的采购订单ID存在，则返回false；否则返回true
- **用途：** 判断合同是否已生成对应的采购订单

## 四、关键SQL模式总结

### 4.1 字典表关联模式
```sql
LEFT JOIN s_dict_data tabX ON 
    tabX.code = '字典类型常量' 
    AND tabX.dict_value = tab1.字段名
```

### 4.2 ID转Name模式
```sql
LEFT JOIN 主数据表 tabX ON tabX.id = tab1.关联ID字段
```

### 4.3 一对多聚合模式
```sql
LEFT JOIN (
    SELECT 主表ID, JSON_ARRAYAGG(JSON_OBJECT(...)) as 聚合字段
    FROM 从表
    GROUP BY 主表ID
) tabX ON tab1.id = tabX.主表ID
```

### 4.4 最新记录获取模式
```sql
LEFT JOIN (
    SELECT * FROM 表名
    WHERE 条件
    ORDER BY 时间字段 DESC
    LIMIT 1
) tabX ON 关联条件
```

---

# 项目管理 Mapper 数据库逻辑分析

## 一、主表从表的链接和链接关系

### 1. 主表：b_project (项目管理表)
- **主键：** id
- **重要字段：** name, code, type, status, payment_method, delivery_type, finance_id

### 2. 从表关系

#### 2.1 项目商品明细表 (b_project_goods)
```sql
-- 关联关系：一对多
LEFT JOIN (
    select project_id,JSON_ARRAYAGG(
        JSON_OBJECT(
            'goods_id', goods_id,
            'goods_code', goods_code,
            'goods_name', goods_name,
            'sku_code', sku_code,
            'sku_name', sku_name,
            'origin', origin,
            'sku_id', sku_id,
            'unit_id', unit_id,
            'qty', qty,
            'price', price,
            'amount', amount,
            'tax_amount', tax_amount,
            'tax_rate', tax_rate
        )
    ) as detailListData
    from b_project_goods 
    GROUP BY project_id
) tab2 ON t1.id = tab2.project_id
```
- **关联条件：** b_project.id = b_project_goods.project_id
- **数据处理：** 使用JSON_ARRAYAGG聚合多条商品明细记录为JSON数组

#### 2.2 项目附件表 (b_project_attach)
```sql
LEFT JOIN b_project_attach tab3 on t1.id = tab3.project_id
```
- **关联条件：** b_project.id = b_project_attach.project_id
- **获取字段：** one_file as doc_att_file

## 二、字典表的链接，获取的name

### 2.1 项目类型字典
```sql
LEFT JOIN s_dict_data t8 ON t1.type = t8.dict_value 
    AND t8.code = 'DICT_B_PROJECT_TYPE'
```
- **获取字段：** t8.label as type_name
- **字典类型：** DICT_B_PROJECT_TYPE
- **对应主表字段：** type

### 2.2 项目状态字典
```sql
LEFT JOIN s_dict_data t9 ON t1.status = t9.dict_value 
    AND t9.code = 'DICT_B_PROJECT_STATUS'
```
- **获取字段：** t9.label as status_name
- **字典类型：** DICT_B_PROJECT_STATUS
- **对应主表字段：** status

### 2.3 付款方式字典
```sql
LEFT JOIN s_dict_data t10 ON t1.payment_method = t10.dict_value 
    AND t10.code = 'DICT_B_PROJECT_PAYMENT_METHOD'
```
- **获取字段：** t10.label as payment_method_name
- **字典类型：** DICT_B_PROJECT_PAYMENT_METHOD
- **对应主表字段：** payment_method

### 2.4 运输方式字典
```sql
LEFT JOIN s_dict_data t11 ON t1.delivery_type = t11.dict_value 
    AND t11.code = 'DICT_B_PROJECT_DELIVERY_TYPE'
```
- **获取字段：** t11.label as delivery_type_name
- **字典类型：** DICT_B_PROJECT_DELIVERY_TYPE
- **对应主表字段：** delivery_type

## 三、ID获取name的链接和获取的内容

### 3.1 企业信息获取
```sql
LEFT JOIN m_enterprise t14 ON t14.id = t1.finance_id
```
- **关联条件：** m_enterprise.id = b_project.finance_id
- **获取字段：** t14.name as finance_name
- **用途：** 获取财务方企业名称

### 3.2 员工信息获取（创建人、修改人）

#### 3.2.1 创建人信息
```sql
LEFT JOIN m_staff tab13 ON tab13.id = t1.c_id
```
- **关联条件：** m_staff.id = b_project.c_id
- **获取字段：** tab13.name as c_name

#### 3.2.2 修改人信息
```sql
LEFT JOIN m_staff tab14 ON tab14.id = t1.u_id
```
- **关联条件：** m_staff.id = b_project.u_id
- **获取字段：** tab14.name as u_name

---

# 采购订单 Mapper 数据库逻辑分析

## 一、主表从表的链接和链接关系

### 1. 主表：b_po_order (采购订单表)
- **主键：** id
- **重要字段：** code, po_contract_id, po_contract_code, supplier_id, purchaser_id, status

### 2. 从表关系

#### 2.1 采购订单明细表 (b_po_order_detail)
```sql
-- 关联关系：一对多
LEFT JOIN (
    select po_order_id,JSON_ARRAYAGG(
        JSON_OBJECT(
            'sku_code', sku_code,
            'sku_name', sku_name,
            'origin', origin,
            'sku_id', sku_id,
            'unit_id', unit_id,
            'qty', qty,
            'price', price,
            'amount', amount,
            'tax_amount', tax_amount,
            'tax_rate', tax_rate,
            'goods_id', goods_id,
            'goods_name', goods_name,
            'goods_code', goods_code
        )
    ) as detailListData,
    GROUP_CONCAT(sku_name) as goods_name
    from b_po_order_detail 
    GROUP BY po_order_id
) tab2 ON tab1.id = tab2.po_order_id
```
- **关联条件：** b_po_order.id = b_po_order_detail.po_order_id
- **数据处理：** 使用JSON_ARRAYAGG聚合明细数据，同时用GROUP_CONCAT连接商品名称

#### 2.2 采购订单附件表 (b_po_order_attach)
```sql
LEFT JOIN b_po_order_attach tabb1 on tab1.id = tabb1.po_order_id
```
- **关联条件：** b_po_order.id = b_po_order_attach.po_order_id
- **获取字段：** one_file as doc_att_file

#### 2.3 采购订单财务汇总表 (b_po_order_total)
```sql
LEFT JOIN b_po_order_total tab16 ON tab16.po_order_id = tab1.id
```
- **关联条件：** b_po_order.id = b_po_order_total.po_order_id
- **获取字段：** 预付款、应付款、已付款等财务汇总数据

#### 2.4 采购合同表 (b_po_contract)
```sql
LEFT JOIN b_po_contract tab11 on tab11.id = tab1.po_contract_id
```
- **关联条件：** b_po_order.po_contract_id = b_po_contract.id
- **获取字段：** sign_date, expiry_date 等合同信息

## 二、字典表的链接，获取的name

### 2.1 订单状态字典
```sql
LEFT JOIN s_dict_data tab3 ON 
    tab3.code = 'DICT_B_PO_ORDER_STATUS' 
    AND tab3.dict_value = tab1.status
```
- **获取字段：** tab3.label as status_name

### 2.2 运输方式字典
```sql
LEFT JOIN s_dict_data tab4 ON 
    tab4.code = 'DICT_B_PO_ORDER_DELIVERY_TYPE' 
    AND tab4.dict_value = tab1.delivery_type
```
- **获取字段：** tab4.label as delivery_type_name

### 2.3 结算方式字典
```sql
LEFT JOIN s_dict_data tab5 ON 
    tab5.code = 'DICT_B_PO_ORDER_SETTLE_TYPE' 
    AND tab5.dict_value = tab1.settle_type
```
- **获取字段：** tab5.label as settle_type_name

### 2.4 票据类型字典
```sql
LEFT JOIN s_dict_data tab6 ON 
    tab6.code = 'DICT_B_PO_ORDER_BILL_TYPE' 
    AND tab6.dict_value = tab1.bill_type
```
- **获取字段：** tab6.label as bill_type_name

### 2.5 付款方式字典
```sql
LEFT JOIN s_dict_data tab7 ON 
    tab7.code = 'DICT_B_PO_ORDER_PAYMENT_TYPE' 
    AND tab7.dict_value = tab1.payment_type
```
- **获取字段：** tab7.label as payment_type_name

### 2.6 合同类型字典
```sql
LEFT JOIN s_dict_data tab12 ON 
    tab12.code = 'DICT_B_PO_CONTRACT_TYPE' 
    AND tab12.dict_value = tab11.type
```
- **获取字段：** tab12.label as type_name

## 三、ID获取name的链接和获取的内容

### 3.1 员工信息获取
```sql
LEFT JOIN m_staff tab13 ON tab13.id = tab1.c_id
LEFT JOIN m_staff tab14 ON tab14.id = tab1.u_id
```
- **获取字段：** tab13.name as c_name, tab14.name as u_name

### 3.2 流程实例信息
```sql
LEFT JOIN (
    SELECT * FROM bpm_instance 
    WHERE serial_type = 'BPM_INSTANCE_B_PO_ORDER'
    ORDER BY c_time DESC 
    LIMIT 1
) as tab10 on tab10.serial_id = tab1.id
```
- **获取字段：** tab10.process_code as process_code

---

# 应付账款 (AP) Mapper 数据库逻辑分析

## 一、主表从表的链接和链接关系

### 1. 主表：b_ap (应付账款表)
- **主键：** id
- **重要字段：** code, type, status, pay_status, po_contract_code, po_order_code, supplier_id, purchaser_id

### 2. 从表关系

#### 2.1 应付账款预付关联单据表 (b_ap_source_advance)
```sql
-- 复杂的一对多聚合查询
LEFT JOIN (
    SELECT t1.ap_id,JSON_ARRAYAGG(JSON_OBJECT(
        'id',t1.id,
        'code',t1.code,
        'ap_id',t1.ap_id,
        'type',t1.type,
        'po_contract_code',t1.po_contract_code,
        'po_order_id',t1.po_order_id,
        'po_order_code',t1.po_order_code,
        'po_goods',t1.po_goods,
        'qty_total',t1.qty_total,
        'amount_total',t1.amount_total,
        'order_amount',t1.order_amount,
        'po_advance_payment_amount',(tt2.paying_amount_total + tt2.paid_amount_total),
        'po_can_advance_payment_amount',(t1.amount_total - ifnull(t3.amount,0))
    )) AS poOrderListData
    FROM b_ap_source_advance t1
    LEFT JOIN b_ap t2 ON t1.ap_id = t2.id
    LEFT JOIN b_ap_total tt2 ON t2.id = tt2.ap_id
    LEFT JOIN (子查询计算已有应付金额) t3 ON t1.po_contract_code = t3.po_contract_code
    GROUP BY t1.ap_id
) tab2 ON tab1.id = tab2.ap_id
```

#### 2.2 应付账款明细表 (b_ap_detail)
```sql
LEFT JOIN (
    SELECT t1.ap_id,JSON_ARRAYAGG(JSON_OBJECT(
        'id',t1.id,
        'bank_accounts_id',t1.bank_accounts_id,
        'bank_accounts_code',t1.bank_accounts_code,
        'payable_amount',t1.payable_amount,
        'paid_amount',t1.paid_amount,
        'name',t2.name,
        'bank_name',t2.bank_name,
        'account_number',t2.account_number,
        'accounts_purpose_type_name',t3.name
    )) AS bankListData
    FROM b_ap_detail t1 
    LEFT JOIN m_bank_accounts t2 ON t1.bank_accounts_id = t2.id
    LEFT JOIN m_bank_accounts_type t3 ON t1.bank_accounts_type_id = t3.id
    GROUP BY t1.ap_id
) tab3 ON tab1.id = tab3.ap_id
```

#### 2.3 应付账款财务汇总表 (b_ap_total)
```sql
LEFT JOIN b_ap_total tabb2 on tab1.id = tabb2.ap_id
```
- **获取字段：** unpay_amount_total, paid_amount_total, paying_amount_total等

#### 2.4 应付账款附件表 (b_ap_attach)
```sql
LEFT JOIN b_ap_attach tabb1 on tab1.id = tabb1.ap_id
```
- **获取字段：** one_file as doc_att_file

#### 2.5 应付退款表 (b_ap_refund)
```sql
LEFT JOIN (
    SELECT t2.ap_id,
        sum(t1.refunded_amount) AS refunded_amount,
        sum(t1.refunding_amount) AS refunding_amount,
        sum(t1.refund_amount) AS refund_amount
    FROM b_ap_refund t1
    LEFT JOIN b_ap_refund_source_advance t2 ON t1.id = t2.ap_refund_id
    WHERE t1.is_del = FALSE 
    GROUP BY t2.ap_id
) tab9 on tab9.ap_id = tab1.id
```

## 二、字典表的链接，获取的name

### 2.1 应付账款状态字典
```sql
LEFT JOIN s_dict_data tab6 ON 
    tab6.code = 'DICT_B_AP_STATUS' 
    AND tab6.dict_value = tab1.status
```
- **获取字段：** tab6.label as status_name

### 2.2 应付账款类型字典
```sql
LEFT JOIN s_dict_data tab7 ON 
    tab7.code = 'DICT_B_AP_TYPE' 
    AND tab7.dict_value = tab1.type
```
- **获取字段：** tab7.label as type_name

### 2.3 付款状态字典
```sql
LEFT JOIN s_dict_data tab8 ON 
    tab8.code = 'DICT_B_AP_PAY_STATUS' 
    AND tab8.dict_value = tab1.pay_status
```
- **获取字段：** tab8.label as pay_status_name

## 三、ID获取name的链接和获取的内容

### 3.1 员工信息获取
```sql
LEFT JOIN m_staff tab4 ON tab4.id = tab1.c_id
LEFT JOIN m_staff tab5 ON tab5.id = tab1.u_id
```
- **获取字段：** tab4.name as c_name, tab5.name as u_name

---

# 应付账款支付 (APPAY) Mapper 数据库逻辑分析

## 一、主表从表的链接和链接关系

### 1. 主表：b_ap_pay (付款单表)
- **主键：** id
- **重要字段：** code, ap_id, ap_code, status, type, supplier_id, purchaser_id

### 2. 从表关系

#### 2.1 应付账款主表 (b_ap)
```sql
LEFT JOIN b_ap tab6 ON tab6.id = tab1.ap_id
```
- **关联条件：** b_ap_pay.ap_id = b_ap.id
- **获取字段：** po_order_code, po_order_id, po_contract_code

#### 2.2 应付账款明细表 (b_ap_detail)
```sql
LEFT JOIN b_ap_detail tab7 ON tab7.ap_id = tab1.ap_id
```
- **用途：** 关联银行账户信息

#### 2.3 银行账户表 (m_bank_accounts)
```sql
LEFT JOIN m_bank_accounts tab8 ON tab8.id = tab7.bank_accounts_id
```
- **用途：** 获取银行账户详细信息

## 二、字典表的链接，获取的name

### 2.1 付款单状态字典
```sql
LEFT JOIN s_dict_data tab4 ON 
    tab4.CODE = 'DICT_B_AP_PAY_BILL_STATUS' 
    AND tab4.dict_value = tab1.status
```
- **获取字段：** tab4.label as status_name

### 2.2 应付账款类型字典
```sql
LEFT JOIN s_dict_data tab5 ON 
    tab5.CODE = 'DICT_B_AP_TYPE' 
    AND tab5.dict_value = tab1.type
```
- **获取字段：** tab5.label as type_name

## 三、ID获取name的链接和获取的内容

### 3.1 员工信息获取
```sql
LEFT JOIN m_staff tab2 ON tab2.id = tab1.c_id
LEFT JOIN m_staff tab3 ON tab3.id = tab1.u_id
```
- **获取字段：** tab2.NAME as c_name, tab3.NAME as u_name

---

# 入库计划 Mapper 数据库逻辑分析

## 一、主表从表的链接和链接关系

### 1. 主表：b_in_plan (入库计划表)
- **主键：** id
- **重要字段：** code, type, status, plan_time, owner_id, consignor_id

### 2. 从表关系

#### 2.1 入库计划明细表 (b_in_plan_detail)
```sql
-- 通常通过程序查询，不在主查询中聚合
-- 关联条件：b_in_plan.id = b_in_plan_detail.in_plan_id
```

#### 2.2 入库计划汇总表 (b_in_plan_total)
```sql
LEFT JOIN b_in_plan_total ON b_in_plan_total.in_plan_id = tab1.id
```
- **用途：** 获取计划的数量、重量、体积等汇总数据

#### 2.3 入库计划附件表 (b_in_plan_attach)
```sql
LEFT JOIN b_in_plan_attach ON b_in_plan_attach.in_plan_id = tab1.id
```
- **获取字段：** 附件信息

## 二、字典表的链接，获取的name

### 2.1 入库计划类型字典
```sql
LEFT JOIN s_dict_data ON 
    s_dict_data.code = 'b_in_plan_type' 
    AND s_dict_data.dict_value = tab1.type
```
- **字典类型：** b_in_plan_type
- **字典值：** 0-采购入库,1-调拨入库,2-退货入库,3-监管入库,4-普通入库,5-生产入库,6-提货入库,7-监管退货

### 2.2 入库计划状态字典
```sql
LEFT JOIN s_dict_data ON 
    s_dict_data.code = 'b_in_plan_status' 
    AND s_dict_data.dict_value = tab1.status
```

## 三、ID获取name的链接和获取的内容

### 3.1 货主信息获取
```sql
LEFT JOIN m_enterprise ON m_enterprise.id = tab1.owner_id
```
- **获取字段：** 货主企业名称

### 3.2 委托方信息获取
```sql
LEFT JOIN m_enterprise ON m_enterprise.id = tab1.consignor_id
```
- **获取字段：** 委托方企业名称

### 3.3 员工信息获取
```sql
LEFT JOIN m_staff ON m_staff.id = tab1.c_id
LEFT JOIN m_staff ON m_staff.id = tab1.u_id
```
- **获取字段：** 创建人和修改人姓名

## 四、特殊业务逻辑

### 4.1 ERP模式判断
- **字段：** is_erp_model
- **用途：** 判断是否为ERP模式的入库计划

### 4.2 超收比例控制
- **字段：** over_receipt_rate
- **用途：** 控制入库时的超收比例

### 4.3 与其他模块的关联
- **调拨单生成入库计划：** 通过b_allocate表关联
- **监管业务入库计划：** 通过b_schedule表关联
- **API接口入库计划：** 支持外部系统创建入库计划
