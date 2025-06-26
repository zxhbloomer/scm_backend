package com.xinyirun.scm.security.security.jwt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xinyirun.scm.common.exception.jwt.JWTAuthException;
import com.xinyirun.scm.common.exception.jwt.JWTAuthGetException;
import com.xinyirun.scm.security.handler.jwt.JWTAuthenticationFailureHandler;
import com.xinyirun.scm.security.handler.jwt.JWTAuthenticationSuccessHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

//For checking JWT Authentication at the time of login

/**
 * 用于在登录时检查 JWT 认证。
 * jwt login
 */
@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    private JWTAuthenticationSuccessHandler successHandler;
    private JWTAuthenticationFailureHandler failureHandler;
    private JWTAuthenticationEntryPoint entryPoint;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationSuccessHandler successHandler,
                                   JWTAuthenticationFailureHandler failureHandler,
                                   JWTAuthenticationEntryPoint entryPoint
    ) {
        this.authenticationManager = authenticationManager;
        this.successHandler = (JWTAuthenticationSuccessHandler) successHandler;
        this.failureHandler = failureHandler;
        this.entryPoint = entryPoint;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        /**
         * 只能POST登录
         */
        if ( !request.getMethod().equals(HttpMethod.POST.name())) {
            throw new JWTAuthGetException("不支持Get方式的提交: " + request.getMethod());
        }
        try {
            log.debug("app验证登录，并颁发token");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
            Map<String, String> authenticationBean = objectMapper.readValue(request.getInputStream(), Map.class);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                    authenticationBean.get(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY),
                    authenticationBean.get(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY));
            return authenticationManager.authenticate(authRequest);
        } catch (Exception e) {
            entryPoint.commence(request, response, new JWTAuthException(e));
        }
        return null;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authentication) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        successHandler.onAuthenticationSuccess(request, response, authentication);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException authentication) throws IOException, ServletException {
        SecurityContextHolder.clearContext();
        failureHandler.onAuthenticationFailure(request, response, authentication);
    }

}