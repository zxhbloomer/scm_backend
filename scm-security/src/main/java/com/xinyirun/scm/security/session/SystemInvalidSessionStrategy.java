package com.xinyirun.scm.security.session;

import com.xinyirun.scm.bean.system.result.utils.v1.ResponseResultUtil;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.system.SystemInvalidSessionStrategyException;
import com.xinyirun.scm.security.properties.SystemSecurityProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.session.InvalidSessionStrategy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 处理 session 失效
 */
public class SystemInvalidSessionStrategy implements InvalidSessionStrategy {

    private SystemSecurityProperties systemSecurityProperties;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    private boolean createNewSession = true;

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // session过期后自动获取判断
        if (this.systemSecurityProperties.getCreateNewSession()) {
            request.getSession();
        }
        // if (CommonUtil.isAjaxRequest(request)) {

        ResponseResultUtil.responseWriteError(request,
            response,
            new SystemInvalidSessionStrategyException("您的会话已过期，请重新登录！"),
            HttpStatus.UNAUTHORIZED.value(),
            ResultEnum.USER_SESSION_TIME_OUT_ERROR,
            "您的会话已过期，请重新登录！");

        // }
        // redirectStrategy.sendRedirect(request, response, systemSecurityProperties.getLogoutUrl());
    }

    public void setWmsSecurityProperties(SystemSecurityProperties systemSecurityProperties) {
        this.systemSecurityProperties = systemSecurityProperties;
    }
}
