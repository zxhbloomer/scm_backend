# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

使用中文和我对话

## 常用命令

```bash
# 构建全部模块
mvn clean install

# 启动主应用（端口 8088，context-path /scm）
cd scm-start && mvn spring-boot:run

# 代码生成（根据数据库表生成 entity/mapper/xml）
cd 00autoCreateCode
mvn exec:java -Dexec.mainClass="CodeGenerator"
```

## 模块架构

分层模块结构，依赖方向：controller → core → bean/common

| 层次 | 模块 | 职责 |
|------|------|------|
| 数据模型 | `scm-bean` | Entity、DTO、VO |
| 公共基础 | `scm-common` | 注解、常量、工具类、异常 |
| 业务逻辑 | `scm-core`, `scm-core-api`, `scm-core-app`, `scm-core-bpm`, `scm-core-tenant` | Service + Mapper |
| 接口层 | `scm-controller`, `scm-controller-api`, `scm-controller-app`, `scm-controller-bpm`, `scm-controller-tenant` | REST Controller |
| 基础设施 | `scm-security`, `scm-redis`, `scm-excel`, `scm-quartz`, `scm-mq`, `scm-framework` | 横切关注点 |
| AI 模块 | `scm-ai` | Spring AI + MCP + 工作流 |
| 启动入口 | `scm-start` | 主应用，组件扫描配置 |

## 关键架构模式

### 多数据源 + 多租户
- `@DataSourceAnnotion("tenant")` 切换到租户数据源
- `DynamicDataSourceContextHolder` 管理数据源上下文
- 租户配置从 `s_tenant_manager` 表读取

### 自定义注解（scm-common/annotations）
- `@SysLogAnnotion` / `@OperationLogAnnotion` — 操作日志（AOP 自动记录）
- `@DataSourceAnnotion` — 动态数据源切换
- `@DataScopeAnnotion` — 数据权限过滤
- `@RepeatSubmitAnnotion` — 防重复提交
- `@LimitAnnotion` — 接口限流

### Controller 基类（scm-framework）
- `SystemBaseController` — 系统端（Session 认证），含文件上传/下载、Excel 处理
- `AppBaseController` — App 端（JWT 认证）
- 新建 Controller 必须继承对应基类

### MyBatis Plus 拦截器
- `MyBatisAutoFillHandel` — 自动填充 create_time/update_time/operator
- `DataChangeInterceptor` — 数据变更记录
- `DataScopeInterceptor` — 数据权限 SQL 注入

## 环境配置

**Profiles**: `dev`（默认）、`centos-ys`、`prod`、`zlprod`

**本地开发依赖**:
- MySQL: `127.0.0.1:3306` / `scm_tenant_20250519_001` / root/123456
- Redis: `127.0.0.1:6379` db=13
- RabbitMQ: `127.0.0.1:5672` / admin/123456

## 新增业务模块流程

1. `scm-bean` — 创建 Entity（`@TableName("b_xxx")`）和 VO/DTO
2. `scm-core` — 创建 Mapper 接口 + XML + Service 接口/实现
3. `scm-controller` — 继承 `SystemBaseController`，添加 `@RestController`
4. 或直接用代码生成器生成 1-2 步的骨架代码
