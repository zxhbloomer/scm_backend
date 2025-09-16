# Repository Guidelines

## Project Structure & Modules
- Root `pom.xml` aggregates modules. Java 17 + Spring Boot 3.5.
- Runtime app: `scm-start` with entrypoint `com.xinyirun.scm.starter.SystemServerStart`.
- Source: `<module>/src/main/java`; resources: `<module>/src/main/resources` (e.g., `application.yml`, `application-dev.yml`).
- Key modules: `scm-core*` (domain/services), `scm-controller*` (REST), `scm-security` (auth/xss/jwt), `scm-mq*` (RabbitMQ), `scm-redis`, `scm-quartz`, `scm-ai`, `scm-excel`, `scm-framework`, `scm-start` (boot). SQL utilities in `sql/`.

## Build, Test, and Run
- Build all modules: `mvn -T 1C clean install -DskipTests`
- Run locally (dev profile): `mvn -pl scm-start -am spring-boot:run -Dspring-boot.run.profiles=dev`
- Package and run Jar:
  - `mvn -pl scm-start -am package -DskipTests`
  - `java -jar scm-start/target/scm-start-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev`
- Module‑scoped build/test: `mvn -pl <module> test` or `mvn -pl <module> install`.

## Coding Style & Naming
- Indentation: 4 spaces; no tabs. UTF‑8 files.
- Packages: lowercase; Classes/Enums: PascalCase; fields/methods: camelCase.
- Conventions: controllers `*Controller`, services `*Service`, mappers `*Mapper` (MyBatis), DTOs `*Request`/`*Response`, entities `*Entity`. Existing interfaces may use `I*` prefix (e.g., `IMessageSendService`).
- Lombok is used (`@Getter`, `@Setter`, `@Slf4j`); keep imports ordered and avoid unused code.

## Testing Guidelines
- This repo has limited automated tests. Add JUnit 5 tests under `<module>/src/test/java` named `*Test`.
- Run all tests: `mvn test`; per module: `mvn -pl <module> test`.
- Prefer service‑level tests; for REST, use `@SpringBootTest` with `-Dspring.profiles.active=test`. Aim for ≥70% coverage on new code.

## Commit & PR Guidelines
- Follow Conventional Commits seen in history: `feat:`, `fix:`, `refactor:`, `docs:`, `chore:` + concise, imperative subject. Scope optional; Chinese or English accepted.
- PRs must include: summary, affected modules, linked issues, test plan, and API examples/screenshots when applicable. Ensure CI/build passes and app runs with `dev` profile.

## Security & Config
- Configure MySQL, Redis, RabbitMQ, and MongoDB in `scm-start/src/main/resources/application-dev.yml`. Do not commit secrets; use env vars or local overrides.
- Sessions use Redis (`@EnableRedisHttpSession`); default TTL is 14400s—adjust via properties if needed.
