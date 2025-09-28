<!--
Sync Impact Report:
Version change: N/A → 1.0.0 (Initial constitution)
Modified principles: N/A (New constitution)
Added sections: All sections (Initial constitution)
Removed sections: N/A
Templates requiring updates:
  ✅ Updated: plan-template.md (references constitution checks)
  ✅ Updated: spec-template.md (aligns with requirements standards)
  ✅ Updated: tasks-template.md (aligns with task categorization)
  ✅ Updated: agent-file-template.md (references project guidelines)
Follow-up TODOs: None
-->

# SCM Backend Constitution

## Core Principles

### I. Modular Architecture
Every component MUST follow the Maven multi-module structure with clear separation of concerns:
- Core data layer (scm-bean, scm-common)
- Business logic layer (scm-core with sub-modules)
- Presentation layer (scm-controller by concern)
- Infrastructure layer (security, redis, excel, etc.)

Rationale: Enables independent development, testing, and deployment of business modules while maintaining enterprise-scale organization and maintainability.

### II. Multi-Tenant Data Isolation (NON-NEGOTIABLE)
All data access MUST implement tenant isolation through application-level routing:
- Use @DataSourceAnnotation for tenant context switching
- Dynamic data source routing via DynamicDataSourceContextHolder
- Each tenant has isolated data while sharing application instance

Rationale: Critical for enterprise SCM serving multiple clients with strict data privacy requirements.

### III. Enterprise Security
Security MUST be implemented as cross-cutting concerns:
- JWT-based stateless authentication for all API endpoints
- Redis session management with 4-hour timeout
- Comprehensive audit logging to MongoDB for all business operations
- External file service integration with authenticated uploads

Rationale: Enterprise SCM systems handle sensitive business data requiring robust security and audit trails.

### IV. Business Process Integration
Business workflows MUST be externalized through Flowable BPM:
- Process templates and definitions for approval workflows
- Task management with form handling
- Process instance tracking and monitoring

Rationale: Supply chain operations require complex approval processes that must be configurable and auditable.

### V. Code Generation First
Code consistency MUST be achieved through automated generation:
- Use CodeGenerator for entity, mapper, and XML file creation
- Follow established patterns for business module structure
- Maintain naming conventions and package organization

Rationale: Ensures consistency across modules and reduces human error in repetitive code patterns.

## Development Standards

### Technology Stack Requirements
- Java 17 with Spring Boot 3.1.4 as application framework
- MyBatis Plus 3.5.12 for database operations with XML mappers
- MySQL as primary database with Redis caching and MongoDB logging
- Maven multi-module architecture with clear dependency management
- Flowable 7.1.0 for business process management

### Database Design Standards
- Entity classes in scm-bean module with @TableName annotations
- MyBatis mappers in scm-core module with corresponding XML files
- Tenant SQL configuration for multi-tenant data isolation
- Druid connection pooling with performance monitoring
- MongoDB for audit logs and operational data storage

## Quality Assurance

### Testing Requirements
- Tests skipped by default but MUST be enabled for critical modules
- H2 in-memory database for unit tests when enabled
- Integration tests for multi-tenant data isolation
- Contract tests for external API integrations

### Code Quality Standards
- Follow established package structure: com.xinyirun.scm.*
- Use @SysLogAnnotation for audit logging of business operations
- Implement proper error handling with request/response logging
- Maintain backwards compatibility in API changes

## Governance

Constitution supersedes all other development practices. All feature development and code changes must verify compliance with these principles. Complex architectural decisions that deviate from these principles require explicit justification and approval process.

Version control and amendments follow semantic versioning:
- MAJOR: Backward incompatible governance or principle changes
- MINOR: New principles or materially expanded guidance
- PATCH: Clarifications, wording fixes, and non-semantic refinements

**Version**: 1.0.0 | **Ratified**: 2025-09-28 | **Last Amended**: 2025-09-28