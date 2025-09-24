package com.xinyirun.scm.security.handler.system;

import com.xinyirun.scm.bean.system.result.utils.v1.ResponseResultUtil;
import com.xinyirun.scm.common.constant.SystemConstants;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.system.CredentialException;
import com.xinyirun.scm.bean.utils.security.SecurityUtil;
import com.xinyirun.scm.core.system.service.client.user.IMUserService;
import com.xinyirun.scm.framework.base.controller.system.v1.SystemBaseController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录成功处理器
 */
@Slf4j
@Component(value = "systemAuthenticationSucessHandler")
public class SystemAuthenticationSucessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private SystemBaseController systemBaseController;

    private SessionRegistry sessionRegistry;

    @Autowired
    private IMUserService imUserService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, CredentialException {
        response.setContentType(SystemConstants.JSON_UTF8);
        Map<String,String> token = new HashMap<String,String>();
        token.put("token",getSessionId(authentication, request.getSession().getId()));
        System.out.println("===================================");
        // session
        try {
            systemBaseController.resetUserSession(SecurityUtil.getLoginUser_id(), SystemConstants.LOGINUSER_OR_STAFF_ID.LOGIN_USER_ID);

            //登录成功 记录最新登录时间
            imUserService.updateLoginDate(SecurityUtil.getLoginUser_id());

            //更新用户AI会话UUID
            imUserService.updateUserAiConversationUuid(SecurityUtil.getLoginUser_id());

            ResponseResultUtil.responseWriteOK(token, response);
        } catch (Exception e) {
            log.error("onAuthenticationSuccess error", e);
            ResponseResultUtil.responseWriteError(request,
                    response,
                    new CredentialException("获取权限发生错误，请联系管理员！"),
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    ResultEnum.SYSTEM_ERROR,
                    "获取权限发生错误，请联系管理员！");
        }
    }
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    /**
     * 获取sessionid
     * @param authentication
     * @param dflt
     * @return
     */
    private String getSessionId(Authentication authentication, String dflt) {
        if (authentication != null && authentication.isAuthenticated() && authentication.getDetails() instanceof WebAuthenticationDetails) {
            String sessionId = ((WebAuthenticationDetails) authentication.getDetails()).getSessionId();
            return sessionId == null ? dflt : sessionId;
        } else {
            // anonymous
            return dflt;
        }
    }


}
