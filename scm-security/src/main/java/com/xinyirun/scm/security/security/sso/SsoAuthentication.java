package com.xinyirun.scm.security.security.sso;

import com.xinyirun.scm.bean.system.bo.user.api.ApiKeyAndSecretKeyBo;
import com.xinyirun.scm.bean.system.bo.user.login.MUserBo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class SsoAuthentication implements Authentication {

    private static final long serialVersionUID = -7833832100496230430L;

    private MUserBo mUserBo;

    public SsoAuthentication(MUserBo mUserBo) {
        this.mUserBo = mUserBo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return mUserBo;
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