package com.xinyirun.scm.security.security.sso;

import com.xinyirun.scm.common.utils.string.StringUtils;
import com.xinyirun.scm.core.system.service.client.user.IMUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;

public class SsoAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private IMUserService userDetailService;



    @Override
    public Authentication authenticate(Authentication authentication) {
        String username = authentication.getName();
        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("账号不可以为空");
        }

        //验证账号

        UserDetails user = userDetailService.loadUserByMd5Username(username);
        //获取用户权限信息
        Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return SsoCodeAuthenticationToken.class.isAssignableFrom(aClass) || UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }

    public IMUserService getUserDetailService() {
        return userDetailService;
    }

    public void setUserDetailService(IMUserService userDetailService) {
        this.userDetailService = userDetailService;
    }
}
