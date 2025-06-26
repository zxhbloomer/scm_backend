package com.xinyirun.scm.starter;

import com.xinyirun.scm.common.properies.SystemConfigProperies;
import com.xinyirun.scm.security.properties.SystemSecurityProperties;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * @author zxh
 */

@SpringBootApplication(
        exclude = { DataSourceAutoConfiguration.class },
        scanBasePackages = {
                "com.xinyirun.scm.framework",
                "com.xinyirun.scm.*",
                "com.xinyirun.scm.common",
                "com.xinyirun.scm.core.bpm",
                "com.xinyirun.scm.security",
                "com.xinyirun.scm.mq",
                "com.xinyirun.scm.mqconsumer",
        })
@EnableTransactionManagement
@EntityScan(basePackages = {"com.xinyirun.scm.*"})
@Slf4j
@EnableCaching
@ServletComponentScan
// session 过期时间，如果EnableRedisHttpSession，则必须在这里制定过期时间:14400 为4个小时
@EnableRedisHttpSession(maxInactiveIntervalInSeconds=14400, redisNamespace = "{spring}:{session}")
@EnableMongoRepositories(basePackages = {"com.xinyirun.scm.mongodb.repository"})
@MapperScan(basePackages = {
        "com.xinyirun.scm.core.bpm.mapper",
        "com.xinyirun.scm.quartz.mapper",
        "com.xinyirun.scm.mq.rabbitmq.log.mapper",
})
@EnableAsync(proxyTargetClass=true)
// 启用spring retry
@EnableRetry
@EnableScheduling
public class WmsBpmStart {

    public static ConfigurableApplicationContext config;

    public static void main(String[] args) {
        log.debug("-----------------------启动开始-------------------------");
        SpringApplication.run(WmsBpmStart.class, args);
        log.debug("-----------------------启动完毕-------------------------");
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer() {
        return registry -> registry.config().commonTags("application", "prometheus-demo");
    }
}
