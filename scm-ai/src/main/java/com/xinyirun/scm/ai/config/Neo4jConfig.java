package com.xinyirun.scm.ai.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.neo4j.repository.config.EnableNeo4jRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Neo4j 配置类
 * 用于知识图谱存储和关系查询
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Configuration
@EnableNeo4jRepositories(basePackages = "com.xinyirun.scm.ai.core.repository.neo4j")
@EnableTransactionManagement
public class Neo4jConfig {

    /**
     * Neo4j 配置通过 application-dev.yml 自动加载
     * 包括：
     * - spring.neo4j.uri: bolt://127.0.0.1:7687
     * - spring.neo4j.authentication.username: neo4j
     * - spring.neo4j.authentication.password: 123456
     * - spring.neo4j.connection-timeout: 30s
     * - spring.neo4j.max-connection-pool-size: 50
     */
}
