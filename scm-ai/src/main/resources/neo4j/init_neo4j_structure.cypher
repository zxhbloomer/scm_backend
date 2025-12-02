// ================================================================
// SCM AI 知识库 Neo4j 图谱数据库结构初始化脚本
// ================================================================
// 创建时间: 2025-10-15
// 用途: 清空现有数据并创建优化后的节点、关系、索引结构
// 执行方式: 在Neo4j Browser或通过Driver执行
// ================================================================

// ================================================================
// 第一步：清空现有数据
// ================================================================
// 警告：此操作会删除所有Entity节点及其关系，请谨慎执行！
// ================================================================

// 1.1 删除所有Entity节点及其关系（DETACH DELETE会级联删除关系）
MATCH (n:Entity)
DETACH DELETE n;

// 1.2 验证删除结果（可选）
// MATCH (n:Entity) RETURN count(n) as entity_count;


// ================================================================
// 第二步：删除旧索引（避免冲突）
// ================================================================

// 2.1 删除可能存在的旧索引
DROP INDEX entity_tenant_idx IF EXISTS;
DROP INDEX entity_tenant_kb_idx IF EXISTS;
DROP INDEX entity_tenant_item_idx IF EXISTS;
DROP INDEX entity_name_fulltext IF EXISTS;
DROP INDEX entity_type_idx IF EXISTS;
DROP INDEX relation_item_idx IF EXISTS;
DROP INDEX relation_tenant_idx IF EXISTS;


// ================================================================
// 第三步：创建优化后的索引结构
// ================================================================

// ----------------------------------------------------------------
// 3.1 租户隔离索引（最高优先级）
// ----------------------------------------------------------------
// 用途: 所有查询都需要按tenant_code过滤，此索引保证多租户数据隔离性能
// 查询场景: WHERE e.tenant_code = $tenant_code
CREATE INDEX entity_tenant_idx IF NOT EXISTS
FOR (n:Entity) ON (n.tenant_code);

// ----------------------------------------------------------------
// 3.2 租户+知识库组合索引
// ----------------------------------------------------------------
// 用途: 查询特定租户下某个知识库的所有实体
// 查询场景: WHERE e.tenant_code = $tenant_code AND e.kb_uuid = $kb_uuid
// 性能: 组合索引比单列索引更高效，避免两次索引查找
CREATE INDEX entity_tenant_kb_idx IF NOT EXISTS
FOR (n:Entity) ON (n.tenant_code, n.kb_uuid);

// ----------------------------------------------------------------
// 3.3 租户+知识项组合索引
// ----------------------------------------------------------------
// 用途: 按文档项查询图谱节点（Neo4jQueryService.queryVertices使用）
// 查询场景: WHERE e.tenant_code = $tenant_code AND e.kb_item_uuid = $kb_item_uuid
// 重要性: 高频查询，用于展示单个文档的知识图谱
CREATE INDEX entity_tenant_item_idx IF NOT EXISTS
FOR (n:Entity) ON (n.tenant_code, n.kb_item_uuid);

// ----------------------------------------------------------------
// 3.4 实体名称全文索引
// ----------------------------------------------------------------
// 用途: 实体名称模糊搜索优化（GraphRetrievalService.searchByKeyword使用）
// 查询场景: WHERE e.entity_name CONTAINS $keyword
// 注意: FULLTEXT索引支持中文分词，比CONTAINS性能更好
CREATE FULLTEXT INDEX entity_name_fulltext IF NOT EXISTS
FOR (n:Entity) ON EACH [n.entity_name];

// ----------------------------------------------------------------
// 3.5 实体类型索引
// ----------------------------------------------------------------
// 用途: 按实体类型筛选（如supplier、product、contract等）
// 查询场景: WHERE e.entity_type = $entity_type
CREATE INDEX entity_type_idx IF NOT EXISTS
FOR (n:Entity) ON (n.entity_type);

// ----------------------------------------------------------------
// 3.6 关系的知识项索引
// ----------------------------------------------------------------
// 用途: 按文档项查询关系边（Neo4jQueryService.queryEdges使用）
// 查询场景: WHERE r.kb_item_uuid = $itemUuid
// 重要性: 高频查询，用于展示单个文档的关系网络
CREATE INDEX relation_item_idx IF NOT EXISTS
FOR ()-[r:RELATED_TO]-() ON (r.kb_item_uuid);

// ----------------------------------------------------------------
// 3.7 关系的租户索引
// ----------------------------------------------------------------
// 用途: 关系级别的租户隔离（新增字段，增强安全性）
// 查询场景: WHERE r.tenant_code = $tenant_code
CREATE INDEX relation_tenant_idx IF NOT EXISTS
FOR ()-[r:RELATED_TO]-() ON (r.tenant_code);


// ================================================================
// 第四步：创建唯一性约束（可选，根据业务需求决定）
// ================================================================

// ----------------------------------------------------------------
// 4.1 实体UUID唯一性约束（租户内唯一）
// ----------------------------------------------------------------
// 用途: 确保同一租户内entity_uuid不重复
// 注意: Neo4j的唯一约束会自动创建索引
// CREATE CONSTRAINT entity_uuid_unique IF NOT EXISTS
// FOR (n:Entity) REQUIRE (n.tenant_code, n.entity_uuid) IS UNIQUE;


// ================================================================
// 第五步：节点和关系结构说明（仅文档，不执行）
// ================================================================

// ----------------------------------------------------------------
// 节点：Entity
// ----------------------------------------------------------------
// 标签: Entity
// 属性说明:
// - id                 : Neo4j内部ID（Long，自动生成）
// - entity_uuid        : 业务UUID（String，32字符，格式：{tenantCode}::{uuid}）
// - entity_name        : 实体名称（String，如"ABC供应商"）
// - entity_type        : 实体类型（String，如supplier/product/contract/purchase_order）
// - entity_metadata    : 实体元数据（String，JSON格式）
// - kb_uuid            : 所属知识库UUID（String，格式：{tenantCode}::{uuid}）
// - kb_item_uuid       : 所属知识项UUID（String，格式：{tenantCode}::{uuid}）⭐新增
// - tenant_code        : 租户编码（String，数据源名称，如"scm_tenant_001"）
// - create_time        : 创建时间（DateTime）
//
// 设计说明:
// 1. entity_uuid格式包含tenantCode前缀，便于跨系统追溯
// 2. kb_item_uuid用于按文档查询图谱，必须字段
// 3. tenant_code用于多租户数据隔离，所有查询必须带此条件
// 4. entity_metadata存储额外的JSON数据，如实体属性、标签等

// ----------------------------------------------------------------
// 关系：RELATED_TO
// ----------------------------------------------------------------
// 类型: RELATED_TO
// 方向: 源实体 -> 目标实体
// 属性说明:
// - relation_type      : 关系类型（String，如supplies/belongs_to/signed/purchased）
// - strength           : 关系强度（Float，范围0.0-1.0，用于相关性排序）
// - metadata           : 关系元数据（String，JSON格式）
// - kb_item_uuid       : 所属知识项UUID（String）⭐新增
// - tenant_code        : 租户编码（String）⭐新增
// - create_time        : 创建时间（DateTime）
//
// 设计说明:
// 1. relation_type定义实体间的语义关系
// 2. strength用于RAG检索时的相关性评分
// 3. kb_item_uuid标记关系来源的文档，用于按文档查询关系边
// 4. tenant_code实现关系级别的租户隔离，双重保障数据安全

// ----------------------------------------------------------------
// 示例数据结构
// ----------------------------------------------------------------
// 实体示例:
// (:Entity {
//   entity_uuid: "scm_tenant_001::abc123def456",
//   entity_name: "ABC供应商",
//   entity_type: "supplier",
//   entity_metadata: '{"address":"上海市","contact":"张三"}',
//   kb_uuid: "scm_tenant_001::kb001",
//   kb_item_uuid: "scm_tenant_001::item001",
//   tenant_code: "scm_tenant_001",
//   create_time: datetime("2025-10-15T10:00:00")
// })
//
// 关系示例:
// (:Entity {entity_name: "ABC供应商"})
//   -[:RELATED_TO {
//     relation_type: "supplies",
//     strength: 0.95,
//     metadata: '{"contract_no":"CT2025001"}',
//     kb_item_uuid: "scm_tenant_001::item001",
//     tenant_code: "scm_tenant_001",
//     create_time: datetime("2025-10-15T10:00:00")
//   }]->
// (:Entity {entity_name: "钢材产品"})


// ================================================================
// 第六步：验证索引创建结果
// ================================================================

// 6.1 查看所有索引
SHOW INDEXES;

// 6.2 查看所有约束
SHOW CONSTRAINTS;

// 6.3 验证数据为空
MATCH (n:Entity) RETURN count(n) as entity_count;


// ================================================================
// 执行完成说明
// ================================================================
// 1. 已清空所有Entity节点和RELATED_TO关系
// 2. 已创建7个性能索引（覆盖高频查询场景）
// 3. 节点和关系结构已按优化方案设计
// 4. tenant_code、kb_item_uuid等关键字段已补充
// 5. 关系类型统一为RELATED_TO
//
// 下一步操作:
// 1. 更新Java实体类EntityNode，添加kb_item_uuid属性
// 2. 更新Java实体类RelatedToRelationship，添加kb_item_uuid、tenant_code属性
// 3. 修改Neo4jQueryService查询，将RELATION改为RELATED_TO
// 4. 测试图谱索引服务，验证查询是否正常
// ================================================================
