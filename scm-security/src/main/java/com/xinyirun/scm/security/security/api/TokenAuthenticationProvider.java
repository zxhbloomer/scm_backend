package com.xinyirun.scm.security.security.api;

import com.xinyirun.scm.bean.system.bo.user.api.ApiKeyAndSecretKeyBo;
import com.xinyirun.scm.core.system.service.sys.app.token.ISTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {

    private final ISTokenService tokenService;

    @Autowired
    public TokenAuthenticationProvider(ISTokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {

        if (authentication.isAuthenticated()) {
            return authentication;
        }

        ApiKeyAndSecretKeyBo key_bo = (ApiKeyAndSecretKeyBo) authentication.getCredentials();

//        if (Strings.isNullOrEmpty(key_bo.getApp_key()) || Strings.isNullOrEmpty(key_bo.getSecret_key())) {
//            return authentication;
//        }

        UserDetails user = tokenService.authenticateToken(key_bo);

        Authentication auth = new PreAuthenticatedAuthenticationToken(
                user, key_bo, user.getAuthorities());
        auth.setAuthenticated(true);

        return auth;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return (TokenAuthenticationFilter.TokenAuthentication.class.isAssignableFrom(aClass));
    }
}
