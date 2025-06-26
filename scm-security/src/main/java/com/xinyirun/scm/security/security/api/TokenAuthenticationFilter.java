package com.xinyirun.scm.security.security.api;

import com.xinyirun.scm.bean.system.bo.user.api.ApiKeyAndSecretKeyBo;
import com.xinyirun.scm.common.enums.api.ApiResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Grab the `?token=xxx` parameter into authentication, so as to trigger spring security's
 * authentication
 */
@Component
@Slf4j
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain fc)
            throws ServletException, IOException {

        SecurityContext context = SecurityContextHolder.getContext();
        if (context.getAuthentication() != null && context.getAuthentication().isAuthenticated()) {
            // do nothing
            log.debug("已经授权");
        } else {
            log.debug("开始授权");
            String app_key;
            String secret_key;
            ApiKeyAndSecretKeyBo key_bo = new ApiKeyAndSecretKeyBo();
            Map<String, String[]> params = req.getParameterMap();

            /**
             * 此处判断url知否包含app_key,secret_key，如果没有则从requese head中去判断
             */
            app_key = req.getHeader("app_key");
            secret_key = req.getHeader("secret_key");
            log.debug("开始授权--app_key--"+app_key);
            log.debug("开始授权--secret_key--"+secret_key);
            if (StringUtils.isNotEmpty(app_key)) {
                key_bo.setApp_key(app_key);
            }
            if (StringUtils.isNotEmpty(secret_key)) {
                key_bo.setSecret_key(secret_key);
            }

            /**
             * request head中没有app_key,secret_key，到request param中去找
             */
            if (StringUtils.isEmpty(app_key)) {
                if(!params.isEmpty()){
                    /**
                     * requese param中包含app_key,secret_key，开始判断
                     */
                    if (!params.containsKey("app_key")) {
                        req.setAttribute("error.msg", ApiResultEnum.NEED_APP_KEY.getMsg());
                        req.setAttribute("error.enum", ApiResultEnum.NEED_APP_KEY);
                        fc.doFilter(req, res);
                        return;
                    }
                    if (!params.containsKey("secret_key")) {
                        req.setAttribute("error.msg", ApiResultEnum.NEED_SECRET_KEY.getMsg());
                        req.setAttribute("error.enum", ApiResultEnum.NEED_SECRET_KEY);
                        fc.doFilter(req, res);
                        return;
                    }
                    if (params.containsKey("app_key")) {
                        app_key = params.get("app_key")[0];
                        if (!StringUtils.isEmpty(app_key)) {
                            key_bo.setApp_key(app_key);
                        } else {
                            req.setAttribute("error.msg", ApiResultEnum.NOT_NULL_APP_KEY.getMsg());
                            req.setAttribute("error.enum", ApiResultEnum.NOT_NULL_APP_KEY);
                            fc.doFilter(req, res);
                            return;
                        }
                    }
                    if (params.containsKey("secret_key")) {
                        secret_key = params.get("secret_key")[0];
                        if (!StringUtils.isEmpty(secret_key)) {
                            key_bo.setSecret_key(secret_key);
                        } else {
                            req.setAttribute("error.msg", ApiResultEnum.NOT_NULL_SECRET_KEY.getMsg());
                            req.setAttribute("error.enum", ApiResultEnum.NOT_NULL_SECRET_KEY);
                            fc.doFilter(req, res);
                            return;
                        }
                    }
                }
            }

            Authentication auth = new TokenAuthentication(key_bo);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        fc.doFilter(req, res);
        return;
    }

    class TokenAuthentication implements Authentication {

        private static final long serialVersionUID = -6833832100496230430L;

        private ApiKeyAndSecretKeyBo key_bo;

        private TokenAuthentication(ApiKeyAndSecretKeyBo key_bo) {
            this.key_bo = key_bo;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
        }

        @Override
        public Object getCredentials() {
            return key_bo;
        }

        @Override
        public Object getDetails() {
            return null;
        }

        @Override
        public Object getPrincipal() {
            return null;
        }

        @Override
        public boolean isAuthenticated() {
            return false;
        }

        @Override
        public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
        }

        @Override
        public String getName() {
            return "token";
        }
    }
}
