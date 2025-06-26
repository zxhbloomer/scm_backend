package com.xinyirun.scm.redis.listener;

import com.xinyirun.scm.common.constant.SystemConstants;
import lombok.extern.slf4j.Slf4j;

import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;

/**
 * Session创建、销毁速率监控
 * 发现在spring boot 中，不能被调用
 * https://github.com/spring-projects/spring-session/issues/5
 */
@Slf4j
@WebListener
public class SystemHttpSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.debug("session 生成，session_id：" + se.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.debug("session 过期了，session_id：" + se.getSession().getId());
        String id = SystemConstants.SESSION_PREFIX.SESSION_USER_PREFIX_PREFIX + "_" + se.getSession().getId();
        log.debug("开始执行userbean销毁操作，id：" + id);
        se.getSession().removeAttribute(id);
        log.debug("执行userbean销毁操作成功");
        log.debug("session 销毁，session_id：" + se.getSession().getId());
    }
}
