package com.xinyirun.scm.framework.config.webclient;

import com.xinyirun.scm.common.properies.SystemConfigProperies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.time.Duration;

/**
 * webclient 自动配置
 * @author zhangxiaohua
 */
@Configuration
public class WebClientConfig {

    @Autowired
    private SystemConfigProperies systemConfigProperies;

    @Bean
    public WebClient webClient() {
        // 无限大
        ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(-1))
                .build();
        ConnectionProvider connectionProvider = ConnectionProvider.builder("http").maxConnections(1000)
                .pendingAcquireMaxCount(1000)
                .pendingAcquireTimeout(Duration.ofMillis(ConnectionProvider.DEFAULT_POOL_ACQUIRE_TIMEOUT))
                .maxIdleTime(Duration.ZERO).build();

        HttpClient httpClient = HttpClient.create(connectionProvider).keepAlive(false);

        WebClient.Builder builder = WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient));

        return builder
                .exchangeStrategies(exchangeStrategies)
                .baseUrl(systemConfigProperies.getFsWebclientBaseurl())
                .build();
    }
}