# Feature Specification: SCM-AI数据库操作标准化

**Feature Branch**: `004-scm-ai-d`
**Created**: 2025-09-30
**Status**: Draft
**Input**: User description: "需求，在scm-ai的代码D:\2025_project\20_project_in_github\00_scm_backend\scm_backend\scm-ai\src，找到所有mapper.java的代码，找到哪些全部insert into 和 update 的代码，注意关于插入、和更新都要使用bean操作，查询要使用sql。插入、和更新都要使用bean操作，查询要使用sql。插入操作，使用实体类，set数据，c_time、u_time、c_id、u_id、dbversion，这4个字段不需要，然后调用mapper.insert(实体类)操作，如果需要的到返回的数据，调用mapper.insert后，这个实体类就是数据。更新操作，由于需要使用bean的操作，不是使用sql来更新，所以需要先select出来数据，最好是使用mapper.selectById 得到整个实体类，然后更新实体类的数据（set方法），然后使用mapper.updateById来更新，你要找出所有关于更新的操作，然后按此方法逻辑来处理。列清单。具体你可以参考，scm-core中插入数据和更新数据的操作"

## Execution Flow (main)
```
1. Parse user description from Input
   → Identified: Mapper code refactoring for consistent database operations
2. Extract key concepts from description
   → Actors: Development team
   → Actions: Standardize INSERT/UPDATE operations to use entity beans
   → Data: Mapper files in scm-ai module
   → Constraints: Follow scm-core patterns, maintain query operations as SQL
3. For each unclear aspect:
   → Performance impact assessment not specified
   → Rollback strategy not defined
4. Fill User Scenarios & Testing section
   → Focus on developer experience and code consistency
5. Generate Functional Requirements
   → Each requirement covers specific operation patterns
6. Identify Key Entities
   → Mapper files, Entity classes, Database operations
7. Run Review Checklist
   → WARN "Spec has uncertainties on performance impact"
8. Return: SUCCESS (spec ready for planning)
```

---

## ⚡ Quick Guidelines
- ✅ Focus on WHAT users need and WHY
- ❌ Avoid HOW to implement (no tech stack, APIs, code structure)
- 👥 Written for business stakeholders, not developers

### Section Requirements
- **Mandatory sections**: Must be completed for every feature
- **Optional sections**: Include only when relevant to the feature
- When a section doesn't apply, remove it entirely (don't leave as "N/A")

### For AI Generation
When creating this spec from a user prompt:
1. **Mark all ambiguities**: Use [NEEDS CLARIFICATION: specific question] for any assumption you'd need to make
2. **Don't guess**: If the prompt doesn't specify something (e.g., "login system" without auth method), mark it
3. **Think like a tester**: Every vague requirement should fail the "testable and unambiguous" checklist item
4. **Common underspecified areas**:
   - User types and permissions
   - Data retention/deletion policies  
   - Performance targets and scale
   - Error handling behaviors
   - Integration requirements
   - Security/compliance needs

---

## User Scenarios & Testing *(mandatory)*

### Primary User Story
作为开发团队，我们需要标准化scm-ai模块中的数据库操作方式，确保所有插入和更新操作使用统一的实体bean操作模式，而查询操作保持SQL方式，以提高代码一致性和维护性。

### Acceptance Scenarios
1. **Given** scm-ai模块中存在不一致的数据库操作方式, **When** 完成标准化重构, **Then** 所有INSERT操作都使用mapper.insert(entity)方式
2. **Given** scm-ai模块中存在直接SQL更新操作, **When** 完成标准化重构, **Then** 所有UPDATE操作都使用selectById+updateById模式
3. **Given** 开发人员需要新增数据库操作, **When** 参考现有代码, **Then** 能够遵循统一的操作模式
4. **Given** 代码审查过程, **When** 检查数据库操作代码, **Then** 所有操作都符合标准化要求

### Edge Cases
- 批量操作场景如何处理？
- 事务边界内的操作如何确保一致性？
- 自动填充字段(c_time、u_time等)的处理逻辑是否正确？

## Requirements *(mandatory)*

### Functional Requirements
- **FR-001**: 系统MUST识别scm-ai模块中所有使用@Insert和@Update注解的Mapper方法
- **FR-002**: 系统MUST列出所有需要重构的INSERT INTO和UPDATE SQL操作
- **FR-003**: 插入操作MUST使用实体类设置数据，并调用mapper.insert(entity)方法
- **FR-004**: 插入操作MUST自动处理c_time、u_time、c_id、u_id、dbversion字段，开发者无需手动设置
- **FR-005**: 更新操作MUST先使用mapper.selectById获取完整实体类
- **FR-006**: 更新操作MUST通过实体类的set方法更新数据，然后使用mapper.updateById提交
- **FR-007**: 查询操作MUST保持使用SQL方式，不进行修改
- **FR-008**: 重构后的代码MUST参考scm-core模块中的标准实现模式
- **FR-009**: 系统MUST提供完整的需要修改的操作清单

### 性能和质量要求
- **FR-010**: 重构后的操作性能MUST不低于原有实现 [NEEDS CLARIFICATION: 性能基准测试标准未明确]
- **FR-011**: 重构过程MUST确保数据一致性和事务完整性

### Key Entities *(include if feature involves data)*
- **Mapper文件**: 包含数据库操作接口的Java文件，位于scm-ai/src目录下
- **实体类**: 与数据库表对应的Java Bean对象，包含自动管理字段
- **数据库操作**: INSERT、UPDATE、SELECT操作，需要遵循不同的实现模式

---

## Review & Acceptance Checklist
*GATE: Automated checks run during main() execution*

### Content Quality
- [x] No implementation details (languages, frameworks, APIs)
- [x] Focused on standardization value and code quality needs
- [x] Written for stakeholders understanding development standards
- [x] All mandatory sections completed

### Requirement Completeness
- [ ] No [NEEDS CLARIFICATION] markers remain
- [x] Requirements are testable and unambiguous
- [x] Success criteria are measurable
- [x] Scope is clearly bounded (scm-ai module only)
- [x] Dependencies identified (scm-core as reference)

---

## Execution Status
*Updated by main() during processing*

- [x] User description parsed
- [x] Key concepts extracted
- [x] Ambiguities marked
- [x] User scenarios defined
- [x] Requirements generated
- [x] Entities identified
- [ ] Review checklist passed (pending clarification on performance criteria)

---
