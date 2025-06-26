package com.xinyirun.scm.security.security.jwt;

import com.xinyirun.scm.bean.system.result.utils.v1.ResponseResultUtil;
import com.xinyirun.scm.common.constant.JWTSecurityConstants;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.jwt.JWTAuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {

   @Override
   public void commence(HttpServletRequest request,
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
       // This is invoked when user tries to access a secured REST resource without supplying any credentials
       // We should just send a 401 Unauthorized response because there is no 'login page' to redirect to
       // Here you can place any message you want
//      response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

       log.warn("警告信息：",authException);
       String header = request.getHeader(JWTSecurityConstants.HEADER_STRING);

       ResponseResultUtil.responseWriteError(request,
               response,
               authException,
               HttpStatus.INTERNAL_SERVER_ERROR.value(),
               ResultEnum.JWT_AUTHENTICATION_HTTP_HEAD_INVALID,
               authException.getMessage());
   }
}
