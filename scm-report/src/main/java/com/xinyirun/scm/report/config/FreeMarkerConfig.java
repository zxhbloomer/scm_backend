package com.xinyirun.scm.report.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import java.util.Properties;

/**
 * FreeMarker模板引擎配置
 * 支持报表预览和打印功能的服务端渲染
 *
 * @author SCM Team
 * @since 2025-01-22
 */
@Configuration
public class FreeMarkerConfig {
    
    /**
     * FreeMarker配置器
     */
    @Bean
    public FreeMarkerConfigurer freeMarkerConfigurer() {
        FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
        configurer.setTemplateLoaderPath("classpath:/templates/");
        configurer.setDefaultEncoding("UTF-8");
        
        Properties settings = new Properties();
        settings.setProperty("template_update_delay", "0");  // 开发环境立即更新
        settings.setProperty("default_encoding", "UTF-8");
        settings.setProperty("number_format", "0.##########");
        settings.setProperty("datetime_format", "yyyy-MM-dd HH:mm:ss");
        settings.setProperty("classic_compatible", "true");
        settings.setProperty("whitespace_stripping", "true");
        configurer.setFreemarkerSettings(settings);
        
        return configurer;
    }
    
    /**
     * FreeMarker视图解析器
     */
    @Bean
    public FreeMarkerViewResolver freeMarkerViewResolver() {
        FreeMarkerViewResolver resolver = new FreeMarkerViewResolver();
        resolver.setCache(true);
        resolver.setPrefix("");
        resolver.setSuffix(".ftl");
        resolver.setContentType("text/html; charset=UTF-8");
        resolver.setRequestContextAttribute("request");
        resolver.setExposeRequestAttributes(true);
        resolver.setExposeSessionAttributes(true);
        resolver.setExposeSpringMacroHelpers(true);
        return resolver;
    }
}