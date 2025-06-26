package com.xinyirun.scm.core.bpm.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringValueResolver;

/**
 * @author : willian fu
 * @date : 2020/5/26
 */
@Component
public class BeanUtil implements ApplicationContextAware, EmbeddedValueResolverAware {

    private static ApplicationContext applicationContext;

    private static StringValueResolver valueResolver;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        if (BeanUtil.applicationContext == null){
            applicationContext = context;
        }
    }

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    //通过name获取 Bean
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name,Class<T> clazz){
        return getApplicationContext().getBean(name, clazz);
    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver stringValueResolver) {
        valueResolver = stringValueResolver;
    }

    public static String getProperties(String name){
        return valueResolver.resolveStringValue(name);
    }
}
