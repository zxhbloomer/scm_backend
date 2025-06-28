# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Supply Chain Management (SCM) backend system built with Spring Boot 3.1.4 and Java 17. The project follows a modular architecture with multiple Maven modules for different concerns (controllers, services, entities, security, etc.).

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
```

## Architecture Overview

### Module Structure
The project is organized into distinct Maven modules:

- **scm-bean**: Entity classes, DTOs, VOs, and data transfer objects
- **scm-common**: Shared utilities, constants, exceptions, and common configurations
- **scm-core**: Business logic layer with separate modules for API, App, BPM, and tenant operations
- **scm-controller**: REST controllers organized by concern (API, App, BPM, tenant)
- **scm-security**: Security configuration, JWT handling, and authentication
- **scm-redis**: Redis configuration and session management
- **scm-excel**: Excel import/export functionality
- **scm-quartz**: Job scheduling and task management
- **scm-mq**: Message queue (RabbitMQ) components
- **scm-framework**: Framework-level configurations and utilities
- **scm-start**: Main application starter modules

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

### Multi-Database Support
The application supports multiple databases through dynamic data source configuration:
- Primary MySQL database for main business data
- MongoDB for logging and document storage
- Redis for caching and session storage

### Entity Mapping
- Entities are located in `scm-bean/src/main/java/com/xinyirun/scm/bean/entity/`
- MyBatis mappers are in respective `core` modules under `mapper` packages
- Uses MyBatis Plus for enhanced CRUD operations

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

### Adding New Features
1. Create entities in `scm-bean` module
2. Add mappers and services in appropriate `scm-core-*` module
3. Implement controllers in corresponding `scm-controller-*` module
4. Update database schemas as needed

### Code Generation
Use the automated code generator in `00autoCreateCode` module for:
- Entity classes
- Mapper interfaces
- Basic CRUD operations
- Controller scaffolding

### Testing
- Tests are configured to be skipped by default in Maven
- Enable tests by removing `<skipTests>true</skipTests>` configuration

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

## Version Information
Current version: v1.0.39 (as configured in application.yml)