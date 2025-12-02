// ========================================
// Neo4j 索引创建脚本
// 用于知识库知识图谱性能优化
// ========================================

// 1. KnowledgeBaseSegment 节点索引
// 为 segment_uuid 创建唯一索引（主键）
CREATE CONSTRAINT kb_segment_uuid_unique IF NOT EXISTS
FOR (n:KnowledgeBaseSegment)
REQUIRE n.segment_uuid IS UNIQUE;

// 为 tenant_id 创建索引（多租户数据隔离）
CREATE INDEX kb_segment_tenant_id IF NOT EXISTS
FOR (n:KnowledgeBaseSegment)
ON (n.tenant_id);

// 为 kb_uuid 创建索引（按知识库查询）
CREATE INDEX kb_segment_kb_uuid IF NOT EXISTS
FOR (n:KnowledgeBaseSegment)
ON (n.kb_uuid);

// 为 item_uuid 创建索引（按文档项查询）
CREATE INDEX kb_segment_item_uuid IF NOT EXISTS
FOR (n:KnowledgeBaseSegment)
ON (n.item_uuid);

// 2. Entity 节点索引
// 为 entity_uuid 创建唯一索引（主键）
CREATE CONSTRAINT entity_uuid_unique IF NOT EXISTS
FOR (n:Entity)
REQUIRE n.entity_uuid IS UNIQUE;

// 为 tenant_id 创建索引（多租户数据隔离）
CREATE INDEX entity_tenant_id IF NOT EXISTS
FOR (n:Entity)
ON (n.tenant_id);

// 为 entity_name 创建索引（按实体名称查询）
CREATE INDEX entity_name IF NOT EXISTS
FOR (n:Entity)
ON (n.entity_name);

// 为 entity_type 创建索引（按实体类型查询）
CREATE INDEX entity_type IF NOT EXISTS
FOR (n:Entity)
ON (n.entity_type);

// 3. 组合索引 - 用于复杂查询优化
// tenant_id + kb_uuid 组合索引（多租户知识库查询）
CREATE INDEX kb_segment_tenant_kb IF NOT EXISTS
FOR (n:KnowledgeBaseSegment)
ON (n.tenant_id, n.kb_uuid);

// tenant_id + entity_type 组合索引（多租户实体类型查询）
CREATE INDEX entity_tenant_type IF NOT EXISTS
FOR (n:Entity)
ON (n.tenant_id, n.entity_type);

// ========================================
// 验证索引创建
// ========================================
// 使用以下命令验证索引是否创建成功：
// SHOW INDEXES;
// SHOW CONSTRAINTS;
