# Neo4j 知识图谱数据库结构说明

**更新时间**: 2025-10-15
**状态**: ✅ 已完成数据清空和索引优化

---

## 📊 执行结果

### ✅ 已完成操作

1. **数据清理**: 已清空所有Entity节点和RELATED_TO关系（当前节点数：0）
2. **索引删除**: 已删除所有旧索引
3. **索引创建**: 已创建7个优化索引
4. **状态验证**: 所有索引状态为ONLINE，populationPercent为100%

### 📋 索引清单

| 索引ID | 索引名称 | 类型 | 对象类型 | 属性 | 用途说明 |
|--------|----------|------|----------|------|----------|
| 3 | entity_tenant_idx | RANGE | NODE(Entity) | tenant_code | 租户隔离（最高优先级） |
| 4 | entity_tenant_kb_idx | RANGE | NODE(Entity) | tenant_code, kb_uuid | 租户+知识库组合查询 |
| 5 | entity_tenant_item_idx | RANGE | NODE(Entity) | tenant_code, kb_item_uuid | 租户+知识项组合查询（高频） |
| 6 | entity_name_fulltext | FULLTEXT | NODE(Entity) | entity_name | 实体名称中文模糊搜索 |
| 7 | entity_type_idx | RANGE | NODE(Entity) | entity_type | 按实体类型筛选 |
| 8 | relation_item_idx | RANGE | REL(RELATED_TO) | kb_item_uuid | 按文档项查询关系边 |
| 9 | relation_tenant_idx | RANGE | REL(RELATED_TO) | tenant_code | 关系级别租户隔离 |

---

## 🏗️ 数据结构定义

### 节点：Entity

**标签**: `Entity`

**属性列表**:

| 属性名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| `id` | Long | 是 | Neo4j内部ID（自动生成） | 1234567890 |
| `entity_uuid` | String | 是 | 业务UUID（32字符） | "scm_tenant_001::abc123" |
| `entity_name` | String | 是 | 实体名称 | "ABC供应商" |
| `entity_type` | String | 是 | 实体类型 | "supplier" |
| `entity_metadata` | String | 否 | 实体元数据（JSON） | '{"address":"上海"}' |
| `kb_uuid` | String | 是 | 所属知识库UUID | "scm_tenant_001::kb001" |
| `kb_item_uuid` | String | 是 | 所属知识项UUID ⭐新增 | "scm_tenant_001::item001" |
| `tenant_code` | String | 是 | 租户编码（数据源名称） | "scm_tenant_001" |
| `create_time` | DateTime | 是 | 创建时间 | datetime() |

**实体类型枚举** (`entity_type`):
- `supplier` - 供应商
- `customer` - 客户
- `product` - 产品
- `contract` - 合同
- `purchase_order` - 采购订单
- `sales_order` - 销售订单
- `warehouse` - 仓库
- `organization` - 组织
- `person` - 人员

**示例节点**:
```cypher
(:Entity {
  entity_uuid: "scm_tenant_001::abc123def456",
  entity_name: "ABC钢材供应商",
  entity_type: "supplier",
  entity_metadata: '{"address":"上海市浦东区","industry":"钢材批发","contact":"张三"}',
  kb_uuid: "scm_tenant_001::kb001",
  kb_item_uuid: "scm_tenant_001::item001",
  tenant_code: "scm_tenant_001",
  create_time: datetime("2025-10-15T10:00:00")
})
```

---

### 关系：RELATED_TO

**类型**: `RELATED_TO`
**方向**: 源实体 -> 目标实体

**属性列表**:

| 属性名 | 类型 | 必填 | 说明 | 示例 |
|--------|------|------|------|------|
| `relation_type` | String | 是 | 关系类型 | "supplies" |
| `strength` | Float | 是 | 关系强度（0.0-1.0） | 0.95 |
| `metadata` | String | 否 | 关系元数据（JSON） | '{"contract_no":"C001"}' |
| `kb_item_uuid` | String | 是 | 所属知识项UUID ⭐新增 | "scm_tenant_001::item001" |
| `tenant_code` | String | 是 | 租户编码 ⭐新增 | "scm_tenant_001" |
| `create_time` | DateTime | 是 | 创建时间 | datetime() |

**关系类型枚举** (`relation_type`):
- `supplies` - 供应关系（供应商→产品）
- `purchases` - 采购关系（客户→产品）
- `belongs_to` - 归属关系（产品→分类）
- `signed` - 签订关系（主体→合同）
- `stores` - 存储关系（仓库→产品）
- `manages` - 管理关系（人员→组织）
- `produces` - 生产关系（工厂→产品）

**示例关系**:
```cypher
(:Entity {entity_name: "ABC钢材供应商"})
  -[:RELATED_TO {
    relation_type: "supplies",
    strength: 0.95,
    metadata: '{"contract_id":"CT2025001","annual_volume":10000}',
    kb_item_uuid: "scm_tenant_001::item001",
    tenant_code: "scm_tenant_001",
    create_time: datetime("2025-10-15T10:00:00")
  }]->
(:Entity {entity_name: "Q235钢材"})
```

---

## 🔍 常用查询模式

### 1. 按租户查询所有实体

```cypher
MATCH (e:Entity {tenant_code: $tenant_code})
RETURN e
LIMIT 100;
```

**索引**: entity_tenant_idx

---

### 2. 按知识库查询实体

```cypher
MATCH (e:Entity {tenant_code: $tenant_code, kb_uuid: $kb_uuid})
RETURN e;
```

**索引**: entity_tenant_kb_idx（组合索引，性能最优）

---

### 3. 按文档项查询图谱节点（高频查询）

```cypher
MATCH (e:Entity {tenant_code: $tenant_code, kb_item_uuid: $kb_item_uuid})
RETURN e;
```

**索引**: entity_tenant_item_idx（组合索引）
**用途**: 展示单个文档的知识图谱

---

### 4. 实体名称模糊搜索

```cypher
CALL db.index.fulltext.queryNodes('entity_name_fulltext', '供应商')
YIELD node, score
WHERE node.tenant_code = $tenant_code
RETURN node, score
ORDER BY score DESC
LIMIT 10;
```

**索引**: entity_name_fulltext（全文索引，支持中文分词）

---

### 5. 查询实体的直接关系

```cypher
MATCH (e:Entity {entity_uuid: $entity_uuid, tenant_code: $tenant_code})
      -[r:RELATED_TO]->(target:Entity)
WHERE target.tenant_code = $tenant_code
RETURN target, r.relation_type, r.strength
ORDER BY r.strength DESC;
```

**索引**: entity_tenant_idx + relation_tenant_idx

---

### 6. 按文档项查询关系边

```cypher
MATCH (n:Entity)-[r:RELATED_TO]->(m:Entity)
WHERE r.kb_item_uuid = $item_uuid
  AND r.tenant_code = $tenant_code
RETURN id(r) as id, id(n) as sourceId, id(m) as targetId,
       r.relation_type as type, r.strength as strength;
```

**索引**: relation_item_idx + relation_tenant_idx
**用途**: 展示单个文档的关系网络

---

### 7. 按实体类型筛选

```cypher
MATCH (e:Entity {entity_type: 'supplier', tenant_code: $tenant_code})
RETURN e
LIMIT 50;
```

**索引**: entity_type_idx

---

### 8. 查询两个实体之间的最短路径

```cypher
MATCH (e1:Entity {entity_uuid: $entity1_uuid, tenant_code: $tenant_code}),
      (e2:Entity {entity_uuid: $entity2_uuid, tenant_code: $tenant_code}),
      path = shortestPath((e1)-[:RELATED_TO*1..5]-(e2))
RETURN path;
```

**用途**: 图谱推理，发现实体间的隐含关系

---

### 9. 查询N度相关实体

```cypher
MATCH (e:Entity {entity_uuid: $entity_uuid, tenant_code: $tenant_code})
      -[:RELATED_TO*1..3]->(related:Entity)
WHERE related.tenant_code = $tenant_code
RETURN DISTINCT related, length(shortestPath((e)-[:RELATED_TO*]-(related))) as distance
ORDER BY distance
LIMIT 20;
```

**用途**: 扩展搜索，发现间接相关实体

---

## ⚠️ 重要注意事项

### 🔴 租户隔离规范

**强制要求**：所有查询必须包含 `tenant_code` 条件！

**正确示例**:
```cypher
✅ MATCH (e:Entity {tenant_code: $tenant_code})
   WHERE e.entity_name CONTAINS $keyword

✅ MATCH (e:Entity {tenant_code: $tenant_code, kb_uuid: $kb_uuid})
```

**错误示例**:
```cypher
❌ MATCH (e:Entity)
   WHERE e.entity_name CONTAINS $keyword  // 缺少tenant_code，会泄露数据！
```

**安全建议**:
1. 所有Repository方法都必须传入 `tenant_code` 参数
2. Service层自动从 `DataSourceHelper.getCurrentDataSourceName()` 获取租户编码
3. 在Code Review时重点检查Neo4j查询是否包含租户过滤

---

### 🟡 字段命名规范

**统一使用 snake_case**（与MySQL一致）:

| Java属性 | Neo4j属性 | 说明 |
|----------|-----------|------|
| entityUuid | entity_uuid | 实体UUID |
| entityName | entity_name | 实体名称 |
| entityType | entity_type | 实体类型 |
| kbUuid | kb_uuid | 知识库UUID |
| kbItemUuid | kb_item_uuid | 知识项UUID |
| tenantCode | tenant_code | 租户编码 |
| relationType | relation_type | 关系类型 |

---

### 🟢 性能优化建议

1. **使用组合索引**: 优先使用 `entity_tenant_kb_idx` 和 `entity_tenant_item_idx`
2. **限制查询深度**: 路径查询不要超过5跳（`*1..5`）
3. **使用LIMIT**: 避免返回超过1000个节点
4. **全文搜索**: 中文模糊搜索使用 `db.index.fulltext.queryNodes` 而不是 `CONTAINS`
5. **批量操作**: 使用 `UNWIND` 批量创建节点和关系

---

## 📝 与aideepin的差异

| 维度 | aideepin | scm-ai |
|------|----------|--------|
| 图谱存储 | PostgreSQL + Apache AGE | Neo4j（原生图数据库） |
| 向量存储 | PostgreSQL + pgvector | Elasticsearch |
| 租户隔离 | 应用层user_id | 数据库级（tenant_code） |
| 索引方式 | 同步Service层 | RabbitMQ异步MQ |
| 关系类型 | RELATED_TO | RELATED_TO（已统一） |
| kb_item_uuid | 无 | 有（新增字段） |

---

## 🚀 后续待办事项

### Java代码同步更新（必须）

- [ ] 更新 `EntityNode` 实体类，添加 `kbItemUuid` 属性
- [ ] 更新 `RelatedToRelationship` 类，添加 `kbItemUuid` 和 `tenantCode` 属性
- [ ] 修改 `Neo4jQueryService` 查询，确认关系类型为 `RELATED_TO`
- [ ] 更新 `EntityRepository` 所有查询方法，确保包含 `tenant_code` 过滤
- [ ] 测试图谱索引服务，验证新索引是否生效

### 测试验证（推荐）

- [ ] 创建测试数据，验证索引性能
- [ ] 测试全文搜索功能（中文分词）
- [ ] 测试路径查询性能
- [ ] 压力测试多租户隔离

---

## 📞 联系方式

如有问题，请联系：SCM AI Team

**文档版本**: v1.0
**最后更新**: 2025-10-15
