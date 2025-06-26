package com.xinyirun.scm.security.handler.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinyirun.scm.bean.system.result.utils.v1.ResultUtil;
import com.xinyirun.scm.common.enums.ResultEnum;
import com.xinyirun.scm.common.exception.jwt.JWTAuthGetException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JWTAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper mapper;

    @Autowired
    public JWTAuthenticationFailureHandler(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        if (exception.getClass() == InternalAuthenticationServiceException.class) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            mapper.writeValue(response.getWriter(),
                    ResultUtil.NG(
                            HttpStatus.BAD_REQUEST.value(),
                            ResultEnum.JWT_AUTHENTICATION_ERROR,
                            exception,
                            "登录名或登录密码不正确！",
                            request
                    )
            );
        } else if (exception.getClass() == BadCredentialsException.class) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            mapper.writeValue(response.getWriter(),
                    ResultUtil.NG(
                            HttpStatus.UNAUTHORIZED.value(),
                            ResultEnum.JWT_AUTHENTICATION_ERROR,
                            exception,
                            "登录名或登录密码不正确！",
                            request
                    )
            );
        } else if (exception.getClass() == JWTAuthGetException.class) {
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            mapper.writeValue(response.getWriter(),
                    ResultUtil.NG(
                            HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            ResultEnum.JWT_AUTHENTICATION_GET_METHOD_ERROR,
                            exception,
                            ResultEnum.JWT_AUTHENTICATION_GET_METHOD_ERROR.getMsg(),
                            request
                    )
            );
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            mapper.writeValue(response.getWriter(),
                    ResultUtil.NG(
                            HttpStatus.UNAUTHORIZED.value(),
                            ResultEnum.JWT_AUTHENTICATION_ERROR,
                            exception,
                            exception.getMessage(),
                            request
                    )
            );
        }

        // exception.printStackTrace();
    }

}
