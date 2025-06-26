package com.xinyirun.scm.redis.config;

import com.xinyirun.scm.redis.listener.SystemHttpSessionAttributeListener;
import com.xinyirun.scm.redis.listener.SystemHttpSessionListener;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.session.RedisSessionProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;

import jakarta.servlet.http.HttpSessionAttributeListener;
import jakarta.servlet.http.HttpSessionListener;
import java.time.Duration;

@Configuration
@EnableConfigurationProperties({RedisSessionProperties.class, RedisProperties.class})
public class RedisSessionConfiguration {

    @Value("${spring.data.redis.lettuce.pool.max-active}")
    private Integer maxActive;
    @Value("${spring.data.redis.lettuce.pool.max-idle}")
    private Integer maxIdle;
    @Value("${spring.data.redis.lettuce.pool.max-wait}")
    private Integer maxWait;
    @Value("${spring.data.redis.lettuce.pool.min-idle}")
    private Integer minIdle;
    @Value("${spring.data.redis.commandtimeout}")
    private Long commandTimeOut;

    @Autowired
    RedisProperties redisProperties;

    @Bean
    public GenericObjectPoolConfig localPoolConfig() {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxActive);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWait);
        config.setMinIdle(minIdle);
        return config;
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(RedisProperties redisProperties) {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setDatabase(redisProperties.getDatabase());
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());
        redisStandaloneConfiguration.setPassword(RedisPassword.of(redisProperties.getPassword()));
        return redisStandaloneConfiguration;
    }

    @Bean("scm_lettuce_connection_factory")
    public LettuceConnectionFactory connectionFactory(
            RedisStandaloneConfiguration defaultRedisConfig,
            GenericObjectPoolConfig defaultPoolConfig
    ) {
        LettuceClientConfiguration clientConfig =
                LettucePoolingClientConfiguration.builder().commandTimeout(Duration.ofMillis(
                    commandTimeOut))
                        .poolConfig(defaultPoolConfig).build();
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(defaultRedisConfig, clientConfig);
        return connectionFactory;
    }

//    @Bean("scm_lettuce_connection_factory")
//    public LettuceConnectionFactory connectionFactory() {
//        LettuceClientConfiguration clientConfig = lettuceClientConfiguration(maxActive, maxIdle, minIdle, maxWait);
//        RedisStandaloneConfiguration redisStandaloneConfiguration = redisStandaloneConfiguration(redisProperties);
//        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(redisStandaloneConfiguration, clientConfig);
//        return lettuceConnectionFactory;
//    }

    /**
     * 构建lettuceClientConfiguration
     *
     * @param maxActive 最大活跃数
     * @param maxIdle   最大空闲数
     * @param minIdle   最小空闲数
     * @param maxWait   最大等待时间
     */
    LettuceClientConfiguration lettuceClientConfiguration(Integer maxActive, Integer maxIdle, Integer minIdle, Integer maxWait) {
        GenericObjectPoolConfig config = new GenericObjectPoolConfig();
        config.setMaxTotal(maxActive);
        config.setMinIdle(minIdle);
        config.setMaxIdle(maxIdle);
        config.setMaxWaitMillis(maxWait);
        LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettucePoolingClientConfiguration.builder()
                .poolConfig(config).commandTimeout(Duration.ofSeconds(commandTimeOut));
        LettuceClientConfiguration clientConfig = builder.build();
        return clientConfig;
    }

    /**
     * 注册自定义的监听器 HttpSessionAttributeListener
     * @return
     */
    @Bean
    public ServletListenerRegistrationBean<HttpSessionAttributeListener> getWmsHttpSessionAttributeListener(){
        SystemHttpSessionAttributeListener listener = new SystemHttpSessionAttributeListener();
        return new ServletListenerRegistrationBean<HttpSessionAttributeListener>(listener);
    }

    /**
     * 注册自定义的监听器
     * @return
     */
    @Bean
    public ServletListenerRegistrationBean<HttpSessionListener> getWmsHttpSessionListener(){
        SystemHttpSessionListener listener = new SystemHttpSessionListener();
        return new ServletListenerRegistrationBean<HttpSessionListener>(listener);
    }
}

