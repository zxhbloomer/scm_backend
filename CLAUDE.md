# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

使用中文和我对话

## Project Overview

This is a Supply Chain Management (SCM) backend system built with Spring Boot 3.1.4 and Java 17. The project follows a modular Maven architecture designed for enterprise-scale supply chain operations, including purchase orders, inventory management, financial settlements, and business process workflows.

## Build and Development Commands

### Building the Project
```bash
# Build all modules
mvn clean install

# Build without tests (tests are skipped by default in configuration)
mvn clean package

# Build specific module
cd scm-bean && mvn clean install
```

### Running the Application
```bash
# Run main application
cd scm-start
mvn spring-boot:run

# Run BPM-specific application
cd scm-start-parent/scm-bpm-start
mvn spring-boot:run
```

### Code Generation
```bash
# Generate code using the auto-code generator
cd 00autoCreateCode
mvn exec:java -Dexec.mainClass="CodeGenerator"

# The generator creates:
# - Entity classes in scm-bean/src/main/java/com/xinyirun/scm/bean/entity/
# - MyBatis mappers in scm-core/src/main/java/com/xinyirun/scm/core/system/mapper/
# - XML mapping files in scm-core/src/main/resources/mapper/
```

### Development Workflow
```bash
# 1. Start development environment
cd scm-start
mvn spring-boot:run

# 2. Application runs on http://localhost:8088/scm
# 3. Check application health: http://localhost:8088/scm/actuator/health
# 4. Default profile: dev (connects to local MySQL/Redis/MongoDB)
```

## Architecture Overview

### Module Structure and Dependencies
The project follows a layered modular architecture with clear separation of concerns:

**Core Data Layer**:
- **scm-bean**: Entity classes, DTOs, VOs - Used by all other modules
- **scm-common**: Shared utilities, constants, exceptions - Foundation for all modules

**Business Logic Layer**:
- **scm-core**: Main business logic with sub-modules:
  - `scm-core-api`: API-specific business logic
  - `scm-core-app`: Application business logic  
  - `scm-core-bpm`: Business Process Management logic
  - `scm-core-tenant`: Multi-tenant specific logic
  - `scm-core-mongodb`: MongoDB operations and logging

**Presentation Layer**:
- **scm-controller**: REST controllers organized by concern:
  - `scm-controller-api`: External API endpoints
  - `scm-controller-app`: Web application endpoints
  - `scm-controller-bpm`: BPM workflow endpoints
  - `scm-controller-tenant`: Tenant management endpoints

**Infrastructure Layer**:
- **scm-security**: JWT authentication, Spring Security configuration
- **scm-redis**: Session management, caching
- **scm-excel**: Excel import/export with Jxls templates
- **scm-quartz**: Scheduled jobs and batch processing
- **scm-mq**: RabbitMQ message handling
- **scm-framework**: Cross-cutting concerns and utilities

**Application Starter**:
- **scm-start**: Main application entry point with component scanning

### Key Technologies
- **Spring Boot 3.1.4** with Java 17
- **MyBatis Plus 3.5.12** for database operations
- **Spring Security 6.1.1** for authentication/authorization
- **Redis** for session management and caching
- **MongoDB** for document storage and logging
- **RabbitMQ** for message queuing
- **Flowable 7.1.0** for BPM (Business Process Management)
- **JWT** for token-based authentication
- **Druid** for database connection pooling

### Package Structure
Base package: `com.xinyirun.scm`

Key packages:
- `com.xinyirun.scm.bean` - Data models and entities
- `com.xinyirun.scm.core` - Business services and mappers
- `com.xinyirun.scm.controller` - REST endpoints
- `com.xinyirun.scm.common` - Shared utilities and configurations
- `com.xinyirun.scm.security` - Security components

## Database and Data Access

### Multi-Database Architecture
The system uses a sophisticated multi-database setup with dynamic data source routing:

**Primary MySQL Database**:
- Development: `127.0.0.1:3306/scm_tenant_20250519_001`
- Production configurations available for different environments
- Connection pooling via Druid with performance monitoring
- Tenant-based data isolation through application-level routing

**MongoDB** (Document Storage):
- Host: `127.0.0.1:27017`
- Database: `wms`
- Used for audit logs, operational data, and document storage
- Configured in `scm-core-mongodb` module

**Redis** (Caching & Sessions):
- Host: `127.0.0.1:6379`, Database: 13
- Session namespace: `XINYIRUN_SCM_SESSION_REDIS_KEY`
- 4-hour session timeout, connection pooling with Lettuce

### Multi-Tenant Data Architecture
The system implements application-level multi-tenancy:
- Tenant context managed through `@DataSourceAnnotion` 
- Dynamic data source switching via `DynamicDataSourceContextHolder`
- Tenant SQL configuration: queries `s_tenant_manager` table for active tenants
- Each tenant has isolated data while sharing the same application instance

### Entity and Mapper Organization
```
scm-bean/entity/
├── business/        # Business entities (orders, inventory, etc.)
├── master/         # Master data (customers, goods, organizations)
├── bpm/           # Business Process Management entities  
├── log/           # Logging and audit entities
└── sys/           # System configuration entities

scm-core/system/mapper/
├── business/      # Business logic mappers
├── master/       # Master data mappers  
├── log/          # Logging mappers
└── sys/          # System mappers
```

## Security and Authentication

### JWT Authentication
- JWT tokens are used for stateless authentication
- Base64 secret configured in application properties
- Token validation and parsing utilities in `JwtUtil` class

### Session Management
- Redis-based session storage with 4-hour timeout
- Session namespace: `{spring}:{session}`

### File Upload Security
External file service integration:
- File server: `file.xinyirunscm.com`
- Authenticated uploads with app_key/secret_key
- Returned URLs stored in business entities

## Business Process Management (BPM)

The system includes comprehensive BPM capabilities using Flowable:
- Process templates and definitions
- Task management and approval workflows
- Form handling and field metadata
- Process instance tracking

## Monitoring and Logging

### Application Monitoring
- Actuator endpoints enabled for health checks
- Prometheus metrics integration
- Custom meter registry configuration

### Logging Configuration
- Logback configuration in `logback-spring.xml`
- MongoDB-based log storage for audit trails
- Request/response logging for API calls

## Development Guidelines

### Adding New Business Modules
When implementing new supply chain features (e.g., new order types, inventory operations):

1. **Entity Creation** (scm-bean):
   ```java
   // Create in scm-bean/src/main/java/com/xinyirun/scm/bean/entity/business/
   @TableName("b_your_table")
   public class YourEntity extends BaseEntity {
       // Use @TableField for custom field mapping
   }
   ```

2. **Mapper Implementation** (scm-core):
   ```java
   // Create in scm-core/src/main/java/com/xinyirun/scm/core/system/mapper/business/
   @Mapper
   public interface YourMapper extends BaseMapper<YourEntity> {
       // Custom queries with @Select, @Update annotations
   }
   ```

3. **Service Layer** (scm-core):
   ```java
   // Service interface and implementation
   // Implement business logic, data validation, tenant context handling
   ```

4. **Controller Implementation** (scm-controller):
   ```java
   // Create REST endpoints with proper error handling and logging
   @RestController
   @RequestMapping("/api/v1/your-module")
   ```

### Code Generation Workflow
The automated generator (CodeGenerator.java) streamlines development:
```bash
cd 00autoCreateCode
mvn exec:java -Dexec.mainClass="CodeGenerator"
# Enter module name when prompted (e.g., "purchase", "inventory")
# Generator connects to: jdbc:mysql://127.0.0.1:3306/scm_tenant_20250519_001
```

### Common Development Patterns

**Multi-Tenant Data Access**:
```java
@DataSourceAnnotion("tenant") // Switch to tenant-specific data source
public class YourService {
    // All operations in this class use tenant data source
}
```

**Business Process Integration**:
```java
// BPM workflow integration
@Autowired
private FlowableProcessEngine processEngine;
// Start process instances, handle approvals
```

**Logging and Audit**:
```java
@SysLogAnnotion(operType = "CREATE", module = "YOUR_MODULE")
public void createRecord() {
    // Automatic logging to MongoDB
}
```

### Testing Configuration
- Tests skipped by default: `<skipTests>true</skipTests>` in pom.xml
- Enable for development: Remove skipTests or run `mvn test -DskipTests=false`
- Database connections use H2 in-memory for unit tests (when enabled)

## Configuration Files

### Main Configuration
- `application.yml` - Main configuration with profile selection
- `application-dev.yml` - Development environment settings
- `mybatis.xml` - MyBatis configuration

### Profiles
Available profiles:
- `dev` - Development (default)
- `centos-ys` - Demo environment
- `prod` - Production
- `zlprod` - Special production environment

## Special Considerations

### Multi-Tenancy
The system supports multi-tenant architecture with:
- Tenant-specific controllers and services
- Data isolation at the application level
- Tenant context management

### Message Queue Integration
RabbitMQ integration for:
- Asynchronous processing
- Event-driven architecture
- System integration messaging

### Excel Processing
Comprehensive Excel handling:
- Template-based exports using Jxls
- Dynamic Excel generation
- Import validation and processing

## Environment Configuration

### Local Development Setup
1. **Database Requirements**:
   ```bash
   # MySQL (Primary Database)
   Host: 127.0.0.1:3306
   Database: scm_tenant_20250519_001
   Username: root
   Password: 123456
   
   # Redis (Session & Cache)
   Host: 127.0.0.1:6379
   Database: 13
   
   # MongoDB (Logging)
   Host: 127.0.0.1:27017
   Database: wms
   Username: wms_db_user
   Password: WmsMongodb%40Shanghai123
   
   # RabbitMQ (Message Queue)
   Host: 127.0.0.1:5672
   Username: admin
   Password: 123456
   ```

2. **Application Profiles**:
   - `dev`: Local development (default)
   - `centos-ys`: Demo environment  
   - `prod`: Production environment
   - `zlprod`: Special production environment

### Key Application Endpoints
- Main Application: `http://localhost:8088/scm`
- Health Check: `http://localhost:8088/scm/actuator/health`
- Metrics: `http://localhost:8088/scm/actuator/prometheus`

### Troubleshooting Common Issues

**Database Connection Issues**:
- Verify MySQL is running and accessible
- Check tenant configuration in `s_tenant_manager` table
- Validate Druid connection pool settings

**Multi-Tenant Problems**:
- Ensure `@DataSourceAnnotion` is properly configured
- Check tenant context in `DynamicDataSourceContextHolder`
- Verify tenant_sql query returns active tenants

**Session/Authentication Issues**:
- Confirm Redis is running and accessible
- Check JWT secret configuration: `scm.security.jwt.base64-secret`
- Verify session namespace: `XINYIRUN_SCM_SESSION_REDIS_KEY`

**BPM Workflow Issues**:
- Check Flowable database tables are created
- Verify process definitions are deployed
- Ensure workflow database type is set to 'mysql'

## Version Information
Current version: v1.0.39 (as configured in application.yml)

## Database Connection (MCP)
```
Host: 127.0.0.1
Port: 3306
Database: scm_tenant_20250519_001
Username: root
Password: 123456
```
