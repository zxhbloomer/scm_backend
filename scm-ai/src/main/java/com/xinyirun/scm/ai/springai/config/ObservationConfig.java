/*
 * SCM AI Module - Observation Config
 * Adapted from ByteDesk AI Module for SCM System
 */
package com.xinyirun.scm.ai.springai.config;

import org.springframework.ai.chat.client.observation.ChatClientObservationConvention;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.xinyirun.scm.ai.springai.observability.CustomChatClientObservationConvention;

import io.micrometer.observation.ObservationRegistry;
import io.micrometer.observation.aop.ObservedAspect;

@Configuration
public class ObservationConfig {

    @Bean
    public ObservedAspect observedAspect(ObservationRegistry observationRegistry) {
        return new ObservedAspect(observationRegistry);
    }

    @Bean
    public ObservationRegistry observationRegistry() {
        return ObservationRegistry.create();
    }

    @Bean
    public ChatClientObservationConvention chatClientObservationConvention() {
        return new CustomChatClientObservationConvention();
    }
}