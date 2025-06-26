package com.xinyirun.scm.common.utils.spring;

import cn.hutool.extra.spring.SpringUtil;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @note
 */
public class SpringHelper extends SpringUtil {

    /**
     * 获取 application 环境变量
     */
    public static String getEnvProperty(String key) {
        Environment env = context().getEnvironment();
        return env.getProperty(key);
    }
    
    /**
     * 如果 BeanFactory 包含一个与所给名称匹配的 bean 定义，则返回 true
     */
    public static boolean containsBean(String name) {
        return getBeanFactory().containsBean(name);
    }

    /**
     * 如果 BeanFactory 包含一个与所给名称匹配的 bean 定义，则返回 true
     */
    public static <T> boolean containsBean(Class<T> clazz) {
        try {
            getBeanFactory().getBean(clazz);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 如果 BeanFactory 包含一个与所给名称匹配的 bean 定义，则返回 true
     */
    public static <T> T getBeanIfPresent(Class<T> clazz) {
        try {
            return getBeanFactory().getBean(clazz);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 如果 BeanFactory 存在，则返回，否则返回 null
     */
    public static ListableBeanFactory getBeanFactoryIfPresent() {
        try {
            return getBeanFactory();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 判断以给定名字注册的 bean 定义是一个 singleton 还是一个 prototype。
     * 如果与给定名字相应的 bean 定义没有被找到，将会抛出一个异常（NoSuchBeanDefinitionException）
     */
    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().isSingleton(name);
    }

    /**
     * @return Class 注册对象的类型
     */
    public static Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getType(name);
    }

    /**
     * 如果给定的 bean 名字在 bean 定义中有别名，则返回这些别名
     */
    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return getBeanFactory().getAliases(name);
    }

    /**
     * 获取 aop 代理对象
     */
    @SuppressWarnings("unchecked")
    public static <T> T getAopProxy(T invoker) {
        return (T) AopContext.currentProxy();
    }


    /**
     * 获取 spring 上下文
     */
    public static ApplicationContext context() {
        return getApplicationContext();
    }

    /**
     * 获取接口下的所有实现类
     */
    public static <T> List<T> getBeansByType(Class<T> clazz) {
        Map<String, T> beansOfType = getBeansOfType(clazz);
        return new ArrayList<>(beansOfType.values());
    }

}
