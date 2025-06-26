package com.xinyirun.scm.security.handler.system;

import com.xinyirun.scm.bean.system.result.utils.v1.ResponseResultUtil;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.system.CredentialException;
import com.xinyirun.scm.common.utils.CommonUtil;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 权限不足处理
 * @author Administrator
 */
public class SystemAuthenticationAccessDeniedHandler implements AccessDeniedHandler {

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        if (CommonUtil.isAjaxRequest(request)) {
            response.setContentType(SystemConstants.JSON_UTF8);
            ResponseResultUtil.responseWriteError(request,
                response,
                new CredentialException("没有该权限！"),
                HttpStatus.UNAUTHORIZED.value(),
                ResultEnum.USER_NO_PERMISSION_ERROR,
                "没有该权限！");
        } else {
            redirectStrategy.sendRedirect(request, response, SystemConstants.FEBS_ACCESS_DENY_URL);
        }
    }
}
