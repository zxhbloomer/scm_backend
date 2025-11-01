package com.xinyirun.scm.security.security;

import com.xinyirun.scm.core.system.service.client.user.IMUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SsoAuthenticationManager implements AuthenticationManager {

    @Autowired
    IMUserService userServiceIml;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = "";
        UserDetails users = userServiceIml.loadUserByUsername(username);

        Authentication authentication2 = new UsernamePasswordAuthenticationToken(users, users.getPassword(), users.getAuthorities());

        return authentication2;
    }
}