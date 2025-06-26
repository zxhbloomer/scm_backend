package com.xinyirun.scm.core.bpm.config;

import com.xinyirun.scm.core.bpm.listener.GlobalTaskEventListener;
import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

/**
 * @Author: Wqf
 * @Description:
 * @CreateTime : 2024/10/29 15:38
 */


@Configuration
public class FlowableConfig {

    @Autowired
    private GlobalTaskEventListener globalTaskEventListener;

    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> processEngineConfigurationConfigurer() {
        return configuration -> {
            configuration.setEventListeners(Collections.singletonList(globalTaskEventListener));
        };
    }
}
