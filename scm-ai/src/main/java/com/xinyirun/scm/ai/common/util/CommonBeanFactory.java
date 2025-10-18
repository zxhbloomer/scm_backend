package com.xinyirun.scm.ai.common.util;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CommonBeanFactory implements ApplicationContextAware {
    private static ApplicationContext context;

    public void setApplicationContext(@NotNull ApplicationContext ctx) throws BeansException {
        context = ctx;
    }


    public static <T> T getBean(Class<T> className) {
        try {
            return context != null && className != null ? context.getBean(className) : null;
        } catch (BeansException e) {
            return null;
        }
    }
}

