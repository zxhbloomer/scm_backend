package com.xinyirun.scm.ai.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.lang.NonNull;

import java.time.Duration;

/**
 * Elasticsearch 配置类
 * 用于知识库向量存储和文本检索
 *
 * <p>说明：继承 ElasticsearchConfiguration 后，Spring 会自动创建以下 beans：</p>
 * <ul>
 *   <li>elasticsearchClient - Elasticsearch 客户端</li>
 *   <li>elasticsearchConverter - 对象转换器</li>
 *   <li>elasticsearchOperations - ElasticsearchOperations 接口实现（实际类型为 ElasticsearchTemplate）</li>
 * </ul>
 *
 * <p>重要：根据Spring Data Elasticsearch官方文档，推荐使用 ElasticsearchOperations 接口而不是 ElasticsearchTemplate 具体类。
 * 父类自动提供的 elasticsearchOperations bean 实际类型就是 ElasticsearchTemplate，因此注入 ElasticsearchOperations 接口即可。</p>
 *
 * @author SCM AI Team
 * @since 2025-10-02
 */
@Configuration
@EnableElasticsearchRepositories(basePackages = "com.xinyirun.scm.ai.core.repository.elasticsearch")
public class ElasticsearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris:http://127.0.0.1:19200}")
    private String elasticsearchUri;

    @Value("${spring.elasticsearch.connection-timeout:1s}")
    private Duration connectionTimeout;

    @Value("${spring.elasticsearch.socket-timeout:30s}")
    private Duration socketTimeout;

    @NonNull
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticsearchUri.replace("http://", "").replace("https://", ""))
                .withConnectTimeout(connectionTimeout)
                .withSocketTimeout(socketTimeout)
                .build();
    }
}
