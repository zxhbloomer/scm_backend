//package com.xinyirun.scm.redis.config;
//
//import com.xinyirun.scm.redis.listener.SystemSpringHttpSessionListener;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.session.data.redis.config.annotation.web.http.RedisHttpSessionConfiguration;
//import org.springframework.stereotype.Component;
//
//import jakarta.servlet.http.HttpSessionListener;
//import java.util.ArrayList;
//import java.util.List;
//
///**
// *
// */
//@Component
//public class WmsSessionConfig extends RedisHttpSessionConfiguration {
//
//    @Value("${server.servlet.session.timeout}")
//    private int time_out;
//
//    public WmsSessionConfig() {
//        List<HttpSessionListener> list = new ArrayList<>();
//        list.add(new SystemSpringHttpSessionListener());
//        this.setHttpSessionListeners(list);
//        // session 过期时间30分钟
//        this.setMaxInactiveIntervalInSeconds(time_out);
//    }
//}