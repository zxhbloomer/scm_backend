//package com.xinyirun.scm.redis.listener;
//
//import com.xinyirun.scm.common.constant.SystemConstants;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.context.annotation.Configuration;
//
//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;
//import javax.servlet.annotation.WebListener;
//import jakarta.servlet.http.HttpSessionAttributeListener;
//import javax.servlet.http.HttpSessionBindingEvent;
//import javax.servlet.http.HttpSessionEvent;
//import jakarta.servlet.http.HttpSessionListener;
//
//@Configuration
//@Slf4j
//@WebListener
//public class SystemSpringHttpSessionListener implements ServletContextListener, HttpSessionListener, HttpSessionAttributeListener {
//
//    @Override
//    public void contextInitialized(ServletContextEvent sce) {
//    }
//
//    @Override
//    public void contextDestroyed(ServletContextEvent sce) {
//    }
//
//    @Override
//    public void sessionCreated(HttpSessionEvent se) {
//        log.debug("session 生成，session_id：" + se.getSession().getId());
//    }
//
//    @Override
//    public void sessionDestroyed(HttpSessionEvent se) {
//        log.debug("session 过期了，session_id：" + se.getSession().getId());
//        String id = SystemConstants.SESSION_PREFIX.SESSION_USER_PREFIX_PREFIX + "_" + se.getSession().getId();
//        log.debug("开始执行userbean销毁操作，id：" + id);
//        se.getSession().removeAttribute(id);
//        log.debug("执行userbean销毁操作成功");
//        log.debug("session 销毁，session_id：" + se.getSession().getId());
//    }
//
//    @Override
//    public void attributeAdded(HttpSessionBindingEvent se) {
//        log.debug("Attribute 添加内容");
//        log.debug("Attribute Name:" + se.getName());
//        log.debug("Attribute Value:" + se.getValue());
//    }
//
//    @Override
//    public void attributeRemoved(HttpSessionBindingEvent se) {
//        log.debug("attribute 删除:" + se.getName());
//    }
//
//    @Override
//    public void attributeReplaced(HttpSessionBindingEvent se) {
//        log.debug("Attribute 替换内容");
//        log.debug("Attribute Name:" + se.getName());
//        log.debug("Attribute Old Value:" + se.getValue());
//    }
//
//}